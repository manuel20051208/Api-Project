package com.apiproject.services.admin;

import com.apiproject.config.CacheConstants;
import com.apiproject.repositories.admin.ClientsSummaryViewRepository;
import com.apiproject.repositories.projection.ClientSummaryProjection;
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
    private static final int MAX_PAGE_SIZE = 100;

    public final ClientsSummaryViewRepository clientsSummaryViewRepository;

    @Transactional(readOnly = true)
    public Page<ClientSummaryProjection> showClientsSummaryByName(Long userId, String clientName, int page, int size) {
        return clientsSummaryViewRepository.findByAdminAndName(userId, clientName, pageable(page, size));
    }

    @Transactional(readOnly = true)
    public Page<ClientSummaryProjection> showClientsSummaryByEmail(Long userId, String clientEmail, int page, int size) {
        return clientsSummaryViewRepository.findByAdminAndEmail(userId, clientEmail, pageable(page, size));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.SALES, key = "'sales-' + #userId + '-' + #page + '-' + #size")
    public Page<ClientSummaryProjection> showAllForSales(Long userId, int page, int size) {
        return clientsSummaryViewRepository.findAllByAdmin(userId, pageable(page, size));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.SALES, key = "'dashboard-' + #userId")
    public Page<ClientSummaryProjection> showAllForDashBoard(Long userId) {
        Pageable pageable = PageRequest.of(0, 5, latestClientsSort());
        return clientsSummaryViewRepository.findAllByAdmin(userId, pageable);
    }

    /**
     * `page` es base 0 y `size` queda acotado a [1, 100] para evitar páginas de tamaño 0 (400)
     * o respuestas gigantes.
     */
    private Pageable pageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize, latestClientsSort());
    }

    private Sort latestClientsSort() {
        return Sort.by(Sort.Direction.DESC, "latest_sale")
                .and(Sort.by(Sort.Direction.ASC, "full_name"))
                .and(Sort.by(Sort.Direction.DESC, "id"));
    }
}
