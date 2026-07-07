"""Detection and conversion helpers for cube_info agent semantic layers."""

from __future__ import annotations

from typing import Any, Dict, List, Optional, Tuple


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


def build_metadata_id_maps(
    metadata_response: dict,
) -> Tuple[Dict[str, str], Dict[str, Tuple[str, str]], Dict[str, str]]:
    """Build table-id and column-id lookup maps from a metadata API response."""
    table_by_id: Dict[str, str] = {}
    column_by_id: Dict[str, Tuple[str, str]] = {}
    column_to_table: Dict[str, str] = {}
    tables = (metadata_response or {}).get("tables") or {}

    for table_name, table in tables.items():
        if not table_name or not isinstance(table, dict):
            continue
        table_id = table.get("id") or table.get("tableId")
        if table_id is not None:
            table_by_id[str(table_id)] = table_name

        for col_name, col in (table.get("columns") or {}).items():
            if not col_name:
                continue
            column_to_table[str(col_name)] = table_name
            col_meta = col if isinstance(col, dict) else {}
            col_id = col_meta.get("id") or col_meta.get("columnId")
            if col_id is not None:
                column_by_id[str(col_id)] = (table_name, col_name)

    return table_by_id, column_by_id, column_to_table


def _resolve_table_name(
    item: dict,
    table_by_id: Dict[str, str],
    column_by_id: Dict[str, Tuple[str, str]],
    column_to_table: Dict[str, str],
) -> Optional[str]:
    table_id = item.get("tableId")
    if table_id is not None:
        resolved = table_by_id.get(str(table_id))
        if resolved:
            return resolved

    column_id = item.get("columnId")
    if column_id is not None:
        resolved = column_by_id.get(str(column_id))
        if resolved:
            return resolved[0]

    column_name = item.get("columnName")
    if column_name:
        return column_to_table.get(str(column_name))

    return None


def _resolve_column_name(
    item: dict,
    column_by_id: Dict[str, Tuple[str, str]],
) -> Optional[str]:
    column_name = item.get("columnName")
    if column_name:
        return str(column_name)

    column_id = item.get("columnId")
    if column_id is not None:
        resolved = column_by_id.get(str(column_id))
        if resolved:
            return resolved[1]
    return None


def cube_info_to_cube_metadata(
    cube_info: list,
    metadata_response: dict,
    domain_topics: Optional[List[str]] = None,
) -> List[dict]:
    """Convert cube_info entries into legacy cube_metadata for the SQL pipeline."""
    table_by_id, column_by_id, column_to_table = build_metadata_id_maps(metadata_response)
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

        for dim in cube.get("dimensions") or []:
            if not isinstance(dim, dict):
                continue
            table_name = _resolve_table_name(dim, table_by_id, column_by_id, column_to_table)
            column_name = _resolve_column_name(dim, column_by_id)
            if not table_name or not column_name:
                continue

            entry = tables.setdefault(
                table_name,
                {
                    "database_table": table_name,
                    "dimension_name": dimension_names or domain_topics,
                    "description": cube_name,
                    "columns": [],
                    "measures": [],
                },
            )
            entry["columns"].append(
                {
                    "column_name": column_name,
                    "description": dim.get("description") or dim.get("dimensionName") or "",
                    "semantic_type": dim.get("semanticType"),
                    "synonyms": dim.get("synonyms") or [],
                }
            )

        for measure in cube.get("measures") or []:
            if not isinstance(measure, dict):
                continue
            table_name = _resolve_table_name(measure, table_by_id, column_by_id, column_to_table)
            column_name = _resolve_column_name(measure, column_by_id)
            if not table_name or not column_name:
                continue

            entry = tables.setdefault(
                table_name,
                {
                    "database_table": table_name,
                    "dimension_name": dimension_names or domain_topics,
                    "description": cube_name,
                    "columns": [],
                    "measures": [],
                },
            )
            entry["measures"].append(
                {
                    "column_name": column_name,
                    "alias_name": measure.get("measureName") or column_name,
                    "aggregator": measure.get("aggregator") or "",
                    "description": measure.get("description") or measure.get("measureName") or "",
                    "semantic_type": measure.get("semanticType"),
                    "format_string": measure.get("formatString"),
                    "metric": measure.get("metric") or {},
                }
            )

    return list(tables.values())


def _metric_name_from_measure(measure: dict) -> str:
    metric_obj = measure.get("metric")
    if isinstance(metric_obj, dict):
        for key in ("metric", "name", "id"):
            value = metric_obj.get(key)
            if value:
                return str(value)
    if measure.get("measureName"):
        return str(measure["measureName"])
    if measure.get("columnName"):
        return str(measure["columnName"])
    return ""


def _description_from_measure(measure: dict) -> str:
    metric_obj = measure.get("metric") if isinstance(measure.get("metric"), dict) else {}
    description = (
        measure.get("description")
        or metric_obj.get("description")
        or measure.get("measureName")
        or ""
    )
    aggregator = measure.get("aggregator") or metric_obj.get("aggregator") or ""
    if aggregator and description:
        return f"{description} (aggregation: {aggregator})"
    if aggregator:
        return f"{_metric_name_from_measure(measure)} (aggregation: {aggregator})"
    return str(description)


def business_metrics_from_cube_info(cube_info: list, metadata_response: dict) -> List[dict]:
    """Build legacy business_metrics entries from cube_info measures."""
    table_by_id, column_by_id, column_to_table = build_metadata_id_maps(metadata_response)
    metrics: List[dict] = []
    seen: set[tuple[str, str]] = set()

    for cube in cube_info or []:
        if not isinstance(cube, dict):
            continue
        for measure in cube.get("measures") or []:
            if not isinstance(measure, dict):
                continue

            table_name = _resolve_table_name(measure, table_by_id, column_by_id, column_to_table)
            metric_name = _metric_name_from_measure(measure)
            if not table_name or not metric_name:
                continue

            dedupe_key = (metric_name, table_name)
            if dedupe_key in seen:
                continue
            seen.add(dedupe_key)

            entry: dict[str, Any] = {
                "metric": metric_name,
                "description": _description_from_measure(measure),
                "tables": [table_name],
            }
            column_name = _resolve_column_name(measure, column_by_id)
            if column_name:
                entry["column_name"] = column_name
            if measure.get("measureName"):
                entry["measure_name"] = measure["measureName"]
            aggregator = measure.get("aggregator")
            if aggregator:
                entry["aggregator"] = aggregator

            metric_obj = measure.get("metric") if isinstance(measure.get("metric"), dict) else {}
            for field in ("formula", "filter"):
                value = measure.get(field) or metric_obj.get(field)
                if value:
                    entry[field] = value

            metrics.append(entry)

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


def prepare_cube_info_agent_data(agent_data: dict, metadata_response: dict) -> dict[str, Any]:
    """Return cube_metadata, domain/topics, and business_metrics for cube_info agents."""
    _, topics = extract_domain_topics(agent_data)
    cube_metadata = cube_info_to_cube_metadata(
        agent_data.get("cube_info"),
        metadata_response,
        domain_topics=topics,
    )
    domains, topics = extract_domain_topics(agent_data)
    derived_metrics = business_metrics_from_cube_info(agent_data.get("cube_info"), metadata_response)
    business_metrics = merge_business_metrics(agent_data.get("business_metrics"), derived_metrics)
    return {
        "cube_metadata": cube_metadata,
        "domain": domains,
        "topics": topics,
        "business_metrics": business_metrics,
    }
