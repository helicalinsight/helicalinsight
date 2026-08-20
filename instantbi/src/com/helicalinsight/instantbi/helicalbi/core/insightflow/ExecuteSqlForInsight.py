import logging

from helicalbi.api.QueryExecutor import execute_query
from helicalbi.model.DataInsightState import DataInsightState

logger = logging.getLogger(__name__)


class ExecuteSqlForInsight:

    def process_flow(self, state: DataInsightState) -> DataInsightState:
        logger.info("ExecuteSqlForInsight flow started")
        state["skip"] = False
        state["sql_error"] = ""

        api_response = execute_query(
            session_cookie=state["session_cookie"],
            md_location=state["md_location"],
            md_file_name=state["md_file_name"],
            sql=state.get("sql", ""),
        )
        status = api_response["status"]
        response_string = api_response["response"]

        if status != 1:
            state["sql_error"] = response_string
            state["skip"] = True
            state["data"] = []
            state["metadata"] = []
            return state

        state["sql_result"] = response_string
        state["data"] = response_string.get("data", [])
        state["metadata"] = response_string.get("metadata", [])
        return state
