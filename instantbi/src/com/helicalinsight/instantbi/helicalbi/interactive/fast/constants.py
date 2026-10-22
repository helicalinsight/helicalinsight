"""Literals for interactive fast (normal) mode."""
from __future__ import annotations

from typing import Final

# Keys copied from cube_info_prepared into graph state / result (DRY merge).
CUBE_INFO_STATE_KEYS: Final[tuple[str, ...]] = (
    "business_metrics",
    "topic_mappings",
    "synonyms",
    "domain_context",
    "format_strings",
    "column_format_strings",
    "ai_instructions",
    "column_ai_instructions",
    "sort_orders",
    "column_sort_orders",
)

# Keys merged onto the post-CubeInfoFlow result (prefer prepared, else existing).
CUBE_INFO_RESULT_KEYS: Final[tuple[str, ...]] = (
    "domain",
    "topics",
    "domain_context",
    "topic_mappings",
    "synonyms",
    "business_metrics",
    "format_strings",
    "column_format_strings",
    "ai_instructions",
    "column_ai_instructions",
    "sort_orders",
    "column_sort_orders",
)

# Defaults when a prepared key is missing (list vs dict vs str).
CUBE_INFO_LIST_KEYS: Final[frozenset[str]] = frozenset(
    {
        "business_metrics",
        "topic_mappings",
        "synonyms",
        "sort_orders",
        "domain",
        "topics",
    }
)
CUBE_INFO_DICT_KEYS: Final[frozenset[str]] = frozenset(
    {"format_strings", "ai_instructions"}
)
