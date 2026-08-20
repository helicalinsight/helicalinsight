"""Fixtures for LLM prompt tests.

Travel + Meetings cube fixtures historically came from
``helicalbi.api.TravelDetailsCube``. That module may be absent in some
checkouts; fixtures that need it are skipped gracefully.

Travle_Agent connection settings are always loaded via
``tests.llm.llm_test_settings`` so SQL/viz scenarios can run independently.
"""
from __future__ import annotations

import os

import pytest

from tests.llm.llm_test_settings import apply_llm_test_env

try:
    from helicalbi.api.TravelDetailsCube import (
        business_metrics,
        cube_metadata,
        domain,
        examples,
        metadata_info,
        relationships,
        synonyms,
        topic_mappings,
    )

    _TRAVEL_CUBE_AVAILABLE = True
except ImportError:  # pragma: no cover - optional legacy cube module
    business_metrics = None
    cube_metadata = None
    domain = None
    examples = None
    metadata_info = None
    relationships = None
    synonyms = None
    topic_mappings = None
    _TRAVEL_CUBE_AVAILABLE = False


def _require_travel_cube():
    if not _TRAVEL_CUBE_AVAILABLE:
        pytest.skip("helicalbi.api.TravelDetailsCube is not available")


@pytest.fixture(scope="session")
def travel_business_metrics():
    _require_travel_cube()
    return business_metrics


@pytest.fixture(scope="session")
def travel_synonyms():
    _require_travel_cube()
    return synonyms


@pytest.fixture(scope="session")
def travel_examples():
    _require_travel_cube()
    return examples


@pytest.fixture(scope="session")
def travel_metadata_info():
    _require_travel_cube()
    return metadata_info


@pytest.fixture(scope="session")
def travel_domain():
    _require_travel_cube()
    return domain


@pytest.fixture(scope="session")
def travel_topic_mappings():
    _require_travel_cube()
    return topic_mappings


@pytest.fixture(scope="session")
def travel_cube_metadata():
    _require_travel_cube()
    return cube_metadata


@pytest.fixture(scope="session")
def travel_relationships():
    _require_travel_cube()
    return relationships


@pytest.fixture(scope="session")
def travel_model_data(
    travel_domain,
    travel_topic_mappings,
    travel_business_metrics,
    travel_cube_metadata,
    travel_synonyms,
    travel_examples,
    travel_relationships,
):
    """Model semantic layer shaped like the runtime ``model_data`` payload."""
    return {
        "domain": travel_domain,
        "topic_mappings": travel_topic_mappings,
        "business_metrics": travel_business_metrics,
        "cube_metadata": travel_cube_metadata,
        "synonyms": travel_synonyms,
        "examples": travel_examples,
        "relationships": travel_relationships,
    }


@pytest.fixture(scope="session")
def llm_mode() -> str:
    """``stub`` runs deterministic recorded outputs, ``live`` calls the real LLM.

    Defaults to ``stub`` so that the suite can be run on CI / dev machines
    without any LLM credentials.  Set ``HELICALBI_LLM_MODE=live`` to drive
    the configured Ollama / OpenAI provider end-to-end.
    """
    return os.environ.get("HELICALBI_LLM_MODE", "stub").strip().lower()


@pytest.fixture(scope="session", autouse=True)
def _apply_travle_llm_test_env():
    """Load Travle_Agent test config (base URL, cookie, OpenAI key) into env."""
    apply_llm_test_env()
