#!/bin/sh
set -eu

# NOTA: este script lo arme con ayuda de IA.
# La idea resumida: este archivo es lo PRIMERO que corre cuando el contenedor
# arranca. Hace 3 cosas: (1) arma la URL de la base de datos, (2) espera a que
# Postgres este vivo, (3) lanza el programa Java. Nada mas.
# ---------------------------------------------------------------
# Entrypoint del contenedor de ApiProject.
# 1) Construye DB_URL (JDBC) desde variables granulares.
# 2) Espera a que PostgreSQL esté listo.
# 3) Arranca la aplicación Spring Boot desde el JAR extraído.
# Las migraciones de base de datos las ejecuta FLYWAY al iniciar Spring.
# ---------------------------------------------------------------

# ---- Valores por defecto ----
DB_PORT="${DB_PORT:-5432}"
DB_SSLMODE="${DB_SSLMODE:-}"
JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx512m}"

# ---- Variables requeridas ----
DB_HOST="${DB_HOST:?Variable DB_HOST requerida (host de PostgreSQL)}"
DB_NAME="${DB_NAME:?Variable DB_NAME requerida (nombre de la base)}"
DB_USERNAME="${DB_USERNAME:?Variable DB_USERNAME requerida}"
DB_PASSWORD="${DB_PASSWORD:?Variable DB_PASSWORD requerida}"

# ---- Armar la URL de la base de datos ----
# Spring Boot no recibe el host/puerto/nombre por separado, sino UNA sola URL
# con este formato:  jdbc:postgresql://HOST:PUERTO/NOMBRE
# Aca pegamos esas piezas (que vienen como variables de entorno) en ese texto.
# El bloque "if" solo agrega "?sslmode=..." si pediste SSL; si no, lo deja vacio.
SSL_QUERY=""
if [ -n "$DB_SSLMODE" ]; then          # -n = "no esta vacio"
    SSL_QUERY="?sslmode=${DB_SSLMODE}"
fi
# Ej: DB_HOST=db, DB_PORT=5432, DB_NAME=apiproject ->
#     jdbc:postgresql://db:5432/apiproject
DB_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}${SSL_QUERY}"
export DB_URL                            # export = que Spring la vea como variable de entorno
echo "[entrypoint] DB_URL=${DB_URL}"     # lo imprime en los logs para depurar

export PGPASSWORD="$DB_PASSWORD"
export PGSSLMODE="${DB_SSLMODE:-prefer}"
PSQL_OPTS="-h ${DB_HOST} -p ${DB_PORT} -U ${DB_USERNAME} -d ${DB_NAME}"

# ---- Esperar a que PostgreSQL esté listo ----
echo "[entrypoint] Esperando por PostgreSQL en ${DB_HOST}:${DB_PORT}..."
i=0
until psql $PSQL_OPTS -tAc "SELECT 1" >/dev/null 2>&1; do
    i=$((i + 1))
    # si tarda mucho cortamos y enviamos un mensaje tipo time out
    if [ "$i" -ge 30 ]; then
        echo "[entrypoint] ERROR: PostgreSQL no estuvo listo tras 60s." >&2
        exit 1
    fi
    sleep 2
done
echo "[entrypoint] PostgreSQL listo."

# ---- Migración delegada a Flyway (arranque de Spring Boot) ----

# ---- Arrancar la aplicación ----
exec java $JAVA_OPTS -Dserver.port="${PORT:-8080}" -jar application.jar
