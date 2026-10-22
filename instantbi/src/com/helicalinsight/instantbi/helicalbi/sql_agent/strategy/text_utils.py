"""Tiny, dependency-free text helpers shared by the strategy modules."""
from __future__ import annotations

import re
from typing import Any

from helicalbi.sql_agent.constants import NON_ALNUM_PATTERN


def clean(value: Any) -> str:
    """Stringify ``value`` (``None``/falsy becomes ``""``) and strip whitespace."""
    return str(value or "").strip()


def compact(value: Any) -> str:
    """Lowercase ``value`` and drop every non-alphanumeric character (comparison key)."""
    return re.sub(NON_ALNUM_PATTERN, "", str(value or "").lower())
