import logging
import threading

from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.core.ConfigWatcher import ConfigWatcher

logger = logging.getLogger(__name__)


class MemoryManager:

    def __init__(self, config_path="memory_provider.yaml"):
        self._config_path = config_path
        self._lock = threading.RLock()
        self.config = ConfigLoader.load_config(config_path)
        ConfigWatcher(config_path, self._on_config_changed).start()

    def _on_config_changed(self) -> None:
        logger.info("memory_provider.yaml changed on disk — reloading configuration.")
        try:
            new_config = ConfigLoader.load_config(self._config_path)
        except Exception:
            logger.exception("Failed to reload memory_provider.yaml; keeping previous config.")
            return
        with self._lock:
            self.config = new_config
        logger.info("Memory configuration reloaded successfully.")

    def get_config(self):
        with self._lock:
            return self.config["memory"]
