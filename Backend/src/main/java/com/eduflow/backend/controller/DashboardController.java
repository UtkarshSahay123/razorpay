package com.eduflow.backend.controller;

import com.eduflow.backend.dto.AdminDashboardDto;
import com.eduflow.backend.dto.StudentDashboardDto;
import com.eduflow.backend.model.User;
import com.eduflow.backend.repository.UserRepository;
import com.eduflow.backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        return userRepository.findByEmail(email).orElse(null);
    }

    @GetMapping("/student")
    public ResponseEntity<StudentDashboardDto> getStudentDashboard() {
        User user = getCurrentUser();
        if (user == null || !"STUDENT".equals(user.getRole())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(dashboardService.getStudentDashboard(user));
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAdminDashboard(@RequestParam(value = "span", defaultValue = "7d") String span) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = "";
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        
        System.out.println("DEBUG: Admin Dashboard requested by email: " + email);
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            System.out.println("DEBUG: User not found in DB for email: " + email);
            return ResponseEntity.status(403).body("User not found in database.");
        }
        
        System.out.println("DEBUG: User role in DB is: " + user.getRole());
        if (!"ADMIN".equals(user.getRole())) {
            System.out.println("DEBUG: Role is not ADMIN");
            return ResponseEntity.status(403).body("User is not an ADMIN.");
        }
        
        try {
            return ResponseEntity.ok(dashboardService.getAdminDashboard(span));
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in getAdminDashboard: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }
}
