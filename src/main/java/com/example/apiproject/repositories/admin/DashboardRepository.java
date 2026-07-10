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
        SELECT
            user_id, month_number, sales_year, month_name,
            monthly_total, number_of_products, count_clients,
            SUM(monthly_total)      OVER (PARTITION BY user_id) AS total_sales,
            SUM(number_of_products) OVER (PARTITION BY user_id) AS total_products,
            SUM(count_clients)      OVER (PARTITION BY user_id) AS total_clients
        FROM view_of_dashboard
        WHERE user_id = :userId
        """, nativeQuery = true)
    List<DashboardProjection> findByUserId(@Param("userId") Long userId);
}
