-- Flyway V4: descuento y cupon aplicado por venta + limite de usos por cliente.
-- cupon_id es FK (NULL = sin cupon). discount 0 por defecto. usage_limit NULL = ilimitado.

ALTER TABLE sales
    ADD COLUMN IF NOT EXISTS discount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS cupon_id BIGINT REFERENCES cupons (id) ON DELETE SET NULL;

ALTER TABLE sh_sales
    ADD COLUMN IF NOT EXISTS discount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS cupon_id BIGINT REFERENCES secondhand_cupons (id) ON DELETE SET NULL;

ALTER TABLE product_cupon_to_a_client
    ADD COLUMN IF NOT EXISTS usage_limit INTEGER;

ALTER TABLE sh_product_cupo_to_a_client
    ADD COLUMN IF NOT EXISTS usage_limit INTEGER;

ALTER TABLE service_cupon_to_a_client
    ADD COLUMN IF NOT EXISTS usage_limit INTEGER;

-- Recrear la vista del historial para exponer el descuento y el codigo del cupon.
DROP VIEW IF EXISTS view_of_client_history;
CREATE VIEW view_of_client_history AS
SELECT
    si.id       AS sale_item_id,
    c.id        AS client_id,
    c.full_name AS client_name,
    c.email     AS client_email,
    c.address   AS client_address,
    s.id        AS sale_id,
    s.user_id   AS user_id,
    p.id        AS product_id,
    p.name      AS product_name,
    p.category  AS product_category,
    si.quantity AS quantity,
    p.price     AS unit_price,
    (COALESCE(si.quantity, 0)::NUMERIC * COALESCE(p.price, 0)::NUMERIC)::DOUBLE PRECISION AS total_amount,
    s.discount   AS discount,
    cp.cupon_code AS cupon_code,
    si.state    AS state,
    COALESCE(si.date, s.created_at) AS occurred_at
FROM sale_items si
         LEFT JOIN sales s   ON s.id = si.sale_id
         LEFT JOIN clients c ON c.id = COALESCE(si.client_id, si.client_id)
         LEFT JOIN products p ON p.id = si.product_id
         LEFT JOIN cupons cp ON cp.id = s.cupon_id;