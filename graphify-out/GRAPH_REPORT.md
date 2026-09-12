# Graph Report - ApiProject  (2026-09-11)

## Corpus Check
- 193 files · ~40,976 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1672 nodes · 4049 edges · 108 communities (96 shown, 12 thin omitted)
- Extraction: 92% EXTRACTED · 8% INFERRED · 0% AMBIGUOUS · INFERRED: 329 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `028ced3c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Product
- UserService
- SecurityConfig.java
- ProductImage
- JwtService
- SaleItemViewProjection
- CuponAdminProjection
- ResponseStatusException
- PaymentCardRepository
- ClientControllers.java
- GlobalExceptionHandler
- SaleService.java
- UserClient
- ClientHistoryProjection
- NotificationService
- PdfService
- read
- ExcelService
- Protected Endpoints
- ColorTypes
- PaymentCard
- mvnw
- CuponUsageProjection
- ClientLoginProjection
- CloudinaryConfig.java
- SaleItemService.java
- SaleResponseDato.java
- com.apiproject:ApiProject
- ShProductCuponAssignmentProjection
- ResourceNotFoundException
- SwaggerConfig.java
- ServiceOffered
- graphify-out Knowledge Graph
- ApiProjectApplication
- ApiProjectApplicationTests.java
- Autenticación y usuarios
- opencode.json
- SecondHandProductService
- graphify.js
- BRAIN — Cerebro del Proyecto ApiProject
- SecondHandProductImage
- Graphify
- ShProductImageDTO
- ProductCuponApplied
- ShSaleController.java
- CuponController
- ShSaleService.java
- ShSaleHistoryProjection
- ApiJuegoInventario
- ReportDashboardProjection
- ShProductCardProjection
- ShCuponUsedByClients
- SaleController.java
- ClientSummaryProjection
- .purchase
- ProductCuponAssignmentProjection
- ServiceCuponAssignmentProjection
- FileTypes
- Sale
- SecondHandProductRepository
- Arquitectura del Código
- ShSale
- Estructura del Proyecto — ApiProject
- JpaRepository
- AuthenticatedUser
- CuponRepository
- CuponUsedByClients
- DashboardController.java
- SecondHandProductCuponsApplied
- Configuración
- RankingsController.java
- Esquema de Base de Datos
- ServiceCuponToAClient
- Endpoints API
- Clientes y tarjetas
- Cupon
- Imágenes de producto
- Panel de administración (ADMIN)
- Productos de segunda mano
- CuponResponseDTO
- Productos
- Servicios ofrecidos
- SecondHandCuponController
- Ventas
- ServiceCuponService
- SecondHandCupon
- UserResponseDTO
- ServiceCuponToAClientRepository
- ServiceCuponController
- .onAuthenticationSuccess
- DashboardService.java
- ServiceCupon
- UserAdmin
- CuponService
- .update
- UserRepository
- UserController.java
- LoginClientResponseDTO
- ShProductCuponToAClientRepository
- DashboardProjection
- ShProductCuponToAClient
- SalesItem
- SecondHandProduct
- ReportDashboard

## God Nodes (most connected - your core abstractions)
1. `AuthenticatedUser` - 99 edges
2. `UserClient` - 61 edges
3. `UserAdmin` - 57 edges
4. `ResourceNotFoundException` - 55 edges
5. `Product` - 51 edges
6. `CuponService` - 39 edges
7. `SecondHandProduct` - 37 edges
8. `SecondHandCuponService` - 36 edges
9. `ClientService` - 33 edges
10. `ProductRepository` - 28 edges

## Surprising Connections (you probably didn't know these)
- `DashboardDTO` --references--> `ClientSummaryProjection`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/Admin/DashboardDTO.java → src/main/java/com/apiproject/repositories/projection/ClientSummaryProjection.java
- `ProductResponseDTO` --references--> `ProductImageDTO`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/General/ProductResponseDTO.java → src/main/java/com/apiproject/DTOs/Admin/ProductImageDTO.java
- `ShProductResponseDTO` --references--> `ShProductImageDTO`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/General/ShProductResponseDTO.java → src/main/java/com/apiproject/DTOs/Admin/ShProductImageDTO.java
- `UserResponseDTO` --references--> `ColorTypes`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/Admin/UserResponseDTO.java → src/main/java/com/apiproject/enums/ColorTypes.java
- `LoginAdminResponseDTO` --references--> `ColorTypes`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/Auth/LoginAdminResponseDTO.java → src/main/java/com/apiproject/enums/ColorTypes.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Bearer Token Authentication Flow** — api_frontend_consumption_login, api_frontend_consumption_register, api_frontend_consumption_bearer_token, api_frontend_consumption_protected_endpoints, api_frontend_consumption_client_token, api_frontend_consumption_admin_token [INFERRED 0.85]
- **Payment Card Feature** — api_frontend_consumption_payment_cards, api_frontend_consumption_purchase, api_frontend_consumption_add_payment_cards_sql, api_frontend_consumption_payment_cards_table [INFERRED 0.85]
- **Product Catalog Feature** — api_frontend_consumption_product_catalog, api_frontend_consumption_product_images, api_frontend_consumption_purchase, api_frontend_consumption_client_token, api_frontend_consumption_admin_token [INFERRED 0.75]

## Communities (108 total, 12 thin omitted)

### Community 0 - "Product"
Cohesion: 0.05
Nodes (44): JdbcTemplate, Lock, PreAuthorize, Scheduled, CacheConstants, GetMapping, Operation, Page (+36 more)

### Community 1 - "UserService"
Cohesion: 0.21
Nodes (7): AllArgsConstructor, Cacheable, CacheManager, Cloudinary, PasswordEncoder, Service, UserService

### Community 2 - "SecurityConfig.java"
Cohesion: 0.15
Nodes (18): CorsConfigurationSource, EnableMethodSecurity, EnableWebSecurity, FilterChain, HttpSecurity, OncePerRequestFilter, SecurityFilterChain, Bean (+10 more)

### Community 3 - "ProductImage"
Cohesion: 0.09
Nodes (29): DeleteMapping, GetMapping, MultipartFile, Operation, PostMapping, RequestMapping, ResponseEntity, RestController (+21 more)

### Community 4 - "JwtService"
Cohesion: 0.19
Nodes (8): BeforeEach, Claims, ExtendWith, SecretKey, Service, JwtService, Test, JwtServiceTest

### Community 5 - "SaleItemViewProjection"
Cohesion: 0.11
Nodes (20): GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag, SalesItemViewController (+12 more)

### Community 7 - "ResponseStatusException"
Cohesion: 0.16
Nodes (12): ResponseStatusException, ClientService, Cacheable, CachePut, Caching, Cloudinary, MultipartFile, PasswordEncoder (+4 more)

### Community 8 - "PaymentCardRepository"
Cohesion: 0.19
Nodes (5): Modifying, Query, Repository, PaymentCardRepository, PaymentCardDetailsProjection

### Community 9 - "ClientControllers.java"
Cohesion: 0.20
Nodes (10): ClientControllers, GetMapping, MultipartFile, Operation, PatchMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+2 more)

### Community 10 - "GlobalExceptionHandler"
Cohesion: 0.28
Nodes (11): DataIntegrityViolationException, ExceptionHandler, HttpRequestMethodNotSupportedException, HttpStatus, MaxUploadSizeExceededException, RestControllerAdvice, ApiError, GlobalExceptionHandler (+3 more)

### Community 11 - "SaleService.java"
Cohesion: 0.14
Nodes (10): PurchaseItemRequestDTO, PurchaseRequestDTO, CacheManager, Caching, CouponResolution, RequiredArgsConstructor, Service, Transactional (+2 more)

### Community 12 - "UserClient"
Cohesion: 0.19
Nodes (10): AllArgsConstructor, Data, Entity, NoArgsConstructor, Table, UserClient, ClientRepository, Modifying (+2 more)

### Community 14 - "NotificationService"
Cohesion: 0.21
Nodes (11): GetMapping, Operation, RequestMapping, RequiredArgsConstructor, RestController, SseEmitter, NotificationController, NotificationEventDTO (+3 more)

### Community 15 - "PdfService"
Cohesion: 0.22
Nodes (7): Color, Document, Font, PdfPTable, Override, Service, PdfService

### Community 16 - "read"
Cohesion: 0.17
Nodes (11): cat *.env*, rm -rf *, type *.env*, $schema, permission, bash, read, *.env (+3 more)

### Community 17 - "ExcelService"
Cohesion: 0.23
Nodes (6): CellStyle, IndexedColors, Sheet, ExcelService, Override, Workbook

### Community 18 - "Protected Endpoints"
Cohesion: 0.22
Nodes (14): add-payment-cards.sql Script, Admin Token, /api Base Path, Bearer Token Authentication, Client Token, Login Endpoint, LoginResponseDTO, Payment Cards API (+6 more)

### Community 19 - "ColorTypes"
Cohesion: 0.22
Nodes (7): ClientResponseDTO, ColorTypes, AMBAR, AZUL, ROSA, VERDE, VIOLETA

### Community 20 - "PaymentCard"
Cohesion: 0.24
Nodes (9): PaymentCardRequestDTO, PaymentCardResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table (+1 more)

### Community 21 - "mvnw"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 22 - "CuponUsageProjection"
Cohesion: 0.23
Nodes (6): Modifying, Query, Repository, Transactional, ServiceCuponRepository, CuponUsageProjection

### Community 24 - "CloudinaryConfig.java"
Cohesion: 0.53
Nodes (4): CloudinaryConfig, Bean, Cloudinary, Configuration

### Community 25 - "SaleItemService.java"
Cohesion: 0.83
Nodes (3): RequiredArgsConstructor, Service, SaleItemService

### Community 29 - "ResourceNotFoundException"
Cohesion: 0.19
Nodes (6): ResourceNotFoundException, CouponResolution, RequiredArgsConstructor, Service, Transactional, SecondHandCuponService

### Community 30 - "SwaggerConfig.java"
Cohesion: 0.53
Nodes (4): OpenAPI, Bean, Configuration, SwaggerConfig

### Community 31 - "ServiceOffered"
Cohesion: 0.09
Nodes (27): DeleteMapping, GetMapping, Operation, PostMapping, PutMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+19 more)

### Community 32 - "graphify-out Knowledge Graph"
Cohesion: 0.40
Nodes (5): GRAPH_REPORT.md, graphify-out Knowledge Graph, graphify query/path/explain Tools, graphify update Command, graphify-out wiki index

### Community 33 - "ApiProjectApplication"
Cohesion: 0.53
Nodes (4): EnableCaching, EnableScheduling, SpringBootApplication, ApiProjectApplication

### Community 34 - "ApiProjectApplicationTests.java"
Cohesion: 0.60
Nodes (3): SpringBootTest, ApiProjectApplicationTests, Test

### Community 35 - "Autenticación y usuarios"
Cohesion: 0.22
Nodes (9): Autenticación y usuarios, GET `/client/{adminId}/admin` — Datos de tienda del admin (público GET), GET `/user/admin` — Datos del admin autenticado (ADMIN), PATCH `/user/modify` — Modificar admin (ADMIN), PATCH `/user/upload-profile` — Subir foto de perfil admin (ADMIN), POST `/client/login` — Login cliente (público), POST `/client/register` — Registro cliente (público), POST `/user/login` — Login admin (público) (+1 more)

### Community 36 - "opencode.json"
Cohesion: 0.50
Nodes (3): $schema, plugin, .opencode/plugins/graphify.js

### Community 37 - "SecondHandProductService"
Cohesion: 0.15
Nodes (16): GetMapping, Operation, PostMapping, PutMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+8 more)

### Community 39 - "BRAIN — Cerebro del Proyecto ApiProject"
Cohesion: 0.07
Nodes (26): 10. Tests, 11. Convenciones y gotchas, 12. Referencias, 1. Qué es, 2. Stack técnico, 3. Estructura de carpetas, 4. Cómo funciona la aplicación (flujo), 5. Capas de código (detalle) (+18 more)

### Community 40 - "SecondHandProductImage"
Cohesion: 0.15
Nodes (17): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandProductImage, Modifying (+9 more)

### Community 42 - "ShProductImageDTO"
Cohesion: 0.14
Nodes (13): DeleteMapping, GetMapping, MultipartFile, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+5 more)

### Community 43 - "ProductCuponApplied"
Cohesion: 0.21
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ProductCuponApplied, Modifying (+4 more)

### Community 44 - "ShSaleController.java"
Cohesion: 0.18
Nodes (11): GetMapping, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Tag (+3 more)

### Community 45 - "CuponController"
Cohesion: 0.18
Nodes (11): CuponController, DeleteMapping, GetMapping, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+3 more)

### Community 46 - "ShSaleService.java"
Cohesion: 0.14
Nodes (16): SaleItemResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShSalesItem (+8 more)

### Community 47 - "ShSaleHistoryProjection"
Cohesion: 0.15
Nodes (3): Query, ShSaleHistoryProjection, Transactional

### Community 48 - "ApiJuegoInventario"
Cohesion: 0.10
Nodes (20): ApiJuegoInventario, Arquitectura, Base de datos, Catálogo y búsqueda de productos, Configuración, Documentación adicional, Ejecución con Docker, Ejecución local (+12 more)

### Community 51 - "ShCuponUsedByClients"
Cohesion: 0.33
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShCuponUsedByClients

### Community 52 - "SaleController.java"
Cohesion: 0.23
Nodes (9): Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, Tag, SaleController, PurchaseItemResponseDTO (+1 more)

### Community 53 - "ClientSummaryProjection"
Cohesion: 0.11
Nodes (20): ClientsSummaryViewController, GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag (+12 more)

### Community 54 - ".purchase"
Cohesion: 0.24
Nodes (5): ShPurchaseItemRequestDTO, ShPurchaseRequestDTO, CouponResolution, ShSaleDraft, ShSaleService

### Community 55 - "ProductCuponAssignmentProjection"
Cohesion: 0.11
Nodes (13): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ProductCuponToAClient, Modifying (+5 more)

### Community 57 - "FileTypes"
Cohesion: 0.17
Nodes (8): FileTypes, EXCEL, PDF, ReportService, ResponseEntity, Component, ReportServiceFactory, Component

### Community 58 - "Sale"
Cohesion: 0.27
Nodes (9): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, Sale, Repository (+1 more)

### Community 59 - "SecondHandProductRepository"
Cohesion: 0.30
Nodes (5): Modifying, Query, Repository, Transactional, SecondHandProductRepository

### Community 60 - "Arquitectura del Código"
Cohesion: 0.18
Nodes (11): Arquitectura del Código, config/, controllers/, DTOs/, entities/, enums/, exceptions/, Raíz (+3 more)

### Community 61 - "ShSale"
Cohesion: 0.29
Nodes (9): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShSale, Repository (+1 more)

### Community 62 - "Estructura del Proyecto — ApiProject"
Cohesion: 0.20
Nodes (10): Arquitectura general, Dependencias principales (pom.xml), Directorios de datos y herramientas, Docker, docker-compose.yml, Dockerfile, Estructura del Proyecto — ApiProject, Mapa de notas (+2 more)

### Community 63 - "JpaRepository"
Cohesion: 0.33
Nodes (6): JpaRepository, Query, Repository, ShCuponUsedByClientsRepository, Repository, SaleItemRepository

### Community 64 - "AuthenticatedUser"
Cohesion: 0.24
Nodes (4): GrantedAuthority, AuthenticatedUser, Override, UserDetails

### Community 65 - "CuponRepository"
Cohesion: 0.36
Nodes (5): CuponRepository, Modifying, Query, Repository, Transactional

### Community 66 - "CuponUsedByClients"
Cohesion: 0.23
Nodes (10): CuponUsedByClients, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, CuponUsedByClientsRepository (+2 more)

### Community 67 - "DashboardController.java"
Cohesion: 0.21
Nodes (11): DashboardController, GetMapping, Operation, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Tag (+3 more)

### Community 68 - "SecondHandProductCuponsApplied"
Cohesion: 0.21
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandProductCuponsApplied, Modifying (+4 more)

### Community 70 - "Configuración"
Cohesion: 0.29
Nodes (7): Archivos (src/main/resources), Configuración, Perfil dev — puntos clave, Reportes, Seguridad (SecurityConfig), Tareas programadas, Variables de entorno (.env)

### Community 71 - "RankingsController.java"
Cohesion: 0.16
Nodes (16): GetMapping, Operation, RequestMapping, RequiredArgsConstructor, RestController, Tag, RankingsController, TheThreeBestClients (+8 more)

### Community 72 - "Esquema de Base de Datos"
Cohesion: 0.18
Nodes (11): Colores de interfaz (`color_config`), Cupones para productos normales, Esquema de Base de Datos, Nota, Segunda mano, Servicios ofrecidos, Tablas, Tablas nuevas — cupones, segunda mano y servicios (+3 more)

### Community 73 - "ServiceCuponToAClient"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ServiceCuponToAClient

### Community 77 - "Endpoints API"
Cohesion: 0.18
Nodes (11): Cupones de productos, Cupones de segunda mano, Endpoints API, GET `/notification/stream` — Stream de eventos (ADMIN), Historial de ventas (ADMIN), Notificaciones (SSE), POST `/cupons` — Crear cupón (ADMIN), Rankings (ADMIN) (+3 more)

### Community 78 - "Clientes y tarjetas"
Cohesion: 0.22
Nodes (9): Clientes y tarjetas, GET `/client/payment-cards` — Listar tarjetas (CLIENT), GET `/client/profile-photo` — Obtener foto de perfil (CLIENT), GET `/client/user-data` — Datos del cliente (CLIENT), GET /client/user-payments — Historial de compras del cliente (CLIENT), PATCH `/client/modify` — Modificar datos del cliente (CLIENT), PATCH `/client/payment-cards/{cardId}/status?active=true` — Activar/desactivar tarjeta (CLIENT), PATCH `/client/upload-profile` — Foto de perfil del cliente (CLIENT) (+1 more)

### Community 79 - "Cupon"
Cohesion: 0.27
Nodes (8): CuponAppliedDTO, Cupon, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table

### Community 80 - "Imágenes de producto"
Cohesion: 0.50
Nodes (4): DELETE `/product-images/{imageId}` — Eliminar imagen (ADMIN), GET `/product-images/{productId}` — Imágenes de un producto (público), Imágenes de producto, POST `/product-images/upload/{productId}` — Subir imagen (ADMIN)

### Community 81 - "Panel de administración (ADMIN)"
Cohesion: 0.50
Nodes (4): GET /dashboard-controller/excel — Reporte en Excel (adjunto .xlsx), GET `/dashboard-controller/get-data-dashboard` — Métricas del dashboard, GET /dashboard-controller/pdf — Reporte en PDF (adjunto .pdf), Panel de administración (ADMIN)

### Community 82 - "Productos de segunda mano"
Cohesion: 0.50
Nodes (4): Imágenes SH — `/api/sh-product-images`, POST `/sh-product/save` — Crear producto SH (ADMIN), POST `/sh-sale/purchase` — Compra SH (CLIENT), Productos de segunda mano

### Community 83 - "CuponResponseDTO"
Cohesion: 0.27
Nodes (3): PutMapping, CuponRequestDTO, CuponResponseDTO

### Community 84 - "Productos"
Cohesion: 0.50
Nodes (4): POST `/product/deleteSafe` — Borrado suave (ADMIN), POST `/product/saveProduct` — Crear producto (ADMIN), Productos, PUT `/product/update/{id}` — Actualizar producto (ADMIN)

### Community 85 - "Servicios ofrecidos"
Cohesion: 0.67
Nodes (3): Cupones de servicio, POST `/services` — Crear servicio (ADMIN), Servicios ofrecidos

### Community 86 - "SecondHandCuponController"
Cohesion: 0.15
Nodes (12): DeleteMapping, GetMapping, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+4 more)

### Community 87 - "Ventas"
Cohesion: 0.67
Nodes (3): POST `/sale/purchase` — Compra de cliente (CLIENT), POST `/sale/sale/refresh` — Recalcular producto por venta (ADMIN), Ventas

### Community 88 - "ServiceCuponService"
Cohesion: 0.22
Nodes (7): PostMapping, CuponAssignmentRequestDTO, ServiceCuponToClientResponseDTO, RequiredArgsConstructor, Service, Transactional, ServiceCuponService

### Community 89 - "SecondHandCupon"
Cohesion: 0.20
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandCupon, Modifying (+4 more)

### Community 90 - "UserResponseDTO"
Cohesion: 0.23
Nodes (6): MultipartFile, PatchMapping, UserResponseDTO, CachePut, MultipartFile, Transactional

### Community 91 - "ServiceCuponToAClientRepository"
Cohesion: 0.33
Nodes (5): Modifying, Query, Repository, Transactional, ServiceCuponToAClientRepository

### Community 92 - "ServiceCuponController"
Cohesion: 0.24
Nodes (9): DeleteMapping, GetMapping, Operation, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Tag (+1 more)

### Community 93 - ".onAuthenticationSuccess"
Cohesion: 0.22
Nodes (9): Authentication, AuthenticationSuccessHandler, Component, HttpServletRequest, HttpServletResponse, Override, RequiredArgsConstructor, Transactional (+1 more)

### Community 94 - "DashboardService.java"
Cohesion: 0.26
Nodes (9): Repository, DashboardRepository, Query, ReportDashboardRepository, DashboardService, Cacheable, RequiredArgsConstructor, Service (+1 more)

### Community 95 - "ServiceCupon"
Cohesion: 0.19
Nodes (10): PutMapping, ServiceCuponRequestDTO, ServiceCuponResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor (+2 more)

### Community 96 - "UserAdmin"
Cohesion: 0.23
Nodes (8): ClientDescriptionAboutUsersDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, UserAdmin

### Community 97 - "CuponService"
Cohesion: 0.23
Nodes (5): CouponResolution, CuponService, RequiredArgsConstructor, Service, Transactional

### Community 98 - ".update"
Cohesion: 0.32
Nodes (3): PutMapping, SecondHandCuponRequestDTO, SecondHandCuponResponseDTO

### Community 99 - "UserRepository"
Cohesion: 0.33
Nodes (5): Modifying, Query, Repository, Transactional, UserRepository

### Community 100 - "UserController.java"
Cohesion: 0.16
Nodes (12): DynamicUpdate, GetMapping, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, Tag (+4 more)

### Community 101 - "LoginClientResponseDTO"
Cohesion: 0.24
Nodes (4): PostMapping, LoginClientRequestDTO, LoginClientResponseDTO, RegisterClientRequestDTO

### Community 102 - "ShProductCuponToAClientRepository"
Cohesion: 0.33
Nodes (5): Modifying, Query, Repository, Transactional, ShProductCuponToAClientRepository

### Community 104 - "ShProductCuponToAClient"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShProductCuponToAClient

### Community 105 - "SalesItem"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SalesItem

### Community 106 - "SecondHandProduct"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandProduct

### Community 107 - "ReportDashboard"
Cohesion: 0.70
Nodes (4): Immutable, Entity, Table, ReportDashboard

## Knowledge Gaps
- **144 isolated node(s):** `$schema`, `*.env`, `*.env.example`, `*.key`, `*.pem` (+139 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **12 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AuthenticatedUser` connect `AuthenticatedUser` to `Product`, `ProductImage`, `JwtService`, `ClientControllers.java`, `NotificationService`, `PaymentCard`, `ServiceOffered`, `SecondHandProductService`, `ShProductImageDTO`, `ShSaleController.java`, `CuponController`, `SaleController.java`, `DashboardController.java`, `RankingsController.java`, `CuponResponseDTO`, `SecondHandCuponController`, `ServiceCuponService`, `UserResponseDTO`, `ServiceCuponController`, `ServiceCupon`, `.update`, `UserController.java`?**
  _High betweenness centrality (0.170) - this node is a cross-community bridge._
- **Why does `UserClient` connect `UserClient` to `Product`, `ResponseStatusException`, `ClientControllers.java`, `SaleService.java`, `ColorTypes`, `PaymentCard`, `ResourceNotFoundException`, `ShSaleService.java`, `ShCuponUsedByClients`, `SaleController.java`, `ClientSummaryProjection`, `.purchase`, `ProductCuponAssignmentProjection`, `Sale`, `ShSale`, `CuponUsedByClients`, `ServiceCuponToAClient`, `ServiceCuponService`, `.onAuthenticationSuccess`, `CuponService`, `LoginClientResponseDTO`, `ShProductCuponToAClient`, `SalesItem`?**
  _High betweenness centrality (0.097) - this node is a cross-community bridge._
- **Why does `ResourceNotFoundException` connect `ResourceNotFoundException` to `Product`, `UserServiceTest`, `ProductImage`, `ResponseStatusException`, `GlobalExceptionHandler`, `SaleService.java`, `ServiceOffered`, `SecondHandProductService`, `SecondHandProductImage`, `ShSaleService.java`, `ReportDashboardProjection`, `.purchase`, `CuponResponseDTO`, `ServiceCuponService`, `UserResponseDTO`, `DashboardService.java`, `ServiceCupon`, `CuponService`, `.update`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **What connects `$schema`, `*.env`, `*.env.example` to the rest of the system?**
  _144 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Product` be split into smaller, more focused modules?**
  _Cohesion score 0.05380333951762523 - nodes in this community are weakly interconnected._
- **Should `ProductImage` be split into smaller, more focused modules?**
  _Cohesion score 0.08788159111933395 - nodes in this community are weakly interconnected._
- **Should `SaleItemViewProjection` be split into smaller, more focused modules?**
  _Cohesion score 0.10570824524312897 - nodes in this community are weakly interconnected._