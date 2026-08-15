# Configuración

> Nota: [[PROJECT_STRUCTURE]] · Relacionada con [[Endpoints API]] y [[Esquema de Base de Datos]]

## Archivos (src/main/resources)

| Archivo | Descripción |
|---|---|
| `application.properties` | Solo activa el perfil `dev` |
| `application-dev.properties` | Datasource, JWT, multipart (30MB), Cloudinary, OAuth2 Google (admin y client), Swagger, caché JPA |
| `db/schema-postgres.sql` | Esquema SQL completo → [[Esquema de Base de Datos]] |
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
- **OAuth2**: dos registrations de Google — `google-admin` (ADMIN) y `google-client` (CLIENT), con redirects `{baseUrl}/login/oauth2/code/...`.
- **Swagger**: activo en `/swagger-ui.html` → [[Endpoints API]].

## Seguridad (SecurityConfig)

- CSRF desactivado, sesiones `IF_REQUIRED`, login por JWT (Bearer) + OAuth2 Google.
- CORS: solo `http://localhost:3000`.
- Reglas por ruta y rol: ver [[Endpoints API#Seguridad (SecurityConfig)]].

## Reportes
- Excel/PDF vía `ReportServiceFactory` + `poi-ooxml`/`openpdf` → [[Arquitectura del Código#services/]] y [[Endpoints API#Panel de administración (ADMIN)]].
