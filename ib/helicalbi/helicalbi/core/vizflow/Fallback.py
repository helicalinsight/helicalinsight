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

logger = logging.getLogger(__name__)


class Fallback:
    def process_flow(self, state: ModelState):
        logger.info("Fallback flow started")
        if state.get("skip"):
            return state

        if state.get("vf_string") != "other":
            return state
        user_query = state.get("query", "")

        data_json = state["sql_result"]
        if isinstance(data_json, str) and data_json.startswith("Not"):
            state["skip"] = True
            return state
        data_md = data_json["metadata"]
        sql = state["sql"]

        try:
            viz_hint = state["viz_hint"]
            previous_viz = get_last_n_viz(state["thread_id"])

            parser = PydanticOutputParser(pydantic_object=ChartFillerResponse)
            prompt = PromptTemplate(
                template= format_instruction_string,
                input_variables=["user_question", "data_types", "viz_hint", "previous_viz"],
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
                    "viz_hint": viz_hint,
                },
                state=state,
            )

            state["vf_string"] = transform_chart_code(response.js_func_string)
            state["insight"] = response.js_func_string
            add_viz_response(state["thread_id"], response)
        except Exception:
            logger.exception("Fallback flow failed")
            state["output"] = traceback.format_exc()

        return state
