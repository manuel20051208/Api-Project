package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.ServiceCuponToAClient;
import com.apiproject.repositories.projection.CouponAssignmentProjection;
import com.apiproject.repositories.projection.ServiceCuponAssignmentProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceCuponToAClientRepository extends JpaRepository<ServiceCuponToAClient, Long> {

    /** Asignaciones de los cupones de servicio del admin (cupón x servicio x cliente). */
    @Query(value = """
            SELECT sct.id, sct.client_id, c.full_name AS client_name, c.email AS client_email,
                   sct.service_cupon_id, sc.service_cupon_code, sc.discount, sc.cupon_date_limit,
                   sct.usage_limit AS usage_limit,
                   sct.service_id, so.name_of_service AS service_name
            FROM service_cupon_to_a_client sct
                     JOIN clients c ON c.id = sct.client_id
                     JOIN services_cupon sc ON sc.id = sct.service_cupon_id
                     JOIN services_offered so ON so.id = sct.service_id
            WHERE sc.user_id = :adminId
            ORDER BY sct.id DESC
            """, nativeQuery = true)
    List<ServiceCuponAssignmentProjection> findAllByAdmin(@Param("adminId") Long adminId);

    /** Asignaciones de servicio del admin hacia un cliente concreto. */
    @Query(value = """
            SELECT sct.id, sct.client_id, c.full_name AS client_name, c.email AS client_email,
                   sct.service_cupon_id, sc.service_cupon_code, sc.discount, sc.cupon_date_limit,
                   sct.usage_limit AS usage_limit,
                   sct.service_id, so.name_of_service AS service_name
            FROM service_cupon_to_a_client sct
                     JOIN clients c ON c.id = sct.client_id
                     JOIN services_cupon sc ON sc.id = sct.service_cupon_id
                     JOIN services_offered so ON so.id = sct.service_id
            WHERE sc.user_id = :adminId AND sct.client_id = :clientId
            ORDER BY sct.id DESC
            """, nativeQuery = true)
    List<ServiceCuponAssignmentProjection> findByAdminAndClient(@Param("adminId") Long adminId, @Param("clientId") Long clientId);

    /** Asignaciones de un cupón de servicio concreto del admin. */
    @Query(value = """
            SELECT sct.id, sct.client_id, c.full_name AS client_name, c.email AS client_email,
                   sct.service_cupon_id, sc.service_cupon_code, sc.discount, sc.cupon_date_limit,
                   sct.usage_limit AS usage_limit,
                   sct.service_id, so.name_of_service AS service_name
            FROM service_cupon_to_a_client sct
                     JOIN clients c ON c.id = sct.client_id
                     JOIN services_cupon sc ON sc.id = sct.service_cupon_id
                     JOIN services_offered so ON so.id = sct.service_id
            WHERE sc.user_id = :adminId AND sct.service_cupon_id = :cuponId
            ORDER BY sct.id DESC
            """, nativeQuery = true)
    List<ServiceCuponAssignmentProjection> findByAdminAndCupon(@Param("adminId") Long adminId, @Param("cuponId") Long cuponId);

    /** Borra las asignaciones de un cupón de servicio completo. */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM service_cupon_to_a_client WHERE service_cupon_id = :cuponId", nativeQuery = true)
    int deleteByCuponId(@Param("cuponId") Long cuponId);

    /** Cambia el limite de usos de todas las asignaciones de un cupón de servicio (a todos los clientes). */
    @Modifying
    @Transactional
    @Query(value = "UPDATE service_cupon_to_a_client SET usage_limit = :usageLimit WHERE service_cupon_id = :cuponId", nativeQuery = true)
    int updateUsageLimitByCupon(@Param("cuponId") Long cuponId, @Param("usageLimit") Integer usageLimit);

    /** Borra las asignaciones de un cupón de servicio para un cliente concreto. */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM service_cupon_to_a_client WHERE service_cupon_id = :cuponId AND client_id = :clientId", nativeQuery = true)
    int deleteByCuponIdAndClientId(@Param("cuponId") Long cuponId, @Param("clientId") Long clientId);

    /** Cupón de servicio asignado al cliente y vigente para ese servicio (id + descuento). */
    @Query(value = """
            SELECT sc.id AS cupon_id, sc.discount AS discount
            FROM service_cupon_to_a_client sct
                     JOIN services_cupon sc ON sc.id = sct.service_cupon_id
            WHERE sct.client_id = :clientId
              AND sct.service_id = :serviceId
              AND LOWER(sc.service_cupon_code) = LOWER(:code)
              AND sc.cupon_date_limit >= NOW()
              AND (sc.quantity IS NULL OR sc.quantity > 0)
            LIMIT 1
            """, nativeQuery = true)
    java.util.Optional<com.apiproject.repositories.projection.CuponUsageProjection>
    findValidForClientAndService(
            @Param("clientId") Long clientId,
            @Param("serviceId") Long serviceId,
            @Param("code") String code);

    /** Fila de asignación de servicio de un cupón a un cliente y servicio concreto (evita duplicados). */
    @Query(value = """
            SELECT sct.id, sct.client_id, c.full_name AS client_name, c.email AS client_email,
                   sct.service_cupon_id, sc.service_cupon_code, sc.discount, sc.cupon_date_limit,
                   sct.usage_limit AS usage_limit,
                   sct.service_id, so.name_of_service AS service_name
            FROM service_cupon_to_a_client sct
                     JOIN clients c ON c.id = sct.client_id
                     JOIN services_cupon sc ON sc.id = sct.service_cupon_id
                     JOIN services_offered so ON so.id = sct.service_id
            WHERE sc.user_id = :adminId AND sct.client_id = :clientId AND sct.service_id = :serviceId
            LIMIT 1
            """, nativeQuery = true)
    java.util.Optional<ServiceCuponAssignmentProjection> findByAdminAndClientAndService(
            @Param("adminId") Long adminId,
            @Param("clientId") Long clientId,
            @Param("serviceId") Long serviceId);

    /** Asignaciones de un cupón de servicio del admin con usage_limit y usos reales por cliente (diálogo "Clientes"). */
    @Query(value = """
            SELECT sct.id,
                   c.full_name AS client_name,
                   c.email AS client_email,
                   sct.usage_limit AS usage_limit,
                   COALESCE(used.used_count, 0) AS used_count,
                   so.name_of_service AS product_name
            FROM service_cupon_to_a_client sct
                     JOIN clients c ON c.id = sct.client_id
                     JOIN services_cupon sc ON sc.id = sct.service_cupon_id
                     JOIN services_offered so ON so.id = sct.service_id
                     LEFT JOIN (
                        SELECT client_user, service_cupon_id, MAX(usage_count) AS used_count
                        FROM services_cupons_used_by_clients
                        GROUP BY client_user, service_cupon_id
                     ) used ON used.client_user = sct.client_id AND used.service_cupon_id = sct.service_cupon_id
            WHERE sc.user_id = :adminId AND sct.service_cupon_id = :cuponId
            ORDER BY sct.id DESC
            """, nativeQuery = true)
    List<CouponAssignmentProjection> findAssignmentsWithUsageByAdminAndCupon(
            @Param("adminId") Long adminId,
            @Param("cuponId") Long cuponId);
}
