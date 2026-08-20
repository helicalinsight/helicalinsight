"""Viz property polish should not replace the SQL insight with a traceback."""

import pytest

from helicalbi.core.vizflow.VizPropertiesPolish import VizPropertiesPolishNode


pytestmark = pytest.mark.functional


def test_polish_llm_failure_preserves_existing_insight(monkeypatch):
    state = {
        "output": "Sales grew 10% this quarter.",
        "thread_id": "t1",
        "query": "sales by city",
        "domain": [],
        "topics": [],
        "viz_hint": "bar",
        "sql_result": {
            "data": [{"city": "NY", "sales": 100}],
            "metadata": [],
        },
        "viz_model": {
            "data": {
                "rows": ["city"],
                "columns": ["sales"],
                "filters": [],
            },
            "chart": {"viz": "Bar", "mark": "Chart"},
            "properties": {"title": "Sales by City"},
        },
    }

    def _fail_invoke(*args, **kwargs):
        raise RuntimeError("ollama failed")

    monkeypatch.setattr(
        "helicalbi.core.vizflow.VizPropertiesPolish.invoke_structured",
        _fail_invoke,
    )
    monkeypatch.setattr(
        "helicalbi.core.vizflow.VizPropertiesPolish.add_viz_response",
        lambda *args, **kwargs: None,
    )

    result = VizPropertiesPolishNode().process_flow(state)

    assert result["output"] == "Sales grew 10% this quarter."
    assert "Traceback" not in result["output"]
    assert result.get("vf_string")
