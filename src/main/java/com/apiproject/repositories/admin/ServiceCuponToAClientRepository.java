package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.ServiceCuponToAClient;
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

    @Query(value = """
            SELECT sct.id, sct.client_id, c.full_name AS client_name, c.email AS client_email,
                   sct.service_cupon_id, sc.service_cupon_code, sc.discount, sc.cupon_date_limit,
                   sct.service_id, so.name_of_service AS service_name
            FROM service_cupon_to_a_client sct
                     JOIN clients c ON c.id = sct.client_id
                     JOIN services_cupon sc ON sc.id = sct.service_cupon_id
                     JOIN services_offered so ON so.id = sct.service_id
            WHERE sc.user_id = :adminId
            ORDER BY sct.id DESC
            """, nativeQuery = true)
    List<ServiceCuponAssignmentProjection> findAllByAdmin(@Param("adminId") Long adminId);

    @Query(value = """
            SELECT sct.id, sct.client_id, c.full_name AS client_name, c.email AS client_email,
                   sct.service_cupon_id, sc.service_cupon_code, sc.discount, sc.cupon_date_limit,
                   sct.service_id, so.name_of_service AS service_name
            FROM service_cupon_to_a_client sct
                     JOIN clients c ON c.id = sct.client_id
                     JOIN services_cupon sc ON sc.id = sct.service_cupon_id
                     JOIN services_offered so ON so.id = sct.service_id
            WHERE sc.user_id = :adminId AND sct.client_id = :clientId
            ORDER BY sct.id DESC
            """, nativeQuery = true)
    List<ServiceCuponAssignmentProjection> findByAdminAndClient(@Param("adminId") Long adminId, @Param("clientId") Long clientId);

    @Query(value = """
            SELECT sct.id, sct.client_id, c.full_name AS client_name, c.email AS client_email,
                   sct.service_cupon_id, sc.service_cupon_code, sc.discount, sc.cupon_date_limit,
                   sct.service_id, so.name_of_service AS service_name
            FROM service_cupon_to_a_client sct
                     JOIN clients c ON c.id = sct.client_id
                     JOIN services_cupon sc ON sc.id = sct.service_cupon_id
                     JOIN services_offered so ON so.id = sct.service_id
            WHERE sc.user_id = :adminId AND sct.service_cupon_id = :cuponId
            ORDER BY sct.id DESC
            """, nativeQuery = true)
    List<ServiceCuponAssignmentProjection> findByAdminAndCupon(@Param("adminId") Long adminId, @Param("cuponId") Long cuponId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM service_cupon_to_a_client WHERE service_cupon_id = :cuponId", nativeQuery = true)
    int deleteByCuponId(@Param("cuponId") Long cuponId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM service_cupon_to_a_client WHERE service_cupon_id = :cuponId AND client_id = :clientId", nativeQuery = true)
    int deleteByCuponIdAndClientId(@Param("cuponId") Long cuponId, @Param("clientId") Long clientId);

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
}
