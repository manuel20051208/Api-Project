package com.apiproject.DTOs.General;

public record TheThreeBestClients(
        Long clientId,
        Long userId,
        String name,
        Long amountOfBuys
) {
}
