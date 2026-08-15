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
| `admin/` | `DashboardController`, `UserController`, `ProductImageController`, `NotificationController` (SSE), `ClientsSummaryViewController`, `SalesItemViewController` |
| `client/` | `ClientControllers` |
| `general/` | `ProductController`, `SaleController` |

Los endpoints de cada controlador están documentados en [[Endpoints API]].

## entities/
| Paquete | Entidades |
|---|---|
| `admin/` | `UserAdmin`, `ProductImage`, `ReportDashboard` |
| `client/` | `UserClient`, `PaymentCard` |
| `general/` | `Product`, `Sale`, `SalesItem` |

Las entidades mapean las tablas de [[Esquema de Base de Datos]].

## DTOs/
| Paquete | DTOs |
|---|---|
| `Auth/` | Login/Register (admin y client) con sus respuestas |
| `Admin/` | `DashboardDTO`, `NotificationEventDTO`, `ProductImageDTO`, `SaleItemResponseDTO`, `SaleResponseDato`, `UserAdminDTO`, `UserResponseDTO` |
| `Client/` | `ClientResponseDTO`, `ClientDescriptionAboutUsersDTO`, `PaymentCardRequestDTO`, `PaymentCardResponseDTO` |
| `General/` | `ProductResponseDTO`, `PurchaseRequestDTO`, `PurchaseResponseDTO`, `PurchaseItemRequestDTO/ResponseDTO` |

Los payloads concretos de cada DTO están en [[Endpoints API]].

## enums/
- `Status` — estados de entidades.
- `FileTypes` — tipos de archivo soportados (Excel/PDF).

## repositories/
| Paquete | Descripción |
|---|---|
| `admin/` | `UserRepository`, `DashboardRepository`, `ProductImageRepository`, `ReportDashboardRepository`, `ClientsSummaryViewRepository`, `SaleItemViewRepository` |
| `client/` | `ClientRepository`, `PaymentCardRepository` |
| `general/` | `ProductRepository`, `SaleRepository`, `SaleItemRepository` |
| `projection/` | Proyecciones JPA: `ClientLoginProjection`, `ClientSummaryProjection`, `ClientHistoryProjection`, `DashboardProjection`, `ReportDashboardProjection`, `PaymentCardDetailsProjection`, `SaleItemViewProjection` |
| `reportGenerator/` | `ReportService` (interfaz de exportación) |

Los repos consultan las vistas de [[Esquema de Base de Datos#Vistas]].

## services/
| Paquete | Servicios |
|---|---|
| `admin/` | `UserService`, `DashboardService`, `ProductImageService`, `NotificationService`, `ClientsSummaryViewService`, `SaleItemViewService`, `ReportServiceFactory` |
| `client/` | `ClientService` |
| `general/` | `ProductService`, `SaleService`, `SaleItemService` |
| `reportGenerator/` | `ReportService`, `ExcelService`, `PdfService` |

## security/
- `AuthenticatedUser` — usuario autenticado en contexto.
- `JwtService` — creación y validación de tokens.
- `JwtAuthenticationFilter` — filtro de autenticación por Bearer token.
- `OAuth2SuccessHandler` — manejo de login exitoso con Google.

## exceptions/
- `GlobalExceptionHandler` — manejo centralizado de errores.
- `ApiError` — estructura de respuesta de error.
- `ResourceNotFoundException` — excepción de recurso no encontrado.
