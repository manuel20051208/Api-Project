# Configuración

> Nota: [[PROJECT_STRUCTURE]] · Relacionada con [[Endpoints API]] y [[Esquema de Base de Datos]]

## Archivos (src/main/resources)

| Archivo | Descripción |
|---|---|
| `application.properties` | Datasource, JWT, Flyway, multipart (30MB), Cloudinary, OAuth2 Google (admin y client), Swagger, caché JPA |
| `db/migration/*.sql` | Migraciones Flyway (V1, V2, V3) → [[Esquema de Base de Datos]] |
| `db/schema.dbml` | Esquema en notación DBML (con `.dbdiagram/settings.json`) |
| `static/`, `templates/` | Contenido estático / vistas (sin archivos) |

## Variables de entorno (.env)

| Variable | Uso |
|---|---|
| `DB_URL` | URL JDBC de PostgreSQL |
| `DB_USERNAME` / `DB_PASSWORD` | Credenciales de la BD |
| `JWT_SECRET_KEY` | Secreto para firmar tokens |
| `JWT_EXPIRATION_MS` | Duración del token (default `86400000`) |
| `CLOUDINARY_CLOUD_NAME` / `API_KEY` / `API_SECRET` | Almacenamiento de imágenes |
| `BASIC_AUTH_USERNAME` / `BASIC_AUTH_PASSWORD` | Usuario básico de Spring Security |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | OAuth2 Google (admin y client) |

## Perfil dev — puntos clave

- **Datasource**: `jdbc:postgresql://localhost:5432/apiproject` (puerto local `5433` vía docker-compose).
- **JPA**: `show-sql=true`, batch inserts de 50, `order_inserts`, dialecto PostgreSQL.
- **Multipart**: máx. 30MB por archivo/solicitud.
- **OAuth2**: dos registrations de Google — `google-admin` (ADMIN) y `google-client` (CLIENT), con redirects `{baseUrl}/login/oauth2/code/...`. `server.forward-headers-strategy=framework` para que `{baseUrl}` resuelva la URL pública detrás del proxy (Render). Tras el login, `OAuth2SuccessHandler` redirige al frontend = `cors.allowed-origin` (`URL_FOR_REQUESTING_AND_RESPONSIVES`, admin a `/`, client a `/portal`) con el JWT como query param.
- **Swagger**: activo en `/swagger-ui.html` → [[Endpoints API]].

## Seguridad (SecurityConfig)

- CSRF desactivado, sesiones `IF_REQUIRED`, login por JWT (Bearer) + OAuth2 Google.
- CORS: solo `http://localhost:3000`.
- Reglas por ruta y rol: ver [[Endpoints API#Seguridad (SecurityConfig)]].

## Reportes
- Excel/PDF vía `ReportServiceFactory` + `poi-ooxml`/`openpdf` → [[Arquitectura del Código#services/]] y [[Endpoints API#Panel de administración (ADMIN)]].

## Tareas programadas
- `MaterializedViewRefreshService` refresca las materialized views de rankings (`three_best_clients` / `three_best_products`) cada **5 minutos** (`@Scheduled(fixedDelay)`) → [[Esquema de Base de Datos#Vistas materializadas — rankings]].
