from __future__ import annotations

from typing import Any, Optional

from helicalbi.integration.TokenUsageFactory import BaseTokenUsageFactory
from helicalbi.model.TokenUsage import TokenUsage


class OpenAITokenUsageFactory(BaseTokenUsageFactory):
    """Token usage for OpenAI / OpenAI-compatible ``ChatOpenAI`` responses."""

    def from_ai_message(self, message: Any) -> TokenUsage:
        usage_metadata = getattr(message, "usage_metadata", None)
        response_metadata = getattr(message, "response_metadata", None)

        if usage_metadata and response_metadata:
            token_usage = response_metadata.get("token_usage") or {}
            return self._build({**response_metadata, **token_usage, **usage_metadata})

        if usage_metadata:
            return self.from_usage_metadata(usage_metadata)

        if response_metadata:
            return self.from_response_metadata(response_metadata)

        return TokenUsage()

    def from_response_metadata(self, metadata: Optional[dict[str, Any]]) -> TokenUsage:
        if not metadata:
            return TokenUsage()

        token_usage = metadata.get("token_usage") or metadata.get("usage") or {}
        if token_usage:
            source = {**metadata, **token_usage}
            return self._build(source)

        cost = self._read_cost(metadata)
        if any(cost.values()):
            return self._build_cost_only(metadata)
        return TokenUsage()
