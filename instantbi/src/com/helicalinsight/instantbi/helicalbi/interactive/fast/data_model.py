"""Fallback fetchData formData when Adhoc sql_to_formdata is unavailable."""
from __future__ import annotations

import base64
from typing import Any

from helicalbi.sql.SqlSanitizer import strip_sql_markdown


class SqlDataModelBuilder:
    """Build ``{query: base64(sql), columns: []}`` for InstantBI fetchData."""

    def build(
        self,
        sql: str,
        *,
        location: str,
        metadata_dir: str,
        metadata_file_name: str,
    ) -> dict[str, Any] | None:
        cleaned_sql = strip_sql_markdown(sql or "").strip()
        if not cleaned_sql or not metadata_file_name or not (location or metadata_dir):
            return None
        return {
            "query": base64.b64encode(cleaned_sql.encode("utf-8")).decode("utf-8"),
            "columns": [],
        }


_BUILDER = SqlDataModelBuilder()


def sql_to_data_model(
    sql: str,
    *,
    location: str,
    metadata_dir: str,
    metadata_file_name: str,
) -> dict[str, Any] | None:
    """Build fetchData formData with base64 SQL in ``query`` and empty columns.

    Used only when sql_to_formdata Adhoc formData is unavailable.
    """
    return _BUILDER.build(
        sql,
        location=location,
        metadata_dir=metadata_dir,
        metadata_file_name=metadata_file_name,
    )
