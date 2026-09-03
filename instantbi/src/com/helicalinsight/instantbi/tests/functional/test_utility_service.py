"""Functional tests for the runtime configuration utility service."""

from __future__ import annotations

from copy import deepcopy
from unittest.mock import MagicMock

import pytest
import yaml

from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.service.utility.exception.UtilityError import UtilityError
from helicalbi.service.utility.provider.LlmConfigProvider import LlmConfigProvider
from helicalbi.service.utility.provider.ModelCatalogProvider import ModelCatalogProvider
from helicalbi.service.utility.provider._helpers import deep_merge, mask_secrets


pytestmark = pytest.mark.functional


class TestHelpers:
    def test_mask_secrets_redacts_api_keys(self):
        data = {"parameters": {"api_key": "sk-secret", "temperature": 0.1}}
        assert mask_secrets(data)["parameters"]["api_key"] == "***"
        assert mask_secrets(data)["parameters"]["temperature"] == 0.1

    def test_mask_secrets_keeps_env_placeholders(self):
        data = {"api_key": "${OPENAI_API_KEY}"}
        assert mask_secrets(data)["api_key"] == "${OPENAI_API_KEY}"

    def test_deep_merge(self):
        base = {"logging": {"level": "INFO", "file": "a.log"}, "sql": {"default_limit": 10}}
        updates = {"logging": {"level": "ERROR"}}
        merged = deep_merge(base, updates)
        assert merged["logging"]["level"] == "ERROR"
        assert merged["logging"]["file"] == "a.log"
        assert merged["sql"]["default_limit"] == 10


class TestModelCatalogProvider:
    def test_lists_models_for_openai_package(self):
        models = ModelCatalogProvider().list_models_for_package("langchain-openai")
        assert "gpt-4.1-mini" in models

    def test_unknown_package_returns_empty(self):
        models = ModelCatalogProvider().list_models_for_package(
            "langchain-unknown-xyz"
        )
        assert models == []
        assert not ModelCatalogProvider().has_package("langchain-unknown-xyz")

    def test_lists_models_for_vertexai_package(self):
        models = ModelCatalogProvider().list_models_for_package(
            "langchain-google-vertexai"
        )
        assert "gemini-2.5-flash" in models
        assert ModelCatalogProvider().has_package("langchain-google-vertexai")


class TestLlmConfigProvider:
    def test_change_model_persists_and_reloads(self, tmp_path, monkeypatch):
        config_path = tmp_path / "llm_config.yaml"
        raw = {
            "default_provider": "openai",
            "base_url": "http://example/hi-ee",
            "providers": {
                "openai": {
                    "package": "langchain-openai",
                    "model": "gpt-4.1-mini",
                    "parameters": {"api_key": "${OPENAI_API_KEY}", "temperature": 0.1},
                    "usage_path": "usage_metadata",
                }
            },
        }
        config_path.write_text(yaml.safe_dump(raw), encoding="utf-8")

        monkeypatch.setattr(
            ConfigLoader,
            "resolve_path",
            staticmethod(lambda path="llm_config.yaml": str(config_path)),
        )

        manager = MagicMock()
        manager._lock = __import__("threading").RLock()
        manager.config = ConfigLoader._resolve_env(deepcopy(raw))
        manager.reload_from_disk = MagicMock(
            side_effect=lambda: setattr(
                manager, "config", ConfigLoader.load_config("llm_config.yaml")
            )
        )

        provider = LlmConfigProvider(manager, config_file="llm_config.yaml")
        result = provider.change_model("gpt-4o", provider="openai")

        assert result["model"] == "gpt-4o"
        persisted = yaml.safe_load(config_path.read_text(encoding="utf-8"))
        assert persisted["providers"]["openai"]["model"] == "gpt-4o"
        # Env placeholder must survive round-trip
        assert persisted["providers"]["openai"]["parameters"]["api_key"] == "${OPENAI_API_KEY}"
        manager.reload_from_disk.assert_called_once()

    def test_upsert_provider_adds_new(self, tmp_path, monkeypatch):
        config_path = tmp_path / "llm_config.yaml"
        raw = {
            "default_provider": "openai",
            "providers": {
                "openai": {
                    "package": "langchain-openai",
                    "model": "gpt-4.1-mini",
                    "parameters": {"api_key": "${OPENAI_API_KEY}"},
                }
            },
        }
        config_path.write_text(yaml.safe_dump(raw), encoding="utf-8")
        monkeypatch.setattr(
            ConfigLoader,
            "resolve_path",
            staticmethod(lambda path="llm_config.yaml": str(config_path)),
        )

        manager = MagicMock()
        manager._lock = __import__("threading").RLock()
        manager.config = deepcopy(raw)
        manager.reload_from_disk = MagicMock()

        provider = LlmConfigProvider(manager)
        provider.upsert_provider(
            "custom",
            package="langchain-openai",
            model="gpt-4o-mini",
            parameters={"api_key": "${OPENAI_API_KEY}"},
            set_as_default=True,
        )

        persisted = yaml.safe_load(config_path.read_text(encoding="utf-8"))
        assert "custom" in persisted["providers"]
        assert persisted["default_provider"] == "custom"

    def test_list_providers_does_not_redact_api_key(self, tmp_path, monkeypatch):
        config_path = tmp_path / "llm_config.yaml"
        raw = {
            "default_provider": "openai",
            "providers": {
                "openai": {
                    "package": "langchain-openai",
                    "model": "gpt-4.1-mini",
                    "parameters": {"api_key": "sk-secret", "temperature": 0.1},
                    "usage_path": "usage_metadata",
                }
            },
        }
        config_path.write_text(yaml.safe_dump(raw), encoding="utf-8")
        monkeypatch.setattr(
            ConfigLoader,
            "resolve_path",
            staticmethod(lambda path="llm_config.yaml": str(config_path)),
        )
        manager = MagicMock()
        manager._lock = __import__("threading").RLock()
        manager.config = deepcopy(raw)
        provider = LlmConfigProvider(manager)
        rows = provider.list_providers()
        openai = next(row for row in rows if row["provider"] == "openai")
        assert openai["parameters"]["api_key"] == "sk-secret"
        assert openai["parameters"]["temperature"] == 0.1

    def test_upsert_keeps_env_placeholder_when_resolved_secret_posted(
        self, tmp_path, monkeypatch
    ):
        config_path = tmp_path / "llm_config.yaml"
        raw = {
            "default_provider": "openai",
            "providers": {
                "openai": {
                    "package": "langchain-openai",
                    "model": "gpt-4.1-mini",
                    "parameters": {"api_key": "${OPENAI_API_KEY}"},
                }
            },
        }
        config_path.write_text(yaml.safe_dump(raw), encoding="utf-8")
        monkeypatch.setattr(
            ConfigLoader,
            "resolve_path",
            staticmethod(lambda path="llm_config.yaml": str(config_path)),
        )
        monkeypatch.setenv("OPENAI_API_KEY", "sk-resolved")
        manager = MagicMock()
        manager._lock = __import__("threading").RLock()
        manager.config = deepcopy(raw)
        manager.reload_from_disk = MagicMock()
        provider = LlmConfigProvider(manager)
        provider.upsert_provider(
            "openai",
            package="langchain-openai",
            model="gpt-4.1-mini",
            parameters={"api_key": "sk-resolved"},
            replace_parameters=True,
        )
        persisted = yaml.safe_load(config_path.read_text(encoding="utf-8"))
        assert persisted["providers"]["openai"]["parameters"]["api_key"] == "${OPENAI_API_KEY}"
