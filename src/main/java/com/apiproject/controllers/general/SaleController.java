package com.apiproject.controllers.general;

import com.apiproject.DTOs.General.PurchaseRequestDTO;
import com.apiproject.DTOs.General.PurchaseResponseDTO;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.general.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
}
