package com.apiproject.DTOs.Auth;

public record LoginAdminRequestDTO(
        String email,
        String password) {
}
