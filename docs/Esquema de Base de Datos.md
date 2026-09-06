# Esquema de Base de Datos

> Nota: [[PROJECT_STRUCTURE]] · Entidades en [[Arquitectura del Código#entities/]] · Consumida por [[Endpoints API]]

PostgreSQL. Archivo de referencia: `src/main/resources/db/schema-postgres.sql` (tablas + índices + vistas). Modelo DBML: `src/main/resources/db/schema.dbml`. Configuración del datasource en [[Configuración]].

## Tablas

| Tabla | Columnas principales | Restricciones / FKs |
|---|---|---|
| `users` | `id BIGSERIAL PK`, `password`, `phone BIGINT`, `full_name`, `email`, `business_name`, `profile_photo`, `profile_photo_url`, `color_config` | `UNIQUE(email)` — admin |
| `clients` | `id BIGSERIAL PK`, `full_name`, `email`, `username`, `password`, `phone`, `address`, `created_at`, `photo`, `color_config` | `UNIQUE(username)`, `UNIQUE(email)` |
| `payment_cards` | `id BIGSERIAL PK`, `client_id`, `card_holder_name`, `brand`, `last_four`, `active`, `created_at` | FK `client_id → clients(id) ON DELETE CASCADE`; `CHECK last_four ~ '^[0-9]{4}$'` |
| `products` | `id BIGSERIAL PK`, `name`, `price DOUBLE`, `stock`, `category`, `description`, `active`, `id_users` | FK `id_users → users(id) ON DELETE SET NULL` |
| `product_image` | `id BIGSERIAL PK`, `file_name`, `file_path`, `display_order`, `product_id` | FK `product_id → products(id) ON DELETE CASCADE`; `UNIQUE(product_id, display_order)` |
| `sales` | `id BIGSERIAL PK`, `client_id`, `user_id`, `total_amount NUMERIC(10,2)`, `created_at` | FK `client_id → clients(id)`, FK `user_id → users(id)`, ambos `SET NULL` |
| `sale_items` | `id BIGSERIAL PK`, `sale_id`, `product_id`, `quantity`, `client_id`, `state`, `date` | FKs `sale_id → sales(id) CASCADE`, `product_id → products(id)`, `client_id → clients(id)`; `CHECK state IN ('COMPLETED','HANGING','CANCELLED')`, `CHECK quantity > 0` |

Relaciones principales:
- `users` (admin) 1 — N `products` (`id_users`)
- `clients` 1 — N `payment_cards` (cascade)
- `clients` 1 — N `sales` y `sale_items`
- `products` 1 — N `product_image` (cascade) y `sale_items`
- `sales` 1 — N `sale_items` (cascade)

## Tablas nuevas — cupones, segunda mano y servicios

Agregadas en `schema-postgres.sql` (secciones 4–7). Todas las consultas JPA sobre estas tablas usan *native query* (→ [[Arquitectura del Código#repositories/]]).

### Cupones para productos normales
| Tabla | Columnas principales | Restricciones / FKs |
|---|---|---|
| `cupons` | `id BIGSERIAL PK`, `cupon_code VARCHAR(15)`, `cupon_date_limit TIMESTAMP`, `discount DOUBLE`, `quantity INTEGER`, `user_id` | FK `user_id → users(id) CASCADE`; `quantity NULL = ilimitado`; índice `LOWER(cupon_code)` |
| `product_cupons_applied` | `id BIGSERIAL PK`, `cupons_id`, `product_id` | N:M cupón↔producto; FKs CASCADE; `UNIQUE(cupons_id, product_id)` |
| `cupons_used_by_clients` | `id BIGSERIAL PK`, `client_user`, `sale_id`, `cupon_id`, `created_at` | FKs `clients`/`sales`/`cupons`; 1 fila por compra con cupón |

### Segunda mano
| Tabla | Columnas principales | Restricciones / FKs |
|---|---|---|
| `secondhand_product` | como `products` + `time_of_use TIMESTAMP NOT NULL`, `level_of_secondhand_product BIGINT NOT NULL`, `id_users NOT NULL` | FK `users CASCADE`; borrado suave vía `active=false` |
| `secondhand_product_images` | `file_name`, `file_path`, `url TEXT`, `display_order`, `product_id`, `user_id` | FK `secondhand_product CASCADE`; `UNIQUE(product_id, display_order)` |
| `secondhand_cupons` | igual que `cupons` pero código en `sh_cupon_code` | aplica solo a productos SH vía tabla de enlace |
| `secondhand_product_cupons_applied` | `sh_cupons_id`, `sh_product_id` | `UNIQUE(sh_cupons_id, sh_product_id)` |
| `sh_sales` / `sh_sales_item` | espejo de `sales` / `sale_items` | una venta por producto; items `COMPLETED` |
| `sh_cupons_used_by_clients` | igual estructura que `cupons_used_by_clients` | ⚠️ **FKs corregidas vs DBML**: `sale_id → sh_sales(id)`, `cupon_id → secondhand_cupons(id)` |

### Servicios ofrecidos
| Tabla | Columnas principales | Restricciones / FKs |
|---|---|---|
| `services_offered` | `user_id`, `name_of_service VARCHAR(40)`, `value_of_service DOUBLE`, `description_of_service VARCHAR(255)` | FK `users CASCADE`; todo `NOT NULL` |
| `services_cupon` | `service_cupon_code VARCHAR(15)`, `cupon_date_limit`, `discount`, `quantity`, `user_id` | ⚠️ columna renombrada vs DBML (`sh_cupon_code` → `service_cupon_code`) |

Relaciones nuevas:
- `users` 1 — N `cupons` / `secondhand_cupons` / `services_offered` / `services_cupon`
- `cupons` 1 — N `product_cupons_applied` N — 1 `products` (ídem SH con sus tablas)
- `clients` 1 — N `cupons_used_by_clients` / `sh_cupons_used_by_clients`
- `sh_sales` 1 — N `sh_sales_item` (cascade)

## Colores de interfaz (`color_config`)

Tanto `users` (admin) como `clients` tienen la columna `color_config` mapeada al enum Java `ColorTypes` en [[Arquitectura del Código#enums/]]. Es un **tipo enum nativo de Postgres** (`NAMED_ENUM` en Hibernate 6 / Spring Boot 3): con `@JdbcTypeCode(SqlTypes.NAMED_ENUM)` + `@Enumerated(EnumType.STRING)`.

Valores del enum `ColorTypes`: `VERDE`, `AZUL`, `VIOLETA`, `AMBAR`, `ROSA`.

## Vistas materializadas — rankings

Se agregan dos **materialized views** para el top de ventas por admin, refrescadas en segundo plano cada 5 min (ver `MaterializedViewRefreshService`):

| Vista | Columnas | Propósito |
|---|---|---|
| `three_best_clients` | `client_id`, `user_id`, `name`, `amount_of_buys` | Top 3 clientes por nº de compras de un admin |
| `three_best_products` | `product_id`, `users_id`, `name`, `amount_of_buys` | Top 3 productos por nº de compras de un admin |

Se refrescan con `REFRESH MATERIALIZED VIEW CONCURRENTLY` (un `execute` por vista, no dos en el mismo — lanzaría excepción). Consultadas vía **native query** en `RankingsRepository` (`findAllByUser` / `findAllByUserId`) → [[Endpoints API#Rankings (ADMIN)]].

## Índices- `products`: `(id_users)`, `(active, name)`, `LOWER(name)`, `LOWER(category)`.
- `product_image`: `(product_id, display_order)`.
- `sales`: `(client_id)`, `(user_id)`, `(created_at)`.
- `sale_items`: `(sale_id)`, `(client_id, date DESC, id DESC)`, `(product_id, date DESC, id DESC)`.
- `clients`: `LOWER(full_name)`, `LOWER(email)`.
- `payment_cards`: `(client_id, active)`.
- Nuevos: `cupons(LOWER(cupon_code))`, `cupons(user_id)`, `product_cupons_applied(product_id)`, `cupons_used_by_clients(cupon_id)`, `secondhand_product(id_users, level_of_secondhand_product)`, `secondhand_product(active, name)`, `secondhand_cupons(LOWER(sh_cupon_code))`, `spca(sh_product_id)`, `sh_sales(client_id/user_id/created_at)`, `sh_sale_items(sale_id, client_date, product_date)`, `services_offered(user_id)`, `services_cupon(LOWER(service_cupon_code))`.

## Vistas

| Vista | Propósito | Usada en |
|---|---|---|
| `view_of_sales` | Ventas detalladas (usuario, cliente, producto, cantidad, total calculado, estado, fecha) | [[Endpoints API#Historial de ventas (ADMIN)]] |
| `view_of_dashboard` | Métricas mensuales del año actual por admin: `month_number`, `month_name`, `monthly_total`, `number_of_products`, `count_clients` | [[Endpoints API#Panel de administración (ADMIN)]] |
| `clients_summary` | Resumen por cliente: `total_quantity`, `total_spent`, `latest_sale` | [[Endpoints API#Resumen de clientes (ADMIN)]] |
| `view_of_client_history` | Historial de compras por cliente (nombre, email, dirección, producto, cantidad, total, estado, fecha) | [[Endpoints API#GET /client/user-payments — Historial de compras del cliente (CLIENT)]] |
| `report_view_dashboard` | Datos para reportes Excel/PDF del dashboard (cliente, producto, cantidad, total, estado, fecha, monto actual) | [[Endpoints API#GET /dashboard-controller/excel — Reporte en Excel (adjunto .xlsx)]] |

Los índices `idx_sale_items_client_date` y `idx_sale_items_product_date` están pensados para que la paginación por cliente + fecha sea rápida; las vistas los aprovechan directamente.

## Nota
El esquema se crea vía `db/schema-postgres.sql` (idempotente: `CREATE ... IF NOT EXISTS` + limpieza previa de vistas). También hay una versión visual en `db/schema.dbml` (DBDiagram).
