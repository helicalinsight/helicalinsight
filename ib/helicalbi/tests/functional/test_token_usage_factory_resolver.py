"""Functional tests for ``helicalbi.core.TokenUsageFactoryResolver``."""
import pytest

from helicalbi.core.TokenUsageFactoryResolver import TokenUsageFactoryResolver
from helicalbi.integration.anthropic.AnthropicTokenUsageFactory import AnthropicTokenUsageFactory
from helicalbi.integration.gemeni.GeminiTokenUsageFactory import GeminiTokenUsageFactory
from helicalbi.integration.ollama.OllamaTokenUsageFactory import OllamaTokenUsageFactory
from helicalbi.integration.openai.OpenAITokenUsageFactory import OpenAITokenUsageFactory


pytestmark = pytest.mark.functional


class TestTokenUsageFactoryResolver:
    def test_resolves_openai(self):
        factory = TokenUsageFactoryResolver.get_factory("openai")
        assert isinstance(factory, OpenAITokenUsageFactory)

    def test_resolves_anthropic(self):
        factory = TokenUsageFactoryResolver.get_factory("anthropic")
        assert isinstance(factory, AnthropicTokenUsageFactory)

    def test_resolves_ollama(self):
        factory = TokenUsageFactoryResolver.get_factory("ollama")
        assert isinstance(factory, OllamaTokenUsageFactory)

    def test_resolves_gemini(self):
        factory = TokenUsageFactoryResolver.get_factory("gemini")
        assert isinstance(factory, GeminiTokenUsageFactory)

    def test_unknown_provider_raises(self):
        with pytest.raises(ValueError, match="Unsupported provider"):
            TokenUsageFactoryResolver.get_factory("unknown")
