from __future__ import annotations

from typing import Any, Optional

from helicalbi.integration.TokenUsageFactory import BaseTokenUsageFactory
from helicalbi.model.TokenUsage import TokenUsage


class OllamaTokenUsageFactory(BaseTokenUsageFactory):
    """Token usage for ``ChatOllama`` responses (``prompt_eval_count`` / ``eval_count``)."""

    def from_response_metadata(self, metadata: Optional[dict[str, Any]]) -> TokenUsage:
        if not metadata:
            return TokenUsage()

        if metadata.get("prompt_eval_count") is not None or metadata.get("eval_count") is not None:
            return self._build(metadata)

        cost = self._read_cost(metadata)
        if any(cost.values()):
            return self._build_cost_only(metadata)
        return TokenUsage()
