package com.example.apiproject.DTOs.Client;

import com.example.apiproject.entities.client.PaymentCard;
import com.example.apiproject.entities.client.UserClient;

import java.time.LocalDateTime;
import java.util.List;

public record ClientResponseDTO(
        Long id,
        String fullName,
        String email,
        Long phone,
        List<PaymentCard> paymentCards,
        String address,
        LocalDateTime createdAt,
        String photo) {

    public static ClientResponseDTO fromEntity(UserClient userClient) {
        return new ClientResponseDTO(
                userClient.getId(),
                userClient.getFullName(),
                userClient.getEmail(),
                userClient.getPhone(),
                userClient.getPaymentCards(),
                userClient.getAddress(),
                userClient.getCreatedAt(),
                userClient.getPhoto());
    }
}
