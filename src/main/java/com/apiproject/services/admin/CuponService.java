package com.apiproject.services.admin;

import com.apiproject.config.CacheConstants;
import com.apiproject.DTOs.Admin.CouponAssignmentResponseDTO;
import com.apiproject.DTOs.Admin.CuponAssignmentRequestDTO;
import com.apiproject.DTOs.Admin.CuponAssignmentUpdateRequestDTO;
import com.apiproject.DTOs.Admin.CuponRequestDTO;
import com.apiproject.DTOs.Admin.CuponResponseDTO;
import com.apiproject.DTOs.Admin.CuponSendRequestDTO;
import com.apiproject.DTOs.Admin.ProductCuponToClientResponseDTO;
import com.apiproject.DTOs.General.CuponValidationResponseDTO;
import com.apiproject.entities.admin.Cupon;
import com.apiproject.entities.admin.CuponUsedByClients;
import com.apiproject.entities.admin.ProductCuponApplied;
import com.apiproject.entities.admin.ProductCuponToAClient;
import com.apiproject.entities.client.UserClient;
import com.apiproject.entities.general.Product;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.*;
import com.apiproject.repositories.client.ClientRepository;
import com.apiproject.repositories.general.ProductRepository;
import com.apiproject.repositories.projection.CouponAssignmentProjection;
import com.apiproject.repositories.projection.CuponAdminProjection;
import com.apiproject.repositories.projection.ProductCuponAssignmentProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
 * Lógica de negocio de cupones de productos normales.
 * Cubre: CRUD del admin, validación (antes de comprar), resolución/canje dentro de una
 * compra (usado por SaleService) y asignación de cupones a clientes.
 */
@Service
@RequiredArgsConstructor
public class CuponService {
    private final CuponRepository cuponRepository;
    private final ProductCuponRepository productCuponRepository;
    private final CuponUsedByClientsRepository cuponUsedByClientsRepository;
    private final ProductCuponToAClientRepository productCuponToAClientRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    // ================= CRUD (ADMIN) =================

    /**
     * Crea un cupón del admin autenticado y lo vincula a sus productos.
     * Primero valida el request y que el admin sea dueño de todos los productos;
     * luego guarda el cupón y persiste los enlaces N:M (product_cupons_applied).
     */
    /**
     * Crea un cupón del admin autenticado y lo vincula a sus productos.
     * Primero valida el request y que el admin sea dueño de todos los productos;
     * luego guarda el cupón y persiste los enlaces N:M (product_cupons_applied).
     */
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CUPONS, allEntries = true),
            @CacheEvict(value = CacheConstants.CUPON_ASSIGNMENTS, allEntries = true)
    })
    @Transactional
    public CuponResponseDTO create(CuponRequestDTO request, Long adminId) {
        validateRequest(request);
        validateProductsOwned(request.productIds(), adminId);

        Cupon cupon = new Cupon();
        applyFields(cupon, request);
        cupon.setUserAdmin(userRepository.getReferenceById(adminId)); // dueño = admin autenticado (solo referencia, no se carga el objeto)
        Cupon saved = cuponRepository.save(cupon);
        replaceProducts(saved, request.productIds()); // crea los enlaces cupón <-> producto

        return CuponResponseDTO.fromEntity(
                saved,
                adminId,
                productCuponRepository.findProductIdsByCuponId(saved.getId()));
    }

    /** Lista los cupones del admin con los ids de productos aplicados (agregados con string_agg en SQL). */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.CUPONS, key = "#adminId", sync = true)
    public List<CuponResponseDTO> findAllByOwner(Long adminId) {
        return cuponRepository.findAllByOwner(adminId).stream()
                .map(this::toDto) // projection -> DTO, parseando el CSV de productIds
                .toList();
    }

    /**
     * Actualiza un cupón propio. Usa LOCK (FOR UPDATE) para evitar ediciones en paralelo;
     * si el request trae productIds, reemplaza por completo los enlaces de productos.
     */
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CUPONS, allEntries = true),
            @CacheEvict(value = CacheConstants.CUPON_ASSIGNMENTS, allEntries = true),
            @CacheEvict(value = CacheConstants.CLIENT_CUPONS, allEntries = true)
    })
    @Transactional
    public CuponResponseDTO update(Long id, CuponRequestDTO request, Long adminId) {
        validateRequest(request);

        Cupon cupon = cuponRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon not found: " + id));
        requireOwner(cupon, adminId); // solo el dueño puede editarlo

        applyFields(cupon, request);
        if (request.productIds() != null) {
            productCuponRepository.deleteByCuponId(cupon.getId()); // limpia enlaces viejos
            replaceProducts(cupon, request.productIds());          // y crea los nuevos
        }

        return CuponResponseDTO.fromEntity(
                cupon,
                adminId,
                productCuponRepository.findProductIdsByCuponId(cupon.getId()));
    }

    /** Borra el cupón junto con todas sus dependencias (asignaciones a clientes, usos y enlaces N:M). */
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CUPONS, allEntries = true),
            @CacheEvict(value = CacheConstants.CUPON_ASSIGNMENTS, allEntries = true),
            @CacheEvict(value = CacheConstants.CLIENT_CUPONS, allEntries = true)
    })
    @Transactional
    public void delete(Long id, Long adminId) {
        Cupon cupon = cuponRepository.lockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon not found: " + id));
        requireOwner(cupon, adminId);
        // La BD real no tiene ON DELETE CASCADE en product_cupon_to_a_client ni
        // cupons_used_by_clients: se limpia cada dependencia antes de borrar el cupón.
        productCuponToAClientRepository.deleteByCuponId(id); // asignaciones cupón -> cliente
        cuponUsedByClientsRepository.deleteByCuponId(id);    // usos reales del cupón
        productCuponRepository.deleteByCuponId(id);          // enlaces N:M cupón <-> producto (FK cascade)
        cuponRepository.delete(cupon);
    }

    /**
     * Valida (antes de comprar) si un cupón aplica a un producto concreto.
     * La query filtra vigencia, usos disponibles y vínculo cupón-producto;
     * si no devuelve fila, se responde que el cupón es inválido.
     */
    @Transactional(readOnly = true)
    public CuponValidationResponseDTO validateForProduct(String code, Long productId) {
        return cuponRepository.findValidForProduct(code, productId, LocalDateTime.now())
                .map(usage -> new CuponValidationResponseDTO(
                        true,
                        "Cupon valido",
                        BigDecimal.valueOf(usage.getDiscount()), // descuento en BigDecimal para el response
                        usage.getCuponId()))
                .orElseGet(() -> CuponValidationResponseDTO.invalid(
                        "El cupon no existe, esta expirado, agoto sus usos o no aplica a este producto"));
    }

    /**
     * Resuelve un cupon para un carrito: debe existir, estar vigente con usos disponibles.
     * El cupon pertenece a un solo admin; aplica descuento únicamente a los productos del
     * carrito que le pertenecen a ese admin y que estan vinculados al cupon (elegibles).
     * No se exige que todos los productos del carrito sean del mismo vendedor; los de otros
     * admins (o no vinculados) se cobran completos. Bloquea la fila (FOR UPDATE).
     *
     * @param code              codigo del cupon
     * @param clientId          id del cliente que canjea (para validar su limite de usos)
     * @param carritoProductIds ids de todos los productos del carrito
     * @param productoOwnerFn   funcion (productId -> adminId) que devuelve el dueño de cada producto
     * @return la resolucion con los ids de productos elegibles para descuento
     */
    @Transactional
    public CouponResolution resolveForCart(String code, Long clientId, List<Long> carritoProductIds,
                                           Function<Long, Long> productoOwnerFn) {
        // LOCK por código: dos compras simultáneas no pueden canjear el mismo cupón
        Cupon cupon = cuponRepository.lockByCode(code)
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
                productCuponRepository.findProductIdsByCuponId(cupon.getId())); // productos vinculados al cupón

        // 2) Elegibles = del carrito, los que son del admin del cupón Y están vinculados
        List<Long> elegibles = carritoProductIds.stream()
                .filter(id -> cuponOwnerId.equals(productoOwnerFn.apply(id)))
                .filter(linkedProducts::contains)
                .toList();

        if (elegibles.isEmpty()) {
            throw new ResponseStatusException(CONFLICT,
                    "El cupon no aplica a ningun producto del carrito de este vendedor");
        }

        // 3) quantity = tope de productos elegibles cubiertos por compra; si es ilimitado cubre todos
        Integer quantity = cupon.getQuantity();
        if (quantity != null && elegibles.size() > quantity) {
            elegibles = elegibles.subList(0, quantity);
        }

        // 4) Limite de usos por cliente: la asignacion puede limitar cuantas veces lo usa el cliente
        productCuponToAClientRepository.findUsageLimitByClientAndCupon(clientId, cupon.getId())
                .filter(limit -> cuponUsedByClientsRepository.countByCuponIdAndClientId(cupon.getId(), clientId) >= limit)
                .ifPresent(limit -> {
                    throw new ResponseStatusException(CONFLICT, "El cliente agoto sus usos del cupon");
                });

        return new CouponResolution(cupon.getId(), cupon.getDiscount(), cuponOwnerId, elegibles);
    }

    /** Descuenta 1 uso de forma atómica (UPDATE ... WHERE quantity > 0); si no toca filas es que se agotó. */
    @Transactional
    public void redeem(CouponResolution resolution) {
        int updatedRows = cuponRepository.decrementQuantity(resolution.cuponId());
        if (updatedRows == 0) {
            throw new ResponseStatusException(CONFLICT, "El cupon agoto sus usos");
        }
    }

    /** Resultado de resolver un cupón: a qué productos del carrito aplica y con qué descuento. */
    public record CouponResolution(Long cuponId, Double discountPercent, Long ownerId, List<Long> elegibleProductIds) {
    }

    // ================= Helpers =================

    /** Convierte la projection del listado a DTO, parseando el CSV "1,2,3" de ids de productos. */
    private CuponResponseDTO toDto(CuponAdminProjection projection) {
        return CuponResponseDTO.fromProjection(projection, parseIds(projection.getAppliedProductIds()));
    }

    /** Copia los campos editables del request a la entidad. */
    private void applyFields(Cupon cupon, CuponRequestDTO request) {
        cupon.setCuponCode(request.cuponCode().trim());
        cupon.setCuponDateLimit(request.cuponDateLimit());
        cupon.setDiscount(request.discount());
        cupon.setQuantity(request.quantity());
    }

    /** Crea (y guarda) una fila product_cupons_applied por cada producto, sin duplicados. */
    private void replaceProducts(Cupon cupon, List<Long> productIds) {
        List<ProductCuponApplied> links = new ArrayList<>();
        for (Long productId : distinct(productIds)) {
            ProductCuponApplied link = new ProductCuponApplied();
            link.setCupon(cupon);
            link.setProduct(productRepository.getReferenceById(productId)); // solo referencia, sin cargar el producto completo
            links.add(link);
        }
        productCuponRepository.saveAll(links);
    }

    /** Valida los campos del request: código no vacío (<=15), fecha futura, descuento 1-100, al menos un producto. */
    private void validateRequest(CuponRequestDTO request) {
        if (request == null || request.cuponCode() == null || request.cuponCode().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponCode es obligatorio");
        }
        if (request.cuponCode().length() > 15) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponCode no puede superar 15 caracteres");
        }
        if (!request.cuponCode().matches("[A-Za-z0-9]+")) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponCode solo puede contener letras y numeros");
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
        if (request.productIds() == null || request.productIds().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Debe indicar al menos un producto para el cupon");
        }
    }

    /** Verifica que el admin sea dueño de todos los productos antes de crearle un cupón. */
    private void validateProductsOwned(List<Long> productIds, Long adminId) {
        for (Long productId : distinct(productIds)) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
            if (product.getUserAdmin() == null || !product.getUserAdmin().getId().equals(adminId)) {
                throw new ResponseStatusException(FORBIDDEN, "No puedes crear cupones sobre productos de otro admin");
            }
        }
    }

    /** Verifica que el cupón pertenezca al admin (para crear/modificar/borrar). */
    private void requireOwner(Cupon cupon, Long adminId) {
        Long ownerId = cupon.getUserAdmin() != null ? cupon.getUserAdmin().getId() : null;
        if (ownerId == null || !ownerId.equals(adminId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes modificar cupones de otro admin");
        }
    }

    /** Divide el CSV de productIds (generado con string_agg) en una lista de Long. */
    static List<Long> parseIds(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        List<Long> ids = new ArrayList<>();
        for (String part : csv.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) ids.add(Long.parseLong(trimmed));
        }
        return ids;
    }

    /** Devuelve la lista sin duplicados (null-safe). */
    private static LinkedHashSet<Long> distinct(List<Long> ids) {
        return ids == null ? new LinkedHashSet<>() : new LinkedHashSet<>(ids);
    }

    /** Persiste un uso real del cupón realizado durante una compra (lo invoca SaleService).
     * Incrementa el contador: filtra por id del cupón + id del cliente + admin dueño,
     * toma el último valor acumulado y le suma 1. */
    public void registerUsage(CuponUsedByClients usage) {
        Long cuponId = usage.getCupon().getId();
        Long clientId = usage.getClientUser().getId();
        Long adminId = usage.getCupon().getUserAdmin() != null ? usage.getCupon().getUserAdmin().getId() : null;
        long used = cuponUsedByClientsRepository.usageCountByCuponAndClient(cuponId, clientId, adminId);
        usage.setUsageCount(Math.toIntExact(used + 1));
        cuponUsedByClientsRepository.save(usage);
    }

    // ================= Asignación de cupones a clientes =================

    /**
     * Asigna un cupón a clientes (todos o una lista indicada).
     * Genera la combinación cupón x producto x cliente en product_cupon_to_a_client
     * y devuelve el listado completo de asignaciones del admin.
     */
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CUPONS, allEntries = true),
            @CacheEvict(value = CacheConstants.CUPON_ASSIGNMENTS, allEntries = true),
            @CacheEvict(value = CacheConstants.CLIENT_CUPONS, allEntries = true)
    })
    @Transactional
    public List<ProductCuponToClientResponseDTO> assignToClients(CuponAssignmentRequestDTO request, Long adminId) {
        if (request.cuponId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "cuponId es obligatorio");
        }
        if (request.usageLimit() == null || request.usageLimit() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "usageLimit es obligatorio y debe ser mayor a cero");
        }
        Cupon cupon = cuponRepository.lockById(request.cuponId()) // LOCK para no duplicar asignaciones en paralelo
                .orElseThrow(() -> new ResourceNotFoundException("Cupon not found: " + request.cuponId()));
        requireOwner(cupon, adminId);

        // El cupón debe tener productos vinculados para asignarlo
        List<Long> productIds = productCuponRepository.findProductIdsByCuponId(cupon.getId());
        if (productIds.isEmpty()) {
            throw new ResponseStatusException(CONFLICT, "El cupon no tiene productos asociados");
        }

        // Destinatarios: todos los clientes o la lista indicada en el request
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

        // Producto cruzado: cada cliente recibe una fila por cada producto del cupón
        List<ProductCuponToAClient> assignments = new ArrayList<>();
        for (Long clientId : targetClientIds) {
            UserClient client = clientRepository.getReferenceById(clientId);
            for (Long productId : productIds) {
                Product product = productRepository.getReferenceById(productId);
                ProductCuponToAClient assignment = new ProductCuponToAClient();
                assignment.setClient(client);
                assignment.setCupon(cupon);
                assignment.setProduct(product);
                assignment.setUsageLimit(request.usageLimit());
                assignments.add(assignment);
            }
        }
        productCuponToAClientRepository.saveAll(assignments);
        return findAllAssignmentsByAdmin(adminId); // retorna el estado completo tras asignar
    }

    /** Todas las asignaciones de los cupones del admin (para la vista de admin). */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.CUPON_ASSIGNMENTS, key = "#adminId", sync = true)
    public List<ProductCuponToClientResponseDTO> findAllAssignmentsByAdmin(Long adminId) {
        return productCuponToAClientRepository.findAllByAdmin(adminId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    /** Asignaciones del admin hacia un cliente concreto. */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.CUPON_ASSIGNMENTS, key = "#adminId + '-' + #clientId", sync = true)
    public List<ProductCuponToClientResponseDTO> findAssignmentsByClient(Long adminId, Long clientId) {
        return productCuponToAClientRepository.findByAdminAndClient(adminId, clientId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    /** Cupones asignados al cliente autenticado (para la app del cliente). */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.CLIENT_CUPONS, key = "#clientId", sync = true)
    public List<ProductCuponToClientResponseDTO> findMyAssignments(Long clientId) {
        return productCuponToAClientRepository.findAllByClient(clientId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    /** Asignaciones de un cupón concreto del admin. */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.CUPON_ASSIGNMENTS, key = "#adminId + '-cupon-' + #cuponId", sync = true)
    public List<ProductCuponToClientResponseDTO> findAssignmentsByCupon(Long adminId, Long cuponId) {
        return productCuponToAClientRepository.findByAdminAndCupon(adminId, cuponId).stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    /** Asignaciones de un cupón con uso (usageLimit + usedCount) para el diálogo "Clientes" del admin. */
    @Transactional(readOnly = true)
    public List<CouponAssignmentResponseDTO> findAssignmentsWithUsageByCupon(Long adminId, Long cuponId) {
        return productCuponToAClientRepository.findAssignmentsWithUsageByAdminAndCupon(adminId, cuponId).stream()
                .map(this::toCouponAssignmentDto)
                .toList();
    }

    /**
     * Botón "enviar cupon": el admin manda un cupón ya creado a un cliente concreto.
     * Crea la asignación cupón x producto x cliente (solo para ese cliente) y evita
     * duplicados re-utilizando la fila existente para el mismo cupón y cliente.
     */
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CUPON_ASSIGNMENTS, allEntries = true),
            @CacheEvict(value = CacheConstants.CLIENT_CUPONS, key = "#requestDTO.clientId()")
    })
    @Transactional
    public List<ProductCuponToClientResponseDTO> sendToClient(CuponSendRequestDTO requestDTO, Long adminId) {
        if (requestDTO == null || requestDTO.clientId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "El cliente es obligatorio para enviar el cupon");
        }
        if (requestDTO.cuponId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "El cupon es obligatorio para enviarlo");
        }
        if (requestDTO.usageLimit() == null || requestDTO.usageLimit() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "usageLimit es obligatorio y debe ser mayor a cero");
        }

        Cupon cupon = cuponRepository.lockById(requestDTO.cuponId())
                .orElseThrow(() -> new ResourceNotFoundException("Cupon not found: " + requestDTO.cuponId()));
        requireOwner(cupon, adminId);

        List<Long> productIds = productCuponRepository.findProductIdsByCuponId(cupon.getId());
        if (productIds.isEmpty()) {
            throw new ResponseStatusException(CONFLICT, "El cupon no tiene productos asociados");
        }

        UserClient client = clientRepository.getReferenceById(requestDTO.clientId());

        // Re-uso: si el cupon ya estaba asignado a ese cliente, no se duplica la fila.
        for (Long productId : productIds) {
            boolean alreadyAssigned = productCuponToAClientRepository
                    .findByAdminAndClientAndProduct(adminId, client.getId(), productId)
                    .isPresent();
            if (alreadyAssigned) {
                continue;
            }
            ProductCuponToAClient assignment = new ProductCuponToAClient();
            assignment.setClient(client);
            assignment.setCupon(cupon);
            assignment.setProduct(productRepository.getReferenceById(productId));
            assignment.setUsageLimit(requestDTO.usageLimit());
            productCuponToAClientRepository.save(assignment);
        }

        return productCuponToAClientRepository.findByAdminAndClient(adminId, client.getId()).stream()
                .filter(p -> p.getCuponId().equals(cupon.getId()))
                .map(this::toAssignmentDto)
                .toList();
    }

    /** Elimina una asignación puntual (previendo que el admin sea dueño del cupón). */
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CUPON_ASSIGNMENTS, allEntries = true),
            @CacheEvict(value = CacheConstants.CLIENT_CUPONS, allEntries = true)
    })
    @Transactional
    public void removeAssignment(Long assignmentId, Long adminId) {
        ProductCuponToAClient assignment = productCuponToAClientRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));
        requireOwner(assignment.getCupon(), adminId);
        productCuponToAClientRepository.deleteById(assignmentId);
    }

    /** Elimina todas las asignaciones de un cupón (validando dueño). */
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CUPON_ASSIGNMENTS, allEntries = true),
            @CacheEvict(value = CacheConstants.CLIENT_CUPONS, allEntries = true)
    })
    @Transactional
    public void removeAllByCupon(Long cuponId, Long adminId) {
        Cupon cupon = cuponRepository.lockById(cuponId)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon not found: " + cuponId));
        requireOwner(cupon, adminId);
        productCuponToAClientRepository.deleteByCuponId(cuponId);
    }

    /** Edita el limite de usos de una asignacion puntual (por cliente). */
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CUPON_ASSIGNMENTS, allEntries = true),
            @CacheEvict(value = CacheConstants.CLIENT_CUPONS, allEntries = true)
    })
    @Transactional
    public ProductCuponToClientResponseDTO updateAssignment(Long assignmentId, CuponAssignmentUpdateRequestDTO request, Long adminId) {
        validateUsageLimit(request);
        ProductCuponToAClient assignment = productCuponToAClientRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));
        requireOwner(assignment.getCupon(), adminId);
        assignment.setUsageLimit(request.usageLimit());
        productCuponToAClientRepository.save(assignment);
        return toAssignmentDto(productCuponToAClientRepository.findByAdminAndClientAndProduct(
                        adminId, assignment.getClient().getId(), assignment.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId)));
    }

    /** Edita el limite de usos de todas las asignaciones de un cupon (a todos los clientes). */
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CUPON_ASSIGNMENTS, allEntries = true),
            @CacheEvict(value = CacheConstants.CLIENT_CUPONS, allEntries = true)
    })
    @Transactional
    public List<ProductCuponToClientResponseDTO> updateAllByCupon(Long cuponId, CuponAssignmentUpdateRequestDTO request, Long adminId) {
        validateUsageLimit(request);
        Cupon cupon = cuponRepository.lockById(cuponId)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon not found: " + cuponId));
        requireOwner(cupon, adminId);
        productCuponToAClientRepository.updateUsageLimitByCupon(cuponId, request.usageLimit());
        return findAssignmentsByCupon(adminId, cuponId);
    }

    /** Valida el usageLimit de un request de actualizacion de asignaciones. */
    private void validateUsageLimit(CuponAssignmentUpdateRequestDTO request) {
        if (request == null || request.usageLimit() == null || request.usageLimit() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "usageLimit es obligatorio y debe ser mayor a cero");
        }
    }

    /** Proyección -> DTO de una asignación (sin casteo manual: ya viene tipada del SQL). */
    private ProductCuponToClientResponseDTO toAssignmentDto(ProductCuponAssignmentProjection p) {
        return new ProductCuponToClientResponseDTO(
                p.getId(),
                p.getClientId(),
                p.getClientName(),
                p.getClientEmail(),
                p.getCuponId(),
                p.getCuponCode(),
                p.getDiscount(),
                p.getCuponDateLimit(),
                p.getProductId(),
                p.getProductName(),
                p.getUsageLimit()
        );
    }

    /** Proyección con uso -> DTO del diálogo "Clientes" (usageLimit nullable, usedCount siempre numérico). */
    private CouponAssignmentResponseDTO toCouponAssignmentDto(CouponAssignmentProjection p) {
        return new CouponAssignmentResponseDTO(
                p.getId(),
                p.getClientName(),
                p.getClientEmail(),
                p.getUsageLimit(),
                p.getUsedCount(),
                p.getUsageLimit() == null ? null : Math.max(0, p.getUsageLimit() - p.getUsedCount().intValue()),
                p.getProductName()
        );
    }
}