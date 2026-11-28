"""Diagrama de componentes UML del backend ApiProject (interfaces provistas = bola, requeridas = socket).
Notacion segun OMG UML 2.5.1 (formal/17-12-05):
- 11.6.4: Component = rectangulo con «component» y el icono de componente arriba a la derecha.
- 10.4.4: interfaz provista = bola (lollipop); requerida = socket; ensamblaje = bola dentro del socket.
- 9.2.4.1: palabra clave entre comillas angulares encima del nombre en negrita.
Los numeros salen del codigo (contados el 2026-11-28): 18 @RestController, 19 @Service de negocio
(services.admin/general/client), 29 *Repository y 17 proyecciones, migraciones Flyway V1..V11.

Genera el XML de draw.io. Exportar despues con:
  python diagrams/_fuente/exportar_drawio_svg.py diagrams/Componentes/componentes-backend.xml diagrams/Componentes/componentes-backend.drawio.svg

Uso: python gen_componentes.py [carpeta-salida]     (por defecto diagrams/Componentes)
"""
import sys, os, math, base64
from xml.sax.saxutils import escape

AQUI = os.path.dirname(os.path.abspath(__file__))
OUT = sys.argv[1] if len(sys.argv) > 1 else os.path.join(AQUI, "..", "Componentes")
PAGE_W, PAGE_H = 1700, 1240
q = {'"': "&quot;"}

# tipo: int (interno), core (servicios), ext (externo), db
COMP = {
    "front": ("ext", "«componente externo»", "Front-end web", ["cliente HTTP (navegador)", "origen CORS configurable"], (20, 200, 220, 130)),
    "goo": ("ext", "«sistema externo»", "Google", ["OAuth2 / OpenID Connect"], (20, 480, 220, 130)),
    "cld": ("ext", "«sistema externo»", "Cloudinary", ["almacenamiento de imágenes"], (1420, 200, 240, 130)),
    "api": ("int", "«component»", "API REST", ["controllers · 18 @RestController", "Swagger / OpenAPI (springdoc)"], (480, 210, 260, 130)),
    "seg": ("int", "«component»", "Seguridad", ["security · config", "JwtAuthenticationFilter, JwtService", "OAuth2SuccessHandler, SecurityConfig"], (400, 480, 240, 130)),
    "srv": ("core", "«component»", "Servicios de negocio", ["services.admin · general · client", "19 @Service · caché Caffeine"], (700, 470, 330, 150)),
    "rep": ("int", "«component»", "Reportes", ["services.reportGenerator", "PdfService, ExcelService", "ReportServiceFactory"], (1120, 490, 200, 120)),
    "per": ("int", "«component»", "Persistencia", ["repositories · 29 *Repository", "17 proyecciones · Spring Data JPA"], (700, 770, 300, 120)),
    "tar": ("int", "«component»", "Tareas programadas", ["services.databasefunctions", "MaterializedViewRefreshService", "@Scheduled cada 5 min"], (1120, 770, 200, 120)),
    "obs": ("int", "«component»", "Observabilidad", ["Spring Boot Actuator", "Micrometer → Prometheus"], (360, 900, 220, 100)),
    "pg": ("db", "«base de datos»", "PostgreSQL 16", ["esquema migrado con Flyway", "V1 … V11"], (700, 1080, 300, 120)),
}
BOUND = (320, 105, 1030, 925)
BOUND_LABEL = "«subsystem» ApiProject — Spring Boot 4.1 (paquete com.apiproject)"

# conectores de ensamblaje:
#  p: punto en el borde del componente que provee, b: bola
#  r: polilinea desde el borde del que requiere hasta b (se recorta en el socket); None = solo interfaz provista
#  label: (texto-lineas, x, y, ancla)
ASM = [
    dict(p=(480, 265), b=(450, 265), r=[(240, 265), (450, 265)],
         label=(["API REST /api/**", "JSON + JWT Bearer"], 330, 292, "start")),
    dict(p=(520, 480), b=(520, 450), r=[(520, 340), (520, 450)],
         label=(["AuthenticatedUser", "filtro JWT · roles"], 536, 405, "start")),
    dict(p=(240, 545), b=(275, 545), r=[(400, 545), (275, 545)],
         label=(["OAuth2 /", "OpenID Connect"], 250, 575, "start")),
    dict(p=(720, 470), b=(720, 440), r=[(720, 340), (720, 440)],
         label=(["*Service (19 @Service)"], 736, 405, "start")),
    dict(p=(1420, 265), b=(1390, 265), r=[(1000, 470), (1000, 265), (1390, 265)],
         label=(["Upload API", "(cloudinary-http45)"], 1376, 226, "end")),
    dict(p=(1120, 545), b=(1090, 545), r=[(1030, 545), (1090, 545)],
         label=(["ReportService", "(PDF / Excel)"], 1075, 578, "middle")),
    dict(p=(850, 770), b=(850, 740), r=[(850, 620), (850, 740)],
         label=(["*Repository"], 866, 745, "start")),
    dict(p=(700, 830), b=(670, 830), r=[(480, 610), (480, 830), (670, 830)],
         label=(["UserRepository", "ClientRepository"], 492, 795, "start")),
    dict(p=(1000, 830), b=(1030, 830), r=[(1120, 830), (1030, 830)],
         label=(["Product", "Repository"], 1075, 795, "middle")),
    dict(p=(850, 1080), b=(850, 1050), r=[(850, 890), (850, 1050)],
         label=(["JDBC (HikariCP)"], 866, 1055, "start")),
    dict(p=(1000, 1140), b=(1030, 1140), r=[(1220, 890), (1220, 1140), (1030, 1140)],
         label=(["JDBC (JdbcTemplate)"], 1046, 1170, "start")),
    dict(p=(580, 950), b=(610, 950), r=None,
         label=(["Actuator", "/actuator/** (ADMIN)"], 625, 938, "start")),
]
RB, RS = 8, 14   # radio bola, radio socket
LINE = "#3d3d3d"

TITLE = "Diagrama de componentes — ApiProject (backend)"
NOTE = ("○ interfaz provista   ◡ interfaz requerida (socket).  "
        "Cada conector une el componente que la requiere con el que la implementa.")

COLORS = {"int": ("#dae8fc", "#6c8ebf"), "core": ("#d5e8d4", "#82b366"),
          "ext": ("#f5f5f5", "#666666"), "db": ("#e1d5e7", "#9673a6")}


def unit(a, b):
    dx, dy = b[0] - a[0], b[1] - a[1]
    n = math.hypot(dx, dy)
    return dx / n, dy / n


def socket_geom(a):
    """devuelve (d_prov, linea del requeridor recortada en el vertice del socket)."""
    B = a["b"]
    dp = unit(B, a["p"])                      # hacia el proveedor
    if a["r"] is None:
        return dp, None
    pts = list(a["r"])
    pts[-1] = (B[0] - RS * dp[0], B[1] - RS * dp[1])
    return dp, pts


cells = []
n = [0]


def nid(p):
    n[0] += 1
    return f"{p}{n[0]}"


def text_cell(html, x, y, w, h, style_extra=""):
    cells.append(f'<mxCell id="{nid("t")}" value="{escape(html, q)}" style="text;html=1;{style_extra}" vertex="1" '
                 f'parent="1"><mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry"/></mxCell>')


def edge(points, w=1.4):
    (sx, sy), (tx, ty) = points[0], points[-1]
    mid = "".join(f'<mxPoint x="{x}" y="{y}"/>' for x, y in points[1:-1])
    arr = f'<Array as="points">{mid}</Array>' if mid else ""
    cells.append(f'<mxCell id="{nid("e")}" value="" style="endArrow=none;html=1;rounded=0;strokeColor={LINE};'
                 f'strokeWidth={w};edgeStyle=none;" edge="1" parent="1"><mxGeometry relative="1" as="geometry">'
                 f'<mxPoint x="{sx}" y="{sy}" as="sourcePoint"/><mxPoint x="{tx}" y="{ty}" as="targetPoint"/>{arr}'
                 f'</mxGeometry></mxCell>')


def socket_img(dp):
    c = 16
    perp = (-dp[1], dp[0])
    p1 = (c + RS * perp[0], c + RS * perp[1]); p2 = (c - RS * perp[0], c - RS * perp[1])
    svg = (f'<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32">'
           f'<path d="M{p1[0]:.1f},{p1[1]:.1f} A{RS},{RS} 0 0 1 {p2[0]:.1f},{p2[1]:.1f}" fill="none" '
           f'stroke="{LINE}" stroke-width="1.6"/></svg>')
    return base64.b64encode(svg.encode()).decode()


text_cell(f"<b>{TITLE}</b>", 40, 18, 900, 28, "fontSize=18;align=left;verticalAlign=middle;")
text_cell(NOTE, 40, 48, 1200, 24, "fontSize=12;align=left;verticalAlign=middle;fontColor=#444444;")
bx, by, bw, bh = BOUND
cells.append(f'<mxCell id="bound" value="{escape("<b>" + BOUND_LABEL + "</b>", q)}" style="rounded=0;html=1;'
             f'dashed=1;dashPattern=8 4;fillColor=#fbfcfe;strokeColor=#7a8aa6;verticalAlign=top;align=left;'
             f'spacingLeft=12;spacingTop=4;fontSize=13;fontColor=#33415c;" vertex="1" parent="1">'
             f'<mxGeometry x="{bx}" y="{by}" width="{bw}" height="{bh}" as="geometry"/></mxCell>')
for k, (kind, stereo, name, det, (x, y, w, h)) in COMP.items():
    f, st = COLORS[kind]
    html = (f'<font style="font-size:11px" color="#555555">{escape(stereo)}</font><br><b style="font-size:15px">'
            f'{escape(name)}</b><br><font style="font-size:11px">{"<br>".join(escape(t) for t in det)}</font>')
    if kind == "db":
        style = f"shape=cylinder3;boundedLbl=1;size=14;whiteSpace=wrap;html=1;fillColor={f};strokeColor={st};"
    else:
        style = f"rounded=0;whiteSpace=wrap;html=1;fillColor={f};strokeColor={st};verticalAlign=middle;"
    cells.append(f'<mxCell id="c_{k}" value="{escape(html, q)}" style="{style}" vertex="1" parent="1">'
                 f'<mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry"/></mxCell>')
    if kind != "db":
        cells.append(f'<mxCell id="i_{k}" value="" style="shape=component;jettyWidth=8;jettyHeight=4;html=1;'
                     f'fillColor=#ffffff;strokeColor=#555555;" vertex="1" parent="c_{k}">'
                     f'<mxGeometry x="{w - 30}" y="10" width="20" height="20" as="geometry"/></mxCell>')

for a in ASM:
    dp, pts = socket_geom(a)
    B = a["b"]
    edge([a["p"], (B[0] + RB * dp[0], B[1] + RB * dp[1])])
    if pts:
        edge(pts)
    cells.append(f'<mxCell id="{nid("b")}" value="" style="ellipse;html=1;fillColor=#ffffff;strokeColor={LINE};'
                 f'strokeWidth=1.6;" vertex="1" parent="1"><mxGeometry x="{B[0] - RB}" y="{B[1] - RB}" '
                 f'width="{2 * RB}" height="{2 * RB}" as="geometry"/></mxCell>')
    if pts:
        cells.append(f'<mxCell id="{nid("s")}" value="" style="shape=image;html=1;imageAspect=0;aspect=fixed;'
                     f'image=data:image/svg+xml,{socket_img(dp)};" vertex="1" parent="1"><mxGeometry x="{B[0] - 16}" '
                     f'y="{B[1] - 16}" width="32" height="32" as="geometry"/></mxCell>')
    lines, lx, ly, anc = a["label"]
    wlab = max(len(t) for t in lines) * 7 + 10
    hl = 15 * len(lines)
    x0 = {"start": lx, "middle": lx - wlab / 2, "end": lx - wlab}[anc]
    al = {"start": "left", "middle": "center", "end": "right"}[anc]
    text_cell("<br>".join(escape(t) for t in lines), x0, ly - 12, wlab, hl,
              f"fontSize=11;align={al};verticalAlign=top;labelBackgroundColor=#ffffff;spacing=0;")

xml = ('<?xml version="1.0" encoding="UTF-8"?>\n<mxfile host="app.diagrams.net">\n'
       '  <diagram name="Componentes" id="componentes-backend">\n'
       f'    <mxGraphModel grid="1" gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" page="1" '
       f'pageScale="1" pageWidth="{PAGE_W}" pageHeight="{PAGE_H}" background="#ffffff" math="0" shadow="0">\n'
       '      <root>\n        <mxCell id="0"/>\n        <mxCell id="1" parent="0"/>\n        '
       + "\n        ".join(cells) + "\n      </root>\n    </mxGraphModel>\n  </diagram>\n</mxfile>\n")
os.makedirs(OUT, exist_ok=True)
salida = os.path.join(OUT, "componentes-backend.xml")
open(salida, "w", encoding="utf-8").write(xml)
print(salida)
