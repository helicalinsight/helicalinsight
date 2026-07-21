from langgraph.graph import StateGraph, END

from helicalbi.core.vizflow.AntdVisualization import AntdVisualization
from helicalbi.core.vizflow.ChartFiller import ChartFiller
from helicalbi.core.vizflow.Fallback import Fallback
from helicalbi.core.vizflow.Visualization import Visualization
from helicalbi.model.ModelState import ModelState


def build_viz_graph():
    workflow = StateGraph(ModelState)

    workflow.add_node("ChartFiller", ChartFiller().process_flow)
    workflow.add_node("AntdVisualization", AntdVisualization().process_flow)

    workflow.set_entry_point("AntdVisualization")
    workflow.add_edge("AntdVisualization", "ChartFiller")
    workflow.add_edge("ChartFiller", END)

    return workflow.compile()
