# Graph Report - .  (2026-08-01)

## Corpus Check
- 95 files · ~160,258 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 803 nodes · 1874 edges · 44 communities (33 shown, 11 thin omitted)
- Extraction: 91% EXTRACTED · 8% INFERRED · 1% AMBIGUOUS · INFERRED: 154 edges (avg confidence: 0.81)
- Token cost: 5,500 input · 2,160 output

## Community Hubs (Navigation)
- Product Catalog Controller
- User Admin Management
- Security & JWT Authentication
- Product Image Management
- Dashboard & Notifications
- Sales Item Views
- Client Summary Views
- Client Service Logic
- Payment Card Persistence
- Client API Endpoints
- Global Error Handling
- Sale & Purchase Flow
- Client Persistence
- Client Purchase History
- Dashboard Queries
- Excel Export
- Sale Item Persistence
- Sale REST Controller
- Frontend API Contract
- Sale Entity
- Dashboard Caching
- Maven Wrapper Script
- Data Model Diagram
- Report Dashboard Entity
- Client Auth DTOs
- Report Export Abstraction
- Report Projection
- SSE Notifications
- Client Login Projection
- Layered Architecture Diagram
- OpenAPI Configuration
- Cloudinary Configuration
- Graphify Integration Docs
- Application Bootstrap
- Context Load Tests
- Sale Item Repository
- OpenCode Plugin Config
- Sale Item Service
- Graphify Plugin Hook
- PDF Export
- Sale Response DTO
- Graphify Concept
- Package Structure Diagram
- Maven Project Module

## God Nodes (most connected - your core abstractions)
1. `Product` - 46 edges
2. `AuthenticatedUser` - 43 edges
3. `UserAdmin` - 41 edges
4. `UserClient` - 41 edges
5. `ClientService` - 33 edges
6. `UserService` - 25 edges
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
- `NotificationController` --references--> `NotificationService`  [EXTRACTED]
  src/main/java/com/example/apiproject/controllers/admin/NotificationController.java → src/main/java/com/example/apiproject/services/admin/NotificationService.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Bearer Token Authentication Flow** — api_frontend_consumption_login, api_frontend_consumption_register, api_frontend_consumption_bearer_token, api_frontend_consumption_protected_endpoints, api_frontend_consumption_client_token, api_frontend_consumption_admin_token [INFERRED 0.85]
- **Payment Card Feature** — api_frontend_consumption_payment_cards, api_frontend_consumption_purchase, api_frontend_consumption_add_payment_cards_sql, api_frontend_consumption_payment_cards_table [INFERRED 0.85]
- **Product Catalog Feature** — api_frontend_consumption_product_catalog, api_frontend_consumption_product_images, api_frontend_consumption_purchase, api_frontend_consumption_client_token, api_frontend_consumption_admin_token [INFERRED 0.75]
- **Layered Architecture Request Flow** — images_arquitecturaporcapas_controllers, images_arquitecturaporcapas_services, images_arquitecturaporcapas_repositories, images_arquitecturaporcapas_entities [INFERRED 0.75]
- **Purchase / Sale Flow** — images_modelodedatos_userclient, images_modelodedatos_sale, images_modelodedatos_salesitem, images_modelodedatos_product [INFERRED 0.85]
- **Product Catalog Management** — images_modelodedatos_useradmin, images_modelodedatos_product, images_modelodedatos_productimage [INFERRED 0.85]

## Communities (44 total, 11 thin omitted)

### Community 0 - "Product Catalog Controller"
Cohesion: 0.07
Nodes (36): Lock, PreAuthorize, PutMapping, GetMapping, Operation, Page, PostMapping, RequestMapping (+28 more)

### Community 1 - "User Admin Management"
Cohesion: 0.06
Nodes (38): DynamicUpdate, GetMapping, MultipartFile, Operation, PatchMapping, PostMapping, RequestMapping, RequiredArgsConstructor (+30 more)

### Community 2 - "Security & JWT Authentication"
Cohesion: 0.06
Nodes (35): Authentication, AuthenticationSuccessHandler, BeforeEach, Claims, CorsConfigurationSource, EnableMethodSecurity, EnableWebSecurity, ExtendWith (+27 more)

### Community 3 - "Product Image Management"
Cohesion: 0.09
Nodes (29): DeleteMapping, GetMapping, MultipartFile, Operation, PostMapping, RequestMapping, ResponseEntity, RestController (+21 more)

### Community 4 - "Dashboard & Notifications"
Cohesion: 0.08
Nodes (23): GrantedAuthority, DashboardController, GetMapping, Operation, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+15 more)

### Community 5 - "Sales Item Views"
Cohesion: 0.11
Nodes (20): GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag, SalesItemViewController (+12 more)

### Community 6 - "Client Summary Views"
Cohesion: 0.11
Nodes (20): ClientsSummaryViewController, GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag (+12 more)

### Community 7 - "Client Service Logic"
Cohesion: 0.15
Nodes (14): ResponseStatusException, ClientResponseDTO, PaymentCardResponseDTO, ClientService, Cacheable, CachePut, Caching, Cloudinary (+6 more)

### Community 8 - "Payment Card Persistence"
Cohesion: 0.12
Nodes (12): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, PaymentCard, Modifying (+4 more)

### Community 9 - "Client API Endpoints"
Cohesion: 0.14
Nodes (13): ClientControllers, GetMapping, MultipartFile, Operation, PatchMapping, PostMapping, RequestMapping, RequiredArgsConstructor (+5 more)

### Community 10 - "Global Error Handling"
Cohesion: 0.28
Nodes (11): DataIntegrityViolationException, ExceptionHandler, HttpRequestMethodNotSupportedException, HttpStatus, MaxUploadSizeExceededException, RestControllerAdvice, ApiError, GlobalExceptionHandler (+3 more)

### Community 11 - "Sale & Purchase Flow"
Cohesion: 0.16
Nodes (10): EntityManager, PurchaseItemRequestDTO, PurchaseRequestDTO, CacheManager, Caching, RequiredArgsConstructor, Service, Transactional (+2 more)

### Community 12 - "Client Persistence"
Cohesion: 0.18
Nodes (10): AllArgsConstructor, Data, Entity, NoArgsConstructor, Table, UserClient, ClientRepository, Modifying (+2 more)

### Community 14 - "Dashboard Queries"
Cohesion: 0.14
Nodes (4): Repository, DashboardRepository, Query, DashboardProjection

### Community 15 - "Excel Export"
Cohesion: 0.25
Nodes (6): CellStyle, Sheet, ExcelExporter, Component, Override, Workbook

### Community 16 - "Sale Item Persistence"
Cohesion: 0.19
Nodes (12): SaleItemResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, SalesItem (+4 more)

### Community 17 - "Sale REST Controller"
Cohesion: 0.23
Nodes (9): Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, Tag, SaleController, PurchaseItemResponseDTO (+1 more)

### Community 18 - "Frontend API Contract"
Cohesion: 0.22
Nodes (14): add-payment-cards.sql Script, Admin Token, /api Base Path, Bearer Token Authentication, Client Token, Login Endpoint, LoginResponseDTO, Payment Cards API (+6 more)

### Community 19 - "Sale Entity"
Cohesion: 0.27
Nodes (9): AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, Sale, Repository (+1 more)

### Community 20 - "Dashboard Caching"
Cohesion: 0.27
Nodes (6): CacheConstants, DashboardService, Cacheable, RequiredArgsConstructor, Service, Transactional

### Community 21 - "Maven Wrapper Script"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 22 - "Data Model Diagram"
Cohesion: 0.56
Nodes (9): Data Model Diagram (ModeloDeDatos), PaymentCard (payment_cards), Product (products), ProductImage (product_image), ReportDashboard (report_view_dashboard), Sale (sales), SalesItem (sale_items), UserAdmin (users) (+1 more)

### Community 23 - "Report Dashboard Entity"
Cohesion: 0.36
Nodes (6): Immutable, Entity, Table, ReportDashboard, Query, ReportDashboardRepository

### Community 27 - "SSE Notifications"
Cohesion: 0.39
Nodes (4): NotificationEventDTO, Service, SseEmitter, NotificationService

### Community 29 - "Layered Architecture Diagram"
Cohesion: 0.48
Nodes (7): Arquitectura Por Capas Diagram (Layered Architecture), Controllers Layer (Presentation / REST), DTOs Layer (Data Transfer Objects), Entities Layer (JPA Models), Repositories Layer (Data Access / Persistence), Security / Config Cross-Cutting Layer, Services Layer (Business Logic)

### Community 30 - "OpenAPI Configuration"
Cohesion: 0.53
Nodes (4): OpenAPI, Bean, Configuration, SwaggerConfig

### Community 31 - "Cloudinary Configuration"
Cohesion: 0.53
Nodes (4): CloudinaryConfig, Bean, Cloudinary, Configuration

### Community 32 - "Graphify Integration Docs"
Cohesion: 0.40
Nodes (5): GRAPH_REPORT.md, graphify-out Knowledge Graph, graphify query/path/explain Tools, graphify update Command, graphify-out wiki index

### Community 33 - "Application Bootstrap"
Cohesion: 0.60
Nodes (3): EnableCaching, SpringBootApplication, ApiProjectApplication

### Community 34 - "Context Load Tests"
Cohesion: 0.60
Nodes (3): SpringBootTest, ApiProjectApplicationTests, Test

### Community 35 - "Sale Item Repository"
Cohesion: 0.83
Nodes (3): JpaRepository, Repository, SaleItemRepository

### Community 36 - "OpenCode Plugin Config"
Cohesion: 0.50
Nodes (3): plugin, $schema, .opencode/plugins/graphify.js

### Community 37 - "Sale Item Service"
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
- **18 isolated node(s):** `$schema`, `.opencode/plugins/graphify.js`, `com.example:ApiProject`, `SaleResponseDato`, `COMPLETED` (+13 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **11 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

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