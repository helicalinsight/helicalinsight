import logging
import threading

from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.core.ConfigWatcher import ConfigWatcher
from helicalbi.core.llm_loader import create_llm
from helicalbi.integration.TokenUsageExtractor import TokenUsageExtractor

logger = logging.getLogger(__name__)


class LLMManager:

    def __init__(self, config_path="llm_config.yaml"):
        self._config_path = config_path
        self._lock = threading.RLock()
        self.config = self._load()
        self._cached_llm = None
        self._cached_provider = None
        ConfigWatcher(config_path, self._on_config_changed).start()

    # ------------------------------------------------------------------
    # Internal helpers
    # ------------------------------------------------------------------

    def _load(self) -> dict:
        return ConfigLoader.load_config(self._config_path)

    def _on_config_changed(self) -> None:
        with self._lock:
            logger.info("llm_config.yaml changed on disk — reloading configuration.")
            try:
                self.config = self._load()
            except Exception:
                logger.exception("Failed to reload llm_config.yaml; keeping previous config.")
                return
            self._cached_llm = None
            self._cached_provider = None
        logger.info("LLM configuration reloaded successfully.")

    # ------------------------------------------------------------------
    # Public API
    # ------------------------------------------------------------------

    def get_llm(self, provider=None):
        with self._lock:
            effective_provider = provider or self.config.get("default_provider")
            if not effective_provider:
                logger.warning("No LLM provider configured; returning None.")
                return None

            if self._cached_llm is not None and self._cached_provider == effective_provider:
                return self._cached_llm

            provider_config = self.config.get("providers", {}).get(effective_provider)
            if not provider_config:
                logger.warning(
                    "Provider '%s' not found in config; returning None.", effective_provider
                )
                return None

            try:
                llm = create_llm(effective_provider, provider_config)
                if llm is not None:
                    self._cached_llm = llm
                    self._cached_provider = effective_provider
                return llm
            except Exception:
                logger.exception(
                    "Failed to create LLM for provider '%s'; returning None.", effective_provider
                )
                return None

    def get_token_usage_extractor(self, provider=None) -> TokenUsageExtractor:
        with self._lock:
            effective_provider = provider or self.config.get("default_provider")
            config = self.config
        return TokenUsageExtractor.from_config(provider=effective_provider, llm_config=config)

    # Backward-compatible alias
    get_token_usage_factory = get_token_usage_extractor

    def get_baseUrl(self):
        with self._lock:
            return self.config.get("base_url")

    def reload_from_disk(self) -> None:
        """Reload ``llm_config.yaml`` and drop the cached chat model."""
        with self._lock:
            try:
                self.config = self._load()
            except Exception:
                logger.exception(
                    "Failed to reload llm_config.yaml; keeping previous config."
                )
                raise
            self._cached_llm = None
            self._cached_provider = None
        logger.info("LLM configuration reloaded from disk.")
