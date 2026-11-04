package com.apiproject.DTOs.General;

/**
 * Payload para crear o actualizar una reseña.
 * En el POST se usa {@code productId}; en el PUT se ignora (manda la ruta).
 * Los campos son opcionales en el PUT: solo se actualiza lo que llegue.
 */
public record ProductReviewRequestDTO(
        Long productId,
        Integer stars,
        String reviews
) {
}
