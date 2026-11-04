package com.apiproject.controllers.general;

import com.apiproject.DTOs.General.ProductReviewRequestDTO;
import com.apiproject.DTOs.General.ProductReviewResponseDTO;
import com.apiproject.DTOs.General.ProductReviewSummaryDTO;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.general.ProductReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product Reviews", description = "Reseñas de productos hechas por clientes")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product-reviews")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @Operation(summary = "Reseñas paginadas de un producto")
    @GetMapping("/product/{productId}")
    public Page<ProductReviewResponseDTO> findByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productReviewService.findByProductId(productId, page, size);
    }

    @Operation(summary = "Promedio de estrellas y total de reseñas de un producto")
    @GetMapping("/product/{productId}/summary")
    public ProductReviewSummaryDTO summary(@PathVariable Long productId) {
        return productReviewService.summary(productId);
    }

    @Operation(summary = "Reseñas del cliente autenticado")
    @GetMapping("/my")
    public Page<ProductReviewResponseDTO> findMine(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productReviewService.findByClientId(authenticatedUser.id(), page, size);
    }

    @Operation(summary = "Obtiene una reseña por id")
    @GetMapping("/{reviewId}")
    public ProductReviewResponseDTO findById(@PathVariable Long reviewId) {
        return productReviewService.findById(reviewId);
    }

    @Operation(summary = "Crea una reseña (solo si el cliente compró el producto)")
    @PostMapping
    public ProductReviewResponseDTO create(
            @RequestBody ProductReviewRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return productReviewService.create(authenticatedUser.id(), request);
    }

    @Operation(summary = "Actualiza la reseña del cliente autenticado")
    @PutMapping("/{reviewId}")
    public ProductReviewResponseDTO update(
            @PathVariable Long reviewId,
            @RequestBody ProductReviewRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return productReviewService.update(reviewId, authenticatedUser.id(), request);
    }

    @Operation(summary = "Elimina la reseña del cliente autenticado")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        productReviewService.delete(reviewId, authenticatedUser.id());
        return ResponseEntity.noContent().build();
    }
}
