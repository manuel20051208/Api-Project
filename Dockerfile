# syntax=docker/dockerfile:1

# ---------- Build ----------
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /build
COPY pom.xml .
# cache mount: reutiliza ~/.m2 entre builds -> Maven no re-descarga dependencias
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B package -DskipTests

# ---------- Extraer capas (mejor caché en builds posteriores) ----------
FROM eclipse-temurin:25-jre-alpine AS extract
WORKDIR /workspace
COPY --from=build /build/target/ApiProject-0.0.1-SNAPSHOT.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# ---------- Runtime ----------
FROM eclipse-temurin:25-jre-alpine AS runtime
RUN apk add --no-cache curl postgresql-client \
    && addgroup -S app && adduser -S -G app app

WORKDIR /app

COPY --from=build --chown=app:app /build/target/ApiProject-0.0.1-SNAPSHOT.jar app.jar

# Las migraciones de BD viajan dentro del jar (src/main/resources/db/migration)
# y las aplica Flyway al arrancar la aplicacion.

COPY --chown=app:app docker/entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

USER app
# Heap relativo al limite de memoria del contenedor (mem_limit del compose):
# 50% de 576m ~= 288m de heap. Metaspace medido en arranque: ~105 MB, por eso
# el tope de 128m (con 96m el contexto muere con OutOfMemoryError: Metaspace).
# -XX:+ExitOnOutOfMemoryError reinicia el contenedor en vez de dejarlo colgado;
# SerialGC conviene en VMs de 1 vCPU con heap chico.
# Perfil de RAM baja (VM de 1 GB compartida con otra API): medido con mem_limit=384m ->
# ~267 MB al arrancar y ~290 MB de pico con carga. C1 solo (TieredStopAtLevel=1) + code cache
# chico + stacks de 512k recortan ~60 MB a cambio de algo menos de rendimiento pico.
ENV JAVA_OPTS="-XX:InitialRAMPercentage=20 -XX:MaxRAMPercentage=45 -XX:MaxMetaspaceSize=128m -XX:ReservedCodeCacheSize=40m -XX:TieredStopAtLevel=1 -XX:CICompilerCount=1 -Xss512k -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
    CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
