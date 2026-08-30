package com.eduflow.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_orders")
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String status; // CREATED, SUCCESS, FAILED
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Double amount;
    private LocalDateTime createdAt;
    
    private String razorpayPaymentLinkId;
    private Boolean isRecovered = false;
    private Double recoveredAmount = 0.0;
    
    private String failureReason;
    private String failureCode;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getRazorpayPaymentLinkId() { return razorpayPaymentLinkId; }
    public void setRazorpayPaymentLinkId(String razorpayPaymentLinkId) { this.razorpayPaymentLinkId = razorpayPaymentLinkId; }
    public Boolean getIsRecovered() { return isRecovered; }
    public void setIsRecovered(Boolean isRecovered) { this.isRecovered = isRecovered; }
    public Double getRecoveredAmount() { return recoveredAmount; }
    public void setRecoveredAmount(Double recoveredAmount) { this.recoveredAmount = recoveredAmount; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public String getFailureCode() { return failureCode; }
    public void setFailureCode(String failureCode) { this.failureCode = failureCode; }
}
