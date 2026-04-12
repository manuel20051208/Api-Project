package com.example.apiproject.repositories.general;

import com.example.apiproject.entities.general.entities.SalesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository<SalesItem, Long> {
}