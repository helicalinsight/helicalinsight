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
from helicalbi.model.output.viz.VizResponse import OtherChartFillerResponse
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.VizFillPrompt import other_fill_prompt_string
from helicalbi.viz._charts import get_chart_config, needs_other_fallback

logger = logging.getLogger(__name__)


class Fallback:
    """Full DrawOther JS fill for ``other`` charts or functional/custom formatters.

    Standard charts use ChartFiller (settings + format injection). This node is
    used when settings injection cannot express the request.
    """

    def process_flow(self, state: ModelState):
        logger.info("Fallback flow started")
        if state.get("skip"):
            return state

        viz_hint = str(state.get("viz_hint") or "").strip().lower()
        user_query = state.get("query", "")
        if not (
            state.get("use_other_fallback")
            or needs_other_fallback(viz_hint, user_query)
        ):
            return state

        data_json = state["sql_result"]
        if isinstance(data_json, str) and data_json.startswith("Not"):
            state["skip"] = True
            return state
        data_md = data_json["metadata"]
        data_rows = data_json.get("data") or state.get("data") or []
        sample_row = data_rows[0] if data_rows else {}
        sql = state.get("sql") or ""

        try:
            chart_config = get_chart_config()
            chart_function = chart_config.get("other", "")
            domain_context = (
                state.get("sql_domain_context")
                or state.get("domain_context")
                or ""
            )
            viz_context = state.get("viz_column_context") or build_viz_column_context(
                data_md,
                cube_metadata=state.get("cube_metadata") or [],
                format_strings=state.get("format_strings") or {},
                ai_instructions=state.get("ai_instructions") or {},
                sort_orders=state.get("sort_orders") or [],
                domain_context=domain_context,
            )
            state["viz_column_context"] = viz_context

            fallback_reason = state.get("fallback_reason") or (
                "other_chart" if viz_hint == "other" else "functional_formatting"
            )
            logger.info(
                "Fallback DrawOther fill viz_hint=%s reason=%s result_fields=%s",
                viz_hint,
                fallback_reason,
                viz_context.get("field_names") or [],
            )

            parser = PydanticOutputParser(pydantic_object=OtherChartFillerResponse)
            prompt = PromptTemplate(
                template=other_fill_prompt_string + format_instruction_string,
                input_variables=[
                    "domain",
                    "topics",
                    "domain_context",
                    "sql",
                    "user_question",
                    "data_types",
                    "sample_row",
                    "chart_function",
                    "column_format_strings",
                    "column_ai_instructions",
                    "column_sort_orders",
                    "column_viz_context",
                    "fallback_reason",
                    "viz_hint",
                ],
                partial_variables={
                    "format_instructions": parser.get_format_instructions()
                },
            )
            response, _ = invoke_structured(
                prompt,
                llm,
                parser,
                {
                    "domain": state.get("domain") or [],
                    "topics": state.get("topics") or [],
                    "domain_context": domain_context,
                    "user_question": user_query,
                    "data_types": data_md,
                    "sample_row": json.dumps(sample_row, default=str),
                    "sql": sql,
                    "chart_function": chart_function,
                    "column_format_strings": viz_context.get("column_format_strings")
                    or "",
                    "column_ai_instructions": viz_context.get("column_ai_instructions")
                    or "",
                    "column_sort_orders": viz_context.get("column_sort_orders") or "",
                    "column_viz_context": viz_context.get("column_context") or "",
                    "fallback_reason": fallback_reason,
                    "viz_hint": viz_hint or "other",
                },
                state=state,
            )

            state["vf_string"] = transform_chart_code(response.code or "")
            state["chart_settings"] = None
            state["insight"] = state["vf_string"]
            state["use_other_fallback"] = False
            add_viz_response(state["thread_id"], response)
        except Exception:
            logger.exception("Fallback flow failed")
            state["output"] = traceback.format_exc()

        return state
