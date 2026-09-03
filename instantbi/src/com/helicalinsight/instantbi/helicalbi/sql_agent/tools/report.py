"""Visualization / report_model tools."""
from __future__ import annotations

from typing import Annotated, Any

from langchain_core.tools import tool
from langgraph.prebuilt import InjectedState

from helicalbi.sql_agent.tools.context import AgentToolContext


class ReportTools:
    """Build InstantBI report_model charts from executed SQL."""

    def build(self, question: str, state: dict[str, Any]) -> dict[str, Any]:
        from helicalbi.model.output.ChatResponse import ChatResponse
        from helicalbi.sql_agent.instantbi_turn import build_viz_for_state

        ctx = AgentToolContext(state)
        session = ctx.session
        max_charts = int(state.get("max_sub_questions") or 5)
        if ctx.chart_count() >= max_charts:
            return {
                "ok": False,
                "error": f"Dashboard already has {max_charts} charts",
                "state_patch": {},
            }
        last = session.get("_last_sql_state")
        if not isinstance(last, dict) or not (last.get("sql") or state.get("generated_sql")):
            return {
                "ok": False,
                "error": "No executed SQL to visualize. Call execute_query first.",
                "state_patch": {},
            }
        last_error = ctx.sql_error(last)
        if last_error:
            return {
                "ok": False,
                "error": last_error,
                "state_patch": {},
            }

        seq = ctx.unique_seq()
        work = dict(last)
        work["query"] = question or state.get("current_sub_question") or work.get("query")
        work["viz_query"] = work["query"]
        if state.get("viz_hint") and not work.get("viz_hint"):
            work["viz_hint"] = state.get("viz_hint")
        result = build_viz_for_state(
            work,
            session,
            thread_id=ctx.thread_id,
            chat_seq_id=seq,
            request_id=ctx.request_id,
            agent_mode=str(state.get("agent_mode") or "") or None,
        )
        session["_last_sql_state"] = result
        chat_response = result.get("_chat_response") or ChatResponse.from_model_state(result).to_dict()
        report_model = chat_response.get("report_model") or {}
        viz_model = report_model.get("viz_model") if isinstance(report_model, dict) else None
        chart_name = str((chat_response.get("viz") or {}).get("chart_name") or "").strip()
        has_viz = bool(isinstance(viz_model, dict) and viz_model) or bool(chart_name)
        insight = str((chat_response.get("summary") or {}).get("insight") or "").strip()
        preview = ctx.preview_data(result)
        analysis = insight or str(state.get("query_result") or preview or "")[:800]
        collected = list(state.get("collected_data") or [])
        collected.append(
            {
                "sub_question": question or state.get("current_sub_question") or "",
                "pruned_schema": state.get("current_schema_subset") or "",
                "generated_sql": state.get("generated_sql") or last.get("sql") or "",
                "execution_result": str(state.get("query_result") or preview or ""),
                "analysis": analysis,
                "chat_response": chat_response,
                "report_model": report_model,
                "chat_seq_id": seq,
                "include_in_dashboard": has_viz,
            }
        )
        return {
            "ok": True,
            "report_model": chat_response.get("report_model") or {},
            "chart": (chat_response.get("viz") or {}).get("chart_name") or "",
            "state_patch": {
                "current_sub_question": question or state.get("current_sub_question"),
                "current_chat_response": chat_response,
                "current_chat_seq_id": seq,
                "collected_data": collected,
                "session_context": session,
                "token_usage": ctx.merge_usage(result),
                "sql_retry_count": 0,
            },
        }


report_tools = ReportTools()


@tool
def build_report(
    question: str,
    *,
    state: Annotated[dict, InjectedState],
) -> str:
    """Build InstantBI report_model (data_model + viz_model) from the latest executed SQL. Dashboard chart only."""
    return AgentToolContext.dump(report_tools.build(question, state))
