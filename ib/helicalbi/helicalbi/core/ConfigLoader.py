import os
import yaml

class ConfigLoader:

    @staticmethod
    def load_config(path="lm_config.yml"):
        # Get current file directory (core/)
        base_dir = os.path.dirname(os.path.abspath(__file__))

        # Go one level up → project/
        project_root = os.path.abspath(os.path.join(base_dir, ".."))

        # Build full path → project/config/llm_config.yaml
        config_path = os.path.join(project_root, "config", path)

        with open(config_path, "r") as f:
            raw = yaml.safe_load(f)
        return ConfigLoader._resolve_env(raw)

    @staticmethod
    def _resolve_env(config):
        if isinstance(config, dict):
            return {k: ConfigLoader._resolve_env(v) for k, v in config.items()}
        elif isinstance(config, list):
            return [ConfigLoader._resolve_env(i) for i in config]
        elif isinstance(config, str) and config.startswith("${"):
            return os.getenv(config[2:-1])
        return config