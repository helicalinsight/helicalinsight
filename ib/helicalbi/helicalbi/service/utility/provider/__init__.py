"""Providers that read/write runtime configuration for the utility service."""

__all__ = ["AppConfigProvider", "LlmConfigProvider", "ModelCatalogProvider"]


def __getattr__(name: str):
    if name == "AppConfigProvider":
        from helicalbi.service.utility.provider.AppConfigProvider import AppConfigProvider

        return AppConfigProvider
    if name == "LlmConfigProvider":
        from helicalbi.service.utility.provider.LlmConfigProvider import LlmConfigProvider

        return LlmConfigProvider
    if name == "ModelCatalogProvider":
        from helicalbi.service.utility.provider.ModelCatalogProvider import (
            ModelCatalogProvider,
        )

        return ModelCatalogProvider
    raise AttributeError(f"module {__name__!r} has no attribute {name!r}")
