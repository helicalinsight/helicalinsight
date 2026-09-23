import json
import logging

from helicalbi.api.QueryExecutor import execute_query
from helicalbi.common.ChatManager import add_insight, get_last_insight
from helicalbi.common.LlmInvokeHelper import invoke_llm
from helicalbi.common.configuration import llm
from helicalbi.model.ModelState import ModelState
from helicalbi.prompt.ErrorPrompt import error_prompt_formatted
from helicalbi.prompt.SqlSuccessPrompty import success_prompt_formatted
from helicalbi.sql.sql_retry import sql_execution_failed

logger = logging.getLogger(__name__)


class SqlExecutor:

    def write_insight(self, state: ModelState):
        """Write success or error insight after SQL execute (and any retries)."""
        user_query = state.get("query") or ""
        if sql_execution_failed(state):
            error_insight, _ = invoke_llm(
                llm,
                error_prompt_formatted.format(
                    response_string=state.get("sql_error") or "",
                    user_query=user_query,
                    username=state.get("user_name") or "",
                ),
                state=state,
            )
            state["output"] = error_insight.content
            add_insight(state["thread_id"], error_insight.content)
            return state
        sql_result = state.get("sql_result")
        if not isinstance(sql_result, dict) or "data" not in sql_result:
            return state
        sql = state.get("sql", "")
        metadata_to_send = {
            "required_tables": state.get("required_tables", []),
            "domain": state.get("domain", []),
            "topics": state.get("topics", []),
        }
        insight, _ = invoke_llm(
            llm,
            success_prompt_formatted.format(
                user_query=user_query,
                sql_query=sql,
                metadata=json.dumps(metadata_to_send, default=str),
            ),
            state=state,
        )
        state["output"] = insight.content
        add_insight(state["thread_id"], insight.content)
        return state

    def process_flow(self, state: ModelState):
        logger.info("SqlExecutor flow started")
        state["sql_result"] = "Not Generated"
        state["sql_error"] = "Not Generated"
        if state.get("skip"):
            return state
        intent = state.get("intent", "")
        if "EXEC" not in intent:
            sql = state.get("sql", "")
            defer_insight = bool(state.get("_defer_sql_insight"))

            try:
                api_response = execute_query(
                    session_cookie=state["session_cookie"],
                    md_location=state["md_location"],
                    md_file_name=state["md_file_name"],
                    sql=sql,
                    request_id="random-request-id",
                )
            except Exception:
                logger.exception("SqlExecutor executeQuery request failed")
                raise

            if not api_response:
                logger.error("SqlExecutor received empty executeQuery response")
                state["sql_error"] = "Empty response from executeQuery"
                state["skip"] = True
                state["metadata"] = []
                state["data"] = []
                if not defer_insight:
                    self.write_insight(state)
                return state

            status = api_response['status']
            response_string = api_response['response']
            get_last_insight(state["thread_id"])

            if status != 1:
                logger.error(
                    "SQL execution failed status=%s response=%s",
                    status,
                    response_string,
                )
                state["sql_error"] = response_string
                state["skip"] = True
                state["metadata"] = []
                state["data"] = []
                if not defer_insight:
                    self.write_insight(state)
                return state
            state["sql_result"] = response_string
            state["data"] = response_string["data"]
            state["metadata"] = response_string["metadata"]
            if not defer_insight:
                self.write_insight(state)

        return state
