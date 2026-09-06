package com.apiproject.DTOs.Client;

import com.apiproject.entities.client.PaymentCard;
import com.apiproject.entities.client.UserClient;
import com.apiproject.enums.ColorTypes;

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
        String photo,
        ColorTypes colorTypes) {

    public static ClientResponseDTO fromEntity(UserClient userClient) {
        return new ClientResponseDTO(
                userClient.getId(),
                userClient.getFullName(),
                userClient.getEmail(),
                userClient.getPhone(),
                userClient.getPaymentCards(),
                userClient.getAddress(),
                userClient.getCreatedAt(),
                userClient.getPhoto(),
                userClient.getColorTypes());
    }
}
