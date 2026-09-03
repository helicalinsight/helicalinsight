"""Schema RAG tools."""
from __future__ import annotations

from typing import Annotated, Any

from langchain_core.tools import tool
from langgraph.prebuilt import InjectedState

from helicalbi.sql_agent.config import DEFAULT_SCHEMA_TOP_K
from helicalbi.sql_agent.database.schema_indexer import get_indexer
from helicalbi.sql_agent.tools.context import AgentToolContext


class SchemaTools:
    """Retrieve relevant tables and columns for a natural-language question."""

    def retrieve(self, question: str, state: dict[str, Any]) -> dict[str, Any]:
        ctx = AgentToolContext(state)
        indexer = get_indexer(ctx.catalog_id)
        top_k = int(state.get("schema_top_k") or DEFAULT_SCHEMA_TOP_K)
        subset = indexer.retrieve_schema(question, top_k=top_k)
        return {
            "ok": True,
            "schema": subset or "(no matching tables)",
            "state_patch": {
                "current_schema_subset": subset,
                "current_sub_question": question,
            },
        }


schema_tools = SchemaTools()


@tool
def retrieve_schema(
    question: str,
    state: Annotated[dict, InjectedState],
) -> str:
    """Retrieve the most relevant tables and columns for a natural-language question."""
    return AgentToolContext.dump(schema_tools.retrieve(question, state))
