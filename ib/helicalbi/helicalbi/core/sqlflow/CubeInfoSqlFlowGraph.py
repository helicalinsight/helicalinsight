"""SQL generation graph specialized for cube_info agent semantic layers."""

from langgraph.graph import END, StateGraph

from helicalbi.core.sqlflow.FinalSqlGen import FinalSqlGen
from helicalbi.core.sqlflow.FindJoinFromApi import FindJoinFromApi
from helicalbi.core.sqlflow.FindTablesFromTopics import FindTablesFromTopics
from helicalbi.core.sqlflow.GetColumnNames import GetColumnNames
from helicalbi.core.sqlflow.GetRequiredMetrics import GetRequiredMetrics
from helicalbi.core.sqlflow.GetRequiredSynonyms import GetRequiredSynonyms
from helicalbi.model.SQLAgent import SQLAgent


def build_cube_info_sql_graph():
    """Build the cube_info SQL pipeline.

    Flow:
      domain/topics → tables → synonyms → columns (qualified table.column)
      → business metrics + default functions + aliases → joins → FinalSqlGen
      (dialect, chat history, previous SQL)
    """
    workflow = StateGraph(SQLAgent)

    workflow.add_node("FindTablesFromTopics", FindTablesFromTopics().process_flow)
    workflow.add_node("GetRequiredSynonyms", GetRequiredSynonyms().process_flow)
    workflow.add_node("GetColumnNames", GetColumnNames().process_flow)
    workflow.add_node("GetRequiredMetrics", GetRequiredMetrics().process_flow)
    workflow.add_node("FindJoinFromApi", FindJoinFromApi().process_flow)
    workflow.add_node("FinalSqlGen", FinalSqlGen().process_flow)

    workflow.set_entry_point("FindTablesFromTopics")
    workflow.add_edge("FindTablesFromTopics", "GetRequiredSynonyms")
    workflow.add_edge("GetRequiredSynonyms", "GetColumnNames")
    workflow.add_edge("GetColumnNames", "GetRequiredMetrics")
    workflow.add_edge("GetRequiredMetrics", "FindJoinFromApi")
    workflow.add_edge("FindJoinFromApi", "FinalSqlGen")
    workflow.add_edge("FinalSqlGen", END)

    return workflow.compile()
