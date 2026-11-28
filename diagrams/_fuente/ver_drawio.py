"""Dibuja un XML de draw.io con el VISOR OFICIAL de draw.io (viewer.diagrams.net) en Edge/Chrome headless.

Sirve para ver el diagrama exactamente como lo dibuja draw.io (enrutado de flechas, cortes de texto) sin tener
draw.io instalado. Tambien puede sacar el SVG que dibuja el visor, para exportar un .drawio.svg.

Uso:
  python ver_drawio.py diagrama.xml salida.png [--zoom 0.5] [--ancho 1830 --alto 1360]
  python ver_drawio.py diagrama.xml salida.svg --svg          (SVG crudo del visor; ver exportar_drawio_svg.py)
Necesita conexion a internet (carga viewer-static.min.js) y Edge o Chrome.
"""
import argparse
import html
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile

NAVEGADORES = [
    r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
    r"C:\Program Files\Microsoft\Edge\Application\msedge.exe",
    r"C:\Program Files\Google\Chrome\Application\chrome.exe",
    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
    "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge",
]


def navegador():
    for n in NAVEGADORES:
        if os.path.exists(n):
            return n
    for n in ("msedge", "google-chrome", "chromium", "chrome"):
        if shutil.which(n):
            return shutil.which(n)
    raise SystemExit("No encuentro Edge ni Chrome")


def pagina(xml, zoom, con_svg):
    cfg = {"xml": xml, "toolbar": "", "nav": False, "resize": False, "lightbox": False, "zoom": zoom}
    extra = ""
    if con_svg:
        extra = ("<script>setTimeout(function(){var s=document.querySelector('.mxgraph svg');"
                 "var t=document.createElement('textarea');t.id='out';"
                 "t.textContent=new XMLSerializer().serializeToString(s);document.body.appendChild(t);},8000);</script>")
    return ('<!doctype html><html><head><meta charset="utf-8"><style>body{margin:0;background:#fff}</style></head><body>'
            '<div class="mxgraph" style="border:0" data-mxgraph="' + html.escape(json.dumps(cfg), quote=True) + '"></div>'
            '<script src="https://viewer.diagrams.net/js/viewer-static.min.js"></script>' + extra + '</body></html>')


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("xml")
    ap.add_argument("salida")
    ap.add_argument("--zoom", type=float, default=0.5)
    ap.add_argument("--ancho", type=int)
    ap.add_argument("--alto", type=int)
    ap.add_argument("--svg", action="store_true")
    a = ap.parse_args()
    xml = open(a.xml, encoding="utf-8").read()
    m = re.search(r'pageWidth="(\d+)"[^>]*pageHeight="(\d+)"', xml)
    pw, ph = (int(m.group(1)), int(m.group(2))) if m else (2500, 1800)
    ancho = a.ancho or int(pw * a.zoom) + 60
    alto = a.alto or int(ph * a.zoom) + 60
    tmp = tempfile.mkdtemp()
    html_path = os.path.join(tmp, "ver.html")
    with open(html_path, "w", encoding="utf-8") as f:
        f.write(pagina(xml, 1 if a.svg else a.zoom, a.svg))
    url = "file:///" + html_path.replace("\\", "/")
    base = [navegador(), "--headless", "--disable-gpu", "--virtual-time-budget=20000"]
    if a.svg:
        dom = subprocess.run(base + ["--dump-dom", url], capture_output=True, text=True, encoding="utf-8").stdout
        m = re.search(r'<textarea id="out">(.*?)</textarea>', dom, re.S)
        if not m:
            raise SystemExit("El visor no devolvio el SVG (sin internet?)")
        with open(a.salida, "w", encoding="utf-8") as f:
            f.write(html.unescape(m.group(1)))
    else:
        subprocess.run(base + [f"--screenshot={os.path.abspath(a.salida)}", f"--window-size={ancho},{alto}", url],
                       capture_output=True)
    print(a.salida)


if __name__ == "__main__":
    sys.exit(main())
