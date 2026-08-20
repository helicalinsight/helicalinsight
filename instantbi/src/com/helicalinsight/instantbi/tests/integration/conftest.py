"""Integration test fixtures.

Importing ``app`` runs side effects: it builds LangGraph pipelines and
prepares Flask routes. The top-level ``tests/conftest.py`` already stubs
``helicalbi.common.configuration`` so that no real LLM is contacted during
the build phase. Here we additionally patch the compiled graphs so that
end-to-end tests can drive them deterministically.
"""
from unittest.mock import MagicMock, patch

import pytest


@pytest.fixture(scope="session")
def app_module():
    """Import ``app`` once for the whole session.

    The module-level graph builds are expensive; we re-use the same module
    across tests and patch its attributes per-test.
    """
    import importlib

    import app as app_module

    importlib.reload(app_module)
    return app_module


@pytest.fixture
def flask_client(app_module):
    app_module.app.config["TESTING"] = True
    with app_module.app.test_client() as client:
        yield client


@pytest.fixture
def patch_graphs(app_module):
    """Replace ``main_graph`` and ``viz_graph`` with predictable mocks."""
    main_mock = MagicMock(name="main_graph")
    viz_mock = MagicMock(name="viz_graph")

    main_mock.invoke.return_value = {
        "sql": "SELECT 1",
        "flow": ["[A1B]"],
        "messages": [],
        "sql_result": {"data": [{"a": 1}], "metadata": [{}]},
    }
    viz_mock.invoke.return_value = {
        "sql": "SELECT 1",
        "flow": ["[A1B]"],
        "messages": [],
        "sql_result": {"data": [{"a": 1}], "metadata": [{}]},
        "visualization": "bar",
        "vf_string": "function(){}",
        "vf_title": "Sales",
    }

    with patch.object(app_module, "main_graph", main_mock), patch.object(
        app_module, "viz_graph", viz_mock
    ):
        yield main_mock, viz_mock
