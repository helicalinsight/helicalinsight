from __future__ import annotations

import json
import logging
from typing import Any, Dict

from langchain_core.messages import HumanMessage, SystemMessage

from helicalbi.common.LlmInvokeHelper import invoke_llm
from helicalbi.common.configuration import llm as default_llm
from helicalbi.sql_agent.config import PLAN_EXECUTION_RULES, TOOL_AGENT_PROMPT
from helicalbi.sql_agent.modes import DEFAULT_MODE, profile_from_state, truncate_text
from helicalbi.sql_agent.nodes.decomposer import findings_for_state
from helicalbi.sql_agent.state import AgentState
from helicalbi.sql_agent.token_budget import charts_complete, trim_planner_history
from helicalbi.sql_agent.tools.tools import planner_tools

logger = logging.getLogger(__name__)


def bootstrap_planner_messages(state: AgentState) -> list:
    profile = profile_from_state(state)
    overview = truncate_text(
        state.get("semantic_overview") or state.get("schema_overview") or "",
        profile.overview_chars,
    )
    plan = state.get("investigation_plan") or {}
    plan_text = json.dumps(plan, indent=2, default=str) if plan else "(none — decompose as needed)"
    return [
        SystemMessage(
            content=TOOL_AGENT_PROMPT.format(
                mode_rules=profile.mode_rules,
                original_question=state.get("original_question") or "",
                semantic_overview=overview,
                investigation_plan=plan_text,
                plan_rules=PLAN_EXECUTION_RULES if plan else "",
                collected_data=findings_for_state(state),
                max_charts=int(state.get("max_sub_questions") or profile.max_charts),
                max_tool_loops=int(state.get("max_tool_loops") or profile.max_tool_loops),
            )
        ),
        HumanMessage(content=state.get("original_question") or ""),
    ]


def planner_node(state: AgentState) -> Dict[str, Any]:
    """LLM step: choose the next InstantBI tool call or stop."""
    profile = profile_from_state(state)
    max_loops = int(state.get("max_tool_loops") or profile.max_tool_loops)
    loop = int(state.get("tool_loop_count") or 0)
    mode_name = state.get("agent_mode") or DEFAULT_MODE

    if charts_complete(state):
        logger.info("Planner stopping: chart cap reached mode=%s", mode_name)
        return {"is_complete": True, "tool_loop_count": loop}

    if loop >= max_loops:
        logger.info(
            "Planner stopping: tool_loop_count=%s mode=%s max=%s",
            loop,
            mode_name,
            max_loops,
        )
        return {"is_complete": True, "tool_loop_count": loop}

    history = list(state.get("messages") or [])
    seeded = False
    if not history:
        history = bootstrap_planner_messages(state)
        seeded = True
    else:
        history = trim_planner_history(
            history,
            keep_tool_rounds=profile.history_tool_rounds,
        )

    bound = default_llm.bind_tools(planner_tools(mode_name))
    ai_message, _ = invoke_llm(bound, history, state=state)
    logger.info(
        "Planner mode=%s tool_calls=%s",
        mode_name,
        [
            call.get("name") if isinstance(call, dict) else getattr(call, "name", "")
            for call in (getattr(ai_message, "tool_calls", None) or [])
        ],
    )
    # Persist only the new AI message (seeded history already in state from bootstrap).
    outgoing = [*history, ai_message] if seeded else [ai_message]
    return {"messages": outgoing, "tool_loop_count": loop + 1}
