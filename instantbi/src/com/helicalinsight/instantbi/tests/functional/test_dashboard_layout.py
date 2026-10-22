"""Functional tests for convert-dashboard layout clamp and item collection."""
from __future__ import annotations

import importlib

import pytest

collect_items_mod = importlib.import_module("helicalbi.core.dashboardflow.collect_items")
from helicalbi.common.ChatGraphMemory import ChatGraphMemory
from helicalbi.core.dashboardflow.collect_items import collect_items, item_cards, normalize_item
from helicalbi.core.dashboardflow.grid_clamp import apply_decision, clamp_rect, default_layout, resolve_overlaps
from helicalbi.model.output.dashboard.DashboardLayout import DashboardLayoutDecision


pytestmark = pytest.mark.functional


def _item(item_id: str, chart: str = "bar", **extra):
    payload = {
        "id": item_id,
        "chat_sequence_id": item_id,
        "component_id": extra.get("component_id") or item_id,
        "sql": f"SELECT region, sum(sales) FROM t_{item_id}",
        "viz": {
            "chart_name": chart,
            "viz_model": {
                "chart": {
                    "mark": "Chart" if chart != "kpi" else "Card",
                    "viz": "Bar" if chart != "kpi" else "KPI",
                },
                "data": {"rows": ["region"], "columns": ["sales"], "filters": [{"name": "region", "value": "West"}]},
                "properties": {"title": f"Chart {item_id}", "color": "#1677ff", "background": "#ffffff"},
            },
        },
        "summary": f"Insight {item_id}",
    }
    payload.update(extra)
    payload.setdefault("component_id", item_id)
    payload.setdefault("chat_sequence_id", item_id)
    return payload


class TestDashboardLayoutModels:
    def test_decision_defaults_are_parts_not_efwdd(self):
        model = DashboardLayoutDecision()
        dumped = model.model_dump()
        assert "gridItemsData" not in dumped
        assert "sql_parts" not in dumped
        assert dumped["dashboard_config"]["theme"]["background"].startswith("#")
        assert dumped["dashboard_config"]["parameters"]["orientation"] == "right"
        assert dumped["widgets"] == []
        assert dumped["templateId"] == ""
        assert "theme" not in dumped
        assert "parameters" not in dumped


class TestLayoutTemplates:
    def test_catalog_loads_all_ascii_layout_files(self):
        from helicalbi.prompt.LayoutTemplates import (
            KNOWN_TEMPLATE_IDS,
            load_decision_table,
            load_layout_catalog,
            list_template_ids,
        )

        table = load_decision_table()
        catalog = load_layout_catalog()
        ids = list_template_ids()
        assert set(ids) == set(KNOWN_TEMPLATE_IDS)
        for template_id in KNOWN_TEMPLATE_IDS:
            assert template_id in table
            assert f"id: {template_id}" in catalog
        assert "┌" in catalog

    def test_layout_prompt_asks_llm_to_pick_a_template(self):
        from helicalbi.prompt.DashboardLayoutPrompt import dashboard_layout_prompt_string

        assert "{decision_table}" in dashboard_layout_prompt_string
        assert "{layout_catalog}" in dashboard_layout_prompt_string
        assert "Choose exactly one templateId" in dashboard_layout_prompt_string or "Use the planned templateId" in dashboard_layout_prompt_string
        assert "component_id" in dashboard_layout_prompt_string
        assert "user_query" in dashboard_layout_prompt_string
        assert "kind=filter" in dashboard_layout_prompt_string
        assert "instant.filter" in dashboard_layout_prompt_string
        assert "kind=kpi" in dashboard_layout_prompt_string
        assert "kind=svg" in dashboard_layout_prompt_string
        assert "kind=summary" in dashboard_layout_prompt_string
        assert "HTML fragment" in dashboard_layout_prompt_string or "<p>" in dashboard_layout_prompt_string
        assert "REQUIRED EXTRA TILES" in dashboard_layout_prompt_string
        assert "{layout_plan}" in dashboard_layout_prompt_string
        assert "{filter_components}" in dashboard_layout_prompt_string


class TestGridClamp:
    def test_clamp_keeps_widget_inside_12_columns(self):
        x, y, w, h = clamp_rect(-2, -1, 20, 0)
        assert x >= 0
        assert y >= 0
        assert w <= 12
        assert h >= 1
        assert x + w <= 12

    def test_resolve_overlaps_pushes_second_widget_down(self):
        a = {"x": 0, "y": 0, "w": 6, "h": 4, "itemId": "a"}
        b = {"x": 0, "y": 0, "w": 6, "h": 4, "itemId": "b"}
        result = resolve_overlaps([a, b])
        first, second = result[0], result[1]
        assert not (
            first["x"] < second["x"] + second["w"]
            and first["x"] + first["w"] > second["x"]
            and first["y"] < second["y"] + second["h"]
            and first["y"] + first["h"] > second["y"]
        )

    def test_default_layout_covers_every_item(self):
        items = [_item("seq-1"), _item("seq-2", "kpi")]
        layout = default_layout(items)
        ids = {row["component_id"] for row in layout["widgets"] if row.get("kind") == "viz"}
        assert ids == {"seq-1", "seq-2"}
        assert layout["dashboard_config"]["theme"]["background"]

    def test_apply_decision_fills_missing_item_ids(self):
        items = [_item("a"), _item("b")]
        clamped = apply_decision(
            items,
            {
                "widgets": [{"kind": "viz", "component_id": "a", "x": 0, "y": 2, "w": 6, "h": 4}],
                "dashboard_config": {
                    "theme": {
                        "id": "mono",
                        "name": "Mono",
                        "background": "#f4f5f6",
                        "dashboardComponent": "#ffffff",
                        "componentHeader": "#111418",
                        "textPrimary": "#181b20",
                        "borderColor": "#e0e2e5",
                        "horizontalMargin": 10,
                        "verticalMargin": 10,
                    }
                },
            },
        )
        ids = [
            row["component_id"]
            for row in clamped["items"]
            if row.get("kind") == "instant"
        ]
        assert "a" in ids
        assert "b" in ids
        assert clamped["dashboard_config"]["theme"]["componentHeader"] == "#111418"
        assert "theme" not in clamped
        assert "header" not in clamped
        assert clamped["sections"] == []
        assert clamped["title"]
        assert "orientation" in clamped["dashboard_config"]["parameters"]
        assert clamped["dashboard_config"]["parameters"]["floatingFilter"] is True
        viz_a = next(row for row in clamped["items"] if row["component_id"] == "a")
        assert viz_a["chat_sequence_id"] == "a"
        assert viz_a["itemId"] == "a"
        assert "layout" not in clamped
        assert viz_a["summary"] == {"insight": "Insight a"}
        assert "chatid" not in viz_a
        assert viz_a["id"] == "a"
        assert "dashboard_model" not in viz_a
        assert viz_a["layout"]["w"] == 6
        assert "header" not in viz_a
        assert "export" not in viz_a
        assert "css" not in viz_a
        assert "js" not in viz_a
        assert "html" not in viz_a
        assert "sql_parts" not in viz_a
        kinds = {
            row.get("kind")
            for row in clamped["items"]
            if row.get("kind")
        }
        assert {"instant", "text", "instant.filter", "kpi", "svg"} <= kinds
        text = next(row for row in clamped["items"] if row.get("kind") == "text")
        assert "chat_sequence_id" not in text

    def test_apply_decision_emits_professional_shell_and_tile_chrome(self):
        items = [_item("a", user_query="Travel spend overview")]
        clamped = apply_decision(
            items,
            {
                "title": "Travel Dashboard",
                "widgets": [
                    {
                        "kind": "viz",
                        "component_id": "a",
                        "x": 0,
                        "y": 2,
                        "w": 4,
                        "h": 2,
                        "header_title": "{{travel_type}}  {{travel_date}}",
                    },
                    {
                        "kind": "filter",
                        "column": "travel_date",
                        "default_values": [2015],
                        "x": 0,
                        "y": 0,
                        "w": 3,
                        "h": 1,
                    },
                    {
                        "kind": "text",
                        "title": "STORY LINE:",
                        "html": "Travel dashboard story",
                        "x": 7,
                        "y": 10,
                        "w": 5,
                        "h": 3,
                    },
                ],
                "dashboard_config": {
                    "theme": "default",
                    "parameters": {"orientation": "right", "enableApplyButton": True},
                },
            },
        )
        assert clamped["title"] == "Travel Dashboard"
        assert "header" not in clamped
        assert "variables" not in clamped
        assert clamped["dashboard_config"]["parameters"]["orientation"] == "right"
        viz = next(row for row in clamped["items"] if row.get("kind") == "instant")
        assert "header" not in viz
        assert "listeners" not in viz
        assert "dashboard_model" not in viz
        assert isinstance(viz.get("report_model"), dict)
        filt = next(
            row for row in clamped["items"] if row.get("kind") == "instant.filter"
        )
        assert filt["defaultValues"] == [2015]
        assert "listeners" not in filt
        assert "dashboard_model" not in filt
        story = next(
            row
            for row in clamped["items"]
            if row.get("kind") == "text"
            and "story" in str(row.get("html") or "").lower()
        )
        assert story["html"] == "<p>Travel dashboard story</p>"
        assert "dashboard_model" not in story

    def test_apply_decision_drops_filters_not_on_chats(self):
        items = [_item("a")]
        items[0]["viz"]["viz_model"]["data"]["filters"] = []
        clamped = apply_decision(
            items,
            {
                "widgets": [
                    {"kind": "viz", "component_id": "a", "x": 0, "y": 0, "w": 6, "h": 4},
                    {"kind": "filter", "column": "invented", "x": 0, "y": 0, "w": 3, "h": 1},
                ],
            },
        )
        extra = [
            row for row in clamped["items"] if row.get("kind") == "instant.filter"
        ]
        assert extra[0]["column"] == "invented"
        assert "dashboard_model" not in extra[0]
        assert extra[0].get("data_model") is None
        assert extra[0].get("viz_model") is None
        assert extra[0].get("report_model") is None

    def test_apply_decision_keeps_summary_and_separator_without_viz_models(self):
        items = [_item("a")]
        svg = '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="8"/></svg>'
        clamped = apply_decision(
            items,
            {
                "widgets": [
                    {"kind": "viz", "component_id": "a", "x": 0, "y": 2, "w": 6, "h": 4, "css": "#a{}"},
                    {"kind": "summary", "html": "Travel spend rose", "x": 0, "y": 0, "w": 12, "h": 1},
                    {"kind": "separator", "html": svg, "x": 0, "y": 1, "w": 12, "h": 1},
                ],
            },
        )
        kinds = [
            row.get("kind")
            for row in clamped["items"]
            if row.get("kind")
        ]
        assert kinds.count("instant") == 1
        viz_row = next(row for row in clamped["items"] if row.get("kind") == "instant")
        assert "viz" not in viz_row
        assert "data_model" not in viz_row
        assert "viz_model" not in viz_row
        assert isinstance(viz_row.get("report_model"), dict)
        assert viz_row["report_model"].get("viz_model") is not None
        assert "text" in kinds
        assert "svg" in kinds or "separator" in kinds
        extra = next(row for row in clamped["items"] if row.get("kind") == "text")
        assert extra.get("data_model") is None
        assert extra.get("viz_model") is None
        assert extra.get("report_model") is None
        assert "viz" not in extra
        assert extra["component_id"]
        assert extra["html"] == "<p>Travel spend rose</p>"
        assert "dashboard_model" not in extra

    def test_apply_decision_replaces_llm_extra_component_ids(self):
        items = [_item("a")]
        clamped = apply_decision(
            items,
            {
                "widgets": [
                    {"kind": "viz", "component_id": "a", "x": 0, "y": 2, "w": 6, "h": 4},
                    {
                        "kind": "summary",
                        "component_id": "summary_insight",
                        "html": "Insight",
                        "x": 0,
                        "y": 0,
                        "w": 12,
                        "h": 1,
                    },
                    {
                        "kind": "kpi",
                        "component_id": "kpi_travel_cost",
                        "title": "Cost",
                        "x": 0,
                        "y": 1,
                        "w": 3,
                        "h": 2,
                    },
                    {
                        "kind": "filter",
                        "component_id": "filter_meet_cancellation_status",
                        "column": "status",
                        "x": 3,
                        "y": 1,
                        "w": 3,
                        "h": 1,
                    },
                    {
                        "kind": "svg",
                        "component_id": "svg_divider",
                        "html": '<svg viewBox="0 0 10 1"><line x1="0" y1="0" x2="10" y2="0"/></svg>',
                        "x": 0,
                        "y": 3,
                        "w": 12,
                        "h": 1,
                    },
                ],
            },
        )
        by_kind = {
            row["kind"]: row["component_id"]
            for row in clamped["items"]
            if row.get("kind") and row["kind"] != "instant"
        }
        assert clamped["items"]
        assert next(
            row["component_id"]
            for row in clamped["items"]
            if row.get("kind") == "instant"
        ) == "a"
        assert by_kind.get("text") not in {
            "summary_insight",
            "kpi_travel_cost",
            "filter_meet_cancellation_status",
            "svg_divider",
            "a",
        }
        assert by_kind["text"] and len(by_kind["text"]) == 8
        assert by_kind.get("kpi") != "kpi_travel_cost"
        assert by_kind.get("instant.filter") != "filter_meet_cancellation_status"
        assert (by_kind.get("svg") or by_kind.get("separator")) != "svg_divider"


class TestCollectItems:
    def test_normalize_strips_vf_template(self):
        item = normalize_item(
            {
                "id": "1",
                "sql": "SELECT 1",
                "viz": {"vf_template": "function Draw(){}", "chart_name": "bar"},
            }
        )
        assert item["viz"].get("vf_template") is None
        assert item["viz"]["chart_name"] == "bar"

    def test_normalize_skips_failed_and_error_items(self):
        assert normalize_item(_item("ok"))["id"] == "ok"
        assert normalize_item({**_item("failed"), "status": "failed"}) is None
        assert normalize_item({**_item("err"), "error": True}) is None
        assert normalize_item({**_item("sqlerr"), "sql_error": "syntax error"}) is None
        assert normalize_item(
            {
                **_item("chaterr"),
                "chat_response": {"error": "Could not generate SQL", "sql": {"raw_sql": "SELECT 1"}},
            }
        ) is None

    def test_memory_skips_failed_nodes(self, monkeypatch):
        memory = ChatGraphMemory()
        memory.add_node(
            "c1",
            "3",
            {
                "sql": "SELECT region FROM sales",
                "chat_response": {
                    "viz": {"chart_name": "bar"},
                    "summary": {"insight": "West leads"},
                },
            },
        )
        memory.add_node(
            "c1",
            "4",
            {
                "sql": "",
                "request_status": "failed",
                "chat_response": {"error": "timeout", "viz": {"chart_name": "bar"}},
            },
        )
        monkeypatch.setattr(collect_items_mod, "chat_graph_memory", memory)
        items = collect_items({"chatid": "c1", "items": []})
        assert [row["id"] for row in items] == ["3"]
        assert items[0]["component_id"]
        assert items[0]["chat_sequence_id"] == "3"

    def test_request_items_win_over_memory(self, monkeypatch):
        memory = ChatGraphMemory()
        memory.add_node("c1", "9", {"sql": "SELECT memory", "chat_response": {"viz": {"chart_name": "pie"}}})
        monkeypatch.setattr(collect_items_mod, "chat_graph_memory", memory)
        items = collect_items({"chatid": "c1", "items": [_item("seq-3")]})
        assert [row["id"] for row in items] == ["seq-3"]

    def test_falls_back_to_chat_memory(self, monkeypatch):
        memory = ChatGraphMemory()
        memory.add_node(
            "c1",
            "3",
            {
                "sql": "SELECT region FROM sales",
                "chat_response": {
                    "viz": {
                        "chart_name": "bar",
                        "viz_model": {"chart": {"mark": "Chart", "viz": "Bar"}},
                    },
                    "summary": {"insight": "West leads"},
                },
            },
        )
        monkeypatch.setattr(collect_items_mod, "chat_graph_memory", memory)
        items = collect_items({"chatid": "c1", "items": []})
        assert len(items) == 1
        assert items[0]["id"] == "3"
        assert "SELECT" in items[0]["sql"]

    def test_item_cards_omit_sql_and_templates(self):
        cards = item_cards([_item("seq-3")])
        assert cards[0]["component_id"] == "seq-3"
        assert "sql" not in cards[0]
        assert "vf_template" not in cards[0]
        assert "region" in cards[0]["viz"]["rows"]
        assert "user_query" in cards[0]


class TestConvertDashboardGraphStages:
    def test_graph_is_single_pipeline_with_audit(self):
        from pathlib import Path

        source = (
            Path(__file__).resolve().parents[2]
            / "helicalbi"
            / "core"
            / "dashboardflow"
            / "DashboardLayoutGraph.py"
        )
        text = source.read_text(encoding="utf-8")
        for node in ("CollectContext", "PlanSummary", "SelectFilters", "MakeLayout", "Assemble", "Audit"):
            assert f'"{node}"' in text
        assert 'add_node("DashboardLayout"' not in text

    def test_collect_context_gathers_chat_domain_topics_and_viz_types(self):
        from helicalbi.core.dashboardflow.CollectDashboardContext import CollectDashboardContext

        item = _item("seq-3", "bar")
        item["domain"] = ["Travel"]
        item["topics"] = ["Cost"]
        item["user_query"] = "Travel cost by type"
        state = CollectDashboardContext().process_flow({"items": [item], "username": "hiadmin"})
        assert state["domain"] == ["Travel"]
        assert state["topics"] == ["Cost"]
        assert "bar" in state["viz_types"]
        assert state["chat_context"][0]["component_id"] == "seq-3"
        assert "Travel cost by type" in state["user_query"]

    def test_select_filters_prefers_datamodel_filters(self):
        from helicalbi.core.dashboardflow.SelectDashboardFilters import select_important_filters

        item = _item("seq-3")
        item["data_model"] = {
            "filters": [{"table": "travel_details", "column": "travel_type"}],
        }
        item["viz"]["viz_model"]["data"]["rows"] = ["region"]
        selected = select_important_filters([item])
        assert selected[0]["column"] == "travel_type"
        assert selected[0]["table"] == "travel_details"
        assert selected[0]["title"] == "travel_type"
        assert selected[0]["sourceItemId"] == "seq-3"

        wired = _item("seq-4")
        wired["data_model"] = {
            "filters": [
                {
                    "column": {
                        "name": "sampletraveldata.public.meeting_details.meet_cancellation_status",
                        "id": "1048",
                    },
                    "label": "Cancellation Status",
                }
            ],
        }
        picked = select_important_filters([wired])
        assert picked[0]["column"] == "meet_cancellation_status"
        assert picked[0]["table"] == "meeting_details"
        assert picked[0]["title"] == "Cancellation Status"

    def test_select_filters_prefers_dimension_columns_over_measure_rows(self):
        from helicalbi.core.dashboardflow.SelectDashboardFilters import select_important_filters

        item = _item("YokrCsQd")
        item["viz"]["viz_model"]["data"]["rows"] = ["Travel Cost"]
        item["viz"]["viz_model"]["data"]["columns"] = ["Source"]
        item["viz"]["viz_model"]["data"]["filters"] = []
        item["data_model"] = {
            "columns": [
                {
                    "column": {
                        "name": "sampletraveldata.public.travel_details.source",
                        "id": "1069",
                    },
                    "alias": "Source",
                },
                {
                    "column": {
                        "name": "sampletraveldata.public.travel_details.travel_cost",
                        "id": "1072",
                    },
                    "alias": "Travel Cost",
                    "aggregate": True,
                    "aggregateList": ["db.generic.aggregate.sum"],
                },
            ],
        }
        selected = select_important_filters([item])
        assert selected[0]["column"] == "source"
        assert selected[0]["table"] == "travel_details"
        assert selected[0]["sourceItemId"] == "YokrCsQd"
        assert all(row["column"] != "Travel Cost" for row in selected)

    def test_apply_decision_injects_filters_into_source_data_model(self):
        items = [
            {
                "id": "1-6",
                "chat_sequence_id": "1-6",
                "component_id": "YokrCsQd",
                "user_query": "Travel Cost by Source",
                "sql": 'SELECT source, SUM(travel_cost) FROM travel_details GROUP BY source',
                "data_model": {
                    "sql": 'SELECT source, SUM(travel_cost) FROM travel_details GROUP BY source',
                    "columns": [
                        {
                            "column": {
                                "name": "sampletraveldata.public.travel_details.source",
                                "id": "1069",
                            },
                            "alias": "Source",
                        },
                        {
                            "column": {
                                "name": "sampletraveldata.public.travel_details.travel_cost",
                                "id": "1072",
                            },
                            "alias": "Travel Cost",
                            "aggregate": True,
                            "aggregateList": ["db.generic.aggregate.sum"],
                        },
                    ],
                },
                "viz": {
                    "chart_name": "bar",
                    "viz_model": {
                        "chart": {"viz": "Bar", "mark": "Chart"},
                        "data": {"rows": ["Travel Cost"], "columns": ["Source"]},
                        "properties": {"title": "Travel Cost by Source"},
                    },
                },
                "summary": "Cost by source",
            }
        ]
        clamped = apply_decision(
            items,
            {
                "widgets": [
                    {"kind": "viz", "component_id": "YokrCsQd", "x": 0, "y": 2, "w": 6, "h": 4},
                    {
                        "kind": "filter",
                        "column": "source",
                        "table": "travel_details",
                        "title": "Source",
                        "sourceItemId": "YokrCsQd",
                        "x": 0,
                        "y": 0,
                        "w": 3,
                        "h": 1,
                    },
                ],
            },
        )
        viz = next(
            row for row in clamped["items"] if row.get("kind") == "instant"
        )
        filt = next(
            row for row in clamped["items"] if row.get("kind") == "instant.filter"
        )
        assert filt["sourceItemId"] == "YokrCsQd"
        assert "dashboard_model" not in filt
        data_model = viz["report_model"]["data_model"]
        assert isinstance(data_model.get("filters"), list)
        assert len(data_model["filters"]) == 1
        wire = data_model["filters"][0]
        assert wire["column"]["name"] == "sampletraveldata.public.travel_details.source"
        assert wire["column"]["id"] == "1069"
        assert wire["alias"] == "Source"
        assert wire["condition"] == "EQUALS"
        assert wire["values"] == []
        assert data_model["filterExpression"] == ["Source"]
        assert "${0}" in data_model["customFilterExpression"]

    def test_apply_decision_resolves_filter_wire_column_across_items(self):
        item1 = {
            "id": "1-5",
            "chat_sequence_id": "1-5",
            "component_id": "comp_month",
            "user_query": "Monthly Travel Cost",
            "data_model": {
                "columns": [
                    {
                        "column": {
                            "name": "sampletraveldata.public.travel_details.travel_date",
                            "id": "1065",
                        },
                        "alias": "Month",
                    },
                ],
            },
            "viz": {"chart_name": "line", "viz_model": {"chart": {"viz": "Line", "mark": "Chart"}}},
        }
        item2 = {
            "id": "1-3",
            "chat_sequence_id": "1-3",
            "component_id": "comp_medium",
            "user_query": "Travel Cost by Medium",
            "data_model": {
                "columns": [
                    {
                        "column": {
                            "name": "sampletraveldata.public.travel_details.travel_medium",
                            "id": "1067",
                        },
                        "alias": "Travel Medium",
                    },
                ],
            },
            "viz": {"chart_name": "bar", "viz_model": {"chart": {"viz": "Bar", "mark": "Chart"}}},
        }
        clamped = apply_decision(
            [item1, item2],
            {
                "widgets": [
                    {"kind": "viz", "component_id": "comp_month", "x": 0, "y": 2, "w": 6, "h": 4},
                    {"kind": "viz", "component_id": "comp_medium", "x": 6, "y": 2, "w": 6, "h": 4},
                    {
                        "kind": "filter",
                        "column": "travel_date",
                        "table": "travel_details",
                        "title": "Month",
                        "sourceItemId": "comp_medium",
                        "x": 0,
                        "y": 0,
                        "w": 3,
                        "h": 1,
                    },
                ],
            },
        )
        medium_viz = next(
            row for row in clamped["items"] if row.get("component_id") == "comp_medium"
        )
        data_model = medium_viz["report_model"]["data_model"]
        assert len(data_model["filters"]) == 1
        filt = data_model["filters"][0]
        assert isinstance(filt["column"], dict)
        assert filt["column"]["name"] == "sampletraveldata.public.travel_details.travel_date"
        assert filt["column"]["id"] == "1065"
        assert "table" not in filt

    def test_filter_title_uses_column_leaf_not_wire_object(self):
        wire = {
            "name": "sampletraveldata.public.meeting_details.meet_cancellation_status",
            "id": "1048",
        }
        items = [_item("a")]
        clamped = apply_decision(
            items,
            {
                "widgets": [
                    {"kind": "viz", "component_id": "a", "x": 0, "y": 2, "w": 6, "h": 4},
                    {
                        "kind": "filter",
                        "column": wire,
                        "title": str(wire),
                        "x": 0,
                        "y": 0,
                        "w": 3,
                        "h": 1,
                    },
                ],
            },
        )
        filt = next(
            row for row in clamped["items"] if row.get("kind") == "instant.filter"
        )
        assert filt["column"] == "meet_cancellation_status"
        assert filt["table"] == "meeting_details"
        assert filt["title"] == "meet_cancellation_status"
        assert "header" not in filt
        assert "{" not in filt["title"]
        assert "dashboard_model" not in filt

    def test_apply_reset_when_freefloat_and_filter(self):
        items = [_item("a")]
        docked = apply_decision(
            items,
            {
                "parameters": {"floatingFilter": False},
                "widgets": [
                    {"kind": "viz", "component_id": "a", "x": 0, "y": 2, "w": 6, "h": 4},
                    {"kind": "filter", "column": "status", "x": 0, "y": 0, "w": 3, "h": 1},
                ],
            },
        )
        assert all(row.get("kind") != "apply-reset" for row in docked["items"])

        bare = [_item("bare")]
        bare[0]["viz"]["viz_model"]["data"]["filters"] = []
        bare[0]["viz"]["viz_model"]["data"]["rows"] = []
        no_filter = apply_decision(
            bare,
            {
                "parameters": {"floatingFilter": True},
                "widgets": [{"kind": "viz", "component_id": "bare", "x": 0, "y": 2, "w": 6, "h": 4}],
            },
        )
        assert all(row.get("kind") != "apply-reset" for row in no_filter["items"])

        clamped = apply_decision(
            items,
            {
                "parameters": {"floatingFilter": True},
                "widgets": [
                    {"kind": "viz", "component_id": "a", "x": 0, "y": 2, "w": 6, "h": 4},
                    {"kind": "filter", "column": "status", "x": 0, "y": 0, "w": 3, "h": 1},
                ],
            },
        )
        button = next(
            row for row in clamped["items"] if row.get("kind") == "apply-reset"
        )
        assert "dashboard_model" not in button
        assert button["isGrouped"] is False
        assert button["isSaved"] is False
        assert "css" not in button
        assert "js" not in button
        assert "html" not in button
        assert "export" not in button
        assert button["id"].startswith("item-")
        assert button["layout"]["w"] == 1
        assert button["layout"]["h"] == 1
        assert button["layout"]["i"] == button["id"]
        assert button["layout"]["static"] is False
        assert button["layout"]["x"] == 3
        assert button["layout"]["y"] == 0
        assert "compType" not in button
        assert "initialPosition" not in button

    def test_empty_context_sets_error(self):
        from helicalbi.core.dashboardflow.CollectDashboardContext import (
            CollectDashboardContext,
            route_after_context,
        )

        state = CollectDashboardContext().process_flow({"items": [], "user_input": {}})
        assert "No visualizations" in state["error"]
        assert route_after_context(state) == "audit"


def _chart_item(component_id: str, *, title: str, x: int, y: int, rows: list[str], columns: list[str], viz: str = "Line"):
    return {
        "component_id": component_id,
        "itemId": component_id,
        "kind": "instant",
        "title": title,
        "layout": {"x": x, "y": y, "w": 6, "h": 4},
        "report_model": {
            "viz_model": {
                "data": {"rows": rows, "columns": columns},
                "chart": {"viz": viz, "mark": "Chart"},
            }
        },
    }


class TestDashboardItemCleanup:
    def test_compacts_gap_under_filter_and_drops_repeated_chart(self):
        from helicalbi.sql_agent.nodes.dashboard import _filter_invented_tiles

        dashboard = {
            "items": [
                {
                    "component_id": "story",
                    "kind": "text",
                    "title": "Overview",
                    "html": "<p>Story</p>",
                    "layout": {"x": 0, "y": 0, "w": 12, "h": 2},
                },
                {
                    "component_id": "date",
                    "kind": "instant.filter",
                    "title": "travel_date",
                    "column": "travel_date",
                    "layout": {"x": 0, "y": 2, "w": 3, "h": 1},
                },
                {
                    "component_id": "kpiFake",
                    "kind": "kpi",
                    "title": "Travel Cost",
                    "html": "",
                    "layout": {"x": 0, "y": 3, "w": 3, "h": 2},
                },
                _chart_item("year-a", title="Travel Cost by Year", x=0, y=5, rows=["Travel Cost"], columns=["Year"]),
                _chart_item("year-b", title="Travel Cost by Year", x=6, y=5, rows=["Travel Cost"], columns=["Year"]),
                _chart_item(
                    "type",
                    title="Travel Cost by Travel Type",
                    x=0,
                    y=9,
                    rows=["Travel Cost"],
                    columns=["Travel Type"],
                    viz="Bar",
                ),
            ]
        }
        cleaned = _filter_invented_tiles(dashboard)
        ids = [row["component_id"] for row in cleaned["items"]]
        assert "kpiFake" not in ids
        assert ids.count("year-a") == 1
        assert "year-b" not in ids
        year = next(row for row in cleaned["items"] if row["component_id"] == "year-a")
        kind_chart = next(row for row in cleaned["items"] if row["component_id"] == "type")
        filt = next(row for row in cleaned["items"] if row["component_id"] == "date")
        assert filt["layout"]["y"] == 2
        assert year["layout"]["y"] == 3
        assert kind_chart["layout"]["y"] == 7
        assert "listeners" not in filt
        assert "dashboard_model" not in filt
