package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.ProductCuponToAClient;
import com.apiproject.repositories.projection.ProductCuponAssignmentProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCuponToAClientRepository extends JpaRepository<ProductCuponToAClient, Long> {

    @Query(value = """
            SELECT pct.id, pct.client_id, c.full_name AS client_name, c.email AS client_email,
                   pct.cupon_id, ct.cupon_code, ct.discount, ct.cupon_date_limit,
                   pct.product_id, p.name AS product_name
            FROM product_cupon_to_a_client pct
                     JOIN clients c ON c.id = pct.client_id
                     JOIN cupons ct ON ct.id = pct.cupon_id
                     JOIN products p ON p.id = pct.product_id
            WHERE ct.user_id = :adminId
            ORDER BY pct.id DESC
            """, nativeQuery = true)
    List<ProductCuponAssignmentProjection> findAllByAdmin(@Param("adminId") Long adminId);

    @Query(value = """
            SELECT pct.id, pct.client_id, c.full_name AS client_name, c.email AS client_email,
                   pct.cupon_id, ct.cupon_code, ct.discount, ct.cupon_date_limit,
                   pct.product_id, p.name AS product_name
            FROM product_cupon_to_a_client pct
                     JOIN clients c ON c.id = pct.client_id
                     JOIN cupons ct ON ct.id = pct.cupon_id
                     JOIN products p ON p.id = pct.product_id
            WHERE ct.user_id = :adminId AND pct.client_id = :clientId
            ORDER BY pct.id DESC
            """, nativeQuery = true)
    List<ProductCuponAssignmentProjection> findByAdminAndClient(@Param("adminId") Long adminId, @Param("clientId") Long clientId);

    @Query(value = """
            SELECT pct.id, pct.client_id, c.full_name AS client_name, c.email AS client_email,
                   pct.cupon_id, ct.cupon_code, ct.discount, ct.cupon_date_limit,
                   pct.product_id, p.name AS product_name
            FROM product_cupon_to_a_client pct
                     JOIN clients c ON c.id = pct.client_id
                     JOIN cupons ct ON ct.id = pct.cupon_id
                     JOIN products p ON p.id = pct.product_id
            WHERE ct.user_id = :adminId AND pct.cupon_id = :cuponId
            ORDER BY pct.id DESC
            """, nativeQuery = true)
    List<ProductCuponAssignmentProjection> findByAdminAndCupon(@Param("adminId") Long adminId, @Param("cuponId") Long cuponId);

    @Query(value = """
            SELECT pct.id, pct.client_id, c.full_name AS client_name, c.email AS client_email,
                   pct.cupon_id, ct.cupon_code, ct.discount, ct.cupon_date_limit,
                   pct.product_id, p.name AS product_name
            FROM product_cupon_to_a_client pct
                     JOIN clients c ON c.id = pct.client_id
                     JOIN cupons ct ON ct.id = pct.cupon_id
                     JOIN products p ON p.id = pct.product_id
            WHERE pct.client_id = :clientId
            ORDER BY pct.id DESC
            """, nativeQuery = true)
    List<ProductCuponAssignmentProjection> findAllByClient(@Param("clientId") Long clientId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_cupon_to_a_client WHERE cupon_id = :cuponId", nativeQuery = true)
    int deleteByCuponId(@Param("cuponId") Long cuponId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_cupon_to_a_client WHERE cupon_id = :cuponId AND client_id = :clientId", nativeQuery = true)
    int deleteByCuponIdAndClientId(@Param("cuponId") Long cuponId, @Param("clientId") Long clientId);

    @Query(value = """
            SELECT EXISTS(
                SELECT 1 FROM product_cupon_to_a_client pct
                JOIN cupons ct ON ct.id = pct.cupon_id
                WHERE pct.client_id = :clientId
                  AND pct.product_id = :productId
                  AND LOWER(ct.cupon_code) = LOWER(:code)
                  AND ct.cupon_date_limit >= NOW()
                  AND (ct.quantity IS NULL OR ct.quantity > 0)
            )
            """, nativeQuery = true)
    boolean existsValidForClientAndProduct(
            @Param("clientId") Long clientId,
            @Param("productId") Long productId,
            @Param("code") String code);

    @Query(value = """
            SELECT ct.id AS cupon_id, ct.discount AS discount
            FROM product_cupon_to_a_client pct
                     JOIN cupons ct ON ct.id = pct.cupon_id
            WHERE pct.client_id = :clientId
              AND pct.product_id = :productId
              AND LOWER(ct.cupon_code) = LOWER(:code)
              AND ct.cupon_date_limit >= NOW()
              AND (ct.quantity IS NULL OR ct.quantity > 0)
            LIMIT 1
            """, nativeQuery = true)
    java.util.Optional<com.apiproject.repositories.projection.CuponUsageProjection>
    findValidForClientAndProduct(
            @Param("clientId") Long clientId,
            @Param("productId") Long productId,
            @Param("code") String code);
}
