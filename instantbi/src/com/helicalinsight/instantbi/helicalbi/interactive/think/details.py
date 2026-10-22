"""Resolve whether LLM activity details should be included."""
from __future__ import annotations

from typing import Any, Optional

from helicalbi.common import app_config
from helicalbi.interactive.think.constants import TRUTHY_STRINGS


class ActivityDetailsPolicy:
    """Decide if ``llm_activity_details`` should be attached to the payload."""

    def enabled(self, override: Optional[Any] = None) -> bool:
        if override is not None:
            if isinstance(override, str):
                return override.strip().lower() in TRUTHY_STRINGS
            return bool(override)
        return bool(getattr(app_config, "show_llm_activity_details", False))


_POLICY = ActivityDetailsPolicy()


def details_enabled(override: Optional[Any] = None) -> bool:
    return _POLICY.enabled(override)
