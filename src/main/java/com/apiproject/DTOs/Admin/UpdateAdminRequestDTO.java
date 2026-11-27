package com.apiproject.DTOs.Admin;

import com.apiproject.enums.ColorTypes;

/**
 * Body de PATCH /api/user/modify. Solo los campos editables del perfil del admin:
 * todos opcionales (null = no se toca). Password, foto (va por /upload-profile), id y
 * productos NO se pueden cambiar desde aqui (antes el body era la entidad UserAdmin).
 */
public record UpdateAdminRequestDTO(
        String fullName,
        String email,
        Long phone,
        String businessName,
        ColorTypes colorTypes
) {
}
