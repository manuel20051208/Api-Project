package com.example.apiproject.repositories.admin;


import com.example.apiproject.entities.admin.ReportDashboard;
import com.example.apiproject.repositories.projection.ReportDashboardProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ReportDashboardRepository extends
        Repository<ReportDashboard, Integer> {

    @Query(value = """
            SELECT *
            FROM report_view_dashboard
            WHERE user_id = :user_id
            """, nativeQuery = true)
    List<ReportDashboardProjection> findAllDashboard(@Param("user_id") Long user_id);
}