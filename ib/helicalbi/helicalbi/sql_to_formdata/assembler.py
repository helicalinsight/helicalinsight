"""Assemble layered formData pieces into the final wire payload."""

from __future__ import annotations

import json
from pathlib import Path
from typing import Any

from .functions_catalog import FunctionCatalog
from .layers import (
    attach_database_functions,
    build_columns,
    build_filters,
    build_functions,
    build_having,
)
from .models import ParsedQuery
from .parser import parse_sql


def load_function_catalog(
    *,
    location: str,
    metadata_file_name: str,
    session_cookie: str = "",
    functions_file: str | Path | None = None,
) -> FunctionCatalog:
    """
    Load getFunctions response and return a FunctionCatalog.

    Prefer `functions_file` when provided (offline / cached). Otherwise call
    ``get_db_function_of_metadata`` (same path as the rest of HelicalBI).
    """
    if functions_file:
        payload = json.loads(Path(functions_file).read_text(encoding="utf-8"))
    else:
        if not session_cookie:
            raise RuntimeError(
                "session_cookie is required to fetch getFunctions "
                "(or pass functions_file / catalog)."
            )
        from helicalbi.api.Metadata import get_db_function_of_metadata

        payload = get_db_function_of_metadata(
            session_cookie, metadata_file_name, location
        )

    return FunctionCatalog.from_api_payload(payload)


def assemble_form_data(
    parsed: ParsedQuery,
    *,
    metadata: dict | None = None,
    location: str = "",
    metadata_file_name: str = "",
    include_layers: bool = False,
) -> dict[str, Any]:
    """
    Build formData from ParsedQuery using independent layers, then merge.

    Only includes keys that come from the SQL / metadata inputs — no skeleton
    defaults (requestId, refresh, analytics, etc.).
    """
    meta = metadata or {}

    columns = build_columns(parsed, meta)
    filters = build_filters(parsed, meta)
    having = build_having(parsed, meta)
    functions = build_functions(parsed, columns)
    db_fn_layer = attach_database_functions(parsed, columns, filters, having)

    form_data: dict[str, Any] = {
        "sql": parsed.sql,
        "location": location or meta.get("location", ""),
        "metadataFileName": metadata_file_name or meta.get("metadataFileName", ""),
        "columns": columns,
    }

    if functions:
        form_data["functions"] = functions

    if parsed.limit is not None:
        form_data["limitBy"] = parsed.limit
    elif meta.get("limitBy") is not None:
        form_data["limitBy"] = meta["limitBy"]

    if parsed.offset is not None:
        form_data["offset"] = parsed.offset

    if db_fn_layer.get("appliedDbfs"):
        form_data["appliedDbfs"] = db_fn_layer["appliedDbfs"]

    if filters:
        form_data["filters"] = filters
        form_data["customFilterExpression"] = _build_expression(len(filters), "AND")

    if having:
        form_data["having"] = having
        form_data["customHavingExpression"] = _build_expression(len(having), "AND")

    if include_layers:
        form_data["_layers"] = {
            "select": columns,
            "groupby": functions.get("groupBy", []),
            "functions": functions,
            "databaseFunction": db_fn_layer,
            "filters": filters,
            "having": having,
        }

    return form_data


def sql_to_form_data(
    sql: str,
    *,
    location: str,
    metadata_file_name: str,
    metadata_dir: str | None = None,
    session_cookie: str = "",
    functions_file: str | Path | None = None,
    catalog: FunctionCatalog | None = None,
    dialect: str | None = None,
    metadata: dict | None = None,
    include_layers: bool = False,
) -> dict[str, Any]:
    """
    End-to-end: getFunctions → catalog (dialect from reference) → parse SQL → assemble formData.

    `location` and `metadata_file_name` are required alongside SQL (used for getFunctions
    unless `catalog` / `functions_file` is supplied). `metadata_dir` defaults to `location`
    when omitted.
    """
    resolved_location = location or metadata_dir or ""
    if catalog is None:
        catalog = load_function_catalog(
            location=resolved_location,
            metadata_file_name=metadata_file_name,
            session_cookie=session_cookie,
            functions_file=functions_file,
        )

    resolved_dialect = dialect or catalog.dialect
    parsed = parse_sql(sql, dialect=resolved_dialect, catalog=catalog)
    return assemble_form_data(
        parsed,
        metadata=metadata,
        location=resolved_location,
        metadata_file_name=metadata_file_name,
        include_layers=include_layers,
    )


def _build_expression(count: int, default_op: str = "AND") -> str:
    if count <= 0:
        return ""
    if count == 1:
        return " ${0} "
    parts = [f"${{{i}}}" for i in range(count)]
    joined = f" {default_op} ".join(parts)
    return f" {joined} "
