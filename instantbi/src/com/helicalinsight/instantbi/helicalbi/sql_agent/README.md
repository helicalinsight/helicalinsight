# InstantBI Dashboard Agent — Code Flow

Reactive (ReAct) LangGraph agent: the planner calls tools, observes results, and loops until it has a **multi-step investigation picture** — not a one-shot answer.

| Layer | Location |
|-------|----------|
| Java proxy | `POST /ai/agent-dashboard` (`mode` optional) |
| InstantBI HTTP | `helicalbi/controller/agent_dashboard.py` → `POST /agent-dashboard` |
| Graph | `helicalbi/sql_agent/dashboard_graph.py` → `run_dashboard_agent` |
| Modes | `helicalbi/sql_agent/modes.py` — `fast` / `balanced` / `research` |

## Token modes

| Mode | Purpose | Charts | Loops | Overview | LLM synthesizer |
|------|---------|--------|-------|----------|-----------------|
| `fast` | Low tokens / latency | ≤2 | ≤10 | truncated short | no |
| `balanced` | Default | ≤5 | ≤24 | medium | yes |
| `research` | Deeper picture | ≤8* | ≤40 | large | yes |

\* Also capped by `dashboard.max_sub_questions`. Set `input.mode` or config `dashboard.default_mode`.

Extra token controls:

- Compact tool messages after `state_patch` is applied (`token_budget.py`)
- Trim planner history to last N tool rounds (fast=2, balanced=4, research=8)
- Fast/balanced reuse semantic retrieve across facets
- Fast binds fewer tools (no `validate_sql` / `analyze_result` / `retrieve_schema`)
- Auto-finish when chart cap is reached (skips another planner LLM call)
- Agent InstantBI SQL/viz graph (`instantbi_agent_graph.py`): skip domain discovery +
  intent rephrase when topics are seeded; fast mode skips viz polish LLM

### Agent InstantBI branch (`generate_sql` / `build_report`)

```text
prepare
  ├─ has selected_topics → seed_domain (got_domain=true)
  │     ├─ fast/balanced → sql_generator
  │     └─ research → UpdateIntentRephrase → sql_generator
  └─ no topics → main_graph → sql_generator

viz:
  fast → VizModelFiller (+ Fallback if needed)
  balanced/research → full viz_graph
```

Interactive `/interactive` is unchanged (still full main_graph + viz_graph).

```python
# helicalbi/sql_agent/modes.py
AGENT_MODES = {
    "fast": AgentModeProfile(..., use_llm_synthesizer=False, max_tool_loops=10, ...),
    "balanced": AgentModeProfile(...),
    "research": AgentModeProfile(..., max_tool_loops=40, ...),
}
```

---

## 0. Tools available to the planner

```12:22:helicalbi/sql_agent/tools/tools.py
def dashboard_tools():
    return [
        retrieve_semantic_model,
        retrieve_schema,
        generate_sql,
        validate_sql,
        execute_query,
        analyze_result,
        build_report,
        finish_dashboard,
    ]
```

Planner intent (multi-step picture, not one-shot) from `config.py`:

```29:71:helicalbi/sql_agent/config.py
TOOL_AGENT_PROMPT = """You are an InstantBI dashboard agent. Your job is to build a clear
picture of the user's question by breaking it into focused sub-questions — not to
resolve everything in one single generate_sql / build_report call.
...
Rules:
- Do not try to answer the whole original question in one shot. Decompose it into
  complementary facets so the dashboard and findings show a clear multi-step picture.
- First call retrieve_semantic_model for the user question (or a focused facet of it).
...
- Lookup steps: retrieve_semantic_model → generate_sql → execute_query → analyze_result.
- Chart steps: retrieve_semantic_model → generate_sql → execute_query → build_report.
- Prefer 3-5 complementary charts (KPI, breakdown, trend). Max charts: {max_charts}.
...
- When the multi-step picture is enough, call finish_dashboard.
"""
```

---

## 1. HTTP entry → agent run

```46:100:helicalbi/controller/agent_dashboard.py
    @flask_app.route("/agent-dashboard", methods=["POST"])
    def agent_dashboard():
        data = request.get_json()
        ...
        user_query = user_input.get("inputString") or user_input.get("query") or ""
        ...
        model_file_name = model.get("file") or ""
        location = model.get("dir") or ""
        thread_id = str(user_input.get("dashboardid") or "")
        chat_seq_id = (
            user_input.get("dashboard_sequence_id")
            or user_input.get("dashboard_seq_id")
            or "1"
        )
        ...
        max_sub_questions = int(app_config.dashboard_max_sub_questions)
        ...
            result = run_dashboard_agent(
                user_query,
                session_cookie=session_cookie,
                username=username,
                model_file_name=model_file_name,
                model_location=location,
                thread_id=thread_id,
                chat_seq_id=chat_seq_id,
                last_chats=user_input.get("last_chats"),
                request_id=request_id,
                max_sub_questions=max_sub_questions,
            )
```

Required request fields: `inputString`, `model.file`, `model.dir`, `dashboardid`, `dashboard_sequence_id`. Chart cap comes from config (`dashboard.max_sub_questions`), not the body.

---

## 2. High-level LangGraph

```text
START → planner → tools → apply_patches → planner (loop)
                 ↘ synthesizer → dashboard → END
```

Graph wiring:

```42:63:helicalbi/sql_agent/dashboard_graph.py
def build_dashboard_agent(checkpointer=None):
    workflow = StateGraph(AgentState)
    workflow.add_node("planner", planner_node)
    workflow.add_node("tools", ToolNode(dashboard_tools()))
    workflow.add_node("apply_patches", apply_tool_patches)
    workflow.add_node("synthesizer", synthesizer_node)
    workflow.add_node("dashboard", dashboard_node)

    workflow.add_edge(START, "planner")
    workflow.add_conditional_edges(
        "planner",
        route_planner,
        {"tools": "tools", "synthesizer": "synthesizer"},
    )
    workflow.add_edge("tools", "apply_patches")
    workflow.add_conditional_edges(
        "apply_patches",
        route_after_tools,
        {"planner": "planner", "synthesizer": "synthesizer"},
    )
    workflow.add_edge("synthesizer", "dashboard")
    workflow.add_edge("dashboard", END)
```

Routing:

```22:39:helicalbi/sql_agent/dashboard_graph.py
def route_planner(state: AgentState) -> str:
    if state.get("is_complete"):
        return "synthesizer"
    if int(state.get("tool_loop_count") or 0) >= DEFAULT_MAX_TOOL_LOOPS:
        return "synthesizer"
    messages = state.get("messages") or []
    last = messages[-1] if messages else None
    if isinstance(last, AIMessage) and last.tool_calls:
        return "tools"
    ...
    return "synthesizer"


def route_after_tools(state: AgentState) -> str:
    if state.get("is_complete"):
        return "synthesizer"
    return "planner"
```

Planner loop (increments `tool_loop_count` / attempt count):

```34:57:helicalbi/sql_agent/nodes/planner.py
def planner_node(state: AgentState) -> Dict[str, Any]:
    """LLM step: choose the next InstantBI tool call or stop."""
    loop = int(state.get("tool_loop_count") or 0)
    if loop >= DEFAULT_MAX_TOOL_LOOPS:
        ...
        return {"is_complete": True, "tool_loop_count": loop}
    ...
    bound = default_llm.bind_tools(dashboard_tools())
    ai_message, _ = invoke_llm(bound, history, state=state)
    ...
    return {"messages": outgoing, "tool_loop_count": loop + 1}
```

Tool results are folded into state via `state_patch`:

```25:26:helicalbi/sql_agent/nodes/apply_patches.py
def apply_tool_patches(state: AgentState) -> Dict[str, Any]:
    """Fold JSON state_patch blobs from the latest tool messages into agent state."""
```

---

## 3. Scenario A — Bootstrap (every request)

```text
run_dashboard_agent
  → load_model_session
  → SchemaIndexer + SemanticLayerIndexer (in-memory)
  → set_indexer / set_semantic_indexer
  → initial_agent_state + bootstrap_planner_messages
  → graph.invoke
```

```95:133:helicalbi/sql_agent/dashboard_graph.py
    session = load_model_session(...)
    indexer = session.pop("indexer", None)
    semantic_indexer = session.pop("semantic_indexer", None)
    catalog_id = f"dashboard:{thread_id}"
    if indexer is not None:
        set_indexer(indexer, catalog_id)
    if semantic_indexer is not None:
        set_semantic_indexer(semantic_indexer, catalog_id)
    ...
    state = initial_agent_state(...)
    state["messages"] = bootstrap_planner_messages(state)
    graph = compiled_agent or get_dashboard_agent()
    return graph.invoke(state, {"recursion_limit": recursion_limit})
```

Session load indexes both semantic model and table metadata:

```107:123:helicalbi/sql_agent/instantbi_turn.py
    indexer = SchemaIndexer()
    indexer.index_from_cube_metadata(cube_metadata, joins)
    schema_overview = indexer.retrieve_schema(user_query, top_k=DEFAULT_SCHEMA_TOP_K)
    ...
    semantic_indexer = SemanticLayerIndexer()
    semantic_indexer.index_model(model_data, cube_info_prepared)
    semantic_overview = semantic_indexer.overview()
    if not semantic_overview:
        semantic_overview = schema_overview
```

**No ChromaDB on this path.** Default is in-memory:

```196:207:helicalbi/sql_agent/database/schema_indexer.py
        if vector_index is not None:
            self._index = vector_index
        elif persist_directory:
            try:
                self._index = ChromaVectorIndex(persist_directory)
            except ImportError:
                ...
                self._index = InMemoryVectorIndex()
        else:
            self._index = InMemoryVectorIndex()
```

SQL always goes through InstantBI `SqlExecutor` → Java `executeQuery`:

```223:235:helicalbi/sql_agent/instantbi_turn.py
def execute_sql_state(state: dict[str, Any], request_id: Optional[str] = None) -> dict[str, Any]:
    """Run InstantBI executeQuery for an already-generated SQL state."""
    ...
    result = SqlExecutor().process_flow(state)
    ...
    return result
```

---

## 4. Scenario B — Semantic model is enough

```text
planner
  → retrieve_semantic_model   # sufficient=true, no metadata RAG
  → generate_sql(facet)
  → execute_query(sql)
  → build_report(facet)       # chart
  … more facets …
  → finish_dashboard
  → synthesizer → dashboard
```

Semantic-first retrieve:

```18:46:helicalbi/sql_agent/tools/semantic.py
    def retrieve(self, question: str, state: dict[str, Any]) -> dict[str, Any]:
        ...
        semantic = get_semantic_indexer(ctx.catalog_id).retrieve(question, top_k=top_k)
        sufficient = bool(semantic.get("sufficient"))
        model_context = str(semantic.get("prompt") or "").strip()
        used_metadata_fallback = False
        schema = ""
        if not sufficient:
            schema = get_indexer(ctx.catalog_id).retrieve_schema(question, top_k=top_k)
            used_metadata_fallback = bool(schema)
        ...
        return {
            "ok": True,
            "sufficient": sufficient,
            "used_metadata_fallback": used_metadata_fallback,
            ...
        }
```

When `sufficient=true`, the `if not sufficient` branch is skipped — no metadata RAG.

---

## 5. Scenario C — Semantic weak → metadata fallback

Same tool; when the semantic hit is weak:

```26:28:helicalbi/sql_agent/tools/semantic.py
        if not sufficient:
            schema = get_indexer(ctx.catalog_id).retrieve_schema(question, top_k=top_k)
            used_metadata_fallback = bool(schema)
```

Response then includes `sufficient=false`, `used_metadata_fallback=true`, and optional `schema`. Planner may still call `retrieve_schema` if columns are still missing (`config.py` rules).

---

## 6. Scenario D — Chart step

```text
retrieve_semantic_model → generate_sql → execute_query → build_report
```

### generate_sql (tracks asked questions)

```21:51:helicalbi/sql_agent/tools/sql.py
    def generate(self, question: str, state: dict[str, Any]) -> dict[str, Any]:
        ...
        result = generate_sql_for_question(
            question,
            session,
            thread_id=ctx.thread_id,
            chat_seq_id=seq,
            request_id=ctx.request_id,
        )
        ...
        return {
            ...
            "state_patch": {
                "current_sub_question": question,
                "asked_questions": append_question(state.get("asked_questions"), question),
                "generated_sql": sql or None,
                ...
            },
        }
```

### execute_query (validate, then InstantBI)

```66:86:helicalbi/sql_agent/tools/sql.py
    def execute(self, sql: str, state: dict[str, Any]) -> dict[str, Any]:
        ...
        error = validate_sql_against_catalog(cleaned, ctx.catalog, dialect=ctx.dialect)
        retry = int(state.get("sql_retry_count") or 0)
        if error:
            ...
            return {
                "ok": False,
                "error": error,
                "state_patch": {
                    ...
                    "sql_retry_count": retry + 1,
                },
            }
```

On success it calls `execute_sql_state` → `SqlExecutor`.

### build_report (dashboard chart)

```57:68:helicalbi/sql_agent/tools/report.py
        collected.append(
            {
                "sub_question": question or state.get("current_sub_question") or "",
                ...
                "report_model": chat_response.get("report_model") or {},
                "chat_seq_id": seq,
                "include_in_dashboard": True,
            }
        )
```

---

## 7. Scenario E — Lookup (not a chart)

```text
retrieve_semantic_model → generate_sql → execute_query → analyze_result
```

```15:36:helicalbi/sql_agent/tools/analysis.py
    def analyze(self, note: str, state: dict[str, Any]) -> dict[str, Any]:
        ...
        collected.append(
            {
                "sub_question": state.get("current_sub_question") or "",
                ...
                "include_in_dashboard": False,
            }
        )
        return {
            "ok": True,
            "analysis": analysis,
            "state_patch": {"collected_data": collected, "sql_retry_count": 0},
        }
```

Lookups stay in findings; they do not become dashboard tiles.

---

## 8. Scenario F — Bad SQL / retry

1. Catalog validation fails before execute (`execute` above) → `sql_retry_count++`, no `executeQuery`.
2. Or `SqlExecutor` returns an error → `sql_retry_count` bumped in the success-path patch when `exec_error` is set.
3. Planner is told to fix via `generate_sql` (see `TOOL_AGENT_PROMPT`), then call `execute_query` again.

---

## 9. Scenario G — Finish + investigation picture

Stop gathering:

```15:20:helicalbi/sql_agent/tools/control.py
    def finish(self, reason: str, state: dict[str, Any]) -> dict[str, Any]:
        return {
            "ok": True,
            "reason": reason,
            "state_patch": {"is_complete": True},
        }
```

Synthesizer: LLM narrative + **programmatic** run summary (not invented by the LLM):

```16:41:helicalbi/sql_agent/nodes/synthesizer.py
def synthesizer_node(state: AgentState) -> Dict[str, Any]:
    """Combine collected findings into the final answer; attach programmatic run stats."""
    parsed = invoke_agent_model(...)
    answer = (parsed.final_answer or "").strip()
    summary = build_run_summary(state)
    ...
    return {
        "final_answer": answer,
        "is_complete": True,
        "asked_questions": summary["asked_questions"],
        "attempt_count": summary["attempt_count"],
        "investigation_steps": summary["investigation_steps"],
    }
```

How the picture is built (no LLM):

```40:73:helicalbi/sql_agent/nodes/run_summary.py
def attempt_count_from_state(state: Dict[str, Any]) -> int:
    """How many planner tool loops ran while building the picture."""
    return int(state.get("tool_loop_count") or 0)


def investigation_steps_from_state(state: Dict[str, Any]) -> List[Dict[str, Any]]:
    """Ordered investigation steps from collected findings (programmatic)."""
    ...
        steps.append(
            {
                "step": index,
                "question": question,
                "kind": "chart" if step.get("include_in_dashboard") else "lookup",
                "analysis": str(step.get("analysis") or "").strip(),
            }
        )
    ...


def build_run_summary(state: Dict[str, Any]) -> Dict[str, Any]:
    """Programmatic multi-step investigation picture for the API response."""
    ...
    return {
        "asked_questions": questions,
        "attempt_count": attempts,
        "question_count": len(questions),
        "investigation_steps": steps,
    }
```

Returned on the HTTP response:

```123:133:helicalbi/controller/agent_dashboard.py
            to_send = {
                "original_question": user_query,
                "final_answer": result.get("final_answer") or "",
                "dashboardid": thread_id,
                "asked_questions": asked_questions,
                "attempt_count": attempt_count,
                "investigation_steps": investigation_steps,
                "sub_questions": _public_steps(result.get("collected_data")),
                "dashboard": result.get("dashboard") or {},
                "token_usage": result.get("token_usage") or {},
            }
```

| Field | Source | Meaning |
|-------|--------|---------|
| `asked_questions` | `generate_sql` tracking / collected | Focused sub-questions |
| `attempt_count` | `tool_loop_count` | Planner loops |
| `investigation_steps` | `collected_data` | Ordered chart/lookup steps |
| `sub_questions` | `collected_data` | Full step payloads + `report_model` |
| `final_answer` | synthesizer LLM | Narrative over the steps |
| `dashboard` | dashboard node | Layout / items / theme |

---

## 10. Key files

```text
helicalbi/
  controller/agent_dashboard.py
  sql_agent/
    README.md                 ← this file
    dashboard_graph.py
    instantbi_turn.py
    config.py
    state.py
    nodes/
      planner.py
      apply_patches.py
      synthesizer.py
      dashboard.py
      run_summary.py
      validator.py
    tools/
      tools.py
      semantic.py
      schema.py
      sql.py
      analysis.py
      report.py
      control.py
    database/
      schema_indexer.py       # metadata RAG (in-memory by default)
      semantic_indexer.py     # semantic-model RAG (in-memory)
```
