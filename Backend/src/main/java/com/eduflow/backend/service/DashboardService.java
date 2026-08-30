package com.eduflow.backend.service;

import com.eduflow.backend.dto.ActivityDto;
import com.eduflow.backend.dto.AdminDashboardDto;
import com.eduflow.backend.dto.CourseDto;
import com.eduflow.backend.dto.StudentDashboardDto;
import com.eduflow.backend.model.Enrollment;
import com.eduflow.backend.model.User;
import com.eduflow.backend.repository.ActivityRepository;
import com.eduflow.backend.repository.CourseRepository;
import com.eduflow.backend.repository.EnrollmentRepository;
import com.eduflow.backend.repository.PaymentOrderRepository;
import com.eduflow.backend.repository.UserRepository;
import com.eduflow.backend.repository.InterventionRepository;
import com.eduflow.backend.dto.InterventionDto;
import com.eduflow.backend.model.PaymentOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private InterventionRepository interventionRepository;

    public StudentDashboardDto getStudentDashboard(User user) {
        StudentDashboardDto dto = new StudentDashboardDto();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        
        long completed = enrollmentRepository.countByUserAndCompleted(user, true);
        dto.setCompletedCourses(completed);
        
        // Mock data for things not tracked currently
        dto.setHoursLearned(completed * 10);
        dto.setCertificatesEarned(completed);
        
        dto.setEnrolledCourses(enrollmentRepository.findByUser(user).stream().map(this::convertToCourseDto).collect(Collectors.toList()));
        return dto;
    }

    public AdminDashboardDto getAdminDashboard(String span) {
        AdminDashboardDto dto = new AdminDashboardDto();
        
        // Total students
        long totalStudents = userRepository.findAll().stream().filter(u -> "STUDENT".equals(u.getRole())).count();
        dto.setTotalStudents(totalStudents);
        
        // Active courses
        dto.setActiveCourses(courseRepository.count());
        
        // Calculate recovered revenue
        List<PaymentOrder> recoveredOrders = paymentOrderRepository.findByStatusAndIsRecovered("SUCCESS", true);
        double totalRecovered = recoveredOrders.stream().mapToDouble(PaymentOrder::getRecoveredAmount).sum();
        dto.setRecoveredRevenue(totalRecovered);
        
        // Calculate real revenue from successful orders
        List<PaymentOrder> allSuccessfulOrders = paymentOrderRepository.findByStatus("SUCCESS");
        double totalRevenue = allSuccessfulOrders.stream().mapToDouble(PaymentOrder::getAmount).sum();
        dto.setMonthlyRevenue(totalRevenue);
        
        dto.setRecentActivities(activityRepository.findTop10ByOrderByTimestampDesc().stream().map(a -> {
            ActivityDto aDto = new ActivityDto();
            aDto.setId(a.getId());
            aDto.setDescription(a.getDescription());
            aDto.setTimestamp(a.getTimestamp());
            aDto.setType(a.getType());
            return aDto;
        }).collect(Collectors.toList()));
        
        // Fetch recent interventions
        dto.setRecentInterventions(interventionRepository.findTop10ByOrderByIdDesc().stream().map(i -> {
            InterventionDto iDto = new InterventionDto();
            iDto.setId(i.getId());
            iDto.setStudentName(i.getUser().getFullName());
            iDto.setCourseName(i.getPaymentOrder().getCourse().getTitle());
            iDto.setInterventionType(i.getInterventionType());
            iDto.setExpectedValue(i.getExpectedValue());
            iDto.setStatus(i.getStatus());
            iDto.setCompletedAt(i.getCompletedAt());
            return iDto;
        }).collect(Collectors.toList()));
        
        // Generate dynamic Revenue Trend based on span
        com.eduflow.backend.dto.ChartDataDto revenueTrend = generateRevenueTrend(allSuccessfulOrders, span);
        dto.setRevenueTrend(revenueTrend);
        
        // Intervention Stats Doughnut
        long recoveredCount = interventionRepository.findByStatus("RECOVERED").size();
        long inProgressCount = interventionRepository.findByStatus("PENDING").size() + interventionRepository.findByStatus("EMAIL_SENT").size();
        long skippedFailedCount = interventionRepository.findByStatus("SKIPPED").size() + interventionRepository.findByStatus("FAILED").size();
        
        com.eduflow.backend.dto.ChartDataDto interventionStats = new com.eduflow.backend.dto.ChartDataDto();
        interventionStats.setLabels(java.util.Arrays.asList("Recovered", "In Progress", "Skipped/Failed"));
        interventionStats.setData(java.util.Arrays.asList((double) recoveredCount, (double) inProgressCount, (double) skippedFailedCount));
        dto.setInterventionStats(interventionStats);
        
        // Generate AI Revenue Suggestions
        java.util.List<String> suggestions = new java.util.ArrayList<>();
        
        long totalPendingOrders = paymentOrderRepository.findByStatus("PENDING").size();
        if (totalPendingOrders > 5) {
            suggestions.add("High checkout abandonment detected. Consider triggering an automated 10% discount email to pending checkouts.");
        }
        
        if (skippedFailedCount > recoveredCount && skippedFailedCount > 0) {
            suggestions.add("Email recovery success is low. Consider integrating SMS or WhatsApp reminders for better conversion.");
        }
        
        long activeCourses = dto.getActiveCourses();
        if (activeCourses > 0 && totalRevenue < 5000) {
            suggestions.add("Course traffic is steady but overall conversions are down. Consider A/B testing your course descriptions or lowering prices.");
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("Metrics look healthy! Consider running a flash sale to boost weekend revenue.");
        }
        dto.setAiSuggestions(suggestions);
        
        return dto;
    }

    private com.eduflow.backend.dto.ChartDataDto generateRevenueTrend(List<PaymentOrder> orders, String span) {
        com.eduflow.backend.dto.ChartDataDto trend = new com.eduflow.backend.dto.ChartDataDto();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        
        LocalDateTime now = LocalDateTime.now();
        
        if ("1y".equals(span)) {
            // Last 12 months
            for (int i = 11; i >= 0; i--) {
                LocalDateTime start = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0);
                LocalDateTime end = start.plusMonths(1);
                double sum = orders.stream()
                        .filter(o -> o.getCreatedAt() != null && !o.getCreatedAt().isBefore(start) && o.getCreatedAt().isBefore(end))
                        .mapToDouble(PaymentOrder::getAmount).sum();
                labels.add(start.getMonth().name().substring(0, 3));
                data.add(sum);
            }
        } else if ("1m".equals(span)) {
            // Last 4 weeks
            for (int i = 3; i >= 0; i--) {
                LocalDateTime start = now.minusWeeks(i).with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0);
                LocalDateTime end = start.plusWeeks(1);
                double sum = orders.stream()
                        .filter(o -> o.getCreatedAt() != null && !o.getCreatedAt().isBefore(start) && o.getCreatedAt().isBefore(end))
                        .mapToDouble(PaymentOrder::getAmount).sum();
                labels.add("Week " + (4 - i));
                data.add(sum);
            }
        } else {
            // 7d default
            for (int i = 6; i >= 0; i--) {
                LocalDateTime start = now.minusDays(i).withHour(0).withMinute(0).withSecond(0);
                LocalDateTime end = start.plusDays(1);
                double sum = orders.stream()
                        .filter(o -> o.getCreatedAt() != null && !o.getCreatedAt().isBefore(start) && o.getCreatedAt().isBefore(end))
                        .mapToDouble(PaymentOrder::getAmount).sum();
                labels.add(start.getDayOfWeek().name().substring(0, 3));
                data.add(sum);
            }
        }
        
        trend.setLabels(labels);
        trend.setData(data);
        return trend;
    }

    private CourseDto convertToCourseDto(Enrollment enrollment) {
        CourseDto dto = new CourseDto();
        dto.setId(enrollment.getCourse().getId());
        dto.setTitle(enrollment.getCourse().getTitle());
        dto.setDescription(enrollment.getCourse().getDescription());
        dto.setThumbnailUrl(enrollment.getCourse().getThumbnailUrl());
        dto.setModuleCount(enrollment.getCourse().getModuleCount());
        dto.setProgressPercentage(enrollment.getProgressPercentage());
        return dto;
    }
}

