"""Catalog of known chat models per LangChain provider package."""

from __future__ import annotations

import logging
import os
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
        """Return known models for *package*, or ``[]`` if the package is not cataloged.

        Unknown packages are allowed so new providers can be configured before
        catalog entries exist; callers can still accept free-typed model ids.
        """
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
            logger.info(
                "No model catalog entry for package '%s'; returning empty list.",
                package,
            )
            return []
        if not isinstance(models, list):
            raise UtilityError(f"Invalid catalog entry for package '{package}'")
        return [str(m) for m in models]

    def add_model(self, package: str, model: str) -> bool:
        """Append *model* under *package* in the catalog YAML.

        Returns ``True`` when the file was updated. Existing ids are left
        unchanged. Unknown packages are created so a name typed in Settings
        shows up in the model list on the next load.
        """
        package = (package or "").strip()
        model = (model or "").strip()
        if not package or not model:
            return False

        catalog = self._load_catalog()
        if not isinstance(catalog, dict):
            raise UtilityError("Model catalog is not a mapping")

        key = self._resolve_package_key(catalog, package)
        models = catalog.get(key)
        if models is None:
            catalog[key] = [model]
            self._separate_new_package(catalog, key)
        else:
            if not isinstance(models, list):
                raise UtilityError(f"Invalid catalog entry for package '{key}'")
            if any(str(item) == model for item in models):
                return False
            self._append_model(models, model)

        self._save_catalog(catalog)
        logger.info("Added model '%s' to catalog package '%s'.", model, key)
        return True

    @staticmethod
    def _append_model(models: list, model: str) -> None:
        """Append *model* without pulling the package's trailing blank line up."""
        from ruamel.yaml.comments import CommentedSeq

        if isinstance(models, CommentedSeq) and models.ca.items and models:
            last_index = len(models) - 1
            trailing = models.ca.items.pop(last_index, None)
            models.append(model)
            if trailing is not None:
                models.ca.items[len(models) - 1] = trailing
            return
        models.append(model)

    @staticmethod
    def _separate_new_package(catalog: dict[str, Any], key: str) -> None:
        if len(catalog) <= 1:
            return
        if not hasattr(catalog, "yaml_set_comment_before_after_key"):
            return
        catalog.yaml_set_comment_before_after_key(key, before="\n")

    def _save_catalog(self, catalog: dict[str, Any]) -> None:
        """Write *catalog* in place so comments and spacing stay intact."""
        config_path = ConfigLoader.resolve_path(self._catalog_file)
        os.makedirs(os.path.dirname(config_path), exist_ok=True)
        with open(config_path, "w", encoding="utf-8") as handle:
            ConfigLoader._rt_yaml().dump(catalog, handle)

    def _resolve_package_key(self, catalog: dict[str, Any], package: str) -> str:
        if package in catalog:
            return package
        hyphenated = package.replace("_", "-")
        if hyphenated in catalog:
            return hyphenated
        underscored = package.replace("-", "_")
        if underscored in catalog:
            return underscored
        return package

    def has_package(self, package: str) -> bool:
        """True when *package* (or hyphen/underscore alias) exists in the catalog."""
        package = (package or "").strip()
        if not package:
            return False
        catalog = self._load_catalog()
        if package in catalog:
            return True
        return package.replace("_", "-") in catalog
