"""Agent-only InstantBI SQL turn graph with domain / rephrase branching.

Interactive chat keeps using main_graph + sql_generator_graph unchanged.
Dashboard agent facets enter here so seeded topics skip redundant LLM steps.
"""
from __future__ import annotations

import logging
from typing import Any, Dict, List, Optional, TypedDict

from langgraph.graph import END, START, StateGraph

from helicalbi.sql_agent.modes import DEFAULT_MODE, resolve_mode_profile

logger = logging.getLogger(__name__)


class AgentSqlTurnState(TypedDict, total=False):
    """Working state for one agent InstantBI SQL generation."""

    model_state: Dict[str, Any]
    session: Dict[str, Any]
    agent_mode: str
    selected_domains: List[Any]
    selected_topics: List[Any]
    has_seeded_topics: bool
    skip_intent_rephrase: bool
    use_cube_info_flow: bool
    thread_id: str
    chat_seq_id: Any
    request_id: Optional[str]
    branch: str


def _as_list(value: Any) -> List[Any]:
    if value is None:
        return []
    if isinstance(value, list):
        return [item for item in value if item is not None and str(item).strip() != ""]
    text = str(value).strip()
    return [text] if text else []


def has_agent_topics(
    selected_topics: Optional[List[Any]] = None,
    *,
    model_state: Optional[Dict[str, Any]] = None,
) -> bool:
    """True when the planner (or cube prep) already identified topics."""
    if _as_list(selected_topics):
        return True
    state = model_state or {}
    if state.get("got_domain") and _as_list(state.get("topics")):
        return True
    return False


def route_after_prepare(state: AgentSqlTurnState) -> str:
    return "seed_domain" if state.get("has_seeded_topics") else "main_graph"


def route_after_seed(state: AgentSqlTurnState) -> str:
    if state.get("skip_intent_rephrase"):
        return "sql_generator"
    return "rephrase"


def prepare_node(state: AgentSqlTurnState) -> Dict[str, Any]:
    from helicalbi.controller.helpers import ensure_not_aborted

    ensure_not_aborted(state.get("request_id"))
    profile = resolve_mode_profile(state.get("agent_mode") or DEFAULT_MODE)
    topics = _as_list(state.get("selected_topics"))
    model_state = dict(state.get("model_state") or {})
    seeded = has_agent_topics(topics, model_state=model_state)
    # Fast/balanced skip rephrase only when topics were seeded by the agent.
    skip_rephrase = bool(profile.skip_intent_rephrase and seeded and topics)
    logger.info(
        "Agent SQL prepare mode=%s seeded_topics=%s skip_rephrase=%s",
        profile.name,
        seeded,
        skip_rephrase,
    )
    return {
        "has_seeded_topics": seeded and bool(topics),
        "skip_intent_rephrase": skip_rephrase,
        "use_cube_info_flow": bool((state.get("session") or {}).get("use_cube_info_flow")),
        "branch": "seed" if (seeded and topics) else "main_graph",
    }


def seed_domain_node(state: AgentSqlTurnState) -> Dict[str, Any]:
    """Seed domain/topics from agent RAG; skip FindDomainAndTopics LLM."""
    from helicalbi.sql_agent.instantbi_turn import _apply_cube_info_prepared

    model_state = dict(state.get("model_state") or {})
    session = state.get("session") or {}
    topics = _as_list(state.get("selected_topics"))
    domains = _as_list(state.get("selected_domains"))
    prepared = session.get("cube_info_prepared") or {}

    if session.get("use_cube_info_flow") and prepared:
        model_state = _apply_cube_info_prepared(model_state, prepared)

    query = model_state.get("query") or ""
    model_state["topics"] = topics or _as_list(model_state.get("topics"))
    model_state["domain"] = domains or _as_list(model_state.get("domain"))
    model_state["got_domain"] = True
    model_state["action"] = model_state.get("action") or "none"
    model_state["sql_query"] = query
    model_state["viz_query"] = query

    # Narrow topic_mappings to agent-selected topics when present.
    selected = {str(t) for t in model_state["topics"]}
    mappings = model_state.get("topic_mappings") or prepared.get("topic_mappings") or []
    if selected and isinstance(mappings, list):
        narrowed = [
            entry
            for entry in mappings
            if isinstance(entry, dict) and entry.get("topic_name") in selected
        ]
        if narrowed:
            model_state["topic_mappings"] = narrowed

    logger.info(
        "Agent SQL seeded domains=%s topics=%s",
        model_state.get("domain"),
        model_state.get("topics"),
    )
    return {"model_state": model_state, "branch": "seed"}


def main_graph_node(state: AgentSqlTurnState) -> Dict[str, Any]:
    """Full InstantBI main_graph (rephrase + domain) when agent has no topics yet."""
    from helicalbi.controller.app_context import app
    from helicalbi.controller.helpers import ensure_not_aborted, graph_invoke_config
    from helicalbi.core.flows.CubeInfoFlow import CubeInfoFlow
    from helicalbi.sql_agent.instantbi_turn import _apply_cube_info_prepared

    ensure_not_aborted(state.get("request_id"))
    model_state = dict(state.get("model_state") or {})
    session = state.get("session") or {}
    config = graph_invoke_config(state.get("thread_id"), state.get("chat_seq_id"))
    prepared = session.get("cube_info_prepared") or {}

    if session.get("use_cube_info_flow"):
        model_state = CubeInfoFlow().process_flow(model_state)
        model_state = _apply_cube_info_prepared(model_state, prepared)

    ensure_not_aborted(state.get("request_id"))
    model_state = app().main_graph.invoke(model_state, config)
    return {"model_state": model_state, "branch": "main_graph"}


def rephrase_node(state: AgentSqlTurnState) -> Dict[str, Any]:
    """Run UpdateIntentRephrase only (domain already seeded)."""
    from helicalbi.controller.helpers import ensure_not_aborted
    from helicalbi.core.flows.UpdateIntentRephrase import UpdateIntentRephrase

    ensure_not_aborted(state.get("request_id"))
    model_state = dict(state.get("model_state") or {})
    model_state = UpdateIntentRephrase().process_flow(model_state)
    return {"model_state": model_state, "branch": "rephrase"}


def sql_generator_node(state: AgentSqlTurnState) -> Dict[str, Any]:
    """Existing InstantBI SQL generator graphs (cube_info or legacy)."""
    from GraphBuilderManger import cube_info_sql_generator_graph, sql_generator_graph
    from helicalbi.controller.helpers import ensure_not_aborted, graph_invoke_config

    ensure_not_aborted(state.get("request_id"))
    model_state = dict(state.get("model_state") or {})
    config = graph_invoke_config(state.get("thread_id"), state.get("chat_seq_id"))
    if state.get("use_cube_info_flow") or (state.get("session") or {}).get("use_cube_info_flow"):
        model_state = cube_info_sql_generator_graph.invoke(model_state, config)
    else:
        model_state = sql_generator_graph.invoke(model_state, config)
    return {"model_state": model_state, "branch": "sql_generator"}


def build_agent_sql_graph():
    """Compile the agent InstantBI SQL turn graph."""
    workflow = StateGraph(AgentSqlTurnState)
    workflow.add_node("prepare", prepare_node)
    workflow.add_node("seed_domain", seed_domain_node)
    workflow.add_node("main_graph", main_graph_node)
    workflow.add_node("rephrase", rephrase_node)
    workflow.add_node("sql_generator", sql_generator_node)

    workflow.add_edge(START, "prepare")
    workflow.add_conditional_edges(
        "prepare",
        route_after_prepare,
        {"seed_domain": "seed_domain", "main_graph": "main_graph"},
    )
    workflow.add_conditional_edges(
        "seed_domain",
        route_after_seed,
        {"rephrase": "rephrase", "sql_generator": "sql_generator"},
    )
    workflow.add_edge("main_graph", "sql_generator")
    workflow.add_edge("rephrase", "sql_generator")
    workflow.add_edge("sql_generator", END)
    return workflow.compile()


_agent_sql_graph = None


def get_agent_sql_graph():
    global _agent_sql_graph
    if _agent_sql_graph is None:
        _agent_sql_graph = build_agent_sql_graph()
    return _agent_sql_graph


def run_agent_sql_turn(
    model_state: Dict[str, Any],
    session: Dict[str, Any],
    *,
    thread_id: str,
    chat_seq_id: Any,
    request_id: Optional[str] = None,
    agent_mode: str = DEFAULT_MODE,
    selected_domains: Optional[List[Any]] = None,
    selected_topics: Optional[List[Any]] = None,
    compiled_graph=None,
) -> Dict[str, Any]:
    """Invoke the agent SQL graph; returns InstantBI model state with SQL."""
    initial: AgentSqlTurnState = {
        "model_state": model_state,
        "session": session,
        "agent_mode": agent_mode or DEFAULT_MODE,
        "selected_domains": _as_list(selected_domains),
        "selected_topics": _as_list(selected_topics),
        "thread_id": str(thread_id or ""),
        "chat_seq_id": chat_seq_id,
        "request_id": request_id,
    }
    graph = compiled_graph or get_agent_sql_graph()
    result = graph.invoke(initial)
    return dict(result.get("model_state") or model_state)


def run_agent_viz(
    model_state: Dict[str, Any],
    *,
    agent_mode: str = DEFAULT_MODE,
    thread_id: str = "",
    chat_seq_id: Any = "1",
    request_id: Optional[str] = None,
) -> Dict[str, Any]:
    """Viz for agent path: fast skips polish LLM; other modes use full viz_graph."""
    from helicalbi.controller.app_context import app
    from helicalbi.controller.helpers import ensure_not_aborted, graph_invoke_config
    from helicalbi.core.vizflow.VizFlowGraph import _route_after_viz_model_filler
    from helicalbi.core.vizflow.Fallback import Fallback
    from helicalbi.core.vizflow.VizModelFiller import VizModelFiller
    from langgraph.graph import END, StateGraph
    from helicalbi.model.ModelState import ModelState

    ensure_not_aborted(request_id)
    profile = resolve_mode_profile(agent_mode)
    config = graph_invoke_config(thread_id, chat_seq_id)

    if profile.skip_viz_polish:
        # Deterministic filler only; still route other/custom to Fallback without polish.
        workflow = StateGraph(ModelState)
        workflow.add_node("VizModelFiller", VizModelFiller().process_flow)
        workflow.add_node("Fallback", Fallback().process_flow)
        workflow.set_entry_point("VizModelFiller")

        def _route_fast(state):
            decision = _route_after_viz_model_filler(state)
            if decision == "fallback":
                return "fallback"
            return "end"

        workflow.add_conditional_edges(
            "VizModelFiller",
            _route_fast,
            {"fallback": "Fallback", "end": END},
        )
        workflow.add_edge("Fallback", END)
        graph = workflow.compile()
        logger.info("Agent viz fast path: VizModelFiller only mode=%s", profile.name)
        return graph.invoke(model_state, config)

    logger.info("Agent viz full path mode=%s", profile.name)
    return app().viz_graph.invoke(model_state, config)
