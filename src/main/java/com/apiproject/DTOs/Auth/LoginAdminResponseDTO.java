package com.apiproject.DTOs.Auth;

import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.enums.ColorTypes;

public record LoginAdminResponseDTO(
        Long id,
        String fullName,
        String email,
        Number phone,
        String businessName,
        String photo,
        ColorTypes colorTypes,
        String accountType,
        String token,
        String message) {

    public static LoginAdminResponseDTO fromAdmin(UserAdmin userAdmin, String token) {
        return new LoginAdminResponseDTO(
                userAdmin.getId(),
                userAdmin.getFullName(),
                userAdmin.getEmail(),
                userAdmin.getPhone(),
                userAdmin.getBusinessName(),
                userAdmin.getProfilePhotoUrl(),
                userAdmin.getColorTypes(),
                "ADMIN",
                token,
                "Inicio de sesion exitoso");
    }
}
