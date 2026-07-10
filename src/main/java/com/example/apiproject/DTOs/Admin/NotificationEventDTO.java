package com.example.apiproject.DTOs.Admin;

public record NotificationEventDTO(
        String tipo,
        String mensaje,
        Long adminId
) {
}
