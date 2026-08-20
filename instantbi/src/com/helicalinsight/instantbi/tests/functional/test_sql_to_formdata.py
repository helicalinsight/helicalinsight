"""Functional tests for SQL → formData, including hidden ORDER BY columns."""
from __future__ import annotations

import pytest

from helicalbi.sql_to_formdata import FunctionCatalog, sql_to_form_data
from helicalbi.sql_to_formdata.metadata import build_column_index


pytestmark = pytest.mark.functional


SQL_ORDER_BY_NOT_IN_SELECT = """
select
    "travel_details"."booking_platform" as "Booking Platform",
    "travel_details"."travel_type" as "Travel Type",
    sum("travel_details"."travel_cost") as "Travel Cost"
from
    "sampletraveldata"."public"."travel_details"
group by
    "travel_details"."booking_platform",
    "travel_details"."travel_type",
    extract(month from "travel_details"."travel_date")
order by
    extract(month from "travel_details"."travel_date") asc
limit 10
"""

SQL_ORDER_BY_IN_SELECT = """
select
    "travel_details"."booking_platform" as "Booking Platform",
    "travel_details"."travel_type" as "Travel Type",
    sum("travel_details"."travel_cost") as "Travel Cost",
    extract(month from "travel_details"."travel_date") as "Travel Month"
from
    "sampletraveldata"."public"."travel_details"
group by
    "travel_details"."booking_platform",
    "travel_details"."travel_type",
    extract(month from "travel_details"."travel_date")
order by
    extract(month from "travel_details"."travel_date") asc
limit 10
"""


def _catalog() -> FunctionCatalog:
    return FunctionCatalog.from_api_payload(
        {
            "response": {
                "reference": "postgresql",
                "functions": {
                    "db.generic.aggregate.sum": "sum",
                    "db.generic.groupBy.group": "group",
                    "db.generic.orderBy.order": "order",
                },
                "databaseFunctions": {
                    "dateTime": [
                        {
                            "key": "sql.dateTime.month",
                            "value": "MONTH",
                            "signature": "extract(month from ${datetime})",
                            "returns": "numeric",
                            "parameters": [{"name": "datetime", "column": True}],
                        }
                    ],
                    "postgresql specific": [
                        {
                            "key": "sql.date.extract",
                            "value": "extract",
                            "signature": "extract(${unit} from ${date})",
                            "returns": "numeric",
                            "parameters": [
                                {"name": "unit", "defaultValue": "'century'"},
                                {"name": "date", "column": True},
                            ],
                        }
                    ],
                },
            }
        }
    )


def _metadata() -> dict:
    return build_column_index(
        {
            "name": "sampletraveldata.public",
            "tables": {
                "travel_details": {
                    "columns": {
                        "booking_platform": {
                            "id": "2868",
                            "alias": "booking_platform",
                        },
                        "travel_type": {"id": "2860", "alias": "travel_type"},
                        "travel_cost": {
                            "id": "2866",
                            "alias": "travel_cost",
                            "type": {"java.lang.Integer": "numeric"},
                        },
                        "travel_date": {"id": "2859", "alias": "travel_date"},
                    }
                },
                "employee_details": {
                    "columns": {
                        "employee_name": {
                            "id": "1051",
                            "alias": "employee_name",
                            "type": {"java.lang.String": "text"},
                        }
                    }
                },
            },
        }
    )


def _form_data(sql: str) -> dict:
    return sql_to_form_data(
        sql,
        location="0007",
        metadata_file_name="pg_sample_travel_data_agent.metadata",
        catalog=_catalog(),
        metadata=_metadata(),
        dialect="postgres",
    )


def _column_by_alias(form_data: dict, alias: str) -> dict:
    for col in form_data["columns"]:
        if col.get("alias") == alias:
            return col
    raise AssertionError(f"missing column alias {alias!r} in {form_data['columns']}")


class TestHiddenOrderByColumns:
    def test_order_by_extract_not_in_select_is_hidden(self):
        form_data = _form_data(SQL_ORDER_BY_NOT_IN_SELECT)

        aliases = [c["alias"] for c in form_data["columns"]]
        assert aliases == [
            "Booking Platform",
            "Travel Type",
            "Travel Cost",
            "Travel Month",
        ]

        month = _column_by_alias(form_data, "Travel Month")
        assert month["hidden"] is True
        assert month["includeInResultset"] is True
        assert month["column"] == {
            "name": "sampletraveldata.public.travel_details.travel_date",
            "id": "2859",
        }
        assert month["databaseFunction"] == {
            "functionName": "sql.dateTime.month",
            "dataType": "numeric",
            "parameters": {
                "datetime": "sampletraveldata.public.travel_details.travel_date"
            },
        }

        assert form_data["functions"]["groupBy"] == [
            {"column": "Booking Platform", "custom": True},
            {"column": "Travel Type", "custom": True},
            {"column": "Travel Month", "custom": True},
        ]
        assert form_data["functions"]["orderBy"] == [
            {"alias": "Travel Month", "order": "asc", "custom": True}
        ]
        assert form_data["limitBy"] == 10

    def test_order_by_extract_in_select_keeps_alias_and_is_not_hidden(self):
        form_data = _form_data(SQL_ORDER_BY_IN_SELECT)
        month = _column_by_alias(form_data, "Travel Month")
        assert "hidden" not in month
        assert "includeInResultset" not in month
        assert month["databaseFunction"]["functionName"] == "sql.dateTime.month"
        assert form_data["functions"]["orderBy"] == [
            {"alias": "Travel Month", "order": "asc", "custom": True}
        ]

    def test_plain_order_by_column_not_in_select_is_hidden(self):
        sql = """
        select travel_details.booking_platform as "Booking Platform"
        from sampletraveldata.public.travel_details
        order by travel_details.travel_date desc
        limit 5
        """
        form_data = _form_data(sql)
        hidden = _column_by_alias(form_data, "travel_date")
        assert hidden["hidden"] is True
        assert hidden["includeInResultset"] is True
        assert hidden["column"]["id"] == "2859"
        assert form_data["functions"]["orderBy"] == [
            {"alias": "travel_date", "order": "desc", "custom": True}
        ]


class TestILikeFilter:
    def test_ilike_contains_uses_like_custom_condition(self):
        sql = """
        SELECT
          "employee_details"."employee_name" AS "Employee Name"
        FROM "employee_details"
        WHERE
          "employee_details"."employee_name" ILIKE '%Mike%'
        ORDER BY
          "employee_details"."employee_name" ASC
        LIMIT 100
        """
        form_data = _form_data(sql)
        assert form_data["filters"] == [
            {
                "column": {
                    "name": "sampletraveldata.public.employee_details.employee_name",
                    "id": "1051",
                },
                "label": "Employee Name",
                "alias": "Employee Name",
                "operator": "AND",
                "dataType": "java.lang.String",
                "id": 0,
                "mode": "auto",
                "condition": "CONTAINS",
                "be_condition": "CUSTOM",
                "customCondition": "like",
                "be_value": ["'%Mike%'"],
                "values": ["Mike"],
                "encloseInQuotes": False,
            }
        ]
        assert form_data["customFilterExpression"] == " ${0} "
        assert form_data["functions"]["orderBy"] == [
            {"alias": "Employee Name", "order": "asc", "custom": True}
        ]
        assert "hidden" not in _column_by_alias(form_data, "Employee Name")

    def test_like_contains_matches_ilike_wire_shape(self):
        sql = """
        SELECT employee_details.employee_name AS "Employee Name"
        FROM employee_details
        WHERE employee_details.employee_name LIKE '%Mike%'
        """
        form_data = _form_data(sql)
        filt = form_data["filters"][0]
        assert filt["customCondition"] == "like"
        assert filt["be_value"] == ["'%Mike%'"]
        assert filt["values"] == ["Mike"]
        assert filt["alias"] == "Employee Name"
        assert filt["condition"] == "CONTAINS"

def _employee_sql(where: str) -> str:
    return f"""
    SELECT employee_details.employee_name AS "Employee Name"
    FROM employee_details
    WHERE {where}
    """


def _base_filter(**overrides) -> dict:
    wire = {
        "column": {
            "name": "sampletraveldata.public.employee_details.employee_name",
            "id": "1051",
        },
        "label": "Employee Name",
        "alias": "Employee Name",
        "operator": "AND",
        "dataType": "java.lang.String",
        "id": 0,
        "mode": "auto",
    }
    wire.update(overrides)
    return wire


class TestTextFilterConditions:
    """Adhoc text-filter dropdown → getFilters.js wire formData.

    Conditions: Contains, Custom, Does not contains, Does not ends with,
    Does not starts with, Ends with, Equals, Is Not Null, Is Null,
    Is One of, Is not One of, Not Equals, Starts with.
    """

    def _filter(self, where: str) -> dict:
        return _form_data(_employee_sql(where))["filters"][0]

    def test_equals(self):
        assert self._filter(
            "employee_details.employee_name = 'Ahmed Haider'"
        ) == _base_filter(
            condition="EQUALS", be_condition="EQUALS", be_value=["Ahmed Haider"], values=["Ahmed Haider"]
        )

    def test_not_equals(self):
        assert self._filter(
            "employee_details.employee_name <> 'Ahmed Haider'"
        ) == _base_filter(
            condition="NOT_EQUALS",
            be_condition="CUSTOM",
            customCondition="<>",
            be_value=["Ahmed Haider"],
            values=["Ahmed Haider"],
            isCustomValue=True,
        )

    def test_contains(self):
        assert self._filter(
            "employee_details.employee_name LIKE '%Haider%'"
        ) == _base_filter(
            condition="CONTAINS",
            be_condition="CUSTOM",
            customCondition="like",
            be_value=["'%Haider%'"],
            values=["Haider"],
            encloseInQuotes=False,
        )


    def test_starts_with(self):
        assert self._filter(
            "employee_details.employee_name LIKE 'Ahmed%'"
        ) == _base_filter(
            condition="STARTS_WITH",
            be_condition="CUSTOM",
            customCondition="like",
            be_value=["'Ahmed%'"],
            values=["Ahmed"],
            encloseInQuotes=False,
        )


    def test_ends_with(self):
        assert self._filter(
            "employee_details.employee_name LIKE '%Haider'"
        ) == _base_filter(
            condition="ENDS_WITH",
            be_condition="CUSTOM",
            customCondition="like",
            be_value=["'%Haider'"],
            values=["Haider"],
            encloseInQuotes=False,
        )


    def test_is_one_of(self):
        assert self._filter(
            "employee_details.employee_name IN ('Ahmed Haider', 'Alec Lynch')"
        ) == _base_filter(
            condition="IS_ONE_OF",
            be_condition="CUSTOM",
            customCondition=" IN (",
            be_value=["'Ahmed Haider','Alec Lynch')"],
            values=["Ahmed Haider", "Alec Lynch"],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_is_not_one_of(self):
        assert self._filter(
            "employee_details.employee_name NOT IN ('Ahmed Haider', 'Alec Lynch')"
        ) == _base_filter(
            condition="IS_NOT_ONE_OF",
            be_condition="CUSTOM",
            customCondition=" NOT IN (",
            be_value=["'Ahmed Haider','Alec Lynch')"],
            values=["Ahmed Haider", "Alec Lynch"],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_is_null(self):
        assert self._filter(
            "employee_details.employee_name IS NULL"
        ) == _base_filter(
            condition="IS_NULL",
            be_condition="CUSTOM",
            customCondition="IS NULL",
            encloseInQuotes=False,
        )

    
    def test_custom(self):
        filt = self._filter("employee_details.employee_name ~ 'Haider'")
        assert filt["condition"] == "CUSTOM"
        assert filt["be_condition"] == "CUSTOM"
        assert filt["mode"] == "custom"
        assert filt["isCustomValue"] is True
        assert filt["alias"] == "Employee Name"
        assert filt["values"] == filt["be_value"]
        assert filt["values"]
        assert "Haider" in str(filt.get("customCondition") or "") or "Haider" in str(
            filt.get("values") or ""
        )

    def test_complex_like_is_custom_and_keeps_original_values(self):
        filt = self._filter("employee_details.employee_name LIKE '%Hai%der%'")
        assert filt["condition"] == "CUSTOM"
        assert filt["be_condition"] == "CUSTOM"
        assert filt["values"] == filt["be_value"]
        assert any("%" in str(v) for v in filt["values"])


def _cost_sql(where: str) -> str:
    return f"""
    SELECT travel_details.travel_cost AS "Travel Cost"
    FROM travel_details
    WHERE {where}
    """


def _base_numeric(**overrides) -> dict:
    wire = {
        "column": {
            "name": "sampletraveldata.public.travel_details.travel_cost",
            "id": "2866",
        },
        "label": "Travel Cost",
        "alias": "Travel Cost",
        "operator": "AND",
        "dataType": "java.lang.Integer",
        "id": 0,
        "mode": "auto",
    }
    wire.update(overrides)
    return wire


class TestNumericFilterConditions:
    """Adhoc numeric-filter dropdown → getFilters.js wire formData.

    Conditions: Custom, Equals, In Range, In between, Is Not Null, Is Null,
    Is One of, Is greater than, Is greater than or equal to, Is less than,
    Is less than or equal to, Is not One of, Not Equals, Not in Range,
    Not in between.
    """

    def _filter(self, where: str) -> dict:
        return _form_data(_cost_sql(where))["filters"][0]

    def test_equals(self):
        assert self._filter("travel_details.travel_cost = 100") == _base_numeric(
            condition="EQUALS", be_condition="EQUALS", be_value=[100], values=[100]
        )

    def test_not_equals(self):
        assert self._filter("travel_details.travel_cost <> 100") == _base_numeric(
            condition="NOT_EQUALS",
            be_condition="CUSTOM",
            customCondition="<>",
            be_value=[100],
            values=[100],
            isCustomValue=True,
        )

    def test_is_greater_than(self):
        assert self._filter("travel_details.travel_cost > 100") == _base_numeric(
            condition="IS_GREATER_THAN",
            be_condition="CUSTOM",
            customCondition=">",
            be_value=["100"],
            values=[100],
            isCustomValue=True,
        )

    def test_is_greater_than_or_equal_to(self):
        assert self._filter("travel_details.travel_cost >= 100") == _base_numeric(
            condition="IS_GREATER_THAN_OR_EQUAL_TO",
            be_condition="CUSTOM",
            customCondition=">=",
            be_value=["100"],
            values=[100],
            isCustomValue=True,
        )

    def test_is_less_than(self):
        assert self._filter("travel_details.travel_cost < 100") == _base_numeric(
            condition="IS_LESS_THAN",
            be_condition="CUSTOM",
            customCondition="<",
            be_value=["100"],
            values=[100],
            isCustomValue=True,
        )

    def test_is_less_than_or_equal_to(self):
        assert self._filter("travel_details.travel_cost <= 100") == _base_numeric(
            condition="IS_LESS_THAN_OR_EQUAL_TO",
            be_condition="CUSTOM",
            customCondition="<=",
            be_value=["100"],
            values=[100],
            isCustomValue=True,
        )

    def test_is_one_of(self):
        assert self._filter(
            "travel_details.travel_cost IN (100, 200)"
        ) == _base_numeric(
            condition="IS_ONE_OF",
            be_condition="CUSTOM",
            customCondition=" IN (",
            be_value=["100,200)"],
            values=[100, 200],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_is_not_one_of(self):
        assert self._filter(
            "travel_details.travel_cost NOT IN (100, 200)"
        ) == _base_numeric(
            condition="IS_NOT_ONE_OF",
            be_condition="CUSTOM",
            customCondition=" NOT IN (",
            be_value=["100,200)"],
            values=[100, 200],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_in_between(self):
        assert self._filter(
            "travel_details.travel_cost BETWEEN 100 AND 200"
        ) == _base_numeric(
            condition="IS_BETWEEN",
            be_condition="CUSTOM",
            customCondition="BETWEEN",
            be_value=["100 AND 200"],
            values=[100, 200],
            isCustomValue=True,
        )

    def test_not_in_between(self):
        assert self._filter(
            "travel_details.travel_cost NOT BETWEEN 100 AND 200"
        ) == _base_numeric(
            condition="IS_NOT_BETWEEN",
            be_condition="CUSTOM",
            customCondition="NOT BETWEEN",
            be_value=["100 AND 200"],
            values=[100, 200],
            isCustomValue=True,
        )

    def test_in_range(self):
        assert self._filter(
            "(travel_details.travel_cost >= 100 AND travel_details.travel_cost <= 200)"
        ) == _base_numeric(
            condition="IN_RANGE",
            be_condition="IN_RANGE",
            be_value=[100.0, 200.0],
            values=[100, 200],
            encloseInQuotes=False,
            isCustomValue=True,
        )

    def test_not_in_range(self):
        assert self._filter(
            "(travel_details.travel_cost < 100 OR travel_details.travel_cost > 200)"
        ) == _base_numeric(
            condition="NOT_IN_RANGE",
            be_condition="NOT_IN_RANGE",
            be_value=[100.0, 200.0],
            values=[100, 200],
            encloseInQuotes=False,
            isCustomValue=True,
        )

    def test_is_null(self):
        assert self._filter("travel_details.travel_cost IS NULL") == _base_numeric(
            condition="IS_NULL",
            be_condition="CUSTOM",
            customCondition="IS NULL",
            encloseInQuotes=False,
        )


    def test_custom(self):
        filt = self._filter("travel_details.travel_cost IS DISTINCT FROM 0")
        assert filt["condition"] == "CUSTOM"
        assert filt["be_condition"] == "CUSTOM"
        assert filt["mode"] == "custom"
        assert filt["isCustomValue"] is True
        assert filt["alias"] == "Travel Cost"
        assert filt["values"] == filt["be_value"]
        assert filt["values"]


def _norm_sql(sql: str) -> str:
    return " ".join(str(sql).lower().replace('"', "").replace("'", "").split())


class TestUnmappedFunctionCustomColumn:
    """SQL functions missing from getFunctions → Adhoc custom column (selectRaw)."""

    def test_unknown_scalar_function_is_custom_column(self):
        form_data = _form_data(
            """
            select upper(employee_details.employee_name) as "Employee Name"
            from sampletraveldata.public.employee_details
            """
        )
        col = _column_by_alias(form_data, "Employee Name")
        assert col["custom"] is True
        assert isinstance(col["column"], str)
        assert "upper" in _norm_sql(col["column"])
        assert "employee_name" in _norm_sql(col["column"])
        assert "databaseFunction" not in col
        assert col["usedColumns"] == [
            "sampletraveldata.public.employee_details.employee_name"
        ]

    def test_anonymous_udf_is_custom_column(self):
        form_data = _form_data(
            """
            select xyzzy(travel_details.travel_cost) as "Mystery Cost"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Mystery Cost")
        assert col["custom"] is True
        assert isinstance(col["column"], str)
        assert "xyzzy" in _norm_sql(col["column"])
        assert "databaseFunction" not in col
        assert col["usedColumns"] == [
            "sampletraveldata.public.travel_details.travel_cost"
        ]

    def test_known_function_wrapping_unknown_nested_is_custom(self):
        form_data = _form_data(
            """
            select extract(month from xyzzy(travel_details.travel_date)) as "Travel Month"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Travel Month")
        assert col["custom"] is True
        assert isinstance(col["column"], str)
        assert "xyzzy" in _norm_sql(col["column"])
        assert "databaseFunction" not in col
        assert col["usedColumns"] == [
            "sampletraveldata.public.travel_details.travel_date"
        ]

    def test_mapped_extract_is_not_custom(self):
        form_data = _form_data(
            """
            select extract(month from travel_details.travel_date) as "Travel Month"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Travel Month")
        assert "custom" not in col
        assert col["databaseFunction"]["functionName"] == "sql.dateTime.month"
        assert col["column"] == {
            "name": "sampletraveldata.public.travel_details.travel_date",
            "id": "2859",
        }

    def test_sum_of_unknown_function_is_custom_measure(self):
        form_data = _form_data(
            """
            select sum(upper(travel_details.travel_cost)) as "Travel Cost"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Travel Cost")
        assert col["custom"] is True
        assert col["aggregate"] is True
        assert col["aggregateList"] == ["db.generic.aggregate.sum"]
        assert isinstance(col["column"], str)
        assert "upper" in _norm_sql(col["column"])
        assert "sum(" not in _norm_sql(col["column"])
        assert "databaseFunction" not in col
        assert form_data["functions"]["aggregate"] == [
            {
                "column": col["column"],
                "function": "db.generic.aggregate.sum",
                "alias": "Travel Cost",
                "custom": True,
            }
        ]
