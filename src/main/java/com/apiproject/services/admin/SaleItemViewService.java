package com.apiproject.services.admin;

import com.apiproject.config.CacheConstants;
import com.apiproject.repositories.admin.SaleItemViewRepository;
import com.apiproject.repositories.projection.SaleItemViewProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class SaleItemViewService {
    private final SaleItemViewRepository saleItemViewRepository;

    /** Tope de filas por pedido: el front ofrece hasta 200 por pagina. Evita traer todo el historial a memoria. */
    public static final int MAX_PAGE_SIZE = 200;

    /** Primeras N ventas del admin (mas recientes). sizePage entre 1 y MAX_PAGE_SIZE. */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.SALE_SUMMARY, key = "'limit-' + #userId + '-' + #pageSize")
    public Page<SaleItemViewProjection> showEverythingWithLimits(Long userId, int pageSize){
        validatePageSize(pageSize, "sizePage");
        return saleItemViewRepository.findAll(userId, PageRequest.of(0, pageSize, latestSalesSort()));
    }

    /**
     * Historial paginado de verdad (antes ignoraba page/size y devolvia siempre las primeras 200).
     * page base 0; size entre 1 y MAX_PAGE_SIZE. La key del cache incluye page y size.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.SALE_SUMMARY, key = "'all-' + #userId + '-' + #page + '-' + #size")
    public Page<SaleItemViewProjection> showEverythingWithNoRestriction(Long userId, int page, int size){
        if (page < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "page debe ser mayor o igual a 0");
        }
        validatePageSize(size, "size");
        return saleItemViewRepository.findAll(userId, PageRequest.of(page, size, latestSalesSort()));
    }

    private static void validatePageSize(int size, String param) {
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(BAD_REQUEST, param + " debe estar entre 1 y " + MAX_PAGE_SIZE);
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.SALE_SUMMARY, key = "'client-' + #userId + '-' + #clientName")
    public List<SaleItemViewProjection> showClientByName(Long userId,String clientName){
        return saleItemViewRepository.findAllByClientName(userId, clientName);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.SALE_SUMMARY, key = "'product-' + #userId + '-' + #productName")
    public List<SaleItemViewProjection> showProductByName(Long userId,String productName){
        return saleItemViewRepository.findALlByProductName(userId,productName);
    }

    private Sort latestSalesSort() {
        return Sort.by(Sort.Direction.DESC, "date")
                .and(Sort.by(Sort.Direction.DESC, "id"));
    }
}
