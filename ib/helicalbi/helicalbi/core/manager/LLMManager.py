from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.core.LLMFactoryResolver import LLMFactoryResolver
from helicalbi.core.TokenUsageFactoryResolver import TokenUsageFactoryResolver
from helicalbi.integration.TokenUsageFactory import TokenUsageFactory


class LLMManager:

    def __init__(self, config_path="llm_config.yaml"):
        self.config = ConfigLoader.load_config(config_path)

    def get_llm(self, provider=None):
        provider = provider or self.config["default_provider"]
        provider_config = self.config["providers"][provider]
        factory = LLMFactoryResolver.get_factory(provider, provider_config)
        return factory.create_llm()

    def get_token_usage_factory(self, provider=None) -> TokenUsageFactory:
        provider = provider or self.config["default_provider"]
        return TokenUsageFactoryResolver.get_factory(provider)

    def get_baseUrl(self):
        return self.config["base_url"]
