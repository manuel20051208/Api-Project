package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.ServiceCupon;
import com.apiproject.repositories.projection.CuponAdminProjection;
import com.apiproject.repositories.projection.CuponUsageProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceCuponRepository extends JpaRepository<ServiceCupon, Long> {

    @Query(value = """
            SELECT c.id,
                   c.service_cupon_code AS cupon_code,
                   c.cupon_date_limit,
                   c.discount,
                   c.quantity,
                   u.id AS owner_id,
                   '' AS applied_products,
                   '' AS applied_product_ids
            FROM services_cupon c
                     JOIN users u ON u.id = c.user_id
            WHERE c.user_id = :adminId
            ORDER BY c.id DESC
            """, nativeQuery = true)
    List<CuponAdminProjection> findAllByOwner(@Param("adminId") Long adminId);

    @Query(value = "SELECT * FROM services_cupon WHERE id = :id FOR UPDATE", nativeQuery = true)
    Optional<ServiceCupon> lockById(@Param("id") Long id);

    @Query(value = """
            SELECT c.id AS cupon_id, c.discount AS discount
            FROM services_cupon c
                     JOIN users u ON u.id = c.user_id
            WHERE LOWER(c.service_cupon_code) = LOWER(:code)
              AND u.id = :ownerId
              AND c.cupon_date_limit >= :now
              AND (c.quantity IS NULL OR c.quantity > 0)
            LIMIT 1
            """, nativeQuery = true)
    Optional<CuponUsageProjection> findValidForOwner(
            @Param("code") String code,
            @Param("ownerId") Long ownerId,
            @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query(value = "UPDATE services_cupon SET quantity = quantity - 1 WHERE id = :id AND (quantity IS NULL OR quantity > 0)", nativeQuery = true)
    int decrementQuantity(@Param("id") Long id);
}
