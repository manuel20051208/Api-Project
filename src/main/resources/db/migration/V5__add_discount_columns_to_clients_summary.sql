-- Flyway V5: expone el descuento total y el total neto (gastado - descuento) en clients_summary.
-- sales.discount (V4) es el monto descontado por venta.

DROP VIEW IF EXISTS clients_summary;
CREATE VIEW clients_summary (id, user_id, full_name, email, total_quantity, total_spent, total_discount, total_spent_discount, latest_sale) AS
SELECT c.id,
       s.user_id,
       c.full_name,
       c.email,
       COALESCE(sum(si.quantity), 0::bigint)::numeric(38, 0)                            AS total_quantity,
       COALESCE(sum(si.quantity::numeric * p.price::numeric), 0::numeric)::numeric(38, 2)
                                                                                        AS total_spent,
       COALESCE(sum(s.discount), 0::numeric)::numeric(38, 2)                            AS total_discount,
       COALESCE(sum(si.quantity::numeric * p.price::numeric) - sum(s.discount),
                0::numeric)::numeric(38, 2)                                             AS total_spent_discount,
       max(COALESCE(si.date, s.created_at))                                             AS latest_sale
FROM clients c
         LEFT JOIN sale_items si ON si.client_id = c.id
         LEFT JOIN sales s ON s.id = si.sale_id
         LEFT JOIN products p ON p.id = si.product_id
GROUP BY c.id, s.user_id, c.full_name, c.email;