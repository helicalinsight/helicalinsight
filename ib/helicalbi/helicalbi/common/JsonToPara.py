from typing import List, Optional
import re


_SAFE_IDENTIFIER = re.compile(r"^[A-Za-z_][A-Za-z0-9_]*$")

SQL_IDENTIFIER_NOTE = (
    "Important: Table and column names containing spaces or special characters "
    'MUST be quoted in SQL (e.g. SELECT "Bill Qty" FROM LSC_Infra). '
    "Always use the exact names shown below."
)


def needs_quoting(identifier: str) -> bool:
    if not identifier:
        return False
    return not _SAFE_IDENTIFIER.match(identifier)


def quote_identifier(identifier: str) -> str:
    if not identifier:
        return identifier
    if not needs_quoting(identifier):
        return identifier
    escaped = str(identifier).replace('"', '""')
    return f'"{escaped}"'


def format_table_column_ref(table_name: str, column_name: str) -> str:
    return f"{quote_identifier(table_name)}.{quote_identifier(column_name)}"


def is_bare_minimum_config(agent_data: dict) -> bool:
    """True when semantic layer lacks topic mappings and supporting metadata."""
    if not agent_data:
        return True
    topic_mappings = agent_data.get("topic_mappings") or []
    if not topic_mappings:
        return True
    return False


def get_all_cube_tables(cube_metadata) -> List[str]:
    return list(dict.fromkeys(
        cube["database_table"]
        for cube in iter_cube_entries(cube_metadata)
    ))


def snake_to_words(key: str) -> str:
    """Convert snake_case to Word Case"""
    return key.replace("_", " ").title()


def describe_dict(d, prefix=""):
    """
    Recursively convert dict keys to descriptive lines.
    Each key becomes one line.
    """
    lines = []
    for k, v in d.items():
        key_name = snake_to_words(k)

        if isinstance(v, dict):
            lines.append(f"{prefix}{key_name}:")
            lines.extend(describe_dict(v, prefix + "  "))
        elif isinstance(v, list):
            if all(isinstance(i, dict) for i in v):
                lines.append(f"{prefix}{key_name}:")
                for idx, item in enumerate(v, 1):
                    lines.append(f"")
                    lines.extend(describe_dict(item, prefix + "    "))
            else:
                lines.append(f"{prefix}{key_name}: {', '.join(map(str, v))}")
        else:
            lines.append(f"{prefix}{key_name}: {v}")

    return lines


def convert_metadata_to_text(metadata_json):
    """
    Entry point for conversion
    """
    output = []
    for idx, block in enumerate(metadata_json, 1):
        output.append(f"Metadata Block {idx}:")
        output.extend(describe_dict(block, prefix="  "))
    return "\n".join(output)


def iter_cube_entries(cube_metadata):
    """Yield cube dicts that define at least a database_table."""
    if not isinstance(cube_metadata, list):
        return
    for cube in cube_metadata:
        if isinstance(cube, dict) and cube.get("database_table"):
            yield cube


def get_column_names(cube: dict) -> List[str]:
    """Return non-empty column_name values from a cube entry."""
    if not isinstance(cube, dict):
        return []
    return [
        col.get("column_name")
        for col in (cube.get("columns") or [])
        if isinstance(col, dict) and col.get("column_name")
    ]


def has_table_column_info(cube_metadata) -> bool:
    """True when at least one cube entry defines one or more column names."""
    return any(get_column_names(cube) for cube in iter_cube_entries(cube_metadata))


def resolve_table_alias(table_name: str, metadata_response: dict) -> str:
    table = ((metadata_response or {}).get("tables") or {}).get(table_name) or {}
    if isinstance(table, dict) and table.get("alias"):
        return str(table["alias"])
    return table_name


def resolve_column_alias(
    table_name: str,
    column_name: str,
    metadata_response: dict,
    fallback: Optional[str] = None,
) -> str:
    table = ((metadata_response or {}).get("tables") or {}).get(table_name) or {}
    col_meta: dict = {}
    if isinstance(table, dict):
        raw_col = (table.get("columns") or {}).get(column_name)
        col_meta = raw_col if isinstance(raw_col, dict) else {}
    if col_meta.get("alias"):
        return str(col_meta["alias"])
    if fallback:
        return str(fallback)
    return column_name


def enrich_cube_metadata_with_aliases(
    cube_metadata: list,
    metadata_response: dict,
) -> List[dict]:
    """Attach table/column aliases from the metadata API to cube_metadata entries."""
    enriched: List[dict] = []
    for cube in iter_cube_entries(cube_metadata):
        if not isinstance(cube, dict):
            continue
        table_name = cube.get("database_table")
        if not table_name:
            continue

        entry = dict(cube)
        entry["table_alias"] = resolve_table_alias(table_name, metadata_response)

        columns: List[dict] = []
        for col in cube.get("columns") or []:
            if not isinstance(col, dict):
                continue
            col_name = col.get("column_name")
            if not col_name:
                continue
            existing_alias = col.get("alias_name")
            columns.append(
                {
                    **col,
                    "alias_name": existing_alias
                    or resolve_column_alias(table_name, col_name, metadata_response),
                }
            )
        entry["columns"] = columns

        measures: List[dict] = []
        for measure in cube.get("measures") or []:
            if not isinstance(measure, dict):
                continue
            col_name = measure.get("column_name")
            if not col_name:
                continue
            existing_alias = measure.get("alias_name")
            measures.append(
                {
                    **measure,
                    "alias_name": existing_alias
                    or resolve_column_alias(table_name, col_name, metadata_response),
                }
            )
        entry["measures"] = measures
        enriched.append(entry)

    return enriched


def _format_semantic_column_line(
    table_name: str,
    col_name: str,
    alias_name: str,
    col_desc: str,
    semantic_type: Optional[str] = None,
    synonyms: Optional[List[str]] = None,
    default_function: Optional[str] = None,
    aggregator: Optional[str] = None,
    formula: Optional[str] = None,
) -> str:
    sql_ref = format_table_column_ref(table_name, col_name)
    detail_parts: List[str] = []
    if col_desc:
        detail_parts.append(f"Description: {col_desc}")
    if semantic_type:
        detail_parts.append(f"type: {semantic_type}")
    if aggregator:
        detail_parts.append(f"aggregator: {aggregator}")
    if formula:
        detail_parts.append(f"computed formula: {formula}")
    if default_function:
        detail_parts.append(f"function: {default_function}")
    if synonyms:
        detail_parts.append(f"synonyms: {', '.join(str(item) for item in synonyms if item)}")
    alias_suffix = f", alias: {alias_name}" if alias_name and alias_name != col_name else ""
    if alias_suffix:
        detail_parts.append(alias_suffix.strip(", "))
    desc_part = "; ".join(detail_parts)
    if needs_quoting(col_name) or needs_quoting(table_name):
        if desc_part:
            return f"  - {col_name} ({desc_part}) [SQL: {sql_ref}]"
        return f"  - {col_name} [SQL: {sql_ref}]"
    if desc_part:
        return f"  - {col_name} ({desc_part})"
    return f"  - {col_name}"


def cube_metadata_from_metadata_api(metadata_response: dict) -> List[dict]:
    """Build minimal cube_metadata entries from a metadata API response."""
    tables = (metadata_response or {}).get("tables") or {}
    semantic_tables: List[dict] = []
    for table_name, table in tables.items():
        if not table_name or not isinstance(table, dict):
            continue
        columns = [
            {
                "column_name": col_name,
                "alias_name": resolve_column_alias(table_name, col_name, metadata_response),
            }
            for col_name in (table.get("columns") or {})
            if col_name
        ]
        semantic_tables.append({
            "database_table": table_name,
            "table_alias": resolve_table_alias(table_name, metadata_response),
            "columns": columns,
        })
    return semantic_tables


def prevalidate_cube_metadata(cube_metadata, metadata_api_response) -> List[dict]:
    """Use agent cube_metadata when it has schema; otherwise fall back to metadata API."""
    cube_metadata = cube_metadata or []
    if has_table_column_info(cube_metadata):
        return enrich_cube_metadata_with_aliases(cube_metadata, metadata_api_response)
    fallback = cube_metadata_from_metadata_api(metadata_api_response)
    return fallback or enrich_cube_metadata_with_aliases(cube_metadata, metadata_api_response)


def get_primary_key_column(cube: dict) -> Optional[str]:
    """Resolve join/PK column from unique_identifier_of_table or first column_name."""
    if not isinstance(cube, dict):
        return None
    uid = cube.get("unique_identifier_of_table")
    if uid and str(uid).strip():
        return str(uid).strip()
    names = get_column_names(cube)
    return names[0] if names else None


def generate_bare_minimum_context(user_query: Optional[str], cube_metadata) -> str:
    """Build full schema + user query context for bare-minimum agent configuration."""
    cubes = list(iter_cube_entries(cube_metadata))
    lines = [
        "Schema mode: bare minimum — use ONLY the tables and columns listed below.",
        SQL_IDENTIFIER_NOTE,
    ]
    if user_query:
        lines.append(f"\nUser Query:\n{user_query}")
    lines.append("\nTables and Columns:")
    lines.append(generate_semantic_hint(cubes))
    return "\n".join(lines)


def generate_semantic_hint(metadata_json: list) -> str:
    """
    Convert metadata JSON into LLM-friendly semantic hint string
    """

    lines = []

    #lines.append("DATABASE SEMANTIC MODEL")
    #lines.append("=" * 50)

    for table in iter_cube_entries(metadata_json):
        table_name = table.get("database_table", "")
        table_alias = table.get("table_alias") or table_name
        description = table.get("description", "")
        seen_column_names: set[str] = set()

        table_label = table_name
        if table_alias and table_alias != table_name:
            table_label = f"{table_name} (alias: {table_alias})"
        if description:
            lines.append(f"==========================\nTable: {table_label} ({description})")
        else:
            lines.append(f"==========================\nTable: {table_label}")

        lines.append("\nColumns:")

        for column in table.get("columns") or []:
            if not isinstance(column, dict):
                continue
            col_name = column.get("column_name")
            if not col_name:
                continue
            seen_column_names.add(str(col_name))
            lines.append(
                _format_semantic_column_line(
                    table_name,
                    col_name,
                    column.get("alias_name") or col_name,
                    column.get("description", "") or "",
                    semantic_type=column.get("semantic_type"),
                    synonyms=column.get("synonyms") or [],
                    default_function=column.get("default_function"),
                )
            )

        # Include physical measure columns as selectable SQL columns in prompts.
        for measure in table.get("measures") or []:
            if not isinstance(measure, dict):
                continue
            col_name = measure.get("column_name")
            if not col_name:
                continue
            col_name = str(col_name)
            if col_name in seen_column_names:
                continue
            seen_column_names.add(col_name)
            col_desc = (
                measure.get("description")
                or measure.get("alias_name")
                or measure.get("measure_name")
                or ""
            )
            metric_obj = measure.get("metric") if isinstance(measure.get("metric"), dict) else {}
            formula = measure.get("formula") or metric_obj.get("formula")
            lines.append(
                _format_semantic_column_line(
                    table_name,
                    col_name,
                    measure.get("alias_name") or col_name,
                    col_desc,
                    semantic_type=measure.get("semantic_type"),
                    default_function=measure.get("default_function"),
                    aggregator=measure.get("aggregator"),
                    formula=formula if measure.get("is_computed") else None,
                )
            )

    return "\n".join(lines)


def get_domain_details(data, user_domain):
    found_items = []
    for item in data:
        if user_domain in item.get("domain", []):

            # Topics
            topics = item.get("topics", [])
            topics_str = ", ".join(topics)

            # Table name
            table_name = item.get("database_table")

            # Columns with datatype
            columns_info = []
            for col in item.get("columns", []):
                if not isinstance(col, dict):
                    continue
                col_name = col.get("column_name")
                if not col_name:
                    continue
                col_type = (col.get("column_metrics") or {}).get("data_type", "unknown")
                columns_info.append(f"{col_name}({col_type})")

            columns_str = ", ".join(columns_info)

            found_items.append(f"Topics: {topics_str}\nTable {table_name}({columns_str})")

    if found_items:
        return "\n\n".join(found_items)
    else:
        return "Domain not found"
