from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any, Optional

from helicalbi.model.TokenUsage import TokenUsage


class TokenUsageFactory(ABC):
    """Build :class:`TokenUsage` from provider-specific LangChain message metadata."""

    @abstractmethod
    def from_usage_metadata(self, metadata: Optional[dict[str, Any]]) -> TokenUsage:
        pass

    @abstractmethod
    def from_response_metadata(self, metadata: Optional[dict[str, Any]]) -> TokenUsage:
        pass

    def from_ai_message(self, message: Any) -> TokenUsage:
        usage_metadata = getattr(message, "usage_metadata", None)
        if usage_metadata:
            return self.from_usage_metadata(usage_metadata)

        response_metadata = getattr(message, "response_metadata", None)
        if response_metadata:
            return self.from_response_metadata(response_metadata)

        return TokenUsage()


class BaseTokenUsageFactory(TokenUsageFactory):
    """Shared helpers for normalizing LangChain usage and cost fields."""

    @staticmethod
    def _read_cost(metadata: dict[str, Any]) -> dict[str, Optional[float]]:
        def _as_float(value: Any) -> Optional[float]:
            if value is None:
                return None
            try:
                return float(value)
            except (TypeError, ValueError):
                return None

        return {
            "input_cost": _as_float(metadata.get("input_cost") or metadata.get("prompt_cost")),
            "output_cost": _as_float(metadata.get("output_cost") or metadata.get("completion_cost")),
            "total_cost": _as_float(metadata.get("total_cost") or metadata.get("cost")),
        }

    @staticmethod
    def _read_token_counts(metadata: dict[str, Any]) -> tuple[int, int, int]:
        input_tokens = int(
            metadata.get("input_tokens")
            or metadata.get("prompt_tokens")
            or metadata.get("prompt_eval_count")
            or 0
        )
        output_tokens = int(
            metadata.get("output_tokens")
            or metadata.get("completion_tokens")
            or metadata.get("eval_count")
            or 0
        )
        total_tokens = int(metadata.get("total_tokens") or 0)
        if total_tokens == 0 and (input_tokens or output_tokens):
            total_tokens = input_tokens + output_tokens
        return input_tokens, output_tokens, total_tokens

    def _build(self, metadata: dict[str, Any]) -> TokenUsage:
        input_tokens, output_tokens, total_tokens = self._read_token_counts(metadata)
        return TokenUsage(
            input_tokens=input_tokens,
            output_tokens=output_tokens,
            total_tokens=total_tokens,
            **self._read_cost(metadata),
        )

    def from_usage_metadata(self, metadata: Optional[dict[str, Any]]) -> TokenUsage:
        if not metadata:
            return TokenUsage()
        return self._build(metadata)
