"""Merge prepared cube_info fields onto a graph result (DRY for JSON + SSE paths)."""
from __future__ import annotations

import logging
from typing import Any, Mapping

from helicalbi.interactive.fast.constants import (
    CUBE_INFO_DICT_KEYS,
    CUBE_INFO_LIST_KEYS,
    CUBE_INFO_RESULT_KEYS,
)

logger = logging.getLogger(__name__)


class CubeInfoResultMerger:
    """Prefer ``cube_info_prepared`` values, fall back to existing result fields."""

    def merge(
        self,
        result: dict[str, Any],
        prepared: Mapping[str, Any] | None,
    ) -> dict[str, Any]:
        if not prepared:
            return result
        logger.info("cube info prepared")
        for key in CUBE_INFO_RESULT_KEYS:
            fallback = self._empty(key)
            result[key] = prepared.get(key) or result.get(key) or fallback
        result["got_domain"] = True
        return result

    @staticmethod
    def _empty(key: str) -> Any:
        if key in CUBE_INFO_LIST_KEYS:
            return []
        if key in CUBE_INFO_DICT_KEYS:
            return {}
        return ""
