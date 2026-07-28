"""Functional tests for ``helicalbi.common.JsonToPara``."""
import pytest

from helicalbi.common.JsonToPara import (
    convert_metadata_to_text,
    describe_dict,
    generate_semantic_hint,
    get_domain_details,
    snake_to_words,
)


pytestmark = pytest.mark.functional


class TestSnakeToWords:
    @pytest.mark.parametrize(
        ("key", "expected"),
        [
            ("employee_id", "Employee Id"),
            ("client_name", "Client Name"),
            ("simple", "Simple"),
            ("multi_word_key_here", "Multi Word Key Here"),
            ("", ""),
        ],
    )
    def test_conversions(self, key, expected):
        assert snake_to_words(key) == expected


class TestDescribeDict:
    def test_describes_flat_dict(self):
        lines = describe_dict({"employee_id": 1, "employee_name": "Ada"})
        assert "Employee Id: 1" in lines
        assert "Employee Name: Ada" in lines

    def test_describes_nested_dict(self):
        lines = describe_dict({"address": {"city": "Pune"}})
        assert "Address:" in lines
        assert any("City: Pune" in line for line in lines)

    def test_describes_list_of_primitives(self):
        lines = describe_dict({"tags": ["a", "b", "c"]})
        assert "Tags: a, b, c" in lines

    def test_describes_list_of_dicts_creates_blank_separators(self):
        data = {"members": [{"name": "A"}, {"name": "B"}]}
        lines = describe_dict(data)
        assert "Members:" in lines
        assert lines.count("") >= 2

    def test_prefix_is_applied(self):
        lines = describe_dict({"k": "v"}, prefix=">>")
        assert lines == [">>K: v"]


class TestConvertMetadataToText:
    def test_emits_header_for_each_block(self):
        result = convert_metadata_to_text([{"name": "A"}, {"name": "B"}])
        assert "Metadata Block 1:" in result
        assert "Metadata Block 2:" in result

    def test_includes_descriptive_lines(self):
        result = convert_metadata_to_text([{"employee_id": 7}])
        assert "Employee Id: 7" in result


class TestGenerateSemanticHint:
    def test_builds_table_and_columns_sections(self):
        metadata = [
            {
                "database_table": "employee_details",
                "description": "Employees",
                "columns": [
                    {"column_name": "employee_id", "description": "PK"},
                    {"column_name": "employee_name", "description": "Full name"},
                ],
            }
        ]
        hint = generate_semantic_hint(metadata)
        assert "Table: employee_details (Employees)" in hint
        assert "employee_id" in hint
        assert "Description: PK" in hint
        assert "employee_name" in hint
        assert "Description: Full name" in hint
        assert "Columns:" in hint

    def test_handles_missing_optional_fields(self):
        hint = generate_semantic_hint([{"database_table": "t", "columns": []}])
        assert "Table: t" in hint
        assert "Columns:" in hint

        assert generate_semantic_hint([{"columns": []}]) == ""

    def test_includes_hierarchy_and_ai_context(self):
        hint = generate_semantic_hint(
            [
                {
                    "database_table": "travel_details",
                    "columns": [
                        {
                            "column_name": "travel_date",
                            "alias_name": "YEAR",
                            "dimension_name": "YEAR",
                            "hierarchy_name": "travel_date",
                            "level_name": "YEAR",
                            "semantic_type": "Text",
                            "formula": "EXTRACT(YEAR from travel_details.travel_date)",
                            "ai_context": {
                                "instructions": "Year of travel.",
                                "synonyms": "year",
                                "examples": "2024",
                            },
                        }
                    ],
                    "measures": [
                        {
                            "column_name": "Cost by Cancellation",
                            "alias_name": "Cost by Cancellation",
                            "measure_name": "Cost by Cancellation",
                            "semantic_type": "Number",
                            "is_computed": True,
                            "formula": "sum(travel_details.travel_cost) filter status=Yes",
                            "ai_context": {
                                "instructions": "Yes means cancelled.",
                                "synonyms": "cancelled",
                                "examples": "",
                            },
                        }
                    ],
                }
            ]
        )
        assert "hierarchy: travel_date" in hint
        assert "level: YEAR" in hint
        assert "aiContext:" in hint
        assert "Year of travel." in hint
        assert "measure: Cost by Cancellation" in hint
        assert "computed formula: sum(travel_details.travel_cost) filter status=Yes" in hint
        assert "type: Number" in hint
        assert "Yes means cancelled." in hint


class TestGetDomainDetails:
    @pytest.fixture
    def data(self):
        return [
            {
                "domain": ["Sales"],
                "topics": ["Travel", "Meetings"],
                "database_table": "travel_details",
                "columns": [
                    {"column_name": "travel_id", "column_metrics": {"data_type": "numeric"}},
                    {"column_name": "destination", "column_metrics": {"data_type": "text"}},
                ],
            },
            {
                "domain": ["HR"],
                "topics": ["Payroll"],
                "database_table": "salary",
                "columns": [
                    {"column_name": "amount", "column_metrics": {"data_type": "numeric"}},
                ],
            },
        ]

    def test_finds_matching_domain(self, data):
        result = get_domain_details(data, "Sales")
        assert "Travel, Meetings" in result
        assert "travel_details" in result
        assert "travel_id(numeric)" in result
        assert "destination(text)" in result

    def test_returns_not_found_for_unknown_domain(self, data):
        assert get_domain_details(data, "Unknown") == "Domain not found"

    def test_uses_unknown_when_data_type_missing(self):
        data = [
            {
                "domain": ["X"],
                "topics": [],
                "database_table": "tbl",
                "columns": [{"column_name": "c"}],
            }
        ]
        result = get_domain_details(data, "X")
        assert "c(unknown)" in result
