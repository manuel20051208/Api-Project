package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.ShCuponUsedByClients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShCuponUsedByClientsRepository extends JpaRepository<ShCuponUsedByClients, Long> {

    /** Veces que un cupón SH fue usado en compras (todas). */
    @Query(value = "SELECT COUNT(*) FROM sh_cupons_used_by_clients WHERE cupon_id = :cuponId", nativeQuery = true)
    long countByCuponId(@Param("cuponId") Long cuponId);

    /** Veces que un cliente concreto usó un cupón SH (desde el contador acumulado). */
    @Query(value = """
            SELECT COALESCE(MAX(usage_count), 0) FROM sh_cupons_used_by_clients
            WHERE cupon_id = :cuponId AND client_user = :clientId
            """, nativeQuery = true)
    long countByCuponIdAndClientId(@Param("cuponId") Long cuponId, @Param("clientId") Long clientId);

    /** Contador actual de usos de un cliente con un cupón SH de un admin (para incrementar al registrar uso). */
    @Query(value = """
            SELECT COALESCE(MAX(u.usage_count), 0)
            FROM sh_cupons_used_by_clients u
                     JOIN secondhand_cupons sc ON sc.id = u.cupon_id
            WHERE u.cupon_id = :cuponId AND u.client_user = :clientId AND sc.user_id = :adminId
            """, nativeQuery = true)
    long usageCountByCuponAndClient(
            @Param("cuponId") Long cuponId,
            @Param("clientId") Long clientId,
            @Param("adminId") Long adminId);
}
