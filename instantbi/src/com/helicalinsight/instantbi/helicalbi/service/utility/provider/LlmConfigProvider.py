"""Read/write ``llm_config.yaml`` and refresh the live LLMManager."""

from __future__ import annotations

import logging
import os
from copy import deepcopy
from typing import Any, Optional

from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.service.utility.exception.UtilityError import UtilityError
from helicalbi.service.utility.provider._helpers import mask_secrets

logger = logging.getLogger(__name__)

_CONFIG_FILE = "llm_config.yaml"


def _preserve_env_placeholders(existing_params: dict, incoming: dict) -> dict:
    """Keep ``${ENV}`` placeholders when the editor posts the resolved secret."""
    merged = deepcopy(incoming or {})
    for key, old in (existing_params or {}).items():
        if not (isinstance(old, str) and old.startswith("${") and old.endswith("}")):
            continue
        new = merged.get(key)
        if new in (None, "", old):
            merged[key] = old
            continue
        if new == os.getenv(old[2:-1]):
            merged[key] = old
    return merged


class LlmConfigProvider:
    """Persist LLM provider settings and apply them at runtime."""

    def __init__(self, llm_manager: Any, config_file: str = _CONFIG_FILE):
        self._llm_manager = llm_manager
        self._config_file = config_file

    # ------------------------------------------------------------------
    # Read
    # ------------------------------------------------------------------

    def get_config(self, *, mask: bool = True) -> dict:
        with self._llm_manager._lock:
            snapshot = deepcopy(self._llm_manager.config)
        return mask_secrets(snapshot) if mask else snapshot

    def _raw_providers(self) -> dict:
        try:
            raw = self._load_raw() or {}
        except Exception:
            return {}
        providers = raw.get("providers") or {}
        return providers if isinstance(providers, dict) else {}

    def _provider_row(
        self,
        name: str,
        cfg: dict,
        default: Any,
        stored: Optional[dict] = None,
    ) -> dict[str, Any]:
        live = cfg or {}
        raw_cfg = stored if isinstance(stored, dict) else {}
        raw_params = (
            raw_cfg.get("parameters")
            if isinstance(raw_cfg.get("parameters"), dict)
            else {}
        )
        live_params = (
            live.get("parameters") if isinstance(live.get("parameters"), dict) else {}
        )
        parameters = deepcopy(live_params or {})
        for key, raw_value in raw_params.items():
            current = parameters.get(key)
            if current in (None, "") and raw_value not in (None, ""):
                parameters[key] = deepcopy(raw_value)
            elif key not in parameters:
                parameters[key] = deepcopy(raw_value)
        for key, value in list(parameters.items()):
            if value is None:
                parameters[key] = ""
        return {
            "provider": name,
            "package": raw_cfg.get("package") or live.get("package"),
            "model": raw_cfg.get("model") or live.get("model"),
            "is_default": name == default,
            "usage_path": raw_cfg.get("usage_path") or live.get("usage_path"),
            "parameters": deepcopy(parameters),
        }

    def list_providers(self) -> list[dict[str, Any]]:
        config = self.get_config(mask=False)
        default = config.get("default_provider")
        providers = config.get("providers") or {}
        stored_providers = self._raw_providers()
        rows: list[dict[str, Any]] = []
        for name, cfg in providers.items():
            rows.append(
                self._provider_row(
                    name,
                    cfg or {},
                    default,
                    stored_providers.get(name),
                )
            )
        return rows

    def get_provider(self, provider: str) -> dict[str, Any]:
        provider = (provider or "").strip()
        if not provider:
            raise UtilityError("provider is required")
        config = self.get_config(mask=False)
        providers = config.get("providers") or {}
        if provider not in providers:
            raise UtilityError(f"Provider '{provider}' not found in llm_config.yaml")
        row = self._provider_row(
            provider,
            providers[provider] or {},
            config.get("default_provider"),
            self._raw_providers().get(provider),
        )
        live = providers[provider] or {}
        row["init_provider"] = live.get("init_provider")
        return row

    # ------------------------------------------------------------------
    # Write helpers
    # ------------------------------------------------------------------

    def _load_raw(self) -> dict:
        return ConfigLoader.load_raw_config(self._config_file)

    def _persist_and_reload(self, raw: dict) -> dict:
        ConfigLoader.save_config(self._config_file, raw)
        self._llm_manager.reload_from_disk()
        # Instantiate now so the next chat uses the new provider without a restart.
        # Failures are logged; get_llm() already returns None when credentials are missing.
        try:
            self._llm_manager.get_llm()
        except Exception:
            logger.exception("Failed to instantiate LLM after config reload")
        try:
            import helicalbi.common.configuration as configuration

            configuration.baseUrl = self._llm_manager.get_baseUrl()
        except Exception:
            logger.exception("Failed to refresh configuration.baseUrl after LLM reload")
        logger.info("Persisted and reloaded %s", self._config_file)
        return self.get_config(mask=True)

    # ------------------------------------------------------------------
    # Mutations
    # ------------------------------------------------------------------

    def set_default_provider(self, provider: str) -> dict:
        provider = (provider or "").strip()
        if not provider:
            raise UtilityError("provider is required")

        raw = self._load_raw()
        providers = raw.get("providers") or {}
        if provider not in providers:
            raise UtilityError(
                f"Cannot set default_provider to '{provider}'; "
                "it is not defined under providers"
            )
        raw["default_provider"] = provider
        return self._persist_and_reload(raw)

    def change_model(
        self,
        model: str,
        *,
        provider: Optional[str] = None,
        set_as_default: bool = True,
    ) -> dict:
        model = (model or "").strip()
        if not model:
            raise UtilityError("model is required")

        raw = self._load_raw()
        providers = raw.setdefault("providers", {})
        effective = (provider or raw.get("default_provider") or "").strip()
        if not effective:
            raise UtilityError(
                "provider is required when default_provider is not set"
            )
        if effective not in providers:
            raise UtilityError(f"Provider '{effective}' not found in llm_config.yaml")

        providers[effective]["model"] = model
        if set_as_default:
            raw["default_provider"] = effective

        result = self._persist_and_reload(raw)
        return {
            "default_provider": result.get("default_provider"),
            "provider": effective,
            "model": model,
            "config": result,
        }

    def upsert_provider(
        self,
        provider: str,
        *,
        package: Optional[str] = None,
        model: Optional[str] = None,
        parameters: Optional[dict] = None,
        usage_path: Optional[str] = None,
        init_provider: Optional[str] = None,
        set_as_default: bool = False,
        replace_parameters: bool = False,
        extra: Optional[dict] = None,
    ) -> dict:
        provider = (provider or "").strip()
        if not provider:
            raise UtilityError("provider is required")

        raw = self._load_raw()
        providers = raw.setdefault("providers", {})
        existing = deepcopy(providers.get(provider) or {})

        if package is not None:
            existing["package"] = package
        if model is not None:
            existing["model"] = model
        if usage_path is not None:
            existing["usage_path"] = usage_path
        if init_provider is not None:
            existing["init_provider"] = init_provider
        if parameters is not None:
            existing_params = deepcopy(existing.get("parameters") or {})
            incoming = _preserve_env_placeholders(existing_params, parameters)
            if replace_parameters:
                existing["parameters"] = incoming
            else:
                merged_params = existing_params
                merged_params.update(incoming)
                existing["parameters"] = merged_params
        if extra:
            for key, value in extra.items():
                if key in (
                    "package",
                    "model",
                    "parameters",
                    "usage_path",
                    "init_provider",
                    "replace_parameters",
                ):
                    continue
                existing[key] = value

        if not existing.get("package"):
            raise UtilityError(f"Provider '{provider}' requires 'package'")
        if not existing.get("model"):
            raise UtilityError(f"Provider '{provider}' requires 'model'")

        providers[provider] = existing
        if set_as_default:
            raw["default_provider"] = provider

        result = self._persist_and_reload(raw)
        return {
            "provider": provider,
            "is_default": result.get("default_provider") == provider,
            "config": result,
        }

    def update_llm_settings(
        self,
        *,
        default_provider: Optional[str] = None,
        base_url: Optional[str] = None,
        providers: Optional[dict] = None,
    ) -> dict:
        """Apply top-level LLM settings and/or merge provider blocks."""
        raw = self._load_raw()

        if default_provider is not None:
            name = default_provider.strip()
            if not name:
                raise UtilityError("default_provider cannot be empty")
            known = set((raw.get("providers") or {}).keys()) | set((providers or {}).keys())
            if name not in known:
                raise UtilityError(
                    f"default_provider '{name}' is not among providers"
                )
            raw["default_provider"] = name

        if base_url is not None:
            raw["base_url"] = base_url

        if providers:
            existing = raw.setdefault("providers", {})
            for name, cfg in providers.items():
                if not isinstance(cfg, dict):
                    raise UtilityError(f"Provider '{name}' config must be an object")
                merged = deepcopy(existing.get(name) or {})
                for key, value in cfg.items():
                    if key == "parameters" and isinstance(value, dict):
                        params = deepcopy(merged.get("parameters") or {})
                        params.update(value)
                        merged["parameters"] = params
                    else:
                        merged[key] = value
                if not merged.get("package") or not merged.get("model"):
                    raise UtilityError(
                        f"Provider '{name}' requires both 'package' and 'model'"
                    )
                existing[name] = merged

        return self._persist_and_reload(raw)
