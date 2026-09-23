"""GROUP BY → functions.groupBy[] entries (wire-functions.json)."""

from __future__ import annotations

from ..models import ParsedQuery
from .select_parts import _find_matching_column


def build_groupby(parsed: ParsedQuery, columns: list[dict] | None = None) -> list[dict]:
    """
    Build ``functions.groupBy`` from the SQL ``GROUP BY`` clause (source of truth).

    Each GROUP BY expression is matched to a formData column alias (including
    hidden columns materialized from GROUP BY / ORDER BY). SELECT-only measures
    that happen to lack ``aggregate: true`` are not added just because they are
    non-aggregate on the wire.

    Wire shape: ``{ "column": "<alias>", "custom": true }``
    """
    result: list[dict] = []
    seen: set[str] = set()
    columns = columns or []

    if not parsed.group_by_items and not parsed.group_by:
        return result

    for item in parsed.group_by_items:
        match = _find_matching_column(item, columns, item.alias)
        if match is None:
            continue
        if match.get("aggregate"):
            continue
        alias = str(match.get("alias") or "").strip()
        if alias and alias not in seen:
            result.append({"column": alias, "custom": True})
            seen.add(alias)

    # Fallback when group_by_items could not be parsed but column refs exist.
    if not result and parsed.group_by:
        for g in parsed.group_by:
            hint = (g.name or "").strip()
            if not hint:
                continue
            match = None
            hint_l = hint.lower()
            for col in columns:
                if str(col.get("alias") or "").lower() == hint_l:
                    match = col
                    break
                col_ref = col.get("column")
                if isinstance(col_ref, dict):
                    name = str(col_ref.get("name") or "")
                    if name.lower().endswith("." + hint_l) or name.lower() == hint_l:
                        match = col
                        break
            if match is None or match.get("aggregate"):
                continue
            alias = str(match.get("alias") or "").strip()
            if alias and alias not in seen:
                result.append({"column": alias, "custom": True})
                seen.add(alias)

    return result
