package com.apiproject.controllers.admin;

import com.apiproject.repositories.projection.SaleItemViewProjection;
import com.apiproject.services.admin.SaleItemViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Sales History", description = "Endpoints for listing and searching sales history")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sales-items")
public class SalesItemViewController {
    private final SaleItemViewService saleItemViewService;

    @Operation(summary = "Get a complete paginated list of all sales history")
    @GetMapping("/show-with-no-restrinction")
    public Page<SaleItemViewProjection> searchAll(@RequestParam("userId") Long userId) {
        return saleItemViewService.showEverythingWithNoRestriction(userId);
    }

    @Operation(summary = "Get a limited paginated list of sales history")
    @GetMapping("/show-with-limits")
    public Page<SaleItemViewProjection> showOnlyOnePart(@RequestParam("userId") Long userId,
                                                        @RequestParam int sizePage) {
        return saleItemViewService.showEverythingWithLimits(userId, sizePage);
    }

    @Operation(summary = "Search sales history by client name")
    @GetMapping("/client")
    public List<SaleItemViewProjection> searchAllClientByName(
            @RequestParam("userId") Long userId,
            @RequestParam("clientName") String clientName) {
        return saleItemViewService.showClientByName(userId, clientName);
    }

    @Operation(summary = "Search sales history by product name")
    @GetMapping("/product/")
    public List<SaleItemViewProjection> searchAllByProductName(
            @RequestParam("userId") Long userId,
            @RequestParam("productName") String productName) {
        return saleItemViewService.showProductByName(userId, productName);
    }
}