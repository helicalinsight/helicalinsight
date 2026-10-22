"""Resolve viz_form_data (Adhoc preferred, fetchData fallback)."""
from __future__ import annotations

import logging
from typing import Any, Optional

from helicalbi.interactive.fast.data_model import SqlDataModelBuilder
from helicalbi.interactive.fast.session import FastModelSession

logger = logging.getLogger(__name__)


class VizFormDataResolver:
    """Fill ``result['viz_form_data']`` when the viz graph did not supply Adhoc formData."""

    def __init__(self, *, data_model: Optional[SqlDataModelBuilder] = None) -> None:
        self._data_model = data_model or SqlDataModelBuilder()

    def ensure(
        self,
        result: dict[str, Any],
        *,
        session: FastModelSession,
        session_cookie: str,
        formatted_sql: str,
        raw_sql: str,
        rm_cols_in_filter: bool,
        thread_id: Any = None,
        chat_seq_id: Any = None,
    ) -> dict[str, Any]:
        if isinstance(result.get("viz_form_data"), dict):
            return result
        try:
            from helicalbi.viz.viz_model_fill import _try_sql_to_form_data

            form_data = _try_sql_to_form_data(
                formatted_sql or raw_sql,
                session_cookie=session_cookie,
                md_location=session.md_location,
                md_file_name=session.med_file_name,
                dialect=result.get("dialect") or session.metadata_fun_ref.get("reference"),
                metadata=session.actual_md,
                catalog=session.metadata_fun_ref,
                rm_cols_in_filter=rm_cols_in_filter,
            )
            if form_data is None:
                form_data = self._data_model.build(
                    formatted_sql or raw_sql,
                    location=session.md_location,
                    metadata_dir=session.md_location,
                    metadata_file_name=session.med_file_name,
                )
            if form_data is not None:
                result["viz_form_data"] = form_data
        except Exception:
            logger.exception(
                "Interactive data_model failed; data_model omitted thread=%s chat_seq_id=%s",
                thread_id,
                chat_seq_id,
            )
        return result
