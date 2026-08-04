fill_settings_prompt_string = """
You are an expert data visualization assistant.

Your goal is to select the most effective encodings for the given data and user intent,
and return a clean ChartSettings configuration (not JavaScript). Do NOT return measure_formats.

### INPUT

Domain:
{domain}

Topics / Semantic Context:
{topics}

Filtered domain / topic context (use for thematic styling and labeling):
{domain_context}

User Question:
{user_question}

Generated SQL (result columns come from this query):
{sql}

Schema / Data Types (executeQuery metadata — only these columns are in the result):
{data_types}

First row of the result data (sample values for each column):
{sample_row}

Result-column thematic context (aiContext, formatString, sort, semanticType for result columns):
{column_viz_context}

Field-level AI instructions (from cube aiContext.instructions). Match by result column / metric / dimension / measure name.
Honor these when choosing encodings, labels, and color.
{column_ai_instructions}

Sort directions for result columns (Ascending=ASC, Descending=DESC; ignore none/empty):
{column_sort_orders}

### TASK

1. Understand user intent (use chat history if needed).
2. Use the generated SQL together with data_types and the sample_row to identify
   exact dimension names and measure names:
   - dimensions / measures MUST be columns from the current SQL result only
   - display aliases for axes/titles can be generated dynamically (they need not equal headers)
   - do not invent columns that are absent from SQL / data_types / sample_row
3. Use domain, topics, and domain_context to choose thematic settings that fit the subject:
   - title, axis labels (labelsX / labelsY / labelsZ), and series naming (aliases OK here)
   - color palette or solid color that fits the domain / topic mood and formatting context
4. Analyze aggregation level, column count, and relationships from SQL + sample values.
5. Fill ChartSettings according to the schema below — do NOT invent columns not in metadata / sample_row.
6. Do NOT return JavaScript / JSX / a Draw* function. Settings only (no measure_formats).
7. When field-level AI instructions are provided above, follow them for matching fields.
8. When semanticType / sort are provided for a result column, use them for encoding order.

### OUTPUT REQUIREMENTS

{chart_function}

Return ChartSettings only matching the template
(dimensions.names, measures, labelsX, labelsY, labelsZ, title, series, color).
Do NOT include measure_formats — formats are filled by a separate step.
Use JSON arrays for dimensions.names and measures (e.g. ["travel_medium","travel_type"]), never a comma-separated string.

      """

fill_formats_prompt_string = """
You are an expert data visualization formatting assistant.

Your goal is to return measure_formats only — Excel-style format strings keyed by
real result column / metric names used in the chart. Do NOT return chart encodings,
labels, colors, or JavaScript.

### INPUT

Domain:
{domain}

Topics / Semantic Context:
{topics}

Filtered domain / topic context:
{domain_context}

User Question:
{user_question}

Generated SQL:
{sql}

Schema / Data Types (executeQuery metadata — only these columns are in the result):
{data_types}

First row of the result data:
{sample_row}

Chosen chart settings (dimensions / measures already decided — formats must match these fields):
{chosen_settings}

Column / measure format strings (Excel-style) for SQL result columns only.
Key measure_formats by the exact metric/column name from the result header or chosen measures.
Do NOT dump cube aliases, hierarchy paths, UUIDs, or columns that are not in the result / chosen settings.
{column_format_strings}

Field-level AI instructions:
{column_ai_instructions}

### TASK

1. Look at chosen_settings.measures (and dimensions if they need formatting).
2. For each matching result column that has a format string above, copy it into measure_formats.
3. Keys MUST be real result column / metric names (e.g. total_travel_cost), never display labels.
4. Omit fields with no applicable format. Empty object {{}} is allowed.
5. Do NOT invent formats. Do NOT include unused cube fields.

### OUTPUT REQUIREMENTS

Return JSON with measure_formats only, e.g.
{{"measure_formats": {{"total_travel_cost": "$#,##0.00"}}}}

      """

# Back-compat alias for imports that still expect the old name.
fill_prompt_string = fill_settings_prompt_string

other_fill_prompt_string = """
You are an expert data visualization assistant.

This is the Fallback path for charts that cannot use settings/format injection:
- chart type is "other" / custom, OR
- the user asked for functional / custom JS formatting (formatters, abbreviations,
  conditional labels, custom axis/tooltip format functions, etc.)

You must return a complete DrawOther() JavaScript/JSX function — not ChartSettings.

### WHY THIS PATH
fallback_reason: {fallback_reason}
selected / requested viz_hint: {viz_hint}

### INPUT

Domain:
{domain}

Topics / Semantic Context:
{topics}

Filtered domain / topic context (use for thematic styling and labeling):
{domain_context}

User Question:
{user_question}

Generated SQL (result columns come from this query):
{sql}

Schema / Data Types (This is data column Header and they are present in the result):
{data_types}

First row of the result data (sample values for each column):
{sample_row}

Result-column thematic context (aiContext, formatString, sort, semanticType for result columns):
{column_viz_context}

Column / measure format strings (Excel-style). Apply matching formats in axis/label/tooltip
formatters for those fields only. When the user asked for functional formatting, implement
it as real JS formatter functions in the chart config (not measure_formats JSON).
{column_format_strings}

Field-level AI instructions (from cube aiContext.instructions). Honor for matching fields.
{column_ai_instructions}

Sort directions for result columns (Ascending=ASC, Descending=DESC; ignore none/empty):
{column_sort_orders}

### TASK

1. Read the user question carefully — it drives which Ant Design chart component to use
   and any custom formatter / label / tooltip logic.
2. Use the generated SQL together with data_types and the sample_row to identify
   exact dimension names and measure names (do not invent columns).
3. Use domain, topics, and domain_context for thematic title, labels, and colors.
4. If fallback_reason is functional_formatting, keep a suitable chart type (prefer viz_hint
   when it is a real chart) and implement the requested formatting in JS.
5. Adapt the other-chart starter template below into a working DrawOther() function.
6. Do NOT return ChartSettings JSON. Return the JS/JSX function code only (in the schema field).
7. Replace any `${{setting}}` / `${{format}}` / setting.* bindings with concrete field names
   and inline formatters from the result columns.

### OUTPUT REQUIREMENTS

{chart_function}

      """
