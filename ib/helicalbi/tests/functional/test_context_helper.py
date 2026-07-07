"""Functional tests for ``helicalbi.memory.getcontext.ContextHelper``."""
import pytest

from helicalbi.memory.getcontext.ContextClass import ChatItem, GraphState
from helicalbi.memory.getcontext.ContextHelper import resolve_context_node


pytestmark = pytest.mark.functional


class TestResolveContextNode:
    def test_new_query_returns_none_context(self):
        state = GraphState(user_query="hi", chat_history=[], query_type="NEW_QUERY")
        assert resolve_context_node(state) == {"resolved_context": None}

    def test_continuation_picks_last_chat_item(self):
        items = [
            ChatItem(question="first"),
            ChatItem(question="second"),
            ChatItem(question="third", sql="SELECT 1"),
        ]
        state = GraphState(
            user_query="follow up",
            chat_history=items,
            query_type="CONTINUATION",
        )
        result = resolve_context_node(state)
        assert result["resolved_context"].question == "third"
        assert result["resolved_context"].sql == "SELECT 1"

    def test_continuation_with_empty_history_returns_none(self):
        state = GraphState(
            user_query="x", chat_history=[], query_type="CONTINUATION"
        )
        assert resolve_context_node(state) == {"resolved_context": None}


class TestChatItemModel:
    def test_minimum_fields(self):
        item = ChatItem(question="q")
        assert item.question == "q"
        assert item.sql is None
        assert item.visualization is None

    def test_optional_fields(self):
        item = ChatItem(question="q", sql="select 1", visualization={"type": "bar"})
        assert item.sql == "select 1"
        assert item.visualization == {"type": "bar"}
