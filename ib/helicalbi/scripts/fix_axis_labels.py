"""Replace axis title placeholders with label_x / label_y in chart skeletons."""
from pathlib import Path

CHARTS = Path(__file__).resolve().parents[1] / "helicalbi" / "viz" / "charts"

REPLACEMENTS = {
    "text: 'dimension_column'": "text: 'label_x'",
    'text: "dimension_column"': 'text: "label_x"',
    "text: 'measure_column'": "text: 'label_y'",
    'text: "measure_column"': 'text: "label_y"',
}


def main() -> None:
    for path in sorted(CHARTS.glob("*.json")):
        text = path.read_text(encoding="utf-8")
        updated = text
        for old, new in REPLACEMENTS.items():
            updated = updated.replace(old, new)
        if updated != text:
            path.write_text(updated, encoding="utf-8")
            print(f"updated {path.name}")


if __name__ == "__main__":
    main()
