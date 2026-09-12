#!/bin/sh
set -eu

# ---------------------------------------------------------------
# Entrypoint del contenedor de ApiProject.
# 1) Construye DB_URL (JDBC) desde variables granulares.
# 2) Espera a que PostgreSQL esté listo.
# 3) Aplica el esquema SOLO si la base está vacía (idempotente).
# 4) Arranca la aplicación Spring Boot (JarLauncher).
# ---------------------------------------------------------------

# ---- Valores por defecto ----
DB_PORT="${DB_PORT:-5432}"
DB_SSLMODE="${DB_SSLMODE:-}"
RUN_DB_MIGRATION="${RUN_DB_MIGRATION:-true}"
SCHEMA_FILE="${SCHEMA_FILE:-/app/db/schema-postgres.sql}"
JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx512m}"

# ---- Variables requeridas ----
DB_HOST="${DB_HOST:?Variable DB_HOST requerida (host de PostgreSQL)}"
DB_NAME="${DB_NAME:?Variable DB_NAME requerida (nombre de la base)}"
DB_USERNAME="${DB_USERNAME:?Variable DB_USERNAME requerida}"
DB_PASSWORD="${DB_PASSWORD:?Variable DB_PASSWORD requerida}"

# ---- URL JDBC para Spring Boot ----
SSL_QUERY=""
if [ -n "$DB_SSLMODE" ]; then
    SSL_QUERY="?sslmode=${DB_SSLMODE}"
fi
DB_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}${SSL_QUERY}"
export DB_URL
echo "[entrypoint] DB_URL=${DB_URL}"

export PGPASSWORD="$DB_PASSWORD"
export PGSSLMODE="${DB_SSLMODE:-prefer}"
PSQL_OPTS="-h ${DB_HOST} -p ${DB_PORT} -U ${DB_USERNAME} -d ${DB_NAME}"

# ---- Esperar a que PostgreSQL esté listo ----
echo "[entrypoint] Esperando por PostgreSQL en ${DB_HOST}:${DB_PORT}..."
i=0
until psql $PSQL_OPTS -tAc "SELECT 1" >/dev/null 2>&1; do
    i=$((i + 1))
    if [ "$i" -ge 30 ]; then
        echo "[entrypoint] ERROR: PostgreSQL no estuvo listo tras 60s." >&2
        exit 1
    fi
    sleep 2
done
echo "[entrypoint] PostgreSQL listo."

# ---- Migración del esquema (solo si está vacío) ----
if [ "$RUN_DB_MIGRATION" = "true" ] && [ -f "$SCHEMA_FILE" ]; then
    HAS_SCHEMA="$(psql $PSQL_OPTS -tAc "SELECT to_regclass('public.users') IS NOT NULL")"
    if [ "$HAS_SCHEMA" = "t" ]; then
        echo "[entrypoint] Esquema ya presente; se omite la migración."
    else
        echo "[entrypoint] Aplicando esquema desde ${SCHEMA_FILE}..."
        psql $PSQL_OPTS -v ON_ERROR_STOP=1 -f "$SCHEMA_FILE"
        echo "[entrypoint] Migración completada."
    fi
else
    echo "[entrypoint] Migración desactivada o sin archivo de esquema; continuando."
fi

# ---- Arrancar la aplicación ----
exec java $JAVA_OPTS -Dserver.port="${PORT:-8080}" org.springframework.boot.loader.launch.JarLauncher