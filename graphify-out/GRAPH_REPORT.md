# Graph Report - ApiProject  (2026-09-16)

## Corpus Check
- 206 files · ~50,640 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1790 nodes · 4453 edges · 111 communities (96 shown, 15 thin omitted)
- Extraction: 91% EXTRACTED · 9% INFERRED · 0% AMBIGUOUS · INFERRED: 391 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `a1174550`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- CuponAdminProjection
- ServiceCuponController
- SecurityConfig.java
- ProductImage
- AuthenticatedUser
- SaleItemViewProjection
- ServiceCuponService
- ClientService
- PaymentCardRepository
- ClientControllers.java
- GlobalExceptionHandler
- SaleService.java
- UserClient
- ClientHistoryProjection
- DashboardProjection
- PdfService
- permission
- ExcelService
- Protected Endpoints
- DashboardService.java
- PaymentCardResponseDTO
- mvnw
- Sale
- ClientLoginProjection
- CloudinaryConfig.java
- SaleItemService.java
- SaleResponseDato.java
- com.apiproject:ApiProject
- CouponAssignmentProjection
- SecondHandCuponController
- SwaggerConfig.java
- ServiceOffered
- graphify-out Knowledge Graph
- ApiProjectApplication
- ApiProjectApplicationTests.java
- Autenticación y usuarios
- Product
- SecondHandProductService
- graphify.js
- BRAIN — Cerebro del Proyecto ApiProject
- SecondHandProductImage
- Graphify
- ShProductImageDTO
- ProductCuponApplied
- Cupon
- CuponController
- SalesItem
- ShSaleHistoryProjection
- ApiJuegoInventario
- ReportDashboardProjection
- ShProductCardProjection
- ShCuponUsedByClients
- SaleController.java
- ClientSummaryProjection
- .update
- ProductCuponAssignmentProjection
- ShProductCuponToAClientRepository
- FileTypes
- ServiceCuponAssignmentProjection
- SecondHandProductRepository
- Arquitectura del Código
- ShSaleController.java
- Estructura del Proyecto — ApiProject
- CuponUsedByClientsRepository
- ShSaleService.java
- ShProductCuponAssignmentProjection
- CuponUsedByClients
- DashboardController.java
- SecondHandProductCuponsApplied
- Configuración
- RankingsController.java
- Esquema de Base de Datos
- ShSale
- Endpoints API
- Clientes y tarjetas
- ServiceCuponToAClientRepository
- Imágenes de producto
- Panel de administración (ADMIN)
- Productos de segunda mano
- JwtService
- Productos
- Servicios ofrecidos
- SecondHandCuponService
- Ventas
- ServiceCuponUsedByClients
- SecondHandCupon
- JpaRepository
- ServiceCuponRepository
- ServiceCupon
- .onAuthenticationSuccess
- ServiceCuponToAClient
- ProductCuponToAClient
- UserService
- ResponseStatusException
- ColorTypes
- UserAdmin
- UserController.java
- LoginClientResponseDTO
- ShSalesItem
- entrypoint.sh
- UserRepository
- Por Implementar — ApiProject
- SecondHandProduct
- ReportDashboard
- .modifyData
- SecondHandCuponService.java
- PaymentCard

## God Nodes (most connected - your core abstractions)
1. `AuthenticatedUser` - 116 edges
2. `UserClient` - 63 edges
3. `ResourceNotFoundException` - 63 edges
4. `UserAdmin` - 59 edges
5. `Product` - 51 edges
6. `CuponService` - 45 edges
7. `SecondHandCuponService` - 41 edges
8. `SecondHandProduct` - 37 edges
9. `ClientService` - 34 edges
10. `ServiceCuponService` - 32 edges

## Surprising Connections (you probably didn't know these)
- `DashboardDTO` --references--> `ClientSummaryProjection`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/Admin/DashboardDTO.java → src/main/java/com/apiproject/repositories/projection/ClientSummaryProjection.java
- `ProductResponseDTO` --references--> `ProductImageDTO`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/General/ProductResponseDTO.java → src/main/java/com/apiproject/DTOs/Admin/ProductImageDTO.java
- `ShProductResponseDTO` --references--> `ShProductImageDTO`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/General/ShProductResponseDTO.java → src/main/java/com/apiproject/DTOs/Admin/ShProductImageDTO.java
- `LoginAdminResponseDTO` --references--> `ColorTypes`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/Auth/LoginAdminResponseDTO.java → src/main/java/com/apiproject/enums/ColorTypes.java
- `LoginClientResponseDTO` --references--> `ColorTypes`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/Auth/LoginClientResponseDTO.java → src/main/java/com/apiproject/enums/ColorTypes.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Bearer Token Authentication Flow** — api_frontend_consumption_login, api_frontend_consumption_register, api_frontend_consumption_bearer_token, api_frontend_consumption_protected_endpoints, api_frontend_consumption_client_token, api_frontend_consumption_admin_token [INFERRED 0.85]
- **Payment Card Feature** — api_frontend_consumption_payment_cards, api_frontend_consumption_purchase, api_frontend_consumption_add_payment_cards_sql, api_frontend_consumption_payment_cards_table [INFERRED 0.85]
- **Product Catalog Feature** — api_frontend_consumption_product_catalog, api_frontend_consumption_product_images, api_frontend_consumption_purchase, api_frontend_consumption_client_token, api_frontend_consumption_admin_token [INFERRED 0.75]

## Communities (111 total, 15 thin omitted)

### Community 1 - "ServiceCuponController"
Cohesion: 0.15
Nodes (12): DeleteMapping, GetMapping, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+4 more)

### Community 2 - "SecurityConfig.java"
Cohesion: 0.14
Nodes (20): CorsConfigurationSource, EnableMethodSecurity, EnableWebSecurity, FilterChain, HttpSecurity, OncePerRequestFilter, SecurityFilterChain, Bean (+12 more)

### Community 3 - "ProductImage"
Cohesion: 0.09
Nodes (29): DeleteMapping, GetMapping, MultipartFile, Operation, PostMapping, RequestMapping, ResponseEntity, RestController (+21 more)

### Community 4 - "AuthenticatedUser"
Cohesion: 0.24
Nodes (4): GrantedAuthority, AuthenticatedUser, Override, UserDetails

### Community 5 - "SaleItemViewProjection"
Cohesion: 0.11
Nodes (20): GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag, SalesItemViewController (+12 more)

### Community 6 - "ServiceCuponService"
Cohesion: 0.20
Nodes (7): PatchMapping, CuponAssignmentUpdateRequestDTO, ServiceCuponToClientResponseDTO, RequiredArgsConstructor, Service, Transactional, ServiceCuponService

### Community 7 - "ClientService"
Cohesion: 0.13
Nodes (13): ClientResponseDTO, ClientService, Cacheable, CachePut, Cloudinary, MultipartFile, Page, Pageable (+5 more)

### Community 8 - "PaymentCardRepository"
Cohesion: 0.19
Nodes (5): Modifying, Query, Repository, PaymentCardRepository, PaymentCardDetailsProjection

### Community 9 - "ClientControllers.java"
Cohesion: 0.15
Nodes (13): ClientControllers, GetMapping, MultipartFile, Operation, Page, PatchMapping, RequestMapping, RequiredArgsConstructor (+5 more)

### Community 10 - "GlobalExceptionHandler"
Cohesion: 0.28
Nodes (11): DataIntegrityViolationException, ExceptionHandler, HttpRequestMethodNotSupportedException, HttpStatus, MaxUploadSizeExceededException, RestControllerAdvice, ApiError, GlobalExceptionHandler (+3 more)

### Community 11 - "SaleService.java"
Cohesion: 0.15
Nodes (10): PurchaseItemRequestDTO, PurchaseRequestDTO, CacheManager, Caching, CouponResolution, RequiredArgsConstructor, Service, Transactional (+2 more)

### Community 12 - "UserClient"
Cohesion: 0.18
Nodes (12): AllArgsConstructor, Data, Entity, NoArgsConstructor, Table, UserClient, ClientRepository, Modifying (+4 more)

### Community 15 - "PdfService"
Cohesion: 0.22
Nodes (7): Color, Document, Font, PdfPTable, Override, Service, PdfService

### Community 16 - "permission"
Cohesion: 0.07
Nodes (26): cat .env, Get-Content .env*, rm -rf *, type .env*, **/.env, **/.env.example, **/.env, **/.env.example (+18 more)

### Community 17 - "ExcelService"
Cohesion: 0.23
Nodes (6): CellStyle, IndexedColors, Sheet, ExcelService, Override, Workbook

### Community 18 - "Protected Endpoints"
Cohesion: 0.22
Nodes (14): add-payment-cards.sql Script, Admin Token, /api Base Path, Bearer Token Authentication, Client Token, Login Endpoint, LoginResponseDTO, Payment Cards API (+6 more)

### Community 19 - "DashboardService.java"
Cohesion: 0.27
Nodes (8): DashboardRepository, Query, ReportDashboardRepository, DashboardService, Cacheable, RequiredArgsConstructor, Service, Transactional

### Community 20 - "PaymentCardResponseDTO"
Cohesion: 0.24
Nodes (4): PostMapping, PaymentCardRequestDTO, PaymentCardResponseDTO, Caching

### Community 21 - "mvnw"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 22 - "Sale"
Cohesion: 0.27
Nodes (9): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, Sale, Repository (+1 more)

### Community 24 - "CloudinaryConfig.java"
Cohesion: 0.53
Nodes (4): CloudinaryConfig, Bean, Cloudinary, Configuration

### Community 25 - "SaleItemService.java"
Cohesion: 0.83
Nodes (3): RequiredArgsConstructor, Service, SaleItemService

### Community 29 - "SecondHandCuponController"
Cohesion: 0.18
Nodes (11): DeleteMapping, GetMapping, Operation, PatchMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+3 more)

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

### Community 36 - "Product"
Cohesion: 0.05
Nodes (44): JdbcTemplate, Lock, PreAuthorize, Scheduled, CacheConstants, GetMapping, Operation, Page (+36 more)

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
Cohesion: 0.22
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ProductCuponApplied, Modifying (+4 more)

### Community 44 - "Cupon"
Cohesion: 0.15
Nodes (13): CuponAppliedDTO, Cupon, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table (+5 more)

### Community 45 - "CuponController"
Cohesion: 0.12
Nodes (14): CuponController, DeleteMapping, GetMapping, Operation, PatchMapping, PostMapping, RequestMapping, RequiredArgsConstructor (+6 more)

### Community 46 - "SalesItem"
Cohesion: 0.19
Nodes (12): SaleItemResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SalesItem (+4 more)

### Community 47 - "ShSaleHistoryProjection"
Cohesion: 0.13
Nodes (5): Query, Repository, ShSaleItemRepository, ShSaleHistoryProjection, Transactional

### Community 48 - "ApiJuegoInventario"
Cohesion: 0.10
Nodes (20): ApiJuegoInventario, Arquitectura, Base de datos, Catálogo y búsqueda de productos, Configuración, Documentación adicional, Ejecución con Docker, Ejecución local (+12 more)

### Community 51 - "ShCuponUsedByClients"
Cohesion: 0.21
Nodes (10): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShCuponUsedByClients, Query (+2 more)

### Community 52 - "SaleController.java"
Cohesion: 0.23
Nodes (9): Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, Tag, SaleController, PurchaseItemResponseDTO (+1 more)

### Community 53 - "ClientSummaryProjection"
Cohesion: 0.10
Nodes (21): Repository, ClientsSummaryViewController, GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController (+13 more)

### Community 54 - ".update"
Cohesion: 0.24
Nodes (4): PostMapping, PutMapping, SecondHandCuponRequestDTO, SecondHandCuponResponseDTO

### Community 55 - "ProductCuponAssignmentProjection"
Cohesion: 0.13
Nodes (6): Modifying, Query, Repository, Transactional, ProductCuponToAClientRepository, ProductCuponAssignmentProjection

### Community 56 - "ShProductCuponToAClientRepository"
Cohesion: 0.27
Nodes (5): Modifying, Query, Repository, Transactional, ShProductCuponToAClientRepository

### Community 57 - "FileTypes"
Cohesion: 0.17
Nodes (8): FileTypes, EXCEL, PDF, ReportService, ResponseEntity, Component, ReportServiceFactory, Component

### Community 59 - "SecondHandProductRepository"
Cohesion: 0.30
Nodes (5): Modifying, Query, Repository, Transactional, SecondHandProductRepository

### Community 60 - "Arquitectura del Código"
Cohesion: 0.18
Nodes (11): Arquitectura del Código, config/, controllers/, DTOs/, entities/, enums/, exceptions/, Raíz (+3 more)

### Community 61 - "ShSaleController.java"
Cohesion: 0.31
Nodes (8): GetMapping, Operation, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Tag, ShSaleController

### Community 62 - "Estructura del Proyecto — ApiProject"
Cohesion: 0.20
Nodes (10): Arquitectura general, Dependencias principales (pom.xml), Directorios de datos y herramientas, Docker, docker-compose.yml, Dockerfile, Estructura del Proyecto — ApiProject, Mapa de notas (+2 more)

### Community 63 - "CuponUsedByClientsRepository"
Cohesion: 0.31
Nodes (5): CuponUsedByClientsRepository, Modifying, Query, Repository, Transactional

### Community 64 - "ShSaleService.java"
Cohesion: 0.15
Nodes (10): PostMapping, ShPurchaseItemRequestDTO, ShPurchaseItemResponseDTO, ShPurchaseRequestDTO, ShPurchaseResponseDTO, CouponResolution, RequiredArgsConstructor, Service (+2 more)

### Community 66 - "CuponUsedByClients"
Cohesion: 0.39
Nodes (7): CuponUsedByClients, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table

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

### Community 73 - "ShSale"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShSale

### Community 77 - "Endpoints API"
Cohesion: 0.18
Nodes (11): Cupones de productos, Cupones de segunda mano, Endpoints API, GET `/notification/stream` — Stream de eventos (ADMIN), Historial de ventas (ADMIN), Notificaciones (SSE), POST `/cupons` — Crear cupón (ADMIN), Rankings (ADMIN) (+3 more)

### Community 78 - "Clientes y tarjetas"
Cohesion: 0.22
Nodes (9): Clientes y tarjetas, GET `/client/payment-cards` — Listar tarjetas (CLIENT), GET `/client/profile-photo` — Obtener foto de perfil (CLIENT), GET `/client/user-data` — Datos del cliente (CLIENT), GET /client/user-payments — Historial de compras del cliente (CLIENT), PATCH `/client/modify` — Modificar datos del cliente (CLIENT), PATCH `/client/payment-cards/{cardId}/status?active=true` — Activar/desactivar tarjeta (CLIENT), PATCH `/client/upload-profile` — Foto de perfil del cliente (CLIENT) (+1 more)

### Community 79 - "ServiceCuponToAClientRepository"
Cohesion: 0.29
Nodes (5): Modifying, Query, Repository, Transactional, ServiceCuponToAClientRepository

### Community 80 - "Imágenes de producto"
Cohesion: 0.50
Nodes (4): DELETE `/product-images/{imageId}` — Eliminar imagen (ADMIN), GET `/product-images/{productId}` — Imágenes de un producto (público), Imágenes de producto, POST `/product-images/upload/{productId}` — Subir imagen (ADMIN)

### Community 81 - "Panel de administración (ADMIN)"
Cohesion: 0.50
Nodes (4): GET /dashboard-controller/excel — Reporte en Excel (adjunto .xlsx), GET `/dashboard-controller/get-data-dashboard` — Métricas del dashboard, GET /dashboard-controller/pdf — Reporte en PDF (adjunto .pdf), Panel de administración (ADMIN)

### Community 82 - "Productos de segunda mano"
Cohesion: 0.50
Nodes (4): Imágenes SH — `/api/sh-product-images`, POST `/sh-product/save` — Crear producto SH (ADMIN), POST `/sh-sale/purchase` — Compra SH (CLIENT), Productos de segunda mano

### Community 83 - "JwtService"
Cohesion: 0.19
Nodes (8): BeforeEach, Claims, SecretKey, Service, JwtService, ExtendWith, Test, JwtServiceTest

### Community 84 - "Productos"
Cohesion: 0.50
Nodes (4): POST `/product/deleteSafe` — Borrado suave (ADMIN), POST `/product/saveProduct` — Crear producto (ADMIN), Productos, PUT `/product/update/{id}` — Actualizar producto (ADMIN)

### Community 85 - "Servicios ofrecidos"
Cohesion: 0.67
Nodes (3): Cupones de servicio, POST `/services` — Crear servicio (ADMIN), Servicios ofrecidos

### Community 86 - "SecondHandCuponService"
Cohesion: 0.26
Nodes (3): CouponResolution, Transactional, SecondHandCuponService

### Community 87 - "Ventas"
Cohesion: 0.67
Nodes (3): POST `/sale/purchase` — Compra de cliente (CLIENT), POST `/sale/sale/refresh` — Recalcular producto por venta (ADMIN), Ventas

### Community 88 - "ServiceCuponUsedByClients"
Cohesion: 0.18
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ServiceCuponUsedByClients, Modifying (+4 more)

### Community 89 - "SecondHandCupon"
Cohesion: 0.15
Nodes (13): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandCupon, Modifying (+5 more)

### Community 90 - "JpaRepository"
Cohesion: 0.48
Nodes (5): JpaRepository, Repository, SaleItemRepository, Repository, ShSaleRepository

### Community 91 - "ServiceCuponRepository"
Cohesion: 0.39
Nodes (5): Modifying, Query, Repository, Transactional, ServiceCuponRepository

### Community 92 - "ServiceCupon"
Cohesion: 0.19
Nodes (10): PutMapping, ServiceCuponRequestDTO, ServiceCuponResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor (+2 more)

### Community 93 - ".onAuthenticationSuccess"
Cohesion: 0.23
Nodes (9): Authentication, AuthenticationSuccessHandler, Component, HttpServletRequest, HttpServletResponse, Override, RequiredArgsConstructor, Transactional (+1 more)

### Community 94 - "ServiceCuponToAClient"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ServiceCuponToAClient

### Community 95 - "ProductCuponToAClient"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ProductCuponToAClient

### Community 96 - "UserService"
Cohesion: 0.18
Nodes (9): AllArgsConstructor, Cacheable, CacheManager, Cloudinary, MultipartFile, PasswordEncoder, Service, Transactional (+1 more)

### Community 97 - "ResponseStatusException"
Cohesion: 0.17
Nodes (11): ResponseStatusException, PutMapping, CuponRequestDTO, CuponResponseDTO, ResourceNotFoundException, CouponResolution, CuponService, Caching (+3 more)

### Community 98 - "ColorTypes"
Cohesion: 0.18
Nodes (13): Cache, UserResponseDTO, ColorTypes, AMBAR, AZUL, ROSA, VERDE, VIOLETA (+5 more)

### Community 99 - "UserAdmin"
Cohesion: 0.22
Nodes (9): Builder, ClientDescriptionAboutUsersDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table (+1 more)

### Community 100 - "UserController.java"
Cohesion: 0.14
Nodes (14): DynamicUpdate, GetMapping, MultipartFile, Operation, PatchMapping, PostMapping, RequestMapping, RequiredArgsConstructor (+6 more)

### Community 102 - "ShSalesItem"
Cohesion: 0.33
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShSalesItem

### Community 103 - "entrypoint.sh"
Cohesion: 0.50
Nodes (3): PGPASSWORD, PGSSLMODE, entrypoint.sh script

### Community 104 - "UserRepository"
Cohesion: 0.33
Nodes (5): Modifying, Query, Repository, Transactional, UserRepository

### Community 105 - "Por Implementar — ApiProject"
Cohesion: 0.33
Nodes (6): Caché, Endpoints / DTOs (por confirmar), Frontend, Migraciones / Vistas, Notificaciones (servicio que no frene la API), Por Implementar — ApiProject

### Community 106 - "SecondHandProduct"
Cohesion: 0.39
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SecondHandProduct

### Community 107 - "ReportDashboard"
Cohesion: 0.70
Nodes (4): Immutable, Entity, Table, ReportDashboard

### Community 109 - "SecondHandCuponService.java"
Cohesion: 0.21
Nodes (9): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, ShProductCuponToAClient, RequiredArgsConstructor (+1 more)

### Community 110 - "PaymentCard"
Cohesion: 0.46
Nodes (7): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, PaymentCard

## Knowledge Gaps
- **162 isolated node(s):** `$schema`, `.opencode/plugins/graphify.js`, `**/.env`, `**/*.key`, `**/*.pem` (+157 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **15 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AuthenticatedUser` connect `AuthenticatedUser` to `ServiceCuponController`, `ProductImage`, `SaleItemViewProjection`, `ServiceCuponService`, `ClientControllers.java`, `PaymentCardResponseDTO`, `SecondHandCuponController`, `ServiceOffered`, `Product`, `SecondHandProductService`, `ShProductImageDTO`, `CuponController`, `SaleController.java`, `ClientSummaryProjection`, `.update`, `ShSaleController.java`, `ShSaleService.java`, `DashboardController.java`, `RankingsController.java`, `JwtService`, `ServiceCupon`, `ResponseStatusException`, `UserController.java`?**
  _High betweenness centrality (0.180) - this node is a cross-community bridge._
- **Why does `ResourceNotFoundException` connect `ResponseStatusException` to `ProductImage`, `ServiceCuponService`, `ClientService`, `GlobalExceptionHandler`, `SaleService.java`, `DashboardService.java`, `ServiceOffered`, `Product`, `SecondHandProductService`, `SecondHandProductImage`, `ReportDashboardProjection`, `.update`, `ShSaleService.java`, `SecondHandCuponService`, `ServiceCupon`, `UserService`, `ColorTypes`, `.modifyData`, `SecondHandCuponService.java`?**
  _High betweenness centrality (0.106) - this node is a cross-community bridge._
- **Why does `UserClient` connect `UserClient` to `ServiceCuponService`, `ClientService`, `ClientControllers.java`, `SaleService.java`, `Sale`, `Product`, `SalesItem`, `ShCuponUsedByClients`, `SaleController.java`, `ClientSummaryProjection`, `ShSaleService.java`, `CuponUsedByClients`, `ShSale`, `ServiceCuponUsedByClients`, `.onAuthenticationSuccess`, `ServiceCuponToAClient`, `ProductCuponToAClient`, `ResponseStatusException`, `ColorTypes`, `LoginClientResponseDTO`, `ShSalesItem`, `SecondHandCuponService.java`, `PaymentCard`?**
  _High betweenness centrality (0.076) - this node is a cross-community bridge._
- **What connects `$schema`, `.opencode/plugins/graphify.js`, `**/.env` to the rest of the system?**
  _162 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ServiceCuponController` be split into smaller, more focused modules?**
  _Cohesion score 0.14814814814814814 - nodes in this community are weakly interconnected._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.14153846153846153 - nodes in this community are weakly interconnected._
- **Should `ProductImage` be split into smaller, more focused modules?**
  _Cohesion score 0.0851063829787234 - nodes in this community are weakly interconnected._