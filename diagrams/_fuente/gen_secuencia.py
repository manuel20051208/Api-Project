#!/usr/bin/env python3
"""
Genera un diagrama de secuencia por caso de uso de ApiProject (CU-001 ... CU-025):
  - diagrams/_fuente/secuencia-casos-de-uso.xml   (draw.io, una pagina por caso de uso; es la fuente)
  - diagrams/Secuencia/<cu-nnn-nombre>.svg        (un SVG por pagina, exportado con el visor oficial de draw.io)

Reglas (skill diagramas-uml, seccion 3):
  - Participantes = actor del diagrama de casos de uso + clases del diagrama de clases (entidades JPA).
    Mensajes = operaciones de esas entidades que el codigo de src/main/java/com/apiproject/services/** llama de verdad
    en ese flujo. Como las entidades son anemicas (Lombok), son los accesores setX/getX/isX que usa el servicio y los
    metodos escritos a mano (getOwnerId()); la creacion con new es <<create>>. Lo que orquesta la aplicacion
    (controladores, servicios, repositorios, DTOs de respuesta) no aparece: sale del actor.
  - Los casos que se resuelven solo con consultas SQL / vistas / proyecciones (no invocan operaciones de entidades)
    llevan una nota que lo dice.
  - Notacion OMG UML 2.5.1 (formal/17-12-05): 17.2.4.1 marco "sd"; 17.3.4.1 linea de vida; 17.2.4.4 barras de
    ejecucion; 17.4.4.1 llamada, retorno y creacion; 17.6.4 alt/opt/loop con guardas y [else]; 17.7.4 ref.

Uso:  python diagrams/_fuente/gen_secuencia.py                  (solo exporta los SVG)
      python diagrams/_fuente/gen_secuencia.py --regenerar-xml  (reescribe tambien el XML; descarta retoques a mano)
      python diagrams/_fuente/gen_secuencia.py --sin-svg        (no exporta SVG)
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from secuencia_lib import *  # noqa: E402,F401,F403
import secuencia_lib as L  # noqa: E402
from casos_uso_datos import CASOS  # noqa: E402

AQUI = os.path.dirname(os.path.abspath(__file__))
SALIDA = os.path.join(AQUI, "secuencia-casos-de-uso.xml")
NOMBRES = {c[0]: c[1] for c in CASOS}


class D(L.Diagrama):
    """Diagrama con dos ayudas mas: nota UML (7.2.4) y rama vacia (la rama solo corta el flujo con un error)."""

    def nota(self, texto, ancho=420, alto=70, x=None):
        x = x if x is not None else 190
        self.celdas_txt.append(
            f'<mxCell id="{self._id("n")}" value="{L.esc(texto)}" style="shape=note;whiteSpace=wrap;html=1;size=14;'
            f'align=left;verticalAlign=top;spacing=6;fontSize=11;fillColor=#fff2cc;strokeColor=#d6b656;" vertex="1" '
            f'parent="1"><mxGeometry x="{x}" y="{self.y + 14}" width="{ancho}" height="{alto}" as="geometry"/></mxCell>')
        self._tocar(x, x + ancho)
        self.y += alto + 24

    def vacio(self):
        self.y += 24

    # las guardas largas necesitan marco ancho: se "toca" el ancho del texto
    def abrir(self, tipo, condicion):
        super().abrir(tipo, condicion)
        self._tocar(60, 110 + int(len(condicion) * 6.6))

    def sino(self, condicion="else"):
        super().sino(condicion)
        self._tocar(60, 110 + int(len(condicion) * 6.6))


diagramas = []


def nuevo(codigo, participantes):
    d = D(f"{codigo} {NOMBRES[codigo]}", participantes)
    diagramas.append(d)
    return d


def actor(nombre):
    return ("act", "actor", nombre)


def o(pid, clase, rol=""):
    return (pid, "obj", f"{rol}:{clase}")


def v(d, pid, *ops):
    """Llamadas void desde el actor (setters): llamada + retorno sin etiqueta."""
    for op in ops:
        d.llamar_y_retornar("act", pid, op)


def g(d, pid, *pares):
    """Getters: (operacion, valor)."""
    for op, val in pares:
        d.llamar_y_retornar("act", pid, op, val)


def entre(d, a, b, op, valor=None):
    d.llamar_y_retornar(a, b, op, valor)


# ===================================================================================================== CU-001
d = nuevo("CU-001", [actor("Visitante"), o("cli", "UserClient"), o("adm", "UserAdmin")])
d.abrir("alt", "registro de cliente (POST /api/client/register)")
d.abrir("alt", "el correo ya está registrado: 409 Conflict")
d.vacio()
d.sino()
d.crear("act", "cli")
v(d, "cli", "setPassword(passwordCifrada)", "setFullName(fullName)", "setEmail(email)", "setPhone(phone)",
  "setAddress(address)")
g(d, "cli", ("getId()", "id"), ("getEmail()", "email"))
d.cerrar()
d.sino("registro de tienda (POST /api/user/register)")
d.abrir("alt", "el correo ya está registrado: 409 Conflict")
d.vacio()
d.sino()
d.crear("act", "adm")
v(d, "adm", "setPassword(passwordCifrada)", "setFullName(fullName)", "setEmail(email)", "setPhone(phone)",
  "setBusinessName(businessName)")
g(d, "adm", ("getId()", "id"), ("getEmail()", "email"))
d.cerrar()
d.cerrar()

# ===================================================================================================== CU-002
d = nuevo("CU-002", [actor("Visitante"), o("cli", "UserClient"), o("adm", "UserAdmin")])
d.abrir("alt", "inicia sesión como cliente (POST /api/client/login)")
g(d, "cli", ("getPassword()", "passwordCifrada"))
d.abrir("alt", "contraseña incorrecta: 401 Unauthorized")
d.vacio()
d.sino()
g(d, "cli", ("getId()", "id"), ("getEmail()", "email"), ("getFullName()", "fullName"), ("getPhone()", "phone"),
  ("getAddress()", "address"), ("getCreatedAt()", "createdAt"), ("getPhoto()", "photo"),
  ("getColorTypes()", "colorTypes"))
d.cerrar()
d.sino("inicia sesión como tienda (POST /api/user/login)")
g(d, "adm", ("getPassword()", "passwordCifrada"))
d.abrir("alt", "contraseña incorrecta: 401 Unauthorized")
d.vacio()
d.sino()
g(d, "adm", ("getId()", "id"), ("getEmail()", "email"), ("getFullName()", "fullName"), ("getPhone()", "phone"),
  ("getBusinessName()", "businessName"), ("getProfilePhotoUrl()", "profilePhotoUrl"),
  ("getColorTypes()", "colorTypes"))
d.cerrar()
d.cerrar()


# ===================================================================================================== CU-003
def dto_producto(d, prod="prod", img="img", adm="adm"):
    """ProductResponseDTO.fromEntity(product): los accesores que lee el DTO."""
    g(d, prod, ("getId()", "id"), ("getName()", "name"), ("getPrice()", "price"), ("getStock()", "stock"),
      ("getCategory()", "category"), ("getDescription()", "description"), ("isActive()", "active"))
    d.llamar("act", prod, "getUserAdmin()")
    d.retornar(prod, "act", "userAdmin")
    g(d, adm, ("getId()", "ownerId"))
    d.llamar("act", prod, "getImages()")
    d.retornar(prod, "act", "images")
    d.abrir("loop", "por cada imagen del producto")
    g(d, img, ("getId()", "id"), ("getFileName()", "fileName"), ("getDisplayOrder()", "displayOrder"),
      ("getUrl()", "url"))
    d.cerrar()


d = nuevo("CU-003", [actor("Visitante"), o("prod", "Product"), o("img", "ProductImage"), o("adm", "UserAdmin")])
d.abrir("alt", "listar o buscar productos (activos, categoría, nombre o id)")
d.abrir("loop", "por cada producto encontrado")
dto_producto(d)
d.cerrar()
d.sino("productos relacionados (GET /api/product/{id}/related)")
d.abrir("alt", "limit fuera de 1..20: 400 Bad Request")
d.vacio()
d.sino()
g(d, "prod", ("getCategory()", "category"))
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.abrir("loop", "por cada producto relacionado")
dto_producto(d)
d.cerrar()
d.cerrar()
d.sino("tienda dueña del producto (GET /api/product/{id}/admin)")
g(d, "prod", ("isActive()", "active"))
d.abrir("alt", "producto inactivo: 400 Bad Request")
d.vacio()
d.sino()
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
g(d, "adm", ("getBusinessName()", "businessName"), ("getFullName()", "fullName"), ("getPhone()", "phone"),
  ("getEmail()", "email"))
d.cerrar()
d.sino("imágenes de un producto (GET /api/product-images/{id})")
d.abrir("loop", "por cada imagen del producto")
g(d, "img", ("getId()", "id"), ("getFileName()", "fileName"), ("getDisplayOrder()", "displayOrder"), ("getUrl()", "url"))
d.cerrar()
d.cerrar()

# ===================================================================================================== CU-004
d = nuevo("CU-004", [actor("Visitante"), o("img", "SecondHandProductImage")])
d.abrir("alt", "catálogo de segunda mano (GET /api/sh-product/search/active)")
d.nota("Se resuelve con una consulta nativa y una proyección (ShProductCardProjection): "
       "no invoca operaciones de entidades.", 460, 56)
d.sino("imágenes de un producto (GET /api/sh-product-images/{productId})")
d.abrir("loop", "por cada imagen del producto")
g(d, "img", ("getId()", "id"), ("getFileName()", "fileName"), ("getDisplayOrder()", "displayOrder"), ("getUrl()", "url"))
d.cerrar()
d.cerrar()

# ===================================================================================================== CU-005
d = nuevo("CU-005", [actor("Visitante"), o("svc", "ServiceOffered"), o("adm", "UserAdmin")])
d.abrir("alt", "catálogo de servicios (GET /api/services/catalog/{ownerId})")
d.abrir("loop", "por cada servicio de la tienda")
g(d, "svc", ("getId()", "id"), ("getNameOfService()", "nameOfService"), ("getValueOfService()", "valueOfService"),
  ("getDescriptionOfService()", "descriptionOfService"))
d.llamar("act", "svc", "getUserAdmin()")
d.retornar("svc", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.cerrar()
d.sino("ficha de la tienda (GET /api/client/{adminId}/admin)")
d.abrir("alt", "la tienda no existe: 404 Not Found")
d.vacio()
d.sino()
g(d, "adm", ("getId()", "id"), ("getBusinessName()", "businessName"), ("getProfilePhotoUrl()", "profilePhotoUrl"))
d.cerrar()
d.cerrar()

# ===================================================================================================== CU-006
d = nuevo("CU-006", [actor("Visitante")])
d.nota("Las reseñas (por producto, resumen de estrellas y por id) se leen con consultas nativas y proyecciones "
       "(ProductReviewProjection, ProductReviewSummaryProjection) y quedan en caché: no se invocan operaciones de "
       "ProductReviews.", 520, 70)

# ===================================================================================================== CU-007
d = nuevo("CU-007", [actor("Cliente"), o("prod", "Product"), o("adm", "UserAdmin"), o("sale", "Sale"),
                     o("item", "SalesItem")])
d.abrir("alt", "clientId distinto del cliente autenticado: 403 Forbidden")
d.vacio()
d.sino("el cliente no tiene una tarjeta activa: 402 Payment Required")
d.vacio()
d.sino("algún producto no existe: 404 Not Found")
d.vacio()
d.sino()
d.abrir("loop", "por cada producto del carrito")
g(d, "prod", ("isActive()", "active"))
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
g(d, "prod", ("getStock()", "stock"))
d.abrir("alt", "inactivo, sin dueño o stock insuficiente: 409 Conflict")
d.vacio()
d.sino()
v(d, "prod", "setStock(stock - quantity)")
g(d, "prod", ("getPrice()", "price"))
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.cerrar()
d.cerrar()
d.abrir("opt", "se informa cuponCode")
d.ref("CU-009 Aplicar cupón", "act", "sale")
d.cerrar()
d.abrir("loop", "por cada producto (una venta por producto)")
d.crear("act", "sale")
v(d, "sale", "setUserClient(client)", "setHora(now)")
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
v(d, "sale", "setUserAdmin(userAdmin)", "setTotalAmount(totalAmount)")
g(d, "sale", ("getTotalAmount()", "totalAmount"))
v(d, "sale", "setDiscount(subtotal - totalAmount)", "setCupon(cupon)")
d.cerrar()
d.abrir("loop", "por cada venta guardada (un ítem por venta)")
d.crear("act", "item")
v(d, "item", "setSales(sale)", "setUserClient(client)", "setProduct(product)", "setQuantity(quantity)",
  "setState(COMPLETED)", "setDate(now)")
g(d, "prod", ("getPrice()", "price"))
v(d, "item", "setUnitPrice(price)")
g(d, "sale", ("getDiscount()", "discount"))
v(d, "item", "setDiscount(discount)")
d.cerrar()
d.abrir("loop", "por cada ítem y venta para armar la respuesta")
g(d, "item", ("getUnitPrice()", "unitPrice"), ("getQuantity()", "quantity"), ("getDiscount()", "discount"))
d.llamar("act", "item", "getProduct()")
d.retornar("item", "act", "product")
g(d, "prod", ("getId()", "id"), ("getName()", "name"))
g(d, "sale", ("getId()", "saleId"), ("getTotalAmount()", "totalAmount"))
d.cerrar()
d.cerrar()

# ===================================================================================================== CU-008
d = nuevo("CU-008", [actor("Cliente"), o("prod", "SecondHandProduct"), o("adm", "UserAdmin"), o("sale", "ShSale"),
                     o("item", "ShSalesItem")])
d.abrir("alt", "clientId distinto del cliente autenticado: 403 Forbidden")
d.vacio()
d.sino("el cliente no tiene una tarjeta activa: 402 Payment Required")
d.vacio()
d.sino("algún producto no existe: 404 Not Found")
d.vacio()
d.sino()
d.abrir("loop", "por cada producto del carrito")
g(d, "prod", ("isActive()", "active"))
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
g(d, "prod", ("getStock()", "stock"))
d.abrir("alt", "inactivo, sin dueño o stock insuficiente: 409 Conflict")
d.vacio()
d.sino()
v(d, "prod", "setStock(stock - quantity)")
g(d, "prod", ("getPrice()", "price"))
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.cerrar()
d.cerrar()
d.abrir("opt", "se informa cuponCode (cupón de segunda mano)")
d.ref("CU-009 Aplicar cupón", "act", "sale")
d.cerrar()
d.abrir("loop", "por cada producto (una venta por producto)")
d.crear("act", "sale")
v(d, "sale", "setUserClient(client)", "setHora(now)", "setUserAdmin(owner)", "setTotalAmount(totalAmount)")
g(d, "sale", ("getTotalAmount()", "totalAmount"))
v(d, "sale", "setDiscount(subtotal - totalAmount)", "setCupon(cupon)")
d.cerrar()
d.abrir("loop", "por cada venta guardada (un ítem por venta)")
d.crear("act", "item")
v(d, "item", "setShSale(sale)", "setUserClient(client)", "setShProduct(product)", "setQuantity(quantity)",
  "setState(COMPLETED)", "setDate(now)")
g(d, "prod", ("getPrice()", "price"))
v(d, "item", "setUnitPrice(price)")
g(d, "sale", ("getDiscount()", "discount"))
v(d, "item", "setDiscount(discount)")
d.cerrar()
d.abrir("loop", "por cada venta para armar la respuesta")
g(d, "sale", ("getTotalAmount()", "totalAmount"), ("getId()", "saleId"))
d.cerrar()
d.cerrar()

# ===================================================================================================== CU-009
d = nuevo("CU-009", [actor("Cliente"), o("cup", "Cupon"), o("adm", "UserAdmin"), o("cli", "UserClient"),
                     o("uso", "CuponUsedByClients")])
g(d, "cup", ("getCuponDateLimit()", "cuponDateLimit"), ("getQuantity()", "quantity"))
d.llamar("act", "cup", "getUserAdmin()")
d.retornar("cup", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.abrir("alt", "el cupón no existe o está vencido (cuponDateLimit < ahora): 409 Conflict")
d.vacio()
d.sino("el cupón agotó sus usos (quantity <= 0): 409 Conflict")
d.vacio()
d.sino("ningún producto del carrito de ese vendedor tiene el cupón: 409 Conflict")
d.vacio()
d.sino("el cupón es exclusivo y no está asignado al cliente: 403 Forbidden")
d.vacio()
d.sino("el cliente agotó sus usos del cupón (o ya lo usó): 409 Conflict")
d.vacio()
d.sino()
g(d, "cup", ("getId()", "cuponId"), ("getDiscount()", "discount"))
d.crear("act", "uso")
v(d, "uso", "setClientUser(client)", "setSale(sale)", "setCupon(cupon)", "setCreatedAt(now)")
d.llamar("act", "uso", "getCupon()")
d.retornar("uso", "act", "cupon")
g(d, "cup", ("getId()", "cuponId"))
d.llamar("act", "uso", "getClientUser()")
d.retornar("uso", "act", "client")
g(d, "cli", ("getId()", "clientId"))
d.llamar("act", "cup", "getUserAdmin()")
d.retornar("cup", "act", "userAdmin")
g(d, "adm", ("getId()", "adminId"))
v(d, "uso", "setUsageCount(used + 1)")
d.cerrar()
d.nota("Es el «extend» de CU-007 y CU-008. Para cupones de segunda mano el flujo es el mismo con SecondHandCupon y "
       "ShCuponUsedByClients. El descuento de quantity se hace con una consulta UPDATE (sin operación de entidad).",
       560, 70)

# ===================================================================================================== CU-010
d = nuevo("CU-010", [actor("Cliente")])
d.nota("«Mis cupones» (GET /api/cupons|sh-cupons|services-cupons/assignments/my) se lee con consultas nativas y "
       "proyecciones (ProductCuponAssignmentProjection y sus equivalentes): no invoca operaciones de entidades.",
       560, 70)

# ===================================================================================================== CU-011
d = nuevo("CU-011", [actor("Cliente"), o("rev", "ProductReviews"), o("prod", "Product"), o("cli", "UserClient")])
d.abrir("alt", "crear reseña (POST /api/product-reviews)")
g(d, "prod", ("isActive()", "active"))
d.abrir("alt", "producto inactivo: 409 Conflict")
d.vacio()
d.sino("el cliente no compró el producto: 403 Forbidden")
d.vacio()
d.sino("el cliente ya reseñó el producto: 409 Conflict")
d.vacio()
d.sino()
d.crear("act", "rev")
v(d, "rev", "setProduct(product)", "setUserClient(client)", "setStars(stars)", "setReviews(reviews)",
  "setCreatedAt(now)", "setUpdatedAt(now)")
d.cerrar()
d.sino("editar reseña (PUT /api/product-reviews/{reviewId})")
d.llamar("act", "rev", "getUserClient()")
d.retornar("rev", "act", "client")
g(d, "cli", ("getId()", "ownerId"))
d.abrir("alt", "la reseña es de otro cliente: 403 Forbidden")
d.vacio()
d.sino()
d.abrir("opt", "se envía stars")
v(d, "rev", "setStars(stars)")
d.cerrar()
d.abrir("opt", "se envía reviews")
v(d, "rev", "setReviews(reviews)")
d.cerrar()
v(d, "rev", "setUpdatedAt(now)")
d.cerrar()
d.sino("borrar reseña (DELETE /api/product-reviews/{reviewId})")
d.llamar("act", "rev", "getUserClient()")
d.retornar("rev", "act", "client")
g(d, "cli", ("getId()", "ownerId"))
d.abrir("alt", "la reseña es de otro cliente: 403 Forbidden")
d.vacio()
d.sino()
d.nota("La reseña se elimina con el repositorio (delete).", 320, 40)
d.cerrar()
d.cerrar()

# ===================================================================================================== CU-012
d = nuevo("CU-012", [actor("Cliente"), o("cli", "UserClient")])
d.abrir("alt", "modificar datos (PATCH /api/client/modify)")
d.abrir("opt", "se envía fullName")
v(d, "cli", "setFullName(fullName)")
d.cerrar()
d.abrir("opt", "se envía email")
d.abrir("alt", "el correo ya está registrado por otro cliente: 409 Conflict")
d.vacio()
d.sino()
v(d, "cli", "setEmail(email)")
d.cerrar()
d.cerrar()
d.sino("cambiar contraseña (PATCH /api/client/change-password)")
g(d, "cli", ("getPassword()", "passwordCifrada"))
d.abrir("alt", "contraseña actual incorrecta o igual a la nueva: 400 Bad Request")
d.vacio()
d.sino()
d.nota("La nueva contraseña se guarda con una consulta UPDATE (updatePasswordById).", 380, 44)
d.cerrar()
d.sino("subir foto (PATCH /api/client/upload-profile)")
g(d, "cli", ("getPhoto()", "oldPhoto"))
v(d, "cli", "setPhoto(secureUrl)")
d.sino("consultar foto (GET /api/client/profile-photo)")
g(d, "cli", ("getPhoto()", "photo"))
d.cerrar()

# ===================================================================================================== CU-013
d = nuevo("CU-013", [actor("Cliente"), o("card", "PaymentCard")])
d.abrir("alt", "agregar tarjeta (POST /api/client/payment-cards)")
d.abrir("alt", "lastFour no tiene 4 dígitos: 400 Bad Request")
d.vacio()
d.sino()
d.crear("act", "card")
v(d, "card", "setUserClient(client)", "setCardHolderName(cardHolderName)", "setBrand(brand)",
  "setLastFour(lastFour)", "setActive(active)")
d.cerrar()
d.sino("listar tarjetas (consulta con proyección, sin operaciones de entidad)")
d.vacio()
d.sino("activar o desactivar (UPDATE por id y cliente, sin operaciones de entidad)")
d.abrir("alt", "tarjeta inexistente o de otro cliente: 400 Bad Request")
d.vacio()
d.sino()
d.vacio()
d.cerrar()
d.cerrar()

# ===================================================================================================== CU-014
d = nuevo("CU-014", [actor("Cliente")])
d.nota("El historial (GET /api/client/user-payments y GET /api/sh-sale/client) se lee de la vista SQL de ventas con "
       "proyecciones (ClientHistoryProjection, ShSaleHistoryProjection): no invoca operaciones de entidades.",
       560, 70)

# ===================================================================================================== CU-015
d = nuevo("CU-015", [actor("Administrador"), o("adm", "UserAdmin")])
d.abrir("alt", "consultar perfil (GET /api/user/admin)")
g(d, "adm", ("getId()", "id"), ("getFullName()", "fullName"), ("getEmail()", "email"), ("getPhone()", "phone"),
  ("getProfilePhotoUrl()", "profilePhotoUrl"), ("getBusinessName()", "businessName"),
  ("getColorTypes()", "colorTypes"))
d.sino("modificar datos (PATCH /api/user/modify)")
d.abrir("opt", "se envía fullName")
v(d, "adm", "setFullName(fullName)")
d.cerrar()
d.abrir("opt", "se envía businessName")
v(d, "adm", "setBusinessName(businessName)")
d.cerrar()
d.abrir("opt", "se envía email")
g(d, "adm", ("getEmail()", "email"))
d.abrir("alt", "correo inválido: 400. Correo ya registrado: 409 Conflict")
d.vacio()
d.sino()
v(d, "adm", "setEmail(email)")
d.cerrar()
d.cerrar()
d.abrir("opt", "se envía phone")
v(d, "adm", "setPhone(phone)")
d.cerrar()
d.abrir("opt", "se envía colorTypes")
v(d, "adm", "setColorTypes(colorTypes)")
d.cerrar()
d.sino("subir foto (PATCH /api/user/upload-profile)")
g(d, "adm", ("getProfilePhoto()", "oldPhoto"))
v(d, "adm", "setProfilePhoto(publicId)", "setProfilePhotoUrl(secureUrl)")
d.cerrar()

# ===================================================================================================== CU-016
d = nuevo("CU-016", [actor("Administrador"), o("prod", "Product", "existente"), o("req", "Product", "solicitud"),
                     o("adm", "UserAdmin"), o("img", "ProductImage")])
d.abrir("alt", "crear producto (POST /api/product/saveProduct)")
v(d, "req", "setUserAdmin(userAdmin)")
d.sino("actualizar producto (PUT /api/product/update/{id})")
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.abrir("alt", "el producto es de otra tienda: 403 Forbidden")
d.vacio()
d.sino()
for campo in ("Name", "Price", "Stock", "Category"):
    g(d, "req", (f"get{campo}()", campo[0].lower() + campo[1:]))
    d.abrir("opt", f"{campo[0].lower() + campo[1:]} no es nulo")
    v(d, "prod", f"set{campo}({campo[0].lower() + campo[1:]})")
    d.cerrar()
g(d, "req", ("getDescription()", "description"))
v(d, "prod", "setDescription(description)")
d.cerrar()
d.sino("borrar producto (POST /api/product/deleteSafe)")
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.abrir("alt", "el producto es de otra tienda: 403 Forbidden")
d.vacio()
d.sino()
v(d, "prod", "setActive(false)")
d.cerrar()
d.sino("consultar mis productos (GET /api/product/search)")
d.abrir("loop", "por cada producto de la tienda")
dto_producto(d, "prod", "img", "adm")
d.cerrar()
d.cerrar()

# ===================================================================================================== CU-017
d = nuevo("CU-017", [actor("Administrador"), o("prod", "Product"), o("img", "ProductImage"), o("adm", "UserAdmin")])
d.abrir("alt", "subir imagen (POST /api/product-images/upload/{productId})")
d.abrir("alt", "archivo vacío: 400. Producto de otra tienda: 403. Límite de imágenes alcanzado: 409")
d.vacio()
d.sino()
d.crear("act", "img")
v(d, "img", "setFileName(fileName)", "setFilePath(publicId)", "setUrl(secureUrl)", "setDisplayOrder(order)",
  "setProduct(product)")
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
v(d, "img", "setUserAdmin(userAdmin)")
d.cerrar()
d.sino("borrar imagen (DELETE /api/product-images/{imageId})")
g(d, "img", ("getId()", "id"), ("getOwnerId()", "ownerId"))
d.llamar("img", "adm", "getId()")
d.retornar("adm", "img", "ownerId")
d.abrir("alt", "la imagen es de otra tienda: 403 Forbidden")
d.vacio()
d.sino()
g(d, "img", ("getFilePath()", "publicId"), ("getProduct()", "product"))
g(d, "prod", ("getId()", "productId"))
d.cerrar()
d.sino("reordenar imágenes (PATCH /api/product-images/reorder)")
d.abrir("alt", "producto de otra tienda: 403. Lista de ids distinta de las imágenes: 400")
d.vacio()
d.sino()
d.abrir("loop", "por cada id del nuevo orden")
g(d, "img", ("getId()", "id"))
v(d, "img", "setDisplayOrder(position)")
d.cerrar()
d.cerrar()
d.cerrar()
d.nota("Las imágenes de segunda mano (/api/sh-product-images) siguen el mismo flujo con SecondHandProduct y "
       "SecondHandProductImage.", 520, 50)

# ===================================================================================================== CU-018
d = nuevo("CU-018", [actor("Administrador"), o("prod", "SecondHandProduct", "existente"),
                     o("req", "SecondHandProduct", "solicitud"), o("adm", "UserAdmin")])
d.abrir("alt", "crear producto (POST /api/sh-product/saveShProduct)")
v(d, "req", "setUserAdmin(userAdmin)", "setActive(true)")
d.sino("actualizar producto (PUT /api/sh-product/update/{id})")
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.abrir("alt", "el producto es de otra tienda: 403 Forbidden")
d.vacio()
d.sino()
for campo in ("Name", "Price", "Stock", "Category", "TimeOfUse", "LevelOfSecondHandProduct"):
    low = campo[0].lower() + campo[1:]
    g(d, "req", (f"get{campo}()", low))
    d.abrir("opt", f"{low} no es nulo")
    v(d, "prod", f"set{campo}({low})")
    d.cerrar()
g(d, "req", ("getDescription()", "description"))
v(d, "prod", "setDescription(description)")
d.cerrar()
d.sino("borrar producto (POST /api/sh-product/deleteSafe)")
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.abrir("alt", "el producto es de otra tienda: 403 Forbidden")
d.vacio()
d.sino()
v(d, "prod", "setActive(false)")
d.cerrar()
d.sino("consultar un producto propio (GET /api/sh-product/{id})")
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.abrir("alt", "el producto es de otra tienda: 403 Forbidden")
d.vacio()
d.sino()
d.vacio()
d.cerrar()
d.cerrar()

# ===================================================================================================== CU-019
d = nuevo("CU-019", [actor("Administrador"), o("svc", "ServiceOffered"), o("adm", "UserAdmin")])
d.abrir("alt", "crear servicio (POST /api/services)")
d.abrir("alt", "datos inválidos: 400 Bad Request")
d.vacio()
d.sino()
d.crear("act", "svc")
v(d, "svc", "setNameOfService(nameOfService)", "setValueOfService(valueOfService)",
  "setDescriptionOfService(descriptionOfService)", "setUserAdmin(userAdmin)")
d.cerrar()
d.sino("actualizar servicio (PUT /api/services/{id})")
d.abrir("alt", "datos inválidos: 400. Servicio de otra tienda: 403. No existe: 404")
d.vacio()
d.sino()
v(d, "svc", "setNameOfService(nameOfService)", "setValueOfService(valueOfService)",
  "setDescriptionOfService(descriptionOfService)")
d.cerrar()
d.sino("borrar servicio (DELETE /api/services/{id})")
d.abrir("alt", "servicio de otra tienda: 403 Forbidden")
d.vacio()
d.sino()
d.nota("El servicio se elimina con el repositorio (deleteById).", 340, 40)
d.cerrar()
d.sino("consultar mis servicios (GET /api/services/my)")
d.abrir("loop", "por cada servicio de la tienda")
g(d, "svc", ("getId()", "id"), ("getNameOfService()", "nameOfService"), ("getValueOfService()", "valueOfService"),
  ("getDescriptionOfService()", "descriptionOfService"))
d.llamar("act", "svc", "getUserAdmin()")
d.retornar("svc", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.cerrar()
d.cerrar()

# ===================================================================================================== CU-020
d = nuevo("CU-020", [actor("Administrador"), o("cup", "Cupon"), o("prod", "Product"), o("adm", "UserAdmin"),
                     o("enl", "ProductCuponApplied")])
d.abrir("alt", "crear cupón (POST /api/cupons)")
d.abrir("loop", "por cada producto del cupón")
d.llamar("act", "prod", "getUserAdmin()")
d.retornar("prod", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.cerrar()
d.abrir("alt", "datos inválidos: 400. Producto de otra tienda: 403 Forbidden")
d.vacio()
d.sino()
d.crear("act", "cup")
v(d, "cup", "setCuponCode(cuponCode)", "setCuponDateLimit(cuponDateLimit)", "setDiscount(discount)",
  "setQuantity(quantity)", "setUserAdmin(userAdmin)")
d.abrir("loop", "por cada producto distinto")
d.crear("act", "enl")
v(d, "enl", "setCupon(cupon)", "setProduct(product)")
d.cerrar()
d.cerrar()
d.sino("actualizar cupón (PUT /api/cupons/{id})")
d.llamar("act", "cup", "getUserAdmin()")
d.retornar("cup", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.abrir("alt", "cupón de otra tienda: 403. No existe: 404. Datos inválidos: 400")
d.vacio()
d.sino()
v(d, "cup", "setCuponCode(cuponCode)", "setCuponDateLimit(cuponDateLimit)", "setDiscount(discount)",
  "setQuantity(quantity)")
d.abrir("opt", "se envían productIds")
d.abrir("loop", "por cada producto distinto")
d.ref("«create» ProductCuponApplied: igual que en «crear cupón»", "act", "enl")
d.cerrar()
d.cerrar()
d.cerrar()
d.sino("borrar cupón (DELETE /api/cupons/{id})")
d.llamar("act", "cup", "getUserAdmin()")
d.retornar("cup", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.abrir("alt", "cupón de otra tienda: 403 Forbidden")
d.vacio()
d.sino()
d.nota("Se borran asignaciones, usos y enlaces con consultas DELETE y luego el cupón con el repositorio.", 420, 50)
d.cerrar()
d.sino("consultar mis cupones (GET /api/cupons/my)")
d.nota("Consulta nativa con proyección (CuponAdminProjection): sin operaciones de entidades.", 400, 44)
d.cerrar()
d.nota("Los cupones de segunda mano (SecondHandCupon) y de servicios (ServiceCupon) siguen el mismo flujo.", 520, 44)

# ===================================================================================================== CU-021
d = nuevo("CU-021", [actor("Administrador"), o("cup", "Cupon"), o("adm", "UserAdmin"), o("cli", "UserClient"),
                     o("prod", "Product"), o("asg", "ProductCuponToAClient")])
d.abrir("alt", "asignar a clientes (POST /api/cupons/assign)")
d.llamar("act", "cup", "getUserAdmin()")
d.retornar("cup", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
g(d, "cup", ("getQuantity()", "quantity"))
d.abrir("alt", "cupón de otra tienda: 403. Sin productos: 409. usageLimit inválido: 400")
d.vacio()
d.sino()
d.abrir("opt", "assignToAll = true")
g(d, "cli", ("getId()", "clientId"))
d.cerrar()
d.abrir("loop", "por cada cliente y cada producto del cupón")
d.crear("act", "asg")
v(d, "asg", "setClient(client)", "setCupon(cupon)", "setProduct(product)", "setUsageLimit(usageLimit)")
d.cerrar()
d.cerrar()
d.sino("enviar a un cliente (POST /api/cupons/send)")
d.llamar("act", "cup", "getUserAdmin()")
d.retornar("cup", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
g(d, "cup", ("getQuantity()", "quantity"))
d.abrir("alt", "datos inválidos: 400. Cupón de otra tienda: 403. Sin productos: 409")
d.vacio()
d.sino()
g(d, "cli", ("getId()", "clientId"))
d.abrir("loop", "por cada producto del cupón")
d.abrir("opt", "el cliente aún no tiene el cupón para ese producto")
d.ref("«create» ProductCuponToAClient: igual que en «asignar a clientes»", "act", "asg")
d.cerrar()
d.cerrar()
d.cerrar()
d.sino("cambiar el límite de usos (PATCH /api/cupons/assignments/{id})")
d.llamar("act", "asg", "getCupon()")
d.retornar("asg", "act", "cupon")
d.llamar("act", "cup", "getUserAdmin()")
d.retornar("cup", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
g(d, "cup", ("getQuantity()", "quantity"))
d.abrir("alt", "asignación inexistente: 404. De otra tienda: 403. usageLimit inválido: 400")
d.vacio()
d.sino()
v(d, "asg", "setUsageLimit(usageLimit)")
d.llamar("act", "asg", "getClient()")
d.retornar("asg", "act", "client")
g(d, "cli", ("getId()", "clientId"))
d.llamar("act", "asg", "getProduct()")
d.retornar("asg", "act", "product")
g(d, "prod", ("getId()", "productId"))
d.cerrar()
d.sino("quitar una asignación (DELETE /api/cupons/assignments/{id})")
d.llamar("act", "asg", "getCupon()")
d.retornar("asg", "act", "cupon")
d.llamar("act", "cup", "getUserAdmin()")
d.retornar("cup", "act", "userAdmin")
g(d, "adm", ("getId()", "ownerId"))
d.abrir("alt", "asignación inexistente: 404. De otra tienda: 403 Forbidden")
d.vacio()
d.sino()
d.vacio()
d.cerrar()
d.cerrar()
d.nota("Listados de asignaciones: consultas con proyecciones (sin operaciones de entidades). Segunda mano y "
       "servicios: mismo flujo con sus cupones y asignaciones.", 540, 56)

# ===================================================================================================== CU-022 a CU-024
d = nuevo("CU-022", [actor("Administrador")])
d.nota("El dashboard (get-data-dashboard) y los reportes (excel, pdf) se arman con la vista SQL view_of_dashboard, "
       "funciones de ventana y proyecciones (DashboardProjection, ReportDashboardProjection); ExcelService y PdfService "
       "(patrón Factory) escriben el archivo: no se invocan operaciones de entidades.", 600, 80)
d = nuevo("CU-023", [actor("Administrador")])
d.nota("Los rankings (best-clients, best-products) se leen de vistas materializadas (refrescadas cada 5 minutos) "
       "con proyecciones: no se invocan operaciones de entidades.", 560, 70)
d = nuevo("CU-024", [actor("Administrador")])
d.nota("Las ventas (/api/sales-items, GET /api/sh-sale/admin) y el resumen de clientes (/api/client-show-summary) se "
       "leen de las vistas SQL sales_view y clients_summary con proyecciones (SaleItemViewProjection, "
       "ClientSummaryProjection, ShSaleHistoryProjection): no se invocan operaciones de entidades.", 600, 90)

# ===================================================================================================== CU-025
d = nuevo("CU-025", [actor("Cliente y Administrador")])
d.nota("Pendiente de implementar", 300, 50)


def main():
    """Exporta los SVG con el visor oficial. El XML solo se reescribe con --regenerar-xml (descarta retoques)."""
    if "--regenerar-xml" in sys.argv:
        cuerpo = "".join(dg.xml() for dg in diagramas)
        with open(SALIDA, "w", encoding="utf-8") as f:
            f.write('<?xml version="1.0" encoding="UTF-8"?>\n<mxfile host="app.diagrams.net">\n' + cuerpo + "</mxfile>\n")
        print(f"{len(diagramas)} diagramas (xml) -> {SALIDA}")
    else:
        print("XML sin tocar (usa --regenerar-xml para reescribirlo; se pierden los retoques hechos en draw.io)")
    if "--sin-svg" not in sys.argv:
        import subprocess
        subprocess.run([sys.executable, os.path.join(AQUI, "exportar_secuencia_svg.py")], check=False)
        subprocess.run([sys.executable, os.path.join(AQUI, "fondo_claro.py")], check=False)


if __name__ == "__main__":
    main()
