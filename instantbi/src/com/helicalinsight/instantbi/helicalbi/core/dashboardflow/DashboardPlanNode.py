"""LLM node: summary component and high-level layout plan."""
from __future__ import annotations

import json
import logging

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.model.DashboardLayoutState import DashboardLayoutState
from helicalbi.model.output.dashboard.DashboardLayout import DashboardPlan
from helicalbi.prompt.DashboardLayoutPrompt import dashboard_plan_prompt_string
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.LayoutTemplates import load_decision_table

logger = logging.getLogger(__name__)


def _fallback_summary(state: DashboardLayoutState) -> tuple[str, str]:
    texts = [str(item.get("summary") or "").strip() for item in (state.get("items") or [])]
    text = " ".join(part for part in texts if part).strip()
    if not text:
        queries = [str(item.get("user_query") or "").strip() for item in (state.get("items") or [])]
        text = next((query for query in queries if query), "Key insights")
    return "Summary", text[:400]


class DashboardPlanNode:
    def process_flow(self, state: DashboardLayoutState) -> DashboardLayoutState:
        if state.get("error") or not (state.get("items") or []):
            return state
        title, text = _fallback_summary(state)
        fallback_theme = {"color": "#1677ff", "background": "#ffffff"}
        try:
            parser = PydanticOutputParser(pydantic_object=DashboardPlan)
            prompt = PromptTemplate(
                template=dashboard_plan_prompt_string + format_instruction_string,
                input_variables=["username", "domain", "topics", "viz_types", "chat_context"],
                partial_variables={
                    "format_instructions": parser.get_format_instructions(),
                    "decision_table": load_decision_table(),
                },
            )
            plan, _ = invoke_structured(
                prompt,
                llm,
                parser,
                {
                    "username": state.get("username") or "",
                    "domain": json.dumps(state.get("domain") or [], default=str),
                    "topics": json.dumps(state.get("topics") or [], default=str),
                    "viz_types": json.dumps(state.get("viz_types") or [], default=str),
                    "chat_context": json.dumps(state.get("chat_context") or [], default=str),
                },
                state=state,
            )
            dumped = plan.model_dump() if hasattr(plan, "model_dump") else dict(plan)
            theme = dumped.get("theme") or fallback_theme
            planned_template = str(state.get("templateId") or "").strip()
            state["templateId"] = planned_template or dumped.get("templateId") or "analytical-grid"
            state["theme"] = theme if isinstance(theme, dict) else fallback_theme
            state["layout_plan"] = str(dumped.get("layout_plan") or "")
            state["summary"] = {
                "title": str(dumped.get("summary_title") or title),
                "text": str(dumped.get("summary_text") or text),
            }
        except Exception:
            logger.exception("Dashboard plan LLM failed; using chat summaries")
            state["templateId"] = state.get("templateId") or "analytical-grid"
            state["theme"] = state.get("theme") or fallback_theme
            state["layout_plan"] = "Place a summary banner at the top, then filters and KPIs, then charts."
            state["summary"] = {"title": title, "text": text}
        return state
