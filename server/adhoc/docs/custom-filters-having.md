# Custom column, filter, and having combinations

This document describes formData for **WHERE** (`filters`) and **HAVING** (`having`) when mixing:

- **Normal column** — metadata field `{ "name", "id" }` in `columns` / `filters` / `having`
- **Custom column** — formula in SELECT: `"custom": true` and `"column"` is the formula string
- **Custom filter / custom having** — `"custom": true` on the filter/having **item**, with nested `"column": { "column": "<formula>", "alias": "..." }`
- **Database function** — `"databaseFunction": { "functionName", "dataType", "parameters" }` on a filter or having item

Metadata used in examples:

- `location`: `HReportTest`
- `metadataFileName`: `Metadata_HReportTest.metadata`

`${n}` in `customFilterExpression` / `customHavingExpression` is the **array index** (0-based), not the item `id`.

---

## How the server reads a filter / having item

| Item flag | `column` shape | Operand used in SQL |
|---|---|---|
| no `custom` | `{ "name", "id" }` (flattened to FQDN before SQL) | quoted identifier |
| `"custom": true` | `{ "column": "<formula>", "alias": "..." }` | formula as-is (not quoted, not the alias) |
| `databaseFunction` present | any | **replaced** by the SQL from `databaseFunction` |
| having + `"function"` | any | wrap operand with the aggregate (`sum(...)`) |

If both `custom` and `databaseFunction` are set, the nested formula is **not** the WHERE/HAVING operand. The database-function SQL is used, then HAVING `function` wraps that result.

Non-aggregate custom formulas belong in **`filters` (WHERE)**. Aggregates belong in **`having`**, with `"function": "db.generic.aggregate.sum"` (same as a normal aggregate having item).

---

## Combination matrix

| # | SELECT | WHERE (`filters`) | HAVING | Typical SQL |
|---|---|---|---|---|
| 1 | Normal column | Custom filter | — | WHERE uses nested formula |
| 2 | Custom column | Custom filter (same formula) | — | WHERE uses formula, not alias |
| 3 | Normal column | Custom filter | Normal having | WHERE formula + HAVING `sum(travel_id)` |
| 4 | Custom aggregate column | — | Custom having | HAVING `sum("source_id")` |
| 5 | Custom column(s) | Custom filter | Custom having | both formulas inlined |
| 6 | Custom column | **Normal** filter | Custom having | normal WHERE + custom HAVING |
| 7 | Custom column | Custom filter **and** normal filter | — | both in WHERE via `${0} AND ${1}` |
| 8 | Normal column | Normal filter + `databaseFunction` | — | `concat(...)` / `year(...)` |
| 9 | Custom column | Custom filter + `databaseFunction` | — | db-function SQL, not alias |
| 10 | Custom aggregate | — | Custom having + `databaseFunction` + `function` | `sum(<db-fn SQL>)` |
| 11 | Custom column **not** in SELECT | Custom filter | — | formula still inlined in WHERE |
| 12 | Mix of 5 + concat db-fn + normal having | all of the above | all of the above | full report payload |

---

## Shared JSON pieces

**Normal SELECT column**

```json
{
  "column": {
    "name": "HIUSER.travel_details.mode_of_payment",
    "id": "1073"
  },
  "alias": "payment mode",
  "floatingType": "discrete"
}
```

**Custom SELECT column (non-aggregate)**

```json
{
  "column": "\"booking_platform\"",
  "alias": "bk_pf",
  "custom": true,
  "floatingType": "discrete"
}
```

**Custom SELECT column (aggregate)**

```json
{
  "column": "\"source_id\"",
  "alias": "s_id",
  "custom": true,
  "aggregate": true,
  "aggregateList": ["db.generic.aggregate.sum"],
  "floatingType": "discrete"
}
```

**Normal filter** (`condition: CUSTOM` is the operator type; this is **not** a custom-column filter)

```json
{
  "values": ["'Credit')"],
  "mode": "auto",
  "operator": "AND",
  "dataType": "java.lang.String",
  "customCondition": " IN (",
  "encloseInQuotes": false,
  "alias": "payment mode",
  "label": "payment mode",
  "isCustomValue": true,
  "column": {
    "name": "HIUSER.travel_details.mode_of_payment",
    "id": "1073"
  },
  "id": 0,
  "condition": "CUSTOM"
}
```

**Custom filter** (item-level `custom: true` + nested formula)

```json
{
  "values": ["'Makemytrip')"],
  "mode": "auto",
  "operator": "AND",
  "dataType": "java.lang.String",
  "customCondition": " IN (",
  "encloseInQuotes": false,
  "alias": "bk_pf",
  "label": "bk_pf",
  "isCustomValue": true,
  "custom": true,
  "column": {
    "column": "\"booking_platform\"",
    "alias": "bk_pf"
  },
  "id": 1,
  "condition": "CUSTOM"
}
```

**Normal having**

```json
{
  "values": ["501501)"],
  "mode": "auto",
  "operator": "AND",
  "dataType": "java.lang.Integer",
  "customCondition": " IN (",
  "encloseInQuotes": false,
  "alias": "sum_travel_id",
  "label": "sum_travel_id",
  "isCustomValue": true,
  "column": {
    "name": "HIUSER.travel_details.travel_id",
    "id": "1064"
  },
  "function": "db.generic.aggregate.sum",
  "id": 1,
  "condition": "CUSTOM"
}
```

**Custom having**

```json
{
  "values": ["10)"],
  "mode": "auto",
  "operator": "AND",
  "dataType": "java.lang.Integer",
  "customCondition": " IN (",
  "encloseInQuotes": false,
  "alias": "s_id",
  "label": "s_id",
  "isCustomValue": true,
  "custom": true,
  "column": {
    "column": "\"source_id\"",
    "alias": "s_id"
  },
  "function": "db.generic.aggregate.sum",
  "id": 2,
  "condition": "CUSTOM"
}
```

**Database function (concat) on a normal filter**

```json
"databaseFunction": {
  "functionName": "sql.text.concat",
  "dataType": "text",
  "parameters": {
    "string1": "travel_details.source",
    "string2": "travel_details.destination"
  }
}
```

**Database function (year)** — same object used on custom or normal items

```json
"databaseFunction": {
  "functionName": "sql.dateTime.year",
  "dataType": "numeric",
  "parameters": {
    "datetime": "meeting_details.meeting_date"
  }
}
```

Do **not** put `"custom": true` on the nested `column` object. Only the filter/having **item** has `"custom": true`. Nested object is `{ "column", "alias" }` only.

---

## 1. Custom filter + normal column

SELECT is a metadata column. WHERE is a custom-column filter (formula). The custom filter does **not** have to match the SELECT column.

```json
{
  "location": "HReportTest",
  "metadataFileName": "Metadata_HReportTest.metadata",
  "columns": [
    {
      "column": { "name": "HIUSER.travel_details.mode_of_payment", "id": "1073" },
      "alias": "payment mode",
      "floatingType": "discrete"
    }
  ],
  "functions": {
    "groupBy": [{ "column": "payment mode", "custom": true }]
  },
  "filters": [
    {
      "values": ["'Makemytrip')"],
      "mode": "auto",
      "operator": "AND",
      "dataType": "java.lang.String",
      "customCondition": " IN (",
      "encloseInQuotes": false,
      "alias": "bk_pf",
      "label": "bk_pf",
      "isCustomValue": true,
      "custom": true,
      "column": { "column": "\"booking_platform\"", "alias": "bk_pf" },
      "id": 0,
      "condition": "CUSTOM"
    }
  ],
  "customFilterExpression": " ${0} ",
  "limitBy": 10,
  "prependTableNameToAlias": false
}
```

**SQL:** `WHERE ("booking_platform" IN ('Makemytrip'))`  
Not `"bk_pf"`. SELECT still shows payment mode.

---

## 2. Custom filter + custom column

SELECT custom alias `bk_pf`. WHERE uses the same nested formula. Alias lookup in SELECT is **not** required; the filter carries the formula.

```json
{
  "columns": [
    {
      "column": "\"booking_platform\"",
      "alias": "bk_pf",
      "custom": true,
      "floatingType": "discrete"
    }
  ],
  "functions": {
    "groupBy": [{ "column": "bk_pf", "custom": true }]
  },
  "filters": [
    {
      "values": ["'Makemytrip')"],
      "customCondition": " IN (",
      "encloseInQuotes": false,
      "alias": "bk_pf",
      "custom": true,
      "column": { "column": "\"booking_platform\"", "alias": "bk_pf" },
      "id": 0,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.String",
      "mode": "auto",
      "operator": "AND",
      "label": "bk_pf"
    }
  ],
  "customFilterExpression": " ${0} "
}
```

**SQL:**  
`SELECT "booking_platform" AS "bk_pf" ... WHERE ("booking_platform" IN ('Makemytrip'))`

---

## 3. Custom filter + having + normal column

Normal column in SELECT, custom formula in WHERE, **normal** aggregate in HAVING.

```json
{
  "columns": [
    {
      "column": { "name": "HIUSER.travel_details.mode_of_payment", "id": "1073" },
      "alias": "payment mode",
      "floatingType": "discrete"
    }
  ],
  "functions": {
    "groupBy": [{ "column": "payment mode", "custom": true }]
  },
  "filters": [
    {
      "values": ["'Makemytrip')"],
      "customCondition": " IN (",
      "custom": true,
      "column": { "column": "\"booking_platform\"", "alias": "bk_pf" },
      "alias": "bk_pf",
      "id": 0,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.String",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "bk_pf"
    }
  ],
  "having": [
    {
      "values": ["501501)"],
      "customCondition": " IN (",
      "alias": "sum_travel_id",
      "column": { "name": "HIUSER.travel_details.travel_id", "id": "1064" },
      "function": "db.generic.aggregate.sum",
      "id": 1,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.Integer",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "sum_travel_id"
    }
  ],
  "customFilterExpression": " ${0} ",
  "customHavingExpression": " ${0} "
}
```

**SQL:**  
`WHERE ("booking_platform" IN ('Makemytrip'))`  
`HAVING (sum("HIUSER"."travel_details"."travel_id") IN (501501))`

---

## 4. Custom column + having (custom having)

Aggregate custom column in SELECT. Filter it in HAVING with the nested formula + `function`.

```json
{
  "columns": [
    {
      "column": "\"source_id\"",
      "alias": "s_id",
      "custom": true,
      "aggregate": true,
      "aggregateList": ["db.generic.aggregate.sum"],
      "floatingType": "discrete"
    }
  ],
  "functions": {
    "aggregate": [
      {
        "column": "\"source_id\"",
        "function": "db.generic.aggregate.sum",
        "alias": "s_id",
        "custom": true
      }
    ]
  },
  "having": [
    {
      "values": ["10)"],
      "customCondition": " IN (",
      "custom": true,
      "column": { "column": "\"source_id\"", "alias": "s_id" },
      "function": "db.generic.aggregate.sum",
      "alias": "s_id",
      "id": 0,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.Integer",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "s_id"
    }
  ],
  "customHavingExpression": " ${0} "
}
```

**SQL:** `HAVING (sum("source_id") IN (10))`  
Not `sum("s_id")` and not `"s_id" IN (10)`.

---

## 5. Custom column + custom filter + custom having

Full custom report: discrete custom column in WHERE, aggregate custom column in HAVING.

```json
{
  "columns": [
    {
      "column": "\"booking_platform\"",
      "alias": "bk_pf",
      "custom": true,
      "floatingType": "discrete"
    },
    {
      "column": "\"source_id\"",
      "alias": "s_id",
      "custom": true,
      "aggregate": true,
      "aggregateList": ["db.generic.aggregate.sum"],
      "floatingType": "discrete"
    }
  ],
  "functions": {
    "aggregate": [
      { "column": "\"source_id\"", "function": "db.generic.aggregate.sum", "alias": "s_id", "custom": true }
    ],
    "groupBy": [{ "column": "bk_pf", "custom": true }]
  },
  "filters": [
    {
      "values": ["'Makemytrip')"],
      "customCondition": " IN (",
      "custom": true,
      "column": { "column": "\"booking_platform\"", "alias": "bk_pf" },
      "alias": "bk_pf",
      "id": 0,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.String",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "bk_pf"
    }
  ],
  "having": [
    {
      "values": ["10)"],
      "customCondition": " IN (",
      "custom": true,
      "column": { "column": "\"source_id\"", "alias": "s_id" },
      "function": "db.generic.aggregate.sum",
      "alias": "s_id",
      "id": 0,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.Integer",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "s_id"
    }
  ],
  "customFilterExpression": " ${0} ",
  "customHavingExpression": " ${0} "
}
```

**SQL:**  
`WHERE ("booking_platform" IN ('Makemytrip'))`  
`HAVING (sum("source_id") IN (10))`

---

## 6. Custom column + custom having + normal filter

Custom aggregate in SELECT/HAVING. WHERE is a **normal** metadata filter.

```json
{
  "columns": [
    {
      "column": { "name": "HIUSER.travel_details.mode_of_payment", "id": "1073" },
      "alias": "payment mode",
      "floatingType": "discrete"
    },
    {
      "column": "\"source_id\"",
      "alias": "s_id",
      "custom": true,
      "aggregate": true,
      "aggregateList": ["db.generic.aggregate.sum"],
      "floatingType": "discrete"
    }
  ],
  "functions": {
    "aggregate": [
      { "column": "\"source_id\"", "function": "db.generic.aggregate.sum", "alias": "s_id", "custom": true }
    ],
    "groupBy": [{ "column": "payment mode", "custom": true }]
  },
  "filters": [
    {
      "values": ["'Credit')"],
      "customCondition": " IN (",
      "column": { "name": "HIUSER.travel_details.mode_of_payment", "id": "1073" },
      "alias": "payment mode",
      "id": 0,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.String",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "payment mode"
    }
  ],
  "having": [
    {
      "values": ["10)"],
      "customCondition": " IN (",
      "custom": true,
      "column": { "column": "\"source_id\"", "alias": "s_id" },
      "function": "db.generic.aggregate.sum",
      "alias": "s_id",
      "id": 0,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.Integer",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "s_id"
    }
  ],
  "customFilterExpression": " ${0} ",
  "customHavingExpression": " ${0} "
}
```

**SQL:**  
`WHERE (<quoted payment column> IN ('Credit'))`  
`HAVING (sum("source_id") IN (10))`

---

## 7. Custom column + custom filter + normal filter

Two WHERE items: index `0` normal, index `1` custom. Expression `${0} AND ${1}`.

```json
{
  "columns": [
    {
      "column": { "name": "HIUSER.travel_details.mode_of_payment", "id": "1073" },
      "alias": "payment mode",
      "floatingType": "discrete"
    },
    {
      "column": "\"booking_platform\"",
      "alias": "bk_pf",
      "custom": true,
      "floatingType": "discrete"
    }
  ],
  "functions": {
    "groupBy": [
      { "column": "payment mode", "custom": true },
      { "column": "bk_pf", "custom": true }
    ]
  },
  "filters": [
    {
      "values": ["'Credit')"],
      "customCondition": " IN (",
      "column": { "name": "HIUSER.travel_details.mode_of_payment", "id": "1073" },
      "alias": "payment mode",
      "id": 0,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.String",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "payment mode"
    },
    {
      "values": ["'Makemytrip')"],
      "customCondition": " IN (",
      "custom": true,
      "column": { "column": "\"booking_platform\"", "alias": "bk_pf" },
      "alias": "bk_pf",
      "id": 1,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.String",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "bk_pf"
    }
  ],
  "customFilterExpression": " ${0} AND ${1} "
}
```

**SQL:**  
`WHERE (<payment> IN ('Credit') AND "booking_platform" IN ('Makemytrip'))`

---

## Database function combinations

### 8. Normal filter + `databaseFunction` (concat)

No `"custom": true` on the item. Same as existing `sr-dest` / HReportTest year filters.

```json
{
  "filters": [
    {
      "values": ["'AgraMumbai')"],
      "customCondition": " IN (",
      "alias": "sr-dest",
      "databaseFunction": {
        "functionName": "sql.text.concat",
        "dataType": "text",
        "parameters": {
          "string1": "travel_details.source",
          "string2": "travel_details.destination"
        }
      },
      "column": { "name": "HIUSER.travel_details.source", "id": "1069" },
      "id": 0,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.String",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "sr-dest"
    }
  ],
  "customFilterExpression": " ${0} "
}
```

**SQL:** `WHERE (concat(...) IN ('AgraMumbai'))` (exact function text is dialect-specific).

### 9. Custom filter + `databaseFunction`

Item has `"custom": true` **and** `databaseFunction`. Operand is the **database-function SQL**, not the nested formula and not the alias.

```json
{
  "filters": [
    {
      "values": ["2015)"],
      "customCondition": " IN (",
      "custom": true,
      "column": { "column": "\"meeting_date\"", "alias": "mtg_yr" },
      "databaseFunction": {
        "functionName": "sql.dateTime.year",
        "dataType": "numeric",
        "parameters": { "datetime": "meeting_details.meeting_date" }
      },
      "alias": "mtg_yr",
      "id": 0,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.Integer",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "mtg_yr"
    }
  ],
  "customFilterExpression": " ${0} "
}
```

**SQL:** `WHERE (year(...) IN (2015))` — not `"mtg_yr"`.

### 10. Custom having + `databaseFunction` + `function`

Order applied by the server: database function first, then aggregate wrap.

```json
{
  "having": [
    {
      "values": ["10)"],
      "customCondition": " IN (",
      "custom": true,
      "column": { "column": "\"source_id\"", "alias": "s_id" },
      "databaseFunction": {
        "functionName": "sql.numeric.abs",
        "dataType": "numeric",
        "parameters": { "number": "source_id" }
      },
      "function": "db.generic.aggregate.sum",
      "alias": "s_id",
      "id": 0,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.Integer",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "s_id"
    }
  ],
  "customHavingExpression": " ${0} "
}
```

**SQL:** `HAVING (sum(abs("source_id")) IN (10))`

### 11. Custom filter not in SELECT

Same nested custom filter as (1)/(2). `columns` has no `bk_pf`. Formula still comes from `filters[].column.column`.

### 12. Full mix (normal + custom + concat + both havings)

```json
{
  "location": "HReportTest",
  "metadataFileName": "Metadata_HReportTest.metadata",
  "columns": [
    {
      "column": { "name": "HIUSER.travel_details.mode_of_payment", "id": "1073" },
      "alias": "payment mode",
      "floatingType": "discrete"
    },
    {
      "column": "\"booking_platform\"",
      "alias": "bk_pf",
      "custom": true,
      "floatingType": "discrete"
    },
    {
      "column": "\"source_id\"",
      "alias": "s_id",
      "custom": true,
      "aggregate": true,
      "aggregateList": ["db.generic.aggregate.sum"],
      "floatingType": "discrete"
    }
  ],
  "functions": {
    "aggregate": [
      { "column": "\"source_id\"", "function": "db.generic.aggregate.sum", "alias": "s_id", "custom": true }
    ],
    "groupBy": [
      { "column": "payment mode", "custom": true },
      { "column": "bk_pf", "custom": true }
    ]
  },
  "filters": [
    {
      "values": ["'Credit')"],
      "customCondition": " IN (",
      "column": { "name": "HIUSER.travel_details.mode_of_payment", "id": "1073" },
      "alias": "payment mode",
      "id": 0,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.String",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "payment mode"
    },
    {
      "values": ["'Makemytrip')"],
      "customCondition": " IN (",
      "custom": true,
      "column": { "column": "\"booking_platform\"", "alias": "bk_pf" },
      "alias": "bk_pf",
      "id": 1,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.String",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "bk_pf"
    },
    {
      "values": ["'AgraMumbai')"],
      "customCondition": " IN (",
      "alias": "sr-dest",
      "databaseFunction": {
        "functionName": "sql.text.concat",
        "dataType": "text",
        "parameters": {
          "string1": "travel_details.source",
          "string2": "travel_details.destination"
        }
      },
      "column": { "name": "HIUSER.travel_details.source", "id": "1069" },
      "id": 2,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.String",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "sr-dest"
    }
  ],
  "having": [
    {
      "values": ["501501)"],
      "customCondition": " IN (",
      "column": { "name": "HIUSER.travel_details.travel_id", "id": "1064" },
      "function": "db.generic.aggregate.sum",
      "alias": "sum_travel_id",
      "id": 1,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.Integer",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "sum_travel_id"
    },
    {
      "values": ["10)"],
      "customCondition": " IN (",
      "custom": true,
      "column": { "column": "\"source_id\"", "alias": "s_id" },
      "function": "db.generic.aggregate.sum",
      "alias": "s_id",
      "id": 2,
      "condition": "CUSTOM",
      "isCustomValue": true,
      "dataType": "java.lang.Integer",
      "mode": "auto",
      "operator": "AND",
      "encloseInQuotes": false,
      "label": "s_id"
    }
  ],
  "customFilterExpression": " ${0} AND ${1} AND ${2} ",
  "customHavingExpression": " ${0} AND ${1} ",
  "limitBy": 10,
  "prependTableNameToAlias": false
}
```

| Index | Clause | Kind | Operand |
|---|---|---|---|
| filters `${0}` | WHERE | normal | payment mode |
| filters `${1}` | WHERE | custom | `"booking_platform"` |
| filters `${2}` | WHERE | db-fn | `concat(source, destination)` |
| having `${0}` | HAVING | normal + sum | `sum(travel_id)` |
| having `${1}` | HAVING | custom + sum | `sum("source_id")` |

---

## Other combinations (short)

| Combination | Where it goes | Notes |
|---|---|---|
| Custom column in SELECT, no filter | — | SELECT emits formula `AS` alias; existing behaviour |
| Normal having only | `having` | `{name,id}` + `function`; no `custom` |
| Custom filter + normal having + custom having | `filters` + `having[0]` + `having[1]` | `${0} AND ${1}` on having |
| Two custom filters | `filters` | `${0} AND ${1}`; each item `custom: true` |
| Custom having without SELECT custom column | `having` only | Formula is on the having item; SELECT can omit `s_id` |
| `databaseFunction` on normal having | `having` | db-fn SQL, then `function` wrap |
| Custom filter + concat db-fn + custom having | mix of 7 + 8 + 4 | concat item is **not** `custom: true` unless the operand is a custom formula |

---

## Rules (do / do not)

**Do**

- Put `"custom": true` on the **filter/having item**.
- Put the SQL formula in nested `column.column`.
- Use `filters` for non-aggregate formulas; `having` + `function` for aggregates.
- Keep `databaseFunction` on the same item that should receive that SQL.

**Do not**

- Put `"custom": true` inside the nested `column` object.
- Send only `"column": { "alias": "bk_pf" }` with no inner formula.
- Use the SELECT alias (`bk_pf` / `s_id`) as the SQL operand.
- Put an aggregate custom item in `filters` (use `having`).
- Treat `${n}` as the item `id` field.
