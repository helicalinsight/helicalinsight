from langchain_google_genai import ChatGoogleGenerativeAI

from helicalbi.integration.BaseInterface import LLMFactory


class GeminiFactory(LLMFactory):

    def __init__(self, config):
        self.config = config

    def create_llm(self):
        return ChatGoogleGenerativeAI(
            model=self.config["model"],
            temperature=self.config["temperature"],
            max_tokens=self.config.get("max_tokens"),
            google_api_key=self.config["api_key"]
        )
