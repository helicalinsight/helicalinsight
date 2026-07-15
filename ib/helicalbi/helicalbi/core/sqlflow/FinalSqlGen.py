import json
import logging
from typing import Any, Optional, Set

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.ChatManager import get_last_sql, add_sql
from helicalbi.common.app_config import default_sql_limit
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.core.sqlflow.util.FallbackSqlHelpers import (
    filter_previous_sql_for_context,
    tables_from_query_plan,
)
from helicalbi.model.SQLAgent import SQLAgent
from helicalbi.model.output.SqlGen import get_sql_gen_model
from helicalbi.prompt.FinalSqlPrompt import final_sql_prompt
from helicalbi.prompt.FormatInstruction import format_instruction_string

logger = logging.getLogger(__name__)


def _parse_query_plan(query_plan_json: Any) -> dict:
    plan = query_plan_json
    if isinstance(plan, str) and plan.strip():
        try:
            plan = json.loads(plan)
        except json.JSONDecodeError:
            return {}
    if not isinstance(plan, dict):
        return {}
    return plan


def _tables_from_query_plan_payload(query_plan_json: Any) -> Set[str]:
    return set(tables_from_query_plan(_parse_query_plan(query_plan_json)))


def _limit_from_query_plan(query_plan_json: Any) -> Optional[int]:
    """Return a positive row limit from the query plan, or None if absent/invalid."""
    limit = _parse_query_plan(query_plan_json).get("limit")
    if limit is None or limit == "":
        return None
    try:
        value = int(limit)
    except (TypeError, ValueError):
        return None
    return value if value > 0 else None


class FinalSqlGen:
    def process_flow(self, state: SQLAgent):
        logger.info("FinalSqlGen flow started")
        user_question = state["query"]
        query_plan_json = state["query_plan"]
        required_metrics = state.get("required_business_metrics") or []
        required_column_description = state.get("required_column_description") or ""
        required_functions = state.get("required_functions") or ""
        domain_context = state.get("domain_context") or ""
        required_metrics_text = (
            json.dumps(required_metrics, indent=2, default=str)
            if required_metrics
            else ""
        )
        last_chats = state["last_chats"]
        required_joins = state["required_joins"]
        dialect = state["dialect"]
        thread_id = state["thread_id"]
        prev_sql = get_last_sql(thread_id)
        allowed_tables = set(str(t) for t in (state.get("required_tables") or []) if t)
        allowed_tables |= _tables_from_query_plan_payload(query_plan_json)
        prev_sql = filter_previous_sql_for_context(prev_sql, allowed_tables)
        logger.debug("Previous SQL for thread %s: %s", thread_id, prev_sql)

        plan_limit = _limit_from_query_plan(query_plan_json)
        sql_limit = plan_limit if plan_limit is not None else default_sql_limit
        if plan_limit is not None:
            logger.debug("Using query-plan limit %s for final SQL", sql_limit)
        else:
            logger.debug("Using default SQL limit %s for final SQL", sql_limit)

        parser = PydanticOutputParser(pydantic_object=get_sql_gen_model())
        prompt = PromptTemplate(
            template=final_sql_prompt + format_instruction_string,
            input_variables=[
                "dialect",
                "last_chats",
                "user_question",
                "query_plan_json",
                "required_joins",
                "required_metrics",
                "required_column_description",
                "required_functions",
                "domain_context",
                "prev_sql",
            ],
            partial_variables={
                "format_instructions": parser.get_format_instructions(),
                "default_sql_limit": sql_limit,
            },
        )
        action = state["action"]
        if action == "updt_viz":
            if prev_sql:
                first_entry = prev_sql[0] if isinstance(prev_sql[0], dict) else {}
                previous_sql_obj = first_entry.get("previous_sql")
                if previous_sql_obj is not None:
                    pr_sql = (
                        previous_sql_obj.get("sql")
                        if isinstance(previous_sql_obj, dict)
                        else getattr(previous_sql_obj, "sql", None)
                    )
                    if pr_sql:
                        state["final_sql"] = pr_sql
                        state["sql_reason"] = (
                            previous_sql_obj.get("reason")
                            if isinstance(previous_sql_obj, dict)
                            else getattr(previous_sql_obj, "reason", "")
                        ) or ""
                        add_sql(state["thread_id"], previous_sql_obj)
                        return state

        response, _ = invoke_structured(
            prompt,
            llm,
            parser,
            {
                "dialect": dialect,
                "last_chats": last_chats,
                "user_question": user_question,
                "query_plan_json": query_plan_json,
                "prev_sql": prev_sql,
                "required_joins": required_joins,
                "required_metrics": required_metrics_text,
                "required_column_description": required_column_description,
                "required_functions": required_functions,
                "domain_context": domain_context,
            },
            state=state,
        )
        state["final_sql"] = response.sql
        state["sql_reason"] = getattr(response, "reason", "") or ""
        add_sql(state["thread_id"], response)
        return state
