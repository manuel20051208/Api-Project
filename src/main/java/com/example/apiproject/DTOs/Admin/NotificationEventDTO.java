package com.example.apiproject.DTOs.Admin;

public record NotificationEvent(
        String tipo,
        String mensaje,
        Long adminId
) {
}
