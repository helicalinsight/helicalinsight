import logging
import traceback

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.ChatManager import add_viz_response, get_last_n_viz
from helicalbi.core.vizflow.util.ChartCodeTransform import transform_chart_code
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.model.ModelState import ModelState
from helicalbi.model.output.viz.VizResponse import OtherChartFillerResponse
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.VizFillPrompt import other_fill_prompt_string
from helicalbi.viz._charts import get_chart_config

logger = logging.getLogger(__name__)


class Fallback:
    """Legacy node: regenerate ``other`` charts via full JS LLM fill (not settings)."""

    def process_flow(self, state: ModelState):
        logger.info("Fallback flow started")
        if state.get("skip"):
            return state

        viz_hint = str(state.get("viz_hint") or "").strip().lower()
        if viz_hint != "other" and state.get("vf_string") != "other":
            return state
        user_query = state.get("query", "")

        data_json = state["sql_result"]
        if isinstance(data_json, str) and data_json.startswith("Not"):
            state["skip"] = True
            return state
        data_md = data_json["metadata"]
        sql = state.get("sql") or ""

        try:
            previous_viz = get_last_n_viz(state["thread_id"])
            chart_config = get_chart_config()
            chart_function = chart_config.get("other", "")

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
                {
                    "domain": state.get("domain") or [],
                    "topics": state.get("topics") or [],
                    "domain_context": state.get("domain_context") or "",
                    "user_question": user_query,
                    "previous_viz": previous_viz,
                    "data_types": data_md,
                    "sql": sql,
                    "chart_function": chart_function,
                    "column_format_strings": state.get("column_format_strings") or "",
                    "column_ai_instructions": state.get("column_ai_instructions") or "",
                    "column_sort_orders": "",
                    "column_viz_context": "",
                },
                state=state,
            )

            state["vf_string"] = transform_chart_code(response.code or "")
            state["chart_settings"] = None
            state["insight"] = state["vf_string"]
            add_viz_response(state["thread_id"], response)
        except Exception:
            logger.exception("Fallback flow failed")
            state["output"] = traceback.format_exc()

        return state
