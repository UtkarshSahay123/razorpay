package com.eduflow.backend.service;

import com.eduflow.backend.dto.UserEventDto;
import com.eduflow.backend.model.Course;
import com.eduflow.backend.model.User;
import com.eduflow.backend.model.UserEvent;
import com.eduflow.backend.repository.CourseRepository;
import com.eduflow.backend.repository.UserEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import com.eduflow.backend.dto.PredictionRequestDto;
import com.eduflow.backend.model.PaymentOrder;
import com.eduflow.backend.model.RecoveryPrediction;
import com.eduflow.backend.repository.PaymentOrderRepository;
import com.eduflow.backend.repository.RecoveryPredictionRepository;

@Service
public class TrackingService {

    @Autowired
    private UserEventRepository userEventRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MlPredictionService mlPredictionService;

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Autowired
    private RecoveryPredictionRepository recoveryPredictionRepository;

    @Autowired
    private AiDecisionService aiDecisionService;

    public void trackEvent(User user, UserEventDto eventDto) {
        UserEvent event = new UserEvent();
        event.setUser(user);
        event.setEventType(eventDto.getEventType());
        event.setSessionId(eventDto.getSessionId());
        event.setMetadata(eventDto.getMetadata());
        event.setTimestamp(LocalDateTime.now());

        if (eventDto.getCourseId() != null) {
            courseRepository.findById(eventDto.getCourseId()).ifPresent(event::setCourse);
        }

        userEventRepository.save(event);

        if ("payment_failed".equals(eventDto.getEventType()) || "checkout_abandoned".equals(eventDto.getEventType())) {
            if (user != null) {
                performRecoveryPrediction(user);
            }
        }
    }
    
    public void trackEventInternal(User user, String eventType, Long courseId, String metadata) {
        UserEventDto dto = new UserEventDto();
        dto.setEventType(eventType);
        dto.setCourseId(courseId);
        dto.setMetadata(metadata);
        trackEvent(user, dto);
    }

    private void performRecoveryPrediction(User user) {
        paymentOrderRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId()).ifPresent(paymentOrder -> {
            PredictionRequestDto request = new PredictionRequestDto();
            request.setUser_id(user.getId());
            // In a real scenario, we would calculate these from activity history.
            // Using placeholder or basic extracted metrics for now.
            request.setCourse_views(2); 
            request.setTime_spent_mins(10);
            request.setCheckout_attempts(1);
            request.setPayment_attempts(1);
            request.setPayment_failures(1);
            request.setPrevious_successful_payments(0);
            request.setDays_since_last_activity(0);
            request.setSubscription_amount(paymentOrder.getAmount() != null ? paymentOrder.getAmount() : 0.0);
            request.setFailure_category(paymentOrder.getFailureCode() != null ? paymentOrder.getFailureCode() : "UNKNOWN");

            double probability = mlPredictionService.getRecoveryProbability(request);
            
            // Calculate Risk Score / Revenue at Risk
            double expectedRecovery = (paymentOrder.getAmount() != null ? paymentOrder.getAmount() : 0.0) * probability;
            
            RecoveryPrediction prediction = new RecoveryPrediction();
            prediction.setUser(user);
            prediction.setPaymentOrder(paymentOrder);
            prediction.setRecoveryProbability(probability);
            // using risk score conceptually as expected recovery here or separate field if added.
            prediction.setRiskScore(expectedRecovery);
            prediction.setModelVersion("xgboost-v1");
            prediction.setPredictedAt(LocalDateTime.now());
            
            prediction = recoveryPredictionRepository.save(prediction);
            
            // Phase 9: AI Decision Layer
            aiDecisionService.determineIntervention(user, paymentOrder, prediction);
        });
    }
}
