-- Flyway V3: materialized views de rankings (top ventas por admin).
-- Las crea MaterializedViewRefreshService con REFRESH MATERIALIZED VIEW CONCURRENTLY
-- (requiere un indice UNIQUE por materialized view, definido abajo).
-- PostgreSQL 16: CREATE MATERIALIZED VIEW ... IF NOT EXISTS.

CREATE MATERIALIZED VIEW IF NOT EXISTS three_best_clients AS
SELECT
    c.id             AS client_id,
    s.user_id        AS user_id,
    c.full_name      AS name,
    COUNT(*)::BIGINT AS amount_of_buys
FROM clients c
         JOIN sales s ON s.client_id = c.id
GROUP BY c.id, s.user_id, c.full_name;

CREATE UNIQUE INDEX IF NOT EXISTS uq_three_best_clients
    ON three_best_clients (user_id, client_id);

CREATE MATERIALIZED VIEW IF NOT EXISTS three_best_products AS
SELECT
    p.id             AS product_id,
    s.user_id        AS users_id,
    p.name           AS name,
    COUNT(*)::BIGINT AS amount_of_buys
FROM sale_items si
         JOIN sales s ON s.id = si.sale_id
         JOIN products p ON p.id = si.product_id
GROUP BY p.id, s.user_id, p.name;

CREATE UNIQUE INDEX IF NOT EXISTS uq_three_best_products
    ON three_best_products (users_id, product_id);