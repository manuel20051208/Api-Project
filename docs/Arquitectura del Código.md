# Arquitectura del Código

> Nota: [[PROJECT_STRUCTURE]] · Sigue a [[Endpoints API]] y [[Esquema de Base de Datos]]

Paquete base: `src/main/java/com/apiproject`. La API expuesta se documenta en [[Endpoints API]]; la persistencia en [[Esquema de Base de Datos]].

## Raíz
- `ApiProjectApplication.java` — Punto de entrada (`@SpringBootApplication`, `@EnableCaching`).

## config/
| Clase | Función |
|---|---|
| `SecurityConfig` | Cadena de filtros: JWT, OAuth2 Google, CORS, reglas por ruta/rol → [[Configuración]] |
| `JwtService` *(security)* / `JwtAuthenticationFilter` | Lógica del token |
| `CloudinaryConfig` | Bean de Cloudinary |
| `SwaggerConfig` | Documentación OpenAPI |
| `CacheConstants` | Constantes de caché |

## controllers/
| Paquete | Controladores |
|---|---|
| `admin/` | `DashboardController`, `UserController`, `ProductImageController`, `NotificationController` (SSE), `ClientsSummaryViewController`, `SalesItemViewController`, `CuponController`, `SecondHandCuponController`, `ServiceOfferedController`, `ServiceCuponController`, `SecondHandProductImageController`, `RankingsController` (`/api/rankings/*` con `best-clients` y `best-products`) |
| `client/` | `ClientControllers` |
| `general/` | `ProductController`, `SaleController`, `ShProductController`, `ShSaleController` |

Los endpoints de cada controlador están documentados en [[Endpoints API]].

## entities/
| Paquete | Entidades |
|---|---|
| `admin/` | `UserAdmin`, `ProductImage`, `ReportDashboard`, `Cupon`, `ProductCuponApplied`, `CuponUsedByClients`, `SecondHandCupon`, `SecondHandProductCuponsApplied`, `ShCuponUsedByClients`, `SecondHandProductImage`, `ServiceOffered`, `ServiceCupon` |
| `client/` | `UserClient`, `PaymentCard` |
| `general/` | `Product`, `Sale`, `SalesItem`, `SecondHandProduct`, `ShSale`, `ShSalesItem` |

Las entidades mapean las tablas de [[Esquema de Base de Datos]].

## DTOs/
| Paquete | DTOs |
|---|---|
| `Auth/` | Login/Register (admin y client) con sus respuestas |
| `Admin/` | `DashboardDTO`, `NotificationEventDTO`, `ProductImageDTO`, `SaleItemResponseDTO`, `SaleResponseDato`, `UserAdminDTO`, `UserResponseDTO`, `CuponRequestDTO`, `CuponResponseDTO`, `SecondHandCuponRequestDTO`, `SecondHandCuponResponseDTO`, `ServiceRequestDTO`, `ServiceResponseDTO`, `ServiceCuponRequestDTO`, `ServiceCuponResponseDTO`, `ShProductImageDTO` |
| `Client/` | `ClientResponseDTO`, `ClientDescriptionAboutUsersDTO`, `PaymentCardRequestDTO`, `PaymentCardResponseDTO` |
| `General/` | `ProductResponseDTO`, `PurchaseRequestDTO` (ahora con `cuponCode` opcional), `PurchaseResponseDTO` (con `originalTotal`/`discountApplied`), `PurchaseItemRequestDTO/ResponseDTO`, `ShProductResponseDTO`, `ShProductCardResponseDTO`, `ShPurchaseRequestDTO`, `ShPurchaseItemRequestDTO`, `ShPurchaseResponseDTO`, `ShPurchaseItemResponseDTO`, `CuponValidationResponseDTO`, `TheThreeBestClients`, `TheThreeBestProducts` |

Los payloads concretos de cada DTO están en [[Endpoints API]].

## enums/
- `Status` — estados de entidades.
- `FileTypes` — tipos de archivo soportados (Excel/PDF).
- `ColorTypes` — colores de interfaz (`VERDE`, `AZUL`, `VIOLETA`, `AMBAR`, `ROSA`); mapeado a la columna `color_config` de `users`/`clients` → [[Esquema de Base de Datos#Colores de interfaz (`color_config`)]].

## repositories/
| Paquete            | Descripción                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| ------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `admin/`           | `UserRepository`, `DashboardRepository`, `ProductImageRepository`, `ReportDashboardRepository`, `ClientsSummaryViewRepository`, `SaleItemViewRepository`, `CuponRepository`, `ProductCuponRepository`, `CuponUsedByClientsRepository`, `SecondHandCuponRepository`, `SecondHandProductCuponRepository`, `ShCuponUsedByClientsRepository`, `ServiceOfferedRepository`, `ServiceCuponRepository`, `SecondHandProductImagesRepository`, `RankingsRepository`                                                                                                            |
| `client/`          | `ClientRepository`, `PaymentCardRepository`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `general/`         | `ProductRepository`, `SaleRepository`, `SaleItemRepository`, `SecondHandProductRepository`, `ShSaleRepository`, `ShSaleItemRepository`                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `projection/`      | Proyecciones JPA: `ClientLoginProjection`, `ClientSummaryProjection`, `ClientHistoryProjection`, `DashboardProjection`, `ReportDashboardProjection`, `PaymentCardDetailsProjection`, `SaleItemViewProjection`, `CuponAdminProjection`, `CuponUsageProjection`, `ShProductCardProjection`, `ShSaleHistoryProjection`, `ProductCuponAssignmentProjection`, `ShProductCuponAssignmentProjection`, `ServiceCuponAssignmentProjection` (asignaciones cupón→cliente con dato de producto/servicio; reemplazaron a `List<Object[]>` + casteo en los 3 servicios de cupones) |
| `reportGenerator/` | `ReportService` (interfaz de exportación)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |

Los repos consultan las vistas de [[Esquema de Base de Datos#Vistas]]. **Convención**: todas las consultas nuevas (cupones, segunda mano, servicios) usan `nativeQuery = true`; los bloqueos de stock y canje de cupones usan `FOR UPDATE`.

## services/
| Paquete | Servicios |
|---|---|
| `admin/` | `UserService`, `DashboardService` (ahora delega en `RankingsService` para top clientes/productos), `ProductImageService`, `NotificationService`, `ClientsSummaryViewService`, `SaleItemViewService`, `ReportServiceFactory`, `CuponService`, `SecondHandCuponService`, `ServiceOfferedService`, `ServiceCuponService`, `SecondHandProductImageService`, `RankingsService` |
| `client/` | `ClientService` |
| `general/` | `ProductService`, `SaleService` (ahora integra cupones en `purchase`), `SaleItemService`, `SecondHandProductService`, `ShSaleService` |
| `reportGenerator/` | `ReportService`, `ExcelService`, `PdfService` |
| `databasefunctions/` | `MaterializedViewRefreshService` — refresca las materialized views de rankings cada 5 min (`REFRESH MATERIALIZED VIEW CONCURRENTLY` vía `JdbcTemplate`) → [[Esquema de Base de Datos#Vistas materializadas — rankings]] |

Regla de cupones (`CuponService.resolveForCart/redeem/registerUsage`, espejo SH): el cupón debe estar vigente y tener usos disponibles, pertenece a un **solo admin** y descuenta **solo los productos del carrito de ese admin que estén vinculados** (`product_cupons_applied`); se permite carrito multi-vendedor (lo de otros admins se cobra completo). Se bloquea con `FOR UPDATE`, se descuenta 1 uso por compra y se registra la fila en `*_used_by_clients`. El descuento es un porcentaje sobre cada subtotal elegible.

## security/
- `AuthenticatedUser` — usuario autenticado en contexto.
- `JwtService` — creación y validación de tokens.
- `JwtAuthenticationFilter` — filtro de autenticación por Bearer token.
- `OAuth2SuccessHandler` — manejo de login exitoso con Google.

## exceptions/
- `GlobalExceptionHandler` — manejo centralizado de errores.
- `ApiError` — estructura de respuesta de error.
- `ResourceNotFoundException` — excepción de recurso no encontrado.
