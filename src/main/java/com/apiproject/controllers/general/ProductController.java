package com.apiproject.controllers.general;

import com.apiproject.DTOs.Admin.UserAdminDTO;
import com.apiproject.DTOs.General.ProductResponseDTO;
import com.apiproject.entities.general.Product;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.general.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Product", description = "Catalogo y CRUD de productos de la tienda")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    public final ProductService productService;

    @Operation(
            summary = "Listar los productos del admin autenticado",
            description = "Devuelve todos los productos (activos e inactivos) del admin del token, con sus imagenes. La respuesta se cachea por admin."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos del admin"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es ADMIN")
    })
    @GetMapping("/search")
    public List<ProductResponseDTO> getAll(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return productService.findAll(authenticatedUser.id());
    }

    @Operation(
            summary = "Listar productos activos con al menos una imagen",
            description = "Catalogo publico usado por el filtro de la tienda. Resuelve productos e imagenes en una sola consulta (sin N+1) y se cachea."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos activos con sus imagenes"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido")
    })
    @GetMapping("/search/active-with-images")
    public List<ProductResponseDTO> getActiveProductsWithImages() {
        return productService.findAllActiveWithImages();
    }

    @Operation(
            summary = "Buscar un producto por ID",
            description = "Devuelve una lista con el producto si existe. Si el ID no existe responde 200 con una lista vacia (no lanza 404)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado, o lista vacia si no existe"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido")
    })
    @GetMapping("/search/id/{id}")
    public ResponseEntity<List<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        List<ProductResponseDTO> products = productService.findAllById(id);
        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Buscar productos por categoria",
            description = "Busqueda por categoria sin distinguir mayusculas/minusculas. Devuelve lista vacia si no hay coincidencias."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos de la categoria"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido")
    })
    @GetMapping("/search/category/{category}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable String category) {
        List<ProductResponseDTO> search = productService.findAllByCategoryIgnoreCase(category);
        return ResponseEntity.ok(search);
    }

    @Operation(
            summary = "Buscar un producto por nombre exacto",
            description = "Busqueda por nombre sin distinguir mayusculas/minusculas. Lanza 404 si no existe."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "404", description = "No existe un producto con ese nombre")
    })
    @GetMapping("/search/name/{name}")
    public ProductResponseDTO getProduct(@PathVariable String name) {
        return productService.findByNameIgnoreCase(name);
    }

    @Operation(
            summary = "Crear un producto (ADMIN)",
            description = "Guarda el producto asociado al admin autenticado. Por compatibilidad recibe la entidad Product directamente en el body (JSON) y el dueño enviado se sobrescribe con el del token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto creado"),
            @ApiResponse(responseCode = "400", description = "Body ausente, invalido o con tipos incorrectos"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es ADMIN"),
            @ApiResponse(responseCode = "409", description = "El producto viola una restriccion de la base de datos")
    })
    @PostMapping(value = "/saveProduct", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Product saveProductJson(
            @RequestBody Product product,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        productService.save(product, authenticatedUser.id());
        return product;
    }

    @Operation(
            summary = "Desactivar un producto (borrado suave) (ADMIN)",
            description = "Recibe el producto por parametros de formulario/query (no JSON) y solo marca active=false; no borra la fila. Lanza 404 si el producto no existe."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto desactivado (devuelve el producto recibido)"),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos o id ausente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario no es ADMIN o el producto pertenece a otro admin"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PostMapping("/deleteSafe")
    public ResponseEntity<Product> deleteProduct(
            Product product,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        productService.deleteProductSafe(product.getId(), authenticatedUser.id());
        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "Actualizar un producto (ADMIN)",
            description = "Actualiza solo los campos enviados (name, price, stock, category, description) del producto del admin autenticado. Valida que el producto sea del admin."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "400", description = "Body ausente o invalido"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario no es ADMIN o el producto pertenece a otro admin"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "409", description = "La actualizacion viola una restriccion de la base de datos")
    })
    @PutMapping("/update/{id}")
    public ProductResponseDTO updateProduct(
            @PathVariable long id,
            @RequestBody Product product,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return productService.updateProduct(id, product, authenticatedUser.id());
    }

    @Operation(
            summary = "Listar productos activos (paginado por tamano)",
            description = "Devuelve la primera pagina de productos activos ordenados por nombre; sizePage define el tamano de pagina (no la pagina). Se cachea por tamano."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagina de productos activos"),
            @ApiResponse(responseCode = "400", description = "sizePage ausente o con un valor no numerico"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido")
    })
    @GetMapping("/activeProducts")
    public Page<ProductResponseDTO> getActiveProducts(@RequestParam(required = false) int sizePage) {
        return productService.findByActiveTrue(sizePage);
    }

    @Operation(
            summary = "Listar todos los productos del admin con sus imagenes",
            description = "Igual que /search pero resuelve las imagenes en la misma consulta. Cacheado por admin."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos con imagenes"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El usuario autenticado no es ADMIN")
    })
    @GetMapping("/search/with-images")
    public List<ProductResponseDTO> getAllWithImages(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return productService.findAllWithImages(authenticatedUser.id());
    }

    @Operation(
            summary = "Obtener la tienda (admin) duena de un producto",
            description = "Devuelve los datos publicos del admin que publico el producto. Lanza 400 si el producto esta inactivo y 404 si no existe."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Datos del admin dueno del producto"),
            @ApiResponse(responseCode = "400", description = "El producto esta inactivo"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{productId}/admin")
    public UserAdminDTO getAdminByProduct(
            @PathVariable Long productId
    ) {
        return productService.showUserAdmin(productId);
    }
}
