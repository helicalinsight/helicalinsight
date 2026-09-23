"""Functional tests for SQL → formData, including hidden ORDER BY columns."""
from __future__ import annotations

import pytest

from helicalbi.common.DialectMapper import resolve_sqlglot_dialect
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
                    "db.generic.aggregate.count": "count",
                    "db.generic.aggregate.avg": "avg",
                    "db.generic.aggregate.min": "min",
                    "db.generic.aggregate.max": "max",
                    "db.generic.aggregate.distinct": "distinct",
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
                        },
                        {
                            "key": "sql.date.datetrunc",
                            "value": "DATETRUNC",
                            "signature": "date_trunc(${unit},${date})",
                            "returns": "date",
                            "parameters": [
                                {"name": "unit"},
                                {"name": "date", "column": True},
                            ],
                        },
                        {
                            "key": "sql.date.to_char",
                            "value": "to_char",
                            "signature": "to_char(${value} , '${formatMask}')",
                            "returns": "text",
                            "parameters": [
                                {"name": "value", "column": True},
                                {"name": "formatMask"},
                            ],
                        },
                    ],
                    "string": [
                        {
                            "key": "sql.string.concat",
                            "value": "CONCAT",
                            "signature": "concat(${string1}, ${string2})",
                            "returns": "text",
                            "parameters": [
                                {"name": "string1", "column": True},
                                {"name": "string2", "column": True},
                            ],
                        },
                        {
                            "key": "sql.string.length",
                            "value": "LENGTH",
                            "signature": "length(cast(${string} as VARCHAR))",
                            "returns": "numeric",
                            "parameters": [{"name": "string", "column": True}],
                        },
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


def _catalog_with_round_and_case() -> FunctionCatalog:
    """Catalog that includes ROUND/CASE — reproduces production getFunctions traps."""
    payload = {
        "response": {
            "reference": "postgresql",
            "functions": {
                "db.generic.aggregate.sum": "sum",
                "db.generic.aggregate.count": "count",
                "db.generic.aggregate.avg": "avg",
                "db.generic.aggregate.min": "min",
                "db.generic.aggregate.max": "max",
                "db.generic.aggregate.distinct": "distinct",
                "db.generic.groupBy.group": "group",
                "db.generic.orderBy.order": "order",
            },
            "databaseFunctions": {
                "numeric": [
                    {
                        "key": "sql.numeric.round",
                        "value": "ROUND",
                        "signature": "round(${numeric},${decimals})",
                        "returns": "numeric",
                        "parameters": [{"name": "numeric"}, {"name": "decimals"}],
                    },
                    {
                        "key": "sql.numeric.nullif",
                        "value": "NULLIF",
                        "signature": "nullif(${value1},${value2})",
                        "returns": "numeric",
                        "parameters": [{"name": "value1"}, {"name": "value2"}],
                    },
                ],
                "conditional": [
                    {
                        "key": "sql.conditional.case",
                        "value": "CASE",
                        "signature": "case()",
                        "returns": "other",
                        "parameters": [],
                    },
                ],
            },
        }
    }
    return FunctionCatalog.from_api_payload(payload)


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
                        "travel_id": {
                            "id": "1064",
                            "alias": "travel_id",
                            "type": {"java.lang.Integer": "numeric"},
                        },
                        "travel_type": {"id": "2860", "alias": "travel_type"},
                        "travel_cost": {
                            "id": "2866",
                            "alias": "travel_cost",
                            "type": {"java.lang.Integer": "numeric"},
                        },
                        "travel_date": {"id": "2859", "alias": "travel_date"},
                        "destination": {
                            "id": "2870",
                            "alias": "destination",
                            "type": {"java.lang.String": "text"},
                        },
                        "source": {
                            "id": "2871",
                            "alias": "source",
                            "type": {"java.lang.String": "text"},
                        },
                        "travelled_by": {
                            "id": "2872",
                            "alias": "travelled_by",
                            "type": {"java.lang.Integer": "numeric"},
                        },
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
                "meeting_details": {
                    "columns": {
                        "meeting_by": {
                            "id": "1044",
                            "alias": "meeting_by",
                            "type": {"java.lang.String": "text"},
                        },
                        "meet_cancellation_status": {
                            "id": "1048",
                            "alias": "meet_cancellation_status",
                            "type": {"java.lang.String": "text"},
                        }
                    }
                },
            },
        }
    )


def _form_data(sql: str, **kwargs) -> dict:
    opts = {
        "location": "0007",
        "metadata_file_name": "pg_sample_travel_data_agent.metadata",
        "catalog": _catalog(),
        "metadata": _metadata(),
        "dialect": "postgres",
    }
    opts.update(kwargs)
    return sql_to_form_data(sql, **opts)


def _column_by_alias(form_data: dict, alias: str) -> dict:
    for col in form_data["columns"]:
        if col.get("alias") == alias:
            return col
    raise AssertionError(f"missing column alias {alias!r} in {form_data['columns']}")


def _norm_sql(sql: str) -> str:
    return " ".join(str(sql).lower().replace('"', "").replace("'", "").split())


def _is_raw_fn(value: object) -> bool:
    text = str(value or "").strip()
    return text[:4].upper() == "RAW(" and text.endswith(")")


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
        assert "custom" not in month
        assert month["databaseFunction"] == 'MONTH("travel_details"."travel_date")'
        assert month["usedColumns"] == [
            "sampletraveldata.public.travel_details.travel_date"
        ]

        assert form_data["functions"]["groupBy"] == [
            {"column": "Booking Platform", "custom": True},
            {"column": "Travel Type", "custom": True},
            {"column": "Travel Month", "custom": True},
        ]
        assert month["order"] == "asc"
        assert "orderBy" not in form_data.get("functions", {})
        assert form_data["limitBy"] == 10

    def test_order_by_extract_in_select_keeps_alias_and_is_not_hidden(self):
        form_data = _form_data(SQL_ORDER_BY_IN_SELECT)
        month = _column_by_alias(form_data, "Travel Month")
        assert "hidden" not in month
        assert "includeInResultset" not in month
        assert "custom" not in month
        assert month["databaseFunction"] == 'MONTH("travel_details"."travel_date")'
        assert month["column"] == {
            "name": "sampletraveldata.public.travel_details.travel_date",
            "id": "2859",
        }
        assert month["usedColumns"] == [
            "sampletraveldata.public.travel_details.travel_date"
        ]
        assert month["order"] == "asc"
        assert "orderBy" not in form_data.get("functions", {})

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
        assert hidden["order"] == "desc"
        assert "order" not in _column_by_alias(form_data, "Booking Platform")
        assert "orderBy" not in form_data.get("functions", {})


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
                "id": 0,
                "mode": "auto",
                "condition": "CONTAINS",
                "customCondition": "like",
                "values": ["Mike"],
                "encloseInQuotes": False,
            }
        ]
        assert form_data["customFilterExpression"] == " ${0} "
        assert form_data["filterExpression"] == ["Employee Name"]
        # Lone SELECT columns stay in data_model even when they are also filtered.
        assert [c.get("alias") for c in form_data.get("columns") or []] == ["Employee Name"]
        assert "orderBy" not in form_data.get("functions", {})

    def test_extract_filter_uses_database_function_on_signature_match(self):
        sql = """
        SELECT travel_details.booking_platform AS "Platform"
        FROM travel_details
        WHERE extract(month from travel_details.travel_date) = 3
        """
        form_data = _form_data(sql)
        filt = form_data["filters"][0]
        assert "custom" not in filt
        assert filt["column"] == {
            "name": "sampletraveldata.public.travel_details.travel_date",
            "id": "2859",
        }
        assert filt["databaseFunction"] == 'MONTH("travel_details"."travel_date")'
        assert filt["usedColumns"] == [
            "sampletraveldata.public.travel_details.travel_date"
        ]
        assert filt["values"] == [3]

    def test_unmapped_filter_function_falls_back_to_raw(self):
        sql = """
        SELECT travel_details.booking_platform AS "Platform"
        FROM travel_details
        WHERE totally_unknown_fn(travel_details.travel_date) = 1
        """
        form_data = _form_data(sql)
        filt = form_data["filters"][0]
        assert "custom" not in filt
        assert filt["column"] == {
            "name": "sampletraveldata.public.travel_details.travel_date",
            "id": "2859",
        }
        assert _is_raw_fn(filt["databaseFunction"])
        assert "totally_unknown_fn" in _norm_sql(filt["databaseFunction"])
        assert "travel_date" in _norm_sql(filt["databaseFunction"])
        assert filt["usedColumns"] == [
            "sampletraveldata.public.travel_details.travel_date"
        ]

    def test_nested_filter_database_function_expression(self):
        sql = """
        SELECT travel_details.booking_platform AS "Platform"
        FROM travel_details
        WHERE length(cast(concat(travel_details.destination, ' x') as varchar)) = 5
        """
        form_data = _form_data(sql)
        filt = form_data["filters"][0]
        assert "custom" not in filt
        assert filt["databaseFunction"] == (
            'LENGTH(CONCAT("travel_details"."destination", \' x\'))'
        )
        assert filt["usedColumns"] == [
            "sampletraveldata.public.travel_details.destination"
        ]
        assert filt["values"] == [5]

    def test_nested_to_char_datetrunc_filter_database_function(self):
        sql = """
        SELECT travel_details.booking_platform AS "Platform"
        FROM travel_details
        WHERE to_char(date_trunc('month', travel_details.travel_date), 'YYYY-MM') = '2024-01'
        """
        form_data = _form_data(sql)
        filt = form_data["filters"][0]
        assert "custom" not in filt
        assert filt["databaseFunction"] == (
            'to_char(DATETRUNC(\'MONTH\', "travel_details"."travel_date"), YYYY-MM)'
        )
        assert filt["values"] == ["2024-01"]

    def test_date_trunc_bound_is_catalog_value_not_select_alias(self):
        """WHERE col >= DATE_TRUNC(...) is a function bound, not a column function.

        ``databaseFunction`` on a filter means the function wraps the filtered
        column (MONTH(date) = 3). DATE_TRUNC on CURRENT_DATE belongs in values.
        Also do not steal SELECT aliases like Year/Month from EXTRACT on the
        same physical column.
        """
        sql = """
        SELECT
          EXTRACT(YEAR FROM travel_details.travel_date) AS "Year",
          EXTRACT(MONTH FROM travel_details.travel_date) AS "Month",
          SUM(travel_details.travel_cost) AS "Travel Cost"
        FROM travel_details
        WHERE travel_details.travel_date >= DATE_TRUNC('QUARTER', CURRENT_DATE - INTERVAL '3 MONTHS')
          AND travel_details.travel_date < DATE_TRUNC('QUARTER', CURRENT_DATE)
        GROUP BY
          EXTRACT(YEAR FROM travel_details.travel_date),
          EXTRACT(MONTH FROM travel_details.travel_date)
        LIMIT 100
        """
        form_data = _form_data(sql)
        filters = form_data["filters"]
        assert len(filters) == 2
        for filt in filters:
            assert "databaseFunction" not in filt
            assert filt["column"] == {
                "name": "sampletraveldata.public.travel_details.travel_date",
                "id": "2859",
            }
            assert filt["alias"] == "travel_date"
            assert filt["label"] == "travel_date"
            value = str((filt.get("values") or [""])[0])
            assert "TIMESTAMP_TRUNC" not in value.upper()
            assert "DATETRUNC" in value.upper() or "DATE_TRUNC" in value.upper()
            assert "QUARTER" in value.upper()
            assert "CURRENT_DATE" in value.upper()
        assert filters[0]["condition"] == "CUSTOM"
        assert filters[0]["customCondition"] == ">="
        assert filters[0]["isCustomValue"] is True
        assert filters[0]["encloseInQuotes"] is False
        assert filters[1]["condition"] == "CUSTOM"
        assert filters[1]["customCondition"] == "<"
        assert filters[1]["isCustomValue"] is True
        assert filters[1]["encloseInQuotes"] is False

    def test_like_contains_matches_ilike_wire_shape(self):
        sql = """
        SELECT employee_details.employee_name AS "Employee Name"
        FROM employee_details
        WHERE employee_details.employee_name LIKE '%Mike%'
        """
        form_data = _form_data(sql)
        filt = form_data["filters"][0]
        assert filt["customCondition"] == "like"
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
            condition="EQUALS", values=["Ahmed Haider"]
        )

    def test_not_equals(self):
        assert self._filter(
            "employee_details.employee_name <> 'Ahmed Haider'"
        ) == _base_filter(
            condition="CUSTOM",
            customCondition="<>",
            values=["Ahmed Haider"],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_contains(self):
        assert self._filter(
            "employee_details.employee_name LIKE '%Haider%'"
        ) == _base_filter(
            condition="CONTAINS",
            customCondition="like",
            values=["Haider"],
            encloseInQuotes=False,
        )


    def test_starts_with(self):
        assert self._filter(
            "employee_details.employee_name LIKE 'Ahmed%'"
        ) == _base_filter(
            condition="STARTS_WITH",
            customCondition="like",
            values=["Ahmed"],
            encloseInQuotes=False,
        )


    def test_ends_with(self):
        assert self._filter(
            "employee_details.employee_name LIKE '%Haider'"
        ) == _base_filter(
            condition="ENDS_WITH",
            customCondition="like",
            values=["Haider"],
            encloseInQuotes=False,
        )


    def test_is_one_of(self):
        assert self._filter(
            "employee_details.employee_name IN ('Ahmed Haider', 'Alec Lynch')"
        ) == _base_filter(
            condition="IS_ONE_OF",
            customCondition=" IN (",
            values=["Ahmed Haider", "Alec Lynch"],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_is_not_one_of(self):
        assert self._filter(
            "employee_details.employee_name NOT IN ('Ahmed Haider', 'Alec Lynch')"
        ) == _base_filter(
            condition="IS_NOT_ONE_OF",
            customCondition=" NOT IN (",
            values=["Ahmed Haider", "Alec Lynch"],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_is_null(self):
        assert self._filter(
            "employee_details.employee_name IS NULL"
        ) == _base_filter(
            condition="CUSTOM",
            customCondition="IS NULL",
            encloseInQuotes=False,
        )

    
    def test_custom(self):
        filt = self._filter("employee_details.employee_name ~ 'Haider'")
        assert filt["condition"] == "CUSTOM"
        assert filt["mode"] == "custom"
        assert filt["isCustomValue"] is True
        assert filt["alias"] == "Employee Name"
        assert filt["values"]
        assert "Haider" in str(filt.get("customCondition") or "") or "Haider" in str(
            filt.get("values") or ""
        )

    def test_complex_like_is_custom_and_keeps_original_values(self):
        filt = self._filter("employee_details.employee_name LIKE '%Hai%der%'")
        assert filt["condition"] == "CUSTOM"
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
            condition="EQUALS", values=[100]
        )

    def test_not_equals(self):
        assert self._filter("travel_details.travel_cost <> 100") == _base_numeric(
            condition="CUSTOM",
            customCondition="<>",
            values=[100],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_is_greater_than(self):
        assert self._filter("travel_details.travel_cost > 100") == _base_numeric(
            condition="CUSTOM",
            customCondition=">",
            values=[100],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_is_greater_than_or_equal_to(self):
        assert self._filter("travel_details.travel_cost >= 100") == _base_numeric(
            condition="CUSTOM",
            customCondition=">=",
            values=[100],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_is_less_than(self):
        assert self._filter("travel_details.travel_cost < 100") == _base_numeric(
            condition="CUSTOM",
            customCondition="<",
            values=[100],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_is_less_than_or_equal_to(self):
        assert self._filter("travel_details.travel_cost <= 100") == _base_numeric(
            condition="CUSTOM",
            customCondition="<=",
            values=[100],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_is_one_of(self):
        assert self._filter(
            "travel_details.travel_cost IN (100, 200)"
        ) == _base_numeric(
            condition="IS_ONE_OF",
            customCondition=" IN (",
            values=[100, 200],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_is_not_one_of(self):
        assert self._filter(
            "travel_details.travel_cost NOT IN (100, 200)"
        ) == _base_numeric(
            condition="IS_NOT_ONE_OF",
            customCondition=" NOT IN (",
            values=[100, 200],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_in_between(self):
        assert self._filter(
            "travel_details.travel_cost BETWEEN 100 AND 200"
        ) == _base_numeric(
            condition="IS_BETWEEN",
            customCondition="BETWEEN",
            values=[100, 200],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_not_in_between(self):
        assert self._filter(
            "travel_details.travel_cost NOT BETWEEN 100 AND 200"
        ) == _base_numeric(
            condition="IS_NOT_BETWEEN",
            customCondition="NOT BETWEEN",
            values=[100, 200],
            isCustomValue=True,
            encloseInQuotes=False,
        )

    def test_in_range(self):
        assert self._filter(
            "(travel_details.travel_cost >= 100 AND travel_details.travel_cost <= 200)"
        ) == _base_numeric(
            condition="IN_RANGE",
            values=[100, 200],
            encloseInQuotes=False,
            isCustomValue=True,
        )

    def test_not_in_range(self):
        assert self._filter(
            "(travel_details.travel_cost < 100 OR travel_details.travel_cost > 200)"
        ) == _base_numeric(
            condition="NOT_IN_RANGE",
            values=[100, 200],
            encloseInQuotes=False,
            isCustomValue=True,
        )

    def test_is_null(self):
        assert self._filter("travel_details.travel_cost IS NULL") == _base_numeric(
            condition="CUSTOM",
            customCondition="IS NULL",
            encloseInQuotes=False,
        )


    def test_custom(self):
        filt = self._filter("travel_details.travel_cost IS DISTINCT FROM 0")
        assert filt["condition"] == "CUSTOM"
        assert filt["mode"] == "custom"
        assert filt["isCustomValue"] is True
        assert filt["alias"] == "Travel Cost"
        assert filt["values"]


class TestUnmappedFunctionRawColumn:
    """SQL functions missing from getFunctions → RAW(complete expression)."""

    def test_unknown_scalar_function_is_raw(self):
        form_data = _form_data(
            """
            select upper(employee_details.employee_name) as "Employee Name"
            from sampletraveldata.public.employee_details
            """
        )
        col = _column_by_alias(form_data, "Employee Name")
        assert "custom" not in col
        assert col["column"] == {
            "name": "sampletraveldata.public.employee_details.employee_name",
            "id": "1051",
        }
        assert _is_raw_fn(col["databaseFunction"])
        assert "upper" in _norm_sql(col["databaseFunction"])
        assert "employee_name" in _norm_sql(col["databaseFunction"])
        assert col["usedColumns"] == [
            "sampletraveldata.public.employee_details.employee_name"
        ]

    def test_anonymous_udf_is_raw(self):
        form_data = _form_data(
            """
            select xyzzy(travel_details.travel_cost) as "Mystery Cost"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Mystery Cost")
        assert "custom" not in col
        assert col["column"] == {
            "name": "sampletraveldata.public.travel_details.travel_cost",
            "id": "2866",
        }
        assert _is_raw_fn(col["databaseFunction"])
        assert "xyzzy" in _norm_sql(col["databaseFunction"])
        assert col["usedColumns"] == [
            "sampletraveldata.public.travel_details.travel_cost"
        ]

    def test_known_function_wrapping_unknown_nested_is_raw(self):
        form_data = _form_data(
            """
            select extract(month from xyzzy(travel_details.travel_date)) as "Travel Month"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Travel Month")
        assert "custom" not in col
        assert col["column"] == {
            "name": "sampletraveldata.public.travel_details.travel_date",
            "id": "2859",
        }
        assert _is_raw_fn(col["databaseFunction"])
        assert "xyzzy" in _norm_sql(col["databaseFunction"])
        assert col["usedColumns"] == [
            "sampletraveldata.public.travel_details.travel_date"
        ]

    def test_concat_extra_args_beyond_catalog_is_raw(self):
        """3-arg CONCAT must not truncate to catalog's 2-param signature."""
        form_data = _form_data(
            """
            select length(concat(
                'Mr',
                employee_details.employee_name,
                rpad('', 20 - length(concat('Mr', employee_details.employee_name)), ' ')
            )) as "Employee Name Length with Mr and Padding"
            from sampletraveldata.public.employee_details
            """
        )
        col = _column_by_alias(
            form_data, "Employee Name Length with Mr and Padding"
        )
        assert "custom" not in col
        assert col["column"] == {
            "name": "sampletraveldata.public.employee_details.employee_name",
            "id": "1051",
        }
        assert _is_raw_fn(col["databaseFunction"])
        assert "rpad" in _norm_sql(col["databaseFunction"])
        assert col["usedColumns"] == [
            "sampletraveldata.public.employee_details.employee_name"
        ]

    def test_literal_expression_without_used_columns_stays_custom(self):
        form_data = _form_data(
            """
            select 1 + 2 as "Three"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Three")
        assert col["custom"] is True
        assert isinstance(col["column"], str)
        assert "databaseFunction" not in col
        assert not col.get("usedColumns")

    def test_unmapped_formula_uses_raw_and_one_host_column(self):
        form_data = _form_data(
            """
            select xyzzy(travel_details.travel_cost) * 100 as "Mystery Percent"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Mystery Percent")
        assert "custom" not in col
        assert col["column"] == {
            "name": "sampletraveldata.public.travel_details.travel_cost",
            "id": "2866",
        }
        raw = col["databaseFunction"]
        assert _is_raw_fn(raw)
        inner = raw[4:-1]
        assert "xyzzy" in _norm_sql(inner)
        assert "100" in inner
        assert col["usedColumns"] == [
            "sampletraveldata.public.travel_details.travel_cost"
        ]

    def test_multi_column_formula_picks_one_host_and_keeps_all_used(self):
        form_data = _form_data(
            """
            select xyzzy(travel_details.travel_cost) + xyzzy(travel_details.travel_date)
                as "Mystery Mix"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Mystery Mix")
        assert "custom" not in col
        assert col["column"]["name"] in col["usedColumns"]
        assert col["column"]["id"] in {"2866", "2859"}
        assert _is_raw_fn(col["databaseFunction"])
        inner = col["databaseFunction"][4:-1]
        assert "xyzzy" in _norm_sql(inner)
        assert set(col["usedColumns"]) == {
            "sampletraveldata.public.travel_details.travel_cost",
            "sampletraveldata.public.travel_details.travel_date",
        }

    def test_mapped_extract_uses_database_function(self):
        form_data = _form_data(
            """
            select extract(month from travel_details.travel_date) as "Travel Month"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Travel Month")
        assert "custom" not in col
        assert col["column"] == {
            "name": "sampletraveldata.public.travel_details.travel_date",
            "id": "2859",
        }
        assert col["databaseFunction"] == 'MONTH("travel_details"."travel_date")'
        assert col["usedColumns"] == [
            "sampletraveldata.public.travel_details.travel_date"
        ]

    def test_mapped_concat_uses_database_function(self):
        form_data = _form_data(
            """
            select concat('Mr ', employee_details.employee_name) as "Employee Name"
            from sampletraveldata.public.employee_details
            """
        )
        col = _column_by_alias(form_data, "Employee Name")
        assert "custom" not in col
        assert col["column"] == {
            "name": "sampletraveldata.public.employee_details.employee_name",
            "id": "1051",
        }
        assert col["databaseFunction"] == (
            'CONCAT(\'Mr \', "employee_details"."employee_name")'
        )
        assert col["usedColumns"] == [
            "sampletraveldata.public.employee_details.employee_name"
        ]

    def test_sum_of_unknown_function_is_raw_measure(self):
        form_data = _form_data(
            """
            select sum(upper(travel_details.travel_cost)) as "Travel Cost"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Travel Cost")
        assert "custom" not in col
        assert col["aggregate"] is True
        assert col["aggregateList"] == ["db.generic.aggregate.sum"]
        assert col["column"] == {
            "name": "sampletraveldata.public.travel_details.travel_cost",
            "id": "2866",
        }
        assert _is_raw_fn(col["databaseFunction"])
        assert "upper" in _norm_sql(col["databaseFunction"])
        assert "sum(" not in _norm_sql(col["databaseFunction"])
        assert "aggregate" not in (form_data.get("functions") or {})


class TestDerbyDialect:
    """HI derby is not a sqlglot dialect; map it so KPI SQL still yields columns."""

    def test_derby_maps_to_oracle(self):
        catalog = FunctionCatalog.from_api_payload({"response": {"reference": "derby"}})
        assert resolve_sqlglot_dialect("derby") == "oracle"
        assert catalog.dialect == "oracle"

    def test_kpi_sum_columns_are_populated(self):
        sql = """
        SELECT
          SUM("travel_details"."travel_cost") AS "Travel Cost"
        FROM "travel_details"
        """
        form_data = sql_to_form_data(
            sql,
            location="08_26",
            metadata_file_name="Metadata_1.metadata",
            catalog=FunctionCatalog.from_api_payload(
                {
                    "response": {
                        "reference": "derby",
                        "functions": {"db.generic.aggregate.sum": "sum"},
                    }
                }
            ),
            metadata=_metadata(),
            dialect="derby",
        )
        col = _column_by_alias(form_data, "Travel Cost")
        assert col["aggregate"] is True
        assert col["aggregateList"] == ["db.generic.aggregate.sum"]
        assert col["column"] == {
            "name": "sampletraveldata.public.travel_details.travel_cost",
            "id": "2866",
        }


class TestStackedAggregates:
    """Nested aggregates become ordered aggregateList (outer → inner)."""

    def test_sum_count_is_stacked(self):
        form_data = _form_data(
            """
            select sum(count(travel_details.destination)) as "Destination Count"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Destination Count")
        assert col["aggregate"] is True
        assert col["aggregateList"] == [
            "db.generic.aggregate.sum",
            "db.generic.aggregate.count",
        ]
        assert col["column"] == {
            "name": "sampletraveldata.public.travel_details.destination",
            "id": "2870",
        }
        assert "aggregate" not in (form_data.get("functions") or {})

    def test_count_distinct_is_stacked(self):
        form_data = _form_data(
            """
            select count(distinct travel_details.destination) as "Distinct Destination Count"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Distinct Destination Count")
        assert col["aggregate"] is True
        assert col["aggregateList"] == [
            "db.generic.aggregate.count",
            "db.generic.aggregate.distinct",
        ]
        assert col["column"] == {
            "name": "sampletraveldata.public.travel_details.destination",
            "id": "2870",
        }
        assert "aggregate" not in (form_data.get("functions") or {})

    def test_sum_count_distinct_is_stacked(self):
        form_data = _form_data(
            """
            select sum(count(distinct travel_details.destination)) as "Distinct Destination Count"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Distinct Destination Count")
        assert col["aggregateList"] == [
            "db.generic.aggregate.sum",
            "db.generic.aggregate.count",
            "db.generic.aggregate.distinct",
        ]
        assert "aggregate" not in (form_data.get("functions") or {})

    def test_avg_sum_count_is_stacked(self):
        form_data = _form_data(
            """
            select avg(sum(count(travel_details.destination))) as "Nested Count"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Nested Count")
        assert col["aggregateList"] == [
            "db.generic.aggregate.avg",
            "db.generic.aggregate.sum",
            "db.generic.aggregate.count",
        ]
        assert "aggregate" not in (form_data.get("functions") or {})

    def test_sum_distinct_appends_distinct(self):
        form_data = _form_data(
            """
            select sum(distinct travel_details.travel_cost) as "Distinct Travel Cost"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Distinct Travel Cost")
        assert col["aggregateList"] == [
            "db.generic.aggregate.sum",
            "db.generic.aggregate.distinct",
        ]

    def test_distinct_count_is_stacked(self):
        form_data = _form_data(
            """
            select distinct(count(travel_details.destination)) as "Destination Count"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Destination Count")
        assert col["aggregate"] is True
        assert col["aggregateList"] == [
            "db.generic.aggregate.distinct",
            "db.generic.aggregate.count",
        ]
        assert col["column"] == {
            "name": "sampletraveldata.public.travel_details.destination",
            "id": "2870",
        }
        assert "aggregate" not in (form_data.get("functions") or {})

    def test_select_distinct_count_is_stacked(self):
        form_data = _form_data(
            """
            select distinct count(travel_details.destination) as "Destination Count"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Destination Count")
        assert col["aggregateList"] == [
            "db.generic.aggregate.distinct",
            "db.generic.aggregate.count",
        ]

    def test_sum_distinct_count_is_stacked(self):
        form_data = _form_data(
            """
            select sum(distinct count(travel_details.destination)) as "Destination Count"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Destination Count")
        assert col["aggregateList"] == [
            "db.generic.aggregate.sum",
            "db.generic.aggregate.distinct",
            "db.generic.aggregate.count",
        ]

    def test_select_distinct_on_dimension_gets_distinct_aggregate(self):
        form_data = _form_data(
            """
            select distinct travel_details.destination as "Destination"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Destination")
        assert col["aggregate"] is True
        assert col["aggregateList"] == ["db.generic.aggregate.distinct"]
        assert "aggregate" not in (form_data.get("functions") or {})

    def test_select_distinct_travel_medium_gets_distinct_aggregate(self):
        form_data = _form_data(
            """
            SELECT DISTINCT "travel_details"."travel_medium" AS "Travel Medium"
            FROM "travel_details"
            LIMIT 100
            """
        )
        col = _column_by_alias(form_data, "Travel Medium")
        assert col["aggregate"] is True
        assert col["aggregateList"] == ["db.generic.aggregate.distinct"]

    def test_min_and_max_are_single_aggregates(self):
        form_data = _form_data(
            """
            select
                min(travel_details.travel_cost) as "Min Travel Cost",
                max(travel_details.travel_cost) as "Max Travel Cost"
            from sampletraveldata.public.travel_details
            """
        )
        min_col = _column_by_alias(form_data, "Min Travel Cost")
        max_col = _column_by_alias(form_data, "Max Travel Cost")
        assert min_col["aggregateList"] == ["db.generic.aggregate.min"]
        assert max_col["aggregateList"] == ["db.generic.aggregate.max"]

    def test_min_distinct_is_stacked(self):
        form_data = _form_data(
            """
            select min(distinct travel_details.travel_cost) as "Min Distinct Cost"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Min Distinct Cost")
        assert col["aggregateList"] == [
            "db.generic.aggregate.min",
            "db.generic.aggregate.distinct",
        ]

    def test_max_count_is_stacked(self):
        form_data = _form_data(
            """
            select max(count(travel_details.destination)) as "Max Destination Count"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Max Destination Count")
        assert col["aggregateList"] == [
            "db.generic.aggregate.max",
            "db.generic.aggregate.count",
        ]
        assert "aggregate" not in (form_data.get("functions") or {})

    def test_min_max_is_stacked(self):
        form_data = _form_data(
            """
            select min(max(travel_details.travel_cost)) as "Min Of Max Cost"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "Min Of Max Cost")
        assert col["aggregateList"] == [
            "db.generic.aggregate.min",
            "db.generic.aggregate.max",
        ]

    def test_all_catalog_aggregates_are_stacked(self):
        form_data = _form_data(
            """
            select avg(sum(min(max(count(distinct travel_details.destination)))))
                as "All Aggregates"
            from sampletraveldata.public.travel_details
            """
        )
        col = _column_by_alias(form_data, "All Aggregates")
        assert col["aggregateList"] == [
            "db.generic.aggregate.avg",
            "db.generic.aggregate.sum",
            "db.generic.aggregate.min",
            "db.generic.aggregate.max",
            "db.generic.aggregate.count",
            "db.generic.aggregate.distinct",
        ]
        assert "aggregate" not in (form_data.get("functions") or {})


class TestFilterAggregate:
    """SUM(...) FILTER (WHERE ...) is a measure, not a GROUP BY dimension."""

    def test_sum_filter_is_not_added_to_groupby(self):
        form_data = _form_data(
            """
            SELECT
              SUM(travel_details.travel_cost)
                FILTER (WHERE travel_details.travel_type = 'International')
                AS "Cost by Cancellation",
              EXTRACT(MONTH FROM travel_details.travel_date) AS "Travel Month",
              SUM(travel_details.travel_id) AS "Travel Id"
            FROM sampletraveldata.public.travel_details
            GROUP BY EXTRACT(MONTH FROM travel_details.travel_date)
            LIMIT 100
            """
        )
        cost = _column_by_alias(form_data, "Cost by Cancellation")
        assert cost["aggregate"] is True
        assert cost["aggregateList"] == ["db.generic.aggregate.sum"]
        assert _is_raw_fn(cost["databaseFunction"])
        assert "filter" in _norm_sql(cost["databaseFunction"])
        group_aliases = [g["column"] for g in form_data.get("functions", {}).get("groupBy", [])]
        assert group_aliases == ["Travel Month"]
        assert "Cost by Cancellation" not in group_aliases

    def test_groupby_follows_sql_not_non_aggregate_selects(self):
        """Rate formulas with nested COUNT must not enter groupBy unless SQL groups by them."""
        form_data = _form_data(
            """
            SELECT
              "meeting_details"."meeting_by" AS "Employee Name",
              COUNT("meeting_details"."meeting_by") AS "Total Meetings",
              COUNT(
                CASE WHEN "meeting_details"."meet_cancellation_status" = 'No' THEN 1 END
              ) AS "Successful Meetings",
              ROUND(
                CAST(
                  COUNT(
                    CASE WHEN "meeting_details"."meet_cancellation_status" = 'Yes' THEN 1 END
                  ) * 100.0
                  / NULLIF(COUNT("meeting_details"."meeting_by"), 0) AS DECIMAL
                ),
                2
              ) AS "Cancellation Rate"
            FROM "sampletraveldata"."public"."meeting_details"
            GROUP BY "meeting_details"."meeting_by"
            LIMIT 100
            """
        )
        rate = _column_by_alias(form_data, "Cancellation Rate")
        assert "aggregate" not in rate
        group_aliases = [g["column"] for g in form_data.get("functions", {}).get("groupBy", [])]
        assert group_aliases == ["Employee Name"]
        assert "Cancellation Rate" not in group_aliases

    def test_round_rate_formula_emits_raw_not_quoted_round(self):
        """ROUND(CAST(100*COUNT…/COUNT…)) must be RAW(full SQL), never ROUND('…')."""
        form_data = _form_data(
            """
            SELECT
              "meeting_details"."meeting_by" AS "Employee Name",
              ROUND(
                CAST(
                  100.0 * COUNT(
                    CASE WHEN "meeting_details"."meet_cancellation_status" = 'Yes' THEN 1 END
                  ) / NULLIF(COUNT("meeting_details"."meeting_by"), 0) AS DECIMAL
                ),
                2
              ) AS "Cancellation Rate"
            FROM "sampletraveldata"."public"."meeting_details"
            GROUP BY "meeting_details"."meeting_by"
            LIMIT 100
            """,
            catalog=_catalog_with_round_and_case(),
        )
        rate = _column_by_alias(form_data, "Cancellation Rate")
        dbf = str(rate.get("databaseFunction") or "")
        assert dbf.startswith("RAW("), dbf
        assert "ROUND(" in dbf
        assert "COUNT(" in dbf
        assert "CASE WHEN" in dbf.upper().replace("  ", " ") or "CASE WHEN" in dbf
        assert not dbf.startswith("ROUND('")
        assert "''Yes''" not in dbf

    def test_count_case_when_emits_raw_case_not_case_parens(self):
        """COUNT(CASE WHEN …) must keep the CASE body as RAW, not CASE()."""
        form_data = _form_data(
            """
            SELECT
              COUNT(
                CASE WHEN "meeting_details"."meet_cancellation_status" = 'Yes' THEN 1 END
              ) AS "Cancelled Meetings"
            FROM "meeting_details"
            LIMIT 100
            """,
            catalog=_catalog_with_round_and_case(),
        )
        col = _column_by_alias(form_data, "Cancelled Meetings")
        assert col["aggregate"] is True
        assert col["aggregateList"] == ["db.generic.aggregate.count"]
        dbf = str(col.get("databaseFunction") or "")
        assert "CASE WHEN" in dbf.upper() or "case when" in dbf.lower()
        assert dbf != "CASE()"
        assert "CASE()" not in dbf


class TestSelectAlsoFiltered:
    """SELECT fields that are also WHERE / HAVING drop out of data_model.columns."""

    def test_where_dimension_is_removed_from_select_columns(self):
        form_data = _form_data(
            """
            SELECT
              travel_details.booking_platform AS "Booking Platform",
              EXTRACT(MONTH FROM travel_details.travel_date) AS "Travel Month",
              SUM(travel_details.travel_cost) AS "Travel Cost"
            FROM travel_details
            WHERE EXTRACT(MONTH FROM travel_details.travel_date) = 3
            GROUP BY
              travel_details.booking_platform,
              EXTRACT(MONTH FROM travel_details.travel_date)
            """
        )
        aliases = [col.get("alias") for col in form_data["columns"]]
        assert aliases == ["Booking Platform", "Travel Cost"]
        assert "Travel Month" not in aliases
        group_aliases = [g["column"] for g in form_data.get("functions", {}).get("groupBy", [])]
        assert group_aliases == ["Booking Platform"]
        assert "Travel Month" not in group_aliases
        assert any(
            item.get("alias") == "Travel Month" or "travel_date" in str(item.get("column") or "").lower()
            for item in form_data.get("filters") or []
        )

    def test_having_measure_is_removed_from_select_columns(self):
        form_data = _form_data(
            """
            SELECT
              travel_details.destination AS "destination",
              SUM(travel_details.travel_cost) AS "Travel Cost"
            FROM travel_details
            GROUP BY travel_details.destination
            HAVING SUM(travel_details.travel_cost) > 100
            """
        )
        aliases = [col.get("alias") for col in form_data["columns"]]
        assert aliases == ["destination"]
        assert "Travel Cost" not in aliases

    def test_lone_having_measure_stays_in_select(self):
        form_data = _form_data(
            """
            SELECT SUM(travel_details.travel_cost) AS "Travel Cost"
            FROM travel_details
            HAVING SUM(travel_details.travel_cost) > 100
               AND SUM(travel_details.travel_cost) < 2
            """
        )
        aliases = [col.get("alias") for col in form_data["columns"]]
        assert aliases == ["Travel Cost"]
        assert form_data["columns"][0]["aggregate"] is True

    def test_lone_where_dimension_stays_in_select(self):
        form_data = _form_data(
            """
            SELECT travel_details.destination AS "destination"
            FROM travel_details
            WHERE travel_details.destination = 'Paris'
            """
        )
        aliases = [col.get("alias") for col in form_data["columns"]]
        assert aliases == ["destination"]
        assert any(
            item.get("alias") == "destination"
            or "destination" in str(item.get("column") or "").lower()
            for item in form_data.get("filters") or []
        )

    def test_filter_inside_select_is_not_treated_as_where(self):
        form_data = _form_data(
            """
            SELECT
              SUM(travel_details.travel_cost)
                FILTER (WHERE travel_details.travel_type = 'International')
                AS "Cost by Cancellation",
              EXTRACT(MONTH FROM travel_details.travel_date) AS "Travel Month"
            FROM travel_details
            GROUP BY EXTRACT(MONTH FROM travel_details.travel_date)
            """
        )
        aliases = [col.get("alias") for col in form_data["columns"]]
        assert "Cost by Cancellation" in aliases
        assert "Travel Month" in aliases

    def test_count_where_same_column_keeps_the_measure(self):
        form_data = _form_data(
            """
            SELECT COUNT(travel_details.destination) AS "Cancelled Meetings"
            FROM travel_details
            WHERE travel_details.destination = 'Paris'
            """
        )
        aliases = [col.get("alias") for col in form_data["columns"]]
        assert aliases == ["Cancelled Meetings"]
        assert form_data["columns"][0]["aggregate"] is True

    def test_rm_cols_in_filter_false_keeps_where_dimension(self):
        sql = """
            SELECT
              travel_details.booking_platform AS "Booking Platform",
              EXTRACT(MONTH FROM travel_details.travel_date) AS "Travel Month",
              SUM(travel_details.travel_cost) AS "Travel Cost"
            FROM travel_details
            WHERE EXTRACT(MONTH FROM travel_details.travel_date) = 3
            GROUP BY
              travel_details.booking_platform,
              EXTRACT(MONTH FROM travel_details.travel_date)
        """
        form_data = _form_data(sql, rm_cols_in_filter=False)
        aliases = [col.get("alias") for col in form_data["columns"]]
        assert "Travel Month" in aliases
        group_aliases = [g["column"] for g in form_data.get("functions", {}).get("groupBy", [])]
        assert "Travel Month" in group_aliases


class TestHavingFoldedIntoFilters:
    """HAVING is built separately, then folded into filters on the wire payload."""

    _SQL = """
        SELECT
          travel_details.travel_type AS "Travel Type",
          COUNT(travel_details.travel_id) AS "Travel Count"
        FROM travel_details
        WHERE travel_details.travel_type = 'International'
        GROUP BY travel_details.travel_type
        HAVING COUNT(travel_details.travel_id) BETWEEN 20 AND 50
        LIMIT 100
    """

    def test_having_is_appended_to_filters_and_dropped(self):
        form_data = _form_data(self._SQL)
        assert "having" not in form_data
        assert "customHavingExpression" not in form_data
        assert form_data["customFilterExpression"] == " ${0} "
        assert form_data["filterExpression"] == ["Travel Type", "count_travel_id"]

        where_item = form_data["filters"][0]
        assert where_item["id"] == 0
        assert where_item["alias"] == "Travel Type"
        assert where_item["condition"] == "EQUALS"
        assert where_item["values"] == ["International"]

        having_item = form_data["filters"][1]
        assert having_item["id"] == 0
        assert having_item["condition"] == "IS_BETWEEN"
        assert having_item["customCondition"] == "BETWEEN"
        assert having_item["values"] == [20, 50]
        assert having_item["isCustomValue"] is True
        assert having_item["encloseInQuotes"] is False
        assert having_item["function"] == "db.generic.aggregate.count"
        assert         having_item["column"] == {
            "name": "sampletraveldata.public.travel_details.travel_id",
            "id": "1064",
        }
        aliases = [col.get("alias") for col in form_data.get("columns") or []]
        assert "Travel Type" not in aliases
        assert "Travel Count" not in aliases
        assert "groupBy" not in form_data.get("functions", {})

    def test_include_parts_keeps_having_before_fold(self):
        form_data = sql_to_form_data(
            self._SQL,
            location="0007",
            metadata_file_name="pg_sample_travel_data_agent.metadata",
            catalog=_catalog(),
            metadata=_metadata(),
            dialect="postgres",
            include_parts=True,
        )
        assert "having" not in form_data
        assert len(form_data["_parts"]["having"]) == 1
        assert form_data["_parts"]["having"][0]["values"] == [20, 50]
        assert len(form_data["filters"]) == 2


class TestFilterExpression:
    """filterExpression uses filter aliases; operators only when there are 2+ items."""

    def test_where_or_two_aliases(self):
        form_data = _form_data(
            """
            SELECT travel_details.travel_type AS "Travel Type"
            FROM travel_details
            WHERE travel_details.destination = 'Paris'
               OR travel_details.source = 'London'
            """
        )
        assert form_data["filterExpression"] == ["destination OR source"]
        assert form_data["customFilterExpression"] == " ${0} OR ${1} "
        assert [item["operator"] for item in form_data["filters"]] == ["OR", "OR"]

    def test_where_and_having_or(self):
        form_data = _form_data(
            """
            SELECT
              travel_details.destination AS "destination",
              SUM(travel_details.travel_cost) AS "Travel Cost",
              SUM(travel_details.travelled_by) AS "Travelled By"
            FROM travel_details
            WHERE travel_details.destination = 'Paris'
               OR travel_details.source = 'London'
            GROUP BY travel_details.destination
            HAVING SUM(travel_details.travel_cost) > 100
                OR SUM(travel_details.travelled_by) > 10
            """
        )
        assert form_data["filterExpression"] == [
            "destination OR source",
            "sum_travel_cost OR sum_travelled_by",
        ]
        assert form_data["customFilterExpression"] == " ${0} OR ${1} "

    def test_having_only_uses_empty_where_slot(self):
        form_data = _form_data(
            """
            SELECT
              travel_details.destination AS "destination",
              SUM(travel_details.travel_cost) AS "Travel Cost",
              SUM(travel_details.travelled_by) AS "Travelled By"
            FROM travel_details
            GROUP BY travel_details.destination
            HAVING SUM(travel_details.travel_cost) > 100
                OR SUM(travel_details.travelled_by) > 10
            """
        )
        assert form_data["filterExpression"] == [
            "",
            "sum_travel_cost OR sum_travelled_by",
        ]
        assert "customFilterExpression" not in form_data

    def test_single_where_omits_operator(self):
        form_data = _form_data(
            """
            SELECT travel_details.destination AS "destination"
            FROM travel_details
            WHERE travel_details.destination = 'Paris'
            """
        )
        assert form_data["filterExpression"] == ["destination"]
        assert form_data["customFilterExpression"] == " ${0} "

    def test_single_having_omits_operator(self):
        form_data = _form_data(
            """
            SELECT
              travel_details.destination AS "destination",
              SUM(travel_details.travel_cost) AS "Travel Cost"
            FROM travel_details
            GROUP BY travel_details.destination
            HAVING SUM(travel_details.travel_cost) > 100
            """
        )
        assert form_data["filterExpression"] == ["", "sum_travel_cost"]


class TestCaseWhenRawUnquoted:
    """CASE WHEN → RAW must omit identifier escapes (Helical blanks quoted CASE)."""

    def test_count_case_when_raw_has_no_identifier_quotes(self):
        form_data = _form_data(
            """
            SELECT COUNT(
              CASE WHEN "meeting_details"."meet_cancellation_status" = 'Yes' THEN 1 END
            ) AS "Cancelled Meetings"
            FROM "meeting_details"
            LIMIT 100
            """
        )
        col = _column_by_alias(form_data, "Cancelled Meetings")
        assert col["aggregate"] is True
        assert col["aggregateList"] == ["db.generic.aggregate.count"]
        assert _is_raw_fn(col["databaseFunction"])
        dbf = str(col["databaseFunction"])
        assert '"' not in dbf
        assert "`" not in dbf
        assert "CASE WHEN meeting_details.meet_cancellation_status = 'Yes' THEN 1 END" in dbf
        assert col["usedColumns"] == [
            "sampletraveldata.public.meeting_details.meet_cancellation_status"
        ]

    def test_backslash_escaped_quotes_in_sql_still_convert(self):
        # JSON-style \\" before identifiers must be ignored before parse.
        sql = (
            'SELECT COUNT(CASE WHEN "meeting_details".\\"meet_cancellation_status\\" = \'Yes\' '
            'THEN 1 END) AS \\"Cancelled Meetings\\" FROM \\"meeting_details\\" LIMIT 100'
        )
        form_data = _form_data(sql)
        col = _column_by_alias(form_data, "Cancelled Meetings")
        dbf = str(col["databaseFunction"])
        assert _is_raw_fn(dbf)
        assert '"' not in dbf
        assert "CASE WHEN meeting_details.meet_cancellation_status = 'Yes' THEN 1 END" in dbf


