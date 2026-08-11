# Graph Report - ApiProject  (2026-08-11)

## Corpus Check
- 93 files · ~13,954 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 824 nodes · 1940 edges · 35 communities (29 shown, 6 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 133 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `47da8693`
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
- ClientService
- PaymentCardDetailsProjection
- ClientControllers.java
- GlobalExceptionHandler
- SaleService.java
- UserClient
- ClientHistoryProjection
- NotificationController.java
- ReportDashboardProjection
- read
- ClientSummaryProjection
- Protected Endpoints
- .register
- PaymentCard
- mvnw
- PaymentCardResponseDTO
- ClientLoginProjection
- CloudinaryConfig.java
- SaleItemService.java
- SaleResponseDato.java
- com.apiproject:ApiProject
- SwaggerConfig.java
- graphify-out Knowledge Graph
- ApiProjectApplication
- ApiProjectApplicationTests.java
- opencode.json
- graphify.js
- Graphify

## God Nodes (most connected - your core abstractions)
1. `Product` - 46 edges
2. `AuthenticatedUser` - 44 edges
3. `UserAdmin` - 41 edges
4. `UserClient` - 41 edges
5. `ClientService` - 33 edges
6. `ResourceNotFoundException` - 25 edges
7. `UserService` - 25 edges
8. `ProductResponseDTO` - 24 edges
9. `ProductRepository` - 24 edges
10. `ClientSummaryProjection` - 24 edges

## Surprising Connections (you probably didn't know these)
- `DashboardDTO` --references--> `ClientSummaryProjection`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/Admin/DashboardDTO.java → src/main/java/com/apiproject/repositories/projection/ClientSummaryProjection.java
- `ProductResponseDTO` --references--> `ProductImageDTO`  [EXTRACTED]
  src/main/java/com/apiproject/DTOs/General/ProductResponseDTO.java → src/main/java/com/apiproject/DTOs/Admin/ProductImageDTO.java
- `SecurityConfig` --references--> `OAuth2SuccessHandler`  [EXTRACTED]
  src/main/java/com/apiproject/config/SecurityConfig.java → src/main/java/com/apiproject/security/OAuth2SuccessHandler.java
- `DashboardController` --references--> `DashboardService`  [EXTRACTED]
  src/main/java/com/apiproject/controllers/admin/DashboardController.java → src/main/java/com/apiproject/services/admin/DashboardService.java
- `DashboardController` --references--> `ReportServiceFactory`  [EXTRACTED]
  src/main/java/com/apiproject/controllers/admin/DashboardController.java → src/main/java/com/apiproject/services/admin/ReportServiceFactory.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Bearer Token Authentication Flow** — api_frontend_consumption_login, api_frontend_consumption_register, api_frontend_consumption_bearer_token, api_frontend_consumption_protected_endpoints, api_frontend_consumption_client_token, api_frontend_consumption_admin_token [INFERRED 0.85]
- **Payment Card Feature** — api_frontend_consumption_payment_cards, api_frontend_consumption_purchase, api_frontend_consumption_add_payment_cards_sql, api_frontend_consumption_payment_cards_table [INFERRED 0.85]
- **Product Catalog Feature** — api_frontend_consumption_product_catalog, api_frontend_consumption_product_images, api_frontend_consumption_purchase, api_frontend_consumption_client_token, api_frontend_consumption_admin_token [INFERRED 0.75]

## Communities (35 total, 6 thin omitted)

### Community 0 - "Product"
Cohesion: 0.06
Nodes (36): Lock, PreAuthorize, PutMapping, GetMapping, Operation, Page, PostMapping, RequestMapping (+28 more)

### Community 1 - "UserAdmin"
Cohesion: 0.05
Nodes (46): Authentication, AuthenticationSuccessHandler, DynamicUpdate, GetMapping, MultipartFile, Operation, PatchMapping, PostMapping (+38 more)

### Community 2 - "SecurityConfig.java"
Cohesion: 0.15
Nodes (18): CorsConfigurationSource, EnableMethodSecurity, EnableWebSecurity, FilterChain, HttpSecurity, OncePerRequestFilter, SecurityFilterChain, Bean (+10 more)

### Community 3 - "ProductImage"
Cohesion: 0.09
Nodes (29): DeleteMapping, GetMapping, MultipartFile, Operation, PostMapping, RequestMapping, ResponseEntity, RestController (+21 more)

### Community 4 - "AuthenticatedUser"
Cohesion: 0.12
Nodes (15): GrantedAuthority, DashboardController, GetMapping, Operation, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+7 more)

### Community 5 - "SaleItemViewProjection"
Cohesion: 0.11
Nodes (20): GetMapping, Operation, Page, RequestMapping, RequiredArgsConstructor, RestController, Tag, SalesItemViewController (+12 more)

### Community 6 - "JwtService"
Cohesion: 0.19
Nodes (8): BeforeEach, Claims, ExtendWith, SecretKey, Service, JwtService, Test, JwtServiceTest

### Community 7 - "ClientService"
Cohesion: 0.16
Nodes (10): ResponseStatusException, ResourceNotFoundException, ClientService, CachePut, Cloudinary, MultipartFile, PasswordEncoder, RequiredArgsConstructor (+2 more)

### Community 8 - "PaymentCardDetailsProjection"
Cohesion: 0.17
Nodes (5): Modifying, Query, Repository, PaymentCardRepository, PaymentCardDetailsProjection

### Community 9 - "ClientControllers.java"
Cohesion: 0.20
Nodes (10): ClientControllers, GetMapping, MultipartFile, Operation, PatchMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+2 more)

### Community 10 - "GlobalExceptionHandler"
Cohesion: 0.28
Nodes (11): DataIntegrityViolationException, ExceptionHandler, HttpRequestMethodNotSupportedException, HttpStatus, MaxUploadSizeExceededException, RestControllerAdvice, ApiError, GlobalExceptionHandler (+3 more)

### Community 11 - "SaleService.java"
Cohesion: 0.05
Nodes (43): JpaRepository, Operation, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, Tag, SaleController (+35 more)

### Community 12 - "UserClient"
Cohesion: 0.19
Nodes (10): AllArgsConstructor, Data, Entity, NoArgsConstructor, Table, UserClient, ClientRepository, Modifying (+2 more)

### Community 14 - "NotificationController.java"
Cohesion: 0.26
Nodes (10): GetMapping, Operation, RequestMapping, RequiredArgsConstructor, RestController, SseEmitter, NotificationController, Service (+2 more)

### Community 15 - "ReportDashboardProjection"
Cohesion: 0.06
Nodes (21): CellStyle, Color, Document, Font, IndexedColors, PdfPTable, Sheet, FileTypes (+13 more)

### Community 16 - "read"
Cohesion: 0.17
Nodes (11): cat *.env*, rm -rf *, type *.env*, $schema, permission, bash, read, *.env (+3 more)

### Community 17 - "ClientSummaryProjection"
Cohesion: 0.05
Nodes (36): Immutable, Repository, CacheConstants, ClientsSummaryViewController, GetMapping, Operation, Page, RequestMapping (+28 more)

### Community 18 - "Protected Endpoints"
Cohesion: 0.22
Nodes (14): add-payment-cards.sql Script, Admin Token, /api Base Path, Bearer Token Authentication, Client Token, Login Endpoint, LoginResponseDTO, Payment Cards API (+6 more)

### Community 19 - ".register"
Cohesion: 0.20
Nodes (4): PostMapping, LoginClientRequestDTO, LoginClientResponseDTO, RegisterClientRequestDTO

### Community 20 - "PaymentCard"
Cohesion: 0.27
Nodes (8): ClientResponseDTO, AllArgsConstructor, Data, DynamicUpdate, Entity, NoArgsConstructor, Table, PaymentCard

### Community 21 - "mvnw"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 22 - "PaymentCardResponseDTO"
Cohesion: 0.33
Nodes (4): PaymentCardRequestDTO, PaymentCardResponseDTO, Caching, Transactional

### Community 24 - "CloudinaryConfig.java"
Cohesion: 0.53
Nodes (4): CloudinaryConfig, Bean, Cloudinary, Configuration

### Community 25 - "SaleItemService.java"
Cohesion: 0.83
Nodes (3): RequiredArgsConstructor, Service, SaleItemService

### Community 30 - "SwaggerConfig.java"
Cohesion: 0.53
Nodes (4): OpenAPI, Bean, Configuration, SwaggerConfig

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

## Knowledge Gaps
- **26 isolated node(s):** `$schema`, `*.env`, `*.env.example`, `*.key`, `*.pem` (+21 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **6 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AuthenticatedUser` connect `AuthenticatedUser` to `Product`, `UserAdmin`, `ProductImage`, `JwtService`, `ClientControllers.java`, `SaleService.java`, `NotificationController.java`, `PaymentCardResponseDTO`?**
  _High betweenness centrality (0.163) - this node is a cross-community bridge._
- **Why does `UserClient` connect `UserClient` to `Product`, `UserAdmin`, `ClientService`, `ClientControllers.java`, `SaleService.java`, `ClientSummaryProjection`, `.register`, `PaymentCard`?**
  _High betweenness centrality (0.110) - this node is a cross-community bridge._
- **Why does `ResourceNotFoundException` connect `ClientService` to `Product`, `UserAdmin`, `ProductImage`, `GlobalExceptionHandler`, `SaleService.java`, `UserClient`, `ClientSummaryProjection`?**
  _High betweenness centrality (0.100) - this node is a cross-community bridge._
- **What connects `$schema`, `*.env`, `*.env.example` to the rest of the system?**
  _26 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Product` be split into smaller, more focused modules?**
  _Cohesion score 0.06414565826330532 - nodes in this community are weakly interconnected._
- **Should `UserAdmin` be split into smaller, more focused modules?**
  _Cohesion score 0.05198537095088819 - nodes in this community are weakly interconnected._
- **Should `ProductImage` be split into smaller, more focused modules?**
  _Cohesion score 0.08788159111933395 - nodes in this community are weakly interconnected._