"""Structured visualization model (data shelves + chart type + display properties).

``properties`` has a few fixed fields and may carry additional unknown keys
(chart-specific options). Extra keys are preserved via ``extra="allow"``.
"""
from __future__ import annotations

from typing import Any, Optional

from pydantic import BaseModel, ConfigDict, Field, model_validator


class VizData(BaseModel):
    """Data shelf encoding: rows and columns (filters live on data_model)."""

    rows: list[str] = Field(
        default_factory=list,
        description="Row / category dimension field names.",
    )
    columns: list[str] = Field(
        default_factory=list,
        description="Column / measure field names.",
    )

    @model_validator(mode="before")
    @classmethod
    def _drop_filters(cls, data: Any) -> Any:
        if not isinstance(data, dict):
            return data
        payload = dict(data)
        payload.pop("filters", None)
        return payload


# Grid Chart combine rejects arc, doughnut, and heatmap. These marks can share
# a dual axis (hreport Marks → Combine).
_DUAL_AXIS_VIZ = {
    "bar": "Bar",
    "line": "Line",
    "area": "Area",
    "point": "Point",
    "tick": "Tick",
}


class DualAxisMeasure(BaseModel):
    """One measure on a Grid Chart dual axis.

    ``viz`` is the per-measure mark. Unsupported marks (arc, doughnut, heatmap,
    text) become Line, which is the secondary series hreport can plot beside
    a bar.
    """

    field: str = Field(description="Measure shelf name (same alias as rows/columns).")
    viz: str = Field(
        description='Grid Chart mark for this measure: "Bar", "Line", "Area", "Point", or "Tick".',
    )
    color: Optional[str] = Field(
        default=None,
        description=(
            "Solid hex color for this measure's series. Empty unless the column "
            "already has a color in properties, or the user recommended one."
        ),
    )

    @model_validator(mode="before")
    @classmethod
    def _normalize_viz(cls, data: Any) -> Any:
        if not isinstance(data, dict):
            return data
        payload = dict(data)
        raw = str(payload.get("viz") or "").strip().lower()
        payload["viz"] = _DUAL_AXIS_VIZ.get(raw, "Line")
        color = payload.get("color")
        if isinstance(color, str):
            payload["color"] = color.strip() or None
        return payload


class VizChart(BaseModel):
    """Chart type selection: HI mark category + child viz.

    ``mark`` is the parent picker name (Card, Maps, Chart, Table, Grid Table,
    Grid Chart, VF). ``viz`` is the selected child under that mark (e.g. Bar,
    Line, Arc); empty when the mark has no children.

    ``dualaxis`` is the hreport Grid Chart Combine option: two or more measures
    share one chart on separate axes. Each entry in ``measures`` sets that
    measure's mark and color.
    """

    viz: str = Field(
        description=(
            'Child visualization under ``mark``, e.g. "Bar", "Line", "Arc". '
            "Empty string when the mark has no child values (Table, Grid Table, VF)."
        ),
    )
    mark: str = Field(
        description=(
            'HI mark / visualization category, e.g. "Chart", "Card", "Maps", '
            '"Table", "Grid Table", "Grid Chart", or "VF".'
        ),
    )
    dualaxis: bool = Field(
        default=False,
        description=(
            "Plot measures on two axes. Only Grid Chart can do this "
            "(hreport Combine). Requires at least two measures."
        ),
    )
    measures: list[DualAxisMeasure] = Field(
        default_factory=list,
        description=(
            "Per-measure mark and optional color when dualaxis is true. "
            "The first measure keeps the primary viz; the others default to Line. "
            "Color is taken from properties for that column, left empty, or "
            "filled when the user recommends one."
        ),
    )


_REMOVED_PROPERTY_KEYS = frozenset({"colorGradient", "theme", "formatter"})


class VizProperties(BaseModel):
    """Display / encoding properties.

    Structural fields (labels, title, Excel-style formatting) are filled
    deterministically. Domain-specific styling (color / background) is filled
    by a focused LLM polish step.
    Extra unknown keys are preserved via ``extra="allow"``.
    """

    model_config = ConfigDict(extra="allow")

    labelX: Optional[str] = Field(
        default=None,
        description="X-axis / primary categorical label.",
    )
    labelY: Optional[str] = Field(
        default=None,
        description="Y-axis / primary measure or secondary categorical label.",
    )
    title: Optional[str] = Field(
        default=None,
        description="Chart title shown to the user.",
    )
    color: Optional[str] = Field(
        default=None,
        description="Solid hex color or color field binding.",
    )
    background: Optional[str] = Field(
        default=None,
        description="Chart background color or CSS background value.",
    )
    formatting: dict[str, str] = Field(
        default_factory=dict,
        description="Per-column Excel-style format strings (deterministic when possible).",
    )

    @model_validator(mode="before")
    @classmethod
    def _normalize_property_keys(cls, data: Any) -> Any:
        if not isinstance(data, dict):
            return data
        payload = dict(data)
        if payload.get("labelX") in (None, "") and "labelsX" in payload:
            payload["labelX"] = payload.pop("labelsX")
        else:
            payload.pop("labelsX", None)
        if payload.get("labelY") in (None, "") and "labelsY" in payload:
            payload["labelY"] = payload.pop("labelsY")
        else:
            payload.pop("labelsY", None)
        for key in _REMOVED_PROPERTY_KEYS:
            payload.pop(key, None)
        return payload


class VizPropertiesPolish(BaseModel):
    """LLM output for domain-specific property polish only (not shelves/chart)."""

    model_config = ConfigDict(extra="allow")

    color: Optional[str] = Field(
        default=None,
        description="Solid hex color fitting the domain / topic mood, or empty.",
    )
    measure_colors: Optional[dict[str, str]] = Field(
        default=None,
        description=(
            "Per-measure hex colors keyed by field name. Only when the user "
            "recommended a color for that column (especially dual-axis). Omit "
            "to leave series colors empty."
        ),
    )
    background: Optional[str] = Field(
        default=None,
        description="Background color/CSS for the chart canvas when thematically useful.",
    )
    title: Optional[str] = Field(
        default=None,
        description="Optional improved business-friendly title; omit to keep deterministic title.",
    )
    labelX: Optional[str] = Field(
        default=None,
        description="Optional improved X label; omit to keep deterministic label.",
    )
    labelY: Optional[str] = Field(
        default=None,
        description="Optional improved Y label; omit to keep deterministic label.",
    )

    @model_validator(mode="before")
    @classmethod
    def _normalize_polish_keys(cls, data: Any) -> Any:
        if not isinstance(data, dict):
            return data
        payload = dict(data)
        if payload.get("labelX") in (None, "") and "labelsX" in payload:
            payload["labelX"] = payload.pop("labelsX")
        else:
            payload.pop("labelsX", None)
        if payload.get("labelY") in (None, "") and "labelsY" in payload:
            payload["labelY"] = payload.pop("labelsY")
        else:
            payload.pop("labelsY", None)
        for key in _REMOVED_PROPERTY_KEYS:
            payload.pop(key, None)
        return payload


class VizModel(BaseModel):
    """Full visualization model: data shelves, chart type, and properties."""

    data: VizData = Field(default_factory=VizData)
    chart: VizChart
    properties: VizProperties = Field(default_factory=VizProperties)
