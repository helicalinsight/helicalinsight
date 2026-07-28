import logging
import traceback

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.ChatManager import get_last_n_viz, add_viz_response
from helicalbi.common.CubeInfoModel import build_viz_column_context
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.model.ModelState import ModelState
from helicalbi.model.output.viz.VizResponse import get_ant_visualization_response_model
from helicalbi.prompt.AntdVizPrompt import antd_viz_prompt
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.viz.ChatDefinition import format_chart_selection_guide
from helicalbi.viz._chart_selection import resolve_similar_charts

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
            column_viz_context = viz_context.get("column_context") or ""

            parser = PydanticOutputParser(pydantic_object=get_ant_visualization_response_model())
            prompt = PromptTemplate(
                template=antd_viz_prompt + format_instruction_string,
                input_variables=["domain", "topics", "domain_context", "user_question", "sql",
                                 "data_types", "chart_selection_guide",
                                 "column_format_strings", "column_ai_instructions",
                                 "column_viz_context"],
                partial_variables={"format_instructions": parser.get_format_instructions()},
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
                    "sql": sql,
                    "data_types": data_md,
                    "chart_selection_guide": chart_selection_guide,
                    "previous_viz": previous_viz,
                    "chat_history": [],
                    "column_format_strings": column_format_strings,
                    "column_ai_instructions": column_ai_instructions,
                    "column_viz_context": column_viz_context,
                },
                state=state,
            )

            viz_type = response.visualization_type
            state["visualization"] = viz_type
            state["vf_title"] = response.visualization_title
            state["viz_hint"] = viz_type
            state["viz_reason"] = getattr(response, "reason", "") or ""
            state["similar_chart"] = resolve_similar_charts(viz_type, data_types=data_md)
            logger.info(
                "AntdVisualization chart_selection chose type=%s title=%s "
                "similar_chart=%s",
                viz_type,
                response.visualization_title,
                state["similar_chart"],
            )

            add_viz_response(state["thread_id"], response)
        except Exception:
            logger.exception("AntdVisualization flow failed")
            state["output"] = traceback.format_exc()

        return state
