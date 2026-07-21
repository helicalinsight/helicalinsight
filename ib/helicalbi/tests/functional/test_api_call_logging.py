"""Functional tests for HI service API call logging in ``HttpCallService``."""
import logging
from unittest.mock import MagicMock, patch

import pytest

from helicalbi.api.ApiCallCache import clear as clear_api_cache
from helicalbi.api.HttpCallService import _log_api_io, fetch_service_api
from helicalbi.common import app_config
from helicalbi.common.auth import set_api_cache_identity


pytestmark = pytest.mark.functional


@pytest.fixture(autouse=True)
def _restore_api_call_log_setting():
    clear_api_cache()
    original = app_config.show_api_call_log
    yield
    clear_api_cache()
    app_config.show_api_call_log = original


class TestApiCallLogging:
    def test_log_api_io_skipped_when_disabled(self, caplog):
        app_config.show_api_call_log = False
        with caplog.at_level(logging.INFO, logger="helicalbi.api.HttpCallService"):
            _log_api_io({"service": "get"}, {"status": 1})
        assert "API Input:" not in caplog.text

    def test_log_api_io_emitted_when_enabled(self, caplog):
        app_config.show_api_call_log = True
        with caplog.at_level(logging.INFO, logger="helicalbi.api.HttpCallService"):
            _log_api_io({"service": "get"}, {"status": 1, "response": {"ok": True}})
        assert "API Input:" in caplog.text
        assert "API Output:" in caplog.text
        assert '"service": "get"' in caplog.text

    def test_fetch_service_api_skips_call_log_when_disabled(self, caplog, session_cookie):
        app_config.show_api_call_log = False
        session = MagicMock()
        response = MagicMock()
        response.status_code = 200
        response.json.return_value = {"status": 1, "response": {"ok": True}}
        session.post.return_value = response
        set_api_cache_identity("alice", "acme")

        with patch(
            "helicalbi.api.HttpCallService.requests.Session", return_value=session
        ):
            with caplog.at_level(logging.INFO, logger="helicalbi.api.HttpCallService"):
                fetch_service_api(
                    session_cookie=session_cookie,
                    service_json={"service": "get", "formData": "{}"},
                )

        assert "Calling service API with session cookie" not in caplog.text
        assert "API Input:" not in caplog.text

    def test_fetch_service_api_logs_io_when_enabled(self, caplog, session_cookie):
        app_config.show_api_call_log = True
        session = MagicMock()
        response = MagicMock()
        response.status_code = 200
        response.json.return_value = {"status": 1, "response": {"ok": True}}
        session.post.return_value = response
        set_api_cache_identity("alice", "acme")

        with patch(
            "helicalbi.api.HttpCallService.requests.Session", return_value=session
        ):
            with caplog.at_level(logging.INFO, logger="helicalbi.api.HttpCallService"):
                fetch_service_api(
                    session_cookie=session_cookie,
                    service_json={"service": "get", "formData": "{}"},
                )

        assert "Calling service API with session cookie" in caplog.text
        assert "API Input:" in caplog.text or "API Cache Input:" in caplog.text
        assert "API Output:" in caplog.text
