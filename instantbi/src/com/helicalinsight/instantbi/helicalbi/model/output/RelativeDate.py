"""Structured relative-date predicates found in a generated SQL query."""

from pydantic import BaseModel, Field


class RelativeDateFilter(BaseModel):
    """One WHERE predicate whose bounds are dates relative to today."""

    column: str = Field(
        description=(
            "Date column the predicate filters. Use the table-qualified name "
            "from the SQL when it is qualified. Do not include EXTRACT, "
            "DATE_TRUNC, or any other wrapper."
        )
    )
    condition: str = Field(
        description=(
            "Hreport date condition. Range (two tokens): IS_BETWEEN, "
            "IS_NOT_BETWEEN, IN_RANGE, NOT_IN_RANGE. One token: EQUALS, "
            "NOT_EQUALS, IS_LESS_THAN, IS_GREATER_THAN, "
            "IS_LESS_THAN_OR_EQUAL_TO, IS_GREATER_THAN_OR_EQUAL_TO. "
            "A list of tokens: IS_ONE_OF, IS_NOT_ONE_OF."
        )
    )
    values: list[str] = Field(
        description=(
            "Hreport relative tokens in SQL order. Names: today, tomorrow, "
            "yesterday, month, year, quarter, hour, minute, second, to_date. "
            "Optional anchor_ prefix and +N or -N, except tomorrow, yesterday, "
            "and to_date. Day is today. Examples: year, year+1, year-3, "
            "today-1, tomorrow, anchor_month, to_date."
        )
    )


class RelativeDates(BaseModel):
    """Relative date filters in the final SQL. Empty when there are none."""

    filters: list[RelativeDateFilter] = Field(
        default_factory=list,
        description=(
            "Relative date predicates. Empty when the query does not filter "
            "by a date relative to today. A named year such as 2015 is not relative."
        ),
    )
