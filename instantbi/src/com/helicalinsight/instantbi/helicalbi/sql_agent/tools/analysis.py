"""Lookup / analysis tools (findings that are not dashboard charts)."""
from __future__ import annotations

import json
import logging
from typing import Annotated, Any, Optional

from langchain_core.tools import tool
from langgraph.prebuilt import InjectedState
from pydantic import BaseModel, Field

from helicalbi.common import app_config
from helicalbi.sql_agent.tools.context import AgentToolContext
from helicalbi.sql_agent.tools.findings import describe_query_findings

logger = logging.getLogger(__name__)

_MAX_SAMPLE_ROWS = 50

STEP_INSIGHT_PROMPT = """You are a business analyst writing Instant BI findings for a non-technical user.
This is the same style as data-insight: ground every claim in the result sample.

Context (do not repeat in technical form):
User: {username}
Step title: {title}
User question: {user_question}
SQL: {sql}
Selected domain: {domain}
Selected topics: {topics}
Result sample: {sample_data}

Task:
Write a conclusive answer and a short descriptive analysis for this investigation step.

Rules:
- answer: 1–2 sentences with the direct takeaway; wrap decisive numbers in **bold** markdown.
- analysis: 2–4 sentences on business meaning and one practical implication when supported.
  Bold the few key figures; optional 2–3 short `-` bullets if a comparison is clearer that way.
- Use only facts supported by the result sample. Do not invent values.
- Clear conversational business language. No SQL, schemas, tables, charts, or system terms.
- No headings or labels like "Answer:" or "Insight:".
- Translate field names into everyday language.

{format_instructions}
"""


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
    ) -> dict[str, Any]:
        ctx = AgentToolContext(state)
        raw = (note or "").strip() or str(state.get("query_result") or "")
        asked = str(question or state.get("current_sub_question") or "").strip()
        heading = str(title or "").strip()
        sql = str(state.get("generated_sql") or "").strip()

        # Prefer live SQL preview rows from the last execute when available.
        session = state.get("session_context") if isinstance(state.get("session_context"), dict) else {}
        last = session.get("_last_sql_state") if isinstance(session, dict) else None
        sample_data: list[Any] = []
        if isinstance(last, dict):
            preview = ctx.preview_data(last)
            if isinstance(preview, list) and preview:
                sample_data = preview[:_MAX_SAMPLE_ROWS]
        if not sample_data:
            row_cap = int(getattr(app_config, "default_sql_limit", None) or _MAX_SAMPLE_ROWS)
            sample_data = _sample_rows_from_raw(raw, limit=row_cap)

        llm_insight = _llm_step_insight(
            question=asked,
            title=heading,
            sql=sql,
            sample_data=sample_data,
            state=state,
            _invoke=_invoke,
        )
        if llm_insight is not None:
            answer = llm_insight.answer
            analysis = llm_insight.analysis
        else:
            described = describe_query_findings(raw, question=asked, title=heading)
            answer = described["answer"]
            analysis = described["analysis"]

        collected = list(state.get("collected_data") or [])
        collected.append(
            {
                "sub_question": asked or state.get("current_sub_question") or "",
                "title": heading,
                "pruned_schema": state.get("current_schema_subset") or "",
                "generated_sql": sql,
                "execution_result": str(state.get("query_result") or state.get("sql_error") or ""),
                "answer": answer,
                "analysis": analysis,
                "chat_response": {
                    "summary": {"insight": answer},
                },
                "report_model": {},
                "chat_seq_id": ctx.unique_seq(),
                "include_in_dashboard": False,
            }
        )
        return {
            "ok": True,
            "analysis": analysis,
            "answer": answer,
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
