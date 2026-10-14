"""Tests for Instant report load + chat-memory hydration."""

import pytest

from helicalbi.common.ChatGraphMemory import chat_graph_memory
from helicalbi.common import ChatManager
from helicalbi.service.report.UpdateChatMemory import (
    hydrate_chat_memory_from_report,
    unwrap_report_data,
)

pytestmark = pytest.mark.functional

_SAMPLE_REPORT = {
    "status": 1,
    "response": {
        "data": {
            "reportName": "Instant_1_edit",
            "metadata": {
                "location": "test",
                "metadataFileName": "pg_sample_travel_data.metadata",
            },
            "state": {
                "inputs": [
                    {
                        "chat_sequence_id": 1,
                        "input": "What is the total travel cost per client per month?",
                    }
                ],
                "chat_responses": [
                    {
                        "chat_sequence_id": 1,
                        "viz": {
                            "vf_template": "",
                            "chart_name": "heatmap",
                            "vf_title": "Total Travel Cost per Client per Month",
                        },
                        "sql": {
                            "raw_sql": (
                                "```sql\nSELECT "
                                '"meeting_details"."client_name" AS "Client Name" '
                                "FROM \"travel_details\" LIMIT 100"
                            ),
                            "dialect": "postgresql",
                            "required_domain": ["Sales Order"],
                            "required_topic": ["Client Meeting", "Sales"],
                            "required_table": ["travel_details", "meeting_details"],
                            "required_column": ["travel_details.travel_cost"],
                            "required_join": "",
                            "required_cube_info": {
                                "picked_dimensions": ["Client Name", "MONTH"],
                                "picked_metrics": ["Travel Cost"],
                            },
                        },
                        "summary": {
                            "insight": "Travel cost by client and month.",
                        },
                        "report_model": {
                            "data_model": {"sql": "SELECT 1"},
                            "viz_model": {
                                "chart": {"viz": "Heatmap"},
                                "properties": {
                                    "title": "Total Travel Cost per Client per Month",
                                    "formatting": {"Travel Cost": "$#,##0.00"},
                                },
                            },
                        },
                        "token_usage": {"total_tokens": 9893},
                        "error": "",
                        "hreportId": "b6c0fb09-f3f5-4032-b79e-b1340a06b388",
                    }
                ],
                "activeChatId": "c80a03e1-9981-460b-a616-5bbdef276bb2",
                "id": "7af8c944-6aa8-4875-81a0-e4fc84e139df",
            },
        }
    },
}


@pytest.fixture
def reset_memory(reset_chat_stores):
    chat_graph_memory.clear()
    yield
    chat_graph_memory.clear()


class TestUnwrapReportData:
    def test_unwraps_hi_envelope(self):
        data = unwrap_report_data(_SAMPLE_REPORT)
        assert data["reportName"] == "Instant_1_edit"
        assert data["state"]["activeChatId"].startswith("c80a03e1")

    def test_accepts_bare_state_object(self):
        payload = {"state": {"activeChatId": "abc"}, "reportName": "x"}
        assert unwrap_report_data(payload)["reportName"] == "x"


class TestHydrateChatMemoryFromReport:
    def test_stores_graph_node_and_history(self, reset_memory):
        result = hydrate_chat_memory_from_report(_SAMPLE_REPORT, username="tester")
        chat_id = "c80a03e1-9981-460b-a616-5bbdef276bb2"
        assert result == {"chatid": chat_id, "turns": 1}

        node = chat_graph_memory.get_node(chat_id, 1)
        assert node is not None
        assert "Client Name" in node["sql"]
        assert "```" not in node["sql"]
        assert node["dialect"] == "postgresql"
        assert node["user_query"].startswith("What is the total travel cost")
        assert node["domain"] == ["Sales Order"]
        assert node["topics"] == ["Client Meeting", "Sales"]
        assert node["vf_title"] == "Total Travel Cost per Client per Month"
        assert node["format_strings"]["Travel Cost"] == "$#,##0.00"
        assert node["chat_response"]["sql"]["required_cube_info"]["picked_metrics"] == [
            "Travel Cost"
        ]
        assert node["chat_response"]["hreportId"].startswith("b6c0fb09")

        assert ChatManager.get_last_n(chat_id)[0]["previous_query"].startswith(
            "What is the total travel cost"
        )
        assert ChatManager.get_last_sql_only(chat_id)[0].startswith("SELECT")
        assert ChatManager.get_last_insight(chat_id)[0]["previous_insight"].startswith(
            "Travel cost"
        )
        viz = ChatManager.get_last_n_viz(chat_id)[0]["previous_visualization"]
        assert viz["chart"]["viz"] == "Heatmap"

    def test_does_not_use_report_tab_id_as_chatid(self, reset_memory):
        result = hydrate_chat_memory_from_report(
            {"state": {"id": "7af8c944-6aa8-4875-81a0-e4fc84e139df", "chat_responses": []}}
        )
        assert result == {"chatid": "", "turns": 0}

    def test_hydrated_sql_is_available_for_data_insight(self, reset_memory):
        from helicalbi.controller.helpers import resolve_sql_from_request

        hydrate_chat_memory_from_report(_SAMPLE_REPORT, username="tester")
        chat_id = "c80a03e1-9981-460b-a616-5bbdef276bb2"
        sql = resolve_sql_from_request({}, chat_id, 1, context="data-insight")
        assert sql.startswith("SELECT")
        assert "```" not in sql

    def test_hydrates_think_mode_supporting_questions_for_data_insight(self, reset_memory):
        from helicalbi.controller.helpers import resolve_sql_from_request

        report = {
            "state": {
                "activeChatId": "think-chat-1",
                "inputs": [{"chat_sequence_id": 1, "input": "Analyze travel cost"}],
                "chat_responses": [
                    {
                        "chat_sequence_id": 1,
                        "is_think_plan": True,
                        "summary": {"insight": "Travel cost rose."},
                        "question_history": [
                            {
                                "index": 1,
                                "question": "What is total travel cost?",
                                "chat_seq_id": "1_1",
                                "chat_response": {
                                    "sql": {
                                        "raw_sql": "SELECT SUM(travel_cost) AS total FROM travel_details",
                                        "dialect": "postgresql",
                                        "required_domain": ["Travel"],
                                        "required_topic": ["Cost"],
                                    },
                                    "summary": {"insight": "Total travel cost is 120000."},
                                    "report_model": {"data_model": {"sql": "SELECT 1"}},
                                },
                            },
                            {
                                "index": 2,
                                "question": "Cost by month?",
                                "chat_response": {
                                    "sql": {
                                        "raw_sql": "SELECT month, SUM(travel_cost) FROM travel_details GROUP BY month",
                                        "dialect": "postgresql",
                                    },
                                    "summary": {"insight": "Cost peaked in March."},
                                },
                            },
                        ],
                    }
                ],
            }
        }

        result = hydrate_chat_memory_from_report(report, username="tester")
        assert result == {"chatid": "think-chat-1", "turns": 1}

        node = chat_graph_memory.get_node("think-chat-1", "1_1")
        assert node is not None
        assert node["user_query"] == "What is total travel cost?"
        assert node["sql"].startswith("SELECT SUM(travel_cost)")
        assert node["domain"] == ["Travel"]
        assert node["topics"] == ["Cost"]

        derived_seq_node = chat_graph_memory.get_node("think-chat-1", "1-2")
        assert derived_seq_node is not None
        assert "GROUP BY month" in derived_seq_node["sql"]

        sql = resolve_sql_from_request({}, "think-chat-1", "1_1", context="data-insight")
        assert "travel_cost" in sql
        assert sql.upper().startswith("SELECT")

    def test_skips_when_chat_id_missing(self, reset_memory):
        result = hydrate_chat_memory_from_report({"state": {"chat_responses": []}})
        assert result == {"chatid": "", "turns": 0}
