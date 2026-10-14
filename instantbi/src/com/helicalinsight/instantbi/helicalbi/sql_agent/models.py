from typing import Any, Dict, List

from pydantic import BaseModel, Field, model_validator


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
    topic: str = Field(
        default="",
        description="Semantic-model topic this chart is grounded in.",
    )
    components: List[str] = Field(
        default_factory=list,
        description="Dimension/measure names from the topic this chart should use.",
    )
    measure_hints: List[str] = Field(
        default_factory=list,
        description="Preferred measure or calculated-metric names from the grounding pack.",
    )
    operation: str = Field(
        default="",
        description="Analytical operation from analytical_strategy (e.g. trend, period_comparison, segment, contribution, mix_analysis, outlier_detection).",
    )
    analytical_strategy: str = Field(
        default="",
        description="Analytical strategy under which this query was framed (e.g. trend, period_comparison, segment, contribution, mix_analysis).",
    )

    @model_validator(mode="after")
    def sync_analytical_strategy(self) -> "DashboardChartSpec":
        op = str(self.operation or self.analytical_strategy or "").strip()
        if not op:
            text = f" {self.question} {self.title} {self.context_anchor} ".lower()
            if any(k in text for k in ("trend", "over time", "monthly", "yearly", "quarterly", "daily", "timeline")) or self.viz_hint == "line":
                op = "trend"
            elif any(k in text for k in ("compare", "versus", " vs ", "prior", "last year", "last quarter", "mom", "yoy", "qoq")):
                op = "period_comparison"
            elif any(k in text for k in ("target", "benchmark", "budget", "actual vs", "baseline", "sla")):
                op = "benchmark_comparison"
            elif any(k in text for k in ("why", "driver", "contribut", "impact", "factor")):
                op = "contribution"
            elif any(k in text for k in ("outlier", "anomaly", "abnormal", "spike")):
                op = "outlier_detection"
            elif any(k in text for k in ("rank", "top", "bottom", "highest", "lowest", "laggard")):
                op = "ranking"
            elif any(k in text for k in ("mix", "composition")):
                op = "mix_analysis"
            elif any(k in text for k in ("distribut", "spread", "histogram")):
                op = "distribution"
            elif any(k in text for k in ("reason", "cancellation", "failure", "defect")):
                op = "reason_analysis"
            elif self.viz_hint == "kpi":
                op = "period_comparison" if self.context_anchor else "trend"
            else:
                op = "segment"
        self.operation = op
        self.analytical_strategy = op
        return self


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
    analytical_operations: List[str] = Field(
        default_factory=list,
        description="Analytical operations selected from analytical_strategy for this plan.",
    )
    query_analytical_strategies: List[Dict[str, Any]] = Field(
        default_factory=list,
        description="Explicit mapping of which query is framed under what analytical strategy.",
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

    @model_validator(mode="after")
    def sync_plan_analytical_strategies(self) -> "InvestigationPlan":
        if not self.query_analytical_strategies and self.charts:
            self.query_analytical_strategies = [
                {
                    "question": str(chart.question or "").strip(),
                    "analytical_strategy": str(chart.analytical_strategy or chart.operation or "").strip(),
                }
                for chart in self.charts
                if str(chart.question or "").strip()
            ]
        if not self.analytical_operations and self.charts:
            seen: set[str] = set()
            ops: list[str] = []
            for chart in self.charts:
                strategy = str(chart.analytical_strategy or chart.operation or "").strip()
                if strategy and strategy not in seen:
                    seen.add(strategy)
                    ops.append(strategy)
            self.analytical_operations = ops
        return self

