import logging

from langchain_google_genai import ChatGoogleGenerativeAI

from helicalbi.integration.BaseInterface import LLMFactory

logger = logging.getLogger(__name__)


class GeminiFactory(LLMFactory):

    def __init__(self, config):
        self.config = config

    def create_llm(self):
        api_key = self.config.get("api_key") or ""
        if not api_key.strip():
            logger.warning(
                "Gemini api_key is not configured in llm_config.yaml; LLM will be unavailable."
            )
            return None
        return ChatGoogleGenerativeAI(
            model=self.config["model"],
            temperature=self.config["temperature"],
            max_tokens=self.config.get("max_tokens"),
            google_api_key=api_key,
        )
