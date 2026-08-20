"""Tests for helicalbi.viz.report_object (minimum GridTable / pivot report)."""

from helicalbi.viz.report_object import (
    build_format_fields,
    build_grid_fields,
    get_minimal_report_properties,
    get_report_object,
    report_object_to_js,
)


class TestGetReportObject:
    def test_minimum_shape_matches_initial_state_keys(self):
        report = get_report_object(
            ["booking_platform", "region"],
            ["travel_cost"],
            format_strings={"travel_cost": "0.00"},
        )
        assert report["mode"] == "create"
        assert len(report["fields"]) == 3
        assert report["fields"][0]["addedAs"] == "row"
        assert report["fields"][0]["autogen_alias"] == "booking_platform"
        assert report["fields"][1]["addedAs"] == "column"
        assert report["fields"][2]["floatingType"] == "continous"
        assert report["fields"][2]["id"] == "3"

        props = report["properties"]
        assert props["format"]["formatFields"][0]["id"] == "3"
        assert props["format"]["formatFields"][0]["values"]["customFormat"] == "0.00"
        assert props["format"]["formatFields"][0]["values"]["enableCustomFormatting"] is True
        assert props["crosstab"]["grandTotalsPosition"] == "Bottom"
        assert props["tooltip"]["showTooltip"] is True
        assert props["title"]["show"] is False

        # S2 reads reportData.properties; getPropertyFieldInfo reads report.properties
        assert report["reportData"]["properties"]["format"]["formatFields"][0]["values"][
            "customFormat"
        ] == "0.00"
        assert report["metadata"][0]["3"]["name"] == "travel_cost"
        assert report["marksList"][0]["value"] == "_all_"

    def test_build_grid_fields_single_dimension(self):
        fields = build_grid_fields(["platform"], ["cost"])
        assert fields[0]["addedAs"] == "row"
        assert fields[1]["addedAs"] == "row"
        assert fields[1]["type"]["dataType"] == "numeric"

    def test_format_fields_empty_without_strings(self):
        fields = build_grid_fields(["a"], ["b"])
        assert build_format_fields(fields, None) == []
        assert build_format_fields(fields, {}) == []

    def test_format_match_case_insensitive(self):
        fields = build_grid_fields(["a"], ["Travel Cost"])
        fmt_fields = build_format_fields(fields, {"travel_cost": "#,##0.00"})
        assert len(fmt_fields) == 1
        assert fmt_fields[0]["values"]["customFormat"] == "#,##0.00"

    def test_minimal_properties_defaults(self):
        props = get_minimal_report_properties(show_totals=False)
        assert props["crosstab"]["showGrandTotals"] is False
        assert props["format"]["formatFields"] == []

    def test_report_object_to_js_is_json(self):
        js = report_object_to_js(get_report_object(["d"], ["m"]))
        assert '"autogen_alias": "d"' in js
        assert '"floatingType": "continous"' in js
