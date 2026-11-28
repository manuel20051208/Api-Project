#!/usr/bin/env python3
"""
Genera un diagrama de secuencia por caso de uso de ApiProject (CU-001 ... CU-025):
  - diagrams/_fuente/secuencia-casos-de-uso.xml   (draw.io, una pagina por caso de uso; es la fuente)
  - diagrams/Secuencia/<cu-nnn-nombre>.svg        (un SVG por pagina, exportado con el visor oficial de draw.io)

Reglas (skill diagramas-uml, seccion 3):
  - Participantes = actor del diagrama de casos de uso + clases del diagrama de clases (entidades JPA).
    Mensajes = operaciones de esas entidades que el codigo de services/** llama de verdad en ese flujo. Como las
    entidades son anemicas (Lombok), son los accesores setX/getX/isX que usa el servicio y los metodos escritos a
    mano (p. ej. getOwnerId()); la creacion con new/builder es <<create>>. Lo que orquesta la aplicacion
    (controladores, servicios, repositorios) no aparece: sale del actor.
  - Notacion OMG UML 2.5.1 (formal/17-12-05): 17.2.4.1 marco "sd"; 17.3.4.1 linea de vida; 17.2.4.4 barras de
    ejecucion; 17.4.4.1 llamada, retorno y creacion; 17.6.4 alt/opt/loop con guardas y [else]; 17.7.4 ref.

Uso:  python diagrams/_fuente/gen_secuencia.py                  (solo los SVG)
      python diagrams/_fuente/gen_secuencia.py --regenerar-xml  (tambien el XML; descarta retoques hechos a mano)
      python diagrams/_fuente/gen_secuencia.py --sin-svg        (no exporta SVG)
"""
import html
import os
import re
import sys
import unicodedata

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from conectar_secuencia import conectar_xml  # noqa: E402

AQUI = os.path.dirname(os.path.abspath(__file__))
SALIDA = os.path.join(AQUI, "secuencia-casos-de-uso.xml")

ESPACIO = 270          # distancia entre lineas de vida
ANCHO_CAJA = 150       # ancho minimo de la cabeza de cada linea de vida
CABEZA = 40            # alto de la cabeza
TOP = 60               # y de las cabezas
Y0 = 170               # y de la primera interaccion
CHAR = 6.4             # ancho aproximado de un caracter (px) a 11 px

CABEZA_RELLENO = "#e1d5e7"   # morado: paleta propia de ApiProject (otros proyectos usan azul/naranja/verde)
CABEZA_BORDE = "#9673a6"
ACTOR_RELLENO = "#f5f5f5"
ACTOR_BORDE = "#666666"
BORDE = "#36393d"


def esc(s):
    """Escapa una cadena para ponerla en un atributo XML."""
    return html.escape(s, quote=True)


def etiqueta_html(lineas):
    """Texto de una etiqueta html=1 de draw.io; se escapa PRIMERO como HTML (si no, 'List<X>' pierde '<X>')."""
    if isinstance(lineas, str):
        lineas = [lineas]
    return "<br>".join(html.escape(linea, quote=False) for linea in lineas)


def envolver(texto, ancho_px):
    """Parte el texto en lineas cortando en ', ' o espacios."""
    maxc = max(12, int(ancho_px / CHAR))
    if len(texto) <= maxc:
        return [texto]
    palabras = texto.replace(", ", ",\u0001").split("\u0001")
    lineas, actual = [], ""
    for p in palabras:
        if actual and len(actual) + len(p) + 1 > maxc:
            lineas.append(actual.rstrip())
            actual = p
        else:
            actual = (actual + " " + p) if actual else p
    if actual:
        lineas.append(actual.strip())
    return lineas


def nombre_operacion(op):
    return op.split("(")[0].strip()


class Diagrama:
    def __init__(self, nombre, participantes):
        """participantes: lista de (id, tipo, etiqueta); tipo = 'actor' | 'obj'."""
        self.nombre = nombre
        self.parts = {}
        self.orden = []
        for i, (pid, tipo, etiqueta) in enumerate(participantes):
            cx = 110 + i * ESPACIO
            ancho = max(ANCHO_CAJA, int(len(etiqueta) * 7.6) + 24)
            self.parts[pid] = dict(tipo=tipo, etiqueta=etiqueta, cx=cx, top=TOP, ancho=ancho)
            self.orden.append(pid)
        self.y = Y0
        self.celdas_frames, self.celdas_act, self.celdas_msg, self.celdas_txt = [], [], [], []
        self.pila_frames = []
        self.activaciones = {pid: [] for pid in self.orden}
        self.ops = {pid: [] for pid in self.orden}
        self._n = 0
        self.max_x = 110 + (len(participantes) - 1) * ESPACIO + 120

    # ------------------------------------------------------------ utilidades
    def _id(self, pref):
        self._n += 1
        return f"{pref}{self._n}"

    def cx(self, pid):
        return self.parts[pid]["cx"]

    def _tocar(self, x1, x2):
        self.max_x = max(self.max_x, x2)
        if self.pila_frames:
            f = self.pila_frames[-1]
            f["min"] = min(f["min"], x1)
            f["max"] = max(f["max"], x2)

    # ------------------------------------------------------------ mensajes
    def _mensaje(self, a, b, etiqueta, tipo):
        """tipo: 'llamada' | 'retorno' | 'creacion'."""
        xa, xb = self.cx(a), self.cx(b)
        if tipo == "creacion":       # la punta llega al borde de la cabeza del objeto creado
            mitad = self.parts[b]["ancho"] / 2
            xb = xb - mitad if xb > xa else xb + mitad
        ancho = abs(xb - xa) - 30
        lineas = envolver(etiqueta, ancho) if etiqueta else [""]
        h = 15 * len(lineas)
        y = self.y + h + 8
        da = 0 if self.parts[a]["tipo"] == "actor" else 5
        db = 0 if (self.parts[b]["tipo"] == "actor" or tipo == "creacion") else 5
        if xb > xa:
            sx, tx = xa + da, xb - db
        else:
            sx, tx = xa - da, xb + db
        if tipo == "llamada":
            punta = "endArrow=block;endFill=1;endSize=8;"
        else:                         # retorno y creacion: punteada con punta abierta
            punta = "dashed=1;endArrow=open;endFill=0;endSize=8;"
        estilo = "html=1;verticalAlign=bottom;rounded=0;fontSize=11;strokeColor=#000000;" + punta
        self.celdas_msg.append(
            f'<mxCell id="{self._id("m")}" value="{esc(etiqueta_html(lineas))}" style="{estilo}" '
            f'edge="1" parent="1"><mxGeometry relative="1" as="geometry">'
            f'<mxPoint x="{sx}" y="{y}" as="sourcePoint"/><mxPoint x="{tx}" y="{y}" as="targetPoint"/>'
            f'</mxGeometry></mxCell>')
        self._tocar(min(sx, tx) - 25, max(sx, tx) + 25)
        self.y = y + 14
        return y

    def llamar(self, a, b, op):
        """Llamada sincrona a la operacion op; deja abierta la ejecucion de b hasta su retorno."""
        y = self._mensaje(a, b, op, "llamada")
        self.activaciones[b].append(y)
        self.ops[b].append(op)

    def retornar(self, b, a, valor=None, cerrar=True):
        """Retorno de b hacia a. Etiqueta '<operacion>(): <valor>' (17.4.4.1) o vacia si no devuelve nada.
        cerrar=False en todas las ramas de un alt menos la ultima: la ejecucion sigue abierta hasta el ultimo retorno."""
        op = (self.ops[b].pop() if cerrar else self.ops[b][-1]) if self.ops[b] else ""
        etiqueta = f"{nombre_operacion(op)}(): {valor}" if valor is not None else ""
        y = self._mensaje(b, a, etiqueta, "retorno")
        if cerrar and self.activaciones[b]:
            y0 = self.activaciones[b].pop()
            self._activacion(b, y0 - 6, y + 6)

    def llamar_y_retornar(self, a, b, op, valor=None):
        self.llamar(a, b, op)
        self.retornar(b, a, valor)

    def crear(self, a, b):
        """Mensaje de creacion: la linea de vida de b empieza en este punto (su cabeza baja hasta la flecha)."""
        y = self._mensaje(a, b, "«create»", "creacion")
        self.parts[b]["top"] = y - CABEZA / 2
        cx, mitad = self.cx(b), self.parts[b]["ancho"] / 2
        self._tocar(cx - mitad, cx + mitad)
        self.y = y + CABEZA / 2 + 12

    def propio(self, a, op):
        """Mensaje de un objeto a si mismo (lazo a la derecha con su ejecucion anidada)."""
        cx = self.cx(a)
        ancho_txt = ESPACIO - 85
        lineas = envolver(op, ancho_txt)
        alto = max(24, 15 * len(lineas) + 4)
        y = self.y + 12
        x1 = cx + 5
        self.celdas_msg.append(
            f'<mxCell id="{self._id("s")}" value="" style="html=1;rounded=0;endArrow=block;endFill=1;'
            f'endSize=8;strokeColor=#000000;" edge="1" parent="1"><mxGeometry relative="1" as="geometry">'
            f'<mxPoint x="{x1}" y="{y}" as="sourcePoint"/><mxPoint x="{x1 + 5}" y="{y + alto}" as="targetPoint"/>'
            f'<Array as="points"><mxPoint x="{x1 + 38}" y="{y}"/><mxPoint x="{x1 + 38}" y="{y + alto}"/>'
            f'</Array></mxGeometry></mxCell>')
        self._texto(x1 + 44, y - 2, ancho_txt, alto + 4, etiqueta_html(lineas), "left")
        self._activacion(a, y + alto - 4, y + alto + 14, desplazo=5)
        self._tocar(cx - 25, x1 + 44 + min(ancho_txt, int(max(len(l) for l in lineas) * CHAR)) + 15)
        self.y = y + alto + 22

    def _activacion(self, pid, y1, y2, desplazo=0):
        x = self.cx(pid) - 5 + desplazo
        self.celdas_act.append(
            f'<mxCell id="{self._id("a")}" value="" style="html=1;points=[];perimeter=orthogonalPerimeter;'
            f'fillColor=#ffffff;strokeColor=#000000;" vertex="1" parent="1">'
            f'<mxGeometry x="{x}" y="{y1}" width="10" height="{max(14, y2 - y1)}" as="geometry"/></mxCell>')

    def _texto(self, x, y, w, h, valor, align="left", estilo_extra=""):
        self.celdas_txt.append(
            f'<mxCell id="{self._id("t")}" value="{esc(valor)}" style="text;html=1;align={align};'
            f'verticalAlign=top;whiteSpace=wrap;overflow=hidden;fontSize=11;{estilo_extra}" '
            f'vertex="1" parent="1"><mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry"/></mxCell>')

    # ------------------------------------------------------------ fragmentos
    def abrir(self, tipo, condicion):
        self.y += 14
        self.pila_frames.append(dict(tipo=tipo, top=self.y, min=10 ** 6, max=0, ramas=[], cond=condicion))
        self.y += 34

    def sino(self, condicion="else"):
        f = self.pila_frames[-1]
        self.y += 6
        f["ramas"].append((self.y, condicion))
        self.y += 34

    def cerrar(self):
        f = self.pila_frames.pop()
        self.y += 8
        x1, x2 = f["min"] - 30, f["max"] + 15
        if x1 > x2:
            x1, x2 = 10, 200
        x1 = max(20, x1)
        top, bottom = f["top"], self.y
        ancho = x2 - x1
        self.celdas_frames.append(
            f'<mxCell id="{self._id("f")}" value="{esc(etiqueta_html(f["tipo"]))}" style="shape=umlFrame;'
            f'whiteSpace=wrap;html=1;pointerEvents=0;fillColor=none;strokeColor=#36393d;fontStyle=1;fontSize=11;'
            f'width=55;height=22;" vertex="1" parent="1"><mxGeometry x="{x1}" y="{top}" width="{ancho}" '
            f'height="{bottom - top}" as="geometry"/></mxCell>')
        self._guarda(x1 + 62, top + 3, max(120, ancho - 70), f["cond"])
        for ry, cond in f["ramas"]:
            self.celdas_frames.append(
                f'<mxCell id="{self._id("d")}" value="" style="endArrow=none;dashed=1;html=1;strokeColor=#36393d;" '
                f'edge="1" parent="1"><mxGeometry relative="1" as="geometry">'
                f'<mxPoint x="{x1}" y="{ry}" as="sourcePoint"/><mxPoint x="{x2}" y="{ry}" as="targetPoint"/>'
                f'</mxGeometry></mxCell>')
            self._guarda(x1 + 8, ry + 3, max(120, ancho - 20), cond)
        self.y += 14
        if self.pila_frames:
            p = self.pila_frames[-1]
            p["min"] = min(p["min"], x1)
            p["max"] = max(p["max"], x2)
        self.max_x = max(self.max_x, x2 + 20)

    def _guarda(self, x, y, w, cond):
        self.celdas_txt.append(
            f'<mxCell id="{self._id("g")}" value="{esc(etiqueta_html("[" + cond + "]"))}" style="text;html=1;'
            f'align=left;verticalAlign=top;whiteSpace=wrap;fontSize=11;fontStyle=2;" vertex="1" parent="1">'
            f'<mxGeometry x="{x}" y="{y}" width="{w}" height="22" as="geometry"/></mxCell>')

    def ref(self, interaccion, desde, hasta):
        """InteractionUse (17.7.4): marco 'ref' con el nombre de la interaccion referida, sobre las lineas de vida."""
        x1 = min(self.cx(desde), self.cx(hasta)) - 70
        x2 = max(self.cx(desde), self.cx(hasta)) + 70
        top = self.y + 14
        alto = 54
        self.celdas_frames.append(
            f'<mxCell id="{self._id("f")}" value="ref" style="shape=umlFrame;whiteSpace=wrap;html=1;'
            f'pointerEvents=0;fillColor=none;strokeColor=#36393d;fontStyle=1;fontSize=11;width=40;height=20;" '
            f'vertex="1" parent="1"><mxGeometry x="{x1}" y="{top}" width="{x2 - x1}" height="{alto}" '
            f'as="geometry"/></mxCell>')
        self.celdas_txt.append(
            f'<mxCell id="{self._id("t")}" value="{esc(etiqueta_html(interaccion))}" style="text;html=1;'
            f'align=center;verticalAlign=middle;whiteSpace=wrap;fontSize=11;" vertex="1" parent="1">'
            f'<mxGeometry x="{x1 + 44}" y="{top + 18}" width="{x2 - x1 - 52}" height="28" as="geometry"/></mxCell>')
        self._tocar(x1, x2)
        self.y = top + alto + 10

    # ------------------------------------------------------------ salida XML
    def xml(self):
        texto, avisos, _ = conectar_xml(self._xml_crudo())
        for a in avisos:
            print("AVISO", a)
        return "  " + texto.strip() + "\n"

    def _xml_crudo(self):
        fin = self.y + 30
        celdas = ['<mxCell id="0"/>', '<mxCell id="1" parent="0"/>']
        for pid in self.orden:
            p = self.parts[pid]
            cx = p["cx"]
            if p["tipo"] == "actor":
                celdas.append(
                    f'<mxCell id="L-{pid}" value="{esc(etiqueta_html(p["etiqueta"]))}" style="shape=umlActor;'
                    f'verticalLabelPosition=bottom;verticalAlign=top;html=1;outlineConnect=0;fontStyle=1;'
                    f'fontSize=11;whiteSpace=wrap;fillColor={ACTOR_RELLENO};strokeColor={ACTOR_BORDE};" vertex="1" parent="1">'
                    f'<mxGeometry x="{cx - 14}" y="{TOP}" width="28" height="56" as="geometry"/></mxCell>')
                celdas.append(
                    f'<mxCell id="V-{pid}" value="" style="endArrow=none;dashed=1;html=1;strokeColor=#666666;" '
                    f'edge="1" parent="1"><mxGeometry relative="1" as="geometry">'
                    f'<mxPoint x="{cx}" y="{Y0 - 14}" as="sourcePoint"/><mxPoint x="{cx}" y="{fin}" as="targetPoint"/>'
                    f'</mxGeometry></mxCell>')
            else:
                top, ancho = p["top"], p["ancho"]
                celdas.append(
                    f'<mxCell id="L-{pid}" value="{esc(etiqueta_html(p["etiqueta"]))}" style="shape=umlLifeline;'
                    f'perimeter=lifelinePerimeter;whiteSpace=wrap;html=1;container=0;dropTarget=0;size={CABEZA};'
                    f'collapsible=0;recursiveResize=0;outlineConnect=0;portConstraint=eastwest;'
                    f'fillColor={CABEZA_RELLENO};strokeColor={CABEZA_BORDE};fontStyle=1;fontSize=12;" vertex="1" parent="1">'
                    f'<mxGeometry x="{cx - ancho / 2:g}" y="{top:g}" width="{ancho}" '
                    f'height="{fin - top:g}" as="geometry"/></mxCell>')
        # marco de la interaccion: pentagono "sd <nombre>" (17.2.4.1)
        sd = f"sd {self.nombre}"
        ancho_sd = int(len(sd) * 7.4) + 24
        ancho = self.max_x + 10
        celdas.append(
            f'<mxCell id="sd" value="{esc(etiqueta_html(sd))}" style="shape=umlFrame;whiteSpace=wrap;html=1;'
            f'pointerEvents=0;fillColor=none;strokeColor=#000000;fontStyle=1;fontSize=12;align=left;spacingLeft=6;'
            f'width={ancho_sd};height=26;" vertex="1" parent="1">'
            f'<mxGeometry x="10" y="10" width="{ancho}" height="{fin + 10}" as="geometry"/></mxCell>')
        celdas += self.celdas_act + self.celdas_msg + self.celdas_txt + self.celdas_frames
        cuerpo = "\n        ".join(celdas)
        return (f'  <diagram name="{esc(self.nombre)}" id="{esc(nombre_archivo(self.nombre))}">\n'
                f'    <mxGraphModel dx="1400" dy="900" grid="1" gridSize="10" guides="1" tooltips="1" connect="1" '
                f'arrows="1" fold="1" page="1" pageScale="1" pageWidth="{ancho + 40}" pageHeight="{fin + 60}" '
                f'math="0" shadow="0">\n      <root>\n        {cuerpo}\n      </root>\n    </mxGraphModel>\n'
                f'  </diagram>\n')


def nombre_archivo(nombre):
    base = unicodedata.normalize("NFD", nombre.lower())
    base = "".join(ch for ch in base if unicodedata.category(ch) != "Mn")
    return re.sub(r"[^a-z0-9]+", "-", base).strip("-")


