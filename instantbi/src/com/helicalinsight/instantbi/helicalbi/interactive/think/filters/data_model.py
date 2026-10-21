"""Shared helpers for reading nested report data_model dicts."""
from __future__ import annotations

from typing import Any


def extract_data_model(rep: dict[str, Any]) -> dict[str, Any]:
    if isinstance(rep.get("report_model"), dict):
        dm = rep["report_model"].get("data_model")
        if isinstance(dm, dict):
            return dm
    if isinstance(rep.get("data_model"), dict):
        return rep["data_model"]
    return {}
