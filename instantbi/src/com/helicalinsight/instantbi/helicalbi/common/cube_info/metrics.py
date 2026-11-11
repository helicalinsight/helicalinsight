"""Business metrics and synonyms derived from cube_info."""

from __future__ import annotations

from typing import Any, Dict, List, Optional, Tuple

from helicalbi.common.JsonToPara import resolve_column_alias, resolve_table_alias
from helicalbi.common.cube_info.fields import (
    _aggregator_from_cube_item,
    _format_string_from_cube_item,
    _sort_order_raw_from_cube_item,
    sort_direction_from_value,
)
from helicalbi.common.cube_info.lookup import (
    _default_function_from_cube_item,
    _formula_from_cube_item,
    _known_table_names,
    _resolve_column_name,
    _resolve_table_name,
    _table_from_formula,
    build_column_to_table_map,
    build_metadata_id_maps,
)
from helicalbi.common.cube_info.normalize import normalize_cube_info


def _metric_name_from_cube_item(item: dict, primary_name_key: str) -> str:
    metric_obj = item.get("metric")
    if isinstance(metric_obj, dict):
        for key in ("metric", "name", "id"):
            value = metric_obj.get(key)
            if value:
                return str(value)
    primary = item.get(primary_name_key)
    if primary:
        return str(primary)
    column_name = item.get("columnName")
    if column_name:
        return str(column_name)
    return ""


def _description_from_cube_item(item: dict, primary_name_key: str) -> str:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    description = (
        item.get("description")
        or metric_obj.get("description")
        or item.get(primary_name_key)
        or ""
    )
    aggregator = _aggregator_from_cube_item(item)
    if aggregator and description:
        return f"{description} (aggregation: {aggregator})"
    if aggregator:
        return f"{_metric_name_from_cube_item(item, primary_name_key)} (aggregation: {aggregator})"
    return str(description)


def _cube_item_has_formula_or_filter(item: dict) -> bool:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    for field in ("formula", "filter"):
        if item.get(field) or metric_obj.get(field):
            return True
    return False


def _business_metric_from_cube_item(
    item: dict,
    table_name: str,
    primary_name_key: str,
    label_key: str,
    metadata_response: Optional[dict] = None,
    column_id_to_ref: Optional[Dict[str, Tuple[str, str]]] = None,
) -> Optional[dict]:
    metric_name = _metric_name_from_cube_item(item, primary_name_key)
    if not table_name or not metric_name:
        return None

    entry: dict[str, Any] = {
        "metric": metric_name,
        "description": _description_from_cube_item(item, primary_name_key),
        "tables": [table_name],
    }
    if metadata_response:
        entry["table_alias"] = resolve_table_alias(table_name, metadata_response)
    column_name = _resolve_column_name(item, column_id_to_ref)
    if column_name:
        entry["column_name"] = column_name
        preferred_alias = item.get(primary_name_key)
        if preferred_alias:
            entry["column_alias"] = str(preferred_alias)
        elif metadata_response:
            entry["column_alias"] = resolve_column_alias(
                table_name,
                column_name,
                metadata_response,
            )
    if item.get(primary_name_key):
        entry[label_key] = item[primary_name_key]
    aggregator = _aggregator_from_cube_item(item)
    if aggregator:
        entry["aggregator"] = aggregator
    format_string = _format_string_from_cube_item(item)
    if format_string:
        entry["format_string"] = format_string
    sort_order = _sort_order_raw_from_cube_item(item)
    if sort_order is not None:
        entry["sort_order"] = sort_order
        entry["sort_direction"] = sort_direction_from_value(sort_order)
    default_function = _default_function_from_cube_item(item, metadata_response)
    if default_function:
        entry["default_function"] = default_function

    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    for field in ("formula", "filter"):
        value = item.get(field) or metric_obj.get(field)
        if value:
            entry[field] = value
    for field in ("grain", "direction", "additivity"):
        value = str(item.get(field) or metric_obj.get(field) or "").strip()
        if value:
            entry[field] = value
    return entry


def _append_business_metric(
    metrics: List[dict],
    seen: set[tuple[str, str]],
    entry: Optional[dict],
) -> None:
    if not entry:
        return
    dedupe_key = (entry["metric"], entry["tables"][0])
    if dedupe_key in seen:
        return
    seen.add(dedupe_key)
    metrics.append(entry)


def business_metrics_from_cube_info(cube_info: list, metadata_response: dict) -> List[dict]:
    """Build legacy business_metrics entries from cube_info measures and dimension formulas."""
    cube_info = normalize_cube_info(cube_info)
    column_to_table = build_column_to_table_map(metadata_response)
    table_id_to_name, column_id_to_ref = build_metadata_id_maps(metadata_response)
    known_tables = _known_table_names(metadata_response)
    metrics: List[dict] = []
    seen: set[tuple[str, str]] = set()

    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue

        for measure in cube.get("measures") or []:
            if not isinstance(measure, dict):
                continue
            table_name = _resolve_table_name(
                measure, column_to_table, table_id_to_name, column_id_to_ref
            )
            if not table_name:
                # Formula-only measure: infer the table from the formula so the
                # metric (and its formula) still reaches the SQL prompt.
                table_name = _table_from_formula(
                    _formula_from_cube_item(measure), known_tables
                )
            entry = _business_metric_from_cube_item(
                measure,
                table_name or "",
                "measureName",
                "measure_name",
                metadata_response,
                column_id_to_ref,
            )
            _append_business_metric(metrics, seen, entry)

        for dim in cube.get("dimensions") or []:
            if not isinstance(dim, dict) or not _cube_item_has_formula_or_filter(dim):
                continue
            table_name = _resolve_table_name(
                dim, column_to_table, table_id_to_name, column_id_to_ref
            )
            entry = _business_metric_from_cube_item(
                dim,
                table_name or "",
                "dimensionName",
                "dimension_name",
                metadata_response,
                column_id_to_ref,
            )
            _append_business_metric(metrics, seen, entry)

    return metrics


def merge_business_metrics(*sources: list) -> List[dict]:
    """Merge business metric lists while dropping exact duplicates."""
    merged: List[dict] = []
    seen: set[tuple[Any, tuple]] = set()

    for source in sources:
        for item in source or []:
            if not isinstance(item, dict):
                continue
            key = (item.get("metric"), tuple(item.get("tables") or []))
            if key in seen:
                continue
            seen.add(key)
            merged.append(item)

    return merged


def synonyms_from_cube_info(cube_info: list, metadata_response: dict) -> List[dict]:
    """Build legacy synonym entries from cube_info dimension/measure synonyms."""
    cube_info = normalize_cube_info(cube_info)
    column_to_table = build_column_to_table_map(metadata_response)
    table_id_to_name, column_id_to_ref = build_metadata_id_maps(metadata_response)
    synonyms: List[dict] = []
    seen: set[tuple[str, str]] = set()

    def _append_synonyms(item: dict, name_key: str) -> None:
        item_synonyms = item.get("synonyms") or []
        if not item_synonyms:
            return
        table_name = _resolve_table_name(
            item, column_to_table, table_id_to_name, column_id_to_ref
        )
        if not table_name:
            return
        label = str(item.get(name_key) or "")
        dedupe_key = (table_name, label)
        if dedupe_key in seen:
            return
        seen.add(dedupe_key)
        synonyms.append(
            {
                "database_table": table_name,
                "dimension_name": label,
                "synonyms": [str(entry) for entry in item_synonyms if entry],
            }
        )

    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for dim in cube.get("dimensions") or []:
            if isinstance(dim, dict):
                _append_synonyms(dim, "dimensionName")
        for measure in cube.get("measures") or []:
            if isinstance(measure, dict):
                _append_synonyms(measure, "measureName")
    return synonyms
