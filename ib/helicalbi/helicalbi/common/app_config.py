"""
app_config
----------
Provides live access to application_config.yaml.

Access pattern
~~~~~~~~~~~~~~
Prefer attribute access through the module object so you always get the
current value after a hot-reload:

    from helicalbi.common import app_config
    if app_config.show_llm_activity: ...

Direct imports (``from helicalbi.common.app_config import show_llm_activity``)
bind the value once at import time and will NOT reflect hot-reloads.  Use
them only for values that never change at runtime (e.g. ``app_env``).

Hot-reload
~~~~~~~~~~
The underlying YAML file is watched by a ``ConfigWatcher`` daemon thread.
Any save to ``application_config.yaml`` is picked up immediately and all
subsequent attribute accesses return the updated values.
"""

from __future__ import annotations

import logging
import threading

from helicalbi.core.ConfigLoader import ConfigLoader
from helicalbi.core.ConfigWatcher import ConfigWatcher

logger = logging.getLogger(__name__)

_CONFIG_FILE = "application_config.yaml"
_lock = threading.RLock()
_raw: dict = {}


def _load() -> dict:
    return ConfigLoader.load_config(_CONFIG_FILE)


def _reload() -> None:
    global _raw
    logger.info("application_config.yaml changed on disk — reloading configuration.")
    try:
        new = _load()
    except Exception:
        logger.exception("Failed to reload application_config.yaml; keeping previous config.")
        return
    with _lock:
        _raw = new
    logger.info("Application configuration reloaded successfully.")


# Initial load
with _lock:
    _raw = _load()

# Start file watcher
ConfigWatcher(_CONFIG_FILE, _reload).start()


# ------------------------------------------------------------------
# Live accessors — always read from the current _raw snapshot
# ------------------------------------------------------------------

def _app() -> dict:
    return _raw.get("app", {})

def _logging() -> dict:
    return _raw.get("logging", {})

def _flags() -> dict:
    return _raw.get("feature_flags", {})

def _api_cache() -> dict:
    return _raw.get("api_cache", {})

def _kpi() -> dict:
    return _raw.get("kpi", {})


# Module-level __getattr__ intercepts attribute access on the module itself
# (Python 3.7+).  This means ``app_config.show_llm_activity`` always
# returns the live value even after a hot-reload.
def __getattr__(name: str):  # noqa: N807
    _computed = {
        # app
        "app_env":               lambda: str(_app().get("env", "dev")).strip().lower(),
        "app_debug":             lambda: bool(_app().get("debug", False)),
        # logging
        "log_level_name":        lambda: str(_logging().get("level", "INFO")).upper(),
        "log_file":              lambda: str(_logging().get("file", "logs/app.log")),
        "log_error_file":        lambda: str(_logging().get("error_file", "logs/error.log")),
        "log_backup_days":       lambda: int(_logging().get("backup_days", 14)),
        "show_llm_activity":     lambda: bool(_logging().get("show_llm_activity", False)),
        "show_endpoint_log":     lambda: bool(_logging().get("show_endpoint_log", False)),
        "show_api_call_log":     lambda: bool(_logging().get("show_api_call_log", False)),
        # feature flags
        "enable_llm_usage_audit": lambda: bool(_flags().get("enable_llm_usage_audit", True)),
        "hide_prompt_reason":    lambda: bool(_flags().get("hide_prompt_reason", False)),
        # api cache
        "api_cache_enabled":     lambda: bool(_api_cache().get("enabled", True)),
        "api_cache_max_entries": lambda: max(1, int(_api_cache().get("max_entries", 100))),
        # sql
        "default_sql_limit":     lambda: int(_raw.get("sql", {}).get("default_limit", 100)),
        # kpi
        "kpi_suggestion_query":  lambda: str(
            _kpi().get("suggestion_query", "Suggest few Measurable and timebound KPIS")
        ),
    }
    if name in _computed:
        return _computed[name]()
    raise AttributeError(f"module 'app_config' has no attribute {name!r}")


# ------------------------------------------------------------------
# Functions (unchanged API)
# ------------------------------------------------------------------

def is_production() -> bool:
    return __getattr__("app_env") in ("prod", "production")


def is_debug() -> bool:
    return __getattr__("app_debug")


def effective_log_level_name() -> str:
    """DEBUG only when both app.debug and logging.level are DEBUG; never based on env."""
    level = __getattr__("log_level_name")
    debug = __getattr__("app_debug")
    if level == "DEBUG" and debug:
        return "DEBUG"
    if level == "DEBUG":
        return "INFO"
    return level


# ------------------------------------------------------------------
# Module-level names kept for backwards-compat with direct imports.
# These are snapshots taken at import time and are NOT hot-reloaded.
# Use ``app_config.<name>`` pattern for live values.
# ------------------------------------------------------------------

app_env = __getattr__("app_env")
app_debug = __getattr__("app_debug")
default_sql_limit = __getattr__("default_sql_limit")
kpi_suggestion_query = __getattr__("kpi_suggestion_query")
hide_prompt_reason = __getattr__("hide_prompt_reason")
enable_llm_usage_audit = __getattr__("enable_llm_usage_audit")
