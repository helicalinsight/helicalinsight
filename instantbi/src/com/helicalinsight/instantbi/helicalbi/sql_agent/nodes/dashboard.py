from __future__ import annotations

import logging
from typing import Any, Dict, List

from helicalbi.core.dashboardflow.collect_items import assign_component_ids, normalize_item
from helicalbi.model.output.dashboard.Kind import Kind
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
        chat_response = step.get("chat_response") if isinstance(step.get("chat_response"), dict) else {}
        has_viz = _has_real_viz(step, chat_response)
        # Think-mode analysis steps set include_in_dashboard=False but still carry
        # report_model for Preview / Dashboard Model layout.
        if step.get("include_in_dashboard") is False and not has_viz:
            continue
        if not has_viz:
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
    model = item.get("dashboard_model") if isinstance(item.get("dashboard_model"), dict) else item
    kind = str(model.get("kind") or item.get("compType") or "").strip().lower()
    if kind == Kind.APPLY_RESET:
        return True
    wire = Kind.wire(kind)
    report = item.get("report_model") if isinstance(item.get("report_model"), dict) else {}
    has_viz = isinstance(report.get("viz_model"), dict) and bool(report.get("viz_model"))
    if not kind:
        return has_viz or isinstance(item.get("viz_model"), dict)
    if wire == Kind.INSTANT:
        return has_viz or bool(item.get("viz_model"))
    if wire == Kind.TEXT:
        return bool(str(model.get("html") or model.get("title") or "").strip())
    if wire == Kind.INSTANT_FILTER:
        return bool(str(model.get("column") or model.get("title") or "").strip())
    if wire in {Kind.KPI, Kind.SVG}:
        return has_viz
    return True


def _layout_box(item: dict[str, Any]) -> dict[str, Any] | None:
    model = item.get("dashboard_model") if isinstance(item.get("dashboard_model"), dict) else item
    layout = model.get("layout") if isinstance(model.get("layout"), dict) else None
    return layout


def _viz_fingerprint(item: dict[str, Any]) -> tuple[Any, ...] | None:
    """Same displayed chart: title, mark, and shelves. Empty when this is not a chart tile."""
    model = item.get("dashboard_model") if isinstance(item.get("dashboard_model"), dict) else item
    if Kind.wire(str(model.get("kind") or "")) != Kind.INSTANT:
        return None
    report = item.get("report_model") if isinstance(item.get("report_model"), dict) else {}
    viz = report.get("viz_model") if isinstance(report.get("viz_model"), dict) else {}
    data = viz.get("data") if isinstance(viz.get("data"), dict) else {}
    chart = viz.get("chart") if isinstance(viz.get("chart"), dict) else {}

    def names(values: Any) -> tuple[str, ...]:
        return tuple(sorted(str(value).strip().lower() for value in (values or []) if str(value).strip()))

    title = str(model.get("title") or "").strip().lower()
    rows = names(data.get("rows"))
    columns = names(data.get("columns"))
    mark = str(chart.get("viz") or chart.get("mark") or "").strip().lower()
    if not title and not rows and not columns:
        return None
    return (title, mark, rows, columns)


def _drop_duplicate_charts(items: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Keep the first chart when two tiles repeat the same question and viz."""
    ordered = sorted(
        enumerate(items),
        key=lambda pair: (
            int((_layout_box(pair[1]) or {}).get("y") or 0),
            int((_layout_box(pair[1]) or {}).get("x") or 0),
            pair[0],
        ),
    )
    seen: set[tuple[Any, ...]] = set()
    drop: set[int] = set()
    for index, item in ordered:
        key = _viz_fingerprint(item)
        if key is None:
            continue
        if key in seen:
            drop.add(index)
            continue
        seen.add(key)
    return [item for index, item in enumerate(items) if index not in drop]


def _compact_empty_rows(items: list[dict[str, Any]]) -> None:
    """Slide tiles up through rows that no remaining tile occupies."""
    boxes = [box for item in items if (box := _layout_box(item)) is not None]
    if not boxes:
        return
    occupied: set[int] = set()
    bottom = 0
    for box in boxes:
        top = int(box.get("y") or 0)
        height = max(1, int(box.get("h") or 1))
        bottom = max(bottom, top + height)
        occupied.update(range(top, top + height))
    shift = 0
    row_shift: dict[int, int] = {}
    for row in range(bottom):
        if row not in occupied:
            shift += 1
        row_shift[row] = shift
    for box in boxes:
        top = int(box.get("y") or 0)
        box["y"] = top - row_shift.get(top, 0)


def _filter_invented_tiles(dashboard: dict[str, Any]) -> dict[str, Any]:
    kept = [
        item for item in (dashboard.get("items") or []) if isinstance(item, dict) and _keep_laid_out_item(item)
    ]
    kept = _drop_duplicate_charts(kept)
    _compact_empty_rows(kept)
    dashboard["items"] = kept
    dashboard.pop("layout", None)
    return dashboard


def dashboard_node(state: AgentState) -> Dict[str, Any]:
    """Assemble InstantBI viz items into a dashboard via the existing layout graph."""
    thread_id = str(state.get("thread_id") or "")
    if state.get("build_dashboard") is False:
        logger.info("Skipping dashboard layout (build_dashboard=False)")
        return {
            "dashboard": {
                "dashboardid": thread_id,
                "chatid": thread_id,
                "items": [],
                "dashboard_config": {},
                "templateId": "",
            }
        }
    items = _items_from_collected(state)
    if not items:
        logger.warning("Dashboard node found no visualization items")
        return {
            "dashboard": {
                "dashboardid": thread_id,
                "chatid": thread_id,
                "items": [],
                "dashboard_config": {},
                "templateId": "",
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
        "title": result.get("title") or state.get("original_question") or "",
        "items": result.get("items") or items,
        "dashboard_config": result.get("dashboard_config") or {},
        "templateId": result.get("templateId") or runtime["template_id"] or "",
        "token_usage": result.get("token_usage") or {},
    }
    if result.get("error"):
        dashboard["error"] = result["error"]
    dashboard = _filter_invented_tiles(dashboard)
    if not dashboard.get("items"):
        dashboard["error"] = "No visualizations were produced for the dashboard."
    if state.get("flat_sql"):
        try:
            from helicalbi.interactive.think_flow import correct_think_mode_filters
            dashboard = correct_think_mode_filters(dashboard)
        except Exception:
            logger.exception("Failed to correct think mode filters in dashboard_node")
    logger.info("Dashboard node laid out %s item(s)", len(dashboard.get("items") or []))
    return {"dashboard": dashboard}
