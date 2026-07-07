"""Functional tests for ``helicalbi.api.HttpCallService.fetch_service_api``."""
from unittest.mock import MagicMock, patch

import pytest

from helicalbi.api.HttpCallService import fetch_service_api


pytestmark = pytest.mark.functional


class TestFetchServiceApi:
    def _session_with_status(self, status_code, json_body=None):
        session = MagicMock()
        response = MagicMock()
        response.status_code = status_code
        if json_body is not None:
            response.json.return_value = json_body
        session.post.return_value = response
        return session

    def test_returns_json_on_success(self, session_cookie):
        session = self._session_with_status(200, {"status": 1, "response": {"ok": True}})
        with patch(
            "helicalbi.api.HttpCallService.requests.Session", return_value=session
        ):
            result = fetch_service_api(session_cookie=session_cookie, service_json={"service": "x"})
        assert result == {"status": 1, "response": {"ok": True}}

    def test_returns_none_on_non_200(self, session_cookie):
        session = self._session_with_status(401)
        with patch(
            "helicalbi.api.HttpCallService.requests.Session", return_value=session
        ):
            result = fetch_service_api(session_cookie=session_cookie, service_json={"service": "x"})
        assert result is None

    def test_sets_jsessionid_cookie(self, session_cookie):
        session = self._session_with_status(200, {"status": 1, "response": {}})
        with patch(
            "helicalbi.api.HttpCallService.requests.Session", return_value=session
        ):
            fetch_service_api(session_cookie=session_cookie, service_json={"service": "x"})

        session.cookies.set.assert_called_once_with("JSESSIONID", session_cookie)

    def test_posts_to_services_endpoint(self, session_cookie):
        session = self._session_with_status(200, {"status": 1, "response": {}})
        with patch(
            "helicalbi.api.HttpCallService.requests.Session", return_value=session
        ):
            fetch_service_api(session_cookie=session_cookie, service_json={"service": "x"})

        posted_url = session.post.call_args.args[0]
        assert posted_url.endswith("/services")
