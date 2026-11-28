"""Lee las entidades JPA de src/main/java/com/apiproject/entities/** y los enums de src/main/java/com/apiproject/enums
y devuelve su modelo (campos con su tipo Java real, metodos escritos a mano y relaciones). No hay nada escrito a mano:
si cambia un campo en el .java, cambia el diagrama al regenerar.

Lo que se lee de cada clase:
  - campos de instancia (visibilidad, tipo, nombre, valor inicial) con sus anotaciones;
  - metodos declarados explicitamente en el cuerpo (los de Lombok @Data/@Getter/@Setter/@Builder no existen en el fuente);
  - relaciones SOLO desde atributos: @ManyToOne / @OneToOne / @OneToMany / @ManyToMany, o un atributo cuyo tipo es un enum.
"""
import os
import re

RAIZ = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
BASE = os.path.join(RAIZ, "src", "main", "java", "com", "apiproject")
CARPETA_ENTIDADES = os.path.join(BASE, "entities")
CARPETA_ENUMS = os.path.join(BASE, "enums")


def sin_comentarios(src):
    src = re.sub(r"/\*.*?\*/", " ", src, flags=re.S)
    out = []
    for linea in src.split("\n"):
        i, en_texto = 0, False
        while i < len(linea):
            c = linea[i]
            if c == '"' and (i == 0 or linea[i - 1] != "\\"):
                en_texto = not en_texto
            elif not en_texto and linea.startswith("//", i):
                linea = linea[:i]
                break
            i += 1
        out.append(linea)
    return "\n".join(out)


def cuerpo_clase(src, palabra="class"):
    m = re.search(r"\b(?:public\s+)?(?:final\s+)?" + palabra + r"\s+(\w+)[^{]*\{", src)
    if not m:
        return None, None, 0
    ini = m.end()
    prof, i = 1, ini
    while i < len(src) and prof:
        if src[i] == "{":
            prof += 1
        elif src[i] == "}":
            prof -= 1
        i += 1
    return m.group(1), src[ini:i - 1], m.start()


def anotaciones(texto):
    """Quita las anotaciones del inicio y las devuelve como {nombre: argumentos}."""
    anots, resto, i = {}, texto.strip(), 0
    while True:
        m = re.match(r"\s*@(\w+)", resto[i:])
        if not m:
            break
        nombre, j = m.group(1), i + m.end()
        args = ""
        if resto[j:j + 1] == "(":
            prof, k = 0, j
            while k < len(resto):
                if resto[k] == "(":
                    prof += 1
                elif resto[k] == ")":
                    prof -= 1
                    if prof == 0:
                        break
                k += 1
            args = resto[j + 1:k]
            j = k + 1
        anots[nombre] = args
        i = j
    return anots, resto[i:].strip()


def miembros(cuerpo):
    """Parte el cuerpo de la clase en miembros de primer nivel: ('campo'|'metodo', texto)."""
    res, actual, prof_ll, prof_pa, i = [], "", 0, 0, 0
    while i < len(cuerpo):
        c = cuerpo[i]
        if c == '"':
            j = i + 1
            while j < len(cuerpo) and not (cuerpo[j] == '"' and cuerpo[j - 1] != "\\"):
                j += 1
            actual += cuerpo[i:j + 1]
            i = j + 1
            continue
        actual += c
        if c == "(":
            prof_pa += 1
        elif c == ")":
            prof_pa -= 1
        elif c == "{" and prof_pa == 0:
            if prof_ll == 0 and "(" in re.sub(r"@\w+(\([^)]*\))?", "", actual) and "=" not in actual.split("(")[0]:
                # metodo: consumir el bloque
                prof, j = 1, i + 1
                while j < len(cuerpo) and prof:
                    prof += (cuerpo[j] == "{") - (cuerpo[j] == "}")
                    j += 1
                res.append(("metodo", actual))
                actual, i = "", j
                continue
            prof_ll += 1
        elif c == "}" and prof_pa == 0:
            prof_ll -= 1
        elif c == ";" and prof_pa == 0 and prof_ll == 0:
            res.append(("campo", actual))
            actual = ""
        i += 1
    return res


def parsear_campo(texto):
    anots, decl = anotaciones(texto)
    decl = re.sub(r"\s+", " ", decl.rstrip("; ").strip())
    init = None
    if "=" in decl:
        decl, init = [p.strip() for p in decl.split("=", 1)]
    mods = {"private", "protected", "public", "static", "final", "transient", "volatile"}
    palabras, vis, estatico = decl.split(" "), "~", False
    while palabras and palabras[0] in mods:
        p = palabras.pop(0)
        if p in ("private", "protected", "public"):
            vis = {"private": "-", "protected": "#", "public": "+"}[p]
        if p == "static":
            estatico = True
    resto = " ".join(palabras)
    m = re.match(r"(.+?)\s+(\w+)$", resto)
    if not m:
        return None
    tipo = re.sub(r"\s+", " ", m.group(1)).replace(", ", ",").replace(",", ", ")
    return dict(vis=vis, tipo=tipo, nombre=m.group(2), init=init, anots=anots, estatico=estatico)


def parsear_metodo(texto):
    anots, decl = anotaciones(texto)
    cab = decl.split("{")[0].strip()
    m = re.match(r"((?:(?:public|private|protected|static|final|synchronized|abstract)\s+)*)(?:<[^>]+>\s+)?(.+?)\s+(\w+)\s*\((.*)\)\s*(?:throws [\w, .]+)?$",
                 cab, re.S)
    if not m:
        return None
    mods, ret, nombre, params = m.groups()
    vis = "+" if "public" in mods else "-" if "private" in mods else "#" if "protected" in mods else "~"
    plist = []
    prof, tramo = 0, ""
    for ch in params:
        if ch in "<(":
            prof += 1
        elif ch in ">)":
            prof -= 1
        if ch == "," and prof == 0:
            plist.append(tramo)
            tramo = ""
        else:
            tramo += ch
    if tramo.strip():
        plist.append(tramo)
    ps = []
    for p in plist:
        _, p = anotaciones(p.strip())
        p = re.sub(r"\bfinal\s+", "", p).strip()
        t, n = p.rsplit(" ", 1)
        ps.append((re.sub(r"\s+", " ", t).replace(", ", ",").replace(",", ", "), n))
    return dict(vis=vis, estatico="static" in mods, nombre=nombre, params=ps, ret=re.sub(r"\s+", " ", ret).strip())


def leer_enums():
    enums = {}
    if not os.path.isdir(CARPETA_ENUMS):
        return enums
    for f in sorted(os.listdir(CARPETA_ENUMS)):
        if f.endswith(".java"):
            src = sin_comentarios(open(os.path.join(CARPETA_ENUMS, f), encoding="utf-8").read())
            nombre, cuerpo, _ = cuerpo_clase(src, "enum")
            if nombre:
                lits = cuerpo.split(";")[0]
                enums[nombre] = [l.strip().split("(")[0].strip() for l in lits.split(",") if l.strip()]
    return enums


def leer_entidades():
    """-> (clases, enums). clases: nombre -> dict(paquete, campos, metodos)."""
    clases = {}
    for pkg in sorted(os.listdir(CARPETA_ENTIDADES)):
        d = os.path.join(CARPETA_ENTIDADES, pkg)
        if not os.path.isdir(d):
            continue
        for f in sorted(os.listdir(d)):
            if not f.endswith(".java"):
                continue
            src = sin_comentarios(open(os.path.join(d, f), encoding="utf-8").read())
            nombre, cuerpo, ini = cuerpo_clase(src)
            if not nombre:
                continue
            campos, metodos = [], []
            for tipo, texto in miembros(cuerpo):
                if tipo == "campo":
                    c = parsear_campo(texto)
                    if c and not c["estatico"]:
                        campos.append(c)
                else:
                    m = parsear_metodo(texto)
                    if m:
                        metodos.append(m)
            clases[nombre] = dict(paquete=pkg, campos=campos, metodos=metodos,
                                  lombok=bool(re.search(r"@(Data|Getter|Setter)\b", src[:ini])))
    return clases, leer_enums()


def tipo_base(tipo):
    """List<Product> -> ('Product', True); Product -> ('Product', False)."""
    m = re.match(r"(?:List|Set|Collection)<\s*(\w+)\s*>$", tipo)
    if m:
        return m.group(1), True
    return tipo, False


def relaciones(clases, enums):
    """Una arista por atributo; las dos puntas de una asociacion bidireccional (mappedBy) forman UNA sola arista.

    Devuelve lista de dicts: origen (el TODO / el que tiene la referencia), destino, tipo
    ('asociacion'|'composicion'|'enum'), mult_destino (junto al destino), mult_origen (junto al origen o None),
    navegable (True si solo se navega origen -> destino) y campos (los atributos que la originan).
    """
    aristas, usados = [], set()
    # primero las colecciones con mappedBy (absorben su lado inverso); despues el resto
    pasadas = ([(n, f) for n, c in clases.items() for f in c["campos"] if tipo_base(f["tipo"])[1]]
               + [(n, f) for n, c in clases.items() for f in c["campos"] if not tipo_base(f["tipo"])[1]])
    for nombre, f in pasadas:
        if True:
            if (nombre, f["nombre"]) in usados:
                continue
            base, es_lista = tipo_base(f["tipo"])
            anots = f["anots"]
            if base in enums and not es_lista:
                aristas.append(dict(origen=nombre, destino=base, tipo="enum", mult_destino="0..1", mult_origen=None,
                                    navegable=True, campos=[(nombre, f["nombre"])]))
                usados.add((nombre, f["nombre"]))
                continue
            if base not in clases or base == nombre and False:
                continue
            rel = next((r for r in ("OneToMany", "ManyToMany", "ManyToOne", "OneToOne") if r in anots), None)
            if rel is None:
                continue
            args = anots[rel]
            obligatorio = ("optional = false" in args or "optional=false" in args
                           or "nullable = false" in anots.get("JoinColumn", "")
                           or "nullable=false" in anots.get("JoinColumn", ""))
            mult = "0..*" if es_lista else ("1" if obligatorio else "0..1")
            if rel in ("OneToMany", "ManyToMany") and es_lista:
                inverso = re.search(r'mappedBy\s*=\s*"(\w+)"', args)
                comp = ("CascadeType.ALL" in args or "CascadeType.REMOVE" in args or "orphanRemoval = true" in args)
                mult_origen, campos = None, [(nombre, f["nombre"])]
                if inverso:
                    for g in clases[base]["campos"]:
                        if g["nombre"] == inverso.group(1) and tipo_base(g["tipo"])[0] == nombre:
                            ga = g["anots"]
                            gobl = ("optional = false" in ga.get("ManyToOne", "") or "nullable = false" in ga.get("JoinColumn", ""))
                            mult_origen = "1" if gobl else "0..1"
                            campos.append((base, g["nombre"]))
                            usados.add((base, g["nombre"]))
                aristas.append(dict(origen=nombre, destino=base, tipo="composicion" if comp else "agregacion",
                                    mult_destino=mult, mult_origen=mult_origen, navegable=mult_origen is None,
                                    campos=campos))
            else:
                aristas.append(dict(origen=nombre, destino=base, tipo="asociacion", mult_destino=mult, mult_origen=None,
                                    navegable=True, campos=[(nombre, f["nombre"])]))
            usados.add((nombre, f["nombre"]))
    return aristas


if __name__ == "__main__":
    cl, en = leer_entidades()
    for n, c in cl.items():
        print(n, c["paquete"], len(c["campos"]), "campos", [m["nombre"] for m in c["metodos"]])
    print(en)
    rs = relaciones(cl, en)
    for r in rs:
        print(r["origen"], "->", r["destino"], r["tipo"], r["mult_origen"], r["mult_destino"], r["navegable"], r["campos"])
    print(len(rs), "relaciones")
