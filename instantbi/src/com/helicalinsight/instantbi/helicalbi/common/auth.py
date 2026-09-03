"""Session cookie helpers for authenticating against the HI backend."""
from __future__ import annotations

from contextvars import ContextVar
from typing import Any, Mapping, Optional, Tuple

_SKIP_HEADER_NAMES = frozenset(
    {
        "host",
        "connection",
        "content-length",
        "content-type",
        "content-encoding",
        "transfer-encoding",
        "keep-alive",
        "proxy-authenticate",
        "proxy-authorization",
        "te",
        "trailer",
        "upgrade",
        "expect",
        "accept-encoding",
    }
)
_AUTH_PARAM_NAMES = ("Authorization", "authToken", "type", "X-Auth-Token")
_AUTH_CREDENTIAL_NAMES = ("Authorization", "authToken", "X-Auth-Token")

_api_cache_username: ContextVar[str] = ContextVar("api_cache_username", default="")
_api_cache_orgname: ContextVar[str] = ContextVar("api_cache_orgname", default="")
_api_cache_user_id: ContextVar[Optional[int]] = ContextVar("api_cache_user_id", default=None)
_api_cache_org_id: ContextVar[Optional[int]] = ContextVar("api_cache_org_id", default=None)
_api_cache_headers: ContextVar[Optional[dict[str, str]]] = ContextVar(
    "api_cache_headers", default=None
)
_api_cache_request_params: ContextVar[Optional[dict[str, str]]] = ContextVar(
    "api_cache_request_params", default=None
)


def _parse_optional_int(value: Any) -> Optional[int]:
    if value is None or value == "":
        return None
    try:
        parsed = int(value)
    except (TypeError, ValueError):
        return None
    return parsed if parsed > 0 else None


def resolve_user_id(
    data: Optional[Mapping[str, Any]] = None,
    user_input: Optional[Mapping[str, Any]] = None,
) -> Optional[int]:
    """Extract user id from a Flask request payload."""
    for source in (user_input, data):
        if not source:
            continue
        for key in ("userId", "user_id", "userid"):
            user_id = _parse_optional_int(source.get(key))
            if user_id is not None:
                return user_id
    return None


def resolve_org_id(
    data: Optional[Mapping[str, Any]] = None,
    user_input: Optional[Mapping[str, Any]] = None,
) -> Optional[int]:
    """Extract organization id from a Flask request payload."""
    for source in (user_input, data):
        if not source:
            continue
        for key in ("orgId", "org_id", "organizationId", "organization_id"):
            org_id = _parse_optional_int(source.get(key))
            if org_id is not None:
                return org_id
    return None


def resolve_orgname(
    data: Optional[Mapping[str, Any]] = None,
    user_input: Optional[Mapping[str, Any]] = None,
) -> str:
    """Extract organization name from a Flask request payload."""
    for source in (user_input, data):
        if not source:
            continue
        for key in ("orgname", "orgName", "organization", "j_organization"):
            value = source.get(key)
            if value is not None and str(value).strip():
                return str(value).strip()
    return ""


def set_api_cache_identity(
    username: str = "",
    orgname: str = "",
    user_id: Optional[int] = None,
    org_id: Optional[int] = None,
    headers: Optional[Mapping[str, Any]] = None,
    request_params: Optional[Mapping[str, Any]] = None,
) -> None:
    _api_cache_username.set(username or "")
    _api_cache_orgname.set(orgname or "")
    _api_cache_user_id.set(user_id)
    _api_cache_org_id.set(org_id)
    set_api_cache_headers(headers)
    set_api_cache_request_params(request_params)


def set_api_cache_headers(headers: Optional[Mapping[str, Any]] = None) -> None:
    _api_cache_headers.set(_as_str_dict(headers))


def set_api_cache_request_params(request_params: Optional[Mapping[str, Any]] = None) -> None:
    _api_cache_request_params.set(_as_str_dict(request_params))


def get_api_cache_headers() -> dict[str, str]:
    return dict(_api_cache_headers.get() or {})


def get_api_cache_request_params() -> dict[str, str]:
    return dict(_api_cache_request_params.get() or {})


def downstream_request_headers(
    headers: Optional[Mapping[str, Any]] = None,
    request_params: Optional[Mapping[str, Any]] = None,
) -> dict[str, str]:
    """Headers InstantBI should send when calling hi-ee (JWT/SSO/session)."""
    merged = _as_str_dict(headers if headers is not None else get_api_cache_headers())
    params = _as_str_dict(
        request_params if request_params is not None else get_api_cache_request_params()
    )
    for name in _AUTH_PARAM_NAMES:
        if _header_value(merged, name):
            continue
        value = _header_value(params, name)
        if value:
            merged[name] = value
    return {
        key: value
        for key, value in merged.items()
        if key.lower() not in _SKIP_HEADER_NAMES and value
    }


def get_api_cache_username() -> str:
    return _api_cache_username.get()


def get_api_cache_orgname() -> str:
    return _api_cache_orgname.get()


def get_api_cache_user_id() -> Optional[int]:
    return _api_cache_user_id.get()


def get_api_cache_org_id() -> Optional[int]:
    return _api_cache_org_id.get()


def bind_request_identity(
    data: Optional[Mapping[str, Any]] = None,
    user_input: Optional[Mapping[str, Any]] = None,
) -> Tuple[str, str, Optional[int], Optional[int]]:
    """Resolve session auth and bind identity for API response caching."""
    session_cookie, username = resolve_session_auth(data, user_input)
    orgname = resolve_orgname(data, user_input)
    user_id = resolve_user_id(data, user_input)
    org_id = resolve_org_id(data, user_input)
    headers = resolve_forwarded_headers(data, user_input)
    request_params = resolve_forwarded_request_params(data, user_input)
    set_api_cache_identity(
        username,
        orgname,
        user_id,
        org_id,
        headers=headers,
        request_params=request_params,
    )
    return session_cookie, username, user_id, org_id


def resolve_role_profile(
    data: Optional[Mapping[str, Any]] = None,
    user_input: Optional[Mapping[str, Any]] = None,
) -> dict[str, list[Any]]:
    """Extract ``userRole`` and ``userProfile`` from a Flask request payload."""
    profile: dict[str, list[Any]] = {
        "userRole": [],
        "userProfile": [],
    }
    for source in (data, user_input):
        if not source:
            continue
        for key in profile:
            if key in source and not profile[key]:
                value = source.get(key)
                profile[key] = value if isinstance(value, list) else []
    return profile


def resolve_forwarded_headers(
    data: Optional[Mapping[str, Any]] = None,
    user_input: Optional[Mapping[str, Any]] = None,
) -> dict[str, str]:
    """Headers forwarded by Java InstantBI, with a Flask request fallback."""
    for source in (user_input, data):
        if not source:
            continue
        raw = source.get("headers")
        if isinstance(raw, Mapping) and raw:
            return _as_str_dict(raw)
    return _headers_from_flask_request()


def resolve_forwarded_request_params(
    data: Optional[Mapping[str, Any]] = None,
    user_input: Optional[Mapping[str, Any]] = None,
) -> dict[str, str]:
    """Extra request parameters forwarded by Java InstantBI (authToken, type, …)."""
    for source in (user_input, data):
        if not source:
            continue
        raw = source.get("requestParams")
        if isinstance(raw, Mapping) and raw:
            return _as_str_dict(raw)
    return {}


def resolve_session_auth(
    data: Optional[Mapping[str, Any]] = None,
    user_input: Optional[Mapping[str, Any]] = None,
) -> Tuple[str, str]:
    """Extract ``sessionCookie`` and ``username`` from a Flask request payload.

    JWT/SSO requests may omit ``JSESSIONID`` when ``Authorization`` / ``authToken``
    headers or params are forwarded instead.
    """
    session_cookie = ""
    username = ""
    for source in (user_input, data):
        if not source:
            continue
        if not username:
            username = str(source.get("username") or "")
        cookie = source.get("sessionCookie")
        if cookie is not None:
            cookie = str(cookie).strip()
            if cookie and not session_cookie:
                session_cookie = cookie
                if not username:
                    username = str(source.get("username") or "")
    headers = resolve_forwarded_headers(data, user_input)
    request_params = resolve_forwarded_request_params(data, user_input)
    if session_cookie or _has_auth_credentials(headers, request_params):
        return session_cookie, username
    raise ValueError("sessionCookie is required")


def _as_str_dict(raw: Optional[Mapping[str, Any]]) -> dict[str, str]:
    if not isinstance(raw, Mapping):
        return {}
    result: dict[str, str] = {}
    for key, value in raw.items():
        name = str(key or "").strip()
        if not name:
            continue
        text = "" if value is None else str(value).strip()
        if text:
            result[name] = text
    return result


def _header_value(headers: Mapping[str, str], name: str) -> str:
    if name in headers and headers[name]:
        return headers[name]
    lowered = name.lower()
    for key, value in headers.items():
        if key.lower() == lowered and value:
            return value
    return ""


def _has_auth_credentials(
    headers: Mapping[str, str],
    request_params: Mapping[str, str],
) -> bool:
    for name in _AUTH_CREDENTIAL_NAMES:
        if _header_value(headers, name) or _header_value(request_params, name):
            return True
    return False


def _headers_from_flask_request() -> dict[str, str]:
    try:
        from flask import has_request_context, request

        if not has_request_context():
            return {}
        return {
            key: value
            for key, value in request.headers.items()
            if key.lower() not in _SKIP_HEADER_NAMES and str(value or "").strip()
        }
    except Exception:  # noqa: BLE001 - auth fallback must not fail the request
        return {}
