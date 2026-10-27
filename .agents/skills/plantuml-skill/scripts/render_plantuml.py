#!/usr/bin/env python3
"""Render one PlantUML source file with a local backend and validate the result."""

from __future__ import annotations

import argparse
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile
import xml.etree.ElementTree as ET


PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"
SECURITY_PROFILE = "SANDBOX"
REMOTE_RESOURCE_PATTERNS = (
    re.compile(r"^\s*![^\r\n]*https?://", re.IGNORECASE | re.MULTILINE),
    re.compile(r"<img\s*:\s*https?://", re.IGNORECASE),
)


class RenderError(RuntimeError):
    """A user-actionable local render failure."""


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Render one .puml file with a host PlantUML CLI or local jar. "
            "This helper uses PlantUML's SANDBOX profile, never installs "
            "dependencies, and rejects remote resource syntax."
        )
    )
    parser.add_argument("source", type=Path, help="PlantUML source file")
    parser.add_argument(
        "--format", choices=("png", "svg"), default="png", help="output format"
    )
    parser.add_argument(
        "--output",
        type=Path,
        help="output path (defaults to the source basename with the chosen suffix)",
    )
    parser.add_argument(
        "--backend",
        choices=("auto", "cli", "jar"),
        default="auto",
        help="local renderer; auto prefers the host CLI, then a configured jar",
    )
    parser.add_argument(
        "--plantuml-command",
        default=os.environ.get("PLANTUML_COMMAND", "plantuml"),
        help="PlantUML CLI executable or path (default: plantuml)",
    )
    parser.add_argument(
        "--jar",
        type=Path,
        default=(
            Path(os.environ["PLANTUML_JAR"])
            if os.environ.get("PLANTUML_JAR")
            else None
        ),
        help="local plantuml.jar path (or set PLANTUML_JAR)",
    )
    parser.add_argument(
        "--timeout", type=int, default=120, help="per-command timeout in seconds"
    )
    return parser.parse_args()


def resolve_executable(command: str) -> str | None:
    candidate = Path(command).expanduser()
    if candidate.parent != Path("."):
        if candidate.is_file() and os.access(candidate, os.X_OK):
            return str(candidate.resolve())
        return None
    return shutil.which(command)


def select_backend(args: argparse.Namespace) -> tuple[list[str], str]:
    cli = resolve_executable(args.plantuml_command)
    java = shutil.which("java")
    jar = args.jar.expanduser().resolve() if args.jar else None
    jar_ready = bool(java and jar and jar.is_file())

    if args.backend in ("auto", "cli") and cli:
        return [cli], f"host PlantUML CLI ({cli})"
    if args.backend == "cli":
        raise RenderError(
            f"PlantUML CLI not found or not executable: {args.plantuml_command}"
        )

    if args.backend in ("auto", "jar") and jar_ready:
        return [
            java,
            f"-DPLANTUML_SECURITY_PROFILE={SECURITY_PROFILE}",
            "-jar",
            str(jar),
        ], f"local plantuml.jar ({jar})"
    if args.backend == "jar":
        if not java:
            raise RenderError("Java was not found on PATH for the jar backend")
        if not jar:
            raise RenderError("No jar path supplied; use --jar or PLANTUML_JAR")
        raise RenderError(f"PlantUML jar not found: {jar}")

    raise RenderError(
        "No local renderer is available. Install/configure one only with user "
        "authorization, or deliver the .puml source without a rendered image."
    )


def renderer_environment() -> dict[str, str]:
    environment = os.environ.copy()
    environment["PLANTUML_SECURITY_PROFILE"] = SECURITY_PROFILE
    return environment


def reject_remote_resources(source: Path) -> None:
    try:
        text = source.read_text(encoding="utf-8")
    except UnicodeDecodeError as exc:
        raise RenderError(f"PlantUML source is not valid UTF-8: {source}") from exc
    except OSError as exc:
        raise RenderError(f"Could not read PlantUML source {source}: {exc}") from exc

    for pattern in REMOTE_RESOURCE_PATTERNS:
        match = pattern.search(text)
        if match:
            line = text.count("\n", 0, match.start()) + 1
            raise RenderError(
                "Remote resource syntax is disabled for local rendering "
                f"(line {line}). Keep the diagram self-contained."
            )


def run(command: list[str], timeout: int) -> subprocess.CompletedProcess[str]:
    try:
        completed = subprocess.run(
            command,
            check=False,
            capture_output=True,
            env=renderer_environment(),
            text=True,
            timeout=timeout,
        )
    except subprocess.TimeoutExpired as exc:
        raise RenderError(
            f"Renderer timed out after {timeout}s: {' '.join(command)}"
        ) from exc
    except OSError as exc:
        raise RenderError(f"Could not execute renderer: {exc}") from exc

    if completed.returncode != 0:
        details = "\n".join(
            part.strip()
            for part in (completed.stdout, completed.stderr)
            if part.strip()
        )
        if not details:
            details = "renderer returned no diagnostic output"
        raise RenderError(
            f"Renderer exited with status {completed.returncode}.\n{details}"
        )
    return completed


def validate_output(path: Path, output_format: str) -> None:
    if not path.is_file() or path.stat().st_size == 0:
        raise RenderError(
            f"Renderer did not create a non-empty {output_format.upper()} file"
        )

    if output_format == "png":
        with path.open("rb") as stream:
            header = stream.read(24)
        if not header.startswith(PNG_SIGNATURE) or header[12:16] != b"IHDR":
            raise RenderError(
                "Rendered output does not have a valid PNG signature and IHDR chunk"
            )
        return

    try:
        root = ET.parse(path).getroot()
    except ET.ParseError as exc:
        raise RenderError(f"Rendered SVG is not well-formed XML: {exc}") from exc
    if root.tag.rsplit("}", 1)[-1].lower() != "svg":
        raise RenderError(f"Rendered XML root is not <svg>: {root.tag}")


def install_atomically(rendered: Path, output: Path) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    temporary: Path | None = None
    try:
        with tempfile.NamedTemporaryFile(
            prefix=f".{output.name}.", dir=output.parent, delete=False
        ) as stream:
            temporary = Path(stream.name)
            with rendered.open("rb") as source_stream:
                shutil.copyfileobj(source_stream, stream)
        os.replace(temporary, output)
    except OSError as exc:
        raise RenderError(
            f"Could not install rendered output at {output}: {exc}"
        ) from exc
    finally:
        if temporary and temporary.exists():
            temporary.unlink()


def main() -> int:
    args = parse_args()
    source = args.source.expanduser().resolve()
    if not source.is_file():
        raise RenderError(f"PlantUML source not found: {source}")
    if args.timeout <= 0:
        raise RenderError("--timeout must be greater than zero")
    reject_remote_resources(source)

    output = (
        args.output or source.with_suffix(f".{args.format}")
    ).expanduser().resolve()
    if output.suffix.lower() != f".{args.format}":
        raise RenderError(
            f"Output suffix {output.suffix or '<none>'} does not match "
            f"--format {args.format}"
        )

    prefix, description = select_backend(args)
    if not shutil.which("dot"):
        print(
            "Warning: Graphviz 'dot' was not found; some diagram types may not render.",
            file=sys.stderr,
        )

    run([*prefix, "-checkonly", str(source)], args.timeout)

    with tempfile.TemporaryDirectory(prefix="plantuml-render-") as directory:
        render_dir = Path(directory)
        run(
            [
                *prefix,
                f"-t{args.format}",
                "-nometadata",
                "-o",
                str(render_dir),
                str(source),
            ],
            args.timeout,
        )
        matches = sorted(
            path
            for path in render_dir.rglob("*")
            if path.is_file() and path.suffix.lower() == f".{args.format}"
        )
        if len(matches) != 1:
            raise RenderError(
                "Expected exactly one rendered artifact; keep one diagram per .puml "
                f"file. Found {len(matches)} in {render_dir}."
            )
        validate_output(matches[0], args.format)
        install_atomically(matches[0], output)

    print(f"Rendered: {source} -> {output}")
    print(f"Backend: {description}")
    print(
        "Security: PlantUML SANDBOX; source stayed local; output metadata disabled"
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except RenderError as exc:
        print(f"error: {exc}", file=sys.stderr)
        raise SystemExit(2)
