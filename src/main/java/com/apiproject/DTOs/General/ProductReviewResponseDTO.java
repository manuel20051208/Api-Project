package com.apiproject.DTOs.General;

import com.apiproject.entities.general.ProductReviews;
import com.apiproject.repositories.projection.ProductReviewProjection;

import java.time.LocalDateTime;

public record ProductReviewResponseDTO(
        Long id,
        Long productId,
        String productName,
        Long clientId,
        String clientName,
        Integer stars,
        String reviews,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProductReviewResponseDTO fromProjection(ProductReviewProjection review) {
        return new ProductReviewResponseDTO(
                review.getId(),
                review.getProductId(),
                review.getProductName(),
                review.getClientId(),
                review.getClientName(),
                review.getStars(),
                review.getReviews(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }

    public static ProductReviewResponseDTO fromEntity(ProductReviews review) {
        return new ProductReviewResponseDTO(
                review.getId(),
                review.getProduct() != null ? review.getProduct().getId() : null,
                review.getProduct() != null ? review.getProduct().getName() : null,
                review.getUserClient() != null ? review.getUserClient().getId() : null,
                review.getUserClient() != null ? review.getUserClient().getFullName() : null,
                review.getStars(),
                review.getReviews(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
