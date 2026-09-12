# Graph Report - ApiProject  (2026-08-29)

## Corpus Check
- 189 files · ~36,438 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1633 nodes · 3959 edges · 108 communities (99 shown, 9 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 296 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e1fd8851`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Product
- UserService
- SecurityConfig.java
- ProductImage
- AuthenticatedUser
- SaleItemViewProjection
- CuponAdminProjection
- ResponseStatusException
- PaymentCardRepository
- ClientControllers.java
- GlobalExceptionHandler
- SaleService
- UserClient
- ClientHistoryProjection
- NotificationService
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
- ResourceNotFoundException
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
- ShSaleHistoryProjection
- CuponController
- ShSaleService.java
- ShSaleController.java
- ApiJuegoInventario
- SaleController.java
- ShProductCardProjection
- ShCuponUsedByClients
- ClientsSummaryViewService
- ClientSummaryProjection
- .purchase
- DashboardService.java
- ClientsSummaryViewController
- CuponResponseDTO
- Sale
- SecondHandProductRepository
- Arquitectura del Código
- ShSale
- Estructura del Proyecto — ApiProject
- JpaRepository
- CuponService.java
- ReportDashboard
- CuponUsedByClients
- SalesItem
- SecondHandProduct
- Configuración
- RankingsController.java
- Esquema de Base de Datos
- Arquitectura
- Endpoints API
- Clientes y tarjetas
- Cupon
- Imágenes de producto
- Panel de administración (ADMIN)
- Productos de segunda mano
- Productos
- Servicios ofrecidos
- Ventas
- SecondHandCuponController
- JwtService
- ServiceCuponToClientResponseDTO
- SecondHandCupon
- UserController.java
- ServiceCuponToAClient
- ServiceCuponController
- .onAuthenticationSuccess
- SecondHandProductCuponsApplied
- ServiceCuponService
- UserAdmin
- ProductCuponToAClientRepository
- ShProductCuponToAClientRepository
- UserRepository
- .register
- ColorTypes
- CuponAssignmentRequestDTO
- SaleService.java
- MaterializedViewRefreshService
- ProductCuponToAClient
- ShProductCuponToAClient
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

## Communities (108 total, 9 thin omitted)

### Community 0 - "Product"
Cohesion: 0.07
Nodes (36): Lock, PreAuthorize, GetMapping, Operation, Page, PostMapping, PutMapping, RequestMapping (+28 more)

### Community 1 - "UserService"
Cohesion: 0.18
Nodes (11): UserResponseDTO, AllArgsConstructor, Cacheable, CacheManager, CachePut, Cloudinary, MultipartFile, PasswordEncoder (+3 more)

### Community 2 - "SecurityConfig.java"
Cohesion: 0.15
Nodes (18): CorsConfigurationSource, EnableMethodSecurity, EnableWebSecurity, FilterChain, HttpSecurity, OncePerRequestFilter, SecurityFilterChain, Bean (+10 more)

### Community 3 - "ProductImage"
Cohesion: 0.09
Nodes (29): DeleteMapping, GetMapping, MultipartFile, Operation, PostMapping, RequestMapping, ResponseEntity, RestController (+21 more)

### Community 4 - "AuthenticatedUser"
Cohesion: 0.24
Nodes (4): GrantedAuthority, AuthenticatedUser, Override, UserDetails

### Community 5 - "SaleItemViewProjection"
Cohesion: 0.11
Nodes (20): GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag, SalesItemViewController (+12 more)

### Community 6 - "CuponAdminProjection"
Cohesion: 0.15
Nodes (3): CuponAdminProjection, CuponAdminProjectionAdapter, Override

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

### Community 11 - "SaleService"
Cohesion: 0.18
Nodes (5): PurchaseItemRequestDTO, PurchaseRequestDTO, CouponResolution, SaleDraft, SaleService

### Community 12 - "UserClient"
Cohesion: 0.19
Nodes (10): AllArgsConstructor, Data, Entity, NoArgsConstructor, Table, UserClient, ClientRepository, Modifying (+2 more)

### Community 14 - "NotificationService"
Cohesion: 0.21
Nodes (11): GetMapping, Operation, RequestMapping, RequiredArgsConstructor, RestController, SseEmitter, NotificationController, NotificationEventDTO (+3 more)

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

### Community 19 - "LoginClientResponseDTO"
Cohesion: 0.24
Nodes (4): PostMapping, LoginClientRequestDTO, LoginClientResponseDTO, RegisterClientRequestDTO

### Community 20 - "PaymentCard"
Cohesion: 0.24
Nodes (9): PaymentCardRequestDTO, PaymentCardResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table (+1 more)

### Community 21 - "mvnw"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 22 - "ServiceCupon"
Cohesion: 0.17
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ServiceCupon, Modifying (+4 more)

### Community 24 - "CloudinaryConfig.java"
Cohesion: 0.53
Nodes (4): CloudinaryConfig, Bean, Cloudinary, Configuration

### Community 25 - "SaleItemService.java"
Cohesion: 0.83
Nodes (3): RequiredArgsConstructor, Service, SaleItemService

### Community 28 - "ResourceNotFoundException"
Cohesion: 0.22
Nodes (4): ResourceNotFoundException, CouponResolution, CuponService, Transactional

### Community 29 - "SecondHandCuponService"
Cohesion: 0.20
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
Cohesion: 0.19
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ProductCuponApplied, Modifying (+4 more)

### Community 44 - "ShSaleHistoryProjection"
Cohesion: 0.15
Nodes (3): Query, ShSaleHistoryProjection, Transactional

### Community 45 - "CuponController"
Cohesion: 0.17
Nodes (11): CuponController, DeleteMapping, GetMapping, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+3 more)

### Community 46 - "ShSaleService.java"
Cohesion: 0.14
Nodes (16): SaleItemResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShSalesItem (+8 more)

### Community 47 - "ShSaleController.java"
Cohesion: 0.18
Nodes (11): GetMapping, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Tag (+3 more)

### Community 48 - "ApiJuegoInventario"
Cohesion: 0.12
Nodes (16): ApiJuegoInventario, Base de datos, Configuración, Documentación adicional, Ejecución con Docker, Ejecución local, Endpoints principales, Estado del proyecto (+8 more)

### Community 49 - "SaleController.java"
Cohesion: 0.23
Nodes (9): Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, Tag, SaleController, PurchaseItemResponseDTO (+1 more)

### Community 51 - "ShCuponUsedByClients"
Cohesion: 0.33
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShCuponUsedByClients

### Community 52 - "ClientsSummaryViewService"
Cohesion: 0.28
Nodes (8): CacheConstants, ClientsSummaryViewService, Cacheable, Page, RequiredArgsConstructor, Service, Sort, Transactional

### Community 53 - "ClientSummaryProjection"
Cohesion: 0.23
Nodes (5): ClientsSummaryViewRepository, Page, Pageable, Query, ClientSummaryProjection

### Community 54 - ".purchase"
Cohesion: 0.24
Nodes (5): ShPurchaseItemRequestDTO, ShPurchaseRequestDTO, CouponResolution, ShSaleDraft, ShSaleService

### Community 55 - "DashboardService.java"
Cohesion: 0.31
Nodes (5): DashboardService, Cacheable, RequiredArgsConstructor, Service, Transactional

### Community 56 - "ClientsSummaryViewController"
Cohesion: 0.32
Nodes (8): ClientsSummaryViewController, GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag

### Community 57 - "CuponResponseDTO"
Cohesion: 0.44
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
Cohesion: 0.33
Nodes (6): JpaRepository, Query, Repository, ShCuponUsedByClientsRepository, Repository, SaleItemRepository

### Community 64 - "CuponService.java"
Cohesion: 0.19
Nodes (8): CuponRepository, Modifying, Query, Repository, Transactional, CuponUsageProjection, RequiredArgsConstructor, Service

### Community 65 - "ReportDashboard"
Cohesion: 0.36
Nodes (6): Immutable, Entity, Table, ReportDashboard, Query, ReportDashboardRepository

### Community 66 - "CuponUsedByClients"
Cohesion: 0.23
Nodes (10): CuponUsedByClients, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, CuponUsedByClientsRepository (+2 more)

### Community 67 - "SalesItem"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SalesItem

### Community 68 - "SecondHandProduct"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandProduct

### Community 70 - "Configuración"
Cohesion: 0.33
Nodes (6): Archivos (src/main/resources), Configuración, Perfil dev — puntos clave, Reportes, Seguridad (SecurityConfig), Variables de entorno (.env)

### Community 71 - "RankingsController.java"
Cohesion: 0.17
Nodes (16): GetMapping, Operation, RequestMapping, RequiredArgsConstructor, RestController, Tag, RankingsController, TheThreeBestClients (+8 more)

### Community 72 - "Esquema de Base de Datos"
Cohesion: 0.22
Nodes (9): Cupones para productos normales, Esquema de Base de Datos, Nota, Segunda mano, Servicios ofrecidos, Tablas, Tablas nuevas — cupones, segunda mano y servicios, Vistas (+1 more)

### Community 73 - "Arquitectura"
Cohesion: 0.67
Nodes (3): Arquitectura, Flujo de generación de reportes, Patrón Factory para reportes

### Community 77 - "Endpoints API"
Cohesion: 0.20
Nodes (10): Cupones de productos, Cupones de segunda mano, Endpoints API, GET `/notification/stream` — Stream de eventos (ADMIN), Historial de ventas (ADMIN), Notificaciones (SSE), POST `/cupons` — Crear cupón (ADMIN), Regla de cupones (+2 more)

### Community 78 - "Clientes y tarjetas"
Cohesion: 0.22
Nodes (9): Clientes y tarjetas, GET `/client/payment-cards` — Listar tarjetas (CLIENT), GET `/client/profile-photo` — Obtener foto de perfil (CLIENT), GET `/client/user-data` — Datos del cliente (CLIENT), GET /client/user-payments — Historial de compras del cliente (CLIENT), PATCH `/client/modify` — Modificar datos del cliente (CLIENT), PATCH `/client/payment-cards/{cardId}/status?active=true` — Activar/desactivar tarjeta (CLIENT), PATCH `/client/upload-profile` — Foto de perfil del cliente (CLIENT) (+1 more)

### Community 79 - "Cupon"
Cohesion: 0.33
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

### Community 83 - "Productos"
Cohesion: 0.50
Nodes (4): POST `/product/deleteSafe` — Borrado suave (ADMIN), POST `/product/saveProduct` — Crear producto (ADMIN), Productos, PUT `/product/update/{id}` — Actualizar producto (ADMIN)

### Community 84 - "Servicios ofrecidos"
Cohesion: 0.67
Nodes (3): Cupones de servicio, POST `/services` — Crear servicio (ADMIN), Servicios ofrecidos

### Community 85 - "Ventas"
Cohesion: 0.67
Nodes (3): POST `/sale/purchase` — Compra de cliente (CLIENT), POST `/sale/sale/refresh` — Recalcular producto por venta (ADMIN), Ventas

### Community 86 - "SecondHandCuponController"
Cohesion: 0.16
Nodes (11): DeleteMapping, GetMapping, Operation, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Tag (+3 more)

### Community 87 - "JwtService"
Cohesion: 0.19
Nodes (8): BeforeEach, Claims, ExtendWith, SecretKey, Service, JwtService, Test, JwtServiceTest

### Community 88 - "ServiceCuponToClientResponseDTO"
Cohesion: 0.17
Nodes (4): GetMapping, PostMapping, ServiceCuponToClientResponseDTO, Transactional

### Community 89 - "SecondHandCupon"
Cohesion: 0.18
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandCupon, Modifying (+4 more)

### Community 90 - "UserController.java"
Cohesion: 0.18
Nodes (12): DynamicUpdate, GetMapping, MultipartFile, Operation, PatchMapping, PostMapping, RequestMapping, RequiredArgsConstructor (+4 more)

### Community 91 - "ServiceCuponToAClient"
Cohesion: 0.20
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ServiceCuponToAClient, Modifying (+4 more)

### Community 92 - "ServiceCuponController"
Cohesion: 0.22
Nodes (9): DeleteMapping, Operation, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Tag, ServiceCuponController (+1 more)

### Community 93 - ".onAuthenticationSuccess"
Cohesion: 0.22
Nodes (9): Authentication, AuthenticationSuccessHandler, Component, HttpServletRequest, HttpServletResponse, Override, RequiredArgsConstructor, Transactional (+1 more)

### Community 94 - "SecondHandProductCuponsApplied"
Cohesion: 0.21
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandProductCuponsApplied, Modifying (+4 more)

### Community 95 - "ServiceCuponService"
Cohesion: 0.30
Nodes (6): PutMapping, ServiceCuponRequestDTO, ServiceCuponResponseDTO, RequiredArgsConstructor, Service, ServiceCuponService

### Community 96 - "UserAdmin"
Cohesion: 0.23
Nodes (8): ClientDescriptionAboutUsersDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, UserAdmin

### Community 97 - "ProductCuponToAClientRepository"
Cohesion: 0.31
Nodes (5): Modifying, Query, Repository, Transactional, ProductCuponToAClientRepository

### Community 98 - "ShProductCuponToAClientRepository"
Cohesion: 0.33
Nodes (5): Modifying, Query, Repository, Transactional, ShProductCuponToAClientRepository

### Community 99 - "UserRepository"
Cohesion: 0.27
Nodes (5): Modifying, Query, Repository, Transactional, UserRepository

### Community 101 - "ColorTypes"
Cohesion: 0.22
Nodes (7): ClientResponseDTO, ColorTypes, AMBAR, AZUL, ROSA, VERDE, VIOLETA

### Community 103 - "SaleService.java"
Cohesion: 0.28
Nodes (7): CacheManager, CachePut, Caching, RequiredArgsConstructor, Scheduled, Service, Transactional

### Community 104 - "MaterializedViewRefreshService"
Cohesion: 0.43
Nodes (5): JdbcTemplate, RequiredArgsConstructor, Scheduled, Service, MaterializedViewRefreshService

### Community 105 - "ProductCuponToAClient"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ProductCuponToAClient

### Community 106 - "ShProductCuponToAClient"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShProductCuponToAClient

### Community 107 - "DashboardDTO"
Cohesion: 0.47
Nodes (3): DashboardDTO, Page, MonthlyDataDTO

## Knowledge Gaps
- **139 isolated node(s):** `$schema`, `*.env`, `*.env.example`, `*.key`, `*.pem` (+134 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **9 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AuthenticatedUser` connect `AuthenticatedUser` to `Product`, `ProductImage`, `ClientControllers.java`, `NotificationService`, `ReportDashboardProjection`, `PaymentCard`, `SecondHandCuponService`, `ServiceOffered`, `SecondHandProductService`, `ShProductImageDTO`, `CuponController`, `ShSaleController.java`, `SaleController.java`, `CuponResponseDTO`, `RankingsController.java`, `SecondHandCuponController`, `JwtService`, `ServiceCuponToClientResponseDTO`, `UserController.java`, `ServiceCuponController`, `ServiceCuponService`, `CuponAssignmentRequestDTO`?**
  _High betweenness centrality (0.177) - this node is a cross-community bridge._
- **Why does `UserClient` connect `UserClient` to `Product`, `ResponseStatusException`, `ClientControllers.java`, `SaleService`, `LoginClientResponseDTO`, `PaymentCard`, `SecondHandCuponService`, `ShSaleService.java`, `SaleController.java`, `ShCuponUsedByClients`, `ClientSummaryProjection`, `.purchase`, `Sale`, `ShSale`, `CuponService.java`, `CuponUsedByClients`, `SalesItem`, `ServiceCuponToAClient`, `.onAuthenticationSuccess`, `ServiceCuponService`, `ColorTypes`, `SaleService.java`, `ProductCuponToAClient`, `ShProductCuponToAClient`?**
  _High betweenness centrality (0.088) - this node is a cross-community bridge._
- **Why does `ResourceNotFoundException` connect `ResourceNotFoundException` to `Product`, `UserServiceTest`, `ProductImage`, `ResponseStatusException`, `GlobalExceptionHandler`, `SaleService`, `ServiceCupon`, `SecondHandCuponService`, `ServiceOffered`, `SecondHandProductService`, `SecondHandProductImage`, `ShSaleService.java`, `.purchase`, `DashboardService.java`, `CuponResponseDTO`, `CuponService.java`, `SecondHandCuponController`, `ServiceCuponToClientResponseDTO`, `ServiceCuponService`, `CuponAssignmentRequestDTO`, `SaleService.java`?**
  _High betweenness centrality (0.087) - this node is a cross-community bridge._
- **What connects `$schema`, `*.env`, `*.env.example` to the rest of the system?**
  _139 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Product` be split into smaller, more focused modules?**
  _Cohesion score 0.06670584778136938 - nodes in this community are weakly interconnected._
- **Should `ProductImage` be split into smaller, more focused modules?**
  _Cohesion score 0.0851063829787234 - nodes in this community are weakly interconnected._
- **Should `SaleItemViewProjection` be split into smaller, more focused modules?**
  _Cohesion score 0.10570824524312897 - nodes in this community are weakly interconnected._