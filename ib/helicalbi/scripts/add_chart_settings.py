"""Rewrite chart JSON ``settings`` blocks into the LLM template shape."""
from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT))

from helicalbi.viz._chart_settings import synthesize_settings_template  # noqa: E402

CHARTS = ROOT / "helicalbi" / "viz" / "charts"

# Charts already authored in the new settings + ${setting} style.
SKIP = {"area", "bar", "line", "pie", "column"}


def main() -> None:
    for path in sorted(CHARTS.glob("*.json")):
        if path.stem in SKIP:
            print(f"skip {path.name}")
            continue
        payload = json.loads(path.read_text(encoding="utf-8"))
        settings = synthesize_settings_template(payload)
        new_payload = {}
        inserted = False
        for key, value in payload.items():
            if key == "settings":
                continue
            new_payload[key] = value
            if key == "conversion":
                new_payload["settings"] = settings
                inserted = True
        if not inserted:
            new_payload["settings"] = settings
        path.write_text(
            json.dumps(new_payload, indent=2, ensure_ascii=False) + "\n",
            encoding="utf-8",
        )
        print(f"updated {path.name}")


if __name__ == "__main__":
    main()
