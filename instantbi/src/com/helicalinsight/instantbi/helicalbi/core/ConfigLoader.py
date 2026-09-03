import os
from typing import Any

from ruamel.yaml import YAML
from ruamel.yaml.comments import CommentedMap, CommentedSeq


class ConfigLoader:

    @staticmethod
    def resolve_path(path="llm_config.yaml") -> str:
        base_dir = os.path.dirname(os.path.abspath(__file__))
        project_root = os.path.abspath(os.path.join(base_dir, ".."))
        return os.path.join(project_root, "config", path)

    @staticmethod
    def _rt_yaml() -> YAML:
        yaml_rt = YAML()
        yaml_rt.preserve_quotes = True
        yaml_rt.default_flow_style = False
        yaml_rt.width = 4096
        yaml_rt.indent(mapping=2, sequence=4, offset=2)
        return yaml_rt

    @staticmethod
    def load_raw_config(path: str = "llm_config.yaml") -> dict:
        """Load YAML without resolving ``${ENV}`` placeholders."""
        config_path = ConfigLoader.resolve_path(path)
        with open(config_path, "r", encoding="utf-8") as f:
            raw = ConfigLoader._rt_yaml().load(f)
        return raw or CommentedMap()

    @staticmethod
    def load_config(path="lm_config.yml"):
        return ConfigLoader._resolve_env(ConfigLoader.load_raw_config(path))

    @staticmethod
    def save_config(path: str, config: dict) -> str:
        """Persist *config* to the YAML file under ``helicalbi/config``.

        Values are merged into the existing document so comments, quotes, and
        key order are kept. Returns the absolute path written. Callers that
        need ``${ENV}`` placeholders preserved must pass the raw (unresolved)
        dict.
        """
        config_path = ConfigLoader.resolve_path(path)
        os.makedirs(os.path.dirname(config_path), exist_ok=True)
        yaml_rt = ConfigLoader._rt_yaml()
        document = None
        if os.path.isfile(config_path):
            with open(config_path, "r", encoding="utf-8") as f:
                document = yaml_rt.load(f)
        if isinstance(document, dict) and isinstance(config, dict):
            ConfigLoader._sync_mapping(document, config)
            to_write = document
        else:
            to_write = config if config is not None else CommentedMap()
        with open(config_path, "w", encoding="utf-8") as f:
            yaml_rt.dump(to_write, f)
        return config_path

    @staticmethod
    def _as_yaml_value(value: Any) -> Any:
        if isinstance(value, dict) and not isinstance(value, CommentedMap):
            node = CommentedMap()
            for key, item in value.items():
                node[key] = ConfigLoader._as_yaml_value(item)
            return node
        if isinstance(value, list) and not isinstance(value, CommentedSeq):
            node = CommentedSeq()
            for item in value:
                node.append(ConfigLoader._as_yaml_value(item))
            return node
        return value

    @staticmethod
    def _sync_mapping(base: dict, updates: dict) -> None:
        for key in list(base.keys()):
            if key not in updates:
                del base[key]
        for key, value in updates.items():
            if key not in base:
                base[key] = ConfigLoader._as_yaml_value(value)
                continue
            current = base[key]
            if isinstance(current, dict) and isinstance(value, dict):
                ConfigLoader._sync_mapping(current, value)
            elif isinstance(current, list) and isinstance(value, list):
                ConfigLoader._sync_sequence(current, value)
            else:
                base[key] = ConfigLoader._as_yaml_value(value)

    @staticmethod
    def _sync_sequence(base: list, updates: list) -> None:
        same_maps = (
            len(base) == len(updates)
            and all(
                isinstance(current, dict) and isinstance(value, dict)
                for current, value in zip(base, updates)
            )
        )
        if same_maps:
            for current, value in zip(base, updates):
                ConfigLoader._sync_mapping(current, value)
            return
        del base[:]
        for item in updates:
            base.append(ConfigLoader._as_yaml_value(item))

    @staticmethod
    def _resolve_env(config: Any):
        if isinstance(config, dict):
            return {k: ConfigLoader._resolve_env(v) for k, v in config.items()}
        elif isinstance(config, list):
            return [ConfigLoader._resolve_env(i) for i in config]
        elif isinstance(config, str) and config.startswith("${") and config.endswith("}"):
            return os.getenv(config[2:-1])
        return config
