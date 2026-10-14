"""Deterministic 12-column grid clamp and overlap repair for convert-dashboard."""
from __future__ import annotations

import html
import re
from typing import Any

from helicalbi.core.dashboardflow.collect_items import (
    column_leaf,
    column_table,
    filter_title,
    new_component_id,
)
from helicalbi.model.output.dashboard.ApplyResetComponent import ApplyResetComponent
from helicalbi.model.output.dashboard.DashboardModel import DashboardModel
from helicalbi.model.output.dashboard.Kind import Kind
from helicalbi.model.output.dashboard.Layout import Layout

GRID_COLUMNS = 12
_MIN_W = 2
_MIN_H = 1
_DEFAULT_VIZ_W = 6
_DEFAULT_VIZ_H = 4
_KPI_W = 3
_KPI_H = 2
_FILTER_W = 3
_FILTER_H = 1
_SUMMARY_H = 1


def clamp_rect(x: Any, y: Any, w: Any, h: Any, *, min_w: int = _MIN_W, min_h: int = _MIN_H) -> tuple[int, int, int, int]:
    width = max(min_w, min(int(w or min_w), GRID_COLUMNS))
    height = max(min_h, int(h or min_h))
    left = max(0, min(int(x or 0), GRID_COLUMNS - width))
    top = max(0, int(y or 0))
    return left, top, width, height


def _overlap(a: tuple[int, int, int, int], b: tuple[int, int, int, int]) -> bool:
    ax, ay, aw, ah = a
    bx, by, bw, bh = b
    return ax < bx + bw and ax + aw > bx and ay < by + bh and ay + ah > by


def resolve_overlaps(rects: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Push overlapping widgets down. Preserves relative order by y then x."""
    placed: list[tuple[int, int, int, int]] = []
    result: list[dict[str, Any]] = []
    ordered = sorted(
        enumerate(rects),
        key=lambda pair: (int(pair[1].get("y") or 0), int(pair[1].get("x") or 0), pair[0]),
    )
    for _index, rect in ordered:
        x, y, w, h = clamp_rect(rect.get("x"), rect.get("y"), rect.get("w"), rect.get("h"))
        box = (x, y, w, h)
        while any(_overlap(box, existing) for existing in placed):
            y += 1
            box = (x, y, w, h)
        placed.append(box)
        next_rect = dict(rect)
        next_rect.update({"x": x, "y": y, "w": w, "h": h})
        result.append(next_rect)
    result.sort(key=lambda item: (item.get("y", 0), item.get("x", 0)))
    return result


def _is_kpi(item: dict[str, Any]) -> bool:
    viz = item.get("viz") or {}
    viz_parts = item.get("viz_parts") or {}
    model = item.get("viz_model") if isinstance(item.get("viz_model"), dict) else {}
    chart_name = str(viz.get("chart_name") or viz_parts.get("chart_name") or "").lower()
    mark = str(
        (model.get("chart") or {}).get("mark")
        or ((viz.get("viz_model") or {}).get("chart") or {}).get("mark")
        or viz_parts.get("mark")
        or ""
    ).lower()
    return chart_name == "kpi" or mark == "card"


_FALLBACK_SVG = (
    '<svg viewBox="0 0 120 8" xmlns="http://www.w3.org/2000/svg">'
    '<rect width="120" height="2" y="3" rx="1" fill="#1677ff"/></svg>'
)
_EXTRA_KINDS = {"summary", "text", "separator", "filter", "kpi", "svg", "image"}
_HTML_TAG_RE = re.compile(r"<\s*/?\s*[a-zA-Z]")
_UNSAFE_BLOCK_RE = re.compile(
    r"<\s*(script|style|iframe|object|embed)\b[^>]*>.*?<\s*/\s*\1\s*>",
    re.IGNORECASE | re.DOTALL,
)
_UNSAFE_TAG_RE = re.compile(
    r"</?\s*(script|style|iframe|object|embed)\b[^>]*>",
    re.IGNORECASE,
)
_ON_ATTR_RE = re.compile(
    r"""\s+on[a-zA-Z]+\s*=\s*(?:"[^"]*"|'[^']*'|[^\s>]+)""",
    re.IGNORECASE,
)
_DEFAULT_PARAMETERS = {
    "enable": True,
    "orientation": "right",
    "enableApplyButton": True,
    "floatingFilter": True,
    "closeOnApply": False,
}


def _dashboard_config(payload: dict[str, Any]) -> dict[str, Any]:
    """Normalize parent dashboard_config (theme + parameters) from payload."""
    from helicalbi.model.output.dashboard.DashboardConfig import (
        DashboardConfig,
        default_dashboard_config,
    )

    raw = payload.get("dashboard_config")
    if raw is None:
        raw = payload.get("dashboardConfig")
    if not isinstance(raw, dict):
        raw = {}
    # Accept top-level parameters during transition and nest under dashboard_config.
    if "parameters" not in raw and isinstance(payload.get("parameters"), dict):
        raw = {**raw, "parameters": payload["parameters"]}
    if raw:
        return DashboardConfig.model_validate(raw).model_dump()
    return default_dashboard_config().model_dump()


def _normalize_kind(kind: str, *, aid: str, known: set[str]) -> str:
    value = str(kind or "viz").strip().lower()
    aliases = {
        "chart": "viz",
        "report": "viz",
        "instant": "viz",
        "insight": "summary",
        "overview": "summary",
        "story": "text",
        "storyline": "text",
        "slicer": "filter",
        "instant.filter": "filter",
        "metric": "kpi",
        "key_metric": "kpi",
        "key-metric": "kpi",
        "card": "kpi",
        "icon": "svg",
        "image": "svg",
        "rule": "svg",
        "separator": "svg",
        "html": "svg",
    }
    value = aliases.get(value, value)
    if value == "kpi" and aid in known:
        return "viz"
    if value not in {"viz"} | _EXTRA_KINDS and value != "svg":
        return "viz" if aid in known else ""
    return value


def _dashboard_parameters(payload: dict[str, Any] | None = None, *, config: dict[str, Any] | None = None) -> dict[str, Any]:
    raw = {}
    if isinstance(config, dict) and isinstance(config.get("parameters"), dict):
        raw = config["parameters"]
    elif isinstance(payload, dict) and isinstance(payload.get("parameters"), dict):
        raw = payload["parameters"]
    orientation = str(raw.get("orientation") or _DEFAULT_PARAMETERS["orientation"]).lower()
    if orientation not in {"right", "left", "top"}:
        orientation = "right"
    return {
        "enable": bool(raw.get("enable", _DEFAULT_PARAMETERS["enable"])),
        "orientation": orientation,
        "enableApplyButton": bool(
            raw.get("enableApplyButton", _DEFAULT_PARAMETERS["enableApplyButton"])
        ),
        "floatingFilter": bool(raw.get("floatingFilter", _DEFAULT_PARAMETERS["floatingFilter"])),
        "closeOnApply": bool(raw.get("closeOnApply", _DEFAULT_PARAMETERS["closeOnApply"])),
    }


def _default_values(raw: dict[str, Any]) -> list[Any]:
    values = raw.get("default_values")
    if values is None:
        values = raw.get("defaultValues")
    if values is None:
        values = raw.get("values")
    if isinstance(values, list):
        return list(values)
    if values in (None, ""):
        return []
    return [values]


def _viz_rows(item: dict[str, Any]) -> list[str]:
    model = item.get("viz_model") if isinstance(item.get("viz_model"), dict) else {}
    if not model:
        model = ((item.get("viz") or {}).get("viz_model") or {}) if isinstance(item.get("viz"), dict) else {}
    data = model.get("data") if isinstance(model.get("data"), dict) else {}
    names: list[str] = []
    for raw in list(data.get("rows") or []) + list(data.get("filters") or []):
        if isinstance(raw, str) and raw.strip():
            name = column_leaf(raw)
        elif isinstance(raw, dict):
            name = column_leaf(raw.get("column") or raw.get("name") or raw.get("alias") or raw)
        else:
            name = ""
        if name:
            names.append(name)
    return names


def _viz_measures(item: dict[str, Any]) -> list[str]:
    model = item.get("viz_model") if isinstance(item.get("viz_model"), dict) else {}
    if not model:
        model = ((item.get("viz") or {}).get("viz_model") or {}) if isinstance(item.get("viz"), dict) else {}
    data = model.get("data") if isinstance(model.get("data"), dict) else {}
    names: list[str] = []
    for raw in list(data.get("columns") or []):
        if isinstance(raw, str) and raw.strip():
            name = column_leaf(raw)
        elif isinstance(raw, dict):
            name = column_leaf(raw.get("column") or raw.get("name") or raw.get("alias") or raw)
        else:
            name = ""
        if name:
            names.append(name)
    return names


def _item_key(item: dict[str, Any]) -> str:
    return str(item.get("component_id") or item.get("id") or "")


def _widget_size(raw: dict[str, Any], *, default_w: int, default_h: int, min_w: int = 1, min_h: int = 1):
    w = raw.get("w") if raw.get("w") not in (None, "") else raw.get("width")
    h = raw.get("h") if raw.get("h") not in (None, "") else raw.get("height")
    return clamp_rect(raw.get("x"), raw.get("y"), w or default_w, h or default_h, min_w=min_w, min_h=min_h)


def _item_data_model(item: dict[str, Any]) -> dict[str, Any]:
    data_model = item.get("data_model") if isinstance(item.get("data_model"), dict) else {}
    if data_model:
        return data_model
    report = item.get("report_model") if isinstance(item.get("report_model"), dict) else {}
    nested = report.get("data_model") if isinstance(report.get("data_model"), dict) else {}
    return nested or {}


def _data_model_dimension_filters(item: dict[str, Any]) -> list[dict[str, Any]]:
    """Non-aggregated columns from data_model — preferred slicer fields."""
    dims: list[dict[str, Any]] = []
    for raw in _item_data_model(item).get("columns") or []:
        if not isinstance(raw, dict):
            continue
        if raw.get("aggregate") or raw.get("aggregateList"):
            continue
        if raw.get("hidden"):
            continue
        dims.append(raw)
    return dims


def _filter_match_key(raw: Any, *, table: str = "") -> tuple[str, str] | None:
    if isinstance(raw, str) and raw.strip():
        column = column_leaf(raw)
        return (column_table(raw, table).lower(), column.lower()) if column else None
    if not isinstance(raw, dict):
        return None
    source = raw.get("column") if raw.get("column") not in (None, "") else raw.get("name")
    if source in (None, ""):
        source = raw.get("alias")
    column = column_leaf(source)
    if not column:
        return None
    return column_table(source, raw.get("table") or table).lower(), column.lower()


def _extract_column_ref(raw: Any) -> dict[str, Any] | None:
    if not isinstance(raw, dict):
        return None
    ref = raw.get("column") if isinstance(raw.get("column"), dict) else raw
    if isinstance(ref, dict) and ref.get("name") and not isinstance(ref.get("name"), dict):
        return {"name": str(ref.get("name")), "id": str(ref.get("id") or "")}
    return None


def _build_column_pool(
    items_by_id: dict[str, dict[str, Any]],
    filter_rows: list[dict[str, Any]],
) -> dict[Any, Any]:
    pool: dict[Any, Any] = {}
    table_prefixes: dict[str, str] = {}

    def _index_ref(ref: Any, fallback_table: str = "") -> None:
        extracted = _extract_column_ref(ref)
        if not extracted:
            return
        name = extracted["name"]
        leaf = column_leaf(name).lower()
        tbl = (column_table(name) or fallback_table).lower()
        if tbl and tbl not in table_prefixes and "." in name:
            table_prefixes[tbl] = name.rsplit(".", 1)[0]
        if leaf:
            if tbl:
                pool[(tbl, leaf)] = extracted
            if ("", leaf) not in pool:
                pool[("", leaf)] = extracted

    for row in filter_rows:
        _index_ref(row.get("column_ref"), str(row.get("table") or ""))
        _index_ref(row.get("column"), str(row.get("table") or ""))

    for item in items_by_id.values():
        dm = _item_data_model(item)
        for col in dm.get("columns") or []:
            if isinstance(col, dict):
                _index_ref(col.get("column"))
        for flt in dm.get("filters") or []:
            if isinstance(flt, dict):
                _index_ref(flt.get("column"))

    pool["__prefixes__"] = table_prefixes
    return pool


def _resolve_column_ref(
    data_model: dict[str, Any],
    column: str,
    table: str = "",
    *,
    pool: dict[Any, Any] | None = None,
    spec: dict[str, Any] | None = None,
) -> Any:
    """Prefer the wire ``{name, id}`` ref from spec, columns, filters, or dashboard pool."""
    if spec:
        extracted = _extract_column_ref(spec.get("column_ref")) or _extract_column_ref(spec.get("column"))
        if extracted:
            return extracted

    target = column_leaf(column)
    if not target:
        return column

    # 1. Target data_model.columns
    for raw in data_model.get("columns") or []:
        if not isinstance(raw, dict):
            continue
        alias = str(raw.get("alias") or "").strip()
        ref = raw.get("column")
        leaf = column_leaf(ref if ref not in (None, "") else alias)
        if leaf.lower() != target.lower() and alias.lower() != target.lower():
            continue
        extracted = _extract_column_ref(ref)
        if extracted:
            return extracted
        if isinstance(ref, dict) or (isinstance(ref, str) and ref.strip()):
            return ref

    # 2. Target data_model.filters
    for raw in data_model.get("filters") or []:
        if not isinstance(raw, dict):
            continue
        alias = str(raw.get("alias") or raw.get("label") or "").strip()
        ref = raw.get("column")
        leaf = column_leaf(ref if ref not in (None, "") else alias)
        if leaf.lower() != target.lower() and alias.lower() != target.lower():
            continue
        extracted = _extract_column_ref(ref)
        if extracted:
            return extracted

    # 3. Global dashboard pool across all items
    if pool:
        tbl = table.lower()
        if (tbl, target.lower()) in pool:
            return pool[(tbl, target.lower())]
        if ("", target.lower()) in pool:
            return pool[("", target.lower())]
        prefixes = pool.get("__prefixes__") or {}
        if tbl and tbl in prefixes:
            return {"name": f"{prefixes[tbl]}.{target}", "id": ""}

    return column


def _wire_slicer_filter(
    spec: dict[str, Any],
    data_model: dict[str, Any],
    *,
    index: int,
    pool: dict[Any, Any] | None = None,
) -> dict[str, Any]:
    column = column_leaf(spec.get("column") if spec.get("column") not in (None, "") else spec.get("name"))
    table = column_table(
        spec.get("column") if spec.get("column") not in (None, "") else spec.get("name"),
        spec.get("table"),
    )
    label = filter_title(spec) or column or "Filter"
    column_ref = _resolve_column_ref(data_model, column, table, pool=pool, spec=spec)
    values = spec.get("default_values")
    if values is None:
        values = spec.get("defaultValues")
    wire: dict[str, Any] = {
        "column": column_ref,
        "label": label,
        "alias": label,
        "operator": "AND",
        "id": index,
        "mode": "auto",
        "condition": "EQUALS",
        "values": list(values or []),
    }
    if table and not isinstance(column_ref, dict):
        wire["table"] = table
    return wire


def _rebuild_filter_expression(filters: list[Any]) -> list[str] | None:
    aliases: list[str] = []
    for raw in filters:
        if not isinstance(raw, dict):
            continue
        alias = str(raw.get("alias") or raw.get("label") or "").strip()
        if not alias:
            alias = column_leaf(raw.get("column") or raw.get("name") or raw.get("alias"))
        if alias:
            aliases.append(alias)
    if not aliases:
        return None
    if len(aliases) == 1:
        return [aliases[0]]
    joined = aliases[0]
    for alias in aliases[1:]:
        joined = f"{joined} AND {alias}"
    return [joined]


def _inject_filters_into_item(
    item: dict[str, Any],
    filter_specs: list[dict[str, Any]],
    *,
    pool: dict[Any, Any] | None = None,
) -> None:
    """Ensure source chart data_model.filters includes each dashboard slicer column."""
    if not filter_specs:
        return
    data_model = dict(_item_data_model(item))
    existing = [
        dict(raw) if isinstance(raw, dict) else raw
        for raw in (data_model.get("filters") or [])
    ]
    seen: set[tuple[str, str]] = set()
    for raw in existing:
        key = _filter_match_key(raw)
        if key:
            seen.add(key)
    changed = False
    for spec in filter_specs:
        key = _filter_match_key(spec)
        if not key or key in seen:
            continue
        existing.append(_wire_slicer_filter(spec, data_model, index=len(existing), pool=pool))
        seen.add(key)
        changed = True
    if not existing:
        return
    if (
        not changed
        and data_model.get("filters")
        and data_model.get("filterExpression")
        and data_model.get("customFilterExpression")
    ):
        return
    for idx, raw in enumerate(existing):
        if isinstance(raw, dict):
            raw["id"] = idx
    data_model["filters"] = existing
    expr = _rebuild_filter_expression(existing)
    if expr is not None:
        data_model["filterExpression"] = expr
    tokens = " AND ".join(f"${{{idx}}}" for idx in range(len(existing)))
    if tokens:
        data_model["customFilterExpression"] = f" {tokens} "
    item["data_model"] = data_model
    report = item.get("report_model") if isinstance(item.get("report_model"), dict) else None
    if report is not None:
        item["report_model"] = {**report, "data_model": data_model}


def _sync_source_filters(
    items_by_id: dict[str, dict[str, Any]],
    filter_rows: list[dict[str, Any]],
) -> None:
    """Write emitted instant.filter columns onto each source item's data_model."""
    pool = _build_column_pool(items_by_id, filter_rows)
    by_source: dict[str, list[dict[str, Any]]] = {}
    for row in filter_rows:
        source_id = str(row.get("sourceItemId") or "").strip()
        if not source_id or source_id not in items_by_id:
            continue
        by_source.setdefault(source_id, []).append(row)
    for source_id, specs in by_source.items():
        _inject_filters_into_item(items_by_id[source_id], specs, pool=pool)


def _item_filters(items: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Dashboard slicers from data_model filters/dimensions, then viz shelves."""
    filters: list[dict[str, Any]] = []
    item_ids = [_item_key(entry) for entry in items if _item_key(entry)]
    seen: set[tuple[str, str]] = set()
    for item in items:
        model = item.get("viz_model") if isinstance(item.get("viz_model"), dict) else {}
        viz_filters = (model.get("data") or {}).get("filters") or []
        if not viz_filters:
            viz_filters = (((item.get("viz") or {}).get("viz_model") or {}).get("data") or {}).get("filters") or []
        data_filters = _item_data_model(item).get("filters") or []
        dim_filters = _data_model_dimension_filters(item)
        row_dims = [{"name": name} for name in _viz_rows(item)]
        # Prefer real filters and dimension columns over measure-like row shelves.
        candidates = list(data_filters) + list(dim_filters) + list(viz_filters) + row_dims
        for raw in candidates:
            if isinstance(raw, str):
                raw = {"name": raw}
            if not isinstance(raw, dict):
                continue
            source = raw.get("column") if raw.get("column") not in (None, "") else raw.get("name")
            if source in (None, ""):
                source = raw.get("alias")
            column = column_leaf(source)
            table = column_table(source, raw.get("table"))
            key = (table.lower(), column.lower())
            if not column or key in seen:
                continue
            seen.add(key)
            col_ref = raw.get("column") if isinstance(raw, dict) and isinstance(raw.get("column"), dict) else None
            filt_item: dict[str, Any] = {
                "kind": "filter",
                "column": column,
                "table": table,
                "sourceItemId": _item_key(item) or (item_ids[0] if item_ids else ""),
                "x": 0,
                "y": 0,
                "w": _FILTER_W,
                "h": _FILTER_H,
            }
            if col_ref:
                filt_item["column_ref"] = col_ref
            filters.append(filt_item)
    return filters


def _html_fragment(value: Any) -> str:
    """Narrative tile body. Plain prose becomes a paragraph; markup is kept."""
    text = str(value or "").strip()
    if not text:
        return ""
    if _HTML_TAG_RE.search(text):
        cleaned = _UNSAFE_BLOCK_RE.sub("", text)
        cleaned = _UNSAFE_TAG_RE.sub("", cleaned)
        cleaned = _ON_ATTR_RE.sub("", cleaned)
        return cleaned[:4000]
    return f"<p>{html.escape(text)}</p>"[:4000]


def _title_from_item(item: dict[str, Any] | None, *, kind: str, raw: dict[str, Any]) -> str:
    source = item or {}
    model = source.get("viz_model") if isinstance(source.get("viz_model"), dict) else {}
    props = model.get("properties") if isinstance(model.get("properties"), dict) else {}
    viz = source.get("viz") if isinstance(source.get("viz"), dict) else {}
    if kind == "filter":
        return filter_title(raw)
    if kind in {"summary", "text"}:
        return str(raw.get("title") or "Summary")
    if kind in {"svg", "image", "separator"}:
        return str(raw.get("title") or "")
    if kind == "kpi":
        return str(raw.get("title") or "Key metric")
    return str(
        raw.get("title")
        or props.get("title")
        or viz.get("vf_title")
        or source.get("user_query")
        or viz.get("chart_name")
        or "Chart"
    )


def _dashboard_model(
    raw: dict[str, Any],
    *,
    kind: str,
    item: dict[str, Any] | None = None,
) -> dict[str, Any]:
    body = str(raw.get("html") or raw.get("text") or raw.get("svg") or "")[:4000]
    if kind in {"summary", "text"}:
        body = _html_fragment(body)
    title = _title_from_item(item, kind=kind, raw=raw)
    export = None
    column = None
    table = None
    source_item_id = None
    default_values = None
    if kind == "viz":
        export = bool(raw.get("export", True))
    if kind == "filter":
        source = raw.get("column") if raw.get("column") not in (None, "") else raw.get("name")
        column = column_leaf(source)
        table = column_table(source, raw.get("table"))
        source_item_id = str(raw.get("sourceItemId") or "")
        default_values = _default_values(raw)
    return DashboardModel(
        Kind.wire(kind),
        title=title,
        layout=Layout(raw.get("x"), raw.get("y"), raw.get("w"), raw.get("h")),
        css=str(raw.get("css") or "")[:4000],
        js=str(raw.get("js") or "")[:4000],
        html=body,
        export=export,
        column=column,
        table=table,
        source_item_id=source_item_id,
        default_values=default_values,
    ).to_dict()


def _summary_insight(source: dict[str, Any]) -> dict[str, str]:
    summary = source.get("summary")
    if isinstance(summary, dict):
        insight = str(summary.get("insight") or summary.get("text") or "")
    else:
        insight = str(summary or "")
    return {"insight": insight}


def _apply_reset_origin(filters: list[dict[str, Any]]) -> tuple[int, int]:
    """Place the 1x1 apply-reset control on the first filter row, to the right of those filters."""
    row_y = min(int(entry.get("y") or 0) for entry in filters)
    row = [entry for entry in filters if int(entry.get("y") or 0) == row_y]
    right = max(int(entry.get("x") or 0) + int(entry.get("w") or 1) for entry in row)
    row_h = max(int(entry.get("h") or 1) for entry in row)
    if right + 1 <= GRID_COLUMNS:
        return right, row_y
    return 0, row_y + row_h


def _output_item(base: dict[str, Any] | None, *, component_id: str, dashboard_model: dict[str, Any]) -> dict[str, Any]:
    source = dict(base or {})
    model_dict = dict(dashboard_model or {})
    kind = Kind.wire(str(model_dict.get("kind") or Kind.INSTANT))
    item = {
        "component_id": component_id,
        "itemId": component_id,
        **model_dict,
    }
    if kind == Kind.INSTANT:
        viz_model = source.get("viz_model")
        if not isinstance(viz_model, dict):
            nested = source.get("viz") if isinstance(source.get("viz"), dict) else {}
            viz_model = nested.get("viz_model") if isinstance(nested.get("viz_model"), dict) else None
        report = source.get("report_model") if isinstance(source.get("report_model"), dict) else {}
        data_model = source.get("data_model")
        if data_model is None and isinstance(report.get("data_model"), dict):
            data_model = report.get("data_model")
        if not isinstance(viz_model, dict) and isinstance(report.get("viz_model"), dict):
            viz_model = report.get("viz_model")
        item["report_model"] = {
            "data_model": data_model,
            "viz_model": viz_model,
        }
        seq = str(source.get("chat_sequence_id") or source.get("id") or "")
        item["id"] = source.get("id") or source.get("chat_sequence_id")
        item["chat_sequence_id"] = seq
        item["itemId"] = seq or component_id
        item["user_query"] = source.get("user_query")
        item["summary"] = _summary_insight(source)
    return item


def _extra_size(kind: str) -> tuple[int, int]:
    if kind == "kpi":
        return _KPI_W, _KPI_H
    if kind == "filter":
        return _FILTER_W, _FILTER_H
    if kind in {"summary", "text"}:
        return GRID_COLUMNS, _SUMMARY_H
    if kind in {"svg", "image", "separator"}:
        return GRID_COLUMNS, _SUMMARY_H
    return _DEFAULT_VIZ_W, _DEFAULT_VIZ_H


def _push_extra(widget_rects: list[dict[str, Any]], used_ids: set[str], *, kind: str, **fields: Any) -> None:
    # Extras never keep LLM-chosen names; always assign a random id.
    aid = new_component_id(used_ids)
    default_w, default_h = _extra_size(kind)
    x, y, w, h = _widget_size(fields, default_w=default_w, default_h=default_h, min_w=1)
    widget_rects.append(
        {
            "kind": kind,
            "component_id": aid,
            "title": str(fields.get("title") or ""),
            "header_title": str(fields.get("header_title") or fields.get("title") or ""),
            "x": x,
            "y": y,
            "w": w,
            "h": h,
            "css": str(fields.get("css") or "")[:4000],
            "js": str(fields.get("js") or "")[:4000],
            "html": str(fields.get("html") or fields.get("text") or fields.get("svg") or "")[:4000],
            "column": str(fields.get("column") or ""),
            "table": str(fields.get("table") or ""),
            "sourceItemId": str(fields.get("sourceItemId") or ""),
            "default_values": _default_values(fields),
            "export": bool(fields.get("export", True)),
        }
    )


def _ensure_required_extras(
    widget_rects: list[dict[str, Any]],
    items: list[dict[str, Any]],
    *,
    used_ids: set[str],
) -> None:
    present = {str(row.get("kind") or "") for row in widget_rects}
    if "summary" not in present and "text" not in present:
        text = " ".join(str(item.get("summary") or "").strip() for item in items).strip()
        if not text:
            queries = [str(item.get("user_query") or "").strip() for item in items]
            text = next((query for query in queries if query), "Key insights")
        _push_extra(
            widget_rects,
            used_ids,
            kind="summary",
            title="Summary",
            html=text[:400],
            x=0,
            y=0,
            w=GRID_COLUMNS,
            h=2 if len(text) > 140 else _SUMMARY_H,
        )
    if "filter" not in present:
        filter_x = 0
        filter_y = 1
        for entry in _item_filters(items)[:4]:
            _push_extra(
                widget_rects,
                used_ids,
                kind="filter",
                title=entry.get("column") or "Filter",
                column=entry.get("column") or "",
                table=entry.get("table") or "",
                sourceItemId=entry.get("sourceItemId") or "",
                x=filter_x,
                y=filter_y,
                w=_FILTER_W,
                h=_FILTER_H,
            )
            filter_x += _FILTER_W
            if filter_x >= GRID_COLUMNS:
                filter_x = 0
                filter_y += _FILTER_H
    if "kpi" not in present:
        measures: list[str] = []
        for item in items:
            for name in _viz_measures(item):
                if name not in measures:
                    measures.append(name)
        if not measures:
            measures = ["Key metric"]
        kpi_x = 0
        for index, name in enumerate(measures[:4]):
            _push_extra(
                widget_rects,
                used_ids,
                kind="kpi",
                title=name,
                html=name,
                x=kpi_x,
                y=2,
                w=_KPI_W,
                h=_KPI_H,
            )
            kpi_x += _KPI_W
            if kpi_x >= GRID_COLUMNS:
                kpi_x = 0
    if not present.intersection({"svg", "image", "separator"}):
        _push_extra(
            widget_rects,
            used_ids,
            kind="svg",
            title="",
            html=_FALLBACK_SVG,
            x=0,
            y=4,
            w=GRID_COLUMNS,
            h=1,
        )


def default_layout(items: list[dict[str, Any]]) -> dict[str, Any]:
    """Fallback composition when the LLM is skipped or returns an empty layout."""
    y = 0
    used = {_item_key(item) for item in items if _item_key(item)}
    extra: list[dict[str, Any]] = []
    filters = _item_filters(items)
    if filters:
        filter_x = 0
        for entry in filters:
            if filter_x + _FILTER_W > GRID_COLUMNS:
                filter_x = 0
                y += _FILTER_H
            extra.append(
                {
                    **entry,
                    "component_id": new_component_id(used),
                    "x": filter_x,
                    "y": y,
                    "w": _FILTER_W,
                    "h": _FILTER_H,
                }
            )
            filter_x += _FILTER_W
        y += _FILTER_H

    x = 0
    widgets: list[dict[str, Any]] = []
    for item in items:
        aid = _item_key(item)
        if not aid:
            continue
        kpi = _is_kpi(item)
        w = _KPI_W if kpi else _DEFAULT_VIZ_W
        h = _KPI_H if kpi else _DEFAULT_VIZ_H
        if x + w > GRID_COLUMNS:
            x = 0
            y += h
        widgets.append(
            {
                "kind": "viz",
                "component_id": aid,
                "x": x,
                "y": y,
                "w": w,
                "h": h,
            }
        )
        x += w
        if x >= GRID_COLUMNS:
            x = 0
            y += h

    return {
        "dashboard_config": _dashboard_config({}),
        "widgets": widgets + extra,
    }


def apply_decision(
    items: list[dict[str, Any]],
    decision: dict[str, Any] | None,
) -> dict[str, Any]:
    """Merge LLM widgets onto chat items, clamp overlaps, emit dashboard_model items."""
    fallback = default_layout(items)
    payload = decision if isinstance(decision, dict) else {}
    by_aid = {_item_key(item): item for item in items if _item_key(item)}
    known = set(by_aid)
    used_ids = set(known)

    raw_widgets = [row for row in (payload.get("widgets") or []) if isinstance(row, dict)]
    if not raw_widgets:
        for row in payload.get("layout") or []:
            if not isinstance(row, dict):
                continue
            raw_widgets.append(
                {
                    "kind": "viz",
                    "component_id": str(row.get("component_id") or row.get("itemId") or ""),
                    "x": row.get("x"),
                    "y": row.get("y"),
                    "w": row.get("w") or row.get("width"),
                    "h": row.get("h") or row.get("height"),
                    "css": row.get("css") or "",
                    "js": row.get("js") or "",
                    "html": row.get("html") or "",
                }
            )
        summary_raw = payload.get("summary") if isinstance(payload.get("summary"), dict) else {}
        summary_text = str(summary_raw.get("text") or summary_raw.get("html") or "").strip()
        if summary_text:
            raw_widgets.append(
                {
                    "kind": "summary",
                    "text": summary_text,
                    "html": summary_text,
                    "x": 0,
                    "y": 0,
                    "w": GRID_COLUMNS,
                    "h": 2 if len(summary_text) > 140 else _SUMMARY_H,
                }
            )
        for decoration in payload.get("decorations") or []:
            if isinstance(decoration, dict):
                raw_widgets.append({**decoration, "kind": decoration.get("kind") or "separator"})
    if not any(str(row.get("kind") or "viz").lower() == "viz" for row in raw_widgets):
        raw_widgets = list(fallback["widgets"]) + raw_widgets

    widget_rects: list[dict[str, Any]] = []
    seen_viz: set[str] = set()
    for raw in raw_widgets:
        if not isinstance(raw, dict):
            continue
        kind = _normalize_kind(
            raw.get("kind") or "viz",
            aid=str(raw.get("component_id") or raw.get("itemId") or "").strip(),
            known=known,
        )
        aid = str(raw.get("component_id") or raw.get("itemId") or "").strip()
        if not kind:
            continue
        if kind == "viz":
            if aid not in known or aid in seen_viz:
                continue
            seen_viz.add(aid)
            default_w, default_h = (_KPI_W, _KPI_H) if _is_kpi(by_aid[aid]) else (_DEFAULT_VIZ_W, _DEFAULT_VIZ_H)
            x, y, w, h = _widget_size(raw, default_w=default_w, default_h=default_h)
            widget_rects.append(
                {
                    "kind": "viz",
                    "component_id": aid,
                    "title": str(raw.get("title") or ""),
                    "header_title": str(raw.get("header_title") or raw.get("title") or ""),
                    "header": raw.get("header") if isinstance(raw.get("header"), dict) else {},
                    "x": x,
                    "y": y,
                    "w": w,
                    "h": h,
                    "css": str(raw.get("css") or "")[:4000],
                    "js": str(raw.get("js") or "")[:4000],
                    "html": str(raw.get("html") or raw.get("svg") or "")[:4000],
                    "export": bool(raw.get("export", True)),
                }
            )
            continue
        if kind not in _EXTRA_KINDS:
            continue
        default_w, default_h = _extra_size(kind)
        # Ignore LLM component_id for extras (summary/kpi/filter/svg); assign randomly.
        aid = new_component_id(used_ids)
        x, y, w, h = _widget_size(raw, default_w=default_w, default_h=default_h, min_w=1)
        raw_col = raw.get("column")
        col_ref = (
            raw.get("column_ref")
            if isinstance(raw.get("column_ref"), dict)
            else (raw_col if isinstance(raw_col, dict) else None)
        )
        extra_row: dict[str, Any] = {
            "kind": kind,
            "component_id": aid,
            "x": x,
            "y": y,
            "w": w,
            "h": h,
            "css": str(raw.get("css") or "")[:4000],
            "js": str(raw.get("js") or "")[:4000],
            "html": str(raw.get("html") or raw.get("text") or raw.get("svg") or "")[:4000],
            "title": str(raw.get("title") or ""),
            "header_title": str(raw.get("header_title") or raw.get("title") or ""),
            "header": raw.get("header") if isinstance(raw.get("header"), dict) else {},
            "column": column_leaf(raw.get("column") if raw.get("column") not in (None, "") else raw.get("name")),
            "table": column_table(
                raw.get("column") if raw.get("column") not in (None, "") else raw.get("name"),
                raw.get("table"),
            ),
            "sourceItemId": str(raw.get("sourceItemId") or (next(iter(known), ""))),
            "default_values": _default_values(raw),
            "export": bool(raw.get("export", True)),
        }
        if col_ref:
            extra_row["column_ref"] = col_ref
        widget_rects.append(extra_row)

    for aid, item in by_aid.items():
        if aid in seen_viz:
            continue
        match = next((row for row in fallback["widgets"] if row.get("component_id") == aid), None)
        if match:
            widget_rects.append({**match, "css": "", "js": "", "html": "", "export": True})

    _ensure_required_extras(widget_rects, items, used_ids=used_ids)

    dashboard_config = _dashboard_config(payload)
    parameters = _dashboard_parameters(payload, config=dashboard_config)
    dashboard_config = {**dashboard_config, "parameters": parameters}
    title = str(payload.get("title") or "").strip()
    if not title:
        title = next(
            (str(item.get("user_query") or "").strip() for item in items if str(item.get("user_query") or "").strip()),
            "Dashboard",
        )

    clamped = resolve_overlaps(widget_rects)
    _sync_source_filters(
        by_aid,
        [row for row in clamped if str(row.get("kind") or "") == "filter"],
    )
    output_items: list[dict[str, Any]] = []
    filters: list[dict[str, Any]] = []
    decorations: list[dict[str, Any]] = []
    summary = {"title": "", "text": "", "x": 0, "y": 0, "w": 12, "h": 0}

    for row in clamped:
        kind = str(row.get("kind") or "viz")
        aid = str(row.get("component_id") or "")
        model = _dashboard_model(
            row,
            kind=kind,
            item=by_aid.get(aid) if kind == "viz" else None,
        )
        if kind == "viz":
            output_items.append(_output_item(by_aid.get(aid), component_id=aid, dashboard_model=model))
        else:
            output_items.append(_output_item(None, component_id=aid, dashboard_model=model))
            if kind == "filter":
                default_values = list(model.get("defaultValues") or [])
                filters.append(
                    {
                        "column": row.get("column"),
                        "table": row.get("table"),
                        "sourceItemId": row.get("sourceItemId"),
                        "defaultValues": default_values,
                        "x": row["x"],
                        "y": row["y"],
                        "w": row["w"],
                        "h": row["h"],
                        "component_id": aid,
                    }
                )
            elif kind in {"summary", "text"}:
                summary = {
                    "title": str(row.get("title") or model.get("title") or ""),
                    "text": str(model.get("html") or ""),
                    "x": row["x"],
                    "y": row["y"],
                    "w": row["w"],
                    "h": row["h"],
                }
            else:
                decorations.append(
                    {
                        "kind": kind,
                        "component_id": aid,
                        "x": row["x"],
                        "y": row["y"],
                        "w": row["w"],
                        "h": row["h"],
                        "html": row.get("html") or "",
                        "css": row.get("css") or "",
                        "js": row.get("js") or "",
                    }
                )

    if filters and parameters.get("floatingFilter"):
        origin_x, origin_y = _apply_reset_origin(filters)
        output_items.append(ApplyResetComponent(x=origin_x, y=origin_y).to_dict())

    return {
        "title": title,
        "dashboard_config": dashboard_config,
        "items": output_items,
        "summary": summary,
        "sections": [],
        "filters": filters,
        "decorations": decorations,
    }
