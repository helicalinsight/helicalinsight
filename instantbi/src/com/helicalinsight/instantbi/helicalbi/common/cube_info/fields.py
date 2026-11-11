"""Format strings, AI instructions, and sort orders derived from cube_info."""

from __future__ import annotations

from typing import Any, Dict, List, Optional, Set

from helicalbi.common.cube_info.lookup import _parse_column_reference
from helicalbi.common.cube_info.normalize import normalize_cube_info


def _aggregator_from_cube_item(item: dict) -> str:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    return str(item.get("aggregator") or metric_obj.get("aggregator") or "")


def _format_string_from_cube_item(item: dict) -> str:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    for source in (item, metric_obj):
        for key in ("formatString", "format_string", "format"):
            value = source.get(key)
            if value:
                return str(value)
    return ""


_NO_SORT_STRINGS = frozenset(
    ("none", "null", "n/a", "-", "undefined", "natural")
)


def _sort_order_raw_from_source(source: dict) -> tuple[bool, Any]:
    """Resolve sort from one dict.

    Returns ``(resolved, value)`` where ``resolved`` is True when an explicit
    decision was made (including Natural/none → value None). Prefer ``sort``
    over numeric ``sortOrder`` so Natural is not overridden by sortOrder=0.
    """
    if not isinstance(source, dict):
        return False, None
    if "sort" in source:
        value = source.get("sort")
        if value in (None, ""):
            pass  # fall through to sortOrder
        elif isinstance(value, str) and value.strip().lower() in _NO_SORT_STRINGS:
            return True, None
        else:
            return True, value
    for key in ("sortOrder", "sort_order"):
        if key not in source:
            continue
        value = source.get(key)
        if value in (None, ""):
            continue
        if isinstance(value, str) and value.strip().lower() in _NO_SORT_STRINGS:
            return True, None
        return True, value
    return False, None


def _sort_order_raw_from_cube_item(item: dict) -> Any:
    """Return the raw sortOrder / sort_order / sort value when present.

    Cube payloads may use numeric ``sortOrder`` (0=ASC, 1=DESC or priority)
    or an explicit ``sort`` direction string such as ``Ascending`` / ``Descending``.
    Values of ``none`` / ``Natural`` / empty are treated as absent so they are
    omitted from the final-SQL sort list. Explicit ``sort: Natural`` wins over
    a numeric ``sortOrder`` on the same item.
    """
    resolved, value = _sort_order_raw_from_source(item if isinstance(item, dict) else {})
    if resolved:
        return value
    metric_obj = item.get("metric") if isinstance(item, dict) and isinstance(item.get("metric"), dict) else {}
    _resolved_metric, metric_value = _sort_order_raw_from_source(metric_obj)
    return metric_value if _resolved_metric else None


def sort_direction_from_value(value: Any) -> Optional[str]:
    """Map cube sortOrder values to SQL directions.

    ``0`` / ``"asc"`` / ``"Ascending"`` → ASC, ``1`` / ``"desc"`` / ``"Descending"`` → DESC.
    ``none`` / ``Natural`` / empty / missing / any other non ASC|DESC string → no sort
    (omit from ORDER BY hints). Other integers default to ASC so sequential sort
    priorities (0, 1, 2, ...) still produce valid ORDER BY clauses.
    """
    if value is None or value == "":
        return None
    if isinstance(value, str):
        lowered = value.strip().lower()
        if not lowered or lowered in _NO_SORT_STRINGS:
            return None
        if lowered in ("0", "asc", "ascending"):
            return "ASC"
        if lowered in ("1", "desc", "descending"):
            return "DESC"
        try:
            value = int(lowered)
        except ValueError:
            # Only Ascending/Descending (and numeric priorities) are sort candidates.
            return None
    try:
        num = int(value)
    except (TypeError, ValueError):
        return None
    if num == 1:
        return "DESC"
    return "ASC"


def _sort_priority_from_value(value: Any) -> int:
    """Numeric priority for ORDER BY sequencing (lower first)."""
    if value is None or value == "":
        return 10**9
    if isinstance(value, str):
        lowered = value.strip().lower()
        if lowered in ("asc", "ascending"):
            return 0
        if lowered in ("desc", "descending"):
            return 1
        try:
            return int(lowered)
        except ValueError:
            return 10**9
    try:
        return int(value)
    except (TypeError, ValueError):
        return 10**9


def _cube_item_display_names(item: dict, primary_name_key: str) -> List[str]:
    """Names to key format/sort maps: dimension/measure/level/column/metric."""
    names: List[str] = []
    for key in (
        primary_name_key,
        "levelName",
        "dimensionName",
        "measureName",
        "columnName",
        "metricId",
    ):
        value = item.get(key)
        if value not in (None, ""):
            names.append(str(value))
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    for key in ("metric", "name", "id"):
        value = metric_obj.get(key)
        if value not in (None, ""):
            names.append(str(value))
    # Prefer bare column leaf when columnName is table.column.
    column_name = item.get("columnName")
    if column_name and "." in str(column_name):
        _, leaf = _parse_column_reference(str(column_name))
        if leaf:
            names.append(leaf)
    return list(dict.fromkeys(names))


def format_strings_from_cube_info(cube_info: list) -> Dict[str, str]:
    """Map dimension/measure/column/metric names to formatString values."""
    cube_info = normalize_cube_info(cube_info)
    mapping: Dict[str, str] = {}
    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for dim in cube.get("dimensions") or []:
            if not isinstance(dim, dict):
                continue
            fmt = _format_string_from_cube_item(dim)
            if not fmt:
                continue
            for name in _cube_item_display_names(dim, "dimensionName"):
                mapping.setdefault(name, fmt)
        for measure in cube.get("measures") or []:
            if not isinstance(measure, dict):
                continue
            fmt = _format_string_from_cube_item(measure)
            if not fmt:
                continue
            for name in _cube_item_display_names(measure, "measureName"):
                mapping.setdefault(name, fmt)
    return mapping


def ai_instructions_from_cube_info(cube_info: list) -> Dict[str, str]:
    """Map dimension/measure/column/metric names to aiContext.instructions."""
    cube_info = normalize_cube_info(cube_info)
    mapping: Dict[str, str] = {}
    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for dim in cube.get("dimensions") or []:
            if not isinstance(dim, dict):
                continue
            instructions = str(dim.get("ai_instructions") or "").strip()
            if not instructions:
                continue
            for name in _cube_item_display_names(dim, "dimensionName"):
                mapping.setdefault(name, instructions)
        for measure in cube.get("measures") or []:
            if not isinstance(measure, dict):
                continue
            instructions = str(measure.get("ai_instructions") or "").strip()
            if not instructions:
                continue
            for name in _cube_item_display_names(measure, "measureName"):
                mapping.setdefault(name, instructions)
    return mapping


def sort_orders_from_cube_info(cube_info: list) -> List[dict]:
    """Collect sortOrder entries for final SQL ORDER BY hints.

    Only **dimensions** are ORDER BY candidates. Measures (physical column or
    formula) are never included, even when they carry Ascending/Descending.

    Each entry: ``{name, direction, priority, raw}`` sorted by priority.
    Direction mapping: ``0``→ASC, ``1``→DESC (or explicit asc/desc strings).
    """
    cube_info = normalize_cube_info(cube_info)
    entries: List[dict] = []
    seen: Set[str] = set()

    def _append(item: dict, primary_name_key: str) -> None:
        raw = _sort_order_raw_from_cube_item(item)
        if raw is None:
            return
        direction = sort_direction_from_value(raw)
        if not direction:
            return
        names = _cube_item_display_names(item, primary_name_key)
        primary = next(
            (n for n in names if n == str(item.get(primary_name_key) or "")),
            names[0] if names else "",
        )
        if not primary or primary in seen:
            return
        seen.add(primary)
        entries.append(
            {
                "name": primary,
                "direction": direction,
                "priority": _sort_priority_from_value(raw),
                "raw": raw,
            }
        )

    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for dim in cube.get("dimensions") or []:
            if isinstance(dim, dict):
                _append(dim, "dimensionName")
        # Measures (column or formula) are not ORDER BY candidates.

    entries.sort(key=lambda e: (e["priority"], e["name"]))
    return entries
