package com.example.apiproject.services.user.admin;

import com.example.apiproject.repositories.admin.SaleItemViewRepository;
import com.example.apiproject.repositories.projection.SaleItemViewProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleItemViewService {
    private final SaleItemViewRepository saleItemViewRepository;

    @Transactional(readOnly = true)
    public Page<SaleItemViewProjection> showEverythingWithLimits(Long userId, int pageSize){
        Pageable limitTen = PageRequest.of(0, pageSize, latestSalesSort());
        return saleItemViewRepository.findAll(userId,limitTen);
    }

    @Transactional(readOnly = true)
    public Page<SaleItemViewProjection> showEverythingWithNoRestriction(Long userId){
        Pageable limitTen = PageRequest.of(0, 200, latestSalesSort());
        return saleItemViewRepository.findAll(userId,limitTen);
    }

    @Transactional(readOnly = true)
    public List<SaleItemViewProjection> showClientByName(Long userId,String clientName){
        return saleItemViewRepository.findAllByClientName(userId, clientName);
    }

    @Transactional(readOnly = true)
    public List<SaleItemViewProjection> showProductByName(Long userId,String productName){
        return saleItemViewRepository.findALlByProductName(userId,productName);
    }

    private Sort latestSalesSort() {
        return Sort.by(Sort.Direction.DESC, "date")
                .and(Sort.by(Sort.Direction.DESC, "id"));
    }
}
