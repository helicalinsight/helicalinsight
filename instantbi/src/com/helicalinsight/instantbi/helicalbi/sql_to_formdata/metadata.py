"""Metadata helpers: report JSON loader + live metadata get → column index."""

from __future__ import annotations

import json
from pathlib import Path
from typing import Any


def load_metadata_from_report(path: str | Path) -> dict[str, Any]:
    """
    Extract column type / ID hints from a report save file like formdatajson.json.
    Returns { by_alias, by_column, location, metadataFileName, limitBy, database }.
    """
    data = json.loads(Path(path).read_text(encoding="utf-8"))
    by_alias: dict[str, Any] = {}
    by_column: dict[str, Any] = {}

    for col in data.get("columns") or []:
        entry = {
            "type": col.get("type"),
            "columnID": col.get("columnID"),
            "hidden": col.get("hidden", False),
            "hiddenIncludeInResultSet": col.get("hiddenIncludeInResultSet", False),
            "databaseName": col.get("databaseName"),
        }
        if col.get("autogen_alias"):
            by_alias[col["autogen_alias"]] = entry
        if col.get("label"):
            by_alias[col["label"]] = entry
        if col.get("column"):
            by_column[col["column"]] = entry
            name = col["column"].split(".")[-1]
            by_column[name] = entry

    state = data.get("state") or {}
    meta = data.get("metadata") or {}
    return {
        "by_alias": by_alias,
        "by_column": by_column,
        "location": data.get("location") or meta.get("location", ""),
        "metadataFileName": meta.get("metadataFileName", ""),
        "limitBy": (state.get("options") or {}).get("limitBy", 1000),
        "database": state.get("database") or "",
    }


def build_column_index(metadata_response: dict[str, Any] | None) -> dict[str, Any]:
    """
    Index a metadata ``get`` response for wire column resolution.

    Returns::
        {
          "database": "sampletraveldata.public",
          "by_column": {
             "travel_details.travel_date": {
                 "id": "1065",
                 "name": "sampletraveldata.public.travel_details.travel_date",
                 "type": {"backendDataType": "...", "dataType": "dateTime"},
                 "alias": "travel_date",
             },
             "travel_date": { ... },  # short-name fallback (last wins)
          },
          "by_alias": { ... },
        }
    """
    response = metadata_response or {}
    if "response" in response and isinstance(response["response"], dict):
        response = response["response"]

    database = str(response.get("name") or "").strip()
    by_column: dict[str, Any] = {}
    by_alias: dict[str, Any] = {}

    tables = response.get("tables") or {}
    if not isinstance(tables, dict):
        return {"database": database, "by_column": by_column, "by_alias": by_alias}

    for table_name, table in tables.items():
        if not isinstance(table, dict):
            continue
        columns = table.get("columns") or {}
        if not isinstance(columns, dict):
            continue
        for col_name, col in columns.items():
            if not isinstance(col, dict):
                continue
            short = f"{table_name}.{col_name}"
            fq = f"{database}.{short}" if database else short
            type_info = _normalize_type(col.get("type"))
            entry = {
                "id": str(col.get("id") or ""),
                "name": fq,
                "table": table_name,
                "column": col_name,
                "type": type_info,
                "alias": col.get("alias") or col_name,
            }
            by_column[short] = entry
            by_column[col_name] = entry
            by_column[fq] = entry
            if entry["alias"]:
                by_alias[str(entry["alias"])] = entry

    return {"database": database, "by_column": by_column, "by_alias": by_alias}


def resolve_wire_column(
    table: str | None,
    column: str | None,
    meta: dict[str, Any] | None,
    *,
    fallback_name: str = "",
) -> dict[str, str] | str:
    """
    Build wire ``column`` as ``{name, id}`` when metadata is available.

    Falls back to a plain string path when id/FQ cannot be resolved.
    """
    meta = meta or {}
    by_column = meta.get("by_column") or {}
    database = str(meta.get("database") or "").strip()

    short = ""
    if table and column:
        short = f"{table}.{column}"
    elif column:
        short = column

    entry = None
    if short:
        entry = by_column.get(short)
    if entry is None and column:
        entry = by_column.get(column)

    if isinstance(entry, dict) and entry.get("name"):
        out = {"name": str(entry["name"]), "id": str(entry.get("id") or "")}
        if out["id"] or out["name"]:
            return out

    # No metadata hit — still emit catalog.schema.table.col when possible
    if short:
        name = f"{database}.{short}" if database else short
        return {"name": name, "id": ""} if database else (fallback_name or short)

    return fallback_name or short or ""


def used_column_fq_names(
    cols: list[Any] | None,
    meta: dict[str, Any] | None,
    *,
    fallback: Any = None,
) -> list[str]:
    """Resolve ColumnRef list → unique metadata FQ names for ``usedColumns``."""
    refs = list(cols or [])
    if not refs and fallback is not None:
        refs = [fallback]
    names: list[str] = []
    seen: set[str] = set()
    for col in refs:
        table = getattr(col, "table", None)
        name = getattr(col, "name", None)
        short = getattr(col, "short", "") or ""
        if not name:
            continue
        ref = resolve_wire_column(
            table,
            name,
            meta,
            fallback_name=short,
        )
        fq = ref["name"] if isinstance(ref, dict) else str(ref or "")
        if fq and fq not in seen:
            seen.add(fq)
            names.append(fq)
    return names


def to_raw_database_function(expr: str) -> str:
    """Wrap a catalog-miss SQL expression as Helical ``RAW(...)``."""
    text = (expr or "").strip()
    if not text:
        return ""
    stripped = text.strip()
    if stripped[:4].upper() == "RAW(" and stripped.endswith(")"):
        return stripped
    return f"RAW({text})"


def resolve_host_column_from_used(
    used: list[str],
    meta: dict[str, Any] | None,
    *,
    column: Any = None,
) -> dict[str, str] | None:
    """Pick one physical column from ``usedColumns`` as the RAW host ``{name, id}``."""
    if not used:
        return None
    meta = meta or {}
    if column is not None:
        table = getattr(column, "table", None)
        name = getattr(column, "name", None)
        short = getattr(column, "short", "") or ""
        if name:
            ref = resolve_wire_column(table, name, meta, fallback_name=short)
            as_dict = _as_column_ref_dict(ref, fallback=used[0])
            if as_dict:
                return as_dict
    fq = str(used[0] or "").strip()
    if not fq:
        return None
    by_column = meta.get("by_column") or {}
    entry = by_column.get(fq)
    if isinstance(entry, dict) and entry.get("name"):
        return {"name": str(entry["name"]), "id": str(entry.get("id") or "")}
    parts = [p for p in fq.replace('"', "").split(".") if p]
    if len(parts) >= 2:
        ref = resolve_wire_column(parts[-2], parts[-1], meta, fallback_name=fq)
        as_dict = _as_column_ref_dict(ref, fallback=fq)
        if as_dict:
            return as_dict
    return {"name": fq, "id": ""}


def _as_column_ref_dict(ref: dict[str, str] | str, *, fallback: str) -> dict[str, str] | None:
    if isinstance(ref, dict) and (ref.get("name") or ref.get("id")):
        return {"name": str(ref.get("name") or fallback), "id": str(ref.get("id") or "")}
    text = str(ref or fallback or "").strip()
    if not text:
        return None
    return {"name": text, "id": ""}


def _normalize_type(raw: Any) -> dict[str, str]:
    """Metadata type is ``{java.lang.String: text}`` → backend/dataType dict."""
    if isinstance(raw, dict):
        if "dataType" in raw and "backendDataType" in raw:
            return {
                "backendDataType": str(raw["backendDataType"]),
                "dataType": str(raw["dataType"]),
            }
        if len(raw) == 1:
            backend, data_type = next(iter(raw.items()))
            return {
                "backendDataType": str(backend),
                "dataType": str(data_type),
            }
    return {
        "backendDataType": "java.lang.String",
        "dataType": "text",
    }
