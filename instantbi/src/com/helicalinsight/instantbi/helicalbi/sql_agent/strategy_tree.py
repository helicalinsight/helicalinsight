"""Compatibility facade for the strategy engine.

Implementation lives in ``helicalbi.sql_agent.strategy`` (small SRP classes) with
literals in ``helicalbi.sql_agent.constants``. Prefer importing from this module
for stable public names used across controllers, plan graph, and tests.
"""
from __future__ import annotations

from helicalbi.sql_agent.constants import (
    ANALYTICAL_CATALOG_PATH,
    CATALOG_PATH,
    STRATEGY_INTENT_LAYOUT,
    STRATEGY_INVERTED_PYRAMID,
    STRATEGY_MECE,
    STRATEGY_PROGRESSIVE_DISCLOSURE,
    STRATEGY_SEMANTIC_COLOR,
    STRATEGY_SO_WHAT,
)
from helicalbi.sql_agent.strategy.analytical import (
    question_mentions_operation,
    select_analytical_operations,
)
from helicalbi.sql_agent.strategy.catalog import (
    clear_strategy_catalog_cache,
    get_strategy,
    custom_operations_from_source,
    list_analytical_operations_for_api,
    list_strategies,
    load_analytical_operations,
    load_strategy_catalog,
    merge_custom_operations,
    operations_from_catalog_payload as _operations_from_catalog_payload,
)
from helicalbi.sql_agent.strategy.diagnostics import (
    canonical_operation_name,
    charts_within_diagnostic_scope,
    diagnostic_activity_reason,
    diagnostic_field_scope,
    diagnostic_scope_lock,
    expand_decompose_measures,
    formula_component_measures,
    iter_applicable_measures,
    iter_diagnostic_uses,
    match_diagnostic_capabilities,
    measure_mentioned,
    measure_trait_guidance,
    operations_for_diagnostic_uses,
    resolve_diagnostic_uses,
)
from helicalbi.sql_agent.strategy.prompts import (
    analytical_operations_prompt_block,
    diagnostic_capabilities_prompt_block,
    strategy_catalog_prompt_block,
    strategy_prompt_block,
)
from helicalbi.sql_agent.strategy.runtime import (
    charts_from_strategy,
    public_persona,
    public_strategy,
    resolve_plan_runtime,
)
from helicalbi.sql_agent.strategy.selection import (
    attach_strategy,
    classify_intent,
    select_strategy,
)
from helicalbi.sql_agent.strategy.text_utils import compact as _compact

__all__ = [
    "ANALYTICAL_CATALOG_PATH",
    "CATALOG_PATH",
    "STRATEGY_INTENT_LAYOUT",
    "STRATEGY_INVERTED_PYRAMID",
    "STRATEGY_MECE",
    "STRATEGY_PROGRESSIVE_DISCLOSURE",
    "STRATEGY_SEMANTIC_COLOR",
    "STRATEGY_SO_WHAT",
    "analytical_operations_prompt_block",
    "attach_strategy",
    "canonical_operation_name",
    "charts_from_strategy",
    "charts_within_diagnostic_scope",
    "classify_intent",
    "diagnostic_activity_reason",
    "diagnostic_field_scope",
    "diagnostic_scope_lock",
    "clear_strategy_catalog_cache",
    "diagnostic_capabilities_prompt_block",
    "expand_decompose_measures",
    "formula_component_measures",
    "get_strategy",
    "iter_applicable_measures",
    "iter_diagnostic_uses",
    "list_analytical_operations_for_api",
    "list_strategies",
    "custom_operations_from_source",
    "load_analytical_operations",
    "merge_custom_operations",
    "load_strategy_catalog",
    "match_diagnostic_capabilities",
    "measure_mentioned",
    "measure_trait_guidance",
    "operations_for_diagnostic_uses",
    "public_persona",
    "public_strategy",
    "question_mentions_operation",
    "resolve_diagnostic_uses",
    "resolve_plan_runtime",
    "select_analytical_operations",
    "select_strategy",
    "strategy_catalog_prompt_block",
    "strategy_prompt_block",
]
