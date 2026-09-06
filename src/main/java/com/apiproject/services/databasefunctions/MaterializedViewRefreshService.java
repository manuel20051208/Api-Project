package com.apiproject.services.databasefunctions;

import com.apiproject.DTOs.General.ProductResponseDTO;
import com.apiproject.config.CacheConstants;
import com.apiproject.repositories.general.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterializedViewRefreshService {

    private final JdbcTemplate jdbcTemplate;
    private final ProductRepository productRepository;

    // refrescamos cada 5 minutos
    // fixedDelay -> Espera cinco minutos después de que termine la ejecución anterior.
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void refreshTopClientsView() {
        // Nota: no se puede ejecutar dos queries en un execute, te lanzara un exception (Lo probé en spring boot 3.5.16)
        jdbcTemplate.execute(
                "REFRESH MATERIALIZED VIEW CONCURRENTLY public.three_best_clients"
        );
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void refreshTopProductsView() {
        // Nota: no se puede ejecutar dos queries en un execute, te lanzara un exception (Lo probé en spring boot 3.5.16)
        jdbcTemplate.execute(
                "REFRESH MATERIALIZED VIEW CONCURRENTLY public.three_best_products"
        );
    }

    @Scheduled(
            initialDelay = 0,
            fixedDelayString = "${app.cache.refresh-ms:20000}"
    )
    @CachePut(
            value = CacheConstants.PRODUCTS_ACTIVE_WITH_IMAGES,
            key = "'all'"
    )
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> refreshClientProductsCache() {
        return productRepository.findAllActiveWithImages()
                .stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }
}
