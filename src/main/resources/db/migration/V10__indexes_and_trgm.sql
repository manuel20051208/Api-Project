-- ========================================================
-- V10: indices de rendimiento + busqueda difusa (pg_trgm)
-- ========================================================
-- Idempotente: CREATE EXTENSION/INDEX IF NOT EXISTS (se puede re-ejecutar sin error).
-- Solo indices: NO crea constraints UNIQUE ni toca FKs.
--
-- Nombres reales verificados contra V1..V9:
--   * sales.user_id / sales.created_at        (no existe user_admin_id)
--   * sale_items.sale_id                      (tabla sale_items, no sales_items)
--   * sh_sales.user_id / sh_sales.created_at  (no existe hora)
--   * sh_sales_item.sh_sale_id                (tabla sh_sales_item, no sh_sales_items)
--   * cupons.user_id / cupons.cupon_code      (no existe owner_id)
--   * product_cupon_to_a_client.client_id     (tabla en singular)
--   * sh_cupons_used_by_clients.cupon_id / client_user (no existe client_id)
--
-- Nota: los CREATE INDEX van sin CONCURRENTLY para poder correr dentro de la
-- transaccion de Flyway; en esta BD (pocos datos) el bloqueo de escritura es breve.
-- ========================================================

-- ========================================================
-- 0. EXTENSION pg_trgm (indices GIN para LIKE/ILIKE '%...%')
-- ========================================================

CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- ========================================================
-- 1. INDICES COMPUESTOS POR ADMIN / FECHA
-- ========================================================

-- Ventas de un admin ordenadas por fecha (dashboard, view_of_sales, reportes).
-- Sustituye en la practica al indice simple idx_sales_user_id para ese patron.
CREATE INDEX IF NOT EXISTS idx_sales_user_created_at
    ON sales (user_id, created_at);

-- Ventas de segunda mano de un admin ordenadas por fecha (findAdminHistory).
CREATE INDEX IF NOT EXISTS idx_sh_sales_user_created_at
    ON sh_sales (user_id, created_at);

-- Cupones de un admin por codigo (listado del admin + lookup por duenio/codigo).
-- OJO: las busquedas por codigo usan LOWER(cupon_code) = LOWER(:code), que ya
-- resuelve idx_cupons_code (btree sobre LOWER(cupon_code)) desde V1.
CREATE INDEX IF NOT EXISTS idx_cupons_user_code
    ON cupons (user_id, cupon_code);

-- ========================================================
-- 2. INDICES PEDIDOS QUE YA EXISTIAN (no-op, se dejan documentados)
-- ========================================================
-- Mismo nombre que en V1/V7: IF NOT EXISTS evita duplicarlos.

-- Items de una venta (FK sale_id).
CREATE INDEX IF NOT EXISTS idx_sale_items_sale_id
    ON sale_items (sale_id);

-- Items de una venta de segunda mano (FK sh_sale_id).
CREATE INDEX IF NOT EXISTS idx_sh_sale_items_sale_id
    ON sh_sales_item (sh_sale_id);

-- Asignaciones de cupon normal por cliente.
CREATE INDEX IF NOT EXISTS idx_pcac_client
    ON product_cupon_to_a_client (client_id);

-- Contador de usos de cupon SH por (cupon, cliente). Ya creado en V7.
CREATE INDEX IF NOT EXISTS idx_sh_cupons_used_cupon_client
    ON sh_cupons_used_by_clients (cupon_id, client_user);

-- ========================================================
-- 3. INDICES GIN TRIGRAM (LIKE/ILIKE '%texto%')
-- ========================================================
-- Los indices van sobre las TABLAS BASE, no sobre las vistas
-- (clients_summary / view_of_sales solo se benefician via clients/products).

-- 3.1 clients.full_name
--   * ILIKE '%...%' desde la vista clients_summary (ClientsSummaryViewRepository).
--   * LOWER(full_name) LIKE LOWER('%...%') de ClientRepository: necesita el indice
--     sobre la expresion LOWER(...) porque la columna va envuelta en lower().
CREATE INDEX IF NOT EXISTS idx_clients_full_name_trgm
    ON clients USING GIN (full_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_clients_lower_full_name_trgm
    ON clients USING GIN (LOWER(full_name) gin_trgm_ops);

-- 3.2 clients.email (mismo par de patrones que full_name)
CREATE INDEX IF NOT EXISTS idx_clients_email_trgm
    ON clients USING GIN (email gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_clients_lower_email_trgm
    ON clients USING GIN (LOWER(email) gin_trgm_ops);

-- 3.3 secondhand_product.name
--   * findActiveCatalogCards / findAdminCards: LOWER(p.name) LIKE '%' || LOWER(:search) || '%'
CREATE INDEX IF NOT EXISTS idx_secondhand_product_lower_name_trgm
    ON secondhand_product USING GIN (LOWER(name) gin_trgm_ops);

-- 3.4 services_offered.name_of_service
--   * findCatalogByOwner: LOWER(name_of_service) LIKE '%' || LOWER(:search) || '%'
CREATE INDEX IF NOT EXISTS idx_services_offered_lower_name_trgm
    ON services_offered USING GIN (LOWER(name_of_service) gin_trgm_ops);

-- ========================================================
-- 4. NO SE INDEXA (justificacion)
-- ========================================================
-- * products.name / products.category: las queries del codigo usan igualdad
--   (LOWER(p.name) = LOWER(:name), LOWER(p.category) = LOWER(:category)) y ya
--   existen idx_products_lower_name / idx_products_lower_category en V1.
--   No hay LIKE '%...%' sobre products en los repositorios.
-- * cupons.cupon_code / secondhand_cupons.sh_cupon_code / services_cupon.service_cupon_code:
--   se buscan con igualdad case-insensitive (LOWER(code) = LOWER(:code)), cubierto
--   por los indices btree sobre LOWER(...) de V1.
