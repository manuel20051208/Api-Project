package com.example.apiproject.services.user.admin;

import com.example.apiproject.DTOs.Admin.ProductImageDTO;
import com.example.apiproject.entities.admin.ProductImage;
import com.example.apiproject.entities.general.Product;
import com.example.apiproject.exceptions.ResourceNotFoundException;
import com.example.apiproject.repositories.admin.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.apiproject.services.general.ProductService;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductService productService;
    private final Path rootLocation = Paths.get("uploads/products");

    public ProductImageDTO uploadImage(Long productId, MultipartFile file, Long adminId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file.");
        }

        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String safeFileName = UUID.randomUUID().toString() + extension;

        Files.copy(file.getInputStream(), this.rootLocation.resolve(safeFileName));

        Product product = productService.findUser(productId, adminId);

        Long order = productImageRepository.countByProductId(productId) + 1;

        ProductImage image = new ProductImage();
        image.setFileName(originalName);
        image.setFilePath(safeFileName);
        image.setDisplayOrder(order);
        image.setProduct(product);
        image.setUserAdmin(product.getUserAdmin());

        ProductImage productImage = productImageRepository.save(image);
        return ProductImageDTO.fromEntity(productImage);
    }

    public List<ProductImageDTO> getImagesByProductId(Long productId) {
        return productImageRepository.findByProductIdOrderByDisplayOrder(productId)
                .stream().map(ProductImageDTO::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteImage(Long imageId, Long adminId) throws IOException {
        ProductImage image = productImageRepository.findDeleteInfoById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found: " + imageId));

        if (image.getId() == null || !image.getOwnerId().equals(adminId)) {
            throw new ResponseStatusException(FORBIDDEN, """
        No puedes eliminar esta imagen, si el error persiste comunicate con nuestros asesores
        """);
        }

        Path filePath = this.rootLocation.resolve(image.getFilePath());
        Files.deleteIfExists(filePath);

        productImageRepository.deleteByImageId(imageId);
    }
}
