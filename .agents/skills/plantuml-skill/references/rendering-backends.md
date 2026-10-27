# Rendering backends

Select a backend before rendering and disclose where the source goes. Installing
this skill does not install any renderer or system package.

| Backend | Dependencies | Source leaves the machine? | Policy |
|---|---|---|---|
| Host PlantUML CLI | `plantuml`; Graphviz for diagram types that need it | No | Default local backend |
| Local `plantuml.jar` | Java, the jar; Graphviz when needed | No | Local fallback when configured |
| Public Kroki | `curl` and network access | Yes | Explicit approval for the current task only |

## Local helper

Prefer the bundled helper because it performs a syntax check, writes output
atomically, validates PNG/SVG structure, and disables embedded PlantUML source
metadata. It also forces PlantUML's `SANDBOX` security profile and rejects
remote-resource syntax so rendering cannot load URLs or arbitrary local files:

```bash
python3 /path/to/plantuml-skill/scripts/render_plantuml.py diagram.puml \
  --format png --output diagram.png
```

Selection rules:

1. Honor an explicit local backend or jar path.
2. With `--backend auto`, select a usable host `plantuml` command first.
3. Otherwise select `--jar` or `PLANTUML_JAR` when Java and the jar are present.
4. Once selected, keep that backend for the repair loop. Do not hide a render
   failure by silently switching.
5. If no local renderer exists, deliver `.puml` only unless the user separately
   approves public Kroki or asks to install dependencies.

The helper itself never contacts Kroki and never installs dependencies.
Because `SANDBOX` also blocks local file includes, keep source self-contained;
bundled PlantUML standard-library resources such as `<C4/...>` remain usable on
distributions that provide them.

## Public Kroki after approval

Before use, say that the full `.puml` source will be POSTed to `kroki.io`. Do
not use it for secrets or sensitive architecture merely because approval is
available; recommend a local backend in that case.

After explicit approval, render the requested format:

```bash
curl --fail-with-body --silent --show-error \
  -X POST https://kroki.io/plantuml/png \
  -H 'Content-Type: text/plain' \
  --data-binary '@diagram.puml' \
  -o diagram.png
```

Use `/svg` and an `.svg` output for SVG. Check the HTTP exit status, verify the
result is a real PNG or well-formed SVG, then perform the same visual QA as a
local render. Report that the source was uploaded. Approval for one task does
not authorize Kroki for later tasks.

## Security and reproducibility

- Never add remote `!includeurl` merely to make local rendering succeed.
- Keep the helper in `SANDBOX`; do not weaken its profile to accommodate an
  include. Inline trusted local content or use a separately reviewed manual
  workflow when file access is truly required.
- Keep renderer choice and version in the handoff when reproducibility matters.
- The local helper disables PlantUML metadata because exported images may be
  shared separately from their source. A manually invoked renderer should use
  `-nometadata` for the same behavior.
- Treat an existing output as user-owned: the helper replaces it only after the
  new artifact passes structural validation.
