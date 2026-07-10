package com.example.apiproject.DTOs.Auth;

import com.example.apiproject.entities.admin.UserAdmin;

public record LoginAdminResponseDTO(
        Long id,
        String username,
        String fullName,
        String email,
        Number phone,
        String businessName,
        String accountType,
        String token,
        String message) {

    public static LoginAdminResponseDTO fromAdmin(UserAdmin userAdmin, String token) {
        return new LoginAdminResponseDTO(
                userAdmin.getId(),
                userAdmin.getUserName(),
                userAdmin.getFullName(),
                userAdmin.getEmail(),
                userAdmin.getPhone(),
                userAdmin.getBusinessName(),
                "ADMIN",
                token,
                "Inicio de sesion exitoso");
    }
}
