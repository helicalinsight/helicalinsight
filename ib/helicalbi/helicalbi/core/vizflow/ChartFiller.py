import logging
import traceback

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.ChatManager import add_viz_response, get_last_n_viz
from helicalbi.common.CubeInfoModel import build_viz_column_context
from helicalbi.core.vizflow.util.ChartCodeTransform import transform_chart_code
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.model.ModelState import ModelState
from helicalbi.model.output.viz.VizResponse import (
    ChartFillerResponse,
    OtherChartFillerResponse,
)
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.VizFillPrompt import fill_prompt_string, other_fill_prompt_string
from helicalbi.viz._charts import (
    get_chart_config,
    get_chart_definition,
    is_other_chart,
)
from helicalbi.viz.chart_conversion import apply_chart_settings

logger = logging.getLogger(__name__)


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
        sql = state.get("sql") or ""

        try:
            viz_hint = (
                str(state["viz_hint"])
                .strip()
                .lower()
            )
            chart_config = get_chart_config()
            use_other = is_other_chart(viz_hint)
            chart_function = (
                chart_config.get("other")
                if use_other
                else chart_config.get(viz_hint, chart_config["other"])
            )
            chart_def = (
                get_chart_definition("other")
                if use_other
                else (get_chart_definition(viz_hint) or get_chart_definition("other"))
            )
            previous_viz = get_last_n_viz(state["thread_id"])

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
            # Prefer result-column-filtered hints; fall back to full-cube prompts.
            column_format_strings = (
                viz_context.get("column_format_strings")
                or state.get("column_format_strings")
                or ""
            )
            column_ai_instructions = (
                viz_context.get("column_ai_instructions")
                or state.get("column_ai_instructions")
                or ""
            )
            column_sort_orders = viz_context.get("column_sort_orders") or ""
            column_viz_context = viz_context.get("column_context") or ""
            state["viz_column_context"] = viz_context

            prompt_inputs = {
                "domain": state.get("domain") or [],
                "topics": state.get("topics") or [],
                "domain_context": domain_context,
                "sql": sql,
                "user_question": user_query,
                "previous_viz": previous_viz,
                "data_types": data_md,
                "chart_function": chart_function,
                "column_format_strings": column_format_strings,
                "column_ai_instructions": column_ai_instructions,
                "column_sort_orders": column_sort_orders,
                "column_viz_context": column_viz_context,
            }

            if use_other:
                logger.info(
                    "ChartFiller other-chart JS fill viz_hint=%s result_fields=%s "
                    "user_question_chars=%s",
                    viz_hint,
                    viz_context.get("field_names") or [],
                    len(user_query or ""),
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
                        "chart_function",
                        "previous_viz",
                        "column_format_strings",
                        "column_ai_instructions",
                        "column_sort_orders",
                        "column_viz_context",
                    ],
                    partial_variables={
                        "format_instructions": parser.get_format_instructions()
                    },
                )
                response, _ = invoke_structured(
                    prompt,
                    llm,
                    parser,
                    prompt_inputs,
                    state=state,
                )
                state["vf_string"] = transform_chart_code(response.code or "")
                state["chart_settings"] = None
                add_viz_response(state["thread_id"], response)
            else:
                logger.info(
                    "ChartFiller filling viz_hint=%s result_fields=%s "
                    "format_strings_chars=%s ai_instructions_chars=%s",
                    viz_hint,
                    viz_context.get("field_names") or [],
                    len(column_format_strings),
                    len(column_ai_instructions),
                )
                parser = PydanticOutputParser(pydantic_object=ChartFillerResponse)
                prompt = PromptTemplate(
                    template=fill_prompt_string + format_instruction_string,
                    input_variables=[
                        "domain",
                        "topics",
                        "domain_context",
                        "sql",
                        "user_question",
                        "data_types",
                        "chart_function",
                        "previous_viz",
                        "column_format_strings",
                        "column_ai_instructions",
                        "column_sort_orders",
                        "column_viz_context",
                    ],
                    partial_variables={
                        "format_instructions": parser.get_format_instructions()
                    },
                )
                response, _ = invoke_structured(
                    prompt,
                    llm,
                    parser,
                    prompt_inputs,
                    state=state,
                )

                filled = apply_chart_settings(
                    response.settings,
                    chart_def=chart_def,
                    format_strings=state.get("format_strings") or {},
                )
                state["vf_string"] = transform_chart_code(filled)
                state["chart_settings"] = response.settings
                add_viz_response(state["thread_id"], response)
        except Exception:
            logger.exception("ChartFiller flow failed")
            state["output"] = traceback.format_exc()

        return state
