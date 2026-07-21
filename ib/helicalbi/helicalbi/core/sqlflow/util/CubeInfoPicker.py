"""Resolve picked cube dimensions and measures for SQL flow responses."""

from __future__ import annotations

import json
import logging
from typing import Any, List, Set, Tuple

from helicalbi.common.JsonToPara import (
    iter_cube_entries,
    split_table_column_ref,
    unquote_identifier,
)
from helicalbi.sql.GetContextForSQL import _lookup_column_meta

logger = logging.getLogger(__name__)


def _normalize_query_plan(query_plan: Any) -> dict:
    if isinstance(query_plan, dict):
        return query_plan
    if isinstance(query_plan, str) and query_plan.strip():
        try:
            parsed = json.loads(query_plan)
        except json.JSONDecodeError:
            logger.error(
                "Invalid query_plan JSON in CubeInfoPicker; using empty plan",
                exc_info=True,
            )
            return {}
        return parsed if isinstance(parsed, dict) else {}
    return {}


def _as_name_list(value: Any) -> List[str]:
    if not value:
        return []
    if isinstance(value, list):
        return [unquote_identifier(str(item)) for item in value if item]
    return [unquote_identifier(str(value))]


def _known_dimension_and_measure_names(
    cube_metadata,
) -> Tuple[Set[str], Set[str], dict]:
    """Collect semantic names from cube dimensions (columns) and measures.

    Returns (dimension_names, measure_names, canonical_name_by_lower).
    """
    dimensions: Set[str] = set()
    measures: Set[str] = set()
    canonical: dict[str, str] = {}

    def _remember(bucket: Set[str], name: Any) -> None:
        text = unquote_identifier(str(name or "")).strip()
        if not text:
            return
        bucket.add(text)
        canonical.setdefault(text.lower(), text)

    for cube in iter_cube_entries(cube_metadata or []):
        for column in cube.get("columns") or []:
            if not isinstance(column, dict):
                continue
            # alias_name is dimensionName from model JSON dimensions section
            _remember(dimensions, column.get("alias_name"))
        for measure in cube.get("measures") or []:
            if not isinstance(measure, dict):
                continue
            # alias_name / measure_name come from model JSON measures section
            _remember(measures, measure.get("alias_name"))
            _remember(measures, measure.get("measure_name"))
    return dimensions, measures, canonical


def _canonicalize(name: str, known: Set[str], canonical: dict) -> str | None:
    text = unquote_identifier(str(name or "")).strip()
    if not text:
        return None
    if text in known:
        return text
    resolved = canonical.get(text.lower())
    if resolved and resolved in known:
        return resolved
    return None


def _filter_to_known(
    names: List[str],
    known: Set[str],
    canonical: dict,
) -> List[str]:
    filtered: List[str] = []
    seen: Set[str] = set()
    for name in names:
        resolved = _canonicalize(name, known, canonical)
        if not resolved or resolved in seen:
            continue
        seen.add(resolved)
        filtered.append(resolved)
    return filtered


def _resolve_cube_for_ref(cube_by_table: dict, table_name: str, col_name: str) -> tuple[dict | None, str]:
    cube = cube_by_table.get(table_name) if table_name else None
    if cube:
        return cube, table_name

    for resolved_table, cube_entry in cube_by_table.items():
        if _lookup_column_meta(cube_entry, col_name):
            return cube_entry, resolved_table
    return None, table_name


def _dimension_or_measure_name(cube: dict, col_name: str) -> Tuple[str | None, str | None]:
    """Map a physical column or alias to its semantic dimension/measure name."""
    target = unquote_identifier(col_name)
    for measure in cube.get("measures") or []:
        if not isinstance(measure, dict):
            continue
        measure_label = (
            measure.get("alias_name")
            or measure.get("measure_name")
            or measure.get("column_name")
        )
        if (
            measure.get("column_name") == target
            or measure.get("alias_name") == target
            or measure.get("measure_name") == target
        ):
            return None, str(measure_label) if measure_label else None
    for column in cube.get("columns") or []:
        if not isinstance(column, dict):
            continue
        if column.get("column_name") == target or column.get("alias_name") == target:
            return str(column.get("alias_name") or column.get("column_name")), None
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
        table_name, col_name = split_table_column_ref(ref)
        if not col_name:
            continue

        cube, _ = _resolve_cube_for_ref(cube_by_table, table_name, col_name)
        if not cube:
            continue

        dimension_name, measure_name = _dimension_or_measure_name(cube, col_name)
        if measure_name:
            metrics.append(measure_name)
        elif dimension_name:
            dimensions.append(dimension_name)

    return list(dict.fromkeys(dimensions)), list(dict.fromkeys(metrics))


def _names_from_business_metrics(required_business_metrics: list) -> Tuple[List[str], List[str]]:
    dimensions: List[str] = []
    metrics: List[str] = []
    for metric in required_business_metrics or []:
        if not isinstance(metric, dict):
            continue
        if metric.get("dimension_name"):
            dimensions.append(unquote_identifier(str(metric["dimension_name"])))
            continue
        name = (
            metric.get("measure_name")
            or metric.get("column_alias")
            or metric.get("metric")
        )
        if name:
            metrics.append(unquote_identifier(str(name)))
    return list(dict.fromkeys(dimensions)), list(dict.fromkeys(metrics))


def build_required_cube_info(
    cube_metadata,
    query_plan: Any,
    required_business_metrics: list | None = None,
) -> dict:
    """Build picked dimension/metric names for SqlSection.required_cube_info.

    Only semantic names from the model JSON dimensions/measures sections
    (exposed as cube_metadata column/measure alias_name) are returned.
    """
    plan = _normalize_query_plan(query_plan)
    known_dimensions, known_measures, canonical = _known_dimension_and_measure_names(
        cube_metadata
    )

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

    picked_dimensions = _filter_to_known(
        explicit_dimensions + derived_dimensions + metric_dimensions,
        known_dimensions,
        canonical,
    )
    picked_metrics = _filter_to_known(
        explicit_metrics + derived_metrics + metric_names,
        known_measures,
        canonical,
    )
    return {
        "picked_dimensions": picked_dimensions,
        "picked_metrics": picked_metrics,
    }
