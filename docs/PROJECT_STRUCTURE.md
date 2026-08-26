# Estructura del Proyecto — ApiProject

> Nota central (MOC). Toda la documentación vive en esta carpeta `docs/`, conectada con enlaces `[[]]` para que Obsidian genere el grafo.

API de inventario y ventas (`ApiJuegoInventario`) construida con Spring Boot 3.5 y Java 21. Base de datos PostgreSQL. Incluye módulos de **cupones**, **productos de segunda mano** (con imágenes Cloudinary y compras) y **servicios ofrecidos** → [[Endpoints API]] · [[Esquema de Base de Datos]].

## Mapa de notas

| Nota | Contenido |
|---|---|
| [[Arquitectura del Código]] | Capas del código: config, controllers, entities, DTOs, repos, services, security |
| [[Endpoints API]] | Todas las rutas con payloads de request/response |
| [[Esquema de Base de Datos]] | Tablas, índices y vistas SQL |
| [[Configuración]] | Properties, variables de entorno, Swagger, seguridad |

---

## Arquitectura general

```
ApiProject/
├── src/
│   ├── main/
│   │   ├── java/com/apiproject/     # Código fuente → [[Arquitectura del Código]]
│   │   └── resources/               # Configuración → [[Configuración]] | SQL → [[Esquema de Base de Datos]]
│   └── test/java/                   # Tests
├── uploads/                         # Archivos subidos (perfiles, productos)
├── graphify-out/                    # Knowledge graph del proyecto (generado por graphify)
├── docker-compose.yml               # Orquestación local (Postgres + API)
├── Dockerfile                       # Build multi-etapa Maven + JRE 21
├── pom.xml                          # Dependencias y plugins Maven
├── API_FRONTEND_CONSUMPTION.txt     # Documentación de consumo para el frontend
├── AGENTS.md                        # Instrucciones para agentes de IA
├── .env                             # Variables de entorno (no versionado)
└── mvnw / mvnw.cmd                  # Maven wrapper
```

Flujo general: los **controllers** reciben las peticiones (`/api/...`) y delegan en los **services** (→ [[Arquitectura del Código]]), que usan los **repositories** sobre las vistas SQL (→ [[Esquema de Base de Datos]]) y devuelven **DTOs** al frontend (→ [[Endpoints API]]).

---

## Dependencias principales (pom.xml)

| Dependencia | Propósito |
|---|---|
| `spring-boot-starter-web` | REST API embebida |
| `spring-boot-starter-data-jpa` | Persistencia / Hibernate |
| `spring-boot-starter-security` + `oauth2-client` | Autenticación JWT + login con Google |
| `spring-boot-starter-actuator` | Health checks (`/actuator/health`) |
| `springdoc-openapi-starter-webmvc-ui` | Swagger UI (`/swagger-ui.html`) |
| `jjwt-api/impl/jackson` (0.13.0) | Generación/validación de tokens JWT |
| `cloudinary-http45` (1.39.0) | Almacenamiento de imágenes en Cloudinary |
| `poi-ooxml` + `openpdf` | Generación de reportes Excel y PDF |
| `postgresql` | Driver PostgreSQL |
| `spring-dotenv` | Carga de `.env` |

> **Restricciones de build**: Maven >= 3.8, Java 21 (`maven-enforcer-plugin`). Perfil `security` ejecuta `dependency-check-maven` (falla si hay CVSS >= 7).

---

## Docker

### docker-compose.yml
- **db**: `postgres:16-alpine`, puerto `5433:5432`, volumen `db_data`, healthcheck.
- **app**: imagen build local, puerto `8080`, perfil `dev`, depende de la salud de la DB, lee todas las variables desde `.env` → [[Configuración]].

### Dockerfile
1. Build: `maven:3.9-eclipse-temurin-21` → `mvn package -DskipTests`.
2. Runtime: `eclipse-temurin:21-jre` → `java -jar app.jar` (expone 8080).

---

## Tests

`src/test/java`:
- `com/apiproject/ApiProjectApplicationTests.java` — smoke test del contexto.
- `com/apiproject/security/JwtServiceTest.java` — pruebas del servicio JWT.

---

## Directorios de datos y herramientas
- `uploads/` → `perfiles/` y `products/` (archivos locales antes de subir a Cloudinary).
- `graphify-out/` → knowledge graph autogenerado (god nodes, comunidades, relaciones). Consultar con `graphify query "..."`
- `.idea/`, `.junie/`, `.opencode/`, `.agents/`, `.mvn/` → config de herramientas/IDE.
- `target/` → salida de build (generada).

---

## Referencias útiles
- **Swagger**: `http://localhost:8080/swagger-ui.html`
- **Health**: `http://localhost:8080/actuator/health`
- **Documentación frontend (API)**: `API_FRONTEND_CONSUMPTION.txt` en la raíz
- **Knowledge graph**: `graphify-out/GRAPH_REPORT.md`
