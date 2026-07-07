from langgraph.graph import StateGraph, END

from helicalbi.core.sqlflow.FinalSqlGen import FinalSqlGen
from helicalbi.core.sqlflow.FindJoinFromApi import FindJoinFromApi
from helicalbi.core.sqlflow.FindTablesFromTopics import FindTablesFromTopics
from helicalbi.core.sqlflow.GetColumnNames import GetColumnNames
from helicalbi.core.sqlflow.GetExamples import GetExamples
from helicalbi.core.sqlflow.GetRequiredMetrics import GetRequiredMetrics
from helicalbi.core.sqlflow.GetRequiredSynonyms import GetRequiredSynonyms
from helicalbi.model.SQLAgent import SQLAgent


def build_sql_graph():
    workflow = StateGraph(SQLAgent)

    workflow.add_node("FindTablesFromTopics", FindTablesFromTopics().process_flow)
    workflow.add_node("GetRequiredSynonyms", GetRequiredSynonyms().process_flow)
    workflow.add_node("GetColumnNames", GetColumnNames().process_flow)
    workflow.add_node("GetRequiredMetrics", GetRequiredMetrics().process_flow)
    workflow.add_node("GetExamples", GetExamples().process_flow)
    workflow.add_node("FindJoinFromApi", FindJoinFromApi().process_flow)
    workflow.add_node("FinalSqlGen", FinalSqlGen().process_flow)

    workflow.set_entry_point("FindTablesFromTopics")
    workflow.add_edge("FindTablesFromTopics", "GetRequiredSynonyms")
    workflow.add_edge("GetRequiredSynonyms", "GetColumnNames")
    workflow.add_edge("GetColumnNames", "GetRequiredMetrics")
    workflow.add_edge("GetRequiredMetrics", "GetExamples")
    workflow.add_edge("GetExamples", "FindJoinFromApi")
    workflow.add_edge("FindJoinFromApi", "FinalSqlGen")
    workflow.add_edge("FinalSqlGen", END)

    return workflow.compile()
