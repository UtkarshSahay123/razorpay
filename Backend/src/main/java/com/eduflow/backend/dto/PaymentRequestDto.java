package com.eduflow.backend.dto;

public class PaymentRequestDto {
    private Long courseId;
    private Double amount;

    // Getters and setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
