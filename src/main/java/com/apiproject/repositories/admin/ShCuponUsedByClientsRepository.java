package com.apiproject.repositories.admin;

import com.apiproject.entities.admin.ShCuponUsedByClients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShCuponUsedByClientsRepository extends JpaRepository<ShCuponUsedByClients, Long> {

    @Query(value = "SELECT COUNT(*) FROM sh_cupons_used_by_clients WHERE cupon_id = :cuponId", nativeQuery = true)
    long countByCuponId(@Param("cuponId") Long cuponId);

    @Query(value = """
            SELECT COUNT(*) FROM sh_cupons_used_by_clients
            WHERE cupon_id = :cuponId AND client_user = :clientId
            """, nativeQuery = true)
    long countByCuponIdAndClientId(@Param("cuponId") Long cuponId, @Param("clientId") Long clientId);
}
