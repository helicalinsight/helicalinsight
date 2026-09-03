from langgraph.graph import END, StateGraph

from helicalbi.core.dashboardflow.CollectDashboardContext import (
    CollectDashboardContext,
    route_after_context,
)
from helicalbi.core.dashboardflow.DashboardAudit import DashboardAudit
from helicalbi.core.dashboardflow.DashboardLayoutClamp import DashboardLayoutClamp
from helicalbi.core.dashboardflow.DashboardLayoutNode import DashboardLayoutNode
from helicalbi.core.dashboardflow.DashboardPlanNode import DashboardPlanNode
from helicalbi.core.dashboardflow.SelectDashboardFilters import SelectDashboardFilters
from helicalbi.model.DashboardLayoutState import DashboardLayoutState


def build_dashboard_layout_graph():
    """Single convert-dashboard graph, sequential stages then audit.

    CollectContext → PlanSummary → SelectFilters → MakeLayout → Assemble → Audit
    """
    workflow = StateGraph(DashboardLayoutState)
    workflow.add_node("CollectContext", CollectDashboardContext().process_flow)
    workflow.add_node("PlanSummary", DashboardPlanNode().process_flow)
    workflow.add_node("SelectFilters", SelectDashboardFilters().process_flow)
    workflow.add_node("MakeLayout", DashboardLayoutNode().process_flow)
    workflow.add_node("Assemble", DashboardLayoutClamp().process_flow)
    workflow.add_node("Audit", DashboardAudit().process_flow)

    workflow.set_entry_point("CollectContext")
    workflow.add_conditional_edges(
        "CollectContext",
        route_after_context,
        {
            "plan": "PlanSummary",
            "audit": "Audit",
        },
    )
    workflow.add_edge("PlanSummary", "SelectFilters")
    workflow.add_edge("SelectFilters", "MakeLayout")
    workflow.add_edge("MakeLayout", "Assemble")
    workflow.add_edge("Assemble", "Audit")
    workflow.add_edge("Audit", END)
    return workflow.compile()
