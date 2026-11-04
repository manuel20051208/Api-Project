package com.apiproject.repositories.projection;

import java.time.LocalDateTime;

public interface ProductReviewProjection {
    Long getId();
    Long getProductId();
    String getProductName();
    Long getClientId();
    String getClientName();
    Integer getStars();
    String getReviews();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
