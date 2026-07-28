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

# G2Plot formatters often receive strings (axis ticks) or datums (labels),
# so `(v) => v.toFixed(n)` throws. Rewrite to coerce by value type first.
_UNSAFE_TOFIXED = re.compile(
    r"\(\s*(?P<var>[A-Za-z_$][\w$]*)\s*\)\s*=>\s*(?P=var)\.toFixed\s*\(\s*(?P<digits>\d+)\s*\)"
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


def _safe_tofixed_formatter(var: str, digits: str) -> str:
    return (
        f"({var}) => {{ "
        f"const _raw = ({var} != null && typeof {var} === 'object') "
        f"? ({var}.value ?? Object.values({var}).find((x) => "
        f"typeof x === 'number' || (typeof x === 'string' && x !== '' && !isNaN(Number(x))))) "
        f": {var}; "
        f"const _num = typeof _raw === 'number' ? _raw : Number(_raw); "
        f"return (typeof _num === 'number' && Number.isFinite(_num)) "
        f"? _num.toFixed({digits}) : (_raw ?? ''); "
        f"}}"
    )


def _rewrite_unsafe_tofixed(code: str) -> str:
    def repl(match: re.Match[str]) -> str:
        return _safe_tofixed_formatter(match.group("var"), match.group("digits"))

    return _UNSAFE_TOFIXED.sub(repl, code)


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

    # 4. Coerce unsafe `.toFixed` formatters (string/datum args)
    code = _rewrite_unsafe_tofixed(code)

    # 5. Merge component names from existing destructuring and JSX usage
    components = _collect_component_names(code)

    # 6. Remove duplicate/existing components destructuring lines
    code = _COMPONENTS_DESTRUCTURE.sub("", code)
    code = re.sub(r"\n{3,}", "\n\n", code)

    if components:
        comp_str = ", ".join(components)
        inject_line = f"const {{ {comp_str} }} = components;\n  "

        # 7. Inject a single destructuring line inside the function
        code = re.sub(
            r"(function\s+\w+\s*\(\)\s*\{\s*)",
            r"\1" + inject_line,
            code,
            count=1,
        )

    return code.strip()
