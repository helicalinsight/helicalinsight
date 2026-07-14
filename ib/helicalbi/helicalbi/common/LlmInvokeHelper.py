from __future__ import annotations

import json
import logging
import time
from typing import Any, Optional

from helicalbi.common import app_config
from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.core.TokenUsageFactoryResolver import TokenUsageFactoryResolver
from helicalbi.integration.TokenUsageFactory import TokenUsageFactory
from helicalbi.model.TimeConsumed import TimeConsumed
from helicalbi.model.TokenUsage import TokenUsage

logger = logging.getLogger(__name__)

_BORDER = "--------------"


def _should_log_llm_activity() -> bool:
    return app_config.show_llm_activity


def _format_prompt_inputs(prompt: Any, inputs: Any) -> str:
    if isinstance(inputs, str):
        return inputs
    if isinstance(inputs, list):
        lines = []
        for item in inputs:
            if hasattr(item, "content"):
                role = getattr(item, "type", None) or item.__class__.__name__
                lines.append(f"[{role}]\n{item.content}")
            else:
                lines.append(str(item))
        return "\n\n".join(lines)
    if isinstance(inputs, dict) and prompt is not None:
        if hasattr(prompt, "format_prompt"):
            try:
                return prompt.format_prompt(**inputs).to_string()
            except (KeyError, TypeError, ValueError):
                pass
        if hasattr(prompt, "format"):
            try:
                return prompt.format(**inputs)
            except (KeyError, TypeError, ValueError):
                pass
    if isinstance(inputs, dict):
        return json.dumps(inputs, indent=2, default=str)
    return str(inputs)


def log_prompt(prompt_or_text: Any, inputs: Any = None) -> None:
    """Log the rendered LLM prompt at INFO level when enabled in config."""
    if not _should_log_llm_activity():
        return
    if inputs is None:
        prompt_text = _format_prompt_inputs(None, prompt_or_text)
    else:
        prompt_text = _format_prompt_inputs(prompt_or_text, inputs)
    logger.info(
        "%s\nLLM Prompt:\n%s\n%s",
        _BORDER,
        prompt_text,
        _BORDER,
    )


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
    if _should_log_llm_activity():
        logger.info(
            "Accumulated token usage: input=%d output=%d total=%d",
            accumulated.input_tokens,
            accumulated.output_tokens,
            accumulated.total_tokens,
        )


def read_time_consumed(state: dict) -> TimeConsumed:
    """Read accumulated timing from agent state."""
    raw = state.get("time_consumed")
    if not raw:
        return TimeConsumed()
    if isinstance(raw, TimeConsumed):
        return raw
    return TimeConsumed.model_validate(raw)


def merge_time_consumed(state: dict, consumed: TimeConsumed) -> None:
    """Add elapsed time from an LLM call into agent state."""
    accumulated = read_time_consumed(state) + consumed
    state["time_consumed"] = accumulated.model_dump(exclude_none=True)
    if _should_log_llm_activity():
        logger.info(
            "Accumulated LLM time: %.3fs (total request: %s)",
            accumulated.llm_seconds,
            f"{accumulated.total_seconds:.3f}s" if accumulated.total_seconds is not None else "n/a",
        )


def set_total_time_consumed(state: dict, total_seconds: float) -> None:
    """Record end-to-end request duration on agent state."""
    consumed = read_time_consumed(state)
    consumed.total_seconds = round(total_seconds, 3)
    state["time_consumed"] = consumed.model_dump(exclude_none=True)


def _record_invoke_metrics(state: Optional[dict], usage: TokenUsage, elapsed_seconds: float) -> None:
    if state is None:
        return
    merge_token_usage(state, usage)
    merge_time_consumed(state, TimeConsumed(llm_seconds=round(elapsed_seconds, 3)))


def invoke_structured(
    prompt: Any,
    llm: Any,
    parser: Any,
    inputs: dict,
    provider: Optional[str] = None,
    state: Optional[dict] = None,
) -> tuple[Any, TokenUsage]:
    """Run ``prompt | llm | parser`` and return the parsed result plus token usage."""
    log_prompt(prompt, inputs)
    started = time.perf_counter()
    ai_message = (prompt | llm).invoke(inputs)
    elapsed = time.perf_counter() - started
    usage = get_token_usage_factory(provider).from_ai_message(ai_message)
    _record_invoke_metrics(state, usage, elapsed)
    if _should_log_llm_activity():
        logger.info(
            "LLM invoke (structured): input=%d output=%d total=%d elapsed=%.3fs",
            usage.input_tokens,
            usage.output_tokens,
            usage.total_tokens,
            elapsed,
        )
    parsed = parser.invoke(ai_message)
    return parsed, usage


def invoke_llm(
    llm: Any,
    inputs: Any,
    provider: Optional[str] = None,
    state: Optional[dict] = None,
) -> tuple[Any, TokenUsage]:
    """Invoke an LLM directly and return the response plus token usage."""
    log_prompt(inputs)
    started = time.perf_counter()
    ai_message = llm.invoke(inputs)
    elapsed = time.perf_counter() - started
    usage = get_token_usage_factory(provider).from_ai_message(ai_message)
    _record_invoke_metrics(state, usage, elapsed)
    if _should_log_llm_activity():
        logger.info(
            "LLM invoke: input=%d output=%d total=%d elapsed=%.3fs",
            usage.input_tokens,
            usage.output_tokens,
            usage.total_tokens,
            elapsed,
        )
    return ai_message, usage
