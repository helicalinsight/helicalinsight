"""Convert-dashboard LLM contract: widgets keyed by component_id."""
from __future__ import annotations

from typing import Optional

from pydantic import BaseModel, Field


class DashboardTheme(BaseModel):
    color: str = Field(default="#1677ff", description="Accent / header hex color.")
    background: str = Field(default="#ffffff", description="Panel background hex color.")


class DashboardWidget(BaseModel):
    """One dashboard tile. Chat viz widgets reuse an input component_id."""

    component_id: str = Field(
        default="",
        description="Python-assigned id for a chat viz, or a new id for summary/svg/filter tiles.",
    )
    kind: str = Field(
        default="viz",
        description=(
            "viz = chat chart; summary = insight banner; kpi = key metric card; "
            "filter = slicer; svg or image = inline SVG decoration."
        ),
    )
    title: str = Field(default="", description="Skeleton tile title shown in the designer.")
    x: int = Field(default=0)
    y: int = Field(default=0)
    w: int = Field(default=6, description="Grid width in columns.")
    h: int = Field(default=4, description="Grid height in rows.")
    width: Optional[int] = Field(
        default=None,
        description="Optional LLM alias for w; not emitted on output layout.",
    )
    height: Optional[int] = Field(
        default=None,
        description="Optional LLM alias for h; not emitted on output layout.",
    )
    css: str = Field(default="", description="Optional widget css section.")
    js: str = Field(default="", description="Optional widget javascript section.")
    html: str = Field(default="", description="Optional widget html section or inline SVG.")
    column: str = Field(default="", description="Filter column when kind=filter.")
    table: str = Field(default="", description="Filter table when kind=filter.")
    listeners: list[str] = Field(
        default_factory=list,
        description="component_ids of viz widgets that listen to this filter.",
    )
    text: str = Field(default="", description="Overview / insight text when kind=summary.")


class DashboardPlan(BaseModel):
    """First-pass plan: template, theme, and the summary component."""

    templateId: str = Field(default="analytical-grid")
    theme: DashboardTheme = Field(default_factory=DashboardTheme)
    summary_title: str = Field(default="Summary")
    summary_text: str = Field(default="")
    layout_plan: str = Field(
        default="",
        description="Short placement plan for charts, KPIs, and filters on the 12-column grid.",
    )


class DashboardLayoutDecision(BaseModel):
    """Structured LLM output. Frontend assembles designer JSON from items + dashboard_model."""

    templateId: str = Field(
        default="",
        description=(
            "Chosen layout template id from the decision table: "
            "executive-kpi-first, analytical-grid, storytelling-narrative, "
            "dashboard-sidebar, mosaic-freeform, comparison, "
            "drilldown-hierarchical, operational-realtime."
        ),
    )
    theme: DashboardTheme = Field(default_factory=DashboardTheme)
    widgets: list[DashboardWidget] = Field(
        default_factory=list,
        description=(
            "One viz widget per chat component_id, plus required summary, kpi, "
            "filter, and svg/image tiles."
        ),
    )
