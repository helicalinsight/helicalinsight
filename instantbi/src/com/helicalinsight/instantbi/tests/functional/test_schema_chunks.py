"""Chunk size for table and column prompts."""
from types import SimpleNamespace
from unittest.mock import patch

import pytest

from helicalbi.common import app_config
from helicalbi.core.sqlflow.FindTablesFromTopics import FindTablesFromTopics
from helicalbi.core.sqlflow.GetColumnNames import GetColumnNames
from helicalbi.core.sqlflow.util.SchemaChunks import merge_column_plans, split_chunks

pytestmark = pytest.mark.functional


def test_split_chunks_uses_configured_size(monkeypatch):
    monkeypatch.setattr(app_config, "schema_chunk_size", 50)
    rows = [f"t{index}" for index in range(120)]
    groups = split_chunks(rows)
    assert [len(group) for group in groups] == [50, 50, 20]


def test_split_chunks_keeps_a_short_list_together():
    assert split_chunks(["a", "b"], size=50) == [["a", "b"]]


def test_merge_column_plans_unions_names_and_keeps_first_limit():
    merged = merge_column_plans(
        [
            {"columnName": ["a.x"], "selectColumnName": ["a.x"], "limit": 10, "reason": "one"},
            {"columnName": ["b.y", "a.x"], "selectColumnName": ["b.y"], "limit": 5, "reason": "two"},
        ]
    )
    assert merged["columnName"] == ["a.x", "b.y"]
    assert merged["selectColumnName"] == ["a.x", "b.y"]
    assert merged["limit"] == 10
    assert merged["reason"] == "one two"


def test_table_selection_calls_once_per_chunk(monkeypatch):
    monkeypatch.setattr(app_config, "schema_chunk_size", 2)
    cubes = [
        {"database_table": "one", "columns": [{"column_name": "id"}]},
        {"database_table": "two", "columns": [{"column_name": "id"}]},
        {"database_table": "three", "columns": [{"column_name": "id"}]},
    ]
    seen = []

    def _invoke(_prompt, _llm, _parser, inputs, state=None):
        text = inputs["table_column_description"]
        picked = [name for name in ("one", "two", "three") if name in text]
        seen.append(picked)
        return SimpleNamespace(required_tables=picked[:1]), None

    with patch(
        "helicalbi.core.sqlflow.FindTablesFromTopics.invoke_structured",
        side_effect=_invoke,
    ):
        selected = FindTablesFromTopics()._select_tables_via_llm(
            {"query": "count", "last_chats": [], "topics": [], "relationship_of_table": []},
            cubes,
            {},
            ["one", "two", "three"],
        )

    assert seen == [["one", "two"], ["three"]]
    assert selected == ["one", "three"]


def test_column_selection_merges_chunks(monkeypatch):
    monkeypatch.setattr(app_config, "schema_chunk_size", 2)
    cubes = [
        {
            "database_table": "travel_details",
            "columns": [
                {"column_name": "travel_id"},
                {"column_name": "travel_cost"},
                {"column_name": "travel_date"},
            ],
            "measures": [],
        }
    ]
    calls = {"n": 0}

    def _invoke(_prompt, _llm, _parser, inputs, state=None):
        calls["n"] += 1
        text = inputs["table_column_description"]
        names = [
            f"travel_details.{name}"
            for name in ("travel_id", "travel_cost", "travel_date")
            if name in text
        ]
        return SimpleNamespace(
            model_dump=lambda names=names: {
                "columnName": names,
                "selectColumnName": names[:1],
                "pickedDimensions": [],
                "pickedMetrics": [],
                "limit": None,
                "reason": "",
            }
        ), None

    state = {
        "query": "cost",
        "required_tables": ["travel_details"],
        "required_synonyms": [],
        "last_chats": [],
        "cube_metadata": cubes,
        "relationship_of_table": [],
        "topic_mappings": [],
        "domain_context": "",
        "domain": [],
        "topics": [],
    }
    with patch(
        "helicalbi.core.sqlflow.GetColumnNames.invoke_structured",
        side_effect=_invoke,
    ):
        result = GetColumnNames().process_flow(state)

    assert calls["n"] == 2
    import json
    plan = json.loads(result["query_plan"])
    assert plan["columnName"] == [
        "travel_details.travel_id",
        "travel_details.travel_cost",
        "travel_details.travel_date",
    ]
