package com.apiproject.controllers.general;

import com.apiproject.DTOs.General.CancelSaleRequestDTO;
import com.apiproject.DTOs.General.CancelSaleResponseDTO;
import com.apiproject.DTOs.General.ShPurchaseRequestDTO;
import com.apiproject.DTOs.General.ShPurchaseResponseDTO;
import com.apiproject.repositories.projection.ShSaleHistoryProjection;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.general.SaleCancellationService;
import com.apiproject.services.general.ShSaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final SaleCancellationService saleCancellationService;

    @Operation(summary = "Compra de segunda mano con tarjeta activa; cupon opcional que debe cubrir todo el carrito")
    @PostMapping("/purchase")
    public ShPurchaseResponseDTO purchase(
            @RequestBody ShPurchaseRequestDTO requestDTO,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return shSaleService.purchase(requestDTO, authenticatedUser.id());
    }

    @Operation(
            summary = "Cancelar una venta SH completa o por items (ADMIN dueno o CLIENT dueno dentro de 24 h)",
            description = "Body opcional {itemIds:[...]}: vacio/ausente cancela toda la venta; con ids cancela solo esos items. Devuelve stock, reembolsa unitPrice*quantity - descuento del item y restituye el cupon solo si la venta queda cancelada por completo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cancelacion aplicada: estado, reembolsos e ids cancelados"),
            @ApiResponse(responseCode = "400", description = "Alguno de los itemIds no pertenece a la venta"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "La venta no es tuya o, siendo cliente, ya pasaron 24 h desde la compra"),
            @ApiResponse(responseCode = "404", description = "La venta no existe"),
            @ApiResponse(responseCode = "409", description = "La venta o alguno de los items ya estaba cancelado")
    })
    @PostMapping("/{saleId}/cancel")
    public CancelSaleResponseDTO cancel(
            @PathVariable Long saleId,
            @RequestBody(required = false) CancelSaleRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return saleCancellationService.cancelShSale(saleId, request, authenticatedUser);
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
