import logging
from typing import Any

from langgraph.graph import END, StateGraph

from helicalbi.common.CommonAppender import append_to_workflow
from helicalbi.common.LlmInvokeHelper import merge_time_consumed, merge_token_usage, read_time_consumed, read_token_usage
from helicalbi.model.AgentState import AgentState
from helicalbi.service.agentservice.AgentLayerHelper import AgentLayerHelper
from helicalbi.sql.SqlSanitizer import extract_sql

logger = logging.getLogger(__name__)


class SqlGenerator:
    """Orchestrates the inner SQL sub-graph and folds its output back into the
    outer ``AgentState``.

    The previous version short-circuited inline with ``if state.get("skip")``
    checks. That control flow is now expressed as a LangGraph conditional
    edge (see :func:`build_sql_generator_graph`) so the skip decision is a
    first-class branch in the graph rather than imperative glue in the caller.
    """

    def __init__(self, sql_graph=None):
        self._sql_graph = sql_graph

    def init_sql_state(self, state: AgentState) -> AgentState:
        """Entry node — always runs.

        Initializes the default ``sql`` placeholder so downstream consumers
        always see a value, regardless of which branch is taken next.
        """
        logger.info("SqlGenerator flow started")
        state["sql"] = "Not generated"
        return state

    def run_sql_subgraph(self, state: AgentState, config=None) -> AgentState:
        """Branch executed only when ``skip`` is falsy.

        Builds the sub-state expected by the inner SQL graph, invokes it,
        and folds the result back into the outer state.
        """
        if config is None and state.get("thread_id"):
            config = {"configurable": {"thread_id": str(state["thread_id"])}}
        sub_state = self._build_sql_state(state)
        sql_res = self._sql_graph.invoke(sub_state, config)
        return self._apply_sql_result(state, sql_res)

    @staticmethod
    def _build_sql_state(state: AgentState) -> dict[str, Any]:
        relationship_of_table = state["relationship_of_table"]
        last_chats = state["last_chats"]
        helper = AgentLayerHelper(state["session_cookie"], state["agent_file_name"], state["agent_location"])
        agent_data = helper.get_agent_semantic_layer() or {}
        cube_metadata = state.get("cube_metadata") or agent_data.get("cube_metadata") or []
        sub_state: dict[str, Any] = {
            "query": state["query"], "table_columns": [], "template_selected": "",
            "session_cookie": state["session_cookie"], "last_chats": last_chats,
            "messages": [],
            "user_name": state["user_name"],
            "cube_metadata": cube_metadata,
            "topic_mappings": agent_data.get("topic_mappings") or [],
            "relationship_of_table": relationship_of_table,
            "examples": agent_data.get("examples") or [],
            "synonyms": agent_data.get("synonyms") or [],
            "agent_file_name": state["agent_file_name"], "agent_location": state["agent_location"],
            "business_metrics": state.get("business_metrics") or agent_data.get("business_metrics") or [],
            "domain_context": state.get("domain_context") or "",
            "required_joins": "",
            "required_tables": "",
            "action": state["action"],
            "dialect": state["dialect"],
            "dbname": state["dbname"],
            "thread_id": state["thread_id"],
            "metadata_file_name": state["md_file_name"], "location": state["md_location"],
            "topics": state["topics"], "domain": state["domain"],
        }
        return sub_state

    @staticmethod
    def _apply_sql_result(state: AgentState, res: dict) -> AgentState:
        res["cube_metadata"] = []
        res["relationship_of_table"] = []
        res["topic_graph"] = {}
        logger.debug("SQL graph messages: %s", res.get("messages", []))
        res["messages"] = []
        res["last_chats"] = []
        state["sqlAgent"] = res
        merge_token_usage(state, read_token_usage(res))
        merge_time_consumed(state, read_time_consumed(res))
        state["sql"] = extract_sql(res["final_sql"], state["dialect"])
        state["sql_reason"] = res.get("sql_reason", "") or ""
        state["required_details"] = {
            "required_tables": res["required_tables"],
            "required_joins": res["required_joins"],
            "required_synonyms": res["required_synonyms"],
            "business_metrics": [],
            "required_business_metrics": res["required_business_metrics"],
            "required_cube_info": res.get("required_cube_info") or {},
            "filters": res.get("filters", ""),
            "messages": [],
        }

        append_to_workflow("Generating yourll sql.", state)

        return state


def route_on_skip(state: AgentState) -> str:
    """Conditional edge: route based on the ``skip`` flag in state."""
    return "skip" if state.get("skip") else "run"


def build_sql_generator_graph(sql_graph):
    """Wrap the SQL generator in a LangGraph whose ``skip`` decision is a
    first-class conditional branch.

    Graph shape::

                  init_sql_state
                        │
                  route_on_skip
                   /         \\
                "run"       "skip"
                 │             │
          run_sql_subgraph     │
                 │             │
                 └──► END ◄────┘
    """
    generator = SqlGenerator(sql_graph=sql_graph)

    workflow = StateGraph(AgentState)
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
