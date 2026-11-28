"""Genera diagrams/Clases/entidades.xml (diagrama de clases de las entidades JPA, draw.io) LEYENDO el codigo:
src/main/java/com/apiproject/entities/** y enums/ (ver parse_entidades.py). Nada de atributos o metodos escritos a mano.

Notacion OMG UML 2.5.1 (formal/17-12-05):
  9.2.4   clasificador: nombre centrado y en negrita; palabra clave <<enumeration>> centrada encima del nombre;
  11.5.4  (pag. 202, Fig. 11.33 pag. 207) asociacion, agregacion y composicion; 9.5.3 (pag. 112) multiplicidad;
  7.2.4   nota (esquina doblada, unida con linea punteada).
Regla obligatoria de relaciones y multiplicidad (skill diagramas-uml, 2026-11-17):
  - el rombo va SIEMPRE en el extremo del TODO (la clase que tiene la coleccion); el todo es el `source` de la arista;
  - la multiplicidad va JUNTO A LA CLASE QUE CUENTA: el numero junto a Product dice cuantos Product tiene UN UserAdmin;
  - las relaciones salen SOLO de ATRIBUTOS (@ManyToOne/@OneToMany/@OneToOne o tipo enum); una arista por atributo;
    las dos puntas de una asociacion bidireccional (mappedBy) son UNA sola arista;
  - composicion (rombo lleno) solo si el todo tiene la coleccion con cascade ALL/REMOVE u orphanRemoval.
  - un extremo lleva numero solo si la clase del OTRO lado tiene un atributo hacia el.

Distribucion: la REJILLA (FILAS) y los pasillos son coordenadas fijas de este script. Las flechas las enruta un
enrutador ortogonal en malla de 10 px (A* con penalizacion de giros y cruces, un carril propio por flecha, puntos de
salida/llegada fijos y rectos de 30 px junto a cada caja) con semilla fija: mismo codigo -> mismo dibujo.
Si cambia un campo, la caja crece y las flechas se vuelven a enrutar solas.

Uso:  python diagrams/_fuente/gen_clases.py            (escribe diagrams/Clases/entidades.xml)
Luego: python diagrams/_fuente/exportar_drawio_svg.py diagrams/Clases/entidades.xml diagrams/Clases/entidades.drawio.svg
       python diagrams/_fuente/fondo_claro.py
"""
import heapq
import html
import math
import os
import random
import sys
import xml.etree.ElementTree as ET

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from parse_entidades import leer_entidades, relaciones  # noqa: E402

AQUI = os.path.dirname(os.path.abspath(__file__))
SALIDA = os.path.join(AQUI, "..", "Clases", "entidades.xml")

from modelo_clases import *  # noqa: E402,F401,F403
from modelo_clases import (GAP_X, GAP_Y, MARGEN, NOTA, NOTA_JUNTO_A, U, CHAR, LINEA, CAB, COLORES, esc, txt,  # noqa: E402
                           clases, enums, rels, enums_usados, cajas, nota_texto)

# ---------------------------------------------------------------------------------------------- REJILLA FIJA
# Cada fila es una lista de columnas; None = hueco. NOTA es la nota de Lombok. Coordenadas fijas del diagrama.
FILAS = [
    ["NOTA", "Sale", None, "CuponUsedByClients", "Cupon", "ProductCuponApplied", None],
    ["ReportDashboard", None, "Status", "SalesItem", "ProductCuponToAClient", "ProductReviews", "Product"],
    [None, "PaymentCard", "ShSalesItem", "UserClient", "ColorTypes", None, "ProductImage"],
    ["SaleStatus", None, "ShSale", "ShCuponUsedByClients", "UserAdmin", "ServiceOffered", None],
    [None, "ShProductCuponToAClient", "SecondHandCupon", None, None, "ServiceCuponToAClient", None],
    [None, "SecondHandProductCuponsApplied", "SecondHandProduct", "SecondHandProductImage", None, "ServiceCupon",
     "ServiceCuponUsedByClients"],
]

if os.environ.get("CLASES_FILAS"):          # solo para experimentos del optimizador
    FILAS = eval(open(os.environ["CLASES_FILAS"], encoding="utf-8").read())
if os.environ.get("CLASES_SALIDA"):
    SALIDA = os.environ["CLASES_SALIDA"]
en_rejilla = {n for f in FILAS for n in f if n}
faltan = set(cajas) - en_rejilla
sobran = en_rejilla - set(cajas)
if faltan or sobran:
    raise SystemExit(f"la rejilla no coincide con el codigo. Faltan en FILAS: {sorted(faltan)}. Sobran: {sorted(sobran)}")


# ---------------------------------------------------------------------------------------------- POSICIONES
ncol = max(len(f) for f in FILAS)
ancho_col = [max((cajas[f[c]]["w"] for f in FILAS if c < len(f) and f[c]), default=0) for c in range(ncol)]
alto_fila = [max((cajas[n]["h"] for n in f if n), default=0) for f in FILAS]
xs, x = [], MARGEN
for a in ancho_col:
    xs.append(x)
    x += a + GAP_X
ys, y = [], MARGEN
for a in alto_fila:
    ys.append(y)
    y += a + GAP_Y
ANCHO = int(x - GAP_X + MARGEN)
ALTO = int(y - GAP_Y + MARGEN)
for fi, f in enumerate(FILAS):
    for ci, n in enumerate(f):
        if n:
            k = cajas[n]
            k["x"] = int((xs[ci] + (ancho_col[ci] - k["w"]) / 2) // U * U)
            k["y"] = int((ys[fi] + (alto_fila[fi] - k["h"]) / 2) // U * U)
            k["fila"], k["col"] = fi, ci

# ---------------------------------------------------------------------------------------------- ENRUTADOR
PAD = 2                 # celdas de aire alrededor de cada caja
STEM = 3                # celdas rectas que salen de cada caja (30 px)
GIRO, CRUCE, PARAL1, PARAL2 = 8, 300, 30, 5
SEP_PUERTOS = 4         # celdas minimas entre dos puertos del mismo lado
DIRS = [(1, 0), (0, 1), (-1, 0), (0, -1)]       # E S W N
W = ANCHO // U + 1
H = ALTO // U + 1

bloqueado = bytearray(W * H)
rect_celdas = {}
for n, k in cajas.items():
    i0, j0, i1, j1 = k["x"] // U, k["y"] // U, (k["x"] + k["w"]) // U, (k["y"] + k["h"]) // U
    rect_celdas[n] = (i0, j0, i1, j1)
    for i in range(max(0, i0 - PAD), min(W - 1, i1 + PAD) + 1):
        for j in range(max(0, j0 - PAD), min(H - 1, j1 + PAD) + 1):
            bloqueado[j * W + i] = 1

ocupa = {}              # (i,j) -> lista de (arista, tipo) con tipo 'h' | 'v' | 'b'
puertos_usados = {}     # (caja, lado) -> {arista: coordenada}
rutas = {}              # arista -> dict(celdas, puerto_o, puerto_d, costo)


def lados_puertos(caja):
    """Todos los puertos candidatos de una caja: (lado, coordenada, celda p0, celda s3, dir_salida)."""
    i0, j0, i1, j1 = rect_celdas[caja]
    res = []
    for i in range(i0 + 2, i1 - 1):
        res.append(("arriba", i, (i, j0), (i, j0 - STEM), 3))
        res.append(("abajo", i, (i, j1), (i, j1 + STEM), 1))
    for j in range(j0 + 2, j1 - 1):
        res.append(("izq", j, (i0, j), (i0 - STEM, j), 2))
        res.append(("der", j, (i1, j), (i1 + STEM, j), 0))
    return res


def tallo(p0, s3, d):
    dx, dy = DIRS[d]
    return [(p0[0] + dx * t, p0[1] + dy * t) for t in range(1, STEM + 1)]


def puerto_valido(caja, arista, lado, coord, p0, s3, d):
    for (c, l), usados in puertos_usados.items():
        if c == caja and l == lado:
            for e, u in usados.items():
                if e != arista and abs(u - coord) < SEP_PUERTOS:
                    return False
    if not (0 <= s3[0] < W and 0 <= s3[1] < H) or bloqueado[s3[1] * W + s3[0]]:
        return False
    for c in tallo(p0, s3, d):
        if c in ocupa:
            return False
        if bloqueado[c[1] * W + c[0]] and not _en_pad_propio(caja, c):
            return False
    return True


def _en_pad_propio(caja, c):
    i0, j0, i1, j1 = rect_celdas[caja]
    return i0 - PAD <= c[0] <= i1 + PAD and j0 - PAD <= c[1] <= j1 + PAD


def penal(celda, nd):
    """(costo extra, permitido) por entrar en `celda` moviendose en la direccion nd."""
    ax = 'h' if nd % 2 == 0 else 'v'
    extra = 0
    if celda in ocupa:
        for e, t in ocupa[celda]:
            if t == 'b' or t == ax:
                return 0, False
        extra += CRUCE
    px, py = (0, 1) if ax == 'h' else (1, 0)
    for off, pen in ((1, PARAL1), (2, PARAL2)):
        for sgn in (-1, 1):
            m = (celda[0] + px * off * sgn, celda[1] + py * off * sgn)
            if m in ocupa and any(t == ax for _, t in ocupa[m]):
                extra += pen
    return extra, True


def buscar(arista, a, b, mejor_costo=None):
    """A* multi-origen / multi-destino entre las cajas a y b. Devuelve dict de ruta o None."""
    orig = [p for p in lados_puertos(a) if puerto_valido(a, arista, p[0], p[1], p[2], p[3], p[4])]
    dest = [p for p in lados_puertos(b) if puerto_valido(b, arista, p[0], p[1], p[2], p[3], p[4])]
    if not orig or not dest:
        return None
    objetivos = {}
    for p in dest:
        d_in = (p[4] + 2) % 4
        objetivos[(p[3], d_in)] = p
    ti0, tj0, ti1, tj1 = rect_celdas[b]

    def h(c):
        return max(0, ti0 - c[0], c[0] - ti1) + max(0, tj0 - c[1], c[1] - tj1)

    dist, previo, cola = {}, {}, []
    cuenta = 0
    for p in orig:
        est = (p[3], p[4])
        if est not in dist:
            dist[est] = 0
            previo[est] = ("origen", p)
            heapq.heappush(cola, (h(p[3]), 0, cuenta, est))
            cuenta += 1
    while cola:
        f, g, _, est = heapq.heappop(cola)
        if g > dist.get(est, 1e18):
            continue
        celda, d = est
        if est in objetivos:
            camino, e = [], est
            while True:
                camino.append(e[0])
                pr = previo[e]
                if pr[0] == "origen":
                    po = pr[1]
                    break
                e = pr[1]
            camino.reverse()
            return dict(camino=camino, po=po, pd=objetivos[est], costo=g)
        for nd in (d, (d + 1) % 4, (d + 3) % 4):
            if nd != d and celda in ocupa:
                continue
            nx, ny = celda[0] + DIRS[nd][0], celda[1] + DIRS[nd][1]
            if not (0 <= nx < W and 0 <= ny < H) or bloqueado[ny * W + nx]:
                continue
            extra, ok = penal((nx, ny), nd)
            if not ok:
                continue
            ng = g + 1 + extra + (GIRO if nd != d else 0)
            ne = ((nx, ny), nd)
            if ng < dist.get(ne, 1e18):
                dist[ne] = ng
                previo[ne] = ("est", est)
                heapq.heappush(cola, (ng + h((nx, ny)), ng, cuenta, ne))
                cuenta += 1
    return None


def reservar(arista, r):
    po, pd = r["po"], r["pd"]
    celdas = tallo(po[2], po[3], po[4])[:-1] + r["camino"] + list(reversed(tallo(pd[2], pd[3], pd[4])[:-1]))
    r["completa"] = celdas
    for idx, c in enumerate(celdas):
        ant = celdas[idx - 1] if idx > 0 else po[2]
        sig = celdas[idx + 1] if idx + 1 < len(celdas) else pd[2]
        d1 = (c[0] - ant[0], c[1] - ant[1])
        d2 = (sig[0] - c[0], sig[1] - c[1])
        if d1 != d2:
            t = 'b'
        else:
            t = 'h' if d1[1] == 0 else 'v'
        ocupa.setdefault(c, []).append((arista, t))
    puertos_usados.setdefault((r["a"], po[0]), {})[arista] = po[1]
    puertos_usados.setdefault((r["b"], pd[0]), {})[arista] = pd[1]


def liberar(arista, r):
    for c in r["completa"]:
        lst = [x for x in ocupa.get(c, []) if x[0] != arista]
        if lst:
            ocupa[c] = lst
        else:
            ocupa.pop(c, None)
    for (caja, lado), us in list(puertos_usados.items()):
        us.pop(arista, None)


def cruces_de(arista):
    n = 0
    for c in rutas[arista]["completa"]:
        tipos = {t for e, t in ocupa.get(c, []) if e != arista}
        mios = {t for e, t in ocupa.get(c, []) if e == arista}
        if tipos & {'h', 'v'} and mios & {'h', 'v'} and not (tipos & mios):
            n += 1
    return n


# ---------------------------------------------------------------------------------------------- ARISTAS
aristas = []
for r in rels:
    aristas.append(dict(id=f'r{len(aristas)}', a=r["origen"], b=r["destino"], rel=r))
aristas.append(dict(id="nota", a=NOTA, b=NOTA_JUNTO_A, rel=dict(tipo="nota", mult_destino=None, mult_origen=None)))


def centro(n):
    k = cajas[n]
    return k["x"] + k["w"] / 2, k["y"] + k["h"] / 2


orden = sorted(aristas, key=lambda e: abs(centro(e["a"])[0] - centro(e["b"])[0]) + abs(centro(e["a"])[1] - centro(e["b"])[1]))
sys.setrecursionlimit(10000)
for e in orden:
    r = buscar(e["id"], e["a"], e["b"])
    if r is None:
        raise SystemExit(f"sin ruta para {e['a']} -> {e['b']}: aumenta GAP_X/GAP_Y")
    r["a"], r["b"] = e["a"], e["b"]
    rutas[e["id"]] = r
    reservar(e["id"], r)


def total():
    return sum(cruces_de(e) for e in rutas) // 2


rng = random.Random(7)
mejor = total()
for ronda in range(6):
    lista = list(rutas)
    rng.shuffle(lista)
    for eid in lista:
        viejo = rutas[eid]
        liberar(eid, viejo)
        e = next(x for x in aristas if x["id"] == eid)
        nuevo = buscar(eid, e["a"], e["b"])
        if nuevo is None:
            nuevo = viejo
        else:
            nuevo["a"], nuevo["b"] = e["a"], e["b"]
        rutas[eid] = nuevo
        reservar(eid, nuevo)
    t = total()
    print(f"ronda {ronda}: {t} cruces")
    if t == 0 or (t == mejor and ronda >= 2):
        break
    mejor = min(mejor, t)

# ---------------------------------------------------------------------------------------------- XML
ids = {n: f"c-{n}" for n in cajas}
salida = []
_n = [0]


def nid(p="x"):
    _n[0] += 1
    return f"{p}{_n[0]}"


def valor_lista(ls):
    return "<br>".join(html.escape(l, quote=False) for l in ls)


ESTILO_TXT = ("text;strokeColor=none;fillColor=none;align=left;verticalAlign=top;spacingLeft=6;spacingRight=4;"
              "overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;"
              "fontFamily=Courier New;fontSize=12;html=1;whiteSpace=nowrap;")
for n, k in cajas.items():
    if k["tipo"] == "nota":
        salida.append(
            f'<mxCell id="{ids[n]}" value="{esc(valor_lista(nota_texto))}" style="shape=note;whiteSpace=wrap;html=1;'
            f'size=14;align=left;verticalAlign=top;spacing=6;fontSize=11;fillColor={k["color"][0]};'
            f'strokeColor={k["color"][1]};" vertex="1" parent="1"><mxGeometry x="{k["x"]}" y="{k["y"]}" '
            f'width="{k["w"]}" height="{k["h"]}" as="geometry"/></mxCell>')
        continue
    cab = CAB + (18 if k["tipo"] == "enum" else 0)
    nombre_html = ("&laquo;enumeration&raquo;<br>" if k["tipo"] == "enum" else "") + f"<b>{txt(n)}</b>"
    estilo = (f"swimlane;fontStyle=0;childLayout=stackLayout;horizontal=1;startSize={cab};horizontalStack=0;"
              f"resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=0;marginBottom=0;align=center;"
              f"fontFamily=Courier New;fontSize=13;html=1;whiteSpace=wrap;fillColor={k['color'][0]};"
              f"strokeColor={k['color'][1]};swimlaneFillColor=#ffffff;")
    salida.append(
        f'<mxCell id="{ids[n]}" value="{esc(nombre_html)}" style="{esc(estilo)}" vertex="1" parent="1">'
        f'<mxGeometry x="{k["x"]}" y="{k["y"]}" width="{k["w"]}" height="{k["h"]}" as="geometry"/></mxCell>')
    y0 = cab
    salida.append(
        f'<mxCell id="{nid("a")}" value="{esc(valor_lista(k["attrs"]))}" style="{esc(ESTILO_TXT)}" vertex="1" '
        f'parent="{ids[n]}"><mxGeometry y="{y0}" width="{k["w"]}" height="{k["ha"]}" as="geometry"/></mxCell>')
    if k["tipo"] == "clase":
        y0 += k["ha"]
        salida.append(
            f'<mxCell id="{nid("l")}" value="" style="line;strokeWidth=1;fillColor=none;align=left;verticalAlign=middle;'
            f'spacingTop=-1;spacingLeft=3;spacingRight=3;rotatable=0;labelPosition=right;points=[];portConstraint=eastwest;'
            f'strokeColor=inherit;" vertex="1" parent="{ids[n]}"><mxGeometry y="{y0}" width="{k["w"]}" height="8" '
            f'as="geometry"/></mxCell>')
        y0 += 8
        salida.append(
            f'<mxCell id="{nid("o")}" value="{esc(valor_lista(k["ops"]))}" style="{esc(ESTILO_TXT)}" vertex="1" '
            f'parent="{ids[n]}"><mxGeometry y="{y0}" width="{k["w"]}" height="{k["ho"]}" as="geometry"/></mxCell>')

BASE = ("edgeStyle=orthogonalEdgeStyle;rounded=1;orthogonalLoop=1;jettySize=auto;html=1;strokeColor=#000000;"
        "strokeWidth=1;jumpStyle=arc;jumpSize=8;exitDx=0;exitDy=0;entryDx=0;entryDy=0;exitPerimeter=0;entryPerimeter=0;")
ESTILO_ARISTA = {
    "asociacion": "startArrow=none;endArrow=open;endFill=0;endSize=10;",
    "enum": "startArrow=none;endArrow=open;endFill=0;endSize=10;",
    "composicion": "startArrow=diamondThin;startFill=1;startSize=16;endArrow=none;",
    "agregacion": "startArrow=diamondThin;startFill=0;startSize=16;endArrow=none;",
    "nota": "startArrow=none;endArrow=none;dashed=1;",
}
ESTILO_ETQ = ("edgeLabel;html=1;align=center;verticalAlign=middle;resizable=0;points=[];labelBackgroundColor=none;"
              "fontFamily=Courier New;fontSize=11;fontColor=#000000;")


def fraccion(caja, lado, coord):
    k = cajas[caja]
    if lado in ("arriba", "abajo"):
        return (coord * U - k["x"]) / k["w"], 0.0 if lado == "arriba" else 1.0
    return 0.0 if lado == "izq" else 1.0, (coord * U - k["y"]) / k["h"]


def puntos_ruta(r):
    """Esquinas de la ruta (sin los extremos, que son los puntos fijos de salida y llegada)."""
    sec = [r["po"][2]] + r["completa"] + [r["pd"][2]]
    res = []
    for i in range(1, len(sec) - 1):
        d1 = (sec[i][0] - sec[i - 1][0], sec[i][1] - sec[i - 1][1])
        d2 = (sec[i + 1][0] - sec[i][0], sec[i + 1][1] - sec[i][1])
        if d1 != d2:
            res.append((sec[i][0] * U, sec[i][1] * U))
    return res


def desplazamiento(lado, texto):
    a = 3.5 * len(texto) + 10.8
    return {"arriba": (-a, -16), "abajo": (a, 16), "izq": (-a + 1, 15), "der": (a - 1, -15)}[lado]


for e in aristas:
    r = rutas[e["id"]]
    rel = e["rel"]
    po, pd = r["po"], r["pd"]
    ex, ey = fraccion(e["a"], po[0], po[1])
    nx, ny = fraccion(e["b"], pd[0], pd[1])
    estilo = (BASE + ESTILO_ARISTA[rel["tipo"]] + f"exitX={ex:.4f};exitY={ey:.4f};entryX={nx:.4f};entryY={ny:.4f};")
    pts = "".join(f'<mxPoint x="{x}" y="{y}"/>' for x, y in puntos_ruta(r))
    salida.append(
        f'<mxCell id="{e["id"]}" style="{esc(estilo)}" edge="1" parent="1" source="{ids[e["a"]]}" target="{ids[e["b"]]}">'
        f'<mxGeometry relative="1" as="geometry">' + (f'<Array as="points">{pts}</Array>' if pts else "")
        + '</mxGeometry></mxCell>')
    for texto, extremo, lado in ((rel["mult_origen"], -1, po[0]), (rel["mult_destino"], 1, pd[0])):
        if not texto:
            continue
        dx, dy = desplazamiento(lado, texto)
        salida.append(
            f'<mxCell id="{nid("m")}" value="{esc(texto)}" style="{ESTILO_ETQ}" vertex="1" connectable="0" '
            f'parent="{e["id"]}"><mxGeometry x="{extremo}" relative="1" as="geometry">'
            f'<mxPoint x="{dx:g}" y="{dy:g}" as="offset"/></mxGeometry></mxCell>')

xml = (
    '<?xml version="1.0" encoding="UTF-8"?>\n<mxfile host="app.diagrams.net">\n'
    '  <diagram name="Entidades" id="entidades">\n'
    f'    <mxGraphModel dx="{ANCHO}" dy="{ALTO}" grid="1" gridSize="10" guides="1" tooltips="1" connect="1" '
    f'arrows="1" fold="1" page="1" pageScale="1" pageWidth="{ANCHO}" pageHeight="{ALTO}" math="0" shadow="0">\n'
    '      <root>\n        <mxCell id="0"/>\n        <mxCell id="1" parent="0"/>\n'
    + "".join(f"        {s}\n" for s in salida)
    + "      </root>\n    </mxGraphModel>\n  </diagram>\n</mxfile>\n")
ET.fromstring(xml.encode("utf-8"))
os.makedirs(os.path.dirname(SALIDA), exist_ok=True)
with open(SALIDA, "w", encoding="utf-8") as f:
    f.write(xml)
print(f"{len([c for c in cajas.values() if c['tipo'] == 'clase'])} clases, {len(enums_usados)} enumeraciones, "
      f"{len(rels)} relaciones, hoja {ANCHO} x {ALTO}, {total()} cruces -> {os.path.normpath(SALIDA)}")
