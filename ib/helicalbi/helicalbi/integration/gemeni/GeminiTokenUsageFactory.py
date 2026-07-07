from __future__ import annotations

from typing import Any, Optional

from helicalbi.integration.TokenUsageFactory import BaseTokenUsageFactory
from helicalbi.model.TokenUsage import TokenUsage


class GeminiTokenUsageFactory(BaseTokenUsageFactory):
    """Token usage for ``ChatGoogleGenerativeAI`` responses."""

    def from_response_metadata(self, metadata: Optional[dict[str, Any]]) -> TokenUsage:
        if not metadata:
            return TokenUsage()

        token_usage = metadata.get("token_usage") or metadata.get("usage") or {}
        if token_usage:
            return self._build({**metadata, **token_usage})

        cost = self._read_cost(metadata)
        if any(cost.values()):
            return TokenUsage(**cost)
        return TokenUsage()
