import logging
import os
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
        self._loaded_mtime = None
        self._last_error = None
        self.config = self._load()
        self._cached_llm = None
        self._cached_provider = None
        ConfigWatcher(config_path, self._on_config_changed).start()

    # ------------------------------------------------------------------
    # Internal helpers
    # ------------------------------------------------------------------

    def _resolved_path(self) -> str:
        return ConfigLoader.resolve_path(self._config_path)

    def _config_mtime(self) -> float:
        try:
            return os.path.getmtime(self._resolved_path())
        except OSError:
            return -1.0

    def _load(self) -> dict:
        self._loaded_mtime = self._config_mtime()
        return ConfigLoader.load_config(self._config_path)

    def _reload_config(self) -> bool:
        """Reload YAML into memory and drop the cached model. Return True on success."""
        try:
            self.config = self._load()
        except Exception:
            logger.exception("Failed to reload llm_config.yaml; keeping previous config.")
            return False
        self._cached_llm = None
        self._cached_provider = None
        return True

    def _reload_if_stale(self) -> None:
        """Pick up disk writes that inotify missed (Docker bind mounts)."""
        if self._config_mtime() != self._loaded_mtime:
            logger.info("llm_config.yaml changed on disk — reloading configuration.")
            if self._reload_config():
                logger.info("LLM configuration reloaded successfully.")

    def _on_config_changed(self) -> None:
        with self._lock:
            logger.info("llm_config.yaml changed on disk — reloading configuration.")
            if self._reload_config():
                logger.info("LLM configuration reloaded successfully.")

    # ------------------------------------------------------------------
    # Public API
    # ------------------------------------------------------------------

    def get_llm(self, provider=None):
        with self._lock:
            self._reload_if_stale()
            effective_provider = provider or self.config.get("default_provider")
            if not effective_provider:
                self._last_error = "No LLM provider configured."
                logger.warning("No LLM provider configured; returning None.")
                return None

            if self._cached_llm is not None and self._cached_provider == effective_provider:
                return self._cached_llm

            provider_config = self.config.get("providers", {}).get(effective_provider)
            if not provider_config:
                self._last_error = f"Provider '{effective_provider}' not found in config."
                logger.warning(
                    "Provider '%s' not found in config; returning None.", effective_provider
                )
                return None

            try:
                llm = create_llm(effective_provider, provider_config)
                if llm is not None:
                    self._cached_llm = llm
                    self._cached_provider = effective_provider
                    self._last_error = None
                else:
                    self._last_error = (
                        f"Provider '{effective_provider}' is missing credentials "
                        "(api_key / base_url)."
                    )
                return llm
            except Exception as exc:
                self._last_error = f"Failed to create LLM for provider '{effective_provider}': {exc}"
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
            self._last_error = None
        logger.info("LLM configuration reloaded from disk.")
