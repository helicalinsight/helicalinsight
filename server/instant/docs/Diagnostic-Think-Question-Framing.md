# Feature: Diagnostic-guided question framing in InstantBI Think mode

## Summary

When a user saves a diagnostic capability on the semantic model, InstantBI Think mode uses that configuration to frame the supporting questions. The capability names the measure, the analytical strategies, and the fields those strategies may use. Think mode turns that configuration into a scope lock before any language model writes questions.

The lock is the same for every LLM provider. The model may phrase a question, but it may not change the strategy, the measures, or the dimensions the user configured. A question that leaves that scope is dropped or sent back for one rewrite. If the model returns nothing usable, Think mode writes one flat question per configured strategy from the saved names.

Interactive Fast mode does not use this lock. The behavior below applies when interactive chat runs in Think mode, including Auto when the router chooses Think.

## Problem

Without a diagnostic, Think mode asks the language model to invent the investigation: which strategies to apply, which measures to use, and which dimensions to break them down by. Different providers phrase that investigation differently, and the same provider can change it between runs. A business user who already knows how a measure should be diagnosed cannot make that method stick.

A diagnostic capability is that method, stored on the semantic model. Think mode must follow it so the supporting questions stay consistent no matter which provider answers the framing call.

## Users and outcome

A semantic-model author configures a diagnostic in the agent semantic editor. A business user later asks a Think question that mentions that capability or one of its measures. InstantBI frames a small set of flat supporting questions that cover only the configured strategies, measures, and dimensions, runs those questions, and synthesizes one answer that cites them.

The author does not write the question text. The author writes the investigation contract. Think mode writes the questions inside that contract.

## Functional requirements

1. The semantic model stores zero or more diagnostic capabilities. Each capability has a name, the measures it applies to, and the analytical strategies it uses. Each strategy may bind the catalog inputs the author dropped in, such as a measure or a dimension.
2. The semantic model stores custom analytical strategies (`type: custom`) beside the built-in catalog. A diagnostic use may name a custom strategy. Think mode treats that name as a configured strategy.
3. Measures keep grain, additivity, direction, formula, synonyms, and AI instructions. Think mode copies grain, additivity, and direction onto the measures a matched capability uses, and it expands a decompose strategy with the formula's component measures.
4. Think mode loads the saved capabilities with the semantic model for the interactive session. A capability is eligible when the user question mentions the capability name, or when it mentions an applicable measure by name, synonym, or distinctive tokens.
5. When more than one capability is eligible, Think mode asks the language model to choose exactly one name from that shortlist. A single eligible capability is used as-is. If the pick fails or names something outside the shortlist, every shortlisted capability stays in force.
6. The chosen capability becomes a use plan. Each use is classified as a strategy, a measure, a dimension, or another semantic item. Strategy inputs become the measures and dimensions bound to that strategy.
7. When the use plan contains at least one strategy, question framing is locked:
   - one flat question per allowed strategy;
   - only the allowed measures and dimensions;
   - no other strategy from the catalog;
   - no complementary breakdown;
   - one optional drill-down, and only when the next question needs a value the previous question will return. That follow-up keeps the same strategy, measures, and dimensions and sets `uses_prior_value` to true.
8. Every framed question must be answerable by one flat `SELECT`. The plan must not require a subquery, CTE, derived table, nested `SELECT`, or a join that fans a measure out across two transaction tables.
9. Questions that use a strategy, measure, or dimension outside the lock are rejected. Missing required uses are also rejected. Think mode rewrites the plan once with that feedback. After the rewrite, charts still outside the lock are dropped. If none remain, Think mode emits the deterministic questions built from the configured names.
10. When no capability matches, Think mode keeps the existing catalog scoring path and frames questions from the selected analytical operations. Configuring a diagnostic does not change questions that do not match it.
11. After the plan is accepted, Think mode runs each supporting question, collects text findings, and writes one cited final answer. The diagnostic changes how questions are framed. It does not change how each question is executed.

## Semantic layer changes

The semantic model is the cube the agent editor saves. Two new lists sit on that model, and measure metadata that already existed is now read during framing.

### Diagnostic capabilities

Saved as `diagnosticCapabilities` on the cube. The editor is the Diagnostic capability shelf in the semantic metadata editor. The author:

- adds a capability and names it;
- drops measures into Applicable to;
- drops analytical strategies from the catalog, or custom strategies, into Uses;
- fills each strategy's input slots by dropping field names (a measure slot, a dimension slot, and any other input the catalog declares).

Persisted shape:

```json
{
  "capability": "cost_change",
  "applicableTo": [{ "measure": "Travel Cost" }],
  "uses": [
    {
      "analyticalOperation": "period_comparison",
      "measure": "Travel Cost",
      "dimensions": ["Year"]
    }
  ]
}
```

`applicableTo` entries are objects `{ "measure": "<name>" }`. Older string entries are still read. A use is an object whose operation name is `analyticalOperation`. Input values are stored as sibling keys on that object: one name as a string, several names as an array. Older uses that are a bare strategy name are still read.

The cube reducer and the agent cube bridge keep `diagnosticCapabilities` on the in-memory cube and write it back when the semantic model is saved. A cube-field update that clones the list must not drop a draft the author has not saved yet; the editor guards that echo.

### Custom analytical strategies

Saved as `customAnalyticalOperations` on the same cube. Each entry has `operation`, `type: "custom"`, `category`, `description`, `inputs`, `preconditions`, `limitations`, `outputFields`, `dashboardVisualizations`, and `execution` (`method`, `logic`). Renaming a custom strategy updates diagnostic uses that point at the old name.

Think mode merges these operations with the built-in analytical catalog before it resolves a use. A custom name in `analyticalOperation` is therefore a real strategy, not free text.

### Measure traits used by framing

On each measure or metric the editor already stores:

| Field | How Think mode uses it |
| --- | --- |
| `grain` | Stay at this grain. Do not join a finer table that duplicates the measure. |
| `additivity` | `additive` may be summed at that grain. `non-additive` is computed from formula components inside each group. `semi-additive` is not summed across time. |
| `direction` | `negative`: an increase is unfavorable. `positive`: a decrease is unfavorable. `neutral`: describe volume, not better or worse. |
| `formula` | When a use resolves to `decompose`, component measure names found in the formula are added to the use plan. |
| synonyms, AI instructions | Synonyms help a question match an applicable measure. Instructions stay in the grounding pack. |

### What InstantBI indexes

`SemanticLayerIndexer` reads `diagnosticCapabilities` (and `diagnostic_capabilities`) from the model or from the prepared cube. It keeps the raw list for exact matching. It also:

- appends each capability to the semantic overview and to the topic grounding pack;
- embeds one chunk per capability (`kind: diagnostic_capability`) so retrieval can see the name, applicable measures, and uses;
- indexes each custom operation the same way.

Matching at plan time does not rely on vector score. The plan graph uses the raw list and a name/measure mention check so a configured capability is not missed because an embedding ranked something else higher.

## Think mode changes

Interactive Think always plans and executes with flat SQL (`FLAT_SQL = true`). That flag is what turns a matched diagnostic into a scope lock. Dashboard planning can still see diagnostic capabilities as guidance. Only Think mode refuses questions outside the configured scope.

### Plan graph

The plan graph runs, in order: load semantic context, select domain and topics, enrich the grounding pack, select analytical operations, draft questions, validate. Domain and topics are chosen before questions are written. The Think question agent is told not to change them.

`select_analytical_operations_node`, when `flat_sql` is set and capabilities exist:

1. Shortlist with `match_diagnostic_capabilities`. A capability matches when its `capability` text appears in the question, or when an applicable measure is mentioned (name, synonym, or enough distinctive tokens). Generic tokens such as "total" and "cost" do not match by themselves.
2. Call `pick_diagnostic_capability` when the shortlist has two or more entries. The prompt lists only those entries and requires the model to return one `capability` string exactly as stored. Zero or one candidate skips the call (`source: only_candidate`). A successful pick returns that one capability (`source: llm`). Failure or an unknown name keeps the shortlist (`source: shortlist`).
3. Resolve uses into a use plan: `strategy`, `measure`, `dimension`, or `other`. Aliases such as `measureDecomposition` map to `decompose`. Bound inputs on a strategy are copied onto that use and also added as their own measure or dimension entries.
4. If a strategy is `decompose`, add formula component measures (`source: formula`).
5. Copy grain, additivity, and direction from the indexer onto the matched measures and onto measure uses.
6. Replace the scored operation list with the operations named by strategy uses, in configuration order. Catalog scoring runs only when the capability declares no strategy.
7. Add those measure and dimension names to the allowed grounding names, and prepend their trait text to the grounding pack.

The activity trace records the shortlist, the pick source, the chosen capability, and the use plan. The user-visible reason names the capability, the measure, the strategies, and the dimensions.

### Question framing

`draft_chart_questions_node` calls `build_investigation_plan` with the matched capabilities and the use plan. In Think mode the planner does not use the generic dashboard agent. It uses `think_question_framer`:

- system prompt: domain, topics, and the grounding pack are already final; write only the questions;
- user prompt: `CONTEXT_PLAN_PROMPT`, with the diagnostic block and the scope lock filled from the use plan.

The diagnostic block is provider-neutral text. It names the capability, the target measures and their trait notes, and every required use. When strategies are present it ends with an exclusive scope: allowed strategies, allowed measures, allowed dimensions, and an instruction to ignore every other name in the grounding pack and the strategy catalog. The scope-lock section of the same prompt repeats that rule and states the drill-down exception.

The strategy catalog shown to the model is replaced with "do not substitute" plus the configured strategy names. The analytical-operations block is limited to those same operations. Dashboard layout slots are optional and must not be filled.

### Enforcement after the model responds

The model output is not trusted as the plan.

1. `charts_within_diagnostic_scope` keeps a chart only when its operation is an allowed strategy, its measure hints are inside the allowed measures, and, when dimensions were configured, the chart uses one of those dimensions. Anything else is dropped.
2. If the model returns no charts, or every chart is dropped, `_charts_for_configured_scope` writes one question per strategy from the configured measure and dimension names. That fallback does not call a language model.
3. `validate_questions_node` checks coverage and scope again. A missing strategy, measure, or dimension, or a question that names a catalog field outside the lock, schedules one repair. The repair prompt is the list of those errors. A second failure does not loop.
4. Accepted questions are capped at five.

This is why framing stays consistent across providers. The provider chooses wording inside a closed set of strategies and fields. The closed set is computed in code from the semantic model before the prompt is sent, and it is checked again after the response comes back.

### What happens to the framed questions

Think mode then:

1. Stores the plan and emits one progress line per supporting question.
2. Writes an opening insight from the user question and the planned questions. The insight does not invent numbers and does not restate the question list.
3. Executes each question as its own flat query. Charts are not auto-hydrated into the client; findings are text plus a report model.
4. Synthesizes one final answer that cites the supporting-question indexes that actually support each claim.

Filter correction and chat-memory hydration of parent and supporting turns are unchanged. They run on the questions this plan produced.

## Implementation design

### Components

| Piece | Responsibility |
| --- | --- |
| Diagnostic capability shelf (`diagnostic-capabilities-panel.jsx`) | Author the capability, bind strategy inputs, edit custom strategies. Persist the object shape above. |
| Cube state (`cube.reducer.js`, `agent-cube-bridge.js`, `semantic-metadata-utils.js`) | Keep `diagnosticCapabilities` and `customAnalyticalOperations` on the cube and on the saved semantic model. |
| `SemanticLayerIndexer` | Load both lists, expose them to the plan graph, and include them in overview, topic pack, and retrieval chunks. |
| `diagnostics.py` | Match, pick, resolve uses, expand decompose, build the scope, build the prompt block, filter charts. |
| Plan graph (`plan_graph.py`) | Run match → pick → use plan → draft → validate, with one repair edge. |
| `investigation_planner.py` | Think-only framer, scope lock in the prompt, post-filter, deterministic fallback questions. |
| Think orchestrator | Call plan and execute with `flat_sql=true`, then opening insight, per-question execution, cited answer. |

The Java InstantBI controller is unchanged. It already posts the interactive request and the semantic model location. The diagnostic is inside the model file the Python service loads.

### Runtime sequence

```text
Author saves semantic model
  diagnosticCapabilities[]
  customAnalyticalOperations[]
  measure grain / additivity / direction / formula

User asks in interactive Think mode
        |
        v
Load model session
  SemanticLayerIndexer.diagnostic_capabilities()
        |
        v
Plan graph
  select domain + topics          (fixed before framing)
  match capabilities              (name or applicable measure)
  pick one if several             (LLM, shortlist only)
  resolve uses + traits + formula
        |
        v
think_question_framer             (any provider, same prompt contract)
        |
        v
Filter charts to the scope
  else one deterministic question per strategy
        |
        v
Validate coverage
  one repair rewrite if a required use is missing
  or a question leaves the scope
        |
        v
Execute each flat question
Synthesize cited answer
```

### Match and pick

Shortlist order is the order stored on the model. A capability is kept when either check is true:

- the `capability` string is a substring of the question, or the same mention check used for measures hits it;
- one applicable measure is in the question. A measure counts when its name or a synonym is present, or when enough of its distinctive tokens are present. A single short token, or a generic token such as "rate" or "total", is not enough.

The pick call sees only the shortlist. Each candidate is shown with its applicable measures, the measures the question actually mentioned, and the configured uses. The structured response is a capability name plus a short reason. The name must compact-match a shortlisted `capability` value. Think mode does not invent a name and does not accept a name that was not shortlisted.

### Use plan

Each use object becomes one or more plan rows:

| `kind` | How it is recognized | Effect on framing |
| --- | --- | --- |
| `strategy` | `analyticalOperation` / `operation` / alias, matched to the catalog including custom operations | One required question. Operation list is this name. |
| `measure` | input kind contains "measure", or the name is a known measure | Must appear in a question. Traits are attached. |
| `dimension` | input kind contains "dimension", or the name is a known dimension | Breakdown must use this dimension and no other. |
| `other` | anything else that has a name | Included when that name exists in the grounding pack. |

Rows are de-duplicated by kind and name. Strategy-bound measures and dimensions are also emitted as their own rows so coverage checks can see them.

Scope used by the lock:

- strategies: strategy rows, in first-seen order;
- measures: measures bound on those strategies, otherwise the matched applicable measures, plus any measure rows and formula components;
- dimensions: dimensions bound on strategies plus dimension rows.

If the capability names strategies but binds no measures, the measures the question matched (or the full applicable list) become the allowed measures.

### Prompt contract

The framing prompt is assembled in code. Provider adapters only fill the structured `InvestigationPlan`. The contract the model must satisfy:

- `chart.question`: one flat question;
- `chart.operation` and `chart.analytical_strategy`: an allowed strategy name;
- `chart.measure_hints`: allowed measures only;
- `chart.components`: allowed measures and dimensions only;
- `chart.uses_prior_value`: true only on a follow-up that consumes a value from the previous question;
- `plan.domain` and `plan.topics`: the values already selected.

`THINK_FLAT_SQL_PLAN_RULES` is appended for every Think plan: flat `SELECT` only, date dimensions from the measure's own table, no fan-out joins, and an explicit period filter when the question is split by period.

### Validation and repair

Grounding still rejects names that are not in the allowed component list. Diagnostic checks add:

- every strategy in the use plan is the operation of some chart, or is mentioned as that operation;
- every required measure and dimension is mentioned in some chart;
- no chart operation is outside the allowed strategies;
- no measure hint, component, or catalog measure/dimension phrase in the question text is outside the lock.

The first failure returns to `draft_chart_questions` with `validation_feedback` set. `repair_count` stops a second loop. Activity entries `select_analytical_operations`, `draft_chart_questions`, and `validate_questions` record the capability, the questions, and whether a repair ran.

### Deterministic fallback

`_charts_for_configured_scope` does not call a model. For each allowed strategy it emits:

- with dimensions: `What is {measures} by {dimensions} using {strategy}?`
- without dimensions: `What is {measures} using {strategy}?`

`operation`, `analytical_strategy`, `measure_hints`, and `components` are set from the scope. This is the path that keeps framing stable when a provider returns an empty plan or a plan that the scope filter rejects entirely.

### Behavior when the user did not configure a diagnostic

`diagnosticCapabilities` empty, or no capability matches the question:

- `matched_diagnostic_capabilities` is empty and the use plan is empty;
- operations are chosen by the existing keyword and description scoring, at most five;
- the diagnostic prompt block is omitted;
- the scope lock is empty;
- Think still frames flat questions, but the model may choose strategies and fields from the grounding pack.

A diagnostic that exists on the model and does not match the question is ignored. It is not offered as a catalog substitute.

## Acceptance criteria

1. Saving a capability with one strategy, one measure, and one dimension, then asking a Think question that names that measure, produces questions whose operations are only that strategy and whose fields are only that measure and dimension.
2. Two capabilities that both match the same question produce one chosen capability. The activity trace shows `capability_pick_source = llm` and the shortlist. A provider that returns a name outside the shortlist leaves the shortlist in place.
3. One matching capability does not make a pick call.
4. A question that does not mention the capability or its measures produces no matched capability and uses catalog scoring.
5. A decompose use on a calculated measure adds the formula's component measures to the allowed measures and to the prompt trait notes.
6. A non-additive or semi-additive measure, and a negative, positive, or neutral direction, appear as trait guidance on the framing prompt.
7. A custom strategy saved on the model and used by a capability is an allowed operation. Renaming it in the editor updates the capability use.
8. A framed question that names another measure or dimension is removed or repaired. After one repair, a plan that still has no in-scope chart is replaced by one deterministic question per configured strategy.
9. Repeating the same question against the same model with two different providers yields the same strategy, measure, and dimension set. Wording may differ. The set may not.
10. Fast mode does not apply the scope lock.

## Out of scope

- Writing SQL inside the diagnostic. Execution of each framed question stays on the existing flat-SQL path.
- Letting the language model add a strategy or a field "because it would be a better analysis." That is the behavior this feature removes when a diagnostic matches.
- Changing the interactive Fast path, the Auto router, or the Java `/ai/interactive-chat` contract.
- Ranking or merging several matched capabilities into one blended investigation. Think mode uses one capability when the pick succeeds, and the untouched shortlist only when the pick does not.
