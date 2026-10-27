# PlantUML visual style guide

Apply this guide to newly generated diagrams unless the user, repository, or
existing document defines a visual system. Prefer a restrained engineering
style: clear hierarchy, generous spacing, semantic color, and minimal ornament.

## Contents

- [Design goals](#design-goals)
- [Default palette](#default-palette)
- [Shared base style](#shared-base-style)
- [Structural diagrams](#structural-diagrams)
- [Sequence diagrams](#sequence-diagrams)
- [Class and ER diagrams](#class-and-er-diagrams)
- [Activity and state diagrams](#activity-and-state-diagrams)
- [C4 diagrams](#c4-diagrams)
- [Composition and typography](#composition-and-typography)
- [Existing styles and output targets](#existing-styles-and-output-targets)
- [Visual QA](#visual-qa)

## Design goals

1. Make the primary reading path obvious before adding detail.
2. Use typography, spacing, grouping, and labels before relying on color.
3. Give color a stable semantic role; do not assign a new hue to every node.
4. Keep backgrounds light, borders visible, and text contrast high.
5. Use one abstraction level and one dominant flow direction per diagram.
6. Optimize for the destination: docs, chat preview, slides, or print.

## Default palette

Use the palette consistently rather than treating it as decoration.

| Role | Fill | Border | Text | Typical use |
|---|---|---|---|---|
| Canvas | `#F8FAFC` | — | `#0F172A` | page background |
| Primary | `#EFF6FF` | `#2563EB` | `#1E3A8A` | application components and main actors |
| Data/success | `#ECFDF5` | `#0F766E` | `#134E4A` | databases, validated output, completed states |
| External/warning | `#FFF7ED` | `#C2410C` | `#7C2D12` | third-party systems, approval gates, cautions |
| Risk/error | `#FEF2F2` | `#DC2626` | `#7F1D1D` | failures and destructive paths only |
| Neutral group | `#FFFFFF` | `#CBD5E1` | `#334155` | boundaries, packages, notes, secondary elements |
| Connector | — | `#64748B` | `#475569` | arrows and edge labels |

Do not use red for ordinary external services. Reserve it for an actual error,
denial, destructive action, or high-risk boundary.

## Shared base style

Copy this block after the diagram start marker. `SansSerif` is a Java logical
font and is more portable across hosts than a product-specific font name.

```text
!theme plain
skinparam backgroundColor #F8FAFC
skinparam shadowing false
skinparam roundCorner 10
skinparam defaultFontName SansSerif
skinparam defaultFontSize 13
skinparam defaultFontColor #0F172A
skinparam ArrowColor #64748B
skinparam ArrowFontColor #475569
skinparam ArrowFontSize 12
skinparam ArrowThickness 1.1
skinparam TitleFontColor #0F172A
skinparam TitleFontSize 20
skinparam TitleFontStyle bold
skinparam NoteBackgroundColor #FFFBEB
skinparam NoteBorderColor #F59E0B
skinparam NoteFontColor #78350F
skinparam LegendBackgroundColor #FFFFFF
skinparam LegendBorderColor #CBD5E1
skinparam LegendFontColor #334155
```

Keep titles short and factual. Omit the title when a surrounding document
already supplies an equivalent heading.

## Structural diagrams

Add this profile for component, architecture, and deployment diagrams:

```text
skinparam componentStyle rectangle
skinparam packageStyle rectangle
skinparam rectangleBackgroundColor #FFFFFF
skinparam rectangleBorderColor #CBD5E1
skinparam rectangleFontColor #334155
skinparam componentBackgroundColor #EFF6FF
skinparam componentBorderColor #2563EB
skinparam componentFontColor #1E3A8A
skinparam databaseBackgroundColor #ECFDF5
skinparam databaseBorderColor #0F766E
skinparam databaseFontColor #134E4A
skinparam queueBackgroundColor #F0FDFA
skinparam queueBorderColor #0F766E
skinparam queueFontColor #134E4A
skinparam cloudBackgroundColor #FFF7ED
skinparam cloudBorderColor #C2410C
skinparam cloudFontColor #7C2D12
skinparam nodeBackgroundColor #FFFFFF
skinparam nodeBorderColor #94A3B8
skinparam nodeFontColor #334155
skinparam actorBackgroundColor #EFF6FF
skinparam actorBorderColor #2563EB
skinparam actorFontColor #1E3A8A
skinparam nodesep 45
skinparam ranksep 40
```

Use neutral fills for boundaries and semantic fills for contained elements.
Label edges with protocol, data, or intent. Use dashed edges for optional,
asynchronous, or approval-gated paths only when that meaning is explicit.

## Sequence diagrams

Use the shared base plus this profile:

```text
skinparam ParticipantPadding 24
skinparam BoxPadding 12
skinparam maxMessageSize 70
skinparam sequence {
  ArrowColor #64748B
  ArrowFontColor #475569
  LifeLineBorderColor #CBD5E1
  LifeLineBackgroundColor #F8FAFC
  ParticipantBackgroundColor #EFF6FF
  ParticipantBorderColor #2563EB
  ParticipantFontColor #1E3A8A
  ActorBackgroundColor #EFF6FF
  ActorBorderColor #2563EB
  ActorFontColor #1E3A8A
  GroupBackgroundColor #F8FAFC
  GroupBorderColor #CBD5E1
  GroupHeaderFontColor #334155
  DividerBackgroundColor #E2E8F0
  DividerBorderColor #94A3B8
  DividerFontColor #334155
}
```

- Declare participants in reading order.
- Keep messages verb-led and usually below 50 characters; wrap intentionally.
- Use activation bars only when execution ownership matters.
- Highlight only the exceptional branch, not every `alt` section.

## Class and ER diagrams

Use neutral classes so relationships and stereotypes remain the focus:

```text
skinparam classBackgroundColor #FFFFFF
skinparam classBorderColor #64748B
skinparam classFontColor #0F172A
skinparam classAttributeIconSize 0
skinparam entityBackgroundColor #ECFDF5
skinparam entityBorderColor #0F766E
skinparam entityFontColor #134E4A
```

Show only members needed to explain the relationship. Separate attributes from
operations and order them consistently. In ER diagrams, emphasize primary and
foreign keys typographically; do not add a legend for standard PK/FK notation.

## Activity and state diagrams

Use blue for normal work, orange for decisions or waiting, green for successful
terminal states, and red only for failure states. Apply colors to the few nodes
that carry those semantics rather than coloring every step individually.

```text
skinparam activityBackgroundColor #EFF6FF
skinparam activityBorderColor #2563EB
skinparam activityFontColor #1E3A8A
skinparam activityDiamondBackgroundColor #FFF7ED
skinparam activityDiamondBorderColor #C2410C
skinparam stateBackgroundColor #FFFFFF
skinparam stateBorderColor #64748B
skinparam stateFontColor #0F172A
```

Use `#ECFDF5`/`#0F766E` inline for success and `#FEF2F2`/`#DC2626` for failure
when a diagram genuinely needs that distinction.

## C4 diagrams

Prefer the C4 library's native visual grammar and one level per diagram. Use
`LAYOUT_LEFT_RIGHT()` or `LAYOUT_TOP_DOWN()` deliberately and apply documented
C4 style macros only when the default hierarchy needs emphasis. Do not paste
generic component styling over C4 macros if it degrades the notation.

Keep descriptions to one short sentence and technologies to compact labels.
Use a legend only when the audience needs notation help or semantic colors.

## Composition and typography

- Use 16–24 px equivalent titles, 12–14 px labels, and no more than two font
  weights. The shared style defaults to 20 and 13.
- Target roughly 40–60 px of whitespace between primary nodes. Reduce content
  before reducing font size.
- Prefer 4:3 or near-square diagrams for chat and docs; use 16:9 only for slide
  layouts or naturally horizontal flows.
- Keep edge labels close to their connector and avoid sentence-length prose.
- Use `\n` for intentional two-line labels. Avoid three or more lines inside a
  normal node.
- Add a legend only for nonstandard colors, line styles, or symbols.
- Avoid gradients, heavy shadows, dark themes, decorative icons, and rainbow
  palettes unless the user or repository explicitly requires them.

## Existing styles and output targets

- When refining an existing diagram, preserve its established palette and
  typography unless the user asks for a restyle. Fix inconsistencies locally.
- When a repository has brand tokens, map the semantic roles above to those
  tokens while retaining contrast and hierarchy.
- For PNG, render large enough for the destination rather than relying on
  upscaling. For SVG, avoid custom fonts that may not exist in the viewer.
- For monochrome print, ensure group, state, and risk distinctions remain clear
  through labels and line styles after color is removed.

## Visual QA

After rendering, verify:

- one dominant reading direction and an obvious entry point;
- consistent fills, borders, corner treatment, and typography;
- semantic colors are used consistently and sparingly;
- no clipped labels, crowded nodes, orphaned notes, or overlapping edge labels;
- color contrast remains readable and meaning is not color-only;
- the title and legend earn their space;
- the artifact fits its destination without excessive empty canvas or scrolling.

When a diagram fails visual QA, simplify content and improve grouping before
adding more layout directives.
