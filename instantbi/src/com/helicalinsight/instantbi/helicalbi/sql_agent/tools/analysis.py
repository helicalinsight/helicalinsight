"""Lookup / analysis tools (findings that are not dashboard charts)."""
from __future__ import annotations

import json
import logging
from typing import Annotated, Any, Optional

from langchain_core.tools import tool
from langgraph.prebuilt import InjectedState
from pydantic import BaseModel, Field

from helicalbi.common import app_config
from helicalbi.sql_agent.prompt import STEP_INSIGHT_PROMPT
from helicalbi.sql_agent.tools.context import AgentToolContext
from helicalbi.sql_agent.tools.findings import describe_query_findings

logger = logging.getLogger(__name__)

_MAX_SAMPLE_ROWS = 50


def _success_insight_metadata(
    state: dict[str, Any],
    last_sql: dict[str, Any] | None = None,
) -> dict[str, Any]:
    src = last_sql if isinstance(last_sql, dict) else {}
    tables = src.get("required_tables") or state.get("required_tables") or []
    domain = src.get("domain") or state.get("domain") or state.get("selected_domains") or []
    topics = src.get("topics") or state.get("topics") or state.get("selected_topics") or []
    return {
        "required_tables": list(tables) if not isinstance(tables, str) else [tables],
        "domain": list(domain) if not isinstance(domain, str) else [domain],
        "topics": list(topics) if not isinstance(topics, str) else [topics],
    }


def sql_success_insight(
    *,
    question: str,
    sql: str,
    state: dict[str, Any],
    last_sql: dict[str, Any] | None = None,
    existing_insight: str = "",
    _invoke_llm=None,
) -> str:
    """Per-question summary.insight: same SqlSuccessPrompt route as fast mode (no result rows)."""
    for candidate in (
        existing_insight,
        str((last_sql or {}).get("output") or "") if isinstance(last_sql, dict) else "",
    ):
        text = str(candidate or "").strip()
        if text and not text.lstrip().startswith(("[", "{")):
            return text
    from helicalbi.core.flows.SqlExecutor import generate_sql_success_insight

    return generate_sql_success_insight(
        question,
        sql,
        _success_insight_metadata(state, last_sql),
        state=state,
        _invoke_llm=_invoke_llm,
    )


def apply_sql_success_insight(
    step: dict[str, Any],
    state: dict[str, Any],
    *,
    question: str,
    sql: str,
    last_sql: dict[str, Any] | None = None,
    _invoke_llm=None,
) -> dict[str, Any]:
    """Set summary.insight from SqlSuccessPrompt; leave analysis for conclusion findings."""
    chat_response = dict(step.get("chat_response") or {})
    summary = dict(chat_response.get("summary") or {})
    insight = sql_success_insight(
        question=question,
        sql=sql,
        state=state,
        last_sql=last_sql,
        existing_insight=str(summary.get("insight") or "").strip(),
        _invoke_llm=_invoke_llm,
    )
    if not insight:
        return step
    summary["insight"] = insight
    chat_response["summary"] = summary
    step["chat_response"] = chat_response
    step.pop("answer", None)
    return step



class StepInsightOutput(BaseModel):
    answer: str = Field(
        description="Conclusive 1–2 sentence answer; bold key numbers with markdown"
    )
    analysis: str = Field(
        description=(
            "2–4 sentence business explanation with bold key figures; "
            "optional short markdown bullets for comparisons"
        )
    )


def _sample_rows_from_raw(raw: Any, *, limit: int = _MAX_SAMPLE_ROWS) -> list[Any]:
    from helicalbi.sql_agent.tools.findings import _parse_rows

    rows = _parse_rows(raw)
    if rows:
        return rows[: max(1, int(limit))]
    if isinstance(raw, list):
        return raw[: max(1, int(limit))]
    text = str(raw or "").strip()
    return [text] if text else []


def _llm_step_insight(
    *,
    question: str,
    title: str,
    sql: str,
    sample_data: list[Any],
    state: dict[str, Any],
    _invoke=None,
) -> Optional[StepInsightOutput]:
    if not sample_data:
        return None
    invoke = _invoke
    if invoke is None:
        from helicalbi.sql_agent.llm import invoke_agent_model

        invoke = invoke_agent_model
    try:
        parsed = invoke(
            STEP_INSIGHT_PROMPT,
            {
                "username": str(state.get("username") or "").strip() or "User",
                "title": title or "",
                "user_question": question or "",
                "sql": sql or "",
                "domain": json.dumps(state.get("selected_domains") or [], default=str),
                "topics": json.dumps(state.get("selected_topics") or [], default=str),
                "sample_data": json.dumps(sample_data, default=str),
            },
            StepInsightOutput,
            state=state,
        )
        answer = str(parsed.answer or "").strip()
        analysis = str(parsed.analysis or "").strip()
        if not answer and not analysis:
            return None
        return StepInsightOutput(
            answer=answer or analysis,
            analysis=analysis or answer,
        )
    except Exception:
        logger.exception("Step data-insight LLM failed; using rule-based findings")
        return None


class AnalysisTools:
    """Store identifier lookups and other non-chart findings."""

    def analyze(
        self,
        note: str,
        state: dict[str, Any],
        *,
        question: str = "",
        title: str = "",
        _invoke=None,
        _invoke_llm=None,
    ) -> dict[str, Any]:
        ctx = AgentToolContext(state)
        raw = (note or "").strip() or str(state.get("query_result") or "")
        asked = str(question or state.get("current_sub_question") or "").strip()
        heading = str(title or "").strip()
        sql = str(state.get("generated_sql") or "").strip()

        # Prefer live SQL preview rows from the last execute when available.
        session = state.get("session_context") if isinstance(state.get("session_context"), dict) else {}
        last = session.get("_last_sql_state") if isinstance(session, dict) else None
        last_sql = last if isinstance(last, dict) else None
        sample_data: list[Any] = []
        if isinstance(last, dict):
            preview = ctx.preview_data(last)
            if isinstance(preview, list) and preview:
                sample_data = preview[:_MAX_SAMPLE_ROWS]
        if not sample_data:
            row_cap = int(getattr(app_config, "default_sql_limit", None) or _MAX_SAMPLE_ROWS)
            sample_data = _sample_rows_from_raw(raw, limit=row_cap)

        insight = sql_success_insight(
            question=asked,
            sql=sql,
            state=state,
            last_sql=last_sql,
            _invoke_llm=_invoke_llm,
        )
        llm_insight = _llm_step_insight(
            question=asked,
            title=heading,
            sql=sql,
            sample_data=sample_data,
            state=state,
            _invoke=_invoke,
        )
        if llm_insight is not None:
            analysis = llm_insight.analysis
            fallback_insight = llm_insight.answer
        else:
            described = describe_query_findings(raw, question=asked, title=heading)
            analysis = described["analysis"]
            fallback_insight = described["answer"]
        insight = insight or fallback_insight

        collected = list(state.get("collected_data") or [])
        collected.append(
            {
                "sub_question": asked or state.get("current_sub_question") or "",
                "title": heading,
                "pruned_schema": state.get("current_schema_subset") or "",
                "generated_sql": sql,
                "execution_result": str(state.get("query_result") or state.get("sql_error") or ""),
                "analysis": analysis,
                "chat_response": {
                    "summary": {"insight": insight},
                },
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


# description= is required: Nuitka --python-flag=no_docstrings strips __doc__.
@tool(description="Store a lookup finding from the latest query result. Do not use for dashboard charts.")
def analyze_result(
    note: str = "",
    *,
    state: Annotated[dict, InjectedState],
) -> str:
    """Store a lookup finding from the latest query result. Do not use for dashboard charts."""
    return AgentToolContext.dump(analysis_tools.analyze(note, state))
