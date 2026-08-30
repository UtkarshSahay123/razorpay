package com.eduflow.backend.service;

import com.eduflow.backend.model.Intervention;
import com.eduflow.backend.model.PaymentOrder;
import com.eduflow.backend.model.RecoveryPrediction;
import com.eduflow.backend.model.User;
import com.eduflow.backend.repository.InterventionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AiDecisionService {

    @Autowired
    private InterventionRepository interventionRepository;

    public Intervention determineIntervention(User user, PaymentOrder order, RecoveryPrediction prediction) {
        String action = "NO_ACTION";
        String reason = "Insufficient recovery probability";
        double actionCost = 0.0;

        String failureCode = order.getFailureCode() != null ? order.getFailureCode() : "UNKNOWN";
        double prob = prediction.getRecoveryProbability() != null ? prediction.getRecoveryProbability() : 0.0;
        double amount = order.getAmount() != null ? order.getAmount() : 0.0;

        // Override AI probability: Always send a link regardless of score
        if ("ABANDONED".equals(failureCode)) {
            action = "EMAIL_REMINDER";
            reason = "Checkout abandonment";
            actionCost = 0.5; // Email cost
        } else {
            action = "PAYMENT_RETRY";
            reason = "Payment failure retry";
            actionCost = 1.0; // SMS/Retry cost
        }

        double expectedRecovery = amount * prob;
        double expectedValue = expectedRecovery - actionCost;
        
        // We removed the `expectedValue <= 0` override because we want to guarantee the link is sent.

        Intervention intervention = new Intervention();
        intervention.setUser(user);
        intervention.setPaymentOrder(order);
        intervention.setInterventionType(action);
        intervention.setReason(reason);
        intervention.setExpectedRecovery(expectedRecovery);
        intervention.setActionCost(actionCost);
        intervention.setExpectedValue(expectedValue);
        intervention.setStatus("PENDING");
        intervention.setSentAt(LocalDateTime.now());
        
        return interventionRepository.save(intervention);
    }
}
