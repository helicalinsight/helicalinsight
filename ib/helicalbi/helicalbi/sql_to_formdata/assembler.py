"""Assemble layered formData pieces into the final wire payload."""

from __future__ import annotations

import json
import logging
from pathlib import Path
from typing import Any

from .functions_catalog import FunctionCatalog, REFERENCE_TO_SQLGLOT
from .layers import (
    attach_database_functions,
    build_columns,
    build_filters,
    build_functions,
    build_having,
)
from .metadata import build_column_index
from .models import ParsedQuery
from .parser import parse_sql

logger = logging.getLogger(__name__)


def _resolve_parse_dialect(
    dialect: str | None,
    catalog: FunctionCatalog,
) -> str:
    """Map API reference (e.g. postgresql) → sqlglot dialect (postgres)."""
    if dialect:
        key = str(dialect).lower().strip()
        return REFERENCE_TO_SQLGLOT.get(key, key)
    return catalog.dialect


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


def load_metadata_index(
    *,
    location: str,
    metadata_file_name: str,
    session_cookie: str = "",
    metadata: dict | None = None,
) -> dict[str, Any]:
    """
    Build column index from a metadata get payload.

    When ``metadata`` is omitted and ``session_cookie`` is set, fetch via API.
    """
    if metadata is None and session_cookie and metadata_file_name:
        try:
            from helicalbi.api.Metadata import get_json_data_metadata

            metadata = get_json_data_metadata(
                session_cookie, metadata_file_name, location
            )
        except Exception:
            logger.exception(
                "sql_to_formdata: metadata get failed location=%s file=%s",
                location,
                metadata_file_name,
            )
            metadata = None

    if not metadata:
        return {"database": "", "by_column": {}, "by_alias": {}}

    # Already an index (has by_column from build_column_index / report loader)
    if isinstance(metadata.get("by_column"), dict) and (
        "database" in metadata or "tables" not in metadata
    ):
        # Report loader shape — ensure database key exists
        if "database" not in metadata:
            metadata = {**metadata, "database": metadata.get("database") or ""}
        # If report loader only, return as-is (string paths / ids may be partial)
        if "tables" not in metadata:
            return metadata

    return build_column_index(metadata)


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
        "prependTableNameToAlias": False,
    }

    if functions:
        form_data["functions"] = functions

    if parsed.limit is not None:
        form_data["limitBy"] = parsed.limit
    elif meta.get("limitBy") is not None:
        form_data["limitBy"] = meta["limitBy"]

    if parsed.offset is not None:
        form_data["offset"] = parsed.offset

    #if db_fn_layer.get("appliedDbfs"):
    #    form_data["appliedDbfs"] = db_fn_layer["appliedDbfs"]

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

    When ``metadata`` is omitted and ``session_cookie`` is set, metadata get is fetched
    so wire columns can include ``{name, id}`` FQ refs.
    """
    resolved_location = location or metadata_dir or ""
    if catalog is None:
        catalog = load_function_catalog(
            location=resolved_location,
            metadata_file_name=metadata_file_name,
            session_cookie=session_cookie,
            functions_file=functions_file,
        )

    column_index = load_metadata_index(
        location=resolved_location,
        metadata_file_name=metadata_file_name,
        session_cookie=session_cookie,
        metadata=metadata,
    )

    resolved_dialect = _resolve_parse_dialect(dialect, catalog)
    parsed = parse_sql(
        sql,
        dialect=resolved_dialect,
        catalog=catalog,
        database_name=str(column_index.get("database") or ""),
    )
    return assemble_form_data(
        parsed,
        metadata=column_index,
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
