"""SQL generate / validate / execute tools."""
from __future__ import annotations

import logging
from typing import Annotated, Any

from langchain_core.tools import tool
from langgraph.prebuilt import InjectedState

from helicalbi.sql.SqlSanitizer import strip_sql_markdown
from helicalbi.sql_agent.nodes.run_summary import append_question
from helicalbi.sql_agent.nodes.validator import validate_sql_against_catalog
from helicalbi.sql_agent.tools.context import AgentToolContext

logger = logging.getLogger(__name__)


class SqlTools:
    """Generate, validate, and execute InstantBI SQL."""

    def generate(self, question: str, state: dict[str, Any]) -> dict[str, Any]:
        from helicalbi.sql_agent.instantbi_turn import generate_sql_for_question

        ctx = AgentToolContext(state)
        session = ctx.session
        seq = ctx.next_seq()
        result = generate_sql_for_question(
            question,
            session,
            thread_id=ctx.thread_id,
            chat_seq_id=seq,
            request_id=ctx.request_id,
            agent_context={
                "agent_mode": state.get("agent_mode") or "",
                "selected_domains": state.get("selected_domains") or [],
                "selected_topics": state.get("selected_topics") or [],
                "current_semantic_context": state.get("current_semantic_context") or "",
            },
        )
        sql = strip_sql_markdown(result.get("sql") or "").strip()
        session["_last_sql_state"] = result
        session["_last_sql_seq"] = seq
        error = ctx.sql_error(result)
        return {
            "ok": not error and bool(sql),
            "sql": sql,
            "error": error,
            "state_patch": {
                "current_sub_question": question,
                "asked_questions": append_question(state.get("asked_questions"), question),
                "generated_sql": sql or None,
                "sql_error": error or None,
                "current_chat_seq_id": seq,
                "session_context": session,
                "token_usage": ctx.merge_usage(result),
            },
        }

    def validate(self, sql: str, state: dict[str, Any]) -> dict[str, Any]:
        ctx = AgentToolContext(state)
        cleaned = strip_sql_markdown(sql or "").strip()
        error = validate_sql_against_catalog(
            cleaned,
            ctx.catalog,
            dialect=ctx.dialect,
            metadata=ctx.metadata,
        )
        return {
            "ok": error is None,
            "error": error,
            "state_patch": {
                "generated_sql": cleaned or None,
                "sql_error": error,
            },
        }

    def execute(self, sql: str, state: dict[str, Any]) -> dict[str, Any]:
        from helicalbi.controller.helpers import turn_state_defaults
        from helicalbi.sql_agent.instantbi_turn import execute_sql_state

        ctx = AgentToolContext(state)
        session = ctx.session
        cleaned = strip_sql_markdown(sql or "").strip()
        error = validate_sql_against_catalog(
            cleaned,
            ctx.catalog,
            dialect=ctx.dialect,
            metadata=ctx.metadata,
        )
        retry = int(state.get("sql_retry_count") or 0)
        if error:
            logger.info("execute_query blocked by validator: %s", error)
            return {
                "ok": False,
                "error": error,
                "state_patch": {
                    "generated_sql": cleaned or None,
                    "sql_error": error,
                    "query_result": None,
                    "sql_retry_count": retry + 1,
                },
            }

        last = session.get("_last_sql_state")
        if isinstance(last, dict):
            work = dict(last)
            work["sql"] = cleaned
            work["skip"] = False
        else:
            base = dict(session.get("base_state") or {})
            work = {
                **base,
                **turn_state_defaults(),
                "sql": cleaned,
                "query": state.get("current_sub_question") or state.get("original_question") or "",
                "thread_id": ctx.thread_id,
            }

        result = execute_sql_state(work, request_id=ctx.request_id)
        session["_last_sql_state"] = result
        exec_error = ctx.sql_error(result)
        preview = ctx.preview_data(result)
        return {
            "ok": not exec_error,
            "row_preview": preview,
            "error": exec_error,
            "state_patch": {
                "generated_sql": cleaned,
                "query_result": exec_error or str(preview),
                "sql_error": exec_error or None,
                "sql_retry_count": retry + 1 if exec_error else 0,
                "session_context": session,
                "token_usage": ctx.merge_usage(result),
            },
        }


sql_tools = SqlTools()


@tool
def generate_sql(
    question: str,
    state: Annotated[dict, InjectedState],
) -> str:
    """Generate SQL for a question using InstantBI metadata and SQL graphs. Does not execute."""
    return AgentToolContext.dump(sql_tools.generate(question, state))


@tool
def validate_sql(
    sql: str,
    state: Annotated[dict, InjectedState],
) -> str:
    """Parse SQL with sqlglot and check table/column names against the catalog. Does not execute."""
    return AgentToolContext.dump(sql_tools.validate(sql, state))


@tool
def execute_query(
    sql: str,
    state: Annotated[dict, InjectedState],
) -> str:
    """Execute a SELECT via InstantBI executeQuery. Always validates first; never runs invalid SQL."""
    return AgentToolContext.dump(sql_tools.execute(sql, state))
