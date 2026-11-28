"""Modelo del diagrama de clases: lee las entidades (parse_entidades.py) y calcula el tamano de cada caja.
Lo comparten gen_clases.py (dibujo) y optimizar_rejilla.py (busqueda de rejilla)."""
import html
import math
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from parse_entidades import leer_entidades, relaciones  # noqa: E402

# ---------------------------------------------------------------------------------------------- REJILLA FIJA
# Cada fila es una lista de columnas; None = hueco. La nota de Lombok es el "nodo" NOTA.
GAP_X = 170         # pasillo vertical entre columnas (px, multiplo de 10)
GAP_Y = 150         # pasillo horizontal entre filas
MARGEN = 120        # margen exterior para carriles perimetrales
NOTA = "NOTA"
NOTA_JUNTO_A = "UserAdmin"

U = 10
CHAR = 7.2          # ancho de un caracter Courier New 12 px
LINEA = 16
CAB = 28
COLORES = {"admin": ("#e1d5e7", "#9673a6"), "client": ("#d5e8d4", "#82b366"), "general": ("#dae8fc", "#6c8ebf"),
           "enum": ("#f5f5f5", "#666666")}


def esc(s):
    return html.escape(s, quote=True)


def txt(s):
    return html.escape(s, quote=False)


# ---------------------------------------------------------------------------------------------- MODELO
clases, enums = leer_entidades()
rels = relaciones(clases, enums)
enums_usados = sorted({r["destino"] for r in rels if r["tipo"] == "enum"})


def linea_atributo(f):
    s = f'{f["vis"]} {f["nombre"]} : {f["tipo"]}'
    if f["init"] and "new " not in f["init"]:
        s += f' = {f["init"]}'
    return s


def linea_metodo(m):
    ps = ", ".join(f"{n} : {t}" for t, n in m["params"])
    r = "" if m["ret"] == "void" else f' : {m["ret"]}'
    return f'{m["vis"]} {m["nombre"]}({ps}){r}'


cajas = {}      # nombre -> dict(attrs, ops, w, h, x, y, color)
for nombre, c in clases.items():
    attrs = [linea_atributo(f) for f in c["campos"]]
    ops = [linea_metodo(m) for m in c["metodos"]]
    mas_larga = max([len(nombre)] + [len(l) for l in attrs + ops])
    w = int(math.ceil((mas_larga * CHAR + 24) / U) * U)
    w = max(w, 200)
    ha = LINEA * max(1, len(attrs)) + 8
    ho = LINEA * len(ops) + 8 if ops else 14
    h = int(math.ceil((CAB + ha + 8 + ho) / U) * U)
    cajas[nombre] = dict(tipo="clase", attrs=attrs, ops=ops, w=w, h=h, ha=ha, ho=ho, color=COLORES[c["paquete"]],
                         pkg=c["paquete"])
for nombre in enums_usados:
    lits = enums[nombre]
    mas_larga = max([len(nombre)] + [len(x) for x in lits] + [len("<<enumeration>>")])
    w = max(200, int(math.ceil((mas_larga * CHAR + 24) / U) * U))
    ha = LINEA * len(lits) + 8
    h = int(math.ceil((CAB + 18 + ha) / U) * U)
    cajas[nombre] = dict(tipo="enum", attrs=list(lits), ops=[], w=w, h=h, ha=ha, ho=0, color=COLORES["enum"], pkg="enum")

sin_lombok = [n for n, c in clases.items() if not c["lombok"]]
nota_texto = ["Getters/setters generados por Lombok (@Data)"]
if sin_lombok:
    nota_texto.append("Excepto " + ", ".join(sin_lombok) + " (sin Lombok: solo campos)")
nota_w = 300
nota_h = int(math.ceil((14 + 16 * (1 + math.ceil(len(nota_texto[-1]) * 6.2 / (nota_w - 16)) if len(nota_texto) > 1 else 1)) / U) * U) + 20
cajas[NOTA] = dict(tipo="nota", w=nota_w, h=nota_h, color=("#fff2cc", "#d6b656"), pkg="nota")

