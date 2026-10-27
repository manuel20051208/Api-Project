# PlantUML troubleshooting

Read this reference after a renderer or visual-QA failure. Preserve the raw
diagnostic, fix the smallest cause, and re-run syntax validation before render.

## Diagnostic order

1. Confirm the intended source and backend: `plantuml -version`, `dot -V`, or
   `java -jar /path/to/plantuml.jar -version`.
2. Run the bundled helper and read both stdout and stderr. It executes
   `-checkonly` before rendering.
3. Fix the exact cited line. Check wrappers, balanced braces, quotes, and block
   terminators.
4. Remove version-sensitive styling or features only after the semantic source
   is understood.
5. Re-render and visually inspect. A PNG signature alone does not prove success:
   some PlantUML versions can emit a valid PNG containing an error message.

## Reliable subset

- Sequence diagrams with balanced `alt`, `opt`, `loop`, `par`, and activation.
- Component, package, class, object, deployment, use-case, activity, state, and
  entity diagrams with modest styling.
- Mind maps and Gantt diagrams with their matching start/end wrappers.
- C4 standard-library includes only when the selected local distribution
  provides them.

Treat sprites, icon packs, remote includes, custom fonts, exotic shapes, and
large `skinparam` blocks as optional and version-sensitive.

## Common failures

| Symptom | Likely cause | Targeted fix |
|---|---|---|
| Missing start/end error | Wrong or unmatched wrapper | Pair `@startuml`/`@enduml`, `@startmindmap`/`@endmindmap`, or `@startgantt`/`@endgantt` |
| Error near a label | Smart quotes, unescaped text, or unbalanced quotes | Retype punctuation and quote the full label |
| Error after a group | Missing `}` or `end` | Balance `package`/class braces and sequence/activity blocks |
| C4 include not found | Local distribution lacks C4 or include name is wrong | Verify `<C4/C4_Context>` locally or translate to plain components; do not fetch remotely |
| Graphviz executable missing | `dot` is unavailable for the selected diagram | Report the missing local dependency; install only when asked |
| Output file is absent | Renderer failed, timed out, or emitted multiple diagrams | Read diagnostics; keep one diagram per `.puml` |
| Text is boxes or mojibake | Host font lacks glyphs or source encoding is wrong | Keep UTF-8 and use an installed font only when necessary |
| Layout is huge or cramped | Too many nodes, edges, or detail levels | Split by concern, reorder declarations, group, or change direction |

## Syntax reminders

- Sequence: every `alt`, `opt`, `loop`, `par`, and `group` needs `end`; declare
  participants in the desired left-to-right order.
- Class: use `<|--` for inheritance, `<|..` for realization, `*--` for
  composition, and `o--` for aggregation; quote multiplicities.
- Activity: do not mix legacy `(*)` syntax with modern `start`/`stop`; close
  every `if` with `endif`.
- State: use `[*]` for initial/final pseudo-states; brace composite states.
- ER: place crow's-foot cardinality between entities and put `--` alone as an
  attribute separator.
- Component: overlap is usually a layout problem, not a parser error.

## Simplification ladder

After a targeted fix fails, simplify one layer at a time and re-render:

1. replace unsupported shapes or includes with plain elements;
2. remove theme and styling;
3. remove or shorten notes and special-character-heavy labels;
4. remove redundant edges;
5. split the diagram or choose a simpler diagram type.

Stop after three repair rounds and report the raw error rather than claiming a
successful artifact.
