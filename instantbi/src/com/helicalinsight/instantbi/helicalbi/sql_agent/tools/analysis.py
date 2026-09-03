"""Lookup / analysis tools (findings that are not dashboard charts)."""
from __future__ import annotations

from typing import Annotated, Any

from langchain_core.tools import tool
from langgraph.prebuilt import InjectedState

from helicalbi.sql_agent.tools.context import AgentToolContext


class AnalysisTools:
    """Store identifier lookups and other non-chart findings."""

    def analyze(self, note: str, state: dict[str, Any]) -> dict[str, Any]:
        ctx = AgentToolContext(state)
        analysis = (note or "").strip() or str(state.get("query_result") or "")[:1000]
        collected = list(state.get("collected_data") or [])
        collected.append(
            {
                "sub_question": state.get("current_sub_question") or "",
                "pruned_schema": state.get("current_schema_subset") or "",
                "generated_sql": state.get("generated_sql") or "",
                "execution_result": str(state.get("query_result") or state.get("sql_error") or ""),
                "analysis": analysis,
                "chat_response": {},
                "report_model": {},
                "chat_seq_id": ctx.unique_seq(),
                "include_in_dashboard": False,
            }
        )
        return {
            "ok": True,
            "analysis": analysis,
            "state_patch": {"collected_data": collected, "sql_retry_count": 0},
        }


analysis_tools = AnalysisTools()


@tool
def analyze_result(
    note: str = "",
    *,
    state: Annotated[dict, InjectedState],
) -> str:
    """Store a lookup finding from the latest query result. Do not use for dashboard charts."""
    return AgentToolContext.dump(analysis_tools.analyze(note, state))
