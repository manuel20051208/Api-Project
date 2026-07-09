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
        String token,
        String message) {

    public static LoginResponseDTO fromAdmin(UserAdmin userAdmin) {
        return fromAdmin(userAdmin, null);
    }

    public static LoginResponseDTO fromAdmin(UserAdmin userAdmin, String token) {
        return new LoginResponseDTO(
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

    public static LoginResponseDTO fromClient(UserClient userClient) {
        return fromClient(userClient, null);
    }

    public static LoginResponseDTO fromClient(UserClient userClient, String token) {
        return new LoginResponseDTO(
                userClient.getId(),
                userClient.getUserName(),
                userClient.getFullName(),
                userClient.getEmail(),
                userClient.getPhone(),
                null,
                "CLIENT",
                token,
                "Inicio de sesion exitoso");
    }
}
