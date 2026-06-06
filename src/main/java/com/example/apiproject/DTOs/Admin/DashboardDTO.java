package com.example.apiproject.DTOs.Admin;

public record DashboardDTO(
        Long userId,
        Integer monthNumber,
        Integer salesYear,
        String monthName,
        Double monthlyTotal,
        Long numberOfProducts,
        Long countClients
) {
}
