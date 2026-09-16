"""Assemble formData parts into the final wire payload."""

from __future__ import annotations

import json
import logging
from pathlib import Path
from typing import Any

from helicalbi.common.DialectMapper import resolve_sqlglot_dialect

from .functions_catalog import FunctionCatalog
from .sql_components import (
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
    """Map HI / getFunctions reference through DialectMapper (derby → oracle)."""
    return resolve_sqlglot_dialect(dialect or catalog.reference) or "postgres"


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

    When ``metadata`` is omitted and ``session_cookie`` is set, metadata get is
    fetched as a legacy fallback. Prefer passing metadata from agent load
    (``ModelLayerHelper.get_metadata()`` / ``provideMetadata``).
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
    include_parts: bool = False,
) -> dict[str, Any]:
    """
    Build formData from ParsedQuery using independent parts, then merge.

    Only includes keys that come from the SQL / metadata inputs — no skeleton
    defaults (requestId, refresh, analytics, etc.).
    """
    meta = dict(metadata or {})
    # Dialect for databaseFunction wire quoting ("table"."column" vs `table`.`column`).
    if parsed.dialect and not meta.get("dialect"):
        meta["dialect"] = parsed.dialect

    columns = build_columns(parsed, meta)
    filters = build_filters(parsed, meta)
    having = build_having(parsed, meta)
    functions = build_functions(parsed, columns)
    db_fn_parts = attach_database_functions(parsed, columns, filters, having)

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

    #if db_fn_parts.get("appliedDbfs"):
    #    form_data["appliedDbfs"] = db_fn_parts["appliedDbfs"]

    if filters:
        form_data["filters"] = filters
        form_data["customFilterExpression"] = _build_indexed_expression(filters)

    if having:
        form_data["having"] = having
        form_data["customHavingExpression"] = _build_indexed_expression(having)

    filter_expression = _build_filter_expression(filters, having)
    if filter_expression is not None:
        form_data["filterExpression"] = filter_expression

    if include_parts:
        form_data["_parts"] = {
            "select": columns,
            "groupby": functions.get("groupBy", []),
            "functions": functions,
            "databaseFunction": db_fn_parts,
            "filters": filters,
            "having": having,
        }

    return fold_having_into_filters(form_data)


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
    include_parts: bool = False,
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
        include_parts=include_parts,
    )


def fold_having_into_filters(form_data: dict[str, Any] | None) -> dict[str, Any] | None:
    """Append ``having[]`` onto ``filters[]`` as-is and drop ``having``.

    Does not rewrite ``customFilterExpression``. ``customHavingExpression`` is
    dropped with ``having`` because that array no longer exists on the wire.

    ``filterExpression`` is alias-based ``[where, having?]`` and is filled here
    when the payload still has a separate ``having`` array (e.g. ChatResponse).
    """
    if not isinstance(form_data, dict):
        return form_data
    payload = dict(form_data)
    having = payload.pop("having", None)
    payload.pop("customHavingExpression", None)
    extra: list[Any] = []
    if isinstance(having, list):
        extra = [
            dict(item) if isinstance(item, dict) else item
            for item in having
            if item not in (None, "")
        ]

    filters = [
        dict(item) if isinstance(item, dict) else item
        for item in (payload.get("filters") or [])
    ]
    if "filterExpression" not in payload:
        expr = _build_filter_expression(filters, extra)
        if expr is not None:
            payload["filterExpression"] = expr

    if extra:
        filters.extend(extra)
        payload["filters"] = filters
    return payload


def _filter_alias(item: Any) -> str:
    if not isinstance(item, dict):
        return ""
    return str(item.get("alias") or item.get("label") or "").strip()


def _join_filter_terms(items: list[Any], term_fn) -> str:
    """Join terms with each item's operator. No operator when there is one item."""
    parts: list[str] = []
    for item in items:
        term = term_fn(item)
        if not term:
            continue
        if not parts:
            parts.append(term)
            continue
        op = "AND"
        if isinstance(item, dict):
            op = str(item.get("operator") or "AND").strip() or "AND"
        parts.append(f"{op} {term}")
    return " ".join(parts)


def _build_indexed_expression(items: list[Any]) -> str:
    index = {"n": 0}

    def _term(_item: Any) -> str:
        token = f"${{{index['n']}}}"
        index["n"] += 1
        return token

    expr = _join_filter_terms(items, _term)
    return f" {expr} " if expr else ""


def _build_filter_expression(
    where_items: list[Any] | None,
    having_items: list[Any] | None,
) -> list[str] | None:
    """Alias expression for WHERE / HAVING, e.g. ``["destination OR source"]``.

    Index 0 is WHERE; index 1 is HAVING when any HAVING predicates exist.
    HAVING-only queries use ``["", "sum_travel_cost OR sum_travelled_by"]``.
    """
    where_items = where_items or []
    having_items = having_items or []
    if not where_items and not having_items:
        return None
    where_expr = _join_filter_terms(where_items, _filter_alias)
    if having_items:
        return [where_expr, _join_filter_terms(having_items, _filter_alias)]
    if not where_expr:
        return None
    return [where_expr]
