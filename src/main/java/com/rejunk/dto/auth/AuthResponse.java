package com.rejunk.dto.auth;

public class AuthResponse {

    private String userId;
    private String fullName;
    private String email;
    private String role;
    private String message;

    public AuthResponse(String userId, String fullName, String email, String role, String message) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
}
