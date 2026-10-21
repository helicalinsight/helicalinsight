"""InstantBI SQL / dashboard agent package.

Public surface (lazy for heavy graphs):

* :data:`AgentState` / :func:`initial_agent_state` — LangGraph state bag
* :func:`run_dashboard_agent` / :func:`build_dashboard_agent` — execute path
* :mod:`helicalbi.sql_agent.investigation` — plan-then-execute facade
* :mod:`helicalbi.sql_agent.strategy_tree` — strategy engine facade
* :mod:`helicalbi.sql_agent.constants` — shared literals (no magic numbers in logic)
"""

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
