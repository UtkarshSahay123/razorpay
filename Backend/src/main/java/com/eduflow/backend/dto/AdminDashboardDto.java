package com.eduflow.backend.dto;

import java.util.List;

public class AdminDashboardDto {
    private long totalStudents;
    private long activeCourses;
    private double monthlyRevenue;
    private double recoveredRevenue;
    private List<ActivityDto> recentActivities;
    private List<InterventionDto> recentInterventions;
    
    private ChartDataDto revenueTrend;
    private ChartDataDto interventionStats;
    private List<String> aiSuggestions;

    // Getters and setters
    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
    public long getActiveCourses() { return activeCourses; }
    public void setActiveCourses(long activeCourses) { this.activeCourses = activeCourses; }
    public double getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(double monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
    public double getRecoveredRevenue() { return recoveredRevenue; }
    public void setRecoveredRevenue(double recoveredRevenue) { this.recoveredRevenue = recoveredRevenue; }
    public List<ActivityDto> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<ActivityDto> recentActivities) { this.recentActivities = recentActivities; }
    public List<InterventionDto> getRecentInterventions() { return recentInterventions; }
    public void setRecentInterventions(List<InterventionDto> recentInterventions) { this.recentInterventions = recentInterventions; }
    public ChartDataDto getRevenueTrend() { return revenueTrend; }
    public void setRevenueTrend(ChartDataDto revenueTrend) { this.revenueTrend = revenueTrend; }
    public ChartDataDto getInterventionStats() { return interventionStats; }
    public void setInterventionStats(ChartDataDto interventionStats) { this.interventionStats = interventionStats; }
    public List<String> getAiSuggestions() { return aiSuggestions; }
    public void setAiSuggestions(List<String> aiSuggestions) { this.aiSuggestions = aiSuggestions; }
}

