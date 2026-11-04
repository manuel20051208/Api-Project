package com.apiproject.services.general;

import com.apiproject.DTOs.General.ProductReviewRequestDTO;
import com.apiproject.DTOs.General.ProductReviewResponseDTO;
import com.apiproject.DTOs.General.ProductReviewSummaryDTO;
import com.apiproject.config.CacheConstants;
import com.apiproject.entities.general.Product;
import com.apiproject.entities.general.ProductReviews;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.client.ClientRepository;
import com.apiproject.repositories.general.ProductRepository;
import com.apiproject.repositories.general.ProductReviewRepository;
import com.apiproject.repositories.general.SaleItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;
    private static final int MAX_REVIEW_LENGTH = 1000;

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final SaleItemRepository saleItemRepository;

    // ================= Lecturas (cacheables) =================

    /** Reseñas paginadas de un producto (pantalla de detalle). */
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheConstants.PRODUCT_REVIEWS_BY_PRODUCT,
            key = "'product-' + #productId + '-p' + #page + '-s' + #size",
            sync = true)
    public Page<ProductReviewResponseDTO> findByProductId(Long productId, int page, int size) {
        Pageable pageable = buildPageable(page, size);
        return productReviewRepository.findByProductId(productId, pageable)
                .map(ProductReviewResponseDTO::fromProjection);
    }

    /** Reseñas paginadas hechas por un cliente ("mis reseñas"). */
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheConstants.PRODUCT_REVIEWS_BY_CLIENT,
            key = "'client-' + #clientId + '-p' + #page + '-s' + #size",
            sync = true)
    public Page<ProductReviewResponseDTO> findByClientId(Long clientId, int page, int size) {
        Pageable pageable = buildPageable(page, size);
        return productReviewRepository.findByClientId(clientId, pageable)
                .map(ProductReviewResponseDTO::fromProjection);
    }

    /** Promedio de estrellas + total de reseñas (se pinta en la tarjeta del producto). */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCT_REVIEW_SUMMARY, key = "#productId", sync = true)
    public ProductReviewSummaryDTO summary(Long productId) {
        return ProductReviewSummaryDTO.fromProjection(
                productId,
                productReviewRepository.findSummaryByProductId(productId));
    }

    /** Una reseña puntual; @CachePut de las escrituras refresca esta entrada. */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.PRODUCT_REVIEW_BY_ID, key = "#reviewId", sync = true)
    public ProductReviewResponseDTO findById(Long reviewId) {
        return productReviewRepository.findReviewById(reviewId)
                .map(ProductReviewResponseDTO::fromProjection)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
    }

    // ================= Escrituras (evict/put) =================

    /**
     * Crea la reseña de un producto. Solo el cliente que compró el producto puede reseñarlo
     * y solo una vez por producto (la BD también lo garantiza con un UNIQUE).
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PRODUCT_REVIEWS_BY_PRODUCT, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCT_REVIEWS_BY_CLIENT, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCT_REVIEW_SUMMARY, allEntries = true)
    })
    public ProductReviewResponseDTO create(Long clientId, ProductReviewRequestDTO request) {
        if (request == null || request.productId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "productId es obligatorio");
        }
        validateStars(request.stars());
        String reviewText = requireReviewText(request.reviews());

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.productId()));

        if (!product.isActive()) {
            throw new ResponseStatusException(CONFLICT, "Product is not active: " + product.getId());
        }

        if (!saleItemRepository.existsPurchaseByClientAndProduct(clientId, product.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Solo puedes reseñar productos que hayas comprado");
        }

        if (productReviewRepository.existsByProductIdAndClientId(product.getId(), clientId)) {
            throw new ResponseStatusException(CONFLICT, "Ya reseñaste este producto");
        }

        LocalDateTime now = LocalDateTime.now();
        ProductReviews review = new ProductReviews();
        review.setProduct(product);
        review.setUserClient(clientRepository.getReferenceById(clientId));
        review.setStars(request.stars());
        review.setReviews(reviewText);
        review.setCreatedAt(now);
        review.setUpdatedAt(now);

        return ProductReviewResponseDTO.fromEntity(productReviewRepository.save(review));
    }

    /**
     * Actualiza la reseña del cliente autenticado. @CachePut deja la entrada individual
     * fresca (sin volver a la BD) y evicta listados/promedio que sí cambian.
     */
    @Transactional
    @Caching(
            put = @CachePut(value = CacheConstants.PRODUCT_REVIEW_BY_ID, key = "#reviewId"),
            evict = {
                    @CacheEvict(value = CacheConstants.PRODUCT_REVIEWS_BY_PRODUCT, allEntries = true),
                    @CacheEvict(value = CacheConstants.PRODUCT_REVIEWS_BY_CLIENT, allEntries = true),
                    @CacheEvict(value = CacheConstants.PRODUCT_REVIEW_SUMMARY, allEntries = true)
            })
    public ProductReviewResponseDTO update(Long reviewId, Long clientId, ProductReviewRequestDTO request) {
        if (request == null || (request.stars() == null && request.reviews() == null)) {
            throw new ResponseStatusException(BAD_REQUEST, "Debes enviar stars y/o reviews");
        }

        ProductReviews review = requireOwnedReview(reviewId, clientId);

        if (request.stars() != null) {
            validateStars(request.stars());
            review.setStars(request.stars());
        }
        if (request.reviews() != null) {
            review.setReviews(requireReviewText(request.reviews()));
        }
        review.setUpdatedAt(LocalDateTime.now());

        return ProductReviewResponseDTO.fromEntity(productReviewRepository.save(review));
    }

    /** Borra la reseña del cliente autenticado y limpia listados/promedio/entrada individual. */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.PRODUCT_REVIEW_BY_ID, key = "#reviewId"),
            @CacheEvict(value = CacheConstants.PRODUCT_REVIEWS_BY_PRODUCT, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCT_REVIEWS_BY_CLIENT, allEntries = true),
            @CacheEvict(value = CacheConstants.PRODUCT_REVIEW_SUMMARY, allEntries = true)
    })
    public void delete(Long reviewId, Long clientId) {
        ProductReviews review = requireOwnedReview(reviewId, clientId);
        productReviewRepository.delete(review);
    }

    // ================= Helpers =================

    private ProductReviews requireOwnedReview(Long reviewId, Long clientId) {
        ProductReviews review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
        Long ownerId = review.getUserClient() != null ? review.getUserClient().getId() : null;
        if (ownerId == null || !ownerId.equals(clientId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes modificar reseñas de otro cliente");
        }
        return review;
    }

    private void validateStars(Integer stars) {
        if (stars == null || stars < 1 || stars > 5) {
            throw new ResponseStatusException(BAD_REQUEST, "stars debe estar entre 1 y 5");
        }
    }

    private String requireReviewText(String reviews) {
        if (reviews == null || reviews.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "reviews es obligatorio");
        }
        String trimmed = reviews.trim();
        if (trimmed.length() > MAX_REVIEW_LENGTH) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "reviews no puede superar " + MAX_REVIEW_LENGTH + " caracteres");
        }
        return trimmed;
    }

    private Pageable buildPageable(int page, int size) {
        int safePage = Math.max(page, DEFAULT_PAGE);
        int safeSize = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        return PageRequest.of(safePage, safeSize);
    }
}
