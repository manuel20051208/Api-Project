"""Genera diagrams/UML/casos-de-uso.drawio.xml (diagrama de casos de uso, draw.io) a partir de casos_uso_datos.py.

Notacion OMG UML 2.5.1 (formal/17-12-05), clausula 18.1.4 (Use cases) y 9.2.4 (generalizacion):
  - actores = umlActor; casos de uso = elipses dentro del rectangulo del sistema;
  - asociacion actor-caso de uso = linea solida sin flecha;
  - generalizacion entre actores = linea solida con triangulo hueco hacia el actor general (cada una con su camino);
  - <<extend>> = flecha punteada con punta abierta, del caso que extiende al extendido (Fig. 18.3);
  - nota (7.2.4) = rectangulo con esquina doblada unido con linea punteada.
Los puntos de union sobre las elipses caen en el perimetro (entryPerimeter=0) y sobre el actor solo en cabeza,
manos y pies (los unicos puntos que draw.io deja pegados al recuadro del umlActor).

Uso: python diagrams/_fuente/gen_casos_uso.py
"""
import html
import math
import os
import sys
import xml.etree.ElementTree as ET

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from casos_uso_datos import CASOS  # noqa: E402

AQUI = os.path.dirname(os.path.abspath(__file__))
SALIDA = os.path.join(AQUI, "..", "UML", "casos-de-uso.drawio.xml")

OW, OH = 190, 60            # tamano de cada elipse
nombres = {c[0]: c[1] for c in CASOS}

# ------------------------------------------------------------------ geometria (todo fijo)
SIS = dict(x=170, y=190, w=1260, h=1110)
ACT_W, ACT_H = 30, 60
ACTORES = {                 # centro x, y superior
    "V": dict(nombre="Visitante", cx=800, y=60),
    "C": dict(nombre="Cliente", cx=75, y=705),
    "A": dict(nombre="Administrador", cx=1570, y=760),
}
POS = {}
for i, cu in enumerate(["CU-001", "CU-002", "CU-003", "CU-004", "CU-005", "CU-006"]):   # fila publica
    POS[cu] = (300 + i * 200, 270)
CLI = ["CU-007", "CU-008", "CU-010", "CU-011", "CU-012", "CU-013", "CU-014"]
for i, cu in enumerate(CLI):                                                             # columna del cliente
    POS[cu] = (330, 450 + i * 95)
POS["CU-009"] = (720, 497)
ADM = ["CU-015", "CU-016", "CU-017", "CU-018", "CU-019", "CU-020", "CU-021", "CU-022", "CU-023", "CU-024"]
for i, cu in enumerate(ADM):                                                             # columna del administrador
    POS[cu] = (1300, 430 + i * 80)
POS["CU-025"] = (800, 1230)


def esc(s):
    return html.escape(s, quote=True)


def etq(s):
    return html.escape(s, quote=False)


celdas = []
ids = {}
_n = [0]


def nuevo_id(p):
    _n[0] += 1
    return f"{p}{_n[0]}"


# ------------------------------------------------------------------ puntos de union
def perimetro(cu, px, py):
    """Fraccion (entryX, entryY) del punto del perimetro de la elipse en direccion a (px, py)."""
    cx, cy = POS[cu]
    dx, dy = px - cx, py - cy
    t = 1.0 / math.sqrt((dx / (OW / 2)) ** 2 + (dy / (OH / 2)) ** 2)
    x, y = cx + dx * t, cy + dy * t
    return (x - (cx - OW / 2)) / OW, (y - (cy - OH / 2)) / OH, (x, y)


ANCLAJES = {"cabeza": (0.5, 0), "mano_i": (0, 1 / 3), "mano_d": (1, 1 / 3), "pie_i": (0, 1), "pie_d": (1, 1)}


def punto_actor(a, anclaje):
    d = ACTORES[a]
    x0, y0 = d["cx"] - ACT_W / 2, d["y"]
    fx, fy = ANCLAJES[anclaje]
    return x0 + ACT_W * fx, y0 + ACT_H * fy


segmentos = []     # (id, [(x,y)...]) para verificar cruces


def arista(estilo, origen, destino, puntos=(), valor=""):
    eid = nuevo_id("e")
    pts = "".join(f'<mxPoint x="{x:g}" y="{y:g}"/>' for x, y in puntos)
    geo = (f'<mxGeometry relative="1" as="geometry"><Array as="points">{pts}</Array></mxGeometry>'
           if pts else '<mxGeometry relative="1" as="geometry"/>')
    celdas.append(f'<mxCell id="{eid}" value="{esc(valor)}" style="{estilo}" edge="1" parent="1" '
                  f'source="{origen}" target="{destino}">{geo}</mxCell>')
    return eid


# ------------------------------------------------------------------ construccion
celdas.append(
    f'<mxCell id="sis" value="{esc("ApiProject (tienda en línea)")}" style="rounded=1;whiteSpace=wrap;html=1;'
    f'arcSize=2;verticalAlign=top;align=center;fontStyle=1;fontSize=15;fillColor=#fafafa;strokeColor=#666666;'
    f'strokeWidth=1.5;spacingTop=172;" vertex="1" parent="1"><mxGeometry x="{SIS["x"]}" y="{SIS["y"]}" '
    f'width="{SIS["w"]}" height="{SIS["h"]}" as="geometry"/></mxCell>')

for cu, (cx, cy) in POS.items():
    ids[cu] = f"cu{cu[3:]}"
    celdas.append(
        f'<mxCell id="{ids[cu]}" value="{esc("<b>" + cu + "</b><br>" + etq(nombres[cu]))}" style="ellipse;whiteSpace=wrap;'
        f'html=1;fontSize=12;fillColor=#e1d5e7;strokeColor=#9673a6;" vertex="1" parent="1">'
        f'<mxGeometry x="{cx - OW / 2:g}" y="{cy - OH / 2:g}" width="{OW}" height="{OH}" as="geometry"/></mxCell>')

LADO_ETIQUETA = {"V": "verticalLabelPosition=top;verticalAlign=bottom;",
                 "C": "labelPosition=left;verticalLabelPosition=middle;align=right;verticalAlign=middle;spacingRight=-6;",
                 "A": "labelPosition=right;verticalLabelPosition=middle;align=left;verticalAlign=middle;spacingLeft=-6;"}
for a, d in ACTORES.items():
    ids[a] = f"act{a}"
    celdas.append(
        f'<mxCell id="{ids[a]}" value="{esc(d["nombre"])}" style="shape=umlActor;{LADO_ETIQUETA[a]}html=1;'
        f'outlineConnect=0;fontStyle=1;fontSize=13;fillColor=#f5f5f5;strokeColor=#666666;" vertex="1" parent="1">'
        f'<mxGeometry x="{d["cx"] - ACT_W / 2:g}" y="{d["y"]}" width="{ACT_W}" height="{ACT_H}" as="geometry"/></mxCell>')

ASOC = ("rounded=0;html=1;endArrow=none;strokeColor=#000000;strokeWidth=1;"
        "exitPerimeter=0;entryPerimeter=0;exitDx=0;exitDy=0;entryDx=0;entryDy=0;")


ENTRADAS = {"arriba": (0.5, 0), "izq": (0, 0.5), "der": (1, 0.5)}


def asociar(a, cu, anclaje, lado):
    """La linea llega al extremo de la elipse que mira al actor: asi nunca roza a las elipses vecinas de la columna."""
    sx, sy = punto_actor(a, anclaje)
    fx, fy = ANCLAJES[anclaje]
    ex, ey = ENTRADAS[lado]
    px, py = POS[cu][0] - OW / 2 + OW * ex, POS[cu][1] - OH / 2 + OH * ey
    arista(ASOC + f"exitX={fx:.4f};exitY={fy:.4f};entryX={ex:.4f};entryY={ey:.4f};", ids[a], ids[cu])
    segmentos.append((f"{a}-{cu}", [(sx, sy), (px, py)]))


for cu in ["CU-001", "CU-002", "CU-003"]:          # Visitante: pie izquierdo / pie derecho
    asociar("V", cu, "pie_i", "arriba")
for cu in ["CU-004", "CU-005", "CU-006"]:
    asociar("V", cu, "pie_d", "arriba")
mitad = (len(CLI) + 1) // 2                         # Cliente: mano derecha (arriba) y pie derecho (abajo)
for cu in CLI[:mitad]:
    asociar("C", cu, "mano_d", "izq")
for cu in CLI[mitad:]:
    asociar("C", cu, "pie_d", "izq")
mitad = (len(ADM) + 1) // 2                         # Administrador: mano izquierda (arriba) y pie izquierdo (abajo)
for cu in ADM[:mitad]:
    asociar("A", cu, "mano_i", "der")
for cu in ADM[mitad:]:
    asociar("A", cu, "pie_i", "der")

# CU-025 Cancelar venta: Cliente y Administrador por el margen inferior (cada uno con su camino)
Y_BAJO = SIS["y"] + SIS["h"] + 30
for a, anclaje, lado in (("C", "pie_i", -1), ("A", "pie_d", 1)):
    sx, sy = punto_actor(a, anclaje)
    cx, cy = POS["CU-025"]
    fy = 0.93
    fx = 0.5 + lado * 0.5 * math.sqrt(1 - ((fy - 0.5) / 0.5) ** 2)
    ex, ey = cx - OW / 2 + OW * fx, cy - OH / 2 + OH * fy
    fxa, fya = ANCLAJES[anclaje]
    estilo = (ASOC + "edgeStyle=orthogonalEdgeStyle;"
              f"exitX={fxa:.4f};exitY={fya:.4f};entryX={fx:.4f};entryY={fy:.4f};")
    arista(estilo, ids[a], ids["CU-025"], [(sx, Y_BAJO), (ex, Y_BAJO)])
    segmentos.append((f"{a}-CU-025", [(sx, sy), (sx, Y_BAJO), (ex, Y_BAJO), (ex, ey)]))

# generalizaciones: Cliente -> Visitante y Administrador -> Visitante (cada una con su camino y su triangulo)
GEN = ("edgeStyle=orthogonalEdgeStyle;rounded=0;html=1;endArrow=block;endFill=0;endSize=14;strokeColor=#000000;"
       "exitPerimeter=0;entryPerimeter=0;exitDx=0;exitDy=0;entryDx=0;entryDy=0;")
vx0, vy0 = punto_actor("V", "mano_i")
cxh, cyh = punto_actor("C", "cabeza")
arista(GEN + "exitX=0.5;exitY=0;entryX=0;entryY=0.3333;", ids["C"], ids["V"], [(cxh, vy0)])
segmentos.append(("gen-C", [(cxh, cyh), (cxh, vy0), (vx0, vy0)]))
vx1, vy1 = punto_actor("V", "mano_d")
axh, ayh = punto_actor("A", "cabeza")
arista(GEN + "exitX=0.5;exitY=0;entryX=1;entryY=0.3333;", ids["A"], ids["V"], [(axh, vy1)])
segmentos.append(("gen-A", [(axh, ayh), (axh, vy1), (vx1, vy1)]))

# <<extend>>: Aplicar cupon extiende a Comprar productos y a Comprar producto de segunda mano (17.1.4 / 18.1.4)
EXT = ("rounded=0;html=1;dashed=1;endArrow=open;endFill=0;endSize=10;strokeColor=#000000;fontSize=11;"
       "exitPerimeter=0;entryPerimeter=0;exitDx=0;exitDy=0;entryDx=0;entryDy=0;")
for destino in ("CU-007", "CU-008"):
    ox, oy = POS["CU-009"]
    dx, dy = POS[destino]
    sx, sy, (spx, spy) = perimetro("CU-009", dx, dy)
    ex, ey, (epx, epy) = perimetro(destino, ox, oy)
    arista(EXT + f"exitX={sx:.4f};exitY={sy:.4f};entryX={ex:.4f};entryY={ey:.4f};",
           ids["CU-009"], ids[destino], valor="«extend»")
    segmentos.append((f"ext-{destino}", [(spx, spy), (epx, epy)]))

# nota (7.2.4): Cancelar venta pendiente
nx, ny, nw, nh = 980, 1185, 320, 90
celdas.append(
    f'<mxCell id="nota1" value="{esc("<b>Pendiente de implementar</b><br>Administrador: siempre. Cliente: dentro de 24 h, la venta completa o por ítem.")}" '
    f'style="shape=note;whiteSpace=wrap;html=1;size=14;align=left;verticalAlign=top;spacing=6;fontSize=11;fillColor=#fff2cc;'
    f'strokeColor=#d6b656;" vertex="1" parent="1"><mxGeometry x="{nx}" y="{ny}" width="{nw}" height="{nh}" as="geometry"/></mxCell>')
sx, sy, _ = perimetro("CU-025", nx, ny + nh / 2)
arista("rounded=0;html=1;dashed=1;endArrow=none;strokeColor=#000000;exitPerimeter=0;entryPerimeter=0;"
       f"exitX={sx:.4f};exitY={sy:.4f};entryX=0;entryY=0.5;exitDx=0;exitDy=0;entryDx=0;entryDy=0;",
       ids["CU-025"], "nota1")

celdas.append(
    f'<mxCell id="nota2" value="{esc("Cliente y Administrador heredan lo que puede hacer el Visitante (generalización): catálogo, reseñas públicas, registro e inicio de sesión.")}" '
    f'style="shape=note;whiteSpace=wrap;html=1;size=14;align=left;verticalAlign=top;spacing=6;fontSize=11;fillColor=#fff2cc;'
    f'strokeColor=#d6b656;" vertex="1" parent="1"><mxGeometry x="195" y="1195" width="340" height="80" as="geometry"/></mxCell>')

ANCHO, ALTO = 1700, 1400
marco = (f'<mxCell id="marco" value="{esc("uc Casos de uso de ApiProject")}" style="shape=umlFrame;whiteSpace=wrap;html=1;'
         f'pointerEvents=0;fillColor=none;strokeColor=#000000;fontStyle=1;fontSize=13;align=left;spacingLeft=6;'
         f'width=270;height=28;" vertex="1" parent="1"><mxGeometry x="10" y="10" width="{ANCHO - 20}" '
         f'height="{ALTO - 20}" as="geometry"/></mxCell>')

xml = ('<?xml version="1.0" encoding="UTF-8"?>\n<mxfile host="app.diagrams.net">\n'
       '  <diagram name="Casos de uso" id="casos-de-uso">\n'
       f'    <mxGraphModel dx="{ANCHO}" dy="{ALTO}" grid="1" gridSize="10" guides="1" tooltips="1" connect="1" '
       f'arrows="1" fold="1" page="1" pageScale="1" pageWidth="{ANCHO}" pageHeight="{ALTO}" math="0" shadow="0">\n'
       '      <root>\n        <mxCell id="0"/>\n        <mxCell id="1" parent="0"/>\n        '
       + marco + "\n" + "".join(f"        {c}\n" for c in celdas)
       + "      </root>\n    </mxGraphModel>\n  </diagram>\n</mxfile>\n")
ET.fromstring(xml.encode("utf-8"))
os.makedirs(os.path.dirname(SALIDA), exist_ok=True)
with open(SALIDA, "w", encoding="utf-8") as f:
    f.write(xml)


# ------------------------------------------------------------------ verificacion geometrica propia
def cruza(p1, p2, p3, p4):
    def o(a, b, c):
        return (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0])
    return o(p3, p4, p1) * o(p3, p4, p2) < 0 and o(p1, p2, p3) * o(p1, p2, p4) < 0


problemas = 0
for i in range(len(segmentos)):
    for j in range(i + 1, len(segmentos)):
        a, pa = segmentos[i]
        b, pb = segmentos[j]
        for k in range(len(pa) - 1):
            for m in range(len(pb) - 1):
                if cruza(pa[k], pa[k + 1], pb[m], pb[m + 1]):
                    print("CRUCE", a, b)
                    problemas += 1
for sid, pts in segmentos:       # lineas que pasan por encima de elipses ajenas
    for k in range(len(pts) - 1):
        (x1, y1), (x2, y2) = pts[k], pts[k + 1]
        for cu, (cx, cy) in POS.items():
            if cu in sid:
                continue
            for t in [i / 40 for i in range(1, 40)]:
                x, y = x1 + (x2 - x1) * t, y1 + (y2 - y1) * t
                if ((x - cx) / (OW / 2 + 1)) ** 2 + ((y - cy) / (OH / 2 + 1)) ** 2 < 1:
                    print("SOBRE ELIPSE", sid, cu)
                    problemas += 1
                    break
print(f"{len(POS)} casos de uso, {len(segmentos)} lineas, {problemas} problemas -> {os.path.normpath(SALIDA)}")
