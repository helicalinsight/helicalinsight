"""Structured visualization model (data shelves + chart type + display properties).

``properties`` has a few fixed fields and may carry additional unknown keys
(chart-specific options). Extra keys are preserved via ``extra="allow"``.
"""
from __future__ import annotations

from typing import Any, Optional

from pydantic import BaseModel, ConfigDict, Field, model_validator


class VizFilter(BaseModel):
    """A single filter binding on the visualization data shelf."""

    name: str = Field(default="", description="Filter field / column name.")
    value: Any = Field(default="", description="Filter value (string, number, list, etc.).")
    condition: str = Field(
        default="",
        description="Filter operator / condition (e.g. equals, in, between).",
    )


class VizData(BaseModel):
    """Data shelf encoding: rows, columns, and filters."""

    rows: list[str] = Field(
        default_factory=list,
        description="Row / category dimension field names.",
    )
    columns: list[str] = Field(
        default_factory=list,
        description="Column / measure field names.",
    )
    filters: list[VizFilter] = Field(
        default_factory=list,
        description="Active filters applied to the visualization data.",
    )


class VizChart(BaseModel):
    """Chart type selection: HI mark category + child viz.

    ``mark`` is the parent picker name (Card, Maps, Chart, Grid Chart,
    Table, Grid Table, VF). ``viz`` is the selected child under that mark
    (e.g. Bar, Line, Arc); empty when the mark has no children.
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
            '"Grid Chart", "Table", "Grid Table", or "VF".'
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
