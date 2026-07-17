package com.example.apiproject.repositories.admin;


import com.example.apiproject.entities.client.UserClient;
import com.example.apiproject.repositories.projection.ClientSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

@org.springframework.stereotype.Repository
// quitamos el extends de JPA para aligerar la api y no cargar metodos del ORM (save(), delete() e.t.c...)
public interface ClientsSummaryViewRepository
        extends Repository<UserClient, Long> {

    @Query(value = "SELECT * FROM clients_summary WHERE user_id = :userId",
            countQuery = "SELECT count(*) FROM clients_summary WHERE user_id = :userId",
            nativeQuery = true)
    Page<ClientSummaryProjection> findAllByAdmin(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM clients_summary WHERE user_id = :userId AND full_name ILIKE :clientName",
            countQuery = "SELECT count(*) FROM clients_summary WHERE user_id = :userId AND full_name ILIKE :clientName",
            nativeQuery = true)
    Page<ClientSummaryProjection> findByAdminAndName(
            @Param("userId") Long userId,
            @Param("clientName") String clientName,
            Pageable pageable
    );

    @Query(value = "SELECT * FROM clients_summary WHERE user_id = :userId AND email ILIKE :clientEmail",
            countQuery = "SELECT count(*) FROM clients_summary WHERE user_id = :userId AND email ILIKE :clientEmail",
            nativeQuery = true)
    Page<ClientSummaryProjection> findByAdminAndEmail(
            @Param("userId") Long userId,
            @Param("clientEmail") String clientEmail,
            Pageable pageable
    );
}
