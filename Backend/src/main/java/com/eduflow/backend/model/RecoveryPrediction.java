package com.eduflow.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recovery_predictions")
public class RecoveryPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "payment_order_id")
    private PaymentOrder paymentOrder;

    private Double recoveryProbability;
    private Double riskScore;
    private String modelVersion;
    private LocalDateTime predictedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public PaymentOrder getPaymentOrder() { return paymentOrder; }
    public void setPaymentOrder(PaymentOrder paymentOrder) { this.paymentOrder = paymentOrder; }
    public Double getRecoveryProbability() { return recoveryProbability; }
    public void setRecoveryProbability(Double recoveryProbability) { this.recoveryProbability = recoveryProbability; }
    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
    public LocalDateTime getPredictedAt() { return predictedAt; }
    public void setPredictedAt(LocalDateTime predictedAt) { this.predictedAt = predictedAt; }
}
