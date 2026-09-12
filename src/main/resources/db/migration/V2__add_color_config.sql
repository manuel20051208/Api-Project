-- Flyway V2: columna color_config en users y clients.
-- Usa el enum nativo de Postgres esperado por Hibernate
-- (@JdbcTypeCode(SqlTypes.NAMED_ENUM) sobre ColorTypes).
-- Idempotente: crea el tipo solo si no existe y agrega la columna solo si falta.

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'color_types') THEN
        CREATE TYPE color_types AS ENUM ('VERDE', 'AZUL', 'VIOLETA', 'AMBAR', 'ROSA');
    END IF;
END
$$;

ALTER TABLE users   ADD COLUMN IF NOT EXISTS color_config color_types;
ALTER TABLE clients ADD COLUMN IF NOT EXISTS color_config color_types;