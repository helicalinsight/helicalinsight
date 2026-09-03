"""Row/column shelf layout for Helical Reports Chart types.

Rules follow the InstantBI / HI Chart table: min shape for suggestion, and
the preferred orientation (dimension in columns, measure in rows) applied
when the user converts to a chart type.
"""
from __future__ import annotations

from dataclasses import dataclass
from typing import Optional

@dataclass(frozen=True)
class ShelfRule:
    """Preferred HI canvas layout for a catalog chart type.

    ``vice_versa`` means the inverted shelves are also valid (suggestion can
    keep InstantBI's default: dims on rows, measures on columns). Convert
    still moves to the preferred orientation.
    """

    dim_shelf: str  # column | row | none | either
    meas_shelf: str  # row | column | none | either
    vice_versa: bool = False
    allow_measure_only: bool = False
    allow_dim_only: bool = False
    date_in_columns: bool = False
    dims_max: Optional[int] = 1
    measures_max: Optional[int] = None


# Keys are canonical catalog names from ``_chart_catalog``.
_SHELF_RULES: dict[str, ShelfRule] = {
    "bar": ShelfRule("column", "row", vice_versa=True, measures_max=None),
    "column": ShelfRule("column", "row", vice_versa=True, measures_max=None),
    "line": ShelfRule("column", "row", vice_versa=True, measures_max=None),
    "area": ShelfRule("column", "row", vice_versa=True, measures_max=None),
    "point": ShelfRule("column", "row", vice_versa=True, measures_max=1),
    "pie": ShelfRule(
        "column", "row", vice_versa=False, allow_measure_only=True, measures_max=1
    ),
    "donut": ShelfRule(
        "column", "row", vice_versa=False, allow_measure_only=True, measures_max=1
    ),
    "waterfall": ShelfRule("column", "row", vice_versa=False, measures_max=1),
    "radar": ShelfRule("column", "row", vice_versa=True, measures_max=1),
    "progress": ShelfRule(
        "column",
        "row",
        vice_versa=True,
        allow_measure_only=True,
        measures_max=2,
    ),
    "gauge": ShelfRule(
        "column",
        "row",
        vice_versa=True,
        allow_measure_only=True,
        measures_max=2,
    ),
    "relation": ShelfRule("column", "row", vice_versa=True, dims_max=None, measures_max=None),
    "wordcloud": ShelfRule("either", "none", allow_dim_only=True, measures_max=0),
    "calendar": ShelfRule(
        "column", "either", date_in_columns=True, allow_dim_only=True, measures_max=1
    ),
    "kpi": ShelfRule("none", "either", allow_measure_only=True, dims_max=0, measures_max=1),
    "heatmap": ShelfRule("either", "either", dims_max=2, measures_max=1),
    "table": ShelfRule("either", "either", dims_max=None, measures_max=None),
    "grid_table": ShelfRule("either", "either", dims_max=None, measures_max=None),
}

_ALIASES = {
    "arc": "pie",
    "doughnut": "donut",
    "text": "wordcloud",
    "card": "kpi",
}


def _normalize_chart(chart_type: str) -> str:
    key = (chart_type or "").strip().lower().replace(" ", "_").replace("-", "_")
    return _ALIASES.get(key, key)


def shelf_rule_for(chart_type: str) -> Optional[ShelfRule]:
    return _SHELF_RULES.get(_normalize_chart(chart_type))


def _key(name: str) -> str:
    return str(name or "").strip().lower()


def _trim(names: list[str], maximum: Optional[int]) -> list[str]:
    if maximum is None:
        return list(names)
    return list(names[: max(0, maximum)])


def _split_by_role(
    names: list[str],
    *,
    dimensions: set[str],
    measures: set[str],
) -> tuple[list[str], list[str]]:
    dims: list[str] = []
    meas: list[str] = []
    for name in names:
        key = _key(name)
        if key in measures:
            meas.append(name)
        elif key in dimensions:
            dims.append(name)
        else:
            dims.append(name)
    return dims, meas


def layout_is_valid(
    rule: ShelfRule,
    *,
    dims_in_rows: list[str],
    meas_in_rows: list[str],
    dims_in_cols: list[str],
    meas_in_cols: list[str],
) -> bool:
    total_dims = len(dims_in_rows) + len(dims_in_cols)
    total_meas = len(meas_in_rows) + len(meas_in_cols)
    if rule.dims_max is not None and total_dims > rule.dims_max:
        return False
    if rule.measures_max is not None and total_meas > rule.measures_max:
        return False
    if rule.allow_dim_only and total_meas == 0 and total_dims >= 1:
        if rule.date_in_columns:
            return bool(dims_in_cols)
        return True
    if rule.allow_measure_only and total_dims == 0 and total_meas >= 1:
        return True
    if rule.dim_shelf == "none" and total_dims > 0:
        return False

    primary = bool(dims_in_cols) and bool(meas_in_rows)
    flipped = bool(dims_in_rows) and bool(meas_in_cols)
    if rule.dim_shelf == "column" and rule.meas_shelf == "row":
        if primary:
            return True
        if rule.vice_versa and flipped:
            return True
        return False
    if rule.dim_shelf == "row" and rule.meas_shelf == "column":
        if flipped:
            return True
        if rule.vice_versa and primary:
            return True
        return False
    return True


def preferred_shelves(
    rule: ShelfRule,
    *,
    dimensions: list[str],
    measures: list[str],
) -> tuple[list[str], list[str]]:
    """Return (rows, columns) in the table's primary orientation."""
    dims = _trim(dimensions, rule.dims_max)
    meas = _trim(measures, rule.measures_max)
    if rule.allow_dim_only and not meas:
        if rule.date_in_columns or rule.dim_shelf == "column":
            return [], dims
        return dims, []
    if rule.allow_measure_only and not dims:
        if rule.meas_shelf == "column":
            return [], meas
        return meas, []
    if rule.dim_shelf == "column" and rule.meas_shelf == "row":
        return meas, dims
    if rule.dim_shelf == "row" and rule.meas_shelf == "column":
        return dims, meas
    return dims, meas


def arrange_shelves(
    chart_type: str,
    rows: list[str],
    columns: list[str],
    *,
    dimensions: list[str],
    measures: list[str],
    force_preferred: bool = False,
) -> tuple[list[str], list[str], bool]:
    """Place fields on HI row/column shelves for ``chart_type``.

    When ``force_preferred`` is true (user asked to convert), always use the
    table's primary orientation — typically swap InstantBI's default
    (dims on rows, measures on columns) to dim-in-columns, measure-in-rows.
    Otherwise keep the current layout if it is already valid.
    """
    rule = shelf_rule_for(chart_type)
    if rule is None:
        return list(rows), list(columns), False

    dim_keys = {_key(n) for n in dimensions}
    meas_keys = {_key(n) for n in measures}
    dims_in_rows, meas_in_rows = _split_by_role(
        rows, dimensions=dim_keys, measures=meas_keys
    )
    dims_in_cols, meas_in_cols = _split_by_role(
        columns, dimensions=dim_keys, measures=meas_keys
    )

    valid = layout_is_valid(
        rule,
        dims_in_rows=dims_in_rows,
        meas_in_rows=meas_in_rows,
        dims_in_cols=dims_in_cols,
        meas_in_cols=meas_in_cols,
    )
    if valid and not force_preferred:
        return list(rows), list(columns), False

    next_rows, next_cols = preferred_shelves(
        rule, dimensions=dimensions, measures=measures
    )
    swapped = next_rows != list(rows) or next_cols != list(columns)
    return next_rows, next_cols, swapped
