"""Split large table and column catalogs before an LLM call."""
from __future__ import annotations

from typing import Any, Iterable, List

from helicalbi.common import app_config
from helicalbi.common.JsonToPara import iter_cube_entries


def schema_chunk_size() -> int:
    """How many tables or columns one prompt may contain."""
    try:
        size = int(app_config.schema_chunk_size)
    except (TypeError, ValueError, AttributeError):
        size = 50
    return size if size > 0 else 50


def split_chunks(items: Iterable[Any], size: int | None = None) -> List[list]:
    """Split ``items`` into lists of at most ``size``. One list when it already fits."""
    rows = list(items or [])
    limit = schema_chunk_size() if size is None else int(size)
    if limit <= 0:
        limit = schema_chunk_size()
    if not rows:
        return []
    return [rows[index:index + limit] for index in range(0, len(rows), limit)]


def column_entries(cube_metadata) -> List[tuple]:
    """Physical columns and measures, in catalog order."""
    entries: List[tuple] = []
    for cube in iter_cube_entries(cube_metadata or []):
        table_name = str(cube.get("database_table") or "").strip()
        if not table_name:
            continue
        for bucket in ("columns", "measures"):
            for item in cube.get(bucket) or []:
                if isinstance(item, dict) and item.get("column_name"):
                    entries.append((table_name, bucket, item))
    return entries


def cubes_for_columns(cube_metadata, entries: List[tuple]) -> list:
    """Cube list containing only the columns in ``entries``."""
    originals = {
        str(cube.get("database_table")): cube
        for cube in iter_cube_entries(cube_metadata or [])
        if cube.get("database_table")
    }
    built: dict[str, dict] = {}
    order: List[str] = []
    for table_name, bucket, item in entries:
        cube = built.get(table_name)
        if cube is None:
            source = originals.get(table_name) or {}
            cube = {
                "database_table": table_name,
                "table_alias": source.get("table_alias"),
                "description": source.get("description") or "",
                "columns": [],
                "measures": [],
            }
            built[table_name] = cube
            order.append(table_name)
        cube[bucket].append(item)
    return [built[name] for name in order]


def cubes_for_tables(cube_metadata, table_names: List[str]) -> list:
    """Cubes whose table name is in ``table_names``, in that order."""
    by_name = {
        str(cube.get("database_table")): cube
        for cube in iter_cube_entries(cube_metadata or [])
        if cube.get("database_table")
    }
    return [by_name[name] for name in table_names if name in by_name]


def merge_name_lists(*groups: Iterable[Any]) -> List[str]:
    names: List[str] = []
    for group in groups:
        for item in group or []:
            text = str(item or "").strip()
            if text and text not in names:
                names.append(text)
    return names


def merge_column_plans(plans: List[dict]) -> dict:
    """Union column picks from each chunk. Keep the first row limit."""
    merged: dict = {}
    for key in ("columnName", "selectColumnName", "pickedDimensions", "pickedMetrics"):
        merged[key] = merge_name_lists(*(plan.get(key) or [] for plan in plans))
    for plan in plans:
        limit = plan.get("limit")
        if limit not in (None, "", 0):
            merged["limit"] = limit
            break
    reasons = [str(plan.get("reason") or "").strip() for plan in plans if plan.get("reason")]
    if reasons:
        merged["reason"] = " ".join(reasons)
    return merged
