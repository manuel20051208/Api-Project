package com.apiproject.DTOs.General;

import com.apiproject.repositories.projection.ProductReviewSummaryProjection;

/**
 * Resumen de reseñas de un producto: promedio de estrellas (0 si no hay) y total.
 */
public record ProductReviewSummaryDTO(
        Long productId,
        Double averageStars,
        Long totalReviews
) {
    private static final double NO_REVIEWS = 0.0;

    public static ProductReviewSummaryDTO fromProjection(Long productId, ProductReviewSummaryProjection summary) {
        double average = summary == null || summary.getAverageStars() == null
                ? NO_REVIEWS
                : Math.round(summary.getAverageStars() * 100.0) / 100.0;
        long total = summary == null || summary.getTotalReviews() == null
                ? 0L
                : summary.getTotalReviews();
        return new ProductReviewSummaryDTO(productId, average, total);
    }
}
