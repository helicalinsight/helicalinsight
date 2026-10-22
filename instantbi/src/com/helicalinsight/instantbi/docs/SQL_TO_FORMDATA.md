# SQL → formData: functions, aggregates, custom columns

How InstantBI turns agent SQL into Helical `formData` wire JSON — especially **database functions**, **aggregates**, **GROUP BY**, **HAVING**, **filters**, **`rm_cols_in_filter`**, and **custom columns**.

Sample getFunctions payload: [`tests/servicefunctionresponse.json`](../tests/servicefunctionresponse.json).  
Functional tests: [`tests/functional/test_sql_to_formdata.py`](../tests/functional/test_sql_to_formdata.py).

---

## Pipeline overview

```
SQL text
   │
   ▼
getFunctions API ──► FunctionCatalog  (aggregates + databaseFunctions by signature)
metadata get     ──► column index     (FQ name + column id)
   │
   ▼
parse_sql (sqlglot) ──► ParsedQuery
   │                     selects / where / having / group_by / order_by
   ▼
parts (independent) ──► assemble_form_data
   │
   ├── select_parts      → formData.columns[]
   ├── filters_parts     → formData.filters[]
   ├── having_parts      → formData.having[]
   ├── groupby_parts     → formData.functions.groupBy[]
   ├── functions_parts   → formData.functions.aggregate[]
   └── database_function_parts → debug summary only (_parts)
```

Entry point: `helicalbi.sql_to_formdata.sql_to_form_data` / `assemble_form_data`.

Dialect comes from getFunctions `reference` (via `DialectMapper`), overridable by caller.

---

## getFunctions catalog

`FunctionCatalog.from_api_payload` flattens:

| API section | Use |
|-------------|-----|
| `response.functions` | Aggregate / groupBy / orderBy keys (`db.generic.aggregate.sum`, …) |
| `response.databaseFunctions.{category}` | Non-aggregate SQL fns (`date`, `dateTime`, `string`, …) — all categories merged into one map |
| `response.reference` | Dialect hint (`postgresql`, …) |

Each database-function entry typically has:

```json
{
  "key": "sql.dateTime.month",
  "value": "MONTH",
  "signature": "extract(month from ${datetime})",
  "returns": "numeric",
  "parameters": [{ "name": "datetime", "column": true }],
  "description": "…"
}
```

Matching is **deterministic (no LLM)**:

1. AST / SQL name lookup (`MONTH`, `EXTRACT`, sqlglot aliases like `TIME_TO_STR` → `TO_CHAR`)
2. EXTRACT unit → unit-specific entry (`MONTH`) via value or signature needle
3. Fuzzy **signature** match across all categories (prefer more literal tokens; description is a weak tie-breaker)
4. On total miss → treat as **custom** (where the part allows)

Wire expression string for filters/having (from catalog ``value`` + quoted column params).
Nested functions expand recursively::

```json
"MONTH (\"sampletraveldata.public.travel_details.travel_date\")"
"LENGTH (CONCAT (\"sampletraveldata.public.travel_details.destination\", ' x'))"
"TO_CHAR (DATETRUNC ('MONTH', \"sampletraveldata.public.travel_details.travel_date\"), 'YYYY-MM')"
```

Built by `to_wire_database_function_expression()`. (Onion shape via `to_wire_database_function()` remains available for nested catalog work / debug.)

---

## Decision matrix (what goes on the wire)

| SQL piece | Catalog match? | Wire result |
|-----------|----------------|-------------|
| SELECT plain column | n/a | `column: { name, id }`, no custom |
| SELECT aggregate `SUM(col)` | aggregate key | `aggregate: true`, `aggregateList: [db.generic.aggregate.sum]`, column = `{name,id}` |
| SELECT DB fn / expression | **any** (mapped or not) | **Always custom column**: `column` = SQL string, `custom: true`, `usedColumns` |
| SELECT `SUM(DB_FN(col))` | nested DB fn mapped or not | Custom column expression for the inner part + aggregate flags (see select part) |
| WHERE plain column | n/a | Filter with `{name,id}` column + condition map |
| WHERE DB fn (e.g. `extract(month from …)`) | **match** | Keep column `{name,id}`, set `databaseFunction` string e.g. `MONTH ("fq.col")`, **no** `custom` |
| WHERE DB fn | **miss** | `custom: true`, `column` = SQL string, `usedColumns` |
| HAVING aggregate / DB fn | same as filters | Same match → `databaseFunction` / miss → custom |
| GROUP BY | n/a | `functions.groupBy[]` entries `{ column: "<alias>", custom: true }` for non-aggregate selects |

**Important asymmetry:** SELECT always emits mapped DB functions as **custom SQL columns** (Helical `SimpleSelectFragment` expects that). Filters/HAVING emit **`databaseFunction`** when the catalog/signature matches.

---

## Custom columns (`formData.columns`)

File: `sql_components/select_parts.py`

A column is custom when `SelectItem.is_custom` **or** `database_function_sql` is set:

```json
{
  "column": "EXTRACT(MONTH FROM travel_details.travel_date)",
  "alias": "Travel Month",
  "custom": true,
  "usedColumns": ["sampletraveldata.public.travel_details.travel_date"]
}
```

Rules:

- `column` is the **expression SQL**, not `{name, id}`
- `usedColumns` lists FQ physical columns touched by the expression
- If wrapped in an aggregate: also `aggregate: true` + `aggregateList`
- ORDER BY / GROUP BY expressions not in SELECT are **materialized as hidden columns** (`hidden: true`, `includeInResultset: true`) so the engine can order/group by alias

Plain dimension / measure columns stay:

```json
{
  "column": { "name": "sampletraveldata.public.travel_details.travel_cost", "id": "2866" },
  "alias": "Travel Cost",
  "aggregate": true,
  "aggregateList": ["db.generic.aggregate.sum"]
}
```

---

## Aggregates

### Parsing

`functions` from getFunctions → `aggregate_by_sql` / `aggregate_by_key`.  
`SUM` / `AVG` / `COUNT` map to keys like `db.generic.aggregate.sum`.  
`COUNT(DISTINCT col)` stacks as `[count, distinct]`; `DISTINCT(COUNT(col))` stacks as `[distinct, count]`.

### On columns

Select part sets `aggregate` + `aggregateList` on the wire column.

### On `formData.functions.aggregate`

`functions_parts.build_functions` copies aggregate columns into:

```json
"functions": {
  "aggregate": [
    {
      "column": { "name": "…", "id": "…" },
      "function": "db.generic.aggregate.sum",
      "alias": "Travel Cost"
    }
  ]
}
```

If the column is custom, the aggregate entry also gets `"custom": true`.

---

## GROUP BY

File: `sql_components/groupby_parts.py`

When SQL has a `GROUP BY`:

1. Take every **non-aggregate** formData column (including hidden ones that are `includeInResultset`)
2. Emit `{ "column": "<alias>", "custom": true }`
3. If somehow empty, fall back to parsed group-by names

```json
"functions": {
  "groupBy": [
    { "column": "Booking Platform", "custom": true },
    { "column": "Travel Month", "custom": true }
  ]
}
```

`custom: true` here means “group by this formData column alias”, not “SQL custom expression”.

ORDER BY directions are attached as `order: "asc"|"desc"` on the matching **column** object; there is no separate `functions.orderBy` list in current wire output.

---

## Filters (WHERE)

File: `sql_components/filters_parts.py`

1. Map comparison to UI condition (`EQUALS`, `CONTAINS`, `IS_ONE_OF`, …)
2. Apply `_apply_database_function_or_custom`:
   - **Catalog match** → `databaseFunction` SQL string (`MONTH ("fq.col")`); keep resolved column `{name,id}`; set `usedColumns`
   - **Expression but no match** → `custom: true`, replace `column` with SQL string + `usedColumns`
3. Condition transform fills `values` / `customCondition` / etc.

### Match example

`WHERE extract(month from travel_details.travel_date) = 3`

```json
{
  "column": { "name": "sampletraveldata.public.travel_details.travel_date", "id": "2859" },
  "condition": "EQUALS",
  "values": [3],
  "databaseFunction": "MONTH (\"sampletraveldata.public.travel_details.travel_date\")",
  "usedColumns": ["sampletraveldata.public.travel_details.travel_date"]
}
```

### Miss example

`WHERE totally_unknown_fn(travel_details.travel_date) = 1`

```json
{
  "column": "TOTALLY_UNKNOWN_FN(travel_details.travel_date)",
  "custom": true,
  "usedColumns": ["sampletraveldata.public.travel_details.travel_date"],
  "values": [1]
}
```

Aggregate predicates in WHERE are skipped by the filters part and emitted under **having**.

---

## Remove SELECT columns that are also filters (`rm_cols_in_filter`)

### What changes on the chart

With the feature on, the chart shows the comparison the person asked for. The condition they asked for stays on the report as a filter. That same field is taken off the chart, so it is not drawn again as a bar, slice, or column. The chart type is chosen from the fields that remain, so a filter does not force an extra split.

| Question | Filter the viewer still sees | What the chart shows |
|----------|-------------------------------|----------------------|
| Travel cost by booking platform for March | Month is March | Booking platform and travel cost. March is not its own group. |
| Destinations where travel cost is above 100 | Travel cost is above 100 | The destinations that qualify. Cost is the cutoff, so it is not also a bar. |
| Travel cost above 100, and that is the only thing to show | Travel cost is above 100 | The cost number stays. The chart is not left blank. |
| How many trips went to Paris | Destination is Paris | The count stays. Paris stays as the filter. |
| International travel cost by month, when International is part of the cost formula | No extra report filter for trip type | Month and that cost both stay. |

With the feature off, the filtered field is on the chart as well as in the filter. March can appear as its own column even though every row is already March, and that extra grouping can change the chart (a simple bar can become a chart split by month).

### How the wire payload changes

After filters and HAVING are built, `assemble_form_data` can drop SELECT columns that repeat a filter field (`drop_select_columns_also_filtered` in `assembler.py`). The filter stays. The column is removed from `formData.columns`, and `functions.groupBy` is built from the columns that remain.

| Case | `rm_cols_in_filter: true` |
|------|---------------------------|
| Dimension in WHERE or HAVING, other SELECT columns exist | Drop the dimension from columns and groupBy |
| Aggregate in HAVING on the same measure, other SELECT columns exist | Drop that measure |
| `COUNT(col) WHERE col = …` | Keep the measure (WHERE does not match an aggregate) |
| Only one SELECT column | Keep it, even if it is also filtered |
| `SUM(col) FILTER (WHERE …)` | Keep it; that WHERE is inside the SELECT, not a query filter |
| `rm_cols_in_filter: false` | Keep every SELECT column; filters are unchanged |

Example (`true`): `SELECT booking_platform, EXTRACT(MONTH FROM travel_date), SUM(travel_cost) … WHERE EXTRACT(MONTH FROM travel_date) = 3` keeps **Booking Platform** and **Travel Cost**. **Travel Month** remains only as a filter.

### Usage

Default is `feature_flags.rm_cols_in_filter` in [`application_config.yaml`](../helicalbi/config/application_config.yaml) (currently `false`). If that key is omitted, the code fallback is `true`.

A request overrides the config. The query string wins, then top-level JSON, then `input.rm_cols_in_filter`.

```
POST /interactive?rm_cols_in_filter=true
```

```json
{ "rm_cols_in_filter": false, "input": { "inputString": "…", "rm_cols_in_filter": true } }
```

The top-level `false` wins over `input.rm_cols_in_filter`.

Java proxy (`InstantBIController` `/ai/interactive-chat`). Omit the parameter to use the yaml flag. It is forwarded as `input.rm_cols_in_filter`:

```
/ai/interactive-chat?removeColsInFilter=true
```

Accepted values: `true` / `false`, `1` / `0`, `yes` / `no`, `y` / `n`, `on` / `off`. Anything else keeps the configured default.

Direct callers pass the same flag into `sql_to_form_data(..., rm_cols_in_filter=True)`.

---

## HAVING

File: `sql_components/having_parts.py`

Same DB-function / custom rules as filters (`_apply_database_function_or_custom`).

Differences:

- Includes `parsed.having_filters` **and** WHERE items that carry an `aggregate`
- Sets `function` to the aggregate key when present
- Adds `dataType` from metadata / inference

```json
{
  "column": { "name": "…travel_cost", "id": "2866" },
  "function": "db.generic.aggregate.sum",
  "condition": "IS_GREATER_THAN",
  "values": [1000],
  "…": "…"
}
```

---

## Signature matching (filters / having / catalog build)

Implemented in `functions_catalog.py` (`build_database_function`, `match_signature`).

For `EXTRACT(month FROM travel_date)`:

1. Detect `Extract` AST → try unit-specific fn `MONTH`
2. Prefer signature `extract(month from ${datetime})` over generic `extract(${unit} from ${date})` (higher literal specificity)
3. Fill parameters in **signature / catalog parameter order** (columns as FQ refs, literals as constants)

Nested functions (e.g. `LENGTH(CAST(CONCAT(…) AS VARCHAR))`) build nested catalog dicts; if any nested fn is unmapped, the whole expression fails mapping → custom path for filters/having (and custom column for SELECT).

---

## Parse model fields (shared)

`SelectItem` / `FilterItem`:

| Field | Meaning |
|-------|---------|
| `database_function` | Catalog-shaped dict after successful match (or `None`) |
| `database_function_sql` | SQL of the expression (always set for Func sides so custom fallback works) |
| `is_custom` / `custom_expression` | SELECT-only custom path |
| `used_columns` | Physical columns referenced |
| `aggregate` / `aggregates` | Aggregate key(s) |

Parser helper `_side_column_meta` always stores `database_function_sql` for function expressions so parts can fall back to custom when `database_function` is `None`.

---

## Debug part

`database_function_parts.attach_database_functions` does **not** change wire columns/filters. It only summarizes applied expressions / catalog hits for `include_parts=True` (`formData._parts.databaseFunction`).

---

## Quick mental model

```
                ┌──────────────────────┐
   SELECT expr  │ always custom column │  (SQL string + usedColumns)
                └──────────────────────┘

                ┌─────────────────────────────────────────────┐
 WHERE / HAVING │ match? ──yes──► databaseFunction string + col ref │
   DB function  │         └──no──► custom:true + SQL column   │
                └─────────────────────────────────────────────┘

   SUM/AVG/…    ► aggregateList on column + functions.aggregate[]

   GROUP BY     ► functions.groupBy[{ column: alias, custom: true }]
```

---

## Key source files

| File | Role |
|------|------|
| `functions_catalog.py` | getFunctions index, build/match DB functions, wire expression string |
| `parser/sql_parser.py` | SQL → `ParsedQuery` |
| `sql_components/select_parts.py` | columns + hidden ORDER/GROUP materialization |
| `sql_components/filters_parts.py` | WHERE; match → `databaseFunction` / miss → custom |
| `sql_components/having_parts.py` | HAVING (reuses filter DB-fn helper) |
| `sql_components/groupby_parts.py` | `functions.groupBy` |
| `sql_components/functions_parts.py` | merge groupBy + aggregate lists |
| `assembler.py` | end-to-end `sql_to_form_data`; drops filtered SELECT columns when `rm_cols_in_filter` is true |
