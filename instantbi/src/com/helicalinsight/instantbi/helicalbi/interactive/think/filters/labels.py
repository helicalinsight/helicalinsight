"""Resolve human-readable labels for think-mode filters."""
from __future__ import annotations

from typing import Any, Optional

from helicalbi.core.dashboardflow.collect_items import (
    column_leaf,
    filter_title,
    is_column_ref_label,
)
from helicalbi.interactive.think.constants import (
    COLUMN_LABEL_KEYS,
    DEFAULT_FILTER_LABEL,
    FILTER_LABEL_KEYS,
)
from helicalbi.interactive.think.filters.data_model import extract_data_model


class FilterLabelResolver:
    """Resolve alias/label/title for a filter from itself, reports, or contexts."""

    def resolve(
        self,
        filt: dict[str, Any],
        target_rep: Optional[dict[str, Any]],
        all_report_items: list[dict[str, Any]],
        extra_contexts: Optional[list[Any]] = None,
    ) -> str:
        col_name = column_leaf(
            filt.get("column") or filt.get("name") or filt.get("alias") or ""
        )

        for key in FILTER_LABEL_KEYS:
            raw_val = str(filt.get(key) or "").strip()
            if raw_val and not is_column_ref_label(raw_val) and raw_val != col_name:
                return raw_val

        candidates: list[dict[str, Any]] = []
        if target_rep:
            candidates.append(target_rep)
        candidates.extend(r for r in all_report_items if r is not target_rep)

        for rep in candidates:
            resolved = self._from_data_model(extract_data_model(rep), col_name)
            if resolved:
                return resolved

        for ctx in extra_contexts or []:
            resolved = self._from_context(ctx, col_name)
            if resolved:
                return resolved

        if filt.get("label") and not is_column_ref_label(filt["label"]):
            return str(filt["label"]).strip()

        title = str(filt.get("title") or "").strip()
        if title and not is_column_ref_label(title):
            return title

        ft = filter_title(filt)
        if ft and not is_column_ref_label(ft):
            return ft

        return col_name or DEFAULT_FILTER_LABEL

    def _from_context(self, ctx: Any, col_name: str) -> str:
        if isinstance(ctx, dict):
            collected = ctx.get("collected_data") or []
        elif isinstance(ctx, list):
            collected = ctx
        else:
            return ""
        for entry in collected:
            if not isinstance(entry, dict):
                continue
            rep = entry.get("report_model") or entry
            dm = (
                rep.get("data_model")
                if isinstance(rep, dict) and isinstance(rep.get("data_model"), dict)
                else {}
            )
            resolved = self._from_data_model(dm, col_name)
            if resolved:
                return resolved
        return ""

    def _from_data_model(self, dm: dict[str, Any], col_name: str) -> str:
        for flt in dm.get("filters") or []:
            if not isinstance(flt, dict):
                continue
            flt_col = column_leaf(
                flt.get("column") or flt.get("name") or flt.get("alias") or ""
            )
            if col_name and flt_col.lower() == col_name.lower():
                resolved = self._pick_label(flt, FILTER_LABEL_KEYS, col_name)
                if resolved:
                    return resolved
        for col in dm.get("columns") or []:
            if not isinstance(col, dict):
                continue
            c_leaf = column_leaf(
                col.get("column") if "column" in col else col.get("name")
            )
            if col_name and c_leaf.lower() == col_name.lower():
                resolved = self._pick_label(col, COLUMN_LABEL_KEYS, col_name)
                if resolved:
                    return resolved
        return ""

    @staticmethod
    def _pick_label(
        source: dict[str, Any],
        keys: tuple[str, ...],
        col_name: str,
    ) -> str:
        for key in keys:
            val = str(source.get(key) or "").strip()
            if val and not is_column_ref_label(val) and val != col_name:
                return val
        return ""


_RESOLVER = FilterLabelResolver()


def resolve_filter_label(
    filt: dict[str, Any],
    target_rep: Optional[dict[str, Any]],
    all_report_items: list[dict[str, Any]],
    extra_contexts: Optional[list[Any]] = None,
) -> str:
    return _RESOLVER.resolve(filt, target_rep, all_report_items, extra_contexts)
