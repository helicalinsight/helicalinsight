"""Helpers shared by utility config providers."""

from __future__ import annotations

from copy import deepcopy
from typing import Any

_SECRET_KEYS = frozenset(
    {
        "api_key",
        "apikey",
        "password",
        "secret",
        "token",
        "access_token",
        "authorization",
    }
)


def deep_merge(base: dict, updates: dict) -> dict:
    """Recursively merge *updates* into a copy of *base*."""
    result = deepcopy(base)
    for key, value in updates.items():
        if (
            key in result
            and isinstance(result[key], dict)
            and isinstance(value, dict)
        ):
            result[key] = deep_merge(result[key], value)
        else:
            result[key] = deepcopy(value)
    return result


def mask_secrets(value: Any) -> Any:
    """Return a copy of *value* with sensitive fields redacted for API output."""
    if isinstance(value, dict):
        masked: dict[str, Any] = {}
        for key, item in value.items():
            if str(key).lower() in _SECRET_KEYS:
                if item is None or item == "":
                    masked[key] = item
                elif isinstance(item, str) and item.startswith("${") and item.endswith("}"):
                    masked[key] = item
                else:
                    masked[key] = "***"
            else:
                masked[key] = mask_secrets(item)
        return masked
    if isinstance(value, list):
        return [mask_secrets(item) for item in value]
    return value
