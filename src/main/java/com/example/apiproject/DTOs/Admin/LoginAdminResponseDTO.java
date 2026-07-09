package com.example.apiproject.DTOs.Client;

import com.example.apiproject.entities.client.UserClient;

import java.time.LocalDateTime;

public record ClientResponseDTO(
        Long id,
        String fullName,
        String email,
        String userName,
        Long phone,
        String address,
        LocalDateTime createdAt) {

    public static ClientResponseDTO fromEntity(UserClient userClient){
        return new ClientResponseDTO(
                userClient.getId(),
                userClient.getFullName(),
                userClient.getEmail(),
                userClient.getUserName(),
                userClient.getPhone(),
                userClient.getAddress(),
                userClient.getCreatedAt());
    }
}
