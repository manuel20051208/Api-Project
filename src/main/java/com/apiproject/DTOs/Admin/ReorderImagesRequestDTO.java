package com.apiproject.DTOs.Admin;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Nuevo orden de las imagenes de un producto: {@code imageIds} debe traer exactamente
 * todas las imagenes del producto, sin repetir, en el orden deseado (la primera es la portada).
 */
public record ReorderImagesRequestDTO(
        @NotNull Long productId,
        @NotEmpty List<@NotNull Long> imageIds
) {
}
