"""Rewrite technical SELECT aliases to business-friendly display names."""

from __future__ import annotations

import logging
import re
from typing import Any, Dict, Optional

import sqlglot
from sqlglot import exp

from helicalbi.common.DialectMapper import resolve_sqlglot_dialect
from helicalbi.common.JsonToPara import (
    iter_cube_entries,
    needs_quoting,
    snake_to_words,
    unquote_identifier,
)

logger = logging.getLogger(__name__)

_CAMEL_BOUNDARY = re.compile(r"([a-z0-9])([A-Z])")
_NON_WORD_SEP = re.compile(r"[,.\-/]+")
_MULTI_SPACE = re.compile(r"\s+")


def is_technical_alias(name: Optional[str]) -> bool:
    """True when a SELECT alias looks machine-style rather than business-friendly."""
    value = unquote_identifier(name or "").strip()
    if not value:
        return False
    if "_" in value or "," in value or "-" in value or "/" in value:
        return True
    if value.isupper() and any(ch.isalpha() for ch in value) and len(value) > 1:
        return True
    if _CAMEL_BOUNDARY.search(value):
        return True
    return False


def to_business_friendly_alias(name: str) -> str:
    """Convert snake_case / camelCase / ALLCAPS / comma-separated labels to Title Case."""
    value = unquote_identifier(name or "").strip()
    if not value:
        return value
    value = _CAMEL_BOUNDARY.sub(r"\1_\2", value)
    value = _NON_WORD_SEP.sub("_", value)
    value = value.replace(" ", "_")
    friendly = snake_to_words(value)
    return _MULTI_SPACE.sub(" ", friendly).strip()


def _preferred_alias(item: dict) -> str:
    return str(
        item.get("alias_name")
        or item.get("measure_name")
        or item.get("dimension_name")
        or item.get("column_name")
        or ""
    ).strip()


def build_physical_to_business_alias_map(cube_metadata) -> Dict[str, str]:
    """Map physical column names (and existing aliases) to preferred business labels."""
    mapping: Dict[str, str] = {}
    for cube in iter_cube_entries(cube_metadata or []):
        for bucket in ("columns", "measures"):
            for item in cube.get(bucket) or []:
                if not isinstance(item, dict):
                    continue
                preferred = _preferred_alias(item)
                if not preferred:
                    continue
                # Prefer a non-technical cube label when available.
                friendly = (
                    preferred
                    if not is_technical_alias(preferred)
                    else to_business_friendly_alias(preferred)
                )
                for key in (
                    item.get("column_name"),
                    item.get("alias_name"),
                    item.get("measure_name"),
                    item.get("dimension_name"),
                ):
                    key_text = unquote_identifier(str(key or "")).strip()
                    if key_text and key_text not in mapping:
                        mapping[key_text] = friendly
                    key_lower = key_text.lower()
                    if key_lower and key_lower not in mapping:
                        mapping[key_lower] = friendly
    return mapping


def _lookup_business_alias(
    current: str,
    physical: Optional[str],
    alias_map: Dict[str, str],
) -> Optional[str]:
    for key in (physical, current):
        if not key:
            continue
        candidate = alias_map.get(key) or alias_map.get(key.lower())
        if candidate:
            return candidate
    return None


def resolve_select_alias(
    current: str,
    physical: Optional[str] = None,
    alias_map: Optional[Dict[str, str]] = None,
) -> Optional[str]:
    """Return a replacement SELECT alias when ``current`` is technical; else None."""
    current_name = unquote_identifier(current or "").strip()
    if not current_name or not is_technical_alias(current_name):
        return None

    preferred = _lookup_business_alias(current_name, physical, alias_map or {})
    if preferred and preferred != current_name:
        return preferred
    friendly = to_business_friendly_alias(current_name)
    return friendly if friendly and friendly != current_name else None


def _column_name_from_expression(node: Any) -> Optional[str]:
    if isinstance(node, exp.Alias):
        node = node.this
    if isinstance(node, exp.Column):
        return unquote_identifier(node.name)
    return None


def _output_name(projection: exp.Expression) -> Optional[str]:
    if isinstance(projection, exp.Alias):
        return unquote_identifier(projection.alias)
    if isinstance(projection, exp.Column):
        return unquote_identifier(projection.name)
    return None


def _with_alias(projection: exp.Expression, alias: str) -> exp.Expression:
    quoted = needs_quoting(alias) or " " in alias
    if isinstance(projection, exp.Alias):
        updated = projection.copy()
        updated.set("alias", exp.to_identifier(alias, quoted=quoted))
        return updated
    return exp.alias_(projection.copy(), alias, quoted=quoted)


def _replace_order_alias_refs(tree: exp.Expression, renames: Dict[str, str]) -> None:
    """Update ORDER BY identifiers that still point at the old SELECT alias."""
    if not renames:
        return
    for ordered in tree.find_all(exp.Ordered):
        target = ordered.this
        if isinstance(target, exp.Column) and not target.table:
            old = unquote_identifier(target.name)
            new = renames.get(old)
            if not new:
                continue
            ordered.set(
                "this",
                exp.column(exp.to_identifier(new, quoted=needs_quoting(new) or " " in new)),
            )


def rewrite_select_aliases(
    sql: str,
    cube_metadata=None,
    dialect: Optional[str] = None,
) -> str:
    """Rewrite technical SELECT aliases to business-friendly names.

    Only SELECT output names are changed. Physical column references in the
    expression body stay as-is. When cube metadata provides a business alias
    for the column, that label wins; otherwise underscore/camelCase/etc. names
    are Title-Cased.
    """
    if not sql or not str(sql).strip():
        return sql

    alias_map = build_physical_to_business_alias_map(cube_metadata)
    read_dialect = resolve_sqlglot_dialect(dialect)
    try:
        tree = sqlglot.parse_one(sql, read=read_dialect)
    except Exception:
        logger.error(
            "SELECT alias rewrite skipped; SQL parse failed for dialect=%s",
            dialect,
            exc_info=True,
        )
        return sql

    select = tree.find(exp.Select)
    if select is None:
        return sql

    renames: Dict[str, str] = {}
    new_projections = []
    changed = False
    for projection in select.expressions:
        current = _output_name(projection)
        if not current:
            new_projections.append(projection)
            continue
        physical = _column_name_from_expression(projection)
        replacement = resolve_select_alias(current, physical, alias_map)
        if not replacement:
            new_projections.append(projection)
            continue
        new_projections.append(_with_alias(projection, replacement))
        renames[current] = replacement
        changed = True

    if not changed:
        return sql

    select.set("expressions", new_projections)
    _replace_order_alias_refs(tree, renames)
    try:
        return tree.sql(dialect=read_dialect)
    except Exception:
        logger.error(
            "SELECT alias rewrite failed to render SQL; returning original",
            exc_info=True,
        )
        return sql
