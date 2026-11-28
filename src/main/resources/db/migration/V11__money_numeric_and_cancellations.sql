-- ========================================================
-- V11: dinero en NUMERIC + precio historico por item + cancelaciones
-- ========================================================
-- 1) Precios y descuentos dejan DOUBLE PRECISION (errores de redondeo) -> NUMERIC.
-- 2) Cada item de venta guarda su precio unitario y su parte del descuento del cupon
--    AL MOMENTO DE LA VENTA. Antes las vistas usaban el precio ACTUAL del producto:
--    cambiar un precio reescribia todo el historial y las metricas.
-- 3) Cancelacion total o por item: estado de la venta, monto reembolsado y fecha de
--    cancelacion por item. Las metricas ignoran lo cancelado.
-- Tambien corrige: clients_summary sumaba sales.discount una vez POR ITEM, y
-- report_view_dashboard unia clientes con COALESCE(si.client_id, si.client_id).
--
-- Las vistas dependen de las columnas que cambian de tipo: se borran y se recrean.
-- ========================================================

-- --------------------------------------------------------
-- 0. Quitar vistas que dependen de las columnas a modificar
-- --------------------------------------------------------
DROP MATERIALIZED VIEW IF EXISTS three_best_clients;
DROP MATERIALIZED VIEW IF EXISTS three_best_products;
DROP VIEW IF EXISTS view_of_sales;
DROP VIEW IF EXISTS report_view_dashboard;
DROP VIEW IF EXISTS clients_summary;
DROP VIEW IF EXISTS view_of_client_history;
DROP VIEW IF EXISTS view_of_dashboard;

-- --------------------------------------------------------
-- 1. Dinero y porcentajes a NUMERIC
-- --------------------------------------------------------
ALTER TABLE products           ALTER COLUMN price            TYPE NUMERIC(12, 2) USING ROUND(price::NUMERIC, 2);
ALTER TABLE secondhand_product ALTER COLUMN price            TYPE NUMERIC(12, 2) USING ROUND(price::NUMERIC, 2);
ALTER TABLE services_offered   ALTER COLUMN value_of_service TYPE NUMERIC(12, 2) USING ROUND(value_of_service::NUMERIC, 2);

-- discount de los cupones es un PORCENTAJE (0..100)
ALTER TABLE cupons            ALTER COLUMN discount TYPE NUMERIC(5, 2) USING ROUND(discount::NUMERIC, 2);
ALTER TABLE secondhand_cupons ALTER COLUMN discount TYPE NUMERIC(5, 2) USING ROUND(discount::NUMERIC, 2);
ALTER TABLE services_cupon    ALTER COLUMN discount TYPE NUMERIC(5, 2) USING ROUND(discount::NUMERIC, 2);

-- --------------------------------------------------------
-- 2. Precio historico + descuento por item + cancelacion por item
-- --------------------------------------------------------
ALTER TABLE sale_items
    ADD COLUMN IF NOT EXISTS unit_price   NUMERIC(12, 2),
    ADD COLUMN IF NOT EXISTS discount     NUMERIC(12, 2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMP;

ALTER TABLE sh_sales_item
    ADD COLUMN IF NOT EXISTS unit_price   NUMERIC(12, 2),
    ADD COLUMN IF NOT EXISTS discount     NUMERIC(12, 2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMP;

-- Ventas viejas: el mejor dato disponible es el precio actual del producto.
UPDATE sale_items si
SET unit_price = p.price
FROM products p
WHERE p.id = si.product_id AND si.unit_price IS NULL;

UPDATE sh_sales_item si
SET unit_price = p.price
FROM secondhand_product p
WHERE p.id = si.sh_product_id AND si.unit_price IS NULL;

UPDATE sale_items SET unit_price = 0 WHERE unit_price IS NULL;
UPDATE sh_sales_item SET unit_price = 0 WHERE unit_price IS NULL;

ALTER TABLE sale_items    ALTER COLUMN unit_price SET NOT NULL;
ALTER TABLE sh_sales_item ALTER COLUMN unit_price SET NOT NULL;

-- Repartir el descuento de cada venta vieja entre sus items, proporcional al subtotal
-- (el ultimo item absorbe el redondeo para que la suma cuadre exacto).
WITH lines AS (
    SELECT si.id, si.sale_id, s.discount AS sale_discount,
           si.quantity * si.unit_price AS line_total,
           SUM(si.quantity * si.unit_price) OVER (PARTITION BY si.sale_id) AS sale_subtotal,
           ROW_NUMBER() OVER (PARTITION BY si.sale_id ORDER BY si.id DESC) AS rn_desc
    FROM sale_items si
    JOIN sales s ON s.id = si.sale_id
    WHERE s.discount > 0
), shares AS (
    SELECT id, sale_id, sale_discount, rn_desc,
           CASE WHEN sale_subtotal > 0 THEN ROUND(sale_discount * line_total / sale_subtotal, 2) ELSE 0 END AS share
    FROM lines
), adjusted AS (
    SELECT id,
           CASE WHEN rn_desc = 1
                THEN sale_discount - (SUM(share) OVER (PARTITION BY sale_id) - share)
                ELSE share END AS item_discount
    FROM shares
)
UPDATE sale_items si SET discount = a.item_discount FROM adjusted a WHERE a.id = si.id;

WITH lines AS (
    SELECT si.id, si.sh_sale_id AS sale_id, s.discount AS sale_discount,
           si.quantity * si.unit_price AS line_total,
           SUM(si.quantity * si.unit_price) OVER (PARTITION BY si.sh_sale_id) AS sale_subtotal,
           ROW_NUMBER() OVER (PARTITION BY si.sh_sale_id ORDER BY si.id DESC) AS rn_desc
    FROM sh_sales_item si
    JOIN sh_sales s ON s.id = si.sh_sale_id
    WHERE s.discount > 0
), shares AS (
    SELECT id, sale_id, sale_discount, rn_desc,
           CASE WHEN sale_subtotal > 0 THEN ROUND(sale_discount * line_total / sale_subtotal, 2) ELSE 0 END AS share
    FROM lines
), adjusted AS (
    SELECT id,
           CASE WHEN rn_desc = 1
                THEN sale_discount - (SUM(share) OVER (PARTITION BY sale_id) - share)
                ELSE share END AS item_discount
    FROM shares
)
UPDATE sh_sales_item si SET discount = a.item_discount FROM adjusted a WHERE a.id = si.id;

-- --------------------------------------------------------
-- 3. Estado de la venta y monto reembolsado
-- --------------------------------------------------------
ALTER TABLE sales
    ADD COLUMN IF NOT EXISTS status          VARCHAR(20)    NOT NULL DEFAULT 'COMPLETED',
    ADD COLUMN IF NOT EXISTS refunded_amount NUMERIC(10, 2) NOT NULL DEFAULT 0;
ALTER TABLE sh_sales
    ADD COLUMN IF NOT EXISTS status          VARCHAR(20)    NOT NULL DEFAULT 'COMPLETED',
    ADD COLUMN IF NOT EXISTS refunded_amount NUMERIC(10, 2) NOT NULL DEFAULT 0;

ALTER TABLE sales DROP CONSTRAINT IF EXISTS chk_sales_status;
ALTER TABLE sales ADD CONSTRAINT chk_sales_status
    CHECK (status IN ('COMPLETED', 'PARTIALLY_CANCELLED', 'CANCELLED'));
ALTER TABLE sh_sales DROP CONSTRAINT IF EXISTS chk_sh_sales_status;
ALTER TABLE sh_sales ADD CONSTRAINT chk_sh_sales_status
    CHECK (status IN ('COMPLETED', 'PARTIALLY_CANCELLED', 'CANCELLED'));

-- --------------------------------------------------------
-- 4. Recrear vistas (precio historico, NUMERIC, sin lo cancelado en metricas)
-- --------------------------------------------------------

-- Busqueda de ventas del admin: muestra TODOS los items (el estado dice si se cancelo).
CREATE VIEW view_of_sales AS
SELECT si.id,
       s.user_id,
       c.full_name AS client_name,
       p.name      AS product_name,
       si.quantity,
       (COALESCE(si.quantity, 0) * si.unit_price)::NUMERIC(12, 2) AS total_calculated,
       si.state,
       COALESCE(si.date::date, s.created_at::date) AS date
FROM sale_items si
     LEFT JOIN sales s    ON s.id = si.sale_id
     LEFT JOIN clients c  ON c.id = COALESCE(si.client_id, s.client_id)
     LEFT JOIN products p ON p.id = si.product_id;

-- Reportes Excel/PDF: total historico + precio actual del producto (current_amount).
CREATE VIEW report_view_dashboard AS
SELECT s.user_id,
       c.full_name AS client_name,
       p.name      AS product_name,
       si.quantity,
       (COALESCE(si.quantity, 0) * si.unit_price)::NUMERIC(12, 2) AS total_calculated,
       si.state,
       COALESCE(si.date, s.created_at) AS date,
       p.price AS current_amount
FROM sale_items si
     LEFT JOIN sales s    ON s.id = si.sale_id
     LEFT JOIN clients c  ON c.id = COALESCE(si.client_id, s.client_id)
     LEFT JOIN products p ON p.id = si.product_id;

-- Resumen por cliente: solo items NO cancelados; descuento por item (ya no se repite por item).
CREATE VIEW clients_summary (id, user_id, full_name, email, total_quantity, total_spent, total_discount,
                             total_spent_discount, latest_sale) AS
SELECT c.id,
       s.user_id,
       c.full_name,
       c.email,
       COALESCE(SUM(si.quantity), 0)::NUMERIC(38, 0)                                  AS total_quantity,
       COALESCE(SUM(si.quantity * si.unit_price), 0)::NUMERIC(38, 2)                  AS total_spent,
       COALESCE(SUM(si.discount), 0)::NUMERIC(38, 2)                                  AS total_discount,
       COALESCE(SUM(si.quantity * si.unit_price) - SUM(si.discount), 0)::NUMERIC(38, 2) AS total_spent_discount,
       MAX(COALESCE(si.date, s.created_at))                                           AS latest_sale
FROM clients c
     LEFT JOIN sale_items si ON si.client_id = c.id AND si.state IS DISTINCT FROM 'CANCELLED'
     LEFT JOIN sales s       ON s.id = si.sale_id
GROUP BY c.id, s.user_id, c.full_name, c.email;

-- Historial del cliente: todos los items (con su estado). total_amount = cobrado neto de reembolsos.
CREATE VIEW view_of_client_history AS
SELECT si.id         AS sale_item_id,
       s.client_id,
       c.full_name   AS client_name,
       c.email       AS client_email,
       c.address     AS client_address,
       s.id          AS sale_id,
       s.user_id,
       p.id          AS product_id,
       p.name        AS product_name,
       p.category    AS product_category,
       si.quantity,
       si.unit_price AS unit_price,
       (COALESCE(si.quantity, 0) * si.unit_price)::NUMERIC(12, 2) AS subtotal,
       (s.total_amount - s.refunded_amount)::NUMERIC(12, 2)       AS total_amount,
       s.discount,
       cp.cupon_code,
       si.state,
       s.created_at  AS occurred_at,
       si.discount   AS item_discount,
       s.status      AS sale_status
FROM sales s
     JOIN sale_items si ON si.sale_id = s.id
     JOIN clients c     ON c.id = s.client_id
     JOIN products p    ON p.id = si.product_id
     LEFT JOIN cupons cp ON cp.id = s.cupon_id;

-- Dashboard mensual: neto de reembolsos y sin ventas canceladas por completo.
CREATE VIEW view_of_dashboard AS
SELECT ((s.user_id * 1000000)::NUMERIC + EXTRACT(YEAR FROM s.created_at) * 100 + EXTRACT(MONTH FROM s.created_at))::BIGINT AS id,
       s.user_id,
       EXTRACT(MONTH FROM s.created_at)::INTEGER AS month_number,
       EXTRACT(YEAR FROM s.created_at)::INTEGER  AS sales_year,
       INITCAP(TRIM(BOTH FROM TO_CHAR(MIN(s.created_at), 'TMMonth'))) AS month_name,
       SUM(s.total_amount - s.refunded_amount)::NUMERIC(14, 2) AS monthly_total,
       COUNT(DISTINCT p.id)        AS number_of_products,
       COUNT(DISTINCT s.client_id) AS count_clients
FROM sales s
     LEFT JOIN users u    ON u.id = s.user_id
     LEFT JOIN products p ON p.id_users = u.id
WHERE EXTRACT(YEAR FROM s.created_at) = EXTRACT(YEAR FROM CURRENT_DATE)
  AND s.status <> 'CANCELLED'
GROUP BY s.user_id, EXTRACT(YEAR FROM s.created_at), EXTRACT(MONTH FROM s.created_at)
ORDER BY s.user_id, EXTRACT(YEAR FROM s.created_at)::INTEGER, EXTRACT(MONTH FROM s.created_at)::INTEGER;

-- Rankings (materializadas, las refresca MaterializedViewRefreshService): sin lo cancelado.
CREATE MATERIALIZED VIEW three_best_clients AS
SELECT c.id AS client_id,
       s.user_id,
       c.full_name AS name,
       COUNT(*) AS amount_of_buys
FROM clients c
     JOIN sales s ON s.client_id = c.id
WHERE s.status <> 'CANCELLED'
GROUP BY c.id, s.user_id, c.full_name;
CREATE UNIQUE INDEX uq_three_best_clients ON three_best_clients (user_id, client_id);

CREATE MATERIALIZED VIEW three_best_products AS
SELECT p.id AS product_id,
       s.user_id AS users_id,
       p.name,
       COUNT(*) AS amount_of_buys
FROM sale_items si
     JOIN sales s    ON s.id = si.sale_id
     JOIN products p ON p.id = si.product_id
WHERE si.state IS DISTINCT FROM 'CANCELLED'
GROUP BY p.id, s.user_id, p.name;
CREATE UNIQUE INDEX uq_three_best_products ON three_best_products (users_id, product_id);
