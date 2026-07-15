from helicalbi.core.ConfigLoader import ConfigLoader

_app_config = ConfigLoader.load_config("application_config.yaml")
default_sql_limit = int(_app_config.get("sql", {}).get("default_limit", 100))

_app_settings = _app_config.get("app", {})
app_env = str(_app_settings.get("env", "dev")).strip().lower()

_debug_setting = _app_settings.get("debug")
if _debug_setting is None:
    app_debug = app_env not in ("prod", "production")
else:
    app_debug = bool(_debug_setting)


def is_production() -> bool:
    return app_env in ("prod", "production")


def is_debug() -> bool:
    return app_debug


_logging_settings = _app_config.get("logging", {})
log_level_name = str(_logging_settings.get("level", "INFO")).upper()
log_file = str(_logging_settings.get("file", "logs/app.log"))
log_backup_days = int(_logging_settings.get("backup_days", 14))
show_llm_activity = bool(_logging_settings.get("show_llm_activity", False))
show_endpoint_log = bool(_logging_settings.get("show_endpoint_log", False))
show_api_call_log = bool(_logging_settings.get("show_api_call_log", False))

_feature_flags = _app_config.get("feature_flags", {})
enable_llm_usage_audit = bool(_feature_flags.get("enable_llm_usage_audit", True))
optional_prompt_reason = bool(_feature_flags.get("optional_prompt_reason", False))

_api_cache_settings = _app_config.get("api_cache", {})
api_cache_enabled = bool(_api_cache_settings.get("enabled", True))
api_cache_max_entries = max(1, int(_api_cache_settings.get("max_entries", 100)))
