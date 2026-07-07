from langchain_community.chat_models import ChatOllama

from helicalbi.integration.BaseInterface import LLMFactory


class OllamaFactory(LLMFactory):

    def __init__(self, config):
        self.config = config

    def create_llm(self):
        return ChatOllama(
            model=self.config["model"],
            base_url=self.config["base_url"],
            temperature=self.config["temperature"]
        )