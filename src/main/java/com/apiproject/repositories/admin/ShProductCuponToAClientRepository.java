package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.ShProductCuponToAClient;
import com.apiproject.repositories.projection.ShProductCuponAssignmentProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShProductCuponToAClientRepository extends JpaRepository<ShProductCuponToAClient, Long> {

    /** Asignaciones de los cupones SH del admin (cupón x producto SH x cliente). */
    @Query(value = """
            SELECT spct.id, spct.client_id, c.full_name AS client_name, c.email AS client_email,
                   spct.sh_cupons_id, sc.sh_cupon_code, sc.discount, sc.cupon_date_limit,
                   spct.sh_product_id, sp.name AS product_name
            FROM sh_product_cupo_to_a_client spct
                     JOIN clients c ON c.id = spct.client_id
                     JOIN secondhand_cupons sc ON sc.id = spct.sh_cupons_id
                     JOIN secondhand_product sp ON sp.id = spct.sh_product_id
            WHERE sc.user_id = :adminId
            ORDER BY spct.id DESC
            """, nativeQuery = true)
    List<ShProductCuponAssignmentProjection> findAllByAdmin(@Param("adminId") Long adminId);

    /** Asignaciones SH del admin hacia un cliente concreto. */
    @Query(value = """
            SELECT spct.id, spct.client_id, c.full_name AS client_name, c.email AS client_email,
                   spct.sh_cupons_id, sc.sh_cupon_code, sc.discount, sc.cupon_date_limit,
                   spct.sh_product_id, sp.name AS product_name
            FROM sh_product_cupo_to_a_client spct
                     JOIN clients c ON c.id = spct.client_id
                     JOIN secondhand_cupons sc ON sc.id = spct.sh_cupons_id
                     JOIN secondhand_product sp ON sp.id = spct.sh_product_id
            WHERE sc.user_id = :adminId AND spct.client_id = :clientId
            ORDER BY spct.id DESC
            """, nativeQuery = true)
    List<ShProductCuponAssignmentProjection> findByAdminAndClient(@Param("adminId") Long adminId, @Param("clientId") Long clientId);

    /** Asignaciones de un cupón SH concreto del admin. */
    @Query(value = """
            SELECT spct.id, spct.client_id, c.full_name AS client_name, c.email AS client_email,
                   spct.sh_cupons_id, sc.sh_cupon_code, sc.discount, sc.cupon_date_limit,
                   spct.sh_product_id, sp.name AS product_name
            FROM sh_product_cupo_to_a_client spct
                     JOIN clients c ON c.id = spct.client_id
                     JOIN secondhand_cupons sc ON sc.id = spct.sh_cupons_id
                     JOIN secondhand_product sp ON sp.id = spct.sh_product_id
            WHERE sc.user_id = :adminId AND spct.sh_cupons_id = :cuponId
            ORDER BY spct.id DESC
            """, nativeQuery = true)
    List<ShProductCuponAssignmentProjection> findByAdminAndCupon(@Param("adminId") Long adminId, @Param("cuponId") Long cuponId);

    /** Borra las asignaciones SH de un cupón completo. */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM sh_product_cupo_to_a_client WHERE sh_cupons_id = :cuponId", nativeQuery = true)
    int deleteByCuponId(@Param("cuponId") Long cuponId);

    /** Borra las asignaciones SH de un cupón para un cliente concreto. */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM sh_product_cupo_to_a_client WHERE sh_cupons_id = :cuponId AND client_id = :clientId", nativeQuery = true)
    int deleteByCuponIdAndClientId(@Param("cuponId") Long cuponId, @Param("clientId") Long clientId);

    /** Cupón SH asignado al cliente y vigente para ese producto SH (id + descuento). */
    @Query(value = """
            SELECT sc.id AS cupon_id, sc.discount AS discount
            FROM sh_product_cupo_to_a_client spct
                     JOIN secondhand_cupons sc ON sc.id = spct.sh_cupons_id
            WHERE spct.client_id = :clientId
              AND spct.sh_product_id = :productId
              AND LOWER(sc.sh_cupon_code) = LOWER(:code)
              AND sc.cupon_date_limit >= NOW()
              AND (sc.quantity IS NULL OR sc.quantity > 0)
            LIMIT 1
            """, nativeQuery = true)
    java.util.Optional<com.apiproject.repositories.projection.CuponUsageProjection>
    findValidForClientAndProduct(
            @Param("clientId") Long clientId,
            @Param("productId") Long productId,
            @Param("code") String code);
}
