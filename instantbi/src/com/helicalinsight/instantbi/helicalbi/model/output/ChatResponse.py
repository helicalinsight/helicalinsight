"""Response structure returned by the ``/interactive`` (connectLLM) endpoint.

The response is built by aggregating relevant fields from the ``ModelState`` and
the nested ``SQLModel`` state (stored under ``state["sqlModel"]``) into a single
client-friendly payload with the shape::

    chat_response: {
        viz:     { vf_template, chart_name, vf_title, vf_reason,
                   similar_chart, viz_model },
        sql:     { raw_sql, dialect, required_domain, required_topic,
                   required_table, required_column, required_join, required_cube_info,
                   reason },
        summary: { insight, reason },
        data:    [],
        metadata:    [],
        data_model: { location, metadataFileName, columns, filters, ... },
        token_usage: { input_tokens, output_tokens, total_tokens,
                        input_cost?, output_cost?, total_cost?, model_name? },
        time_consumed: { llm_seconds, total_seconds? }
    }
"""
import base64
import json
import logging
from typing import Any, Optional

from pydantic import BaseModel, Field

from helicalbi.common import app_config
from helicalbi.common.LlmInvokeHelper import read_time_consumed, read_token_usage
from helicalbi.model.TimeConsumed import TimeConsumed
from helicalbi.model.TokenUsage import TokenUsage
from helicalbi.viz._chart_selection import format_similar_chart_wire

logger = logging.getLogger(__name__)


class VizSection(BaseModel):
    vf_template: str = Field(default="", description="Visualization function template (JS function string).")
    chart_name: str = Field(default="", description="Selected chart / visualization type, e.g. bar, pie, line.")
    vf_title: str = Field(default="", description="Human friendly visualization title.")
    vf_reason: str = Field(default="", description="Reason for choosing this visualization.")
    similar_chart: list[dict[str, str]] = Field(
        default_factory=list,
        description=(
            "Current chart plus alternate types that also fit this data "
            '(wire format: [{"vf.heatmap": "heatmap"}, {"vf.column": "column"}, '
            '{"vf.dual_line": "dual line"}, ...]).'
        ),
    )
    viz_model: Optional[dict[str, Any]] = Field(
        default=None,
        description=(
            "Structured visualization model: data shelves (rows/columns/filters), "
            "chart (viz/mark), and properties (labelX/labelY, title, color, background, "
            "formatting)."
        ),
    )


class SqlSection(BaseModel):
    raw_sql: str = Field(default="", description="Final SQL produced by the SQL flow.")
    dialect: str = Field(default="", description="SQL dialect used for generation/execution.")
    required_domain: list[Any] = Field(default_factory=list, description="Domains identified as relevant to the SQL query.")
    required_topic: list[Any] = Field(default_factory=list, description="Topics identified as relevant to the SQL query.")
    required_table: list[Any] = Field(default_factory=list, description="Tables required by the SQL query.")
    required_column: list[Any] = Field(default_factory=list, description="Columns required by the SQL query.")
    required_join: Any = Field(default="", description="Joins required by the SQL query.")
    required_cube_info: dict[str, Any] = Field(
        default_factory=dict,
        description="Picked cube dimensions and measures from the query planner.",
    )
    reason: str = Field(default="", description="LLM reasoning behind the generated SQL.")


class SummarySection(BaseModel):
    insight: str = Field(default="", description="Human readable insight / summary of the result.")
    reason: str = Field(default="", description="Reason / explanation associated with the insight.")




class ChatResponse(BaseModel):
    """Top-level response payload for the connectLLM (``/interactive``) endpoint."""

    viz: VizSection = Field(default_factory=VizSection)
    sql: SqlSection = Field(default_factory=SqlSection)
    summary: SummarySection = Field(default_factory=SummarySection)
    data: list[Any] = Field(default_factory=list, description="Raw rows returned by the SQL execution.")
    metadata: list[Any] = Field(default_factory=list, description="Metadata returned by the SQL execution.")
    data_model: Optional[dict[str, Any]] = Field(
        default=None,
        description=(
            "Adhoc formData from sql_to_formdata (location, metadataFileName, "
            "columns, filters). Raw fetchData ``query`` is omitted when columns "
            "are present so InstantBI generates SQL from the model."
        ),
    )
    token_usage: TokenUsage = Field(default_factory=TokenUsage, description="Accumulated LLM token usage for the request.")
    time_consumed: TimeConsumed = Field(default_factory=TimeConsumed, description="Elapsed time for LLM calls and the full request.")
    error: str = Field(default="", description="SQL execution or flow error message from the query API.")

    @classmethod
    def from_model_state(cls, state: dict) -> "ChatResponse":
        """Build a :class:`ChatResponse` from a final ``ModelState`` dict.

        Pulls visualization fields directly from the top-level ``ModelState``
        and SQL-related fields from the nested ``SQLModel`` state stored under
        ``state["sqlModel"]`` (falling back to top-level keys when the sub-state
        is missing, e.g. when an error short-circuited the flow).
        """
        state = state or {}
        sql_model: dict = state.get("sqlModel") or {}

        sql_result = state.get("sql_result")
        sql_result_dict = sql_result if isinstance(sql_result, dict) else {}

        required_details = state.get("required_details")
        if not isinstance(required_details, dict):
            required_details = sql_model.get("required_details") if isinstance(sql_model.get("required_details"), dict) else {}

        required_columns = _extract_required_columns(sql_model)
        sql_reason = (
            sql_model.get("sql_reason")
            or state.get("sql_reason")
            or _extract_reason_from_query_plan(sql_model)
            or ""
        )

        vf_string = state.get("vf_string")
        vf_template_encoded = (
            base64.b64encode(vf_string.encode("utf-8")).decode("utf-8")
            if isinstance(vf_string, str) and vf_string
            else ""
        )

        chart_name = _as_str(state.get("viz_hint")).replace("_", " ")
        similar_chart = format_similar_chart_wire(
            state.get("similar_chart"),
            chart_name=chart_name,
        )

        viz = VizSection(
            vf_template=vf_template_encoded,
            chart_name=chart_name,
            vf_title=_as_str(state.get("vf_title")),
            vf_reason=_as_str(state.get("viz_reason")),
            similar_chart=similar_chart,
            viz_model=_as_viz_model(state.get("viz_model")),
        )

        required_tables = (
            required_details.get("required_tables")
            if isinstance(required_details, dict)
            else None
        )
        if required_tables is None:
            required_tables = sql_model.get("required_tables") or state.get("required_tables")

        required_join = (
            required_details.get("required_joins")
            if isinstance(required_details, dict)
            else None
        )
        if required_join is None:
            required_join = sql_model.get("required_joins", "")

        required_domain = (
            required_details.get("required_domain")
            if isinstance(required_details, dict)
            else None
        )
        if required_domain is None:
            required_domain = (
                sql_model.get("domain")
                or state.get("domain")
            )

        required_topic = (
            required_details.get("required_topic")
            if isinstance(required_details, dict)
            else None
        )
        if required_topic is None:
            required_topic = (
                sql_model.get("topics")
                or state.get("topics")
            )

        required_cube_info = (
            required_details.get("required_cube_info")
            if isinstance(required_details, dict)
            else None
        )
        if not isinstance(required_cube_info, dict):
            required_cube_info = sql_model.get("required_cube_info")
        if not isinstance(required_cube_info, dict):
            required_cube_info = {}

        sql = SqlSection(
            raw_sql=_as_str(state.get("sql")),
            dialect=_as_str(state.get("dialect")),
            required_domain=_as_list(required_domain),
            required_topic=_as_list(required_topic),
            required_table=_as_list(required_tables),
            required_column=required_columns,
            required_join=required_join or "",
            required_cube_info=required_cube_info,
            reason=_as_str(sql_reason),
        )

        summary = SummarySection(
            insight=_as_insight(state.get("output")),
            reason=_as_str(state.get("output2")),
        )

    
        data=state.get("data") or sql_result_dict.get("data") or []
        metadata=state.get("metadata") or sql_result_dict.get("metadata") or []
        data_model = _wire_data_model(state.get("viz_form_data"))

        return cls(
            viz=viz,
            sql=sql,
            summary=summary,
            data=data,
            metadata=metadata,
            data_model=data_model,
            token_usage=read_token_usage(state),
            time_consumed=read_time_consumed(state),
            error=_resolved_sql_error(state.get("sql_error") or state.get("error")),
        )

    def to_dict(self) -> dict:
        """Serialise to a plain ``dict`` (pydantic v1/v2 compatible)."""
        if hasattr(self, "model_dump"):
            payload = self.model_dump()
        else:
            payload = self.dict()
        if app_config.hide_prompt_reason:
            _strip_reason_fields(payload)
        return payload


def _strip_reason_fields(payload: dict) -> None:
    viz = payload.get("viz")
    if isinstance(viz, dict):
        viz.pop("vf_reason", None)
    sql = payload.get("sql")
    if isinstance(sql, dict):
        sql.pop("reason", None)
    summary = payload.get("summary")
    if isinstance(summary, dict):
        summary.pop("reason", None)


def _wire_data_model(form_data: Any) -> Optional[dict[str, Any]]:
    """Return Adhoc formData for the client, without a fetchData ``query`` override.

    InstantBI ``QueryGeneratorAndExecutor`` executes ``formData.query`` as raw SQL
    when that key is present, ignoring columns/filters. sql_to_formdata models
    therefore must not carry ``query`` (including base64 SQL).
    """
    if not isinstance(form_data, dict) or not form_data:
        return None
    payload = dict(form_data)
    if payload.get("columns"):
        payload.pop("query", None)
    return payload


def _resolved_sql_error(value: Any) -> str:
    if value is None or value == "" or value == "Not Generated":
        return ""
    return _as_str(value)


def _as_str(value: Any) -> str:
    if value is None:
        return ""
    if isinstance(value, str):
        return value
    return str(value)


def _as_insight(value: Any) -> str:
    """Map model-state output to a user-facing insight; never return a traceback."""
    text = _as_str(value)
    if "Traceback (most recent call last)" in text:
        logger.warning("Dropping traceback from summary.insight")
        return ""
    return text


def _as_viz_model(value: Any) -> Optional[dict[str, Any]]:
    """Normalize ModelState.viz_model to a plain dict for the wire response."""
    if value is None or value == "":
        return None
    try:
        from helicalbi.model.output.viz.VizModel import VizModel

        if isinstance(value, VizModel):
            return value.model_dump()
        if isinstance(value, dict):
            return VizModel.model_validate(value).model_dump()
        if hasattr(value, "model_dump"):
            return VizModel.model_validate(value.model_dump()).model_dump()
        if hasattr(value, "dict"):
            return VizModel.model_validate(value.dict()).model_dump()
    except Exception:
        logger.debug("Unable to normalize viz_model; returning raw payload", exc_info=True)
        if isinstance(value, dict):
            return value
        if hasattr(value, "model_dump"):
            try:
                return value.model_dump()
            except Exception:
                logger.debug("Unable to dump viz_model via model_dump", exc_info=True)
        if hasattr(value, "dict"):
            try:
                return value.dict()
            except Exception:
                logger.debug("Unable to dump viz_model via dict()", exc_info=True)
    return None


def _as_list(value: Any) -> list:
    if value is None or value == "":
        return []
    if isinstance(value, list):
        return value
    return [value]


def _extract_required_columns(sql_model: dict) -> list:
    """Pull the column list from the ``query_plan`` (stored as a JSON string)."""
    query_plan = sql_model.get("query_plan")
    if not query_plan:
        return []
    if isinstance(query_plan, dict):
        return _as_list(query_plan.get("columnName"))
    if isinstance(query_plan, str):
        try:
            parsed = json.loads(query_plan)
        except (json.JSONDecodeError, TypeError):
            logger.error(
                "query_plan is not valid JSON, returning empty column list",
                exc_info=True,
            )
            return []
        if isinstance(parsed, dict):
            return _as_list(parsed.get("columnName"))
    return []


def _extract_reason_from_query_plan(sql_model: dict) -> str:
    """Fallback: derive a reason from the column-detection step when no SQL reason was stored."""
    query_plan = sql_model.get("query_plan")
    if isinstance(query_plan, str) and query_plan:
        try:
            parsed = json.loads(query_plan)
        except (json.JSONDecodeError, TypeError):
            logger.error(
                "query_plan is not valid JSON, returning empty reason",
                exc_info=True,
            )
            return ""
        if isinstance(parsed, dict):
            return _as_str(parsed.get("reason"))
    if isinstance(query_plan, dict):
        return _as_str(query_plan.get("reason"))
    return ""
