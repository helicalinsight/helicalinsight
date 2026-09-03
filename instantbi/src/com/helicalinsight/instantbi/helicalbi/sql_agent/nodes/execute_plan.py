"""Deterministic execution of a stored investigation plan (no ReAct planner)."""
from __future__ import annotations

import logging
from typing import Any, Dict, List

from helicalbi.sql_agent.state import AgentState
from helicalbi.sql_agent.tools.analysis import analysis_tools
from helicalbi.sql_agent.tools.context import AgentToolContext
from helicalbi.sql_agent.tools.report import report_tools
from helicalbi.sql_agent.tools.semantic import semantic_tools
from helicalbi.sql_agent.tools.sql import sql_tools

logger = logging.getLogger(__name__)


def planned_charts(state: AgentState | dict[str, Any] | None) -> List[dict[str, Any]]:
    """Chart specs from the stored plan that have a non-empty question."""
    plan = (state or {}).get("investigation_plan") or {}
    charts: List[dict[str, Any]] = []
    for chart in plan.get("charts") or []:
        if not isinstance(chart, dict):
            continue
        question = str(chart.get("question") or "").strip()
        if question:
            charts.append(chart)
    return charts


def has_planned_charts(state: AgentState | dict[str, Any] | None) -> bool:
    return bool(planned_charts(state))


def _as_name_list(value: Any) -> List[str]:
    if value is None:
        return []
    if isinstance(value, str):
        text = value.strip()
        return [text] if text else []
    if isinstance(value, list):
        names: List[str] = []
        for item in value:
            text = str(item or "").strip()
            if text:
                names.append(text)
        return names
    text = str(value).strip()
    return [text] if text else []


def _merge(state: dict[str, Any], patch: Any) -> dict[str, Any]:
    if not isinstance(patch, dict) or not patch:
        return state
    merged = dict(state)
    merged.update(patch)
    return merged


def _seed_plan_scope(work: dict[str, Any]) -> tuple[list[str], list[str]]:
    plan = work.get("investigation_plan") or {}
    topics = _as_name_list(plan.get("topics")) or _as_name_list(work.get("selected_topics"))
    domains = _as_name_list(plan.get("domain")) or _as_name_list(work.get("selected_domains"))
    if topics:
        work["selected_topics"] = topics
    if domains:
        work["selected_domains"] = domains
    return topics, domains


def _apply_viz_hint(work: dict[str, Any], chart: dict[str, Any]) -> dict[str, Any]:
    hint = str(chart.get("viz_hint") or "").strip()
    if not hint:
        return work
    work["viz_hint"] = hint
    session = work.get("session_context")
    if not isinstance(session, dict):
        return work
    last = session.get("_last_sql_state")
    if isinstance(last, dict):
        last = dict(last)
        last["viz_hint"] = hint
        last["visualization"] = hint
        session["_last_sql_state"] = last
        work["session_context"] = session
    return work


def _append_failure(work: dict[str, Any], question: str, error: str, *, title: str = "") -> dict[str, Any]:
    ctx = AgentToolContext(work)
    seq = ctx.unique_seq()
    collected = list(work.get("collected_data") or [])
    message = (error or "Chart could not be built.").strip()
    collected.append(
        {
            "sub_question": question,
            "title": title,
            "pruned_schema": work.get("current_schema_subset") or "",
            "generated_sql": work.get("generated_sql") or "",
            "execution_result": message,
            "analysis": message,
            "chat_response": {
                "error": message,
                "summary": {"insight": message},
                "data": [],
                "viz": {},
                "sql": {},
            },
            "report_model": {},
            "chat_seq_id": seq,
            "include_in_dashboard": False,
        }
    )
    work["collected_data"] = collected
    work["current_chat_seq_id"] = seq
    return work


def execute_plan_node(state: AgentState) -> Dict[str, Any]:
    """Run each planned chart: generate_sql → execute_query → build_report (one retry)."""
    work = dict(state)
    charts = planned_charts(work)
    max_charts = int(work.get("max_sub_questions") or len(charts) or 5)
    charts = charts[: max(1, max_charts)]
    plan_topics, plan_domains = _seed_plan_scope(work)
    loops = 0
    original = str(work.get("original_question") or "").strip()

    try:
        retrieved = semantic_tools.retrieve(original or (charts[0]["question"] if charts else ""), work)
        work = _merge(work, retrieved.get("state_patch") or {})
        loops += 1
    except Exception:
        logger.exception("Plan execute: semantic retrieve failed")
    if plan_topics:
        work["selected_topics"] = plan_topics
    if plan_domains:
        work["selected_domains"] = plan_domains

    logger.info("Plan execute: %s chart(s) in stored order", len(charts))
    for chart in charts:
        question = str(chart.get("question") or "").strip()
        title = str(chart.get("title") or "").strip()
        want_chart = chart.get("include_in_dashboard") is not False
        logger.info("Plan execute chart=%s question=%s", title or "untitled", question)

        generated = sql_tools.generate(question, work)
        work = _merge(work, generated.get("state_patch") or {})
        loops += 1
        if not generated.get("ok"):
            work = _append_failure(
                work,
                question,
                generated.get("error") or "SQL generation failed",
                title=title,
            )
            continue

        sql = generated.get("sql") or work.get("generated_sql") or ""
        executed = sql_tools.execute(sql, work)
        work = _merge(work, executed.get("state_patch") or {})
        loops += 1
        if not executed.get("ok"):
            generated = sql_tools.generate(question, work)
            work = _merge(work, generated.get("state_patch") or {})
            loops += 1
            if generated.get("ok"):
                sql = generated.get("sql") or work.get("generated_sql") or ""
                executed = sql_tools.execute(sql, work)
                work = _merge(work, executed.get("state_patch") or {})
                loops += 1
            if not executed.get("ok"):
                work = _append_failure(
                    work,
                    question,
                    executed.get("error") or "SQL execution failed",
                    title=title,
                )
                continue

        if not want_chart:
            analyzed = analysis_tools.analyze(
                str(work.get("query_result") or "")[:1000],
                work,
            )
            work = _merge(work, analyzed.get("state_patch") or {})
            loops += 1
            continue

        work = _apply_viz_hint(work, chart)
        built = report_tools.build(question, work)
        work = _merge(work, built.get("state_patch") or {})
        loops += 1
        if not built.get("ok"):
            work = _append_failure(
                work,
                question,
                built.get("error") or "Visualization failed",
                title=title,
            )
            continue
        collected = list(work.get("collected_data") or [])
        if collected and title:
            last = dict(collected[-1])
            last["title"] = title
            collected[-1] = last
            work["collected_data"] = collected

    return {
        "collected_data": work.get("collected_data") or [],
        "asked_questions": work.get("asked_questions") or [],
        "session_context": work.get("session_context") or {},
        "selected_topics": work.get("selected_topics") or [],
        "selected_domains": work.get("selected_domains") or [],
        "current_semantic_context": work.get("current_semantic_context"),
        "current_schema_subset": work.get("current_schema_subset"),
        "token_usage": work.get("token_usage") or {},
        "tool_loop_count": loops,
        "is_complete": True,
        "generated_sql": work.get("generated_sql"),
        "query_result": work.get("query_result"),
        "sql_error": work.get("sql_error"),
        "current_sub_question": work.get("current_sub_question"),
        "current_chat_seq_id": work.get("current_chat_seq_id"),
        "current_chat_response": work.get("current_chat_response"),
    }
