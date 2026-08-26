package com.apiproject.repositories.projection;

import java.time.LocalDateTime;

public interface ShProductCardProjection {
    Long getId();
    String getName();
    Double getPrice();
    Integer getStock();
    String getCategory();
    String getDescription();
    Boolean getActive();
    LocalDateTime getTimeOfUse();
    Long getLevelOfSecondHandProduct();
    String getOwnerName();
    String getImageUrl();
}
