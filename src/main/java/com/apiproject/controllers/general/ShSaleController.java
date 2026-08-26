package com.apiproject.controllers.general;

import com.apiproject.DTOs.General.ShPurchaseRequestDTO;
import com.apiproject.DTOs.General.ShPurchaseResponseDTO;
import com.apiproject.repositories.projection.ShSaleHistoryProjection;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.general.ShSaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Second Hand Sale", description = "Compra e historial de ventas de productos de segunda mano")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sh-sale")
public class ShSaleController {
    private final ShSaleService shSaleService;

    @Operation(summary = "Compra de segunda mano con tarjeta activa; cupon opcional que debe cubrir todo el carrito")
    @PostMapping("/purchase")
    public ShPurchaseResponseDTO purchase(
            @RequestBody ShPurchaseRequestDTO requestDTO,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return shSaleService.purchase(requestDTO, authenticatedUser.id());
    }

    @Operation(summary = "Historial de compras segunda mano del cliente autenticado")
    @GetMapping("/client")
    public ResponseEntity<List<ShSaleHistoryProjection>> clientHistory(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ResponseEntity.ok(shSaleService.clientHistory(authenticatedUser.id()));
    }

    @Operation(summary = "Historial de ventas segunda mano del admin autenticado (filtro opcional clientId)")
    @GetMapping("/admin")
    public ResponseEntity<List<ShSaleHistoryProjection>> adminHistory(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(required = false) Long clientId
    ) {
        return ResponseEntity.ok(shSaleService.adminHistory(authenticatedUser.id(), clientId));
    }
}
