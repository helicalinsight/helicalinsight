"""Semantic-model RAG tests."""

import json

import pytest

from helicalbi.sql_agent.database.catalog import ColumnMeta, TableMeta
from helicalbi.sql_agent.database.schema_indexer import SchemaIndexer, clear_indexers, set_indexer
from helicalbi.sql_agent.database.semantic_indexer import (
    SemanticLayerIndexer,
    clear_semantic_indexers,
    set_semantic_indexer,
)
from helicalbi.sql_agent.state import initial_agent_state
from helicalbi.sql_agent.tools.semantic import retrieve_semantic_model


pytestmark = pytest.mark.functional


@pytest.fixture(autouse=True)
def _reset_indexers():
    clear_indexers()
    clear_semantic_indexers()
    yield
    clear_indexers()
    clear_semantic_indexers()


def _travel_model():
    return {
        "domain": [
            {
                "domain_name": "Sales Operation",
                "description": "Travel spend and bookings",
                "topics": [
                    {
                        "topic": "Travel",
                        "description": "Cost of employee travel by region",
                        "query_explanation": "use travel cost, not lodging only",
                        "components": [{"name": "Travel Cost", "kind": "measure"}],
                    }
                ],
            }
        ],
        "business_metrics": [
            {
                "metric": "travel_cost",
                "description": "Sum of travel cost",
                "tables": ["travel_details"],
            }
        ],
        "cube_info": [
            {
                "cubeName": "Travel Cube",
                "measures": [
                    {
                        "measureName": "Travel Cost",
                        "columnName": "cost",
                        "description": "Total travel cost",
                        "aiContext": {
                            "instructions": "sum cost; do not average",
                            "examples": "travel cost by region",
                            "synonyms": "spend, travel spend",
                        },
                    }
                ],
                "dimensions": [],
            }
        ],
    }


def test_semantic_retrieve_picks_topic_definition_and_query_explanation():
    indexer = SemanticLayerIndexer()
    indexer.index_model(_travel_model())
    overview = indexer.overview()
    assert "Sales Operation" in overview
    assert "Travel" in overview

    hit = indexer.retrieve("why is travel cost high by region")
    assert hit["sufficient"] is True
    assert "Travel" in hit["topics"]
    assert "definition:" in hit["prompt"]
    assert "travel cost" in hit["prompt"].lower()


def test_retrieve_semantic_model_falls_back_to_metadata_when_empty():
    set_semantic_indexer(SemanticLayerIndexer(), "sem-fallback")
    schema = SchemaIndexer()
    schema.index_tables(
        [
            TableMeta(
                name="travel_details",
                columns=[ColumnMeta(name="cost", data_type="numeric")],
            )
        ]
    )
    set_indexer(schema, "sem-fallback")
    state = initial_agent_state("unrelated xyzzy question", catalog_id="sem-fallback")
    payload = json.loads(retrieve_semantic_model.func("unrelated xyzzy question", state))
    assert payload["sufficient"] is False
    assert payload["used_metadata_fallback"] is True
    assert "TABLE travel_details" in payload["schema"]


def test_retrieve_semantic_model_skips_metadata_when_topics_match():
    semantic = SemanticLayerIndexer()
    semantic.index_model(_travel_model())
    set_semantic_indexer(semantic, "sem-hit")
    state = initial_agent_state("travel cost by region", catalog_id="sem-hit")
    payload = json.loads(retrieve_semantic_model.func("travel cost by region", state))
    assert payload["sufficient"] is True
    assert payload["used_metadata_fallback"] is False
    assert "Travel" in payload["topics"]
    assert payload["state_patch"]["selected_topics"]
