package com.example.apiproject.DTOs.Client;

public record PaymentCardRequestDTO(
        String cardHolderName,
        String brand,
        String lastFour,
        Boolean active
) {
}
