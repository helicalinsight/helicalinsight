"""Match business metrics against required tables and columns."""

from __future__ import annotations

import json
import logging
import re
from typing import Any, Iterable, List, Set, Tuple

logger = logging.getLogger(__name__)

_IDENTIFIER = re.compile(r"[A-Za-z_][A-Za-z0-9_]*")
_QUALIFIED_REF = re.compile(r"([A-Za-z_][A-Za-z0-9_]*)\.([A-Za-z_][A-Za-z0-9_]*)")


from helicalbi.common.JsonToPara import split_table_column_ref


def _normalize_query_plan(query_plan: Any) -> dict:
    if isinstance(query_plan, dict):
        return query_plan
    if isinstance(query_plan, str) and query_plan.strip():
        try:
            parsed = json.loads(query_plan)
        except json.JSONDecodeError:
            logger.error(
                "Invalid query_plan JSON in BusinessMetricMatcher; using empty plan",
                exc_info=True,
            )
            return {}
        return parsed if isinstance(parsed, dict) else {}
    return {}


def extract_required_schema(
    table_list: list,
    query_plan: Any = None,
) -> Tuple[Set[str], Set[str], Set[str]]:
    """Collect required tables, bare columns, and table.column refs."""
    tables = {str(table) for table in (table_list or []) if table}
    columns: Set[str] = set()
    table_columns: Set[str] = set()

    plan = _normalize_query_plan(query_plan)
    for ref in plan.get("columnName") or []:
        table, column = split_table_column_ref(ref)
        if not column:
            continue
        if table:
            tables.add(table)
            table_columns.add(f"{table}.{column}")
        columns.add(column)

    return tables, columns, table_columns


def iter_metric_parts(value: Any, depth: int = 0) -> Iterable[str]:
    """Yield string forms of every key and value in a metric object."""
    if depth > 12:
        return
    if isinstance(value, dict):
        for key, item in value.items():
            yield str(key)
            yield from iter_metric_parts(item, depth + 1)
    elif isinstance(value, list):
        for item in value:
            yield from iter_metric_parts(item, depth + 1)
    elif value is not None:
        yield str(value)


def _identifiers(text: str) -> Set[str]:
    return set(_IDENTIFIER.findall(text or ""))


def _text_references_schema(
    text: str,
    required_tables: Set[str],
    required_columns: Set[str],
    required_table_columns: Set[str],
) -> bool:
    if not text:
        return False

    for ref in required_table_columns:
        if ref and ref in text:
            return True

    for table_name, _ in _QUALIFIED_REF.findall(text):
        if table_name in required_tables:
            return True

    tokens = _identifiers(text)
    # When specific query-plan columns are known, bare table-token matches are
    # too broad and pull unrelated same-table metrics into the final prompt.
    if not required_columns and tokens & required_tables:
        return True
    if tokens & required_columns:
        return True
    return False


def metric_matches_required_schema(
    metric: dict,
    required_tables: Set[str],
    required_columns: Set[str],
    required_table_columns: Set[str],
) -> bool:
    """Return True when a metric references any required table or column."""
    if not isinstance(metric, dict):
        return False

    has_column_targets = bool(required_columns or required_table_columns)
    for table in metric.get("tables") or []:
        if not has_column_targets and str(table) in required_tables:
            return True

    column_name = metric.get("column_name")
    if column_name and str(column_name) in required_columns:
        return True

    all_parts: list[str] = []
    for part in iter_metric_parts(metric):
        all_parts.append(part)
        if _text_references_schema(part, required_tables, required_columns, required_table_columns):
            return True

    # Handle nested metrics where table/column tokens are split across keys.
    if required_table_columns and all_parts:
        tokens = _identifiers(" ".join(all_parts))
        for ref in required_table_columns:
            table_name, _, column_name = ref.partition(".")
            if table_name and column_name and table_name in tokens and column_name in tokens:
                return True

    return False


def filter_required_business_metrics(
    business_metrics: list,
    required_tables: list,
    query_plan: Any = None,
) -> List[dict]:
    """Keep metrics whose tables, formula, filter, or fields match required schema."""
    required_table_set, required_column_set, required_table_columns = extract_required_schema(
        required_tables,
        query_plan,
    )
    if not required_table_set and not required_column_set:
        return []

    matched: List[dict] = []
    for metric in business_metrics or []:
        if not isinstance(metric, dict):
            continue
        if metric_matches_required_schema(
            metric,
            required_table_set,
            required_column_set,
            required_table_columns,
        ):
            matched.append(metric)
    return matched
