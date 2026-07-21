"""Functional tests for ``helicalbi.audit.llm_usage_audit``."""

from unittest.mock import MagicMock, patch

import pytest
import requests

from helicalbi.audit import llm_usage_audit
from helicalbi.audit.llm_usage_audit import audit_llm_usage_async

pytestmark = pytest.mark.functional


class TestAuditLlmUsageAsync:
    def test_no_op_when_disabled(self):
        with patch.object(llm_usage_audit.app_config, "enable_llm_usage_audit", False), patch.object(
            llm_usage_audit, "_executor"
        ) as executor:
            audit_llm_usage_async(
                endpoint="/interactive",
                user_id=42,
                session_cookie="sess",
                base_url="http://localhost/hi-ee",
                user_query="show sales",
                token_usage={"total_tokens": 10},
                request_status="SUCCESS",
            )
            executor.submit.assert_not_called()

    def test_no_op_when_total_tokens_zero(self):
        with patch.object(llm_usage_audit.app_config, "enable_llm_usage_audit", True), patch.object(
            llm_usage_audit, "_executor"
        ) as executor:
            audit_llm_usage_async(
                endpoint="/interactive",
                user_id=42,
                session_cookie="sess",
                base_url="http://localhost/hi-ee",
                user_query="show sales",
                token_usage={"total_tokens": 0},
                request_status="SUCCESS",
            )
            executor.submit.assert_not_called()

    def test_uses_configured_base_url_when_request_omits_it(self):
        usage = {"total_tokens": 10}
        with patch.object(llm_usage_audit.app_config, "enable_llm_usage_audit", True), patch.object(
            llm_usage_audit, "configured_base_url", "http://hiee:8080/hi-ee"
        ), patch.object(llm_usage_audit, "_executor") as executor:
            audit_llm_usage_async(
                endpoint="/interactive",
                user_id=42,
                session_cookie="sess",
                base_url="",
                user_query="show sales",
                token_usage=usage,
                request_status="SUCCESS",
            )
            executor.submit.assert_called_once()
            assert executor.submit.call_args.kwargs["base_url"] == "http://hiee:8080/hi-ee"

    def test_uses_api_cache_user_id_when_request_omits_it(self):
        usage = {"total_tokens": 10}
        with patch.object(llm_usage_audit.app_config, "enable_llm_usage_audit", True), patch.object(
            llm_usage_audit, "get_api_cache_user_id", return_value=99
        ), patch.object(llm_usage_audit, "_executor") as executor:
            audit_llm_usage_async(
                endpoint="/interactive",
                user_id=None,
                session_cookie="sess",
                base_url="http://localhost/hi-ee",
                user_query="show sales",
                token_usage=usage,
                request_status="SUCCESS",
            )
            executor.submit.assert_called_once()
            assert executor.submit.call_args.kwargs["user_id"] == 99

    def test_no_op_when_session_context_still_missing(self):
        with patch.object(llm_usage_audit.app_config, "enable_llm_usage_audit", True), patch.object(
            llm_usage_audit, "configured_base_url", ""
        ), patch.object(llm_usage_audit, "get_api_cache_user_id", return_value=None), patch.object(
            llm_usage_audit, "_executor"
        ) as executor:
            audit_llm_usage_async(
                endpoint="/interactive",
                user_id=None,
                session_cookie="",
                base_url="",
                user_query="show sales",
                token_usage={"total_tokens": 10},
                request_status="SUCCESS",
            )
            executor.submit.assert_not_called()

    def test_strips_non_finite_cost_values_before_submit(self):
        usage = {
            "input_tokens": 10,
            "output_tokens": 5,
            "total_tokens": 15,
            "input_cost": float("nan"),
            "output_cost": float("inf"),
            "total_cost": "not-a-number",
        }
        with patch.object(llm_usage_audit.app_config, "enable_llm_usage_audit", True), patch.object(
            llm_usage_audit, "_executor"
        ) as executor:
            audit_llm_usage_async(
                endpoint="/interactive",
                user_id=42,
                session_cookie="sess",
                base_url="http://localhost/hi-ee",
                user_query="show sales",
                token_usage=usage,
                request_status="SUCCESS",
            )
            executor.submit.assert_called_once()
            submitted_usage = executor.submit.call_args.kwargs["token_usage"]
            assert submitted_usage["total_tokens"] == 15
            assert "input_cost" not in submitted_usage
            assert "output_cost" not in submitted_usage
            assert "total_cost" not in submitted_usage

    def test_submits_background_task_when_tokens_present(self):
        usage = {"input_tokens": 5, "output_tokens": 5, "total_tokens": 10}
        with patch.object(llm_usage_audit.app_config, "enable_llm_usage_audit", True), patch.object(
            llm_usage_audit, "_executor"
        ) as executor:
            audit_llm_usage_async(
                endpoint="/data-insight",
                user_id=42,
                session_cookie="sess",
                base_url="http://localhost/hi-ee",
                user_query="insight",
                token_usage=usage,
                request_status="ERROR",
                error_message="failed",
            )
            executor.submit.assert_called_once()
            kwargs = executor.submit.call_args.kwargs
            assert kwargs["endpoint"] == "/data-insight"
            assert kwargs["token_usage"] == usage

    def test_post_audit_posts_json_payload(self):
        session = MagicMock()
        response = MagicMock(status_code=200)
        response.json.return_value = {"status": 1}
        session.post.return_value = response
        with patch("helicalbi.audit.llm_usage_audit.requests.Session", return_value=session):
            llm_usage_audit._post_audit(
                endpoint="/interactive",
                user_id=42,
                session_cookie="sess",
                base_url="http://localhost/hi-ee",
                user_query="show sales",
                token_usage={"total_tokens": 12, "input_tokens": 10, "output_tokens": 2},
                request_status="SUCCESS",
                error_message=None,
                chat_id="chat-1",
                chat_seq_id="2",
            )
        session.cookies.set.assert_called_once_with("JSESSIONID", "sess")
        session.post.assert_called_once()
        call_kwargs = session.post.call_args.kwargs
        assert call_kwargs["json"]["userId"] == 42
        assert call_kwargs["json"]["endpoint"] == "/interactive"
        assert call_kwargs["json"]["tokenUsage"]["total_tokens"] == 12

    def test_post_audit_logs_unsaved_record_when_api_rejects(self, caplog):
        session = MagicMock()
        response = MagicMock(status_code=200, text='{"status":0,"message":"Failed to persist LLM usage audit."}')
        response.json.return_value = {"status": 0, "message": "Failed to persist LLM usage audit."}
        session.post.return_value = response
        with patch("helicalbi.audit.llm_usage_audit.requests.Session", return_value=session):
            llm_usage_audit._post_audit(
                endpoint="/interactive",
                user_id=42,
                session_cookie="sess",
                base_url="http://localhost/hi-ee",
                user_query="show sales",
                token_usage={"total_tokens": 12},
                request_status="SUCCESS",
                error_message=None,
                chat_id="chat-1",
                chat_seq_id="2",
            )
        assert "LLM_USAGE_AUDIT_UNSAVED" in caplog.text
        assert "Failed to persist LLM usage audit." in caplog.text
        assert '"userId":42' in caplog.text
        assert '"total_tokens":12' in caplog.text

    def test_post_audit_logs_unsaved_record_on_http_error(self, caplog):
        session = MagicMock()
        response = MagicMock(status_code=500, text="internal error")
        response.json.side_effect = ValueError("not json")
        session.post.return_value = response
        with patch("helicalbi.audit.llm_usage_audit.requests.Session", return_value=session):
            llm_usage_audit._post_audit(
                endpoint="/data-insight",
                user_id=42,
                session_cookie="sess",
                base_url="http://localhost/hi-ee",
                user_query="insight",
                token_usage={"total_tokens": 8},
                request_status="ERROR",
                error_message="failed",
                chat_id=None,
                chat_seq_id=None,
            )
        assert "LLM_USAGE_AUDIT_UNSAVED" in caplog.text
        assert "http_status_500" in caplog.text

    def test_post_audit_logs_unsaved_record_on_request_exception(self, caplog):
        session = MagicMock()
        session.post.side_effect = requests.RequestException("connection reset")
        with patch("helicalbi.audit.llm_usage_audit.requests.Session", return_value=session):
            llm_usage_audit._post_audit(
                endpoint="/interactive",
                user_id=42,
                session_cookie="sess",
                base_url="http://localhost/hi-ee",
                user_query="show sales",
                token_usage={"total_tokens": 5},
                request_status="SUCCESS",
                error_message=None,
                chat_id=None,
                chat_seq_id=None,
            )
        assert "LLM_USAGE_AUDIT_UNSAVED" in caplog.text
        assert "request_failed:RequestException" in caplog.text
