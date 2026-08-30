package com.eduflow.backend.controller;

import com.eduflow.backend.dto.UserEventDto;
import com.eduflow.backend.model.User;
import com.eduflow.backend.repository.UserRepository;
import com.eduflow.backend.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin(origins = "*")
public class TrackingController {

    @Autowired
    private TrackingService trackingService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }

    @PostMapping("/event")
    public ResponseEntity<?> recordEvent(@RequestBody UserEventDto eventDto) {
        User user = getCurrentUser();
        // Allow anonymous tracking if no user is found, though we'll associate it with null user
        trackingService.trackEvent(user, eventDto);
        return ResponseEntity.ok().build();
    }
}
