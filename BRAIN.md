# BRAIN — Cerebro del Proyecto ApiProject

> Este archivo es la fuente de conocimiento sobre **cómo está estructurado y cómo funciona** el proyecto.
> **Protocolo del agente**: antes de buscar en el código o responder preguntas sobre este repo, consulta este archivo. Para preguntas profundas usa también el grafo: `graphify query "<pregunta>"`.

---

## 1. Qué es

API REST de **inventario y ventas** (`ApiJuegoInventario`) para una tienda con:
- **Dos roles**: `ADMIN` (dueño de tienda) y `CLIENT` (comprador).
- Catálogo de productos con **imágenes en Cloudinary**.
- Compra con **tarjetas simuladas**.
- **Dashboard de métricas** y **reportes Excel/PDF** por admin.
- Autenticación **JWT** + **OAuth2 con Google**.
- Notificaciones en tiempo real por **SSE**.

## 2. Stack técnico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.5 (Web, Data JPA, Security, OAuth2, Actuator) |
| Base de datos | PostgreSQL 16 (dialecto PostgreSQL, vistas SQL) |
| Seguridad | JWT (`jjwt 0.13.0`) + OAuth2 Google (dos registrations: `google-admin`, `google-client`) |
| Docs | springdoc OpenAPI → Swagger UI en `/swagger-ui.html` |
| Imágenes | Cloudinary (`cloudinary-http45`) |
| Reportes | Apache POI (Excel) + OpenPDF (PDF) vía `ReportServiceFactory` |
| Extras | Lombok, spring-dotenv (carga `.env`), caché (`@EnableCaching`) |
| Build | Maven (enforcer: Java 21, Maven >= 3.8; perfil `security` con dependency-check) |
| Deploy | Docker (`docker-compose` + multi-stage Dockerfile) |

## 3. Estructura de carpetas

```
ApiProject/
├── src/main/java/com/apiproject/
│   ├── ApiProjectApplication.java     # Entry point
│   ├── config/        # SecurityConfig, CloudinaryConfig, SwaggerConfig, CacheConstants
│   ├── controllers/   # admin/ · client/ · general/
│   ├── services/      # admin/ · client/ · general/ · reportGenerator/
│   ├── repositories/  # admin/ · client/ · general/ · projection/ · reportGenerator/
│   ├── entities/      # admin/ · client/ · general/
│   ├── DTOs/          # Auth/ · Admin/ · Client/ · General/
│   ├── enums/         # Status, FileTypes
│   ├── security/      # JwtService, JwtAuthenticationFilter, AuthenticatedUser, OAuth2SuccessHandler
│   └── exceptions/    # GlobalExceptionHandler, ApiError, ResourceNotFoundException
├── src/main/resources/
│   ├── application.properties         # Activa perfil dev
│   ├── application-dev.properties     # Datasource, JWT, multipart, Cloudinary, OAuth2, Swagger
│   └── db/            # schema-postgres.sql, schema.dbml
├── src/test/java/     # ApiProjectApplicationTests, JwtServiceTest
├── uploads/           # perfiles/ y products/ (local, antes de Cloudinary)
├── graphify-out/      # Knowledge graph (graph.json, GRAPH_REPORT.md)
├── docs/              # Notas Obsidian conectadas ([[]]) — documentación visual
├── docker-compose.yml # Postgres (5433) + app (8080)
├── Dockerfile         # maven build → temurin jre
├── pom.xml            # Dependencias y build
└── AGENTS.md          # Instrucciones para agentes (este protocolo)
```

> Detalle por nota Obsidian en `docs/`: [[Arquitectura del Código]], [[Endpoints API]], [[Esquema de Base de Datos]], [[Configuración]].

## 4. Cómo funciona la aplicación (flujo)

1. **Petición** llega a un `@RestController` (`controllers/`), ruta base `/api/...`.
2. **SecurityConfig** filtra: permite públicos (login/register/Swagger/imágenes), exige `Authorization: Bearer <jwt>` para el resto y valida rol (`CLIENT`/`ADMIN`) por ruta.
3. **JwtAuthenticationFilter** parsea el token → `AuthenticatedUser` se inyecta con `@AuthenticationPrincipal authenticatedUser.id()`.
4. El **controller** delega en un **service** (`services/`), que contiene la lógica de negocio.
5. El **service** usa **repositories** (Spring Data JPA). Las consultas de lectura de ventas/resúmenes van contra **vistas SQL** (ver §7.3) mediante **proyecciones** (`repositories/projection/`).
6. Se devuelve un **DTO** (`DTOs/`) — nunca entidades directamente (salvo excepciones legadas como `saveProduct`).
7. Errores controlados por `GlobalExceptionHandler` → `ApiError` (401/402/403/404/5xx).
8. **Imágenes**: suben local a `uploads/` y se publican en Cloudinary (`ProductImageService`).
9. **Reportes**: `ReportServiceFactory` elige `ExcelService` o `PdfService` según `FileTypes`.

## 5. Capas de código (detalle)

### config/
| Clase | Función |
|---|---|
| `SecurityConfig` | Cadena de filtros, CORS (solo `http://localhost:3000`), reglas por ruta/rol, OAuth2 |
| `CloudinaryConfig` | Bean Cloudinary |
| `SwaggerConfig` | OpenAPI |
| `CacheConstants` | Constantes de caché |

### controllers/
- **admin/**: `DashboardController` (métricas + excel/pdf), `UserController` (login/register/admin/modify/upload-profile), `ProductImageController` (upload/list/delete imágenes), `NotificationController` (SSE stream), `ClientsSummaryViewController`, `SalesItemViewController`.
- **client/**: `ClientControllers` (login/register/modify/fotos/tarjetas/user-data/history).
- **general/**: `ProductController` (catálogo + CRUD), `SaleController` (purchase, refresh).

### services/
- **admin/**: `UserService`, `DashboardService`, `ProductImageService`, `NotificationService`, `ClientsSummaryViewService`, `SaleItemViewService`, `ReportServiceFactory`.
- **client/**: `ClientService`.
- **general/**: `ProductService`, `SaleService`, `SaleItemService`.
- **reportGenerator/**: `ReportService` (interfaz), `ExcelService`, `PdfService`.

### entities/
- **admin/**: `UserAdmin` → tabla `users`; `ProductImage` → `product_image`; `ReportDashboard` (vista `report_view_dashboard`).
- **client/**: `UserClient` → `clients`; `PaymentCard` → `payment_cards`.
- **general/**: `Product` → `products`; `Sale` → `sales`; `SalesItem` → `sale_items`.

### repositories/ · projection/
Repos estándar JPA por entidad. Proyecciones para vistas: `ClientLoginProjection`, `ClientSummaryProjection`, `ClientHistoryProjection`, `DashboardProjection`, `ReportDashboardProjection`, `PaymentCardDetailsProjection`, `SaleItemViewProjection`.

### security/
- `JwtService`: firma/valida JWT (clave `JWT_SECRET_KEY`, expiración `JWT_EXPIRATION_MS`).
- `JwtAuthenticationFilter`: filtra por Bearer token.
- `AuthenticatedUser`: principal con `id()`, `email()`, roles.
- `OAuth2SuccessHandler`: éxito del login Google.

### enums/
- `Status`: estados de venta/ítem.
- `FileTypes`: `EXCEL`, `PDF`.

### exceptions/
- `GlobalExceptionHandler`, `ApiError`, `ResourceNotFoundException`.

## 6. Endpoints (resumen operativo)

Base `/api` (salvo dashboard). Auth: `Authorization: Bearer <token>`.

| Endpoint | Rol | Uso |
|---|---|---|
| `POST /user/login` | Público | Login admin (`{email, password}`) |
| `POST /user/register` | Público | Registro admin |
| `POST /client/login` | Público | Login cliente |
| `POST /client/register` | Público | Registro cliente |
| `GET /client/{adminId}/admin` | Público | Datos de tienda del admin |
| `GET /user/admin` | ADMIN | Perfil admin |
| `PATCH /user/modify` · `PATCH /user/upload-profile` | ADMIN | Editar / foto |
| `POST /client/payment-cards` | CLIENT | Crear tarjeta |
| `GET /client/payment-cards` | CLIENT | Listar tarjetas |
| `PATCH /client/payment-cards/{cardId}/status?active=` | CLIENT | Activar/desactivar |
| `PATCH /client/modify` · `/upload-profile` · `GET /profile-photo` | CLIENT | Datos / foto |
| `GET /client/user-data` · `GET /client/user-payments` | CLIENT | Datos / historial |
| `GET /product/search*` · `GET /product/activeProducts` | Auth/ADMIN | Catálogo |
| `POST /product/saveProduct` · `POST /product/deleteSafe` · `PUT /product/update/{id}` | ADMIN | CRUD |
| `GET /product/{productId}/admin` | Auth | Admin dueño |
| `POST /product-images/upload/{productId}` · `DELETE /product-images/{imageId}` | ADMIN | Imágenes |
| `GET /product-images/{productId}` | Público | Imágenes de producto |
| `POST /sale/purchase` | CLIENT | Compra (402 si sin tarjeta activa) |
| `POST /sale/sale/refresh` | ADMIN | Recalcular venta (legado) |
| `GET /dashboard-controller/get-data-dashboard` | ADMIN | Métricas |
| `GET /dashboard-controller/excel` · `/pdf` | ADMIN | Reportes |
| `GET /sales-items/show-with-no-restrinction` · `/show-with-limits` · `/client` · `/product/` | ADMIN | Historial ventas |
| `GET /client-show-summary/getNames/{userId}` · `/name/{userId}` · `/email/{userId}` | ADMIN | Resumen clientes |
| `GET /notification/stream` | ADMIN | SSE notificaciones |

### Payloads clave (resumen)

**Login/Registro** → response con `{ id, fullName, email, phone, businessName|address, photo, accountType, token, message }`.
**Tarjeta** request: `{ cardHolderName, brand, lastFour, active }` → response `{ id, clientId, cardHolderName, brand, lastFour, active, createdAt }`.
**Compra** request:
```json
{ "clientId": 1, "items": [ { "productId": 10, "quantity": 2 } ] }
```
response: `{ saleId, saleIds, clientId, totalAmount, createdAt, items:[{ productId, productName, quantity, unitPrice, subtotal }] }`.
**Producto** response: `{ id, name, price, stock, category, description, active, userAdminId, images:[{ id, fileName, displayOrder, url }] }`.
**Dashboard**: `{ totalSales, totalProducts, totalClients, monthlyData:[{ monthName, monthlyTotal, numberOfProducts, countClients }], showLatestSales }`.

> Payloads completos de cada endpoint en `docs/Endpoints API.md` y `API_FRONTEND_CONSUMPTION.txt`.

### Reglas de seguridad (resumen)
- Públicos: Swagger, `/actuator/health`, `/oauth2/**`, login/register, `GET /api/product-images/**`, `GET /api/user/{id}/admin`.
- `/api/client/**` → CLIENT. `/api/user/**`, `/dashboard-controller/**`, `/api/sales-items/**`, `/api/client-show-summary/**` → ADMIN.
- Catálogo de lectura → autenticado. Escritura de productos/imágenes → ADMIN.
- `POST /api/sale/purchase` → CLIENT; resto `/api/sale/**` → ADMIN.
- CORS: solo `http://localhost:3000`.

## 7. Base de datos (PostgreSQL)

Esquema en `src/main/resources/db/schema-postgres.sql` (idempotente). Modelo visual en `schema.dbml`.

### 7.1 Tablas
| Tabla | Notas |
|---|---|
| `users` | Admin. `UNIQUE(email)` |
| `clients` | Cliente. `UNIQUE(username)`, `UNIQUE(email)` |
| `payment_cards` | FK `clients` CASCADE; `CHECK last_four ~ '^[0-9]{4}$'` |
| `products` | FK `users` SET NULL; `active` bool |
| `product_image` | FK `products` CASCADE; `UNIQUE(product_id, display_order)` |
| `sales` | FKs `clients`/`users` SET NULL; `total_amount NUMERIC(10,2)` |
| `sale_items` | FKs `sales` CASCADE; `state IN ('COMPLETED','HANGING','CANCELLED')`, `quantity > 0` |

### 7.2 Relaciones
- `users` 1—N `products`; `products` 1—N `product_image` (cascade) y `sale_items`
- `clients` 1—N `payment_cards` (cascade), `sales`, `sale_items`
- `sales` 1—N `sale_items` (cascade)

### 7.3 Vistas SQL (claves del "cómo funciona")
| Vista | Para qué |
|---|---|
| `view_of_sales` | Historial de ventas (`sales-items/*`) |
| `view_of_dashboard` | Métricas mensuales del año en curso (dashboard) |
| `clients_summary` | Resumen de clientes (`client-show-summary/*`, dashboard) |
| `view_of_client_history` | Historial de compras del cliente (`/client/user-payments`) |
| `report_view_dashboard` | Datos de reportes Excel/PDF |

Índices optimizados: `idx_sale_items_client_date` y `idx_sale_items_product_date` para paginación por cliente/producto + fecha.

## 8. Configuración

- `application.properties` → `spring.profiles.active=dev`.
- `application-dev.properties`: datasource `jdbc:postgresql://localhost:5432/apiproject`, JPA show-sql + batch 50, multipart 30MB, OAuth2 Google (admin/client), Swagger on.
- Variables `.env` (vía spring-dotenv): `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET_KEY`, `JWT_EXPIRATION_MS`, `CLOUDINARY_CLOUD_NAME/API_KEY/API_SECRET`, `BASIC_AUTH_USERNAME/PASSWORD`, `GOOGLE_CLIENT_ID/SECRET`.

## 9. Docker

- **docker-compose**: `db` postgres:16-alpine en `5433:5432` + `app` en `8080` (perfil dev, espera healthcheck de la DB, env desde `.env`).
- **Dockerfile**: build `maven:3.9-eclipse-temurin-21` (`mvn package -DskipTests`) → runtime `eclipse-temurin:21-jre` (`java -jar app.jar`).

## 10. Tests

- `ApiProjectApplicationTests` — smoke de contexto.
- `security/JwtServiceTest` — pruebas del JWT.

## 11. Convenciones y gotchas

- **DTOs en responses**, pero `saveProduct`, `deleteSafe`, `modify` y `sale/refresh` aceptan **entidades** directamente (legado; `sale/refresh` es un body combinado `Product`+`UserClient`+`amount`).
- `deleteSafe` es **borrado suave** (pone `active=false`).
- `PurchaseRequestDTO` tiene `clientId`, `userId` (lista, sin uso actual) e `items`.
- Puertos: DB host `5433` (mapeado a `5432` en el contenedor), API `8080`.
- CORS solo para localhost:3000.
- Mantén el grafo actualizado tras cambios de código: `graphify update .`

## 12. Referencias

- Swagger: `http://localhost:8080/swagger-ui.html` · Health: `/actuator/health`
- Notas Obsidian: `docs/` · Grafo: `graphify-out/` (`graphify query/path/explain`)
- Frontend: `API_FRONTEND_CONSUMPTION.txt`
