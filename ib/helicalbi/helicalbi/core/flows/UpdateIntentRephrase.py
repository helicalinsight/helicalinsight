import logging

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.model.AgentState import AgentState
from helicalbi.model.output.UpdateRephrase import UpdateRephrase
from helicalbi.prompt.FormatInstruction import format_instruction_string
from helicalbi.prompt.UpdateRephrasePrompt import update_rephrase_prompt

logger = logging.getLogger(__name__)


class UpdateIntentRephrase:
    def process_flow(self, state: AgentState):
        if state.get("skip"):
            return state

        user_query = state.get("query", "") or ""
        chat_history = state["last_chats"]

        parser = PydanticOutputParser(pydantic_object=UpdateRephrase)
        prompt = PromptTemplate(
            template=update_rephrase_prompt + format_instruction_string,
            input_variables=["current_user_query", "chat_history"],
            partial_variables={"format_instructions": parser.get_format_instructions()},
        )
        logger.info("UpdateIntentRephrase started query_len=%s", len(user_query))
        response: UpdateRephrase
        response, _ = invoke_structured(
            prompt,
            llm,
            parser,
            {
                "current_user_query": user_query,
                "chat_history": chat_history,
            },
            state=state,
        )

        action = response.action

        state["action"] = action
        state["viz_query"] = response.viz_query
        state["sql_query"] = response.sql_query

        return state
