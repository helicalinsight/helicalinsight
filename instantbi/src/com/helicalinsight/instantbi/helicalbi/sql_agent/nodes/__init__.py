"""Public node exports (lazy — avoid circular imports with sql_agent.tools)."""
from __future__ import annotations

from typing import Any

__all__ = [
    "apply_tool_patches",
    "bootstrap_planner_messages",
    "build_investigation_plan",
    "build_run_summary",
    "dashboard_node",
    "execute_plan_node",
    "fallback_investigation_plan",
    "findings_for_prompt",
    "has_planned_charts",
    "planner_node",
    "run_plan_context_graph",
    "synthesizer_node",
    "validate_sql_against_catalog",
]

_EXPORTS = {
    "apply_tool_patches": ("helicalbi.sql_agent.nodes.apply_patches", "apply_tool_patches"),
    "bootstrap_planner_messages": ("helicalbi.sql_agent.nodes.planner", "bootstrap_planner_messages"),
    "build_investigation_plan": (
        "helicalbi.sql_agent.nodes.investigation_planner",
        "build_investigation_plan",
    ),
    "build_run_summary": ("helicalbi.sql_agent.nodes.run_summary", "build_run_summary"),
    "dashboard_node": ("helicalbi.sql_agent.nodes.dashboard", "dashboard_node"),
    "execute_plan_node": ("helicalbi.sql_agent.nodes.execute_plan", "execute_plan_node"),
    "fallback_investigation_plan": (
        "helicalbi.sql_agent.nodes.investigation_planner",
        "fallback_investigation_plan",
    ),
    "findings_for_prompt": ("helicalbi.sql_agent.nodes.decomposer", "findings_for_prompt"),
    "has_planned_charts": ("helicalbi.sql_agent.nodes.execute_plan", "has_planned_charts"),
    "planner_node": ("helicalbi.sql_agent.nodes.planner", "planner_node"),
    "run_plan_context_graph": ("helicalbi.sql_agent.nodes.plan_graph", "run_plan_context_graph"),
    "synthesizer_node": ("helicalbi.sql_agent.nodes.synthesizer", "synthesizer_node"),
    "validate_sql_against_catalog": (
        "helicalbi.sql_agent.nodes.validator",
        "validate_sql_against_catalog",
    ),
}


def __getattr__(name: str) -> Any:
    target = _EXPORTS.get(name)
    if target is None:
        raise AttributeError(f"module {__name__!r} has no attribute {name!r}")
    module_name, attr = target
    from importlib import import_module

    value = getattr(import_module(module_name), attr)
    globals()[name] = value
    return value
