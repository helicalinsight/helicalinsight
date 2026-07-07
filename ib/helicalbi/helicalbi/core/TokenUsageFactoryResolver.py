from helicalbi.integration.TokenUsageFactory import TokenUsageFactory
from helicalbi.integration.anthropic.AnthropicTokenUsageFactory import AnthropicTokenUsageFactory
from helicalbi.integration.gemeni.GeminiTokenUsageFactory import GeminiTokenUsageFactory
from helicalbi.integration.ollama.OllamaTokenUsageFactory import OllamaTokenUsageFactory
from helicalbi.integration.openai.OpenAITokenUsageFactory import OpenAITokenUsageFactory


class TokenUsageFactoryResolver:

    _FACTORIES: dict[str, type[TokenUsageFactory]] = {
        "openai": OpenAITokenUsageFactory,
        "anthropic": AnthropicTokenUsageFactory,
        "ollama": OllamaTokenUsageFactory,
        "gemini": GeminiTokenUsageFactory,
    }

    @classmethod
    def get_factory(cls, provider: str) -> TokenUsageFactory:
        factory_cls = cls._FACTORIES.get(provider)
        if factory_cls is None:
            raise ValueError(f"Unsupported provider: {provider}")
        return factory_cls()
