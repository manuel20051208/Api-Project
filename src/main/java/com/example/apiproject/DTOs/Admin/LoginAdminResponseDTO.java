package com.example.apiproject.DTOs.Admin;

import com.example.apiproject.entities.client.UserClient;

import java.time.LocalDateTime;

public record LoginAdminResponseDTO(
        Long id,
        String fullName,
        String email,
        Long phone,
        String address,
        LocalDateTime createdAt) {

    public static LoginAdminResponseDTO fromEntity(UserClient userClient){
        return new LoginAdminResponseDTO(
                userClient.getId(),
                userClient.getFullName(),
                userClient.getEmail(),
                userClient.getPhone(),
                userClient.getAddress(),
                userClient.getCreatedAt());
    }
}
