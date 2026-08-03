"""Per-chart settings templates declared in ``viz/charts/*.json``.

The ``settings`` block is a *prompt template* passed to the LLM. The LLM fills
the same shape; that filled object is injected into chart ``code`` at
``${setting}`` and referenced via ``setting.*`` attributes.
"""
from __future__ import annotations

import json
from typing import Any, Optional


def synthesize_settings_template(payload: dict) -> dict[str, Any]:
    """Build a default settings template from dims/measures cardinality."""
    dims_min = int(payload.get("dims_min") or 0)
    dims_max = payload.get("dims_max")
    measures_min = int(payload.get("measures_min") or 0)
    measures_max = payload.get("measures_max")

    conversion = payload.get("conversion") if isinstance(payload.get("conversion"), dict) else {}
    family = str((conversion or {}).get("family") or "").strip().lower()

    if dims_min == 0 and (dims_max == 0 or dims_max is None) and family == "kpi":
        dimensions: Any = {
            "names": "optional dimension names as JSON array (usually unused for KPI)"
        }
    else:
        max_txt = "any" if dims_max is None else str(dims_max)
        dimensions = {
            "names": (
                f"provide dimension names as JSON array, "
                f"min {dims_min} max {max_txt}"
            )
        }

    max_m = "any" if measures_max is None else str(measures_max)
    measures = (
        f"provide measure names separated by comma, "
        f"min {measures_min} max {max_m}"
    )

    template: dict[str, Any] = {
        "dimensions": dimensions,
        "measures": [measures],
        "labelsX": "Label for x field",
        "labelsY": "Label for y field",
        # Plain string — avoid nested schema objects the LLM may echo back.
        "color": (
            "optional solid hex string (e.g. '#5B8FF9') "
            "or palette array of hex colors"
        ),
    }

    if family in {"pie", "kpi", "percent", "hierarchy", "tiny"}:
        template.pop("labelsX", None)
        template.pop("labelsY", None)
        if family != "kpi":
            template["title"] = "Chart title"
        else:
            template["title"] = "KPI title"
    elif family == "bubble":
        template["labelsZ"] = "Label for size / z field"
    elif family == "dual_axes":
        template["labelsZ"] = "Label for secondary measure"
    elif family == "heatmap":
        template["dimensions"] = {
            "names": "provide dimension names as JSON array, min 2 max 2"
        }
        template["color"] = (
            "required sequential hex palette array for the heat scale"
        )

    # Optional series when conversion declares a series role.
    fields = (conversion or {}).get("fields") or {}
    if isinstance(fields, dict):
        for placeholder, spec in fields.items():
            if not isinstance(spec, dict):
                continue
            role = str(spec.get("role") or "").strip().lower()
            key = str(placeholder).casefold()
            if role == "series" or "series" in key:
                template["series"] = "optional series / group / stack field name"
                break

    return template


def settings_template_from_payload(payload: dict) -> dict[str, Any]:
    """Return the chart's settings template (LLM fill shape).

    Prefer the explicit ``settings`` block; otherwise synthesize from cardinality.
    """
    raw = payload.get("settings")
    if isinstance(raw, dict) and raw:
        return dict(raw)
    return synthesize_settings_template(payload)


def settings_template_to_prompt(template: dict[str, Any]) -> str:
    return json.dumps(_prompt_safe_settings_template(template), indent=2, ensure_ascii=False)


def _prompt_safe_settings_template(template: dict[str, Any]) -> dict[str, Any]:
    """Flatten nested schema hints (e.g. color.mode/optional) into plain fill hints."""
    out = dict(template)
    color = out.get("color")
    if isinstance(color, dict):
        desc = str(color.get("description") or "").strip()
        mode = str(color.get("mode") or "").strip().lower()
        optional = bool(color.get("optional"))
        if not desc:
            desc = (
                "sequential hex palette array"
                if mode == "palette"
                else "solid hex string or palette array of hex colors"
            )
        prefix = "optional " if optional or mode == "optional" else ""
        if mode == "palette" and not optional:
            prefix = "required "
        out["color"] = f"{prefix}{desc}".strip()
    return out
