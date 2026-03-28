package com.example.apiproject.DTOs;

import com.example.apiproject.entities.admin.UserAdmin;
import org.jspecify.annotations.NonNull;

public record UserResponseDTO(
        Long id,
        String userName) {

    public static UserResponseDTO fromEntity(@NonNull UserAdmin userAdmin) {
        return new UserResponseDTO(
                userAdmin.getId(),
                userAdmin.getUserName());
    }
}