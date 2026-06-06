package com.example.apiproject.repositories.admin;

import com.example.apiproject.entities.general.Sale;
import com.example.apiproject.repositories.projection.DashboardProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@org.springframework.stereotype.Repository
public interface DashboardRepository extends Repository<Sale, Integer> {

    @Query(value = """
        SELECT user_id, month_number, sales_year, month_name,
               monthly_total, number_of_products, count_clients
        FROM view_of_dashboard
        WHERE user_id = :userId
        """, nativeQuery = true)
    List<DashboardProjection> findByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT SUM(count_clients) FROM view_of_dashboard WHERE user_id = :userId", nativeQuery = true)
    Long countClients(@Param("userId") Long userId);

    @Query(value = "SELECT SUM(monthly_total) FROM view_of_dashboard WHERE user_id = :userId", nativeQuery = true)
    Double getMonthlyTotal(@Param("userId") Long userId);

    @Query(value = "SELECT SUM(number_of_products) FROM view_of_dashboard WHERE user_id = :userId", nativeQuery = true)
    Long getNumberOfProducts(@Param("userId") Long userId);
}
