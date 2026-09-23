"""GROUP BY → formData.functions.groupBy (extractColumnFunctions)."""

from __future__ import annotations

from ..models import ParsedQuery
from .groupby_parts import build_groupby


def build_functions(
    parsed: ParsedQuery,
    columns: list[dict] | None = None,
) -> dict:
    """Wire ``functions`` with ``groupBy`` only.

    Measure aggregation lives on each column (``aggregate`` / ``aggregateList``).
    InstantBI and Instant-to-HR read those column fields; they do not use
    ``functions.aggregate``.
    """
    functions: dict = {}
    group_by = build_groupby(parsed, columns or [])
    if group_by:
        functions["groupBy"] = group_by
    return functions
