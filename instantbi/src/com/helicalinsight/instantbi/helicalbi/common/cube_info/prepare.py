"""Assemble the model payload consumed by the SQL and viz pipelines."""

from __future__ import annotations

from typing import Any

from helicalbi.common.JsonToPara import enrich_cube_metadata_with_aliases
from helicalbi.common.cube_info.convert import cube_info_to_cube_metadata
from helicalbi.common.cube_info.fields import (
    ai_instructions_from_cube_info,
    format_strings_from_cube_info,
    sort_orders_from_cube_info,
)
from helicalbi.common.cube_info.metrics import (
    business_metrics_from_cube_info,
    merge_business_metrics,
    synonyms_from_cube_info,
)
from helicalbi.common.cube_info.normalize import extract_domain_topics, normalize_cube_info
from helicalbi.common.cube_info.prompt import (
    format_ai_instructions_for_prompt,
    format_format_strings_for_prompt,
    format_sort_orders_for_prompt,
)
from helicalbi.common.cube_info.topics import (
    domain_context_from_model,
    topic_mappings_from_cube_info,
    topic_mappings_from_domain,
)


def prepare_cube_info_model_data(model_data: dict, metadata_response: dict) -> dict[str, Any]:
    """Return cube_metadata, domain/topics, and business_metrics for cube_info models."""
    _, topics = extract_domain_topics(model_data)
    # Flatten hierarchy levels into dimensions/measures before any conversion.
    cube_info = normalize_cube_info(model_data.get("cube_info") or model_data.get("cube") or [])
    cube_metadata = enrich_cube_metadata_with_aliases(
        cube_info_to_cube_metadata(
            cube_info,
            metadata_response,
            domain_topics=topics,
        ),
        metadata_response,
    )
    domains, topics = extract_domain_topics(model_data)
    derived_metrics = business_metrics_from_cube_info(cube_info, metadata_response)
    business_metrics = merge_business_metrics(model_data.get("business_metrics"), derived_metrics)
    topic_mappings = (
        model_data.get("topic_mappings")
        or topic_mappings_from_domain(model_data, cube_info=cube_info)
        or topic_mappings_from_cube_info(cube_info, topics)
    )
    derived_synonyms = synonyms_from_cube_info(cube_info, metadata_response)
    synonyms = list(model_data.get("synonyms") or [])
    existing_keys = {
        (entry.get("database_table"), entry.get("dimension_name"))
        for entry in synonyms
        if isinstance(entry, dict)
    }
    for entry in derived_synonyms:
        key = (entry.get("database_table"), entry.get("dimension_name"))
        if key not in existing_keys:
            synonyms.append(entry)
            existing_keys.add(key)
    format_strings = format_strings_from_cube_info(cube_info)
    ai_instructions = ai_instructions_from_cube_info(cube_info)
    sort_orders = sort_orders_from_cube_info(cube_info)
    domain_context = domain_context_from_model(model_data, topic_mappings=topic_mappings)
    return {
        "cube_metadata": cube_metadata,
        "domain": domains,
        "topics": topics,
        "business_metrics": business_metrics,
        "topic_mappings": topic_mappings,
        "synonyms": synonyms,
        "domain_context": domain_context,
        "format_strings": format_strings,
        "ai_instructions": ai_instructions,
        "sort_orders": sort_orders,
        "column_sort_orders": format_sort_orders_for_prompt(sort_orders),
        "column_format_strings": format_format_strings_for_prompt(format_strings),
        "column_ai_instructions": format_ai_instructions_for_prompt(ai_instructions),
    }
