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


class TestConfigureLogging:
    def test_installs_two_handlers(self, tmp_path, monkeypatch):
        monkeypatch.setattr(app_config, "log_file", str(tmp_path / "logs" / "app.log"))
        logger = _call_under_isolated_root()

        assert len(logger.handlers) == 2
        assert any(
            isinstance(h, logging.StreamHandler)
            and not isinstance(h, TimedRotatingFileHandler)
            for h in logger.handlers
        )
        assert any(
            isinstance(h, TimedRotatingFileHandler) for h in logger.handlers
        )

    def test_creates_log_directory(self, tmp_path, monkeypatch):
        log_file = tmp_path / "fresh_dir" / "app.log"
        assert not log_file.parent.exists()
        monkeypatch.setattr(app_config, "log_file", str(log_file))
        _call_under_isolated_root()
        assert log_file.parent.exists()

    def test_no_op_when_handlers_already_present(self, tmp_path, monkeypatch):
        monkeypatch.setattr(app_config, "log_file", str(tmp_path / "skip.log"))

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
        monkeypatch.setattr(app_config, "log_level_name", "DEBUG")
        monkeypatch.setattr(app_config, "log_file", str(tmp_path / "debug.log"))
        logger = _call_under_isolated_root()
        assert logger.level == logging.DEBUG

    def test_invalid_log_level_falls_back_to_info(self, tmp_path, monkeypatch):
        monkeypatch.setattr(app_config, "log_level_name", "NOT_A_LEVEL")
        monkeypatch.setattr(app_config, "log_file", str(tmp_path / "warn.log"))
        logger = _call_under_isolated_root()
        assert logger.level == logging.INFO
