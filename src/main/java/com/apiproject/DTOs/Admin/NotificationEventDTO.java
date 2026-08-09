package com.apiproject.DTOs.Admin;

public record NotificationEventDTO(
        String tipo,
        String mensaje,
        Long adminId
) {
}
