"""POST /agent-dashboard — decompose a question, run InstantBI per chart, layout a dashboard."""
from __future__ import annotations

import logging
import time
import traceback
from typing import Any

from flask import request

from helicalbi.audit.llm_usage_audit import audit_llm_usage_async
from helicalbi.common import app_config
from helicalbi.common.app_config import is_debug
from helicalbi.common.auth import bind_request_identity, resolve_role_profile
from helicalbi.common.LlmInvokeHelper import set_total_time_consumed
from helicalbi.common.RequestCancellation import request_cancellation
from helicalbi.controller.helpers import (
    RequestAborted,
    extract_token_usage_dict,
    json_response,
    log_endpoint_input,
    resolve_audit_status_from_response,
    resolve_request_id,
)

logger = logging.getLogger(__name__)


def _public_steps(collected: list[dict[str, Any]] | None) -> list[dict[str, Any]]:
    steps = []
    for step in collected or []:
        chat_response = step.get("chat_response") if isinstance(step.get("chat_response"), dict) else {}
        steps.append(
            {
                "sub_question": step.get("sub_question") or "",
                "analysis": step.get("analysis") or "",
                "chat_seq_id": step.get("chat_seq_id") or "",
                "chat_response": chat_response,
                "report_model": step.get("report_model") or chat_response.get("report_model") or {},
            }
        )
    return steps


def _execution_payload(result: dict[str, Any], *, user_query: str, thread_id: str, agent_mode: Any) -> dict[str, Any]:
    from helicalbi.sql_agent.strategy_tree import public_persona, public_strategy

    asked_questions = [
        str(q).strip()
        for q in (result.get("asked_questions") or [])
        if str(q or "").strip()
    ]
    if not asked_questions:
        asked_questions = [
            str(step.get("sub_question") or "").strip()
            for step in (result.get("collected_data") or [])
            if str(step.get("sub_question") or "").strip()
        ]
    attempt_count = int(
        result.get("attempt_count")
        if result.get("attempt_count") is not None
        else (result.get("tool_loop_count") or 0)
    )
    investigation_steps = result.get("investigation_steps") or []
    if not investigation_steps:
        from helicalbi.sql_agent.nodes.run_summary import build_run_summary

        investigation_steps = build_run_summary(result).get("investigation_steps") or []
    plan = result.get("investigation_plan") or {}
    strategy_id = public_strategy(
        plan.get("strategy_id")
        or (result.get("persona") or {}).get("strategy_id")
        or result.get("strategy")
    )
    payload = {
        "phase": "execute",
        "original_question": result.get("original_question") or user_query,
        "final_answer": result.get("final_answer") or "",
        "dashboardid": thread_id,
        "mode": result.get("mode") or {"name": result.get("agent_mode") or agent_mode},
        "persona": public_persona(result.get("persona") or {}),
        "strategy": strategy_id,
        "plan": plan,
        "asked_questions": asked_questions,
        "attempt_count": attempt_count,
        "investigation_steps": investigation_steps,
        "sub_questions": _public_steps(result.get("collected_data")),
        "dashboard": result.get("dashboard") or {},
        "token_usage": result.get("token_usage") or {},
    }
    if (result.get("dashboard") or {}).get("error") and not result.get("final_answer"):
        payload["error"] = result["dashboard"]["error"]
    return payload


def register(flask_app) -> None:
    @flask_app.route("/agent-dashboard", methods=["POST"])
    def agent_dashboard():
        data = request.get_json()
        log_endpoint_input("/agent-dashboard", data)
        user_input = data["input"] if data else {}
        user_query = user_input.get("inputString") or user_input.get("query") or ""
        session_cookie, username, user_id, _org_id = bind_request_identity(data, user_input)
        model = user_input.get("model") or {}
        model_file_name = model.get("file") or ""
        location = model.get("dir") or ""
        thread_id = str(user_input.get("dashboardid") or "")
        chat_seq_id = (
            user_input.get("dashboard_sequence_id")
            or user_input.get("dashboard_seq_id")
            or "1"
        )
        request_id = resolve_request_id(data, user_input)
        role_profile = resolve_role_profile(data, user_input)
        max_sub_questions = int(app_config.dashboard_max_sub_questions)
        agent_mode = (
            user_input.get("mode")
            or user_input.get("agent_mode")
            or app_config.dashboard_default_mode
        )
        to_send: dict[str, Any] = {}
        request_status = "SUCCESS"
        error_message: str | None = None
        request_started = time.perf_counter()
        result: dict[str, Any] = {}

        if request_id:
            request_cancellation.register(request_id)

        try:
            if not user_query.strip():
                raise ValueError("input.inputString is required")
            if not model_file_name or not location:
                raise ValueError("input.model.file and input.model.dir are required")
            if not thread_id:
                raise ValueError("input.dashboardid is required")

            from helicalbi.sql_agent.dashboard_graph import run_dashboard_agent
            from helicalbi.sql_agent.investigation import (
                create_and_store_plan,
                is_execute_plan_request,
                stored_plan_or_raise,
            )
            from helicalbi.sql_agent.plan_memory import save_plan

            logger.info(
                "Agent-dashboard request user=%s thread=%s mode=%s query=%s",
                username,
                thread_id,
                agent_mode,
                user_query,
            )
            if is_execute_plan_request(user_query, user_input):
                stored = stored_plan_or_raise(thread_id, chat_seq_id)
                question = stored.get("original_question") or user_query
                result = run_dashboard_agent(
                    question,
                    session_cookie=session_cookie,
                    username=username,
                    model_file_name=model_file_name,
                    model_location=location,
                    thread_id=thread_id,
                    chat_seq_id=chat_seq_id,
                    last_chats=user_input.get("last_chats"),
                    request_id=request_id,
                    max_sub_questions=max_sub_questions,
                    agent_mode=str(stored.get("agent_mode") or agent_mode or ""),
                    investigation_plan=stored.get("plan") or {},
                    persona=stored.get("persona") or {},
                    user_role=stored.get("user_role") or role_profile.get("userRole") or [],
                    user_profile=stored.get("user_profile") or role_profile.get("userProfile") or [],
                )
                set_total_time_consumed(result, time.perf_counter() - request_started)
                stored["status"] = "executed"
                save_plan(thread_id, chat_seq_id, stored)
                to_send = _execution_payload(
                    result,
                    user_query=question,
                    thread_id=thread_id,
                    agent_mode=agent_mode,
                )
                to_send["dashboard_sequence_id"] = str(chat_seq_id)
            else:
                result = create_and_store_plan(
                    user_query,
                    session_cookie=session_cookie,
                    username=username,
                    model_file_name=model_file_name,
                    model_location=location,
                    thread_id=thread_id,
                    chat_seq_id=chat_seq_id,
                    last_chats=user_input.get("last_chats"),
                    request_id=request_id,
                    max_sub_questions=max_sub_questions,
                    agent_mode=str(agent_mode or ""),
                    user_role=role_profile.get("userRole") or user_input.get("userRole") or [],
                    user_profile=role_profile.get("userProfile") or user_input.get("userProfile") or [],
                    persona_hint=user_input.get("persona") or user_input.get("user_persona"),
                    strategy_hint=user_input.get("strategy") or user_input.get("strategy_id"),
                )
                set_total_time_consumed(result, time.perf_counter() - request_started)
                to_send = dict(result)
            logger.info(
                "Agent-dashboard completed user=%s thread=%s phase=%s",
                username,
                thread_id,
                to_send.get("phase") or "unknown",
            )
        except RequestAborted:
            logger.info("Agent-dashboard aborted requestId=%s", request_id)
            request_status = "ABORTED"
            error_message = "Request has been cancelled."
            to_send["error"] = error_message
            to_send["aborted"] = True
            to_send["messages"] = []
        except Exception as exc:
            logger.exception("Error while processing agent-dashboard request")
            request_status = "ERROR"
            error_message = str(exc)
            to_send["error"] = error_message
            to_send["messages"] = []
            if is_debug():
                to_send["stack"] = traceback.format_exc()
        finally:
            if request_id:
                request_cancellation.clear(request_id)
            request_status, error_message = resolve_audit_status_from_response(
                to_send,
                request_status,
                error_message,
            )
            audit_llm_usage_async(
                endpoint="/agent-dashboard",
                user_id=user_id,
                session_cookie=session_cookie,
                user_query=user_query,
                token_usage=extract_token_usage_dict(to_send),
                request_status=request_status,
                error_message=error_message,
                chat_id=str(thread_id) if thread_id else None,
                chat_seq_id=str(chat_seq_id) if chat_seq_id is not None else None,
            )

        return json_response(to_send)
