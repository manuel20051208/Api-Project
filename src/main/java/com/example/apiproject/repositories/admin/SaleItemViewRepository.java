package com.example.apiproject.repositories.admin;



import com.example.apiproject.entities.admin.SaleItemView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleItemViewRepository extends JpaRepository<SaleItemView, Long> {
    List<SaleItemView> findAllByClientNameIgnoreCase(String clientName);
    List<SaleItemView> findAllByProductNameIgnoreCase(String productName);
    Page<SaleItemView> findAll(Pageable pageable);
}