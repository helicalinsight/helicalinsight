from langgraph.graph import StateGraph, END

from helicalbi.core.flows.FindDomainAndTopics import FindDomainAndTopics
from helicalbi.core.flows.UpdateIntentRephrase import UpdateIntentRephrase
from helicalbi.model.ModelState import ModelState


def build_main_graph():
    workflow = StateGraph(ModelState)

    workflow.add_node("UpdateIntentRephrase", UpdateIntentRephrase().process_flow)
    workflow.add_node("FindDomainAndTopics", FindDomainAndTopics().process_flow)

    workflow.set_entry_point("UpdateIntentRephrase")
    workflow.add_edge("UpdateIntentRephrase", "FindDomainAndTopics")
    workflow.add_edge("FindDomainAndTopics", END)

    return workflow.compile()
