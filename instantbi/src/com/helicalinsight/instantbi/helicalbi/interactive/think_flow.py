"""Think mode for /interactive: plan, execute text-only findings, conclusive answer."""
from __future__ import annotations

import json
import logging
import re
from typing import Any, Callable, Iterator, Mapping, Optional, Sequence

from pydantic import BaseModel, Field

from helicalbi.common import app_config
from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.controller.activity import ActivityReporter
from helicalbi.controller.helpers import RequestAborted, resolve_request_id
from helicalbi.interactive.modes import INTERACTIVE_MODE_THINK, apply_requested_mode
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.sql_agent.activity_details import build_llm_activity_details
from helicalbi.sql_agent.config import DEFAULT_DASHBOARD_SUB_QUESTIONS

logger = logging.getLogger(__name__)

ProgressCallback = Callable[[str, str, str], None]

THINK_FINAL_ANSWER_PROMPT = """You are InstantBI writing the Conclusion for a business user.
An investigation ran numbered supporting questions and collected text findings (no charts).
Write a strategic answer to the user question, grounded only in those findings.

Goal:
- Answer like an advisor: what matters most, why, and what to do next.
- Do NOT write a generic multi-paragraph report summary that restates every finding.
- Prefer a decisive recommendation or prioritization when the findings support one.

Voice and structure (vary the shape — do NOT always use the same 3 equal paragraphs):
- Open with the strategic answer in 1–2 sentences (what to prioritize, avoid, or decide).
- Then surface key points: use markdown **bold** on the few decisive numbers, names, or ratios.
- Prefer 2–4 short markdown bullets for drivers, criteria, or ranked implications when that
  makes the answer clearer than prose. Bullets should be punchy, not mini-paragraphs.
- Close with one concrete next action or implication when the findings support it.
- Choose the structure that fits the question (e.g. ranking, criteria list, risk callout,
  cost-driver focus). Avoid repeating the same prose pattern every time.
- Keep it concise: roughly 80–180 words. Skip filler and inventory of every supporting question.
- Use everyday business language. Do not mention SQL, schemas, agents, or charts.
- Do not use headings or labels like "Insight:", "Summary:", or "Conclusion:".
- If something could not be determined from the findings, say that briefly once.

Formatting:
- Markdown only: **bold** for key figures/entities, and optional `-` bullets.
- No tables, code blocks, or numbered multi-section outlines.

Accuracy:
- Use only numbers and entities present in the findings; never invent values.
- If findings conflict or look inconsistent, prefer the clearest supported claim and note uncertainty.

Citations (required):
- After key claims, add inline markers like [1] or [2] that match the supporting
  Question numbers from the numbered list below.
- Set cited_question_indexes to those same 1-based indexes (only the ones you used).
- Cite the questions whose answers actually support each claim; do not invent indexes.
- Prefer markers next to decisive figures or recommendations, not on every sentence.

User question:
{original_question}

Supporting questions:
{numbered_questions}

Findings:
{findings}

Plan rationale (optional):
{rationale}

{format_instructions}
"""


class ThinkCitedAnswer(BaseModel):
    final_answer: str = Field(
        description=(
            "Strategic markdown conclusion with inline [n] citations to supporting "
            "questions; lead with the decision/takeaway, bold key figures, optional "
            "short bullets, and one next action."
        )
    )
    cited_question_indexes: list[int] = Field(
        default_factory=list,
        description=(
            "1-based indexes of supporting questions cited in final_answer "
            "(must match the [n] markers used in the prose)"
        ),
    )


def _ensure_not_aborted(request_id: Optional[str]) -> None:
    if request_id and request_cancellation.is_cancelled(request_id):
        raise RequestAborted(f"Request {request_id} was cancelled")


def _interactive_chat_dict(chat_response: Any) -> dict[str, Any]:
    """Shape one step like /interactive wire chat_response (no data/metadata)."""
    if isinstance(chat_response, ChatResponse):
        return chat_response.to_interactive_client_dict()
    if not isinstance(chat_response, dict):
        return {}
    try:
        return ChatResponse.model_validate(chat_response).to_interactive_client_dict()
    except Exception:
        payload = dict(chat_response)
        for key in ("data", "metadata"):
            payload.pop(key, None)
        return payload


def _public_step(step: Mapping[str, Any]) -> dict[str, Any]:
    chat_response = step.get("chat_response") if isinstance(step.get("chat_response"), dict) else {}
    return {
        "sub_question": step.get("sub_question") or "",
        "title": step.get("title") or "",
        "analysis": step.get("analysis") or "",
        "chat_seq_id": step.get("chat_seq_id") or "",
        "chat_response": _interactive_chat_dict(chat_response),
        "report_model": step.get("report_model")
        or (chat_response.get("report_model") if isinstance(chat_response, dict) else {})
        or {},
    }


def _asked_questions_from_plan(plan: Mapping[str, Any] | None) -> list[str]:
    questions: list[str] = []
    for chart in (plan or {}).get("charts") or []:
        if not isinstance(chart, dict):
            continue
        question = str(chart.get("question") or "").strip()
        if question:
            questions.append(question)
    return questions


def _numbered_questions_block(asked_questions: Sequence[str]) -> str:
    lines = []
    for index, question in enumerate(asked_questions, start=1):
        lines.append(f"Question {index}: {question}")
    return "\n".join(lines) if lines else "(none)"


def _fallback_cited_answer(
    question: str,
    asked_questions: Sequence[str],
    *,
    findings: Sequence[str] | None = None,
) -> ThinkCitedAnswer:
    if not asked_questions:
        return ThinkCitedAnswer(
            final_answer=(
                f"To answer “{question}”, I need supporting investigation questions "
                "from the plan."
            ),
            cited_question_indexes=[],
        )
    noted = [str(item).strip() for item in (findings or []) if str(item or "").strip()]
    if noted:
        cited = list(range(1, len(asked_questions) + 1))
        body = " ".join(noted[:3])
        return ThinkCitedAnswer(final_answer=body, cited_question_indexes=cited)
    cited = list(range(1, min(3, len(asked_questions)) + 1))
    cites = ", ".join(f"Question {i}" for i in cited)
    body = (
        f"Investigation for “{question}” covered {cites}. "
        "Supporting findings were limited; refine the question or try Fast mode "
        "for a single focused chart."
    )
    return ThinkCitedAnswer(final_answer=body, cited_question_indexes=cited)


def _findings_block(history: Sequence[Mapping[str, Any]]) -> str:
    lines: list[str] = []
    for item in history:
        index = item.get("index") or 0
        analysis = str(item.get("analysis") or "").strip()
        title = str(item.get("title") or "").strip()
        question = str(item.get("question") or "").strip()
        if not analysis:
            continue
        label = title or question or f"Step {index}"
        lines.append(f"Question {index} — {label}: {analysis}")
    return "\n".join(lines) if lines else "(none)"


def synthesize_think_final_answer(
    question: str,
    asked_questions: Sequence[str],
    *,
    plan: Optional[Mapping[str, Any]] = None,
    question_history: Optional[Sequence[Mapping[str, Any]]] = None,
    state: Optional[dict[str, Any]] = None,
    _invoke=None,
) -> ThinkCitedAnswer:
    """LLM (or fallback) conclusive answer that cites Question N indexes."""
    asked = [str(q).strip() for q in asked_questions if str(q or "").strip()]
    history = list(question_history or [])
    findings = [
        str(item.get("analysis") or "").strip()
        for item in history
        if str(item.get("analysis") or "").strip()
    ]
    if not asked:
        return _fallback_cited_answer(question, asked, findings=findings)

    invoke = _invoke
    if invoke is None:
        from helicalbi.sql_agent.llm import invoke_agent_model

        invoke = invoke_agent_model

    try:
        parsed = invoke(
            THINK_FINAL_ANSWER_PROMPT,
            {
                "original_question": question,
                "numbered_questions": _numbered_questions_block(asked),
                "findings": _findings_block(history),
                "rationale": str((plan or {}).get("rationale") or "").strip() or "(none)",
            },
            ThinkCitedAnswer,
            state=state,
        )
        indexes = []
        for raw in parsed.cited_question_indexes or []:
            try:
                idx = int(raw)
            except (TypeError, ValueError):
                continue
            if 1 <= idx <= len(asked) and idx not in indexes:
                indexes.append(idx)
        answer = str(parsed.final_answer or "").strip()
        if not answer:
            return _fallback_cited_answer(question, asked, findings=findings)
        if not indexes:
            for match in re.finditer(r"\[(\d+)\]", answer):
                try:
                    idx = int(match.group(1))
                except (TypeError, ValueError):
                    continue
                if 1 <= idx <= len(asked) and idx not in indexes:
                    indexes.append(idx)
        if not indexes:
            indexes = [
                int(item.get("index") or 0)
                for item in history
                if str(item.get("analysis") or "").strip() and int(item.get("index") or 0) > 0
            ] or list(range(1, min(3, len(asked)) + 1))
        return ThinkCitedAnswer(final_answer=answer, cited_question_indexes=indexes)
    except Exception:
        logger.exception("Think final-answer synthesis failed; using fallback")
        return _fallback_cited_answer(question, asked, findings=findings)


def _opening_sql_context(
    asked_questions: Sequence[str],
    plan: Optional[Mapping[str, Any]] = None,
) -> str:
    lines = []
    for index, asked in enumerate(asked_questions, start=1):
        text = str(asked or "").strip()
        if text:
            lines.append(f"{index}. {text}")
    body = "\n".join(lines) if lines else "(none)"
    rationale = str((plan or {}).get("rationale") or "").strip()
    if rationale:
        return f"{body}\n\nRationale: {rationale}"
    return body


def _opening_metadata(
    plan: Optional[Mapping[str, Any]] = None,
    plan_payload: Optional[Mapping[str, Any]] = None,
) -> dict[str, Any]:
    payload = plan_payload if isinstance(plan_payload, Mapping) else {}
    plan_map = plan if isinstance(plan, Mapping) else {}
    domain = payload.get("selected_domains") or plan_map.get("domain") or []
    topics = payload.get("selected_topics") or plan_map.get("topics") or []
    tables = payload.get("selected_tables") or plan_map.get("tables") or []
    return {
        "required_tables": list(tables) if not isinstance(tables, str) else [tables],
        "domain": list(domain) if not isinstance(domain, str) else [domain],
        "topics": list(topics) if not isinstance(topics, str) else [topics],
    }


def synthesize_think_opening_insight(
    question: str,
    asked_questions: Sequence[str],
    *,
    plan: Optional[Mapping[str, Any]] = None,
    plan_payload: Optional[Mapping[str, Any]] = None,
    state: Optional[dict[str, Any]] = None,
    _invoke_llm=None,
) -> str:
    """Fast-mode SqlSuccess insight, adapted for think mode, shown above questions."""
    from helicalbi.prompt.SqlSuccessPrompty import think_success_prompt_formatted

    formatted = think_success_prompt_formatted.format(
        user_query=question,
        sql_query=_opening_sql_context(asked_questions, plan),
        metadata=json.dumps(_opening_metadata(plan, plan_payload), default=str),
    )
    invoke = _invoke_llm
    try:
        if invoke is None:
            from helicalbi.common.configuration import llm
            from helicalbi.common.LlmInvokeHelper import invoke_llm

            insight, _ = invoke_llm(llm, formatted, state=state)
        else:
            insight, _ = invoke(formatted, state=state)
        return str(getattr(insight, "content", insight) or "").strip()
    except Exception:
        logger.exception("Think opening insight failed; continuing without overview")
        return ""


def _step_analysis(step: Mapping[str, Any] | None) -> str:
    if not isinstance(step, Mapping):
        return ""
    analysis = str(step.get("analysis") or "").strip()
    if analysis and not analysis.lstrip().startswith(("[", "{")):
        return analysis
    answer = str(step.get("answer") or "").strip()
    if answer and not answer.lstrip().startswith(("[", "{")):
        return answer
    chat_response = step.get("chat_response") if isinstance(step.get("chat_response"), dict) else {}
    summary = chat_response.get("summary") if isinstance(chat_response.get("summary"), dict) else {}
    insight = str(summary.get("insight") or "").strip()
    if insight and not insight.lstrip().startswith(("[", "{")):
        return insight
    # Last resort: never surface raw JSON previews in the UI.
    from helicalbi.sql_agent.tools.findings import describe_query_findings

    raw = (
        str(step.get("execution_result") or "").strip()
        or str(step.get("analysis") or "").strip()
        or str(step.get("answer") or "").strip()
    )
    described = describe_query_findings(
        raw,
        question=str(step.get("sub_question") or "").strip(),
        title=str(step.get("title") or "").strip(),
    )
    return described["analysis"]


def _step_answer(step: Mapping[str, Any] | None) -> str:
    if not isinstance(step, Mapping):
        return ""
    answer = str(step.get("answer") or "").strip()
    if answer and not answer.lstrip().startswith(("[", "{")):
        return answer
    analysis = _step_analysis(step)
    return analysis


def _text_only_plan(plan: Mapping[str, Any] | None) -> dict[str, Any]:
    """Copy plan charts with include_in_dashboard=False so execute stores analysis only."""
    raw = dict(plan or {})
    charts: list[dict[str, Any]] = []
    for chart in raw.get("charts") or []:
        if not isinstance(chart, dict):
            continue
        item = dict(chart)
        item["include_in_dashboard"] = False
        charts.append(item)
    raw["charts"] = charts
    return raw


def _asked_questions_from_result(result: Mapping[str, Any], plan: Mapping[str, Any]) -> list[str]:
    asked = [
        str(q).strip()
        for q in (result.get("asked_questions") or [])
        if str(q or "").strip()
    ]
    if asked:
        return asked
    from_steps = [
        str(step.get("sub_question") or "").strip()
        for step in (result.get("collected_data") or [])
        if str(step.get("sub_question") or "").strip()
    ]
    if from_steps:
        return from_steps
    return _asked_questions_from_plan(plan)


def _question_history(
    asked_questions: Sequence[str],
    plan: Mapping[str, Any] | None,
    collected_data: Sequence[Mapping[str, Any]] | None = None,
) -> list[dict[str, Any]]:
    charts = [
        chart for chart in ((plan or {}).get("charts") or []) if isinstance(chart, dict)
    ]
    steps = [step for step in (collected_data or []) if isinstance(step, Mapping)]
    by_question: dict[str, Mapping[str, Any]] = {}
    for step in steps:
        key = str(step.get("sub_question") or "").strip().lower()
        if key and key not in by_question:
            by_question[key] = step
    history: list[dict[str, Any]] = []
    for index, question in enumerate(asked_questions, start=1):
        chart = charts[index - 1] if index - 1 < len(charts) else {}
        step = by_question.get(str(question).strip().lower())
        if step is None and index - 1 < len(steps):
            step = steps[index - 1]
        chat_response = (step or {}).get("chat_response") if isinstance(step, Mapping) else None
        if not isinstance(chat_response, dict):
            chat_response = {}
        report_model = (step or {}).get("report_model") if isinstance(step, Mapping) else None
        if not isinstance(report_model, dict):
            report_model = chat_response.get("report_model") if isinstance(chat_response.get("report_model"), dict) else {}
        public_chat = _interactive_chat_dict(chat_response) if chat_response else {}
        if report_model and not public_chat.get("report_model"):
            public_chat = dict(public_chat)
            public_chat["report_model"] = report_model
        components = [
            str(c).strip()
            for c in (chart.get("components") or chart.get("measure_hints") or [])
            if str(c).strip()
        ]
        dimensions = [
            str(d).strip()
            for d in (chart.get("dimensions") or [])
            if str(d).strip()
        ]
        # Fill empty Metrics fields from the plan chart so the UI is not blank
        # when ChatResponse.sql arrived with empty lists.
        sql_section = public_chat.get("sql") if isinstance(public_chat.get("sql"), dict) else {}
        sql_section = dict(sql_section)
        cube = sql_section.get("required_cube_info")
        if not isinstance(cube, dict):
            cube = {}
        else:
            cube = dict(cube)
        if components and not cube.get("picked_metrics"):
            cube["picked_metrics"] = components
        if dimensions and not cube.get("picked_dimensions"):
            cube["picked_dimensions"] = dimensions
        if cube:
            sql_section["required_cube_info"] = cube
        if components and not sql_section.get("required_column"):
            sql_section["required_column"] = components
        plan_domain = str((plan or {}).get("domain") or "").strip()
        plan_domains = (plan or {}).get("domains") if isinstance((plan or {}).get("domains"), list) else []
        plan_topics = (plan or {}).get("topics") if isinstance((plan or {}).get("topics"), list) else []
        if plan_domain and not sql_section.get("required_domain"):
            sql_section["required_domain"] = [plan_domain]
        elif plan_domains and not sql_section.get("required_domain"):
            sql_section["required_domain"] = [str(d).strip() for d in plan_domains if str(d).strip()]
        if plan_topics and not sql_section.get("required_topic"):
            sql_section["required_topic"] = [str(t).strip() for t in plan_topics if str(t).strip()]
        if sql_section:
            public_chat = dict(public_chat)
            public_chat["sql"] = sql_section
        history.append(
            {
                "index": index,
                "question": question,
                "title": str((step or {}).get("title") or chart.get("title") or "").strip(),
                "viz_hint": str(chart.get("viz_hint") or "").strip(),
                "purpose": str(chart.get("purpose") or "").strip(),
                "components": components,
                "measure_hints": components,
                "dimensions": dimensions,
                "answer": _step_answer(step),
                "analysis": _step_analysis(step),
                "chat_seq_id": str((step or {}).get("chat_seq_id") or "").strip(),
                "chat_response": public_chat,
                "report_model": report_model or {},
                "fullChatResponse": public_chat,
            }
        )
    return history


def _details_enabled(override: Optional[Any] = None) -> bool:
    if override is not None:
        if isinstance(override, str):
            return override.strip().lower() in {"1", "true", "yes", "on"}
        return bool(override)
    return bool(getattr(app_config, "show_llm_activity_details", False))


def run_think_mode(
    question: str,
    *,
    session_cookie: str,
    username: str,
    model_file_name: str,
    model_location: str,
    thread_id: str,
    chat_seq_id: Any = "1",
    last_chats: Optional[list] = None,
    request_id: Optional[str] = None,
    max_sub_questions: Optional[int] = None,
    agent_mode: Optional[str] = None,
    user_role: Optional[Sequence[Any]] = None,
    user_profile: Optional[Sequence[Any]] = None,
    persona_hint: Optional[str] = None,
    strategy_hint: Optional[str] = None,
    show_llm_activity_details: Optional[Any] = None,
    on_progress: Optional[ProgressCallback] = None,
    _create_and_store_plan=None,
    _run_dashboard_agent=None,
    _load_plan=None,
    _save_plan=None,
    _synthesize_final_answer=None,
    _synthesize_opening_insight=None,
) -> dict[str, Any]:
    """Plan then execute investigation questions as text findings.

    Charts are marked ``include_in_dashboard=False`` so execute stores analysis +
    report_model without auto-hydrating client charts. Dashboard layout is still
    built from those report_models (positions/sizing) for the Dashboard Model tab.
    """
    create_and_store_plan = _create_and_store_plan
    run_dashboard_agent = _run_dashboard_agent
    load_plan = _load_plan
    save_plan = _save_plan
    if create_and_store_plan is None or run_dashboard_agent is None:
        from helicalbi.sql_agent.dashboard_graph import run_dashboard_agent as _rda
        from helicalbi.sql_agent.investigation import create_and_store_plan as _casp

        create_and_store_plan = create_and_store_plan or _casp
        run_dashboard_agent = run_dashboard_agent or _rda
    if load_plan is None or save_plan is None:
        from helicalbi.sql_agent.plan_memory import load_plan as _lp
        from helicalbi.sql_agent.plan_memory import save_plan as _sp

        load_plan = load_plan or _lp
        save_plan = save_plan or _sp

    _ensure_not_aborted(request_id)
    if not str(question or "").strip():
        raise ValueError("input.inputString is required for think mode")
    if not model_file_name or not model_location:
        raise ValueError("input.model.file and input.model.dir are required")
    if not thread_id:
        raise ValueError("input.chatid is required for think mode")

    chart_ceiling = (
        int(max_sub_questions)
        if max_sub_questions is not None
        else int(app_config.dashboard_max_sub_questions or DEFAULT_DASHBOARD_SUB_QUESTIONS)
    )
    resolved_agent_mode = str(
        agent_mode or app_config.dashboard_default_mode or "balanced"
    )
    include_details = _details_enabled(show_llm_activity_details)

    def _emit(stage: str, status: str, message: str) -> None:
        if on_progress:
            on_progress(stage, status, message)

    logger.info(
        "Interactive think mode start user=%s thread=%s seq=%s agent_mode=%s",
        username,
        thread_id,
        chat_seq_id,
        resolved_agent_mode,
    )
    _emit("think_plan", "started", "Planning investigation questions…")

    plan_payload = create_and_store_plan(
        question,
        session_cookie=session_cookie,
        username=username,
        model_file_name=model_file_name,
        model_location=model_location,
        thread_id=thread_id,
        chat_seq_id=chat_seq_id,
        last_chats=last_chats,
        request_id=request_id,
        max_sub_questions=chart_ceiling,
        agent_mode=resolved_agent_mode,
        user_role=user_role,
        user_profile=user_profile,
        persona_hint=persona_hint,
        strategy_hint=strategy_hint,
    )
    _ensure_not_aborted(request_id)

    plan = _text_only_plan(plan_payload.get("plan") or {})
    asked_from_plan = list(plan_payload.get("asked_questions") or []) or _asked_questions_from_plan(
        plan
    )
    _emit("think_plan", "done", f"Prepared {len(asked_from_plan)} investigation question(s).")
    opening_state = {"token_usage": {}}
    synthesize_opening = _synthesize_opening_insight or synthesize_think_opening_insight
    _emit("think_intro", "started", "Writing a short overview…")
    try:
        opening_insight = str(
            synthesize_opening(
                question,
                asked_from_plan,
                plan=plan,
                plan_payload=plan_payload,
                state=opening_state,
            )
            or ""
        ).strip()
    except Exception:
        logger.exception("Think opening insight hook failed; continuing without overview")
        opening_insight = ""
    _emit("think_intro", "done", "Overview is ready.")
    for index, asked in enumerate(asked_from_plan, start=1):
        _emit("think_question", "done", f"Question {index}: {asked}")

    _emit("think_execute", "started", "Starting supporting questions…")
    result = run_dashboard_agent(
        question,
        session_cookie=session_cookie,
        username=username,
        model_file_name=model_file_name,
        model_location=model_location,
        thread_id=thread_id,
        chat_seq_id=chat_seq_id,
        last_chats=last_chats,
        request_id=request_id,
        max_sub_questions=chart_ceiling,
        agent_mode=str(
            (plan_payload.get("mode") or {}).get("name")
            or plan_payload.get("agent_mode")
            or resolved_agent_mode
        ),
        investigation_plan=plan,
        persona=plan_payload.get("persona") or {},
        user_role=list(user_role or []),
        user_profile=list(user_profile or []),
        build_dashboard=True,
        on_progress=on_progress,
    )
    _ensure_not_aborted(request_id)

    try:
        stored = load_plan(thread_id, chat_seq_id) or {}
        if stored:
            stored["status"] = "executed"
            stored["plan"] = plan
            save_plan(thread_id, chat_seq_id, stored)
    except Exception:
        logger.exception(
            "Interactive think: failed to mark plan executed thread=%s seq=%s",
            thread_id,
            chat_seq_id,
        )

    asked_questions = asked_from_plan or _asked_questions_from_result(result, plan)
    question_history = _question_history(
        asked_questions,
        plan,
        result.get("collected_data") or [],
    )
    _emit(
        "think_execute",
        "done",
        f"Collected findings for {sum(1 for item in question_history if item.get('analysis'))} "
        "question(s).",
    )

    synthesizer_answer = str(result.get("final_answer") or "").strip()
    synthesize = _synthesize_final_answer or synthesize_think_final_answer
    # Always draft the think-mode Conclusion with the explanatory prompt.
    # The dashboard synthesizer answer tends to dump every step's numbers.
    cited = synthesize(
        question,
        asked_questions,
        plan=plan,
        question_history=question_history,
        state={"token_usage": dict(result.get("token_usage") or {})},
    )
    final_answer = str(cited.final_answer or "").strip() or synthesizer_answer
    cited_indexes = list(cited.cited_question_indexes or [])
    if not cited_indexes:
        cited_indexes = [
            int(item.get("index") or 0)
            for item in question_history
            if item.get("analysis") and int(item.get("index") or 0) > 0
        ]
    _emit("think_answer", "done", "Drafted conclusive final answer.")

    token_usage = dict(result.get("token_usage") or {})
    plan_usage = plan_payload.get("token_usage") or {}
    if isinstance(plan_usage, dict) and plan_usage:
        for key, value in plan_usage.items():
            if value is None:
                continue
            if isinstance(value, (int, float)) and isinstance(token_usage.get(key), (int, float)):
                token_usage[key] = token_usage.get(key, 0) + value
            elif key not in token_usage:
                token_usage[key] = value
    opening_usage = opening_state.get("token_usage") or {}
    if isinstance(opening_usage, dict) and opening_usage:
        for key, value in opening_usage.items():
            if value is None:
                continue
            if isinstance(value, (int, float)) and isinstance(token_usage.get(key), (int, float)):
                token_usage[key] = token_usage.get(key, 0) + value
            elif key not in token_usage:
                token_usage[key] = value

    payload = {
        "mode": INTERACTIVE_MODE_THINK,
        "phase": "execute",
        "original_question": result.get("original_question") or question,
        "asked_questions": asked_questions,
        "question_history": question_history,
        "cited_question_indexes": cited_indexes,
        # Text-only: do not hydrate client charts from chat_responses.
        "chat_responses": [],
        "chat_response": {},
        "opening_insight": opening_insight,
        "final_answer": final_answer,
        "plan": plan,
        "persona": plan_payload.get("persona") or result.get("persona") or {},
        "strategy": plan_payload.get("strategy") or result.get("strategy") or "",
        "token_usage": token_usage,
        "investigation_steps": result.get("investigation_steps")
        or plan_payload.get("investigation_steps")
        or [],
        "attempt_count": int(
            result.get("attempt_count")
            if result.get("attempt_count") is not None
            else (result.get("tool_loop_count") or 0)
        ),
        "message": "Investigation complete. Findings are shown as text explanations.",
        "dashboard": result.get("dashboard") or {},
        "dashboard_model": result.get("dashboard") or {},
    }

    if include_details:
        strategy_detail = plan_payload.get("strategy_detail") or {
            "id": plan_payload.get("strategy") or "",
            "selection": {},
        }
        payload["llm_activity_details"] = build_llm_activity_details(
            persona=plan_payload.get("persona") or {},
            strategy=strategy_detail,
            strategy_selection=(
                strategy_detail.get("selection")
                if isinstance(strategy_detail, Mapping)
                else {}
            ),
            selected_domains=plan_payload.get("selected_domains") or [],
            selected_topics=plan_payload.get("selected_topics") or [],
            selected_tables=plan_payload.get("selected_tables") or [],
            plan=plan,
            plan_graph=plan_payload.get("plan_activity_trace") or [],
            execute_graph=result.get("activity_trace") or [],
            question_history=question_history,
            cited_question_indexes=cited_indexes,
            final_answer=final_answer,
        )

    logger.info(
        "Interactive think completed thread=%s questions=%s cited=%s answer_chars=%s",
        thread_id,
        len(asked_questions),
        payload["cited_question_indexes"],
        len(final_answer or ""),
    )
    return payload


class ThinkTurn:
    """SSE/JSON turn for interactive think (plan + text-only execute)."""

    def __init__(self, payload: dict[str, Any]) -> None:
        self.payload = payload or {}
        self.user_input = self.payload["input"]
        self.user_query = self.user_input["inputString"]
        from helicalbi.common.auth import bind_request_identity, resolve_role_profile
        from helicalbi.common.ChatManager import add_message, get_last_n
        from helicalbi.common.LlmInvokeHelper import set_total_time_consumed
        import time

        self._time = time
        self._add_message = add_message
        self._get_last_n = get_last_n
        self._set_total_time_consumed = set_total_time_consumed
        (
            self.session_cookie,
            self.username,
            self.user_id,
            _org_id,
        ) = bind_request_identity(self.payload, self.user_input)
        self.role_profile = resolve_role_profile(self.payload, self.user_input)
        self.model_file_name = self.user_input["model"]["file"]
        self.location = self.user_input["model"]["dir"]
        self.thread_id = self.user_input["chatid"]
        self.chat_seq_id = self.user_input["chat_seq_id"]
        self.request_id = resolve_request_id(self.payload, self.user_input)
        self.reporter = ActivityReporter(self.user_query)
        self.to_send: dict[str, Any] = {}
        self.request_status = "SUCCESS"
        self.error_message: str | None = None
        self.request_started = time.perf_counter()

    def run(self) -> dict[str, Any]:
        self._execute()
        return self._client_payload()

    def _client_payload(self) -> dict[str, Any]:
        self.to_send = apply_requested_mode(self.to_send, self.user_input)
        return self.to_send

    def stream(self) -> Iterator[str]:
        import queue
        import threading

        self.reporter.enable_streaming()
        writer = self.reporter.writer
        event_queue: queue.Queue = queue.Queue()

        def on_progress(stage: str, status: str, message: str) -> None:
            event_queue.put(writer.progress(stage, status, message))

        def worker() -> None:
            try:
                self._run_think(stream=True, on_progress=on_progress)
                event_queue.put(("__done__", None))
            except RequestAborted as error:
                event_queue.put(("__aborted__", error))
            except Exception as error:
                event_queue.put(("__error__", error))

        try:
            yield writer.begin()
            self.reporter.understood_intent()
            yield from self.reporter.drain()
            thread = threading.Thread(target=worker, daemon=True)
            thread.start()
            while True:
                item = event_queue.get()
                if isinstance(item, tuple):
                    kind, payload = item
                    if kind == "__done__":
                        break
                    if kind == "__aborted__":
                        self.request_status = "ABORTED"
                        self.error_message = "Request has been cancelled."
                        self.to_send = {
                            "mode": INTERACTIVE_MODE_THINK,
                            "phase": "plan",
                            "error": self.error_message,
                            "aborted": True,
                            "asked_questions": self.to_send.get("asked_questions") or [],
                            "question_history": self.to_send.get("question_history") or [],
                            "chat_responses": [],
                            "final_answer": self.to_send.get("final_answer") or "",
                        }
                        yield writer.error(self._client_payload())
                        return
                    if kind == "__error__":
                        logger.exception("Think stream failed")
                        self.request_status = "ERROR"
                        self.error_message = str(payload)
                        self.to_send = {
                            "mode": INTERACTIVE_MODE_THINK,
                            "phase": "plan",
                            "error": self.error_message,
                            "asked_questions": [],
                            "question_history": [],
                            "chat_responses": [],
                            "final_answer": "",
                        }
                        yield writer.error(self._client_payload())
                        return
                else:
                    yield item
            yield from self.reporter.drain()
            yield writer.complete(self._client_payload())
        finally:
            self._finish()

    def _execute(self) -> None:
        try:
            self._run_think(stream=False)
        except RequestAborted:
            self.request_status = "ABORTED"
            self.error_message = "Request has been cancelled."
            self.to_send = {
                "mode": INTERACTIVE_MODE_THINK,
                "phase": "plan",
                "error": self.error_message,
                "aborted": True,
                "asked_questions": [],
                "question_history": [],
                "chat_responses": [],
                "final_answer": "",
            }
        except Exception as error:
            logger.exception("Think turn failed")
            self.request_status = "ERROR"
            self.error_message = str(error)
            self.to_send = {
                "mode": INTERACTIVE_MODE_THINK,
                "phase": "plan",
                "error": self.error_message,
                "asked_questions": [],
                "question_history": [],
                "chat_responses": [],
                "final_answer": "",
            }
        finally:
            self._finish()

    def _run_think(
        self,
        *,
        stream: bool,
        on_progress: Optional[ProgressCallback] = None,
    ) -> None:
        if self.request_id:
            request_cancellation.register(self.request_id)
        last_chats = self._get_last_n(self.thread_id)
        if not last_chats:
            last_chats = self.user_input.get("last_chats", [])
        self._add_message(self.thread_id, self.user_query)

        progress_cb = on_progress
        if progress_cb is None and self.reporter.enabled:

            def progress_cb(stage: str, status: str, message: str) -> None:
                self.reporter.custom(stage, status, message)

        self.to_send = run_think_mode(
            self.user_query,
            session_cookie=self.session_cookie,
            username=self.username,
            model_file_name=self.model_file_name,
            model_location=self.location,
            thread_id=str(self.thread_id),
            chat_seq_id=self.chat_seq_id,
            last_chats=last_chats,
            request_id=self.request_id,
            agent_mode=self.user_input.get("agent_mode")
            or self.user_input.get("dashboard_mode"),
            user_role=self.role_profile.get("userRole")
            or self.user_input.get("userRole")
            or [],
            user_profile=self.role_profile.get("userProfile")
            or self.user_input.get("userProfile")
            or [],
            persona_hint=self.user_input.get("persona")
            or self.user_input.get("user_persona"),
            strategy_hint=self.user_input.get("strategy")
            or self.user_input.get("strategy_id"),
            show_llm_activity_details=self.user_input.get("show_llm_activity_details")
            if "show_llm_activity_details" in self.user_input
            else self.payload.get("show_llm_activity_details"),
            on_progress=progress_cb,
        )
        self._set_total_time_consumed(
            self.to_send, self._time.perf_counter() - self.request_started
        )

    def _finish(self) -> None:
        from helicalbi.audit.llm_usage_audit import audit_llm_usage_async
        from helicalbi.controller.helpers import (
            extract_token_usage_dict,
            resolve_audit_status_from_response,
        )

        if self.request_id:
            request_cancellation.clear(self.request_id)
        self.request_status, self.error_message = resolve_audit_status_from_response(
            self.to_send,
            self.request_status,
            self.error_message,
        )
        audit_llm_usage_async(
            endpoint="/interactive",
            user_id=self.user_id,
            session_cookie=self.session_cookie,
            user_query=self.user_query,
            token_usage=extract_token_usage_dict(self.to_send),
            request_status=self.request_status,
            error_message=self.error_message,
            chat_id=str(self.thread_id) if self.thread_id else None,
            chat_seq_id=str(self.chat_seq_id) if self.chat_seq_id is not None else None,
        )


# Keep ChatResponse helper available for tests that import step shaping.
__all__ = [
    "ThinkCitedAnswer",
    "ThinkTurn",
    "run_think_mode",
    "synthesize_think_final_answer",
    "synthesize_think_opening_insight",
    "_public_step",
]
