"""Chart-type selection guide for viz prompts.

Chart catalogs live in ``viz/charts/*.json``. This module filters those options
by data shape and builds a compact prompt guide — never the full table.
"""
from __future__ import annotations

import logging
from dataclasses import dataclass
from typing import Any, Iterable, Optional

logger = logging.getLogger(__name__)


@dataclass(frozen=True)
class ChartOption:
    visualization_type: str
    dims_min: int
    dims_max: Optional[int]  # None = unbounded
    measures_min: int
    measures_max: Optional[int]
    instruction: str
    requires_ordered: bool = False
    aliases: tuple[str, ...] = ()


def get_chart_options() -> tuple[ChartOption, ...]:
    """Return all registered chart options from ``charts/*.json``."""
    from helicalbi.viz._charts import get_chart_options as _load

    return _load()


CHART_SELECTION_RULES = """1. Choose EXACTLY ONE visualization_type from the possible values listed for this data shape.
2. Evaluate each option's 'Pick exactly when' condition against user intent.
3. When multiple options fit, prefer an explicit user request, then the most specific condition.
4. If the user names a chart via an alias, map that alias to the matching visualization_type key.
5. Return the visualization_type key copied verbatim from the list — never return an alias.
6. visualization_type keys use underscores only; never return spaces or hyphens."""


def _count_in_range(count: int, minimum: int, maximum: Optional[int]) -> bool:
    if count < minimum:
        return False
    if maximum is not None and count > maximum:
        return False
    return True


def _matches(
    option: ChartOption,
    dimension_count: int,
    measure_count: int,
    ordered: bool,
) -> bool:
    if not _count_in_range(dimension_count, option.dims_min, option.dims_max):
        return False
    if not _count_in_range(measure_count, option.measures_min, option.measures_max):
        return False
    if option.requires_ordered and not ordered:
        return False
    return True


def possible_chart_options(
    dimension_count: int,
    measure_count: int,
    ordered: bool = False,
) -> list[ChartOption]:
    """Return chart options compatible with the given data shape."""
    catalog = get_chart_options()
    matched = [
        option
        for option in catalog
        if _matches(option, dimension_count, measure_count, ordered)
    ]
    logger.info(
        "chart_selection.filter dims=%s measures=%s ordered=%s "
        "catalog=%s matched=%s types=%s",
        dimension_count,
        measure_count,
        ordered,
        len(catalog),
        len(matched),
        [opt.visualization_type for opt in matched],
    )
    return matched


_MEASURE_TOKENS = frozenset({
    "numeric", "number", "int", "integer", "float", "double", "decimal",
    "bigdecimal", "long", "short", "byte", "money", "currency",
})
_ORDERED_TOKENS = frozenset({
    "date", "datetime", "timestamp", "time", "datatime",
})
_DIMENSION_TOKENS = frozenset({
    "text", "string", "categorical", "category", "boolean", "bool",
    "varchar", "char", "object", "other",
}) | _ORDERED_TOKENS


_TYPE_KEYS = ("type", "data_type", "dataType", "dtype", "columnType")
# Trailing executeQuery footer entries, e.g. {"rows": 1}
_META_ONLY_KEYS = frozenset({"rows", "row_count", "rowcount", "count", "total_rows"})


def _normalize_type_token(raw: Any) -> str:
    if raw is None:
        return ""
    if isinstance(raw, dict):
        # Prefer an explicit type field on column descriptors.
        for key in _TYPE_KEYS:
            if key in raw and raw[key] is not None:
                return _normalize_type_token(raw[key])
        # Java type map: {"java.lang.Integer": "numeric"} or {"java.lang.Integer": True}
        for key, value in raw.items():
            if isinstance(value, str) and value.strip():
                return value.strip().lower()
            key_l = str(key).lower()
            if "timestamp" in key_l or key_l.endswith(".date"):
                return "date"
            if "time" in key_l and "timestamp" not in key_l:
                return "time"
            if any(part in key_l for part in (
                "integer", "long", "short", "byte", "float", "double", "decimal", "bigdecimal",
            )):
                return "numeric"
            if "string" in key_l or "char" in key_l:
                return "text"
            if "boolean" in key_l:
                return "boolean"
        return ""
    text = str(raw).strip().lower()
    if "." in text:
        return _normalize_type_token({text: True})
    return text


def _is_meta_entry(item: dict) -> bool:
    keys = {str(k).lower() for k in item.keys()}
    return bool(keys) and keys <= _META_ONLY_KEYS


def _type_from_column_desc(desc: Any) -> str:
    if isinstance(desc, dict):
        for key in _TYPE_KEYS:
            if key in desc and desc[key] is not None:
                return _normalize_type_token(desc[key])
        return ""
    return _normalize_type_token(desc)


def _iter_field_types(data_types: Any) -> Iterable[str]:
    """Yield normalized type tokens from SQL result metadata.

    Supports executeQuery shapes such as::

        [{'1': {'name': 'Cost of travels', 'type': 'numeric'}}, {'rows': 1}]
        [{'name': 'Cost of travels', 'type': 'numeric'}, {'rows': 1}]
    """
    if data_types is None:
        logger.info("chart_selection.parse route=none reason=data_types_is_none")
        return
    if isinstance(data_types, str):
        logger.info("chart_selection.parse route=string_csv")
        for part in data_types.split(","):
            part = part.strip()
            if not part:
                continue
            if ":" in part:
                yield _normalize_type_token(part.split(":", 1)[1])
            else:
                yield _normalize_type_token(part)
        return
    if isinstance(data_types, dict):
        if _is_meta_entry(data_types):
            logger.info("chart_selection.parse route=dict_meta_skip keys=%s", list(data_types.keys()))
            return
        # Direct column descriptor or index->column map.
        if any(k in data_types for k in _TYPE_KEYS):
            logger.info("chart_selection.parse route=dict_direct_column")
            token = _type_from_column_desc(data_types)
            if token:
                yield token
            return
        logger.info("chart_selection.parse route=dict_index_map keys=%s", list(data_types.keys()))
        for value in data_types.values():
            token = _type_from_column_desc(value)
            if token:
                yield token
        return
    if isinstance(data_types, list):
        logger.info("chart_selection.parse route=list_metadata items=%s", len(data_types))
        for index, item in enumerate(data_types):
            if not isinstance(item, dict):
                token = _normalize_type_token(item)
                if token:
                    logger.info(
                        "chart_selection.parse route=list_scalar index=%s token=%s",
                        index,
                        token,
                    )
                    yield token
                continue
            if _is_meta_entry(item):
                logger.info(
                    "chart_selection.parse route=list_meta_skip index=%s keys=%s",
                    index,
                    list(item.keys()),
                )
                continue
            # {"name": "...", "type": "numeric"}
            if any(k in item for k in _TYPE_KEYS):
                token = _type_from_column_desc(item)
                logger.info(
                    "chart_selection.parse route=list_direct_column index=%s name=%s token=%s",
                    index,
                    item.get("name"),
                    token or "<empty>",
                )
                if token:
                    yield token
                continue
            # {"1": {"name": "...", "type": "numeric"}}
            logger.info(
                "chart_selection.parse route=list_indexed_column index=%s keys=%s",
                index,
                list(item.keys()),
            )
            for value in item.values():
                token = _type_from_column_desc(value)
                name = value.get("name") if isinstance(value, dict) else None
                logger.info(
                    "chart_selection.parse route=list_indexed_column_value "
                    "index=%s name=%s token=%s",
                    index,
                    name,
                    token or "<empty>",
                )
                if token:
                    yield token
        return
    logger.info(
        "chart_selection.parse route=unsupported type=%s",
        type(data_types).__name__,
    )


def infer_chart_shape(data_types: Any) -> tuple[int, int, bool]:
    """Infer (dimension_count, measure_count, ordered) from SQL result metadata."""
    dimension_count = 0
    measure_count = 0
    ordered = False
    classified: list[str] = []
    for token in _iter_field_types(data_types):
        if not token:
            continue
        if token in _MEASURE_TOKENS or any(t in token for t in _MEASURE_TOKENS):
            measure_count += 1
            classified.append(f"{token}->measure")
            continue
        if token in _ORDERED_TOKENS or any(t in token for t in _ORDERED_TOKENS):
            dimension_count += 1
            ordered = True
            classified.append(f"{token}->ordered_dim")
            continue
        if token in _DIMENSION_TOKENS or any(t in token for t in ("text", "string", "cat")):
            dimension_count += 1
            classified.append(f"{token}->dimension")
            continue
        dimension_count += 1
        classified.append(f"{token}->dimension_default")
    logger.info(
        "chart_selection.infer dims=%s measures=%s ordered=%s fields=%s",
        dimension_count,
        measure_count,
        ordered,
        classified,
    )
    return dimension_count, measure_count, ordered


def format_chart_selection_guide(
    dimension_count: Optional[int] = None,
    measure_count: Optional[int] = None,
    ordered: bool = False,
    *,
    data_types: Any = None,
) -> str:
    """Build a compact chart-selection guide for the prompt."""
    if dimension_count is None or measure_count is None:
        if data_types is not None:
            logger.info("chart_selection.guide route=infer_from_data_types")
            dimension_count, measure_count, ordered = infer_chart_shape(data_types)
        else:
            logger.info("chart_selection.guide route=rules_only reason=no_shape_or_data_types")
            return CHART_SELECTION_RULES
    else:
        logger.info(
            "chart_selection.guide route=explicit_counts dims=%s measures=%s ordered=%s",
            dimension_count,
            measure_count,
            ordered,
        )

    options = possible_chart_options(dimension_count, measure_count, ordered)
    used_fallback = False
    if not options:
        used_fallback = True
        options = [
            opt for opt in get_chart_options()
            if opt.visualization_type in {"table", "other"}
        ]
        logger.info(
            "chart_selection.guide route=fallback_table_other types=%s",
            [opt.visualization_type for opt in options],
        )
    else:
        logger.info(
            "chart_selection.guide route=filtered_matches count=%s types=%s",
            len(options),
            [opt.visualization_type for opt in options],
        )

    ordered_note = "yes" if ordered else "no"
    lines = [
        (
            f"Data shape: {dimension_count} dimension(s), "
            f"{measure_count} measure(s), ordered dimension: {ordered_note}."
        ),
        "",
        "Possible visualization_type values (Pick exactly when):",
    ]
    for option in options:
        if option.aliases:
            alias_text = ", ".join(option.aliases)
            lines.append(
                f"- {option.visualization_type} (aliases: {alias_text}): "
                f"{option.instruction}"
            )
        else:
            lines.append(f"- {option.visualization_type}: {option.instruction}")
    lines.extend(["", CHART_SELECTION_RULES])
    guide = "\n".join(lines)
    logger.info(
        "chart_selection.guide done dims=%s measures=%s ordered=%s "
        "fallback=%s option_count=%s guide_chars=%s",
        dimension_count,
        measure_count,
        ordered,
        used_fallback,
        len(options),
        len(guide),
    )
    return guide


def _format_constraint(minimum: int, maximum: Optional[int], *, ordered: bool = False) -> str:
    if minimum == 0 and maximum is None:
        return "any"
    if ordered and minimum == 1 and maximum == 1:
        return "1 ordered"
    if maximum is None:
        return f"{minimum}+"
    if minimum == maximum:
        return str(minimum)
    return f"{minimum}-{maximum}"


def _build_chart_selection_table(options: tuple[ChartOption, ...]) -> str:
    rows = "\n".join(
        "| {viz:<33} | {dims:<10} | {meas:<8} | {when:<56} |".format(
            viz=opt.visualization_type,
            dims=_format_constraint(opt.dims_min, opt.dims_max, ordered=opt.requires_ordered),
            meas=_format_constraint(opt.measures_min, opt.measures_max),
            when=opt.instruction[:56],
        )
        for opt in options
    )
    return (
        "+-----------------------------------+------------+----------+----------------------------------------------------------+\n"
        "| visualization_type                | Dimensions | Measures | Pick exactly when                                        |\n"
        "+-----------------------------------+------------+----------+----------------------------------------------------------+\n"
        f"{rows}\n"
        "+-----------------------------------+------------+----------+----------------------------------------------------------+"
    )


def __getattr__(name: str):
    if name == "CHART_OPTIONS":
        return get_chart_options()
    if name == "CHART_SELECTION_TABLE":
        return _build_chart_selection_table(get_chart_options())
    raise AttributeError(f"module {__name__!r} has no attribute {name!r}")
