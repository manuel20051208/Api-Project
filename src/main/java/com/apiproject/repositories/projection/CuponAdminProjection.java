package com.apiproject.repositories.projection;

import java.math.BigDecimal;

import java.time.LocalDateTime;

public interface CuponAdminProjection {
    Long getId();
    String getCuponCode();
    LocalDateTime getCuponDateLimit();
    BigDecimal getDiscount();
    Integer getQuantity();
    Long getOwnerId();
    String getAppliedProducts();
    String getAppliedProductIds();
}
