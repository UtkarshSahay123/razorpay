package com.eduflow.backend.controller;

import com.eduflow.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@CrossOrigin(origins = "*")
public class WebhookController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/razorpay")
    public ResponseEntity<?> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        
        if (signature == null || signature.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing signature");
        }

        boolean processed = paymentService.processWebhook(payload, signature);
        if (processed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Invalid signature or failed to process");
        }
    }
}
