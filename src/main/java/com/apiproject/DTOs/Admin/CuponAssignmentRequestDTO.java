package com.apiproject.DTOs.Admin;

import java.util.List;

public record CuponAssignmentRequestDTO(
        Long cuponId,
        List<Long> clientIds,
        boolean assignToAll
) {
}
