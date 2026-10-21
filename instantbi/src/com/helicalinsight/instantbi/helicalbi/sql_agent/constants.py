"""Shared literals for the SQL agent package.

Strategy catalog paths/ids, analytical scoring weights, plan/execute budgets,
progress phrases, and LLM prompt templates live here so modules stay free of
magic numbers. Tune behavior by editing this file — not call sites.
"""
from __future__ import annotations

from pathlib import Path

# --------------------------------------------------------------------------------------
# Catalog locations (JSON files stay in ``sql_agent/strategies``)
# --------------------------------------------------------------------------------------
SQL_AGENT_DIR = Path(__file__).resolve().parent
STRATEGIES_DIR = SQL_AGENT_DIR / "strategies"
DASHBOARD_CATALOG_FILENAME = "dashboard_strategies.json"
ANALYTICAL_CATALOG_FILENAME = "analytical_strategy.json"
CATALOG_PATH = STRATEGIES_DIR / DASHBOARD_CATALOG_FILENAME
ANALYTICAL_CATALOG_PATH = STRATEGIES_DIR / ANALYTICAL_CATALOG_FILENAME
JSON_ENCODING = "utf-8"

STRATEGY_CATALOG_CACHE_SIZE = 4
ANALYTICAL_CATALOG_CACHE_SIZE = 2

# --------------------------------------------------------------------------------------
# Dashboard strategy ids and intents
# --------------------------------------------------------------------------------------
STRATEGY_INVERTED_PYRAMID = "inverted_pyramid"
STRATEGY_PROGRESSIVE_DISCLOSURE = "progressive_disclosure"
STRATEGY_MECE = "mece_metric_structuring"
STRATEGY_SEMANTIC_COLOR = "semantic_color_coding"
# Retired layouts. They used the same template as the layout they alias to.
STRATEGY_INTENT_LAYOUT = "intent_driven_layout"
STRATEGY_SO_WHAT = "so_what_contextual_anchoring"
LAYOUT_ALIASES = {
    STRATEGY_SO_WHAT: STRATEGY_INVERTED_PYRAMID,
    STRATEGY_INTENT_LAYOUT: STRATEGY_SEMANTIC_COLOR,
}

INTENT_OVERVIEW = "overview"

# Catalog JSON keys
CATALOG_KEY_STRATEGIES = "strategies"
CATALOG_KEY_DEFAULT_STRATEGY = "default_strategy"
CATALOG_KEY_INTENTS = "intents"
CATALOG_KEY_RULES = "rules"
CATALOG_KEY_LAYOUT = "layout"
CATALOG_KEY_DECISION_TREE = "decision_tree"
ANALYTICAL_KEY_WRAPPER = "analyticalOperationsCatalog"
ANALYTICAL_KEY_OPERATIONS = "operations"

# Decision tree JSON keys
TREE_KEY_STRATEGY = "strategy"
TREE_KEY_MATCH = "match"
TREE_KEY_CASES = "cases"
TREE_KEY_DEFAULT = "default"
TREE_KEY_EQUALS = "equals"
TREE_KEY_IN = "in"
TREE_KEY_THEN = "then"

# Context fields handed to the decision tree
CONTEXT_PERSONA = "persona"
CONTEXT_INTENT = "intent"
CONTEXT_STRATEGY_HINT = "strategy_hint"

# Persona keys read/written while selecting and attaching strategies
PERSONA_KEY_NAME = "name"
PERSONA_KEY_STRATEGY_HINT = "strategy_hint"
PERSONA_KEY_STRATEGY_ID = "strategy_id"
PERSONA_KEY_STRATEGIES = "strategies"
PERSONA_KEY_STRATEGY = "strategy"
PERSONA_KEY_STRATEGY_GUIDE = "strategy_guide"
PERSONA_KEY_TEMPLATE_ID = "template_id"
ATTACH_DROPPED_KEYS = (PERSONA_KEY_STRATEGY_GUIDE, PERSONA_KEY_TEMPLATE_ID)
PUBLIC_PERSONA_STRIPPED_KEYS = (
    PERSONA_KEY_STRATEGY,
    PERSONA_KEY_STRATEGIES,
    PERSONA_KEY_STRATEGY_GUIDE,
    PERSONA_KEY_TEMPLATE_ID,
    PERSONA_KEY_STRATEGY_ID,
)

# Strategy selection result labels
SELECTION_KEY = "selection"
SELECTION_SOURCE_HINT = "hint"
SELECTION_SOURCE_DECISION_TREE = "decision_tree"
SELECTED_DIAGNOSTIC_CAPABILITY_KEY = "diagnostic_capability"

# --------------------------------------------------------------------------------------
# Chart slot defaults (charts_from_strategy / strategy_prompt_block)
# --------------------------------------------------------------------------------------
DEFAULT_MAX_CHARTS = 5
FALLBACK_MAX_CHARTS = 3  # used when max_charts is falsy (0/None)
DEFAULT_CHART_LEVEL = "middle"
DEFAULT_VIZ_HINT = "bar"
DEFAULT_CHART_TITLE = "Chart"
DEFAULT_CHART_PURPOSE = "reference slot"
DEFAULT_QUESTION_FALLBACK = "the requested metric"
CHART_QUESTION_TEMPLATE = "{question} — {title}"

# --------------------------------------------------------------------------------------
# Capability / diagnostic JSON field names
# --------------------------------------------------------------------------------------
CAP_KEY_CAPABILITY = "capability"
CAP_KEY_APPLICABLE_TO = "applicableTo"
CAP_KEY_APPLICABLE_TO_SNAKE = "applicable_to"
CAP_KEY_USES = "uses"
CAP_KEY_MATCHED_MEASURES = "matchedMeasures"
CAP_KEY_MEASURE_TRAITS = "measureTraits"
APPLICABLE_NAME_KEYS = ("measure", "name", "fields")

USE_TYPE_KEYS = ("type", "kind")
USE_NAME_KEYS = ("name", "id")
USE_STRATEGY_KEYS = frozenset({"analyticalOperation", "analytical_operation", "strategy", "operation"})
# Order matters: first populated key wins when a use has no explicit name.
USE_FALLBACK_NAME_KEYS = (
    "analyticalOperation",
    "analytical_operation",
    "strategy",
    "operation",
    "measure",
    "dimension",
)

# Diagnostic use kinds
KIND_STRATEGY = "strategy"
KIND_MEASURE = "measure"
KIND_DIMENSION = "dimension"
KIND_OTHER = "other"
HINT_KIND_OPERATION = "operation"
HINT_KIND_ANALYTICAL_STRATEGY = "analytical_strategy"
EXPLICIT_STRATEGY_HINT_KINDS = frozenset({KIND_STRATEGY, HINT_KIND_OPERATION, HINT_KIND_ANALYTICAL_STRATEGY})
STRATEGY_LOOKUP_HINT_KINDS = EXPLICIT_STRATEGY_HINT_KINDS | {""}

USE_PLAN_KEY_KIND = "kind"
USE_PLAN_KEY_NAME = "name"
USE_PLAN_KEY_SOURCE = "source"
USE_PLAN_KEY_CAPABILITY = "capability"
USE_PLAN_KEY_MEASURES = "measures"
USE_PLAN_KEY_DIMENSIONS = "dimensions"
USE_REF_KEY_HINTED_KIND = "hinted_kind"
USE_REF_KEY_INPUTS = "inputs"
USE_SOURCE_FORMULA = "formula"
# Keys on a use object that name the use itself. Every other key is a configured input.
USE_INPUT_RESERVED_KEYS = frozenset({
    "analyticalOperation",
    "analytical_operation",
    "strategy",
    "operation",
    "name",
    "id",
    "type",
    "kind",
    "key",
    "requiredInputs",
    "optionalInputs",
})

# Trait fields on a diagnostic capability's measureTraits entries
TRAIT_KEY_NAME = "name"
TRAIT_KEY_GRAIN = "grain"
TRAIT_KEY_ADDITIVITY = "additivity"
TRAIT_KEY_DIRECTION = "direction"

OPERATION_KEY = "operation"
OPERATION_DESCRIPTION_KEY = "description"
OPERATION_CATEGORY_KEY = "category"
OPERATION_TYPE_KEY = "type"
CUSTOM_OPERATION_TYPE = "custom"
CUSTOM_OPERATIONS_KEY = "customAnalyticalOperations"
CUSTOM_OPERATIONS_KEY_SNAKE = "custom_analytical_operations"
OPERATION_ANALYTICAL_STRATEGY_KEY = "analytical_strategy"
OPERATION_DECOMPOSE = "decompose"

# Fields copied into the public analytical operations projection (order preserved).
API_OPERATION_LIST_FIELDS = (
    "requiredInputs",
    "optionalInputs",
    "outputFields",
    "dashboardVisualizations",
)

# --------------------------------------------------------------------------------------
# Measure / text matching thresholds
# --------------------------------------------------------------------------------------
MIN_PHRASE_LENGTH = 3  # shortest measure name or synonym matched by substring
MIN_TOKEN_LENGTH = 4  # shortest distinctive token taken from a measure name
SINGLE_TOKEN_MIN_LENGTH = 6  # a lone distinctive token must be at least this long
LONG_TOKEN_LENGTH = 8  # one long token in the text is enough to count as a mention
MIN_MATCHING_TOKENS = 2  # otherwise require at least this many tokens
MIN_FORMULA_MEASURE_NAME_LENGTH = 3
SYNONYM_SPLIT_PATTERN = r"[,;|]"
TOKEN_PATTERN = r"[a-z0-9]+"
NON_ALNUM_PATTERN = r"[^a-z0-9]+"

GENERIC_MEASURE_TOKENS = frozenset(
    {
        "rate",
        "cost",
        "total",
        "count",
        "value",
        "amount",
        "per",
        "the",
        "and",
        "for",
        "with",
        "avg",
        "average",
        "number",
        "percent",
        "percentage",
    }
)

# Diagnostic aliases (compacted lowercase) -> canonical analytical operation name.
OPERATION_ALIASES = {
    "measuredecomposition": "decompose",
    "measure_decomposition": "decompose",
    "decomposition": "decompose",
    "decomposemeasure": "decompose",
    "periodcomparison": "period_comparison",
    "segmentanalysis": "segment",
    "trendanalysis": "trend",
    "ratiorateanalysis": "ratio_rate_analysis",
    "contributionanalysis": "contribution",
}

# --------------------------------------------------------------------------------------
# Measure trait guidance
# --------------------------------------------------------------------------------------
ADDITIVITY_NON_ADDITIVE_LABELS = frozenset({"non additive", "not additive"})
ADDITIVITY_SEMI_ADDITIVE = "semi additive"
ADDITIVITY_ADDITIVE = "additive"
DIRECTION_NEGATIVE = "negative"
DIRECTION_POSITIVE = "positive"
DIRECTION_NEUTRAL = "neutral"
TRAIT_NOTE_SEPARATOR = "; "

TRAIT_GRAIN_NOTE = "stay at grain '{grain}' and do not join a finer-grain table that duplicates those rows"
TRAIT_NON_ADDITIVE_NOTE = "do not SUM this measure; compute it from its formula components inside each group"
TRAIT_SEMI_ADDITIVE_NOTE = "do not SUM this measure across time; sum only across non-time dimensions"
TRAIT_ADDITIVE_NOTE = "SUM is valid across dimensions at this grain"
TRAIT_NEGATIVE_NOTE = "an increase is unfavorable"
TRAIT_POSITIVE_NOTE = "a decrease is unfavorable"
TRAIT_NEUTRAL_NOTE = "describe volume and distribution; do not treat a change as good or bad"

# --------------------------------------------------------------------------------------
# Analytical operation scoring
# --------------------------------------------------------------------------------------
DEFAULT_MAX_OPERATIONS = 5
SCORE_DIAGNOSTIC_BOOST = 20.0  # operation declared by a matching diagnostic capability
SCORE_NAME_IN_QUESTION = 5.0  # operation name appears in the question
SCORE_KEYWORD_HIT = 3.0  # per operation keyword found in the question
SCORE_DESCRIPTION_TOKEN = 0.5  # per question token found in the operation description
SCORE_BASELINE_BONUS = 0.2  # tie-breaker favouring common baseline operations
DESCRIPTION_TOKEN_MIN_LENGTH = 4
BASELINE_OPERATIONS = frozenset({"trend", "period_comparison", "segment", "contribution", "distribution"})

# Question keywords that signal each analytical operation.
OPERATION_KEYWORDS: dict[str, tuple[str, ...]] = {
    "trend": ("trend", "trajectory", "over time", "monthly", "yearly", "quarterly", "daily", "historic", "timeline"),
    "period_comparison": ("compare", "comparison", "vs", "versus", "prior", "last year", "last month", "mom", "yoy", "qoq", "change"),
    "segment": ("breakdown", "segment", "by", "categor", "split", "group", "region", "department"),
    "decompose": ("decompose", "components", "break down", "additive", "parts", "formula"),
    "contribution": ("contribut", "driver", "driven", "impact", "factor", "responsible", "why", "drop", "spike", "increase", "decrease"),
    "distribution": ("distribut", "spread", "histogram", "range", "dispersion", "variance", "frequency"),
    "reason_analysis": ("reason", "cause", "explanation", "cancellation", "failure", "defect"),
    "mix_analysis": ("mix", "composition", "share shift", "portfolio mix", "volume mix"),
    "outlier_detection": ("outlier", "anomaly", "abnormal", "spike", "deviation", "unusual", "exception"),
    "ranking": ("rank", "top", "bottom", "highest", "lowest", "best", "worst", "leading"),
    "pareto_analysis": ("pareto", "80/20", "vital few", "majority"),
    "concentration_risk": ("concentration", "risk", "exposure", "dependency", "heavily dependent"),
    "benchmark_comparison": ("benchmark", "target", "sla", "budget", "goal", "standard", "baseline"),
    "variance_waterfall": ("waterfall", "variance", "bridge", "walk"),
    "gap_analysis": ("gap", "shortfall", "surplus", "deficit", "miss"),
    "correlation": ("correlat", "relationship", "association", "linked to"),
    "cohort_analysis": ("cohort", "vintage", "retention over time"),
    "funnel_dropoff": ("funnel", "conversion", "dropoff", "attrition", "stage", "pipeline"),
    "churn_analysis": ("churn", "attrition", "retention", "cancel", "lost client", "lost customer"),
}

# --------------------------------------------------------------------------------------
# Prompt text
# --------------------------------------------------------------------------------------
PROMPT_NONE = "(none)"

STRATEGY_PROMPT_SELECTED = "Selected strategy: {strategy_id}"
STRATEGY_PROMPT_TITLE = "Title: {title}"
STRATEGY_PROMPT_TEMPLATE = "Layout template name: {template_id}"
STRATEGY_PROMPT_PURPOSE = "Purpose: {purpose}"
STRATEGY_PROMPT_CHART_INTRO = (
    "Chart slots (band + viz only — write questions from the semantic model "
    "dimensions/metrics; do not copy slot labels or invent textbook drivers):"
)
STRATEGY_PROMPT_CHART_LINE = "{index}. band={level} viz={viz} — {purpose}"
STRATEGY_CATALOG_LINE = "- {strategy_id}: {purpose}"

ANALYTICAL_PROMPT_HEADER = (
    "Available Analytical Operations to Apply (assign an operation to each question):"
)
ANALYTICAL_PROMPT_LINE = "- {name}: {description}"
ANALYTICAL_PROMPT_CUSTOM_LINE = "- {name} (custom): {description}"
ANALYTICAL_PROMPT_CUSTOM_DETAIL = "  {label}: {value}"

DIAGNOSTIC_PROMPT_HEADER = (
    "Configured Diagnostic Capabilities (must guide question framing when relevant):"
)
DIAGNOSTIC_PROMPT_CAPABILITY = "- Capability '{name}'"
DIAGNOSTIC_PROMPT_APPLICABLE = " applicable to measures: {measures}"
DIAGNOSTIC_PROMPT_USES = " uses: {uses}"
DIAGNOSTIC_PROMPT_RULE = (
    "  * Rule: When the question is about an applicable measure, cover each listed use. "
    "A use may be an analytical strategy (for example trend, segment, or measureDecomposition), "
    "a measure, a dimension, or another semantic-model item. "
    "Frame 3 to 5 targeted questions rather than exhaustive breakdowns."
)

THINK_PROMPT_HEADER = (
    "Think-mode diagnostic rule (required):",
    "The question matches a configured diagnostic capability. "
    "Investigate the applicable measures using every declared use.",
)
THINK_PROMPT_CAPABILITY = "- Capability '{name}' applies to: {targets}."
THINK_PROMPT_UNSPECIFIED = "(unspecified)"
THINK_PROMPT_TRAIT = "  {name}: {guidance}."
THINK_PROMPT_COVER_ALL = (
    "Cover every required use below. A use may be an analytical strategy, "
    "a measure, a dimension, or another semantic-model item. Do not add unrelated breakdowns."
)
THINK_PROMPT_STRATEGY_ORIGIN = " (configured as {source})"
THINK_PROMPT_STRATEGY = (
    "- strategy {name}{origin}: write one flat question that applies this strategy "
    "to the applicable measures."
)
THINK_PROMPT_STRATEGY_FIELDS = (
    "  Configured fields for this strategy: {fields}. Do not add any other field."
)
THINK_PROMPT_ALLOWED_STRATEGIES = "Allowed strategies: {names}."
THINK_PROMPT_ALLOWED_MEASURES = "Allowed measures: {names}."
THINK_PROMPT_ALLOWED_DIMENSIONS = "Allowed dimensions: {names}."
THINK_PROMPT_EXCLUSIVE = (
    "Exclusive scope: Do not use any other analytical strategy, measure, or dimension. "
    "Ignore every other name in the grounding pack and the strategy catalog."
)
THINK_PROMPT_FORMULA_ORIGIN = " (component from the measure formula)"
THINK_PROMPT_MEASURE = "- measure {name}{origin}: include this measure in the investigation.{trait_note}"
THINK_PROMPT_DIMENSION = "- dimension {name}: break an applicable measure down by this dimension."
THINK_PROMPT_OTHER = "- {name}: use this semantic-model item when it is present in the grounding pack."
THINK_PROMPT_FOOTER = (
    "Write 3 to 5 flat questions about the applicable measures. "
    "Set chart.operation to the strategy name. "
    "Set measure_hints to the applicable measures and any required measures. "
    "Set components to the measures and dimensions each question uses."
)
THINK_PROMPT_FOOTER_LOCKED = (
    "Write one flat question per allowed strategy. "
    "Add one drill-down question only when the next analysis needs a value found by that question. "
    "The drill-down uses the same strategy, measures, and dimensions, and sets uses_prior_value to true. "
    "Each question uses only the allowed measures and dimensions. "
    "Set chart.operation to that strategy name. "
    "Set measure_hints and components to only those allowed names."
)
THINK_SCOPE_LOCK_STRATEGIES = (
    "- Scope lock: every question must use this same analytical strategy: {strategies}. "
    "Do not apply any other strategy and do not choose a substitute from the catalog."
)
THINK_SCOPE_LOCK_MEASURES = (
    "- Use only these measures: {measures}. Do not use any other measure."
)
THINK_SCOPE_LOCK_DIMENSIONS = (
    "- Use only these dimensions: {dimensions}. "
    "Do not add, substitute, or break the question down by any other dimension."
)
THINK_SCOPE_LOCK_RULE = (
    "- Do not add complementary breakdowns, exploratory questions, or fields outside this configured scope. "
    "A drill-down question is optional: add it only when a later question must use a value found by the previous result, and keep the same strategy, measure, and dimensions. "
    "Dashboard layout slots are optional. Do not fill every band."
)
DIAGNOSTIC_ACTIVITY_CAPABILITY = "Picked capability '{names}'."
DIAGNOSTIC_ACTIVITY_MEASURE = "Measure: {names}."
DIAGNOSTIC_ACTIVITY_STRATEGY = "Analytical strategy: {names}."
DIAGNOSTIC_ACTIVITY_DIMENSION = "Dimension: {names}."
DIAGNOSTIC_ACTIVITY_RULE = (
    "Questions use only this analytical strategy and these dimensions. "
    "Dashboard layout slots are optional."
)

LOG_STRATEGY_SELECTED = (
    "Selected dashboard strategy=%s intent=%s persona=%s diagnostic_cap=%s"
)
ERROR_INVALID_CATALOG = "Invalid strategy catalog: {path}"
ERROR_EMPTY_CATALOG = "Strategy catalog has no strategies"
ERROR_INVALID_ANALYTICAL_PAYLOAD = "Invalid analytical operations catalog payload"

# --------------------------------------------------------------------------------------
# Agent knobs (also re-exported from config.py for historical imports)
# --------------------------------------------------------------------------------------
DEFAULT_SCHEMA_TOP_K = 5
DEFAULT_DASHBOARD_SUB_QUESTIONS = 5
THINK_EXTRA_QUESTIONS = 0
DEFAULT_MAX_TOOL_LOOPS = 32
DEFAULT_RESULT_ROW_CAP = 50
DEFAULT_EMBEDDING_DIM = 384
RECURSION_LIMIT = 80

# --------------------------------------------------------------------------------------
# Planning / semantic budgets & topic scoring
# --------------------------------------------------------------------------------------
DEFAULT_OVERVIEW_CHARS = 3500
MIN_OVERVIEW_CHARS = 500
RELATION_PACK_MIN_CHARS = 800
RELATION_PACK_MAX_CHARS = 2500
TOPIC_SCORE_EXACT_TOPIC = 5.0
TOPIC_SCORE_EXACT_DOMAIN = 3.0
TOPIC_SCORE_TOKEN_IN_BLOB = 1.0
TOPIC_SCORE_COMPONENT = 2.5
TOPIC_SCORE_COMPONENT_TOKEN = 0.5
TOPIC_TOKEN_MIN_LENGTH = 3
TOPIC_COMPONENT_TOKEN_MIN_LENGTH = 4
TOPIC_TRACE_TOP_SCORES = 8
PLAN_GRAPH_MAX_REPAIR = 1
SEMANTIC_MIN_SUFFICIENT_SCORE = 0.12

# --------------------------------------------------------------------------------------
# Execute-plan progress copy (randomised beside the step title)
# --------------------------------------------------------------------------------------
EXECUTE_STEP_STARTED = (
    "Looking into",
    "Checking",
    "Reviewing",
    "Working through",
    "Digging into",
    "Exploring",
    "Gathering",
    "Examining",
    "Going through",
    "Sorting out",
    "Tracing",
    "Pulling together",
)
EXECUTE_STEP_DONE = (
    "is ready",
    "is in",
    "is set",
    "is together",
    "is complete",
    "came through",
    "looks good",
    "is lined up",
    "is wrapped up",
    "is on hand",
)
EXECUTE_STEP_FAILED = (
    "could not be finished",
    "did not come through",
    "could not be gathered",
    "did not line up",
    "ran into a problem",
    "stopped short",
    "could not be sorted out",
    "did not come together",
)
EXECUTE_STEP_DEFAULT_LABEL = "this step"
CHART_BUILD_FAILURE_DEFAULT = "Chart could not be built."
