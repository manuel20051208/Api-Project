package com.example.apiproject.repositories.projection;

import java.time.LocalDateTime;

public interface PaymentCardDetailsProjection {
    Long getId();
    Long getClientId();
    String getCardHolderName();
    String getBrand();
    String getLastFour();
    boolean isActive();
    LocalDateTime getCreatedAt();
}
