from langgraph.graph import StateGraph, END

from helicalbi.memory.getcontext.ContextClass import GraphState
from helicalbi.memory.getcontext.ContextHelper import resolve_context_node
from helicalbi.memory.getcontext.ContextIdentifier import classify_intent_node
from helicalbi.memory.getcontext.RephraseQuestion import rewrite_query_node
from helicalbi.memory.getcontext.Route import route_after_classification

builder = StateGraph(GraphState)

builder.add_node("classify", classify_intent_node)
builder.add_node("resolve_context", resolve_context_node)
builder.add_node("rewrite_query", rewrite_query_node)

builder.set_entry_point("classify")

builder.add_conditional_edges(
    "classify",
    route_after_classification,
    {
        "resolve_context": "resolve_context",
        "rewrite_query": "rewrite_query"
    }
)

builder.add_edge("resolve_context", "rewrite_query")
builder.add_edge("rewrite_query", END)

context_graph = builder.compile()