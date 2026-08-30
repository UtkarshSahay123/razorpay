package com.eduflow.backend.dto;

public class AuthDto {
    public static class LoginRequest {
        public String email;
        public String password;
    }
    public static class RegisterRequest {
        public String fullName;
        public String email;
        public String password;
        public String role; // "STUDENT" or "ADMIN"
    }
    public static class AuthResponse {
        public String token;
        public String role;
        public AuthResponse(String token, String role) {
            this.token = token;
            this.role = role;
        }
    }
}
