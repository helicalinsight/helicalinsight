"""Functional tests for ``helicalbi.core.LLMFactoryResolver``."""
import pytest

from helicalbi.core.LLMFactoryResolver import LLMFactoryResolver
from helicalbi.integration.anthropic.AnthropicFactory import AnthropicFactory
from helicalbi.integration.gemeni.GeminiFactory import GeminiFactory
from helicalbi.integration.ollama.OllamaFactory import OllamaFactory
from helicalbi.integration.openai.OpenAIFactory import OpenAIFactory


pytestmark = pytest.mark.functional


class TestLLMFactoryResolver:
    def test_resolves_openai(self):
        factory = LLMFactoryResolver.get_factory("openai", {"model": "gpt-4o-mini"})
        assert isinstance(factory, OpenAIFactory)

    def test_resolves_anthropic(self):
        factory = LLMFactoryResolver.get_factory(
            "anthropic", {"model": "claude-opus-4-6"}
        )
        assert isinstance(factory, AnthropicFactory)

    def test_resolves_ollama(self):
        factory = LLMFactoryResolver.get_factory(
            "ollama", {"model": "llama3", "base_url": "http://localhost:11434"}
        )
        assert isinstance(factory, OllamaFactory)

    def test_resolves_gemini(self):
        factory = LLMFactoryResolver.get_factory(
            "gemini", {"model": "gemini-2.5-flash"}
        )
        assert isinstance(factory, GeminiFactory)

    def test_unknown_provider_raises(self):
        with pytest.raises(ValueError, match="Unsupported provider"):
            LLMFactoryResolver.get_factory("unknown", {})
