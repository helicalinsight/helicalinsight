"""Deterministic clamp node after the dashboard layout LLM."""
from __future__ import annotations

from helicalbi.core.dashboardflow.grid_clamp import apply_decision
from helicalbi.model.DashboardLayoutState import DashboardLayoutState


class DashboardLayoutClamp:
    def process_flow(self, state: DashboardLayoutState) -> DashboardLayoutState:
        items = state.get("items") or []
        if state.get("error") or not items:
            return state
        decision = {
            "dashboard_config": state.get("dashboard_config"),
            "widgets": state.get("widgets") or [],
            "summary": state.get("summary"),
            "filters": state.get("filters"),
            "layout": state.get("layout"),
            "decorations": state.get("decorations") or [],
            "title": state.get("title") or state.get("user_query") or "",
        }
        clamped = apply_decision(items, decision)
        state["dashboard_config"] = clamped["dashboard_config"]
        state["title"] = clamped.get("title") or ""
        state["items"] = clamped.get("items") or items
        state["summary"] = clamped.get("summary") or {}
        state["sections"] = clamped.get("sections") or []
        state["filters"] = clamped.get("filters") or []
        state["layout"] = clamped.get("layout") or []
        state["decorations"] = clamped.get("decorations") or []
        return state
