import logging
import traceback

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.ChatManager import get_last_n_viz, add_viz_response
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.model.ModelState import ModelState
from helicalbi.model.output.viz.VizResponse import get_ant_visualization_response_model
from helicalbi.prompt.AntdVizPrompt import antd_viz_prompt
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.viz.ChatDefinition import format_chart_selection_guide

logger = logging.getLogger(__name__)


class AntdVisualization:
    def process_flow(self, state: ModelState):
        logger.info("AntdVisualization flow started")
        if state.get("skip"):
            return state

        user_query = state.get("query", "")

        sql = state["sql"]
        data_json = state["sql_result"]
        if isinstance(data_json, str) and data_json.startswith("Not"):
            state["skip"] = True
            return state
        data_md = data_json["metadata"]
        try:
            previous_viz = get_last_n_viz(state["thread_id"])
            logger.info(
                "AntdVisualization chart_selection start metadata=%s",
                data_md,
            )
            chart_selection_guide = format_chart_selection_guide(data_types=data_md)
            logger.info(
                "AntdVisualization chart_selection guide ready chars=%s",
                len(chart_selection_guide),
            )

            parser = PydanticOutputParser(pydantic_object=get_ant_visualization_response_model())
            prompt = PromptTemplate(
                template=antd_viz_prompt + format_instruction_string,
                input_variables=["domain", "topics", "user_question", "sql",
                                 "data_types", "chart_selection_guide",
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
                    "sql": sql,
                    "data_types": data_md,
                    "chart_selection_guide": chart_selection_guide,
                    "previous_viz": previous_viz,
                    "chat_history": [],
                    "column_format_strings": state.get("column_format_strings") or "",
                },
                state=state,
            )


            state["visualization"] = response.visualization_type
            state["vf_title"] = response.visualization_title
            state["viz_hint"] = response.visualization_type
            state["viz_reason"] = getattr(response, "reason", "") or ""
            logger.info(
                "AntdVisualization chart_selection chose type=%s title=%s",
                response.visualization_type,
                response.visualization_title,
            )

            add_viz_response(state["thread_id"], response)
        except Exception:
            logger.exception("AntdVisualization flow failed")
            state["output"] = traceback.format_exc()

        return state
