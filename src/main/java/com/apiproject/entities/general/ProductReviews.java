package com.apiproject.entities.general;

import com.apiproject.entities.client.UserClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * Reseña de un producto hecha por un cliente.
 * Regla: una sola reseña por cliente y producto (constraint uk_product_reviews_product_client).
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@DynamicUpdate
@Entity
@Table(name = "product_reviews")
public class ProductReviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private UserClient userClient;

    @Column(nullable = false)
    private Integer stars;

    @Column(nullable = false, length = 1000)
    private String reviews;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
