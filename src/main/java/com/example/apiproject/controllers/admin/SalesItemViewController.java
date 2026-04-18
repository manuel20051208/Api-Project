package com.example.apiproject.controllers.admin;

import com.example.apiproject.entities.admin.SaleItemView;
import com.example.apiproject.services.user.admin.SaleItemViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Sales History", description = "Endpoints for listing and searching sales history")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sales-items")
public class SalesItemViewController {
    private final SaleItemViewService saleItemViewService;

    @Operation(summary = "Get a complete paginated list of all sales history")
    @GetMapping("/show-with-no-restrinction")
    public Page<SaleItemView> searchAll(){
        return saleItemViewService.showEverythingWithNoRestriction();
    }

    @Operation(summary = "Get a limited paginated list of sales history")
    @GetMapping("/show-with-limits/{sizePage}")
    public Page<SaleItemView> showOnlyOnePart(@PathVariable int sizePage){
        return saleItemViewService.showEverythingWithLimits(sizePage);
    }

    @Operation(summary = "Search sales history by client name")
    @GetMapping("/client/{clientName}")
    public List<SaleItemView> searchAllClientByName(@PathVariable String clientName){
        return saleItemViewService.showClientByName(clientName);
    }

    @Operation(summary = "Search sales history by product name")
    @GetMapping("/product/{productName}")
    public List<SaleItemView> searchAllByProductName(@PathVariable
                                                  String productName){
        return saleItemViewService.showProductByName(productName);
    }
}