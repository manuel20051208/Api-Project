package com.example.apiproject.DTOs.Auth;

public record LoginClientRequestDTO(
        String email,
        String password) {
}
