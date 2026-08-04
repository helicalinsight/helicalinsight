"""Functional tests for ``helicalbi.core.llm_loader``."""
from unittest.mock import MagicMock, patch

import pytest

from helicalbi.core import llm_loader


pytestmark = pytest.mark.functional


class TestEnsurePackage:
    def test_skips_install_when_module_present(self):
        with patch.object(llm_loader.importlib.util, "find_spec", return_value=object()):
            with patch.object(llm_loader.subprocess, "check_call") as check_call:
                llm_loader.ensure_package("langchain-openai")
                check_call.assert_not_called()

    def test_installs_when_module_missing(self):
        with patch.object(llm_loader.importlib.util, "find_spec", return_value=None):
            with patch.object(llm_loader.subprocess, "check_call") as check_call:
                llm_loader.ensure_package("langchain-openai")
                check_call.assert_called_once()
                args = check_call.call_args[0][0]
                assert args[-1] == "langchain-openai"


class TestCreateLlm:
    def test_missing_package_raises(self):
        with pytest.raises(ValueError, match="missing 'package'"):
            llm_loader.create_llm("openai", {"model": "gpt-4o-mini"})

    def test_missing_model_raises(self):
        with pytest.raises(ValueError, match="missing 'model'"):
            llm_loader.create_llm(
                "openai", {"package": "langchain-openai"}
            )

    def test_empty_api_key_returns_none(self):
        cfg = {
            "package": "langchain-openai",
            "model": "gpt-4.1-mini",
            "parameters": {"api_key": "", "temperature": 0},
        }
        with patch.object(llm_loader, "ensure_package"):
            assert llm_loader.create_llm("openai", cfg) is None

    def test_empty_ollama_base_url_returns_none(self):
        cfg = {
            "package": "langchain-ollama",
            "model": "llama3",
            "parameters": {"base_url": ""},
        }
        with patch.object(llm_loader, "ensure_package"):
            assert llm_loader.create_llm("ollama", cfg) is None

    def test_calls_init_chat_model_with_provider_prefix(self):
        cfg = {
            "package": "langchain-openai",
            "model": "gpt-4.1-mini",
            "parameters": {"api_key": "sk-test", "temperature": 0.1},
        }
        mock_llm = MagicMock()
        with patch.object(llm_loader, "ensure_package") as ensure:
            with patch.object(llm_loader, "init_chat_model", return_value=mock_llm) as init:
                result = llm_loader.create_llm("openai", cfg)
        ensure.assert_called_once_with("langchain-openai")
        init.assert_called_once_with(
            model="openai:gpt-4.1-mini",
            api_key="sk-test",
            temperature=0.1,
        )
        assert result is mock_llm

    def test_usage_path_is_not_passed_to_init_chat_model(self):
        cfg = {
            "package": "langchain-openai",
            "model": "gpt-4.1-mini",
            "parameters": {"api_key": "sk-test", "temperature": 0.1},
            "usage_path": "usage_metadata",
        }
        with patch.object(llm_loader, "ensure_package"):
            with patch.object(llm_loader, "init_chat_model", return_value=MagicMock()) as init:
                llm_loader.create_llm("openai", cfg)
        assert "usage_path" not in init.call_args.kwargs
        assert init.call_args.kwargs["api_key"] == "sk-test"

    def test_maps_gemini_to_google_genai(self):
        cfg = {
            "package": "langchain-google-genai",
            "model": "gemini-2.5-flash",
            "parameters": {"api_key": "g-key"},
        }
        with patch.object(llm_loader, "ensure_package"):
            with patch.object(llm_loader, "init_chat_model", return_value=MagicMock()) as init:
                llm_loader.create_llm("gemini", cfg)
        assert init.call_args.kwargs["model"] == "google_genai:gemini-2.5-flash"

    def test_maps_google_genai_hyphen_provider(self):
        cfg = {
            "package": "langchain-google-genai",
            "model": "gemini-2.5-flash",
            "parameters": {"api_key": "g-key"},
        }
        with patch.object(llm_loader, "ensure_package"):
            with patch.object(llm_loader, "init_chat_model", return_value=MagicMock()) as init:
                llm_loader.create_llm("google-genai", cfg)
        assert init.call_args.kwargs["model"] == "google_genai:gemini-2.5-flash"
