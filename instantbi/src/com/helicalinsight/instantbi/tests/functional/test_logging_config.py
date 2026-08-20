"""Functional tests for ``helicalbi.common.logging_config``.

``configure_logging`` operates on the root logger via ``logging.getLogger()``.
Pytest's own logging plugin attaches handlers to the root logger and would
make assertions about ``configure_logging``'s mutations impossible. We work
around this by briefly patching ``logging.getLogger`` to return a private
logger only for the duration of the call.
"""
import logging
from logging.handlers import TimedRotatingFileHandler
from unittest.mock import patch

import pytest

from helicalbi.common import app_config
from helicalbi.common.logging_config import configure_logging


pytestmark = pytest.mark.functional


def _call_under_isolated_root():
    """Invoke ``configure_logging`` against a fresh, throw-away logger.

    Returns the logger so callers can inspect handlers / level afterwards.
    """
    fresh = logging.Logger("helicalbi.tests.isolated")
    fresh.handlers.clear()
    real_get_logger = logging.getLogger

    def fake_get_logger(name=None):
        if name in (None, ""):
            return fresh
        return real_get_logger(name)

    with patch("helicalbi.common.logging_config.logging.getLogger", fake_get_logger):
        configure_logging()
    return fresh


def _file_handlers(logger: logging.Logger) -> list[TimedRotatingFileHandler]:
    return [h for h in logger.handlers if isinstance(h, TimedRotatingFileHandler)]


class TestConfigureLogging:
    def test_installs_console_app_and_error_handlers(self, tmp_path, monkeypatch):
        monkeypatch.setattr(app_config, "log_file", str(tmp_path / "logs" / "app.log"))
        monkeypatch.setattr(
            app_config, "log_error_file", str(tmp_path / "logs" / "error.log")
        )
        logger = _call_under_isolated_root()

        assert len(logger.handlers) == 3
        assert any(
            isinstance(h, logging.StreamHandler)
            and not isinstance(h, TimedRotatingFileHandler)
            for h in logger.handlers
        )
        file_handlers = _file_handlers(logger)
        assert len(file_handlers) == 2
        assert any(h.level == logging.ERROR for h in file_handlers)
        assert any(h.level == logging.NOTSET for h in file_handlers)

    def test_creates_log_directories(self, tmp_path, monkeypatch):
        log_file = tmp_path / "fresh_dir" / "app.log"
        error_file = tmp_path / "error_dir" / "error.log"
        assert not log_file.parent.exists()
        assert not error_file.parent.exists()
        monkeypatch.setattr(app_config, "log_file", str(log_file))
        monkeypatch.setattr(app_config, "log_error_file", str(error_file))
        _call_under_isolated_root()
        assert log_file.parent.exists()
        assert error_file.parent.exists()

    def test_error_handler_only_records_errors(self, tmp_path, monkeypatch):
        app_log = tmp_path / "app.log"
        error_log = tmp_path / "error.log"
        monkeypatch.setattr(app_config, "log_file", str(app_log))
        monkeypatch.setattr(app_config, "log_error_file", str(error_log))
        logger = _call_under_isolated_root()
        logger.setLevel(logging.DEBUG)

        logger.info("info only")
        logger.error("error only")

        for handler in logger.handlers:
            handler.flush()

        app_text = app_log.read_text(encoding="utf-8")
        error_text = error_log.read_text(encoding="utf-8")
        assert "info only" in app_text
        assert "error only" in app_text
        assert "info only" not in error_text
        assert "error only" in error_text

    def test_no_op_when_handlers_already_present(self, tmp_path, monkeypatch):
        monkeypatch.setattr(app_config, "log_file", str(tmp_path / "skip.log"))
        monkeypatch.setattr(app_config, "log_error_file", str(tmp_path / "skip_err.log"))

        fresh = logging.Logger("helicalbi.tests.preloaded")
        fresh.addHandler(logging.NullHandler())
        real_get_logger = logging.getLogger

        def fake_get_logger(name=None):
            if name in (None, ""):
                return fresh
            return real_get_logger(name)

        with patch(
            "helicalbi.common.logging_config.logging.getLogger",
            fake_get_logger,
        ):
            configure_logging()

        assert len(fresh.handlers) == 1
        assert isinstance(fresh.handlers[0], logging.NullHandler)

    def test_respects_log_level_from_config(self, tmp_path, monkeypatch):
        monkeypatch.setattr(app_config, "log_level_name", "INFO")
        monkeypatch.setattr(app_config, "app_debug", False)
        monkeypatch.setattr(app_config, "log_file", str(tmp_path / "info.log"))
        monkeypatch.setattr(app_config, "log_error_file", str(tmp_path / "info_err.log"))
        logger = _call_under_isolated_root()
        assert logger.level == logging.INFO

    def test_debug_level_requires_app_debug(self, tmp_path, monkeypatch):
        monkeypatch.setattr(app_config, "log_level_name", "DEBUG")
        monkeypatch.setattr(app_config, "app_debug", False)
        monkeypatch.setattr(app_config, "log_file", str(tmp_path / "debug.log"))
        monkeypatch.setattr(app_config, "log_error_file", str(tmp_path / "debug_err.log"))
        logger = _call_under_isolated_root()
        assert logger.level == logging.INFO

    def test_debug_level_when_app_debug_and_level_debug(self, tmp_path, monkeypatch):
        monkeypatch.setattr(app_config, "log_level_name", "DEBUG")
        monkeypatch.setattr(app_config, "app_debug", True)
        monkeypatch.setattr(app_config, "log_file", str(tmp_path / "debug.log"))
        monkeypatch.setattr(app_config, "log_error_file", str(tmp_path / "debug_err.log"))
        logger = _call_under_isolated_root()
        assert logger.level == logging.DEBUG

    def test_invalid_log_level_falls_back_to_info(self, tmp_path, monkeypatch):
        monkeypatch.setattr(app_config, "log_level_name", "NOT_A_LEVEL")
        monkeypatch.setattr(app_config, "app_debug", False)
        monkeypatch.setattr(app_config, "log_file", str(tmp_path / "warn.log"))
        monkeypatch.setattr(app_config, "log_error_file", str(tmp_path / "warn_err.log"))
        logger = _call_under_isolated_root()
        assert logger.level == logging.INFO
