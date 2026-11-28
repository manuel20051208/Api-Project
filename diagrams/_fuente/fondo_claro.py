#!/usr/bin/env python3
"""
Deja los SVG de diagrams/ siempre en fondo claro.

draw.io exporta SVG "adaptativos": con light-dark(claro, oscuro) y color-scheme: light dark se ven oscuros
cuando el navegador o GitHub estan en modo oscuro. Este script se queda con el color claro de cada
light-dark(...), fija el esquema a "light" y pone fondo blanco, asi el diagrama se ve igual en cualquier tema.

Uso:  python diagrams/_fuente/fondo_claro.py            (todos los .svg de diagrams/)
      python diagrams/_fuente/fondo_claro.py a.svg b.svg (solo esos)

Es idempotente: sobre un SVG ya claro no cambia nada. El hook pre-commit (scripts/hooks/pre-commit) lo corre
solo sobre los .svg de diagrams/ que se van a commitear.
"""
import os
import re
import sys

RAIZ = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
CLAVE = "light-dark("


def primer_argumento(texto):
    """light-dark(A, B) -> A, respetando parentesis anidados: rgb(...), var(--x, #fff)."""
    salida, i = [], 0
    while True:
        j = texto.find(CLAVE, i)
        if j < 0:
            salida.append(texto[i:])
            return "".join(salida)
        salida.append(texto[i:j])
        k, prof, coma = j + len(CLAVE), 1, None
        while k < len(texto) and prof:
            ch = texto[k]
            if ch == "(":
                prof += 1
            elif ch == ")":
                prof -= 1
            elif ch == "," and prof == 1 and coma is None:
                coma = k
            k += 1
        if coma is None or prof:  # no es un light-dark bien formado: se deja tal cual
            salida.append(texto[j:j + len(CLAVE)])
            i = j + len(CLAVE)
            continue
        salida.append(primer_argumento(texto[j + len(CLAVE):coma]).strip())
        i = k


def a_claro(svg):
    svg = primer_argumento(svg)
    svg = svg.replace("color-scheme: light dark", "color-scheme: light")
    # fondo blanco en la raiz: sin esto un SVG transparente se ve negro sobre negro en modo oscuro
    svg = re.sub(r"(<svg\b[^>]*?style=\")([^\"]*)(\")", lambda m: m.group(1) + fondo_blanco(m.group(2)) + m.group(3),
                 svg, count=1)
    return svg


def fondo_blanco(estilo):
    estilo = re.sub(r"background(?:-color)?\s*:[^;]*;?\s*", "", estilo).strip()
    return "background: #ffffff; background-color: #ffffff; " + estilo if estilo else "background: #ffffff; background-color: #ffffff;"


def main():
    rutas = sys.argv[1:]
    if not rutas:
        # solo los diagramas UML nuevos: los .svg antiguos de diagrams/ no se tocan
        for sub in ("UML", "Clases", "Secuencia"):
            for d, _, fs in os.walk(os.path.join(RAIZ, "diagrams", sub)):
                rutas += [os.path.join(d, f) for f in fs if f.endswith(".svg")]
    cambiados = 0
    for ruta in rutas:
        crudo = open(ruta, encoding="utf-8", newline="").read()
        nuevo = a_claro(crudo)
        if nuevo != crudo:
            open(ruta, "w", encoding="utf-8", newline="").write(nuevo)
            cambiados += 1
            print("claro:", os.path.relpath(ruta, RAIZ))
    print(f"{cambiados} de {len(rutas)} svg ajustados")


if __name__ == "__main__":
    main()
