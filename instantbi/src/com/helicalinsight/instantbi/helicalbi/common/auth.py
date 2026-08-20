"""Session cookie helpers for authenticating against the HI backend."""
from __future__ import annotations

from contextvars import ContextVar
from typing import Any, Mapping, Optional, Tuple

_api_cache_username: ContextVar[str] = ContextVar("api_cache_username", default="")
_api_cache_orgname: ContextVar[str] = ContextVar("api_cache_orgname", default="")
_api_cache_user_id: ContextVar[Optional[int]] = ContextVar("api_cache_user_id", default=None)
_api_cache_org_id: ContextVar[Optional[int]] = ContextVar("api_cache_org_id", default=None)


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
) -> None:
    _api_cache_username.set(username or "")
    _api_cache_orgname.set(orgname or "")
    _api_cache_user_id.set(user_id)
    _api_cache_org_id.set(org_id)


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
    set_api_cache_identity(username, orgname, user_id, org_id)
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
