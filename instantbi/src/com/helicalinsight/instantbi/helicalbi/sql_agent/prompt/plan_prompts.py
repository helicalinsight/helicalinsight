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
- Table Affinity: Query metrics with date dimensions from the same table (e.g. travel_details.travel_date for Travel Cost). Avoid cross-transaction joins.
- Anti-Fan-Out: Never plan questions requiring joins between two transaction tables via an entity key (e.g. travel_details to meeting_details via employee_id) that cause Cartesian duplication of sums.
- Single-Period Filters: When splitting into period questions (e.g. current year vs prior year), specify the target period filter explicitly in the question so queries filter to a single period instead of running unbounded identical scans.
- Add a drill-down question only when the next analysis needs a value that is unknown until the previous question returns (for example the year, category, or extreme to filter on). Do not add one when a single flat question already answers the ask.
- When a drill-down is required, the first question finds that value. The following question analyzes the same analytical strategy, measure, and dimensions using the value found. Set uses_prior_value to true on that follow-up only. Refer to the value as "the value found in the previous question" because the literal is unknown while planning.
- Do not use a drill-down to introduce another strategy, measure, or dimension.
- Honor each measure's grain, additivity, and direction. Non-additive measures are not summed. Semi-additive measures are not summed across time. Neutral direction is described as volume, not as better or worse."""

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

Other catalog strategies:
{strategy_catalog}

{analytical_operations_block}

{diagnostic_capabilities_block}

{validation_feedback}

Core Rules:
1. Grounding:
   - Use only real measures and dimensions from the grounding pack (e.g. Travel Cost, Client Name).
   - Never invent textbook drivers or metrics not present in the pack (such as average order value, CAC, COGS, OpEx).
   - Prefer existing calculated metrics over inventing formulas.
   - Grain, additivity, and direction: Use the grain, additivity, and direction stored on each measure or metric. Stay at that grain and do not join a finer-grain table that fans the measure out. additive: SUM across dimensions at that grain is valid. non-additive: do not SUM the measure; compute it from its formula components inside each segment or period. semi-additive: do not SUM across time. direction negative: an increase is unfavorable. direction positive: a decrease is unfavorable. direction neutral: describe volume and distribution; do not treat a change as good or bad.
   - Table Affinity & Date Isolation: When querying a metric from a table (e.g. Travel Cost on travel_details), always use the date/time hierarchy levels from that SAME table (e.g. travel_details.travel_date YEAR/MONTH) rather than date columns from other transaction tables (e.g. meeting_details.meeting_date). Never introduce unrelated transaction tables unless an attribute unique to that foreign table is explicitly requested.
   - Fan-Out Join Trap Prohibition: Never join two distinct transaction tables across a shared entity key (e.g. joining travel_details to meeting_details via employee_id) when computing sums or aggregates, as this creates a Cartesian fan-out that inflates aggregate values.
   - Single-Period Filters: For questions asking for a specific period (e.g. 'most recent year' or 'prior year'), specify the period filter clearly so queries filter to that single period rather than executing unbounded multi-year scans.
2. Structure & Strategy:
   - Chart slots are a layout suggestion (band + viz type). Filling every slot is optional. When a scope lock is present, write only the locked questions and leave unused bands empty. Write original chart.question, title, and purpose for each question you do include.
   - Never copy placeholder strings, slot names like "Independent drivers", or fill "{{question}}".
   - If the suggested strategy is a poor fit, choose the best alternative from the catalog, set strategy_id accordingly, and explain why.
   - Set template_id to the chosen strategy's layout template name (e.g. analytical-grid).
   - Diagnostic Capabilities: When a diagnostic capability applies to a target measure, investigate that measure using every declared use. A use may be an analytical strategy (trend, segment, measureDecomposition), a measure, a dimension, or another semantic-model item. Frame 3 to 5 focused questions that cover those uses rather than exhaustive breakdowns. When a scope lock is present, it replaces this rule: use only the locked strategies, measures, and dimensions.
{scope_lock}
3. Question Design:
   - Each chart.question must be a focused sub-question answerable with one query/chart.
   - When a scope lock is present, do not add complementary breakdowns. Otherwise, think across relations and include complementary breakdowns using same-table columns and join-related tables.
   - Set chart.topic, chart.components, and chart.measure_hints using the names from the grounding pack.
   - Specify the analytical operation applied (from available analytical operations) for each chart question.
   - Period comparisons: use one headline comparison chart, supported by categorical dimension breakdowns. Skip this when a scope lock does not include period comparison.
{sql_shape_rules}
4. Scope & Guidelines:
   - When a scope lock is present, write one question per locked strategy. Add one drill-down chart only when it needs a value found by that question, using the same strategy, measure, and dimensions. Otherwise, determine and frame the complementary charts needed to thoroughly answer the inquiry. Let the analytical requirements decide the questions without an artificial cap.{chart_limit}
   - Fill context_anchor on KPI charts only when supported by the data model.
   - Set plan.domain and plan.topics from the selected grounding pack.
""" + format_instruction_string
