"""LangChain tool registry for the InstantBI dashboard agent."""
from __future__ import annotations

from typing import List, Optional, Sequence

from helicalbi.sql_agent.modes import tool_names_for_mode
from helicalbi.sql_agent.tools.analysis import analyze_result
from helicalbi.sql_agent.tools.control import finish_dashboard
from helicalbi.sql_agent.tools.report import build_report
from helicalbi.sql_agent.tools.schema import retrieve_schema
from helicalbi.sql_agent.tools.semantic import retrieve_semantic_model
from helicalbi.sql_agent.tools.sql import execute_query, generate_sql, validate_sql

_ALL_TOOLS = [
    retrieve_semantic_model,
    retrieve_schema,
    generate_sql,
    validate_sql,
    execute_query,
    analyze_result,
    build_report,
    finish_dashboard,
]


def dashboard_tools(mode: Optional[str] = None):
    """Full tool set for ToolNode (must include every tool the planner might call)."""
    return list(_ALL_TOOLS)


def planner_tools(mode: Optional[str] = None) -> List:
    """Subset bound to the planner LLM for the active mode (token control)."""
    allowed = set(tool_names_for_mode(mode))
    return [tool for tool in _ALL_TOOLS if getattr(tool, "name", None) in allowed]
