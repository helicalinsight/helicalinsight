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
