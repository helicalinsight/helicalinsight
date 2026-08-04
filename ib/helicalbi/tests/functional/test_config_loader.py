"""Functional tests for ``helicalbi.core.ConfigLoader``."""
import os

import pytest

from helicalbi.core.ConfigLoader import ConfigLoader


pytestmark = pytest.mark.functional


class TestLoadConfig:
    def test_loads_llm_config(self):
        config = ConfigLoader.load_config("llm_config.yaml")
        assert "default_provider" in config
        assert "providers" in config
        assert "ollama" in config["providers"]
        assert "package" in config["providers"]["ollama"]
        assert "parameters" in config["providers"]["openai"]
        assert config["providers"]["openai"]["usage_path"] == "usage_metadata"
        assert config["providers"]["ollama"]["usage_path"] == "response_metadata"

    def test_loads_raw_preserves_env_placeholders(self):
        raw = ConfigLoader.load_raw_config("llm_config.yaml")
        assert raw["providers"]["openai"]["parameters"]["api_key"] == "${OPENAI_API_KEY}"

    def test_save_config_round_trip(self, tmp_path, monkeypatch):
        target = tmp_path / "roundtrip.yaml"
        monkeypatch.setattr(
            ConfigLoader,
            "resolve_path",
            staticmethod(lambda path="llm_config.yaml": str(target)),
        )
        payload = {"default_provider": "openai", "providers": {"openai": {"model": "gpt-4o"}}}
        ConfigLoader.save_config("roundtrip.yaml", payload)
        loaded = ConfigLoader.load_raw_config("roundtrip.yaml")
        assert loaded == payload

    def test_loads_memory_config(self):
        config = ConfigLoader.load_config("memory_provider.yaml")
        assert "memory" in config
        assert "provider" in config["memory"]

    def test_missing_file_raises(self):
        with pytest.raises(FileNotFoundError):
            ConfigLoader.load_config("does_not_exist.yaml")


class TestResolveEnv:
    def test_resolves_env_variables(self, monkeypatch):
        monkeypatch.setenv("HELICAL_TEST_KEY", "secret-value")
        result = ConfigLoader._resolve_env({"key": "${HELICAL_TEST_KEY}"})
        assert result == {"key": "secret-value"}

    def test_unresolved_env_returns_none(self, monkeypatch):
        monkeypatch.delenv("UNSET_HELICAL_VAR", raising=False)
        result = ConfigLoader._resolve_env({"key": "${UNSET_HELICAL_VAR}"})
        assert result == {"key": None}

    def test_passes_through_plain_values(self):
        result = ConfigLoader._resolve_env({"k": "v", "n": 1, "b": True})
        assert result == {"k": "v", "n": 1, "b": True}

    def test_recurses_into_lists(self, monkeypatch):
        monkeypatch.setenv("HK", "hello")
        result = ConfigLoader._resolve_env(["${HK}", "plain"])
        assert result == ["hello", "plain"]

    def test_recurses_into_nested_dicts(self, monkeypatch):
        monkeypatch.setenv("DEEP", "val")
        result = ConfigLoader._resolve_env({"outer": {"inner": "${DEEP}"}})
        assert result == {"outer": {"inner": "val"}}
