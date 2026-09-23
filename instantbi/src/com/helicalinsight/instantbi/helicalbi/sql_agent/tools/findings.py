"""Turn raw SQL preview rows into short answer + descriptive analysis text."""
from __future__ import annotations

import ast
import json
import logging
import re
from typing import Any, Mapping, Sequence

logger = logging.getLogger(__name__)

_RAW_PREVIEW_RE = re.compile(r"^\s*[\[{]")


def _looks_like_raw_preview(text: str) -> bool:
    cleaned = str(text or "").strip()
    if not cleaned:
        return False
    if _RAW_PREVIEW_RE.match(cleaned):
        return True
    return cleaned.startswith("[{") or cleaned.startswith("{")


def _parse_rows(raw: Any) -> list[dict[str, Any]]:
    if isinstance(raw, list):
        return [row for row in raw if isinstance(row, dict)]
    if isinstance(raw, dict):
        return [raw]
    text = str(raw or "").strip()
    if not text:
        return []
    for loader in (json.loads, ast.literal_eval):
        try:
            parsed = loader(text)
        except Exception:
            continue
        if isinstance(parsed, list):
            return [row for row in parsed if isinstance(row, dict)]
        if isinstance(parsed, dict):
            return [parsed]
    return []


def _format_number(value: Any) -> str:
    try:
        number = float(value)
    except (TypeError, ValueError):
        return str(value)
    if number.is_integer():
        return f"{int(number):,}"
    return f"{number:,.2f}"


def _humanize(label: Any) -> str:
    text = str(label or "").strip()
    if not text:
        return ""
    return text.replace("_", " ")


def _measure_keys(rows: Sequence[Mapping[str, Any]]) -> list[str]:
    keys: list[str] = []
    for row in rows:
        for key, value in row.items():
            if key in keys:
                continue
            try:
                float(value)
            except (TypeError, ValueError):
                continue
            keys.append(str(key))
    return keys


def _dimension_keys(rows: Sequence[Mapping[str, Any]], measure_keys: Sequence[str]) -> list[str]:
    keys: list[str] = []
    measure_set = set(measure_keys)
    for row in rows:
        for key in row.keys():
            name = str(key)
            if name in measure_set or name in keys:
                continue
            keys.append(name)
    return keys


def _is_timeish(label: str) -> bool:
    lower = label.lower()
    return any(token in lower for token in ("date", "time", "month", "week", "year", "day", "period"))


def describe_query_findings(
    raw_result: Any,
    *,
    question: str = "",
    title: str = "",
) -> dict[str, str]:
    """Build user-facing ``answer`` and ``analysis`` from a SQL preview.

    Never returns raw JSON/list dumps as the analysis body.
    """
    rows = _parse_rows(raw_result)
    asked = str(question or "").strip()
    heading = str(title or "").strip() or (asked.rstrip("?") if asked else "This step")

    if not rows:
        text = str(raw_result or "").strip()
        if text and not _looks_like_raw_preview(text):
            return {"answer": text, "analysis": text}
        empty = (
            f"No rows were returned for “{asked or heading}”, so this step "
            "could not quantify the result."
        )
        return {"answer": empty, "analysis": empty}

    measures = _measure_keys(rows)
    dimensions = _dimension_keys(rows, measures)
    measure = measures[0] if measures else ""
    measure_label = _humanize(measure) or "value"

    if len(rows) == 1 and measures and not dimensions:
        value = _format_number(rows[0].get(measure))
        answer = f"The {measure_label.lower()} is {value}."
        analysis = (
            f"{heading} answers the question with a single headline figure. "
            f"{measure_label} for the selected scope is {value}."
        )
        return {"answer": answer, "analysis": analysis}

    if len(rows) == 1:
        parts = []
        for key, value in rows[0].items():
            label = _humanize(key)
            if key in measures:
                parts.append(f"{label} is {_format_number(value)}")
            else:
                parts.append(f"{label} is {value}")
        answer = "; ".join(parts) + "."
        analysis = (
            f"{heading} returns one result row. "
            + " ".join(parts)
            + "."
        )
        return {"answer": answer, "analysis": analysis}

    dim = dimensions[0] if dimensions else ""
    dim_label = _humanize(dim) or "category"
    time_series = bool(dim and _is_timeish(dim)) or any(_is_timeish(d) for d in dimensions)

    if time_series and measure:
        values = []
        for row in rows:
            try:
                values.append(float(row.get(measure)))
            except (TypeError, ValueError):
                continue
        first_label = rows[0].get(dim)
        last_label = rows[-1].get(dim)
        if values:
            total = sum(values)
            peak = max(values)
            low = min(values)
            answer = (
                f"{measure_label} moves across {len(rows)} {dim_label.lower()} points, "
                f"from {_format_number(values[0])} to {_format_number(values[-1])} "
                f"(peak {_format_number(peak)}, low {_format_number(low)})."
            )
            analysis = (
                f"{heading} is a detailed time series of {measure_label.lower()} "
                f"over {dim_label.lower()}. Across {len(rows)} points "
                f"(from {first_label} to {last_label}), the total observed "
                f"{measure_label.lower()} is {_format_number(total)}. "
                f"The highest point is {_format_number(peak)} and the lowest is "
                f"{_format_number(low)}. Use Show to open this detail as a table."
            )
        else:
            answer = f"{heading} lists {len(rows)} {dim_label.lower()} points."
            analysis = (
                f"{heading} shows how values change over {dim_label.lower()} "
                f"across {len(rows)} detailed rows."
            )
        return {"answer": answer, "analysis": analysis}

    if dimensions and measure:
        ranked = []
        for row in rows:
            try:
                ranked.append((row.get(dim), float(row.get(measure))))
            except (TypeError, ValueError):
                ranked.append((row.get(dim), None))
        numeric = [(label, value) for label, value in ranked if value is not None]
        numeric.sort(key=lambda item: item[1], reverse=True)
        if numeric:
            top_bits = [
                f"{label} ({_format_number(value)})"
                for label, value in numeric[:3]
            ]
            total = sum(value for _label, value in numeric)
            answer = (
                f"{measure_label} breaks down by {dim_label.lower()}: "
                + ", ".join(top_bits)
                + ("…" if len(numeric) > 3 else "")
                + f". Combined total is {_format_number(total)}."
            )
            analysis = (
                f"{heading} explains composition of {measure_label.lower()} "
                f"across {dim_label.lower()}. "
                f"There are {len(numeric)} categories totaling {_format_number(total)}. "
                f"The largest contributors are {', '.join(top_bits)}."
            )
            return {"answer": answer, "analysis": analysis}

    answer = f"{heading} returned {len(rows)} result rows."
    analysis = (
        f"{heading} produced {len(rows)} detailed rows for "
        f"“{asked or heading}”. Open Show to inspect the values as a table."
    )
    return {"answer": answer, "analysis": analysis}


def prefers_detail_table(
    *,
    viz_hint: str = "",
    question: str = "",
    title: str = "",
    purpose: str = "",
) -> bool:
    """True when Show should prefer a table (trend / detailed report)."""
    blob = " ".join(
        [
            str(viz_hint or ""),
            str(question or ""),
            str(title or ""),
            str(purpose or ""),
        ]
    ).lower()
    hint = str(viz_hint or "").strip().lower()
    if hint in {"line", "area", "timeseries", "time_series", "trend"}:
        return True
    tokens = (
        "trend",
        "over time",
        "over date",
        "time series",
        "timeseries",
        "by date",
        "by month",
        "by week",
        "by day",
        "moved over",
        "detailed",
        "detail report",
    )
    return any(token in blob for token in tokens)
