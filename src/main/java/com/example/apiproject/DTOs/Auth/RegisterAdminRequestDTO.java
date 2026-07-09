package com.example.apiproject.DTOs.Auth;

public record RegisterAdminRequestDTO(
        String username,
        String password,
        String fullName,
        String email,
        Long phone,
        String businessName) {
}
