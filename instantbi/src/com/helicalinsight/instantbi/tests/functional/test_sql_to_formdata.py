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
                        "destination": {
                            "id": "2870",
                            "alias": "destination",
                            "type": {"java.lang.String": "text"},
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
        assert _column_by_alias(form_data, "Employee Name")["order"] == "asc"
        assert "orderBy" not in form_data.get("functions", {})
        assert "hidden" not in _column_by_alias(form_data, "Employee Name")

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
            condition="NOT_EQUALS",
            customCondition="<>",
            values=["Ahmed Haider"],
            isCustomValue=True,
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
            condition="IS_NULL",
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
            condition="NOT_EQUALS",
            customCondition="<>",
            values=[100],
            isCustomValue=True,
        )

    def test_is_greater_than(self):
        assert self._filter("travel_details.travel_cost > 100") == _base_numeric(
            condition="IS_GREATER_THAN",
            customCondition=">",
            values=[100],
            isCustomValue=True,
        )

    def test_is_greater_than_or_equal_to(self):
        assert self._filter("travel_details.travel_cost >= 100") == _base_numeric(
            condition="IS_GREATER_THAN_OR_EQUAL_TO",
            customCondition=">=",
            values=[100],
            isCustomValue=True,
        )

    def test_is_less_than(self):
        assert self._filter("travel_details.travel_cost < 100") == _base_numeric(
            condition="IS_LESS_THAN",
            customCondition="<",
            values=[100],
            isCustomValue=True,
        )

    def test_is_less_than_or_equal_to(self):
        assert self._filter("travel_details.travel_cost <= 100") == _base_numeric(
            condition="IS_LESS_THAN_OR_EQUAL_TO",
            customCondition="<=",
            values=[100],
            isCustomValue=True,
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
        )

    def test_not_in_between(self):
        assert self._filter(
            "travel_details.travel_cost NOT BETWEEN 100 AND 200"
        ) == _base_numeric(
            condition="IS_NOT_BETWEEN",
            customCondition="NOT BETWEEN",
            values=[100, 200],
            isCustomValue=True,
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
            condition="IS_NULL",
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
        assert form_data["functions"]["aggregate"] == [
            {
                "column": col["column"],
                "function": "db.generic.aggregate.sum",
                "alias": "Travel Cost",
            }
        ]


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
