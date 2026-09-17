package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.CuponUsedByClients;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CuponUsedByClientsRepository extends JpaRepository<CuponUsedByClients, Long> {

    /** Veces que un cupón fue usado en compras (todas). */
    @Query(value = "SELECT COUNT(*) FROM cupons_used_by_clients WHERE cupon_id = :cuponId", nativeQuery = true)
    long countByCuponId(@Param("cuponId") Long cuponId);

    /** Veces que un cliente concreto usó un cupón (desde el contador acumulado). */
    @Query(value = """
            SELECT COALESCE(MAX(usage_count), 0) FROM cupons_used_by_clients
            WHERE cupon_id = :cuponId AND client_user = :clientId
            """, nativeQuery = true)
    long countByCuponIdAndClientId(@Param("cuponId") Long cuponId, @Param("clientId") Long clientId);

    /** Contador actual de usos de un cliente con un cupón de un admin (para incrementar al registrar uso). */
    @Query(value = """
            SELECT COALESCE(MAX(u.usage_count), 0)
            FROM cupons_used_by_clients u
                     JOIN cupons c ON c.id = u.cupon_id
            WHERE u.cupon_id = :cuponId AND u.client_user = :clientId AND c.user_id = :adminId
            """, nativeQuery = true)
    long usageCountByCuponAndClient(
            @Param("cuponId") Long cuponId,
            @Param("clientId") Long clientId,
            @Param("adminId") Long adminId);

    /** Borra los registros de uso de un cupón (la BD real no tiene ON DELETE CASCADE en esta FK). */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cupons_used_by_clients WHERE cupon_id = :cuponId", nativeQuery = true)
    void deleteByCuponId(@Param("cuponId") Long cuponId);
}
