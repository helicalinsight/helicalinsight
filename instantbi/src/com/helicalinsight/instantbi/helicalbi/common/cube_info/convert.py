"""Convert normalized cube_info into legacy cube_metadata."""

from __future__ import annotations

from typing import Dict, List, Optional

from helicalbi.common.JsonToPara import resolve_column_alias, resolve_table_alias
from helicalbi.common.cube_info.fields import (
    _aggregator_from_cube_item,
    _format_string_from_cube_item,
    _sort_order_raw_from_cube_item,
    sort_direction_from_value,
)
from helicalbi.common.cube_info.lookup import (
    _ai_context_payload,
    _default_function_from_cube_item,
    _formula_from_cube_item,
    _known_table_names,
    _resolve_column_name,
    _resolve_table_name,
    _table_from_formula,
    build_column_to_table_map,
    build_metadata_id_maps,
)
from helicalbi.common.cube_info.normalize import normalize_cube_info


def _preferred_alias(preferred, table_name: str, column_name: str, metadata_response: dict) -> str:
    """Use the cube display name, otherwise the metadata column alias."""
    if preferred:
        return str(preferred)
    return resolve_column_alias(table_name, column_name, metadata_response)


def cube_info_to_cube_metadata(
    cube_info: list,
    metadata_response: dict,
    domain_topics: Optional[List[str]] = None,
) -> List[dict]:
    """Convert cube_info entries into legacy cube_metadata for the SQL pipeline."""
    cube_info = normalize_cube_info(cube_info)
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
            preferred_alias = dim.get("dimensionName")
            formula = _formula_from_cube_item(dim)
            column_entry = {
                "column_name": column_name,
                # Prefer dimension name; fall back to metadata alias when absent.
                "alias_name": _preferred_alias(
                    preferred_alias, table_name, column_name, metadata_response
                ),
                "description": dim.get("description") or dim.get("dimensionName") or "",
                "semantic_type": dim.get("semanticType"),
                "synonyms": dim.get("synonyms") or [],
                "default_function": _default_function_from_cube_item(
                    dim, metadata_response, table_name, column_name
                ) or None,
                "format_string": _format_string_from_cube_item(dim) or None,
                "sort_order": _sort_order_raw_from_cube_item(dim),
                "sort_direction": sort_direction_from_value(
                    _sort_order_raw_from_cube_item(dim)
                ),
            }
            # Table/column selection needs dimensionName, semanticType, columnName,
            # and the whole aiContext object. Formula is a top-level key (not
            # nested under metric).
            if preferred_alias:
                column_entry["dimension_name"] = preferred_alias
            if formula:
                column_entry["formula"] = formula
            hierarchy_name = dim.get("hierarchyName") or dim.get("hierarchy_name")
            if hierarchy_name:
                column_entry["hierarchy_name"] = hierarchy_name
            level_name = dim.get("levelName") or dim.get("level_name")
            if level_name:
                column_entry["level_name"] = level_name
            ai_context = _ai_context_payload(dim)
            if ai_context:
                column_entry["ai_context"] = ai_context
            if dim.get("ai_instructions"):
                column_entry["ai_instructions"] = dim.get("ai_instructions")
            if dim.get("ai_examples"):
                column_entry["ai_examples"] = dim.get("ai_examples")
            entry["columns"].append(column_entry)

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
            preferred_alias = measure.get("measureName")
            formula = _formula_from_cube_item(measure)
            physical_column = None if computed else column_name
            measure_entry = {
                "column_name": column_name,
                # Prefer measure name; fall back to metadata alias when absent.
                "alias_name": _preferred_alias(
                    preferred_alias, table_name, column_name, metadata_response
                ),
                "aggregator": _aggregator_from_cube_item(measure),
                "description": measure.get("description") or measure.get("measureName") or "",
                "semantic_type": measure.get("semanticType"),
                "format_string": _format_string_from_cube_item(measure) or None,
                # Measures are never ORDER BY candidates — omit sort metadata.
                "sort_order": None,
                "sort_direction": None,
                "default_function": _default_function_from_cube_item(
                    measure, metadata_response, table_name, physical_column
                ) or None,
                "synonyms": measure.get("synonyms") or [],
            }
            # Table/column selection needs measureName, columnName, and aiContext.
            # Blank-column (formula-only) measures also need formula + semanticType.
            # Formula is a top-level key (not nested under metric).
            if preferred_alias:
                measure_entry["measure_name"] = preferred_alias
            if measure.get("grain"):
                measure_entry["grain"] = measure.get("grain")
            if measure.get("direction"):
                measure_entry["direction"] = measure.get("direction")
            if measure.get("additivity"):
                measure_entry["additivity"] = measure.get("additivity")
            process_stages = measure.get("processStages") or measure.get("process_stages")
            if process_stages:
                measure_entry["process_stages"] = process_stages
            ai_context = _ai_context_payload(measure)
            if ai_context:
                measure_entry["ai_context"] = ai_context
            if measure.get("ai_instructions"):
                measure_entry["ai_instructions"] = measure.get("ai_instructions")
            if measure.get("ai_examples"):
                measure_entry["ai_examples"] = measure.get("ai_examples")
            if computed:
                # Not a physical column; carries a formula the LLM must expand.
                measure_entry["is_computed"] = True
                measure_entry["formula"] = formula
            elif formula:
                measure_entry["formula"] = formula
            entry["measures"].append(measure_entry)

    return list(tables.values())
