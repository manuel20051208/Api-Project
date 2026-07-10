package com.example.apiproject.DTOs.Auth;

import com.example.apiproject.entities.client.UserClient;

import java.time.LocalDateTime;

public record LoginClientResponseDTO(
        Long id,
        String fullName,
        String email,
        Number phone,
        String address,
        LocalDateTime createdAt,
        String accountType,
        String token,
        String message
) {
    public static LoginClientResponseDTO fromClient(UserClient userClient, String token) {
        return new LoginClientResponseDTO(
                userClient.getId(),
                userClient.getFullName(),
                userClient.getEmail(),
                userClient.getPhone(),
                userClient.getAddress(),
                userClient.getCreatedAt(),
                "CLIENT",
                token,
                "Inicio de sesion exitoso");
    }
}
