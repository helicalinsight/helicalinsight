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
# only — it matches every shape, so putting it earlier hid heatmaps
# and radar for multi-dimension / multi-measure results.
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
_FALLBACK_CHARTS = frozenset({"table", "grid_table"})
_ORDERED_PREFERENCE = (
    "line",
    "area",
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
    ("kpi", "kpi"),
    ("card", "kpi"),
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


def _pick_chart_type(
    data_types: Any,
    *,
    viz_hint: str = "",
    user_query: str = "",
    viz_update: bool = False,
) -> str:
    """Choose a catalog visualization_type without calling the LLM.

    On ``VIZ_UPDATE``, a chart named in the user request beats leftover
    ``viz_hint``. On a new / SQL query, leftover hint is ignored so the
    pick follows the actual result shape (unless the user named a chart).
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

    dims, measures, ordered = infer_chart_shape(data_types)
    options = possible_chart_options(dims, measures, ordered)
    if not options:
        return "table"

    by_name = {opt.visualization_type: opt for opt in options}
    preference = _ORDERED_PREFERENCE + _CHART_PREFERENCE if ordered else _CHART_PREFERENCE
    for preferred in preference:
        if preferred in by_name and preferred not in _FALLBACK_CHARTS:
            return preferred
    for opt in options:
        if opt.visualization_type not in _FALLBACK_CHARTS:
            return opt.visualization_type
    return "table"


def similar_charts_for_data(
    data_types: Any,
    *,
    current: str = "",
    limit: int = 8,
) -> list[str]:
    """Chart types compatible with the result shape, excluding the current pick."""
    dims, measures, ordered = infer_chart_shape(data_types)
    options = possible_chart_options(dims, measures, ordered)
    current_key = (resolve_chart_name(current) or current or "").strip().lower()
    skip = _FALLBACK_CHARTS | {current_key, "other", ""}
    by_name = {opt.visualization_type: opt for opt in options}
    ordered_names: list[str] = []
    preference = _ORDERED_PREFERENCE + _CHART_PREFERENCE if ordered else _CHART_PREFERENCE
    for name in preference:
        if name in by_name and name not in skip and name not in ordered_names:
            ordered_names.append(name)
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


def _chart_viz_and_mark(chart_type: str) -> VizChart:
    """Map catalog type → VizChart(mark=HI parent, viz=child under that mark)."""
    key = resolve_chart_name(chart_type) or str(chart_type or "").strip().lower()
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
        )
        if not (form_data.get("columns") or []):
            logger.info("viz_model_fill: sql_to_formdata returned no columns")
            return None
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
) -> tuple[list[str], list[str], Optional[dict[str, Any]]]:
    """Prefer sql_to_formdata for rows/columns shelves."""
    result_fields = extract_result_field_names(data_types)
    form_data = _try_sql_to_form_data(
        sql,
        session_cookie=session_cookie,
        md_location=md_location,
        md_file_name=md_file_name,
        dialect=dialect,
    )

    if form_data:
        rows, columns = _shelves_from_form_data(
            form_data, result_fields=result_fields
        )
        if rows or columns:
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
) -> tuple[VizModel, str, dict[str, Any]]:
    """Build a VizModel and related viz context.

    Rows / columns prefer ``sql_to_formdata`` (same path as instant-to-hr).
    Preferred shelf swap (dim on columns, measure on rows) runs only when
    ``viz_update`` is True and the user asked to convert / named a chart.
    Returns ``(viz_model, chart_type, viz_column_context)``.
    """
    requested = _chart_named_in_query(user_query)
    chart_type = _pick_chart_type(
        data_types,
        viz_hint=viz_hint,
        user_query=user_query,
        viz_update=viz_update,
    )
    rows, columns, form_data = _resolve_shelves(
        data_types=data_types,
        sql=sql,
        sample_row=sample_row,
        session_cookie=session_cookie,
        md_location=md_location,
        md_file_name=md_file_name,
        dialect=dialect,
    )

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

    convert_requested = bool(
        viz_update and (requested or _CONVERT_RE.search(user_query or ""))
    )
    rows, columns, swapped = arrange_shelves(
        chart_type,
        rows,
        columns,
        dimensions=dimensions,
        measures=measures,
        force_preferred=convert_requested,
    )
    if swapped:
        logger.info(
            "viz_model_fill swapped shelves chart=%s viz_update=%s "
            "force_preferred=%s rows=%s columns=%s",
            chart_type,
            viz_update,
            convert_requested,
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
    viz_context["similar_chart"] = similar_charts_for_data(
        data_types, current=chart_type
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

    model = VizModel(
        data=VizData(
            rows=rows,
            columns=columns,
        ),
        chart=_chart_viz_and_mark(chart_type),
        properties=VizProperties(
            labelX=label_x,
            labelY=label_y,
            title=title,
            color="",
            formatting=formatting,
        ),
    )
    logger.info(
        "viz_model_fill built chart=%s viz=%s mark=%s rows=%s columns=%s source=%s",
        chart_type,
        model.chart.viz,
        model.chart.mark,
        rows,
        columns,
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
