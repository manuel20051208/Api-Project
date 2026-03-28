package com.example.apiproject.services.user.admin;

import com.example.apiproject.entities.admin.SaleItemView;
import com.example.apiproject.repositories.admin.user.repositories.SaleItemViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleItemViewService {
    private final SaleItemViewRepository saleItemViewRepository;

    public List<SaleItemView> showEverything(){
        return saleItemViewRepository.findAll();
    }

    public List<SaleItemView> showClientByName(String clientName){
        return saleItemViewRepository.findAllByClientNameIgnoreCase(clientName);
    }

    public List<SaleItemView> showProductByName(String productName){
        return saleItemViewRepository.findAllByProductNameIgnoreCase(productName);
    }
}