"""Response structure returned by the ``/interactive`` (connectLLM) endpoint.

The response is built by aggregating relevant fields from the ``ModelState`` and
the nested ``SQLModel`` state (stored under ``state["sqlModel"]``) into a single
client-friendly payload with the shape::

    chat_response: {
        viz:     { vf_template, chart_name, vf_title, vf_reason },
        sql:     { raw_sql, dialect, required_domain, required_topic,
                   required_table, required_column, required_join, required_cube_info,
                   reason },
        summary: { insight, reason },
        data:    [],
        metadata:    [],
        report_model: {
            data_model: { columns, filters, filterExpression, ... },
            viz_model:  { data, chart, properties, ... },
        },
        token_usage: { input_tokens, output_tokens, total_tokens,
                        input_cost?, output_cost?, total_cost?, model_name?,
                        llm_seconds, total_seconds? }
    }

``data`` and ``metadata`` are omitted from the ``/interactive`` wire response
(see :meth:`ChatResponse.to_interactive_client_dict`) but remain in the full
:meth:`ChatResponse.to_dict` payload used for chat memory. ``viz`` is included
on the wire (``vf_template``, ``chart_name``, ``vf_title``, etc.).
"""
import base64
import json
import logging
import re
from typing import Any, Optional

from pydantic import BaseModel, Field

from helicalbi.common import app_config
from helicalbi.common.LlmInvokeHelper import read_time_consumed, read_token_usage
from helicalbi.model.TokenUsage import TokenUsage
from helicalbi.viz._charts import resolve_chart_name

logger = logging.getLogger(__name__)


class VizSection(BaseModel):
    vf_template: str = Field(default="", description="Visualization function template (JS function string).")
    chart_name: str = Field(default="", description="Selected chart / visualization type, e.g. bar, pie, line.")
    vf_title: str = Field(default="", description="Human friendly visualization title.")
    vf_reason: str = Field(default="", description="Reason for choosing this visualization.")
    similar_chart: list[str] = Field(
        default_factory=list,
        description="Other chart types compatible with the current result shape.",
    )


class ReportModelSection(BaseModel):
    data_model: Optional[dict[str, Any]] = Field(
        default=None,
        description=(
            "Adhoc formData from sql_to_formdata (columns, filters, filterExpression). "
            "Raw fetchData ``query`` is omitted when columns are present so InstantBI "
            "generates SQL from the model. ``location`` and ``metadataFileName`` are "
            "internal assembler inputs and are not sent on the wire."
        ),
    )
    viz_model: Optional[dict[str, Any]] = Field(
        default=None,
        description=(
            "Structured visualization model: data shelves (rows/columns), "
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
        description="Picked cube dimension and measure names from the query planner.",
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
    report_model: ReportModelSection = Field(
        default_factory=ReportModelSection,
        description="Report payload holding data_model and viz_model for InstantBI.",
    )
    token_usage: TokenUsage = Field(
        default_factory=TokenUsage,
        description=(
            "Accumulated LLM token usage, cost, and timing for the request "
            "(includes llm_seconds / total_seconds)."
        ),
    )
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
        required_columns = _append_filter_columns(
            required_columns,
            state.get("viz_form_data"),
        )
        sql_reason = (
            sql_model.get("sql_reason")
            or state.get("sql_reason")
            or _extract_reason_from_query_plan(sql_model)
            or ""
        )

        vf_string = state.get("vf_string")
        chart_name = _as_str(state.get("viz_hint")).replace("_", " ")
        # Catalog Ant Charts use viz_model only; vf_template is for DrawOther / custom VF.
        vf_template_encoded = ""
        if _is_other_chart_type(chart_name) and isinstance(vf_string, str) and vf_string.strip():
            vf_template_encoded = base64.b64encode(vf_string.encode("utf-8")).decode("utf-8")

        viz = VizSection(
            vf_template=vf_template_encoded,
            chart_name=chart_name,
            vf_title=_as_str(state.get("vf_title")),
            vf_reason=_as_str(state.get("viz_reason")),
            similar_chart=_as_similar_charts(state),
        )
        report_model = ReportModelSection(
            data_model=_wire_data_model(state.get("viz_form_data")),
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
        # Internal SQL-prompt payload; keep only flat names for the client.
        required_cube_info = {
            key: value
            for key, value in required_cube_info.items()
            if key != "picked_by_table"
        }
        required_cube_info = _merge_filter_picks(
            required_cube_info,
            sql_model.get("cube_metadata") or state.get("cube_metadata"),
            state.get("viz_form_data"),
        )

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

        return cls(
            viz=viz,
            sql=sql,
            summary=summary,
            data=data,
            metadata=metadata,
            report_model=report_model,
            token_usage=_token_usage_with_timing(state),
            error=_resolved_sql_error(state.get("sql_error") or state.get("error")),
        )

    def to_dict(self) -> dict:
        """Serialise to a plain ``dict`` (pydantic v1/v2 compatible)."""
        return _serialize_payload(self)

    def to_interactive_client_dict(self) -> dict:
        """Serialise for ``/interactive`` wire response without data/metadata."""
        payload = _serialize_payload(self)
        for key in ("data", "metadata"):
            payload.pop(key, None)
        return payload


def _serialize_payload(model: ChatResponse) -> dict:
    if hasattr(model, "model_dump"):
        payload = model.model_dump()
    else:
        payload = model.dict()
    if app_config.hide_prompt_reason:
        _strip_reason_fields(payload)
    return payload


def _token_usage_with_timing(state: dict) -> TokenUsage:
    """Combine state token_usage with legacy time_consumed into one TokenUsage."""
    usage = read_token_usage(state)
    consumed = read_time_consumed(state)
    if consumed.llm_seconds:
        usage.llm_seconds = consumed.llm_seconds
    if consumed.total_seconds is not None:
        usage.total_seconds = consumed.total_seconds
    return usage


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

    ``location`` and ``metadataFileName`` are used internally to fetch metadata
    and functions; the InstantBI client does not read them from ``data_model``.
    ``functions.aggregate`` is omitted for the same reason: measures use
    column ``aggregate`` / ``aggregateList``. ``functions.groupBy`` is kept.

    HAVING predicates are folded into ``filters`` here so the wire ``data_model``
    never exposes a separate ``having`` array.
    """
    if not isinstance(form_data, dict) or not form_data:
        return None
    payload = dict(form_data)
    if payload.get("columns"):
        payload.pop("query", None)
    payload.pop("location", None)
    payload.pop("metadataFileName", None)
    _strip_functions_aggregate(payload)
    from helicalbi.sql_to_formdata import fold_having_into_filters

    return fold_having_into_filters(payload)


def _strip_functions_aggregate(payload: dict[str, Any]) -> None:
    functions = payload.get("functions")
    if not isinstance(functions, dict):
        return
    functions = dict(functions)
    functions.pop("aggregate", None)
    if functions:
        payload["functions"] = functions
    else:
        payload.pop("functions", None)


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


def _as_similar_charts(state: dict) -> list[str]:
    raw = state.get("similar_chart")
    if not isinstance(raw, list):
        context = state.get("viz_column_context")
        if isinstance(context, dict):
            raw = context.get("similar_chart")
    if not isinstance(raw, list):
        return []
    names: list[str] = []
    seen: set[str] = set()
    for item in raw:
        if not isinstance(item, str):
            continue
        text = item.strip()
        if not text:
            continue
        key = text.lower()
        if key in seen:
            continue
        seen.add(key)
        names.append(text)
    return names


def _is_other_chart_type(chart_name: str) -> bool:
    """True only for the catch-all ``other`` chart (not unknown / empty)."""
    hint = _as_str(chart_name).strip()
    if not hint:
        return False
    resolved = resolve_chart_name(hint)
    if resolved:
        return resolved == "other"
    return hint.lower().replace(" ", "_") == "other"


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


_DATE_PART_WORDS = frozenset({
    "year", "quarter", "month", "week", "day", "hour", "minute", "second",
    "distinct", "from", "as", "extract",
})
_QUOTED_TABLE_COLUMN = re.compile(r'"([^"]+)"\s*\.\s*"([^"]+)"')
_BARE_TABLE_COLUMN = re.compile(
    r"\b([A-Za-z_][\w]*)\s*\.\s*([A-Za-z_][\w]*)\b"
)


def _column_names_from_ref(value: Any) -> list[str]:
    """Keep ``table.column``. Unwrap ``EXTRACT(...)`` / ``YEAR(...)`` to that column."""
    text = str(value or "").strip()
    if not text:
        return []
    if "(" not in text:
        return [text]
    without_literals = re.sub(r"'(?:[^']|'')*'", " ", text)
    quoted = [
        f"{table}.{column}"
        for table, column in _QUOTED_TABLE_COLUMN.findall(without_literals)
    ]
    if quoted:
        return quoted
    bare = [
        f"{table}.{column}"
        for table, column in _BARE_TABLE_COLUMN.findall(without_literals)
    ]
    if bare:
        return bare
    idents = re.findall(r"\b([A-Za-z_][\w]*)\b", without_literals)
    columns = [
        name for name in idents[1:] if name.lower() not in _DATE_PART_WORDS
    ]
    return columns[:1]


def _column_names_for_output(columns: list) -> list:
    names: list[str] = []
    seen: set[str] = set()
    for item in columns:
        for name in _column_names_from_ref(item):
            if name in seen:
                continue
            seen.add(name)
            names.append(name)
    return names


def _table_column_labels(value: Any) -> list[str]:
    labels: list[str] = []
    for name in _column_names_from_ref(value):
        parts = [part.strip().strip('"') for part in str(name).split(".") if part.strip()]
        if len(parts) >= 2:
            labels.append(f"{parts[-2]}.{parts[-1]}")
        elif parts:
            labels.append(parts[-1])
    return labels


def _filter_column_refs(form_data: Any) -> list[str]:
    if not isinstance(form_data, dict):
        return []
    refs: list[str] = []
    for key in ("filters", "having"):
        for item in form_data.get(key) or []:
            if not isinstance(item, dict):
                continue
            sources: list[Any] = list(item.get("usedColumns") or [])
            column = item.get("column")
            if isinstance(column, dict):
                sources.append(column.get("name") or "")
            elif column:
                sources.append(column)
            if item.get("databaseFunction"):
                sources.append(item.get("databaseFunction"))
            for source in sources:
                refs.extend(_table_column_labels(source))
    return refs


def _append_filter_columns(columns: list, form_data: Any) -> list:
    extra = _filter_column_refs(form_data)
    if not extra:
        return columns
    return _column_names_for_output(list(columns) + extra)


def _merge_filter_picks(cube_info: dict, cube_metadata: Any, form_data: Any) -> dict:
    refs = _filter_column_refs(form_data)
    if not refs or not cube_metadata:
        return cube_info
    from helicalbi.core.sqlflow.util.CubeInfoPicker import (
        _append_names,
        _derive_picks_from_column_refs,
    )

    dimensions, metrics = _derive_picks_from_column_refs(cube_metadata, refs)
    merged = dict(cube_info)
    merged["picked_dimensions"] = _append_names(
        list(merged.get("picked_dimensions") or []),
        dimensions,
    )
    merged["picked_metrics"] = _append_names(
        list(merged.get("picked_metrics") or []),
        metrics,
    )
    return merged


def _extract_required_columns(sql_model: dict) -> list:
    """Pull column names from the ``query_plan`` (stored as a JSON string).

    Database functions such as ``EXTRACT(YEAR FROM table.column)`` are reduced
    to the column they wrap. The client column list does not include the function.
    """
    query_plan = sql_model.get("query_plan")
    raw: list = []
    if not query_plan:
        return []
    if isinstance(query_plan, dict):
        raw = _as_list(query_plan.get("columnName"))
    elif isinstance(query_plan, str):
        try:
            parsed = json.loads(query_plan)
        except (json.JSONDecodeError, TypeError):
            logger.error(
                "query_plan is not valid JSON, returning empty column list",
                exc_info=True,
            )
            return []
        if isinstance(parsed, dict):
            raw = _as_list(parsed.get("columnName"))
    return _column_names_for_output(raw)


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
