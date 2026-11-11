"""Build per-result-column visualization context from cube metadata."""

from __future__ import annotations

from typing import Any, Dict, List, Optional

from helicalbi.common.cube_info.prompt import (
    format_ai_instructions_for_prompt,
    format_format_strings_for_prompt,
    format_sort_orders_for_prompt,
)


def extract_result_field_names(data_types: Any) -> List[str]:
    """Extract result column/alias names from executeQuery metadata."""
    names: List[str] = []

    def _append_name(value: Any) -> None:
        text = str(value or "").strip()
        if text:
            names.append(text)

    def _from_desc(desc: Any) -> None:
        if not isinstance(desc, dict):
            return
        for key in ("name", "alias", "alias_name", "column", "column_name", "label"):
            if desc.get(key) not in (None, ""):
                _append_name(desc.get(key))
                return

    if data_types is None:
        return []
    if isinstance(data_types, str):
        for part in data_types.split(","):
            part = part.strip()
            if not part:
                continue
            if ":" in part:
                _append_name(part.split(":", 1)[0])
            else:
                _append_name(part)
        return list(dict.fromkeys(names))
    if isinstance(data_types, dict):
        keys = {str(k).lower() for k in data_types.keys()}
        if keys and keys <= {"rows", "row_count", "rowcount", "count", "total_rows"}:
            return []
        if any(k in data_types for k in ("name", "alias", "column_name", "type", "data_type")):
            _from_desc(data_types)
            return list(dict.fromkeys(names))
        for value in data_types.values():
            _from_desc(value)
        return list(dict.fromkeys(names))
    if isinstance(data_types, list):
        for item in data_types:
            if not isinstance(item, dict):
                continue
            keys = {str(k).lower() for k in item.keys()}
            if keys and keys <= {"rows", "row_count", "rowcount", "count", "total_rows"}:
                continue
            if any(k in item for k in ("name", "alias", "column_name", "type", "data_type")):
                _from_desc(item)
                continue
            for value in item.values():
                _from_desc(value)
        return list(dict.fromkeys(names))
    return list(dict.fromkeys(names))


def _match_map_value(mapping: Optional[Dict[str, Any]], field_name: str) -> Any:
    if not mapping or not field_name:
        return None
    if field_name in mapping:
        return mapping[field_name]
    lowered = field_name.lower()
    for key, value in mapping.items():
        if str(key).lower() == lowered:
            return value
        # Bare leaf match for table.column keys.
        if "." in str(key) and str(key).rsplit(".", 1)[-1].lower() == lowered:
            return value
    return None


def _cube_fields_by_name(cube_metadata: Optional[list]) -> Dict[str, dict]:
    """Index cube_metadata columns/measures by alias and physical name."""
    from helicalbi.common.JsonToPara import iter_cube_entries

    index: Dict[str, dict] = {}
    for cube in iter_cube_entries(cube_metadata or []):
        table_name = cube.get("database_table") or ""
        for bucket in ("columns", "measures"):
            for item in cube.get(bucket) or []:
                if not isinstance(item, dict):
                    continue
                enriched = dict(item)
                enriched.setdefault("table", table_name)
                for key in (
                    "alias_name",
                    "dimension_name",
                    "measure_name",
                    "level_name",
                    "column_name",
                ):
                    value = enriched.get(key)
                    if value in (None, ""):
                        continue
                    index.setdefault(str(value).lower(), enriched)
    return index


def build_viz_column_context(
    data_types: Any,
    *,
    cube_metadata: Optional[list] = None,
    format_strings: Optional[Dict[str, str]] = None,
    ai_instructions: Optional[Dict[str, str]] = None,
    sort_orders: Optional[List[dict]] = None,
    domain_context: str = "",
) -> Dict[str, Any]:
    """Build per-result-column viz hints from executeQuery metadata.

    For each result field, attach matching formatString, aiContext instructions,
    sort direction, and semantic type from the cube model.
    """
    field_names = extract_result_field_names(data_types)
    cube_index = _cube_fields_by_name(cube_metadata)
    sort_by_name = {
        str(entry.get("name") or "").lower(): entry
        for entry in (sort_orders or [])
        if isinstance(entry, dict) and entry.get("name")
    }

    columns: List[dict] = []
    filtered_formats: Dict[str, str] = {}
    filtered_ai: Dict[str, str] = {}
    filtered_sorts: List[dict] = []

    for field_name in field_names:
        cube_item = cube_index.get(field_name.lower()) or {}
        format_string = _match_map_value(format_strings, field_name)
        if not format_string:
            format_string = cube_item.get("format_string") or cube_item.get("formatString")
        ai_text = _match_map_value(ai_instructions, field_name)
        ai_context = cube_item.get("ai_context") if isinstance(cube_item.get("ai_context"), dict) else {}
        if not ai_text and ai_context:
            ai_text = str(ai_context.get("instructions") or "").strip() or None
        sort_entry = sort_by_name.get(field_name.lower())
        kind = cube_item.get("kind")
        is_measure = kind in ("measure", "computed_measure") or bool(
            cube_item.get("measure_name") or cube_item.get("aggregator")
        )
        # Measures (column or formula) are never ORDER BY / sort candidates.
        if is_measure:
            sort_entry = None
        elif not sort_entry and cube_item.get("sort_direction") in ("ASC", "DESC"):
            sort_entry = {
                "name": field_name,
                "direction": cube_item.get("sort_direction"),
                "raw": cube_item.get("sort_order"),
            }
        semantic_type = (
            cube_item.get("semantic_type")
            or cube_item.get("semanticType")
            or ""
        )
        entry = {
            "name": field_name,
            "semantic_type": semantic_type or None,
            "format_string": str(format_string) if format_string else None,
            "ai_instructions": str(ai_text) if ai_text else None,
            "ai_context": ai_context or None,
            "sort_direction": (sort_entry or {}).get("direction"),
            "sort": (sort_entry or {}).get("raw"),
            "kind": cube_item.get("kind"),
            "hierarchy_name": cube_item.get("hierarchy_name"),
            "level_name": cube_item.get("level_name"),
            "dimension_name": cube_item.get("dimension_name"),
            "measure_name": cube_item.get("measure_name"),
        }
        # Drop empty optional keys for a cleaner prompt payload.
        entry = {
            key: value
            for key, value in entry.items()
            if value not in (None, "", {}, [])
        }
        columns.append(entry)
        if format_string:
            filtered_formats[field_name] = str(format_string)
        if ai_text:
            filtered_ai[field_name] = str(ai_text)
        if sort_entry and sort_entry.get("direction") in ("ASC", "DESC"):
            filtered_sorts.append(
                {
                    "name": field_name,
                    "direction": sort_entry.get("direction"),
                    "raw": sort_entry.get("raw"),
                    "priority": sort_entry.get("priority", 0),
                }
            )

    return {
        "field_names": field_names,
        "columns": columns,
        "format_strings": filtered_formats,
        "ai_instructions": filtered_ai,
        "sort_orders": filtered_sorts,
        "domain_context": str(domain_context or "").strip(),
        "column_format_strings": format_format_strings_for_prompt(filtered_formats),
        "column_ai_instructions": format_ai_instructions_for_prompt(filtered_ai),
        "column_sort_orders": format_sort_orders_for_prompt(filtered_sorts),
        "column_context": format_viz_column_context_for_prompt(columns),
    }


def format_viz_column_context_for_prompt(columns: Optional[List[dict]]) -> str:
    """Render result-column format/ai/sort/semantic hints for ChartFiller."""
    if not columns:
        return ""
    lines = [
        "Result-column thematic context (only columns from executeQuery metadata). "
        "Use these for encodings, labels, tooltips, formatting, and visual hierarchy:"
    ]
    for entry in columns:
        if not isinstance(entry, dict) or not entry.get("name"):
            continue
        parts = [f"column: {entry['name']}"]
        if entry.get("semantic_type"):
            parts.append(f"semanticType: {entry['semantic_type']}")
        if entry.get("kind"):
            parts.append(f"kind: {entry['kind']}")
        if entry.get("format_string"):
            parts.append(f"formatString: {entry['format_string']}")
        if entry.get("sort_direction"):
            raw = entry.get("sort")
            if raw not in (None, "") and str(raw).upper() != str(entry["sort_direction"]):
                parts.append(f"sort: {entry['sort_direction']} (sort={raw})")
            else:
                parts.append(f"sort: {entry['sort_direction']}")
        if entry.get("hierarchy_name"):
            parts.append(f"hierarchy: {entry['hierarchy_name']}")
        if entry.get("level_name"):
            parts.append(f"level: {entry['level_name']}")
        if entry.get("ai_instructions"):
            parts.append(f"aiContext.instructions: {entry['ai_instructions']}")
        ai_context = entry.get("ai_context")
        if isinstance(ai_context, dict) and ai_context:
            ai_bits = []
            for key in ("instructions", "synonyms", "examples"):
                value = ai_context.get(key)
                if value not in (None, ""):
                    ai_bits.append(f"{key}: {value}")
            if ai_bits and not entry.get("ai_instructions"):
                parts.append(f"aiContext: {{{'; '.join(ai_bits)}}}")
            elif ai_bits:
                # Still expose synonyms/examples when instructions already listed.
                extra = [
                    bit
                    for bit in ai_bits
                    if not bit.startswith("instructions:")
                ]
                if extra:
                    parts.append(f"aiContext: {{{'; '.join(extra)}}}")
        lines.append("- " + "; ".join(parts))
    return "\n".join(lines) if len(lines) > 1 else ""
