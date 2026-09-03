"""Read/write ``application_config.yaml`` (logging, KPI questions, flags, …)."""

from __future__ import annotations

import logging
from copy import deepcopy
from typing import Any, Optional

from helicalbi.common import app_config
from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.service.utility.exception.UtilityError import UtilityError
from helicalbi.service.utility.provider._helpers import deep_merge

logger = logging.getLogger(__name__)

_CONFIG_FILE = "application_config.yaml"

# Sections the utility API is allowed to update.
_ALLOWED_SECTIONS = frozenset(
    {"app", "logging", "feature_flags", "api_cache", "sql", "kpi", "dashboard"}
)


class AppConfigProvider:
    """Persist application settings and apply them at runtime."""

    def __init__(self, config_file: str = _CONFIG_FILE):
        self._config_file = config_file

    def get_config(self) -> dict:
        return app_config.get_snapshot()

    def update_config(self, updates: dict, *, sections: Optional[list[str]] = None) -> dict:
        if not isinstance(updates, dict) or not updates:
            raise UtilityError("updates object is required")

        allowed = set(sections) if sections else set(_ALLOWED_SECTIONS)
        unknown = [k for k in updates if k not in allowed]
        if unknown:
            raise UtilityError(
                f"Unsupported application config section(s): {', '.join(sorted(unknown))}. "
                f"Allowed: {', '.join(sorted(_ALLOWED_SECTIONS))}"
            )

        raw = ConfigLoader.load_raw_config(self._config_file)
        merged = deep_merge(raw, updates)
        ConfigLoader.save_config(self._config_file, merged)
        app_config.reload_from_disk()
        app_config.apply_runtime_logging()
        logger.info("Persisted and reloaded %s", self._config_file)
        return self.get_config()

    def update_logging(self, logging_updates: dict) -> dict:
        return self.update_config({"logging": logging_updates})

    def update_kpi(self, kpi_updates: dict) -> dict:
        return self.update_config({"kpi": kpi_updates})

    def update_question_config(
        self,
        *,
        suggestion_query: Optional[str] = None,
        extra: Optional[dict[str, Any]] = None,
    ) -> dict:
        """Update KPI / question-related application settings."""
        kpi: dict[str, Any] = {}
        if suggestion_query is not None:
            kpi["suggestion_query"] = suggestion_query
        if extra:
            kpi.update(extra)
        if not kpi:
            raise UtilityError("No question/KPI fields provided to update")
        return self.update_kpi(kpi)
