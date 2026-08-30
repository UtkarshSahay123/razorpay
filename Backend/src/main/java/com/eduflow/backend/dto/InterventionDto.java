package com.eduflow.backend.dto;

import java.time.LocalDateTime;

public class InterventionDto {
    private Long id;
    private String studentName;
    private String courseName;
    private String interventionType;
    private Double expectedValue;
    private String status;
    private LocalDateTime completedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getInterventionType() { return interventionType; }
    public void setInterventionType(String interventionType) { this.interventionType = interventionType; }
    
    public Double getExpectedValue() { return expectedValue; }
    public void setExpectedValue(Double expectedValue) { this.expectedValue = expectedValue; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
