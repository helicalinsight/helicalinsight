"""Pick important dashboard filters from chat data_model / viz shelves."""
from __future__ import annotations

from typing import Any

from helicalbi.core.dashboardflow.collect_items import (
    column_leaf,
    column_table,
    filter_title,
    is_column_ref_label,
)
from helicalbi.core.dashboardflow.grid_clamp import _item_key, _viz_rows
from helicalbi.model.DashboardLayoutState import DashboardLayoutState

_MAX_FILTERS = 4


def _as_filter(raw: Any) -> tuple[str, str] | None:
    if isinstance(raw, str) and raw.strip():
        column = column_leaf(raw)
        return (column_table(raw), column) if column else None
    if not isinstance(raw, dict):
        return None
    source = raw.get("column")
    if source in (None, ""):
        source = raw.get("name") if raw.get("name") not in (None, "") else raw.get("alias")
    column = column_leaf(source)
    if not column:
        return None
    return column_table(source, raw.get("table")), column


def _item_data_model(item: dict[str, Any]) -> dict[str, Any]:
    data_model = item.get("data_model") if isinstance(item.get("data_model"), dict) else {}
    if data_model:
        return data_model
    report = item.get("report_model") if isinstance(item.get("report_model"), dict) else {}
    nested = report.get("data_model") if isinstance(report.get("data_model"), dict) else {}
    return nested or {}


def _data_model_filters(item: dict[str, Any]) -> list[Any]:
    data_model = _item_data_model(item)
    filters = data_model.get("filters")
    if isinstance(filters, list):
        return filters
    sql_parts = data_model.get("sql_parts") if isinstance(data_model.get("sql_parts"), dict) else {}
    extra = sql_parts.get("filters")
    return extra if isinstance(extra, list) else []


def _data_model_dimensions(item: dict[str, Any]) -> list[Any]:
    """Non-aggregated data_model columns — the natural slicer candidates."""
    data_model = _item_data_model(item)
    dims: list[Any] = []
    for raw in data_model.get("columns") or []:
        if not isinstance(raw, dict):
            continue
        if raw.get("aggregate") or raw.get("aggregateList"):
            continue
        if raw.get("hidden"):
            continue
        dims.append(raw)
    return dims


def _viz_filters(item: dict[str, Any]) -> list[Any]:
    model = item.get("viz_model") if isinstance(item.get("viz_model"), dict) else {}
    if not model:
        viz = item.get("viz") if isinstance(item.get("viz"), dict) else {}
        model = viz.get("viz_model") if isinstance(viz.get("viz_model"), dict) else {}
    data = model.get("data") if isinstance(model.get("data"), dict) else {}
    filters = data.get("filters")
    return filters if isinstance(filters, list) else []


def select_important_filters(items: list[dict[str, Any]], *, limit: int = _MAX_FILTERS) -> list[dict[str, Any]]:
    """Rank filters: data_model filters, then dimensions, then viz shelves."""
    scores: dict[tuple[str, str], dict[str, Any]] = {}
    item_ids = [_item_key(item) for item in items if _item_key(item)]

    def bump(raw: Any, weight: int, source_id: str) -> None:
        parsed = _as_filter(raw)
        if not parsed:
            return
        table, column = parsed
        key = (table.lower(), column.lower())
        label = ""
        col_ref = None
        if isinstance(raw, dict):
            label = str(raw.get("label") or "").strip()
            if is_column_ref_label(label) or not label:
                alias = str(raw.get("alias") or "").strip()
                label = "" if is_column_ref_label(alias) else alias
            if isinstance(raw.get("column"), dict) and raw["column"].get("name"):
                col_ref = raw["column"]
            elif "name" in raw and "id" in raw:
                col_ref = {"name": str(raw["name"]), "id": str(raw.get("id") or "")}
        entry = scores.setdefault(
            key,
            {
                "kind": "filter",
                "column": column,
                "table": table,
                "label": label,
                "sourceItemId": source_id,
                "score": 0,
            },
        )
        if label and not entry.get("label"):
            entry["label"] = label
        if col_ref and not entry.get("column_ref"):
            entry["column_ref"] = col_ref
        entry["score"] += weight
        if not entry.get("sourceItemId"):
            entry["sourceItemId"] = source_id

    for item in items:
        aid = _item_key(item)
        for raw in _data_model_filters(item):
            bump(raw, 10, aid)
        for raw in _data_model_dimensions(item):
            bump(raw, 8, aid)
        for raw in _viz_filters(item):
            bump(raw, 2, aid)
        for name in _viz_rows(item):
            bump(name, 1, aid)

    from_filters = [row for row in scores.values() if int(row.get("score") or 0) >= 10]
    from_dims = [row for row in scores.values() if int(row.get("score") or 0) >= 8]
    pool = from_filters or from_dims or list(scores.values())
    ranked = sorted(pool, key=lambda row: (-int(row.get("score") or 0), str(row.get("column") or "")))
    selected: list[dict[str, Any]] = []
    for row in ranked[:limit]:
        filt_item: dict[str, Any] = {
            "kind": "filter",
            "column": row["column"],
            "table": row.get("table") or "",
            "label": row.get("label") or "",
            "alias": row.get("label") or "",
            "title": filter_title({"column": row["column"], "label": row.get("label") or ""}),
            "sourceItemId": row.get("sourceItemId") or (item_ids[0] if item_ids else ""),
            "x": 0,
            "y": 1,
            "w": 3,
            "h": 1,
        }
        if row.get("column_ref"):
            filt_item["column_ref"] = row["column_ref"]
        selected.append(filt_item)
    return selected


class SelectDashboardFilters:
    def process_flow(self, state: DashboardLayoutState) -> DashboardLayoutState:
        if state.get("error"):
            return state
        components = select_important_filters(state.get("items") or [])
        state["filter_components"] = components
        state["filters"] = components
        return state
