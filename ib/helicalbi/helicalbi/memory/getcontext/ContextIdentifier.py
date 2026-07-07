import json
import logging
import re

from langchain_core.prompts import PromptTemplate

logger = logging.getLogger(__name__)

from helicalbi.memory.getcontext.ContextClass import GraphState


def _parse_llm_json(content: str) -> dict:
    if not content:
        return {}

    text = content.strip()
    fence = re.search(r"```(?:json)?\s*([\s\S]*?)```", text)
    if fence:
        text = fence.group(1).strip()

    try:
        parsed = json.loads(text)
    except (json.JSONDecodeError, TypeError):
        logger.error("Failed to parse LLM JSON response", exc_info=True)
        return {}

    return parsed if isinstance(parsed, dict) else {}


def classify_intent_node(state: GraphState, llm):
    prompt = PromptTemplate(
        template="""
Classify the user query.

User Query:
{user_query}

Chat History:
{chat_history}

Output JSON:
{{
  "query_type": "NEW_QUERY | CONTINUATION",
  "continuation_type": "VISUALIZATION_UPDATE | SQL_UPDATE | GENERIC_FOLLOWUP | NONE"
}}
""",
        input_variables=["user_query", "chat_history"],
    )

    chain = prompt | llm
    response = chain.invoke({
        "user_query": state.user_query or "",
        "chat_history": state.chat_history or [],
    })

    result = _parse_llm_json(
        response.content if hasattr(response, "content") else str(response)
    )

    continuation_type = result.get("continuation_type")
    if continuation_type == "NONE":
        continuation_type = None

    return {
        "query_type": result.get("query_type"),
        "continuation_type": continuation_type,
    }
