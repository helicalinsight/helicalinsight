"""LLM / InstantBI test settings for Travle_Agent scenarios.

Defaults target the shared hi-ee host used for Travle_Agent.model. Override via
environment variables (preferred for CI / rotating session cookies):

* ``HELICALBI_TEST_BASE_URL``
* ``HELICALBI_TEST_SESSION_COOKIE``
* ``HELICALBI_TEST_MODEL_DIR`` / ``HELICALBI_TEST_MODEL_FILE``
* ``HELICALBI_TEST_METADATA_DIR`` / ``HELICALBI_TEST_METADATA_FILE``
* ``HELICALBI_TEST_OPENAI_API_KEY`` / ``OPENAI_API_KEY``
* ``HELICALBI_TEST_DEFAULT_PROVIDER``

Optional local YAML at ``tests/llm/llm_test_config.local.yaml`` (gitignored)
wins over defaults but loses to env vars.
"""
from __future__ import annotations

import os
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any

try:
    import yaml
except ImportError:  # pragma: no cover
    yaml = None  # type: ignore


_SETTINGS_DIR = Path(__file__).resolve().parent
_LOCAL_CONFIG = _SETTINGS_DIR / "llm_test_config.local.yaml"
_DEFAULT_CONFIG = _SETTINGS_DIR / "llm_test_config.yaml"


@dataclass(frozen=True)
class LlmTestSettings:
    """Connection + model + provider pointers for Travle_Agent SQL/viz tests."""

    base_url: str
    session_cookie: str
    model_dir: str
    model_file: str
    metadata_dir: str
    metadata_file: str
    default_provider: str = "openai"
    providers: dict[str, Any] = field(default_factory=dict)

    @property
    def services_url(self) -> str:
        return f"{self.base_url.rstrip('/')}/services"

    @property
    def model_ref(self) -> dict[str, str]:
        return {"dir": self.model_dir, "file": self.model_file}

    @property
    def metadata_ref(self) -> dict[str, str]:
        return {
            "location": self.metadata_dir,
            "metadataFileName": self.metadata_file,
        }

    @property
    def openai_api_key(self) -> str:
        provider = self.providers.get(self.default_provider) or {}
        if isinstance(provider, dict):
            return str(provider.get("api_key") or "").strip()
        return ""


_DEFAULTS: dict[str, Any] = {
    "base_url": "https://164.52.206.202/hi-ee",
    "session_cookie": "",
    "model_dir": "0806",
    "model_file": "Travle_Agent.model",
    "metadata_dir": "test",
    "metadata_file": "pg_sample_travel_data.metadata",
    "default_provider": "openai",
    "providers": {
        "openai": {
            "model": "gpt-4.1-mini",
            "temperature": 0.1,
            "max_tokens": 4000,
            "api_key": "",
        }
    },
}


def _deep_merge(base: dict[str, Any], overlay: dict[str, Any]) -> dict[str, Any]:
    merged = dict(base)
    for key, value in overlay.items():
        if (
            key in merged
            and isinstance(merged[key], dict)
            and isinstance(value, dict)
        ):
            merged[key] = _deep_merge(merged[key], value)
        else:
            merged[key] = value
    return merged


def _read_yaml(path: Path) -> dict[str, Any]:
    if not path.is_file() or yaml is None:
        return {}
    with path.open(encoding="utf-8") as handle:
        payload = yaml.safe_load(handle) or {}
    if not isinstance(payload, dict):
        return {}
    nested_model = payload.get("model") if isinstance(payload.get("model"), dict) else {}
    nested_meta = (
        payload.get("metadata") if isinstance(payload.get("metadata"), dict) else {}
    )
    providers = payload.get("providers") if isinstance(payload.get("providers"), dict) else None
    flat: dict[str, Any] = {
        "base_url": payload.get("base_url"),
        "session_cookie": payload.get("session_cookie") or payload.get("JSESSIONID"),
        "model_dir": nested_model.get("dir") or payload.get("model_dir"),
        "model_file": nested_model.get("file") or payload.get("model_file"),
        "metadata_dir": nested_meta.get("dir")
        or nested_meta.get("location")
        or payload.get("metadata_dir"),
        "metadata_file": nested_meta.get("file")
        or nested_meta.get("metadataFileName")
        or payload.get("metadata_file"),
        "default_provider": payload.get("default_provider"),
    }
    if providers is not None:
        flat["providers"] = providers
    return {key: value for key, value in flat.items() if value not in (None, "")}


def load_llm_test_settings() -> LlmTestSettings:
    """Merge defaults ← committed yaml ← local yaml ← env vars."""
    merged: dict[str, Any] = dict(_DEFAULTS)
    merged["providers"] = dict(_DEFAULTS["providers"])
    for path in (_DEFAULT_CONFIG, _LOCAL_CONFIG):
        overlay = _read_yaml(path)
        if "providers" in overlay and isinstance(overlay["providers"], dict):
            merged["providers"] = _deep_merge(
                merged.get("providers") or {}, overlay.pop("providers")
            )
        merged.update(overlay)

    env_map = {
        "base_url": "HELICALBI_TEST_BASE_URL",
        "session_cookie": "HELICALBI_TEST_SESSION_COOKIE",
        "model_dir": "HELICALBI_TEST_MODEL_DIR",
        "model_file": "HELICALBI_TEST_MODEL_FILE",
        "metadata_dir": "HELICALBI_TEST_METADATA_DIR",
        "metadata_file": "HELICALBI_TEST_METADATA_FILE",
        "default_provider": "HELICALBI_TEST_DEFAULT_PROVIDER",
    }
    for key, env_name in env_map.items():
        value = os.environ.get(env_name, "").strip()
        if value:
            merged[key] = value

    api_key = (
        os.environ.get("HELICALBI_TEST_OPENAI_API_KEY", "").strip()
        or os.environ.get("OPENAI_API_KEY", "").strip()
    )
    providers = dict(merged.get("providers") or {})
    openai_cfg = dict(providers.get("openai") or {})
    if api_key:
        openai_cfg["api_key"] = api_key
    providers["openai"] = openai_cfg
    merged["providers"] = providers

    base_url = str(merged["base_url"]).rstrip("/")
    return LlmTestSettings(
        base_url=base_url,
        session_cookie=str(merged.get("session_cookie") or ""),
        model_dir=str(merged["model_dir"]),
        model_file=str(merged["model_file"]),
        metadata_dir=str(merged["metadata_dir"]),
        metadata_file=str(merged["metadata_file"]),
        default_provider=str(merged.get("default_provider") or "openai"),
        providers=providers,
    )


def apply_llm_test_env(settings: LlmTestSettings | None = None) -> LlmTestSettings:
    """Export cookie/API key into env so LLMManager / HTTP clients can resolve them."""
    settings = settings or load_llm_test_settings()
    if settings.session_cookie:
        os.environ.setdefault("HELICALBI_TEST_SESSION_COOKIE", settings.session_cookie)
    if settings.openai_api_key:
        os.environ.setdefault("OPENAI_API_KEY", settings.openai_api_key)
        os.environ.setdefault("HELICALBI_TEST_OPENAI_API_KEY", settings.openai_api_key)
    os.environ.setdefault("HELICALBI_TEST_BASE_URL", settings.base_url)
    return settings
