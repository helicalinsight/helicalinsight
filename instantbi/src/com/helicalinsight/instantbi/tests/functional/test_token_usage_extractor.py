"""Functional tests for ``helicalbi.integration.TokenUsageExtractor``."""
from __future__ import annotations

from typing import Optional

import pytest

from helicalbi.integration.TokenUsageExtractor import TokenUsageExtractor


pytestmark = pytest.mark.functional


def _extractor(provider: Optional[str] = None) -> TokenUsageExtractor:
    return TokenUsageExtractor.from_config(provider=provider)


class TestTokenUsageExtractorConfig:
    def test_loads_usage_path_from_provider(self):
        extractor = _extractor("openai")
        assert extractor.provider == "openai"
        assert extractor.usage_path == "usage_metadata"

    def test_ollama_uses_response_metadata_path(self):
        extractor = _extractor("ollama")
        assert extractor.usage_path == "response_metadata"

    def test_reads_usage_path_under_provider_in_llm_config(self):
        extractor = TokenUsageExtractor.from_config(
            llm_config={
                "default_provider": "custom",
                "providers": {
                    "custom": {"usage_path": "response_metadata.token_usage"},
                },
            },
            provider="custom",
        )
        assert extractor.usage_path == "response_metadata.token_usage"


class TestTokenUsageExtractorParsing:
    def test_from_usage_metadata(self):
        usage = _extractor("openai").from_usage_metadata(
            {
                "input_tokens": 100,
                "output_tokens": 25,
                "total_tokens": 125,
                "model_name": "gpt-4o-mini",
            }
        )
        assert usage.input_tokens == 100
        assert usage.output_tokens == 25
        assert usage.total_tokens == 125
        assert usage.model_name == "gpt-4o-mini"

    def test_response_metadata_openai_nested_token_usage(self):
        usage = _extractor("openai").from_response_metadata(
            {
                "model_name": "gpt-4o",
                "token_usage": {
                    "prompt_tokens": 50,
                    "completion_tokens": 10,
                    "total_tokens": 60,
                },
            }
        )
        assert usage.input_tokens == 50
        assert usage.output_tokens == 10
        assert usage.total_tokens == 60
        assert usage.model_name == "gpt-4o"

    def test_response_metadata_ollama_shape(self):
        usage = _extractor("ollama").from_response_metadata(
            {
                "model": "deepseek-coder-v2",
                "prompt_eval_count": 559,
                "eval_count": 95,
                "done": True,
            }
        )
        assert usage.input_tokens == 559
        assert usage.output_tokens == 95
        assert usage.total_tokens == 654
        assert usage.model_name == "deepseek-coder-v2"

    def test_response_metadata_anthropic_nested_usage(self):
        usage = _extractor("anthropic").from_response_metadata(
            {
                "model": "claude-opus-4-6",
                "usage": {"input_tokens": 120, "output_tokens": 30},
            }
        )
        assert usage.input_tokens == 120
        assert usage.output_tokens == 30
        assert usage.total_tokens == 150
        assert usage.model_name == "claude-opus-4-6"

    def test_prefers_usage_metadata_on_ai_message(self):
        class _Msg:
            usage_metadata = {
                "input_tokens": 15,
                "output_tokens": 109,
                "total_tokens": 124,
            }
            response_metadata = {
                "token_usage": {
                    "prompt_tokens": 1,
                    "completion_tokens": 2,
                    "total_tokens": 3,
                },
                "model_name": "gpt-4o-mini-2024-07-18",
            }

        usage = _extractor("openai").from_ai_message(_Msg())
        assert usage.input_tokens == 15
        assert usage.output_tokens == 109
        assert usage.total_tokens == 124
        assert usage.model_name == "gpt-4o-mini-2024-07-18"

    def test_falls_back_to_response_metadata_when_no_usage_metadata(self):
        class _Msg:
            usage_metadata = None
            response_metadata = {
                "model": "deepseek-coder-v2",
                "prompt_eval_count": 559,
                "eval_count": 95,
            }

        usage = _extractor("ollama").from_ai_message(_Msg())
        assert usage.input_tokens == 559
        assert usage.output_tokens == 95
        assert usage.total_tokens == 654
        assert usage.model_name == "deepseek-coder-v2"

    def test_usage_path_into_nested_response_metadata(self):
        class _Msg:
            usage_metadata = None
            response_metadata = {
                "token_usage": {
                    "prompt_tokens": 7,
                    "completion_tokens": 3,
                    "total_tokens": 10,
                },
                "model_name": "demo",
            }

        usage = TokenUsageExtractor.from_usage_path(
            "response_metadata.token_usage"
        ).from_ai_message(_Msg())
        assert usage.input_tokens == 7
        assert usage.output_tokens == 3
        assert usage.total_tokens == 10
        assert usage.model_name == "demo"
