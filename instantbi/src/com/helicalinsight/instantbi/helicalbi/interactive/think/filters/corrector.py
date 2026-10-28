"""Correct think-mode filter ↔ report associations and label sync."""
from __future__ import annotations

from typing import Any, Optional

from helicalbi.core.dashboardflow.collect_items import column_leaf
from helicalbi.core.dashboardflow.grid_clamp import (
    _build_column_pool,
    _inject_filters_into_item,
    _rebuild_filter_expression,
)
from helicalbi.interactive.think.constants import FILTER_TITLE_SYNC_KEYS
from helicalbi.interactive.think.filters.kinds import DashboardItemClassifier
from helicalbi.interactive.think.filters.labels import FilterLabelResolver
from helicalbi.interactive.think.filters.matcher import ReportItemMatcher
from helicalbi.model.output.dashboard.Kind import Kind


class ThinkModeFilterCorrector:
    """Ensure filters associate with a report model and titles match labels."""

    def __init__(
        self,
        *,
        classifier: Optional[DashboardItemClassifier] = None,
        matcher: Optional[ReportItemMatcher] = None,
        labels: Optional[FilterLabelResolver] = None,
    ) -> None:
        self._classifier = classifier or DashboardItemClassifier()
        self._matcher = matcher or ReportItemMatcher()
        self._labels = labels or FilterLabelResolver()

    def correct(
        self,
        payload_or_dashboard: dict[str, Any],
        *,
        result: Optional[dict[str, Any]] = None,
        question_history: Optional[list[dict[str, Any]]] = None,
    ) -> dict[str, Any]:
        if not isinstance(payload_or_dashboard, dict):
            return payload_or_dashboard

        for db in self._collect_dashboards(payload_or_dashboard):
            self._correct_dashboard(
                db,
                result=result,
                question_history=question_history,
            )
        self._sync_history_report_models(payload_or_dashboard, question_history)
        return payload_or_dashboard

    def _collect_dashboards(
        self, payload_or_dashboard: dict[str, Any]
    ) -> list[dict[str, Any]]:
        dashboards: list[dict[str, Any]] = []
        if "items" in payload_or_dashboard:
            dashboards.append(payload_or_dashboard)
        for key in ("dashboard", "dashboard_model"):
            nested = payload_or_dashboard.get(key)
            if isinstance(nested, dict) and "items" in nested and nested not in dashboards:
                dashboards.append(nested)
        return dashboards

    def _correct_dashboard(
        self,
        db: dict[str, Any],
        *,
        result: Optional[dict[str, Any]],
        question_history: Optional[list[dict[str, Any]]],
    ) -> None:
        items = db.get("items") or []
        filter_items = [it for it in items if self._classifier.is_filter(it)]
        report_items = [it for it in items if self._classifier.is_report(it)]
        if not filter_items:
            return

        items_by_id = {
            str(it.get("component_id") or it.get("itemId") or it.get("id") or ""): it
            for it in report_items
            if str(it.get("component_id") or it.get("itemId") or it.get("id") or "")
        }
        pool = _build_column_pool(items_by_id, filter_items)

        for filt in filter_items:
            self._correct_filter(
                filt,
                report_items,
                pool=pool,
                result=result,
                question_history=question_history,
            )

        self._sync_dashboard_filters_list(db, filter_items)

    def _correct_filter(
        self,
        filt: dict[str, Any],
        report_items: list[dict[str, Any]],
        *,
        pool: Any,
        result: Optional[dict[str, Any]],
        question_history: Optional[list[dict[str, Any]]],
    ) -> None:
        # instant.filter must never contain report_model
        filt.pop("report_model", None)
        if isinstance(filt.get("dashboard_model"), dict):
            filt["dashboard_model"].pop("report_model", None)

        target_rep = self._matcher.find(filt, report_items)
        resolved_alias = self._labels.resolve(
            filt,
            target_rep,
            report_items,
            extra_contexts=[result, question_history],
        )
        self._apply_alias(filt, resolved_alias)

        if target_rep is None:
            return

        target_id = str(
            target_rep.get("component_id")
            or target_rep.get("itemId")
            or target_rep.get("id")
            or ""
        )
        filt["sourceItemId"] = target_id
        if isinstance(filt.get("dashboard_model"), dict):
            filt["dashboard_model"]["sourceItemId"] = target_id

        filt_wire_spec = {
            "column": filt.get("column_ref") or filt.get("column"),
            "table": filt.get("table") or "",
            "title": resolved_alias,
            "label": resolved_alias,
            "alias": resolved_alias,
            "default_values": filt.get("defaultValues") or filt.get("default_values") or [],
        }
        _inject_filters_into_item(target_rep, [filt_wire_spec], pool=pool)
        self._sync_alias_into_data_models(target_rep, filt, resolved_alias)

    def _apply_alias(self, filt: dict[str, Any], resolved_alias: str) -> None:
        for key in FILTER_TITLE_SYNC_KEYS:
            filt[key] = resolved_alias
        if isinstance(filt.get("dashboard_model"), dict):
            for key in FILTER_TITLE_SYNC_KEYS:
                filt["dashboard_model"][key] = resolved_alias
        if "header_title" in filt:
            filt["header_title"] = resolved_alias
        if (
            isinstance(filt.get("dashboard_model"), dict)
            and "header_title" in filt["dashboard_model"]
        ):
            filt["dashboard_model"]["header_title"] = resolved_alias

    def _sync_alias_into_data_models(
        self,
        target_rep: dict[str, Any],
        filt: dict[str, Any],
        resolved_alias: str,
    ) -> None:
        c_col = column_leaf(
            filt.get("column") or filt.get("name") or filt.get("alias") or ""
        )
        for dm in self._iter_data_models(target_rep):
            for f in dm.get("filters") or []:
                if not isinstance(f, dict):
                    continue
                f_col = column_leaf(
                    f.get("column") or f.get("name") or f.get("alias") or ""
                )
                if f_col and c_col and f_col.lower() == c_col.lower():
                    f["alias"] = resolved_alias
                    f["label"] = resolved_alias
            expr = _rebuild_filter_expression(dm.get("filters") or [])
            if expr is not None:
                dm["filterExpression"] = expr

    @staticmethod
    def _iter_data_models(target_rep: dict[str, Any]):
        dm = target_rep.get("data_model")
        if isinstance(dm, dict):
            yield dm
        rep_model = target_rep.get("report_model")
        if isinstance(rep_model, dict) and isinstance(rep_model.get("data_model"), dict):
            yield rep_model["data_model"]

    def _sync_history_report_models(
        self,
        payload_or_dashboard: dict[str, Any],
        question_history: Optional[list[dict[str, Any]]],
    ) -> None:
        """Copy each instant tile report_model onto the matching history chat response.

        Filter correction writes filters onto the dashboard instant report_model.
        question_history is built earlier, so its chat_response.report_model is a
        separate copy. Align them after that correction.
        """
        histories: list[list[dict[str, Any]]] = []
        if isinstance(question_history, list):
            histories.append(question_history)
        nested = payload_or_dashboard.get("question_history")
        if isinstance(nested, list) and nested is not question_history:
            histories.append(nested)
        if not histories:
            return

        by_seq: dict[str, dict[str, Any]] = {}
        for db in self._collect_dashboards(payload_or_dashboard):
            for item in db.get("items") or []:
                if not isinstance(item, dict) or not self._is_instant(item):
                    continue
                report = item.get("report_model")
                if not isinstance(report, dict):
                    model = item.get("dashboard_model")
                    if isinstance(model, dict):
                        report = model.get("report_model")
                if not isinstance(report, dict):
                    continue
                for key in ("chat_sequence_id", "itemId", "id"):
                    seq = str(item.get(key) or "").strip()
                    if seq:
                        by_seq.setdefault(seq, report)
        if not by_seq:
            return

        for history in histories:
            for entry in history:
                if not isinstance(entry, dict):
                    continue
                report = by_seq.get(str(entry.get("chat_seq_id") or "").strip())
                if report is None:
                    continue
                chat = entry.get("chat_response")
                if not isinstance(chat, dict):
                    chat = {}
                    entry["chat_response"] = chat
                chat["report_model"] = report

    @staticmethod
    def _is_instant(item: dict[str, Any]) -> bool:
        kind = str(item.get("kind") or item.get("compType") or "").strip()
        if Kind.wire(kind) == Kind.INSTANT:
            return True
        model = item.get("dashboard_model")
        if isinstance(model, dict):
            return Kind.wire(str(model.get("kind") or "")) == Kind.INSTANT
        return False

    def _sync_dashboard_filters_list(
        self,
        db: dict[str, Any],
        filter_items: list[dict[str, Any]],
    ) -> None:
        if not isinstance(db.get("filters"), list):
            return
        for entry in db["filters"]:
            if not isinstance(entry, dict):
                continue
            match_filt = next(
                (
                    f
                    for f in filter_items
                    if (
                        (f.get("component_id") and f.get("component_id") == entry.get("component_id"))
                        or (
                            column_leaf(f.get("column")).lower()
                            == column_leaf(entry.get("column")).lower()
                        )
                    )
                ),
                None,
            )
            if not match_filt:
                continue
            if match_filt.get("sourceItemId"):
                entry["sourceItemId"] = match_filt["sourceItemId"]
            entry["title"] = match_filt.get("title") or entry.get("title")
            entry["label"] = match_filt.get("label") or entry.get("label")
            entry["alias"] = match_filt.get("alias") or entry.get("alias")


_CORRECTOR = ThinkModeFilterCorrector()


def correct_think_mode_filters(
    payload_or_dashboard: dict[str, Any],
    *,
    result: Optional[dict[str, Any]] = None,
    question_history: Optional[list[dict[str, Any]]] = None,
) -> dict[str, Any]:
    return _CORRECTOR.correct(
        payload_or_dashboard,
        result=result,
        question_history=question_history,
    )
