"""LangGraph: tool-calling planner → InstantBI tools → synthesize → dashboard."""
from __future__ import annotations

from typing import Any, Optional

from langchain_core.messages import AIMessage
from langgraph.graph import END, START, StateGraph
from langgraph.prebuilt import ToolNode

from helicalbi.sql_agent.config import DEFAULT_DASHBOARD_SUB_QUESTIONS, RECURSION_LIMIT
from helicalbi.sql_agent.database.schema_indexer import set_indexer
from helicalbi.sql_agent.database.semantic_indexer import set_semantic_indexer
from helicalbi.sql_agent.instantbi_turn import load_model_session
from helicalbi.sql_agent.modes import (
    DEFAULT_MODE,
    mode_to_public_dict,
    recursion_limit_for_loops,
    resolve_mode_profile,
)
from helicalbi.sql_agent.nodes.apply_patches import apply_tool_patches
from helicalbi.sql_agent.nodes.dashboard import dashboard_node
from helicalbi.sql_agent.nodes.execute_plan import execute_plan_node, has_planned_charts
from helicalbi.sql_agent.nodes.planner import bootstrap_planner_messages, planner_node
from helicalbi.sql_agent.nodes.synthesizer import synthesizer_node
from helicalbi.sql_agent.state import AgentState, initial_agent_state
from helicalbi.sql_agent.tools import dashboard_tools
from helicalbi.sql_agent.token_budget import charts_complete


def gathering_should_stop(state: AgentState) -> bool:
    if state.get("is_complete") or charts_complete(state):
        return True
    max_loops = int(state.get("max_tool_loops") or 0)
    return bool(max_loops and int(state.get("tool_loop_count") or 0) >= max_loops)


def route_planner(state: AgentState) -> str:
    if gathering_should_stop(state):
        return "synthesizer"
    messages = state.get("messages") or []
    last = messages[-1] if messages else None
    if isinstance(last, AIMessage) and last.tool_calls:
        return "tools"
    return "synthesizer"


def route_after_tools(state: AgentState) -> str:
    if gathering_should_stop(state):
        return "synthesizer"
    return "planner"


def route_start(state: AgentState) -> str:
    """Stored investigation plans run chart-by-chart; free-form questions use ReAct."""
    return "execute_plan" if has_planned_charts(state) else "planner"


def _plan_scope(plan: dict[str, Any]) -> tuple[list[str], list[str]]:
    topics = [str(topic).strip() for topic in (plan.get("topics") or []) if str(topic).strip()]
    domain = plan.get("domain")
    if isinstance(domain, str) and domain.strip():
        domains = [domain.strip()]
    elif isinstance(domain, list):
        domains = [str(item).strip() for item in domain if str(item).strip()]
    else:
        domains = []
    return topics, domains


def build_dashboard_agent(checkpointer=None):
    workflow = StateGraph(AgentState)
    workflow.add_node("execute_plan", execute_plan_node)
    workflow.add_node("planner", planner_node)
    workflow.add_node("tools", ToolNode(dashboard_tools()))
    workflow.add_node("apply_patches", apply_tool_patches)
    workflow.add_node("synthesizer", synthesizer_node)
    workflow.add_node("dashboard", dashboard_node)

    workflow.add_conditional_edges(
        START,
        route_start,
        {"execute_plan": "execute_plan", "planner": "planner"},
    )
    workflow.add_edge("execute_plan", "synthesizer")
    workflow.add_conditional_edges(
        "planner",
        route_planner,
        {"tools": "tools", "synthesizer": "synthesizer"},
    )
    workflow.add_edge("tools", "apply_patches")
    workflow.add_conditional_edges(
        "apply_patches",
        route_after_tools,
        {"planner": "planner", "synthesizer": "synthesizer"},
    )
    workflow.add_edge("synthesizer", "dashboard")
    workflow.add_edge("dashboard", END)

    if checkpointer is not None:
        return workflow.compile(checkpointer=checkpointer)
    return workflow.compile()


_dashboard_agent = None


def get_dashboard_agent():
    global _dashboard_agent
    if _dashboard_agent is None:
        _dashboard_agent = build_dashboard_agent()
    return _dashboard_agent


def run_dashboard_agent(
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
    compiled_agent=None,
    recursion_limit: Optional[int] = None,
    investigation_plan: Optional[dict] = None,
    persona: Optional[dict] = None,
    user_role: Optional[list] = None,
    user_profile: Optional[list] = None,
) -> AgentState:
    """Load HI metadata, run the tool-calling dashboard agent, layout a dashboard."""
    profile = resolve_mode_profile(
        agent_mode,
        config_max_charts=max_sub_questions,
    )
    plan = investigation_plan if isinstance(investigation_plan, dict) else {}
    plan_charts = [chart for chart in (plan.get("charts") or []) if isinstance(chart, dict)]
    max_charts = profile.max_charts
    if plan_charts:
        max_charts = min(max_charts, max(1, len(plan_charts)))
    session = load_model_session(
        session_cookie=session_cookie,
        username=username,
        model_file_name=model_file_name,
        model_location=model_location,
        thread_id=thread_id,
        user_query=question,
        last_chats=last_chats,
    )
    indexer = session.pop("indexer", None)
    semantic_indexer = session.pop("semantic_indexer", None)
    catalog_id = f"dashboard:{thread_id}"
    if indexer is not None:
        set_indexer(indexer, catalog_id)
    if semantic_indexer is not None:
        set_semantic_indexer(semantic_indexer, catalog_id)

    dialect = (session.get("base_state") or {}).get("dialect") or "postgres"
    state = initial_agent_state(
        question,
        dialect=dialect,
        catalog_id=catalog_id,
        schema_top_k=profile.schema_top_k,
        max_sub_questions=max_charts,
        max_tool_loops=profile.max_tool_loops,
        agent_mode=profile.name,
        use_llm_synthesizer=profile.use_llm_synthesizer,
        session_cookie=session_cookie,
        md_location=session.get("md_location"),
        md_file_name=session.get("md_file_name"),
        schema_overview=session.get("schema_overview") or "",
        semantic_overview=session.get("semantic_overview") or "",
        session_context=session,
        thread_id=thread_id,
        chat_seq_id=str(chat_seq_id or "1"),
        request_id=request_id,
        username=username,
        build_dashboard=True,
        investigation_plan=plan,
        persona=persona or {},
        user_role=user_role or [],
        user_profile=user_profile or [],
    )
    plan_topics, plan_domains = _plan_scope(plan)
    if plan_topics:
        state["selected_topics"] = plan_topics
    if plan_domains:
        state["selected_domains"] = plan_domains
    state["mode"] = mode_to_public_dict(profile)
    if not has_planned_charts(state):
        state["messages"] = bootstrap_planner_messages(state)
    graph = compiled_agent or get_dashboard_agent()
    limit = recursion_limit if recursion_limit is not None else profile.recursion_limit
    if limit is None:
        limit = recursion_limit_for_loops(profile.max_tool_loops) or RECURSION_LIMIT
    return graph.invoke(state, {"recursion_limit": limit})
