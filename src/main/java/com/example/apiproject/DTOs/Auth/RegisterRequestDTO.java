package com.example.apiproject.DTOs.Auth;

public record RegisterRequestDTO(
        String username,
        String password,
        String fullName,
        String email,
        Long phone) {
}
