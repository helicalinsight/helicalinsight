"""Tool-calling dashboard agent tests."""

import json

import pytest
from langchain_core.messages import AIMessage, ToolMessage

from helicalbi.sql_agent.database.catalog import ColumnMeta, TableMeta
from helicalbi.sql_agent.database.schema_indexer import SchemaIndexer, clear_indexers, set_indexer
from helicalbi.sql_agent.database.semantic_indexer import clear_semantic_indexers
from helicalbi.sql_agent.dashboard_graph import (
    build_dashboard_agent,
    route_after_tools,
    route_planner,
    route_start,
)
from helicalbi.sql_agent.nodes.dashboard import dashboard_node
from helicalbi.sql_agent.tools.report import report_tools
from helicalbi.sql_agent.models import SynthesizerOutput
from helicalbi.sql_agent.nodes.apply_patches import apply_tool_patches
from helicalbi.sql_agent.nodes.planner import bootstrap_planner_messages
from helicalbi.sql_agent.state import initial_agent_state
from helicalbi.sql_agent.tools import execute_query, retrieve_schema


pytestmark = pytest.mark.functional


@pytest.fixture(autouse=True)
def _reset_indexers():
    clear_indexers()
    clear_semantic_indexers()
    yield
    clear_indexers()
    clear_semantic_indexers()


def _travel_indexer() -> SchemaIndexer:
    indexer = SchemaIndexer()
    indexer.index_tables(
        [
            TableMeta(
                name="travel_details",
                description="Travel cost records",
                columns=[
                    ColumnMeta(name="region", data_type="text"),
                    ColumnMeta(name="cost", data_type="numeric"),
                    ColumnMeta(name="travel_id", is_primary_key=True),
                ],
                primary_keys=["travel_id"],
            )
        ]
    )
    return indexer


def test_retrieve_schema_tool_returns_matching_table():
    set_indexer(_travel_indexer(), "tools-catalog")
    state = initial_agent_state("travel cost", catalog_id="tools-catalog")
    payload = json.loads(retrieve_schema.func("travel cost by region", state))
    assert payload["ok"] is True
    assert "TABLE travel_details" in payload["schema"]
    assert payload["state_patch"]["current_sub_question"] == "travel cost by region"


def test_execute_query_tool_blocks_invalid_column(monkeypatch):
    set_indexer(_travel_indexer(), "tools-catalog")
    called = {"execute": 0}

    def boom(*args, **kwargs):
        called["execute"] += 1
        return {}

    monkeypatch.setattr("helicalbi.sql_agent.instantbi_turn.execute_sql_state", boom)
    state = initial_agent_state("travel cost", catalog_id="tools-catalog", dialect="postgres")
    payload = json.loads(
        execute_query.func("SELECT travel_details.missing FROM travel_details", state)
    )
    assert payload["ok"] is False
    assert "missing" in payload["error"]
    assert called["execute"] == 0


def test_apply_tool_patches_merges_latest_tool_json():
    patch = apply_tool_patches(
        {
            "messages": [
                AIMessage(content="", tool_calls=[{"name": "retrieve_schema", "args": {}, "id": "1"}]),
                ToolMessage(
                    content=json.dumps({"state_patch": {"generated_sql": "SELECT 1", "sql_error": None}}),
                    tool_call_id="1",
                ),
            ]
        }
    )
    assert patch["generated_sql"] == "SELECT 1"
    assert patch["sql_error"] is None


def test_apply_tool_patches_later_tool_wins():
    patch = apply_tool_patches(
        {
            "messages": [
                AIMessage(
                    content="",
                    tool_calls=[
                        {"name": "generate_sql", "args": {}, "id": "1"},
                        {"name": "execute_query", "args": {}, "id": "2"},
                    ],
                ),
                ToolMessage(
                    content=json.dumps(
                        {
                            "state_patch": {
                                "generated_sql": "SELECT 1",
                                "sql_error": "stale",
                                "session_context": {"_last_sql_state": {"sql": "SELECT 1"}},
                            }
                        }
                    ),
                    tool_call_id="1",
                ),
                ToolMessage(
                    content=json.dumps(
                        {
                            "state_patch": {
                                "generated_sql": "SELECT 2",
                                "sql_error": None,
                                "query_result": "ok",
                                "session_context": {"_last_sql_state": {"sql": "SELECT 2", "data": [1]}},
                            }
                        }
                    ),
                    tool_call_id="2",
                ),
            ]
        }
    )
    assert patch["generated_sql"] == "SELECT 2"
    assert patch["sql_error"] is None
    assert patch["query_result"] == "ok"
    assert patch["session_context"]["_last_sql_state"]["data"] == [1]


def test_bootstrap_includes_investigation_plan():
    state = initial_agent_state(
        "Are we meeting targets?",
        investigation_plan={
            "charts": [{"title": "KPI", "question": "What is total revenue vs target?"}]
        },
    )
    messages = bootstrap_planner_messages(state)
    assert "What is total revenue vs target?" in messages[0].content
    assert "PLAN EXECUTION" in messages[0].content


def test_route_planner_tools_when_tool_calls_present():
    message = AIMessage(
        content="",
        tool_calls=[{"name": "retrieve_schema", "args": {"question": "x"}, "id": "1"}],
    )
    assert route_planner({"messages": [message], "tool_loop_count": 1, "is_complete": False}) == "tools"
    assert route_planner({"is_complete": True, "messages": [message], "tool_loop_count": 1}) == "synthesizer"
    assert route_planner({"messages": [AIMessage(content="done")], "tool_loop_count": 2}) == "synthesizer"
    assert (
        route_planner(
            {
                "messages": [message],
                "tool_loop_count": 24,
                "max_tool_loops": 24,
                "is_complete": False,
            }
        )
        == "synthesizer"
    )


def test_route_after_tools_stops_at_loop_cap():
    assert (
        route_after_tools(
            {"is_complete": False, "tool_loop_count": 24, "max_tool_loops": 24}
        )
        == "synthesizer"
    )
    assert (
        route_after_tools(
            {"is_complete": False, "tool_loop_count": 3, "max_tool_loops": 24}
        )
        == "planner"
    )


class _ScriptedLLM:
    def __init__(self, script):
        self.script = list(script)
        self.i = 0

    def bind_tools(self, tools, **kwargs):
        return self

    def invoke(self, input, config=None, **kwargs):
        if self.i >= len(self.script):
            return AIMessage(content="done")
        message = self.script[self.i]
        self.i += 1
        return message


def _ai_tool(name, args, call_id):
    return AIMessage(content="", tool_calls=[{"name": name, "args": args, "id": call_id}])


def test_dashboard_agent_uses_tool_calls(monkeypatch):
    indexer = _travel_indexer()
    set_indexer(indexer, "dash-tools")

    script = [
        _ai_tool("retrieve_schema", {"question": "Travel cost by region"}, "c1"),
        _ai_tool("generate_sql", {"question": "Travel cost by region"}, "c2"),
        _ai_tool(
            "execute_query",
            {"sql": "SELECT travel_details.region, travel_details.cost FROM travel_details"},
            "c3",
        ),
        _ai_tool("build_report", {"question": "Travel cost by region"}, "c4"),
        _ai_tool("finish_dashboard", {"reason": "enough charts"}, "c5"),
    ]
    monkeypatch.setattr(
        "helicalbi.sql_agent.nodes.planner.default_llm",
        _ScriptedLLM(script),
    )
    monkeypatch.setattr(
        "helicalbi.sql_agent.nodes.synthesizer.invoke_agent_model",
        lambda *args, **kwargs: SynthesizerOutput(final_answer="West region drives travel cost."),
    )

    sql_state = {
        "sql": "SELECT travel_details.region, travel_details.cost FROM travel_details",
        "dialect": "postgres",
        "query": "Travel cost by region",
        "token_usage": {},
        "sql_error": "",
    }

    def fake_generate(query, session, **kwargs):
        return dict(sql_state)

    def fake_execute(state, request_id=None):
        result = dict(state)
        result["data"] = [{"region": "West", "cost": 10}]
        result["sql_result"] = {"data": result["data"], "metadata": []}
        result["sql_error"] = ""
        result["output"] = "West is highest"
        return result

    def fake_viz(result, session, **kwargs):
        payload = dict(result)
        payload["viz_hint"] = "bar"
        payload["vf_title"] = "Travel cost by region"
        payload["viz_model"] = {
            "chart": {"viz": "Bar", "mark": "Chart"},
            "data": {"rows": ["region"], "columns": ["cost"]},
        }
        payload["viz_form_data"] = {
            "location": "/meta",
            "metadataFileName": "metadata.json",
            "columns": [{"alias": "region"}, {"alias": "cost"}],
            "sql": sql_state["sql"],
        }
        payload["_chat_response"] = {
            "viz": {"chart_name": "bar", "vf_title": "Travel cost by region"},
            "sql": {"raw_sql": sql_state["sql"], "dialect": "postgres"},
            "summary": {"insight": "West is highest"},
            "report_model": {
                "data_model": payload["viz_form_data"],
                "viz_model": payload["viz_model"],
            },
            "data": payload.get("data") or [],
            "error": "",
        }
        return payload

    monkeypatch.setattr("helicalbi.sql_agent.instantbi_turn.generate_sql_for_question", fake_generate)
    monkeypatch.setattr("helicalbi.sql_agent.instantbi_turn.execute_sql_state", fake_execute)
    monkeypatch.setattr("helicalbi.sql_agent.instantbi_turn.build_viz_for_state", fake_viz)

    def fake_dashboard(payload):
        assert payload["items"]
        assert len(payload["items"]) == 1
        return {
            "items": payload["items"],
            "theme": {"color": "#1677ff"},
            "templateId": "analytical-grid",
            "layout": [{"itemId": payload["items"][0]["component_id"], "x": 0, "y": 0, "w": 6, "h": 4}],
        }

    monkeypatch.setattr("helicalbi.sql_agent.nodes.dashboard.invoke_dashboard_layout", fake_dashboard)

    graph = build_dashboard_agent()
    state = initial_agent_state(
        "Why is travel cost high and how does it break down?",
        catalog_id="dash-tools",
        thread_id="chat-dash",
        chat_seq_id="10",
        build_dashboard=True,
        max_sub_questions=5,
        dialect="postgres",
        session_context={"base_state": {}, "md_location": "/meta", "md_file_name": "metadata.json"},
        schema_overview="TABLE travel_details",
    )
    result = graph.invoke(state, {"recursion_limit": 40})

    assert result["is_complete"] is True
    assert result["final_answer"] == "West region drives travel cost."
    assert result["asked_questions"] == ["Travel cost by region"]
    assert result["attempt_count"] >= 5
    assert result["dashboard"]["templateId"] == "analytical-grid"
    charts = [step for step in result["collected_data"] if step.get("include_in_dashboard")]
    assert len(charts) == 1
    assert charts[0]["report_model"]["viz_model"]["chart"]["viz"] == "Bar"
    names = [
        call["name"]
        for message in result["messages"]
        if isinstance(message, AIMessage) and message.tool_calls
        for call in message.tool_calls
    ]
    assert names == [
        "retrieve_schema",
        "generate_sql",
        "execute_query",
        "build_report",
        "finish_dashboard",
    ]


def test_route_start_uses_execute_plan_when_charts_present():
    assert route_start({"investigation_plan": {"charts": [{"question": "What is total spend?"}]}}) == "execute_plan"
    assert route_start({"investigation_plan": {"charts": [{"title": "No question"}]}}) == "planner"
    assert route_start({"investigation_plan": {}}) == "planner"
    assert route_start({}) == "planner"


def _sql_state(query: str) -> dict:
    return {
        "sql": "SELECT travel_details.region, travel_details.cost FROM travel_details",
        "dialect": "postgres",
        "query": query,
        "token_usage": {},
        "sql_error": "",
    }


def _fake_viz_for(query: str):
    def fake_viz(result, session, **kwargs):
        payload = dict(result)
        payload["viz_hint"] = result.get("viz_hint") or "bar"
        payload["vf_title"] = query
        payload["viz_model"] = {
            "chart": {"viz": "Bar", "mark": "Chart"},
            "data": {"rows": ["region"], "columns": ["cost"]},
        }
        payload["viz_form_data"] = {
            "location": "/meta",
            "metadataFileName": "metadata.json",
            "columns": [{"alias": "region"}, {"alias": "cost"}],
            "sql": payload.get("sql") or "",
        }
        payload["_chat_response"] = {
            "viz": {"chart_name": "bar", "vf_title": query},
            "sql": {"raw_sql": payload.get("sql") or "", "dialect": "postgres"},
            "summary": {"insight": f"Result for {query}"},
            "report_model": {
                "data_model": payload["viz_form_data"],
                "viz_model": payload["viz_model"],
            },
            "data": payload.get("data") or [{"region": "West", "cost": 10}],
            "error": "",
        }
        return payload

    return fake_viz


def test_execute_plan_runs_charts_in_order(monkeypatch):
    set_indexer(_travel_indexer(), "dash-plan")
    asked = []
    topics_seen = []

    def fake_generate(query, session, **kwargs):
        asked.append(query)
        topics_seen.append(list((kwargs.get("agent_context") or {}).get("selected_topics") or []))
        return _sql_state(query)

    def fake_execute(state, request_id=None):
        result = dict(state)
        result["data"] = [{"region": "West", "cost": 10}]
        result["sql_result"] = {"data": result["data"], "metadata": []}
        result["sql_error"] = ""
        return result

    monkeypatch.setattr("helicalbi.sql_agent.instantbi_turn.generate_sql_for_question", fake_generate)
    monkeypatch.setattr("helicalbi.sql_agent.instantbi_turn.execute_sql_state", fake_execute)
    monkeypatch.setattr(
        "helicalbi.sql_agent.instantbi_turn.build_viz_for_state",
        lambda result, session, **kwargs: _fake_viz_for(result.get("query") or "")(result, session, **kwargs),
    )
    monkeypatch.setattr(
        "helicalbi.sql_agent.nodes.synthesizer.invoke_agent_model",
        lambda *args, **kwargs: SynthesizerOutput(final_answer="Spend rose on expensive routes."),
    )

    def fake_dashboard(payload):
        items = []
        for item in payload["items"]:
            items.append(
                {
                    **item,
                    "dashboard_model": {"kind": "viz", "title": item.get("user_query") or ""},
                    "report_model": {
                        "data_model": item.get("data_model") or {},
                        "viz_model": item.get("viz_model") or {"chart": {"viz": "Bar"}},
                    },
                }
            )
        extras = [
            {"component_id": "kpiFake", "dashboard_model": {"kind": "kpi", "title": "Empty KPI", "html": ""}},
            {
                "component_id": "sum1",
                "dashboard_model": {"kind": "summary", "title": "Overview", "html": "Travel spend overview"},
            },
            {"component_id": "svgFake", "dashboard_model": {"kind": "svg", "title": "Divider", "html": "<svg></svg>"}},
        ]
        return {
            "items": extras + items,
            "theme": {"color": "#1677ff"},
            "templateId": "analytical-grid",
            "layout": [
                {"itemId": "kpiFake", "component_id": "kpiFake"},
                {"itemId": items[0]["id"], "component_id": items[0]["component_id"]},
                {"itemId": items[1]["id"], "component_id": items[1]["component_id"]},
            ],
        }

    monkeypatch.setattr("helicalbi.sql_agent.nodes.dashboard.invoke_dashboard_layout", fake_dashboard)

    graph = build_dashboard_agent()
    state = initial_agent_state(
        "Why did our travel expenses increase/decrease, and what should we do about it?",
        catalog_id="dash-plan",
        thread_id="chat-plan",
        chat_seq_id="1",
        build_dashboard=True,
        max_sub_questions=5,
        dialect="postgres",
        session_context={"base_state": {}, "md_location": "/meta", "md_file_name": "metadata.json"},
        schema_overview="TABLE travel_details",
        investigation_plan={
            "domain": "Travel Spend",
            "topics": ["Trip Economics", "Travel Routes"],
            "charts": [
                {
                    "title": "Total Travel Spend",
                    "question": "What is the total travel spend for the current period compared to last period?",
                    "viz_hint": "kpi",
                },
                {
                    "title": "Route Efficiency",
                    "question": "What is the average cost per mile and cost per route for our top travel routes?",
                    "viz_hint": "bar",
                },
            ],
        },
    )
    result = graph.invoke(state, {"recursion_limit": 40})

    assert asked == [
        "What is the total travel spend for the current period compared to last period?",
        "What is the average cost per mile and cost per route for our top travel routes?",
    ]
    assert topics_seen[0] == ["Trip Economics", "Travel Routes"]
    seqs = [step["chat_seq_id"] for step in result["collected_data"]]
    assert seqs == ["1-1", "1-2"]
    assert result["attempt_count"] < 24
    assert result["final_answer"] == "Spend rose on expensive routes."
    kinds = [item["dashboard_model"]["kind"] for item in result["dashboard"]["items"]]
    assert "kpi" not in kinds
    assert "svg" not in kinds
    assert "summary" in kinds
    assert kinds.count("viz") == 2
    planner_calls = [
        call["name"]
        for message in result["messages"]
        if isinstance(message, AIMessage) and message.tool_calls
        for call in message.tool_calls
    ]
    assert planner_calls == []


def test_execute_plan_continues_after_failed_chart(monkeypatch):
    set_indexer(_travel_indexer(), "dash-plan-fail")

    def fake_generate(query, session, **kwargs):
        if "fail" in query.lower():
            return {"sql": "", "sql_error": "cannot generate", "token_usage": {}}
        return _sql_state(query)

    def fake_execute(state, request_id=None):
        result = dict(state)
        result["data"] = [{"region": "West", "cost": 10}]
        result["sql_result"] = {"data": result["data"], "metadata": []}
        result["sql_error"] = ""
        return result

    monkeypatch.setattr("helicalbi.sql_agent.instantbi_turn.generate_sql_for_question", fake_generate)
    monkeypatch.setattr("helicalbi.sql_agent.instantbi_turn.execute_sql_state", fake_execute)
    monkeypatch.setattr(
        "helicalbi.sql_agent.instantbi_turn.build_viz_for_state",
        lambda result, session, **kwargs: _fake_viz_for(result.get("query") or "")(result, session, **kwargs),
    )
    monkeypatch.setattr(
        "helicalbi.sql_agent.nodes.synthesizer.invoke_agent_model",
        lambda *args, **kwargs: SynthesizerOutput(final_answer="Partial picture."),
    )
    monkeypatch.setattr(
        "helicalbi.sql_agent.nodes.dashboard.invoke_dashboard_layout",
        lambda payload: {
            "items": payload["items"],
            "theme": {},
            "templateId": "analytical-grid",
            "layout": [],
        },
    )

    graph = build_dashboard_agent()
    state = initial_agent_state(
        "Why did spend change?",
        catalog_id="dash-plan-fail",
        thread_id="chat-plan-fail",
        chat_seq_id="1",
        build_dashboard=True,
        dialect="postgres",
        session_context={"base_state": {}, "md_location": "/meta", "md_file_name": "metadata.json"},
        investigation_plan={
            "charts": [
                {"title": "Broken", "question": "This should fail to generate"},
                {"title": "Ok", "question": "What is total travel spend?"},
            ]
        },
    )
    result = graph.invoke(state, {"recursion_limit": 40})
    assert len(result["collected_data"]) == 2
    assert result["collected_data"][0]["include_in_dashboard"] is False
    assert result["collected_data"][1]["include_in_dashboard"] is True
    assert result["collected_data"][0]["chat_seq_id"] != result["collected_data"][1]["chat_seq_id"]


def test_build_report_does_not_reuse_collected_seq(monkeypatch):
    monkeypatch.setattr(
        "helicalbi.sql_agent.instantbi_turn.build_viz_for_state",
        lambda result, session, **kwargs: _fake_viz_for("second")(result, session, **kwargs),
    )
    state = initial_agent_state(
        "q",
        chat_seq_id="1",
        session_context={"_last_sql_state": {"sql": "SELECT 1", "sql_error": ""}},
    )
    state["current_chat_seq_id"] = "1-1"
    state["collected_data"] = [
        {"sub_question": "first", "chat_seq_id": "1-1", "include_in_dashboard": True}
    ]
    payload = report_tools.build("second question", state)
    assert payload["ok"] is True
    assert payload["state_patch"]["collected_data"][-1]["chat_seq_id"] == "1-2"


def test_dashboard_node_skips_empty_viz_model(monkeypatch):
    monkeypatch.setattr(
        "helicalbi.sql_agent.nodes.dashboard.invoke_dashboard_layout",
        lambda payload: {
            "items": payload["items"],
            "theme": {},
            "templateId": "analytical-grid",
            "layout": [],
        },
    )
    result = dashboard_node(
        {
            "thread_id": "t-empty",
            "original_question": "why",
            "collected_data": [
                {
                    "sub_question": "empty chart",
                    "chat_seq_id": "1-1",
                    "include_in_dashboard": True,
                    "report_model": {
                        "data_model": {"query": "SELECT 1", "columns": []},
                        "viz_model": None,
                    },
                    "chat_response": {
                        "viz": {"chart_name": "", "vf_title": ""},
                        "sql": {"raw_sql": "SELECT 1"},
                        "report_model": {"data_model": {"query": "SELECT 1"}, "viz_model": None},
                        "summary": {"insight": ""},
                        "error": "",
                    },
                }
            ],
        }
    )
    assert result["dashboard"]["items"] == []
    assert "No visualizations" in result["dashboard"]["error"]
