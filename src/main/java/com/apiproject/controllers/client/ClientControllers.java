package com.apiproject.controllers.client;

import com.apiproject.DTOs.Auth.ChangePasswordRequestDTO;
import com.apiproject.DTOs.Auth.LoginClientRequestDTO;
import com.apiproject.DTOs.Auth.LoginClientResponseDTO;
import com.apiproject.DTOs.Auth.RegisterClientRequestDTO;
import com.apiproject.DTOs.Client.ClientDescriptionAboutUsersDTO;
import com.apiproject.DTOs.Client.ClientResponseDTO;
import com.apiproject.DTOs.Client.PaymentCardRequestDTO;
import com.apiproject.DTOs.Client.PaymentCardResponseDTO;
import com.apiproject.DTOs.Client.UpdateClientRequestDTO;
import com.apiproject.repositories.projection.ClientHistoryProjection;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.UserService;
import com.apiproject.services.client.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Client", description = "Registro, perfil, tarjetas simuladas e historial de compras del cliente")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class ClientControllers {
    private final ClientService clientService;
    private final UserService userService;

    @Operation(
            summary = "Iniciar sesion como cliente",
            description = "Valida email y password (BCrypt) y devuelve los datos del cliente junto con un JWT de rol CLIENT. Endpoint publico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto con token JWT"),
            @ApiResponse(responseCode = "400", description = "Email o password ausentes"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public LoginClientResponseDTO login(
            @RequestBody LoginClientRequestDTO loginRequestDTO
    ) {
        return clientService.loginClient(
                loginRequestDTO.email(),
                loginRequestDTO.password());
    }

    @Operation(
            summary = "Obtener los datos publicos de una tienda (admin)",
            description = "Devuelve la ficha publica del admin dueno de la tienda (nombre, negocio, contacto, foto). Endpoint publico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Datos publicos del admin"),
            @ApiResponse(responseCode = "404", description = "No existe un admin con ese ID")
    })
    @GetMapping("/{adminId}/admin")
    public ClientDescriptionAboutUsersDTO gerAdminDataForStore(
            @PathVariable Long adminId
    ) {
        return userService.getUserAdminForStore(adminId);
    }

    @Operation(
            summary = "Registrar un cliente",
            description = "Crea la cuenta del cliente, cifra la contrasena con BCrypt y devuelve los datos junto con un JWT. Endpoint publico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente registrado con token JWT"),
            @ApiResponse(responseCode = "400", description = "Body invalido o faltan password, fullName o email"),
            @ApiResponse(responseCode = "409", description = "El email ya esta registrado")
    })
    @PostMapping("/register")
    public LoginClientResponseDTO register(
            @RequestBody RegisterClientRequestDTO registerClientRequestDTO
    ) {
        return clientService.register(registerClientRequestDTO);
    }

    @Operation(
            summary = "Modificar el perfil del cliente autenticado",
            description = "Actualiza solo los campos enviados (fullName, email, phone, address, colorTypes). No cambia la contrasena (para eso esta /change-password). Valida texto y unicidad del email."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado"),
            @ApiResponse(responseCode = "400", description = "Body ausente, campo vacio o texto demasiado largo"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es CLIENT"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "El email ya esta registrado por otro cliente")
    })
    @PatchMapping(value = "/modify")
    public ClientResponseDTO modify(
            @RequestBody UpdateClientRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.modifyData(authenticatedUser.id(), request);
    }

    @Operation(
            summary = "Cambiar la contrasena del cliente autenticado",
            description = "Exige la contrasena actual y una nueva de al menos 8 caracteres, distinta de la actual. Responde 204 sin cuerpo. Devuelve 400 (no 401) en los errores para que el front no cierre la sesion; tambien 400 si la cuenta se creo con Google y no tiene contrasena local."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contrasena actualizada"),
            @ApiResponse(responseCode = "400", description = "Campos ausentes, nueva contrasena corta o igual, actual incorrecta, o cuenta sin contrasena local"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es CLIENT"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PatchMapping(value = "/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequestDTO request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        clientService.changePassword(authenticatedUser.id(), request);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Subir la foto de perfil del cliente",
            description = "Recibe el archivo multipart 'profilePhoto' (solo imagenes), lo publica en Cloudinary y actualiza la foto del cliente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foto actualizada; devuelve el perfil del cliente"),
            @ApiResponse(responseCode = "400", description = "Archivo vacio, ausente o que no es una imagen"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es CLIENT"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "413", description = "El archivo supera el tamano maximo permitido"),
            @ApiResponse(responseCode = "500", description = "Fallo al subir la imagen a Cloudinary")
    })
    @PatchMapping(value = "/upload-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ClientResponseDTO uploadProfilePhoto(
            @RequestPart("profilePhoto") MultipartFile profilePhoto,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) throws IOException {
        return clientService.subirFotoPerfil(authenticatedUser.id(), profilePhoto);
    }

    @Operation(
            summary = "Obtener la foto de perfil del cliente",
            description = "Responde 302 con la URL de Cloudinary en la cabecera Location (no devuelve el binario)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redireccion a la foto en Cloudinary"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es CLIENT"),
            @ApiResponse(responseCode = "404", description = "El cliente no existe o no tiene foto de perfil")
    })
    @GetMapping("/profile-photo")
    public ResponseEntity<Void> getProfilePhoto(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.obtenerFotoPerfil(authenticatedUser.id());
    }

    @Operation(
            summary = "Agregar una tarjeta de pago simulada",
            description = "Registra una tarjeta simulada del cliente autenticado. lastFour debe tener exactamente 4 digitos; active por defecto es true."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjeta creada"),
            @ApiResponse(responseCode = "400", description = "Body ausente, lastFour invalido o cliente inexistente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es CLIENT"),
            @ApiResponse(responseCode = "409", description = "La tarjeta viola una restriccion de la base de datos")
    })
    @PostMapping("/payment-cards")
    public PaymentCardResponseDTO addPaymentCard(
            @RequestBody PaymentCardRequestDTO paymentCardRequestDTO,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.addPaymentCard(paymentCardRequestDTO, authenticatedUser.id());
    }

    @Operation(
            summary = "Listar las tarjetas simuladas del cliente",
            description = "Devuelve las tarjetas del cliente autenticado ordenadas por fecha de creacion descendente. Respuesta cacheada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tarjetas del cliente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es CLIENT")
    })
    @GetMapping("/payment-cards")
    public List<PaymentCardResponseDTO> getPaymentCards(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.getPaymentCards(authenticatedUser.id());
    }

    @Operation(
            summary = "Activar o desactivar una tarjeta simulada",
            description = "Cambia el estado active de una tarjeta que pertenezca al cliente autenticado. Si la tarjeta no existe o es de otro cliente responde 400."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjeta actualizada"),
            @ApiResponse(responseCode = "400", description = "Tarjeta no encontrada o sin permiso, o parametro active invalido"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es CLIENT")
    })
    @PatchMapping("/payment-cards/{cardId}/status")
    public PaymentCardResponseDTO updatePaymentCardStatus(
            @PathVariable Long cardId,
            @RequestParam boolean active,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.updatePaymentCardStatus(cardId, active, authenticatedUser.id());
    }

    @Operation(
            summary = "Obtener los datos del cliente autenticado",
            description = "Devuelve el perfil completo del cliente del token, incluidas sus tarjetas. Respuesta cacheada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Datos del cliente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es CLIENT"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/user-data")
    public ClientResponseDTO getClientData(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return clientService.searForClientId(authenticatedUser.id());
    }

    @Operation(
            summary = "Historial de compras del cliente autenticado",
            description = "Devuelve el historial paginado del cliente desde la vista view_of_client_history. page base 0, size entre 1 y 100 y sort 'campo,asc|desc' (solo occurredAt se aplica; otro campo cae a fecha DESC)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagina del historial de compras"),
            @ApiResponse(responseCode = "400", description = "page negativo o size fuera del rango 1..100"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es CLIENT")
    })
    @GetMapping("/user-payments")
    public Page<ClientHistoryProjection> getClientHistory(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "occurredAt,desc") String sort) {
        return clientService.showBuysPaginated(authenticatedUser.id(), page, size, sort);
    }
}
