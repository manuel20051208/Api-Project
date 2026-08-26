# Endpoints API

> Nota: [[PROJECT_STRUCTURE]] · Implementados en [[Arquitectura del Código#controllers/]] · Datos de las vistas en [[Esquema de Base de Datos#Vistas]]

Base: `/api` (salvo el dashboard y las vistas admin). Autenticación con header `Authorization: Bearer <token>`. Códigos de error típicos: `401` token inválido/faltante, `403` sin permisos, `402` sin tarjeta activa (compra).

## Autenticación y usuarios

### POST `/user/login` — Login admin (público)
Request:
```json
{ "email": "admin@mail.com", "password": "123456" }
```
Response `LoginAdminResponseDTO`:
```json
{
  "id": 1,
  "fullName": "Juan Perez",
  "email": "admin@mail.com",
  "phone": 3001234567,
  "businessName": "Mi Negocio",
  "photo": "https://res.cloudinary.com/...",
  "accountType": "ADMIN",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Inicio de sesion exitoso"
}
```

### POST `/user/register` — Registro admin (público)
Request `RegisterAdminRequestDTO`:
```json
{
  "password": "123456",
  "fullName": "Juan Perez",
  "email": "admin@mail.com",
  "phone": 3001234567,
  "businessName": "Mi Negocio"
}
```
Response: `LoginAdminResponseDTO` (igual que login).

### POST `/client/login` — Login cliente (público)
Request `LoginClientRequestDTO`:
```json
{ "email": "juan@mail.com", "password": "123456" }
```
Response `LoginClientResponseDTO`:
```json
{
  "id": 1,
  "fullName": "Juan Perez",
  "email": "juan@mail.com",
  "phone": 3001234567,
  "address": "Calle 1 #2-3",
  "createdAt": "2026-05-25T01:10:00",
  "photo": null,
  "accountType": "CLIENT",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Inicio de sesion exitoso"
}
```

### POST `/client/register` — Registro cliente (público)
Request `RegisterClientRequestDTO`:
```json
{
  "username": "juan123",
  "fullName": "Juan Perez",
  "email": "juan@mail.com",
  "password": "123456",
  "phone": 3001234567,
  "address": "Calle 1 #2-3"
}
```
Response: `LoginClientResponseDTO`.

### GET `/user/admin` — Datos del admin autenticado (ADMIN)
Response `UserResponseDTO`:
```json
{
  "id": 1,
  "fullName": "Juan Perez",
  "email": "admin@mail.com",
  "phone": 3001234567,
  "photo": "https://res.cloudinary.com/...",
  "businessName": "Mi Negocio"
}
```

### PATCH `/user/modify` — Modificar admin (ADMIN)
Request: entidad `UserAdmin` (solo campos a cambiar). Response: `UserResponseDTO`.

### PATCH `/user/upload-profile` — Subir foto de perfil admin (ADMIN)
`multipart/form-data`, campo `profilePhoto`. Response: `UserResponseDTO`.

### GET `/client/{adminId}/admin` — Datos de tienda del admin (público GET)
Response `ClientDescriptionAboutUsersDTO`:
```json
{ "id": 1, "businessName": "Mi Negocio", "photo": "https://..." }
```

## Clientes y tarjetas

### POST `/client/payment-cards` — Crear tarjeta (CLIENT)
Request `PaymentCardRequestDTO`:
```json
{ "cardHolderName": "Juan Perez", "brand": "Visa", "lastFour": "4242", "active": true }
```
Response `PaymentCardResponseDTO`:
```json
{
  "id": 1,
  "clientId": 1,
  "cardHolderName": "Juan Perez",
  "brand": "Visa",
  "lastFour": "4242",
  "active": true,
  "createdAt": "2026-05-25T01:10:00"
}
```

### GET `/client/payment-cards` — Listar tarjetas (CLIENT)
Response: `List<PaymentCardResponseDTO>`.

### PATCH `/client/payment-cards/{cardId}/status?active=true` — Activar/desactivar tarjeta (CLIENT)
Sin body. Response: `PaymentCardResponseDTO`.

### GET `/client/user-data` — Datos del cliente (CLIENT)
Response `ClientResponseDTO`:
```json
{
  "id": 1,
  "fullName": "Juan Perez",
  "email": "juan@mail.com",
  "phone": 3001234567,
  "paymentCards": [ { "id": 1, "cardHolderName": "Juan Perez", "brand": "Visa", "lastFour": "4242", "active": true, "createdAt": "..." } ],
  "address": "Calle 1 #2-3",
  "createdAt": "2026-05-25T01:10:00",
  "photo": null
}
```

### GET /client/user-payments — Historial de compras del cliente (CLIENT)
Response: `List<ClientHistoryProjection>` (vista `view_of_client_history` → [[Esquema de Base de Datos#Vistas]]):
```json
[
  {
    "saleItemId": 1, "clientId": 1, "clientName": "Juan Perez",
    "clientEmail": "juan@mail.com", "saleId": 100, "userId": 1,
    "productId": 10, "productName": "Mouse Gamer", "productCategory": "Perifericos",
    "quantity": 2, "unitPrice": 25000.0, "totalAmount": 50000.0,
    "state": "COMPLETED", "occurredAt": "2026-05-25T01:10:00"
  }
]
```

### PATCH `/client/modify` — Modificar datos del cliente (CLIENT)
Request: entidad `UserClient` (campos a cambiar). Response: `ClientResponseDTO`.

### PATCH `/client/upload-profile` — Foto de perfil del cliente (CLIENT)
`multipart/form-data`, campo `profilePhoto`. Response: `ClientResponseDTO`.

### GET `/client/profile-photo` — Obtener foto de perfil (CLIENT)
Devuelve el archivo de imagen (contenido binario).

## Productos

| Método / Ruta | Rol | Respuesta |
|---|---|---|
| GET `/product/search` | ADMIN | `List<ProductResponseDTO>` (productos del admin autenticado) |
| GET `/product/search/with-images` | ADMIN | `List<ProductResponseDTO>` |
| GET `/product/search/active-with-images` | Autenticado | `List<ProductResponseDTO>` (catálogo activo con imágenes) |
| GET `/product/search/id/{id}` | Autenticado | `List<ProductResponseDTO>` |
| GET `/product/search/category/{category}` | Autenticado | `List<ProductResponseDTO>` (case-insensitive) |
| GET `/product/search/name/{name}` | Autenticado | `ProductResponseDTO` (nombre exacto) |
| GET `/product/activeProducts?sizePage=10` | Autenticado | `Page<ProductResponseDTO>` |
| GET `/product/{productId}/admin` | Autenticado | `UserAdminDTO` (admin dueño del producto) |

`ProductResponseDTO`:
```json
{
  "id": 10,
  "name": "Mouse Gamer",
  "price": 25000.0,
  "stock": 18,
  "category": "Perifericos",
  "description": "Mouse RGB",
  "active": true,
  "userAdminId": 1,
  "images": [
    { "id": 7, "fileName": "mouse.png", "displayOrder": 1, "url": "https://res.cloudinary.com/.../mouse.png" }
  ]
}
```

### POST `/product/saveProduct` — Crear producto (ADMIN)
Request: entidad `Product` (JSON):
```json
{
  "name": "Mouse Gamer",
  "price": 25000.0,
  "stock": 18,
  "category": "Perifericos",
  "description": "Mouse RGB",
  "active": true
}
```
Response: el `Product` creado.

### POST `/product/deleteSafe` — Borrado suave (ADMIN)
Request: entidad `Product` (usa `id`). Response: el `Product` con `active=false`.

### PUT `/product/update/{id}` — Actualizar producto (ADMIN)
Request: entidad `Product` con campos nuevos. Response: `ProductResponseDTO`.

## Imágenes de producto

### POST `/product-images/upload/{productId}` — Subir imagen (ADMIN)
`multipart/form-data`, campo `file`. Response `ProductImageDTO`:
```json
{ "id": 7, "fileName": "mouse.png", "displayOrder": 1, "url": "https://res.cloudinary.com/..." }
```

### GET `/product-images/{productId}` — Imágenes de un producto (público)
Response: `List<ProductImageDTO>`.

### DELETE `/product-images/{imageId}` — Eliminar imagen (ADMIN)
Response: `204 No Content`.

## Ventas

### POST `/sale/purchase` — Compra de cliente (CLIENT)
Request `PurchaseRequestDTO` (`cuponCode` opcional → [[#Regla de cupones]]):
```json
{
  "clientId": 1,
  "cuponCode": "VERANO10",
  "items": [
    { "productId": 10, "quantity": 2 },
    { "productId": 15, "quantity": 1 }
  ]
}
```
Response `PurchaseResponseDTO` (sin cupón los últimos 3 campos son `null`):
```json
{
  "saleId": 100,
  "saleIds": [100],
  "clientId": 1,
  "totalAmount": 76500.0,
  "createdAt": "2026-05-25T01:10:00",
  "cuponCode": "VERANO10",
  "originalTotal": 85000.0,
  "discountApplied": true,
  "items": [
    { "productId": 10, "productName": "Mouse Gamer", "quantity": 2, "unitPrice": 25000.0, "subtotal": 45000.0 },
    { "productId": 15, "productName": "Teclado Mecanico", "quantity": 1, "unitPrice": 35000.0, "subtotal": 31500.0 }
  ]
}
```
Error: `402 Payment Required` si el cliente no tiene tarjeta activa; `400` si el cupón no es válido para el carrito.

### POST `/sale/sale/refresh` — Recalcular producto por venta (ADMIN)
Body: `Product` + `UserClient` + `Integer amount` (endpoint legado, cuerpo combinado). Response: `ProductResponseDTO`.

## Panel de administración (ADMIN)

### GET `/dashboard-controller/get-data-dashboard` — Métricas del dashboard
Response `DashboardDTO`:
```json
{
  "totalSales": 250000.0,
  "totalProducts": 12,
  "totalClients": 8,
  "monthlyData": [
    { "monthName": "Mayo", "monthlyTotal": 85000.0, "numberOfProducts": 3, "countClients": 2 }
  ],
  "showLatestSales": { "content": [/* Page<ClientSummaryProjection> */] }
}
```

### GET /dashboard-controller/excel — Reporte en Excel (adjunto .xlsx)
### GET /dashboard-controller/pdf — Reporte en PDF (adjunto .pdf)

`ClientSummaryProjection` (vista `clients_summary` → [[Esquema de Base de Datos#Vistas]]):
```json
{ "id": 1, "userId": 1, "fullName": "Juan Perez", "email": "juan@mail.com",
  "totalQuantity": 5, "totalSpent": 85000.0, "latestSale": "2026-05-25T01:10:00" }
```

## Historial de ventas (ADMIN)

| Método / Ruta | Respuesta |
|---|---|
| GET `/sales-items/show-with-no-restrinction?userId=1` | `Page<SaleItemViewProjection>` |
| GET `/sales-items/show-with-limits?userId=1&sizePage=10` | `Page<SaleItemViewProjection>` |
| GET `/sales-items/client?userId=1&clientName=Juan` | `List<SaleItemViewProjection>` |
| GET `/sales-items/product/?userId=1&productName=Mouse` | `List<SaleItemViewProjection>` |

`SaleItemViewProjection` (vista `view_of_sales` → [[Esquema de Base de Datos#Vistas]]):
```json
{ "id": 1, "userId": 1, "clientName": "Juan Perez", "productName": "Mouse Gamer",
  "quantity": 2, "totalCalculated": 50000.0, "state": "COMPLETED", "date": "2026-05-25" }
```

## Resumen de clientes (ADMIN)

| Método / Ruta | Respuesta |
|---|---|
| GET `/client-show-summary/getNames/{userId}` | `Page<ClientSummaryProjection>` |
| GET `/client-show-summary/name/{userId}?name=Juan&page=0` | `Page<ClientSummaryProjection>` |
| GET `/client-show-summary/email/{userId}?email=juan@mail.com&page=0` | `Page<ClientSummaryProjection>` |

## Notificaciones (SSE)

### GET `/notification/stream` — Stream de eventos (ADMIN)
`Content-Type: text/event-stream` (`SseEmitter`). Se emiten `NotificationEventDTO` cuando hay eventos del dashboard (incluye `VENTA_NUEVA_SH` y `STOCK_BAJO`).

## Regla de cupones

Común a cupones normales, SH y de servicios:
1. Debe cubrir **todos** los productos del carrito (`product_cupons_applied`) y pertenecer al mismo admin dueño.
2. Vigente: `cupon_date_limit` >= ahora.
3. Usos disponibles: `quantity` > 0 (`NULL` = ilimitado).
4. Se bloquea con `FOR UPDATE`, se descuenta 1 uso por compra (`redeem`) y se registra la fila en `cupons_used_by_clients` / `sh_cupons_used_by_clients`.
5. El descuento es un **porcentaje** sobre cada subtotal.

## Cupones de productos

### POST `/cupons` — Crear cupón (ADMIN)
Request `CuponRequestDTO`:
```json
{
  "cuponCode": "VERANO10",
  "dateLimit": "2026-12-31T23:59:59",
  "discount": 10.0,
  "quantity": 100,
  "productIds": [10, 15]
}
```
Response `CuponResponseDTO`: igual + `id` + lista de ids de productos aplicados.

| Método / Ruta | Rol | Respuesta |
|---|---|---|
| PUT `/cupons/{id}` | ADMIN | `CuponResponseDTO` |
| DELETE `/cupons/{id}` | ADMIN | `204 No Content` |
| GET `/cupons/my` | ADMIN | `List<CuponResponseDTO>` (con productos aplicados) |
| GET `/cupons/validate?code=X&productId=1` | Autenticado | `CuponValidationResponseDTO {valid, discount, message}` |

## Cupones de segunda mano

Igual que los anteriores bajo `/api/sh-cupons`, con `shCuponCode` como código y `shProductIds`. CRUD + `/my` + `/validate?code=&shProductId=` (ADMIN; validate autenticado). DTOs: `SecondHandCuponRequestDTO` / `SecondHandCuponResponseDTO`.

## Servicios ofrecidos

### POST `/services` — Crear servicio (ADMIN)
Request `ServiceRequestDTO`:
```json
{ "nameOfService": "Instalacion", "valueOfService": 50000.0, "descriptionOfService": "Instalacion a domicilio" }
```
Response `ServiceResponseDTO`.

| Método / Ruta | Rol | Respuesta |
|---|---|---|
| GET `/services/my` | ADMIN | `List<ServiceResponseDTO>` |
| GET `/services/catalog/{ownerId}` | Autenticado | `List<ServiceResponseDTO>` |
| PUT `/services/{id}` · DELETE `/services/{id}` | ADMIN | DTO / `204` |

### Cupones de servicio
Bajo `/api/services-cupons` (ADMIN): CRUD con `ServiceCuponRequestDTO {serviceCuponCode, dateLimit, discount, quantity}`, `GET /my`, y `GET /validate/{ownerId}?code=&value=` → valida vigencia/usos y devuelve el descuento a aplicar sobre el valor del servicio.

## Productos de segunda mano

### POST `/sh-product/save` — Crear producto SH (ADMIN)
Request `ShProductRequestDTO` = campos de producto + `timeOfUse` (ISO datetime) + `levelOfSecondhandProduct` (0–10).

| Método / Ruta | Rol | Respuesta |
|---|---|---|
| GET `/sh-product/my?page=&sizePage=` | ADMIN | `Page<ShProductCardResponseDTO>` (tarjetas con primera imagen) |
| GET `/sh-product/search/active/{ownerId}?page=&sizePage=` | Autenticado | `Page<ShProductCardResponseDTO>` catálogo activo |
| PUT `/sh-product/update/{id}` | ADMIN | `ShProductResponseDTO` |
| POST `/sh-product/deleteSafe` | ADMIN | borrado suave (`active=false`) |

`ShProductCardResponseDTO`: `{id, name, price, levelOfSecondhandProduct, timeOfUse, firstImageUrl}`.

### Imágenes SH — `/api/sh-product-images`
| Método / Ruta | Rol | Detalle |
|---|---|---|
| POST `/upload/{shProductId}` | ADMIN | multipart `file` → Cloudinary carpeta `secondhand` |
| GET `/{shProductId}` | público | `List<ShProductImageDTO>` |
| DELETE `/{imageId}` | ADMIN | `204 No Content` |

### POST `/sh-sale/purchase` — Compra SH (CLIENT)
Request `ShPurchaseRequestDTO`:
```json
{
  "clientId": 1,
  "cuponCode": null,
  "items": [{ "shProductId": 3, "quantity": 1 }]
}
```
Una venta por producto/admin (como la compra normal), descuenta stock con `FOR UPDATE`, exige tarjeta activa (`402` si no) y emite SSE `VENTA_NUEVA_SH` (+`STOCK_BAJO` si queda <= 5). Response `ShPurchaseResponseDTO` análogo al de `/sale/purchase` (con `saleIds` múltiples).

Historiales: `GET /sh-sale/client` (CLIENT) y `GET /sh-sale/admin` (ADMIN) → `ShSaleHistoryProjection`.

## Seguridad (SecurityConfig)
- Públicos: Swagger, `/actuator/health`, `/oauth2/**`, login/register, `GET /api/product-images/**`, `GET /api/user/{id}/admin`, `GET /api/sh-product-images/**`.
- `/api/client/**` → rol `CLIENT`; `/api/user/**`, `/dashboard-controller/**`, `/api/sales-items/**`, `/api/client-show-summary/**`, `/api/cupons/**`, `/api/sh-cupons/**`, `/api/services/**`, `/api/services-cupons/**`, `/api/sh-product/**`, `/api/sh-sale/**` → rol `ADMIN`.
- Autenticados: validaciones de cupones (`/cupons/validate`, `/sh-cupons/validate`, `/services/catalog/*`, `/services-cupons/validate/*`), `/sh-product/search/active/*`, catálogo normal.
- Escritura de productos/imágenes (normales y SH) → `ADMIN`.
- `/api/sale/purchase` y `/api/sh-sale/purchase`, `/api/sh-sale/client` → `CLIENT`; resto de ventas → `ADMIN`.
- CORS solo para `http://localhost:3000` → [[Configuración#Seguridad (SecurityConfig)]].

> Documentación previa del frontend: `API_FRONTEND_CONSUMPTION.txt` en la raíz del proyecto.
