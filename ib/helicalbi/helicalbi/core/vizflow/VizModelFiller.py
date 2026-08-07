"""Deterministic VizModel shelves/chart fill (no LLM).

Domain styling (color/background) and label polish happen in VizPropertiesPolish.
Other/custom charts still route to Fallback.
"""
from __future__ import annotations

import logging

from helicalbi.model.ModelState import ModelState
from helicalbi.viz._charts import (
    is_other_chart,
    needs_other_fallback,
    requests_functional_formatting,
)
from helicalbi.viz.viz_model_fill import (
    resolve_similar_for_model,
    viz_model_to_chart_settings,
    build_viz_model,
)

logger = logging.getLogger(__name__)


class VizModelFiller:
    """Deterministic shelves + chart type. Leaves property polish to the next node."""

    def process_flow(self, state: ModelState):
        logger.info("VizModelFiller flow started")
        if state.get("skip"):
            return state

        user_query = state.get("query", "")
        data_json = state.get("sql_result")
        if isinstance(data_json, str) and str(data_json).startswith("Not"):
            state["skip"] = True
            return state
        if not isinstance(data_json, dict) or "metadata" not in data_json:
            state["skip"] = True
            return state

        data_md = data_json["metadata"]
        data_rows = data_json.get("data") or state.get("data") or []
        sample_row = data_rows[0] if data_rows else {}
        sql = state.get("sql") or ""

        try:
            existing_hint = str(
                state.get("viz_hint") or state.get("visualization") or ""
            ).strip()
            viz_model, chart_type, viz_context = build_viz_model(
                data_types=data_md,
                sql=sql,
                sample_row=sample_row if isinstance(sample_row, dict) else None,
                viz_hint=existing_hint,
                user_query=user_query,
                vf_title=str(state.get("vf_title") or ""),
                format_strings=state.get("format_strings") or {},
                cube_metadata=state.get("cube_metadata") or [],
                ai_instructions=state.get("ai_instructions") or {},
                sort_orders=state.get("sort_orders") or [],
                domain_context=(
                    state.get("sql_domain_context")
                    or state.get("domain_context")
                    or ""
                ),
                session_cookie=str(state.get("session_cookie") or ""),
                md_location=str(state.get("md_location") or ""),
                md_file_name=str(state.get("md_file_name") or ""),
                dialect=state.get("dialect") or None,
            )

            state["viz_model"] = viz_model.model_dump()
            state["visualization"] = chart_type
            state["viz_hint"] = chart_type
            state["vf_title"] = viz_model.properties.title or ""
            form_source = (
                "sql_to_formdata"
                if (viz_context or {}).get("form_data") is not None
                else "metadata"
            )
            state["viz_reason"] = (
                f"Deterministic shelves via {form_source}; chart from result shape"
            )
            state["similar_chart"] = resolve_similar_for_model(chart_type, data_md)
            state["viz_column_context"] = viz_context
            if (viz_context or {}).get("form_data") is not None:
                state["viz_form_data"] = viz_context["form_data"]
            state["chart_settings"] = viz_model_to_chart_settings(viz_model)

            if needs_other_fallback(chart_type, user_query):
                functional = requests_functional_formatting(user_query)
                logger.info(
                    "VizModelFiller deferring to Fallback chart=%s "
                    "other_chart=%s functional_format=%s",
                    chart_type,
                    is_other_chart(chart_type),
                    functional,
                )
                state["use_other_fallback"] = True
                state["fallback_reason"] = (
                    "functional_formatting"
                    if functional and not is_other_chart(chart_type)
                    else "other_chart"
                )
                return state

            state["use_other_fallback"] = False
            logger.info(
                "VizModelFiller done chart=%s title=%s rows=%s columns=%s "
                "(property polish next)",
                chart_type,
                state["vf_title"],
                viz_model.data.rows,
                viz_model.data.columns,
            )
        except Exception:
            logger.exception("VizModelFiller flow failed")

        return state
