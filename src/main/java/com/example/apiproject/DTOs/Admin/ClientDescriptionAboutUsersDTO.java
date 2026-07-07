package com.example.apiproject.DTOs.Admin;

import com.example.apiproject.entities.admin.UserAdmin;

public record ClientDescriptionAboutUsersDTO(
        Long id,
        String businessName,
        String photo
) {
    public static ClientDescriptionAboutUsersDTO fromEntity(UserAdmin admin) {
        return new ClientDescriptionAboutUsersDTO(
                admin.getId(),
                admin.getBusinessName(),
                admin.getProfilePhoto());
    }
}
