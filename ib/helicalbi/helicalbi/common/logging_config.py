import logging
import os
from logging.handlers import TimedRotatingFileHandler

from helicalbi.common import app_config


def configure_logging() -> None:
    """Configure root logger with console and daily rotating file handlers."""
    root_logger = logging.getLogger()
    if root_logger.handlers:
        return

    log_level = getattr(logging, app_config.log_level_name, logging.INFO)
    log_file = app_config.log_file
    backup_count = app_config.log_backup_days

    log_dir = os.path.dirname(log_file)
    if log_dir:
        os.makedirs(log_dir, exist_ok=True)

    formatter = logging.Formatter(
        "%(asctime)s | %(levelname)s | %(name)s | %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
    )

    root_logger.setLevel(log_level)

    console_handler = logging.StreamHandler()
    console_handler.setFormatter(formatter)
    root_logger.addHandler(console_handler)

    file_handler = TimedRotatingFileHandler(
        log_file,
        when="midnight",
        interval=1,
        backupCount=backup_count,
        encoding="utf-8",
    )
    file_handler.suffix = "%Y-%m-%d"
    file_handler.setFormatter(formatter)
    root_logger.addHandler(file_handler)
