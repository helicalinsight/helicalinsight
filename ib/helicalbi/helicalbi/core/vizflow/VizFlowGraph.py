from langgraph.graph import StateGraph, END

from helicalbi.core.vizflow.AntdVisualization import AntdVisualization
from helicalbi.core.vizflow.ChartFiller import ChartFiller
from helicalbi.core.vizflow.Fallback import Fallback
from helicalbi.core.vizflow.Visualization import Visualization
from helicalbi.model.AgentState import AgentState


def build_viz_graph():
    workflow = StateGraph(AgentState)

    workflow.add_node("Visualization", Visualization().process_flow)
    workflow.add_node("ChartFiller", ChartFiller().process_flow)
    workflow.add_node("AntdVisualization", AntdVisualization().process_flow)
    workflow.add_node("Fallback", Fallback().process_flow)

    workflow.set_entry_point("Visualization")
    workflow.add_edge("Visualization", "ChartFiller")
    workflow.add_edge("ChartFiller", "AntdVisualization")
    workflow.add_edge("AntdVisualization", "Fallback")
    workflow.add_edge("Fallback", END)

    return workflow.compile()
