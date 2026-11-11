"""Resolve cube_info columns and tables against metadata."""

from __future__ import annotations

import re
from typing import Dict, Optional, Set, Tuple

from helicalbi.common.cube_info.normalize import _ai_context_from_item


def build_column_to_table_map(metadata_response: dict) -> Dict[str, str]:
    """Map physical column names to table names from the metadata API response."""
    column_to_table: Dict[str, str] = {}
    tables = (metadata_response or {}).get("tables") or {}

    for table_name, table in tables.items():
        if not table_name or not isinstance(table, dict):
            continue
        for col_name in (table.get("columns") or {}):
            if col_name:
                column_to_table[str(col_name)] = table_name

    return column_to_table


def build_metadata_id_maps(
    metadata_response: dict,
) -> Tuple[Dict[str, str], Dict[str, Tuple[str, str]]]:
    """Map table/column IDs from the metadata API to physical names."""
    table_id_to_name: Dict[str, str] = {}
    column_id_to_ref: Dict[str, Tuple[str, str]] = {}
    tables = (metadata_response or {}).get("tables") or {}

    for table_name, table in tables.items():
        if not table_name or not isinstance(table, dict):
            continue
        table_id = table.get("id")
        if table_id:
            table_id_to_name[str(table_id)] = table_name
        for col_name, col_meta in (table.get("columns") or {}).items():
            if not col_name:
                continue
            col_id = col_meta.get("id") if isinstance(col_meta, dict) else None
            if col_id:
                column_id_to_ref[str(col_id)] = (table_name, str(col_name))

    return table_id_to_name, column_id_to_ref


def _parse_column_reference(column_name: str) -> Tuple[Optional[str], str]:
    """Split ``table.column`` references; bare column names return ``(None, column)``."""
    column_name = str(column_name).strip()
    if "." in column_name:
        table_name, col_name = column_name.rsplit(".", 1)
        table_name = table_name.strip()
        col_name = col_name.strip()
        if table_name and col_name:
            return table_name, col_name
    return None, column_name


def _default_function_from_cube_item(
    item: dict,
    metadata_response: Optional[dict] = None,
    resolved_table: Optional[str] = None,
    resolved_column: Optional[str] = None,
) -> str:
    """Read defaultFunction from the item, then from metadata for a resolved column.

    Resolved names are arguments so callers do not stamp them onto the cube item.
    """
    for key in ("defaultFunction", "default_function"):
        value = item.get(key)
        if value:
            return str(value)

    table_name = resolved_table if resolved_table is not None else item.get("_resolved_table")
    column_name = resolved_column if resolved_column is not None else item.get("_resolved_column")
    if table_name and column_name and metadata_response:
        table = ((metadata_response or {}).get("tables") or {}).get(table_name) or {}
        col_meta = ((table.get("columns") or {}).get(column_name) or {}) if isinstance(table, dict) else {}
        if isinstance(col_meta, dict):
            for key in ("defaultFunction", "default_function"):
                value = col_meta.get(key)
                if value:
                    return str(value)
    return ""


def _resolve_table_name(
    item: dict,
    column_to_table: Dict[str, str],
    table_id_to_name: Optional[Dict[str, str]] = None,
    column_id_to_ref: Optional[Dict[str, Tuple[str, str]]] = None,
) -> Optional[str]:
    column_name = item.get("columnName")
    if column_name:
        table_name, bare_column = _parse_column_reference(str(column_name))
        if table_name:
            return table_name
        resolved = column_to_table.get(bare_column)
        if resolved:
            return resolved

    table_id = item.get("tableId")
    if table_id and table_id_to_name:
        resolved = table_id_to_name.get(str(table_id))
        if resolved:
            return resolved

    column_id = item.get("columnId")
    if column_id and column_id_to_ref:
        ref = column_id_to_ref.get(str(column_id))
        if ref:
            return ref[0]

    return None


def _resolve_column_name(
    item: dict,
    column_id_to_ref: Optional[Dict[str, Tuple[str, str]]] = None,
) -> Optional[str]:
    column_name = item.get("columnName")
    if column_name:
        _, bare_column = _parse_column_reference(str(column_name))
        return bare_column

    column_id = item.get("columnId")
    if column_id and column_id_to_ref:
        ref = column_id_to_ref.get(str(column_id))
        if ref:
            return ref[1]

    return None


_FORMULA_TABLE_REF = re.compile(r"([A-Za-z_][A-Za-z0-9_]*)\s*\.\s*[A-Za-z_][A-Za-z0-9_]*")


def _known_table_names(metadata_response: Optional[dict]) -> Set[str]:
    """Collect physical table names available in the metadata API response."""
    return {
        str(name)
        for name in ((metadata_response or {}).get("tables") or {})
        if name
    }


def _formula_from_cube_item(item: dict) -> str:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    return str(item.get("formula") or metric_obj.get("formula") or "")


def _ai_context_payload(item: dict) -> Optional[dict]:
    """Return the whole aiContext object when present (for selection prompts)."""
    ai = _ai_context_from_item(item)
    return ai or None


def _table_from_formula(formula: str, known_tables: Set[str]) -> Optional[str]:
    """Infer a table name from qualified ``table.column`` refs inside a formula."""
    matches = _FORMULA_TABLE_REF.findall(formula or "")
    for token in matches:
        if token in known_tables:
            return token
    return matches[0] if matches else None
