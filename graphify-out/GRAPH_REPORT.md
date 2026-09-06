# Graph Report - ApiProject  (2026-08-29)

## Corpus Check
- 189 files · ~36,776 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1637 nodes · 3963 edges · 100 communities (86 shown, 14 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 296 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e1fd8851`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Product
- UserService
- JwtService
- ProductImage
- AuthenticatedUser
- SaleItemViewProjection
- CuponAdminProjection
- ClientService
- PaymentCardRepository
- ClientControllers.java
- GlobalExceptionHandler
- ResponseStatusException
- UserClient
- ClientHistoryProjection
- ShSaleService.java
- ReportDashboardProjection
- read
- DashboardProjection
- Protected Endpoints
- LoginClientResponseDTO
- PaymentCard
- mvnw
- ServiceCupon
- ClientLoginProjection
- CloudinaryConfig.java
- SaleItemService.java
- SaleResponseDato.java
- com.apiproject:ApiProject
- ShProductCuponToClientResponseDTO
- SecondHandCuponService
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
- CuponAdminProjectionAdapter
- CuponController
- SalesItem
- ClientDescriptionAboutUsersDTO
- ApiJuegoInventario
- ServiceCuponToAClient
- ShProductCardProjection
- ShSale
- ClientsSummaryViewService
- ClientSummaryProjection
- ShSalesItem
- DashboardService.java
- ClientsSummaryViewController
- CuponService
- Sale
- SecondHandProductRepository
- Arquitectura del Código
- JpaRepository
- Estructura del Proyecto — ApiProject
- ShCuponUsedByClientsRepository
- CuponService.java
- ReportDashboard
- CuponUsedByClients
- CuponUsedByClientsRepository
- SecondHandProduct
- Configuración
- RankingsController.java
- Esquema de Base de Datos
- Endpoints API
- Clientes y tarjetas
- Cupon
- Imágenes de producto
- Panel de administración (ADMIN)
- Productos de segunda mano
- SecondHandCuponController
- CuponAssignmentRequestDTO
- SecondHandCupon
- UserController.java
- ServiceCuponToAClientRepository
- ServiceCuponController
- .onAuthenticationSuccess
- SecondHandProductCuponsApplied
- ServiceCuponService
- UserAdmin
- ProductCuponToAClientRepository
- ShProductCuponToAClient
- UserRepository
- .register
- ResourceNotFoundException
- MaterializedViewRefreshService
- ProductCuponToAClient
- DashboardDTO

## God Nodes (most connected - your core abstractions)
1. `AuthenticatedUser` - 98 edges
2. `UserClient` - 61 edges
3. `UserAdmin` - 57 edges
4. `ResourceNotFoundException` - 55 edges
5. `Product` - 51 edges
6. `CuponService` - 38 edges
7. `SecondHandProduct` - 37 edges
8. `SecondHandCuponService` - 36 edges
9. `ClientService` - 33 edges
10. `UserRepository` - 28 edges

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

## Communities (100 total, 14 thin omitted)

### Community 0 - "Product"
Cohesion: 0.05
Nodes (45): Lock, PreAuthorize, GetMapping, Operation, Page, PostMapping, PutMapping, RequestMapping (+37 more)

### Community 1 - "UserService"
Cohesion: 0.21
Nodes (8): AllArgsConstructor, CacheManager, CachePut, Cloudinary, MultipartFile, PasswordEncoder, Service, UserService

### Community 2 - "JwtService"
Cohesion: 0.08
Nodes (26): BeforeEach, Claims, CorsConfigurationSource, EnableMethodSecurity, EnableWebSecurity, ExtendWith, FilterChain, HttpSecurity (+18 more)

### Community 3 - "ProductImage"
Cohesion: 0.09
Nodes (29): DeleteMapping, GetMapping, MultipartFile, Operation, PostMapping, RequestMapping, ResponseEntity, RestController (+21 more)

### Community 4 - "AuthenticatedUser"
Cohesion: 0.24
Nodes (4): GrantedAuthority, AuthenticatedUser, Override, UserDetails

### Community 5 - "SaleItemViewProjection"
Cohesion: 0.11
Nodes (20): GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag, SalesItemViewController (+12 more)

### Community 7 - "ClientService"
Cohesion: 0.14
Nodes (12): ClientResponseDTO, ClientService, Cacheable, CachePut, Caching, Cloudinary, MultipartFile, PasswordEncoder (+4 more)

### Community 8 - "PaymentCardRepository"
Cohesion: 0.19
Nodes (5): Modifying, Query, Repository, PaymentCardRepository, PaymentCardDetailsProjection

### Community 9 - "ClientControllers.java"
Cohesion: 0.13
Nodes (14): ClientControllers, GetMapping, MultipartFile, Operation, PatchMapping, PostMapping, RequestMapping, RequiredArgsConstructor (+6 more)

### Community 10 - "GlobalExceptionHandler"
Cohesion: 0.28
Nodes (11): DataIntegrityViolationException, ExceptionHandler, HttpRequestMethodNotSupportedException, HttpStatus, MaxUploadSizeExceededException, RestControllerAdvice, ApiError, GlobalExceptionHandler (+3 more)

### Community 11 - "ResponseStatusException"
Cohesion: 0.13
Nodes (13): ResponseStatusException, PurchaseItemRequestDTO, PurchaseRequestDTO, CacheManager, CachePut, Caching, CouponResolution, RequiredArgsConstructor (+5 more)

### Community 12 - "UserClient"
Cohesion: 0.20
Nodes (10): AllArgsConstructor, Data, Entity, NoArgsConstructor, Table, UserClient, ClientRepository, Modifying (+2 more)

### Community 14 - "ShSaleService.java"
Cohesion: 0.05
Nodes (34): GetMapping, Operation, RequestMapping, RequiredArgsConstructor, RestController, SseEmitter, NotificationController, GetMapping (+26 more)

### Community 15 - "ReportDashboardProjection"
Cohesion: 0.05
Nodes (29): CellStyle, Color, Document, Font, IndexedColors, PdfPTable, Sheet, DashboardController (+21 more)

### Community 16 - "read"
Cohesion: 0.17
Nodes (11): cat *.env*, rm -rf *, type *.env*, $schema, permission, bash, read, *.env (+3 more)

### Community 17 - "DashboardProjection"
Cohesion: 0.14
Nodes (4): Repository, DashboardRepository, Query, DashboardProjection

### Community 18 - "Protected Endpoints"
Cohesion: 0.22
Nodes (14): add-payment-cards.sql Script, Admin Token, /api Base Path, Bearer Token Authentication, Client Token, Login Endpoint, LoginResponseDTO, Payment Cards API (+6 more)

### Community 20 - "PaymentCard"
Cohesion: 0.46
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, PaymentCard

### Community 21 - "mvnw"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 22 - "ServiceCupon"
Cohesion: 0.20
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ServiceCupon, Modifying (+4 more)

### Community 24 - "CloudinaryConfig.java"
Cohesion: 0.53
Nodes (4): CloudinaryConfig, Bean, Cloudinary, Configuration

### Community 25 - "SaleItemService.java"
Cohesion: 0.83
Nodes (3): RequiredArgsConstructor, Service, SaleItemService

### Community 29 - "SecondHandCuponService"
Cohesion: 0.19
Nodes (7): PutMapping, SecondHandCuponRequestDTO, SecondHandCuponResponseDTO, CouponResolution, RequiredArgsConstructor, Service, SecondHandCuponService

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

### Community 45 - "CuponController"
Cohesion: 0.16
Nodes (12): CuponController, DeleteMapping, GetMapping, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+4 more)

### Community 46 - "SalesItem"
Cohesion: 0.19
Nodes (12): SaleItemResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SalesItem (+4 more)

### Community 48 - "ApiJuegoInventario"
Cohesion: 0.11
Nodes (19): ApiJuegoInventario, Arquitectura, Base de datos, Configuración, Documentación adicional, Ejecución con Docker, Ejecución local, Endpoints principales (+11 more)

### Community 49 - "ServiceCuponToAClient"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ServiceCuponToAClient

### Community 51 - "ShSale"
Cohesion: 0.18
Nodes (14): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShCuponUsedByClients, AllArgsConstructor (+6 more)

### Community 52 - "ClientsSummaryViewService"
Cohesion: 0.28
Nodes (8): CacheConstants, ClientsSummaryViewService, Cacheable, Page, RequiredArgsConstructor, Service, Sort, Transactional

### Community 53 - "ClientSummaryProjection"
Cohesion: 0.23
Nodes (5): ClientsSummaryViewRepository, Page, Pageable, Query, ClientSummaryProjection

### Community 54 - "ShSalesItem"
Cohesion: 0.33
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShSalesItem

### Community 55 - "DashboardService.java"
Cohesion: 0.31
Nodes (5): DashboardService, Cacheable, RequiredArgsConstructor, Service, Transactional

### Community 56 - "ClientsSummaryViewController"
Cohesion: 0.32
Nodes (8): ClientsSummaryViewController, GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag

### Community 57 - "CuponService"
Cohesion: 0.28
Nodes (4): PutMapping, CuponRequestDTO, CuponResponseDTO, CuponService

### Community 58 - "Sale"
Cohesion: 0.27
Nodes (9): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, Sale, Repository (+1 more)

### Community 59 - "SecondHandProductRepository"
Cohesion: 0.30
Nodes (5): Modifying, Query, Repository, Transactional, SecondHandProductRepository

### Community 60 - "Arquitectura del Código"
Cohesion: 0.18
Nodes (11): Arquitectura del Código, config/, controllers/, DTOs/, entities/, enums/, exceptions/, Raíz (+3 more)

### Community 61 - "JpaRepository"
Cohesion: 0.48
Nodes (5): JpaRepository, Repository, SaleItemRepository, Repository, ShSaleRepository

### Community 62 - "Estructura del Proyecto — ApiProject"
Cohesion: 0.20
Nodes (10): Arquitectura general, Dependencias principales (pom.xml), Directorios de datos y herramientas, Docker, docker-compose.yml, Dockerfile, Estructura del Proyecto — ApiProject, Mapa de notas (+2 more)

### Community 63 - "ShCuponUsedByClientsRepository"
Cohesion: 0.53
Nodes (3): Query, Repository, ShCuponUsedByClientsRepository

### Community 64 - "CuponService.java"
Cohesion: 0.14
Nodes (9): CuponRepository, Modifying, Query, Repository, Transactional, CuponUsageProjection, CouponResolution, RequiredArgsConstructor (+1 more)

### Community 65 - "ReportDashboard"
Cohesion: 0.36
Nodes (6): Immutable, Entity, Table, ReportDashboard, Query, ReportDashboardRepository

### Community 66 - "CuponUsedByClients"
Cohesion: 0.39
Nodes (7): CuponUsedByClients, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table

### Community 67 - "CuponUsedByClientsRepository"
Cohesion: 0.53
Nodes (3): CuponUsedByClientsRepository, Query, Repository

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

### Community 77 - "Endpoints API"
Cohesion: 0.10
Nodes (21): Cupones de productos, Cupones de segunda mano, Cupones de servicio, Endpoints API, GET `/notification/stream` — Stream de eventos (ADMIN), Historial de ventas (ADMIN), Notificaciones (SSE), POST `/cupons` — Crear cupón (ADMIN) (+13 more)

### Community 78 - "Clientes y tarjetas"
Cohesion: 0.22
Nodes (9): Clientes y tarjetas, GET `/client/payment-cards` — Listar tarjetas (CLIENT), GET `/client/profile-photo` — Obtener foto de perfil (CLIENT), GET `/client/user-data` — Datos del cliente (CLIENT), GET /client/user-payments — Historial de compras del cliente (CLIENT), PATCH `/client/modify` — Modificar datos del cliente (CLIENT), PATCH `/client/payment-cards/{cardId}/status?active=true` — Activar/desactivar tarjeta (CLIENT), PATCH `/client/upload-profile` — Foto de perfil del cliente (CLIENT) (+1 more)

### Community 79 - "Cupon"
Cohesion: 0.26
Nodes (7): Cupon, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table

### Community 80 - "Imágenes de producto"
Cohesion: 0.50
Nodes (4): DELETE `/product-images/{imageId}` — Eliminar imagen (ADMIN), GET `/product-images/{productId}` — Imágenes de un producto (público), Imágenes de producto, POST `/product-images/upload/{productId}` — Subir imagen (ADMIN)

### Community 81 - "Panel de administración (ADMIN)"
Cohesion: 0.50
Nodes (4): GET /dashboard-controller/excel — Reporte en Excel (adjunto .xlsx), GET `/dashboard-controller/get-data-dashboard` — Métricas del dashboard, GET /dashboard-controller/pdf — Reporte en PDF (adjunto .pdf), Panel de administración (ADMIN)

### Community 82 - "Productos de segunda mano"
Cohesion: 0.50
Nodes (4): Imágenes SH — `/api/sh-product-images`, POST `/sh-product/save` — Crear producto SH (ADMIN), POST `/sh-sale/purchase` — Compra SH (CLIENT), Productos de segunda mano

### Community 86 - "SecondHandCuponController"
Cohesion: 0.19
Nodes (10): DeleteMapping, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Tag (+2 more)

### Community 89 - "SecondHandCupon"
Cohesion: 0.18
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandCupon, Modifying (+4 more)

### Community 90 - "UserController.java"
Cohesion: 0.20
Nodes (12): DynamicUpdate, GetMapping, MultipartFile, Operation, PatchMapping, RequestMapping, RequiredArgsConstructor, RestController (+4 more)

### Community 91 - "ServiceCuponToAClientRepository"
Cohesion: 0.24
Nodes (5): Modifying, Query, Repository, Transactional, ServiceCuponToAClientRepository

### Community 92 - "ServiceCuponController"
Cohesion: 0.19
Nodes (10): DeleteMapping, GetMapping, Operation, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Tag (+2 more)

### Community 93 - ".onAuthenticationSuccess"
Cohesion: 0.22
Nodes (9): Authentication, AuthenticationSuccessHandler, Component, HttpServletRequest, HttpServletResponse, Override, RequiredArgsConstructor, Transactional (+1 more)

### Community 94 - "SecondHandProductCuponsApplied"
Cohesion: 0.21
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandProductCuponsApplied, Modifying (+4 more)

### Community 95 - "ServiceCuponService"
Cohesion: 0.23
Nodes (7): PutMapping, ServiceCuponRequestDTO, ServiceCuponResponseDTO, RequiredArgsConstructor, Service, Transactional, ServiceCuponService

### Community 96 - "UserAdmin"
Cohesion: 0.17
Nodes (13): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, UserAdmin, ColorTypes (+5 more)

### Community 97 - "ProductCuponToAClientRepository"
Cohesion: 0.31
Nodes (5): Modifying, Query, Repository, Transactional, ProductCuponToAClientRepository

### Community 98 - "ShProductCuponToAClient"
Cohesion: 0.20
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShProductCuponToAClient, Modifying (+4 more)

### Community 99 - "UserRepository"
Cohesion: 0.33
Nodes (5): Modifying, Query, Repository, Transactional, UserRepository

### Community 100 - ".register"
Cohesion: 0.27
Nodes (4): PostMapping, LoginAdminRequestDTO, LoginAdminResponseDTO, RegisterAdminRequestDTO

### Community 104 - "MaterializedViewRefreshService"
Cohesion: 0.43
Nodes (5): JdbcTemplate, RequiredArgsConstructor, Scheduled, Service, MaterializedViewRefreshService

### Community 105 - "ProductCuponToAClient"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ProductCuponToAClient

### Community 107 - "DashboardDTO"
Cohesion: 0.47
Nodes (3): DashboardDTO, Page, MonthlyDataDTO

## Knowledge Gaps
- **143 isolated node(s):** `$schema`, `*.env`, `*.env.example`, `*.key`, `*.pem` (+138 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **14 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AuthenticatedUser` connect `AuthenticatedUser` to `Product`, `JwtService`, `ProductImage`, `ClientControllers.java`, `ShSaleService.java`, `ReportDashboardProjection`, `ShProductCuponToClientResponseDTO`, `SecondHandCuponService`, `ServiceOffered`, `SecondHandProductService`, `ShProductImageDTO`, `CuponController`, `CuponService`, `RankingsController.java`, `SecondHandCuponController`, `CuponAssignmentRequestDTO`, `UserController.java`, `ServiceCuponController`, `ServiceCuponService`?**
  _High betweenness centrality (0.176) - this node is a cross-community bridge._
- **Why does `UserClient` connect `UserClient` to `Product`, `ClientService`, `ClientControllers.java`, `ResponseStatusException`, `ShSaleService.java`, `LoginClientResponseDTO`, `PaymentCard`, `SecondHandCuponService`, `SalesItem`, `ServiceCuponToAClient`, `ShSale`, `ClientSummaryProjection`, `ShSalesItem`, `Sale`, `CuponService.java`, `CuponUsedByClients`, `.onAuthenticationSuccess`, `ServiceCuponService`, `UserAdmin`, `ShProductCuponToAClient`, `ProductCuponToAClient`?**
  _High betweenness centrality (0.088) - this node is a cross-community bridge._
- **Why does `ResourceNotFoundException` connect `ResourceNotFoundException` to `Product`, `UserService`, `ProductImage`, `ClientService`, `GlobalExceptionHandler`, `ResponseStatusException`, `ShSaleService.java`, `SecondHandCuponService`, `ServiceOffered`, `SecondHandProductService`, `SecondHandProductImage`, `CuponController`, `ClientDescriptionAboutUsersDTO`, `DashboardService.java`, `CuponService`, `CuponService.java`, `Cupon`, `CuponAssignmentRequestDTO`, `UserController.java`, `ServiceCuponToAClientRepository`, `ServiceCuponService`?**
  _High betweenness centrality (0.087) - this node is a cross-community bridge._
- **What connects `$schema`, `*.env`, `*.env.example` to the rest of the system?**
  _143 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Product` be split into smaller, more focused modules?**
  _Cohesion score 0.05297876726448155 - nodes in this community are weakly interconnected._
- **Should `JwtService` be split into smaller, more focused modules?**
  _Cohesion score 0.08484848484848485 - nodes in this community are weakly interconnected._
- **Should `ProductImage` be split into smaller, more focused modules?**
  _Cohesion score 0.0851063829787234 - nodes in this community are weakly interconnected._