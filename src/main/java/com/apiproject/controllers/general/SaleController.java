package com.apiproject.controllers.general;

import com.apiproject.DTOs.General.PurchaseRequestDTO;
import com.apiproject.DTOs.General.PurchaseResponseDTO;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.general.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Sale", description = "Endpoints for handling sales")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sale")
public class SaleController {
    private final SaleService saleService;

    @Operation(summary = "Create a purchase for a client with an active simulated card")
    @PostMapping("/purchase")
    public PurchaseResponseDTO purchase(
            @RequestBody PurchaseRequestDTO purchaseRequestDTO,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return saleService.purchase(purchaseRequestDTO, authenticatedUser.id());
    }
}
