package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.Cupon;
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
public interface CuponRepository extends JpaRepository<Cupon, Long> {

    @Query(value = """
            SELECT c.id,
                   c.cupon_code,
                   c.cupon_date_limit,
                   c.discount,
                   c.quantity,
                   u.id AS owner_id,
                   COALESCE(string_agg(DISTINCT p.name, ', '), '') AS applied_products,
                   COALESCE(string_agg(DISTINCT pca.product_id::TEXT, ','), '') AS applied_product_ids
            FROM cupons c
                     JOIN users u ON u.id = c.user_id
                     LEFT JOIN product_cupons_applied pca ON pca.cupons_id = c.id
                     LEFT JOIN products p ON p.id = pca.product_id
            WHERE c.user_id = :adminId
            GROUP BY c.id, u.id
            ORDER BY c.id DESC
            """, nativeQuery = true)
    List<CuponAdminProjection> findAllByOwner(@Param("adminId") Long adminId);

    @Query(value = "SELECT * FROM cupons WHERE id = :id FOR UPDATE", nativeQuery = true)
    Optional<Cupon> lockById(@Param("id") Long id);

    @Query(value = "SELECT * FROM cupons WHERE LOWER(cupon_code) = LOWER(:code) FOR UPDATE", nativeQuery = true)
    Optional<Cupon> lockByCode(@Param("code") String code);

    @Query(value = """
            SELECT c.id AS cupon_id, c.discount AS discount
            FROM cupons c
                     JOIN product_cupons_applied pca ON pca.cupons_id = c.id
                     JOIN products p ON p.id = :productId
            WHERE LOWER(c.cupon_code) = LOWER(:code)
              AND pca.product_id = :productId
              AND p.id_users = c.user_id
              AND c.cupon_date_limit >= :now
              AND (c.quantity IS NULL OR c.quantity > 0)
            LIMIT 1
            """, nativeQuery = true)
    Optional<CuponUsageProjection> findValidForProduct(
            @Param("code") String code,
            @Param("productId") Long productId,
            @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query(value = "UPDATE cupons SET quantity = quantity - 1 WHERE id = :id AND (quantity IS NULL OR quantity > 0)", nativeQuery = true)
    int decrementQuantity(@Param("id") Long id);
}
