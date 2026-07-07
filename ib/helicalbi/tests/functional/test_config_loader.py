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
