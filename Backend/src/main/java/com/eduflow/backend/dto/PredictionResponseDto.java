package com.eduflow.backend.dto;

public class PredictionResponseDto {
    private Long user_id;
    private double recovery_probability;

    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public double getRecovery_probability() { return recovery_probability; }
    public void setRecovery_probability(double recovery_probability) { this.recovery_probability = recovery_probability; }
}
