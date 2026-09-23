"""Deterministic execution of a stored investigation plan (no ReAct planner)."""
from __future__ import annotations

import logging
from typing import Any, Dict, List

from helicalbi.sql_agent.activity_details import append_trace, trace_entry
from helicalbi.sql_agent.state import AgentState
from helicalbi.sql_agent.tools.analysis import analysis_tools
from helicalbi.sql_agent.tools.context import AgentToolContext
from helicalbi.sql_agent.tools.report import report_tools
from helicalbi.sql_agent.tools.semantic import semantic_tools
from helicalbi.sql_agent.tools.sql import sql_tools
from helicalbi.sql.sql_retry import SQL_RETRY_MAX, rewrite_failed_sql_prompt

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


def _rewrite_prompt(question: str, sql: str, error: str) -> str:
    """Feed prior SQL + engine error into the next generation attempt."""
    return rewrite_failed_sql_prompt(question, sql, error)


def execute_plan_node(state: AgentState) -> Dict[str, Any]:
    """Run each planned chart: generate_sql → execute_query → build_report.

    On execute failure, rewrite SQL from the error in an agentic retry loop.
    """
    work = dict(state)
    charts = planned_charts(work)
    max_charts = int(work.get("max_sub_questions") or len(charts) or 5)
    charts = charts[: max(1, max_charts)]
    plan_topics, plan_domains = _seed_plan_scope(work)
    loops = 0
    original = str(work.get("original_question") or "").strip()

    activity_trace = list(work.get("activity_trace") or [])
    chart_traces: List[Dict[str, Any]] = []
    try:
        retrieved = semantic_tools.retrieve(original or (charts[0]["question"] if charts else ""), work)
        work = _merge(work, retrieved.get("state_patch") or {})
        loops += 1
        activity_trace = append_trace(
            activity_trace,
            trace_entry(
                "semantic_retrieve",
                stage="execute_graph",
                result={
                    "topics": list(work.get("selected_topics") or []),
                    "domains": list(work.get("selected_domains") or []),
                },
                reason="Retrieved semantic scope for planned chart execution.",
            ),
        )
    except Exception:
        logger.exception("Plan execute: semantic retrieve failed")
        activity_trace = append_trace(
            activity_trace,
            trace_entry(
                "semantic_retrieve",
                stage="execute_graph",
                status="error",
                reason="Semantic retrieve failed; continuing with plan scope.",
            ),
        )
    if plan_topics:
        work["selected_topics"] = plan_topics
    if plan_domains:
        work["selected_domains"] = plan_domains

    logger.info("Plan execute: %s chart(s) in stored order", len(charts))
    session = work.get("session_context") if isinstance(work.get("session_context"), dict) else {}
    progress_cb = session.get("_on_progress") if isinstance(session, dict) else None

    def _progress(stage: str, status: str, message: str) -> None:
        if callable(progress_cb):
            try:
                progress_cb(stage, status, message)
            except Exception:
                logger.debug("execute_plan progress callback failed", exc_info=True)

    for chart_index, chart in enumerate(charts, start=1):
        question = str(chart.get("question") or "").strip()
        title = str(chart.get("title") or "").strip()
        want_chart = chart.get("include_in_dashboard") is not False
        label = title or question
        logger.info("Plan execute chart=%s question=%s", title or "untitled", question)
        _progress(
            "think_step",
            "started",
            f"Analyzing question {chart_index}: {label}",
        )
        step_ok = True

        try:
            chart_topic = str(chart.get("topic") or "").strip()
            chart_components = [
                str(item).strip()
                for item in (chart.get("components") or [])
                if str(item).strip()
            ]
            chart_hints = [
                str(item).strip()
                for item in (chart.get("measure_hints") or [])
                if str(item).strip()
            ]
            if chart_topic or plan_topics:
                work["selected_topics"] = list(
                    dict.fromkeys(
                        ([chart_topic] if chart_topic else []) + list(plan_topics or [])
                    )
                )
            if plan_domains:
                work["selected_domains"] = list(plan_domains)
            work["chart_components"] = chart_components
            work["chart_measure_hints"] = chart_hints
            if chart_components or chart_hints:
                hint_bits = []
                if chart_hints:
                    hint_bits.append("Preferred measures: " + ", ".join(chart_hints))
                if chart_components:
                    hint_bits.append("Preferred components: " + ", ".join(chart_components))
                prior = str(work.get("current_semantic_context") or "").strip()
                work["current_semantic_context"] = (
                    f"{prior}\n" + "\n".join(hint_bits) if prior else "\n".join(hint_bits)
                ).strip()

            prompt = question
            executed: Dict[str, Any] = {"ok": False, "error": "SQL execution failed"}
            sql_attempts = 0
            last_error = ""
            for attempt in range(SQL_RETRY_MAX):
                sql_attempts = attempt + 1
                generated = sql_tools.generate(question, work, prompt=prompt)
                work = _merge(work, generated.get("state_patch") or {})
                loops += 1
                if not generated.get("ok"):
                    executed = {
                        "ok": False,
                        "error": generated.get("error") or "SQL generation failed",
                    }
                    last_error = str(executed["error"])
                    # Still try rewrite from generation error on next attempt.
                    prompt = _rewrite_prompt(
                        question,
                        work.get("generated_sql") or "",
                        executed["error"],
                    )
                    continue

                sql = generated.get("sql") or work.get("generated_sql") or ""
                executed = sql_tools.execute(sql, work)
                work = _merge(work, executed.get("state_patch") or {})
                loops += 1
                if executed.get("ok"):
                    break
                err = executed.get("error") or work.get("sql_error") or "SQL execution failed"
                last_error = str(err)
                logger.info(
                    "Plan execute retry %s/%s for %s: %s",
                    attempt + 1,
                    SQL_RETRY_MAX,
                    title or question[:40],
                    err,
                )
                prompt = _rewrite_prompt(question, sql, err)

            if not executed.get("ok"):
                step_ok = False
                work = _append_failure(
                    work,
                    question,
                    executed.get("error") or "SQL execution failed",
                    title=title,
                )
                chart_traces.append(
                    {
                        "index": chart_index,
                        "question": question,
                        "title": title,
                        "status": "failed",
                        "sql_attempts": sql_attempts,
                        "result": last_error or executed.get("error") or "SQL execution failed",
                        "reason": (
                            f"SQL failed after {sql_attempts} attempt(s): "
                            f"{last_error or executed.get('error') or 'unknown error'}"
                        ),
                    }
                )
                continue

            if not want_chart:
                # Build report_model + sql metadata (and chat memory under parent_seq_N)
                # so Preview/Metrics work without a second interactive-chat call.
                work = _apply_viz_hint(work, chart)
                built = report_tools.build(question, work)
                work = _merge(work, built.get("state_patch") or {})
                loops += 1
                if not built.get("ok"):
                    analyzed = analysis_tools.analyze(
                        str(work.get("query_result") or "")[:4000],
                        work,
                        question=question,
                        title=title,
                    )
                    work = _merge(work, analyzed.get("state_patch") or {})
                    loops += 1
                    collected = list(work.get("collected_data") or [])
                    analysis_text = ""
                    if collected:
                        last = dict(collected[-1])
                        last["title"] = title
                        last["include_in_dashboard"] = False
                        collected[-1] = last
                        work["collected_data"] = collected
                        analysis_text = str(last.get("analysis") or "").strip()
                    chart_traces.append(
                        {
                            "index": chart_index,
                            "question": question,
                            "title": title,
                            "status": "ok",
                            "sql_attempts": sql_attempts,
                            "kind": "text_insight",
                            "result": analysis_text[:500],
                            "reason": (
                                str(chart.get("purpose") or "").strip()
                                or "Text insight produced after viz build failed."
                            ),
                        }
                    )
                    continue

                # Overlay LLM narrative onto the built step (do not append a second step).
                from helicalbi.sql_agent.tools.analysis import (
                    _llm_step_insight,
                    _sample_rows_from_raw,
                    _MAX_SAMPLE_ROWS,
                )

                sql_session = (
                    work.get("session_context")
                    if isinstance(work.get("session_context"), dict)
                    else {}
                )
                last_sql = (
                    sql_session.get("_last_sql_state")
                    if isinstance(sql_session, dict)
                    else None
                )
                sample_data: list = []
                if isinstance(last_sql, dict):
                    from helicalbi.sql_agent.tools.context import AgentToolContext

                    preview = AgentToolContext(work).preview_data(last_sql)
                    if isinstance(preview, list) and preview:
                        sample_data = preview[:_MAX_SAMPLE_ROWS]
                if not sample_data:
                    sample_data = _sample_rows_from_raw(
                        work.get("query_result") or "",
                        limit=_MAX_SAMPLE_ROWS,
                    )
                llm_insight = _llm_step_insight(
                    question=question,
                    title=title,
                    sql=str(work.get("generated_sql") or ""),
                    sample_data=sample_data,
                    state=work,
                )
                narrative_answer = str(llm_insight.answer or "").strip() if llm_insight else ""
                narrative_analysis = str(llm_insight.analysis or "").strip() if llm_insight else ""
                collected = list(work.get("collected_data") or [])
                if collected:
                    last = dict(collected[-1])
                    last["title"] = title
                    last["include_in_dashboard"] = False
                    if narrative_answer:
                        last["answer"] = narrative_answer
                    if narrative_analysis:
                        last["analysis"] = narrative_analysis
                    elif narrative_answer:
                        last["analysis"] = narrative_answer
                    chat_response = dict(last.get("chat_response") or {})
                    summary = dict(chat_response.get("summary") or {})
                    if narrative_answer:
                        summary["insight"] = narrative_answer
                    if narrative_analysis:
                        summary["analysis"] = narrative_analysis
                    chat_response["summary"] = summary
                    last["chat_response"] = chat_response
                    if not last.get("report_model") and chat_response.get("report_model"):
                        last["report_model"] = chat_response.get("report_model")
                    collected[-1] = last
                    work["collected_data"] = collected
                loops += 1
                chart_traces.append(
                    {
                        "index": chart_index,
                        "question": question,
                        "title": title,
                        "status": "ok",
                        "sql_attempts": sql_attempts,
                        "kind": "text_insight",
                        "result": (narrative_analysis or narrative_answer)[:500],
                        "reason": (
                            str(chart.get("purpose") or "").strip()
                            or "Built report_model for Preview; narrative for final answer."
                        ),
                    }
                )
                continue

            work = _apply_viz_hint(work, chart)
            built = report_tools.build(question, work)
            work = _merge(work, built.get("state_patch") or {})
            loops += 1
            if not built.get("ok"):
                step_ok = False
                work = _append_failure(
                    work,
                    question,
                    built.get("error") or "Visualization failed",
                    title=title,
                )
                chart_traces.append(
                    {
                        "index": chart_index,
                        "question": question,
                        "title": title,
                        "status": "failed",
                        "sql_attempts": sql_attempts,
                        "kind": "visualization",
                        "result": built.get("error") or "Visualization failed",
                        "reason": "Query succeeded but visualization build failed.",
                    }
                )
                continue
            collected = list(work.get("collected_data") or [])
            if collected and title:
                last = dict(collected[-1])
                last["title"] = title
                collected[-1] = last
                work["collected_data"] = collected
            chart_traces.append(
                {
                    "index": chart_index,
                    "question": question,
                    "title": title,
                    "status": "ok",
                    "sql_attempts": sql_attempts,
                    "kind": "visualization",
                    "result": title or question,
                    "reason": (
                        str(chart.get("purpose") or "").strip()
                        or "Chart built for dashboard layout."
                    ),
                }
            )
        finally:
            _progress(
                "think_step",
                "done" if step_ok else "failed",
                (
                    f"Completed question {chart_index}: {label}"
                    if step_ok
                    else f"Could not complete question {chart_index}: {label}"
                ),
            )

    activity_trace = append_trace(
        activity_trace,
        trace_entry(
            "execute_plan",
            stage="execute_graph",
            result={"charts": chart_traces, "chart_count": len(chart_traces)},
            reason=f"Executed {len(chart_traces)} planned question(s) in stored order.",
        ),
    )

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
        "activity_trace": activity_trace,
    }
