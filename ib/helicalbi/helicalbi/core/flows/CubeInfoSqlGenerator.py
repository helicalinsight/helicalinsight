"""SQL generator wrapper that routes cube_info models through a dedicated graph."""

from __future__ import annotations

import logging
from typing import Any

from langgraph.graph import END, StateGraph

from helicalbi.common.CommonAppender import append_to_workflow
from helicalbi.common.LlmInvokeHelper import merge_time_consumed, merge_token_usage, read_time_consumed, read_token_usage
from helicalbi.model.ModelState import ModelState
from helicalbi.sql.SqlSanitizer import extract_sql

logger = logging.getLogger(__name__)


class CubeInfoSqlGenerator:
    """Orchestrates the cube_info SQL sub-graph with domain/topic/column context."""

    def __init__(self, sql_graph=None):
        self._sql_graph = sql_graph

    def init_sql_state(self, state: ModelState) -> ModelState:
        logger.info("CubeInfoSqlGenerator flow started")
        state["sql"] = "Not generated"
        return state

    def run_sql_subgraph(self, state: ModelState, config=None) -> ModelState:
        if config is None and state.get("thread_id"):
            config = {"configurable": {"thread_id": str(state["thread_id"])}}
        sub_state = self._build_sql_state(state)
        sql_res = self._sql_graph.invoke(sub_state, config)
        return self._apply_sql_result(state, sql_res)

    @staticmethod
    def _build_sql_state(state: ModelState) -> dict[str, Any]:
        """Build SQLModel state with domain, metrics, aliases, dialect, and chat history."""
        return {
            "query": state["query"],
            "table_columns": [],
            "template_selected": "",
            "session_cookie": state["session_cookie"],
            "last_chats": state.get("last_chats") or [],
            "messages": [],
            "user_name": state["user_name"],
            "cube_metadata": state.get("cube_metadata") or [],
            "topic_mappings": state.get("topic_mappings") or [],
            "relationship_of_table": state["relationship_of_table"],
            "examples": [],
            "synonyms": state.get("synonyms") or [],
            "model_file_name": state["model_file_name"],
            "model_location": state["model_location"],
            "business_metrics": state.get("business_metrics") or [],
            "domain_context": state.get("domain_context") or "",
            "column_sort_orders": state.get("column_sort_orders") or "",
            "sort_orders": state.get("sort_orders") or [],
            "format_strings": state.get("format_strings") or {},
            "column_format_strings": state.get("column_format_strings") or "",
            "required_joins": "",
            "required_tables": "",
            "action": state.get("action") or "none",
            "dialect": state["dialect"],
            "dbname": state["dbname"],
            "thread_id": state["thread_id"],
            "metadata_file_name": state["md_file_name"],
            "location": state["md_location"],
            "topics": state.get("topics") or [],
            "domain": state.get("domain") or [],
        }

    @staticmethod
    def _apply_sql_result(state: ModelState, res: dict) -> ModelState:
        res["cube_metadata"] = []
        res["relationship_of_table"] = []
        res["topic_graph"] = {}
        logger.debug("CubeInfo SQL graph messages: %s", res.get("messages", []))
        res["messages"] = []
        res["last_chats"] = []
        state["sqlModel"] = res
        merge_token_usage(state, read_token_usage(res))
        merge_time_consumed(state, read_time_consumed(res))
        state["sql"] = extract_sql(res["final_sql"], state["dialect"])
        state["sql_reason"] = res.get("sql_reason", "") or ""
        state["required_details"] = {
            "required_tables": res.get("required_tables"),
            "required_joins": res.get("required_joins"),
            "required_synonyms": res.get("required_synonyms"),
            "business_metrics": [],
            "required_business_metrics": res.get("required_business_metrics"),
            "required_column_description": res.get("required_column_description"),
            "required_functions": res.get("required_functions"),
            "required_cube_info": res.get("required_cube_info") or {},
            "column_sort_orders": res.get("column_sort_orders") or state.get("column_sort_orders") or "",
            "filters": res.get("filters", ""),
            "messages": [],
        }
        append_to_workflow("Generating your sql.", state)
        return state


def route_on_skip(state: ModelState) -> str:
    return "skip" if state.get("skip") else "run"


def build_cube_info_sql_generator_graph(sql_graph):
    """Wrap the cube_info SQL graph with the same skip-routing as the legacy generator."""
    generator = CubeInfoSqlGenerator(sql_graph=sql_graph)

    workflow = StateGraph(ModelState)
    workflow.add_node("init_sql_state", generator.init_sql_state)
    workflow.add_node("run_sql_subgraph", generator.run_sql_subgraph)

    workflow.set_entry_point("init_sql_state")
    workflow.add_conditional_edges(
        "init_sql_state",
        route_on_skip,
        {
            "run": "run_sql_subgraph",
            "skip": END,
        },
    )
    workflow.add_edge("run_sql_subgraph", END)
    return workflow.compile()
