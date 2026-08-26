package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.SecondHandCupon;
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
public interface SecondHandCuponRepository extends JpaRepository<SecondHandCupon, Long> {

    @Query(value = """
            SELECT c.id,
                   c.sh_cupon_code AS cupon_code,
                   c.cupon_date_limit,
                   c.discount,
                   c.quantity,
                   u.id AS owner_id,
                   COALESCE(string_agg(DISTINCT p.name, ', '), '') AS applied_products,
                   COALESCE(string_agg(DISTINCT spca.sh_product_id::TEXT, ','), '') AS applied_product_ids
            FROM secondhand_cupons c
                     JOIN users u ON u.id = c.user_id
                     LEFT JOIN secondhand_product_cupons_applied spca ON spca.sh_cupons_id = c.id
                     LEFT JOIN secondhand_product p ON p.id = spca.sh_product_id
            WHERE c.user_id = :adminId
            GROUP BY c.id, u.id
            ORDER BY c.id DESC
            """, nativeQuery = true)
    List<CuponAdminProjection> findAllByOwner(@Param("adminId") Long adminId);

    @Query(value = "SELECT * FROM secondhand_cupons WHERE id = :id FOR UPDATE", nativeQuery = true)
    Optional<SecondHandCupon> lockById(@Param("id") Long id);

    @Query(value = "SELECT * FROM secondhand_cupons WHERE LOWER(sh_cupon_code) = LOWER(:code) FOR UPDATE", nativeQuery = true)
    Optional<SecondHandCupon> lockByCode(@Param("code") String code);

    @Query(value = """
            SELECT c.id AS cupon_id, c.discount AS discount
            FROM secondhand_cupons c
                     JOIN secondhand_product_cupons_applied spca ON spca.sh_cupons_id = c.id
                     JOIN secondhand_product p ON p.id = spca.sh_product_id
            WHERE LOWER(c.sh_cupon_code) = LOWER(:code)
              AND spca.sh_product_id = :productId
              AND p.id_users = c.user_id
              AND c.cupon_date_limit >= :now
              AND (c.quantity IS NULL OR c.quantity > 0)
            LIMIT 1
            """, nativeQuery = true)
    Optional<CuponUsageProjection> findValidForShProduct(
            @Param("code") String code,
            @Param("productId") Long productId,
            @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query(value = "UPDATE secondhand_cupons SET quantity = quantity - 1 WHERE id = :id AND (quantity IS NULL OR quantity > 0)", nativeQuery = true)
    int decrementQuantity(@Param("id") Long id);
}
