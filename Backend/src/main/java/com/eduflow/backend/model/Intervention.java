package com.eduflow.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interventions")
public class Intervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "payment_order_id")
    private PaymentOrder paymentOrder;

    private String interventionType;
    private String reason;
    private Double expectedRecovery;
    private Double actionCost;
    private Double expectedValue;
    private String status;
    private LocalDateTime sentAt;
    private LocalDateTime completedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public PaymentOrder getPaymentOrder() { return paymentOrder; }
    public void setPaymentOrder(PaymentOrder paymentOrder) { this.paymentOrder = paymentOrder; }
    public String getInterventionType() { return interventionType; }
    public void setInterventionType(String interventionType) { this.interventionType = interventionType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Double getExpectedRecovery() { return expectedRecovery; }
    public void setExpectedRecovery(Double expectedRecovery) { this.expectedRecovery = expectedRecovery; }
    public Double getActionCost() { return actionCost; }
    public void setActionCost(Double actionCost) { this.actionCost = actionCost; }
    public Double getExpectedValue() { return expectedValue; }
    public void setExpectedValue(Double expectedValue) { this.expectedValue = expectedValue; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
