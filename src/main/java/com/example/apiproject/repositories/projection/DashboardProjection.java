package com.example.apiproject.repositories.projection;

public interface DashboardProjection {
    Long getUserId();
    Integer getMonthNumber();
    Integer getSalesYear();
    String getMonthName();
    Double getMonthlyTotal();
    Long getNumberOfProducts();
    Long getCountClients();
}
