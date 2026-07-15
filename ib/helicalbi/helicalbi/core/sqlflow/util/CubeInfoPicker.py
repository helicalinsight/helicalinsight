"""Resolve picked cube dimensions and measures for SQL flow responses."""

from __future__ import annotations

import json
from typing import Any, List, Tuple

from helicalbi.common.JsonToPara import iter_cube_entries
from helicalbi.sql.GetContextForSQL import _lookup_column_meta


def _normalize_query_plan(query_plan: Any) -> dict:
    if isinstance(query_plan, dict):
        return query_plan
    if isinstance(query_plan, str) and query_plan.strip():
        try:
            parsed = json.loads(query_plan)
        except json.JSONDecodeError:
            return {}
        return parsed if isinstance(parsed, dict) else {}
    return {}


def _as_name_list(value: Any) -> List[str]:
    if not value:
        return []
    if isinstance(value, list):
        return [str(item) for item in value if item]
    return [str(value)]


def _resolve_cube_for_ref(cube_by_table: dict, table_name: str, col_name: str) -> tuple[dict | None, str]:
    cube = cube_by_table.get(table_name) if table_name else None
    if cube:
        return cube, table_name

    for resolved_table, cube_entry in cube_by_table.items():
        if _lookup_column_meta(cube_entry, col_name):
            return cube_entry, resolved_table
    return None, table_name


def _dimension_or_measure_name(cube: dict, col_name: str) -> Tuple[str | None, str | None]:
    for measure in cube.get("measures") or []:
        if isinstance(measure, dict) and measure.get("column_name") == col_name:
            return None, str(
                measure.get("alias_name")
                or measure.get("measure_name")
                or col_name
            )
    for column in cube.get("columns") or []:
        if isinstance(column, dict) and column.get("column_name") == col_name:
            return str(column.get("alias_name") or col_name), None
    return None, None


def _derive_picks_from_column_refs(
    cube_metadata,
    column_refs: List[Any],
) -> Tuple[List[str], List[str]]:
    cube_by_table = {
        cube.get("database_table"): cube
        for cube in iter_cube_entries(cube_metadata or [])
        if cube.get("database_table")
    }
    dimensions: List[str] = []
    metrics: List[str] = []

    for ref in column_refs or []:
        ref = str(ref).strip()
        if not ref:
            continue

        if "." in ref:
            table_name, col_name = ref.rsplit(".", 1)
        else:
            table_name, col_name = "", ref

        cube, _ = _resolve_cube_for_ref(cube_by_table, table_name, col_name)
        if not cube:
            if col_name:
                dimensions.append(col_name)
            continue

        dimension_name, measure_name = _dimension_or_measure_name(cube, col_name)
        if measure_name:
            metrics.append(measure_name)
        elif dimension_name:
            dimensions.append(dimension_name)
        elif col_name:
            dimensions.append(col_name)

    return list(dict.fromkeys(dimensions)), list(dict.fromkeys(metrics))


def _names_from_business_metrics(required_business_metrics: list) -> Tuple[List[str], List[str]]:
    dimensions: List[str] = []
    metrics: List[str] = []
    for metric in required_business_metrics or []:
        if not isinstance(metric, dict):
            continue
        if metric.get("dimension_name"):
            dimensions.append(str(metric["dimension_name"]))
            continue
        name = (
            metric.get("measure_name")
            or metric.get("column_alias")
            or metric.get("metric")
        )
        if name:
            metrics.append(str(name))
    return list(dict.fromkeys(dimensions)), list(dict.fromkeys(metrics))


def build_required_cube_info(
    cube_metadata,
    query_plan: Any,
    required_business_metrics: list | None = None,
) -> dict:
    """Build picked dimension/metric names for SqlSection.required_cube_info."""
    plan = _normalize_query_plan(query_plan)
    explicit_dimensions = _as_name_list(
        plan.get("pickedDimensions") or plan.get("picked_dimensions")
    )
    explicit_metrics = _as_name_list(
        plan.get("pickedMetrics") or plan.get("picked_metrics")
    )
    derived_dimensions, derived_metrics = _derive_picks_from_column_refs(
        cube_metadata,
        plan.get("columnName") or [],
    )
    metric_dimensions, metric_names = _names_from_business_metrics(
        required_business_metrics or []
    )

    picked_dimensions = list(
        dict.fromkeys(explicit_dimensions + derived_dimensions + metric_dimensions)
    )
    picked_metrics = list(
        dict.fromkeys(explicit_metrics + derived_metrics + metric_names)
    )
    return {
        "picked_dimensions": picked_dimensions,
        "picked_metrics": picked_metrics,
    }
