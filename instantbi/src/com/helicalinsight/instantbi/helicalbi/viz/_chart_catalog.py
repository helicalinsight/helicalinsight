"""Static InstantBI chart catalog.

Standard charts no longer ship ``viz/charts/*.json`` templates — only ``other.json``
remains for DrawOther VF generation. Selection and aliases come from this mapping.
"""
from __future__ import annotations

# Shape + alias rows: (name, dims_min, dims_max, measures_min, measures_max,
# instruction, requires_ordered, aliases)
# Bounds mirror the former charts/*.json files.
# Bounds match HI Chart canvas rules (row/column shelves).
# Convert swaps to dim-in-columns + measure-in-rows when the table requires it.
_CHART_OPTION_ROWS: tuple[tuple, ...] = (
    ("bar", 1, 1, 1, None, "1 dimension (column or row) and 1+ measures on the other shelf.", False, ("bar chart", "horizontal bar", "horizontal bar chart")),
    ("column", 1, 1, 1, None, "Vertical comparison: 1 dimension and 1+ measures.", False, ("column chart", "vertical bar", "vertical bar chart")),
    ("line", 1, 1, 1, None, "Trend: 1 dimension and 1+ measures (date preferred).", False, ("line chart", "trend line", "line plot")),
    ("area", 1, 1, 1, None, "Trend with fill: 1 dimension and 1+ measures.", False, ("area chart", "filled area", "area plot")),
    ("pie", 0, 1, 1, 1, "Parts of a whole: 1 measure, optional 1 dimension in columns.", False, ("pie chart", "arc", "arc chart")),
    ("donut", 0, 1, 1, 1, "Parts of a whole (donut): 1 measure, optional 1 dimension in columns.", False, ("donut chart", "doughnut", "doughnut chart")),
    ("point", 1, 1, 1, 1, "Scatter: 1 dimension and 1 measure.", False, ("scatter", "scatter plot", "point chart")),
    ("radar", 1, 1, 1, 1, "Radial comparison: 1 dimension and 1 measure.", False, ("radar chart", "spider", "spider chart")),
    ("heatmap", 2, 2, 1, 1, "Two dimensions vs one measure matrix.", False, ("heat map", "heat-map", "matrix heatmap")),
    ("relation", 1, None, 1, None, "Relationship / Sankey: 1+ dimensions and 1+ measures.", False, ("relation chart", "sankey", "graph")),
    ("kpi", 0, 0, 1, 1, "Card KPI: 1 measure and 0 dimensions.", False, ("KPI", "statistic", "big number", "card")),
    ("gauge", 0, 1, 1, 2, "Progress against a target.", False, ("gauge chart", "dial", "speedometer")),
    ("progress", 0, 1, 1, 2, "Percent complete: 1–2 measures, optional 1 dimension.", False, ("progress chart", "progress bar", "percent complete")),
    ("waterfall", 1, 1, 1, 1, "Cumulative change: 1 dimension in columns and 1 measure in rows.", False, ("waterfall chart",)),
    ("calendar", 1, 1, 0, 1, "Calendar: 1 date dimension in columns.", True, ("calendar heatmap", "calendar chart", "date heatmap")),
    ("wordcloud", 1, 1, 0, 0, "Word cloud: 1 dimension and 0 measures.", False, ("word cloud", "tag cloud", "text")),
    ("table", 0, None, 0, None, "Tabular fallback for any shape.", False, ("data table", "grid")),
    ("grid_table", 0, None, 0, None, "Crosstab / grid table.", False, ("crosstab", "pivot table")),
)


def _normalize(name: str) -> str:
    return (name or "").strip().lower().replace(" ", "_").replace("-", "_")


def build_chart_aliases() -> dict[str, str]:
    """Alias / display-name → canonical chart key."""
    aliases: dict[str, str] = {
        "other": "other",
        "custom": "other",
        "fallback": "other",
        "custom_chart": "other",
        "scatter": "point",
        "scatter_plot": "point",
    }
    for row in _CHART_OPTION_ROWS:
        name = row[0]
        aliases[_normalize(name)] = name
        for alias in row[7]:
            aliases.setdefault(_normalize(alias), name)
    return aliases


CHART_ALIASES: dict[str, str] = build_chart_aliases()
