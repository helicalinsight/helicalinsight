"""Functional tests for ``helicalbi.common.ChatManager``."""
import pytest

from helicalbi.common import ChatManager


pytestmark = pytest.mark.functional


class TestAddMessage:
    def test_add_message_creates_thread_when_missing(self, reset_chat_stores):
        ChatManager.add_message("t1", "hello")
        assert "t1" in ChatManager.chat_store
        assert ChatManager.chat_store["t1"] == [{"previous_query": "hello"}]

    def test_add_message_appends_to_existing_thread(self, reset_chat_stores):
        ChatManager.add_message("t1", "first")
        ChatManager.add_message("t1", "second")
        assert ChatManager.chat_store["t1"] == [
            {"previous_query": "first"},
            {"previous_query": "second"},
        ]

    def test_threads_are_isolated(self, reset_chat_stores):
        ChatManager.add_message("a", "hi")
        ChatManager.add_message("b", "hey")
        assert ChatManager.chat_store["a"] == [{"previous_query": "hi"}]
        assert ChatManager.chat_store["b"] == [{"previous_query": "hey"}]


class TestGetLastN:
    def test_returns_empty_list_for_unknown_thread(self, reset_chat_stores):
        assert ChatManager.get_last_n("missing") == []

    def test_returns_last_n_items_in_order(self, reset_chat_stores):
        for i in range(5):
            ChatManager.add_message("t", f"q{i}")
        result = ChatManager.get_last_n("t", n=3)
        assert result == [
            {"previous_query": "q2"},
            {"previous_query": "q3"},
            {"previous_query": "q4"},
        ]

    def test_default_window_is_three(self, reset_chat_stores):
        for i in range(10):
            ChatManager.add_message("t", f"q{i}")
        assert len(ChatManager.get_last_n("t")) == 3


class TestVizSqlInsightStores:
    def test_add_and_retrieve_viz_response(self, reset_chat_stores):
        ChatManager.add_viz_response("t", {"type": "bar"})
        assert ChatManager.get_last_n_viz("t") == [
            {"previous_visualization": {"type": "bar"}}
        ]

    def test_add_and_retrieve_sql(self, reset_chat_stores):
        ChatManager.add_sql("t", {"sql": "select 1"})
        assert ChatManager.get_last_sql("t") == [
            {"previous_sql": {"sql": "select 1"}}
        ]

    def test_add_and_retrieve_insight(self, reset_chat_stores):
        ChatManager.add_insight("t", "an insight")
        assert ChatManager.get_last_insight("t") == [
            {"previous_insight": "an insight"}
        ]

    def test_get_last_sql_only_extracts_sql_strings(self, reset_chat_stores):
        ChatManager.add_sql("t", {"sql": "SELECT 1"})
        ChatManager.add_sql("t", {"sql": "SELECT 2"})
        assert ChatManager.get_last_sql_only("t", 1) == ["SELECT 2"]
        assert ChatManager.get_last_sql_only("t", 2) == ["SELECT 1", "SELECT 2"]

    def test_get_last_sql_only_missing_thread_returns_empty(self, reset_chat_stores):
        assert ChatManager.get_last_sql_only("ghost") == []

    def test_unknown_thread_returns_empty_for_all_getters(self, reset_chat_stores):
        assert ChatManager.get_last_n_viz("ghost") == []
        assert ChatManager.get_last_sql("ghost") == []
        assert ChatManager.get_last_insight("ghost") == []
