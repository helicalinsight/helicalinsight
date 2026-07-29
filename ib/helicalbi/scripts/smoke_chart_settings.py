"""Smoke-test: every chart has ${setting} and apply_chart_settings succeeds."""
from __future__ import annotations

import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT))

# Clear chart cache if module already imported
import helicalbi.viz._charts as charts_mod

charts_mod._CACHE = None
charts_mod._ALIAS_INDEX = None

from helicalbi.model.output.viz.ChartSettings import ChartSettings, DimensionSetting
from helicalbi.viz._charts import get_charts
from helicalbi.viz.chart_conversion import apply_chart_settings, extract_fields


def sample_settings(name: str, payload_settings: dict) -> ChartSettings:
    dims = payload_settings.get("dimensions") or {}
    if isinstance(dims, dict) and "names" in dims:
        dimension = DimensionSetting(name="Region", names=["Region", "Product"])
    else:
        dimension = DimensionSetting(name="Category")

    measures = ["Sales", "Profit", "Quantity"]
    family_hint = name
    if name in {"kpi", "gauge", "progress", "histogram"}:
        measures = ["Sales", "Target"]
    if name == "bubble_chart":
        measures = ["Sales", "Profit", "Quantity"]
    if name in {"dual_line", "column_line", "grouped_column_line", "stacked_column_line", "stacked_and_grouped_column_line"}:
        measures = ["Sales", "Profit"]

    return ChartSettings(
        dimensions=dimension,
        measures=measures,
        labelsX="X Label",
        labelsY="Y Label",
        labelsZ="Z Label",
        title=f"{name} title",
        series="Series" if "series" in payload_settings else None,
        color="#5B8FF9",
        measure_formats={"Sales": "0.00"},
    )


def main() -> None:
    charts = get_charts()
    missing = []
    failed = []
    for name, chart in sorted(charts.items()):
        if "${setting}" not in chart.code:
            missing.append(name)
            continue
        try:
            settings = sample_settings(name, chart.settings or {})
            code = apply_chart_settings(settings, chart_def=chart)
            assert "const setting =" in code
            assert "${setting}" not in code
            # Round-trip extract for setting-based charts
            fields = extract_fields(code)
            assert fields.measures, f"{name}: no measures extracted"
        except Exception as exc:  # noqa: BLE001
            failed.append((name, str(exc)))

    print(f"charts={len(charts)} missing_placeholder={missing} failed={len(failed)}")
    for name, err in failed:
        print(f"FAIL {name}: {err}")
    if missing or failed:
        sys.exit(1)
    print("OK")


if __name__ == "__main__":
    main()
