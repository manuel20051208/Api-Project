# PlantUML authoring guide

Use this reference when creating or refining `.puml` source. Start with the
smallest relevant template and add detail only when it improves the answer.

## Contents

- [Portable defaults](#portable-defaults)
- [Visual defaults](#visual-defaults)
- [Layout heuristics](#layout-heuristics)
- [Sequence](#sequence)
- [Component and architecture](#component-and-architecture)
- [Class and ER](#class-and-er)
- [Activity and state](#activity-and-state)
- [C4](#c4)
- [Other diagram types](#other-diagram-types)

## Portable defaults

Use a self-contained file and one diagram per file:

```plantuml
@startuml
!theme plain

' Copy the shared base and diagram-specific profile from style-guide.md here.

title Concise diagram title

' diagram body
@enduml
```

- Keep identifiers ASCII and stable; put user-facing text in quoted labels.
- Match the user's language and avoid unexplained abbreviations.
- Avoid `FontName` unless the destination environment guarantees the font.
- Avoid remote `!includeurl`, sprites, and icon packs. Prefer basic shapes.
- Treat colors as secondary encoding. Preserve readable contrast and add labels.
- Use `\n` to wrap long labels deliberately.

## Visual defaults

Read [`style-guide.md`](style-guide.md) for the shared base style, semantic
palette, diagram-specific profiles, typography, spacing, and visual-QA rules.
Apply it to new diagrams unless the user, repository, or surrounding document
already defines a style. The syntax examples below emphasize semantics and omit
the repeated style block for readability.

## Layout heuristics

- Use `left to right direction` for pipelines and layered architecture; use
  `top to bottom direction` for hierarchies and long labels.
- Declare elements in the intended reading order before adding directional arrow
  hints such as `-right->` or `-down->`.
- Group related elements with `package`, `rectangle`, or `together { }`.
- Use hidden edges only as a last layout hint: `A -[hidden]-> B`.
- Split a view when it has roughly more than 12 primary nodes, 20 relationships,
  several abstraction levels, or labels that need paragraph-length text.

## Sequence

Use for a single time-ordered scenario. Declare participants explicitly and
show important alternatives; omit implementation calls that do not affect the
reader's understanding.

```plantuml
@startuml
!theme plain
title Login request

actor User as U
participant "Web app" as W
participant "Auth API" as A
database "User DB" as D

U -> W : Submit credentials
W -> A : POST /login
A -> D : Find user
D --> A : User record
alt credentials valid
  A --> W : Access token
  W --> U : Signed in
else invalid
  A --> W : 401 Unauthorized
  W --> U : Show error
end
@enduml
```

Useful constructs: `alt`/`else`/`end`, `opt`, `loop`, `par`, `activate`, and
`deactivate`. Use `->` for calls, `-->` for responses, and `->>` for asynchronous
messages only when that distinction is factual.

## Component and architecture

Use for modules, services, storage, queues, and trust or deployment boundaries.

```plantuml
@startuml
!theme plain
skinparam componentStyle rectangle
left to right direction

actor Client as client
rectangle "Application" {
  component "API" as api
  component "Worker" as worker
}
database "Primary DB" as db
queue "Jobs" as jobs

client --> api : HTTPS
api --> db : read/write
api --> jobs : enqueue
jobs --> worker : deliver
@enduml
```

Common shapes: `actor`, `component`, `rectangle`, `package`, `node`, `database`,
`queue`, and `cloud`. Label edges with protocol, data, or intent when known.

## Class and ER

Use class diagrams for type semantics:

```plantuml
@startuml
interface Repository
class UserRepository
class User

Repository <|.. UserRepository
UserRepository --> User : loads
@enduml
```

Relationship meanings:

| Syntax | Meaning |
|---|---|
| `Base <|-- Child` | inheritance |
| `Interface <|.. Impl` | realization |
| `Whole *-- Part` | composition/lifecycle ownership |
| `Whole o-- Part` | aggregation |
| `A --> B` | directed association or dependency |

Do not infer composition merely because one type references another.

Use ER diagrams only when keys and cardinalities are supported by a schema or
model definition:

```plantuml
@startuml
entity "USER" as user {
  * id : bigint <<PK>>
  --
  email : varchar
}
entity "ORDER" as orders {
  * id : bigint <<PK>>
  --
  * user_id : bigint <<FK>>
}
user ||--o{ orders : places
@enduml
```

## Activity and state

Use activity diagrams for procedures and decisions:

```plantuml
@startuml
start
:Validate request;
if (Valid?) then (yes)
  :Process;
else (no)
  :Reject;
endif
stop
@enduml
```

Use state diagrams for allowed lifecycle transitions:

```plantuml
@startuml
[*] --> Pending
Pending --> Running : start
Running --> Succeeded : complete
Running --> Failed : error
Succeeded --> [*]
Failed --> [*]
@enduml
```

Do not turn a procedural flow into a state diagram unless states persist and
transitions are meaningful domain events.

## C4

Use one C4 level per diagram. A local PlantUML distribution may provide the C4
standard library:

```plantuml
@startuml
!include <C4/C4_Context>
title Payment system context

Person(customer, "Customer", "Pays for an order")
System(payment, "Payment system", "Processes payments")
System_Ext(bank, "Bank", "Authorizes charges")

Rel(customer, payment, "Uses", "HTTPS")
Rel(payment, bank, "Authorizes with", "HTTPS")
@enduml
```

Use `<C4/C4_Container>` or `<C4/C4_Component>` for lower levels. If the local
renderer lacks C4, translate the same semantics into plain PlantUML components;
do not fetch a remote include without explicit approval.

## Other diagram types

| Type | Wrapper or key syntax | Use for |
|---|---|---|
| Use case | `actor`, `usecase` | actors and user-visible capabilities |
| Deployment | `node`, `artifact`, `database` | runtime placement and infrastructure |
| Mind map | `@startmindmap` / `@endmindmap` | concept hierarchy |
| Gantt | `@startgantt` / `@endgantt` | dated tasks and dependencies |

Consult local PlantUML help or official syntax documentation only when a feature
is version-specific. Keep the generated source compatible with the renderer
actually used for the task.
