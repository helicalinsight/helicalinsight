"""Browser LLM run form writes provider, optional api key, and optional base URL."""
from __future__ import annotations

import threading
from http.client import HTTPConnection
from urllib.parse import urlencode

import pytest
import yaml

from helicalbi.core.ConfigLoader import ConfigLoader

pytestmark = pytest.mark.functional

_SAMPLE = """\
default_provider: openai
base_url: http://hiee:8080/hi-ee
providers:
  openai:
    package: langchain-openai
    model: gpt-4.1-mini
    parameters:
      temperature: 0.1
      api_key: ${OPENAI_API_KEY}
    usage_path: usage_metadata
  deepseek:
    package: langchain-deepseek
    model: deepseek-chat
    parameters:
      temperature: 0.1
      api_key: ${DEEPSEEK_API_KEY}
    usage_path: usage_metadata
  ollama:
    package: langchain-ollama
    model: deepseek-coder-v2
    parameters:
      temperature: 0.1
      base_url: http://localhost:11434
    usage_path: response_metadata
"""


@pytest.fixture
def llm_config_file(tmp_path, monkeypatch):
    target = tmp_path / "llm_config.yaml"
    target.write_text(_SAMPLE, encoding="utf-8")
    monkeypatch.setattr(
        ConfigLoader,
        "resolve_path",
        staticmethod(lambda path="llm_config.yaml": str(target)),
    )
    return target


def test_apply_sets_provider_and_leaves_optional_fields(llm_config_file):
    from tests.llm.llm_config_prompt import apply_llm_run_config

    apply_llm_run_config("deepseek", api_key="", base_url="")

    saved = yaml.safe_load(llm_config_file.read_text(encoding="utf-8"))
    assert saved["default_provider"] == "deepseek"
    assert saved["providers"]["deepseek"]["parameters"]["api_key"] == "${DEEPSEEK_API_KEY}"
    assert "base_url" not in saved["providers"]["deepseek"]["parameters"]
    assert saved["base_url"] == "http://hiee:8080/hi-ee"


def test_apply_writes_optional_api_key_and_provider_base_url(llm_config_file):
    from tests.llm.llm_config_prompt import apply_llm_run_config

    apply_llm_run_config(
        "ollama",
        api_key="sk-from-browser",
        base_url="http://127.0.0.1:11434",
    )

    saved = yaml.safe_load(llm_config_file.read_text(encoding="utf-8"))
    assert saved["default_provider"] == "ollama"
    params = saved["providers"]["ollama"]["parameters"]
    assert params["api_key"] == "sk-from-browser"
    assert params["base_url"] == "http://127.0.0.1:11434"
    assert params["temperature"] == 0.1


def test_apply_rejects_unknown_provider(llm_config_file):
    from tests.llm.llm_config_prompt import apply_llm_run_config

    with pytest.raises(ValueError, match="not in llm_config"):
        apply_llm_run_config("missing-provider")
    saved = yaml.safe_load(llm_config_file.read_text(encoding="utf-8"))
    assert saved["default_provider"] == "openai"


def test_form_post_updates_llm_config(llm_config_file):
    from tests.llm.llm_config_prompt import LlmConfigPrompt

    prompt = LlmConfigPrompt()
    thread = threading.Thread(target=prompt.serve, kwargs={"open_browser": False}, daemon=True)
    thread.start()
    prompt.wait_until_listening()

    connection = HTTPConnection("127.0.0.1", prompt.port, timeout=5)
    connection.request("GET", "/")
    page = connection.getresponse().read().decode("utf-8")
    assert "Provider" in page
    assert "API key" in page
    assert "Base URL" in page

    body = urlencode(
        {
            "provider": "deepseek",
            "api_key": "sk-posted",
            "base_url": "https://api.deepseek.com",
        }
    )
    connection.request(
        "POST",
        "/",
        body=body,
        headers={"Content-Type": "application/x-www-form-urlencoded"},
    )
    response = connection.getresponse()
    assert response.status == 200
    saved = yaml.safe_load(llm_config_file.read_text(encoding="utf-8"))
    assert saved["default_provider"] == "deepseek"
    assert saved["providers"]["deepseek"]["parameters"]["api_key"] == "sk-posted"
    assert saved["providers"]["deepseek"]["parameters"]["base_url"] == "https://api.deepseek.com"
    prompt.close()
