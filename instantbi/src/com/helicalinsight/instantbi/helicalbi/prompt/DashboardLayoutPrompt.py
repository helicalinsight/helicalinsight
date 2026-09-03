dashboard_layout_prompt_string = """
You are composing a Helical Insight dashboard skeleton from InstantBI chat visualizations.

Return JSON only. Do not invent SQL, chart types, or Helical Report / .efwdd documents.
Pick one layout template, then place widgets on a 12-column grid using domain, topics, and each user_query -> viz pairing.

### TEMPLATE DECISION TABLE
{decision_table}

### LAYOUT TEMPLATES
ASCII sketches are in *.layout.txt files. Place widgets to match the chosen sketch on a 12-column grid.
{layout_catalog}

### GRID
- Columns: 12. x, y start at 0. Use w/h (or width/height).
- kind=kpi key metric card: w=3 h=2
- kind=filter: w=3 h=1, usually under the summary
- kind=summary: y=0, w=12, h=1 or 2
- kind=svg / image: compact icon w=2 h=2 or a divider w=12 h=1
- Typical chart (kind=viz): w=6 h=4. Table: w=12 h=4
- Do not use the word Dashboard in titles.

### PLAN
Honor this layout plan and theme. Do not change templateId unless the plan is empty.
{layout_plan}

### ITEMS
Each card is one chat visualization. Chat charts use the given component_id:
{item_cards}

### SELECTED FILTERS
Use these filter components as kind=filter widgets. Do not invent extra filter columns.
{filter_components}

### REQUIRED EXTRA TILES
You MUST add extra widgets that have empty data_model / viz_model.
Omit component_id on extras (summary / kpi / filter / svg) — the server assigns random ids. Never reuse a chat component_id.
1. kind=summary — exactly one insight banner using the planned summary title/text.
2. kind=kpi — 2 to 4 key-metric cards. title is the metric name (from domain, topics, or viz columns).
3. kind=filter — one tile per selected filter above. title and column must match. Set listeners to the chat component_ids.
4. kind=svg — at least one inline SVG in html (small icon or horizontal divider). Must include <svg viewBox="...">, no script, no foreignObject, no external URLs. Keep under 1200 characters.

Do not invent SQL.

### CSS / JS / HTML
Optional css/js/html on any widget. SVG only in html as inline markup.

### TASK
1. Use the planned templateId when present; otherwise choose exactly one from the decision table.
2. Emit one kind=viz widget for every input component_id. Do not invent extra charts.
3. Also emit the required summary, kpi, filter, and svg extras above.
4. Place summary at the top, then filters/kpis, then charts.

Username: {username}
"""

dashboard_plan_prompt_string = """
You are planning a Helical Insight dashboard from InstantBI chat visualizations.

Return JSON only. Do not invent SQL, chart types, or Helical Report / .efwdd documents.

### TEMPLATE DECISION TABLE
{decision_table}

### CONTEXT
Username: {username}
Domains: {domain}
Topics: {topics}
Viz types: {viz_types}
Chats:
{chat_context}

### TASK
1. Choose exactly one templateId from the decision table using domain, topics, and the viz mix.
2. Write one kind=summary banner: summary_title and summary_text from the user queries and insights.
3. Write layout_plan: a short placement plan (summary on top, then filters/KPIs, then charts).
4. Pick theme.color and theme.background as hex colors that fit the domain.
"""
