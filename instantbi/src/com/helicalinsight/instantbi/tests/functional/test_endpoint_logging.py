"""Functional tests for endpoint request logging in ``helicalbi.controller.helpers``."""
import logging

import pytest

from helicalbi.controller.helpers import log_endpoint_input
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
        with caplog.at_level(logging.INFO, logger="helicalbi.controller.helpers"):
            log_endpoint_input("/interactive", {"inputString": "hello"})
        assert "Endpoint /interactive Input:" not in caplog.text

    def test_log_endpoint_input_emitted_when_enabled(self, caplog):
        app_config.show_endpoint_log = True
        with caplog.at_level(logging.INFO, logger="helicalbi.controller.helpers"):
            log_endpoint_input("/interactive", {"inputString": "hello"})
        assert "Endpoint /interactive Input:" in caplog.text
        assert '"inputString": "hello"' in caplog.text


class TestRmColsInFilterParam:
    def test_as_request_bool_defaults_and_parses_false(self):
        from helicalbi.controller.helpers import as_request_bool

        assert as_request_bool(None) is True
        assert as_request_bool("false") is False
        assert as_request_bool("0") is False
        assert as_request_bool("true") is True
        assert as_request_bool(False) is False

    def test_resolve_query_param_and_json_body(self):
        from flask import Flask

        from helicalbi.controller.helpers import resolve_rm_cols_in_filter

        app = Flask(__name__)
        with app.test_request_context("/interactive?rm_cols_in_filter=false"):
            assert resolve_rm_cols_in_filter() is False
        with app.test_request_context("/interactive"):
            assert resolve_rm_cols_in_filter() is False
        with app.test_request_context("/interactive"):
            assert resolve_rm_cols_in_filter({"input": {}}, {"rm_cols_in_filter": False}) is False
        with app.test_request_context("/interactive?rm_cols_in_filter=true"):
            assert resolve_rm_cols_in_filter({"rm_cols_in_filter": False}) is True

    def test_resolve_uses_application_config_until_request_overrides(self, monkeypatch):
        from flask import Flask

        from helicalbi.common import app_config
        from helicalbi.controller.helpers import resolve_rm_cols_in_filter

        monkeypatch.setattr(app_config, "rm_cols_in_filter", False)
        app = Flask(__name__)
        with app.test_request_context("/interactive"):
            assert resolve_rm_cols_in_filter() is False
        with app.test_request_context("/interactive?rm_cols_in_filter=true"):
            assert resolve_rm_cols_in_filter() is True

