import json
import logging
from typing import Any, Optional, Set

from langchain_core.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate

from helicalbi.common.ChatManager import get_last_sql, add_sql
from helicalbi.common import app_config
from helicalbi.common.JsonToPara import split_table_column_ref
from helicalbi.common.LlmInvokeHelper import invoke_structured
from helicalbi.common.configuration import llm
from helicalbi.core.sqlflow.util.FallbackSqlHelpers import (
    filter_previous_sql_for_context,
    tables_from_query_plan,
)
from helicalbi.model.SQLModel import SQLModel
from helicalbi.model.output.SqlGen import get_sql_gen_model
from helicalbi.prompt.FinalSqlPrompt import format_metrics_for_prompt, render_final_sql_prompt
from helicalbi.prompt.FormatInstruction import (
    brief_format_instructions,
    format_instruction_string,
)
from helicalbi.autocorrection import apply_sql_correction
from helicalbi.sql.SelectAliasRewriter import rewrite_select_aliases
from helicalbi.sql.exclusion_predicate import prefer_not_in
from helicalbi.sql_agent.nodes.validator import sql_disallowed_names

logger = logging.getLogger(__name__)


def _parse_query_plan(query_plan_json: Any) -> dict:
    plan = query_plan_json
    if isinstance(plan, str) and plan.strip():
        try:
            plan = json.loads(plan)
        except json.JSONDecodeError:
            logger.error(
                "Invalid query_plan JSON in FinalSqlGen; using empty plan",
                exc_info=True,
            )
            return {}
    if not isinstance(plan, dict):
        return {}
    return plan


def _tables_from_query_plan_payload(query_plan_json: Any) -> Set[str]:
    return set(tables_from_query_plan(_parse_query_plan(query_plan_json)))


def _attach_relative_dates(state: SQLModel, sql: str) -> None:
    """Read relative-date tokens from the final SQL. A failure leaves an empty list."""
    from helicalbi.core.sqlflow.RelativeDateDetect import detect_relative_dates

    try:
        detected = detect_relative_dates(
            sql,
            state.get("parameter_functions"),
            state=state,
        )
        state["relative_dates"] = detected.model_dump()
    except Exception:
        logger.exception("Relative date detection failed")
        state["relative_dates"] = {"filters": []}


def _columns_from_query_plan(query_plan_json: Any) -> Set[str]:
    """Column names listed on the query plan, without their table qualifier."""
    columns: Set[str] = set()
    plan = _parse_query_plan(query_plan_json)
    refs: list = []
    for key in ("columnName", "selectColumnName"):
        values = plan.get(key) or []
        if isinstance(values, list):
            refs.extend(values)
    for raw_ref in refs:
        _table_name, column_name = split_table_column_ref(str(raw_ref))
        if column_name:
            columns.add(column_name)
    return columns


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
    def process_flow(self, state: SQLModel):
        logger.info("FinalSqlGen flow started")
        user_question = (state.get("sql_retry_context") or "").strip() or state["query"]
        query_plan_json = state["query_plan"]
        required_metrics = state.get("required_business_metrics") or []
        required_column_description = state.get("required_column_description") or ""
        required_functions = state.get("required_functions") or ""
        domain_context = (
            state.get("sql_domain_context")
            or state.get("domain_context")
            or ""
        )
        column_sort_orders = state.get("column_sort_orders") or ""
        required_metrics_text = format_metrics_for_prompt(required_metrics)
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
        sql_limit = plan_limit if plan_limit is not None else app_config.default_sql_limit
        if plan_limit is not None:
            logger.debug("Using query-plan limit %s for final SQL", sql_limit)
        else:
            logger.debug("Using default SQL limit %s for final SQL", sql_limit)

        parser = PydanticOutputParser(pydantic_object=get_sql_gen_model())
        prompt = PromptTemplate(
            template=render_final_sql_prompt(user_question) + format_instruction_string,
            input_variables=[
                "dialect",
                "last_chats",
                "user_question",
                "query_plan_json",
                "required_joins",
                "required_metrics",
                "required_column_description",
                "required_functions",
                "column_sort_orders",
                "domain_context",
                "prev_sql",
            ],
            partial_variables={
                "format_instructions": brief_format_instructions(parser.pydantic_object),
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
                        _attach_relative_dates(state, pr_sql)
                        state["sql_reason"] = (
                            previous_sql_obj.get("reason")
                            if isinstance(previous_sql_obj, dict)
                            else getattr(previous_sql_obj, "reason", "")
                        ) or ""
                        add_sql(state["thread_id"], previous_sql_obj)
                        return state

        allowed_columns = _columns_from_query_plan(query_plan_json)
        invoke_inputs = {
            "dialect": dialect,
            "last_chats": last_chats,
            "user_question": user_question,
            "query_plan_json": query_plan_json,
            "prev_sql": prev_sql,
            "required_joins": required_joins,
            "required_metrics": required_metrics_text,
            "required_column_description": required_column_description,
            "required_functions": required_functions,
            "column_sort_orders": column_sort_orders,
            "domain_context": domain_context,
        }

        def _generate(question: str):
            invoke_inputs["user_question"] = question
            generated, _usage = invoke_structured(
                prompt,
                llm,
                parser,
                invoke_inputs,
                state=state,
            )
            sql_text = rewrite_select_aliases(
                generated.sql,
                cube_metadata=state.get("cube_metadata"),
                dialect=dialect,
            )
            sql_text = prefer_not_in(
                sql_text,
                question=user_question,
                dialect=dialect,
            )
            return generated, sql_text

        response, rewritten_sql = _generate(user_question)
        name_problem = sql_disallowed_names(
            rewritten_sql,
            allowed_tables,
            allowed_columns,
            dialect,
        )
        if name_problem:
            logger.warning("Final SQL rejected before execution: %s", name_problem)
            response, rewritten_sql = _generate(
                f"{user_question}\n\nThe previous SQL was rejected: {name_problem} "
                "Generate the query again. In FROM and JOIN write the original "
                "table name and an alias, and qualify columns with that alias. "
                "Copy table and column names only from the provided columns."
            )
            name_problem = sql_disallowed_names(
                rewritten_sql,
                allowed_tables,
                allowed_columns,
                dialect,
            )
            if name_problem:
                logger.error(
                    "Final SQL still uses names outside the provided columns: %s",
                    name_problem,
                )
        rewritten_sql, response = apply_sql_correction(
            rewritten_sql,
            response,
            state=state,
            question=user_question,
            dialect=dialect,
            parser=parser,
        )
        state["final_sql"] = rewritten_sql
        _attach_relative_dates(state, rewritten_sql)
        state["sql_reason"] = getattr(response, "reason", "") or ""
        # Persist the rewritten SQL so chat history / viz reuse the same aliases.
        if rewritten_sql != response.sql:
            try:
                response.sql = rewritten_sql
            except Exception:
                logger.debug("Could not mutate structured SQL response alias text", exc_info=True)
        add_sql(state["thread_id"], response)
        return state
