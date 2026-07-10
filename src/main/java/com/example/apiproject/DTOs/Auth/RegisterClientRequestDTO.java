package com.example.apiproject.DTOs.Auth;

import java.time.LocalDateTime;

public record RegisterClientRequestDTO(
        String username,
        String fullName,
        String email,
        String password,
        Long phone,
        String address,
        LocalDateTime createdAt
) {
}
