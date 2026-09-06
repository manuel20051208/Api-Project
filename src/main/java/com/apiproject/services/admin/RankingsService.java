package com.apiproject.services.admin;

import com.apiproject.DTOs.General.TheThreeBestClients;
import com.apiproject.DTOs.General.TheThreeBestProducts;
import com.apiproject.config.CacheConstants;
import com.apiproject.repositories.admin.RankingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingsService {

    private final RankingsRepository rankingsRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.BEST_CLIENTS, key = "#userId")
    public List<TheThreeBestClients> getBestClients(Long userId) {
        return rankingsRepository.findAllByUser(userId);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.BEST_PRODUCTS, key = "#userId")
    public List<TheThreeBestProducts> getBestProducts(Long userId) {
        return rankingsRepository.findAllByUserId(userId);
    }
}
