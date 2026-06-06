package com.example.apiproject.DTOs.Client;

import com.example.apiproject.entities.client.UserClient;

public record ClientResponseDTO(
        Long id,
        String fullName,
        String email,
        String userName,
        Long phone) {

    public static ClientResponseDTO fromEntity(UserClient userClient){
        return new ClientResponseDTO(
                userClient.getId(),
                userClient.getFullName(),
                userClient.getEmail(),
                userClient.getUserName(),
                userClient.getPhone());
    }
}
