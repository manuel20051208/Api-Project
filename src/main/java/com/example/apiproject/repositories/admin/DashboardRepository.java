package com.example.apiproject.repositories.admin;

import com.example.apiproject.entities.admin.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.Optional;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Integer> {
    Optional<Dashboard> findFirstByOrderBySalesYearDescMonthNumberDesc();
}