# Graph Report - ApiProject  (2026-09-05)

## Corpus Check
- 193 files · ~41,024 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1670 nodes · 4046 edges · 106 communities (97 shown, 9 thin omitted)
- Extraction: 92% EXTRACTED · 8% INFERRED · 0% AMBIGUOUS · INFERRED: 328 edges (avg confidence: 0.8)
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
- NotificationController.java
- AuthenticatedUser
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
- .update
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
- SalesItem
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
- CuponResponseDTO
- Sale
- SecondHandProductRepository
- Arquitectura del Código
- ShSale
- Estructura del Proyecto — ApiProject
- JpaRepository
- DashboardProjection
- ClientsSummaryViewService
- CuponUsedByClients
- ShProductCuponToAClient
- SecondHandProduct
- Configuración
- RankingsController.java
- Esquema de Base de Datos
- ClientsSummaryViewController
- Endpoints API
- Clientes y tarjetas
- Cupon
- Imágenes de producto
- Panel de administración (ADMIN)
- Productos de segunda mano
- LoginClientResponseDTO
- Productos
- Servicios ofrecidos
- SecondHandCuponController
- Ventas
- ResourceNotFoundException
- SecondHandCupon
- UserResponseDTO
- ServiceCuponToAClient
- ServiceCuponController
- .onAuthenticationSuccess
- SecondHandProductCuponsApplied
- ServiceCupon
- UserAdmin
- CuponService
- ShSaleService.java
- UserRepository
- UserController.java
- DashboardService.java
- SecondHandCuponService
- ShSalesItem
- DashboardDTO
- ProductCuponToAClient

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

## Communities (106 total, 9 thin omitted)

### Community 0 - "Product"
Cohesion: 0.06
Nodes (43): JdbcTemplate, Lock, PreAuthorize, Scheduled, GetMapping, Operation, Page, PostMapping (+35 more)

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

### Community 14 - "NotificationController.java"
Cohesion: 0.39
Nodes (7): GetMapping, Operation, RequestMapping, RequiredArgsConstructor, RestController, SseEmitter, NotificationController

### Community 15 - "AuthenticatedUser"
Cohesion: 0.06
Nodes (26): Color, Document, Font, GrantedAuthority, PdfPTable, DashboardController, GetMapping, Operation (+18 more)

### Community 16 - "read"
Cohesion: 0.17
Nodes (11): cat *.env*, rm -rf *, type *.env*, $schema, permission, bash, read, *.env (+3 more)

### Community 17 - "ExcelService"
Cohesion: 0.25
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
Cohesion: 0.22
Nodes (6): Modifying, Query, Repository, Transactional, ServiceCuponRepository, CuponUsageProjection

### Community 24 - "CloudinaryConfig.java"
Cohesion: 0.53
Nodes (4): CloudinaryConfig, Bean, Cloudinary, Configuration

### Community 25 - "SaleItemService.java"
Cohesion: 0.83
Nodes (3): RequiredArgsConstructor, Service, SaleItemService

### Community 28 - "ShProductCuponAssignmentProjection"
Cohesion: 0.16
Nodes (6): Modifying, Query, Repository, Transactional, ShProductCuponToAClientRepository, ShProductCuponAssignmentProjection

### Community 29 - ".update"
Cohesion: 0.29
Nodes (3): PutMapping, SecondHandCuponRequestDTO, SecondHandCuponResponseDTO

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

### Community 46 - "SalesItem"
Cohesion: 0.19
Nodes (12): SaleItemResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SalesItem (+4 more)

### Community 47 - "ShSaleHistoryProjection"
Cohesion: 0.14
Nodes (5): Query, Repository, ShSaleItemRepository, ShSaleHistoryProjection, Transactional

### Community 48 - "ApiJuegoInventario"
Cohesion: 0.10
Nodes (20): ApiJuegoInventario, Arquitectura, Base de datos, Catálogo y búsqueda de productos, Configuración, Documentación adicional, Ejecución con Docker, Ejecución local (+12 more)

### Community 49 - "ReportDashboardProjection"
Cohesion: 0.14
Nodes (7): Immutable, Entity, Table, ReportDashboard, Query, ReportDashboardRepository, ReportDashboardProjection

### Community 51 - "ShCuponUsedByClients"
Cohesion: 0.21
Nodes (10): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShCuponUsedByClients, Query (+2 more)

### Community 52 - "SaleController.java"
Cohesion: 0.23
Nodes (9): Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, Tag, SaleController, PurchaseItemResponseDTO (+1 more)

### Community 53 - "ClientSummaryProjection"
Cohesion: 0.23
Nodes (5): ClientsSummaryViewRepository, Page, Pageable, Query, ClientSummaryProjection

### Community 54 - ".purchase"
Cohesion: 0.24
Nodes (5): ShPurchaseItemRequestDTO, ShPurchaseRequestDTO, CouponResolution, ShSaleDraft, ShSaleService

### Community 55 - "ProductCuponAssignmentProjection"
Cohesion: 0.15
Nodes (6): Modifying, Query, Repository, Transactional, ProductCuponToAClientRepository, ProductCuponAssignmentProjection

### Community 57 - "CuponResponseDTO"
Cohesion: 0.32
Nodes (3): PutMapping, CuponRequestDTO, CuponResponseDTO

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
Cohesion: 0.83
Nodes (3): JpaRepository, Repository, SaleItemRepository

### Community 64 - "DashboardProjection"
Cohesion: 0.14
Nodes (4): Repository, DashboardRepository, Query, DashboardProjection

### Community 65 - "ClientsSummaryViewService"
Cohesion: 0.28
Nodes (8): CacheConstants, ClientsSummaryViewService, Cacheable, Page, RequiredArgsConstructor, Service, Sort, Transactional

### Community 66 - "CuponUsedByClients"
Cohesion: 0.23
Nodes (10): CuponUsedByClients, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, CuponUsedByClientsRepository (+2 more)

### Community 67 - "ShProductCuponToAClient"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShProductCuponToAClient

### Community 68 - "SecondHandProduct"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandProduct

### Community 70 - "Configuración"
Cohesion: 0.29
Nodes (7): Archivos (src/main/resources), Configuración, Perfil dev — puntos clave, Reportes, Seguridad (SecurityConfig), Tareas programadas, Variables de entorno (.env)

### Community 71 - "RankingsController.java"
Cohesion: 0.17
Nodes (16): GetMapping, Operation, RequestMapping, RequiredArgsConstructor, RestController, Tag, RankingsController, TheThreeBestClients (+8 more)

### Community 72 - "Esquema de Base de Datos"
Cohesion: 0.18
Nodes (11): Colores de interfaz (`color_config`), Cupones para productos normales, Esquema de Base de Datos, Nota, Segunda mano, Servicios ofrecidos, Tablas, Tablas nuevas — cupones, segunda mano y servicios (+3 more)

### Community 73 - "ClientsSummaryViewController"
Cohesion: 0.32
Nodes (8): ClientsSummaryViewController, GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag

### Community 77 - "Endpoints API"
Cohesion: 0.18
Nodes (11): Cupones de productos, Cupones de segunda mano, Endpoints API, GET `/notification/stream` — Stream de eventos (ADMIN), Historial de ventas (ADMIN), Notificaciones (SSE), POST `/cupons` — Crear cupón (ADMIN), Rankings (ADMIN) (+3 more)

### Community 78 - "Clientes y tarjetas"
Cohesion: 0.22
Nodes (9): Clientes y tarjetas, GET `/client/payment-cards` — Listar tarjetas (CLIENT), GET `/client/profile-photo` — Obtener foto de perfil (CLIENT), GET `/client/user-data` — Datos del cliente (CLIENT), GET /client/user-payments — Historial de compras del cliente (CLIENT), PATCH `/client/modify` — Modificar datos del cliente (CLIENT), PATCH `/client/payment-cards/{cardId}/status?active=true` — Activar/desactivar tarjeta (CLIENT), PATCH `/client/upload-profile` — Foto de perfil del cliente (CLIENT) (+1 more)

### Community 79 - "Cupon"
Cohesion: 0.15
Nodes (13): CuponAppliedDTO, Cupon, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table (+5 more)

### Community 80 - "Imágenes de producto"
Cohesion: 0.50
Nodes (4): DELETE `/product-images/{imageId}` — Eliminar imagen (ADMIN), GET `/product-images/{productId}` — Imágenes de un producto (público), Imágenes de producto, POST `/product-images/upload/{productId}` — Subir imagen (ADMIN)

### Community 81 - "Panel de administración (ADMIN)"
Cohesion: 0.50
Nodes (4): GET /dashboard-controller/excel — Reporte en Excel (adjunto .xlsx), GET `/dashboard-controller/get-data-dashboard` — Métricas del dashboard, GET /dashboard-controller/pdf — Reporte en PDF (adjunto .pdf), Panel de administración (ADMIN)

### Community 82 - "Productos de segunda mano"
Cohesion: 0.50
Nodes (4): Imágenes SH — `/api/sh-product-images`, POST `/sh-product/save` — Crear producto SH (ADMIN), POST `/sh-sale/purchase` — Compra SH (CLIENT), Productos de segunda mano

### Community 83 - "LoginClientResponseDTO"
Cohesion: 0.24
Nodes (4): PostMapping, LoginClientRequestDTO, LoginClientResponseDTO, RegisterClientRequestDTO

### Community 84 - "Productos"
Cohesion: 0.50
Nodes (4): POST `/product/deleteSafe` — Borrado suave (ADMIN), POST `/product/saveProduct` — Crear producto (ADMIN), Productos, PUT `/product/update/{id}` — Actualizar producto (ADMIN)

### Community 85 - "Servicios ofrecidos"
Cohesion: 0.67
Nodes (3): Cupones de servicio, POST `/services` — Crear servicio (ADMIN), Servicios ofrecidos

### Community 86 - "SecondHandCuponController"
Cohesion: 0.19
Nodes (11): DeleteMapping, GetMapping, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+3 more)

### Community 87 - "Ventas"
Cohesion: 0.67
Nodes (3): POST `/sale/purchase` — Compra de cliente (CLIENT), POST `/sale/sale/refresh` — Recalcular producto por venta (ADMIN), Ventas

### Community 88 - "ResourceNotFoundException"
Cohesion: 0.29
Nodes (5): ResourceNotFoundException, RequiredArgsConstructor, Service, Transactional, ServiceCuponService

### Community 89 - "SecondHandCupon"
Cohesion: 0.20
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandCupon, Modifying (+4 more)

### Community 90 - "UserResponseDTO"
Cohesion: 0.23
Nodes (6): MultipartFile, PatchMapping, UserResponseDTO, CachePut, MultipartFile, Transactional

### Community 91 - "ServiceCuponToAClient"
Cohesion: 0.20
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ServiceCuponToAClient, Modifying (+4 more)

### Community 92 - "ServiceCuponController"
Cohesion: 0.13
Nodes (13): DeleteMapping, GetMapping, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+5 more)

### Community 93 - ".onAuthenticationSuccess"
Cohesion: 0.22
Nodes (9): Authentication, AuthenticationSuccessHandler, Component, HttpServletRequest, HttpServletResponse, Override, RequiredArgsConstructor, Transactional (+1 more)

### Community 94 - "SecondHandProductCuponsApplied"
Cohesion: 0.21
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandProductCuponsApplied, Modifying (+4 more)

### Community 95 - "ServiceCupon"
Cohesion: 0.18
Nodes (10): PutMapping, ServiceCuponRequestDTO, ServiceCuponResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor (+2 more)

### Community 96 - "UserAdmin"
Cohesion: 0.23
Nodes (8): ClientDescriptionAboutUsersDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, UserAdmin

### Community 97 - "CuponService"
Cohesion: 0.23
Nodes (5): CouponResolution, CuponService, RequiredArgsConstructor, Service, Transactional

### Community 98 - "ShSaleService.java"
Cohesion: 0.27
Nodes (6): NotificationEventDTO, Service, SseEmitter, NotificationService, RequiredArgsConstructor, Service

### Community 99 - "UserRepository"
Cohesion: 0.33
Nodes (5): Modifying, Query, Repository, Transactional, UserRepository

### Community 100 - "UserController.java"
Cohesion: 0.16
Nodes (12): DynamicUpdate, GetMapping, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, Tag (+4 more)

### Community 101 - "DashboardService.java"
Cohesion: 0.31
Nodes (5): DashboardService, Cacheable, RequiredArgsConstructor, Service, Transactional

### Community 102 - "SecondHandCuponService"
Cohesion: 0.22
Nodes (5): CouponResolution, RequiredArgsConstructor, Service, Transactional, SecondHandCuponService

### Community 103 - "ShSalesItem"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShSalesItem

### Community 104 - "DashboardDTO"
Cohesion: 0.47
Nodes (3): DashboardDTO, Page, MonthlyDataDTO

### Community 105 - "ProductCuponToAClient"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ProductCuponToAClient

## Knowledge Gaps
- **144 isolated node(s):** `$schema`, `*.env`, `*.env.example`, `*.key`, `*.pem` (+139 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **9 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AuthenticatedUser` connect `AuthenticatedUser` to `Product`, `ProductImage`, `JwtService`, `ClientControllers.java`, `NotificationController.java`, `PaymentCard`, `.update`, `ServiceOffered`, `SecondHandProductService`, `ShProductImageDTO`, `ShSaleController.java`, `CuponController`, `SaleController.java`, `CuponResponseDTO`, `RankingsController.java`, `SecondHandCuponController`, `UserResponseDTO`, `ServiceCuponController`, `ServiceCupon`, `UserController.java`?**
  _High betweenness centrality (0.172) - this node is a cross-community bridge._
- **Why does `ResourceNotFoundException` connect `ResourceNotFoundException` to `Product`, `CuponService`, `UserServiceTest`, `ProductImage`, `ShSaleService.java`, `DashboardService.java`, `SecondHandCuponService`, `ResponseStatusException`, `SecondHandProductImage`, `SecondHandProductService`, `GlobalExceptionHandler`, `SaleService.java`, `.purchase`, `CuponResponseDTO`, `UserResponseDTO`, `.update`, `ServiceOffered`?**
  _High betweenness centrality (0.095) - this node is a cross-community bridge._
- **Why does `UserClient` connect `UserClient` to `Product`, `ResponseStatusException`, `ClientControllers.java`, `SaleService.java`, `ColorTypes`, `PaymentCard`, `SalesItem`, `ShCuponUsedByClients`, `SaleController.java`, `ClientSummaryProjection`, `.purchase`, `Sale`, `ShSale`, `CuponUsedByClients`, `ShProductCuponToAClient`, `LoginClientResponseDTO`, `ResourceNotFoundException`, `ServiceCuponToAClient`, `.onAuthenticationSuccess`, `CuponService`, `ShSaleService.java`, `SecondHandCuponService`, `ShSalesItem`, `ProductCuponToAClient`?**
  _High betweenness centrality (0.084) - this node is a cross-community bridge._
- **What connects `$schema`, `*.env`, `*.env.example` to the rest of the system?**
  _144 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Product` be split into smaller, more focused modules?**
  _Cohesion score 0.057195149851292613 - nodes in this community are weakly interconnected._
- **Should `ProductImage` be split into smaller, more focused modules?**
  _Cohesion score 0.0851063829787234 - nodes in this community are weakly interconnected._
- **Should `SaleItemViewProjection` be split into smaller, more focused modules?**
  _Cohesion score 0.10570824524312897 - nodes in this community are weakly interconnected._