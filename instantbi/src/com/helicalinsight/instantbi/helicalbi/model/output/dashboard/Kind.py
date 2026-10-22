"""Public dashboard tile kinds emitted on dashboard_model."""
from __future__ import annotations

from enum import StrEnum

# Widget kinds used while laying out the grid, mapped to the public wire value.
_WIDGET_KIND = {
    "viz": "instant",
    "summary": "text",
    "filter": "instant.filter",
}


class Kind(StrEnum):
    """Wire kind on a dashboard item.

    Layout still plans with viz, summary, and filter. Those resolve to
    instant, text, and instant.filter when the item is emitted.
    """

    INSTANT = "instant"
    INSTANT_FILTER = "instant.filter"
    TEXT = "text"
    APPLY_RESET = "apply-reset"
    KPI = "kpi"
    SVG = "svg"
    IMAGE = "image"
    SEPARATOR = "separator"

    @classmethod
    def wire(cls, kind: str) -> str:
        """Public kind string. Unknown values pass through unchanged."""
        value = str(kind or "").strip().lower()
        if not value:
            return ""
        mapped = _WIDGET_KIND.get(value)
        if mapped:
            return mapped
        try:
            return cls(value).value
        except ValueError:
            return value
