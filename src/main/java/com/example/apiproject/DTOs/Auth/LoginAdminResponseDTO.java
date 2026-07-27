package com.example.apiproject.DTOs.Auth;

import com.example.apiproject.entities.admin.UserAdmin;

public record LoginAdminResponseDTO(
        Long id,
        String fullName,
        String email,
        Number phone,
        String businessName,
        String photo,
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
                "ADMIN",
                token,
                "Inicio de sesion exitoso");
    }
}
