import logging

from helicalbi.api.QueryExecutor import execute_query
from helicalbi.common.ChatManager import add_insight, get_last_insight
from helicalbi.common.LlmInvokeHelper import invoke_llm
from helicalbi.common.configuration import llm
from helicalbi.model.AgentState import AgentState
from helicalbi.prompt.ErrorPrompt import error_prompt_formatted
from helicalbi.prompt.SqlSuccessPrompty import success_prompt_formatted, success_response_method

logger = logging.getLogger(__name__)


class SqlExecutor:


    def process_flow(self, state: AgentState):
        logger.info("SqlExecutor flow started")
        state["sql_result"] = "Not Generated"
        state["sql_error"] = "Not Generated"
        if state.get("skip"):
            return state
        intent = state.get("intent", "")
        if "EXEC" not in intent:
            sql = state.get("sql", "")
            required_tables = state.get("required_tables",[])
            domain = state.get("domain",[])
            topics = state.get("topics",[])
            metadata_to_send={}
            metadata_to_send["required_tables"]=required_tables
            metadata_to_send["domain"]=domain
            metadata_to_send["topics"]=topics

            api_response = execute_query(
                session_cookie=state["session_cookie"],
                md_location=state["md_location"],
                md_file_name=state["md_file_name"],
                sql=sql,
                request_id="random-request-id",
            )
            status = api_response['status']
            response_string = api_response['response']
            user_query = state["query"]

            prev_responses = get_last_insight(state["thread_id"])

            if status != 1:
                state["sql_error"] = response_string
                error_insight, _ = invoke_llm(
                    llm,
                    error_prompt_formatted.format(
                        response_string=response_string,
                        user_query=user_query,
                        username=state["user_name"],
                    ),
                    state=state,
                )
                state["output"] = error_insight.content
                add_insight(state["thread_id"], error_insight.content)
                state["skip"] = True
                state["metadata"] = []
                state["data"] = []
                return state
                # raise Exception("Something went wrong")
            # Here you'd call a real DB executor,
            # but for now just simulate
            state["sql_result"] = response_string
            state["data"] = response_string["data"]
            state["metadata"] = response_string["metadata"]

            formatted_format = success_prompt_formatted.format(user_query=user_query, sql_query=sql,
                                                               metadata=metadata_to_send, )
            insight, _ = invoke_llm(
                llm,
                formatted_format,
                state=state,
            )
            state["output"] = insight.content
            add_insight(state["thread_id"], insight.content)

        return state
