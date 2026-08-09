package com.apiproject.DTOs.Auth;

public record LoginClientRequestDTO(
        String email,
        String password) {
}
