package com.apiproject.DTOs.Client;

import com.apiproject.enums.ColorTypes;

/**
 * Body de PATCH /api/client/modify. Solo datos de perfil, todos opcionales (null = no se toca).
 * La contrasena ya NO se cambia aqui: va por PATCH /api/client/change-password, que exige la actual.
 */
public record UpdateClientRequestDTO(
        String fullName,
        String email,
        Long phone,
        String address,
        ColorTypes colorTypes
) {
}
