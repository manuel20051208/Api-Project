package com.apiproject.repositories.general;

import com.apiproject.entities.general.ShSale;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShSaleRepository extends JpaRepository<ShSale, Long> {

    /** Bloquea la venta (FOR UPDATE) para que dos cancelaciones simultaneas no reembolsen dos veces. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ShSale s WHERE s.id = :id")
    Optional<ShSale> findByIdForUpdate(@Param("id") Long id);
}
