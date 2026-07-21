from langchain_core.prompts import PromptTemplate

from helicalbi.common.LlmInvokeHelper import log_prompt
from helicalbi.memory.getcontext.ContextClass import GraphState
from helicalbi.memory.getcontext.ContextHelper import format_chat_item_field


def rewrite_query_node(state: GraphState, llm):
    prompt = PromptTemplate(
        template="""
Rewrite the query into a self-contained query.

User Query:
{user_query}

Continuation Type:
{continuation_type}

Context:
- Previous Question: {question}
- SQL: {sql}
- Visualization: {visualization}

Return only the final rewritten query.
""",
        input_variables=[
            "user_query",
            "continuation_type",
            "question",
            "sql",
            "visualization",
        ],
    )

    context = state.resolved_context

    chain = prompt | llm
    invoke_inputs = {
        "user_query": state.user_query or "",
        "continuation_type": state.continuation_type or "",
        "question": format_chat_item_field(context, "question"),
        "sql": format_chat_item_field(context, "sql"),
        "visualization": format_chat_item_field(context, "visualization"),
    }
    log_prompt(prompt, invoke_inputs)
    response = chain.invoke(invoke_inputs)

    final_query = response.content if hasattr(response, "content") else str(response)
    return {"final_query": final_query or None}
