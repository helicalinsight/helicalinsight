"""InstantBI dashboard agent (LangGraph)."""

from helicalbi.sql_agent.state import AgentState, SubQuestionStep, initial_agent_state

__all__ = [
    "AgentState",
    "SubQuestionStep",
    "build_dashboard_agent",
    "get_dashboard_agent",
    "initial_agent_state",
    "run_dashboard_agent",
]


def __getattr__(name: str):
    if name in {"build_dashboard_agent", "get_dashboard_agent", "run_dashboard_agent"}:
        from helicalbi.sql_agent import dashboard_graph

        return getattr(dashboard_graph, name)
    raise AttributeError(f"module {__name__!r} has no attribute {name!r}")
