"""Plan-then-execute helpers for the dashboard agent endpoint."""
from __future__ import annotations

import logging
from typing import Any, Mapping, Optional, Sequence

from helicalbi.sql_agent.activity_details import strategy_selection_reason
from helicalbi.sql_agent.config import DEFAULT_DASHBOARD_SUB_QUESTIONS
from helicalbi.sql_agent.instantbi_turn import load_model_session
from helicalbi.sql_agent.modes import (
    DEFAULT_MODE,
    MODE_FAST,
    MODE_RESEARCH,
    mode_to_public_dict,
    resolve_mode_profile,
)
from helicalbi.sql_agent.nodes.plan_graph import run_plan_context_graph
from helicalbi.sql_agent.personas import resolve_persona
from helicalbi.sql_agent.plan_memory import load_plan, save_plan
from helicalbi.sql_agent.strategy_tree import (
    attach_strategy,
    get_strategy,
    public_persona,
    public_strategy,
    select_strategy,
)

logger = logging.getLogger(__name__)

_EXECUTE_PHRASES = {
    "execute plan",
    "execute_plan",
    "run plan",
    "run_plan",
}
_EXECUTE_ACTIONS = {"execute", "execute_plan", "run", "run_plan"}

PLAN_READY_MESSAGE = (
    'Plan ready. Send input "execute plan" with the same dashboardid and '
    "dashboard_sequence_id to build the dashboard."
)


def is_execute_plan_request(
    query: str,
    user_input: Optional[Mapping[str, Any]] = None,
) -> bool:
    action = str(
        (user_input or {}).get("action") or (user_input or {}).get("phase") or ""
    ).strip().lower()
    if action in _EXECUTE_ACTIONS:
        return True
    normalized = " ".join(str(query or "").strip().lower().split())
    return normalized in _EXECUTE_PHRASES or normalized.replace(" ", "_") in _EXECUTE_PHRASES


_PUBLIC_PLAN_KEYS = (
    "persona",
    "tier",
    "strategy_id",
    "template_id",
    "domain",
    "topics",
    "original_question",
    "rationale",
    "charts",
)


def plan_to_public_dict(plan: Any) -> dict[str, Any]:
    raw = plan.model_dump() if hasattr(plan, "model_dump") else dict(plan or {})
    public = {key: raw.get(key) for key in _PUBLIC_PLAN_KEYS if key in raw}
    public.setdefault("template_id", "")
    public.setdefault("strategy_id", "")
    public.setdefault("charts", [])
    return public


def _topic_caps_for_mode(mode_name: str) -> tuple[int, int]:
    if mode_name == MODE_FAST:
        return 1, 2
    if mode_name == MODE_RESEARCH:
        return 2, 4
    return 2, 3


def create_and_store_plan(
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
    max_sub_questions: int = DEFAULT_DASHBOARD_SUB_QUESTIONS,
    agent_mode: str = DEFAULT_MODE,
    user_role: Optional[Sequence[Any]] = None,
    user_profile: Optional[Sequence[Any]] = None,
    persona_hint: Optional[str] = None,
    strategy_hint: Optional[str] = None,
) -> dict[str, Any]:
    """Context prep + consulting plan, stored against dashboardid / sequence id."""
    mode_profile = resolve_mode_profile(agent_mode, config_max_charts=max_sub_questions)
    persona = resolve_persona(user_role, user_profile, hint=persona_hint)
    strategy = select_strategy(question, persona=persona, hint=strategy_hint)
    persona = attach_strategy(persona, strategy)
    session = load_model_session(
        session_cookie=session_cookie,
        username=username,
        model_file_name=model_file_name,
        model_location=model_location,
        thread_id=thread_id,
        user_query=question,
        last_chats=last_chats,
    )
    max_domains, max_topics = _topic_caps_for_mode(mode_profile.name)
    work_state: dict[str, Any] = {"token_usage": {}, "request_id": request_id}
    graph_result = run_plan_context_graph(
        question,
        persona=persona,
        strategy=strategy,
        session=session,
        max_charts=mode_profile.max_charts,
        overview_chars=mode_profile.overview_chars,
        max_domains=max_domains,
        max_topics=max_topics,
        request_id=request_id,
        token_usage=work_state.get("token_usage"),
    )
    work_state["token_usage"] = graph_result.get("token_usage") or {}
    plan_dict = plan_to_public_dict(graph_result.get("plan") or {})
    if not plan_dict.get("domain") and graph_result.get("selected_domains"):
        plan_dict["domain"] = graph_result["selected_domains"][0]
    if not plan_dict.get("topics") and graph_result.get("selected_topics"):
        plan_dict["topics"] = list(graph_result.get("selected_topics") or [])
    applied_id = str(plan_dict.get("strategy_id") or strategy.get("id") or "")
    if applied_id and applied_id != strategy.get("id"):
        suggested_id = strategy.get("id")
        strategy = get_strategy(applied_id)
        strategy["selection"] = {
            "source": "llm_adapt",
            "intent": (persona.get("strategy") or {}).get("selection", {}).get("intent"),
            "persona": persona.get("name"),
            "suggested_strategy_id": suggested_id,
            "strategy_id": applied_id,
        }
        persona = attach_strategy(persona, strategy)
    if not plan_dict.get("template_id") and applied_id:
        plan_dict["template_id"] = str(get_strategy(applied_id).get("template_id") or "")
    strategy_selection = dict(strategy.get("selection") or {})
    strategy_detail = {
        "id": str(strategy.get("id") or applied_id or ""),
        "name": strategy.get("name") or strategy.get("label") or "",
        "template_id": strategy.get("template_id") or plan_dict.get("template_id") or "",
        "selection": strategy_selection,
        "reason": strategy_selection_reason(strategy),
    }
    public_persona_view = public_persona(persona)
    strategy_id = public_strategy(plan_dict.get("strategy_id") or strategy)
    if not plan_dict.get("strategy_id"):
        plan_dict["strategy_id"] = strategy_id
    record = {
        "status": "planned",
        "original_question": question,
        "plan": plan_dict,
        "persona": public_persona_view,
        "user_role": list(user_role or []),
        "user_profile": list(user_profile or []),
        "agent_mode": mode_profile.name,
        "model": {"file": model_file_name, "dir": model_location},
        "request_id": request_id,
        "selected_domains": list(graph_result.get("selected_domains") or []),
        "selected_topics": list(graph_result.get("selected_topics") or []),
        "selected_tables": list(graph_result.get("selected_tables") or []),
        "strategy_selection": strategy_selection,
        "plan_activity_trace": list(graph_result.get("activity_trace") or []),
    }
    save_plan(thread_id, chat_seq_id, record)
    logger.info(
        "Stored dashboard plan thread=%s seq=%s persona=%s strategy=%s template=%s charts=%s topics=%s",
        thread_id,
        chat_seq_id,
        public_persona_view.get("name"),
        strategy_id,
        plan_dict.get("template_id"),
        len(plan_dict.get("charts") or []),
        plan_dict.get("topics"),
    )
    return {
        "phase": "plan",
        "original_question": question,
        "dashboardid": thread_id,
        "dashboard_sequence_id": str(chat_seq_id or "1"),
        "persona": public_persona_view,
        "strategy": strategy_id,
        "strategy_detail": strategy_detail,
        "plan": plan_dict,
        "message": PLAN_READY_MESSAGE,
        "token_usage": work_state.get("token_usage") or {},
        "mode": mode_to_public_dict(mode_profile),
        "asked_questions": [
            str(chart.get("question") or "").strip()
            for chart in (plan_dict.get("charts") or [])
            if str(chart.get("question") or "").strip()
        ],
        "final_answer": "",
        "dashboard": {},
        "sub_questions": [],
        "investigation_steps": [
            {
                "step": index,
                "question": chart.get("question") or "",
                "kind": "planned_chart",
                "analysis": chart.get("purpose") or "",
            }
            for index, chart in enumerate(plan_dict.get("charts") or [], start=1)
        ],
        "attempt_count": 0,
        "selected_domains": list(graph_result.get("selected_domains") or []),
        "selected_topics": list(graph_result.get("selected_topics") or []),
        "selected_tables": list(graph_result.get("selected_tables") or []),
        "plan_activity_trace": list(graph_result.get("activity_trace") or []),
    }


def stored_plan_or_raise(thread_id: str, chat_seq_id: Any) -> dict[str, Any]:
    record = load_plan(thread_id, chat_seq_id)
    if not record or not isinstance(record.get("plan"), dict):
        raise ValueError(
            "No stored plan for this dashboardid and dashboard_sequence_id. "
            "Ask a question first, then send input \"execute plan\"."
        )
    return record
