from typing import List

from pydantic import BaseModel, Field


class SynthesizerOutput(BaseModel):
    final_answer: str = Field(description="User-facing answer to the original question.")


class DashboardChartSpec(BaseModel):
    """One planned chart or lookup the execute phase should build."""

    level: str = Field(
        default="middle",
        description="Layout band: top, middle, bottom, primary, supporting, or detail.",
    )
    title: str = Field(description="Short tile title for the dashboard.")
    question: str = Field(description="Focused sub-question for generate_sql / build_report.")
    viz_hint: str = Field(
        default="bar",
        description="Suggested viz: kpi, line, bar, table, map, heatmap, pie, or scatter.",
    )
    purpose: str = Field(default="", description="Why this chart answers the user question.")
    context_anchor: str = Field(
        default="",
        description="Target, benchmark, or period comparison to show beside the value.",
    )
    include_in_dashboard: bool = Field(default=True)


class InvestigationPlan(BaseModel):
    """Persona-aware dashboard investigation plan produced before SQL/charts run."""

    persona: str = Field(description="executive, operational_manager, tactical_manager, or analyst.")
    tier: str = Field(
        default="tactical",
        description="Dashboard cadence: strategic, operational, tactical, or analytical.",
    )
    strategies: List[str] = Field(
        default_factory=list,
        description="Consulting techniques applied. Execute uses the first (selected) strategy.",
    )
    strategy_id: str = Field(
        default="",
        description="Decision-tree selected strategy id (inverted_pyramid, progressive_disclosure, ...).",
    )
    domain: str = Field(default="", description="Business domain inferred from the semantic model.")
    topics: List[str] = Field(default_factory=list, description="Semantic-model topics this plan covers.")
    original_question: str = Field(default="", description="User question this plan answers.")
    rationale: str = Field(default="", description="Why this persona, strategy, and chart set fit.")
    design_application: str = Field(
        default="",
        description="How the layout should look (KPI band, drill-down, MECE cards, alerts, ...).",
    )
    charts: List[DashboardChartSpec] = Field(default_factory=list)
    template_id: str = Field(
        default="",
        description="Layout template name from the strategy catalog (e.g. analytical-grid).",
    )
    layout_guidance: str = Field(default="", description="Grid placement guidance for the layout step.")
    color_guidance: str = Field(
        default="",
        description="Semantic color rules (neutral vs amber/red alerts), if any.",
    )
