"""Structured visualization model (data shelves + chart type + display properties).

``properties`` has a few fixed fields and may carry additional unknown keys
(chart-specific options). Extra keys are preserved via ``extra="allow"``.
"""
from __future__ import annotations

from typing import Any, Optional

from pydantic import BaseModel, ConfigDict, Field


class VizFilter(BaseModel):
    """A single filter binding on the visualization data shelf."""

    name: str = Field(default="", description="Filter field / column name.")
    value: Any = Field(default="", description="Filter value (string, number, list, etc.).")
    condition: str = Field(
        default="",
        description="Filter operator / condition (e.g. equals, in, between).",
    )


class VizData(BaseModel):
    """Data shelf encoding: rows, columns, filters, and hidden fields."""

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
    hidden: list[str] = Field(
        default_factory=list,
        description="Field names present in the query but hidden from the viz.",
    )


class VizChart(BaseModel):
    """Chart type selection (high-level viz family + mark geometry)."""

    viz: str = Field(
        description='Visualization family, e.g. "Bar" or "arc".',
    )
    mark: str = Field(
        description='Mark geometry, e.g. "bar" or "pie".',
    )


class VizProperties(BaseModel):
    """Display / encoding properties.

    Structural fields (labels, title, Excel-style formatting) are filled
    deterministically. Domain-specific styling (color/gradient/theme/background)
    and custom JS formatters are filled by a focused LLM polish step.
    Extra unknown keys are preserved via ``extra="allow"``.
    """

    model_config = ConfigDict(extra="allow")

    labelsX: Optional[str] = Field(
        default=None,
        description="X-axis / primary categorical label.",
    )
    labelsY: Optional[str] = Field(
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
    colorGradient: Optional[list[str]] = Field(
        default=None,
        description="Ordered hex palette / gradient stops for domain-themed coloring.",
    )
    theme: Optional[str] = Field(
        default=None,
        description="Visual theme name or token (e.g. finance-dark, retail-warm).",
    )
    background: Optional[str] = Field(
        default=None,
        description="Chart background color or CSS background value.",
    )
    formatting: dict[str, str] = Field(
        default_factory=dict,
        description="Per-column Excel-style format strings (deterministic when possible).",
    )
    formatter: dict[str, str] = Field(
        default_factory=dict,
        description=(
            "Per-column custom JS formatter function bodies when Excel-style "
            "formatting is not enough (e.g. domain units, conditional labels)."
        ),
    )


class VizPropertiesPolish(BaseModel):
    """LLM output for domain-specific property polish only (not shelves/chart)."""

    model_config = ConfigDict(extra="allow")

    color: Optional[str] = Field(
        default=None,
        description="Solid hex color fitting the domain / topic mood, or empty.",
    )
    colorGradient: Optional[list[str]] = Field(
        default=None,
        description="2+ hex colors forming a domain-appropriate gradient / palette.",
    )
    theme: Optional[str] = Field(
        default=None,
        description="Short theme token for the domain (e.g. travel-cool, sales-bold).",
    )
    background: Optional[str] = Field(
        default=None,
        description="Background color/CSS for the chart canvas when thematically useful.",
    )
    title: Optional[str] = Field(
        default=None,
        description="Optional improved business-friendly title; omit to keep deterministic title.",
    )
    labelsX: Optional[str] = Field(
        default=None,
        description="Optional improved X label; omit to keep deterministic label.",
    )
    labelsY: Optional[str] = Field(
        default=None,
        description="Optional improved Y label; omit to keep deterministic label.",
    )
    formatter: dict[str, str] = Field(
        default_factory=dict,
        description=(
            "Map of result column name → JavaScript function body string "
            "(args: value, datum). Only for columns that need custom logic "
            "beyond Excel-style format strings. Example: "
            "'return value == null ? \"—\" : value.toFixed(1) + \" km\";'"
        ),
    )


class VizModel(BaseModel):
    """Full visualization model: data shelves, chart type, and properties."""

    data: VizData = Field(default_factory=VizData)
    chart: VizChart
    properties: VizProperties = Field(default_factory=VizProperties)
