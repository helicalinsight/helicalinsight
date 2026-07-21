"""Chart template registry.

Chart definitions live in ``helicalbi/viz/charts/*.json``.
Add a chart by creating ``charts/<viz_type>.json`` — no Python module needed.
"""
from __future__ import annotations

from helicalbi.viz._chart_selection import (  # noqa: F401
    CHART_SELECTION_RULES,
    ChartOption,
    format_chart_selection_guide,
    get_chart_options,
    infer_chart_shape,
    possible_chart_options,
)
from helicalbi.viz._charts import get_chart_config
from helicalbi.viz._template_instructions import (  # noqa: F401
    BASE_RULES,
    OTHER_BASE_RULES,
)


def __getattr__(name: str):
    if name == "chart_config":
        return get_chart_config()
    if name == "CHART_SELECTION_TABLE":
        from helicalbi.viz._chart_selection import _build_chart_selection_table

        return _build_chart_selection_table(get_chart_options())
    raise AttributeError(f"module {__name__!r} has no attribute {name!r}")
