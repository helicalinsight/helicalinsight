"""Object-oriented strategy engine for the SQL agent.

Split from the former monolithic ``strategy_tree`` module:

* ``catalog`` — JSON load / list / get
* ``selection`` — intent + decision-tree strategy pick
* ``analytical`` — analytical operation scoring
* ``diagnostics`` — capability match / use resolution / traits
* ``prompts`` — LLM prompt block builders
* ``runtime`` — public projections + plan hydration

Import from ``helicalbi.sql_agent.strategy_tree`` for the historical facade, or from
this package directly.
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
    AnalyticalOperationSelector,
    question_mentions_operation,
    select_analytical_operations,
)
from helicalbi.sql_agent.strategy.catalog import (
    StrategyCatalog,
    clear_strategy_catalog_cache,
    get_strategy,
    custom_operations_from_source,
    list_analytical_operations_for_api,
    list_strategies,
    load_analytical_operations,
    load_strategy_catalog,
    merge_custom_operations,
    operations_from_catalog_payload,
)
from helicalbi.sql_agent.strategy.diagnostics import (
    ApplicableMeasuresNormalizer,
    DecomposeMeasureExpander,
    DiagnosticCapabilityMatcher,
    DiagnosticUseResolver,
    DiagnosticUsesNormalizer,
    FormulaComponentExtractor,
    MeasureMentionDetector,
    MeasureTraitGuidance,
    OperationNameCanonicalizer,
    canonical_operation_name,
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
    AnalyticalPromptBuilder,
    DiagnosticPromptBuilder,
    StrategyPromptBuilder,
    analytical_operations_prompt_block,
    diagnostic_capabilities_prompt_block,
    strategy_catalog_prompt_block,
    strategy_prompt_block,
)
from helicalbi.sql_agent.strategy.runtime import (
    PlanRuntimeResolver,
    PublicPersonaProjector,
    PublicStrategyProjector,
    StrategyChartBuilder,
    charts_from_strategy,
    public_persona,
    public_strategy,
    resolve_plan_runtime,
)
from helicalbi.sql_agent.strategy.selection import (
    DecisionTreeWalker,
    IntentClassifier,
    StrategyAttacher,
    StrategySelector,
    attach_strategy,
    classify_intent,
    select_strategy,
)
from helicalbi.sql_agent.strategy.text_utils import clean, compact

__all__ = [
    "ANALYTICAL_CATALOG_PATH",
    "CATALOG_PATH",
    "STRATEGY_INTENT_LAYOUT",
    "STRATEGY_INVERTED_PYRAMID",
    "STRATEGY_MECE",
    "STRATEGY_PROGRESSIVE_DISCLOSURE",
    "STRATEGY_SEMANTIC_COLOR",
    "STRATEGY_SO_WHAT",
    "AnalyticalOperationSelector",
    "AnalyticalPromptBuilder",
    "ApplicableMeasuresNormalizer",
    "DecisionTreeWalker",
    "DecomposeMeasureExpander",
    "DiagnosticCapabilityMatcher",
    "DiagnosticPromptBuilder",
    "DiagnosticUseResolver",
    "DiagnosticUsesNormalizer",
    "FormulaComponentExtractor",
    "IntentClassifier",
    "MeasureMentionDetector",
    "MeasureTraitGuidance",
    "OperationNameCanonicalizer",
    "PlanRuntimeResolver",
    "PublicPersonaProjector",
    "PublicStrategyProjector",
    "StrategyAttacher",
    "StrategyCatalog",
    "StrategyChartBuilder",
    "StrategyPromptBuilder",
    "StrategySelector",
    "analytical_operations_prompt_block",
    "attach_strategy",
    "canonical_operation_name",
    "charts_from_strategy",
    "classify_intent",
    "clean",
    "clear_strategy_catalog_cache",
    "compact",
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
    "operations_from_catalog_payload",
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
