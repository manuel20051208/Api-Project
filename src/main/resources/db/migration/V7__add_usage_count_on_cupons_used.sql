-- V7: contador de usos por cliente+cupon en las tablas *_used_by_clients.
-- El contador evita COUNT(*) en el calculo final (usedCount del dialogo Clientes
-- y el limite por cliente al canjear). Se incrementa al registrar cada uso
-- filtrado por cupon_id + client_user (+ admin via duenio del cupon).

ALTER TABLE cupons_used_by_clients ADD COLUMN IF NOT EXISTS usage_count INTEGER NOT NULL DEFAULT 0;
ALTER TABLE sh_cupons_used_by_clients ADD COLUMN IF NOT EXISTS usage_count INTEGER NOT NULL DEFAULT 0;

-- Indices compuestos para el lookup del contador por (cupon, cliente).
CREATE INDEX IF NOT EXISTS idx_cupons_used_cupon_client ON cupons_used_by_clients (cupon_id, client_user);
CREATE INDEX IF NOT EXISTS idx_sh_cupons_used_cupon_client ON sh_cupons_used_by_clients (cupon_id, client_user);

-- Backfill: numerar los usos existentes 1..N por (cupon, cliente) en orden cronologico,
-- para que el contador refleje los usos reales ya registrados.
UPDATE cupons_used_by_clients u
SET usage_count = sub.rn
FROM (
    SELECT id, ROW_NUMBER() OVER (PARTITION BY cupon_id, client_user ORDER BY created_at, id) AS rn
    FROM cupons_used_by_clients
) sub
WHERE u.id = sub.id;

UPDATE sh_cupons_used_by_clients u
SET usage_count = sub.rn
FROM (
    SELECT id, ROW_NUMBER() OVER (PARTITION BY cupon_id, client_user ORDER BY created_at, id) AS rn
    FROM sh_cupons_used_by_clients
) sub
WHERE u.id = sub.id;

-- Espejo para cupones de servicios (no existia una tabla de usos).
CREATE TABLE IF NOT EXISTS services_cupons_used_by_clients (
    id BIGSERIAL PRIMARY KEY,
    client_user BIGINT NOT NULL,
    service_cupon_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usage_count INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_scubc_clients
        FOREIGN KEY (client_user)
        REFERENCES clients (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_scubc_services_cupon
        FOREIGN KEY (service_cupon_id)
        REFERENCES services_cupon (id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_services_cupons_used_cupon ON services_cupons_used_by_clients (service_cupon_id);
CREATE INDEX IF NOT EXISTS idx_services_cupons_used_cupon_client ON services_cupons_used_by_clients (service_cupon_id, client_user);