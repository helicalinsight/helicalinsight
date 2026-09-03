"""LLM node that places viz, summary, filter, KPI, and svg widgets on the grid."""
from __future__ import annotations

import json
import logging

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.core.dashboardflow.collect_items import item_cards
from helicalbi.core.dashboardflow.grid_clamp import default_layout
from helicalbi.model.DashboardLayoutState import DashboardLayoutState
from helicalbi.model.output.dashboard.DashboardLayout import DashboardLayoutDecision
from helicalbi.prompt.DashboardLayoutPrompt import dashboard_layout_prompt_string
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.LayoutTemplates import load_decision_table, load_layout_catalog

logger = logging.getLogger(__name__)


def _merge_planned_widgets(state: DashboardLayoutState, widgets: list[dict]) -> list[dict]:
    merged = [row for row in widgets if isinstance(row, dict)]
    kinds = {str(row.get("kind") or "").lower() for row in merged}
    summary = state.get("summary") if isinstance(state.get("summary"), dict) else {}
    if "summary" not in kinds and (summary.get("text") or summary.get("title")):
        merged.append(
            {
                "kind": "summary",
                "title": summary.get("title") or "Summary",
                "text": summary.get("text") or "",
                "html": summary.get("text") or "",
                "x": 0,
                "y": 0,
                "w": 12,
                "h": 1,
            }
        )
    if "filter" not in kinds:
        merged.extend(state.get("filter_components") or [])
    return merged


class DashboardLayoutNode:
    def process_flow(self, state: DashboardLayoutState) -> DashboardLayoutState:
        items = state.get("items") or []
        if state.get("error") or not items:
            return state

        fallback = default_layout(items)
        try:
            parser = PydanticOutputParser(pydantic_object=DashboardLayoutDecision)
            prompt = PromptTemplate(
                template=dashboard_layout_prompt_string + format_instruction_string,
                input_variables=["item_cards", "username", "layout_plan", "filter_components"],
                partial_variables={
                    "format_instructions": parser.get_format_instructions(),
                    "decision_table": load_decision_table(),
                    "layout_catalog": load_layout_catalog(),
                },
            )
            plan = {
                "templateId": state.get("templateId") or "",
                "theme": state.get("theme") or {},
                "summary": state.get("summary") or {},
                "layout_plan": state.get("layout_plan") or "",
            }
            decision, _ = invoke_structured(
                prompt,
                llm,
                parser,
                {
                    "item_cards": json.dumps(
                        item_cards(
                            items,
                            domain=state.get("domain"),
                            topics=state.get("topics"),
                        ),
                        default=str,
                    ),
                    "username": state.get("username") or "",
                    "layout_plan": json.dumps(plan, default=str),
                    "filter_components": json.dumps(state.get("filter_components") or [], default=str),
                },
                state=state,
            )
            dumped = decision.model_dump() if hasattr(decision, "model_dump") else dict(decision)
            state["templateId"] = dumped.get("templateId") or state.get("templateId") or ""
            state["theme"] = dumped.get("theme") or state.get("theme") or fallback["theme"]
            state["widgets"] = _merge_planned_widgets(state, dumped.get("widgets") or fallback["widgets"])
        except Exception:
            logger.exception("Dashboard layout LLM failed; using deterministic layout")
            state["templateId"] = state.get("templateId") or "executive-kpi-first"
            state["theme"] = state.get("theme") or fallback["theme"]
            state["widgets"] = _merge_planned_widgets(state, fallback["widgets"])
        return state
