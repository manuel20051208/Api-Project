package com.apiproject.repositories.general;

import com.apiproject.entities.general.Sale;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findById(Long id);

    /** Bloquea la venta (FOR UPDATE) para que dos cancelaciones simultaneas no reembolsen dos veces. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Sale s WHERE s.id = :id")
    Optional<Sale> findByIdForUpdate(@Param("id") Long id);
}