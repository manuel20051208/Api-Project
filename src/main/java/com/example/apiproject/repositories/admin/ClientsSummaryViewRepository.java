package com.example.apiproject.repositories.admin;

import com.example.apiproject.entities.admin.ClientsSummaryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientsSummaryViewRepository
        extends JpaRepository<ClientsSummaryView, Long> {

    List<ClientsSummaryView> findAllByClientNameIgnoreCase(String clientName);
    List<ClientsSummaryView> findAllByClientEmailIgnoreCase(String clientEmail);
}