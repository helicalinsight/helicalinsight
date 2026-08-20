from helicalbi.memory.getcontext.ContextClass import GraphState


def route_after_classification(state: GraphState):
    if state.query_type in (None, "NEW_QUERY"):
        return "rewrite_query"  # no context needed
    return "resolve_context"