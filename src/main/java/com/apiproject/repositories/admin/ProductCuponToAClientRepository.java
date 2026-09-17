package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.ProductCuponToAClient;
import com.apiproject.repositories.projection.CouponAssignmentProjection;
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

    /** Asignaciones de los cupones del admin (cupón x producto x cliente). */
    @Query(value = """
            SELECT pct.id, pct.client_id, c.full_name AS client_name, c.email AS client_email,
                   pct.cupon_id, ct.cupon_code, ct.discount, ct.cupon_date_limit,
                   pct.usage_limit AS usage_limit,
                   pct.product_id, p.name AS product_name
            FROM product_cupon_to_a_client pct
                     JOIN clients c ON c.id = pct.client_id
                     JOIN cupons ct ON ct.id = pct.cupon_id
                     JOIN products p ON p.id = pct.product_id
            WHERE ct.user_id = :adminId
            ORDER BY pct.id DESC
            """, nativeQuery = true)
    List<ProductCuponAssignmentProjection> findAllByAdmin(@Param("adminId") Long adminId);

    /** Asignaciones del admin hacia un cliente concreto. */
    @Query(value = """
            SELECT pct.id, pct.client_id, c.full_name AS client_name, c.email AS client_email,
                   pct.cupon_id, ct.cupon_code, ct.discount, ct.cupon_date_limit,
                   pct.usage_limit AS usage_limit,
                   pct.product_id, p.name AS product_name
            FROM product_cupon_to_a_client pct
                     JOIN clients c ON c.id = pct.client_id
                     JOIN cupons ct ON ct.id = pct.cupon_id
                     JOIN products p ON p.id = pct.product_id
            WHERE ct.user_id = :adminId AND pct.client_id = :clientId
            ORDER BY pct.id DESC
            """, nativeQuery = true)
    List<ProductCuponAssignmentProjection> findByAdminAndClient(@Param("adminId") Long adminId, @Param("clientId") Long clientId);

    /** Asignaciones de un cupón concreto del admin. */
    @Query(value = """
            SELECT pct.id, pct.client_id, c.full_name AS client_name, c.email AS client_email,
                   pct.cupon_id, ct.cupon_code, ct.discount, ct.cupon_date_limit,
                   pct.usage_limit AS usage_limit,
                   pct.product_id, p.name AS product_name
            FROM product_cupon_to_a_client pct
                     JOIN clients c ON c.id = pct.client_id
                     JOIN cupons ct ON ct.id = pct.cupon_id
                     JOIN products p ON p.id = pct.product_id
            WHERE ct.user_id = :adminId AND pct.cupon_id = :cuponId
            ORDER BY pct.id DESC
            """, nativeQuery = true)
    List<ProductCuponAssignmentProjection> findByAdminAndCupon(@Param("adminId") Long adminId, @Param("cuponId") Long cuponId);

    /** Asignaciones de un cupón del admin con usage_limit y usos reales por cliente (diálogo "Clientes"). */
    @Query(value = """
            SELECT pct.id,
                   c.full_name AS client_name,
                   c.email AS client_email,
                   pct.usage_limit AS usage_limit,
                   COALESCE(used.used_count, 0) AS used_count,
                   p.name AS product_name
            FROM product_cupon_to_a_client pct
                     JOIN clients c ON c.id = pct.client_id
                     JOIN cupons ct ON ct.id = pct.cupon_id
                     JOIN products p ON p.id = pct.product_id
                     LEFT JOIN (
                        SELECT client_user, cupon_id, COUNT(*) AS used_count
                        FROM cupons_used_by_clients
                        GROUP BY client_user, cupon_id
                     ) used ON used.client_user = pct.client_id AND used.cupon_id = pct.cupon_id
            WHERE ct.user_id = :adminId AND pct.cupon_id = :cuponId
            ORDER BY pct.id DESC
            """, nativeQuery = true)
    List<CouponAssignmentProjection> findAssignmentsWithUsageByAdminAndCupon(
            @Param("adminId") Long adminId,
            @Param("cuponId") Long cuponId);

    /** Asignaciones de cualquier admin hacia el cliente (para la app del cliente). */
    @Query(value = """
            SELECT pct.id, pct.client_id, c.full_name AS client_name, c.email AS client_email,
                   pct.cupon_id, ct.cupon_code, ct.discount, ct.cupon_date_limit,
                   pct.usage_limit AS usage_limit,
                   pct.product_id, p.name AS product_name
            FROM product_cupon_to_a_client pct
                     JOIN clients c ON c.id = pct.client_id
                     JOIN cupons ct ON ct.id = pct.cupon_id
                     JOIN products p ON p.id = pct.product_id
            WHERE pct.client_id = :clientId
            ORDER BY pct.id DESC
            """, nativeQuery = true)
    List<ProductCuponAssignmentProjection> findAllByClient(@Param("clientId") Long clientId);

    /** Borra las asignaciones de un cupón completo. */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_cupon_to_a_client WHERE cupon_id = :cuponId", nativeQuery = true)
    int deleteByCuponId(@Param("cuponId") Long cuponId);

    /** Cambia el limite de usos de todas las asignaciones de un cupon (a todos los clientes). */
    @Modifying
    @Transactional
    @Query(value = "UPDATE product_cupon_to_a_client SET usage_limit = :usageLimit WHERE cupon_id = :cuponId", nativeQuery = true)
    int updateUsageLimitByCupon(@Param("cuponId") Long cuponId, @Param("usageLimit") Integer usageLimit);

    /** Borra las asignaciones de un cupón para un cliente concreto. */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_cupon_to_a_client WHERE cupon_id = :cuponId AND client_id = :clientId", nativeQuery = true)
    int deleteByCuponIdAndClientId(@Param("cuponId") Long cuponId, @Param("clientId") Long clientId);

    /** True si al cliente le fue asignado (y está vigente) ese cupón para ese producto. */
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

    /** Fila de asignación de un cupón a un cliente para un producto concreto (evita duplicados al enviar). */
    @Query(value = """
            SELECT pct.id, pct.client_id, c.full_name AS client_name, c.email AS client_email,
                   pct.cupon_id, ct.cupon_code, ct.discount, ct.cupon_date_limit,
                   pct.usage_limit AS usage_limit,
                   pct.product_id, p.name AS product_name
            FROM product_cupon_to_a_client pct
                     JOIN clients c ON c.id = pct.client_id
                     JOIN cupons ct ON ct.id = pct.cupon_id
                     JOIN products p ON p.id = pct.product_id
            WHERE ct.user_id = :adminId AND pct.client_id = :clientId AND pct.product_id = :productId
            LIMIT 1
            """, nativeQuery = true)
    java.util.Optional<ProductCuponAssignmentProjection> findByAdminAndClientAndProduct(
            @Param("adminId") Long adminId,
            @Param("clientId") Long clientId,
            @Param("productId") Long productId);

    /** Cupón asignado al cliente y vigente para ese producto (id + descuento). */
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

    /** Limite de usos asignado a un cliente para un cupon concreto (NULL = ilimitado). */
    @Query(value = """
            SELECT usage_limit FROM product_cupon_to_a_client
            WHERE client_id = :clientId AND cupon_id = :cuponId
            LIMIT 1
            """, nativeQuery = true)
    java.util.Optional<Integer> findUsageLimitByClientAndCupon(
            @Param("clientId") Long clientId,
            @Param("cuponId") Long cuponId);
}
