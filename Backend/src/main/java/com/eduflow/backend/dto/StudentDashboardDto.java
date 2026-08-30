package com.eduflow.backend.dto;

import java.util.List;

public class StudentDashboardDto {
    private String fullName;
    private String email;
    private long completedCourses;
    private long hoursLearned; // mocked
    private long certificatesEarned; // mocked
    private List<CourseDto> enrolledCourses;

    // Getters and setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public long getCompletedCourses() { return completedCourses; }
    public void setCompletedCourses(long completedCourses) { this.completedCourses = completedCourses; }
    public long getHoursLearned() { return hoursLearned; }
    public void setHoursLearned(long hoursLearned) { this.hoursLearned = hoursLearned; }
    public long getCertificatesEarned() { return certificatesEarned; }
    public void setCertificatesEarned(long certificatesEarned) { this.certificatesEarned = certificatesEarned; }
    public List<CourseDto> getEnrolledCourses() { return enrolledCourses; }
    public void setEnrolledCourses(List<CourseDto> enrolledCourses) { this.enrolledCourses = enrolledCourses; }
}
