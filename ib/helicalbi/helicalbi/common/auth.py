"""Session cookie helpers for authenticating against the HI backend."""
from __future__ import annotations

from contextvars import ContextVar
from typing import Any, Mapping, Optional, Tuple

_api_cache_username: ContextVar[str] = ContextVar("api_cache_username", default="")
_api_cache_orgname: ContextVar[str] = ContextVar("api_cache_orgname", default="")


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


def set_api_cache_identity(username: str = "", orgname: str = "") -> None:
    _api_cache_username.set(username or "")
    _api_cache_orgname.set(orgname or "")


def get_api_cache_username() -> str:
    return _api_cache_username.get()


def get_api_cache_orgname() -> str:
    return _api_cache_orgname.get()


def bind_request_identity(
    data: Optional[Mapping[str, Any]] = None,
    user_input: Optional[Mapping[str, Any]] = None,
) -> Tuple[str, str]:
    """Resolve session auth and bind username/orgname for API response caching."""
    session_cookie, username = resolve_session_auth(data, user_input)
    orgname = resolve_orgname(data, user_input)
    set_api_cache_identity(username, orgname)
    return session_cookie, username


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


def resolve_session_auth(
    data: Optional[Mapping[str, Any]] = None,
    user_input: Optional[Mapping[str, Any]] = None,
) -> Tuple[str, str]:
    """Extract ``sessionCookie`` and ``username`` from a Flask request payload."""
    for source in (user_input, data):
        if not source:
            continue
        session_cookie = source.get("sessionCookie")
        if session_cookie is not None:
            session_cookie = str(session_cookie).strip()
            if session_cookie:
                username = str(source.get("username") or "")
                return session_cookie, username
    raise ValueError("sessionCookie is required")
