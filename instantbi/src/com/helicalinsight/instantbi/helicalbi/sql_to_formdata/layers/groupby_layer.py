"""GROUP BY → functions.groupBy[] entries (wire-functions.json)."""

from __future__ import annotations

from ..models import ParsedQuery


def build_groupby(parsed: ParsedQuery, columns: list[dict] | None = None) -> list[dict]:
    """
    Prefer SELECT aliases that are dimensions; fall back to parsed.group_by columns.
    Wire shape: { "column": "<alias>", "custom": true }

    When GROUP BY is present, all non-aggregate SELECT columns are included
    (covers DB-function dimensions whose GROUP BY expr won't match the alias).
    """
    result: list[dict] = []
    seen: set[str] = set()

    if not parsed.group_by:
        return result

    for col in columns or []:
        if col.get("aggregate"):
            continue
        if col.get("hidden") and not col.get("includeInResultset"):
            continue
        alias = col.get("alias") or ""
        if alias and alias not in seen:
            result.append({"column": alias, "custom": True})
            seen.add(alias)

    if not result:
        for g in parsed.group_by:
            alias = g.name
            if alias not in seen:
                result.append({"column": alias, "custom": True})
                seen.add(alias)

    return result
