from __future__ import annotations

from typing import Any, Optional

from helicalbi.integration.TokenUsageFactory import BaseTokenUsageFactory
from helicalbi.model.TokenUsage import TokenUsage


class AnthropicTokenUsageFactory(BaseTokenUsageFactory):
    """Token usage for ``ChatAnthropic`` responses."""

    def from_response_metadata(self, metadata: Optional[dict[str, Any]]) -> TokenUsage:
        if not metadata:
            return TokenUsage()

        usage = metadata.get("usage") or {}
        if usage:
            return self._build({**metadata, **usage})

        cost = self._read_cost(metadata)
        if any(cost.values()):
            return TokenUsage(**cost)
        return TokenUsage()
