import json
import logging
import traceback

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.ChatManager import add_viz_response
from helicalbi.common.CubeInfoModel import build_viz_column_context
from helicalbi.core.vizflow.util.ChartCodeTransform import transform_chart_code
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.model.ModelState import ModelState
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.VizFillPrompt import (
    fill_formats_prompt_string,
    fill_settings_prompt_string,
)
from helicalbi.model.output.viz.VizResponse import (
    ChartFillerResponse,
    ChartFormatResponse,
)
from helicalbi.viz._charts import (
    get_chart_config,
    get_chart_definition,
    is_other_chart,
    needs_other_fallback,
    requests_functional_formatting,
)
from helicalbi.model.output.viz.ChartSettings import DimensionSetting
from helicalbi.viz.chart_conversion import (
    apply_chart_settings,
    _infer_columns_from_metadata,
)

logger = logging.getLogger(__name__)


def _normalize_field_token(value: str) -> str:
    text = str(value or "").strip()
    if not text:
        return ""
    # table.column / cube.path.leaf -> leaf token
    return text.rsplit(".", 1)[-1].strip().lower()


def _build_result_field_index(result_field_names):
    """Map lookup tokens (full name + leaf) -> canonical result header name."""
    index = {}
    for name in result_field_names or []:
        canonical = str(name or "").strip()
        if not canonical:
            continue
        index.setdefault(canonical.lower(), canonical)
        leaf = _normalize_field_token(canonical)
        if leaf:
            index.setdefault(leaf, canonical)
    return index


def _resolve_to_result_field(key, result_index, alias_index=None):
    """Resolve an LLM/cube alias key to a result header column when possible."""
    name = str(key or "").strip()
    if not name:
        return None
    lowered = name.lower()
    if lowered in result_index:
        return result_index[lowered]
    leaf = _normalize_field_token(name)
    if leaf in result_index:
        return result_index[leaf]
    if alias_index:
        # alias_index: normalized alias/token -> canonical result field
        if lowered in alias_index:
            return alias_index[lowered]
        if leaf and leaf in alias_index:
            return alias_index[leaf]
    return None


def _build_alias_index(result_field_names, format_strings=None, cube_metadata=None):
    """Map cube/format aliases onto result header columns (not the reverse dump)."""
    from helicalbi.common.CubeInfoModel import _cube_fields_by_name, _match_map_value

    result_index = _build_result_field_index(result_field_names)
    alias_index = dict(result_index)

    # format_strings keys that refer to a result column become alias lookups.
    if isinstance(format_strings, dict):
        for field in result_field_names or []:
            canonical = str(field or "").strip()
            if not canonical:
                continue
            # If this result column has a format entry (possibly under another key),
            # index every format_strings key whose leaf matches this column.
            matched = _match_map_value(format_strings, canonical)
            leaf = _normalize_field_token(canonical)
            for key in format_strings.keys():
                token = str(key or "").strip()
                if not token:
                    continue
                t_leaf = _normalize_field_token(token)
                if token.lower() == canonical.lower() or (leaf and t_leaf == leaf):
                    alias_index.setdefault(token.lower(), canonical)
                    if t_leaf:
                        alias_index.setdefault(t_leaf, canonical)
                elif matched is not None and t_leaf == leaf:
                    alias_index.setdefault(token.lower(), canonical)

    # Cube alias_name / measure_name / dimension_name / column_name -> result field.
    cube_index = _cube_fields_by_name(cube_metadata)
    for field in result_field_names or []:
        canonical = str(field or "").strip()
        if not canonical:
            continue
        cube_item = cube_index.get(canonical.lower()) or cube_index.get(
            _normalize_field_token(canonical)
        ) or {}
        if not cube_item:
            continue
        for attr in (
            "alias_name",
            "dimension_name",
            "measure_name",
            "level_name",
            "column_name",
        ):
            value = cube_item.get(attr)
            if value in (None, ""):
                continue
            token = str(value).strip()
            alias_index.setdefault(token.lower(), canonical)
            leaf = _normalize_field_token(token)
            if leaf:
                alias_index.setdefault(leaf, canonical)

    return alias_index


def _filter_settings_formats(
    settings,
    result_field_names,
    result_format_strings,
    *,
    format_strings=None,
    cube_metadata=None,
):
    """Keep measure_formats only for SQL result metrics/columns.

    Display aliases from the LLM are resolved onto the underlying result
    column/metric name when possible. Unrelated cube aliases are dropped.
    """
    if settings is None:
        return settings

    result_index = _build_result_field_index(result_field_names)
    alias_index = _build_alias_index(
        result_field_names,
        format_strings=format_strings,
        cube_metadata=cube_metadata,
    )
    filtered = {}

    # Start from result-header formats (already scoped to SQL columns).
    for name, fmt in (result_format_strings or {}).items():
        canonical = _resolve_to_result_field(name, result_index, alias_index)
        val = str(fmt or "").strip()
        if canonical and val:
            filtered.setdefault(canonical, val)

    # LLM may key formats by a display alias; remap to the metric/column name.
    for key, value in (settings.measure_formats or {}).items():
        canonical = _resolve_to_result_field(key, result_index, alias_index)
        fmt = str(value or "").strip()
        if canonical and fmt:
            filtered[canonical] = fmt

    # Also accept formats keyed by the chosen measures list when they resolve.
    for measure in getattr(settings, "measures", None) or []:
        canonical = _resolve_to_result_field(measure, result_index, alias_index)
        if not canonical or canonical in filtered:
            continue
        # Prefer already-filtered result formats; else resolve from full map value only.
        from helicalbi.common.CubeInfoModel import _match_map_value

        fmt = (result_format_strings or {}).get(canonical) or _match_map_value(
            format_strings, canonical
        )
        if fmt:
            filtered[canonical] = str(fmt).strip()

    settings.measure_formats = filtered
    return settings


def _coerce_settings_to_result_columns(
    settings,
    *,
    data_types,
    result_field_names,
    sample_row=None,
    format_strings=None,
    cube_metadata=None,
):
    """Force dimensions/measures onto columns that exist in the SQL result.

    Prevents previous-viz / cube hallucinations (e.g. travel_medium) from
    overriding the current query columns (e.g. destination, total_travel_cost).
    Display labels may stay freeform unless they are clearly stale column names.
    """
    if settings is None:
        return settings

    field_names = {
        str(name).strip()
        for name in (result_field_names or [])
        if str(name).strip()
    }
    if isinstance(sample_row, dict):
        field_names.update(
            str(key).strip() for key in sample_row.keys() if str(key).strip()
        )
    if not field_names:
        return settings

    result_index = _build_result_field_index(field_names)
    alias_index = _build_alias_index(
        field_names,
        format_strings=format_strings,
        cube_metadata=cube_metadata,
    )

    meta_dims, meta_measures = _infer_columns_from_metadata(data_types)
    # Prefer metadata roles; fall back to sample_row key order when metadata is thin.
    if not meta_dims and not meta_measures and isinstance(sample_row, dict):
        for key, value in sample_row.items():
            name = str(key).strip()
            if not name:
                continue
            if isinstance(value, (int, float)) and not isinstance(value, bool):
                meta_measures.append(name)
            else:
                # numeric strings still count as measures
                try:
                    float(str(value).replace(",", ""))
                    meta_measures.append(name)
                except (TypeError, ValueError):
                    meta_dims.append(name)

    meta_dims = [result_index.get(n.lower(), n) for n in meta_dims if n]
    meta_measures = [result_index.get(n.lower(), n) for n in meta_measures if n]
    # Keep only names present in the result.
    meta_dims = [n for n in meta_dims if n.lower() in result_index]
    meta_measures = [n for n in meta_measures if n.lower() in result_index]
    # Any leftover result columns go to dims (categorical) or measures.
    assigned = {n.lower() for n in meta_dims + meta_measures}
    for name in field_names:
        if name.lower() in assigned:
            continue
        # Default unmatched columns to dimension unless already have no measures.
        if not meta_measures:
            meta_measures.append(result_index[name.lower()])
        else:
            meta_dims.append(result_index[name.lower()])

    llm_dims = list(settings.dimension_names() or [])
    llm_measures = [str(m).strip() for m in (settings.measures or []) if str(m).strip()]
    stale_tokens = {
        t.lower()
        for t in llm_dims + llm_measures
        if t and not _resolve_to_result_field(t, result_index, alias_index)
    }

    resolved_dims = []
    for name in llm_dims:
        canonical = _resolve_to_result_field(name, result_index, alias_index)
        if canonical and canonical not in resolved_dims:
            resolved_dims.append(canonical)

    resolved_measures = []
    for name in llm_measures:
        canonical = _resolve_to_result_field(name, result_index, alias_index)
        if canonical and canonical not in resolved_measures:
            # A column can't be both dim and measure; prefer measure if LLM said so.
            resolved_measures.append(canonical)
            if canonical in resolved_dims:
                resolved_dims = [d for d in resolved_dims if d != canonical]

    if not resolved_dims:
        resolved_dims = [d for d in meta_dims if d not in resolved_measures] or list(
            meta_dims
        )
    if not resolved_measures:
        resolved_measures = [
            m for m in meta_measures if m not in resolved_dims
        ] or list(meta_measures)

    # Final safety: never keep a field absent from the result header.
    resolved_dims = [
        d for d in resolved_dims if d.lower() in result_index
    ] or [next(iter(dict.fromkeys(result_index.values())))]
    resolved_measures = [
        m for m in resolved_measures if m.lower() in result_index
    ]
    if not resolved_measures:
        # last resort: any result field not used as dimension
        resolved_measures = [
            result_index[k]
            for k in result_index
            if result_index[k] not in resolved_dims
        ] or list(dict.fromkeys(result_index.values()))

    settings.dimensions = DimensionSetting(names=resolved_dims)
    settings.measures = resolved_measures

    if settings.series:
        series = _resolve_to_result_field(
            settings.series, result_index, alias_index
        )
        settings.series = series

    dim0 = resolved_dims[0] if resolved_dims else ""
    meas0 = resolved_measures[0] if resolved_measures else ""
    meas1 = resolved_measures[1] if len(resolved_measures) > 1 else ""

    def _refresh_label(label, fallback):
        text = (label or "").strip()
        if not text:
            return fallback or None
        # Keep pretty aliases; replace stale copied column names from previous viz.
        if text.lower() in stale_tokens:
            return fallback or None
        if _resolve_to_result_field(text, result_index, alias_index):
            # Label is itself a result column name — OK (or remap to canonical).
            return _resolve_to_result_field(text, result_index, alias_index)
        return text

    settings.labelsX = _refresh_label(settings.labelsX, dim0)
    settings.labelsY = _refresh_label(settings.labelsY, meas0)
    settings.labelsZ = _refresh_label(settings.labelsZ, meas1 or None)

    title = (settings.title or "").strip()
    if title and any(token in title.lower() for token in stale_tokens):
        settings.title = (
            f"{meas0} by {dim0}" if meas0 and dim0 else (meas0 or dim0 or None)
        )

    return settings


class ChartFiller:
    def process_flow(self, state: ModelState):
        logger.info("ChartFiller flow started")
        if state.get("skip"):
            return state

        user_query = state.get("query", "")

        data_json = state["sql_result"]
        if isinstance(data_json, str) and data_json.startswith("Not"):
            state["skip"] = True
            return state
        data_md = data_json["metadata"]
        data_rows = data_json.get("data") or state.get("data") or []
        sample_row = data_rows[0] if data_rows else {}
        sql = state.get("sql") or ""

        try:
            viz_hint = (
                str(state["viz_hint"])
                .strip()
                .lower()
            )
            # ``other`` charts and user-requested functional/custom formatters
            # are generated as full JS in Fallback — not settings/format injection.
            if needs_other_fallback(viz_hint, user_query):
                functional = requests_functional_formatting(user_query)
                logger.info(
                    "ChartFiller deferring to Fallback viz_hint=%s "
                    "other_chart=%s functional_format=%s",
                    viz_hint,
                    is_other_chart(viz_hint),
                    functional,
                )
                state["use_other_fallback"] = True
                state["fallback_reason"] = (
                    "functional_formatting"
                    if functional and not is_other_chart(viz_hint)
                    else "other_chart"
                )
                return state

            state["use_other_fallback"] = False
            chart_config = get_chart_config()
            chart_function = chart_config.get(viz_hint, chart_config["other"])
            chart_def = get_chart_definition(viz_hint) or get_chart_definition("other")

            domain_context = (
                state.get("sql_domain_context")
                or state.get("domain_context")
                or ""
            )
            viz_context = build_viz_column_context(
                data_md,
                cube_metadata=state.get("cube_metadata") or [],
                format_strings=state.get("format_strings") or {},
                ai_instructions=state.get("ai_instructions") or {},
                sort_orders=state.get("sort_orders") or [],
                domain_context=domain_context,
            )
            # Prefer result-column-filtered hints; never fall back to full-cube prompts.
            column_format_strings = viz_context.get("column_format_strings") or ""
            column_ai_instructions = viz_context.get("column_ai_instructions") or ""
            column_sort_orders = viz_context.get("column_sort_orders") or ""
            column_viz_context = viz_context.get("column_context") or ""
            # Formats keyed only by executeQuery / SQL result column names.
            result_format_strings = viz_context.get("format_strings") or {}
            result_field_names = {
                str(name).strip()
                for name in (viz_context.get("field_names") or [])
                if str(name).strip()
            }
            state["viz_column_context"] = viz_context

            prompt_inputs = {
                "domain": state.get("domain") or [],
                "topics": state.get("topics") or [],
                "domain_context": domain_context,
                "sql": sql,
                "user_question": user_query,
                "data_types": data_md,
                "sample_row": json.dumps(sample_row, default=str),
                "chart_function": chart_function,
                "column_format_strings": column_format_strings,
                "column_ai_instructions": column_ai_instructions,
                "column_sort_orders": column_sort_orders,
                "column_viz_context": column_viz_context,
            }

            logger.info(
                "ChartFiller settings fill viz_hint=%s result_fields=%s "
                "ai_instructions_chars=%s",
                viz_hint,
                viz_context.get("field_names") or [],
                len(column_ai_instructions),
            )
            settings_vars = [
                "domain",
                "topics",
                "domain_context",
                "sql",
                "user_question",
                "data_types",
                "sample_row",
                "chart_function",
                "column_ai_instructions",
                "column_sort_orders",
                "column_viz_context",
            ]
            settings_inputs = {
                key: prompt_inputs[key]
                for key in settings_vars
            }
            settings_parser = PydanticOutputParser(
                pydantic_object=ChartFillerResponse
            )
            settings_prompt = PromptTemplate(
                template=fill_settings_prompt_string + format_instruction_string,
                input_variables=settings_vars,
                partial_variables={
                    "format_instructions": settings_parser.get_format_instructions()
                },
            )
            settings_response, _ = invoke_structured(
                settings_prompt,
                llm,
                settings_parser,
                settings_inputs,
                state=state,
            )

            settings = _coerce_settings_to_result_columns(
                settings_response.settings,
                data_types=data_md,
                result_field_names=result_field_names,
                sample_row=sample_row if isinstance(sample_row, dict) else None,
                format_strings=state.get("format_strings") or {},
                cube_metadata=state.get("cube_metadata") or [],
            )
            # Formats come from a dedicated prompt — clear any accidental bleed.
            settings.measure_formats = {}

            logger.info(
                "ChartFiller formats fill viz_hint=%s measures=%s "
                "format_strings_chars=%s",
                viz_hint,
                settings.measures,
                len(column_format_strings),
            )
            format_vars = [
                "domain",
                "topics",
                "domain_context",
                "sql",
                "user_question",
                "data_types",
                "sample_row",
                "chosen_settings",
                "column_format_strings",
                "column_ai_instructions",
            ]
            format_inputs = {
                "domain": prompt_inputs["domain"],
                "topics": prompt_inputs["topics"],
                "domain_context": domain_context,
                "sql": sql,
                "user_question": user_query,
                "data_types": data_md,
                "sample_row": prompt_inputs["sample_row"],
                "chosen_settings": json.dumps(
                    settings.to_js_object(), default=str
                ),
                "column_format_strings": column_format_strings,
                "column_ai_instructions": column_ai_instructions,
            }
            format_parser = PydanticOutputParser(
                pydantic_object=ChartFormatResponse
            )
            format_prompt = PromptTemplate(
                template=fill_formats_prompt_string + format_instruction_string,
                input_variables=format_vars,
                partial_variables={
                    "format_instructions": format_parser.get_format_instructions()
                },
            )
            format_response, _ = invoke_structured(
                format_prompt,
                llm,
                format_parser,
                format_inputs,
                state=state,
            )
            settings.measure_formats = dict(
                format_response.measure_formats or {}
            )
            settings = _filter_settings_formats(
                settings,
                result_field_names,
                result_format_strings,
                format_strings=state.get("format_strings") or {},
                cube_metadata=state.get("cube_metadata") or [],
            )

            settings_response.settings = settings
            filled = apply_chart_settings(
                settings,
                chart_def=chart_def,
                format_strings=result_format_strings,
            )
            state["vf_string"] = transform_chart_code(filled)
            state["chart_settings"] = settings
            add_viz_response(state["thread_id"], settings_response)
        except Exception:
            logger.exception("ChartFiller flow failed")
            state["output"] = traceback.format_exc()

        return state
