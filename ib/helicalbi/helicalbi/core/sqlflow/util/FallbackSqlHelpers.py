"""Pure helpers for metadata-fallback SQL hardening (no LLM imports)."""

from __future__ import annotations

import re
from typing import Any, List, Optional, Set

_TABLE_FROM_REF = re.compile(r"\b([A-Za-z_][A-Za-z0-9_]*)\.[A-Za-z_][A-Za-z0-9_]*\b")
_FROM_JOIN_TABLE = re.compile(
    r"\b(?:FROM|JOIN)\s+([A-Za-z_][A-Za-z0-9_]*)\b",
    re.IGNORECASE,
)


def needs_llm_table_narrowing(
    input_tables: List[str],
    known_tables: List[str],
    topics,
) -> bool:
    """True when topic matching could not reduce a multi-table catalog."""
    del topics  # topics emptiness is reflected in un-narrowed input_tables
    if not known_tables:
        return False
    if not input_tables:
        return True
    known_set = {str(t) for t in known_tables if t}
    input_set = {str(t) for t in input_tables if t}
    return input_set == known_set and len(known_set) > 1


def tables_from_query_plan(query_plan: dict, known_tables: Optional[List[str]] = None) -> List[str]:
    """Extract distinct table names from qualified query-plan columns."""
    known_lookup = {str(table) for table in (known_tables or []) if table}
    tables: List[str] = []
    seen = set()
    for raw_ref in (query_plan or {}).get("columnName") or []:
        ref = str(raw_ref).strip()
        if not ref or "." not in ref:
            continue
        table_name = ref.rsplit(".", 1)[0].strip()
        if not table_name or table_name in seen:
            continue
        if known_lookup and table_name not in known_lookup:
            continue
        seen.add(table_name)
        tables.append(table_name)
    return tables


def is_join_api_error(output: dict) -> bool:
    if not isinstance(output, dict) or not output:
        return False
    if output.get("query"):
        return False
    message = str(output.get("message") or "")
    class_name = str(output.get("className") or "")
    if class_name:
        return True
    lowered = message.lower()
    return "error" in lowered or "exception" in lowered


def _sql_text_from_prev_entry(entry: Any) -> str:
    previous_sql_obj = None
    if isinstance(entry, dict):
        previous_sql_obj = entry.get("previous_sql")
    elif hasattr(entry, "sql"):
        return str(getattr(entry, "sql") or "")

    if previous_sql_obj is None:
        return ""
    if isinstance(previous_sql_obj, dict):
        return str(previous_sql_obj.get("sql") or "")
    return str(getattr(previous_sql_obj, "sql", "") or "")


def tables_mentioned_in_sql(sql: str) -> Set[str]:
    if not sql:
        return set()
    tables = set(_FROM_JOIN_TABLE.findall(sql))
    tables.update(match.group(1) for match in _TABLE_FROM_REF.finditer(sql))
    return {t for t in tables if t}


def filter_previous_sql_for_context(prev_sql: List[Any], allowed_tables: Set[str]) -> List[Any]:
    """Keep previous SQL only when it stays within currently allowed tables."""
    if not prev_sql:
        return []
    if not allowed_tables:
        return prev_sql

    filtered: List[Any] = []
    for entry in prev_sql:
        sql_text = _sql_text_from_prev_entry(entry)
        mentioned = tables_mentioned_in_sql(sql_text)
        if not mentioned:
            continue
        if mentioned.issubset(allowed_tables):
            filtered.append(entry)
    return filtered
