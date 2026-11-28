"""Herramienta (se corre a mano; NO hace falta para regenerar): busca una rejilla (FILAS) para el diagrama de clases con
recocido simulado, usando el TAMANO REAL de las cajas y rutas en L entre ellas como aproximacion de un dibujo
ortogonal. Costo = longitud + cruces entre rutas + rutas que pasan por encima de una caja ajena + area de la hoja.
Imprime la mejor rejilla para pegarla en gen_clases.py (FILAS).

Uso: python diagrams/_fuente/optimizar_rejilla.py filas columnas semilla iteraciones
"""
import math
import os
import random
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from modelo_clases import cajas, rels, GAP_X, GAP_Y, NOTA, NOTA_JUNTO_A  # noqa: E402

FILAS_N, COLS_N = int(sys.argv[1]), int(sys.argv[2])
SEMILLA, ITER = int(sys.argv[3]), int(sys.argv[4])

nodos = list(cajas)
aristas = sorted({(r["origen"], r["destino"]) for r in rels} | {(NOTA, NOTA_JUNTO_A)})
W = {n: cajas[n]["w"] for n in nodos}
H = {n: cajas[n]["h"] for n in nodos}


def geometria(pos):
    ancho = [0] * COLS_N
    alto = [0] * FILAS_N
    for n, (c, f) in pos.items():
        ancho[c] = max(ancho[c], W[n])
        alto[f] = max(alto[f], H[n])
    xs, x = [], 0
    for a in ancho:
        xs.append(x)
        x += a + GAP_X
    ys, y = [], 0
    for a in alto:
        ys.append(y)
        y += a + GAP_Y
    rect = {}
    for n, (c, f) in pos.items():
        rect[n] = (xs[c] + (ancho[c] - W[n]) / 2, ys[f] + (alto[f] - H[n]) / 2, W[n], H[n])
    return rect, x, y


def seg_cruza(a, b):
    """a, b = ((x1,y1),(x2,y2)) ortogonales."""
    (ax1, ay1), (ax2, ay2) = a
    (bx1, by1), (bx2, by2) = b
    ah, bh = ay1 == ay2, by1 == by2
    if ah == bh:
        return False
    if ah:
        return min(ax1, ax2) < bx1 < max(ax1, ax2) and min(by1, by2) < ay1 < max(by1, by2)
    return min(bx1, bx2) < ax1 < max(bx1, bx2) and min(ay1, ay2) < by1 < max(ay1, ay2)


def seg_caja(s, r):
    (x1, y1), (x2, y2) = s
    rx, ry, rw, rh = r
    if y1 == y2:
        return ry < y1 < ry + rh and min(x1, x2) < rx + rw and max(x1, x2) > rx
    return rx < x1 < rx + rw and min(y1, y2) < ry + rh and max(y1, y2) > ry


def ruta_l(ra, rb, modo):
    ax, ay = ra[0] + ra[2] / 2, ra[1] + ra[3] / 2
    bx, by = rb[0] + rb[2] / 2, rb[1] + rb[3] / 2
    if abs(ax - bx) < 1 or abs(ay - by) < 1:
        return [((ax, ay), (bx, by))]
    if modo == 0:
        return [((ax, ay), (bx, ay)), ((bx, ay), (bx, by))]
    return [((ax, ay), (ax, by)), ((ax, by), (bx, by))]


def costo(pos):
    rect, tw, th = geometria(pos)
    mejores = []
    c = 0.0
    # para cada arista elige la L con menos choques con cajas
    for a, b in aristas:
        opciones = []
        for modo in (0, 1):
            segs = ruta_l(rect[a], rect[b], modo)
            golpes = sum(1 for s in segs for n in nodos if n not in (a, b) and seg_caja(s, rect[n]))
            largo = sum(abs(s[0][0] - s[1][0]) + abs(s[0][1] - s[1][1]) for s in segs)
            opciones.append((golpes * 400 + largo, golpes, largo, segs))
        o = min(opciones)
        c += o[0] * 0.6
        mejores.append((a, b, o[3]))
    for i in range(len(mejores)):
        for j in range(i + 1, len(mejores)):
            a1, b1, s1 = mejores[i]
            a2, b2, s2 = mejores[j]
            if len({a1, b1, a2, b2}) < 4:
                continue
            for x in s1:
                for y in s2:
                    if seg_cruza(x, y):
                        c += 250
    c += (tw * th) / 4000.0
    return c


random.seed(SEMILLA)
libres = [(c, f) for f in range(FILAS_N) for c in range(COLS_N)]
pos = dict(zip(nodos, random.sample(libres, len(nodos))))
mejor, mejor_c = dict(pos), costo(pos)
actual = mejor_c
T0 = 900.0
for it in range(ITER):
    T = T0 * (1 - it / ITER) + 5
    n = random.choice(nodos)
    destino = random.choice(libres)
    ocupado = next((k for k, v in pos.items() if v == destino), None)
    if ocupado == n:
        continue
    antes = dict(pos)
    if ocupado:
        pos[ocupado], pos[n] = pos[n], destino
    else:
        pos[n] = destino
    nuevo = costo(pos)
    if nuevo <= actual or random.random() < math.exp((actual - nuevo) / T):
        actual = nuevo
        if nuevo < mejor_c:
            mejor, mejor_c = dict(pos), nuevo
    else:
        pos = antes

print("costo", round(mejor_c, 1))
for f in range(FILAS_N):
    fila = [next((k for k, v in mejor.items() if v == (c, f)), None) for c in range(COLS_N)]
    print("    " + repr(fila) + ",")
