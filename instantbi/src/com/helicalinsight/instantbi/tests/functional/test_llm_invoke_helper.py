"""Functional tests for ``helicalbi.common.LlmInvokeHelper`` logging."""
import logging
from unittest.mock import MagicMock

import pytest

from helicalbi.common import app_config
from helicalbi.common.LlmInvokeHelper import log_prompt, merge_token_usage, read_time_consumed
from helicalbi.model.TokenUsage import TokenUsage


pytestmark = pytest.mark.functional


@pytest.fixture(autouse=True)
def _restore_llm_activity_setting():
    original = app_config.show_llm_activity
    yield
    app_config.show_llm_activity = original


class TestLlmActivityLogging:
    def test_log_prompt_skipped_when_disabled(self, caplog):
        app_config.show_llm_activity = False
        with caplog.at_level(logging.INFO, logger="helicalbi.common.LlmInvokeHelper"):
            log_prompt("SELECT 1 FROM t")
        assert "LLM Prompt:" not in caplog.text

    def test_log_prompt_emitted_when_enabled(self, caplog):
        app_config.show_llm_activity = True
        with caplog.at_level(logging.INFO, logger="helicalbi.common.LlmInvokeHelper"):
            log_prompt("SELECT 1 FROM t")
        assert "LLM Prompt:" in caplog.text
        assert "SELECT 1 FROM t" in caplog.text

    def test_merge_token_usage_skipped_when_disabled(self, caplog):
        app_config.show_llm_activity = False
        state = {}
        with caplog.at_level(logging.INFO, logger="helicalbi.common.LlmInvokeHelper"):
            merge_token_usage(state, TokenUsage(input_tokens=10, output_tokens=5, total_tokens=15))
        assert "Accumulated token usage" not in caplog.text
        assert state["token_usage"]["total_tokens"] == 15

    def test_merge_token_usage_logged_when_enabled(self, caplog):
        app_config.show_llm_activity = True
        state = {}
        with caplog.at_level(logging.INFO, logger="helicalbi.common.LlmInvokeHelper"):
            merge_token_usage(state, TokenUsage(input_tokens=10, output_tokens=5, total_tokens=15))
        assert "Accumulated token usage" in caplog.text
        assert "total=15" in caplog.text

    def test_invoke_llm_skips_activity_logs_when_disabled(self, caplog, monkeypatch):
        from helicalbi.common.LlmInvokeHelper import invoke_llm

        app_config.show_llm_activity = False
        llm = MagicMock()
        llm.invoke.return_value = MagicMock()
        usage_factory = MagicMock()
        usage_factory.from_ai_message.return_value = TokenUsage(
            input_tokens=3, output_tokens=2, total_tokens=5
        )
        monkeypatch.setattr(
            "helicalbi.common.LlmInvokeHelper.get_token_usage_factory",
            lambda provider=None: usage_factory,
        )

        with caplog.at_level(logging.INFO, logger="helicalbi.common.LlmInvokeHelper"):
            invoke_llm(llm, "hello")

        assert "LLM Prompt:" not in caplog.text
        assert "LLM invoke:" not in caplog.text
        llm.invoke.assert_called_once_with("hello")

    def test_invoke_llm_logs_activity_when_enabled(self, caplog, monkeypatch):
        from helicalbi.common.LlmInvokeHelper import invoke_llm

        app_config.show_llm_activity = True
        llm = MagicMock()
        llm.invoke.return_value = MagicMock()
        usage_factory = MagicMock()
        usage_factory.from_ai_message.return_value = TokenUsage(
            input_tokens=3, output_tokens=2, total_tokens=5
        )
        monkeypatch.setattr(
            "helicalbi.common.LlmInvokeHelper.get_token_usage_factory",
            lambda provider=None: usage_factory,
        )

        with caplog.at_level(logging.INFO, logger="helicalbi.common.LlmInvokeHelper"):
            invoke_llm(llm, "hello")

        assert "LLM Prompt:" in caplog.text
        assert "hello" in caplog.text
        assert "LLM invoke:" in caplog.text
        assert "total=5" in caplog.text

    def test_invoke_llm_records_time_when_state_provided(self, monkeypatch):
        from helicalbi.common.LlmInvokeHelper import invoke_llm

        llm = MagicMock()
        llm.invoke.return_value = MagicMock()
        usage_factory = MagicMock()
        usage_factory.from_ai_message.return_value = TokenUsage(
            input_tokens=3, output_tokens=2, total_tokens=5
        )
        monkeypatch.setattr(
            "helicalbi.common.LlmInvokeHelper.get_token_usage_factory",
            lambda provider=None: usage_factory,
        )

        state = {}
        invoke_llm(llm, "hello", state=state)
        consumed = read_time_consumed(state)
        assert consumed.llm_seconds >= 0
        assert state["token_usage"]["total_tokens"] == 5
