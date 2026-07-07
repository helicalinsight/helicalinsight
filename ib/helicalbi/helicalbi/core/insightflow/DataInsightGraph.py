from langgraph.graph import END, StateGraph

from helicalbi.core.insightflow.ExecuteSqlForInsight import ExecuteSqlForInsight
from helicalbi.core.insightflow.GenerateDataInsight import GenerateDataInsight
from helicalbi.model.DataInsightState import DataInsightState


def build_data_insight_graph():
    workflow = StateGraph(DataInsightState)

    workflow.add_node("ExecuteSqlForInsight", ExecuteSqlForInsight().process_flow)
    workflow.add_node("GenerateDataInsight", GenerateDataInsight().process_flow)

    workflow.set_entry_point("ExecuteSqlForInsight")
    workflow.add_edge("ExecuteSqlForInsight", "GenerateDataInsight")
    workflow.add_edge("GenerateDataInsight", END)

    return workflow.compile()
