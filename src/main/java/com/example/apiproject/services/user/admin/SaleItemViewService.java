package com.example.apiproject.services.user.admin;

import com.example.apiproject.entities.admin.SaleItemView;
import com.example.apiproject.repositories.admin.SaleItemViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleItemViewService {
    private final SaleItemViewRepository saleItemViewRepository;

    public Page<SaleItemView> showEverythingWithLimits(int pageSize){
        Pageable limitTen = PageRequest.of(0, pageSize);
        return saleItemViewRepository.findAll(limitTen);
    }

    public Page<SaleItemView> showEverythingWithNoRestriction(){
        Pageable limitTen = PageRequest.of(0, 200);
        return saleItemViewRepository.findAll(limitTen);
    }

    public List<SaleItemView> showClientByName(String clientName){
        return saleItemViewRepository.findAllByClientNameIgnoreCase(clientName);
    }

    public List<SaleItemView> showProductByName(String productName){
        return saleItemViewRepository.findAllByProductNameIgnoreCase(productName);
    }
}