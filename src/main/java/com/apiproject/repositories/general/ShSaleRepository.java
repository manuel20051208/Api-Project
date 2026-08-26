package com.apiproject.repositories.general;

import com.apiproject.entities.general.ShSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShSaleRepository extends JpaRepository<ShSale, Long> {
}
