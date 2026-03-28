package com.example.apiproject.DTOs;

import com.example.apiproject.entities.client.UserClient;

public record ClientResponseDTO(
        Long id,
        String fullName,
        String email) {

    public static ClientResponseDTO fromEntity(UserClient userClient){
        return new ClientResponseDTO(
                userClient.getId(),
                userClient.getFullName(),
                userClient.getEmail());
    }
}
