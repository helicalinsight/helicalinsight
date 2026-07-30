import logging

from langchain_openai import ChatOpenAI

from helicalbi.integration.BaseInterface import LLMFactory

logger = logging.getLogger(__name__)


class OpenAIFactory(LLMFactory):

    def __init__(self, config):
        self.config = config

    def create_llm(self):
        api_key = self.config.get("api_key") or ""
        if not api_key.strip():
            logger.warning(
                "OpenAI api_key is not configured in llm_config.yaml; LLM will be unavailable."
            )
            return None
        return ChatOpenAI(
            model=self.config["model"],
            temperature=self.config["temperature"],
            max_tokens=self.config.get("max_tokens"),
            api_key=api_key,
        )
