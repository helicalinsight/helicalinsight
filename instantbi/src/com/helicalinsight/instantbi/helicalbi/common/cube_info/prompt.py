"""Render cube_info hints for SQL and visualization prompts."""

from __future__ import annotations

from typing import Dict, List, Optional


_NO_ORDER_BY_PROMPT = (
    "No Ascending/Descending dimension sorts are configured "
    "(all Natural/none/empty). "
    "Do NOT add an ORDER BY clause unless the user question explicitly asks "
    "for a sort or ranking (e.g. top, highest, lowest, ascending, descending). "
    "Do not ORDER BY every selected column."
)


def format_sort_orders_for_prompt(sort_orders: List[dict]) -> str:
    """Render sort-order hints for the final SQL prompt.

    Only ascending/descending entries are included; none/empty/Natural sorts
    are omitted. When nothing remains, return an explicit no-ORDER-BY rule so
    the final-SQL model does not invent ORDER BY on all columns.
    """
    usable = [
        entry
        for entry in (sort_orders or [])
        if isinstance(entry, dict)
        and entry.get("name")
        and entry.get("direction") in ("ASC", "DESC")
    ]
    if not usable:
        return _NO_ORDER_BY_PROMPT
    lines = [
        "Use these column sort directions in ORDER BY "
        "(Ascending=ASC, Descending=DESC; skip none/empty/Natural; "
        "follow listed priority). "
        "Do not add extra ORDER BY columns beyond this list unless the user "
        "question explicitly requests a different sort:"
    ]
    for entry in usable:
        lines.append(
            f"- {entry['name']}: {entry['direction']} "
            f"(sort={entry.get('raw')})"
        )
    return "\n".join(lines)


def filter_sort_orders_for_picked(
    sort_orders: Optional[List[dict]],
    picked_names: Optional[List[str]],
) -> List[dict]:
    """Keep ASC/DESC sort hints that match picked dimension names only."""
    allowed = {
        str(name).strip().lower()
        for name in (picked_names or [])
        if name and str(name).strip()
    }
    filtered: List[dict] = []
    for entry in sort_orders or []:
        if not isinstance(entry, dict):
            continue
        direction = entry.get("direction")
        if direction not in ("ASC", "DESC"):
            continue
        name = str(entry.get("name") or "").strip()
        if not name:
            continue
        if allowed and name.lower() not in allowed:
            continue
        filtered.append(entry)
    return filtered


def format_format_strings_for_prompt(format_strings: Dict[str, str]) -> str:
    """Render format-string hints for visualization prompts."""
    if not format_strings:
        return ""
    lines = [
        "Apply these Excel-style format strings on matching chart axes / labels / tooltips "
        "(match by column, metric, dimension, or measure name):"
    ]
    for name, fmt in format_strings.items():
        lines.append(f"- {name}: {fmt}")
    return "\n".join(lines)


def format_ai_instructions_for_prompt(ai_instructions: Dict[str, str]) -> str:
    """Render per-field aiContext.instructions for visualization prompts."""
    if not ai_instructions:
        return ""
    lines = [
        "Follow these field-level AI instructions when choosing and configuring the chart "
        "(match by column, metric, dimension, or measure name):"
    ]
    for name, instructions in ai_instructions.items():
        lines.append(f"- {name}: {instructions}")
    return "\n".join(lines)
