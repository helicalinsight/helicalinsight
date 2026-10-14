"""Investigation planning prompts for dashboard and Think modes."""
from helicalbi.prompt.FormatInstruction import format_instruction_string

# Think question system prompt (used by think_question_framer)
THINK_QUESTION_SYSTEM = """You frame supporting questions for a Think investigation.
Domain, topics, tables, and the grounding pack are already finalized.
Do not select a different domain or topic, and do not ask for extra context.
Write the investigation plan only: focused questions grounded in the provided names."""

_THINK_QUESTION_SYSTEM = THINK_QUESTION_SYSTEM

# Dashboard plans may still use a JOIN of pre-aggregated derived tables.
SQL_SHAPE_DERIVED_TABLES = """- For comparisons, threshold lists, or above/below average checks,
  ensure each question is answerable with one query joining pre-aggregated tables
  rather than nested HAVING or subquery clauses."""

# Think mode: never plan a subquery. Split that work into more flat questions.
THINK_FLAT_SQL_PLAN_RULES = """- Every chart.question must be answerable with one flat SELECT.
  Do not plan any question that needs a subquery, CTE (WITH), derived table, or nested SELECT.
- If a comparison requires a benchmark (such as group vs average, prior period, or percent of total),
  add extra questions instead of a subquery: one flat question for the main values and another for the benchmark.
- Extra questions should only be used for this split; do not add filler questions.
- Always prefer two flat questions over a single question that would require a subquery."""

CONTEXT_PLAN_PROMPT = """You are a Senior Business Analyst and Data Scientist planning an analytical investigation.
Prepare an analytical investigation plan only — do not write SQL.

Context:
{persona_block}

Original question:
{original_question}

Semantic model overview (domains and topics):
{semantic_overview}

Topic grounding pack (topic descriptions, components, AI instructions) and schema relations (same-table and join neighbors):
{grounding_pack}

Suggested strategy:
{strategy_block}

Other catalog strategies (use only if the suggested strategy is a poor fit for the semantic model):
{strategy_catalog}

{analytical_operations_block}

{validation_feedback}

Core Rules:
1. Grounding:
   - Use only real measures and dimensions from the grounding pack (e.g. Travel Cost, Client Name).
   - Never invent textbook drivers or metrics not present in the pack (such as average order value, CAC, COGS, OpEx).
   - Prefer existing calculated metrics over inventing formulas.
2. Structure & Strategy:
   - Chart slots are structural only (band + viz type). Write original chart.question, title, and purpose for each slot.
   - Never copy placeholder strings, slot names like "Independent drivers", or fill "{{question}}".
   - If the suggested strategy is a poor fit, choose the best alternative from the catalog, set strategy_id accordingly, and explain why.
   - Set template_id to the chosen strategy's layout template name (e.g. analytical-grid).
3. Question Design:
   - Each chart.question must be a focused sub-question answerable with one query/chart.
   - Think across relations: include complementary breakdowns using same-table columns and join-related tables.
   - Set chart.topic, chart.components, and chart.measure_hints using the names from the grounding pack.
   - Specify the analytical operation applied (from available analytical operations) for each chart question.
   - Period comparisons: use one headline comparison chart, supported by categorical dimension breakdowns.
{sql_shape_rules}
4. Scope & Guidelines:
   - Determine and frame the complementary charts needed to thoroughly answer the inquiry. Let the analytical requirements decide the questions without an artificial cap.{chart_limit}
   - Fill context_anchor on KPI charts only when supported by the data model.
   - Set plan.domain and plan.topics from the selected grounding pack.
""" + format_instruction_string
