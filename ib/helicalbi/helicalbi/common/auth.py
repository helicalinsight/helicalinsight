"""Session cookie helpers for authenticating against the HI backend."""
from __future__ import annotations

from typing import Any, Mapping, Optional, Tuple


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
