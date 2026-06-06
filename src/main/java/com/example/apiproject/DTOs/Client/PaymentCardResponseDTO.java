package com.example.apiproject.DTOs.Client;

import com.example.apiproject.entities.client.PaymentCard;
import com.example.apiproject.repositories.projection.PaymentCardDetailsProjection;

import java.time.LocalDateTime;

public record PaymentCardResponseDTO(
        Long id,
        Long clientId,
        String cardHolderName,
        String brand,
        String lastFour,
        boolean active,
        LocalDateTime createdAt
) {
    public static PaymentCardResponseDTO fromEntity(PaymentCard paymentCard) {
        return new PaymentCardResponseDTO(
                paymentCard.getId(),
                paymentCard.getUserClient().getId(),
                paymentCard.getCardHolderName(),
                paymentCard.getBrand(),
                paymentCard.getLastFour(),
                paymentCard.isActive(),
                paymentCard.getCreatedAt()
        );
    }

    public static PaymentCardResponseDTO fromProjection(PaymentCardDetailsProjection paymentCard) {
        return new PaymentCardResponseDTO(
                paymentCard.getId(),
                paymentCard.getClientId(),
                paymentCard.getCardHolderName(),
                paymentCard.getBrand(),
                paymentCard.getLastFour(),
                paymentCard.isActive(),
                paymentCard.getCreatedAt()
        );
    }
}
