from __future__ import annotations

from typing import Any, Optional

from helicalbi.integration.TokenUsageFactory import BaseTokenUsageFactory
from helicalbi.model.TokenUsage import TokenUsage


class AnthropicTokenUsageFactory(BaseTokenUsageFactory):
    """Token usage for ``ChatAnthropic`` responses."""

    def from_ai_message(self, message: Any) -> TokenUsage:
        usage_metadata = getattr(message, "usage_metadata", None)
        response_metadata = getattr(message, "response_metadata", None)

        if usage_metadata and response_metadata:
            usage = response_metadata.get("usage") or {}
            return self._build({**response_metadata, **usage, **usage_metadata})

        if usage_metadata:
            return self.from_usage_metadata(usage_metadata)

        if response_metadata:
            return self.from_response_metadata(response_metadata)

        return TokenUsage()

    def from_response_metadata(self, metadata: Optional[dict[str, Any]]) -> TokenUsage:
        if not metadata:
            return TokenUsage()

        usage = metadata.get("usage") or {}
        if usage:
            return self._build({**metadata, **usage})

        cost = self._read_cost(metadata)
        if any(cost.values()):
            return self._build_cost_only(metadata)
        return TokenUsage()
