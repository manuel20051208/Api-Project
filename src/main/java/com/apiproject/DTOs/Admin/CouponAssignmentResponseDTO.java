package com.apiproject.DTOs.Admin;

/**
 * Asignación de un cupón a un cliente con su límite asignado y los usos reales.
 * usageLimit NULL = ilimitado. usedCount es el contador de *_used_by_clients.
 * remainingUsage = usageLimit - usedCount (0 si ya se agotó; null si es ilimitado).
 */
public record CouponAssignmentResponseDTO(
        Long id,
        String clientName,
        String clientEmail,
        Integer usageLimit,
        Long usedCount,
        Integer remainingUsage,
        String productName
) {
}