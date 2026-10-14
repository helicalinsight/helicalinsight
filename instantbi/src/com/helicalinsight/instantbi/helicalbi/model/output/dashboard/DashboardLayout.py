"""Convert-dashboard LLM contract: widgets keyed by component_id.

Wire shape mirrors Helical Insight efwdd designer parts (not full efwdd JSON):

- dashboard shell: title, dashboard_config, layout
- per-item ``dashboard_model`` (``DashboardModel``): ``Kind`` + ``Layout`` + extras
- per-item ``report_model`` (viz tiles only): data_model + viz_model
"""
from __future__ import annotations

from typing import Any, Optional

from pydantic import BaseModel, Field

from helicalbi.model.output.dashboard.DashboardConfig import (
    DashboardConfig,
    default_dashboard_config,
)


class DashboardWidget(BaseModel):
    """One dashboard tile. Chat viz widgets reuse an input component_id."""

    component_id: str = Field(
        default="",
        description="Python-assigned id for a chat viz, or a new id for summary/svg/filter tiles.",
    )
    kind: str = Field(
        default="viz",
        description=(
            "viz or instant = chat chart/report; summary or text = HTML story banner; "
            "kpi = key metric card; filter or instant.filter = slicer; "
            "svg|image = inline SVG decoration."
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
    html: str = Field(
        default="",
        description=(
            "Widget HTML. For a story banner (kind summary or text) this must be an HTML "
            "fragment of one or more <p> elements, with optional <strong> and <ul><li>. "
            "Never plain text. For svg tiles, inline <svg>. No script, style, or event handlers."
        ),
    )
    column: str = Field(default="", description="Filter column when kind=filter.")
    table: str = Field(default="", description="Filter table when kind=filter.")
    text: str = Field(
        default="",
        description="Same HTML fragment as html when kind is summary or text. Not plain text.",
    )
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
        description="Initial filter values when kind=filter.",
    )


class DashboardPlan(BaseModel):
    """First-pass plan: template, dashboard_config, and the summary component."""

    templateId: str = Field(default="analytical-grid")
    dashboard_config: DashboardConfig = Field(
        default_factory=default_dashboard_config,
        description="Parent chrome: theme + parameters drawer settings.",
    )
    title: str = Field(default="", description="Dashboard display name.")
    summary_title: str = Field(default="Summary")
    summary_text: str = Field(
        default="",
        description=(
            "Story banner as an HTML fragment, not plain text. Use one or more <p> elements "
            "that say what the dashboard shows and how to read it. Optional <strong> and a "
            "short <ul> of <li> items. No <html>, <body>, <script>, <style>, or event handlers."
        ),
    )
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
    dashboard_config: DashboardConfig = Field(
        default_factory=default_dashboard_config,
        description="Parent chrome: theme + parameters drawer settings.",
    )
    title: str = Field(default="", description="Dashboard display name for the shell.")
    widgets: list[DashboardWidget] = Field(
        default_factory=list,
        description=(
            "One viz widget per chat component_id, plus required summary, kpi, "
            "filter, and svg/image tiles."
        ),
    )
