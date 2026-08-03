"""Catalog of known chat models per LangChain provider package."""

from __future__ import annotations

import logging
from typing import Any

from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.service.utility.exception.UtilityError import UtilityError

logger = logging.getLogger(__name__)

_CATALOG_FILE = "llm_models_catalog.yaml"


class ModelCatalogProvider:
    """Lists selectable models for a LangChain provider package."""

    def __init__(self, catalog_file: str = _CATALOG_FILE):
        self._catalog_file = catalog_file

    def _load_catalog(self) -> dict[str, Any]:
        try:
            return ConfigLoader.load_raw_config(self._catalog_file)
        except FileNotFoundError:
            logger.warning("Model catalog %s not found.", self._catalog_file)
            return {}

    def list_packages(self) -> list[str]:
        return sorted(self._load_catalog().keys())

    def list_models_for_package(self, package: str) -> list[str]:
        package = (package or "").strip()
        if not package:
            raise UtilityError("package is required to list models")

        catalog = self._load_catalog()
        models = catalog.get(package)
        if models is None:
            # Allow package aliases with underscores vs hyphens.
            alt = package.replace("_", "-")
            models = catalog.get(alt)
        if models is None:
            raise UtilityError(
                f"No model catalog entry for package '{package}'. "
                f"Known packages: {', '.join(sorted(catalog.keys())) or '(none)'}"
            )
        if not isinstance(models, list):
            raise UtilityError(f"Invalid catalog entry for package '{package}'")
        return [str(m) for m in models]
