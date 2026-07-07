from helicalbi.integration.BaseInterface import LLMFactory
from helicalbi.integration.anthropic.AnthropicFactory import AnthropicFactory
from helicalbi.integration.gemeni.GeminiFactory import GeminiFactory
from helicalbi.integration.ollama.OllamaFactory import OllamaFactory
from helicalbi.integration.openai.OpenAIFactory import OpenAIFactory


class LLMFactoryResolver:

    @staticmethod
    def get_factory(provider: str, config: dict) -> LLMFactory:
        if provider == "openai":
            return OpenAIFactory(config)
        elif provider == "anthropic":
            return AnthropicFactory(config)
        elif provider == "ollama":
            return OllamaFactory(config)
        elif provider == "gemini":
            return GeminiFactory(config)
        else:
            raise ValueError(f"Unsupported provider: {provider}")