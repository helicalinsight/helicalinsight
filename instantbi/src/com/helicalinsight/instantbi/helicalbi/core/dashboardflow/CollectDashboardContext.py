"""Collect chat items, domain, topics, and viz types for convert-dashboard."""
from __future__ import annotations

from helicalbi.core.dashboardflow.collect_items import (
    collect_items,
    item_cards,
    list_names,
)
from helicalbi.model.DashboardLayoutState import DashboardLayoutState


def _viz_type(item: dict) -> str:
    viz = item.get("viz") if isinstance(item.get("viz"), dict) else {}
    model = item.get("viz_model") if isinstance(item.get("viz_model"), dict) else {}
    if not model:
        model = viz.get("viz_model") if isinstance(viz.get("viz_model"), dict) else {}
    chart = model.get("chart") if isinstance(model.get("chart"), dict) else {}
    return str(viz.get("chart_name") or chart.get("viz") or chart.get("mark") or "").strip()


def _unique_names(*groups) -> list[str]:
    seen: set[str] = set()
    names: list[str] = []
    for group in groups:
        for name in list_names(group):
            key = name.lower()
            if key in seen:
                continue
            seen.add(key)
            names.append(name)
    return names


class CollectDashboardContext:
    def process_flow(self, state: DashboardLayoutState) -> DashboardLayoutState:
        user_input = state.get("user_input") if isinstance(state.get("user_input"), dict) else {}
        items = list(state.get("items") or [])
        if not items:
            items = collect_items(user_input)
        if not items:
            state["error"] = "No visualizations were provided for convert-dashboard."
            state["items"] = []
            state["chat_context"] = []
            state["viz_types"] = []
            return state

        domain = _unique_names(state.get("domain"), user_input.get("domain"), *[item.get("domain") for item in items])
        topics = _unique_names(state.get("topics"), user_input.get("topics"), *[item.get("topics") for item in items])
        viz_types = _unique_names([_viz_type(item) for item in items])
        cards = item_cards(items, domain=domain, topics=topics)
        queries = [str(item.get("user_query") or "").strip() for item in items if str(item.get("user_query") or "").strip()]
        state["items"] = items
        state["domain"] = domain
        state["topics"] = topics
        state["viz_types"] = viz_types
        state["chat_context"] = cards
        if not state.get("user_query"):
            state["user_query"] = " | ".join(queries) or "convert-dashboard"
        return state


def route_after_context(state: DashboardLayoutState) -> str:
    if state.get("error") or not (state.get("items") or []):
        return "audit"
    return "plan"
