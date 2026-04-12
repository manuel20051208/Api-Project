package com.example.apiproject.controllers.user.admin;

import com.example.apiproject.entities.admin.SaleItemView;
import com.example.apiproject.services.user.admin.SaleItemViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sales-items")
public class SalesItemViewController {
    private final SaleItemViewService saleItemViewService;

    @GetMapping("/amount-sales/{}")
    public Page<SaleItemView> searchAll(@RequestParam int sizePage){
        return saleItemViewService.showEverythingWithNoRestriction(sizePage);
    }

    @GetMapping("/with-limits")
    public Page<SaleItemView> showOnlyOnePart(){
        return saleItemViewService.showEverythingWithLimits();
    }

    @GetMapping("/client/{clientName}")
    public List<SaleItemView> searchAllClientByName(@PathVariable String clientName){
        return saleItemViewService.showClientByName(clientName);
    }

    @GetMapping("/product/{productName}")
    public List<SaleItemView> searchAllByProductName(@PathVariable
                                                  String productName){
        return saleItemViewService.showProductByName(productName);
    }
}