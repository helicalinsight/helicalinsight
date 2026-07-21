from helicalbi.core.ConfigLoader import ConfigLoader

_app_config = ConfigLoader.load_config("application_config.yaml")
default_sql_limit = int(_app_config.get("sql", {}).get("default_limit", 100))

_app_settings = _app_config.get("app", {})
app_env = str(_app_settings.get("env", "dev")).strip().lower()
# Independent of env — set explicitly in application_config.yaml
app_debug = bool(_app_settings.get("debug", False))


def is_production() -> bool:
    return app_env in ("prod", "production")


def is_debug() -> bool:
    return app_debug


_logging_settings = _app_config.get("logging", {})
log_level_name = str(_logging_settings.get("level", "INFO")).upper()
log_file = str(_logging_settings.get("file", "logs/app.log"))
log_error_file = str(_logging_settings.get("error_file", "logs/error.log"))
log_backup_days = int(_logging_settings.get("backup_days", 14))
show_llm_activity = bool(_logging_settings.get("show_llm_activity", False))
show_endpoint_log = bool(_logging_settings.get("show_endpoint_log", False))
show_api_call_log = bool(_logging_settings.get("show_api_call_log", False))


def effective_log_level_name() -> str:
    """DEBUG only when both app.debug and logging.level are DEBUG; never based on env."""
    if log_level_name == "DEBUG" and app_debug:
        return "DEBUG"
    if log_level_name == "DEBUG":
        return "INFO"
    return log_level_name

_feature_flags = _app_config.get("feature_flags", {})
enable_llm_usage_audit = bool(_feature_flags.get("enable_llm_usage_audit", True))
hide_prompt_reason = bool(_feature_flags.get("hide_prompt_reason", False))

_api_cache_settings = _app_config.get("api_cache", {})
api_cache_enabled = bool(_api_cache_settings.get("enabled", True))
api_cache_max_entries = max(1, int(_api_cache_settings.get("max_entries", 100)))
