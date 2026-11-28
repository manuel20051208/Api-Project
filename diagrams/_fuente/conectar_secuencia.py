"""Deja bien conectados los diagramas de secuencia de draw.io y trae los fragmentos (alt/opt/loop) al frente.

Sobre cada pagina:
  1. el actor (shape=umlActor + linea punteada suelta) pasa a ser una linea de vida con figura de actor
     (shape=umlLifeline;participant=umlActor), la forma nativa de draw.io, para que se le puedan pegar flechas;
  2. cada mensaje (tambien el de creacion, que llega a la cabeza de la linea de vida creada) (y cada llamada a si mismo) queda CONECTADO: source/target apuntan a la barra de activacion que
     toca su punta (o a la linea de vida si no hay barra), con el punto fijo (exitX/exitY, entryX/entryY). Si se
     mueve una barra o una linea de vida en draw.io, la flecha la sigue;
  3. los fragmentos (umlFrame), sus separadores de rama y sus condiciones [..] pasan al final del XML, es decir,
     al frente. No tienen relleno y llevan pointerEvents=0: no tapan nada y se sigue pudiendo hacer clic en lo
     que queda dentro.
No mueve ninguna figura: las flechas quedan exactamente donde estaban.

Uso como script:  python conectar_secuencia.py [secuencia.xml]      (reescribe el archivo)
Uso como modulo:  conectar_xml(texto) -> (texto, avisos)
"""
import os
import re
import sys
import xml.etree.ElementTree as ET

TOL_X, TOL_Y = 1.6, 1.2


def estilo_dict(s):
    d = {}
    for par in (s or "").split(";"):
        if "=" in par:
            k, v = par.split("=", 1)
            d[k] = v
        elif par:
            d[par] = ""
    return d


def estilo_str(d):
    return "".join(f"{k}={v};" if v != "" else f"{k};" for k, v in d.items())


def geo(c):
    g = c.find("mxGeometry")
    return tuple(float(g.get(k) or 0) for k in ("x", "y", "width", "height"))


def partir_actor(texto, ancho=14):
    plano = re.sub(r"<[^>]+>", " ", texto).split()
    lineas, actual = [], ""
    for pal in plano:
        if actual and len(actual) + len(pal) + 1 > ancho:
            lineas.append(actual)
            actual = pal
        else:
            actual = (actual + " " + pal).strip()
    if actual:
        lineas.append(actual)
    return "<br>".join(lineas)


def conectar_pagina(raiz, nombre=""):
    avisos = []
    celdas = [c for c in raiz if c.tag == "mxCell"]
    por_id = {c.get("id"): c for c in celdas}

    # 1) actores -> linea de vida con figura de actor
    for c in celdas:
        st = estilo_dict(c.get("style"))
        if st.get("shape") != "umlActor":
            continue
        x, y, w, h = geo(c)
        cx = x + w / 2
        fin = y + h
        linea = None
        for e in celdas:
            if e.get("edge") == "1" and not e.get("source") and estilo_dict(e.get("style")).get("endArrow") == "none":
                pts = {p.get("as"): p for p in e.find("mxGeometry").findall("mxPoint")}
                if "sourcePoint" in pts and abs(float(pts["sourcePoint"].get("x")) - cx) < 1 \
                        and abs(float(pts["targetPoint"].get("x")) - cx) < 1:
                    linea = e
                    fin = max(float(pts["sourcePoint"].get("y")), float(pts["targetPoint"].get("y")))
        nuevo = {
            "shape": "umlLifeline", "participant": "umlActor", "perimeter": "lifelinePerimeter",
            "whiteSpace": "nowrap", "html": "1", "container": "0", "dropTarget": "0", "collapsible": "0",
            "recursiveResize": "0", "outlineConnect": "0", "portConstraint": "eastwest", "size": f"{h:g}",
            "verticalAlign": "top", "spacingTop": f"{h + 2:g}", "labelBackgroundColor": "#ffffff",
            "fontStyle": st.get("fontStyle", "1"), "fontSize": st.get("fontSize", "11"),
            "fillColor": st.get("fillColor", "#f5f5f5"), "strokeColor": st.get("strokeColor", "#36393d"),
        }
        c.set("style", estilo_str(nuevo))
        c.set("value", partir_actor(c.get("value") or ""))
        c.find("mxGeometry").set("height", f"{fin - y:g}")
        if linea is not None:
            raiz.remove(linea)
            celdas.remove(linea)

    # 2) conexiones
    barras, vidas = [], []
    for c in celdas:
        st = estilo_dict(c.get("style"))
        if c.get("vertex") != "1":
            continue
        if "orthogonalPerimeter" in st.get("perimeter", ""):
            barras.append((c, geo(c)))
        elif st.get("shape") == "umlLifeline":
            vidas.append((c, geo(c)))

    def terminal(x, y):
        cand = []
        for orden, (c, (bx, by, bw, bh)) in enumerate(barras):
            if bx - TOL_X <= x <= bx + bw + TOL_X and by - TOL_Y <= y <= by + bh + TOL_Y:
                d_izq, d_der = abs(x - bx), abs(x - (bx + bw))
                cand.append((min(d_izq, d_der), -orden, c, 0.0 if d_izq <= d_der else 1.0, (y - by) / bh))
        if cand:
            cand.sort(key=lambda t: (t[0], t[1]))
            _, _, c, fx, fy = cand[0]
            return c, fx, min(1.0, max(0.0, fy))
        for c, (lx, ly, lw, lh) in vidas:
            if abs(x - (lx + lw / 2)) <= 8 and ly <= y <= ly + lh:
                return c, 0.5, (y - ly) / lh
        # mensaje de creacion (17.4.4.1): la punta llega al borde de la cabeza de la linea de vida creada
        for c, (lx, ly, lw, lh) in vidas:
            cabeza = float(estilo_dict(c.get("style")).get("size", 40))
            if lx - 2 <= x <= lx + lw + 2 and ly - 2 <= y <= ly + cabeza + 2:
                return c, min(1.0, max(0.0, (x - lx) / lw)), (y - ly) / lh
        return None

    conectadas = 0
    for e in celdas:
        if e.get("edge") != "1":
            continue
        st = estilo_dict(e.get("style"))
        if st.get("endArrow") == "none":          # separadores de rama y lineas punteadas
            continue
        pts = {p.get("as"): p for p in e.find("mxGeometry").findall("mxPoint")}
        if "sourcePoint" not in pts or "targetPoint" not in pts:
            continue
        sx, sy = float(pts["sourcePoint"].get("x")), float(pts["sourcePoint"].get("y"))
        tx, ty = float(pts["targetPoint"].get("x")), float(pts["targetPoint"].get("y"))
        a, b = terminal(sx, sy), terminal(tx, ty)
        etiqueta = re.sub(r"<[^>]+>", " ", e.get("value") or "(retorno)").strip() or "(sin texto)"
        if a is None or b is None:
            avisos.append(f"[{nombre}] {etiqueta}: punta sin barra ni linea de vida "
                          f"({'origen' if a is None else 'destino'})")
            continue
        for k in [k for k in st if k.startswith(("exit", "entry"))]:
            del st[k]
        st.update({"exitX": f"{a[1]:.4f}", "exitY": f"{a[2]:.4f}", "exitDx": "0", "exitDy": "0",
                   "exitPerimeter": "0", "entryX": f"{b[1]:.4f}", "entryY": f"{b[2]:.4f}", "entryDx": "0",
                   "entryDy": "0", "entryPerimeter": "0"})
        e.set("style", estilo_str(st))
        e.set("source", a[0].get("id"))
        e.set("target", b[0].get("id"))
        # las coordenadas guardadas pasan a ser las del punto conectado (p. ej. la linea del actor, no 5 px antes)
        for (c, fx, fy), pt in ((a, pts["sourcePoint"]), (b, pts["targetPoint"])):
            x, y, w, h = geo(c)
            pt.set("x", f"{x + w * fx:g}")
            pt.set("y", f"{y + h * fy:g}")
        conectadas += 1

    # 3) fragmentos al frente: marcos, separadores y condiciones [..]
    frente = []
    for c in celdas:
        st = estilo_dict(c.get("style"))
        es_marco = st.get("shape") == "umlFrame"
        es_sep = c.get("edge") == "1" and st.get("endArrow") == "none" and not c.get("source")
        es_cond = (c.get("style") or "").startswith("text;") and st.get("fontStyle") == "2" \
            and (c.get("value") or "").lstrip().startswith("[")
        if es_marco:
            st["pointerEvents"] = "0"
            st["fillColor"] = "none"
            c.set("style", estilo_str(st))
        if es_marco or es_sep or es_cond:
            frente.append((0 if es_marco else 1 if es_sep else 2, c))
    for _, c in sorted(frente, key=lambda t: t[0]):
        raiz.remove(c)
        raiz.append(c)
    return conectadas, avisos


def conectar_xml(texto):
    """Aplica conectar_pagina a cada <diagram> del texto; devuelve (texto nuevo, avisos)."""
    arbol = ET.fromstring(texto.encode("utf-8"))
    avisos, total = [], 0
    diagramas = [arbol] if arbol.tag == "diagram" else arbol.findall("diagram")
    for d in diagramas:
        raiz = d.find("mxGraphModel/root")
        n, a = conectar_pagina(raiz, d.get("name"))
        total += n
        avisos += a
    ET.indent(arbol, space="  ", level=1 if arbol.tag == "diagram" else 0)
    salida = ET.tostring(arbol, encoding="unicode")
    if texto.lstrip().startswith("<?xml"):
        salida = '<?xml version="1.0" encoding="UTF-8"?>\n' + salida + "\n"
    return salida, avisos, total


if __name__ == "__main__":
    ruta = sys.argv[1] if len(sys.argv) > 1 else os.path.join(os.path.dirname(os.path.abspath(__file__)),
                                                               "secuencia-casos-de-uso.xml")
    texto = open(ruta, encoding="utf-8").read()
    nuevo, avisos, total = conectar_xml(texto)
    for a in avisos:
        print("AVISO", a)
    with open(ruta, "w", encoding="utf-8") as f:
        f.write(nuevo)
    print(f"{total} mensajes conectados, {len(avisos)} avisos -> {ruta}")
