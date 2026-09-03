"""Load convert-dashboard ASCII layout templates (*.layout.txt)."""
from __future__ import annotations

from pathlib import Path

LAYOUTS_DIR = Path(__file__).resolve().parent / "layouts"
DECISION_TABLE_NAME = "decision-table.layout.txt"

KNOWN_TEMPLATE_IDS = (
    "executive-kpi-first",
    "analytical-grid",
    "storytelling-narrative",
    "dashboard-sidebar",
    "mosaic-freeform",
    "comparison",
    "drilldown-hierarchical",
    "operational-realtime",
)


def _read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8").strip()


def load_decision_table() -> str:
    path = LAYOUTS_DIR / DECISION_TABLE_NAME
    if not path.is_file():
        return ""
    return _read_text(path)


def _template_id(path: Path) -> str:
    name = path.name
    suffix = ".layout.txt"
    if name.endswith(suffix):
        return name[: -len(suffix)]
    return path.stem


def load_layout_catalog() -> str:
    blocks: list[str] = []
    for path in sorted(LAYOUTS_DIR.glob("*.layout.txt"), key=_template_id):
        if path.name == DECISION_TABLE_NAME:
            continue
        blocks.append(_read_text(path))
    return "\n\n".join(blocks)


def list_template_ids() -> list[str]:
    return [
        _template_id(path)
        for path in sorted(LAYOUTS_DIR.glob("*.layout.txt"), key=_template_id)
        if path.name != DECISION_TABLE_NAME
    ]
