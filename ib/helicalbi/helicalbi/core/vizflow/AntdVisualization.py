import logging
import traceback

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.ChatManager import get_last_n_viz, add_viz_response
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.model.AgentState import AgentState
from helicalbi.model.output.viz.VizResponse import get_ant_visualization_response_model
from helicalbi.prompt.AntdVizPrompt import antd_viz_prompt
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.VizPrompt import viz_prompt

logger = logging.getLogger(__name__)


class AntdVisualization:
    def process_flow(self, state: AgentState):
        logger.info("AntdVisualization flow started")
        if state.get("skip"):
            return state
        if state["vf_string"] != "other":
            return state

        user_query = state.get("query", "")

        sql = state["sql"]
        data_json = state["sql_result"]
        if isinstance(data_json, str) and data_json.startswith("Not"):
            state["skip"] = True
            return state
        data = data_json["data"]
        data_md = data_json["metadata"]
        data = data[:10]

        content = "Not generated"
        try:
            previous_viz = get_last_n_viz(state["thread_id"])

            parser = PydanticOutputParser(pydantic_object=get_ant_visualization_response_model())
            prompt = PromptTemplate(
                template=antd_viz_prompt + format_instruction_string,
                input_variables=["domain", "topics", "user_question", "sql",
                                 "data_types"],
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
                    "sql": sql,
                    "data_types": data_md,
                    "previous_viz": previous_viz,
                    "chat_history": [],
                },
                state=state,
            )


            state["visualization"] = response.visualization_type
            state["vf_title"] = response.visualization_title
            state["viz_hint"] = response.visualization_type
            state["viz_reason"] = response.reason

            add_viz_response(state["thread_id"], response)
        except Exception:
            logger.exception("AntdVisualization flow failed")
            state["output"] = traceback.format_exc()

        return state
