"""Functional tests for the cube_info SQL generator wiring."""

from unittest.mock import MagicMock, patch

import pytest

from helicalbi.core.flows.CubeInfoSqlGenerator import CubeInfoSqlGenerator

pytestmark = pytest.mark.functional


def test_build_sql_state_includes_domain_context_and_history():
    state = {
        "query": "sum of travel cost by platform",
        "session_cookie": "cookie",
        "last_chats": [{"role": "user", "content": "hi"}],
        "user_name": "tester",
        "cube_metadata": [{"database_table": "travel_details"}],
        "topic_mappings": [{"topic_name": "Travel", "component": ["booking platform"]}],
        "relationship_of_table": {},
        "synonyms": [],
        "model_file_name": "model.json",
        "model_location": "/",
        "business_metrics": [{"metric": "cost of travel", "tables": ["travel_details"]}],
        "domain_context": "Domain: Sales Travel; topics: Travel, Expense",
        "action": "none",
        "dialect": "postgres",
        "dbname": "demo",
        "thread_id": "t1",
        "md_file_name": "md.json",
        "md_location": "/",
        "topics": ["Travel"],
        "domain": ["Sales Travel"],
    }
    sub_state = CubeInfoSqlGenerator._build_sql_state(state)
    assert sub_state["domain_context"].startswith("Domain: Sales Travel")
    assert sub_state["topics"] == ["Travel"]
    assert sub_state["business_metrics"][0]["metric"] == "cost of travel"
    assert sub_state["topic_mappings"][0]["topic_name"] == "Travel"
    assert sub_state["dialect"] == "postgres"
    assert sub_state["last_chats"] == [{"role": "user", "content": "hi"}]
    assert sub_state["cube_metadata"][0]["database_table"] == "travel_details"


def test_run_sql_subgraph_folds_final_sql():
    sql_graph = MagicMock()
    sql_graph.invoke.return_value = {
        "final_sql": "SELECT 1",
        "sql_reason": "ok",
        "required_tables": ["travel_details"],
        "required_joins": "",
        "required_synonyms": [],
        "required_business_metrics": [{"metric": "cost"}],
        "required_column_description": "- travel_details.booking_platform",
        "required_functions": "- travel_details.booking_platform: db.generic.groupBy.group",
        "required_cube_info": {
            "picked_dimensions": ["booking platform"],
            "picked_metrics": ["cost of travel"],
        },
        "messages": ["keep-me"],
        "last_chats": ["drop-me"],
    }
    generator = CubeInfoSqlGenerator(sql_graph=sql_graph)
    outer = {
        "query": "q",
        "session_cookie": "c",
        "last_chats": [],
        "user_name": "u",
        "cube_metadata": [],
        "topic_mappings": [],
        "relationship_of_table": {},
        "synonyms": [],
        "model_file_name": "a",
        "model_location": "/",
        "business_metrics": [],
        "domain_context": "",
        "action": "none",
        "dialect": "postgres",
        "dbname": "db",
        "thread_id": "1",
        "md_file_name": "m",
        "md_location": "/",
        "topics": [],
        "domain": [],
        "token_usage": {},
        "time_consumed": {},
        "flow": [],
    }
    with patch(
        "helicalbi.core.flows.CubeInfoSqlGenerator.extract_sql",
        return_value="SELECT 1",
    ), patch(
        "helicalbi.core.flows.CubeInfoSqlGenerator.append_to_workflow",
    ):
        result = generator.run_sql_subgraph(outer)

    assert result["sql"] == "SELECT 1"
    assert result["sql_reason"] == "ok"
    assert result["required_details"]["required_functions"].startswith("- travel_details")
    assert result["required_details"]["required_cube_info"]["picked_dimensions"] == [
        "booking platform"
    ]
    sql_graph.invoke.assert_called_once()
    called_state = sql_graph.invoke.call_args[0][0]
    assert "domain_context" in called_state
    assert "dialect" in called_state
    assert "last_chats" in called_state
