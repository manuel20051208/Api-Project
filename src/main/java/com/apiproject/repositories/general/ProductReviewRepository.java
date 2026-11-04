package com.apiproject.repositories.general;

import com.apiproject.entities.general.ProductReviews;
import com.apiproject.repositories.projection.ProductReviewProjection;
import com.apiproject.repositories.projection.ProductReviewSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReviews, Long> {

    String REVIEW_COLUMNS = """
            SELECT r.id              AS id,
                   r.product_id      AS product_id,
                   p.name            AS product_name,
                   r.client_id       AS client_id,
                   COALESCE(c.full_name, c.email) AS client_name,
                   r.stars           AS stars,
                   r.reviews         AS reviews,
                   r.created_at      AS created_at,
                   r.updated_at      AS updated_at
            FROM product_reviews r
                     JOIN products p ON p.id = r.product_id
                     JOIN clients c ON c.id = r.client_id
            """;

    @Query(value = REVIEW_COLUMNS + """
            WHERE r.product_id = :productId
            ORDER BY r.created_at DESC, r.id DESC
            """,
            countQuery = "SELECT COUNT(*) FROM product_reviews WHERE product_id = :productId",
            nativeQuery = true)
    Page<ProductReviewProjection> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query(value = REVIEW_COLUMNS + """
            WHERE r.client_id = :clientId
            ORDER BY r.created_at DESC, r.id DESC
            """,
            countQuery = "SELECT COUNT(*) FROM product_reviews WHERE client_id = :clientId",
            nativeQuery = true)
    Page<ProductReviewProjection> findByClientId(@Param("clientId") Long clientId, Pageable pageable);

    @Query(value = REVIEW_COLUMNS + " WHERE r.id = :reviewId", nativeQuery = true)
    Optional<ProductReviewProjection> findReviewById(@Param("reviewId") Long reviewId);

    @Query(value = """
            SELECT COALESCE(AVG(stars), 0)::float8 AS average_stars,
                   COUNT(*)                       AS total_reviews
            FROM product_reviews
            WHERE product_id = :productId
            """, nativeQuery = true)
    ProductReviewSummaryProjection findSummaryByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT COUNT(*) > 0 FROM product_reviews WHERE product_id = :productId AND client_id = :clientId",
            nativeQuery = true)
    boolean existsByProductIdAndClientId(@Param("productId") Long productId, @Param("clientId") Long clientId);
}
