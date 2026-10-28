"""Functional tests for think mode filter association with report models and title-label matching."""
from __future__ import annotations

import pytest

from helicalbi.interactive.think_flow import correct_think_mode_filters, run_think_mode
from helicalbi.sql_agent.nodes.dashboard import dashboard_node

pytestmark = pytest.mark.functional


def _make_report_item(
    comp_id: str,
    columns: list[dict],
    filters: list[dict] | None = None,
    user_query: str = "Query",
) -> dict:
    data_model = {
        "columns": columns,
        "filters": list(filters or []),
        "filterExpression": [],
        "customFilterExpression": "",
    }
    viz_model = {
        "chart": {"viz": "Bar", "mark": "Chart"},
        "data": {"rows": [c.get("alias") or c.get("name") for c in columns if isinstance(c, dict)]},
        "properties": {"title": f"Chart {comp_id}"},
    }
    return {
        "kind": "instant",
        "component_id": comp_id,
        "itemId": comp_id,
        "id": comp_id,
        "chat_sequence_id": comp_id,
        "user_query": user_query,
        "data_model": data_model,
        "viz_model": viz_model,
        "report_model": {
            "data_model": data_model,
            "viz_model": viz_model,
        },
    }


def _make_filter_item(
    comp_id: str,
    column: str,
    title: str,
    label: str = "",
    alias: str = "",
    source_item_id: str = "",
    table: str = "",
) -> dict:
    filt = {
        "kind": "instant.filter",
        "component_id": comp_id,
        "itemId": comp_id,
        "id": comp_id,
        "column": column,
        "title": title,
        "sourceItemId": source_item_id,
        "table": table,
        "defaultValues": [],
        "dashboard_model": {
            "kind": "instant.filter",
            "title": title,
            "column": column,
            "sourceItemId": source_item_id,
            "table": table,
            "defaultValues": [],
        },
    }
    if label:
        filt["label"] = label
        filt["dashboard_model"]["label"] = label
    if alias:
        filt["alias"] = alias
        filt["dashboard_model"]["alias"] = alias
    return filt


class TestThinkModeFilterAssociation:
    def test_filter_associates_with_matching_report_model_by_column(self):
        rep1 = _make_report_item(
            "comp_sales",
            columns=[
                {"column": {"name": "public.orders.region", "id": "1"}, "alias": "Region"},
                {"column": {"name": "public.orders.sales", "id": "2"}, "alias": "Sales"},
            ],
        )
        rep2 = _make_report_item(
            "comp_inventory",
            columns=[
                {"column": {"name": "public.stock.product_id", "id": "3"}, "alias": "Product ID"},
                {"column": {"name": "public.stock.qty", "id": "4"}, "alias": "Quantity"},
            ],
        )
        filt = _make_filter_item("filt_1", column="region", title="region", source_item_id="")

        dashboard = {
            "dashboardid": "thread_1",
            "items": [rep1, rep2, filt],
        }

        corrected = correct_think_mode_filters(dashboard)
        items = corrected["items"]
        filt_out = next(it for it in items if it.get("component_id") == "filt_1")

        # Must be associated with comp_sales which has the region column
        assert filt_out["sourceItemId"] == "comp_sales"
        assert filt_out["dashboard_model"]["sourceItemId"] == "comp_sales"
        # instant.filter must NOT contain report_model
        assert filt_out.get("report_model") is None
        assert filt_out["dashboard_model"].get("report_model") is None

        # Alias on instant.filter and same alias present in the filter of sourceItemId
        assert filt_out["alias"] == "Region"
        rep1_filters = rep1["report_model"]["data_model"]["filters"]
        assert len(rep1_filters) == 1
        assert rep1_filters[0]["alias"] == filt_out["alias"]

    def test_filter_associates_with_any_report_model_when_no_column_matches(self):
        rep = _make_report_item(
            "comp_first",
            columns=[
                {"column": {"name": "public.hr.emp_id", "id": "10"}, "alias": "Employee ID"},
            ],
        )
        filt = _make_filter_item("filt_unmatched", column="travel_type", title="travel_type", source_item_id="")

        dashboard = {
            "dashboardid": "thread_2",
            "items": [rep, filt],
        }

        corrected = correct_think_mode_filters(dashboard)
        filt_out = next(it for it in corrected["items"] if it.get("component_id") == "filt_unmatched")

        # Associated with the available report model
        assert filt_out["sourceItemId"] == "comp_first"
        assert filt_out["dashboard_model"]["sourceItemId"] == "comp_first"
        # No report_model in instant.filter
        assert filt_out.get("report_model") is None

        # Same alias in instant.filter and in the filter of sourceItemId
        assert filt_out["alias"] == "travel_type"
        rep_filters = rep["report_model"]["data_model"]["filters"]
        assert len(rep_filters) == 1
        assert rep_filters[0]["alias"] == filt_out["alias"]

    def test_filter_reassociates_when_source_item_id_is_invalid(self):
        rep = _make_report_item(
            "comp_real",
            columns=[{"column": {"name": "public.travel.city", "id": "20"}, "alias": "City"}],
        )
        filt = _make_filter_item("filt_hallucinated", column="city", title="city", source_item_id="hallucinated_xyz")

        dashboard = {
            "dashboardid": "thread_3",
            "items": [rep, filt],
        }

        corrected = correct_think_mode_filters(dashboard)
        filt_out = next(it for it in corrected["items"] if it.get("component_id") == "filt_hallucinated")

        assert filt_out["sourceItemId"] == "comp_real"
        assert filt_out.get("report_model") is None
        assert filt_out["alias"] == "City"
        assert rep["report_model"]["data_model"]["filters"][0]["alias"] == filt_out["alias"]


class TestThinkModeFilterTitleMatchesLabel:
    def test_filter_title_corrected_from_own_label(self):
        rep = _make_report_item("comp_1", columns=[{"column": "travel_type", "alias": "travel_type"}])
        filt = _make_filter_item(
            "filt_own",
            column="travel_type",
            title="travel_type",  # Raw db name
            label="Travel Type",  # Human label
            source_item_id="comp_1",
        )

        dashboard = {"items": [rep, filt]}
        corrected = correct_think_mode_filters(dashboard)
        filt_out = next(it for it in corrected["items"] if it.get("component_id") == "filt_own")

        assert filt_out["title"] == "Travel Type"
        assert filt_out["label"] == "Travel Type"
        assert filt_out["alias"] == "Travel Type"
        assert filt_out["dashboard_model"]["title"] == "Travel Type"
        assert filt_out["dashboard_model"]["label"] == "Travel Type"
        assert filt_out["dashboard_model"]["alias"] == "Travel Type"

        # Check sourceItemId filter has the same alias
        rep_filters = rep["report_model"]["data_model"]["filters"]
        assert rep_filters[0]["alias"] == filt_out["alias"]

    def test_filter_title_corrected_from_report_model_filter_label(self):
        rep = _make_report_item(
            "comp_2",
            columns=[{"column": "meet_cancellation_status", "alias": "status"}],
            filters=[
                {
                    "column": "meet_cancellation_status",
                    "label": "Cancellation Status",
                    "alias": "Cancellation Status",
                }
            ],
        )
        filt = _make_filter_item(
            "filt_rep_flt",
            column="meet_cancellation_status",
            title="meet_cancellation_status",
            label="",  # Not provided on filter
            source_item_id="comp_2",
        )

        dashboard = {"items": [rep, filt]}
        corrected = correct_think_mode_filters(dashboard)
        filt_out = next(it for it in corrected["items"] if it.get("component_id") == "filt_rep_flt")

        assert filt_out["title"] == "Cancellation Status"
        assert filt_out["label"] == "Cancellation Status"
        assert filt_out["alias"] == "Cancellation Status"
        assert rep["report_model"]["data_model"]["filters"][0]["alias"] == filt_out["alias"]

    def test_filter_title_corrected_from_report_model_column_alias(self):
        rep = _make_report_item(
            "comp_3",
            columns=[
                {
                    "column": {"name": "public.orders.dept_code", "id": "5"},
                    "alias": "Department Name",
                }
            ],
        )
        filt = _make_filter_item(
            "filt_rep_col",
            column="dept_code",
            title="dept_code",
            label="",
            source_item_id="comp_3",
        )

        dashboard = {"items": [rep, filt]}
        corrected = correct_think_mode_filters(dashboard)
        filt_out = next(it for it in corrected["items"] if it.get("component_id") == "filt_rep_col")

        assert filt_out["title"] == "Department Name"
        assert filt_out["label"] == "Department Name"
        assert filt_out["alias"] == "Department Name"
        assert rep["report_model"]["data_model"]["filters"][0]["alias"] == filt_out["alias"]

    def test_filter_title_and_label_match_when_only_title_present(self):
        rep = _make_report_item("comp_4", columns=[{"column": "region", "alias": "region"}])
        filt = _make_filter_item(
            "filt_title_only",
            column="region",
            title="Sales Region",
            label="",
            source_item_id="comp_4",
        )

        dashboard = {"items": [rep, filt]}
        corrected = correct_think_mode_filters(dashboard)
        filt_out = next(it for it in corrected["items"] if it.get("component_id") == "filt_title_only")

        assert filt_out["title"] == "Sales Region"
        assert filt_out["label"] == "Sales Region"
        assert filt_out["alias"] == "Sales Region"
        assert rep["report_model"]["data_model"]["filters"][0]["alias"] == filt_out["alias"]

    def test_sync_dashboard_filters_metadata_list(self):
        rep = _make_report_item("comp_5", columns=[{"column": "dept", "alias": "Department"}])
        filt = _make_filter_item("filt_5", column="dept", title="dept", label="Department", source_item_id="")
        dashboard = {
            "items": [rep, filt],
            "filters": [
                {"component_id": "filt_5", "column": "dept", "title": "dept", "sourceItemId": ""},
            ],
        }

        corrected = correct_think_mode_filters(dashboard)
        db_flt = corrected["filters"][0]
        assert db_flt["sourceItemId"] == "comp_5"
        assert db_flt["title"] == "Department"
        assert db_flt["label"] == "Department"
        assert db_flt["alias"] == "Department"


class TestThinkModePipelineIntegration:
    def test_run_think_mode_corrects_filters_at_end_of_result(self, monkeypatch):
        rep = _make_report_item(
            "comp_report",
            columns=[
                {"column": {"name": "public.travel.travel_type", "id": "1"}, "alias": "Travel Type"},
            ],
        )
        filt = _make_filter_item(
            "filt_travel",
            column="travel_type",
            title="travel_type",  # Raw db name
            label="",
            source_item_id="",    # Unassociated
        )
        mock_dashboard = {
            "dashboardid": "thread_think",
            "items": [rep, filt],
        }

        def fake_create_plan(*args, **kwargs):
            return {
                "plan": {"supporting_questions": [{"question": "Q1"}]},
                "asked_questions": ["Q1"],
                "token_usage": {},
            }

        def fake_run_dashboard_agent(*args, **kwargs):
            return {
                "dashboard": mock_dashboard,
                "collected_data": [],
                "token_usage": {},
                "final_answer": "Final analysis conclusion.",
            }

        def fake_synthesize_opening(*args, **kwargs):
            return "Overview"

        def fake_synthesize_final(*args, **kwargs):
            from helicalbi.interactive.think_flow import ThinkCitedAnswer
            return ThinkCitedAnswer(
                final_answer="Conclusion citing [1].",
                cited_question_indexes=[1],
            )

        payload = run_think_mode(
            "Analyze travel spend",
            session_cookie="test_cookie",
            username="test_user",
            model_file_name="travel.hsm",
            model_location="travel_dir",
            thread_id="thread_think",
            _create_and_store_plan=fake_create_plan,
            _run_dashboard_agent=fake_run_dashboard_agent,
            _load_plan=lambda *a: None,
            _save_plan=lambda *a: None,
            _synthesize_opening_insight=fake_synthesize_opening,
            _synthesize_final_answer=fake_synthesize_final,
        )

        # Verify payload and dashboard_model at the end of the result
        db = payload["dashboard"]
        filt_res = next(it for it in db["items"] if it.get("component_id") == "filt_travel")

        # 1. Associated with report model, but no report_model inside instant.filter
        assert filt_res["sourceItemId"] == "comp_report"
        assert filt_res.get("report_model") is None

        # 2. Title matches label and alias
        assert filt_res["title"] == "Travel Type"
        assert filt_res["label"] == "Travel Type"
        assert filt_res["alias"] == "Travel Type"
        assert filt_res["dashboard_model"]["title"] == "Travel Type"
        assert filt_res["dashboard_model"]["alias"] == "Travel Type"

        # 3. Same alias in the filter of sourceItemId
        rep_filters = rep["report_model"]["data_model"]["filters"]
        assert rep_filters[0]["alias"] == filt_res["alias"]

        # Also verified on dashboard_model
        db_model = payload["dashboard_model"]
        filt_model = next(it for it in db_model["items"] if it.get("component_id") == "filt_travel")
        assert filt_model["sourceItemId"] == "comp_report"
        assert filt_model["title"] == "Travel Type"
        assert filt_model["alias"] == "Travel Type"

    def test_dashboard_node_applies_correction_in_think_mode(self):
        rep = _make_report_item(
            "comp_dash",
            columns=[
                {"column": {"name": "public.orders.city", "id": "1"}, "alias": "City"},
            ],
        )
        filt = _make_filter_item(
            "filt_dash",
            column="city",
            title="city",
            label="City Name",
            source_item_id="",
        )

        state = {
            "flat_sql": True,  # Think mode flag
            "thread_id": "thread_dash",
            "original_question": "City sales",
            "build_dashboard": True,
            "investigation_plan": {},
            "collected_data": [
                {
                    "step_id": 1,
                    "chat_response": {
                        "report_model": rep["report_model"],
                        "viz_model": rep["viz_model"],
                    },
                }
            ],
        }

        # Mock invoke_dashboard_layout to return unassociated filter and report
        import helicalbi.sql_agent.nodes.dashboard as dash_mod
        orig_invoke = dash_mod.invoke_dashboard_layout
        try:
            dash_mod.invoke_dashboard_layout = lambda _ctx: {
                "title": "Dashboard",
                "items": [rep, filt],
                "dashboard_config": {},
                "token_usage": {},
            }
            node_result = dashboard_node(state)
            db = node_result["dashboard"]
            filt_res = next(it for it in db["items"] if it.get("component_id") == "filt_dash")
            assert filt_res["sourceItemId"] == "comp_dash"
            assert filt_res.get("report_model") is None
            assert filt_res["title"] == "City Name"
            assert filt_res["label"] == "City Name"
            assert filt_res["alias"] == "City Name"
            assert rep["report_model"]["data_model"]["filters"][0]["alias"] == filt_res["alias"]
        finally:
            dash_mod.invoke_dashboard_layout = orig_invoke

    def test_history_chat_response_uses_instant_report_model(self):
        instant = _make_report_item(
            "1-1",
            columns=[
                {"column": {"name": "public.travel.travel_date", "id": "1"}, "alias": "Year"},
            ],
        )
        other = _make_report_item(
            "1-3",
            columns=[
                {"column": {"name": "public.travel.travel_type", "id": "2"}, "alias": "Travel Type"},
            ],
        )
        filt = _make_filter_item(
            "filt_year",
            column="travel_date",
            title="Year",
            label="Year",
            source_item_id="1-1",
        )
        stale = {
            "data_model": {"columns": [], "filters": []},
            "viz_model": {"chart": {"viz": "Bar"}},
        }
        history = [
            {
                "index": 1,
                "chat_seq_id": "1-1",
                "chat_response": {"report_model": stale},
            },
            {
                "index": 2,
                "chat_seq_id": "1-3",
                "chat_response": {"report_model": {"data_model": {"filters": [{"alias": "old"}]}}},
            },
            {
                "index": 3,
                "chat_seq_id": "1-9",
                "chat_response": {"report_model": {"data_model": {"filters": []}}},
            },
        ]
        payload = {
            "dashboard": {"items": [instant, other, filt]},
            "dashboard_model": None,
            "question_history": history,
        }
        payload["dashboard_model"] = payload["dashboard"]

        corrected = correct_think_mode_filters(payload, question_history=history)

        first = corrected["question_history"][0]["chat_response"]["report_model"]
        second = corrected["question_history"][1]["chat_response"]["report_model"]
        untouched = corrected["question_history"][2]["chat_response"]["report_model"]
        assert first is instant["report_model"]
        assert second is other["report_model"]
        assert first["data_model"]["filters"][0]["alias"] == "Year"
        assert untouched["data_model"]["filters"] == []
