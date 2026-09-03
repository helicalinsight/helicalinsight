"""Viz property polish should not replace the SQL insight with a traceback."""

import pytest

from helicalbi.core.vizflow.VizPropertiesPolish import VizPropertiesPolishNode
from helicalbi.model.output.viz.VizModel import VizPropertiesPolish


pytestmark = pytest.mark.functional


def _base_state():
    return {
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


def test_polish_llm_failure_preserves_existing_insight(monkeypatch):
    state = _base_state()

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
    assert result["chart_settings"] is not None
    assert "vf_string" not in result or not result.get("vf_string")


def test_polish_success_updates_viz_model_without_vf_string(monkeypatch):
    state = _base_state()

    def _ok_invoke(*args, **kwargs):
        return (
            VizPropertiesPolish(
                color="#5B8FF9",
                background="#ffffff",
                labelX="City",
                labelY="Sales",
            ),
            None,
        )

    monkeypatch.setattr(
        "helicalbi.core.vizflow.VizPropertiesPolish.invoke_structured",
        _ok_invoke,
    )
    monkeypatch.setattr(
        "helicalbi.core.vizflow.VizPropertiesPolish.add_viz_response",
        lambda *args, **kwargs: None,
    )

    result = VizPropertiesPolishNode().process_flow(state)

    assert result["viz_model"]["properties"]["color"] == "#5B8FF9"
    assert result["viz_model"]["properties"]["labelX"] == "City"
    assert result["chart_settings"] is not None
    assert "vf_string" not in result or not result.get("vf_string")
