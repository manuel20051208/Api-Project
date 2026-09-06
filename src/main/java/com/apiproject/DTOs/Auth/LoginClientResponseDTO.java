package com.apiproject.DTOs.Auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.apiproject.entities.client.UserClient;
import com.apiproject.enums.ColorTypes;

import java.time.LocalDateTime;

public record LoginClientResponseDTO(
        Long id,
        String fullName,
        String email,
        Number phone,
        String address,
        LocalDateTime createdAt,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String photo,
        ColorTypes colorTypes,
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
                userClient.getPhoto(),
                userClient.getColorTypes(),
                "CLIENT",
                token,
                "Inicio de sesion exitoso");
    }
}
