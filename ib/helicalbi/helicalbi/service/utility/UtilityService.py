"""Facade for runtime LLM and application configuration utility operations."""

from __future__ import annotations

from typing import Any, Optional

from helicalbi.common.configuration import llm_manager
from helicalbi.service.utility.exception.UtilityError import UtilityError
from helicalbi.service.utility.provider.AppConfigProvider import AppConfigProvider
from helicalbi.service.utility.provider.LlmConfigProvider import LlmConfigProvider
from helicalbi.service.utility.provider.ModelCatalogProvider import ModelCatalogProvider


class UtilityService:
    """Public entry point used by the ``/utility/*`` HTTP handlers."""

    def __init__(
        self,
        *,
        llm_provider: Optional[LlmConfigProvider] = None,
        app_provider: Optional[AppConfigProvider] = None,
        catalog_provider: Optional[ModelCatalogProvider] = None,
    ):
        self.llm = llm_provider or LlmConfigProvider(llm_manager)
        self.app = app_provider or AppConfigProvider()
        self.catalog = catalog_provider or ModelCatalogProvider()

    # ------------------------------------------------------------------
    # LLM
    # ------------------------------------------------------------------

    def list_llm_providers(self) -> dict[str, Any]:
        config = self.llm.get_config(mask=True)
        return {
            "default_provider": config.get("default_provider"),
            "base_url": config.get("base_url"),
            "providers": self.llm.list_providers(),
        }

    def get_llm_provider(self, provider: str) -> dict[str, Any]:
        return self.llm.get_provider(provider)

    def list_models_for_package(
        self,
        *,
        package: Optional[str] = None,
        provider: Optional[str] = None,
    ) -> dict[str, Any]:
        resolved_package = (package or "").strip()
        current_model = None
        if not resolved_package and provider:
            info = self.llm.get_provider(provider)
            resolved_package = info.get("package") or ""
            current_model = info.get("model")
        if not resolved_package:
            raise UtilityError("package or provider is required")

        models = self.catalog.list_models_for_package(resolved_package)
        if current_model is None and provider:
            try:
                current_model = self.llm.get_provider(provider).get("model")
            except Exception:
                current_model = None

        # Include the currently configured model even if absent from catalog.
        if current_model and current_model not in models:
            models = [current_model, *models]

        return {
            "package": resolved_package,
            "provider": provider,
            "current_model": current_model,
            "models": models,
        }

    def change_model(
        self,
        model: str,
        *,
        provider: Optional[str] = None,
        set_as_default: bool = True,
    ) -> dict[str, Any]:
        return self.llm.change_model(
            model, provider=provider, set_as_default=set_as_default
        )

    def set_default_provider(self, provider: str) -> dict[str, Any]:
        return self.llm.set_default_provider(provider)

    def upsert_llm_provider(self, payload: dict) -> dict[str, Any]:
        ignored = {
            "provider",
            "package",
            "model",
            "parameters",
            "usage_path",
            "init_provider",
            "set_as_default",
            "replace_parameters",
            # Java session plumbing — never persist into llm_config.yaml
            "body",
            "requestId",
            "htmlId",
            "sessionCookie",
            "username",
            "userId",
            "orgId",
        }
        return self.llm.upsert_provider(
            payload.get("provider") or "",
            package=payload.get("package"),
            model=payload.get("model"),
            parameters=payload.get("parameters"),
            usage_path=payload.get("usage_path"),
            init_provider=payload.get("init_provider"),
            set_as_default=bool(payload.get("set_as_default", False)),
            replace_parameters=bool(payload.get("replace_parameters", False)),
            extra={k: v for k, v in payload.items() if k not in ignored},
        )

    def update_llm_config(self, payload: dict) -> dict[str, Any]:
        return self.llm.update_llm_settings(
            default_provider=payload.get("default_provider"),
            base_url=payload.get("base_url"),
            providers=payload.get("providers"),
        )

    # ------------------------------------------------------------------
    # Application config
    # ------------------------------------------------------------------

    def get_app_config(self) -> dict[str, Any]:
        return self.app.get_config()

    def update_app_config(self, updates: dict) -> dict[str, Any]:
        return self.app.update_config(updates)

    def update_logging(self, logging_updates: dict) -> dict[str, Any]:
        return self.app.update_logging(logging_updates)

    def update_question_config(self, payload: dict) -> dict[str, Any]:
        return self.app.update_question_config(
            suggestion_query=payload.get("suggestion_query"),
            extra={
                k: v for k, v in payload.items() if k != "suggestion_query"
            }
            or None,
        )

    def get_settings_bootstrap(self) -> dict[str, Any]:
        """Single payload for Admin InstantBI Settings UI initial load."""
        llm = self.list_llm_providers()
        return {
            "llm": llm,
            "default_provider": llm.get("default_provider"),
            "base_url": llm.get("base_url"),
            "providers": llm.get("providers") or [],
            "config": self.get_app_config(),
        }


# Module-level singleton for route handlers.
utility_service = UtilityService()
