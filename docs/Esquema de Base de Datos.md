# Esquema de Base de Datos

> Nota: [[PROJECT_STRUCTURE]] · Entidades en [[Arquitectura del Código#entities/]] · Consumida por [[Endpoints API]]

PostgreSQL. Archivo de referencia: `src/main/resources/db/schema-postgres.sql` (tablas + índices + vistas). Modelo DBML: `src/main/resources/db/schema.dbml`. Configuración del datasource en [[Configuración]].

## Tablas

| Tabla | Columnas principales | Restricciones / FKs |
|---|---|---|
| `users` | `id BIGSERIAL PK`, `password`, `phone BIGINT`, `full_name`, `email`, `business_name`, `profile_photo`, `profile_photo_url` | `UNIQUE(email)` — admin |
| `clients` | `id BIGSERIAL PK`, `full_name`, `email`, `username`, `password`, `phone`, `address`, `created_at`, `photo` | `UNIQUE(username)`, `UNIQUE(email)` |
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

## Índices
- `products`: `(id_users)`, `(active, name)`, `LOWER(name)`, `LOWER(category)`.
- `product_image`: `(product_id, display_order)`.
- `sales`: `(client_id)`, `(user_id)`, `(created_at)`.
- `sale_items`: `(sale_id)`, `(client_id, date DESC, id DESC)`, `(product_id, date DESC, id DESC)`.
- `clients`: `LOWER(full_name)`, `LOWER(email)`.
- `payment_cards`: `(client_id, active)`.

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
