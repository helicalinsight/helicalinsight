import logging
import os
from logging.handlers import TimedRotatingFileHandler
from typing import Optional

from helicalbi.common import app_config


def _ensure_log_dir(path: str) -> None:
    log_dir = os.path.dirname(path)
    if log_dir:
        os.makedirs(log_dir, exist_ok=True)


def _rotating_file_handler(
    path: str,
    formatter: logging.Formatter,
    backup_count: int,
    level: Optional[int] = None,
) -> TimedRotatingFileHandler:
    _ensure_log_dir(path)
    handler = TimedRotatingFileHandler(
        path,
        when="midnight",
        interval=1,
        backupCount=backup_count,
        encoding="utf-8",
    )
    handler.suffix = "%Y-%m-%d"
    handler.setFormatter(formatter)
    if level is not None:
        handler.setLevel(level)
    return handler


def configure_logging() -> None:
    """Configure root logger with console, app, and error-only file handlers."""
    root_logger = logging.getLogger()
    if root_logger.handlers:
        return

    log_level = getattr(
        logging, app_config.effective_log_level_name(), logging.INFO
    )
    log_file = app_config.log_file
    error_file = app_config.log_error_file
    backup_count = app_config.log_backup_days

    formatter = logging.Formatter(
        "%(asctime)s | %(levelname)s | %(name)s | %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
    )

    root_logger.setLevel(log_level)

    console_handler = logging.StreamHandler()
    console_handler.setFormatter(formatter)
    root_logger.addHandler(console_handler)

    root_logger.addHandler(
        _rotating_file_handler(log_file, formatter, backup_count)
    )
    root_logger.addHandler(
        _rotating_file_handler(
            error_file, formatter, backup_count, level=logging.ERROR
        )
    )
