import logging

from langchain_community.chat_models import ChatOllama

from helicalbi.integration.BaseInterface import LLMFactory

logger = logging.getLogger(__name__)


class OllamaFactory(LLMFactory):

    def __init__(self, config):
        self.config = config

    def create_llm(self):
        base_url = self.config.get("base_url") or ""
        if not base_url.strip():
            logger.warning(
                "Ollama base_url is not configured in llm_config.yaml; LLM will be unavailable."
            )
            return None
        return ChatOllama(
            model=self.config["model"],
            base_url=base_url,
            temperature=self.config["temperature"],
        )