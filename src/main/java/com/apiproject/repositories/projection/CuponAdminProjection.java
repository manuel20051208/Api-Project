package com.apiproject.repositories.projection;

import java.time.LocalDateTime;

public interface CuponAdminProjection {
    Long getId();
    String getCuponCode();
    LocalDateTime getCuponDateLimit();
    Double getDiscount();
    Integer getQuantity();
    Long getOwnerId();
    String getAppliedProducts();
    String getAppliedProductIds();
}
