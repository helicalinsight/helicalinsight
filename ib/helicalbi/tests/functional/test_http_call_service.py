"""Functional tests for ``helicalbi.api.HttpCallService.fetch_service_api``."""
from unittest.mock import MagicMock, patch

import pytest

from helicalbi.api.ApiCallCache import clear as clear_api_cache
from helicalbi.api.HttpCallService import fetch_service_api
from helicalbi.common.auth import set_api_cache_identity


pytestmark = pytest.mark.functional


@pytest.fixture(autouse=True)
def _reset_api_cache():
    clear_api_cache()
    set_api_cache_identity("", "")
    yield
    clear_api_cache()
    set_api_cache_identity("", "")


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

    def test_caches_non_execute_query_responses(self, session_cookie):
        session = self._session_with_status(200, {"status": 1, "response": {"ok": True}})
        service_json = {
            "service": "get",
            "formData": '{"metadataFileName":"meta.json"}',
        }
        set_api_cache_identity("alice", "acme")

        with patch(
            "helicalbi.api.HttpCallService.requests.Session", return_value=session
        ):
            first = fetch_service_api(session_cookie=session_cookie, service_json=service_json)
            second = fetch_service_api(session_cookie=session_cookie, service_json=service_json)

        assert first == {"status": 1, "response": {"ok": True}}
        assert second == first
        assert session.post.call_count == 1

    def test_does_not_cache_execute_query(self, session_cookie):
        session = self._session_with_status(200, {"status": 1, "response": {"data": []}})
        service_json = {
            "service": "executeQuery",
            "formData": '{"query":"SELECT 1"}',
        }
        set_api_cache_identity("alice", "acme")

        with patch(
            "helicalbi.api.HttpCallService.requests.Session", return_value=session
        ):
            fetch_service_api(session_cookie=session_cookie, service_json=service_json)
            fetch_service_api(session_cookie=session_cookie, service_json=service_json)

        assert session.post.call_count == 2

    def test_does_not_cache_generate_query(self, session_cookie):
        session = self._session_with_status(200, {"status": 1, "response": {"query": "SELECT 1"}})
        service_json = {
            "service": "generateQuery",
            "formData": '{"columns":["id"]}',
        }
        set_api_cache_identity("alice", "acme")

        with patch(
            "helicalbi.api.HttpCallService.requests.Session", return_value=session
        ):
            fetch_service_api(session_cookie=session_cookie, service_json=service_json)
            fetch_service_api(session_cookie=session_cookie, service_json=service_json)

        assert session.post.call_count == 2

    def test_cache_key_varies_by_username_and_orgname(self, session_cookie):
        session = self._session_with_status(200, {"status": 1, "response": {"ok": True}})
        service_json = {
            "service": "get",
            "formData": '{"metadataFileName":"meta.json"}',
        }

        with patch(
            "helicalbi.api.HttpCallService.requests.Session", return_value=session
        ):
            set_api_cache_identity("alice", "acme")
            fetch_service_api(session_cookie=session_cookie, service_json=service_json)
            set_api_cache_identity("bob", "acme")
            fetch_service_api(session_cookie=session_cookie, service_json=service_json)

        assert session.post.call_count == 2

    def test_skips_cache_when_disabled(self, session_cookie, monkeypatch):
        from helicalbi.common import app_config

        monkeypatch.setattr(app_config, "api_cache_enabled", False)
        session = self._session_with_status(200, {"status": 1, "response": {"ok": True}})
        service_json = {
            "service": "get",
            "formData": '{"metadataFileName":"meta.json"}',
        }
        set_api_cache_identity("alice", "acme")

        with patch(
            "helicalbi.api.HttpCallService.requests.Session", return_value=session
        ):
            fetch_service_api(session_cookie=session_cookie, service_json=service_json)
            fetch_service_api(session_cookie=session_cookie, service_json=service_json)

        assert session.post.call_count == 2
