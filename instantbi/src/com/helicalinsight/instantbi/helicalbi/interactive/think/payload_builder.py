"""Assemble the think-mode response payload."""
from __future__ import annotations

from typing import Any, Mapping, Optional, Sequence

from helicalbi.interactive.modes import INTERACTIVE_MODE_THINK
from helicalbi.interactive.think.constants import (
    MSG_INVESTIGATION_COMPLETE,
    RESPONSE_PHASE_EXECUTE,
)
from helicalbi.sql_agent.activity_details import build_llm_activity_details


class ThinkResultPayloadBuilder:
    """Build the client-facing think execute payload from stage outputs."""

    def build(
        self,
        *,
        question: str,
        asked_questions: Sequence[str],
        question_history: Sequence[Mapping[str, Any]],
        cited_indexes: Sequence[int],
        opening_insight: str,
        final_answer: str,
        plan: Mapping[str, Any],
        plan_payload: Mapping[str, Any],
        result: Mapping[str, Any],
        token_usage: Mapping[str, Any],
        include_details: bool,
    ) -> dict[str, Any]:
        payload: dict[str, Any] = {
            "mode": INTERACTIVE_MODE_THINK,
            "phase": RESPONSE_PHASE_EXECUTE,
            "original_question": result.get("original_question") or question,
            "asked_questions": list(asked_questions),
            "question_history": list(question_history),
            "cited_question_indexes": list(cited_indexes),
            # Text-only: do not hydrate client charts from chat_responses.
            "chat_responses": [],
            "chat_response": {},
            "opening_insight": opening_insight,
            "final_answer": final_answer,
            "plan": plan,
            "persona": plan_payload.get("persona") or result.get("persona") or {},
            "strategy": plan_payload.get("strategy") or result.get("strategy") or "",
            "token_usage": dict(token_usage),
            "investigation_steps": result.get("investigation_steps")
            or plan_payload.get("investigation_steps")
            or [],
            "attempt_count": int(
                result.get("attempt_count")
                if result.get("attempt_count") is not None
                else (result.get("tool_loop_count") or 0)
            ),
            "message": MSG_INVESTIGATION_COMPLETE,
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
        return payload


class ThinkErrorPayloadBuilder:
    """Build aborted / error payloads shared by JSON and SSE turns."""

    def build(
        self,
        *,
        error: str,
        aborted: bool = False,
        asked_questions: Optional[Sequence[Any]] = None,
        question_history: Optional[Sequence[Any]] = None,
        final_answer: str = "",
    ) -> dict[str, Any]:
        from helicalbi.interactive.think.constants import RESPONSE_PHASE_PLAN

        payload: dict[str, Any] = {
            "mode": INTERACTIVE_MODE_THINK,
            "phase": RESPONSE_PHASE_PLAN,
            "error": error,
            "asked_questions": list(asked_questions or []),
            "question_history": list(question_history or []),
            "chat_responses": [],
            "final_answer": final_answer,
        }
        if aborted:
            payload["aborted"] = True
        return payload
