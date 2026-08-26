# Graph Report - ApiProject  (2026-08-22)

## Corpus Check
- 172 files · ~31,983 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1465 nodes · 3445 edges · 77 communities (68 shown, 9 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 252 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `bfca20f8`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Product
- UserAdmin
- SecurityConfig.java
- ProductImage
- AuthenticatedUser
- SaleItemViewProjection
- JwtService
- ResponseStatusException
- PaymentCardDetailsProjection
- ClientControllers.java
- GlobalExceptionHandler
- SaleService.java
- UserClient
- ClientHistoryProjection
- NotificationController.java
- ExcelService
- read
- DashboardProjection
- Protected Endpoints
- .register
- PaymentCard
- mvnw
- CuponAdminProjection
- ClientLoginProjection
- CloudinaryConfig.java
- SaleItemService.java
- SaleResponseDato.java
- com.apiproject:ApiProject
- CuponService
- SecondHandCuponService
- SwaggerConfig.java
- ServiceOffered
- graphify-out Knowledge Graph
- ApiProjectApplication
- ApiProjectApplicationTests.java
- Endpoints API
- opencode.json
- SecondHandProductService
- graphify.js
- BRAIN — Cerebro del Proyecto ApiProject
- SecondHandProductImage
- Graphify
- ShProductImageDTO
- PdfService
- ShSaleHistoryProjection
- ReportService
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
- DashboardController.java
- Sale
- SecondHandProductRepository
- Arquitectura del Código
- ShSale
- Estructura del Proyecto — ApiProject
- JpaRepository
- ReportDashboardProjection
- ReportDashboard
- CuponUsedByClients
- SalesItem
- SecondHandProduct
- Configuración
- ClientsSummaryViewRepository.java
- Esquema de Base de Datos
- Arquitectura

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

## Communities (77 total, 9 thin omitted)

### Community 0 - "Product"
Cohesion: 0.06
Nodes (37): Lock, PreAuthorize, GetMapping, Operation, Page, PostMapping, PutMapping, RequestMapping (+29 more)

### Community 1 - "UserAdmin"
Cohesion: 0.05
Nodes (46): Authentication, AuthenticationSuccessHandler, DynamicUpdate, GetMapping, MultipartFile, Operation, PatchMapping, PostMapping (+38 more)

### Community 2 - "SecurityConfig.java"
Cohesion: 0.10
Nodes (22): BeforeEach, CorsConfigurationSource, EnableMethodSecurity, EnableWebSecurity, ExtendWith, FilterChain, HttpSecurity, OncePerRequestFilter (+14 more)

### Community 3 - "ProductImage"
Cohesion: 0.09
Nodes (29): DeleteMapping, GetMapping, MultipartFile, Operation, PostMapping, RequestMapping, ResponseEntity, RestController (+21 more)

### Community 4 - "AuthenticatedUser"
Cohesion: 0.24
Nodes (4): GrantedAuthority, AuthenticatedUser, Override, UserDetails

### Community 5 - "SaleItemViewProjection"
Cohesion: 0.11
Nodes (20): GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag, SalesItemViewController (+12 more)

### Community 6 - "JwtService"
Cohesion: 0.43
Nodes (4): Claims, SecretKey, Service, JwtService

### Community 7 - "ResponseStatusException"
Cohesion: 0.15
Nodes (12): ResponseStatusException, ClientService, Cacheable, CachePut, Caching, Cloudinary, MultipartFile, PasswordEncoder (+4 more)

### Community 9 - "ClientControllers.java"
Cohesion: 0.15
Nodes (12): ClientControllers, GetMapping, MultipartFile, Operation, PatchMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+4 more)

### Community 10 - "GlobalExceptionHandler"
Cohesion: 0.28
Nodes (11): DataIntegrityViolationException, ExceptionHandler, HttpRequestMethodNotSupportedException, HttpStatus, MaxUploadSizeExceededException, RestControllerAdvice, ApiError, GlobalExceptionHandler (+3 more)

### Community 11 - "SaleService.java"
Cohesion: 0.12
Nodes (13): Scheduled, NotificationEventDTO, PurchaseItemRequestDTO, PurchaseRequestDTO, CacheManager, CachePut, Caching, CouponResolution (+5 more)

### Community 12 - "UserClient"
Cohesion: 0.16
Nodes (11): ClientResponseDTO, AllArgsConstructor, Data, Entity, NoArgsConstructor, Table, UserClient, ClientRepository (+3 more)

### Community 14 - "NotificationController.java"
Cohesion: 0.26
Nodes (10): GetMapping, Operation, RequestMapping, RequiredArgsConstructor, RestController, SseEmitter, NotificationController, Service (+2 more)

### Community 15 - "ExcelService"
Cohesion: 0.25
Nodes (6): CellStyle, IndexedColors, Sheet, ExcelService, Override, Workbook

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
Cohesion: 0.24
Nodes (4): PostMapping, LoginClientRequestDTO, LoginClientResponseDTO, RegisterClientRequestDTO

### Community 20 - "PaymentCard"
Cohesion: 0.21
Nodes (11): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, PaymentCard, Modifying (+3 more)

### Community 21 - "mvnw"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 22 - "CuponAdminProjection"
Cohesion: 0.05
Nodes (35): GetMapping, GetMapping, DeleteMapping, GetMapping, Operation, PostMapping, PutMapping, RequestMapping (+27 more)

### Community 24 - "CloudinaryConfig.java"
Cohesion: 0.53
Nodes (4): CloudinaryConfig, Bean, Cloudinary, Configuration

### Community 25 - "SaleItemService.java"
Cohesion: 0.83
Nodes (3): RequiredArgsConstructor, Service, SaleItemService

### Community 28 - "CuponService"
Cohesion: 0.06
Nodes (42): CuponController, DeleteMapping, Operation, PostMapping, PutMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+34 more)

### Community 29 - "SecondHandCuponService"
Cohesion: 0.06
Nodes (41): DeleteMapping, Operation, PostMapping, PutMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+33 more)

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

### Community 35 - "Endpoints API"
Cohesion: 0.05
Nodes (39): Autenticación y usuarios, Clientes y tarjetas, DELETE `/product-images/{imageId}` — Eliminar imagen (ADMIN), Endpoints API, GET `/client/{adminId}/admin` — Datos de tienda del admin (público GET), GET `/client/payment-cards` — Listar tarjetas (CLIENT), GET `/client/profile-photo` — Obtener foto de perfil (CLIENT), GET `/client/user-data` — Datos del cliente (CLIENT) (+31 more)

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

### Community 43 - "PdfService"
Cohesion: 0.22
Nodes (7): Color, Document, Font, PdfPTable, Override, Service, PdfService

### Community 44 - "ShSaleHistoryProjection"
Cohesion: 0.14
Nodes (5): Query, Repository, ShSaleItemRepository, ShSaleHistoryProjection, Transactional

### Community 45 - "ReportService"
Cohesion: 0.18
Nodes (7): FileTypes, EXCEL, PDF, ReportService, Component, ReportServiceFactory, Component

### Community 46 - "ShSaleService.java"
Cohesion: 0.15
Nodes (14): SaleItemResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShSalesItem (+6 more)

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
Cohesion: 0.21
Nodes (10): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShCuponUsedByClients, Query (+2 more)

### Community 52 - "ClientsSummaryViewService"
Cohesion: 0.32
Nodes (8): ClientsSummaryViewRepository, ClientsSummaryViewService, Cacheable, Page, RequiredArgsConstructor, Service, Sort, Transactional

### Community 53 - "ClientSummaryProjection"
Cohesion: 0.18
Nodes (4): DashboardDTO, Page, MonthlyDataDTO, ClientSummaryProjection

### Community 54 - ".purchase"
Cohesion: 0.24
Nodes (5): ShPurchaseItemRequestDTO, ShPurchaseRequestDTO, CouponResolution, ShSaleDraft, ShSaleService

### Community 55 - "DashboardService.java"
Cohesion: 0.26
Nodes (6): CacheConstants, DashboardService, Cacheable, RequiredArgsConstructor, Service, Transactional

### Community 56 - "ClientsSummaryViewController"
Cohesion: 0.32
Nodes (8): ClientsSummaryViewController, GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag

### Community 57 - "DashboardController.java"
Cohesion: 0.35
Nodes (8): DashboardController, GetMapping, Operation, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Tag

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
Nodes (6): JpaRepository, CuponUsedByClientsRepository, Query, Repository, Repository, SaleItemRepository

### Community 65 - "ReportDashboard"
Cohesion: 0.36
Nodes (6): Immutable, Entity, Table, ReportDashboard, Query, ReportDashboardRepository

### Community 66 - "CuponUsedByClients"
Cohesion: 0.33
Nodes (7): CuponUsedByClients, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table

### Community 67 - "SalesItem"
Cohesion: 0.33
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SalesItem

### Community 68 - "SecondHandProduct"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandProduct

### Community 70 - "Configuración"
Cohesion: 0.33
Nodes (6): Archivos (src/main/resources), Configuración, Perfil dev — puntos clave, Reportes, Seguridad (SecurityConfig), Variables de entorno (.env)

### Community 71 - "ClientsSummaryViewRepository.java"
Cohesion: 0.60
Nodes (3): Page, Pageable, Query

### Community 72 - "Esquema de Base de Datos"
Cohesion: 0.40
Nodes (5): Esquema de Base de Datos, Nota, Tablas, Vistas, Índices

### Community 73 - "Arquitectura"
Cohesion: 0.67
Nodes (3): Arquitectura, Flujo de generación de reportes, Patrón Factory para reportes

## Knowledge Gaps
- **123 isolated node(s):** `$schema`, `*.env`, `*.env.example`, `*.key`, `*.pem` (+118 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **9 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AuthenticatedUser` connect `AuthenticatedUser` to `Product`, `UserAdmin`, `SecurityConfig.java`, `ProductImage`, `SecondHandProductService`, `ClientControllers.java`, `ShProductImageDTO`, `NotificationController.java`, `ShSaleController.java`, `SaleController.java`, `CuponAdminProjection`, `DashboardController.java`, `CuponService`, `SecondHandCuponService`, `ServiceOffered`?**
  _High betweenness centrality (0.163) - this node is a cross-community bridge._
- **Why does `ResourceNotFoundException` connect `Product` to `UserAdmin`, `ProductImage`, `SecondHandProductService`, `ResponseStatusException`, `SecondHandProductImage`, `GlobalExceptionHandler`, `SaleService.java`, `ShSaleService.java`, `CuponAdminProjection`, `DashboardService.java`, `.purchase`, `CuponService`, `SecondHandCuponService`, `ServiceOffered`?**
  _High betweenness centrality (0.108) - this node is a cross-community bridge._
- **Why does `UserAdmin` connect `UserAdmin` to `Product`, `ProductImage`, `SecondHandProduct`, `SecondHandProductService`, `SecondHandProductImage`, `SaleService.java`, `ShSale`, `CuponAdminProjection`, `.purchase`, `Sale`, `CuponService`, `SecondHandCuponService`, `ServiceOffered`?**
  _High betweenness centrality (0.076) - this node is a cross-community bridge._
- **What connects `$schema`, `*.env`, `*.env.example` to the rest of the system?**
  _123 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Product` be split into smaller, more focused modules?**
  _Cohesion score 0.0628174284950548 - nodes in this community are weakly interconnected._
- **Should `UserAdmin` be split into smaller, more focused modules?**
  _Cohesion score 0.05198537095088819 - nodes in this community are weakly interconnected._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.09759759759759759 - nodes in this community are weakly interconnected._