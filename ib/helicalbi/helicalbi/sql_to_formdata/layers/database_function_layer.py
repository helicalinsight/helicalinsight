"""Attach / normalize databaseFunction on columns & filters."""

from __future__ import annotations

from ..functions_catalog import collect_applied_dbfs
from ..models import ParsedQuery


def attach_database_functions(
    parsed: ParsedQuery,
    columns: list[dict],
    filters: list[dict] | None = None,
    having: list[dict] | None = None,
) -> dict:
    """
    Ensures databaseFunction objects from the parse model are present on wire objects.
    Returns a summary layer including appliedDbfs templates (catalog entries used).
    """
    applied = []
    applied_dbfs: dict[str, dict] = {}

    # Columns already carry databaseFunction from select_layer when present;
    # re-sync from parse model by alias.
    by_alias = {s.alias: s for s in parsed.selects}
    for col in columns:
        item = by_alias.get(col.get("alias"))
        if item and item.database_function and "databaseFunction" not in col:
            from ..functions_catalog import to_wire_database_function

            wire_dbf = to_wire_database_function(item.database_function)
            if wire_dbf:
                col["databaseFunction"] = wire_dbf
        db_fn = col.get("databaseFunction")
        if db_fn:
            applied.append({"target": "column", "alias": col.get("alias"), "databaseFunction": db_fn})
            # appliedDbfs expects catalog-shaped entries; prefer parse model
            catalog_dbf = item.database_function if item else db_fn
            collect_applied_dbfs(catalog_dbf if isinstance(catalog_dbf, dict) else None, applied_dbfs)

    for bucket_name, bucket in (("filters", filters or []), ("having", having or [])):
        for f in bucket:
            db_fn = f.get("databaseFunction")
            if db_fn:
                applied.append({"target": bucket_name, "label": f.get("label"), "databaseFunction": db_fn})
                collect_applied_dbfs(db_fn, applied_dbfs)

    return {
        "applied": applied,
        "appliedDbfs": list(applied_dbfs.values()),
    }
