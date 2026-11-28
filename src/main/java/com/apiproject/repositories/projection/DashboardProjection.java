package com.apiproject.repositories.projection;

import java.math.BigDecimal;

public interface DashboardProjection {
    Long getUserId();
    Integer getMonthNumber();
    Integer getSalesYear();
    String getMonthName();
    BigDecimal getMonthlyTotal();
    Long getNumberOfProducts();
    Long getCountClients();
    BigDecimal getTotalSales();
    Long getTotalProducts();
    Long getTotalClients();
}
