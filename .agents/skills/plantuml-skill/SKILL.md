---
name: plantuml-skill
description: Create, render, inspect, refine, and restyle PlantUML diagrams as editable .puml source plus PNG or SVG. Use for sequence, class, component, deployment, activity or flowchart, state, use-case, ER or database, C4 or architecture, mind-map, and Gantt diagrams; for diagrams derived from source code; for PlantUML fenced blocks or linked .puml files in Markdown; and when a system explanation with several interacting components, APIs, data flows, or type relationships benefits from a diagram. Prefer local rendering and never upload source to Kroki without explicit user approval.
---

# PlantUML diagrams

## Core contract

- Produce an editable `.puml` source file. Render PNG or SVG when an approved
  backend is available.
- Prefer local execution: host `plantuml`, then a user-provided or configured
  local `plantuml.jar`.
- Do not install Java, Graphviz, PlantUML, or other packages unless the user asks.
- Do not use public Kroki or remote `!includeurl` directives without explicit
  approval for the current task. Explain that the diagram source will leave the
  machine before requesting or acting on that approval.
- Ground diagrams of code and systems in inspected evidence. Mark inferences;
  do not present guessed components, calls, cardinalities, or states as facts.
- Do not claim success until syntax, output bytes, and visual readability have
  been checked.
- Apply a restrained, consistent visual system to new diagrams. Preserve an
  established repository or document style when one exists.

## Route the request

Read only the playbooks needed for the request:

| Request | Required playbook |
|---|---|
| Create or refine a diagram | This workflow; read [`references/authoring-guide.md`](references/authoring-guide.md) and [`references/style-guide.md`](references/style-guide.md) |
| Derive a diagram from a repository or source files | Read [`references/from-source-code.md`](references/from-source-code.md) |
| Extract or render PlantUML in Markdown | Read [`references/markdown-embed.md`](references/markdown-embed.md) |
| Select a renderer or use approved Kroki | Read [`references/rendering-backends.md`](references/rendering-backends.md) |
| Diagnose a render or layout failure | Read [`references/plantuml-troubleshooting.md`](references/plantuml-troubleshooting.md) |
| Review an existing rendered diagram | Start at visual QA below; inspect its `.puml` when available |

If the user explicitly requests Mermaid, draw.io, Excalidraw, or another format,
honor that format instead of converting the task to PlantUML.

## Workflow

### 1. Establish the deliverable

- Inspect any existing source, image, Markdown document, and surrounding repo
  conventions before editing.
- Preserve the requested filename and format. Otherwise choose a stable,
  descriptive kebab-case basename and default to `.puml` plus PNG.
- Prefer SVG for web documentation or scalable line art when the target supports
  it; prefer PNG for chat preview and broad document compatibility.
- Keep one diagram per `.puml` file so rendering, review, and later edits stay
  unambiguous.

### 2. Choose the diagram model

| Evidence or intent | Diagram type |
|---|---|
| Time-ordered calls, protocol, request lifecycle | Sequence |
| Services, modules, dependencies, deployment boundaries | Component or deployment |
| Types, interfaces, inheritance, ownership | Class |
| Tables, keys, cardinalities | ER |
| Decisions and procedural flow | Activity |
| Lifecycle and allowed transitions | State |
| Actors and user-visible capabilities | Use case |
| System boundary and audience-level architecture | C4 context/container/component |
| Topic hierarchy | Mind map |
| Dated work and dependencies | Gantt |

Choose the smallest diagram that answers the question. Split by concern or
abstraction level when a single view becomes crowded; do not mix C4 context,
container, and component detail in one view.

### 3. Author the source

- Match the user's language. Keep labels concise and use explicit aliases.
- Apply the palette, typography, spacing, and diagram-specific profile from
  [`references/style-guide.md`](references/style-guide.md). Use semantic color
  sparingly; do not create a rainbow of unrelated node colors.
- Encode semantic relationships accurately; do not use arrow direction only for
  visual convenience when it changes meaning.
- Declare sequence participants in desired order. Group related architecture
  elements with `package`, `rectangle`, or C4 boundaries.
- Start with plain, portable syntax. Avoid custom fonts, sprites, remote includes,
  and heavy styling until the structure renders correctly.
- Use `!include <C4/...>` only when the selected local renderer provides that
  standard library. If it does not, use plain components rather than a remote
  include.
- Apply layout hints only after the semantic graph is correct.

Read [`references/authoring-guide.md`](references/authoring-guide.md) for compact
templates, relation syntax, styling defaults, and layout heuristics.

### 4. Render locally

Resolve `scripts/render_plantuml.py` relative to this `SKILL.md`. The helper:

- selects only local backends;
- forces PlantUML's `SANDBOX` security profile and rejects remote-resource
  syntax, blocking URL and local-file resource loading during rendering;
- runs a syntax check before rendering;
- renders into a temporary directory and atomically installs validated output;
- disables embedded PlantUML source metadata in the exported image;
- never installs dependencies or contacts the network.

```bash
python3 /path/to/plantuml-skill/scripts/render_plantuml.py diagram.puml \
  --format png --output diagram.png
```

Use an explicit local jar when requested:

```bash
python3 /path/to/plantuml-skill/scripts/render_plantuml.py diagram.puml \
  --backend jar --jar /path/to/plantuml.jar --format svg
```

With `--backend auto`, choose the host CLI first and then `--jar` or
`PLANTUML_JAR`. Once a backend is selected, do not silently switch after a render
failure. If no local backend exists, keep the `.puml` and report that rendering
was skipped. Public Kroki is a separate, approval-gated path described in
[`references/rendering-backends.md`](references/rendering-backends.md).

### 5. Repair syntax failures

Read the complete renderer diagnostic and fix the cited line first. Re-run the
syntax check and render after every edit. Make at most three targeted repair
rounds, simplifying in this order when needed:

1. remove unsupported shapes, sprites, or includes;
2. remove themes and custom `skinparam` blocks;
3. simplify notes and labels;
4. reduce nonessential relationships;
5. use a simpler diagram type that preserves the core meaning.

Use [`references/plantuml-troubleshooting.md`](references/plantuml-troubleshooting.md)
for error-specific fixes. If the third repair still fails, stop and report the
raw diagnostic and the valid `.puml` work completed so far.

### 6. Perform visual QA

Inspect the rendered artifact with the available image-viewing capability. A
successful renderer command is insufficient; PlantUML can produce a valid image
that is unreadable or even an error image on some versions.

Check:

- all requested elements and only supported/in-scope elements are present;
- labels are legible, unclipped, correctly encoded, and free of mojibake;
- relationship direction, cardinality, ordering, and boundary placement match
  the source evidence;
- contrast works and the aspect ratio is suitable for the destination;
- palette, font treatment, border weight, corner treatment, and whitespace are
  consistent with the selected visual system;
- crossings, long arrows, and dense clusters do not hide the main reading path;
- title, legend, notes, and abstraction level are useful rather than decorative.

Make at most two layout/readability repair rounds. Re-render and re-check after
each change. Prefer reordering declarations, changing direction, grouping, or
splitting the view before adding brittle layout tricks. If image inspection is
unavailable, validate the file and disclose that visual QA was skipped.

### 7. Refine safely

- Modify the existing `.puml` rather than reconstructing it from the image.
- Apply the smallest semantic edit that satisfies feedback.
- Re-run syntax validation, rendering, and visual QA after every change.
- Overwrite the intended output; do not accumulate `v2-final-final` files unless
  the user asks to preserve versions.
- Preserve unrelated user changes in a dirty worktree.

### 8. Hand off

Report:

- clickable paths to the `.puml` source and each requested PNG/SVG artifact;
- the diagram type and scope in one sentence;
- the exact backend used;
- whether the source stayed local or was uploaded after approval;
- any skipped render, visual-QA limitation, inference, or unresolved issue.

Do not include dependency-install instructions unless they are relevant to a
blocked render or the user asks for setup help.
