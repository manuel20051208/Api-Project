"""Exporta cada pagina de secuencia-casos-de-uso.xml a Diagramas/Secuencia/<nombre>.svg con el VISOR OFICIAL de
draw.io, asi el SVG del README es exactamente lo que se ve en draw.io (incluidos los retoques hechos a mano).
Cada SVG lleva su pagina dentro (atributo content): tambien se abre y se edita en draw.io.

Uso: python Diagramas/_fuente/exportar_secuencia_svg.py [nombre-de-pagina ...]
     sin argumentos exporta las 32. Necesita internet (carga el visor) y Edge o Chrome.
"""
import html
import os
import re
import subprocess
import sys
import tempfile
import unicodedata
from concurrent.futures import ThreadPoolExecutor

AQUI = os.path.dirname(os.path.abspath(__file__))
RAIZ = os.path.abspath(os.path.join(AQUI, "..", ".."))
XML = os.path.join(AQUI, "secuencia-casos-de-uso.xml")
CARPETA = os.path.join(RAIZ, "diagrams", "Secuencia")


def nombre_archivo(nombre):
    base = unicodedata.normalize("NFD", nombre.lower())
    base = "".join(ch for ch in base if unicodedata.category(ch) != "Mn")
    return re.sub(r"[^a-z0-9]+", "-", base).strip("-")


def main():
    texto = open(XML, encoding="utf-8").read()
    tmp = tempfile.mkdtemp()
    trabajos = []
    for m in re.finditer(r'<diagram name="([^"]+)".*?</diagram>', texto, re.S):
        archivo = nombre_archivo(html.unescape(m.group(1)))
        if sys.argv[1:] and archivo not in sys.argv[1:]:
            continue
        pagina = os.path.join(tmp, archivo + ".xml")
        with open(pagina, "w", encoding="utf-8") as f:
            f.write('<?xml version="1.0" encoding="UTF-8"?>\n<mxfile host="app.diagrams.net">\n'
                    + m.group(0) + "\n</mxfile>\n")
        trabajos.append((pagina, os.path.join(CARPETA, archivo + ".svg")))

    def exportar(t):
        r = subprocess.run([sys.executable, os.path.join(AQUI, "exportar_drawio_svg.py"), *t],
                           capture_output=True, text=True)
        return t[1], r.returncode

    os.makedirs(CARPETA, exist_ok=True)
    with ThreadPoolExecutor(4) as ex:
        fallos = [s for s, rc in ex.map(exportar, trabajos) if rc != 0]
    for f in fallos:
        print("FALLO", f)
    print(f"{len(trabajos) - len(fallos)} de {len(trabajos)} svg -> {CARPETA}")
    return 1 if fallos else 0


if __name__ == "__main__":
    sys.exit(main())
