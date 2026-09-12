# syntax=docker/dockerfile:1

# ---------- Build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
# cache mount: reutiliza ~/.m2 entre builds -> Maven no re-descarga dependencias
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B package -DskipTests

# ---------- Extraer capas (mejor caché en builds posteriores) ----------
FROM eclipse-temurin:21-jre-alpine AS extract
WORKDIR /workspace
COPY --from=build /build/target/ApiProject-0.0.1-SNAPSHOT.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# ---------- Runtime ----------
FROM eclipse-temurin:21-jre-alpine AS runtime
RUN apk add --no-cache curl postgresql-client \
    && addgroup -S app && adduser -S -G app app

WORKDIR /app

COPY --from=extract --chown=app:app /workspace/dependencies/ ./
COPY --from=extract --chown=app:app /workspace/spring-boot-loader/ ./
COPY --from=extract --chown=app:app /workspace/snapshot-dependencies/ ./
COPY --from=extract --chown=app:app /workspace/application/ ./

# Las migraciones de BD viajan dentro del jar (src/main/resources/db/migration)
# y las aplica Flyway al arrancar la aplicacion.

COPY --chown=app:app docker/entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

USER app
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:MaxMetaspaceSize=160m"

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
    CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]