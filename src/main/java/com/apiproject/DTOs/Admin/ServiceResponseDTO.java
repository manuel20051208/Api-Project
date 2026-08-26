package com.apiproject.DTOs.Admin;

import com.apiproject.entities.admin.ServiceOffered;

public record ServiceResponseDTO(
        Long id,
        String nameOfService,
        Double valueOfService,
        String descriptionOfService,
        Long userAdminId
) {
    public static ServiceResponseDTO fromEntity(ServiceOffered serviceOffered) {
        return new ServiceResponseDTO(
                serviceOffered.getId(),
                serviceOffered.getNameOfService(),
                serviceOffered.getValueOfService(),
                serviceOffered.getDescriptionOfService(),
                serviceOffered.getUserAdmin() != null ? serviceOffered.getUserAdmin().getId() : null
        );
    }
}
