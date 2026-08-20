"""Read/write ``llm_config.yaml`` and refresh the live LLMManager."""

from __future__ import annotations

import logging
from copy import deepcopy
from typing import Any, Optional

from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.service.utility.exception.UtilityError import UtilityError
from helicalbi.service.utility.provider._helpers import mask_secrets

logger = logging.getLogger(__name__)

_CONFIG_FILE = "llm_config.yaml"


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

    def list_providers(self) -> list[dict[str, Any]]:
        config = self.get_config(mask=True)
        default = config.get("default_provider")
        providers = config.get("providers") or {}
        rows: list[dict[str, Any]] = []
        for name, cfg in providers.items():
            rows.append(
                {
                    "provider": name,
                    "package": (cfg or {}).get("package"),
                    "model": (cfg or {}).get("model"),
                    "is_default": name == default,
                    "usage_path": (cfg or {}).get("usage_path"),
                    "parameters": (cfg or {}).get("parameters") or {},
                }
            )
        return rows

    def get_provider(self, provider: str) -> dict[str, Any]:
        provider = (provider or "").strip()
        if not provider:
            raise UtilityError("provider is required")
        config = self.get_config(mask=True)
        providers = config.get("providers") or {}
        if provider not in providers:
            raise UtilityError(f"Provider '{provider}' not found in llm_config.yaml")
        cfg = providers[provider] or {}
        return {
            "provider": provider,
            "package": cfg.get("package"),
            "model": cfg.get("model"),
            "is_default": provider == config.get("default_provider"),
            "usage_path": cfg.get("usage_path"),
            "parameters": cfg.get("parameters") or {},
            "init_provider": cfg.get("init_provider"),
        }

    # ------------------------------------------------------------------
    # Write helpers
    # ------------------------------------------------------------------

    def _load_raw(self) -> dict:
        return ConfigLoader.load_raw_config(self._config_file)

    def _persist_and_reload(self, raw: dict) -> dict:
        ConfigLoader.save_config(self._config_file, raw)
        self._llm_manager.reload_from_disk()
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
            if replace_parameters:
                existing["parameters"] = deepcopy(parameters)
            else:
                merged_params = deepcopy(existing.get("parameters") or {})
                merged_params.update(parameters)
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
