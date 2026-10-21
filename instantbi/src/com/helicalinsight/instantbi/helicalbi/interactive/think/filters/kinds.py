"""Classify dashboard items as filters vs reports."""
from __future__ import annotations

from typing import Any

from helicalbi.interactive.think.constants import (
    FILTER_KIND_ALIASES,
    NON_REPORT_KINDS,
    REPORT_KIND_ALIASES,
)
from helicalbi.model.output.dashboard.Kind import Kind


class DashboardItemClassifier:
    """Single responsibility: identify filter vs report dashboard items."""

    def is_filter(self, item: dict[str, Any]) -> bool:
        if not isinstance(item, dict):
            return False
        kind = str(item.get("kind") or item.get("compType") or "").strip().lower()
        return kind in FILTER_KIND_ALIASES or Kind.wire(kind) == Kind.INSTANT_FILTER

    def is_report(self, item: dict[str, Any]) -> bool:
        if not isinstance(item, dict) or self.is_filter(item):
            return False
        kind = str(item.get("kind") or item.get("compType") or "").strip().lower()
        if kind in NON_REPORT_KINDS:
            return False
        return (
            item.get("report_model") is not None
            or item.get("data_model") is not None
            or item.get("viz_model") is not None
            or kind in REPORT_KIND_ALIASES
        )


_CLASSIFIER = DashboardItemClassifier()


def is_filter_item(item: dict[str, Any]) -> bool:
    return _CLASSIFIER.is_filter(item)


def is_report_item(item: dict[str, Any]) -> bool:
    return _CLASSIFIER.is_report(item)
