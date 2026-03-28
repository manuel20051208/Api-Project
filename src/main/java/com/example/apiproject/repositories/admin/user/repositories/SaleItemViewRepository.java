package com.example.apiproject.repositories.admin.user.repositories;

import com.example.apiproject.entities.admin.SaleItemView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleItemViewRepository extends JpaRepository<SaleItemView, Long> {
    List<SaleItemView> findAllByClientNameIgnoreCase(String clientName);
    List<SaleItemView> findAllByProductNameIgnoreCase(String productName);
    List<SaleItemView> findAll();
}