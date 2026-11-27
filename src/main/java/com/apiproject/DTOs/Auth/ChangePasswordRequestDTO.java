package com.apiproject.DTOs.Auth;

/** Body de PATCH /api/client/change-password: exige la contrasena vigente para poner una nueva. */
public record ChangePasswordRequestDTO(
        String currentPassword,
        String newPassword
) {
}
