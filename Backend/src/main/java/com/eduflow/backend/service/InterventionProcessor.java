package com.eduflow.backend.service;

import com.eduflow.backend.model.Intervention;
import com.eduflow.backend.repository.InterventionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InterventionProcessor {

    @Autowired
    private InterventionRepository interventionRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EmailService emailService;

    // Runs every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void processPendingInterventions() {
        List<Intervention> pendingInterventions = interventionRepository.findByStatus("PENDING");
        
        for (Intervention intervention : pendingInterventions) {
            String type = intervention.getInterventionType();
            
            if ("EMAIL_REMINDER".equals(type) || "PAYMENT_RETRY".equals(type)) {
                try {
                    // Create Razorpay payment link
                    String paymentLink = paymentService.createRecoveryPaymentLink(intervention.getPaymentOrder());
                    System.out.println("\n=======================================================");
                    System.out.println("Generated Razorpay payment link: " + paymentLink);
                    System.out.println("=======================================================\n");
                    
                    // Send recovery email
                    emailService.sendRecoveryEmail(
                        intervention.getUser(), 
                        intervention.getPaymentOrder().getCourse().getTitle(), 
                        paymentLink
                    );
                    
                    // Update intervention status
                    intervention.setStatus("EMAIL_SENT");
                    intervention.setCompletedAt(LocalDateTime.now());
                    interventionRepository.save(intervention);
                } catch (Exception e) {
                    System.err.println("Failed to process intervention ID " + intervention.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            } else if ("COURSE_RECOMMENDATION".equals(type) || "NO_ACTION".equals(type)) {
                // Nothing to send for these types
                intervention.setStatus("SKIPPED");
                intervention.setCompletedAt(LocalDateTime.now());
                interventionRepository.save(intervention);
            }
        }
    }
}
