import logging
import traceback

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.ChatManager import add_viz_response, get_last_n_viz
from helicalbi.core.vizflow.util.ChartCodeTransform import transform_chart_code
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.model.ModelState import ModelState
from helicalbi.model.output.viz.VizResponse import ChartFillerResponse
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.VizFillPrompt import fill_prompt_string
from helicalbi.viz._charts import get_chart_config

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
        sql = state["sql"]

        try:
            viz_hint = (
                str(state["viz_hint"])
                .strip()
                .lower()
            )
            chart_config = get_chart_config()
            chart_function = chart_config.get(viz_hint, chart_config["other"])
            previous_viz = get_last_n_viz(state["thread_id"])
            column_format_strings = state.get("column_format_strings") or ""
            logger.info(
                "ChartFiller filling viz_hint=%s format_strings_chars=%s",
                viz_hint,
                len(column_format_strings),
            )
            parser = PydanticOutputParser(pydantic_object=ChartFillerResponse)
            prompt = PromptTemplate(
                template=fill_prompt_string + format_instruction_string,
                input_variables=["domain", "topics", "user_question",
                                 "data_types", "chart_function", "previous_viz",
                                 "column_format_strings"],
                partial_variables={"format_instructions": parser.get_format_instructions()},
            )
            response, _ = invoke_structured(
                prompt,
                llm,
                parser,
                {
                    "domain": state["domain"],
                    "topics": state["topics"],
                    "user_question": user_query,
                    "previous_viz": previous_viz,
                    "data_types": data_md,
                    "chart_function": chart_function,
                    "column_format_strings": column_format_strings,
                },
                state=state,
            )

            state["vf_string"] = transform_chart_code(response.js_func_string)
            add_viz_response(state["thread_id"], response)
        except Exception:
            logger.exception("ChartFiller flow failed")
            state["output"] = traceback.format_exc()

        return state
