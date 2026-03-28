package com.example.apiproject.services.user.admin;

import com.example.apiproject.entities.admin.ProductImage;
import com.example.apiproject.entities.general.entities.Product;
import com.example.apiproject.repositories.admin.user.repositories.ProductImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.apiproject.services.general.service.ProductService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductImageService {

    private final ProductImageRepository imageRepository;
    private final ProductService productService;
    private final Path rootLocation = Paths.get("uploads/products");

    public ProductImageService(ProductImageRepository imageRepository, ProductService productService) {
        this.imageRepository = imageRepository;
        this.productService = productService;
    }

    public ProductImage uploadImage(Long productId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file, bro.");
        }

        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }

        // Security: Get extension and generate a random name
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String safeFileName = UUID.randomUUID().toString() + extension;

        // Physical Save
        Files.copy(file.getInputStream(), this.rootLocation.resolve(safeFileName));

        // Business Logic
        Product product = productService.findById(productId);

        // Count existing images for display order
        Long order = (Long) imageRepository.countByProductId(productId) + 1;

        ProductImage image = new ProductImage();
        image.setFileName(originalName);
        image.setFilePath(safeFileName);
        image.setDisplayOrder(order);
        image.setProduct(product);

        return imageRepository.save(image);
    }

    public List<ProductImage> getImagesByProductId(Long productId) {
        return imageRepository.findByProductIdOrderByDisplayOrder(productId);
    }

    public void deleteImage(Long imageId) throws IOException {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found: " + imageId));

        // Delete from disk
        Path filePath = this.rootLocation.resolve(image.getFilePath());
        Files.deleteIfExists(filePath);

        // Delete from Database
        imageRepository.delete(image);
    }
}