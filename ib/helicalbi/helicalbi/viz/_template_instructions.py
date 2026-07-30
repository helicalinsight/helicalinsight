"""
Shared output constraints only. Chart-specific config/polish lives in each viz module.

Default charts: the LLM fills ChartSettings and measure_formats separately;
both are injected at ``${setting}`` and ``${format}``.
``other`` charts: settings injection is not enough — the LLM returns a full DrawOther JS function.
"""

BASE_RULES = """
@ant-design/charts@1.4.2 field roles. Output ChartSettings only: no JS/JSX/markdown.
Pick dimension / measure column names from executeQuery metadata (dims=text/cat/date, measures=numeric).
Fill dimensions.name (or dimensions.names), measures[], labelsX/labelsY/title, and color as the template asks.
Do not include measure_formats here — formats are filled and injected separately via `${format}`.
"""

OTHER_BASE_RULES = """
@ant-design/charts@1.4.2 custom / fallback viz.
Return a complete DrawOther() JavaScript/JSX function only — no markdown fences, no ChartSettings JSON.
Use executeQuery metadata for real column names. Keep `data` as-is (never inline sample rows).
Destructure chart components from `components`. Replace ChartComponent with the plot that matches the user question.
Do not leave `${setting}` or `setting.*` references — bind concrete field names.
"""
