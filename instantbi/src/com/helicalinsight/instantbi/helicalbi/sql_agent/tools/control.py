"""Control-flow tools for the dashboard agent."""
from __future__ import annotations

from typing import Annotated, Any

from langchain_core.tools import tool
from langgraph.prebuilt import InjectedState

from helicalbi.sql_agent.tools.context import AgentToolContext


class ControlTools:
    """Stop gathering data and hand off to dashboard layout."""

    def finish(self, reason: str, state: dict[str, Any]) -> dict[str, Any]:
        return {
            "ok": True,
            "reason": reason,
            "state_patch": {"is_complete": True},
        }


control_tools = ControlTools()


@tool
def finish_dashboard(
    reason: str = "",
    *,
    state: Annotated[dict, InjectedState],
) -> str:
    """Stop gathering data and build the dashboard from collected charts."""
    return AgentToolContext.dump(control_tools.finish(reason, state))
