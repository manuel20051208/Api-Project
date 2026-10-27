# Render PlantUML embedded in Markdown

Use this playbook when a Markdown document contains PlantUML and the user wants
rendered images for a target that does not support PlantUML source directly.

## Discover the inputs

Inspect the whole document for:

1. fenced `plantuml` or `puml` code blocks;
2. links or image references whose target ends in `.puml`;
3. existing generated image links and repository naming conventions.

Count matches before editing. Resolve every linked source relative to the
Markdown file, not the current shell directory.

## Extract and render

1. Derive a stable basename from the document and nearest heading or alt text,
   for example `images/readme-login-flow.svg`.
2. Disambiguate repeated headings deterministically with a short numeric suffix.
3. Save each fenced block as an editable `.puml` beside the images or in the
   repository's existing diagram-source directory.
4. Render every source through the main local render workflow. Use SVG for web
   documentation and PNG when the publishing target requires it.
5. Validate every artifact before changing the Markdown. If the task requires
   all diagrams and any render fails, preserve the original document and report
   the failure rather than leaving a partially rewritten page.

## Rewrite safely

- Replace a fenced block with a relative image link whose alt text describes the
  diagram, or keep the source in a collapsed `<details>` block when editability
  in the document is useful.
- Repoint `.puml` image links to the rendered file while keeping the `.puml`
  source on disk.
- Preserve surrounding whitespace, headings, captions, anchors, and unrelated
  user changes.
- Re-run the workflow without creating duplicate files or changing stable names.
- Resolve the final image links from the Markdown file's directory and verify
  that every target exists.

Example that keeps source visible to maintainers:

````markdown
![Login request sequence](images/readme-login-request.svg)

<details>
<summary>PlantUML source</summary>

```plantuml
...original source...
```

</details>
````

## Hand off

Report the number found, rendered, and rewritten; list any unchanged or failed
blocks; and name the backend and source-boundary behavior once for the batch.
