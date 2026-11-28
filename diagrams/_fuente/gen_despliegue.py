"""Diagrama de despliegue UML de ApiProject: navegador, VM AWS EC2 (nginx + Docker con la API y PostgreSQL),
GitHub Actions + GHCR (CI/CD) y los servicios externos Cloudinary y Google OAuth2.
Fuentes: Dockerfile, docker-compose.yml, .github/workflows/api-project.yml y README (seccion VM).
Notacion segun OMG UML 2.5.1 (formal/17-12-05):
- 19.4.4: Node = cubo en perspectiva; Device con «device»; ExecutionEnvironment con «executionEnvironment».
- 19.3.4: Artifact = rectangulo con «artifact»; anidado dentro del nodo donde se despliega (19.4.4).
- 19.4.4: CommunicationPath = linea de Association (solida) entre nodos; se rotula con el nombre de la asociacion,
  no con estereotipos propios (que exigirian un Profile, clausula 12).
- 9.2.4.1: palabra clave entre comillas angulares, centrada encima del nombre; nombre centrado y en negrita.
- 7.2.4: Comment = rectangulo con la esquina doblada unido por linea punteada al elemento.
- Anexo A: marco con encabezado [<kind>]<name>; para despliegue el kind es "dep".

Genera el XML de draw.io. Exportar despues con:
  python diagrams/_fuente/exportar_drawio_svg.py diagrams/Despliegue/despliegue.xml diagrams/Despliegue/despliegue.drawio.svg

Uso: python gen_despliegue.py [salida.xml]     (por defecto diagrams/Despliegue/despliegue.xml)
"""
import os
import sys
from xml.sax.saxutils import escape

AQUI = os.path.dirname(os.path.abspath(__file__))
SALIDA = sys.argv[1] if len(sys.argv) > 1 else os.path.join(AQUI, "..", "Despliegue", "despliegue.xml")
PAGE_W, PAGE_H = 1780, 820

NODO = ("shape=cube;size=12;direction=south;html=1;whiteSpace=wrap;boundedLbl=1;verticalAlign=top;align=center;"
        "spacingTop=4;fontSize=12;strokeColor=#36393d;")
ENTORNO = NODO.replace("size=12", "size=8")
ARTEFACTO = ("rounded=0;html=1;whiteSpace=wrap;fontSize=11;align=center;verticalAlign=middle;"
             "strokeColor=#36393d;fillColor=#ffffff;")
ENLACE = ("edgeStyle=orthogonalEdgeStyle;rounded=0;html=1;endArrow=none;startArrow=none;strokeColor=#000000;"
          "fontSize=11;labelBackgroundColor=#ffffff;")

celdas = []
nodos = set()   # cajas 3D (direction=south): draw.io gira sus puntos de conexion


def girar(id_, punto):
    """Con direction=south draw.io lleva el punto (x, y) a (1 - y, x); se pasa el inverso para que quede donde se pide."""
    x, y = punto
    return (round(y, 4), round(1 - x, 4)) if id_ in nodos else (x, y)


def caja(id_, x, y, w, h, texto, estilo):
    celdas.append(f'<mxCell id="{id_}" value="{escape(texto, {chr(34): "&quot;"})}" style="{estilo}" vertex="1" '
                  f'parent="1"><mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry"/></mxCell>')


def nodo(id_, x, y, w, h, palabra, nombre, relleno="#dae8fc", estilo=NODO):
    """Nodo UML: caja 3D con su palabra clave (si tiene) y su nombre."""
    texto = (f"&laquo;{palabra}&raquo;<br>" if palabra else "") + f"<b>{nombre}</b>"
    nodos.add(id_)
    caja(id_, x, y, w, h, texto, estilo + f"fillColor={relleno};")


def artefacto(id_, x, y, w, h, nombre):
    caja(id_, x, y, w, h, f"&laquo;artifact&raquo;<br><b>{nombre}</b>", ARTEFACTO)


def nota(id_, x, y, w, h, texto):
    caja(id_, x, y, w, h, texto, "shape=note;size=14;html=1;whiteSpace=wrap;fontSize=11;align=left;spacingLeft=8;"
         "strokeColor=#36393d;fillColor=#ffffff;")


def enlace(id_, origen, destino, texto, salida, entrada, puntos=(), extra=""):
    sx, sy = girar(origen, salida)
    ex, ey = girar(destino, entrada)
    pts = "".join(f'<mxPoint x="{x}" y="{y}"/>' for x, y in puntos)
    geo = (f'<mxGeometry relative="1" as="geometry"><Array as="points">{pts}</Array></mxGeometry>' if pts
           else '<mxGeometry relative="1" as="geometry"/>')
    estilo = (ENLACE + f"exitX={sx};exitY={sy};exitDx=0;exitDy=0;entryX={ex};entryY={ey};entryDx=0;entryDy=0;"
              + extra)
    celdas.append(f'<mxCell id="{id_}" value="{escape(texto, {chr(34): "&quot;"})}" style="{estilo}" edge="1" '
                  f'parent="1" source="{origen}" target="{destino}">{geo}</mxCell>')


# ---------------------------------------------------------------- marco (Anexo A): encabezado "dep <nombre>"
caja("marco", 20, 90, 1740, 700, "<b>dep</b> ApiProject",
     "shape=umlFrame;whiteSpace=wrap;html=1;width=210;height=30;boundedLbl=1;verticalAlign=middle;align=left;"
     "spacingLeft=8;fontSize=13;fillColor=none;strokeColor=#36393d;")

# ---------------------------------------------------------------- nodos
nodo("cli", 40, 230, 220, 180, "device", "Dispositivo del usuario", "#f5f5f5")
nodo("nav", 60, 290, 180, 100, "executionEnvironment", "Navegador web", "#ffffff", ENTORNO)

nodo("ec2", 380, 150, 600, 520, "device", "AWS EC2 (Ubuntu)", "#ffe6cc")
nodo("nginx", 410, 300, 150, 100, "executionEnvironment", "Nginx", "#fff5eb", ENTORNO)
nodo("docker", 620, 215, 330, 440, "executionEnvironment", "Docker (compose)", "#fff5eb", ENTORNO)
nodo("api", 645, 272, 280, 165, "executionEnvironment",
     "Contenedor apiproject-api<br><span style=\"font-weight:normal\">eclipse-temurin:25-jre-alpine</span>",
     "#ffffff", ENTORNO)
artefacto("jar", 670, 355, 230, 60, "ApiProject-0.0.1-SNAPSHOT.jar")
nodo("db", 645, 492, 280, 150, "executionEnvironment",
     "Contenedor apiproject-db", "#ffffff", ENTORNO)
nodo("pg", 670, 552, 230, 70, "executionEnvironment", "PostgreSQL 16<br>"
     "<span style=\"font-weight:normal\">postgres:16-alpine</span>", "#f0f7ec", ENTORNO)

nodo("goo", 1100, 170, 240, 90, "", "Google OAuth2", "#f5f5f5")
nodo("cld", 1100, 300, 240, 90, "", "Cloudinary", "#f5f5f5")

nodo("ghcr", 1100, 470, 260, 170, "", "GitHub Container Registry", "#e1d5e7")
artefacto("img", 1115, 560, 230, 60, "ghcr.io/manuel20051208/<br>api-project:&lt;sha&gt;")
nodo("gha", 1460, 470, 260, 170, "", "GitHub Actions", "#e1d5e7")
nodo("run", 1485, 540, 210, 80, "executionEnvironment", "Runner ubuntu-latest", "#f8f0fb", ENTORNO)

nota("nota_vm", 390, 700, 270, 50, "Comparte la VM (1 GB RAM + 2 GB swap) con horizontal-api (127.0.0.1:8080)")
nota("nota_front", 40, 450, 220, 80, "El front-end corre en el navegador; su origen se configura en "
     "URL_FOR_REQUESTING_AND_RESPONSIVES (CORS)")

# ---------------------------------------------------------------- caminos de comunicacion
enlace("c1", "nav", "nginx", "HTTPS :443<br>bizadmin.duckdns.org", (1, 0.5), (0, 0.5))
enlace("c2", "nginx", "api", "HTTP<br>127.0.0.1:8081", (1, 0.5), (0, 0.5))
enlace("c3", "api", "db", "JDBC :5432", (0.5, 1), (0.5, 0))
enlace("c4", "api", "goo", "HTTPS :443", (1, 0.2), (0, 0.5), ((1000, 305), (1000, 215)))
enlace("c5", "api", "cld", "HTTPS :443", (1, 0.4485), (0, 0.5))
enlace("c6", "docker", "ghcr", "HTTPS :443<br>docker pull", (1, 0.7727), (0, 0.5))
enlace("c7", "run", "ghcr", "HTTPS<br>docker push", (0, 0.5), (1, 0.5))
enlace("c8", "gha", "ec2", "SSH :22 (deploy)", (0.5, 1), (0.5, 1), ((1590, 730), (680, 730)))
enlace("c9", "nav", "goo", "HTTPS :443 login OAuth2", (0.5, 0), (0.5, 0), ((150, 130), (1220, 130)))
for i, (o, d) in enumerate((("nota_vm", "ec2"), ("nota_front", "cli"))):
    celdas.append(f'<mxCell id="ancla{i}" style="endArrow=none;dashed=1;html=1;strokeColor=#36393d;" edge="1" '
                  f'parent="1" source="{o}" target="{d}"><mxGeometry relative="1" as="geometry"/></mxCell>')

os.makedirs(os.path.dirname(os.path.abspath(SALIDA)), exist_ok=True)
with open(SALIDA, "w", encoding="utf-8") as f:
    f.write('<?xml version="1.0" encoding="UTF-8"?>\n<mxfile host="app.diagrams.net">\n'
            '  <diagram name="Despliegue" id="despliegue">\n'
            f'    <mxGraphModel grid="1" gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" '
            f'page="1" pageScale="1" pageWidth="{PAGE_W}" pageHeight="{PAGE_H}" background="#ffffff" math="0" '
            'shadow="0">\n      <root>\n        <mxCell id="0"/>\n        <mxCell id="1" parent="0"/>\n'
            + "".join(f"        {c}\n" for c in celdas)
            + "      </root>\n    </mxGraphModel>\n  </diagram>\n</mxfile>\n")
print(SALIDA)
