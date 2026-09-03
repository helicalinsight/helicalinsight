"""Deterministic 12-column grid clamp and overlap repair for convert-dashboard."""
from __future__ import annotations

from typing import Any

from helicalbi.core.dashboardflow.collect_items import new_component_id

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
_EXTRA_KINDS = {"summary", "separator", "filter", "kpi", "svg", "image"}


def _normalize_kind(kind: str, *, aid: str, known: set[str]) -> str:
    value = str(kind or "viz").strip().lower()
    aliases = {
        "chart": "viz",
        "report": "viz",
        "insight": "summary",
        "overview": "summary",
        "slicer": "filter",
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


def _viz_rows(item: dict[str, Any]) -> list[str]:
    model = item.get("viz_model") if isinstance(item.get("viz_model"), dict) else {}
    if not model:
        model = ((item.get("viz") or {}).get("viz_model") or {}) if isinstance(item.get("viz"), dict) else {}
    data = model.get("data") if isinstance(model.get("data"), dict) else {}
    names: list[str] = []
    for raw in list(data.get("rows") or []) + list(data.get("filters") or []):
        if isinstance(raw, str) and raw.strip():
            names.append(raw.strip())
        elif isinstance(raw, dict):
            name = str(raw.get("name") or raw.get("column") or raw.get("alias") or "").strip()
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
            names.append(raw.strip())
        elif isinstance(raw, dict):
            name = str(raw.get("name") or raw.get("column") or raw.get("alias") or "").strip()
            if name:
                names.append(name)
    return names


def _item_key(item: dict[str, Any]) -> str:
    return str(item.get("component_id") or item.get("id") or "")


def _widget_size(raw: dict[str, Any], *, default_w: int, default_h: int, min_w: int = 1, min_h: int = 1):
    w = raw.get("w") if raw.get("w") not in (None, "") else raw.get("width")
    h = raw.get("h") if raw.get("h") not in (None, "") else raw.get("height")
    return clamp_rect(raw.get("x"), raw.get("y"), w or default_w, h or default_h, min_w=min_w, min_h=min_h)


def _item_filters(items: list[dict[str, Any]]) -> list[dict[str, Any]]:
    """Dashboard slicers only from filters that already exist on chat viz items."""
    filters: list[dict[str, Any]] = []
    item_ids = [_item_key(entry) for entry in items if _item_key(entry)]
    seen: set[tuple[str, str]] = set()
    for item in items:
        sql_filters = []
        model = item.get("viz_model") if isinstance(item.get("viz_model"), dict) else {}
        viz_filters = (model.get("data") or {}).get("filters") or []
        if not viz_filters:
            viz_filters = (((item.get("viz") or {}).get("viz_model") or {}).get("data") or {}).get("filters") or []
        data_filters = (item.get("data_model") or {}).get("filters") or [] if isinstance(item.get("data_model"), dict) else []
        row_dims = [{"name": name} for name in _viz_rows(item)]
        for raw in list(sql_filters) + list(viz_filters) + list(data_filters) + row_dims:
            if not isinstance(raw, dict):
                continue
            column = str(raw.get("column") or raw.get("name") or "").strip()
            table = str(raw.get("table") or "").strip()
            key = (table.lower(), column.lower())
            if not column or key in seen:
                continue
            seen.add(key)
            filters.append(
                {
                    "kind": "filter",
                    "column": column,
                    "table": table,
                    "sourceItemId": _item_key(item) or (item_ids[0] if item_ids else ""),
                    "listeners": list(item_ids),
                    "x": 0,
                    "y": 0,
                    "w": _FILTER_W,
                    "h": _FILTER_H,
                }
            )
    return filters


def _title_from_item(item: dict[str, Any] | None, *, kind: str, raw: dict[str, Any]) -> str:
    source = item or {}
    model = source.get("viz_model") if isinstance(source.get("viz_model"), dict) else {}
    props = model.get("properties") if isinstance(model.get("properties"), dict) else {}
    viz = source.get("viz") if isinstance(source.get("viz"), dict) else {}
    if kind == "filter":
        return str(raw.get("title") or raw.get("column") or "Filter")
    if kind == "summary":
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


def _dashboard_model(raw: dict[str, Any], *, kind: str, item: dict[str, Any] | None = None) -> dict[str, Any]:
    html = str(raw.get("html") or raw.get("text") or raw.get("svg") or "")[:4000]
    model = {
        "kind": kind,
        "title": _title_from_item(item, kind=kind, raw=raw),
        "layout": {
            "x": int(raw.get("x") or 0),
            "y": int(raw.get("y") or 0),
            "w": int(raw.get("w") or 0),
            "h": int(raw.get("h") or 0),
        },
        "css": str(raw.get("css") or "")[:4000],
        "js": str(raw.get("js") or "")[:4000],
        "html": html,
    }
    if kind == "filter":
        model["column"] = str(raw.get("column") or "")
        model["table"] = str(raw.get("table") or "")
        model["listeners"] = list(raw.get("listeners") or [])
        model["sourceItemId"] = str(raw.get("sourceItemId") or "")
    return model


def _output_item(base: dict[str, Any] | None, *, component_id: str, dashboard_model: dict[str, Any]) -> dict[str, Any]:
    source = dict(base or {})
    kind = str((dashboard_model or {}).get("kind") or "viz")
    item = {
        "component_id": component_id,
        "dashboard_model": dashboard_model,
    }
    if kind == "viz":
        viz_model = source.get("viz_model")
        if not isinstance(viz_model, dict):
            nested = source.get("viz") if isinstance(source.get("viz"), dict) else {}
            viz_model = nested.get("viz_model") if isinstance(nested.get("viz_model"), dict) else None
        item["report_model"] = {
            "data_model": source.get("data_model"),
            "viz_model": viz_model,
        }
        item["id"] = source.get("id") or source.get("chat_sequence_id")
        item["user_query"] = source.get("user_query")
        item["summary"] = source.get("summary")
    return item


def _extra_size(kind: str) -> tuple[int, int]:
    if kind == "kpi":
        return _KPI_W, _KPI_H
    if kind == "filter":
        return _FILTER_W, _FILTER_H
    if kind == "summary":
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
            "x": x,
            "y": y,
            "w": w,
            "h": h,
            "css": str(fields.get("css") or "")[:4000],
            "js": str(fields.get("js") or "")[:4000],
            "html": str(fields.get("html") or fields.get("text") or fields.get("svg") or "")[:4000],
            "column": str(fields.get("column") or ""),
            "table": str(fields.get("table") or ""),
            "listeners": list(fields.get("listeners") or []),
            "sourceItemId": str(fields.get("sourceItemId") or ""),
        }
    )


def _ensure_required_extras(
    widget_rects: list[dict[str, Any]],
    items: list[dict[str, Any]],
    *,
    used_ids: set[str],
    known: set[str],
) -> None:
    present = {str(row.get("kind") or "") for row in widget_rects}
    listeners = list(known)
    if "summary" not in present:
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
                listeners=listeners,
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
        "theme": {"color": "#1677ff", "background": "#ffffff"},
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
                    "x": x,
                    "y": y,
                    "w": w,
                    "h": h,
                    "css": str(raw.get("css") or "")[:4000],
                    "js": str(raw.get("js") or "")[:4000],
                    "html": str(raw.get("html") or raw.get("svg") or "")[:4000],
                }
            )
            continue
        if kind not in _EXTRA_KINDS:
            continue
        default_w, default_h = _extra_size(kind)
        # Ignore LLM component_id for extras (summary/kpi/filter/svg); assign randomly.
        aid = new_component_id(used_ids)
        x, y, w, h = _widget_size(raw, default_w=default_w, default_h=default_h, min_w=1)
        listeners = [
            str(listener)
            for listener in (raw.get("listeners") or list(known))
            if str(listener) in known
        ]
        widget_rects.append(
            {
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
                "column": str(raw.get("column") or ""),
                "table": str(raw.get("table") or ""),
                "listeners": listeners,
                "sourceItemId": str(raw.get("sourceItemId") or (next(iter(known), ""))),
            }
        )

    for aid, item in by_aid.items():
        if aid in seen_viz:
            continue
        match = next((row for row in fallback["widgets"] if row.get("component_id") == aid), None)
        if match:
            widget_rects.append({**match, "css": "", "js": "", "html": ""})

    _ensure_required_extras(widget_rects, items, used_ids=used_ids, known=known)

    theme_raw = payload.get("theme") if isinstance(payload.get("theme"), dict) else {}
    theme = {
        "color": str(theme_raw.get("color") or fallback["theme"]["color"]),
        "background": str(theme_raw.get("background") or fallback["theme"]["background"]),
    }

    clamped = resolve_overlaps(widget_rects)
    output_items: list[dict[str, Any]] = []
    layout: list[dict[str, Any]] = []
    filters: list[dict[str, Any]] = []
    decorations: list[dict[str, Any]] = []
    summary = {"title": "", "text": "", "x": 0, "y": 0, "w": 12, "h": 0}

    for row in clamped:
        kind = str(row.get("kind") or "viz")
        aid = str(row.get("component_id") or "")
        model = _dashboard_model(row, kind=kind, item=by_aid.get(aid) if kind == "viz" else None)
        if kind == "viz":
            output_items.append(_output_item(by_aid.get(aid), component_id=aid, dashboard_model=model))
            layout.append(
                {
                    "itemId": str((by_aid.get(aid) or {}).get("id") or aid),
                    "component_id": aid,
                    "x": row["x"],
                    "y": row["y"],
                    "w": row["w"],
                    "h": row["h"],
                }
            )
        else:
            output_items.append(_output_item(None, component_id=aid, dashboard_model=model))
            if kind == "filter":
                filters.append(
                    {
                        "column": row.get("column"),
                        "table": row.get("table"),
                        "sourceItemId": row.get("sourceItemId"),
                        "listeners": row.get("listeners") or [],
                        "x": row["x"],
                        "y": row["y"],
                        "w": row["w"],
                        "h": row["h"],
                        "component_id": aid,
                    }
                )
            elif kind == "summary":
                summary = {
                    "title": "",
                    "text": str(row.get("html") or ""),
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

    return {
        "theme": theme,
        "items": output_items,
        "summary": summary,
        "sections": [],
        "filters": filters,
        "layout": layout,
        "decorations": decorations,
    }
