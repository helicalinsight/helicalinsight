import os
from typing import Any

import yaml


class ConfigLoader:

    @staticmethod
    def resolve_path(path="llm_config.yaml") -> str:
        base_dir = os.path.dirname(os.path.abspath(__file__))
        project_root = os.path.abspath(os.path.join(base_dir, ".."))
        return os.path.join(project_root, "config", path)

    @staticmethod
    def load_raw_config(path: str = "llm_config.yaml") -> dict:
        """Load YAML without resolving ``${ENV}`` placeholders."""
        config_path = ConfigLoader.resolve_path(path)
        with open(config_path, "r", encoding="utf-8") as f:
            raw = yaml.safe_load(f)
        return raw or {}

    @staticmethod
    def load_config(path="lm_config.yml"):
        return ConfigLoader._resolve_env(ConfigLoader.load_raw_config(path))

    @staticmethod
    def save_config(path: str, config: dict) -> str:
        """Persist *config* to the YAML file under ``helicalbi/config``.

        Returns the absolute path written. Callers that need ``${ENV}``
        placeholders preserved must pass the raw (unresolved) dict.
        """
        config_path = ConfigLoader.resolve_path(path)
        os.makedirs(os.path.dirname(config_path), exist_ok=True)
        with open(config_path, "w", encoding="utf-8") as f:
            yaml.safe_dump(
                config,
                f,
                default_flow_style=False,
                allow_unicode=True,
                sort_keys=False,
            )
        return config_path

    @staticmethod
    def _resolve_env(config: Any):
        if isinstance(config, dict):
            return {k: ConfigLoader._resolve_env(v) for k, v in config.items()}
        elif isinstance(config, list):
            return [ConfigLoader._resolve_env(i) for i in config]
        elif isinstance(config, str) and config.startswith("${") and config.endswith("}"):
            return os.getenv(config[2:-1])
        return config
