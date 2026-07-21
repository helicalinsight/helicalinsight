"""Fixtures for LLM prompt tests.

The model schema under test is the one defined in
``helicalbi.api.TravelDetailsCube`` (Travel + Meetings domain).  We expose
the raw lists/dicts as pytest fixtures along with a fully-assembled
``model_data`` payload that mirrors what
``ModelLayerHelper.get_model_semantic_layer()`` returns at runtime so the
existing :class:`InformationProvider` plumbing can be reused inside the
tests.

These fixtures live in their own ``tests/llm`` package so that:

* the heavier ``deepeval`` dependency is only imported when this folder is
  collected, and
* the schema fixtures stay separate from the lightweight functional
  fixtures in ``tests/conftest.py``.
"""
from __future__ import annotations

import os

import pytest

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


@pytest.fixture(scope="session")
def travel_business_metrics():
    return business_metrics


@pytest.fixture(scope="session")
def travel_synonyms():
    return synonyms


@pytest.fixture(scope="session")
def travel_examples():
    return examples


@pytest.fixture(scope="session")
def travel_metadata_info():
    return metadata_info


@pytest.fixture(scope="session")
def travel_domain():
    return domain


@pytest.fixture(scope="session")
def travel_topic_mappings():
    return topic_mappings


@pytest.fixture(scope="session")
def travel_cube_metadata():
    return cube_metadata


@pytest.fixture(scope="session")
def travel_relationships():
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
