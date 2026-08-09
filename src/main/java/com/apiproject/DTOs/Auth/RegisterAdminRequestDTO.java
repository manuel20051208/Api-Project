package com.apiproject.DTOs.Auth;

public record RegisterAdminRequestDTO(
        String password,
        String fullName,
        String email,
        Long phone,
        String businessName) {
}
