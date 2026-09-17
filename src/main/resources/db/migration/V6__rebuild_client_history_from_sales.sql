-- Flyway V6: reconstruir view_of_client_history desde sales.
-- Ahora muestra subtotal (original), total_amount (final post-descuento),
-- discount y cupon_code. occurred_at = fecha real de la venta.

DROP VIEW IF EXISTS view_of_client_history;
CREATE VIEW view_of_client_history AS
SELECT
    si.id        AS sale_item_id,
    s.client_id  AS client_id,
    c.full_name  AS client_name,
    c.email      AS client_email,
    c.address    AS client_address,
    s.id         AS sale_id,
    s.user_id    AS user_id,
    p.id         AS product_id,
    p.name       AS product_name,
    p.category   AS product_category,
    si.quantity  AS quantity,
    p.price      AS unit_price,
    -- subtotal: lo que costaria sin descuento
    (COALESCE(si.quantity, 0)::NUMERIC * COALESCE(p.price, 0)::NUMERIC)::DOUBLE PRECISION AS subtotal,
    -- total_amount: lo que realmente pago (sales.total_amount ya tiene el descuento aplicado)
    s.total_amount::DOUBLE PRECISION AS total_amount,
    -- descuento y cupon aplicado en esta venta
    s.discount   AS discount,
    cp.cupon_code AS cupon_code,
    si.state     AS state,
    s.created_at AS occurred_at
FROM sales s
         JOIN sale_items si ON si.sale_id = s.id
         JOIN clients c    ON c.id = s.client_id
         JOIN products p   ON p.id = si.product_id
         LEFT JOIN cupons cp ON cp.id = s.cupon_id;