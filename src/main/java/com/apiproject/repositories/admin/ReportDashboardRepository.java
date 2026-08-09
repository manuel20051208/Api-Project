package com.apiproject.repositories.admin;


import com.apiproject.entities.admin.ReportDashboard;
import com.apiproject.repositories.projection.ReportDashboardProjection;
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