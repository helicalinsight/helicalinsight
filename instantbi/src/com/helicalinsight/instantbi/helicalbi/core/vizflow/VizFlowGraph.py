from langgraph.graph import StateGraph, END

from helicalbi.core.vizflow.AntdVisualization import AntdVisualization
from helicalbi.core.vizflow.ChartFiller import ChartFiller
from helicalbi.core.vizflow.Fallback import Fallback
from helicalbi.core.vizflow.VizModelFiller import VizModelFiller
from helicalbi.core.vizflow.VizPropertiesPolish import VizPropertiesPolishNode
from helicalbi.model.ModelState import ModelState


def _route_after_viz_model_filler(state: ModelState) -> str:
    """Deterministic fill → polish, or Fallback for other/custom charts."""
    if state.get("skip"):
        return "end"
    if state.get("use_other_fallback"):
        return "fallback"
    return "polish"


def _route_after_chart_filler(state: ModelState) -> str:
    """Legacy settings+format path ends; other / functional-format goes to Fallback."""
    if state.get("skip"):
        return "end"
    if state.get("use_other_fallback"):
        return "fallback"
    return "end"


def build_viz_graph():
    """Default viz graph: deterministic VizModel + 1 LLM property polish."""
    workflow = StateGraph(ModelState)

    workflow.add_node("VizModelFiller", VizModelFiller().process_flow)
    workflow.add_node("VizPropertiesPolish", VizPropertiesPolishNode().process_flow)
    workflow.add_node("Fallback", Fallback().process_flow)

    workflow.set_entry_point("VizModelFiller")
    workflow.add_conditional_edges(
        "VizModelFiller",
        _route_after_viz_model_filler,
        {
            "polish": "VizPropertiesPolish",
            "fallback": "Fallback",
            "end": END,
        },
    )
    workflow.add_edge("VizPropertiesPolish", END)
    workflow.add_edge("Fallback", END)

    return workflow.compile()


def build_legacy_viz_graph():
    """Previous LLM-heavy path (chart pick + settings + formats). Kept for A/B."""
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
