"""Convert an existing Ant Charts ``vf_template`` to another chart type (no LLM).

The wire format matches InstantBI: ``vf_template`` is base64-encoded UTF-8 JS/JSX.
Conversion extracts a semantic field bag from the source function, validates the
target chart's JSON ``conversion`` contract, fills that skeleton, and re-encodes.

Preserved across conversion (embedded in the filled ``vf_template``, not as new
response keys): Excel-style ``measureFormats`` and chart ``color`` palette/solid.
"""

from __future__ import annotations

import base64
import json
import logging
import re
from dataclasses import dataclass, field
from typing import Any, Iterable, Optional

from helicalbi.core.vizflow.util.ChartCodeTransform import transform_chart_code
from helicalbi.viz._chart_selection import (
    _DIMENSION_TOKENS,
    _MEASURE_TOKENS,
    _ORDERED_TOKENS,
    _TYPE_KEYS,
    _is_meta_entry,
    _normalize_type_token,
    _type_from_column_desc,
    resolve_similar_charts,
)
from helicalbi.viz._charts import (
    ChartConversion,
    ChartDefinition,
    get_chart_definition,
    resolve_chart_name,
)
from helicalbi.model.output.viz.ChartSettings import (
    ChartSettings,
    DimensionSetting,
)

logger = logging.getLogger(__name__)

# Fallback family heuristics when the source chart has no conversion block yet.
_FAMILY_BY_NAME: dict[str, str] = {
    "bar": "bar",
    "pie": "pie",
    "donut": "pie",
    "doughnut": "pie",
    "arc": "pie",
    "heatmap": "heatmap",
    "calendar": "heatmap",
    "bubble_chart": "bubble",
    "kpi": "kpi",
    "column_line": "dual_axes",
    "dual_line": "dual_axes",
    "grouped_column_line": "dual_axes",
    "stacked_column_line": "dual_axes",
    "stacked_and_grouped_column_line": "dual_axes",
    "tiny_line": "tiny",
    "tiny_column": "tiny",
    "tiny_area": "tiny",
    "treemap": "hierarchy",
    "circle_packing": "hierarchy",
    "relation": "hierarchy",
    "sunburst": "hierarchy",
    "gauge": "percent",
    "progress": "percent",
    "table": "table",
    "grid_table": "table",
    "pivot_table": "table",
    "histogram": "other",
    "wordcloud": "other",
    "other": "other",
    "waterfall": "cartesian",
    "funnel_chart": "cartesian",
    "radar": "cartesian",
    "rose_chart": "cartesian",
    "scatter": "cartesian",
    "column": "cartesian",
    "line": "cartesian",
    "area": "cartesian",
    "point": "cartesian",
}

_COMPONENT_TO_CHART: dict[str, str] = {
    "Bar": "bar",
    "Column": "column",
    "Line": "line",
    "Area": "area",
    "Scatter": "scatter",
    "Funnel": "funnel_chart",
    "DualAxes": "column_line",
    "Heatmap": "heatmap",
    "Radar": "radar",
    "Rose": "rose_chart",
    "WordCloud": "wordcloud",
    "Gauge": "gauge",
    "Waterfall": "waterfall",
    "Treemap": "treemap",
    "Sunburst": "sunburst",
    "Progress": "progress",
    "Histogram": "histogram",
    "Table": "table",
    "TinyLine": "tiny_line",
    "TinyColumn": "tiny_column",
    "TinyArea": "tiny_area",
    "CirclePacking": "circle_packing",
    "Card": "kpi",
    "Statistic": "kpi",
}

# Minimum bag sizes required before filling a target family.
_FAMILY_REQUIREMENTS: dict[str, dict[str, int]] = {
    "cartesian": {"dimensions": 1, "measures": 1},
    "bar": {"dimensions": 1, "measures": 1},
    "pie": {"dimensions": 1, "measures": 1},
    "tiny": {"dimensions": 1, "measures": 1},
    "dual_axes": {"dimensions": 1, "measures": 2},
    "heatmap": {"dimensions": 2, "measures": 1},
    "bubble": {"measures": 2},
    "hierarchy": {"dimensions": 1, "measures": 1},
    "percent": {"measures": 1},
    "kpi": {"measures": 1},
    "table": {},
    # Catch-all: per-field required flags enforce specifics (histogram, wordcloud, …).
    "other": {},
}

_STRING_FIELD = re.compile(
    r"(?P<key>xField|yField|angleField|colorField|seriesField|binField|"
    r"sizeField|wordField|weightField|groupField)\s*:\s*"
    r"(?:'(?P<sq>[^']+)'|\"(?P<dq>[^\"]+)\"|"
    r"\[(?P<arr>[^\]]+)\]|(?P<ident>[A-Za-z_][\w]*))",
)

_CONST_FIELD = re.compile(
    r"const\s+(?P<name>measureField|dimensionField|primaryDimension|"
    r"secondaryDimension|targetField|dimensionFields|measureFields)\s*=\s*"
    r"(?:'(?P<sq>[^']+)'|\"(?P<dq>[^\"]+)\"|"
    r"\[(?P<arr>[^\]]+)\])",
)

_TITLE_PATTERNS = (
    re.compile(r"title\s*:\s*\{[^}]*?text\s*:\s*'(?P<t>[^']+)'", re.DOTALL),
    re.compile(r"title\s*:\s*\{[^}]*?text\s*:\s*\"(?P<t>[^\"]+)\"", re.DOTALL),
    re.compile(r"formatter\s*:\s*\(\)\s*=>\s*'(?P<t>[^']+)'"),
    re.compile(r"formatter\s*:\s*\(\)\s*=>\s*\"(?P<t>[^\"]+)\""),
    re.compile(r"title\s*=\s*'(?P<t>[^']+)'"),
    re.compile(r"title\s*=\s*\"(?P<t>[^\"]+)\""),
)

_FUNCTION_NAME = re.compile(r"function\s+Draw([A-Za-z0-9_]+)\s*\(")
_JSX_COMPONENT = re.compile(r"<\s*([A-Z][A-Za-z0-9_]*)\b")


@dataclass
class ExtractedFields:
    """Semantic bag shared across Ant Design Charts v1 families."""

    dimensions: list[str] = field(default_factory=list)
    measures: list[str] = field(default_factory=list)
    series: Optional[str] = None
    title: Optional[str] = None
    label_x: Optional[str] = None
    label_y: Optional[str] = None
    label_z: Optional[str] = None
    source_chart: Optional[str] = None
    source_family: Optional[str] = None
    # Excel-style format strings keyed by measure / column name.
    measure_formats: dict[str, str] = field(default_factory=dict)
    # Raw JS expression for chart color (string literal or array), e.g. "'#5B8FF9'" or "['#a','#b']".
    color: Optional[str] = None


_MEASURE_FORMATS_BLOCK = re.compile(
    r"const\s+(?:measureFormats|columnFormats)\s*=\s*\{(?P<body>.*?)\n\s*\};",
    re.DOTALL,
)
_FORMAT_ENTRY = re.compile(
    r"(?:'(?P<ksq>[^']+)'|\"(?P<kdq>[^\"]+)\"|(?P<kident>[A-Za-z_$][\w$]*))"
    r"\s*:\s*"
    r"(?:'(?P<vsq>[^']*)'|\"(?P<vdq>[^\"]*)\")"
)
# Chart palette / solid color — not CSS style.color / valueStyle color.
_COLOR_VALUE = re.compile(
    r"(?<![.\w])color\s*:\s*(?P<expr>\[[^\]]*\]|'(?:\\'|[^'])*'|\"(?:\\\"|[^\"])*\")"
)
_VALUE_STYLE_COLOR = re.compile(
    r"valueStyle\s*=\s*\{\{\s*color\s*:\s*(?P<expr>'[^']*'|\"[^\"]*\")\s*\}\}"
)
_CONFIG_OPEN = re.compile(r"(const\s+config\s*=\s*\{\s*)")
_CONVERSION_HINTS_LINE = re.compile(
    r"^[ \t]*//\s*conversionHints\s*:\s*"
    r"(?P<body>.*?)\s*$",
    re.MULTILINE | re.IGNORECASE,
)
_HINT_FIELD = re.compile(
    r"(?P<key>xField|yField)\s*:\s*"
    r"(?:'(?P<sq>[^']+)'|\"(?P<dq>[^\"]+)\"|"
    r"\[(?P<arr>[^\]]*)\])",
    re.IGNORECASE,
)
_PLACEHOLDER_FORMAT_KEYS = frozenset(
    {
        "measure_column",
        "measure_column_2",
        "value_column",
        "target_column",
        "bin_column",
        "weight_column",
        "size_column",
    }
)

class ChartConversionError(ValueError):
    """Raised when chart conversion cannot proceed.

    When a target skeleton can still be produced (e.g. family requirements fail),
    ``viz`` carries the requested chart template so the client can edit and retry.
    """

    def __init__(self, message: str, *, viz: Optional[dict[str, Any]] = None):
        super().__init__(message)
        self.viz = viz


def decode_vf_template(encoded: str) -> str:
    """Decode a base64 ``vf_template`` into a JS/JSX function string."""
    if not isinstance(encoded, str) or not encoded.strip():
        raise ChartConversionError("vf_template is required.")
    try:
        return base64.b64decode(encoded).decode("utf-8")
    except Exception as exc:  # noqa: BLE001 - surface as conversion error
        raise ChartConversionError("vf_template is not valid base64 UTF-8.") from exc


def encode_vf_template(code: str) -> str:
    """Encode a JS/JSX function string as base64 for the InstantBI wire format."""
    return base64.b64encode((code or "").encode("utf-8")).decode("utf-8")


def color_expr_from_settings(color: Any) -> Optional[str]:
    """Convert a color value into a JS expression string."""
    if color is None or color == "":
        return None
    if isinstance(color, list):
        parts = [_js_string_literal(str(item).strip()) for item in color if str(item).strip()]
        return f"[{', '.join(parts)}]" if parts else None
    text = str(color).strip()
    if not text:
        return None
    # Already a JS array / quoted literal from extraction.
    if text.startswith("[") or text.startswith("'") or text.startswith('"'):
        return text
    return _js_string_literal(text)


def settings_to_fields(
    settings: ChartSettings,
    *,
    source_chart: Optional[str] = None,
    source_family: Optional[str] = None,
    format_strings: Optional[dict[str, str]] = None,
) -> ExtractedFields:
    """Map LLM / conversion ChartSettings onto the skeleton field bag."""
    formats = dict(settings.measure_formats or {})
    if isinstance(format_strings, dict):
        for key, value in format_strings.items():
            name = str(key or "").strip()
            fmt = str(value or "").strip()
            if name and fmt and name not in _PLACEHOLDER_FORMAT_KEYS:
                formats.setdefault(name, fmt)

    return ExtractedFields(
        dimensions=settings.dimension_names(),
        measures=[str(m).strip() for m in (settings.measures or []) if str(m).strip()],
        series=(str(settings.series).strip() if settings.series else None) or None,
        title=(settings.title or "").strip() or None,
        label_x=(settings.labelsX or "").strip() or None,
        label_y=(settings.labelsY or "").strip() or None,
        label_z=(settings.labelsZ or "").strip() or None,
        source_chart=source_chart,
        source_family=source_family,
        measure_formats=formats,
        color=color_expr_from_settings(settings.color),
    )


def fields_to_settings(fields: ExtractedFields) -> ChartSettings:
    """Map an extracted field bag back to ChartSettings (shared conversion shape)."""
    color_value: Any = None
    if fields.color:
        expr = fields.color.strip()
        if expr.startswith("[") and expr.endswith("]"):
            inner = expr[1:-1]
            color_value = [
                part.strip().strip("'\"")
                for part in inner.split(",")
                if part.strip().strip("'\"")
            ]
        else:
            color_value = expr.strip("'\"")

    dims = list(fields.dimensions or [])
    if len(dims) > 1:
        dimension = DimensionSetting(name=dims[0], names=dims)
    elif dims:
        dimension = DimensionSetting(name=dims[0])
    else:
        dimension = DimensionSetting()

    return ChartSettings(
        dimensions=dimension,
        measures=list(fields.measures or []),
        series=fields.series,
        labelsX=fields.label_x,
        labelsY=fields.label_y,
        labelsZ=fields.label_z,
        title=fields.title,
        color=color_value,
        measure_formats=dict(fields.measure_formats or {}),
    )


def _normalize_chart_settings(
    settings: ChartSettings,
    *,
    format_strings: Optional[dict[str, str]] = None,
) -> ChartSettings:
    """Fill blank labels / formats from field names before JS injection."""
    dims = settings.dimension_names()
    measures = [str(m).strip() for m in (settings.measures or []) if str(m).strip()]
    dim0 = dims[0] if dims else ""
    meas0 = measures[0] if measures else ""
    meas1 = measures[1] if len(measures) > 1 else ""

    formats = dict(settings.measure_formats or {})
    if isinstance(format_strings, dict):
        for key, value in format_strings.items():
            name = str(key or "").strip()
            fmt = str(value or "").strip()
            if name and fmt and name not in _PLACEHOLDER_FORMAT_KEYS:
                formats.setdefault(name, fmt)

    title = (settings.title or "").strip() or None
    if not title:
        if meas0 and dim0:
            title = f"{meas0} by {dim0}"
        else:
            title = meas0 or dim0 or None

    if len(dims) > 1:
        dimension = DimensionSetting(name=dims[0], names=dims)
    elif dims:
        dimension = DimensionSetting(name=dims[0])
    else:
        dimension = DimensionSetting(name=settings.dimensions.name)

    return ChartSettings(
        dimensions=dimension,
        measures=measures,
        labelsX=(settings.labelsX or "").strip() or dim0 or None,
        labelsY=(settings.labelsY or "").strip() or meas0 or None,
        labelsZ=(settings.labelsZ or "").strip() or meas1 or None,
        title=title,
        series=(str(settings.series).strip() if settings.series else None) or None,
        color=settings.color,
        measure_formats=formats,
    )


def settings_to_js_literal(settings: ChartSettings) -> str:
    """Serialize filled settings as a JS object literal for ``${setting}``."""
    return json.dumps(settings.to_js_object(), ensure_ascii=False, indent=2)


_SETTING_PLACEHOLDER = re.compile(r"\$\{\s*setting\s*\}")


def inject_setting_object(code: str, settings: ChartSettings) -> str:
    """Replace ``${setting}`` with the filled settings JS object literal."""
    literal = settings_to_js_literal(settings)
    if not _SETTING_PLACEHOLDER.search(code or ""):
        return code
    return _SETTING_PLACEHOLDER.sub(literal, code, count=1)


def apply_chart_settings(
    settings: ChartSettings,
    *,
    chart_def: Optional[ChartDefinition] = None,
    chart_name: Optional[str] = None,
    format_strings: Optional[dict[str, str]] = None,
) -> str:
    """Fill a chart skeleton from ChartSettings (LLM fill or conversion).

    Preferred path: chart ``code`` contains ``const setting = ${setting}`` and
    references ``setting.dimensions.name``, ``setting.measures[0]``, etc.

    Legacy path: charts without ``${setting}`` still use placeholder replacement
    via ``fill_skeleton``.
    """
    target = chart_def or (get_chart_definition(chart_name) if chart_name else None)
    if not target:
        raise ChartConversionError(
            f"Unknown chart type: {(chart_name or getattr(chart_def, 'name', None))!r}"
        )
    if not target.conversion:
        raise ChartConversionError(
            f"Chart type {target.name!r} has no conversion contract; "
            "add a conversion block to its chart JSON."
        )
    if not target.code:
        raise ChartConversionError(f"No code skeleton for chart type: {target.name}")

    normalized = _normalize_chart_settings(settings, format_strings=format_strings)
    fields = settings_to_fields(
        normalized,
        source_chart=target.name,
        source_family=target.conversion.family,
        format_strings=format_strings,
    )

    if _SETTING_PLACEHOLDER.search(target.code):
        code = inject_setting_object(target.code, normalized)
        code = _inject_conversion_hints(code, fields)
        return code.strip()

    return fill_skeleton(target.code, fields, conversion=target.conversion)


def convert_chart(
    vf_template: str,
    selected_chart: str,
    *,
    data_types: Any = None,
    vf_title: str = "",
    format_strings: Optional[dict[str, str]] = None,
) -> dict[str, Any]:
    """Convert ``vf_template`` to ``selected_chart`` without calling an LLM.

    Returns a viz-section style dict::

        {
            "vf_template": "<base64>",
            "chart_name": "bar",
            "vf_title": "...",
            "similar_chart": [...],
        }
    """
    target_def = get_chart_definition(selected_chart)
    if not target_def:
        raise ChartConversionError(f"Unknown chart type: {selected_chart!r}")

    if not target_def.conversion:
        raise ChartConversionError(
            f"Chart type {target_def.name!r} has no conversion contract; "
            "add a conversion block to its chart JSON."
        )
    if not target_def.code:
        raise ChartConversionError(f"No code skeleton for chart type: {target_def.name}")

    source_js = decode_vf_template(vf_template)
    if not source_js.strip():
        raise ChartConversionError("Decoded vf_template is empty.")

    fields = extract_fields(
        source_js,
        data_types=data_types,
        format_strings=format_strings,
    )
    title = (vf_title or fields.title or "").strip()
    chart_label = target_def.name.replace("_", " ")

    def _viz_payload(filled_code: str, *, reason: str) -> dict[str, Any]:
        return {
            "vf_template": encode_vf_template(filled_code),
            "chart_name": chart_label,
            "vf_title": title,
            "vf_reason": reason,
        }

    try:
        validate_conversion_requirements(target_def.conversion, fields)
    except ChartConversionError as exc:
        # Still return the requested skeleton (best-effort fill) so the UI can
        # let the user edit bindings and regenerate.
        filled = apply_chart_settings(
            fields_to_settings(fields),
            chart_def=target_def,
            format_strings=format_strings,
        )
        filled = transform_chart_code(filled)
        logger.info(
            "Conversion requirements failed target=%s dims=%s measures=%s error=%s; "
            "returning requested template for edit",
            target_def.name,
            fields.dimensions,
            fields.measures,
            exc,
        )
        raise ChartConversionError(
            str(exc),
            viz=_viz_payload(
                filled,
                reason=(
                    f"Could not fully convert to {chart_label}; "
                    "template returned for editing."
                ),
            ),
        ) from exc

    filled = apply_chart_settings(
        fields_to_settings(fields),
        chart_def=target_def,
        format_strings=format_strings,
    )
    filled = transform_chart_code(filled)

    #similar = resolve_similar_charts(target_def.name, data_types=data_types)

    logger.info(
        "Converted chart source=%s family=%s target=%s dims=%s measures=%s series=%s "
        "formats=%s color=%s",
        fields.source_chart,
        fields.source_family,
        target_def.name,
        fields.dimensions,
        fields.measures,
        fields.series,
        fields.measure_formats,
        bool(fields.color),
    )

    return _viz_payload(
        filled,
        reason=f"Converted visualization to {chart_label}.",
    )


def validate_conversion_requirements(
    conversion: ChartConversion,
    fields: ExtractedFields,
) -> None:
    """Raise if the semantic bag cannot satisfy the target family."""
    required = _FAMILY_REQUIREMENTS.get(conversion.family)
    if required is None:
        raise ChartConversionError(
            f"Unsupported conversion family: {conversion.family!r}"
        )

    dim_need = required.get("dimensions", 0)
    meas_need = required.get("measures", 0)
    if len(fields.dimensions) < dim_need:
        raise ChartConversionError(
            f"Chart family {conversion.family!r} needs at least {dim_need} "
            f"dimension(s); found {len(fields.dimensions)}."
        )
    if len(fields.measures) < meas_need:
        raise ChartConversionError(
            f"Chart family {conversion.family!r} needs at least {meas_need} "
            f"measure(s); found {len(fields.measures)}."
        )

    for placeholder, spec in conversion.fields.items():
        if spec.optional:
            continue
        value = _role_value(fields, spec.role, spec.index)
        if not value:
            raise ChartConversionError(
                f"Missing required field for placeholder {placeholder!r} "
                f"(role={spec.role!r}, index={spec.index})."
            )


_SETTING_ASSIGN = re.compile(r"const\s+setting\s*=\s*\{")


def _extract_balanced_object(text: str, open_index: int) -> Optional[str]:
    """Return the substring of a `{...}` object starting at ``open_index``."""
    if open_index < 0 or open_index >= len(text) or text[open_index] != "{":
        return None
    depth = 0
    in_str: Optional[str] = None
    escaped = False
    for i in range(open_index, len(text)):
        ch = text[i]
        if in_str:
            if escaped:
                escaped = False
            elif ch == "\\":
                escaped = True
            elif ch == in_str:
                in_str = None
            continue
        if ch in {'"', "'"}:
            in_str = ch
            continue
        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            if depth == 0:
                return text[open_index : i + 1]
    return None


def _extract_settings_object(source_js: str) -> Optional[ChartSettings]:
    """Parse a filled ``const setting = {...};`` block from chart JS when present."""
    match = _SETTING_ASSIGN.search(source_js or "")
    if not match:
        return None
    open_index = source_js.find("{", match.start())
    body = _extract_balanced_object(source_js, open_index)
    if not body:
        return None
    try:
        payload = json.loads(body)
    except json.JSONDecodeError:
        cleaned = re.sub(r",\s*}", "}", body)
        cleaned = re.sub(r",\s*]", "]", cleaned)
        try:
            payload = json.loads(cleaned)
        except json.JSONDecodeError:
            logger.debug("Unable to parse setting object from source chart JS")
            return None
    if not isinstance(payload, dict):
        return None
    try:
        return ChartSettings.model_validate(payload)
    except Exception:  # noqa: BLE001
        logger.debug("setting object failed ChartSettings validation", exc_info=True)
        return None


def extract_fields(
    source_js: str,
    *,
    data_types: Any = None,
    format_strings: Optional[dict[str, str]] = None,
) -> ExtractedFields:
    """Pull dimension / measure / series / title bindings from source chart JS."""
    # Prefer the injected settings object when present (${setting} charts).
    from_settings = _extract_settings_object(source_js)
    if from_settings is not None:
        source_chart = _detect_source_chart(source_js)
        source_def = get_chart_definition(source_chart) if source_chart else None
        source_family = _resolve_source_family(source_chart, source_def)
        fields = settings_to_fields(
            from_settings,
            source_chart=source_chart,
            source_family=source_family,
            format_strings=format_strings,
        )
        if not fields.color:
            fields.color = _extract_color(source_js)
        # Tables often leave dimensions/measures empty (all columns render);
        # fill gaps from result metadata so conversion (e.g. → wordcloud) works.
        if data_types is not None and (not fields.dimensions or not fields.measures):
            meta_dims, meta_measures = _infer_columns_from_metadata(data_types)
            if not fields.dimensions:
                measure_set = set(fields.measures)
                fields.dimensions = _unique(
                    [d for d in meta_dims if d not in measure_set]
                )
            if not fields.measures:
                dim_set = set(fields.dimensions)
                fields.measures = _unique(
                    [m for m in meta_measures if m not in dim_set]
                )
        return fields

    source_chart = _detect_source_chart(source_js)
    source_def = get_chart_definition(source_chart) if source_chart else None
    source_family = _resolve_source_family(source_chart, source_def)

    raw: dict[str, list[str]] = {}
    for match in _STRING_FIELD.finditer(source_js):
        key = match.group("key")
        values = _values_from_match(match)
        if values:
            raw.setdefault(key, []).extend(values)

    for match in _CONST_FIELD.finditer(source_js):
        name = match.group("name")
        values = _values_from_match(match)
        if not values:
            continue
        if name in {"measureField", "measureFields", "targetField"}:
            raw.setdefault("yField", []).extend(values)
            raw.setdefault("angleField", []).extend(values)
        elif name in {"dimensionField", "dimensionFields", "primaryDimension"}:
            raw.setdefault("xField", []).extend(values)
            raw.setdefault("colorField", []).extend(values)
        elif name == "secondaryDimension":
            raw.setdefault("yField", []).extend(values)

    hint_dims, hint_measures = _extract_conversion_hints(source_js)
    dimensions: list[str] = []
    measures: list[str] = []
    series: Optional[str] = None
    format_maps = _extract_format_maps(source_js)
    if isinstance(format_strings, dict):
        for key, value in format_strings.items():
            name = str(key or "").strip()
            fmt = str(value or "").strip()
            if name and fmt and name not in _PLACEHOLDER_FORMAT_KEYS:
                format_maps.setdefault(name, fmt)

    if source_family == "bar":
        measures.extend(raw.get("xField") or [])
        dimensions.extend(raw.get("yField") or [])
        series = _first(raw.get("seriesField"))
    elif source_family == "pie":
        measures.extend(raw.get("angleField") or raw.get("yField") or [])
        dimensions.extend(raw.get("colorField") or raw.get("xField") or [])
        series = _first(raw.get("seriesField"))
    elif source_family == "heatmap":
        dimensions.extend(raw.get("xField") or [])
        dimensions.extend(raw.get("yField") or [])
        measures.extend(raw.get("colorField") or [])
    elif source_family == "bubble":
        measures.extend(raw.get("xField") or [])
        measures.extend(raw.get("yField") or [])
        measures.extend(raw.get("sizeField") or [])
        dimensions.extend(raw.get("colorField") or [])
    elif source_family == "kpi":
        measures.extend(_extract_kpi_measure(source_js))
        measures.extend(raw.get("yField") or raw.get("angleField") or [])
    elif source_family == "dual_axes":
        dimensions.extend(raw.get("xField") or [])
        measures.extend(raw.get("yField") or [])
        series = _first(raw.get("seriesField") or raw.get("groupField"))
    elif source_family == "table":
        # Prefer conversionHints; fall back to formatted columns as measures.
        dimensions.extend(hint_dims)
        measures.extend(hint_measures or list(format_maps.keys()))
    else:
        # cartesian / tiny / hierarchy / percent / other / unknown
        dimensions.extend(raw.get("xField") or raw.get("colorField") or [])
        y_vals = raw.get("yField") or raw.get("angleField") or raw.get("binField") or []
        measures.extend(y_vals)
        measures.extend(raw.get("sizeField") or [])
        series = _first(raw.get("seriesField") or raw.get("groupField"))

    # Commented conversionHints fill gaps for any family (including unfilled tables).
    if not dimensions:
        dimensions.extend(hint_dims)
    if not measures:
        measures.extend(hint_measures)

    if not dimensions and not measures:
        for key in ("xField", "yField", "angleField", "colorField", "binField"):
            for value in raw.get(key) or []:
                if key in {"angleField", "binField", "sizeField"} or (
                    source_family == "bar" and key == "xField"
                ):
                    measures.append(value)
                else:
                    dimensions.append(value)

    if data_types is not None:
        meta_dims, meta_measures = _infer_columns_from_metadata(data_types)
        if source_family == "table":
            measure_set = set(measures)
            if not dimensions:
                dimensions.extend(d for d in meta_dims if d not in measure_set)
            if not measures:
                measures.extend(meta_measures)
        else:
            if not dimensions:
                dimensions.extend(meta_dims)
            if not measures:
                measures.extend(meta_measures)

    dimensions = _unique(dimensions)
    measures = _unique([m for m in measures if m not in set(dimensions)])
    label_x, label_y = _extract_axis_labels(source_js)

    return ExtractedFields(
        dimensions=dimensions,
        measures=measures,
        series=series,
        title=_extract_title(source_js),
        label_x=label_x,
        label_y=label_y,
        source_chart=source_chart,
        source_family=source_family,
        measure_formats=format_maps,
        color=_extract_color(source_js),
    )


def fill_skeleton(
    skeleton: str,
    fields: ExtractedFields,
    *,
    conversion: ChartConversion,
) -> str:
    """Replace catalog placeholders using the target chart's conversion contract.

    Bare object keys (``measure_column:``) are quoted when the replacement is not
    a valid JS identifier (e.g. names with spaces). Occurrences already inside
    string literals keep the raw field name.

    Preserves source ``measureFormats`` and chart ``color`` when present, without
    changing the convert-chart response shape.
    """
    replacements = _build_replacements(fields, conversion)
    # Longest-first to avoid partial placeholder collisions.
    placeholders = sorted(replacements.keys(), key=len, reverse=True)

    code = skeleton
    for placeholder in placeholders:
        value = replacements[placeholder]
        key = _quote_js_object_key(value)
        # Quote bare property keys before the global value replace.
        code = re.sub(
            rf"\b{re.escape(placeholder)}(\s*:)",
            lambda match, k=key: f"{k}{match.group(1)}",
            code,
        )
        code = code.replace(placeholder, value)

    if conversion.omit_when_missing:
        missing_keys = [
            key
            for key in conversion.omit_when_missing
            if _should_omit(key, fields, conversion)
        ]
        if missing_keys:
            code = _omit_config_keys(code, missing_keys)

    code = _apply_preserved_styles(code, fields)
    code = _inject_conversion_hints(code, fields)
    return code.strip()

_JS_IDENTIFIER = re.compile(r"^[A-Za-z_$][\w$]*$")


def _quote_js_object_key(name: str) -> str:
    """Return a JS object-key token, quoting when ``name`` is not a valid identifier."""
    if _JS_IDENTIFIER.fullmatch(name):
        return name
    if "'" not in name:
        return f"'{name}'"
    if '"' not in name:
        return f'"{name}"'
    escaped = name.replace("\\", "\\\\").replace("'", "\\'")
    return f"'{escaped}'"


def _js_string_literal(value: str) -> str:
    """Quote a JS string, preferring single quotes."""
    if "'" not in value:
        return f"'{value}'"
    if '"' not in value:
        return f'"{value}"'
    escaped = value.replace("\\", "\\\\").replace("'", "\\'")
    return f"'{escaped}'"


def _extract_measure_formats(source_js: str) -> dict[str, str]:
    """Parse ``measureFormats`` / ``columnFormats`` maps from source chart JS."""
    return _extract_format_maps(source_js)


def _extract_format_maps(source_js: str) -> dict[str, str]:
    """Parse Excel-style format maps from source chart JS."""
    formats: dict[str, str] = {}
    for match in _MEASURE_FORMATS_BLOCK.finditer(source_js or ""):
        for entry in _FORMAT_ENTRY.finditer(match.group("body") or ""):
            key = (
                entry.group("ksq")
                or entry.group("kdq")
                or entry.group("kident")
                or ""
            ).strip()
            raw_val = entry.group("vsq")
            if raw_val is None:
                raw_val = entry.group("vdq")
            if not key or raw_val is None:
                continue
            if key in _PLACEHOLDER_FORMAT_KEYS or key.endswith("_column"):
                continue
            formats[key] = raw_val
    return formats


def _hint_values(raw: Optional[str]) -> list[str]:
    if not raw:
        return []
    text = raw.strip()
    if text.startswith("[") and text.endswith("]"):
        inner = text[1:-1]
        return [
            part.strip().strip("'\"")
            for part in inner.split(",")
            if part.strip().strip("'\"")
        ]
    return [text.strip().strip("'\"")] if text.strip().strip("'\"") else []


def _is_placeholder_field(name: str) -> bool:
    text = (name or "").strip()
    return (
        not text
        or text in _PLACEHOLDER_FORMAT_KEYS
        or text.endswith("_column")
        or text in {"dimension_column", "measure_column", "series_field", "title_text"}
    )


def _extract_conversion_hints(source_js: str) -> tuple[list[str], list[str]]:
    """Parse ``// conversionHints: xField: '...', yField: '...'`` comments."""
    dimensions: list[str] = []
    measures: list[str] = []
    for match in _CONVERSION_HINTS_LINE.finditer(source_js or ""):
        body = match.group("body") or ""
        for field in _HINT_FIELD.finditer(body):
            key = (field.group("key") or "").lower()
            raw = field.group("sq") or field.group("dq")
            if raw is None and field.group("arr") is not None:
                raw = f"[{field.group('arr')}]"
            values = [
                value
                for value in _hint_values(raw)
                if not _is_placeholder_field(value)
            ]
            if key == "xfield":
                dimensions.extend(values)
            elif key == "yfield":
                measures.extend(values)
    return _unique(dimensions), _unique(measures)


def _js_hint_list(values: list[str]) -> str:
    cleaned = [v for v in values if v and not _is_placeholder_field(v)]
    if not cleaned:
        return "''"
    if len(cleaned) == 1:
        return _js_string_literal(cleaned[0])
    joined = ", ".join(_js_string_literal(v) for v in cleaned)
    return f"[{joined}]"


def _inject_conversion_hints(code: str, fields: ExtractedFields) -> str:
    """Keep / rewrite conversionHints so the next conversion can recover bindings."""
    if not fields.dimensions and not fields.measures:
        return code

    hint_line = (
        "  // conversionHints: "
        f"xField: {_js_hint_list(fields.dimensions)}, "
        f"yField: {_js_hint_list(fields.measures)}"
    )
    if _CONVERSION_HINTS_LINE.search(code):
        return _CONVERSION_HINTS_LINE.sub(hint_line, code, count=1)

    # Insert after components destructure when present.
    components = re.search(
        r"(const\s*\{[^}]+\}\s*=\s*components\s*;\s*\n)",
        code,
    )
    if components:
        insert_at = components.end()
        return code[:insert_at] + hint_line + "\n" + code[insert_at:]

    function_open = re.search(r"(function\s+Draw[A-Za-z0-9_]*\s*\([^)]*\)\s*\{\s*\n)", code)
    if function_open:
        insert_at = function_open.end()
        return code[:insert_at] + hint_line + "\n" + code[insert_at:]
    return code


def _classify_metadata_type(token: str) -> str:
    """Return ``measure`` or ``dimension`` for a normalized metadata type token."""
    if not token:
        return "dimension"
    if token in _MEASURE_TOKENS or any(t in token for t in _MEASURE_TOKENS):
        return "measure"
    if token in _ORDERED_TOKENS or any(t in token for t in _ORDERED_TOKENS):
        return "dimension"
    if token in _DIMENSION_TOKENS or any(t in token for t in ("text", "string", "cat")):
        return "dimension"
    return "dimension"


def _iter_named_columns(data_types: Any) -> Iterable[tuple[str, str]]:
    """Yield ``(column_name, role)`` pairs from SQL result metadata."""
    if data_types is None:
        return
    if isinstance(data_types, str):
        for part in data_types.split(","):
            part = part.strip()
            if not part:
                continue
            if ":" in part:
                name, type_token = part.split(":", 1)
                yield name.strip(), _classify_metadata_type(
                    _normalize_type_token(type_token)
                )
            else:
                yield part, "dimension"
        return
    if isinstance(data_types, dict):
        if _is_meta_entry(data_types):
            return
        if any(k in data_types for k in _TYPE_KEYS):
            name = str(
                data_types.get("name")
                or data_types.get("columnName")
                or data_types.get("column")
                or ""
            ).strip()
            if name:
                yield name, _classify_metadata_type(_type_from_column_desc(data_types))
            return
        for value in data_types.values():
            if isinstance(value, dict):
                name = str(
                    value.get("name")
                    or value.get("columnName")
                    or value.get("column")
                    or ""
                ).strip()
                if name:
                    yield name, _classify_metadata_type(_type_from_column_desc(value))
        return
    if isinstance(data_types, list):
        for item in data_types:
            if not isinstance(item, dict):
                continue
            if _is_meta_entry(item):
                continue
            if any(k in item for k in _TYPE_KEYS):
                name = str(
                    item.get("name")
                    or item.get("columnName")
                    or item.get("column")
                    or ""
                ).strip()
                if name:
                    yield name, _classify_metadata_type(_type_from_column_desc(item))
                continue
            for value in item.values():
                if not isinstance(value, dict):
                    continue
                name = str(
                    value.get("name")
                    or value.get("columnName")
                    or value.get("column")
                    or ""
                ).strip()
                if name:
                    yield name, _classify_metadata_type(_type_from_column_desc(value))
        return


def _infer_columns_from_metadata(data_types: Any) -> tuple[list[str], list[str]]:
    """Infer dimension and measure column names from executeQuery metadata."""
    dimensions: list[str] = []
    measures: list[str] = []
    for name, role in _iter_named_columns(data_types):
        if role == "measure":
            measures.append(name)
        else:
            dimensions.append(name)
    return _unique(dimensions), _unique(measures)


def _extract_color(source_js: str) -> Optional[str]:
    """Extract a portable chart color expression (palette array or color string)."""
    text = source_js or ""
    for match in _COLOR_VALUE.finditer(text):
        prefix = text[max(0, match.start() - 48) : match.start()]
        if re.search(r"style\s*:\s*\{\s*$", prefix.rstrip()) or "valueStyle" in prefix:
            continue
        expr = (match.group("expr") or "").strip()
        if expr:
            return expr
    value_style = _VALUE_STYLE_COLOR.search(text)
    if value_style:
        return (value_style.group("expr") or "").strip() or None
    return None


def _apply_preserved_styles(code: str, fields: ExtractedFields) -> str:
    """Re-apply source measure formats and color onto the filled target skeleton."""
    code = _apply_measure_formats(code, fields)
    if fields.color:
        code = _apply_color(code, fields.color)
    return code


def _apply_measure_formats(code: str, fields: ExtractedFields) -> str:
    """Rewrite ``measureFormats`` values using preserved source formats when keys match.

    When the target block has a single measure key and the source had a single
    format entry, apply that format even if the measure names differ (rename).
    """
    formats = dict(fields.measure_formats or {})
    if not formats and not fields.measures:
        return code

    # Single-measure rename: source format under old name → target measure key.
    if len(formats) == 1 and len(fields.measures) == 1:
        only_fmt = next(iter(formats.values()))
        formats.setdefault(fields.measures[0], only_fmt)

    match = _MEASURE_FORMATS_BLOCK.search(code)
    if not match:
        return code

    body = match.group("body") or ""
    entries = list(_FORMAT_ENTRY.finditer(body))
    if not entries and not formats:
        return code

    lines: list[str] = []
    seen: set[str] = set()
    for entry in entries:
        key = (
            entry.group("ksq")
            or entry.group("kdq")
            or entry.group("kident")
            or ""
        ).strip()
        raw_val = entry.group("vsq")
        if raw_val is None:
            raw_val = entry.group("vdq")
        if not key:
            continue
        if key in formats:
            raw_val = formats[key]
        elif (
            len(entries) == 1
            and len(formats) == 1
            and key not in _PLACEHOLDER_FORMAT_KEYS
        ):
            raw_val = next(iter(formats.values()))
        if raw_val is None:
            raw_val = "0.00"
        lines.append(f"    {_quote_js_object_key(key)}: {_js_string_literal(raw_val)}")
        seen.add(key)

    # Keep any extra source formats for measures present in the bag but not yet listed.
    for name, fmt in formats.items():
        if name in seen:
            continue
        if name not in set(fields.measures):
            continue
        lines.append(f"    {_quote_js_object_key(name)}: {_js_string_literal(fmt)}")
        seen.add(name)

    if not lines:
        return code

    new_body = "\n" + ",\n".join(lines) + "\n  "
    start, end = match.start("body"), match.end("body")
    return code[:start] + new_body + code[end:]


def _color_prefix_is_style(code: str, index: int) -> bool:
    prefix = code[max(0, index - 48) : index]
    return bool(
        re.search(r"style\s*:\s*\{\s*$", prefix.rstrip()) or "valueStyle" in prefix
    )


def _apply_color(code: str, color_expr: str) -> str:
    """Replace existing chart color slots, or inject into ``config`` when absent."""
    expr = (color_expr or "").strip()
    if not expr:
        return code

    # KPI Statistic uses valueStyle={{ color: '...' }}.
    if _VALUE_STYLE_COLOR.search(code):
        return _VALUE_STYLE_COLOR.sub(
            f"valueStyle={{{{ color: {expr} }}}}",
            code,
            count=1,
        )

    replaced = False

    def _repl(match: re.Match[str]) -> str:
        nonlocal replaced
        if _color_prefix_is_style(match.string, match.start()):
            return match.group(0)
        replaced = True
        return f"color: {expr}"

    new_code = _COLOR_VALUE.sub(_repl, code)
    if replaced:
        return new_code

    # Do not inject a second color when an object form already exists (tables).
    if re.search(r"(?<![.\w])color\s*:", code):
        return code

    config_open = _CONFIG_OPEN.search(code)
    if not config_open:
        return code
    insert_at = config_open.end()
    return code[:insert_at] + f"\n    color: {expr}," + code[insert_at:]


def _build_replacements(
    fields: ExtractedFields,
    conversion: ChartConversion,
) -> dict[str, str]:
    dim0 = fields.dimensions[0] if fields.dimensions else "category"
    meas0 = fields.measures[0] if fields.measures else "value"
    title = fields.title or f"{meas0} by {dim0}"

    replacements: dict[str, str] = {}
    for placeholder, spec in conversion.fields.items():
        value = _role_value(fields, spec.role, spec.index)
        if not value:
            if spec.optional:
                continue
            # Required fields already validated; still provide a safe default.
            if spec.role == "dimension":
                value = dim0
            elif spec.role == "measure":
                value = meas0
            elif spec.role == "series":
                value = fields.series or dim0
            else:
                value = title
        replacements[placeholder] = value

    # Always allow title_text even if not declared (common in skeletons).
    replacements.setdefault("title_text", title)
    # Axis / display labels (LLM settings or defaults from field names).
    replacements.setdefault("label_x", fields.label_x or dim0)
    replacements.setdefault("label_y", fields.label_y or meas0)
    if fields.label_z:
        replacements.setdefault("label_z", fields.label_z)
    # Legacy skeleton placeholders used before settings-based labels.
    replacements.setdefault("Label for dimension axis", fields.label_x or dim0)
    replacements.setdefault("Label for measure axis", fields.label_y or meas0)
    return replacements


def _role_value(fields: ExtractedFields, role: str, index: int) -> Optional[str]:
    if role == "dimension":
        return fields.dimensions[index] if len(fields.dimensions) > index else None
    if role == "measure":
        return fields.measures[index] if len(fields.measures) > index else None
    if role == "series":
        return fields.series
    if role == "title":
        return fields.title
    return None


_OMIT_KEY_ALIASES: dict[str, tuple[str, ...]] = {
    "series_field": ("seriesField", "series_field"),
    "series_column": ("seriesField", "series_column"),
    "secondary_series_column": ("seriesField", "secondary_series_column"),
    "line_series_column": ("seriesField", "line_series_column"),
    "stack_column": ("seriesField", "stack_column"),
    "group_column": ("groupField", "seriesField", "group_column"),
    "legend": ("legend",),
    "seriesField": ("seriesField",),
    # Bubble optional color dim leaves colorField: 'dimension_column' if unbound.
    "dimension_column": ("colorField",),
}


def _should_omit(
    key: str,
    fields: ExtractedFields,
    conversion: ChartConversion,
) -> bool:
    """True when an omit_when_missing key should be stripped from the skeleton."""
    spec = conversion.fields.get(key)
    if spec is not None:
        return not bool(_role_value(fields, spec.role, spec.index))
    if key in {"legend", "seriesField"}:
        return not bool(fields.series)
    return False


def _omit_config_keys(code: str, keys: list[str]) -> str:
    """Drop config entries / unresolved placeholders that should not appear."""
    cleaned = code
    props: list[str] = []
    for key in keys:
        props.extend(_OMIT_KEY_ALIASES.get(key, (key,)))
    for prop in dict.fromkeys(props):
        cleaned = re.sub(
            rf",?\s*{re.escape(prop)}\s*:\s*(?:'[^']*'|\"[^\"]*\"|[A-Za-z_][\w]*)\s*",
            "",
            cleaned,
        )
        cleaned = re.sub(
            rf",?\s*{re.escape(prop)}\s*:\s*\{{[^{{}}]*\}}\s*",
            "",
            cleaned,
        )
    cleaned = re.sub(r",\s*}", " }", cleaned)
    cleaned = re.sub(r",\s*,", ",", cleaned)
    return cleaned


def _resolve_source_family(
    source_chart: Optional[str],
    source_def: Optional[ChartDefinition],
) -> Optional[str]:
    if source_def and source_def.conversion:
        return source_def.conversion.family
    if source_chart:
        return _FAMILY_BY_NAME.get(source_chart, "cartesian")
    return None


def _detect_source_chart(source_js: str) -> Optional[str]:
    fn = _FUNCTION_NAME.search(source_js)
    if fn:
        token = re.sub(r"(?<!^)(?=[A-Z])", "_", fn.group(1)).lower()
        resolved = resolve_chart_name(token)
        if resolved:
            return resolved
        if token in _FAMILY_BY_NAME or token in {
            "bar",
            "column",
            "line",
            "area",
            "pie",
            "donut",
            "doughnut",
            "arc",
            "point",
        }:
            return token

    components = _JSX_COMPONENT.findall(source_js)
    if "Pie" in components:
        if re.search(r"innerRadius\s*:", source_js):
            return "doughnut"
        return "pie"
    for name in components:
        mapped = _COMPONENT_TO_CHART.get(name)
        if mapped:
            return mapped
    return None


def _values_from_match(match: re.Match) -> list[str]:
    if match.groupdict().get("sq"):
        return [match.group("sq")]
    if match.groupdict().get("dq"):
        return [match.group("dq")]
    arr = match.groupdict().get("arr")
    if arr:
        return [
            part.strip().strip("'\"")
            for part in arr.split(",")
            if part.strip().strip("'\"")
        ]
    return []


_AXIS_TITLE = re.compile(
    r"(?P<axis>xAxis|yAxis)\s*:\s*\{[^}]*?title\s*:\s*\{[^}]*?text\s*:\s*"
    r"(?:'(?P<sq>[^']+)'|\"(?P<dq>[^\"]+)\")",
    re.DOTALL,
)


def _extract_axis_labels(source_js: str) -> tuple[Optional[str], Optional[str]]:
    """Best-effort xAxis / yAxis title texts from source chart JS."""
    label_x: Optional[str] = None
    label_y: Optional[str] = None
    skip = {
        "title_text",
        "label_x",
        "label_y",
        "label_z",
        "Label for measure axis",
        "Label for dimension axis",
        "dimension_column",
        "measure_column",
    }
    for match in _AXIS_TITLE.finditer(source_js or ""):
        text = (match.group("sq") or match.group("dq") or "").strip()
        if not text or text in skip or text.endswith("_column"):
            continue
        axis = match.group("axis")
        if axis == "xAxis" and not label_x:
            label_x = text
        elif axis == "yAxis" and not label_y:
            label_y = text
    return label_x, label_y


def _extract_title(source_js: str) -> Optional[str]:
    for pattern in _TITLE_PATTERNS:
        match = pattern.search(source_js)
        if not match:
            continue
        text = (match.group("t") or "").strip()
        if not text or text in {
            "title_text",
            "Put the title here",
            "Label for measure axis",
            "Label for dimension axis",
            "label_x",
            "label_y",
            "label_z",
        }:
            continue
        if text.endswith("_column") or text in {"dimension_column", "measure_column"}:
            continue
        return text
    return None


def _extract_kpi_measure(source_js: str) -> list[str]:
    match = re.search(r"data\[0\]\?\.?\[['\"]([^'\"]+)['\"]\]", source_js)
    return [match.group(1)] if match else []


def _first(values: Optional[list[str]]) -> Optional[str]:
    if not values:
        return None
    return values[0]


def _unique(values: list[str]) -> list[str]:
    seen: set[str] = set()
    out: list[str] = []
    for value in values:
        text = (value or "").strip()
        if not text or text in seen:
            continue
        if text.endswith("_column") or text in {"series_field", "title_text"}:
            continue
        seen.add(text)
        out.append(text)
    return out
