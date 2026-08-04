"""
Generic token-usage reader driven by per-provider ``usage_path`` in ``llm_config.yaml``.

Most providers populate ``usage_metadata``. For providers that do not (e.g. Ollama),
set ``usage_path: response_metadata`` (or a dotted path into it).
"""

from __future__ import annotations

import logging
from typing import Any, Optional

from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.model.TokenUsage import TokenUsage

logger = logging.getLogger(__name__)

_LLM_CONFIG = "llm_config.yaml"
_DEFAULT_USAGE_PATH = "usage_metadata"


def _as_float(value: Any) -> Optional[float]:
    if value is None:
        return None
    try:
        return float(value)
    except (TypeError, ValueError):
        return None


def _as_int(value: Any) -> int:
    if value is None:
        return 0
    try:
        return int(value)
    except (TypeError, ValueError):
        return 0


def _resolve_message_path(message: Any, path: str) -> Any:
    """Resolve ``usage_metadata`` / ``response_metadata.token_usage`` against a message."""
    if not path:
        return None
    parts = path.split(".")
    current: Any = getattr(message, parts[0], None)
    for part in parts[1:]:
        if not isinstance(current, dict):
            return None
        current = current.get(part)
    return current


def _flatten_usage_dict(meta: dict[str, Any]) -> dict[str, Any]:
    """Merge nested ``token_usage`` / ``usage`` objects onto the top-level dict."""
    nested = meta.get("token_usage") or meta.get("usage") or {}
    if isinstance(nested, dict) and nested:
        return {**meta, **nested}
    return meta


class TokenUsageExtractor:
    """Normalize LangChain AI-message metadata into :class:`TokenUsage`."""

    def __init__(self, usage_path: str = _DEFAULT_USAGE_PATH, provider: Optional[str] = None):
        self.provider = provider
        self.usage_path = usage_path or _DEFAULT_USAGE_PATH

    @classmethod
    def from_config(
        cls,
        config_path: str = _LLM_CONFIG,
        provider: Optional[str] = None,
        llm_config: Optional[dict[str, Any]] = None,
    ) -> "TokenUsageExtractor":
        """Load ``providers.<provider>.usage_path`` from ``llm_config.yaml``."""
        config = llm_config if llm_config is not None else ConfigLoader.load_config(config_path)
        effective = provider or config.get("default_provider")
        if not effective:
            logger.warning("No LLM provider configured for token usage; defaulting to usage_metadata.")
            return cls(usage_path=_DEFAULT_USAGE_PATH, provider=None)

        provider_cfg = (config.get("providers") or {}).get(effective) or {}
        usage_path = provider_cfg.get("usage_path") or _DEFAULT_USAGE_PATH
        return cls(usage_path=usage_path, provider=effective)

    @classmethod
    def from_usage_path(
        cls, usage_path: str, provider: Optional[str] = None
    ) -> "TokenUsageExtractor":
        """Build from an explicit usage_path (useful for tests)."""
        return cls(usage_path=usage_path, provider=provider)

    # ------------------------------------------------------------------
    # Public API
    # ------------------------------------------------------------------

    def from_usage_metadata(self, metadata: Optional[dict[str, Any]]) -> TokenUsage:
        if not metadata:
            return TokenUsage()
        return self._normalize(metadata)

    def from_response_metadata(self, metadata: Optional[dict[str, Any]]) -> TokenUsage:
        if not metadata:
            return TokenUsage()
        return self._normalize(metadata)

    def from_ai_message(self, message: Any) -> TokenUsage:
        """
        Preferred: ``usage_metadata`` (most LangChain providers).

        Fallback: resolve ``usage_path`` (e.g. ``response_metadata``) and map
        common provider keys into TokenUsage.
        """
        usage_metadata = getattr(message, "usage_metadata", None)
        response_metadata = getattr(message, "response_metadata", None) or {}

        if usage_metadata:
            return self._normalize(usage_metadata, response_metadata)

        resolved = _resolve_message_path(message, self.usage_path)
        if isinstance(resolved, dict) and resolved:
            return self._normalize(resolved, response_metadata)

        # usage_path pointed at a leaf (e.g. response_metadata.eval_count) —
        # normalize the whole response_metadata container instead.
        if response_metadata:
            return self._normalize(response_metadata)

        return TokenUsage()

    # ------------------------------------------------------------------
    # Internals
    # ------------------------------------------------------------------

    def _normalize(
        self,
        meta: dict[str, Any],
        response_metadata: Optional[dict[str, Any]] = None,
    ) -> TokenUsage:
        source = _flatten_usage_dict(meta)
        extras = _flatten_usage_dict(response_metadata or {})

        input_tokens = _as_int(
            source.get("input_tokens")
            or source.get("prompt_tokens")
            or source.get("prompt_eval_count")
        )
        output_tokens = _as_int(
            source.get("output_tokens")
            or source.get("completion_tokens")
            or source.get("eval_count")
        )
        total_tokens = _as_int(source.get("total_tokens"))
        if total_tokens == 0 and (input_tokens or output_tokens):
            total_tokens = input_tokens + output_tokens

        model_name = (
            source.get("model_name")
            or source.get("model")
            or source.get("model_id")
            or extras.get("model_name")
            or extras.get("model")
            or extras.get("model_id")
        )
        if model_name is not None:
            model_name = str(model_name).strip() or None

        return TokenUsage(
            input_tokens=input_tokens,
            output_tokens=output_tokens,
            total_tokens=total_tokens,
            model_name=model_name,
            input_cost=_as_float(
                source.get("input_cost") or source.get("prompt_cost") or extras.get("input_cost")
            ),
            output_cost=_as_float(
                source.get("output_cost")
                or source.get("completion_cost")
                or extras.get("output_cost")
            ),
            total_cost=_as_float(
                source.get("total_cost") or source.get("cost") or extras.get("total_cost")
            ),
        )
