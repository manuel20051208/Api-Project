package com.apiproject.controllers.admin;

import com.apiproject.repositories.projection.SaleItemViewProjection;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.SaleItemViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public Page<SaleItemViewProjection> searchAll(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return saleItemViewService.showEverythingWithNoRestriction(authenticatedUser.id());
    }

    @Operation(summary = "Get a limited paginated list of sales history")
    @GetMapping("/show-with-limits")
    public Page<SaleItemViewProjection> showOnlyOnePart(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                        @RequestParam int sizePage) {
        return saleItemViewService.showEverythingWithLimits(authenticatedUser.id(), sizePage);
    }

    @Operation(summary = "Search sales history by client name")
    @GetMapping("/client")
    public List<SaleItemViewProjection> searchAllClientByName(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam("clientName") String clientName) {
        return saleItemViewService.showClientByName(authenticatedUser.id(), clientName);
    }

    @Operation(summary = "Search sales history by product name")
    @GetMapping("/product/")
    public List<SaleItemViewProjection> searchAllByProductName(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam("productName") String productName) {
        return saleItemViewService.showProductByName(authenticatedUser.id(), productName);
    }
}