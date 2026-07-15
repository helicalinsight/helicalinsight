"""Detection and conversion helpers for cube_info agent semantic layers."""

from __future__ import annotations

import re
from typing import Any, Dict, List, Optional, Set, Tuple

from helicalbi.common.JsonToPara import (
    enrich_cube_metadata_with_aliases,
    resolve_column_alias,
    resolve_table_alias,
)


def is_cube_info_agent(agent_data: dict) -> bool:
    """Return True when the agent file uses the cube_info semantic structure."""
    if not isinstance(agent_data, dict):
        return False

    cube_info = agent_data.get("cube_info")
    if not isinstance(cube_info, list) or not cube_info:
        return False

    for cube in cube_info:
        if not isinstance(cube, dict):
            continue
        for key in ("dimensions", "measures"):
            items = cube.get(key)
            if not isinstance(items, list) or not items:
                continue
            for item in items:
                if not isinstance(item, dict):
                    continue
                if item.get("columnName") or item.get("dimensionName") or item.get("measureName"):
                    return True
    return False


def extract_domain_topics(agent_data: dict) -> Tuple[List[str], List[str]]:
    """Collect domain names and topics from the cube_info agent domain block."""
    domains: List[str] = []
    topics: List[str] = []
    for entry in agent_data.get("domain") or []:
        if not isinstance(entry, dict):
            continue
        domain_name = entry.get("domain_name")
        if domain_name:
            domains.append(str(domain_name))
        for topic in entry.get("topics") or []:
            if topic:
                topics.append(str(topic))
    return domains, list(dict.fromkeys(topics))


def build_column_to_table_map(metadata_response: dict) -> Dict[str, str]:
    """Map physical column names to table names from the metadata API response."""
    column_to_table: Dict[str, str] = {}
    tables = (metadata_response or {}).get("tables") or {}

    for table_name, table in tables.items():
        if not table_name or not isinstance(table, dict):
            continue
        for col_name in (table.get("columns") or {}):
            if col_name:
                column_to_table[str(col_name)] = table_name

    return column_to_table


def build_metadata_id_maps(
    metadata_response: dict,
) -> Tuple[Dict[str, str], Dict[str, Tuple[str, str]]]:
    """Map table/column IDs from the metadata API to physical names."""
    table_id_to_name: Dict[str, str] = {}
    column_id_to_ref: Dict[str, Tuple[str, str]] = {}
    tables = (metadata_response or {}).get("tables") or {}

    for table_name, table in tables.items():
        if not table_name or not isinstance(table, dict):
            continue
        table_id = table.get("id")
        if table_id:
            table_id_to_name[str(table_id)] = table_name
        for col_name, col_meta in (table.get("columns") or {}).items():
            if not col_name:
                continue
            col_id = col_meta.get("id") if isinstance(col_meta, dict) else None
            if col_id:
                column_id_to_ref[str(col_id)] = (table_name, str(col_name))

    return table_id_to_name, column_id_to_ref


def _parse_column_reference(column_name: str) -> Tuple[Optional[str], str]:
    """Split ``table.column`` references; bare column names return ``(None, column)``."""
    column_name = str(column_name).strip()
    if "." in column_name:
        table_name, col_name = column_name.rsplit(".", 1)
        table_name = table_name.strip()
        col_name = col_name.strip()
        if table_name and col_name:
            return table_name, col_name
    return None, column_name


def _default_function_from_cube_item(item: dict, metadata_response: Optional[dict] = None) -> str:
    for key in ("defaultFunction", "default_function"):
        value = item.get(key)
        if value:
            return str(value)

    table_name = item.get("_resolved_table")
    column_name = item.get("_resolved_column")
    if table_name and column_name and metadata_response:
        table = ((metadata_response or {}).get("tables") or {}).get(table_name) or {}
        col_meta = ((table.get("columns") or {}).get(column_name) or {}) if isinstance(table, dict) else {}
        if isinstance(col_meta, dict):
            for key in ("defaultFunction", "default_function"):
                value = col_meta.get(key)
                if value:
                    return str(value)
    return ""


def _resolve_table_name(
    item: dict,
    column_to_table: Dict[str, str],
    table_id_to_name: Optional[Dict[str, str]] = None,
    column_id_to_ref: Optional[Dict[str, Tuple[str, str]]] = None,
) -> Optional[str]:
    column_name = item.get("columnName")
    if column_name:
        table_name, bare_column = _parse_column_reference(str(column_name))
        if table_name:
            return table_name
        resolved = column_to_table.get(bare_column)
        if resolved:
            return resolved

    table_id = item.get("tableId")
    if table_id and table_id_to_name:
        resolved = table_id_to_name.get(str(table_id))
        if resolved:
            return resolved

    column_id = item.get("columnId")
    if column_id and column_id_to_ref:
        ref = column_id_to_ref.get(str(column_id))
        if ref:
            return ref[0]

    return None


def _resolve_column_name(
    item: dict,
    column_id_to_ref: Optional[Dict[str, Tuple[str, str]]] = None,
) -> Optional[str]:
    column_name = item.get("columnName")
    if column_name:
        _, bare_column = _parse_column_reference(str(column_name))
        return bare_column

    column_id = item.get("columnId")
    if column_id and column_id_to_ref:
        ref = column_id_to_ref.get(str(column_id))
        if ref:
            return ref[1]

    return None


_FORMULA_TABLE_REF = re.compile(r"([A-Za-z_][A-Za-z0-9_]*)\s*\.\s*[A-Za-z_][A-Za-z0-9_]*")


def _known_table_names(metadata_response: Optional[dict]) -> Set[str]:
    """Collect physical table names available in the metadata API response."""
    return {
        str(name)
        for name in ((metadata_response or {}).get("tables") or {})
        if name
    }


def _formula_from_cube_item(item: dict) -> str:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    return str(item.get("formula") or metric_obj.get("formula") or "")


def _table_from_formula(formula: str, known_tables: Set[str]) -> Optional[str]:
    """Infer a table name from qualified ``table.column`` refs inside a formula."""
    matches = _FORMULA_TABLE_REF.findall(formula or "")
    for token in matches:
        if token in known_tables:
            return token
    return matches[0] if matches else None


def cube_info_to_cube_metadata(
    cube_info: list,
    metadata_response: dict,
    domain_topics: Optional[List[str]] = None,
) -> List[dict]:
    """Convert cube_info entries into legacy cube_metadata for the SQL pipeline."""
    column_to_table = build_column_to_table_map(metadata_response)
    table_id_to_name, column_id_to_ref = build_metadata_id_maps(metadata_response)
    known_tables = _known_table_names(metadata_response)
    tables: Dict[str, dict] = {}
    domain_topics = domain_topics or []

    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue

        cube_name = cube.get("cubeName") or ""
        dimension_names = [
            str(dim.get("dimensionName"))
            for dim in (cube.get("dimensions") or [])
            if isinstance(dim, dict) and dim.get("dimensionName")
        ]
        measure_names = [
            str(measure.get("measureName"))
            for measure in (cube.get("measures") or [])
            if isinstance(measure, dict) and measure.get("measureName")
        ]
        component_names = list(dict.fromkeys(dimension_names + measure_names))

        for dim in cube.get("dimensions") or []:
            if not isinstance(dim, dict):
                continue
            table_name = _resolve_table_name(
                dim, column_to_table, table_id_to_name, column_id_to_ref
            )
            column_name = _resolve_column_name(dim, column_id_to_ref)
            if not table_name or not column_name:
                continue

            entry = tables.setdefault(
                table_name,
                {
                    "database_table": table_name,
                    "table_alias": resolve_table_alias(table_name, metadata_response),
                    "dimension_name": component_names or domain_topics,
                    "description": cube_name,
                    "columns": [],
                    "measures": [],
                },
            )
            dim["_resolved_table"] = table_name
            dim["_resolved_column"] = column_name
            preferred_alias = dim.get("dimensionName")
            entry["columns"].append(
                {
                    "column_name": column_name,
                    # Prefer dimension name; fall back to metadata alias when absent.
                    "alias_name": str(preferred_alias)
                    if preferred_alias
                    else resolve_column_alias(table_name, column_name, metadata_response),
                    "description": dim.get("description") or dim.get("dimensionName") or "",
                    "semantic_type": dim.get("semanticType"),
                    "synonyms": dim.get("synonyms") or [],
                    "default_function": _default_function_from_cube_item(dim, metadata_response) or None,
                }
            )

        for measure in cube.get("measures") or []:
            if not isinstance(measure, dict):
                continue
            table_name = _resolve_table_name(
                measure, column_to_table, table_id_to_name, column_id_to_ref
            )
            column_name = _resolve_column_name(measure, column_id_to_ref)

            # Formula-only measures leave columnName/columnId blank. Expose the
            # measure name itself as a selectable computed column so the SQL
            # pipeline can pick it up (heading = measureName, carrying formula).
            computed = False
            if not column_name:
                formula = _formula_from_cube_item(measure)
                measure_label = measure.get("measureName")
                if not formula or not measure_label:
                    continue
                if not table_name:
                    table_name = _table_from_formula(formula, known_tables)
                if not table_name:
                    continue
                column_name = str(measure_label)
                computed = True

            if not table_name or not column_name:
                continue

            entry = tables.setdefault(
                table_name,
                {
                    "database_table": table_name,
                    "table_alias": resolve_table_alias(table_name, metadata_response),
                    "dimension_name": component_names or domain_topics,
                    "description": cube_name,
                    "columns": [],
                    "measures": [],
                },
            )
            measure["_resolved_table"] = table_name
            if not computed:
                measure["_resolved_column"] = column_name
            preferred_alias = measure.get("measureName")
            measure_entry = {
                "column_name": column_name,
                # Prefer measure name; fall back to metadata alias when absent.
                "alias_name": str(preferred_alias)
                if preferred_alias
                else resolve_column_alias(table_name, column_name, metadata_response),
                "aggregator": _aggregator_from_cube_item(measure),
                "description": measure.get("description") or measure.get("measureName") or "",
                "semantic_type": measure.get("semanticType"),
                "format_string": _format_string_from_cube_item(measure) or None,
                "default_function": _default_function_from_cube_item(measure, metadata_response) or None,
                "metric": measure.get("metric") or {},
            }
            if computed:
                # Not a physical column; carries a formula the LLM must expand.
                measure_entry["is_computed"] = True
                measure_entry["formula"] = _formula_from_cube_item(measure)
            entry["measures"].append(measure_entry)

    return list(tables.values())


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


def _metric_name_from_cube_item(item: dict, primary_name_key: str) -> str:
    metric_obj = item.get("metric")
    if isinstance(metric_obj, dict):
        for key in ("metric", "name", "id"):
            value = metric_obj.get(key)
            if value:
                return str(value)
    primary = item.get(primary_name_key)
    if primary:
        return str(primary)
    column_name = item.get("columnName")
    if column_name:
        return str(column_name)
    return ""


def _description_from_cube_item(item: dict, primary_name_key: str) -> str:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    description = (
        item.get("description")
        or metric_obj.get("description")
        or item.get(primary_name_key)
        or ""
    )
    aggregator = _aggregator_from_cube_item(item)
    if aggregator and description:
        return f"{description} (aggregation: {aggregator})"
    if aggregator:
        return f"{_metric_name_from_cube_item(item, primary_name_key)} (aggregation: {aggregator})"
    return str(description)


def _cube_item_has_formula_or_filter(item: dict) -> bool:
    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    for field in ("formula", "filter"):
        if item.get(field) or metric_obj.get(field):
            return True
    return False


def _business_metric_from_cube_item(
    item: dict,
    table_name: str,
    primary_name_key: str,
    label_key: str,
    metadata_response: Optional[dict] = None,
    column_id_to_ref: Optional[Dict[str, Tuple[str, str]]] = None,
) -> Optional[dict]:
    metric_name = _metric_name_from_cube_item(item, primary_name_key)
    if not table_name or not metric_name:
        return None

    entry: dict[str, Any] = {
        "metric": metric_name,
        "description": _description_from_cube_item(item, primary_name_key),
        "tables": [table_name],
    }
    if metadata_response:
        entry["table_alias"] = resolve_table_alias(table_name, metadata_response)
    column_name = _resolve_column_name(item, column_id_to_ref)
    if column_name:
        entry["column_name"] = column_name
        preferred_alias = item.get(primary_name_key)
        if preferred_alias:
            entry["column_alias"] = str(preferred_alias)
        elif metadata_response:
            entry["column_alias"] = resolve_column_alias(
                table_name,
                column_name,
                metadata_response,
            )
    if item.get(primary_name_key):
        entry[label_key] = item[primary_name_key]
    aggregator = _aggregator_from_cube_item(item)
    if aggregator:
        entry["aggregator"] = aggregator
    format_string = _format_string_from_cube_item(item)
    if format_string:
        entry["format_string"] = format_string
    default_function = _default_function_from_cube_item(item, metadata_response)
    if default_function:
        entry["default_function"] = default_function

    metric_obj = item.get("metric") if isinstance(item.get("metric"), dict) else {}
    for field in ("formula", "filter"):
        value = item.get(field) or metric_obj.get(field)
        if value:
            entry[field] = value
    return entry


def _append_business_metric(
    metrics: List[dict],
    seen: set[tuple[str, str]],
    entry: Optional[dict],
) -> None:
    if not entry:
        return
    dedupe_key = (entry["metric"], entry["tables"][0])
    if dedupe_key in seen:
        return
    seen.add(dedupe_key)
    metrics.append(entry)


def business_metrics_from_cube_info(cube_info: list, metadata_response: dict) -> List[dict]:
    """Build legacy business_metrics entries from cube_info measures and dimension formulas."""
    column_to_table = build_column_to_table_map(metadata_response)
    table_id_to_name, column_id_to_ref = build_metadata_id_maps(metadata_response)
    known_tables = _known_table_names(metadata_response)
    metrics: List[dict] = []
    seen: set[tuple[str, str]] = set()

    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue

        for measure in cube.get("measures") or []:
            if not isinstance(measure, dict):
                continue
            table_name = _resolve_table_name(
                measure, column_to_table, table_id_to_name, column_id_to_ref
            )
            if not table_name:
                # Formula-only measure: infer the table from the formula so the
                # metric (and its formula) still reaches the SQL prompt.
                table_name = _table_from_formula(
                    _formula_from_cube_item(measure), known_tables
                )
            entry = _business_metric_from_cube_item(
                measure,
                table_name or "",
                "measureName",
                "measure_name",
                metadata_response,
                column_id_to_ref,
            )
            _append_business_metric(metrics, seen, entry)

        for dim in cube.get("dimensions") or []:
            if not isinstance(dim, dict) or not _cube_item_has_formula_or_filter(dim):
                continue
            table_name = _resolve_table_name(
                dim, column_to_table, table_id_to_name, column_id_to_ref
            )
            entry = _business_metric_from_cube_item(
                dim,
                table_name or "",
                "dimensionName",
                "dimension_name",
                metadata_response,
                column_id_to_ref,
            )
            _append_business_metric(metrics, seen, entry)

    return metrics


def merge_business_metrics(*sources: list) -> List[dict]:
    """Merge business metric lists while dropping exact duplicates."""
    merged: List[dict] = []
    seen: set[tuple[Any, tuple]] = set()

    for source in sources:
        for item in source or []:
            if not isinstance(item, dict):
                continue
            key = (item.get("metric"), tuple(item.get("tables") or []))
            if key in seen:
                continue
            seen.add(key)
            merged.append(item)

    return merged


def _cube_component_names(cube_info: list) -> List[str]:
    """Collect dimension and measure names used as topic-mapping components."""
    components: List[str] = []
    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for dim in cube.get("dimensions") or []:
            if isinstance(dim, dict) and dim.get("dimensionName"):
                components.append(" alias:-"+str(dim["dimensionName"]))
        for measure in cube.get("measures") or []:
            if isinstance(measure, dict) and measure.get("measureName"):
                components.append(" alias:-"+str(measure["measureName"]))
    return list(dict.fromkeys(components))


def topic_mappings_from_cube_info(cube_info: list, topics: List[str]) -> List[dict]:
    """Build topic_mappings so topics resolve to cube dimensions and measures."""
    components = _cube_component_names(cube_info)
    if not topics or not components:
        return []
    return [
        {"topic_name": topic, "component": components}
        for topic in topics
        if topic
    ]


def synonyms_from_cube_info(cube_info: list, metadata_response: dict) -> List[dict]:
    """Build legacy synonym entries from cube_info dimension synonyms."""
    column_to_table = build_column_to_table_map(metadata_response)
    table_id_to_name, column_id_to_ref = build_metadata_id_maps(metadata_response)
    synonyms: List[dict] = []
    seen: set[tuple[str, str]] = set()

    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for dim in cube.get("dimensions") or []:
            if not isinstance(dim, dict):
                continue
            dim_synonyms = dim.get("synonyms") or []
            if not dim_synonyms:
                continue
            table_name = _resolve_table_name(
                dim, column_to_table, table_id_to_name, column_id_to_ref
            )
            if not table_name:
                continue
            dedupe_key = (table_name, str(dim.get("dimensionName") or ""))
            if dedupe_key in seen:
                continue
            seen.add(dedupe_key)
            synonyms.append(
                {
                    "database_table": table_name,
                    "dimension_name": dim.get("dimensionName") or "",
                    "synonyms": [str(item) for item in dim_synonyms if item],
                }
            )
    return synonyms


def domain_context_from_agent(agent_data: dict) -> str:
    """Format domain descriptions for SQL-generation prompts."""
    lines: List[str] = []
    for entry in agent_data.get("domain") or []:
        if not isinstance(entry, dict):
            continue
        domain_name = entry.get("domain_name")
        description = entry.get("description")
        topics = entry.get("topics") or []
        if not domain_name and not description:
            continue
        parts = [f"Domain: {domain_name}"] if domain_name else []
        if description:
            parts.append(f"description: {description}")
        if topics:
            parts.append(f"topics: {', '.join(str(topic) for topic in topics)}")
        lines.append("; ".join(parts))
    return "\n".join(lines)


def prepare_cube_info_agent_data(agent_data: dict, metadata_response: dict) -> dict[str, Any]:
    """Return cube_metadata, domain/topics, and business_metrics for cube_info agents."""
    _, topics = extract_domain_topics(agent_data)
    cube_info = agent_data.get("cube_info") or []
    cube_metadata = enrich_cube_metadata_with_aliases(
        cube_info_to_cube_metadata(
            cube_info,
            metadata_response,
            domain_topics=topics,
        ),
        metadata_response,
    )
    domains, topics = extract_domain_topics(agent_data)
    derived_metrics = business_metrics_from_cube_info(cube_info, metadata_response)
    business_metrics = merge_business_metrics(agent_data.get("business_metrics"), derived_metrics)
    topic_mappings = agent_data.get("topic_mappings") or topic_mappings_from_cube_info(cube_info, topics)
    derived_synonyms = synonyms_from_cube_info(cube_info, metadata_response)
    synonyms = list(agent_data.get("synonyms") or [])
    existing_tables = {entry.get("database_table") for entry in synonyms if isinstance(entry, dict)}
    for entry in derived_synonyms:
        if entry.get("database_table") not in existing_tables:
            synonyms.append(entry)
    return {
        "cube_metadata": cube_metadata,
        "domain": domains,
        "topics": topics,
        "business_metrics": business_metrics,
        "topic_mappings": topic_mappings,
        "synonyms": synonyms,
        "domain_context": domain_context_from_agent(agent_data),
    }
