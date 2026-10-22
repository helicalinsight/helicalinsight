"""Merge token-usage maps from plan / execute / opening stages."""
from __future__ import annotations

from typing import Any, Mapping, MutableMapping


class TokenUsageMerger:
    """Accumulate numeric counters; keep first non-numeric value for a key."""

    def merge(
        self,
        target: MutableMapping[str, Any],
        source: Mapping[str, Any] | None,
    ) -> MutableMapping[str, Any]:
        if not isinstance(source, Mapping) or not source:
            return target
        for key, value in source.items():
            if value is None:
                continue
            if isinstance(value, (int, float)) and isinstance(
                target.get(key), (int, float)
            ):
                target[key] = target.get(key, 0) + value
            elif key not in target:
                target[key] = value
        return target

    def combine(self, *sources: Mapping[str, Any] | None) -> dict[str, Any]:
        result: dict[str, Any] = {}
        for source in sources:
            self.merge(result, source)
        return result


_MERGER = TokenUsageMerger()


def merge_token_usage(
    target: MutableMapping[str, Any],
    source: Mapping[str, Any] | None,
) -> MutableMapping[str, Any]:
    return _MERGER.merge(target, source)
