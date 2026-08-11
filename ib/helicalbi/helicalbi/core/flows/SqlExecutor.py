import json
import logging

from helicalbi.api.QueryExecutor import execute_query
from helicalbi.common import app_config
from helicalbi.common.ChatManager import add_insight
from helicalbi.common.LlmInvokeHelper import invoke_llm
from helicalbi.common.configuration import llm
from helicalbi.model.ModelState import ModelState
from helicalbi.prompt.ErrorPrompt import error_prompt_formatted
from helicalbi.prompt.SqlRepairPrompt import sql_repair_prompt
from helicalbi.prompt.SqlSuccessPrompty import success_prompt_formatted
from helicalbi.sql.SqlSanitizer import extract_sql

logger = logging.getLogger(__name__)


class SqlExecutor:

    def process_flow(self, state: ModelState, request_id=None):
        logger.info("SqlExecutor flow started")
        state["sql_result"] = "Not Generated"
        state["sql_error"] = "Not Generated"
        if state.get("skip"):
            return state
        intent = state.get("intent", "")
        if "EXEC" in intent:
            return state

        required_tables = state.get("required_tables", [])
        domain = state.get("domain", [])
        topics = state.get("topics", [])
        metadata_to_send = {
            "required_tables": required_tables,
            "domain": domain,
            "topics": topics,
        }

        user_query = state["query"]
        max_repair_attempts = app_config.sql_repair_max_attempts
        attempt = 0

        while True:
            sql = state.get("sql", "")

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
                return state

            status = api_response['status']
            response_string = api_response['response']

            if status == 1:
                if attempt:
                    logger.info(
                        "SQL succeeded after %d self-correction attempt(s)", attempt
                    )
                state["sql_error"] = "Not Generated"
                state["sql_result"] = response_string
                state["data"] = response_string["data"]
                state["metadata"] = response_string["metadata"]

                formatted_format = success_prompt_formatted.format(
                    user_query=user_query,
                    sql_query=sql,
                    metadata=json.dumps(metadata_to_send, default=str),
                )
                insight, _ = invoke_llm(
                    llm,
                    formatted_format,
                    state=state,
                )
                state["output"] = insight.content
                add_insight(state["thread_id"], insight.content)
                return state

            # status != 1 -> the database rejected the query.
            logger.error(
                "SQL execution failed status=%s response=%s",
                status,
                response_string,
            )
            state["sql_error"] = response_string

            if attempt < max_repair_attempts:
                self._abort_check(request_id)
                repaired_sql = self._repair_sql(state, sql, response_string)
                if (
                    repaired_sql
                    and repaired_sql.strip()
                    and repaired_sql.strip() != (sql or "").strip()
                ):
                    attempt += 1
                    logger.info(
                        "Attempting SQL self-correction %d/%d",
                        attempt,
                        max_repair_attempts,
                    )
                    state["sql"] = repaired_sql
                    continue
                logger.info(
                    "SQL self-correction produced no usable change; giving up "
                    "after %d attempt(s)",
                    attempt,
                )

            # No repair attempts left (or repair unavailable): surface a calm,
            # non-technical message to the user (unchanged behaviour).
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

    def _repair_sql(self, state: ModelState, failed_sql, db_error):
        """Ask the model to fix ``failed_sql`` given the database ``db_error``.

        Reuses the schema context already gathered during generation
        (``required_details``), so a repair is a single cheap LLM call with no
        re-retrieval. Returns the corrected SQL string, or ``None`` if the
        repair call itself failed (the caller then falls back to the error
        message path).
        """
        try:
            allowed_context = json.dumps(
                state.get("required_details", {}), default=str
            )
            prompt_text = sql_repair_prompt.format(
                dialect=state.get("dialect", ""),
                dbname=state.get("dbname", ""),
                user_query=state.get("query", ""),
                failed_sql=failed_sql or "",
                db_error=str(db_error),
                allowed_context=allowed_context,
            )
            ai_message, _ = invoke_llm(llm, prompt_text, state=state)
            return extract_sql(ai_message.content, state.get("dialect"))
        except Exception:
            logger.exception("SQL self-correction attempt failed to produce a query")
            return None

    @staticmethod
    def _abort_check(request_id):
        """Cooperative cancellation check between self-correction attempts.

        Imported lazily to avoid a module import cycle between the core flows
        and the request-facing ``bl`` package. An abort raised here is allowed
        to propagate so the loop stops immediately.
        """
        if not request_id:
            return
        from bl.helpers import ensure_not_aborted

        ensure_not_aborted(request_id)
