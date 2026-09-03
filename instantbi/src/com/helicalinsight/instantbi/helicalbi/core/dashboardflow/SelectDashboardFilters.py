"""Pick important dashboard filters from chat data_model / viz shelves."""
from __future__ import annotations

from typing import Any

from helicalbi.core.dashboardflow.grid_clamp import _item_key, _viz_rows
from helicalbi.model.DashboardLayoutState import DashboardLayoutState

_MAX_FILTERS = 4


def _as_filter(raw: Any) -> tuple[str, str] | None:
    if isinstance(raw, str) and raw.strip():
        return "", raw.strip()
    if not isinstance(raw, dict):
        return None
    column = str(raw.get("column") or raw.get("name") or raw.get("alias") or "").strip()
    if not column:
        return None
    table = str(raw.get("table") or "").strip()
    return table, column


def _data_model_filters(item: dict[str, Any]) -> list[Any]:
    data_model = item.get("data_model") if isinstance(item.get("data_model"), dict) else {}
    filters = data_model.get("filters")
    if isinstance(filters, list):
        return filters
    sql_parts = data_model.get("sql_parts") if isinstance(data_model.get("sql_parts"), dict) else {}
    extra = sql_parts.get("filters")
    return extra if isinstance(extra, list) else []


def _viz_filters(item: dict[str, Any]) -> list[Any]:
    model = item.get("viz_model") if isinstance(item.get("viz_model"), dict) else {}
    if not model:
        viz = item.get("viz") if isinstance(item.get("viz"), dict) else {}
        model = viz.get("viz_model") if isinstance(viz.get("viz_model"), dict) else {}
    data = model.get("data") if isinstance(model.get("data"), dict) else {}
    filters = data.get("filters")
    return filters if isinstance(filters, list) else []


def select_important_filters(items: list[dict[str, Any]], *, limit: int = _MAX_FILTERS) -> list[dict[str, Any]]:
    """Rank filters: data_model first, then viz filters, then row dimensions."""
    scores: dict[tuple[str, str], dict[str, Any]] = {}
    listeners_all = [_item_key(item) for item in items if _item_key(item)]

    def bump(raw: Any, weight: int, source_id: str) -> None:
        parsed = _as_filter(raw)
        if not parsed:
            return
        table, column = parsed
        key = (table.lower(), column.lower())
        entry = scores.setdefault(
            key,
            {
                "kind": "filter",
                "column": column,
                "table": table,
                "sourceItemId": source_id,
                "listeners": [],
                "score": 0,
            },
        )
        entry["score"] += weight
        if source_id and source_id not in entry["listeners"]:
            entry["listeners"].append(source_id)
        if not entry.get("sourceItemId"):
            entry["sourceItemId"] = source_id

    for item in items:
        aid = _item_key(item)
        for raw in _data_model_filters(item):
            bump(raw, 10, aid)
        for raw in _viz_filters(item):
            bump(raw, 2, aid)
        for name in _viz_rows(item):
            bump(name, 1, aid)

    from_datamodel = [row for row in scores.values() if int(row.get("score") or 0) >= 10]
    pool = from_datamodel or list(scores.values())
    ranked = sorted(pool, key=lambda row: (-int(row.get("score") or 0), str(row.get("column") or "")))
    selected: list[dict[str, Any]] = []
    for row in ranked[:limit]:
        listeners = list(row.get("listeners") or []) or list(listeners_all)
        selected.append(
            {
                "kind": "filter",
                "column": row["column"],
                "table": row.get("table") or "",
                "title": row["column"],
                "sourceItemId": row.get("sourceItemId") or (listeners[0] if listeners else ""),
                "listeners": listeners,
                "x": 0,
                "y": 1,
                "w": 3,
                "h": 1,
            }
        )
    return selected


class SelectDashboardFilters:
    def process_flow(self, state: DashboardLayoutState) -> DashboardLayoutState:
        if state.get("error"):
            return state
        components = select_important_filters(state.get("items") or [])
        state["filter_components"] = components
        state["filters"] = components
        return state
