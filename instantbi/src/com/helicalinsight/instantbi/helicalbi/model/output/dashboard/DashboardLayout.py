"""Convert-dashboard LLM contract: widgets keyed by component_id.

Wire shape mirrors Helical Insight efwdd designer parts (not full efwdd JSON):

- dashboard shell: title, header banner, theme, parameters panel, variables, layout
- per-item ``dashboard_model``: kind + grid rect + tile chrome (header) + extras
- per-item ``report_model`` (viz tiles only): data_model + viz_model
"""
from __future__ import annotations

from typing import Any, Optional

from pydantic import BaseModel, Field


class DashboardTheme(BaseModel):
    color: str = Field(default="#1677ff", description="Accent / tile-header hex color.")
    background: str = Field(default="#ffffff", description="Panel / page background hex color.")


class DashboardHeader(BaseModel):
    """Dashboard-level or tile-level chrome banner (HI gridSettingsData / gridItemConfig header)."""

    enable: bool = Field(default=True, description="Whether the header bar is shown.")
    title: str = Field(
        default="",
        description=(
            "Header title. May include Mustache-style variable placeholders "
            "such as {{travel_date}} or {{travel_type}} that resolve from dashboard variables."
        ),
    )
    backgroundColor: str = Field(
        default="#000000",
        description="Header bar background hex color (dashboard banner defaults to black).",
    )


class DashboardParameters(BaseModel):
    """Global filter / parameter drawer settings (HI designerSettings.parameters)."""

    enable: bool = Field(default=True, description="Show the parameters / filter drawer.")
    orientation: str = Field(
        default="right",
        description='Drawer side: "right", "left", or "top".',
    )
    enableApplyButton: bool = Field(default=True)
    floatingFilter: bool = Field(default=False)
    closeOnApply: bool = Field(default=False)


class DashboardWidget(BaseModel):
    """One dashboard tile. Chat viz widgets reuse an input component_id."""

    component_id: str = Field(
        default="",
        description="Python-assigned id for a chat viz, or a new id for summary/svg/filter tiles.",
    )
    kind: str = Field(
        default="viz",
        description=(
            "viz = chat chart/report; summary|text = insight / story banner; "
            "kpi = key metric card; filter = slicer; svg|image = inline SVG decoration."
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
        description=(
            "For filter tiles: component_ids of viz widgets that listen. "
            "For viz tiles: variable / filter names this report reacts to "
            "(inter-panel communication)."
        ),
    )
    text: str = Field(default="", description="Overview / insight text when kind=summary|text.")
    header_title: str = Field(
        default="",
        description=(
            "Optional tile header title override (supports {{variable}} placeholders). "
            "When empty, frontend falls back to title."
        ),
    )
    export: bool = Field(
        default=True,
        description="Whether the tile exposes export actions (viz tiles).",
    )
    default_values: list[Any] = Field(
        default_factory=list,
        description="Initial filter values when kind=filter (seeded into dashboard variables).",
    )


class DashboardPlan(BaseModel):
    """First-pass plan: template, theme, header, and the summary component."""

    templateId: str = Field(default="analytical-grid")
    theme: DashboardTheme = Field(default_factory=DashboardTheme)
    title: str = Field(default="", description="Dashboard display name.")
    header: DashboardHeader = Field(default_factory=DashboardHeader)
    parameters: DashboardParameters = Field(default_factory=DashboardParameters)
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
    title: str = Field(default="", description="Dashboard display name for the shell.")
    header: DashboardHeader = Field(
        default_factory=DashboardHeader,
        description="Dashboard-level banner (title + backgroundColor).",
    )
    parameters: DashboardParameters = Field(
        default_factory=DashboardParameters,
        description="Global filter drawer settings.",
    )
    widgets: list[DashboardWidget] = Field(
        default_factory=list,
        description=(
            "One viz widget per chat component_id, plus required summary, kpi, "
            "filter, and svg/image tiles."
        ),
    )
