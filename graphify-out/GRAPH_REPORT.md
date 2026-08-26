# Graph Report - ApiProject  (2026-08-22)

## Corpus Check
- 172 files · ~33,031 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1480 nodes · 3460 edges · 86 communities (78 shown, 8 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 252 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `bfca20f8`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Product
- UserAdmin
- JwtService
- ProductImage
- AuthenticatedUser
- SaleItemViewProjection
- CuponAdminProjection
- ClientService
- PaymentCardRepository
- ClientControllers.java
- GlobalExceptionHandler
- SaleService.java
- UserClient
- ClientHistoryProjection
- NotificationService
- ReportDashboardProjection
- read
- DashboardProjection
- Protected Endpoints
- .register
- PaymentCard
- mvnw
- ServiceCuponService
- ClientLoginProjection
- CloudinaryConfig.java
- SaleItemService.java
- SaleResponseDato.java
- com.apiproject:ApiProject
- ResourceNotFoundException
- ResponseStatusException
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
- ShSalesItem
- ShSaleController.java
- ApiJuegoInventario
- SaleController.java
- ShProductCardProjection
- ShCuponUsedByClients
- ClientsSummaryViewService
- ClientSummaryProjection
- ShSaleService.java
- DashboardService.java
- ClientsSummaryViewController
- CuponResponseDTO
- Sale
- SecondHandProductRepository
- Arquitectura del Código
- ShSale
- Estructura del Proyecto — ApiProject
- JpaRepository
- CuponRepository
- ReportDashboard
- CuponUsedByClients
- SalesItem
- SecondHandProduct
- Configuración
- ClientsSummaryViewRepository
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

## God Nodes (most connected - your core abstractions)
1. `AuthenticatedUser` - 77 edges
2. `UserAdmin` - 54 edges
3. `UserClient` - 51 edges
4. `Product` - 49 edges
5. `ResourceNotFoundException` - 46 edges
6. `SecondHandProduct` - 35 edges
7. `ClientService` - 33 edges
8. `CuponService` - 29 edges
9. `SaleService` - 28 edges
10. `UserRepository` - 27 edges

## Surprising Connections (you probably didn't know these)
- `ProductResponseDTO` --references--> `ProductImageDTO`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/General/ProductResponseDTO.java → src/main/java/com/apiproject/DTOs/Admin/ProductImageDTO.java
- `ShProductResponseDTO` --references--> `ShProductImageDTO`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/General/ShProductResponseDTO.java → src/main/java/com/apiproject/DTOs/Admin/ShProductImageDTO.java
- `ClientResponseDTO` --references--> `PaymentCard`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/Client/ClientResponseDTO.java → src/main/java/com/apiproject/entities/client/PaymentCard.java
- `SecurityConfig` --references--> `OAuth2SuccessHandler`  [EXTRACTED]
  src/main/java/com/apiproject/config/SecurityConfig.java → src/main/java/com/apiproject/security/OAuth2SuccessHandler.java
- `ClientsSummaryViewController` --references--> `ClientsSummaryViewService`  [EXTRACTED]
  src/main/java/com/apiproject/controllers/admin/ClientsSummaryViewController.java → src/main/java/com/apiproject/services/admin/ClientsSummaryViewService.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Bearer Token Authentication Flow** — api_frontend_consumption_login, api_frontend_consumption_register, api_frontend_consumption_bearer_token, api_frontend_consumption_protected_endpoints, api_frontend_consumption_client_token, api_frontend_consumption_admin_token [INFERRED 0.85]
- **Payment Card Feature** — api_frontend_consumption_payment_cards, api_frontend_consumption_purchase, api_frontend_consumption_add_payment_cards_sql, api_frontend_consumption_payment_cards_table [INFERRED 0.85]
- **Product Catalog Feature** — api_frontend_consumption_product_catalog, api_frontend_consumption_product_images, api_frontend_consumption_purchase, api_frontend_consumption_client_token, api_frontend_consumption_admin_token [INFERRED 0.75]

## Communities (86 total, 8 thin omitted)

### Community 0 - "Product"
Cohesion: 0.06
Nodes (36): Lock, PreAuthorize, GetMapping, Operation, Page, PostMapping, PutMapping, RequestMapping (+28 more)

### Community 1 - "UserAdmin"
Cohesion: 0.05
Nodes (46): Authentication, AuthenticationSuccessHandler, DynamicUpdate, GetMapping, MultipartFile, Operation, PatchMapping, PostMapping (+38 more)

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
Cohesion: 0.13
Nodes (13): MultipartFile, ClientResponseDTO, ClientService, Cacheable, CachePut, Caching, Cloudinary, MultipartFile (+5 more)

### Community 8 - "PaymentCardRepository"
Cohesion: 0.19
Nodes (5): Modifying, Query, Repository, PaymentCardRepository, PaymentCardDetailsProjection

### Community 9 - "ClientControllers.java"
Cohesion: 0.22
Nodes (9): ClientControllers, GetMapping, Operation, PatchMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+1 more)

### Community 10 - "GlobalExceptionHandler"
Cohesion: 0.28
Nodes (11): DataIntegrityViolationException, ExceptionHandler, HttpRequestMethodNotSupportedException, HttpStatus, MaxUploadSizeExceededException, RestControllerAdvice, ApiError, GlobalExceptionHandler (+3 more)

### Community 11 - "SaleService.java"
Cohesion: 0.13
Nodes (12): Scheduled, PurchaseItemRequestDTO, PurchaseRequestDTO, CacheManager, CachePut, Caching, CouponResolution, RequiredArgsConstructor (+4 more)

### Community 12 - "UserClient"
Cohesion: 0.22
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

### Community 19 - ".register"
Cohesion: 0.20
Nodes (4): PostMapping, LoginClientRequestDTO, LoginClientResponseDTO, RegisterClientRequestDTO

### Community 20 - "PaymentCard"
Cohesion: 0.22
Nodes (9): PaymentCardRequestDTO, PaymentCardResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table (+1 more)

### Community 21 - "mvnw"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 22 - "ServiceCuponService"
Cohesion: 0.07
Nodes (31): DeleteMapping, GetMapping, Operation, PostMapping, PutMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+23 more)

### Community 24 - "CloudinaryConfig.java"
Cohesion: 0.53
Nodes (4): CloudinaryConfig, Bean, Cloudinary, Configuration

### Community 25 - "SaleItemService.java"
Cohesion: 0.83
Nodes (3): RequiredArgsConstructor, Service, SaleItemService

### Community 28 - "ResourceNotFoundException"
Cohesion: 0.16
Nodes (7): ResourceNotFoundException, CuponUsageProjection, CouponResolution, CuponService, RequiredArgsConstructor, Service, Transactional

### Community 29 - "ResponseStatusException"
Cohesion: 0.06
Nodes (44): ResponseStatusException, DeleteMapping, GetMapping, Operation, PostMapping, PutMapping, RequestMapping, RequiredArgsConstructor (+36 more)

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
Cohesion: 0.14
Nodes (18): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandProductImage, Modifying (+10 more)

### Community 42 - "ShProductImageDTO"
Cohesion: 0.15
Nodes (12): DeleteMapping, GetMapping, MultipartFile, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+4 more)

### Community 43 - "ProductCuponApplied"
Cohesion: 0.21
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ProductCuponApplied, Modifying (+4 more)

### Community 44 - "ShSaleHistoryProjection"
Cohesion: 0.14
Nodes (5): Query, Repository, ShSaleItemRepository, ShSaleHistoryProjection, Transactional

### Community 45 - "CuponController"
Cohesion: 0.22
Nodes (9): CuponController, DeleteMapping, GetMapping, Operation, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+1 more)

### Community 46 - "ShSalesItem"
Cohesion: 0.33
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShSalesItem

### Community 47 - "ShSaleController.java"
Cohesion: 0.31
Nodes (8): GetMapping, Operation, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Tag, ShSaleController

### Community 48 - "ApiJuegoInventario"
Cohesion: 0.12
Nodes (16): ApiJuegoInventario, Base de datos, Configuración, Documentación adicional, Ejecución con Docker, Ejecución local, Endpoints principales, Estado del proyecto (+8 more)

### Community 49 - "SaleController.java"
Cohesion: 0.23
Nodes (9): Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, Tag, SaleController, PurchaseItemResponseDTO (+1 more)

### Community 51 - "ShCuponUsedByClients"
Cohesion: 0.21
Nodes (10): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShCuponUsedByClients, Query (+2 more)

### Community 52 - "ClientsSummaryViewService"
Cohesion: 0.37
Nodes (7): ClientsSummaryViewService, Cacheable, Page, RequiredArgsConstructor, Service, Sort, Transactional

### Community 53 - "ClientSummaryProjection"
Cohesion: 0.18
Nodes (4): DashboardDTO, Page, MonthlyDataDTO, ClientSummaryProjection

### Community 54 - "ShSaleService.java"
Cohesion: 0.15
Nodes (10): PostMapping, ShPurchaseItemRequestDTO, ShPurchaseItemResponseDTO, ShPurchaseRequestDTO, ShPurchaseResponseDTO, CouponResolution, RequiredArgsConstructor, Service (+2 more)

### Community 55 - "DashboardService.java"
Cohesion: 0.26
Nodes (6): CacheConstants, DashboardService, Cacheable, RequiredArgsConstructor, Service, Transactional

### Community 56 - "ClientsSummaryViewController"
Cohesion: 0.32
Nodes (8): ClientsSummaryViewController, GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag

### Community 57 - "CuponResponseDTO"
Cohesion: 0.33
Nodes (4): PostMapping, PutMapping, CuponRequestDTO, CuponResponseDTO

### Community 58 - "Sale"
Cohesion: 0.24
Nodes (9): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, Sale, Repository (+1 more)

### Community 59 - "SecondHandProductRepository"
Cohesion: 0.30
Nodes (5): Modifying, Query, Repository, Transactional, SecondHandProductRepository

### Community 60 - "Arquitectura del Código"
Cohesion: 0.18
Nodes (11): Arquitectura del Código, config/, controllers/, DTOs/, entities/, enums/, exceptions/, Raíz (+3 more)

### Community 61 - "ShSale"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShSale

### Community 62 - "Estructura del Proyecto — ApiProject"
Cohesion: 0.20
Nodes (10): Arquitectura general, Dependencias principales (pom.xml), Directorios de datos y herramientas, Docker, docker-compose.yml, Dockerfile, Estructura del Proyecto — ApiProject, Mapa de notas (+2 more)

### Community 63 - "JpaRepository"
Cohesion: 0.48
Nodes (5): JpaRepository, Repository, SaleItemRepository, Repository, ShSaleRepository

### Community 64 - "CuponRepository"
Cohesion: 0.33
Nodes (5): CuponRepository, Modifying, Query, Repository, Transactional

### Community 65 - "ReportDashboard"
Cohesion: 0.36
Nodes (6): Immutable, Entity, Table, ReportDashboard, Query, ReportDashboardRepository

### Community 66 - "CuponUsedByClients"
Cohesion: 0.21
Nodes (10): CuponUsedByClients, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, CuponUsedByClientsRepository (+2 more)

### Community 67 - "SalesItem"
Cohesion: 0.21
Nodes (12): SaleItemResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SalesItem (+4 more)

### Community 68 - "SecondHandProduct"
Cohesion: 0.33
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandProduct

### Community 70 - "Configuración"
Cohesion: 0.33
Nodes (6): Archivos (src/main/resources), Configuración, Perfil dev — puntos clave, Reportes, Seguridad (SecurityConfig), Variables de entorno (.env)

### Community 71 - "ClientsSummaryViewRepository"
Cohesion: 0.57
Nodes (4): ClientsSummaryViewRepository, Page, Pageable, Query

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

## Knowledge Gaps
- **134 isolated node(s):** `$schema`, `*.env`, `*.env.example`, `*.key`, `*.pem` (+129 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **8 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AuthenticatedUser` connect `AuthenticatedUser` to `Product`, `UserAdmin`, `JwtService`, `ProductImage`, `ClientService`, `ClientControllers.java`, `NotificationService`, `ReportDashboardProjection`, `PaymentCard`, `ServiceCuponService`, `ResponseStatusException`, `ServiceOffered`, `SecondHandProductService`, `ShProductImageDTO`, `CuponController`, `ShSaleController.java`, `SaleController.java`, `ShSaleService.java`, `CuponResponseDTO`?**
  _High betweenness centrality (0.161) - this node is a cross-community bridge._
- **Why does `ResourceNotFoundException` connect `ResourceNotFoundException` to `Product`, `UserAdmin`, `ProductImage`, `SecondHandProductService`, `ClientService`, `SecondHandProductImage`, `GlobalExceptionHandler`, `SaleService.java`, `ServiceCuponService`, `DashboardService.java`, `ShSaleService.java`, `CuponResponseDTO`, `ResponseStatusException`, `ServiceOffered`?**
  _High betweenness centrality (0.106) - this node is a cross-community bridge._
- **Why does `UserAdmin` connect `UserAdmin` to `Product`, `ProductImage`, `SecondHandProduct`, `SecondHandProductService`, `SecondHandProductImage`, `SaleService.java`, `Cupon`, `ShSale`, `ServiceCuponService`, `ShSaleService.java`, `Sale`, `ResponseStatusException`, `ServiceOffered`?**
  _High betweenness centrality (0.075) - this node is a cross-community bridge._
- **What connects `$schema`, `*.env`, `*.env.example` to the rest of the system?**
  _134 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Product` be split into smaller, more focused modules?**
  _Cohesion score 0.06498599439775911 - nodes in this community are weakly interconnected._
- **Should `UserAdmin` be split into smaller, more focused modules?**
  _Cohesion score 0.05107252298263534 - nodes in this community are weakly interconnected._
- **Should `JwtService` be split into smaller, more focused modules?**
  _Cohesion score 0.08484848484848485 - nodes in this community are weakly interconnected._