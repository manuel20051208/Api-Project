package com.apiproject.services.general;

import com.apiproject.DTOs.Admin.UserAdminDTO;
import com.apiproject.DTOs.General.ProductResponseDTO;
import com.apiproject.config.CacheConstants;
import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.entities.general.Product;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.UserRepository;
import com.apiproject.repositories.general.ProductRepository;
import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCTS_BY_ADMIN, key = "#adminId", sync = true)
    public List<ProductResponseDTO> findAll(Long adminId) {
        return productRepository.findAllByUserAdminIdWithImages(adminId).stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCTS_ACTIVE_WITH_IMAGES, key = "'all'", sync = true)
    public List<ProductResponseDTO> findAllActiveWithImages() {
        return productRepository.findAllActiveWithImages().stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCTS_BY_CATEGORY, key = "#category.toLowerCase()", sync = true)
    public List<ProductResponseDTO> findAllByCategoryIgnoreCase(String category) {
        return productRepository.findAllByCategoryIgnoreCaseWithImages(category).stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCT_BY_ID, key = "#id", sync = true)
    public List<ProductResponseDTO> findAllById(Long id) {
        return productRepository.findByIdWithImages(id)
                .map(ProductResponseDTO::fromEntity)
                .stream()
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCT_BY_NAME, key = "#name.toLowerCase()", sync = true)
    public ProductResponseDTO findByNameIgnoreCase(String name) {
        Product product = productRepository.findByNameIgnoreCaseWithImages(name)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + name));
        return ProductResponseDTO.fromEntity(product);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PRODUCTS_BY_ADMIN, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_WITH_IMAGES_BY_ADMIN, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_ACTIVE_WITH_IMAGES, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_BY_CATEGORY, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_ACTIVE_PAGE, allEntries = true)
    })
    @Transactional
    public void save(Product product, Long adminId) {
        UserAdmin userAdmin = userRepository.getReferenceById(adminId);
        product.setUserAdmin(userAdmin);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCTS_ACTIVE_PAGE, key = "#pageSize", sync = true)
    public Page<ProductResponseDTO> findByActiveTrue(int pageSize) {
        Pageable firstPageByName = PageRequest.of(0, pageSize, Sort.by("name").ascending());
        Page<Long> idPage = productRepository.findActiveProductIds(firstPageByName);

        if (idPage.isEmpty()) {
            return Page.empty(firstPageByName);
        }

        Map<Long, Product> productsById = productRepository.findAllWithImagesByIdIn(idPage.getContent()).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<ProductResponseDTO> content = idPage.getContent().stream()
                .map(productsById::get)
                .filter(Objects::nonNull)
                .map(ProductResponseDTO::fromEntity)
                .toList();

        return new PageImpl<>(content, firstPageByName, idPage.getTotalElements());
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PRODUCTS_BY_ADMIN, key = "#adminId"),
            @CacheEvict(value = CacheConstants.PRODUCTS_WITH_IMAGES_BY_ADMIN, key = "#adminId"),
            @CacheEvict(value = CacheConstants.PRODUCT_BY_ID, key = "#id"),
            @CacheEvict(value = CacheConstants.PRODUCT_ADMIN, key = "#id"),
            @CacheEvict(value = CacheConstants.PRODUCT_BY_NAME, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_ACTIVE_WITH_IMAGES, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_BY_CATEGORY, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_ACTIVE_PAGE, allEntries = true)
    })
    @Transactional
    public ProductResponseDTO updateProduct(Long id, @NonNull Product product, Long adminId) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        validateOwner(existing, adminId);

        Optional.ofNullable(product.getName()).ifPresent(existing::setName);
        Optional.ofNullable(product.getPrice()).ifPresent(existing::setPrice);
        Optional.ofNullable(product.getStock()).ifPresent(existing::setStock);
        Optional.ofNullable(product.getCategory()).ifPresent(existing::setCategory);
        existing.setDescription(product.getDescription());

        return ProductResponseDTO.fromEntity(productRepository.save(existing));
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PRODUCTS_BY_ADMIN, key = "#adminId"),
            @CacheEvict(value = CacheConstants.PRODUCTS_WITH_IMAGES_BY_ADMIN, key = "#adminId"),
            @CacheEvict(value = CacheConstants.PRODUCT_BY_ID, key = "#id"),
            @CacheEvict(value = CacheConstants.PRODUCT_ADMIN, key = "#id"),
            @CacheEvict(value = CacheConstants.PRODUCT_BY_NAME, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_ACTIVE_WITH_IMAGES, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_BY_CATEGORY, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCTS_ACTIVE_PAGE, allEntries = true)
    })
    @Transactional
    public void deleteProductSafe(Long id, Long adminId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        validateOwner(product, adminId);
        product.setActive(false);
        productRepository.save(product);
    }

    public Product findUser(Long id, Long adminId) {
        if (!productRepository.existsByIdAndUserAdmin_Id(id, adminId)) {
            throw new ResponseStatusException(
                    FORBIDDEN,
                    "No puedes modificar productos de otro usuario admin");
        }
        return productRepository.getReferenceById(id);
    }

    private void validateOwner(Product product, Long adminId) {
        Long ownerId = product.getUserAdmin() != null ? product.getUserAdmin().getId() : null;
        if (ownerId == null || !ownerId.equals(adminId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    FORBIDDEN,
                    "No puedes modificar productos de otro usuario admin");
        }
    }

    // ================= Caché compartida fuera del CRUD =================

    /**
     * Refresca las caches de producto después de una compra (el stock cambió).
     * <p>
     * {@code productById} se reescribe con el DTO actualizado (equivale a {@code @CachePut}) y
     * {@code productByName} se evicta por nombre; las listas/paginados se evictan para que se
     * recarguen desde la base de datos y no queden datos fantasmas.
     */
    public void refreshCachesAfterStockChange(Collection<Product> products) {
        if (products == null || products.isEmpty()) {return;}

        Cache byId = cacheManager.getCache(CacheConstants.PRODUCT_BY_ID);
        Cache byName = cacheManager.getCache(CacheConstants.PRODUCT_BY_NAME);

        for (Product product : products) {
            if (product == null || product.getId() == null) {continue;}
            if (byId != null) {
                byId.put(product.getId(), List.of(ProductResponseDTO.fromEntity(product)));
            }
            if (byName != null && product.getName() != null) {
                byName.evict(product.getName().toLowerCase());
            }
        }

        Set<Long> adminIds = products.stream()
                .map(product -> product != null && product.getUserAdmin() != null
                        ? product.getUserAdmin().getId()
                        : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        evictCollectionCaches(adminIds);
    }

    /** Invalida las caches de producto cuando cambian sus imágenes (subida o borrado). */
    public void evictProductCaches(Long productId, Long adminId) {
        evictKey(CacheConstants.PRODUCT_BY_ID, productId);
        evictAll(CacheConstants.PRODUCT_BY_NAME);
        evictCollectionCaches(adminId == null ? Set.of() : Set.of(adminId));
    }

    private void evictCollectionCaches(Set<Long> adminIds) {
        adminIds.forEach(adminId -> {
            evictKey(CacheConstants.PRODUCTS_BY_ADMIN, adminId);
            evictKey(CacheConstants.PRODUCTS_WITH_IMAGES_BY_ADMIN, adminId);
        });
        evictAll(CacheConstants.PRODUCTS_ACTIVE_WITH_IMAGES);
        evictAll(CacheConstants.PRODUCTS_BY_CATEGORY);
        evictAll(CacheConstants.PRODUCTS_ACTIVE_PAGE);
    }

    private void evictKey(String cacheName, Object key) {
        if (key == null) {return;}
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    private void evictAll(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCTS_WITH_IMAGES_BY_ADMIN, key = "#adminId", sync = true)
    public List<ProductResponseDTO> findAllWithImages(Long adminId) {
        return productRepository.findAllWithImagesByUserAdminId(adminId).stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCT_ADMIN, key = "#productId", sync = true)
    public UserAdminDTO showUserAdmin(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        if (!product.isActive()) {
            throw new IllegalArgumentException("Product is not active");
        }

        return UserAdminDTO.fromEntity(product.getUserAdmin());
    }
}
