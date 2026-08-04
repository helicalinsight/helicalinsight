"""
YAML-driven LLM loader using LangChain ``init_chat_model``.

Add or switch providers by editing ``llm_config.yaml`` only — no factory
classes or provider if/else. Missing provider packages are installed on demand.
"""

from __future__ import annotations

import importlib.util
import logging
import subprocess
import sys
from typing import Any

from langchain.chat_models import init_chat_model

logger = logging.getLogger(__name__)

# Config keys that are not passed through to init_chat_model.
# usage_path is only for TokenUsageExtractor — never a model kwargs.
_META_KEYS = frozenset({"package", "model", "init_provider", "parameters", "usage_path"})

# Config provider name -> init_chat_model provider id when they differ.
_PROVIDER_IDS = {
    "gemini": "google_genai",
    "google-genai": "google_genai",
}


def ensure_package(package: str) -> None:
    """Install a missing LangChain provider package via pip."""
    module = package.replace("-", "_")
    if importlib.util.find_spec(module) is not None:
        return
    logger.info("Installing missing LLM provider package: %s", package)
    subprocess.check_call(
        [sys.executable, "-m", "pip", "install", "-U", package],
    )


def _build_params(provider_cfg: dict) -> dict[str, Any]:
    """Merge ``parameters`` with any legacy flat keys on the provider block.

    Meta keys such as ``usage_path`` are never forwarded to the chat model.
    """
    params = dict(provider_cfg.get("parameters") or {})
    for key, value in provider_cfg.items():
        if key not in _META_KEYS and key not in params:
            params[key] = value
    # Guard: never pass usage_path even if it was wrongly placed under parameters.
    params.pop("usage_path", None)
    return {k: v for k, v in params.items() if v is not None}


def _credentials_ok(provider: str, params: dict[str, Any]) -> bool:
    """Return False (and log) when required credentials are missing."""
    if "api_key" in params:
        api_key = params.get("api_key") or ""
        if not str(api_key).strip():
            logger.warning(
                "Provider '%s' api_key is not configured; LLM will be unavailable.",
                provider,
            )
            return False
    if provider == "ollama":
        base_url = params.get("base_url") or ""
        if not str(base_url).strip():
            logger.warning(
                "Ollama base_url is not configured; LLM will be unavailable."
            )
            return False
    return True


def create_llm(provider: str, provider_cfg: dict):
    """
    Create a chat model for *provider* from its YAML config block.

    Expected shape::

        package: langchain-openai
        model: gpt-4.1-mini
        parameters:
          temperature: 0.1
          api_key: <resolved from ${ENV}>
        usage_path: usage_metadata  # for TokenUsageExtractor only; not a model kwarg
    """
    package = provider_cfg.get("package")
    if not package:
        raise ValueError(
            f"Provider '{provider}' is missing 'package' in llm_config.yaml"
        )
    model_name = provider_cfg.get("model")
    if not model_name:
        raise ValueError(
            f"Provider '{provider}' is missing 'model' in llm_config.yaml"
        )

    ensure_package(package)

    params = _build_params(provider_cfg)
    if not _credentials_ok(provider, params):
        return None

    init_provider = (
        provider_cfg.get("init_provider")
        or _PROVIDER_IDS.get(provider)
        or provider.replace("-", "_")
    )
    model = f"{init_provider}:{model_name}"

    return init_chat_model(model=model, **params)
