"""Score and match filters to report items."""
from __future__ import annotations

from typing import Any, Optional

from helicalbi.core.dashboardflow.collect_items import column_leaf, column_table
from helicalbi.interactive.think.constants import (
    ITEM_ID_KEYS,
    SCORE_COLUMN_MATCH,
    SCORE_LABEL_MATCH,
    SCORE_TABLE_BONUS,
    SCORE_TITLE_MATCH,
)
from helicalbi.interactive.think.filters.data_model import extract_data_model


class ItemIdMatcher:
    """Match dashboard items by any known id key."""

    def matches(self, rep: dict[str, Any], target_id: str) -> bool:
        if not target_id:
            return False
        target = str(target_id).strip()
        for key in ITEM_ID_KEYS:
            val = str(rep.get(key) or "").strip()
            if val and val == target:
                return True
        return False


class ReportItemMatcher:
    """Find the best report item for a filter via source id or column scoring."""

    def __init__(self, *, id_matcher: Optional[ItemIdMatcher] = None) -> None:
        self._ids = id_matcher or ItemIdMatcher()

    def find(
        self,
        filt: dict[str, Any],
        report_items: list[dict[str, Any]],
    ) -> Optional[dict[str, Any]]:
        if not report_items:
            return None

        source_id = str(filt.get("sourceItemId") or "").strip()
        if source_id:
            for rep in report_items:
                if self._ids.matches(rep, source_id):
                    return rep

        col = column_leaf(
            filt.get("column") or filt.get("name") or filt.get("alias") or ""
        )
        table = column_table(
            filt.get("column") or filt.get("name") or filt.get("alias") or "",
            filt.get("table") or "",
        )
        title = str(filt.get("title") or "").strip().lower()
        label = str(filt.get("label") or filt.get("alias") or "").strip().lower()

        best_match: Optional[dict[str, Any]] = None
        best_score = -1

        for rep in report_items:
            score = self._score_report(rep, col, table, title, label)
            if score > best_score:
                best_score = score
                best_match = rep

        if best_match and best_score > 0:
            return best_match
        return report_items[0]

    def _score_report(
        self,
        rep: dict[str, Any],
        col: str,
        table: str,
        title: str,
        label: str,
    ) -> int:
        dm = extract_data_model(rep)
        score = 0
        for c in dm.get("columns") or []:
            if isinstance(c, dict):
                score += self._score_field(c, col, table, title, label, use_column_key=True)
        for f in dm.get("filters") or []:
            if isinstance(f, dict):
                score += self._score_field(f, col, table, title, label, use_column_key=False)
        return score

    def _score_field(
        self,
        field: dict[str, Any],
        col: str,
        table: str,
        title: str,
        label: str,
        *,
        use_column_key: bool,
    ) -> int:
        if use_column_key:
            raw = field.get("column") if "column" in field else field.get("name")
        else:
            raw = field.get("column") or field.get("name") or field.get("alias") or ""
        leaf = column_leaf(raw)
        alias = str(field.get("alias") or "").lower()
        field_label = str(field.get("label") or "").lower()
        field_table = column_table(raw, field.get("table") or "")

        score = 0
        if col and leaf.lower() == col.lower():
            score += SCORE_COLUMN_MATCH
            if table and field_table and table.lower() == field_table.lower():
                score += SCORE_TABLE_BONUS
        elif label and (alias == label or field_label == label):
            score += SCORE_LABEL_MATCH
        elif title and (alias == title or field_label == title):
            score += SCORE_TITLE_MATCH
        return score


_IDS = ItemIdMatcher()
_MATCHER = ReportItemMatcher()


def item_id_matches(rep: dict[str, Any], target_id: str) -> bool:
    return _IDS.matches(rep, target_id)


def find_target_report_item(
    filt: dict[str, Any],
    report_items: list[dict[str, Any]],
) -> Optional[dict[str, Any]]:
    return _MATCHER.find(filt, report_items)
