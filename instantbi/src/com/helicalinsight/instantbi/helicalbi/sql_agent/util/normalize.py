"""Normalize scalars / lists and extract plan domain/topic scope (DRY helpers)."""
from __future__ import annotations

import re
from typing import Any, List, Mapping


def normalize_whitespace(text: Any) -> str:
    """Lowercase, strip, and collapse internal whitespace."""
    return re.sub(r"\s+", " ", str(text or "").strip().lower())


def as_list(value: Any) -> List[Any]:
    """Wrap a scalar as a one-item list; leave sequences alone; ``None`` → ``[]``."""
    if value is None:
        return []
    if isinstance(value, list):
        return list(value)
    if isinstance(value, (tuple, set)):
        return list(value)
    return [value]


def as_name_list(value: Any) -> List[str]:
    """Flatten a string or sequence into non-empty stripped name strings."""
    if value is None:
        return []
    if isinstance(value, str):
        text = value.strip()
        return [text] if text else []
    if isinstance(value, list):
        names: List[str] = []
        for item in value:
            text = str(item or "").strip()
            if text:
                names.append(text)
        return names
    text = str(value).strip()
    return [text] if text else []


def plan_scope(plan: Mapping[str, Any] | None) -> tuple[list[str], list[str]]:
    """Return ``(topics, domains)`` from an investigation plan dict."""
    plan = plan if isinstance(plan, Mapping) else {}
    topics = as_name_list(plan.get("topics"))
    domains = as_name_list(plan.get("domain"))
    return topics, domains
