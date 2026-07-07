import json
import logging

from helicalbi.common.ChatManager import add_insight, get_last_insight
from helicalbi.common.LlmInvokeHelper import invoke_llm, merge_token_usage
from helicalbi.common.configuration import llm
from helicalbi.model.DataInsightState import DataInsightState
from helicalbi.prompt.DataInsightPrompt import data_insight_prompt_formatted
from helicalbi.prompt.ErrorPrompt import error_prompt_formatted

logger = logging.getLogger(__name__)

_MAX_SAMPLE_ROWS = 50


class GenerateDataInsight:

    def process_flow(self, state: DataInsightState) -> DataInsightState:
        logger.info("GenerateDataInsight flow started")

        thread_id = state.get("thread_id", "")
        prev_responses = get_last_insight(thread_id) if thread_id else []
        username = state.get("username", "")
        user_question = state.get("user_question", "")
        profile = state.get("profile") or {}
        user_profile = profile.get("userProfile") or []
        memory = state.get("memory") or {}
        selected_domain = memory.get("domain") or []
        selected_topics = memory.get("topics") or []

        if state.get("skip"):
            insight, usage = invoke_llm(
                llm,
                error_prompt_formatted.format(
                    response_string=state.get("sql_error", ""),
                    user_query=user_question,
                    username=username,
                ),
            )
        else:
            sample_data = state.get("data") or []
            if len(sample_data) > _MAX_SAMPLE_ROWS:
                sample_data = sample_data[:_MAX_SAMPLE_ROWS]

            insight, usage = invoke_llm(
                llm,
                data_insight_prompt_formatted.format(
                    username=username,
                    user_question=user_question,
                    sql=state.get("sql", ""),
                    userProfile=json.dumps(user_profile, default=str),
                    domain=json.dumps(selected_domain, default=str),
                    topics=json.dumps(selected_topics, default=str),
                    sample_data=json.dumps(sample_data, default=str),
                    prev_responses=prev_responses,
                ),
            )

        merge_token_usage(state, usage)
        state["insight"] = insight.content

        if thread_id:
            add_insight(thread_id, insight.content)

        return state
