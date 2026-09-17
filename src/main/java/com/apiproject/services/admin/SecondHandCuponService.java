package com.apiproject.services.admin;

import com.apiproject.DTOs.Admin.CouponAssignmentResponseDTO;
import com.apiproject.DTOs.Admin.CuponAssignmentRequestDTO;
import com.apiproject.DTOs.Admin.CuponAssignmentUpdateRequestDTO;
import com.apiproject.DTOs.Admin.CuponRequestDTO;
import com.apiproject.DTOs.Admin.CuponResponseDTO;
import com.apiproject.DTOs.Admin.SecondHandCuponRequestDTO;
import com.apiproject.DTOs.Admin.SecondHandCuponResponseDTO;
import com.apiproject.DTOs.Admin.ShProductCuponToClientResponseDTO;
import com.apiproject.DTOs.General.CuponValidationResponseDTO;
import com.apiproject.entities.admin.SecondHandCupon;
import com.apiproject.entities.admin.SecondHandProductCuponsApplied;
import com.apiproject.entities.admin.ShCuponUsedByClients;
import com.apiproject.entities.admin.ShProductCuponToAClient;
import com.apiproject.entities.client.UserClient;
import com.apiproject.entities.general.SecondHandProduct;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.SecondHandCuponRepository;
import com.apiproject.repositories.admin.SecondHandProductCuponRepository;
import com.apiproject.repositories.admin.ShCuponUsedByClientsRepository;
import com.apiproject.repositories.admin.ShProductCuponToAClientRepository;
import com.apiproject.repositories.admin.UserRepository;
import com.apiproject.repositories.client.ClientRepository;
import com.apiproject.repositories.general.SecondHandProductRepository;
import com.apiproject.repositories.projection.CouponAssignmentProjection;
import com.apiproject.repositories.projection.CuponAdminProjection;
import com.apiproject.repositories.projection.ShProductCuponAssignmentProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.*;

/**
 * Espejo de {@link CuponService} para productos de segunda mano.
 * Misma lógica (CRUD, validación, resolución en compra, asignación a clientes)
 * pero contra las tablas secondhand_cupons / sh_*.
 */
@Service
@RequiredArgsConstructor
public class SecondHandCuponService {
    private final SecondHandCuponRepository secondHandCuponRepository;
    private final SecondHandProductCuponRepository secondHandProductCuponRepository;
    private final ShCuponUsedByClientsRepository shCuponUsedByClientsRepository;
    private final ShProductCuponToAClientRepository shProductCuponToAClientRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final SecondHandProductRepository secondHandProductRepository;

    // ================= CRUD (ADMIN) =================

    /** Crea un cupón SH del admin y lo vincula a sus productos de segunda mano. */
    @Transactional
    public SecondHandCuponResponseDTO create(SecondHandCuponRequestDTO request, Long adminId) {
        validateRequest(request);
        validateProductsOwned(request.shProductIds(), adminId);

        SecondHandCupon cupon = new SecondHandCupon();
        applyFields(cupon, request);
        cupon.setUserAdmin(userRepository.getReferenceById(adminId)); // dueño = admin autenticado
        SecondHandCupon saved = secondHandCuponRepository.save(cupon);
        replaceProducts(saved, request.shProductIds()); // enlaces N:M cupón <-> producto SH

        // Cupón recién creado => siempre usable inicialmente (active = true)
        return new SecondHandCuponResponseDTO(
                saved.getId(),
                saved.getShCuponCode(),
                saved.getCuponDateLimit(),
                saved.getDiscount(),
                saved.getQuantity(),
                true,
                adminId,
                secondHandProductCuponRepository.findShProductIdsByCuponId(saved.getId()));
    }

    /** Lista los cupones SH del admin con sus productos aplicados. */
    @Transactional(readOnly = true)
    public List<SecondHandCuponResponseDTO> findAllByOwner(Long adminId) {
        return secondHandCuponRepository.findAllByOwner(adminId).stream()
                .map(projection -> SecondHandCuponResponseDTO.fromProjection(
                        projection,
                        CuponService.parseIds(projection.getAppliedProductIds()))) // parsea el CSV de ids
                .toList();
    }

    /** Actualiza un cupón SH propio (LOCK FOR UPDATE); reemplaza enlaces si vienen shProductIds. */
    @Transactional
    public SecondHandCuponResponseDTO update(Long id, SecondHandCuponRequestDTO request, Long adminId) {
        validateRequest(request);

        SecondHandCupon cupon = secondHandCuponRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Second hand cupon not found: " + id));
        requireOwner(cupon, adminId);

        applyFields(cupon, request);
        if (request.shProductIds() != null) {
            secondHandProductCuponRepository.deleteByCuponId(cupon.getId()); // limpia enlaces viejos
            replaceProducts(cupon, request.shProductIds());                  // y crea los nuevos
        }

        // active se recalcula según vigencia y usos (el cupón puede haber expirado)
        return new SecondHandCuponResponseDTO(
                cupon.getId(),
                cupon.getShCuponCode(),
                cupon.getCuponDateLimit(),
                cupon.getDiscount(),
                cupon.getQuantity(),
                CuponResponseDTO.isUsable(cupon.getCuponDateLimit(), cupon.getQuantity()),
                adminId,
                secondHandProductCuponRepository.findShProductIdsByCuponId(cupon.getId()));
    }

    /** Borra el cupón SH junto con sus enlaces (previo chequeo de dueño). */
    @Transactional
    public void delete(Long id, Long adminId) {
        SecondHandCupon cupon = secondHandCuponRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Second hand cupon not found: " + id));
        requireOwner(cupon, adminId);
        secondHandProductCuponRepository.deleteByCuponId(id); // borra la N:M antes por la FK
        secondHandCuponRepository.delete(cupon);
    }

    // ============ Validación pública (CLIENT) ============

    /** Valida antes de comprar si un cupón SH aplica a un producto SH concreto. */
    @Transactional(readOnly = true)
    public CuponValidationResponseDTO validateForShProduct(String code, Long shProductId) {
        return secondHandCuponRepository.findValidForShProduct(code, shProductId, LocalDateTime.now())
                .map(usage -> new CuponValidationResponseDTO(
                        true,
                        "Cupon valido",
                        BigDecimal.valueOf(usage.getDiscount()),
                        usage.getCuponId()))
                .orElseGet(() -> CuponValidationResponseDTO.invalid(
                        "El cupon no existe, esta expirado, agoto sus usos o no aplica a este producto"));
    }

    /**
     * Igual que CuponService.resolveForCart pero para productos de segunda mano.
     * El cupon pertenece a un solo admin y aplica descuento solo a los productos del
     * carrito de ese admin que esten vinculados; se permite carrito multi-vendedor.
     */
    @Transactional
    public CouponResolution resolveForCart(String code, Long clientId, List<Long> shCarritoProductIds,
                                           Function<Long, Long> shProductoOwnerFn) {
        // LOCK por código: evita canjear el mismo cupón dos veces en paralelo
        SecondHandCupon cupon = secondHandCuponRepository.lockByCode(code)
                .orElseThrow(() -> new ResponseStatusException(CONFLICT, "El cupon no existe: " + code));

        // 1) Vigencia y usos disponibles
        if (cupon.getCuponDateLimit().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(CONFLICT, "El cupon esta expirado");
        }
        if (cupon.getQuantity() != null && cupon.getQuantity() <= 0) {
            throw new ResponseStatusException(CONFLICT, "El cupon agoto sus usos");
        }

        Long cuponOwnerId = cupon.getUserAdmin().getId();
        Set<Long> linkedProducts = new LinkedHashSet<>(
                secondHandProductCuponRepository.findShProductIdsByCuponId(cupon.getId())); // productos SH vinculados

        // 2) Elegibles = del carrito, los del admin del cupón Y vinculados
        List<Long> elegibles = shCarritoProductIds.stream()
                .filter(id -> cuponOwnerId.equals(shProductoOwnerFn.apply(id)))
                .filter(linkedProducts::contains)
                .toList();

        if (elegibles.isEmpty()) {
            throw new ResponseStatusException(CONFLICT,
                    "El cupon no aplica a ningun producto del carrito de este vendedor");
        }

        // 3) quantity limita cuántos productos elegibles cubre el cupón por compra
        Integer quantity = cupon.getQuantity();
        if (quantity != null && elegibles.size() > quantity) {
            elegibles = elegibles.subList(0, quantity);
        }

        // 4) Limite de usos por cliente: la asignacion puede limitar cuantas veces lo usa el cliente
        shProductCuponToAClientRepository.findUsageLimitByClientAndCupon(clientId, cupon.getId())
                .filter(limit -> shCuponUsedByClientsRepository.countByCuponIdAndClientId(cupon.getId(), clientId) >= limit)
                .ifPresent(limit -> {
                    throw new ResponseStatusException(CONFLICT, "El cliente agoto sus usos del cupon");
                });

        return new CouponResolution(cupon.getId(), cupon.getDiscount(), cuponOwnerId, elegibles);
    }

    /** Descuenta 1 uso del cupón SH de forma atómica; falla si ya no quedan usos. */
    @Transactional
    public void redeem(CouponResolution resolution) {
        int updatedRows = secondHandCuponRepository.decrementQuantity(resolution.cuponId());
        if (updatedRows == 0) {
            throw new ResponseStatusException(CONFLICT, "El cupon agoto sus usos");
        }
    }

    /** Resultado de resolver un cupón SH sobre un carrito. */
    public record CouponResolution(Long cuponId, Double discountPercent, Long ownerId, List<Long> elegibleProductIds) {
    }

    // ================= Helpers =================

    /** Copia los campos editables del request a la entidad. */
    private void applyFields(SecondHandCupon cupon, SecondHandCuponRequestDTO request) {
        cupon.setShCuponCode(request.shCuponCode().trim());
        cupon.setCuponDateLimit(request.cuponDateLimit());
        cupon.setDiscount(request.discount());
        cupon.setQuantity(request.quantity());
    }

    /** Crea (y guarda) una fila secondhand_product_cupons_applied por cada producto SH, sin duplicados. */
    private void replaceProducts(SecondHandCupon cupon, List<Long> shProductIds) {
        List<SecondHandProductCuponsApplied> links = new ArrayList<>();
        for (Long productId : distinct(shProductIds)) {
            SecondHandProductCuponsApplied link = new SecondHandProductCuponsApplied();
            link.setCupon(cupon);
            link.setProduct(secondHandProductRepository.getReferenceById(productId)); // solo referencia
            links.add(link);
        }
        secondHandProductCuponRepository.saveAll(links);
    }

    /** Valida los campos del request (mismas reglas que CuponService.validateRequest). */
    private void validateRequest(SecondHandCuponRequestDTO request) {
        if (request == null || request.shCuponCode() == null || request.shCuponCode().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "shCuponCode es obligatorio");
        }
        if (request.shCuponCode().length() > 15) {
            throw new ResponseStatusException(BAD_REQUEST, "shCuponCode no puede superar 15 caracteres");
        }
        if (!request.shCuponCode().matches("[A-Za-z0-9]+")) {
            throw new ResponseStatusException(BAD_REQUEST, "shCuponCode solo puede contener letras y numeros");
        }
        if (request.cuponDateLimit() == null || request.cuponDateLimit().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponDateLimit debe ser una fecha futura");
        }
        if (request.discount() == null || request.discount() <= 0 || request.discount() > 100) {
            throw new ResponseStatusException(BAD_REQUEST, "discount debe estar entre 0 y 100 (porcentaje)");
        }
        if (request.quantity() != null && request.quantity() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "quantity debe ser mayor a cero");
        }
        if (request.shProductIds() == null || request.shProductIds().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Debe indicar al menos un producto para el cupon");
        }
    }

    /** Verifica que el admin sea dueño de los productos SH antes de crear el cupón. */
    private void validateProductsOwned(List<Long> shProductIds, Long adminId) {
        for (Long productId : distinct(shProductIds)) {
            secondHandProductRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Second hand product not found: " + productId));
            if (!secondHandProductRepository.existsByIdAndOwner(productId, adminId)) {
                throw new ResponseStatusException(FORBIDDEN, "No puedes crear cupones sobre productos de otro admin");
            }
        }
    }

    /** Verifica que el cupón SH pertenezca al admin. */
    private void requireOwner(SecondHandCupon cupon, Long adminId) {
        Long ownerId = cupon.getUserAdmin() != null ? cupon.getUserAdmin().getId() : null;
        if (ownerId == null || !ownerId.equals(adminId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes modificar cupones de otro admin");
        }
    }

    /** Devuelve la lista sin duplicados (null-safe). */
    private static LinkedHashSet<Long> distinct(List<Long> ids) {
        return ids == null ? new LinkedHashSet<>() : new LinkedHashSet<>(ids);
    }

    /** Persiste el uso de un cupón SH hecho durante una compra (lo invoca ShSaleService).
     * Incrementa el contador: filtra por id del cupón + id del cliente + admin dueño,
     * toma el último valor acumulado y le suma 1. */
    public void registerUsage(ShCuponUsedByClients usage) {
        Long cuponId = usage.getCupon().getId();
        Long clientId = usage.getClientUser().getId();
        Long adminId = usage.getCupon().getUserAdmin() != null ? usage.getCupon().getUserAdmin().getId() : null;
        long used = shCuponUsedByClientsRepository.usageCountByCuponAndClient(cuponId, clientId, adminId);
        usage.setUsageCount(Math.toIntExact(used + 1));
        shCuponUsedByClientsRepository.save(usage);
    }

    // ================= Asignación de cupones SH a clientes =================

    /**
     * Asigna un cupón SH a clientes (todos o una lista).
     * Genera cupón x producto SH x cliente en sh_product_cupo_to_a_client.
     */
    @Transactional
    public List<ShProductCuponToClientResponseDTO> assignToClients(CuponAssignmentRequestDTO request, Long adminId) {
        if (request.cuponId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponId es obligatorio");
        }
        if (request.usageLimit() == null || request.usageLimit() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "usageLimit es obligatorio y debe ser mayor a cero");
        }
        SecondHandCupon cupon = secondHandCuponRepository.lockById(request.cuponId()) // LOCK contra duplicados
                .orElseThrow(() -> new ResourceNotFoundException("Second hand cupon not found: " + request.cuponId()));
        requireOwner(cupon, adminId);

        // El cupón debe tener productos SH vinculados para asignarlo
        List<Long> productIds = secondHandProductCuponRepository.findShProductIdsByCuponId(cupon.getId());
        if (productIds.isEmpty()) {
            throw new ResponseStatusException(CONFLICT, "El cupon no tiene productos asociados");
        }

        // Destinatarios: todos los clientes o la lista indicada
        List<Long> targetClientIds;
        if (request.assignToAll()) {
            targetClientIds = clientRepository.findAll().stream()
                    .map(UserClient::getId)
                    .toList();
        } else if (request.clientIds() != null && !request.clientIds().isEmpty()) {
            targetClientIds = request.clientIds();
        } else {
            throw new ResponseStatusException(BAD_REQUEST, "Debe indicar clientIds o assignToAll=true");
        }

        // Producto cruzado: cada cliente recibe una fila por cada producto SH del cupón
        List<ShProductCuponToAClient> assignments = new ArrayList<>();
        for (Long clientId : targetClientIds) {
            UserClient client = clientRepository.getReferenceById(clientId);
            for (Long productId : productIds) {
                SecondHandProduct product = secondHandProductRepository.getReferenceById(productId);
                ShProductCuponToAClient assignment = new ShProductCuponToAClient();
                assignment.setClient(client);
                assignment.setCupon(cupon);
                assignment.setProduct(product);
                assignment.setUsageLimit(request.usageLimit());
                assignments.add(assignment);
            }
        }
        shProductCuponToAClientRepository.saveAll(assignments);
        return findAllAssignmentsByAdmin(adminId); // estado completo tras asignar
    }

    /** Todas las asignaciones SH del admin. */
    @Transactional(readOnly = true)
    public List<ShProductCuponToClientResponseDTO> findAllAssignmentsByAdmin(Long adminId) {
        return shProductCuponToAClientRepository.findAllByAdmin(adminId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    /** Asignaciones SH del admin hacia un cliente concreto. */
    @Transactional(readOnly = true)
    public List<ShProductCuponToClientResponseDTO> findAssignmentsByClient(Long adminId, Long clientId) {
        return shProductCuponToAClientRepository.findByAdminAndClient(adminId, clientId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    /** Asignaciones SH de un cupón concreto del admin. */
    @Transactional(readOnly = true)
    public List<ShProductCuponToClientResponseDTO> findAssignmentsByCupon(Long adminId, Long cuponId) {
        return shProductCuponToAClientRepository.findByAdminAndCupon(adminId, cuponId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    /** Asignaciones SH de un cupón con uso (usageLimit + usedCount) para el diálogo "Clientes" del admin. */
    @Transactional(readOnly = true)
    public List<CouponAssignmentResponseDTO> findAssignmentsWithUsageByCupon(Long adminId, Long cuponId) {
        return shProductCuponToAClientRepository.findAssignmentsWithUsageByAdminAndCupon(adminId, cuponId).stream()
                .map(this::toCouponAssignmentDto)
                .toList();
    }

    /** Elimina una asignación SH puntual (validando dueño del cupón). */
    @Transactional
    public void removeAssignment(Long assignmentId, Long adminId) {
        ShProductCuponToAClient assignment = shProductCuponToAClientRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));
        requireOwner(assignment.getCupon(), adminId);
        shProductCuponToAClientRepository.deleteById(assignmentId);
    }

    /** Elimina todas las asignaciones de un cupón SH (validando dueño). */
    @Transactional
    public void removeAllByCupon(Long cuponId, Long adminId) {
        SecondHandCupon cupon = secondHandCuponRepository.lockById(cuponId)
                .orElseThrow(() -> new ResourceNotFoundException("Second hand cupon not found: " + cuponId));
        requireOwner(cupon, adminId);
        shProductCuponToAClientRepository.deleteByCuponId(cuponId);
    }

    /** Edita el limite de usos de una asignación SH puntual (por cliente). */
    @Transactional
    public ShProductCuponToClientResponseDTO updateAssignment(Long assignmentId, CuponAssignmentUpdateRequestDTO request, Long adminId) {
        validateUsageLimit(request);
        ShProductCuponToAClient assignment = shProductCuponToAClientRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));
        requireOwner(assignment.getCupon(), adminId);
        assignment.setUsageLimit(request.usageLimit());
        shProductCuponToAClientRepository.save(assignment);
        return toAssignmentDto(shProductCuponToAClientRepository.findByAdminAndClientAndShProduct(
                        adminId, assignment.getClient().getId(), assignment.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId)));
    }

    /** Edita el limite de usos de todas las asignaciones de un cupón SH (a todos los clientes). */
    @Transactional
    public List<ShProductCuponToClientResponseDTO> updateAllByCupon(Long cuponId, CuponAssignmentUpdateRequestDTO request, Long adminId) {
        validateUsageLimit(request);
        SecondHandCupon cupon = secondHandCuponRepository.lockById(cuponId)
                .orElseThrow(() -> new ResourceNotFoundException("Second hand cupon not found: " + cuponId));
        requireOwner(cupon, adminId);
        shProductCuponToAClientRepository.updateUsageLimitByCupon(cuponId, request.usageLimit());
        return findAssignmentsByCupon(adminId, cuponId);
    }

    /** Valida el usageLimit de un request de actualizacion de asignaciones. */
    private void validateUsageLimit(CuponAssignmentUpdateRequestDTO request) {
        if (request == null || request.usageLimit() == null || request.usageLimit() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "usageLimit es obligatorio y debe ser mayor a cero");
        }
    }

    /** Proyección -> DTO de una asignación SH (sin casteo manual). */
    private ShProductCuponToClientResponseDTO toAssignmentDto(ShProductCuponAssignmentProjection p) {
        return new ShProductCuponToClientResponseDTO(
                p.getId(),
                p.getClientId(),
                p.getClientName(),
                p.getClientEmail(),
                p.getShCuponsId(),
                p.getShCuponCode(),
                p.getDiscount(),
                p.getCuponDateLimit(),
                p.getShProductId(),
                p.getProductName(),
                p.getUsageLimit()
        );
    }

    /** Proyección con uso -> DTO del diálogo "Clientes" SH (usageLimit nullable, usedCount siempre numérico). */
    private CouponAssignmentResponseDTO toCouponAssignmentDto(CouponAssignmentProjection p) {
        return new CouponAssignmentResponseDTO(
                p.getId(),
                p.getClientName(),
                p.getClientEmail(),
                p.getUsageLimit(),
                p.getUsedCount(),
                p.getProductName()
        );
    }
}