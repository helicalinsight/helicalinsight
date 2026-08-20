"""Integration test combining multiple helpers used during a chat turn.

The scenario is the bookkeeping that happens around a single LLM turn:

1. A user query arrives and is appended to the chat store.
2. The flow records the generated SQL, visualization, and insight.
3. The next turn reads the prior context back out, both as raw messages
   and the convenience ``get_last_sql_only`` view.

These helpers cooperate via in-memory module state, so integrating them
together catches regressions that pure unit tests would miss.
"""
import pytest

from helicalbi.common import ChatManager
from helicalbi.common.CommonAppender import append_to_workflow
from helicalbi.sql.SqlSanitizer import extract_sql


pytestmark = pytest.mark.integration


class TestChatToInsightFlow:
    def test_full_turn_bookkeeping_round_trip(self, reset_chat_stores):
        thread = "t-int-1"

        ChatManager.add_message(thread, "What were last quarter's sales?")

        raw_sql = "select region, sum(amount) from sales group by region"
        sanitized = extract_sql(raw_sql, "postgres")
        ChatManager.add_sql(thread, {"sql": sanitized})

        ChatManager.add_viz_response(thread, {"type": "bar", "title": "Sales"})
        ChatManager.add_insight(thread, "Sales were highest in APAC.")

        state = {}
        append_to_workflow("Rephrased query.", state)
        append_to_workflow("Generated SQL.", state)
        append_to_workflow("Executed SQL.", state)

        assert ChatManager.get_last_n(thread, 1) == [
            {"previous_query": "What were last quarter's sales?"}
        ]
        last_sql = ChatManager.get_last_sql(thread, 1)
        assert last_sql[0]["previous_sql"]["sql"] == sanitized
        assert ChatManager.get_last_sql_only(thread, 1) == [sanitized]

        assert ChatManager.get_last_n_viz(thread, 1)[0][
            "previous_visualization"
        ]["type"] == "bar"
        assert ChatManager.get_last_insight(thread, 1) == [
            {"previous_insight": "Sales were highest in APAC."}
        ]

        assert state["flow"] == [
            "Rephrased query.",
            "Generated SQL.",
            "Executed SQL.",
        ]

    def test_multiple_threads_remain_isolated(self, reset_chat_stores):
        ChatManager.add_message("user-a", "hello")
        ChatManager.add_sql("user-a", {"sql": "SELECT 1"})
        ChatManager.add_message("user-b", "hi")

        assert ChatManager.get_last_n("user-a") == [{"previous_query": "hello"}]
        assert ChatManager.get_last_n("user-b") == [{"previous_query": "hi"}]
        assert ChatManager.get_last_sql_only("user-a") == ["SELECT 1"]
        assert ChatManager.get_last_sql_only("user-b") == []
