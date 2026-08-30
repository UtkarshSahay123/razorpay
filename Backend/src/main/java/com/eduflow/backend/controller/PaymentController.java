package com.eduflow.backend.controller;

import com.eduflow.backend.dto.PaymentOrderResponseDto;
import com.eduflow.backend.dto.PaymentRequestDto;
import com.eduflow.backend.dto.PaymentVerifyDto;
import com.eduflow.backend.model.User;
import com.eduflow.backend.repository.UserRepository;
import com.eduflow.backend.service.PaymentService;
import com.eduflow.backend.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrackingService trackingService;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        return userRepository.findByEmail(email).orElse(null);
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentRequestDto request) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(403).build();
            }
            PaymentOrderResponseDto response = paymentService.createOrder(request, user);
            
            // Log payment attempt
            trackingService.trackEventInternal(user, "payment_attempted", request.getCourseId(), "{\"orderId\":\"" + response.getOrderId() + "\"}");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating order: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerifyDto verifyDto) {
        boolean isValid = paymentService.verifyPayment(verifyDto);
        User user = getCurrentUser();
        
        if (isValid) {
            if (user != null) {
                trackingService.trackEventInternal(user, "payment_success", null, "{\"razorpayOrderId\":\"" + verifyDto.getRazorpayOrderId() + "\"}");
            }
            return ResponseEntity.ok("Payment verified and enrollment successful");
        } else {
            if (user != null) {
                trackingService.trackEventInternal(user, "payment_failed", null, "{\"razorpayOrderId\":\"" + verifyDto.getRazorpayOrderId() + "\", \"reason\":\"verification_failed\"}");
            }
            return ResponseEntity.badRequest().body("Payment verification failed");
        }
    }

    @PostMapping("/failed")
    public ResponseEntity<?> paymentFailed(@RequestBody com.eduflow.backend.dto.PaymentFailedDto failedDto) {
        paymentService.markPaymentFailed(failedDto.getOrderId(), failedDto.getFailureCode(), failedDto.getFailureReason());
        User user = getCurrentUser();
        if (user != null) {
            trackingService.trackEventInternal(user, "payment_failed", null, 
                "{\"razorpayOrderId\":\"" + failedDto.getOrderId() + "\", \"failureCode\":\"" + failedDto.getFailureCode() + "\"}");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/abandoned")
    public ResponseEntity<?> checkoutAbandoned(@RequestBody com.eduflow.backend.dto.CheckoutAbandonedDto abandonedDto) {
        paymentService.markCheckoutAbandoned(abandonedDto.getOrderId());
        User user = getCurrentUser();
        if (user != null) {
            trackingService.trackEventInternal(user, "checkout_abandoned", null, 
                "{\"razorpayOrderId\":\"" + abandonedDto.getOrderId() + "\"}");
        }
        return ResponseEntity.ok().build();
    }
}
