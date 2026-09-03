"""Functional tests for InstantBI JWT/SSO header forwarding."""

import pytest

from helicalbi.common.auth import (
    bind_request_identity,
    downstream_request_headers,
    resolve_session_auth,
    set_api_cache_identity,
)

pytestmark = pytest.mark.functional


class TestResolveSessionAuth:
    def test_returns_session_cookie(self):
        cookie, username = resolve_session_auth(
            {"sessionCookie": "abc", "username": "alice"}
        )
        assert cookie == "abc"
        assert username == "alice"

    def test_allows_jwt_headers_without_session_cookie(self):
        cookie, username = resolve_session_auth(
            {
                "username": "jwt-user",
                "headers": {"Authorization": "Bearer jwt-token", "type": "jwt"},
            }
        )
        assert cookie == ""
        assert username == "jwt-user"

    def test_allows_auth_token_request_param_without_session_cookie(self):
        cookie, username = resolve_session_auth(
            {
                "username": "sso-user",
                "requestParams": {"authToken": "Bearer sso-token", "type": "token"},
            }
        )
        assert cookie == ""
        assert username == "sso-user"

    def test_type_header_alone_is_not_enough(self):
        with pytest.raises(ValueError, match="sessionCookie is required"):
            resolve_session_auth({"headers": {"type": "jwt"}})

    def test_still_requires_session_or_auth(self):
        with pytest.raises(ValueError, match="sessionCookie is required"):
            resolve_session_auth({"username": "alice"})


class TestBindRequestIdentity:
    def test_stores_forwarded_headers_for_downstream_calls(self):
        set_api_cache_identity("", "")
        bind_request_identity(
            {
                "sessionCookie": "sess",
                "username": "alice",
                "headers": {"Authorization": "Bearer jwt-token", "Host": "skip.me"},
                "requestParams": {"type": "jwt"},
            }
        )
        headers = downstream_request_headers()
        assert headers["Authorization"] == "Bearer jwt-token"
        assert headers["type"] == "jwt"
        assert "Host" not in headers
