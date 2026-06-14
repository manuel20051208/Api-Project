package com.example.apiproject.services.user.admin;

import com.example.apiproject.config.CacheConstants;
import com.example.apiproject.repositories.admin.ClientsSummaryViewRepository;
import com.example.apiproject.repositories.projection.ClientSummaryProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientsSummaryViewService {
    public final ClientsSummaryViewRepository clientsSummaryViewRepository;

    @Transactional(readOnly = true)
    public Page<ClientSummaryProjection> showClientsSummaryByName(Long userId, String clientName, int page) {
        Pageable pageable = PageRequest.of(0, page, latestClientsSort());
        return clientsSummaryViewRepository.findByAdminAndName(userId, clientName, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ClientSummaryProjection> showClientsSummaryByEmail(Long userId, String clientEmail, int page) {
        Pageable pageable = PageRequest.of(0, page, latestClientsSort());
        return clientsSummaryViewRepository.findByAdminAndEmail(userId, clientEmail, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.SALES, key = "#userId")
    public Page<ClientSummaryProjection> showAllForSales(Long userId) {
        Pageable pageable = PageRequest.of(0, 100, latestClientsSort());
        return clientsSummaryViewRepository.findAllByAdmin(userId, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.SALES, key = "#userId")
    public Page<ClientSummaryProjection> showAllForDashBoard(Long userId) {
        Pageable pageable = PageRequest.of(0, 5, latestClientsSort());
        return clientsSummaryViewRepository.findAllByAdmin(userId, pageable);
    }

    private Sort latestClientsSort() {
        return Sort.by(Sort.Direction.DESC, "latest_sale")
                .and(Sort.by(Sort.Direction.DESC, "id"));
    }
}
