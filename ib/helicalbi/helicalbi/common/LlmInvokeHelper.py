from __future__ import annotations

import logging
from typing import Any, Optional

from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.core.TokenUsageFactoryResolver import TokenUsageFactoryResolver
from helicalbi.integration.TokenUsageFactory import TokenUsageFactory
from helicalbi.model.TokenUsage import TokenUsage

logger = logging.getLogger(__name__)


def get_token_usage_factory(provider: Optional[str] = None) -> TokenUsageFactory:
    """Return the token-usage factory for the configured (or given) LLM provider."""
    if not provider:
        config = ConfigLoader.load_config("llm_config.yaml")
        provider = config["default_provider"]
    return TokenUsageFactoryResolver.get_factory(provider)


def read_token_usage(state: dict) -> TokenUsage:
    """Read accumulated token usage from agent state."""
    raw = state.get("token_usage")
    if not raw:
        return TokenUsage()
    if isinstance(raw, TokenUsage):
        return raw
    return TokenUsage.model_validate(raw)


def merge_token_usage(state: dict, usage: TokenUsage) -> None:
    """Add token usage from an LLM call into agent state."""
    accumulated = read_token_usage(state) + usage
    state["token_usage"] = accumulated.model_dump(exclude_none=True)
    logger.info(
        "Accumulated token usage: input=%d output=%d total=%d",
        accumulated.input_tokens,
        accumulated.output_tokens,
        accumulated.total_tokens,
    )


def invoke_structured(
    prompt: Any,
    llm: Any,
    parser: Any,
    inputs: dict,
    provider: Optional[str] = None,
) -> tuple[Any, TokenUsage]:
    """Run ``prompt | llm | parser`` and return the parsed result plus token usage."""
    ai_message = (prompt | llm).invoke(inputs)
    usage = get_token_usage_factory(provider).from_ai_message(ai_message)
    logger.info(
        "LLM invoke (structured): input=%d output=%d total=%d",
        usage.input_tokens,
        usage.output_tokens,
        usage.total_tokens,
    )
    parsed = parser.invoke(ai_message)
    return parsed, usage


def invoke_llm(llm: Any, inputs: Any, provider: Optional[str] = None) -> tuple[Any, TokenUsage]:
    """Invoke an LLM directly and return the response plus token usage."""
    ai_message = llm.invoke(inputs)
    usage = get_token_usage_factory(provider).from_ai_message(ai_message)
    logger.info(
        "LLM invoke: input=%d output=%d total=%d",
        usage.input_tokens,
        usage.output_tokens,
        usage.total_tokens,
    )
    return ai_message, usage
