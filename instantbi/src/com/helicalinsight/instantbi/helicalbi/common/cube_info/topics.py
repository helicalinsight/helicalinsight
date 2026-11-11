"""Topic component indexes, mappings, and domain context."""

from __future__ import annotations

from typing import Any, Dict, List, Optional, Set, Tuple

from helicalbi.common.cube_info.lookup import _ai_context_payload, _formula_from_cube_item
from helicalbi.common.cube_info.normalize import _topic_name, normalize_cube_info


def _component_alias(name: str) -> str:
    return " alias:-" + str(name)


def _strip_component_alias(token: Any) -> str:
    """Normalize topic component tokens (`` alias:-name`` or bare name)."""
    text = str(token or "").strip()
    if text.startswith(" alias:-"):
        return text[len(" alias:-") :]
    return text


def _topic_mapping_has_components(entry: dict) -> bool:
    """True when a topic mapping still lists at least one dim/measure component."""
    if not isinstance(entry, dict):
        return False
    rich = entry.get("components")
    if isinstance(rich, list):
        for component in rich:
            if not isinstance(component, dict):
                continue
            if str(component.get("name") or component.get("id") or "").strip():
                return True
    legacy = entry.get("component")
    if isinstance(legacy, list):
        for token in legacy:
            if str(_strip_component_alias(token) or "").strip():
                return True
    return False


def _cube_item_kind(item: dict, name_key: str) -> str:
    """Classify a cube item for topic component mapping."""
    if name_key == "measureName":
        column_name = item.get("columnName")
        formula = _formula_from_cube_item(item)
        if (not column_name or not str(column_name).strip()) and formula:
            return "computed_measure"
        return "measure"
    if item.get("hierarchyName") or item.get("levelName"):
        return "hierarchy"
    return "dimension"


def _component_ids_from_item(item: dict) -> List[str]:
    ids: List[str] = []
    for key in ("columnId", "metricId", "primaryColumnId"):
        value = item.get(key)
        if value not in (None, ""):
            ids.append(str(value))
    return list(dict.fromkeys(ids))


def _component_record_from_item(item: dict, name_key: str) -> Optional[dict]:
    """Build a topic-component record from a dimension/measure/hierarchy item."""
    if not isinstance(item, dict):
        return None
    name = item.get(name_key) or item.get("levelName") or item.get("hierarchyName")
    if not name:
        return None
    kind = _cube_item_kind(item, name_key)
    record: dict[str, Any] = {
        "id": (
            str(item.get("columnId") or item.get("metricId") or "")
            or None
        ),
        "name": str(name),
        "kind": kind,
    }
    if record["id"] is None:
        record.pop("id", None)
    else:
        record["id"] = str(record["id"])
    column_name = item.get("columnName")
    if column_name not in (None, ""):
        record["columnName"] = str(column_name)
    table_id = item.get("tableId") or item.get("table_id")
    if table_id not in (None, ""):
        record["tableId"] = str(table_id)
    table_name = item.get("tableName") or item.get("table_name")
    if table_name not in (None, ""):
        record["tableName"] = str(table_name)
    semantic_type = item.get("semanticType")
    if semantic_type not in (None, ""):
        record["semanticType"] = semantic_type
    hierarchy_name = item.get("hierarchyName")
    if hierarchy_name:
        record["hierarchyName"] = hierarchy_name
    level_name = item.get("levelName")
    if level_name:
        record["levelName"] = level_name
    formula = _formula_from_cube_item(item)
    if formula:
        record["formula"] = formula
    ai_context = _ai_context_payload(item)
    if ai_context:
        record["aiContext"] = ai_context
    return record


def build_cube_component_index(cube_info: list) -> Tuple[Dict[str, List[dict]], Dict[str, List[dict]]]:
    """Index cube dimensions/measures/hierarchies by id and by name."""
    cube_info = normalize_cube_info(cube_info)
    by_id: Dict[str, List[dict]] = {}
    by_name: Dict[str, List[dict]] = {}

    def _index(item: dict, name_key: str) -> None:
        record = _component_record_from_item(item, name_key)
        if not record:
            return
        by_name.setdefault(str(record["name"]).lower(), []).append(record)
        if record.get("levelName"):
            by_name.setdefault(str(record["levelName"]).lower(), []).append(record)
        if record.get("hierarchyName"):
            by_name.setdefault(str(record["hierarchyName"]).lower(), []).append(record)
        for cid in _component_ids_from_item(item):
            by_id.setdefault(cid, []).append(record)

    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for dim in cube.get("dimensions") or []:
            if isinstance(dim, dict):
                _index(dim, "dimensionName")
        for measure in cube.get("measures") or []:
            if isinstance(measure, dict):
                _index(measure, "measureName")
    return by_id, by_name


def resolve_topic_component(
    component: dict,
    by_id: Dict[str, List[dict]],
    by_name: Dict[str, List[dict]],
    preferred_table_id: Optional[str] = None,
) -> dict:
    """Resolve a domain topic component ``{id, name}`` against the cube index.

    Topics may reference a dimension, measure, hierarchy level, or a
    blank-column (computed) measure. When multiple candidates exist across
    tables, ``preferred_table_id`` disambiguates in favor of the topic's primary table.
    """
    raw_id = component.get("id")
    raw_name = component.get("name")
    cid = str(raw_id).strip() if raw_id not in (None, "") else ""
    name = str(raw_name).strip() if raw_name not in (None, "") else ""

    candidates: List[dict] = []
    if cid:
        candidates = list(by_id.get(cid) or [])
    if name and candidates:
        lowered = name.lower()
        named = [
            entry
            for entry in candidates
            if str(entry.get("name") or "").lower() == lowered
            or str(entry.get("levelName") or "").lower() == lowered
            or str(entry.get("hierarchyName") or "").lower() == lowered
        ]
        if named:
            candidates = named
    if not candidates and name:
        candidates = list(by_name.get(name.lower()) or [])

    # If candidates don't match preferred_table_id, check if by_name has a match on the preferred table.
    if preferred_table_id and name:
        preferred_candidates = [
            c for c in (by_name.get(name.lower()) or [])
            if str(c.get("tableId") or "") == str(preferred_table_id)
        ]
        if preferred_candidates:
            candidates = preferred_candidates + [
                c for c in candidates if str(c.get("tableId") or "") != str(preferred_table_id)
            ]
        elif candidates:
            # Sort matching table first
            candidates = sorted(
                candidates,
                key=lambda c: 0 if str(c.get("tableId") or "") == str(preferred_table_id) else 1,
            )

    resolved = dict(candidates[0]) if candidates else {}
    result: dict[str, Any] = {}
    if cid:
        result["id"] = cid
    elif resolved.get("id"):
        result["id"] = resolved["id"]
    if name:
        result["name"] = name
    elif resolved.get("name"):
        result["name"] = resolved["name"]
    if resolved:
        for key in (
            "kind",
            "columnName",
            "tableId",
            "tableName",
            "semanticType",
            "hierarchyName",
            "levelName",
            "formula",
            "aiContext",
        ):
            if key in resolved and resolved[key] not in (None, ""):
                result[key] = resolved[key]
    elif name:
        # Unresolved component — still expose id/name for selection prompts.
        result.setdefault("kind", "unknown")
    return result


def _cube_component_names(cube_info: list) -> List[str]:
    """Collect dimension and measure names used as topic-mapping components."""
    cube_info = normalize_cube_info(cube_info)
    components: List[str] = []
    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for dim in cube.get("dimensions") or []:
            if isinstance(dim, dict) and dim.get("dimensionName"):
                name = str(dim["dimensionName"])
                components.append(name)
                components.append(_component_alias(name))
        for measure in cube.get("measures") or []:
            if isinstance(measure, dict) and measure.get("measureName"):
                name = str(measure["measureName"])
                components.append(name)
                components.append(_component_alias(name))
    return list(dict.fromkeys(components))


def topic_mappings_from_domain(
    model_data: dict,
    cube_info: Optional[list] = None,
) -> List[dict]:
    """Build topic_mappings from domain topic component lists when present.

    Each topic keeps ``component`` (legacy name tokens for table matching) and
    ``components`` (resolved ``{id, name, kind, ...}`` objects). Components may
    resolve to a dimension, measure, hierarchy level, or blank-column measure.
    """
    if cube_info is None:
        cube_info = model_data.get("cube_info") or model_data.get("cube") or []
    cube_info = normalize_cube_info(cube_info)
    by_id, by_name = build_cube_component_index(cube_info)

    mappings: List[dict] = []
    for entry in model_data.get("domain") or []:
        if not isinstance(entry, dict):
            continue
        domain_name = entry.get("domain_name")
        for topic in entry.get("topics") or []:
            if not isinstance(topic, dict):
                continue
            topic_name = _topic_name(topic)
            if not topic_name:
                continue

            # First pass: identify the dominant table for this topic to resolve ambiguous level names.
            table_counts: Dict[str, int] = {}
            for comp in topic.get("components") or []:
                if not isinstance(comp, dict):
                    continue
                c_id = str(comp.get("id") or "").strip()
                c_name = str(comp.get("name") or "").strip().lower()
                c_candidates = (by_id.get(c_id) or []) if c_id else (by_name.get(c_name) or [])
                for cand in c_candidates:
                    t_id = str(cand.get("tableId") or "").strip()
                    if t_id:
                        table_counts[t_id] = table_counts.get(t_id, 0) + 1
            dominant_table_id = max(table_counts.items(), key=lambda x: x[1])[0] if table_counts else None

            legacy_components: List[str] = []
            rich_components: List[dict] = []
            for component in topic.get("components") or []:
                if not isinstance(component, dict):
                    continue
                resolved = resolve_topic_component(
                    component, by_id, by_name, preferred_table_id=dominant_table_id
                )
                if not resolved.get("name") and not resolved.get("id"):
                    continue
                rich_components.append(resolved)
                name = resolved.get("name")
                if name:
                    legacy_components.append(str(name))
                    legacy_components.append(_component_alias(name))
            if not rich_components and not legacy_components:
                continue
            mapping: dict[str, Any] = {
                "topic_name": topic_name,
                "component": list(dict.fromkeys(legacy_components)),
                "components": rich_components,
            }
            if domain_name:
                mapping["domain_name"] = domain_name
            description = str(topic.get("description") or "").strip()
            if description:
                mapping["description"] = description
            mappings.append(mapping)
    return mappings


def topic_mappings_from_cube_info(cube_info: list, topics: List[str]) -> List[dict]:
    """Build topic_mappings so topics resolve to cube dimensions and measures."""
    cube_info = normalize_cube_info(cube_info)
    by_id, by_name = build_cube_component_index(cube_info)
    rich_components: List[dict] = []
    seen: Set[str] = set()
    for records in list(by_id.values()) + list(by_name.values()):
        for record in records:
            key = f"{record.get('kind')}:{record.get('id')}:{record.get('name')}"
            if key in seen:
                continue
            seen.add(key)
            rich_components.append(record)
    components = _cube_component_names(cube_info)
    if not topics or (not components and not rich_components):
        return []
    return [
        {
            "topic_name": topic,
            "component": components,
            "components": rich_components,
        }
        for topic in topics
        if topic
    ]


def format_topic_mappings_for_prompt(topic_mappings: Optional[list]) -> str:
    """Render domain topic → component id/name/kind mappings for selection prompts."""
    if not topic_mappings:
        return ""
    lines = [
        "Domain topics and mapped components "
        "(id links to dimension, measure, hierarchy level, or blank-column measure):"
    ]
    for entry in topic_mappings:
        if not isinstance(entry, dict):
            continue
        topic_name = entry.get("topic_name")
        if not topic_name or not _topic_mapping_has_components(entry):
            continue
        header_parts = [f"Topic: {topic_name}"]
        if entry.get("domain_name"):
            header_parts.append(f"domain: {entry['domain_name']}")
        if entry.get("description"):
            header_parts.append(f"description: {entry['description']}")
        lines.append("- " + "; ".join(header_parts))
        rich = entry.get("components")
        if isinstance(rich, list) and rich:
            for component in rich:
                if not isinstance(component, dict):
                    continue
                parts: List[str] = []
                name = component.get("name")
                if name:
                    parts.append(str(name))
                if component.get("id") not in (None, ""):
                    parts.append(f"id: {component['id']}")
                if component.get("kind"):
                    parts.append(f"kind: {component['kind']}")
                if component.get("columnName"):
                    parts.append(f"column: {component['columnName']}")
                if component.get("hierarchyName"):
                    parts.append(f"hierarchy: {component['hierarchyName']}")
                if component.get("levelName"):
                    parts.append(f"level: {component['levelName']}")
                if component.get("formula"):
                    parts.append(f"formula: {component['formula']}")
                if component.get("semanticType"):
                    parts.append(f"type: {component['semanticType']}")
                ai = component.get("aiContext")
                if isinstance(ai, dict) and ai:
                    ai_bits = []
                    for key in ("instructions", "synonyms", "examples"):
                        value = ai.get(key)
                        if value not in (None, ""):
                            ai_bits.append(f"{key}: {value}")
                    if ai_bits:
                        parts.append(f"aiContext: {{{'; '.join(ai_bits)}}}")
                if parts:
                    lines.append("  - " + "; ".join(parts))
        else:
            for token in entry.get("component") or []:
                name = _strip_component_alias(token)
                if name:
                    lines.append(f"  - {name}")
    return "\n".join(lines) if len(lines) > 1 else ""


def format_topic_mappings_compact(topic_mappings: Optional[list]) -> str:
    """One-line-per-topic mapping for FindTables (names + owning table when known)."""
    if not topic_mappings:
        return ""
    lines = [
        "Topics → components (prefer tables that own these; names only):",
    ]
    for entry in topic_mappings:
        if not isinstance(entry, dict):
            continue
        topic_name = entry.get("topic_name")
        if not topic_name:
            continue
        names: List[str] = []
        rich = entry.get("components")
        if isinstance(rich, list) and rich:
            for component in rich:
                if not isinstance(component, dict):
                    continue
                name = str(component.get("name") or "").strip()
                if not name:
                    continue
                column_ref = str(component.get("columnName") or "").strip()
                table_hint = ""
                if "." in column_ref:
                    table_hint = column_ref.rsplit(".", 1)[0].strip().strip('"').strip("`")
                if table_hint:
                    names.append(f"{name}@{table_hint}")
                else:
                    names.append(name)
        else:
            for token in entry.get("component") or []:
                name = _strip_component_alias(token)
                if name:
                    names.append(name)
        if names:
            lines.append(f"- {topic_name}: {', '.join(names)}")
        else:
            lines.append(f"- {topic_name}")
    return "\n".join(lines) if len(lines) > 1 else ""


def domain_context_from_model(model_data: dict, topic_mappings: Optional[list] = None) -> str:
    """Format domain descriptions for SQL and visualization prompts."""
    lines: List[str] = []
    mappings_by_topic = {
        entry.get("topic_name"): entry
        for entry in (topic_mappings or [])
        if isinstance(entry, dict) and entry.get("topic_name")
    }
    for entry in model_data.get("domain") or []:
        if not isinstance(entry, dict):
            continue
        domain_name = entry.get("domain_name")
        description = entry.get("description")
        topics = entry.get("topics") or []
        if not domain_name and not description and not topics:
            continue
        parts = [f"Domain: {domain_name}"] if domain_name else []
        if description:
            parts.append(f"description: {description}")
        topic_labels: List[str] = []
        for topic in topics:
            if isinstance(topic, dict):
                name = _topic_name(topic)
                if not name:
                    continue
                topic_desc = str(topic.get("description") or "").strip()
                mapping = mappings_by_topic.get(name) or {}
                rich = mapping.get("components") if isinstance(mapping, dict) else None
                if isinstance(rich, list) and rich:
                    component_labels = []
                    for component in rich:
                        if not isinstance(component, dict):
                            continue
                        label = str(component.get("name") or component.get("id") or "")
                        extras = []
                        if component.get("id") not in (None, ""):
                            extras.append(f"id={component['id']}")
                        if component.get("kind"):
                            extras.append(str(component["kind"]))
                        if extras:
                            label = f"{label} [{', '.join(extras)}]"
                        if label:
                            component_labels.append(label)
                else:
                    component_labels = [
                        str(component.get("name"))
                        for component in (topic.get("components") or [])
                        if isinstance(component, dict) and component.get("name")
                    ]
                label = name
                extras: List[str] = []
                if topic_desc:
                    extras.append(topic_desc)
                if component_labels:
                    extras.append(f"components: {', '.join(component_labels)}")
                if extras:
                    label = f"{name} ({'; '.join(extras)})"
                topic_labels.append(label)
            else:
                name = _topic_name(topic)
                if name:
                    topic_labels.append(name)
        if topic_labels:
            parts.append(f"topics: {', '.join(topic_labels)}")
        lines.append("; ".join(parts))

    topic_prompt = format_topic_mappings_for_prompt(
        topic_mappings if topic_mappings is not None else model_data.get("topic_mappings")
    )
    if topic_prompt:
        lines.append(topic_prompt)
    return "\n".join(lines)


def filter_topic_mappings_for_selection(
    topic_mappings: Optional[list],
    topics: Optional[list],
) -> List[dict]:
    """Return topic mappings limited to the selected topic names."""
    selected = {
        str(topic).strip()
        for topic in (topics or [])
        if topic and str(topic).strip()
    }
    if not selected:
        return [entry for entry in (topic_mappings or []) if isinstance(entry, dict)]
    return [
        entry
        for entry in (topic_mappings or [])
        if isinstance(entry, dict) and entry.get("topic_name") in selected
    ]


def filter_topic_mappings_for_picked(
    topic_mappings: Optional[list],
    picked_names: Optional[List[str]],
) -> List[dict]:
    """Keep topics whose picked dimensions/measures are used in the query.

    Unused semantic-model dimensions and measures are dropped so the Semantic
    section matches the fields used in generated SQL. Topics that have no
    remaining components after that filter are omitted entirely.
    """
    allowed = {
        str(name).strip().lower()
        for name in (picked_names or [])
        if name and str(name).strip()
    }
    if not allowed:
        return [entry for entry in (topic_mappings or []) if isinstance(entry, dict)]

    filtered: List[dict] = []
    for entry in topic_mappings or []:
        if not isinstance(entry, dict):
            continue
        narrowed = dict(entry)
        rich = entry.get("components")
        if isinstance(rich, list) and rich:
            narrowed["components"] = [
                component
                for component in rich
                if isinstance(component, dict)
                and str(component.get("name") or "").strip().lower() in allowed
            ]
        legacy = entry.get("component")
        if isinstance(legacy, list) and legacy:
            narrowed["component"] = [
                token
                for token in legacy
                if str(_strip_component_alias(token) or "").strip().lower() in allowed
            ]
        if not _topic_mapping_has_components(narrowed):
            continue
        filtered.append(narrowed)
    return filtered


def _names_from_picked_mappings(
    topic_mappings: Optional[list],
    selected_names: Optional[list],
    key: str,
) -> List[str]:
    """Preserve selection order for mapping field values that are still present."""
    used: List[str] = []
    seen: Set[str] = set()
    for entry in topic_mappings or []:
        if not isinstance(entry, dict):
            continue
        name = str(entry.get(key) or "").strip()
        lowered = name.lower()
        if not name or lowered in seen:
            continue
        seen.add(lowered)
        used.append(name)
    selected = [
        str(item).strip()
        for item in (selected_names or [])
        if item and str(item).strip()
    ]
    if not used:
        return selected
    if not selected:
        return used
    used_lookup = {name.lower() for name in used}
    return [name for name in selected if name.lower() in used_lookup]


def topics_from_picked_mappings(
    topic_mappings: Optional[list],
    selected_topics: Optional[list] = None,
) -> List[str]:
    """Topic names that still have picked components, preserving selection order."""
    return _names_from_picked_mappings(topic_mappings, selected_topics, "topic_name")


def domains_from_picked_mappings(
    topic_mappings: Optional[list],
    selected_domains: Optional[list] = None,
) -> List[str]:
    """Domain names whose remaining topics still have picked components."""
    return _names_from_picked_mappings(topic_mappings, selected_domains, "domain_name")


def _selected_domain_description(model_data: Optional[dict], selected_domains: List[str]) -> str:
    """One domain sentence. Component catalogs are already in the schema."""
    allowed = {name.lower() for name in selected_domains}
    for entry in (model_data or {}).get("domain") or []:
        if not isinstance(entry, dict):
            continue
        domain_name = str(entry.get("domain_name") or "").strip()
        if allowed and domain_name.lower() not in allowed:
            continue
        description = str(entry.get("description") or "").strip()
        if description:
            return description
    return ""


def _format_sql_topics(topic_mappings: Optional[list]) -> str:
    """Topic name, one description, and component names. No ids or column paths."""
    lines: List[str] = []
    for entry in topic_mappings or []:
        if not isinstance(entry, dict):
            continue
        topic_name = str(entry.get("topic_name") or "").strip()
        if not topic_name:
            continue
        description = str(entry.get("description") or "").strip()
        header = f"Topic: {topic_name}"
        if description:
            header = f"{header}. {description}"
        lines.append(header)
        names: List[str] = []
        rich = entry.get("components")
        if isinstance(rich, list) and rich:
            for component in rich:
                if not isinstance(component, dict):
                    continue
                name = str(component.get("name") or "").strip()
                if name:
                    names.append(name)
        else:
            for token in entry.get("component") or []:
                name = _strip_component_alias(token)
                if name:
                    names.append(str(name))
        if names:
            lines.append(", ".join(names))
    return "\n".join(lines)


def filter_domain_context_for_sql(
    model_data: Optional[dict] = None,
    *,
    domain: Optional[list] = None,
    topics: Optional[list] = None,
    topic_mappings: Optional[list] = None,
    domain_context: str = "",
) -> str:
    """Build domain/topic context for final SQL from the filtered selection."""
    selected_domains = [
        str(item).strip()
        for item in (domain or [])
        if item and str(item).strip()
    ]
    selected_topics = [
        str(item).strip()
        for item in (topics or [])
        if item and str(item).strip()
    ]
    mappings = filter_topic_mappings_for_selection(topic_mappings, selected_topics)
    mappings = [entry for entry in mappings if _topic_mapping_has_components(entry)]
    # Semantic lists only topics/domains that still have used dims/metrics.
    selected_topics = topics_from_picked_mappings(mappings, selected_topics)
    selected_domains = domains_from_picked_mappings(mappings, selected_domains)

    parts: List[str] = []
    if selected_domains:
        parts.append("Domain: " + ", ".join(selected_domains))
    if selected_topics:
        parts.append("topics: " + ", ".join(selected_topics))
    description = _selected_domain_description(model_data, selected_domains)
    if description:
        parts.append(description)
    topic_lines = _format_sql_topics(mappings)
    if topic_lines:
        parts.append(topic_lines)
    if parts:
        return "\n".join(parts)
    return str(domain_context or "").strip()
