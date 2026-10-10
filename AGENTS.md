# Agent Instructions

## Cerebro del proyecto

Read `BRAIN.md` before answering any codebase question or making changes. It contains the full structure, how the project works, endpoints, DB schema, config, and conventions. Consult it FIRST — it saves browsing and greps.

## Checklist de pendientes

Read `docs/POR IMPLEMENTAR.md` at the START of every session and remind the user which items are still pending. Update it as tasks get done (mark `- [x]`).

> `docs/` está **gitignored** (vault solo local): si la carpeta no existe (p. ej. tras clonar), crearla a mano con su `POR IMPLEMENTAR.md`.

## graphify

This project has a knowledge graph at graphify-out/ with god nodes, community structure, and cross-file relationships.

> `graphify-out/` está **gitignored** (artefacto local, NO se versiona). Si no existe (tras clonar/borrarse), generarlo con `graphify update .`. Contenido "dirty" dentro de esa carpeta es esperable y no se commitea.

When the user types `/graphify`, use the installed graphify skill or instructions before doing anything else.

Rules:
- For codebase questions, after reading BRAIN.md, run `graphify query "<question>"` when graphify-out/graph.json exists. Use `graphify path "<A>" "<B>"` for relationships and `graphify explain "<concept>"` for focused concepts. These return a scoped subgraph, usually much smaller than GRAPH_REPORT.md or raw grep output.
- Dirty graphify-out/ files are expected after hooks or incremental updates; dirty graph files are not a reason to skip graphify. Only skip graphify if the task is about stale or incorrect graph output, or the user explicitly says not to use it.
- If graphify-out/wiki/index.md exists, use it for broad navigation instead of raw source browsing.
- Read graphify-out/GRAPH_REPORT.md only for broad architecture review or when query/path/explain do not surface enough context.
- After modifying code, run `graphify update .` to keep the graph current (AST-only, no API cost).

## Obsidian docs

`docs/` holds the human-readable Obsidian vault (MOC in `docs/PROJECT_STRUCTURE.md`). Está **gitignored (solo local)** — no se sube a GitHub; mantenerlo en sync local cuando cambie la estructura. Los **diagramas versionados** viven en `diagrams/` (antes `docs/diagrams/`).

## Arbol de estructura (navegacion directa)

Mapa del codigo para llegar a la ubicacion pedida SIN buscar rama por rama. Si el usuario dice "ve al controlador X", ir directo a su archivo. Tabla base: `src/main/java/com/apiproject/`.

Regla de nombres: si el nombre exacto que da el usuario NO existe (o parece herrado), NO buscar en todo el `src` de golpe: buscar primero archivos "similares" en la raiz correspondiente (`controllers/`, `services/`, `repositories/`, `DTOs/`, `entities/`) usando patrón glob parcial (ej: `*Cupon*Controller*`). Solo si no hay coincidencia en esa raiz, ampliar la busqueda.

### Regla de capas por dominio (SOLID)

Todo tema de negocios vive en su propia raiz y capa. Palabras clave del usuario → capa a la que va:

- **Dominios por raiz**: `client/`, `admin/`, `general/`, `auth/`. Si el usuario habla de CLIENTES → raiz `client/` (controller `client/ClientControllers.java`, service `client/ClientService.java`). ADMINS → `admin/`. Compras/ventas generales → `general/`. Login/registro → DTOs en `Auth/`.
- **"dame un endpoint" / "investiga el endpoint" / "que recibe la ruta"** → ir directo al CONTROLADOR del dominio (`controllers/<dominio>/`). Ahí está la ruta y los parametros.
- **"como lo implementamos" / "que hiciste en X metodo" / "de que manera"** → ir directo al SERVICE del dominio (`services/<dominio>/`). La logica de negocio vive ahi.
- **"donde esta la query" / "que consulta hace"** → repositorio del dominio (`repositories/<dominio>/`) y su proyeccion (`repositories/projection/`).
- Respuesta JSON / forma del DTO → `DTOs/<dominio>/`.

### Contexto en toda respuesta

Al explicar cualquier endpoint, método o consulta, SIEMPRE dar contextoy breve y en este orden:

1. **Qué retorna** (el JSON de respuesta, con campos reales del DTO/proyección).
2. **Qué recibe** (parámetros/ruta/body JSON que espera).
3. **Cómo lo hace** (el flujo por lo alto: qué servicio llama, qué query/vista usa, y POR QUÉ se usó esa vía — p.ej. vista materializada, cache, window function, paginación). Explicar brevemente el "cómo" y el "por qué", sin ir al detalle línea por línea salvo que lo pidan.

### controllers/

- `admin/` → ventas en vista, resumen clientes, dashboard, cupones, rankings, usuarios admin, servicios, productos SH, imagenes.
  - `Admin/AdminList` no existe. Listado real:
  - `DashboardController.java` → `/dashboard-controller/get-data-dashboard` (JSON del dashboard, totales).
  - `ClientsSummaryViewController.java` → `/api/client-show-summary/*` (resumen de clientes por vista `clients_summary`).
  - `SalesItemViewController.java` → `/api/sales-items/*` (BUSQUEDA DE VENTAS: `show-with-limits`, `show-with-no-restrinction`, `searchAllClientByName`, `searchAllByProductName`).
  - `CuponController.java` → `/api/cupons/*` (CRUD cupones, asignacion, send).
  - `RankingsController.java`, `ProductImageController.java`, `SecondHandCuponController.java`, `ServiceCuponController.java`, `ServiceOfferedController.java`, `SecondHandProductImageController.java`, `UserController.java`.
- `client/` → `ClientControllers.java` (CRUD clientes/cards), `ProductController.java`, `ShProductController.java`.
- `general/` → `SaleController.java` (`/api/sale/purchase`, `/api/sale/sale/refresh`), `ShSaleController.java` (`/clientHistory`, `/adminHistory`).

### services/

- `admin/` → `DashboardService.java`, `ClientsSummaryViewService.java`, `CuponService.java`, `SaleItemViewService.java`, `RankingsService.java`, `UserService.java`, `ReportServiceFactory.java`, etc.
- `general/` → `SaleService.java` (logica de compra/venta + totales), `ShSaleService.java`, `ProductService.java`, `SecondHandProductService.java`.
- `client/` → `ClientService.java`.
- `reportGenerator/` → `ExcelService.java`, `PdfService.java`, `ReportService.java` (interfaz).
- `databasefunctions/` → `MaterializedViewRefreshService.java` (refresca rankings cada 5 min).

### repositories/

- `admin/` → `DashboardRepository.java` (total_sales con window functions sobre `view_of_dashboard`), `SalesItemViewRepository` no existe: es `SaleItemViewRepository.java`, `ClientsSummaryViewRepository.java` (query sobre `clients_summary`), `CuponRepository.java`, `ProductCuponToAClientRepository.java`, etc.
- `general/` → `SaleRepository.java`, `SaleItemRepository.java`, `ShSaleRepository.java`, `ProductRepository.java`, etc.
- `client/` → `ClientRepository.java`, `PaymentCardRepository.java`.
- `projection/` → interfaces de proyeccion (`ClientSummaryProjection`, `DashboardProjection`, `SaleItemViewProjection`, `ShSaleHistoryProjection`, etc.).
- `reportGenerator/` → `ReportService.java`.

Nota: OJO con nombre real `SaleItemViewRepository` y `SaleItemViewService` (NO SalesItemView...).

### DTOs/ (records)

- `Admin/` → `DashboardDTO` (totalSales, totalProducts, totalClients, monthlyData, showLatestSales), `CuponRequestDTO`, `CuponResponseDTO`, `CuponSendRequestDTO`, etc.
- `General/` → `PurchaseResponseDTO` (saleId, saleIds, clientId, cuponCode, cupon, originalTotal, discountApplied, totalAmount, createdAt, items), `PurchaseRequestDTO`, `ShPurchaseResponseDTO`.
- `Auth/` → login/register req-resp.
- `Client/` → `ClientResponseDTO`, payment card DTOs.

### entities/

- `admin/` → `UserAdmin.java`, `Cupon.java`, `ProductCuponApplied.java`, `ProductCuponToAClient.java`, etc.
- `client/` → `UserClient.java`, `PaymentCard.java`.
- `general/` → `Product.java`, `Sale.java` (totalAmount), `SalesItem.java`, `SecondHandProduct.java`, `ShSale.java`, `ShSalesItem.java`.

### config/ + security/ + otros

- `config/` → `CacheConstants.java`, `SecurityConfig.java`, `CloudinaryConfig.java`, `SwaggerConfig.java`.
- `security/` → `AuthenticatedUser.java` (principal), `JwtService.java`, `JwtAuthenticationFilter.java`.
- `exceptions/` → `GlobalExceptionHandler.java`, `ResourceNotFoundException.java`.
- Migraciones SQL: `src/main/resources/db/migration/V1__schema_inicial.sql` (tablas+vistas), `V2__add_color_config.sql`, `V3__rankings_materialized_views.sql`.
- IDs de respuesta de ventas: `SaleResponseDato.java` y `SaleItemResponseDTO.java`.

## Git flow

- Siempre ejecutar los tests antes de commitear. Requieren **Java 25+** (el enforcer del `pom.xml` exige `[25,)`); local hay JDK 26 (`mvn -B test`). El CI los corre con temurin 25 + Postgres 16 de servicio.
- Si los tests pasan: hacer commit en `main` y hacer `git push origin main` automáticamente.
- No commitear ni pushear si los tests fallan.
- Push a `main` dispara **CI/CD**: GitHub Actions testea → construye la imagen Docker → GHCR → SSH a la VM (EC2) (`git reset --hard origin/main` + `docker compose pull/up`). `render.yaml` es la **segunda opción** (deploy en nube Render, ya no activo).
- `graphify-out/`, `docs/` y `API_FRONTEND_CONSUMPTION.txt` están en `.gitignore`: NO se commitean. `diagrams/` SÍ se versiona.
