package com.apiproject.controllers.general;

import com.apiproject.DTOs.General.CancelSaleRequestDTO;
import com.apiproject.DTOs.General.CancelSaleResponseDTO;
import com.apiproject.DTOs.General.PurchaseRequestDTO;
import com.apiproject.DTOs.General.PurchaseResponseDTO;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.general.SaleCancellationService;
import com.apiproject.services.general.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Sale", description = "Registro de compras de productos normales")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sale")
public class SaleController {
    private final SaleService saleService;
    private final SaleCancellationService saleCancellationService;

    @Operation(
            summary = "Registrar la compra de un carrito (CLIENT)",
            description = "Valida que el clientId coincida con el cliente autenticado, que tenga una tarjeta activa, que los productos existan y tengan stock, y resuelve el cupon opcional (cuponCode). Crea una venta por admin/producto, descuenta stock y devuelve los totales y los items."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compra registrada con sus totales e items"),
            @ApiResponse(responseCode = "400", description = "Body invalido: sin clientId, sin items, item sin productId o cantidad <= 0"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "402", description = "El cliente no tiene una tarjeta activa"),
            @ApiResponse(responseCode = "403", description = "El clientId no corresponde al cliente autenticado, o el cupon es exclusivo de otros clientes"),
            @ApiResponse(responseCode = "404", description = "Cliente o alguno de los productos no existe"),
            @ApiResponse(responseCode = "409", description = "Producto inactivo o sin dueno, stock insuficiente, cupon inexistente/expirado/agotado o ya usado por el cliente")
    })
    @PostMapping("/purchase")
    public PurchaseResponseDTO purchase(
            @RequestBody PurchaseRequestDTO purchaseRequestDTO,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return saleService.purchase(purchaseRequestDTO, authenticatedUser.id());
    }

    @Operation(
            summary = "Cancelar una venta  completa o por items (ADMIN dueno o CLIENT dueno dentro de 24 h)",
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
        return saleCancellationService.cancelSale(saleId, request, authenticatedUser);
    }
}
