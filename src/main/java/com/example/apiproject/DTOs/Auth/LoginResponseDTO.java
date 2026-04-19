package com.example.apiproject.DTOs.Auth;

import com.example.apiproject.entities.admin.UserAdmin;
import com.example.apiproject.entities.client.UserClient;

public record LoginResponseDTO(
        Long id,
        String username,
        String fullName,
        String email,
        Number phone,
        String businessName,
        String accountType,
        String message) {

    public static LoginResponseDTO fromAdmin(UserAdmin userAdmin) {
        return new LoginResponseDTO(
                userAdmin.getId(),
                userAdmin.getUserName(),
                userAdmin.getFullName(),
                userAdmin.getEmail(),
                userAdmin.getPhone(),
                userAdmin.getBusinessName(),
                "USER",
                "Inicio de sesion exitoso");
    }

    public static LoginResponseDTO fromClient(UserClient userClient) {
        return new LoginResponseDTO(
                userClient.getId(),
                userClient.getUserName(),
                userClient.getFullName(),
                userClient.getEmail(),
                userClient.getPhone(),
                null,
                "CLIENT",
                "Inicio de sesion exitoso");
    }
}
