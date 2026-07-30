from langgraph.graph import StateGraph, END

from helicalbi.core.vizflow.AntdVisualization import AntdVisualization
from helicalbi.core.vizflow.ChartFiller import ChartFiller
from helicalbi.core.vizflow.Fallback import Fallback
from helicalbi.model.ModelState import ModelState


def _route_after_chart_filler(state: ModelState) -> str:
    """Settings+format path ends; other / functional-format goes to Fallback."""
    if state.get("skip"):
        return "end"
    if state.get("use_other_fallback"):
        return "fallback"
    return "end"


def build_viz_graph():
    workflow = StateGraph(ModelState)

    workflow.add_node("AntdVisualization", AntdVisualization().process_flow)
    workflow.add_node("ChartFiller", ChartFiller().process_flow)
    workflow.add_node("Fallback", Fallback().process_flow)

    workflow.set_entry_point("AntdVisualization")
    workflow.add_edge("AntdVisualization", "ChartFiller")
    workflow.add_conditional_edges(
        "ChartFiller",
        _route_after_chart_filler,
        {
            "fallback": "Fallback",
            "end": END,
        },
    )
    workflow.add_edge("Fallback", END)

    return workflow.compile()
