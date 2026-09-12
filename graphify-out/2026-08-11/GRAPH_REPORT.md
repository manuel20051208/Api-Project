# Graph Report - ApiProject  (2026-08-03)

## Corpus Check
- 92 files · ~159,984 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 813 nodes · 1880 edges · 42 communities (30 shown, 12 thin omitted)
- Extraction: 91% EXTRACTED · 8% INFERRED · 1% AMBIGUOUS · INFERRED: 154 edges (avg confidence: 0.81)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `78f9172e`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Product
- UserAdmin
- .onAuthenticationSuccess
- ProductImage
- AuthenticatedUser
- SaleItemViewProjection
- ClientSummaryProjection
- ClientService
- PaymentCard
- ClientControllers.java
- GlobalExceptionHandler
- SaleService.java
- UserClient
- ClientHistoryProjection
- DashboardProjection
- ExcelExporter
- read
- Repository
- Protected Endpoints
- PaymentCardResponseDTO
- DashboardService
- mvnw
- Data Model Diagram (ModeloDeDatos)
- ReportDashboard
- .register
- ReportExporter
- ReportDashboardProjection
- ClientLoginProjection
- Arquitectura Por Capas Diagram (Layered Architecture)
- SwaggerConfig.java
- CloudinaryConfig.java
- graphify-out Knowledge Graph
- ApiProjectApplication
- ApiProjectApplicationTests.java
- opencode.json
- SaleItemService.java
- graphify.js
- PdfExporter.java
- SaleResponseDato.java
- Graphify
- Estructura De Paquetes (Package Structure Diagram)
- com.example:ApiProject

## God Nodes (most connected - your core abstractions)
1. `Product` - 46 edges
2. `AuthenticatedUser` - 43 edges
3. `UserAdmin` - 41 edges
4. `UserClient` - 41 edges
5. `ClientService` - 33 edges
6. `UserServiceTest` - 25 edges
7. `ProductResponseDTO` - 24 edges
8. `ResourceNotFoundException` - 24 edges
9. `ProductRepository` - 24 edges
10. `ClientSummaryProjection` - 24 edges

## Surprising Connections (you probably didn't know these)
- `DashboardDTO` --references--> `ClientSummaryProjection`  [EXTRACTED]
  src/main/java/com/example/apiproject/DTOs/Admin/DashboardDTO.java → src/main/java/com/example/apiproject/repositories/projection/ClientSummaryProjection.java
- `ProductResponseDTO` --references--> `ProductImageDTO`  [EXTRACTED]
  src/main/java/com/example/apiproject/DTOs/General/ProductResponseDTO.java → src/main/java/com/example/apiproject/DTOs/Admin/ProductImageDTO.java
- `ClientResponseDTO` --references--> `PaymentCard`  [EXTRACTED]
  src/main/java/com/example/apiproject/DTOs/Client/ClientResponseDTO.java → src/main/java/com/example/apiproject/entities/client/PaymentCard.java
- `DashboardController` --references--> `DashboardService`  [EXTRACTED]
  src/main/java/com/example/apiproject/controllers/admin/DashboardController.java → src/main/java/com/example/apiproject/services/admin/DashboardService.java
- `ClientControllers` --references--> `UserServiceTest`  [EXTRACTED]
  src/main/java/com/example/apiproject/controllers/client/ClientControllers.java → src/main/java/com/example/apiproject/services/admin/UserService.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Bearer Token Authentication Flow** — api_frontend_consumption_login, api_frontend_consumption_register, api_frontend_consumption_bearer_token, api_frontend_consumption_protected_endpoints, api_frontend_consumption_client_token, api_frontend_consumption_admin_token [INFERRED 0.85]
- **Payment Card Feature** — api_frontend_consumption_payment_cards, api_frontend_consumption_purchase, api_frontend_consumption_add_payment_cards_sql, api_frontend_consumption_payment_cards_table [INFERRED 0.85]
- **Product Catalog Feature** — api_frontend_consumption_product_catalog, api_frontend_consumption_product_images, api_frontend_consumption_purchase, api_frontend_consumption_client_token, api_frontend_consumption_admin_token [INFERRED 0.75]
- **Layered Architecture Request Flow** — images_arquitecturaporcapas_controllers, images_arquitecturaporcapas_services, images_arquitecturaporcapas_repositories, images_arquitecturaporcapas_entities [INFERRED 0.75]
- **Purchase / Sale Flow** — images_modelodedatos_userclient, images_modelodedatos_sale, images_modelodedatos_salesitem, images_modelodedatos_product [INFERRED 0.85]
- **Product Catalog Management** — images_modelodedatos_useradmin, images_modelodedatos_product, images_modelodedatos_productimage [INFERRED 0.85]

## Communities (42 total, 12 thin omitted)

### Community 0 - "Product"
Cohesion: 0.06
Nodes (36): Lock, PreAuthorize, PutMapping, GetMapping, Operation, Page, PostMapping, RequestMapping (+28 more)

### Community 1 - "UserAdmin"
Cohesion: 0.06
Nodes (37): DynamicUpdate, GetMapping, MultipartFile, Operation, PatchMapping, PostMapping, RequestMapping, RequiredArgsConstructor (+29 more)

### Community 2 - ".onAuthenticationSuccess"
Cohesion: 0.06
Nodes (35): Authentication, AuthenticationSuccessHandler, BeforeEach, Claims, CorsConfigurationSource, EnableMethodSecurity, EnableWebSecurity, ExtendWith (+27 more)

### Community 3 - "ProductImage"
Cohesion: 0.09
Nodes (29): DeleteMapping, GetMapping, MultipartFile, Operation, PostMapping, RequestMapping, ResponseEntity, RestController (+21 more)

### Community 4 - "AuthenticatedUser"
Cohesion: 0.07
Nodes (26): GrantedAuthority, DashboardController, GetMapping, Operation, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+18 more)

### Community 5 - "SaleItemViewProjection"
Cohesion: 0.11
Nodes (20): GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag, SalesItemViewController (+12 more)

### Community 6 - "ClientSummaryProjection"
Cohesion: 0.12
Nodes (20): ClientsSummaryViewController, GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag (+12 more)

### Community 7 - "ClientService"
Cohesion: 0.14
Nodes (13): ResponseStatusException, ResourceNotFoundException, ClientService, Cacheable, CachePut, Caching, Cloudinary, MultipartFile (+5 more)

### Community 8 - "PaymentCard"
Cohesion: 0.12
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, PaymentCard, Modifying (+4 more)

### Community 9 - "ClientControllers.java"
Cohesion: 0.19
Nodes (11): ClientControllers, GetMapping, MultipartFile, Operation, PatchMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+3 more)

### Community 10 - "GlobalExceptionHandler"
Cohesion: 0.28
Nodes (11): DataIntegrityViolationException, ExceptionHandler, HttpRequestMethodNotSupportedException, HttpStatus, MaxUploadSizeExceededException, RestControllerAdvice, ApiError, GlobalExceptionHandler (+3 more)

### Community 11 - "SaleService.java"
Cohesion: 0.05
Nodes (43): JpaRepository, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, Tag, SaleController (+35 more)

### Community 12 - "UserClient"
Cohesion: 0.20
Nodes (10): AllArgsConstructor, Data, Entity, NoArgsConstructor, Table, UserClient, ClientRepository, Modifying (+2 more)

### Community 15 - "ExcelExporter"
Cohesion: 0.25
Nodes (6): CellStyle, Sheet, ExcelExporter, Component, Override, Workbook

### Community 16 - "read"
Cohesion: 0.17
Nodes (11): cat *.env*, rm -rf *, type *.env*, $schema, permission, bash, read, *.env (+3 more)

### Community 17 - "Repository"
Cohesion: 0.33
Nodes (5): Repository, DashboardRepository, Query, Query, ReportDashboardRepository

### Community 18 - "Protected Endpoints"
Cohesion: 0.22
Nodes (14): add-payment-cards.sql Script, Admin Token, /api Base Path, Bearer Token Authentication, Client Token, Login Endpoint, LoginResponseDTO, Payment Cards API (+6 more)

### Community 20 - "DashboardService"
Cohesion: 0.24
Nodes (6): CacheConstants, DashboardService, Cacheable, RequiredArgsConstructor, Service, Transactional

### Community 21 - "mvnw"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 22 - "Data Model Diagram (ModeloDeDatos)"
Cohesion: 0.56
Nodes (9): Data Model Diagram (ModeloDeDatos), PaymentCard (payment_cards), Product (products), ProductImage (product_image), ReportDashboard (report_view_dashboard), Sale (sales), SalesItem (sale_items), UserAdmin (users) (+1 more)

### Community 23 - "ReportDashboard"
Cohesion: 0.70
Nodes (4): Immutable, Entity, Table, ReportDashboard

### Community 24 - ".register"
Cohesion: 0.24
Nodes (4): PostMapping, LoginClientRequestDTO, LoginClientResponseDTO, RegisterClientRequestDTO

### Community 29 - "Arquitectura Por Capas Diagram (Layered Architecture)"
Cohesion: 0.48
Nodes (7): Arquitectura Por Capas Diagram (Layered Architecture), Controllers Layer (Presentation / REST), DTOs Layer (Data Transfer Objects), Entities Layer (JPA Models), Repositories Layer (Data Access / Persistence), Security / Config Cross-Cutting Layer, Services Layer (Business Logic)

### Community 30 - "SwaggerConfig.java"
Cohesion: 0.53
Nodes (4): OpenAPI, Bean, Configuration, SwaggerConfig

### Community 31 - "CloudinaryConfig.java"
Cohesion: 0.53
Nodes (4): CloudinaryConfig, Bean, Cloudinary, Configuration

### Community 32 - "graphify-out Knowledge Graph"
Cohesion: 0.40
Nodes (5): GRAPH_REPORT.md, graphify-out Knowledge Graph, graphify query/path/explain Tools, graphify update Command, graphify-out wiki index

### Community 33 - "ApiProjectApplication"
Cohesion: 0.60
Nodes (3): EnableCaching, SpringBootApplication, ApiProjectApplication

### Community 34 - "ApiProjectApplicationTests.java"
Cohesion: 0.60
Nodes (3): SpringBootTest, ApiProjectApplicationTests, Test

### Community 36 - "opencode.json"
Cohesion: 0.50
Nodes (3): $schema, plugin, .opencode/plugins/graphify.js

### Community 37 - "SaleItemService.java"
Cohesion: 0.83
Nodes (3): RequiredArgsConstructor, Service, SaleItemService

## Ambiguous Edges - Review These
- `Arquitectura Por Capas Diagram (Layered Architecture)` → `Controllers Layer (Presentation / REST)`  [AMBIGUOUS]
  images/ArquitecturaPorCapas.png · relation: references
- `Arquitectura Por Capas Diagram (Layered Architecture)` → `DTOs Layer (Data Transfer Objects)`  [AMBIGUOUS]
  images/ArquitecturaPorCapas.png · relation: references
- `Arquitectura Por Capas Diagram (Layered Architecture)` → `Entities Layer (JPA Models)`  [AMBIGUOUS]
  images/ArquitecturaPorCapas.png · relation: references
- `Arquitectura Por Capas Diagram (Layered Architecture)` → `Repositories Layer (Data Access / Persistence)`  [AMBIGUOUS]
  images/ArquitecturaPorCapas.png · relation: references
- `Arquitectura Por Capas Diagram (Layered Architecture)` → `Security / Config Cross-Cutting Layer`  [AMBIGUOUS]
  images/ArquitecturaPorCapas.png · relation: references
- `Arquitectura Por Capas Diagram (Layered Architecture)` → `Services Layer (Business Logic)`  [AMBIGUOUS]
  images/ArquitecturaPorCapas.png · relation: references
- `Controllers Layer (Presentation / REST)` → `Services Layer (Business Logic)`  [AMBIGUOUS]
  images/ArquitecturaPorCapas.png · relation: calls
- `DTOs Layer (Data Transfer Objects)` → `Entities Layer (JPA Models)`  [AMBIGUOUS]
  images/ArquitecturaPorCapas.png · relation: shares_data_with
- `Services Layer (Business Logic)` → `Repositories Layer (Data Access / Persistence)`  [AMBIGUOUS]
  images/ArquitecturaPorCapas.png · relation: calls
- `Repositories Layer (Data Access / Persistence)` → `Entities Layer (JPA Models)`  [AMBIGUOUS]
  images/ArquitecturaPorCapas.png · relation: references

## Knowledge Gaps
- **26 isolated node(s):** `$schema`, `*.env`, `*.env.example`, `*.key`, `*.pem` (+21 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **12 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Arquitectura Por Capas Diagram (Layered Architecture)` and `Controllers Layer (Presentation / REST)`?**
  _Edge tagged AMBIGUOUS (relation: references) - confidence is low._
- **What is the exact relationship between `Arquitectura Por Capas Diagram (Layered Architecture)` and `DTOs Layer (Data Transfer Objects)`?**
  _Edge tagged AMBIGUOUS (relation: references) - confidence is low._
- **What is the exact relationship between `Arquitectura Por Capas Diagram (Layered Architecture)` and `Entities Layer (JPA Models)`?**
  _Edge tagged AMBIGUOUS (relation: references) - confidence is low._
- **What is the exact relationship between `Arquitectura Por Capas Diagram (Layered Architecture)` and `Repositories Layer (Data Access / Persistence)`?**
  _Edge tagged AMBIGUOUS (relation: references) - confidence is low._
- **What is the exact relationship between `Arquitectura Por Capas Diagram (Layered Architecture)` and `Security / Config Cross-Cutting Layer`?**
  _Edge tagged AMBIGUOUS (relation: references) - confidence is low._
- **What is the exact relationship between `Arquitectura Por Capas Diagram (Layered Architecture)` and `Services Layer (Business Logic)`?**
  _Edge tagged AMBIGUOUS (relation: references) - confidence is low._
- **What is the exact relationship between `Controllers Layer (Presentation / REST)` and `Services Layer (Business Logic)`?**
  _Edge tagged AMBIGUOUS (relation: calls) - confidence is low._