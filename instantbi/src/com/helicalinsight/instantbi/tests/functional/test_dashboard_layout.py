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
        assert dumped["theme"]["color"].startswith("#")
        assert dumped["widgets"] == []
        assert dumped["templateId"] == ""


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
        assert "kind=kpi" in dashboard_layout_prompt_string
        assert "kind=svg" in dashboard_layout_prompt_string
        assert "kind=summary" in dashboard_layout_prompt_string
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
        assert layout["theme"]["color"]

    def test_apply_decision_fills_missing_item_ids(self):
        items = [_item("a"), _item("b")]
        clamped = apply_decision(
            items,
            {
                "widgets": [{"kind": "viz", "component_id": "a", "x": 0, "y": 2, "w": 6, "h": 4}],
                "theme": {"color": "#111111", "background": "#fafafa"},
            },
        )
        ids = [row["component_id"] for row in clamped["items"] if row["dashboard_model"]["kind"] == "viz"]
        assert "a" in ids
        assert "b" in ids
        assert clamped["theme"]["color"] == "#111111"
        assert clamped["sections"] == []
        viz_a = next(row for row in clamped["items"] if row["component_id"] == "a")
        assert "chat_sequence_id" not in viz_a
        assert "chatid" not in viz_a
        assert viz_a["id"] == "a"
        assert viz_a["dashboard_model"]["layout"]["w"] == 6
        assert "sql_parts" not in viz_a
        kinds = {row["dashboard_model"]["kind"] for row in clamped["items"]}
        assert {"viz", "summary", "filter", "kpi", "svg"} <= kinds

    def test_apply_decision_drops_filters_not_on_chats(self):
        items = [_item("a")]
        items[0]["viz"]["viz_model"]["data"]["filters"] = []
        clamped = apply_decision(
            items,
            {
                "widgets": [
                    {"kind": "viz", "component_id": "a", "x": 0, "y": 0, "w": 6, "h": 4},
                    {"kind": "filter", "column": "invented", "listeners": ["a"], "x": 0, "y": 0, "w": 3, "h": 1},
                ],
            },
        )
        extra = [row for row in clamped["items"] if row["dashboard_model"]["kind"] == "filter"]
        assert extra[0]["dashboard_model"]["column"] == "invented"
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
        kinds = [row["dashboard_model"]["kind"] for row in clamped["items"]]
        assert kinds.count("viz") == 1
        viz_row = next(row for row in clamped["items"] if row["dashboard_model"]["kind"] == "viz")
        assert "viz" not in viz_row
        assert "data_model" not in viz_row
        assert "viz_model" not in viz_row
        assert isinstance(viz_row.get("report_model"), dict)
        assert viz_row["report_model"].get("viz_model") is not None
        assert "summary" in kinds
        assert "svg" in kinds or "separator" in kinds
        extra = next(row for row in clamped["items"] if row["dashboard_model"]["kind"] == "summary")
        assert extra.get("data_model") is None
        assert extra.get("viz_model") is None
        assert extra.get("report_model") is None
        assert "viz" not in extra
        assert extra["component_id"]
        assert extra["dashboard_model"]["html"] == "Travel spend rose"

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
                        "listeners": ["a"],
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
            row["dashboard_model"]["kind"]: row["component_id"]
            for row in clamped["items"]
            if row["dashboard_model"]["kind"] != "viz"
        }
        assert clamped["items"]
        assert next(row["component_id"] for row in clamped["items"] if row["dashboard_model"]["kind"] == "viz") == "a"
        assert by_kind.get("summary") not in {
            "summary_insight",
            "kpi_travel_cost",
            "filter_meet_cancellation_status",
            "svg_divider",
            "a",
        }
        assert by_kind["summary"] and len(by_kind["summary"]) == 8
        assert by_kind.get("kpi") != "kpi_travel_cost"
        assert by_kind.get("filter") != "filter_meet_cancellation_status"
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
        assert "seq-3" in selected[0]["listeners"]

    def test_empty_context_sets_error(self):
        from helicalbi.core.dashboardflow.CollectDashboardContext import (
            CollectDashboardContext,
            route_after_context,
        )

        state = CollectDashboardContext().process_flow({"items": [], "user_input": {}})
        assert "No visualizations" in state["error"]
        assert route_after_context(state) == "audit"
