from __future__ import annotations

import logging
from typing import Any, Dict, List

from helicalbi.core.dashboardflow.collect_items import assign_component_ids, normalize_item
from helicalbi.sql_agent.state import AgentState
from helicalbi.sql_agent.strategy_tree import resolve_plan_runtime

logger = logging.getLogger(__name__)


def invoke_dashboard_layout(payload: dict[str, Any]) -> dict[str, Any]:
    from GraphBuilderManger import dashboard_layout_graph

    return dashboard_layout_graph.invoke(payload)


def _has_real_viz(step: dict[str, Any], chat_response: dict[str, Any]) -> bool:
    report = step.get("report_model") or chat_response.get("report_model") or {}
    if isinstance(report, dict) and isinstance(report.get("viz_model"), dict) and report.get("viz_model"):
        return True
    viz = chat_response.get("viz") if isinstance(chat_response.get("viz"), dict) else {}
    if str(viz.get("chart_name") or "").strip():
        return True
    return bool(isinstance(viz.get("viz_model"), dict) and viz.get("viz_model"))


def _items_from_collected(state: AgentState) -> List[dict[str, Any]]:
    thread_id = str(state.get("thread_id") or "")
    items: List[dict[str, Any]] = []
    for index, step in enumerate(state.get("collected_data") or [], start=1):
        if step.get("include_in_dashboard") is False:
            continue
        chat_response = step.get("chat_response") if isinstance(step.get("chat_response"), dict) else {}
        if not _has_real_viz(step, chat_response):
            continue
        seq = str(step.get("chat_seq_id") or index)
        raw = {
            "id": seq,
            "chat_sequence_id": seq,
            "chatid": thread_id,
            "user_query": step.get("title") or step.get("sub_question") or "",
            "sql": chat_response.get("sql"),
            "viz": chat_response.get("viz"),
            "report_model": step.get("report_model") or chat_response.get("report_model"),
            "summary": chat_response.get("summary") or {"insight": step.get("analysis") or ""},
            "error": chat_response.get("error"),
        }
        item = normalize_item(raw, fallback_id=seq)
        if item:
            items.append(item)
    return assign_component_ids(items, chatid=thread_id)


def _keep_laid_out_item(item: dict[str, Any]) -> bool:
    """Drop empty KPI/svg tiles the layout LLM invents without a real report."""
    model = item.get("dashboard_model") if isinstance(item.get("dashboard_model"), dict) else {}
    kind = str(model.get("kind") or "").strip().lower()
    report = item.get("report_model") if isinstance(item.get("report_model"), dict) else {}
    has_viz = isinstance(report.get("viz_model"), dict) and bool(report.get("viz_model"))
    if not kind:
        return has_viz or isinstance(item.get("viz_model"), dict)
    if kind == "viz":
        return has_viz or bool(item.get("viz_model"))
    if kind == "summary":
        return bool(str(model.get("html") or model.get("title") or "").strip())
    if kind == "filter":
        return bool(str(model.get("column") or model.get("title") or "").strip())
    if kind in {"kpi", "svg"}:
        return has_viz
    return True


def _filter_invented_tiles(dashboard: dict[str, Any]) -> dict[str, Any]:
    items = [item for item in (dashboard.get("items") or []) if isinstance(item, dict) and _keep_laid_out_item(item)]
    kept_ids = {str(item.get("component_id") or "") for item in items}
    kept_seq = {str(item.get("id") or item.get("chat_sequence_id") or "") for item in items}
    layout = []
    for slot in dashboard.get("layout") or []:
        if not isinstance(slot, dict):
            continue
        component_id = str(slot.get("component_id") or "")
        item_id = str(slot.get("itemId") or "")
        if component_id in kept_ids or item_id in kept_ids or item_id in kept_seq:
            layout.append(slot)
    dashboard["items"] = items
    dashboard["layout"] = layout
    return dashboard


def dashboard_node(state: AgentState) -> Dict[str, Any]:
    """Assemble InstantBI viz items into a dashboard via the existing layout graph."""
    items = _items_from_collected(state)
    thread_id = str(state.get("thread_id") or "")
    if not items:
        logger.warning("Dashboard node found no visualization items")
        return {
            "dashboard": {
                "dashboardid": thread_id,
                "chatid": thread_id,
                "items": [],
                "theme": {},
                "templateId": "",
                "layout": [],
                "error": "No visualizations were produced for the dashboard.",
            }
        }

    runtime = resolve_plan_runtime(state.get("investigation_plan") or {})
    result = invoke_dashboard_layout(
        {
            "items": items,
            "user_input": {
                "chatid": thread_id,
                "inputString": state.get("original_question") or "",
                "investigation_plan": state.get("investigation_plan") or {},
                "persona": state.get("persona") or {},
            },
            "username": state.get("username") or "",
            "session_cookie": state.get("session_cookie") or "",
            "thread_id": thread_id,
            "chatid": thread_id,
            "user_query": state.get("original_question") or "",
            "layout_plan": runtime["layout_guidance"],
            "templateId": runtime["template_id"],
            "domain": (state.get("investigation_plan") or {}).get("domain") or state.get("selected_domains") or [],
            "topics": (state.get("investigation_plan") or {}).get("topics") or state.get("selected_topics") or [],
        }
    )
    dashboard = {
        "dashboardid": thread_id,
        "chatid": thread_id,
        "items": result.get("items") or items,
        "theme": result.get("theme") or {},
        "templateId": result.get("templateId") or runtime["template_id"] or "",
        "layout": result.get("layout") or [],
        "token_usage": result.get("token_usage") or {},
    }
    if result.get("error"):
        dashboard["error"] = result["error"]
    dashboard = _filter_invented_tiles(dashboard)
    if not dashboard.get("items"):
        dashboard["error"] = "No visualizations were produced for the dashboard."
    logger.info("Dashboard node laid out %s item(s)", len(dashboard.get("items") or []))
    return {"dashboard": dashboard}
