"""Pure helpers for transforming LLM-generated chart JS/JSX code."""

from __future__ import annotations

import re

_COMPONENTS_DESTRUCTURE = re.compile(
    r"^\s*const\s*\{\s*([^}]+)\s*\}\s*=\s*components\s*;\s*$",
    re.MULTILINE,
)
_JSX_COMPONENT = re.compile(r"<\s*([A-Z][A-Za-z0-9_]*)\b")

# G2Plot Heatmap gradient expects a color array; { type: "sequential" } crashes
# with "Cannot read properties of undefined (reading '0')".
_INVALID_SEQUENTIAL_COLOR = re.compile(
    r"color\s*:\s*\{\s*type\s*:\s*['\"]sequential['\"]\s*\}",
    re.IGNORECASE,
)
_DEFAULT_SEQUENTIAL_COLORS = (
    '["#B8E1FF", "#9AC5FF", "#7DAAFF", "#5B8FF9", "#3D76DD", '
    '"#085EC0", "#0047A5", "#00318A", "#001D70"]'
)


def _parse_component_names(binding: str) -> list[str]:
    return [name.strip() for name in binding.split(",") if name.strip()]


def _collect_component_names(code: str) -> list[str]:
    names: list[str] = []
    seen: set[str] = set()

    for match in _COMPONENTS_DESTRUCTURE.finditer(code):
        for name in _parse_component_names(match.group(1)):
            if name not in seen:
                seen.add(name)
                names.append(name)

    for name in _JSX_COMPONENT.findall(code):
        if name not in seen:
            seen.add(name)
            names.append(name)

    return names


def transform_chart_code(code: str) -> str:
    # 1. Remove all import statements
    code = re.sub(r"import\s+.*?;\s*", "", code)

    # 2. Remove export statements (e.g. export default DrawChart;)
    code = re.sub(r"^\s*export\s+default\s+\w+\s*;?\s*$", "", code, flags=re.MULTILINE)
    code = re.sub(r"^\s*export\s+\{[^}]+\}\s*;?\s*$", "", code, flags=re.MULTILINE)

    # 3. Fix invalid sequential color object used by Heatmap
    code = _INVALID_SEQUENTIAL_COLOR.sub(
        f"color: {_DEFAULT_SEQUENTIAL_COLORS}",
        code,
    )

    # 4. Merge component names from existing destructuring and JSX usage
    components = _collect_component_names(code)

    # 5. Remove duplicate/existing components destructuring lines
    code = _COMPONENTS_DESTRUCTURE.sub("", code)
    code = re.sub(r"\n{3,}", "\n\n", code)

    if components:
        comp_str = ", ".join(components)
        inject_line = f"const {{ {comp_str} }} = components;\n  "

        # 6. Inject a single destructuring line inside the function
        code = re.sub(
            r"(function\s+\w+\s*\(\)\s*\{\s*)",
            r"\1" + inject_line,
            code,
            count=1,
        )

    return code.strip()
