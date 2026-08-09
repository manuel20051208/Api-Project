package com.apiproject.repositories.general;

import com.apiproject.entities.general.SalesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository<SalesItem, Long> {
}