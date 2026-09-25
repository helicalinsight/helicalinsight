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
            _remember(dimensions, column.get("dimension_name"))
            _remember(dimensions, column.get("level_name"))
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


def _explicit_metric_names(plan: dict) -> List[str]:
    """Planner measure names: pickedMetrics and pickedMeasures."""
    return _as_name_list(
        plan.get("pickedMetrics") or plan.get("picked_metrics")
    ) + _as_name_list(
        plan.get("pickedMeasures") or plan.get("picked_measures")
    )


def _column_refs_for_derivation(plan: dict) -> List[Any]:
    """SELECT-clause refs. Filter columns are added separately."""
    select_refs = plan.get("selectColumnName") or plan.get("select_column_name") or []
    if select_refs:
        return select_refs
    return plan.get("columnName") or []


def _ref_key(ref: Any) -> str:
    table_name, col_name = split_table_column_ref(ref)
    table = unquote_identifier(str(table_name or "")).strip().lower()
    column = unquote_identifier(str(col_name or "")).strip().lower()
    if table and column:
        return f"{table}.{column}"
    return column


def _filter_only_refs(plan: dict) -> List[Any]:
    """Columns used outside SELECT (WHERE / HAVING), still listed on columnName."""
    select_refs = plan.get("selectColumnName") or plan.get("select_column_name") or []
    if not select_refs:
        return []
    selected = {_ref_key(ref) for ref in select_refs}
    selected.discard("")
    extras: List[Any] = []
    seen: Set[str] = set()
    for ref in plan.get("columnName") or []:
        key = _ref_key(ref)
        if not key or key in selected or key in seen:
            continue
        seen.add(key)
        extras.append(ref)
    return extras


def _append_names(existing: List[str], extra: List[str]) -> List[str]:
    seen = {name.lower() for name in existing}
    merged = list(existing)
    for name in extra:
        text = str(name or "").strip()
        if not text or text.lower() in seen:
            continue
        seen.add(text.lower())
        merged.append(text)
    return merged


def _used_physical_names(column_refs: List[Any]) -> Set[str]:
    used: Set[str] = set()
    for ref in column_refs or []:
        _, col_name = split_table_column_ref(ref)
        text = unquote_identifier(str(col_name or "")).strip().lower()
        if text:
            used.add(text)
    return used


def _physical_columns_for_semantic_name(cube_metadata, name: str) -> Set[str]:
    """Physical column names (and the semantic name) that a dim/measure maps to."""
    target = unquote_identifier(str(name or "")).strip().lower()
    if not target:
        return set()
    physicals: Set[str] = {target}
    for cube in iter_cube_entries(cube_metadata or []):
        for bucket in ("columns", "measures"):
            for item in cube.get(bucket) or []:
                if not isinstance(item, dict):
                    continue
                labels = (
                    item.get("alias_name"),
                    item.get("measure_name"),
                    item.get("dimension_name"),
                    item.get("level_name"),
                    item.get("column_name"),
                )
                if not any(
                    unquote_identifier(str(label or "")).strip().lower() == target
                    for label in labels
                    if label
                ):
                    continue
                column_name = unquote_identifier(str(item.get("column_name") or "")).strip()
                if column_name:
                    physicals.add(column_name.lower())
    return physicals


def _align_picks_to_used_columns(
    explicit: List[str],
    derived: List[str],
    used_physical: Set[str],
    cube_metadata,
    known: Set[str],
    canonical: dict,
) -> List[str]:
    """Match cube picks to columns the query actually uses.

    Planner names whose physical column is not in the used set are dropped.
    Used columns the planner omitted are still included.
    When the planner names a valid subset of used columns, extra unused
    query-plan fields stay out.
    """
    explicit_known = _filter_to_known(explicit, known, canonical)
    derived_known = _filter_to_known(derived, known, canonical)
    if not used_physical:
        return explicit_known or derived_known

    kept: List[str] = []
    for name in explicit_known:
        physicals = _physical_columns_for_semantic_name(cube_metadata, name)
        if physicals & used_physical:
            kept.append(name)
    if kept:
        return kept
    return derived_known


def build_required_cube_info(
    cube_metadata,
    query_plan: Any,
    required_business_metrics: list | None = None,
) -> dict:
    """Build picked dimension/metric names and full cube items for SQL.

    Returns semantic names plus the full metadata array items (dimensions,
    hierarchies, measures, blank-column computed measures) arranged by table.
    ``formatString`` is stripped from those items — formatting is viz-only.

    Picks follow the columns used in SELECT (or ``columnName`` when SELECT is
    absent). Planner names that are not in those columns are dropped; used
    columns the planner omitted are still included. Columns that appear only
    as filters (in ``columnName`` but not ``selectColumnName``) are added as
    dimensions or metrics as well.
    """
    from helicalbi.sql.GetContextForSQL import collect_picked_column_items

    plan = _normalize_query_plan(query_plan)
    known_dimensions, known_measures, canonical = _known_dimension_and_measure_names(
        cube_metadata
    )

    explicit_dimensions = _as_name_list(
        plan.get("pickedDimensions") or plan.get("picked_dimensions")
    )
    explicit_metrics = _explicit_metric_names(plan)
    used_refs = _column_refs_for_derivation(plan)
    used_physical = _used_physical_names(used_refs)
    derived_dimensions, derived_metrics = _derive_picks_from_column_refs(
        cube_metadata,
        used_refs,
    )
    metric_dimensions, metric_names = _names_from_business_metrics(
        required_business_metrics or []
    )
    if used_physical:
        metric_dimensions = [
            name
            for name in metric_dimensions
            if _physical_columns_for_semantic_name(cube_metadata, name) & used_physical
        ]
        metric_names = [
            name
            for name in metric_names
            if _physical_columns_for_semantic_name(cube_metadata, name) & used_physical
        ]
    derived_dimensions = derived_dimensions + metric_dimensions
    derived_metrics = derived_metrics + metric_names

    picked_dimensions = _align_picks_to_used_columns(
        explicit_dimensions,
        derived_dimensions,
        used_physical,
        cube_metadata,
        known_dimensions,
        canonical,
    )
    picked_metrics = _align_picks_to_used_columns(
        explicit_metrics,
        derived_metrics,
        used_physical,
        cube_metadata,
        known_measures,
        canonical,
    )
    filter_dimensions, filter_metrics = _derive_picks_from_column_refs(
        cube_metadata,
        _filter_only_refs(plan),
    )
    picked_dimensions = _append_names(picked_dimensions, filter_dimensions)
    picked_metrics = _append_names(picked_metrics, filter_metrics)
    plan_for_items = dict(plan)
    plan_for_items["pickedDimensions"] = picked_dimensions
    plan_for_items["pickedMetrics"] = picked_metrics
    plan_for_items["pickedMeasures"] = []
    picked_by_table = collect_picked_column_items(cube_metadata, plan_for_items)
    return {
        "picked_dimensions": picked_dimensions,
        "picked_metrics": picked_metrics,
        "picked_by_table": picked_by_table,
    }
