# InstantBI VizModel — Bugzilla requirement

**Summary:** InstantBI VizModel — backend builds visualization model; frontend renders it

**Description:**

InstantBI today often relies on server-generated visualization templates (`vf_template`) for charts. We need a declarative visualization model (`VizModel`) that the backend fills and returns to the client so the frontend can render charts without depending on template execution. The model is already defined in InstantBI (`VizModel` with `data`, `chart`, and `properties`). Backend filling should stay mostly deterministic (minimal LLM). This ticket tracks the BE→FE contract and acceptance criteria for that handoff.

**Contract (VizModel JSON):**

- `data.rows` — list of row/category dimension field names
- `data.columns` — list of column/measure field names
- `data.filters` — list of `{ name, value, condition }`
- `data.hidden` — field names in the query but hidden from the viz
- `chart.viz` — visualization family (e.g. `Bar`, `arc`)
- `chart.mark` — mark geometry (e.g. `bar`, `pie`)
- `properties` — fixed fields: `labelsX`, `labelsY`, `title`, `color`, `formatting` (plus `colorGradient`, `theme`, `background`, `formatter` where used)
- `properties` must allow additional unknown keys (chart-specific options) without stripping them

**Example:**

```json
{
  "data": {
    "rows": ["Travel Type", "Travel Medium"],
    "columns": ["Travel Cost"],
    "filters": [{ "name": "", "value": "", "condition": "" }],
    "hidden": []
  },
  "chart": { "viz": "Bar", "mark": "bar" },
  "properties": {
    "labelsX": "Travel Type",
    "labelsY": "Travel Medium",
    "title": "Travel Cost by Travel Type and Medium",
    "color": "",
    "formatting": { "colA": "formattingx", "colb": "formaty" }
  }
}
```

**Responsibilities:**

- Backend (InstantBI filler / graph): build a valid `VizModel` from query result metadata and chart selection; optional LLM polish for styling only
- Java `/ai/*` proxy: pass `viz_model` through to the client unchanged
- Frontend: map `VizModel` to chart components and render; do not require `vf_template` for primary render

**Out of scope:**

- Redesigning EFW/adhoc report designer
- Inventing a second/alternate viz schema
- Making FE responsible for SQL, shelf inference, or LLM chart selection

**Acceptance criteria:**

- API responses that return a chart include a complete `VizModel` (`data` + `chart` + `properties`)
- Frontend can render supported charts (at least bar/pie equivalents) from `VizModel` alone
- Unknown `properties` keys round-trip and are available to the FE renderer
- Legacy `vf_template` may still be returned during migration but must not be required for FE render once `VizModel` is present
- Shelf/chart population remains backend-owned and mostly deterministic (minimal LLM)

**Notes:**

- Source of truth: `ib/helicalbi/helicalbi/model/output/viz/VizModel.py`
- Related work: deterministic `VizModelFiller` / viz model fill graph in InstantBI
