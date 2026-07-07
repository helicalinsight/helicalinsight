"""Top-level pytest configuration.

The HelicalBI project performs heavy side-effects at import time:

* ``helicalbi.common.configuration`` instantiates an LLM client and immediately
  calls ``llm.invoke("How are you")`` which requires a live Ollama server.
* ``app.py`` and ``GraphBuilderManger.py`` build LangGraph pipelines whose
  nodes resolve LLMs and external services.

To make the tests deterministic and runnable on a developer machine without
any external services, this conftest installs lightweight stubs into
``sys.modules`` BEFORE any production module is imported by collection.
"""
from __future__ import annotations

import os
import sys
import types
from pathlib import Path
from unittest.mock import MagicMock

import pytest


PROJECT_ROOT = Path(__file__).resolve().parent.parent
if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))


def _install_postgres_stub() -> None:
    """Stub the Postgres factory so tests don't require psycopg binaries.

    ``helicalbi.memory.MemoryFactoryResolver`` eagerly imports
    ``PostgresFactory`` which pulls in ``langgraph.checkpoint.postgres`` and
    ``psycopg``. On developer machines without the C/binary libpq wrapper
    that import fails. We install a lightweight stand-in so the resolver
    can still be imported, and tests can still verify ``provider==postgres``
    routing.
    """
    module_name = "helicalbi.memory.storagetype.PostgresFactory"
    if module_name in sys.modules:
        return

    stub = types.ModuleType(module_name)

    class _StubPostgresFactory:
        def __init__(self, config):
            self.config = config

        def create_saver(self):
            return MagicMock(name="stub_postgres_saver")

    stub.PostgresFactory = _StubPostgresFactory
    sys.modules[module_name] = stub


def _install_configuration_stub() -> None:
    """Replace ``helicalbi.common.configuration`` with a stub.

    The real module eagerly builds an LLM client and pings it at import time.
    Tests should never hit a real LLM, so we register a tiny stand-in module
    that exposes the symbols imported elsewhere in the codebase.
    """
    module_name = "helicalbi.common.configuration"
    if module_name in sys.modules:
        return

    stub = types.ModuleType(module_name)
    fake_llm = MagicMock(name="stub_llm")
    fake_llm.invoke.return_value = MagicMock(content="stub-llm-response")
    stub.llm = fake_llm
    stub.llm_manager = MagicMock(name="stub_llm_manager")
    stub.baseUrl = "https://stub.invalid/hi-ee"
    stub.rule_strategy = "basic"
    stub.result_llm = MagicMock(content="stub-llm-response")

    class _ResponseSchema:
        def __init__(self, answer: str = "", confidence: float = 0.0, sources=None):
            self.answer = answer
            self.confidence = confidence
            self.sources = sources or []

    stub.ResponseSchema = _ResponseSchema
    sys.modules[module_name] = stub


_install_postgres_stub()
_install_configuration_stub()


@pytest.fixture
def fake_llm():
    """Provide access to the stubbed module-level LLM for assertions."""
    return sys.modules["helicalbi.common.configuration"].llm


@pytest.fixture
def reset_chat_stores():
    """Clear in-memory chat stores between tests that touch them."""
    from helicalbi.common import ChatManager

    ChatManager.chat_store.clear()
    ChatManager.viz_response_store.clear()
    ChatManager.sql_store.clear()
    ChatManager.prev_insight.clear()
    yield
    ChatManager.chat_store.clear()
    ChatManager.viz_response_store.clear()
    ChatManager.sql_store.clear()
    ChatManager.prev_insight.clear()


@pytest.fixture
def session_cookie():
    return "test-session-cookie"


@pytest.fixture
def session_auth(session_cookie):
    return {"sessionCookie": session_cookie, "username": "tester"}


@pytest.fixture
def sample_cube_metadata():
    return [
        {
            "dimension_name": ["Employee"],
            "unique_identifier_of_table": "employee_id",
            "description": "Employees of the org",
            "database_table": "employee_details",
            "columns": [
                {"column_name": "employee_id", "description": "PK"},
                {"column_name": "employee_name", "description": "Full name"},
            ],
        },
        {
            "dimension_name": ["Client", "Meeting"],
            "unique_identifier_of_table": "meeting_id",
            "description": "Meetings",
            "database_table": "meeting_details",
            "columns": [
                {"column_name": "meeting_id", "description": "PK"},
                {"column_name": "client_name", "description": "Client"},
            ],
        },
    ]


@pytest.fixture
def sample_agent_data(sample_cube_metadata):
    return {
        "domain": [
            {"domain_name": "Sales Operation", "topics": ["Travel", "Meetings"]},
            {"domain_name": "HR", "topics": ["Headcount"]},
        ],
        "topic_mappings": [
            {"topic_name": "Meetings", "component": ["Employee", "Client"]},
            {"topic_name": "Travel", "component": ["Travel", "Employee"]},
            {"topic_name": "Headcount", "component": ["Employee"]},
        ],
        "business_metrics": [
            {
                "metric": "canceled_meetings",
                "description": "Total canceled meetings",
                "tables": ["meeting_details"],
            },
            {
                "metric": "active_employees",
                "description": "Active employees count",
                "tables": ["employee_details"],
            },
        ],
        "cube_metadata": sample_cube_metadata,
    }
