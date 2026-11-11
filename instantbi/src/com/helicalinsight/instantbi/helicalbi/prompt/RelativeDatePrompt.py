"""Prompt that reads final SQL and returns relative-date tokens."""

from __future__ import annotations

_DEFAULT_NAMES = (
    "today",
    "tomorrow",
    "yesterday",
    "month",
    "year",
    "quarter",
    "hour",
    "minute",
    "second",
    "to_date",
)

relative_date_prompt = """
You read one SQL query and list every WHERE predicate that an hreport relative
date filter can store. A named calendar value such as the year 2015 is not relative.

Tokens, and only these, matching the hreport relative list:
{catalog_names}
Day is the token today, never day. Prefix anchor_ when the period is relative
to a fixed anchor date instead of today (anchor_year, anchor_today-1).
today, month, year, quarter, hour, minute, and second may take +N or -N.
tomorrow, yesterday, and to_date never take +N or -N.

Parts and the preset the hreport radio list stores:
- this period: the bare token (year, quarter, month, today, hour, minute, second)
- next period: token+1. A single next day is tomorrow.
- previous period: token-1. A single previous day is yesterday.
- next N (N > 1): one value is token+N. A range is [token+1, token+N].
- last N (N > 1): one value is token-N. A range is [token-N, token-1].
- to date, range only: [token, to_date]. This year to date is ["year", "to_date"].
  The same shape is quarter, month, today, hour, minute, and second.
A closed period uses the same token twice: this year is ["year", "year"],
previous year is ["year-1", "year-1"].

column is the date column, table-qualified when the SQL qualifies it, without
EXTRACT, DATE_TRUNC, or any other wrapper.
condition is the hreport condition:
- IS_BETWEEN, IS_NOT_BETWEEN, IN_RANGE, NOT_IN_RANGE for a range (two tokens)
- EQUALS, NOT_EQUALS, IS_LESS_THAN, IS_GREATER_THAN, IS_LESS_THAN_OR_EQUAL_TO,
  IS_GREATER_THAN_OR_EQUAL_TO for one token
- IS_ONE_OF, IS_NOT_ONE_OF for a list of tokens
values are tokens in SQL order.

Example:
EXTRACT(YEAR FROM "travel_details"."travel_date") IN (
  EXTRACT(YEAR FROM CURRENT_DATE),
  EXTRACT(YEAR FROM CURRENT_DATE) + 1
)
column: travel_details.travel_date
condition: IS_ONE_OF
values: ["year", "year+1"]

Return an empty filters list when the SQL has no relative date.

SQL:
{sql}
"""


def catalog_token_names(catalog: dict | None) -> list[str]:
    """Names the prompt may emit. The request catalog wins; otherwise the shipped names."""
    names: list[str] = []
    for item in (catalog or {}).get("functions") or []:
        if not isinstance(item, dict):
            continue
        name = str(item.get("name") or "").strip()
        if name and name not in names:
            names.append(name)
    return names or list(_DEFAULT_NAMES)


def render_relative_date_prompt(catalog: dict | None) -> str:
    names = ", ".join(catalog_token_names(catalog))
    return relative_date_prompt.replace("{catalog_names}", names)
