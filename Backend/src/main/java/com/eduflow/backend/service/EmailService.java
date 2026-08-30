package com.eduflow.backend.service;

import com.eduflow.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRecoveryEmail(User user, String courseName, String paymentLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("utkarshsahay321@gmail.com");
            message.setTo(user.getEmail());
            message.setSubject("Complete your enrollment for " + courseName);
            message.setText("Hi " + user.getFullName() + ",\n\n" +
                    "We noticed you couldn't complete your payment for " + courseName + ".\n" +
                    "Click the link below to seamlessly complete your purchase:\n" +
                    paymentLink + "\n\n" +
                    "Best regards,\nLumina Education Team");
            
            mailSender.send(message);
            
            System.out.println("PHASE 11: Recovery email sent successfully to " + user.getEmail());
        } catch (Exception e) {
            System.err.println("PHASE 11: Failed to send recovery email to " + user.getEmail());
            e.printStackTrace();
        }
    }
}
