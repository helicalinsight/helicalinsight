"""Functional tests for token usage helpers in ``bl.helpers``."""

import pytest

from bl.helpers import extract_token_usage_dict, resolve_audit_status_from_response

pytestmark = pytest.mark.functional


class TestExtractTokenUsageDict:
    def test_reads_top_level_token_usage(self):
        payload = {"token_usage": {"input_tokens": 1, "output_tokens": 2, "total_tokens": 3}}
        assert extract_token_usage_dict(payload) == payload["token_usage"]

    def test_reads_nested_chat_response_token_usage(self):
        payload = {
            "chat_response": {
                "token_usage": {"input_tokens": 4, "output_tokens": 6, "total_tokens": 10}
            }
        }
        assert extract_token_usage_dict(payload) == payload["chat_response"]["token_usage"]

    def test_prefers_top_level_over_nested(self):
        payload = {
            "token_usage": {"total_tokens": 1},
            "chat_response": {"token_usage": {"total_tokens": 99}},
        }
        assert extract_token_usage_dict(payload)["total_tokens"] == 1

    def test_returns_empty_dict_when_missing(self):
        assert extract_token_usage_dict({}) == {}
        assert extract_token_usage_dict({"chat_response": {}}) == {}

    def test_supports_partial_agent_state_after_failure(self):
        from helicalbi.model.output.ChatResponse import ChatResponse

        partial_state = {
            "token_usage": {"input_tokens": 100, "output_tokens": 50, "total_tokens": 150},
            "error": "viz step failed",
        }
        chat_response = ChatResponse.from_agent_state(partial_state).to_dict()
        usage = extract_token_usage_dict({"chat_response": chat_response})
        assert usage["total_tokens"] == 150


class TestResolveAuditStatusFromResponse:
    def test_promotes_success_to_failure_for_non_empty_error(self):
        status, message = resolve_audit_status_from_response(
            {"error": "syntax error"},
            "SUCCESS",
        )
        assert status == "FAILURE"
        assert message == "syntax error"

    def test_keeps_success_when_error_is_empty(self):
        status, message = resolve_audit_status_from_response(
            {"error": "   "},
            "SUCCESS",
        )
        assert status == "SUCCESS"
        assert message is None

    def test_keeps_success_when_error_is_missing(self):
        status, message = resolve_audit_status_from_response({}, "SUCCESS")
        assert status == "SUCCESS"
        assert message is None

    def test_does_not_override_error_status(self):
        status, message = resolve_audit_status_from_response(
            {"error": "syntax error"},
            "ERROR",
            "boom",
        )
        assert status == "ERROR"
        assert message == "boom"

    def test_does_not_override_aborted_status(self):
        status, message = resolve_audit_status_from_response(
            {"error": "syntax error"},
            "ABORTED",
            "Request has been cancelled.",
        )
        assert status == "ABORTED"
        assert message == "Request has been cancelled."
