"""Functional tests for endpoint request logging in ``bl.helpers``."""
import logging

import pytest

from bl.helpers import log_endpoint_input
from helicalbi.common import app_config


pytestmark = pytest.mark.functional


@pytest.fixture(autouse=True)
def _restore_endpoint_log_setting():
    original = app_config.show_endpoint_log
    yield
    app_config.show_endpoint_log = original


class TestEndpointLogging:
    def test_log_endpoint_input_skipped_when_disabled(self, caplog):
        app_config.show_endpoint_log = False
        with caplog.at_level(logging.INFO, logger="bl.helpers"):
            log_endpoint_input("/interactive", {"inputString": "hello"})
        assert "Endpoint /interactive Input:" not in caplog.text

    def test_log_endpoint_input_emitted_when_enabled(self, caplog):
        app_config.show_endpoint_log = True
        with caplog.at_level(logging.INFO, logger="bl.helpers"):
            log_endpoint_input("/interactive", {"inputString": "hello"})
        assert "Endpoint /interactive Input:" in caplog.text
        assert '"inputString": "hello"' in caplog.text
