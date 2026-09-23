"""Deterministic VizModel construction (no LLM).

Builds shelves, chart type, and properties from SQL result metadata,
cube format strings, and optional user chart hints already on ModelState.
"""
from __future__ import annotations

import logging
import re
from typing import Any, Optional

from helicalbi.common.CubeInfoModel import (
    _cube_fields_by_name,
    _match_map_value,
    build_viz_column_context,
    extract_result_field_names,
)
from helicalbi.model.output.viz.ChartSettings import ChartSettings, DimensionSetting
from helicalbi.model.output.viz.VizModel import (
    VizChart,
    VizData,
    VizModel,
    VizProperties,
)
from helicalbi.viz._chart_selection import (
    _MEASURE_TOKENS,
    infer_chart_shape,
    possible_chart_options,
)
from helicalbi.viz._charts import resolve_chart_name
from helicalbi.viz._shelf_layout import arrange_shelves
from helicalbi.viz.report_object import _match_format_string

logger = logging.getLogger(__name__)

# Prefer common charts when several options match the data shape.
# Keys must match viz/charts/*.json names. ``table`` is last-resort
# only. Maps are chosen only when the user asks for a map.
# ``grid_table`` is omitted here — auto-picked only for multi-dimension
# results that include aggregates (see dims >= 2 branch in _pick_chart_type).
_CHART_PREFERENCE = (
    "bar",
    "column",
    "line",
    "area",
    "kpi",
    "progress",
    "radar",
    "heatmap",
    "relation",
    "pie",
    "donut",
    "gauge",
    "waterfall",
    "calendar",
    "wordcloud",
    "point",
)
_MAP_CHARTS = frozenset({"heatmap"})
_MAP_MARK_VIZ: dict[str, tuple[str, str]] = {
    "line": ("Maps", "Line"),
    "point": ("Maps", "Point"),
    "heatmap": ("Maps", "Heatmap"),
}
_LAT_TOKENS = frozenset({"lat", "latitude"})
_LON_TOKENS = frozenset({"lon", "lng", "longitude"})
_CITY_TOKENS = frozenset({"city", "cities"})
_STATE_TOKENS = frozenset({
    "state",
    "states",
    "province",
    "provinces",
    "state_province",
})
_COUNTRY_TOKENS = frozenset({"country", "countries"})
_WORLD_TOKENS = frozenset({"world"})
_PLACE_TOKENS = (
    _CITY_TOKENS
    | _STATE_TOKENS
    | _COUNTRY_TOKENS
    | _WORLD_TOKENS
    | frozenset({
        "county",
        "counties",
        "zip",
        "zipcode",
        "postal",
        "postalcode",
        "postcode",
        "address",
    })
)
# HelicalReports Geographic submenu keys (menu.jsx / geographicalSubTypes).
_GEO_ROLE_LAT = "lat"
_GEO_ROLE_LON = "long"
_GEO_ROLE_CITY = "city"
_GEO_ROLE_STATE = "state"
_GEO_ROLE_COUNTRY = "country"
_GEO_ROLE_WORLD = "world"
_AGG_SQL_RE = re.compile(
    r"\b(?:sum|count|avg|average|min|max)\s*\(|\bgroup\s+by\b",
    re.IGNORECASE,
)
_VIZ_UPDATE_ACTIONS = frozenset({"updt_viz", "updt_both", "viz_update"})
_VIZ_UPDATE_INTENTS = frozenset({
    "VIZ_UPDATE",
    "VISUALIZATION_UPDATE",
    "UPDT_VIZ",
    "UPDATE_VIZ",
})
_CONVERT_RE = re.compile(
    r"\b(convert(?:\s+to)?|change\s+to|switch\s+to|show\s+as|make\s+it\s+a)\b",
    re.IGNORECASE,
)
# Plain table matches every shape — keep it last-resort only.
# grid_table is reserved for multi-dimension + aggregate (see _pick_chart_type).
_FALLBACK_CHARTS = frozenset({"table"})
_ORDERED_PREFERENCE = (
    "line",
    "area",
)
# Time-part / date names — EXTRACT(QUARTER) is often typed numeric in result metadata.
_ORDERED_NAME_TOKENS = frozenset({
    "date",
    "datetime",
    "timestamp",
    "time",
    "year",
    "quarter",
    "qtr",
    "month",
    "week",
    "day",
    "hour",
    "period",
})
_ORDERED_FN_RE = re.compile(
    r"\b(year|quarter|qtr|month|week|dayofyear|day|hour|date_trunc|datetrunc|extract)\b"
    r"|sql\.datetime|sql\.date\.",
    re.IGNORECASE,
)

# HI report mark (parent) → allowed child viz values.
# ``mark`` on VizChart is the parent name; ``viz`` is one of the children
# (or "" when the mark has no children).
MARK_VIZ_CATALOG: list[dict[str, Any]] = [
    {
        "name": "Chart",
        "values": [
            "Bar",
            "Line",
            "Word cloud",
            "Arc",
            "Area",
            "Doughnut",
            "Point",
            "Waterfall",
            "Radar",
            "Progress",
            "Relation",
            "Calendar",
        ],
    },
    {"name": "Maps", "values": ["Line", "Point", "Heatmap"]},
    {"name": "Table", "values": ["Table"]},
    {"name": "Grid Table", "values": ["Grid table"]},
    {"name": "Card", "values": ["KPI", "Trend"]},
    {"name": "VF", "values": []},
]

# InstantBI catalog chart_type → (mark, viz) using MARK_VIZ_CATALOG.
_CHART_TYPE_TO_MARK_VIZ: dict[str, tuple[str, str]] = {
    "bar": ("Chart", "Bar"),
    "column": ("Chart", "Bar"),
    "line": ("Chart", "Line"),
    "area": ("Chart", "Area"),
    "donut": ("Chart", "Doughnut"),
    "doughnut": ("Chart", "Doughnut"),
    "pie": ("Chart", "Arc"),
    "arc": ("Chart", "Arc"),
    "gauge": ("Chart", "Arc"),
    "point": ("Chart", "Point"),
    "calendar": ("Chart", "Calendar"),
    "progress": ("Chart", "Progress"),
    "radar": ("Chart", "Radar"),
    "relation": ("Chart", "Relation"),
    "waterfall": ("Chart", "Waterfall"),
    "wordcloud": ("Chart", "Word cloud"),
    "text": ("Chart", "Word cloud"),
    "heatmap": ("Maps", "Heatmap"),
    "kpi": ("Card", "KPI"),
    "table": ("Table", "Table"),
    "grid_table": ("Grid Table", "Grid table"),
}

_META_ONLY_KEYS = frozenset({"rows", "row_count", "rowcount", "count", "total_rows"})
_TYPE_KEYS = ("type", "data_type", "dataType", "dtype", "columnType")


def _name_tokens(name: str) -> set[str]:
    text = str(name or "").strip().lower().replace("-", "_").replace(".", "_")
    if not text:
        return set()
    tokens = {part for part in re.split(r"[^\w]+|_", text) if part}
    compact = text.replace("_", "")
    if compact:
        tokens.add(compact)
    return tokens


def _candidate_field_names(
    data_types: Any = None,
    *,
    field_names: Optional[list[str]] = None,
    form_data: Optional[dict[str, Any]] = None,
) -> list[str]:
    names: list[str] = list(field_names or [])
    names.extend(extract_result_field_names(data_types))
    if isinstance(form_data, dict):
        for col in form_data.get("columns") or []:
            if not isinstance(col, dict):
                continue
            names.append(_display_name(col))
            names.append(_wire_column_path(col.get("column")))
    return _unique(names)


def infer_geographic_type(
    name: str,
    *,
    cube_metadata: Optional[list] = None,
) -> Optional[str]:
    """Map a field name / cube semantic to HelicalReports ``geographicType``.

    Values match the Geographic submenu: lat, long, world, country, state, city.
    """
    text = str(name or "").strip()
    if not text:
        return None
    tokens = _name_tokens(text)
    cube_index = _cube_fields_by_name(cube_metadata)
    cube_item = cube_index.get(text.lower()) or {}
    semantic = str(
        cube_item.get("semantic_type") or cube_item.get("semanticType") or ""
    ).strip().lower()

    if tokens & _LAT_TOKENS or "latitud" in semantic:
        return _GEO_ROLE_LAT
    if tokens & _LON_TOKENS or "longitud" in semantic:
        return _GEO_ROLE_LON
    if tokens & _CITY_TOKENS or "city" in semantic:
        return _GEO_ROLE_CITY
    if tokens & _STATE_TOKENS or "province" in semantic or "state_province" in semantic:
        return _GEO_ROLE_STATE
    if tokens & _COUNTRY_TOKENS or "country" in semantic:
        return _GEO_ROLE_COUNTRY
    if tokens & _WORLD_TOKENS:
        return _GEO_ROLE_WORLD
    if tokens & _PLACE_TOKENS:
        # County / postal / address → city GeoJSON role.
        return _GEO_ROLE_CITY
    if "geography" in semantic or semantic in {"geo", "geographic"}:
        return _GEO_ROLE_CITY
    return None


def geographic_roles_for_names(
    names: list[str],
    *,
    cube_metadata: Optional[list] = None,
) -> dict[str, str]:
    """Alias → HelicalReports geographicType for map-tagged fields."""
    roles: dict[str, str] = {}
    for name in _unique(names):
        role = infer_geographic_type(name, cube_metadata=cube_metadata)
        if role:
            roles[name] = role
    return roles


def apply_geographic_types_to_form_data(
    form_data: Optional[dict[str, Any]],
    roles: dict[str, str],
) -> Optional[dict[str, Any]]:
    """Stamp ``geographicType`` onto matching ``form_data`` columns (by alias)."""
    if not isinstance(form_data, dict) or not roles:
        return form_data
    index = {str(k).strip().lower(): v for k, v in roles.items() if k and v}
    if not index:
        return form_data
    payload = dict(form_data)
    columns: list[Any] = []
    for col in payload.get("columns") or []:
        if not isinstance(col, dict):
            columns.append(col)
            continue
        entry = dict(col)
        alias = str(entry.get("alias") or "").strip()
        path = _wire_column_path(entry.get("column"))
        leaf = path.rsplit(".", 1)[-1].strip() if path else ""
        role = (
            index.get(alias.lower())
            or index.get(leaf.lower())
            or index.get(path.lower())
        )
        if role:
            entry["geographicType"] = role
        columns.append(entry)
    payload["columns"] = columns
    return payload


def _is_distinct_only_aggregate(col: dict[str, Any]) -> bool:
    """True when the only applied aggregate is Distinct (SELECT DISTINCT col)."""
    keys = col.get("aggregateList")
    if not isinstance(keys, list) or len(keys) != 1:
        return False
    return "aggregate.distinct" in str(keys[0] or "").lower()


def _has_aggregate_columns(
    form_data: Optional[dict[str, Any]],
    sql: str,
    measure_count: int,
) -> bool:
    if isinstance(form_data, dict):
        for col in form_data.get("columns") or []:
            if not isinstance(col, dict):
                continue
            if _is_distinct_only_aggregate(col):
                continue
            if col.get("aggregate") or col.get("aggregateList"):
                return True
            if str(col.get("fieldType") or "").lower() == "measure":
                return True
    if _AGG_SQL_RE.search(sql or ""):
        return True
    return measure_count >= 1


def _unique(names: list[str]) -> list[str]:
    seen: set[str] = set()
    out: list[str] = []
    for name in names:
        text = str(name or "").strip()
        if not text:
            continue
        key = text.lower()
        if key in seen:
            continue
        seen.add(key)
        out.append(text)
    return out


def _type_token(raw: Any) -> str:
    if raw is None:
        return ""
    if isinstance(raw, dict):
        for key in _TYPE_KEYS:
            if key in raw and raw[key] is not None:
                return _type_token(raw[key])
        for key, value in raw.items():
            if isinstance(value, str) and value.strip():
                return value.strip().lower()
            key_l = str(key).lower()
            if any(
                part in key_l
                for part in (
                    "integer",
                    "long",
                    "short",
                    "float",
                    "double",
                    "decimal",
                    "bigdecimal",
                    "number",
                )
            ):
                return "numeric"
            if "string" in key_l or "char" in key_l:
                return "text"
        return ""
    return str(raw).strip().lower()


def _is_measure_token(token: str) -> bool:
    if not token:
        return False
    return token in _MEASURE_TOKENS or any(t in token for t in _MEASURE_TOKENS)


def _name_from_desc(desc: Any, *, fallback: str = "") -> str:
    """Prefer nested column descriptor names over index keys (e.g. ``\"1\"``)."""
    if isinstance(desc, dict):
        for key in ("name", "alias", "alias_name", "column", "column_name", "label"):
            text = str(desc.get(key) or "").strip()
            if text:
                return text
    return str(fallback or "").strip()


def _iter_named_roles(data_types: Any):
    """Yield (name, role) from executeQuery-style metadata.

    Supports shapes such as::

        [{'1': {'name': 'Travel Type', 'type': 'text'}}, {'rows': 5}]
        [{'name': 'Travel Type', 'type': 'text'}, {'rows': 5}]
    """
    if data_types is None:
        return

    def _yield_desc(desc: Any, *, fallback: str = ""):
        name = _name_from_desc(desc, fallback=fallback)
        if not name:
            return
        token = _type_token(desc)
        yield name, ("measure" if _is_measure_token(token) else "dimension")

    if isinstance(data_types, list):
        for item in data_types:
            if not isinstance(item, dict):
                continue
            keys = {str(k).lower() for k in item.keys()}
            if keys and keys <= _META_ONLY_KEYS:
                continue
            # Direct column descriptor: {"name": "...", "type": "text"}
            if any(k in item for k in ("name", "alias", "column_name", "type", "data_type")):
                yield from _yield_desc(item)
                continue
            # Indexed map: {"1": {"name": "...", "type": "text"}, ...}
            for key, value in item.items():
                if str(key).lower() in _META_ONLY_KEYS:
                    continue
                yield from _yield_desc(value, fallback=str(key))
        return
    if isinstance(data_types, dict):
        keys = {str(k).lower() for k in data_types.keys()}
        if keys and keys <= _META_ONLY_KEYS:
            return
        if any(k in data_types for k in ("name", "alias", "column_name", "type", "data_type")):
            yield from _yield_desc(data_types)
            return
        for key, value in data_types.items():
            if str(key).lower() in _META_ONLY_KEYS:
                continue
            yield from _yield_desc(value, fallback=str(key))


_QUERY_CHART_ALIASES = (
    ("donut", "donut"),
    ("doughnut", "donut"),
    ("pie", "pie"),
    ("arc", "pie"),
    ("gauge", "gauge"),
    ("wordcloud", "wordcloud"),
    ("word cloud", "wordcloud"),
    ("line", "line"),
    ("area", "area"),
    ("heatmap", "heatmap"),
    ("geo map", "heatmap"),
    ("map", "heatmap"),
    ("kpi", "kpi"),
    ("card", "kpi"),
    # Longer grid phrases first so "grid chart" / "grid table" win over "grid".
    ("grid chart", "grid_table"),
    ("grid table", "grid_table"),
    ("pivot table", "grid_table"),
    ("crosstab", "grid_table"),
    ("data table", "table"),
    ("grid", "table"),
    ("table", "table"),
    ("bar", "bar"),
    ("column", "column"),
    ("radar", "radar"),
    ("point", "point"),
    ("progress", "progress"),
    ("waterfall", "waterfall"),
    ("calendar", "calendar"),
    ("relation", "relation"),
)


_MAP_REQUEST_RE = re.compile(
    r"\b(?:heat\s*maps?|heatmaps?|geo\s*maps?|maps?)\b",
    re.IGNORECASE,
)


def _user_asked_for_map(user_query: str) -> bool:
    """True when the question names a map, heatmap, or geo map."""
    return bool(_MAP_REQUEST_RE.search(user_query or ""))


def _chart_named_in_query(user_query: str) -> Optional[str]:
    query = (user_query or "").lower()
    if not query:
        return None
    for alias, name in _QUERY_CHART_ALIASES:
        if re.search(rf"\b{alias}\b", query):
            resolved = resolve_chart_name(name)
            if resolved:
                return resolved
    return None


def is_viz_update_intent(action: str = "", intent: str = "") -> bool:
    """True when UpdateIntentRephrase / classifier marked a visualization update."""
    if str(action or "").strip().lower() in _VIZ_UPDATE_ACTIONS:
        return True
    token = (
        str(intent or "")
        .strip()
        .upper()
        .replace(" ", "_")
        .replace("-", "_")
    )
    return token in _VIZ_UPDATE_INTENTS


def _text_looks_ordered(text: str) -> bool:
    raw = str(text or "").strip()
    if not raw:
        return False
    if _name_tokens(raw) & _ORDERED_NAME_TOKENS:
        return True
    return bool(_ORDERED_FN_RE.search(raw))


def _form_data_has_ordered_dimension(form_data: Optional[dict[str, Any]]) -> bool:
    """True when a non-aggregate formData column is a date/time part (EXTRACT, etc.)."""
    if not isinstance(form_data, dict):
        return False
    for col in form_data.get("columns") or []:
        if not isinstance(col, dict):
            continue
        if col.get("aggregate") or col.get("aggregateList"):
            if not _is_distinct_only_aggregate(col):
                continue
        if _text_looks_ordered(_display_name(col)):
            return True
        dbf = col.get("databaseFunction")
        if isinstance(dbf, dict):
            dbf = str(dbf.get("function") or dbf.get("key") or dbf)
        if _text_looks_ordered(str(dbf or "")):
            return True
        if _text_looks_ordered(_wire_column_path(col.get("column"))):
            return True
    return False


def _names_or_cube_are_ordered(
    names: list[str],
    *,
    cube_metadata: Optional[list] = None,
) -> bool:
    cube_index = _cube_fields_by_name(cube_metadata)
    for name in names:
        if _text_looks_ordered(name):
            return True
        cube_item = cube_index.get(str(name or "").strip().lower()) or {}
        semantic = str(
            cube_item.get("semantic_type") or cube_item.get("semanticType") or ""
        ).lower()
        if any(
            marker in semantic
            for marker in ("date", "time", "temporal", "quarter", "year", "month")
        ):
            return True
    return False


def _detect_ordered(
    data_types: Any,
    *,
    ordered: bool = False,
    field_names: Optional[list[str]] = None,
    form_data: Optional[dict[str, Any]] = None,
    cube_metadata: Optional[list] = None,
) -> bool:
    """Prefer line/area when a shelf is a date or EXTRACT(year/quarter/month/...)."""
    if ordered:
        return True
    _, _, inferred = infer_chart_shape(data_types)
    if inferred:
        return True
    if _form_data_has_ordered_dimension(form_data):
        return True
    if isinstance(form_data, dict):
        # Shelves already inspected; do not treat measure names like "YoY" as ordered.
        return False
    names = _candidate_field_names(
        data_types, field_names=field_names, form_data=form_data
    )
    return _names_or_cube_are_ordered(names, cube_metadata=cube_metadata)


def _pick_chart_type(
    data_types: Any,
    *,
    viz_hint: str = "",
    user_query: str = "",
    viz_update: bool = False,
    dimension_count: Optional[int] = None,
    measure_count: Optional[int] = None,
    ordered: bool = False,
    form_data: Optional[dict[str, Any]] = None,
    cube_metadata: Optional[list] = None,
    sql: str = "",
    field_names: Optional[list[str]] = None,
) -> str:
    """Choose a catalog visualization_type without calling the LLM.

    On ``VIZ_UPDATE``, a chart named in the user request beats leftover
    ``viz_hint``. On a new / SQL query, leftover hint is ignored so the
    pick follows the actual result shape (unless the user named a chart).

    Maps are used only when the question names a map. Extra grouping
    columns become a table, or a grid table when aggregates are present.
    """
    requested = _chart_named_in_query(user_query)
    if viz_update:
        if requested:
            return requested
        hint = resolve_chart_name(viz_hint) if viz_hint else None
        if hint:
            return hint
    elif requested:
        return requested

    if dimension_count is not None and measure_count is not None:
        dims, measures = dimension_count, measure_count
    else:
        dims, measures, ordered = infer_chart_shape(data_types)
    ordered = _detect_ordered(
        data_types,
        ordered=ordered,
        field_names=field_names,
        form_data=form_data,
        cube_metadata=cube_metadata,
    )

    options = possible_chart_options(dims, measures, ordered)
    if not options:
        return "table"

    by_name = {opt.visualization_type: opt for opt in options}
    # Maps heatmap is geo-only; never auto-pick it for a generic matrix.
    for map_chart in _MAP_CHARTS:
        by_name.pop(map_chart, None)

    # Crosstab / Grid Table only when several dimensions break down an aggregate.
    if dims >= 2:
        has_agg = _has_aggregate_columns(form_data, sql, measures)
        if has_agg and "grid_table" in by_name:
            return "grid_table"
        if "table" in by_name:
            return "table"

    preference = _ORDERED_PREFERENCE + _CHART_PREFERENCE if ordered else _CHART_PREFERENCE
    for preferred in preference:
        if preferred in by_name and preferred not in _FALLBACK_CHARTS:
            return preferred
    for opt in options:
        name = opt.visualization_type
        if name in _MAP_CHARTS or name in _FALLBACK_CHARTS:
            continue
        if name in by_name:
            return name
    return "table"


def similar_charts_for_data(
    data_types: Any,
    *,
    current: str = "",
    limit: int = 8,
    dimension_count: Optional[int] = None,
    measure_count: Optional[int] = None,
    ordered: bool = False,
    form_data: Optional[dict[str, Any]] = None,
    cube_metadata: Optional[list] = None,
    field_names: Optional[list[str]] = None,
    sql: str = "",
) -> list[str]:
    """Chart types compatible with the result shape, excluding the current pick."""
    if dimension_count is not None and measure_count is not None:
        dims, measures = dimension_count, measure_count
    else:
        dims, measures, ordered = infer_chart_shape(data_types)
    ordered = _detect_ordered(
        data_types,
        ordered=ordered,
        field_names=field_names,
        form_data=form_data,
        cube_metadata=cube_metadata,
    )
    options = possible_chart_options(dims, measures, ordered)
    current_key = (resolve_chart_name(current) or current or "").strip().lower()
    skip = _FALLBACK_CHARTS | _MAP_CHARTS | {current_key, "other", ""}
    # Same rule as auto-pick: grid_table only for multi-dim + aggregates.
    has_agg = _has_aggregate_columns(form_data, sql, measures)
    if not (dims >= 2 and has_agg):
        skip = skip | {"grid_table"}
    by_name = {opt.visualization_type: opt for opt in options}
    ordered_names: list[str] = []
    preference = _ORDERED_PREFERENCE + _CHART_PREFERENCE if ordered else _CHART_PREFERENCE
    for name in preference:
        if name in by_name and name not in skip and name not in ordered_names:
            ordered_names.append(name)
    # Prefer grid_table over relation for multi-dim aggregate suggestions.
    if (
        dims >= 2
        and has_agg
        and "grid_table" in by_name
        and "grid_table" not in skip
        and "grid_table" not in ordered_names
    ):
        # Insert after common categorical charts, before relation/sankey-style.
        insert_at = len(ordered_names)
        for i, name in enumerate(ordered_names):
            if name == "relation":
                insert_at = i
                break
        ordered_names.insert(insert_at, "grid_table")
    for opt in options:
        name = opt.visualization_type
        if name not in skip and name not in ordered_names:
            ordered_names.append(name)
    return ordered_names[:limit]


def _roles_from_metadata(data_types: Any) -> tuple[list[str], list[str]]:
    dimensions: list[str] = []
    measures: list[str] = []
    for name, role in _iter_named_roles(data_types):
        if role == "measure":
            measures.append(name)
        else:
            dimensions.append(name)
    return _unique(dimensions), _unique(measures)


def _chart_viz_and_mark(chart_type: str, *, geo: bool = False) -> VizChart:
    """Map catalog type → VizChart(mark=HI parent, viz=child under that mark)."""
    key = resolve_chart_name(chart_type) or str(chart_type or "").strip().lower()
    if geo and key in _MAP_MARK_VIZ:
        mark, viz = _MAP_MARK_VIZ[key]
        return VizChart(viz=viz, mark=mark)
    mapped = _CHART_TYPE_TO_MARK_VIZ.get(key)
    if mapped:
        mark, viz = mapped
        return VizChart(viz=viz, mark=mark)

    # Unknown catalog types fall back to VF (custom / no child viz).
    return VizChart(viz="", mark="VF")


def _wire_column_path(column: Any) -> str:
    """Normalize formData column field (string or ``{name, id}``) to a path string."""
    if isinstance(column, dict):
        return str(column.get("name") or "").strip()
    return str(column or "").strip()


def _display_name(column_wire: dict) -> str:
    """Prefer SELECT alias (matches result headers); else leaf of column path."""
    alias = str(column_wire.get("alias") or "").strip()
    if alias:
        return alias
    path = _wire_column_path(column_wire.get("column"))
    if not path:
        return ""
    return path.rsplit(".", 1)[-1].strip()


def _align_to_result_fields(names: list[str], result_fields: list[str]) -> list[str]:
    """Map sql_to_formdata aliases onto executeQuery header names when possible."""
    if not names:
        return []
    if not result_fields:
        return _unique(names)
    index = {f.lower(): f for f in result_fields if f}
    aligned: list[str] = []
    for name in names:
        key = str(name or "").strip()
        if not key:
            continue
        canonical = index.get(key.lower())
        if not canonical:
            leaf = key.rsplit(".", 1)[-1].strip().lower()
            canonical = index.get(leaf)
        aligned.append(canonical or key)
    return _unique(aligned)


def _shelves_from_form_data(
    form_data: dict[str, Any],
    *,
    result_fields: Optional[list[str]] = None,
) -> tuple[list[str], list[str]]:
    """Map sql_to_formdata wire columns → VizModel shelves."""
    rows: list[str] = []
    columns: list[str] = []

    for col in form_data.get("columns") or []:
        if not isinstance(col, dict):
            continue
        name = _display_name(col)
        if not name:
            continue
        if col.get("hidden"):
            continue
        is_measure = bool(col.get("aggregate")) or (
            str(col.get("fieldType") or "").lower() == "measure"
        )
        # Distinct-only is still a categorical dimension (unique values list).
        if _is_distinct_only_aggregate(col):
            is_measure = False
        if is_measure:
            columns.append(name)
        else:
            rows.append(name)

    rows = _align_to_result_fields(rows, result_fields or [])
    columns = _align_to_result_fields(columns, result_fields or [])
    return _unique(rows), _unique(columns)


def _data_model_column_names(form_data: Optional[dict[str, Any]]) -> list[str]:
    """Return display/alias names for every column in sql_to_formdata wire data."""
    if not isinstance(form_data, dict):
        return []
    names: list[str] = []
    for col in form_data.get("columns") or []:
        if not isinstance(col, dict):
            continue
        name = _display_name(col)
        if name:
            names.append(name)
    return _unique(names)


def _build_properties_formatting(
    column_names: list[str],
    *,
    format_strings: Optional[dict[str, str]] = None,
    cube_metadata: Optional[list] = None,
) -> dict[str, str]:
    """Map data_model / result columns → Excel formatString from the semantic model."""
    if not column_names:
        return {}

    cube_index = _cube_fields_by_name(cube_metadata)
    formatting: dict[str, str] = {}
    for name in column_names:
        fmt = _match_format_string(name, format_strings or {})
        if not fmt:
            fmt = _match_map_value(format_strings, name)
        if not fmt:
            cube_item = cube_index.get(name.lower()) or {}
            fmt = cube_item.get("format_string") or cube_item.get("formatString")
        formatting[name] = str(fmt).strip() if fmt else ""
    return formatting


def _as_function_catalog(catalog: Any) -> Any:
    if catalog is None:
        return None
    from helicalbi.sql_to_formdata import FunctionCatalog

    if isinstance(catalog, FunctionCatalog):
        return catalog
    if isinstance(catalog, dict) and catalog:
        try:
            return FunctionCatalog.from_api_payload(catalog)
        except Exception:
            logger.debug("Unable to build FunctionCatalog from payload", exc_info=True)
    return None


def _try_sql_to_form_data(
    sql: str,
    *,
    session_cookie: str = "",
    md_location: str = "",
    md_file_name: str = "",
    dialect: str | None = None,
    metadata: dict[str, Any] | None = None,
    catalog: Any = None,
    rm_cols_in_filter: bool = True,
) -> Optional[dict[str, Any]]:
    """Run sql_to_formdata when metadata refs are available; else None.

    Does not need executeQuery results, so it can still populate
    ``data_model.columns`` after a SQL error.
    """
    from helicalbi.sql.SqlSanitizer import strip_sql_markdown

    text = strip_sql_markdown(sql or "").strip()
    location = (md_location or "").strip()
    file_name = (md_file_name or "").strip()
    if not text or not location or not file_name:
        logger.info(
            "viz_model_fill: sql_to_formdata skipped "
            "(sql=%s location=%s file=%s)",
            bool(text),
            bool(location),
            bool(file_name),
        )
        return None
    try:
        from helicalbi.sql_to_formdata import sql_to_form_data

        form_data = sql_to_form_data(
            text,
            location=location,
            metadata_dir=location,
            metadata_file_name=file_name,
            session_cookie=session_cookie or "",
            dialect=dialect,
            metadata=metadata,
            catalog=_as_function_catalog(catalog),
            rm_cols_in_filter=rm_cols_in_filter,
        )
        logger.info(
            "viz_model_fill: sql_to_formdata ok columns=%s filters=%s",
            len(form_data.get("columns") or []),
            len(form_data.get("filters") or []),
        )
        return form_data
    except Exception:
        logger.exception("viz_model_fill: sql_to_formdata failed; using metadata fallback")
        return None


def _shelves_from_metadata(
    data_types: Any,
    *,
    sample_row: Optional[dict] = None,
) -> tuple[list[str], list[str]]:
    """Infer rows (dimensions) and columns (measures) from result metadata."""
    rows: list[str] = []
    columns: list[str] = []
    for name, role in _iter_named_roles(data_types):
        if role == "measure":
            columns.append(name)
        else:
            rows.append(name)
    rows, columns = _unique(rows), _unique(columns)
    if rows or columns:
        return rows, columns

    if isinstance(sample_row, dict) and sample_row:
        dims: list[str] = []
        measures: list[str] = []
        for key, value in sample_row.items():
            name = str(key or "").strip()
            if not name:
                continue
            if isinstance(value, bool):
                dims.append(name)
            elif isinstance(value, (int, float)):
                measures.append(name)
            else:
                dims.append(name)
        if measures or dims:
            return _unique(dims), _unique(measures)

    field_names = extract_result_field_names(data_types)
    if not field_names:
        return [], []
    if len(field_names) == 1:
        return [], _unique(field_names)
    return _unique(field_names[:-1]), _unique(field_names[-1:])


def _resolve_shelves(
    *,
    data_types: Any,
    sql: str = "",
    sample_row: Optional[dict] = None,
    session_cookie: str = "",
    md_location: str = "",
    md_file_name: str = "",
    dialect: str | None = None,
    rm_cols_in_filter: bool = True,
) -> tuple[list[str], list[str], Optional[dict[str, Any]]]:
    """Prefer sql_to_formdata for rows/columns shelves."""
    result_fields = extract_result_field_names(data_types)
    form_data = _try_sql_to_form_data(
        sql,
        session_cookie=session_cookie,
        md_location=md_location,
        md_file_name=md_file_name,
        dialect=dialect,
        rm_cols_in_filter=rm_cols_in_filter,
    )

    if form_data is not None:
        rows, columns = _shelves_from_form_data(
            form_data, result_fields=result_fields
        )
        return rows, columns, form_data

    rows, columns = _shelves_from_metadata(data_types, sample_row=sample_row)
    return rows, columns, form_data


def _default_title(
    rows: list[str],
    columns: list[str],
    vf_title: str = "",
    *,
    dimensions: Optional[list[str]] = None,
    measures: Optional[list[str]] = None,
) -> str:
    if (vf_title or "").strip():
        return vf_title.strip()
    dim_names = list(dimensions or [])
    meas_names = list(measures or [])
    meas = (meas_names[0] if meas_names else "") or (columns[0] if columns else "")
    dim = (dim_names[0] if dim_names else "") or (rows[0] if rows else "")
    if meas and dim:
        return f"{meas} by {dim}"
    return meas or dim or "Visualization"


def build_viz_model(
    *,
    data_types: Any,
    sql: str = "",
    sample_row: Optional[dict] = None,
    viz_hint: str = "",
    user_query: str = "",
    vf_title: str = "",
    format_strings: Optional[dict[str, str]] = None,
    cube_metadata: Optional[list] = None,
    ai_instructions: Optional[dict] = None,
    sort_orders: Optional[list] = None,
    domain_context: str = "",
    session_cookie: str = "",
    md_location: str = "",
    md_file_name: str = "",
    dialect: str | None = None,
    viz_update: bool = False,
    rm_cols_in_filter: bool = True,
) -> tuple[VizModel, str, dict[str, Any]]:
    """Build a VizModel and related viz context.

    Rows / columns prefer ``sql_to_formdata`` (same path as instant-to-hr).
    At the end, shelves are always swapped into the preferred HI orientation
    (typically dimensions on columns, measures on rows).
    Returns ``(viz_model, chart_type, viz_column_context)``.
    """
    rows, columns, form_data = _resolve_shelves(
        data_types=data_types,
        sql=sql,
        sample_row=sample_row,
        session_cookie=session_cookie,
        md_location=md_location,
        md_file_name=md_file_name,
        dialect=dialect,
        rm_cols_in_filter=rm_cols_in_filter,
    )
    pick_kwargs = {
        "viz_hint": viz_hint,
        "user_query": user_query,
        "viz_update": viz_update,
        "form_data": form_data,
        "cube_metadata": cube_metadata,
        "sql": sql,
        "field_names": list(rows) + list(columns),
    }
    if form_data is not None:
        chart_type = _pick_chart_type(
            data_types,
            dimension_count=len(rows),
            measure_count=len(columns),
            **pick_kwargs,
        )
    else:
        chart_type = _pick_chart_type(data_types, **pick_kwargs)

    if form_data is not None:
        dimensions, measures = list(rows), list(columns)
    else:
        dimensions, measures = _roles_from_metadata(data_types)
        if not dimensions and not measures:
            dimensions, measures = list(rows), list(columns)
        else:
            # Keep shelf names that metadata did not classify.
            known = {n.lower() for n in dimensions + measures}
            for name in list(rows) + list(columns):
                if name.lower() in known:
                    continue
                if name in columns:
                    measures.append(name)
                else:
                    dimensions.append(name)
            dimensions, measures = _unique(dimensions), _unique(measures)

    # Always finish in preferred HI orientation (typically measures on rows,
    # dimensions on columns) — InstantBI form_data defaults are the reverse.
    rows, columns, swapped = arrange_shelves(
        chart_type,
        rows,
        columns,
        dimensions=dimensions,
        measures=measures,
        force_preferred=True,
    )
    if swapped:
        logger.info(
            "viz_model_fill swapped shelves chart=%s viz_update=%s "
            "force_preferred=True rows=%s columns=%s",
            chart_type,
            viz_update,
            rows,
            columns,
        )

    viz_context = build_viz_column_context(
        data_types,
        cube_metadata=cube_metadata or [],
        format_strings=format_strings or {},
        ai_instructions=ai_instructions or {},
        sort_orders=sort_orders or [],
        domain_context=domain_context or "",
    )
    if form_data is not None:
        viz_context = dict(viz_context)
        viz_context["form_data"] = form_data
    viz_context = dict(viz_context)
    similar_kwargs = {
        "current": chart_type,
        "form_data": form_data,
        "cube_metadata": cube_metadata,
        "field_names": list(rows) + list(columns),
        "sql": sql or "",
    }
    if form_data is not None:
        viz_context["similar_chart"] = similar_charts_for_data(
            data_types,
            dimension_count=len(rows),
            measure_count=len(columns),
            **similar_kwargs,
        )
    else:
        viz_context["similar_chart"] = similar_charts_for_data(
            data_types, **similar_kwargs
        )

    model_columns = _data_model_column_names(form_data)
    if not model_columns:
        model_columns = list(viz_context.get("field_names") or [])
    if not model_columns:
        model_columns = _unique(list(rows) + list(columns))
    formatting = _build_properties_formatting(
        model_columns,
        format_strings={
            **(format_strings or {}),
            **(viz_context.get("format_strings") or {}),
        },
        cube_metadata=cube_metadata,
    )

    title = _default_title(
        rows,
        columns,
        vf_title=vf_title,
        dimensions=dimensions,
        measures=measures,
    )
    dim_keys = {n.lower() for n in dimensions}
    meas_keys = {n.lower() for n in measures}
    label_x = next(
        (name for name in list(columns) + list(rows) if name.lower() in dim_keys),
        columns[0] if columns else None,
    )
    label_y = next(
        (name for name in list(rows) + list(columns) if name.lower() in meas_keys),
        rows[0] if rows else (columns[1] if len(columns) > 1 else None),
    )

    chart = _chart_viz_and_mark(
        chart_type,
        geo=_user_asked_for_map(user_query) and chart_type in {"line", "point"},
    )
    geographic_roles: dict[str, str] = {}
    if chart.mark == "Maps":
        # Use all result/form fields — shelf layout may drop a lat/lon pair.
        geo_names = _candidate_field_names(
            data_types,
            field_names=list(rows) + list(columns),
            form_data=form_data,
        )
        geographic_roles = geographic_roles_for_names(
            geo_names,
            cube_metadata=cube_metadata,
        )
        if geographic_roles and isinstance(form_data, dict):
            form_data = apply_geographic_types_to_form_data(
                form_data, geographic_roles
            )

    model = VizModel(
        data=VizData(
            rows=rows,
            columns=columns,
        ),
        chart=chart,
        properties=VizProperties(
            labelX=label_x,
            labelY=label_y,
            title=title,
            color="",
            formatting=formatting,
            **({"geographicRoles": geographic_roles} if geographic_roles else {}),
        ),
    )
    if form_data is not None:
        viz_context = dict(viz_context)
        viz_context["form_data"] = form_data
    if geographic_roles:
        viz_context = dict(viz_context)
        viz_context["geographic_roles"] = geographic_roles
    logger.info(
        "viz_model_fill built chart=%s viz=%s mark=%s rows=%s columns=%s "
        "geo_roles=%s source=%s",
        chart_type,
        model.chart.viz,
        model.chart.mark,
        rows,
        columns,
        geographic_roles,
        "sql_to_formdata" if form_data is not None else "metadata",
    )
    return model, chart_type, viz_context


def viz_model_to_chart_settings(
    model: VizModel,
    *,
    data_types: Any = None,
) -> ChartSettings:
    """Bridge VizModel shelves/properties → ChartSettings for VF injection.

    Dimensions/measures are classified from result metadata when provided so
    swapped HI shelves (measures on rows) still map to semantic ChartSettings.
    """
    props = model.properties
    rows = list(model.data.rows or [])
    columns = list(model.data.columns or [])
    if data_types is not None:
        dimensions, measures = _roles_from_metadata(data_types)
        dim_keys = {n.lower() for n in dimensions}
        meas_keys = {n.lower() for n in measures}
        dim_names = [
            name for name in rows + columns if name.lower() in dim_keys
        ]
        meas_names = [
            name for name in rows + columns if name.lower() in meas_keys
        ]
        if not dim_names and not meas_names:
            dim_names, meas_names = rows, columns
    else:
        dim_names, meas_names = rows, columns
    measure_formats = {
        name: fmt
        for name in meas_names
        for fmt in [_match_format_string(name, props.formatting or {})]
        if fmt
    }
    return ChartSettings(
        dimensions=DimensionSetting(names=_unique(dim_names)),
        measures=_unique(meas_names),
        labelsX=props.labelX,
        labelsY=props.labelY,
        title=props.title,
        color=props.color or None,
        measure_formats=measure_formats,
    )


_DROPPED_PROPERTY_KEYS = frozenset({"colorGradient", "theme", "formatter", "labelsX", "labelsY"})


def merge_properties_polish(model: VizModel, polish) -> VizModel:
    """Merge LLM polish fields into an existing VizModel (shelves/chart stay frozen)."""
    from helicalbi.model.output.viz.VizModel import VizProperties

    current = model.properties.model_dump()
    incoming = (
        polish.model_dump(exclude_none=True)
        if hasattr(polish, "model_dump")
        else {k: v for k, v in dict(polish or {}).items() if v is not None}
    )
    # Do not let polish wipe deterministic Excel-style formatting.
    incoming.pop("formatting", None)

    # Drop removed / renamed keys from both sides.
    for key in _DROPPED_PROPERTY_KEYS:
        current.pop(key, None)
        incoming.pop(key, None)

    for key in ("title", "labelX", "labelY"):
        text = str(incoming.get(key) or "").strip()
        if text:
            current[key] = text
        incoming.pop(key, None)

    if "color" in incoming:
        current["color"] = incoming.pop("color") or ""
    if "background" in incoming:
        current["background"] = incoming.pop("background") or None

    # Preserve unknown polish keys on properties (extra="allow"),
    # but never reintroduce removed property keys.
    for key, value in incoming.items():
        if key in _DROPPED_PROPERTY_KEYS:
            continue
        current[key] = value

    model.properties = VizProperties.model_validate(current)
    return model
