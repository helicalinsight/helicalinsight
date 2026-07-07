from langchain_anthropic import ChatAnthropic

from helicalbi.integration.BaseInterface import LLMFactory


class AnthropicFactory(LLMFactory):

    def __init__(self, config):
        self.config = config

    def create_llm(self):
        return ChatAnthropic(
            model=self.config["model"],
            temperature=self.config["temperature"],
            max_tokens=self.config.get("max_tokens"),
            api_key=self.config["api_key"]
        )