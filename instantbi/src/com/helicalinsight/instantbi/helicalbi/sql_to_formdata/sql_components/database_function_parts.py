"""Summarize mapped database-function expressions from the parse model.

Wire behavior (see ``docs/SQL_TO_FORMDATA.md``):

- **SELECT / filters / having** — catalog/signature match → wire
  ``databaseFunction`` string (``FN("table"."column")``); miss with ``usedColumns`` →
  ``RAW(complete expression)`` on a host column; miss with empty ``usedColumns`` →
  ``custom: true`` + SQL ``column``.

This part only reports what was applied (for ``include_parts`` debug).
"""

from __future__ import annotations

from ..functions_catalog import collect_applied_dbfs
from ..models import ParsedQuery


def attach_database_functions(
    parsed: ParsedQuery,
    columns: list[dict],
    filters: list[dict] | None = None,
    having: list[dict] | None = None,
) -> dict:
    applied = []
    applied_dbfs: dict[str, dict] = {}

    by_alias = {s.alias: s for s in parsed.selects}
    for col in columns:
        item = by_alias.get(col.get("alias"))
        sql = ""
        catalog_dbf = None
        if item and item.database_function_sql:
            sql = item.database_function_sql.strip()
            catalog_dbf = item.database_function
        elif item and item.custom_expression:
            sql = str(item.custom_expression).strip()
        elif col.get("databaseFunction"):
            sql = str(col["databaseFunction"]).strip()
        elif col.get("custom") and isinstance(col.get("column"), str):
            sql = str(col["column"]).strip()
        if sql:
            applied.append(
                {"target": "column", "alias": col.get("alias"), "expression": sql}
            )
            collect_applied_dbfs(
                catalog_dbf if isinstance(catalog_dbf, dict) else None, applied_dbfs
            )

    for bucket_name, bucket in (("filters", filters or []), ("having", having or [])):
        for f in bucket:
            if f.get("databaseFunction"):
                applied.append(
                    {
                        "target": bucket_name,
                        "label": f.get("label"),
                        "databaseFunction": f["databaseFunction"],
                    }
                )
            elif f.get("custom") and isinstance(f.get("column"), str):
                applied.append(
                    {
                        "target": bucket_name,
                        "label": f.get("label"),
                        "expression": f["column"],
                    }
                )

    for item in parsed.where_filters + parsed.having_filters:
        if item.database_function:
            collect_applied_dbfs(item.database_function, applied_dbfs)

    return {
        "applied": applied,
        "appliedDbfs": list(applied_dbfs.values()),
    }
