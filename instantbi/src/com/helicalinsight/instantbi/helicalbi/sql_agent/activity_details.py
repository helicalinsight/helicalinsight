"""Structured think-mode path details when ``show_llm_activity_details`` is on."""
from __future__ import annotations

from typing import Any, Mapping, Optional, Sequence


def trace_entry(
    node: str,
    *,
    result: Optional[Mapping[str, Any]] = None,
    reason: str = "",
    status: str = "done",
    stage: str = "",
) -> dict[str, Any]:
    entry: dict[str, Any] = {
        "node": node,
        "status": status,
        "result": dict(result or {}),
        "reason": str(reason or "").strip(),
    }
    if stage:
        entry["stage"] = stage
    return entry


def append_trace(
    existing: Optional[Sequence[Mapping[str, Any]]],
    entry: Mapping[str, Any],
) -> list[dict[str, Any]]:
    trace = [dict(item) for item in (existing or []) if isinstance(item, Mapping)]
    trace.append(dict(entry))
    return trace


def strategy_selection_reason(strategy: Mapping[str, Any] | None) -> str:
    strategy = strategy or {}
    selection = strategy.get("selection") if isinstance(strategy.get("selection"), Mapping) else {}
    source = str(selection.get("source") or "").strip()
    intent = str(selection.get("intent") or "").strip()
    persona = str(selection.get("persona") or "").strip()
    strategy_id = str(strategy.get("id") or selection.get("strategy_id") or "").strip()
    if source == "hint":
        return (
            f"Strategy '{strategy_id}' chosen from explicit hint"
            + (f"; classified intent={intent}" if intent else "")
            + "."
        )
    if source == "llm_adapt":
        suggested = str(selection.get("suggested_strategy_id") or "").strip()
        return (
            f"LLM adapted strategy from '{suggested}' to '{strategy_id}'"
            + (f" (intent={intent})" if intent else "")
            + "."
        )
    if source == "decision_tree":
        bits = [f"Decision tree selected strategy '{strategy_id}'"]
        if intent:
            bits.append(f"intent={intent}")
        if persona:
            bits.append(f"persona={persona}")
        return "; ".join(bits) + "."
    if strategy_id:
        return f"Strategy '{strategy_id}' selected."
    return "No strategy selected."


def questions_from_plan(plan: Mapping[str, Any] | None) -> list[dict[str, Any]]:
    questions: list[dict[str, Any]] = []
    for index, chart in enumerate((plan or {}).get("charts") or [], start=1):
        if not isinstance(chart, Mapping):
            continue
        question = str(chart.get("question") or "").strip()
        if not question:
            continue
        purpose = str(chart.get("purpose") or "").strip()
        questions.append(
            {
                "index": index,
                "question": question,
                "title": str(chart.get("title") or "").strip(),
                "purpose": purpose,
                "why": purpose
                or "Supporting investigation question toward the user ask.",
                "topic": str(chart.get("topic") or "").strip(),
                "components": [
                    str(c).strip()
                    for c in (chart.get("components") or [])
                    if str(c).strip()
                ],
                "measure_hints": [
                    str(h).strip()
                    for h in (chart.get("measure_hints") or [])
                    if str(h).strip()
                ],
                "viz_hint": str(chart.get("viz_hint") or "").strip(),
            }
        )
    return questions


def build_llm_activity_details(
    *,
    persona: Mapping[str, Any] | None = None,
    strategy: Mapping[str, Any] | str | None = None,
    strategy_selection: Mapping[str, Any] | None = None,
    selected_domains: Sequence[str] | None = None,
    selected_topics: Sequence[str] | None = None,
    selected_tables: Sequence[str] | None = None,
    plan: Mapping[str, Any] | None = None,
    plan_graph: Sequence[Mapping[str, Any]] | None = None,
    execute_graph: Sequence[Mapping[str, Any]] | None = None,
    question_history: Sequence[Mapping[str, Any]] | None = None,
    cited_question_indexes: Sequence[int] | None = None,
    final_answer: str = "",
) -> dict[str, Any]:
    """Assemble the client-facing think-mode reasoning path."""
    strategy_id = (
        strategy
        if isinstance(strategy, str)
        else str((strategy or {}).get("id") or "").strip()
    )
    selection = dict(strategy_selection or {})
    if not selection and isinstance(strategy, Mapping):
        raw = strategy.get("selection")
        if isinstance(raw, Mapping):
            selection = dict(raw)

    strategy_payload = {
        "id": strategy_id,
        "selection": selection,
    }
    if isinstance(strategy, Mapping):
        for key in ("name", "label", "template_id", "description"):
            if strategy.get(key):
                strategy_payload[key] = strategy.get(key)

    questions = questions_from_plan(plan)
    history_by_index = {
        int(item.get("index") or 0): item
        for item in (question_history or [])
        if isinstance(item, Mapping) and int(item.get("index") or 0) > 0
    }
    for item in questions:
        hist = history_by_index.get(int(item["index"]))
        if hist:
            item["finding"] = str(hist.get("analysis") or hist.get("answer") or "").strip()
            item["answer"] = str(hist.get("answer") or "").strip()

    plan_nodes = [dict(n) for n in (plan_graph or []) if isinstance(n, Mapping)]
    exec_nodes = [dict(n) for n in (execute_graph or []) if isinstance(n, Mapping)]

    path: list[dict[str, Any]] = [
        trace_entry(
            "resolve_persona",
            stage="persona",
            result={"persona": dict(persona or {})},
            reason="Persona resolved from user role/profile/hint for strategy routing.",
        ),
        trace_entry(
            "select_strategy",
            stage="strategy",
            result=strategy_payload,
            reason=strategy_selection_reason(
                {"id": strategy_id, "selection": selection}
                if selection or strategy_id
                else strategy if isinstance(strategy, Mapping) else {}
            ),
        ),
        trace_entry(
            "select_domain_topics",
            stage="domain_topics",
            result={
                "domains": list(selected_domains or []),
                "topics": list(selected_topics or []),
            },
            reason=(
                "Queried semantic index and scored topic catalog against the user question; "
                f"kept domains={list(selected_domains or [])}, topics={list(selected_topics or [])}."
            ),
        ),
        trace_entry(
            "select_tables",
            stage="tables",
            result={"tables": list(selected_tables or [])},
            reason=(
                "Relation exploration picked same-table and join-neighbor tables for grounding."
                if selected_tables
                else "No physical tables were resolved during topic enrichment."
            ),
        ),
        trace_entry(
            "draft_questions",
            stage="questions",
            result={
                "questions": questions,
                "rationale": str((plan or {}).get("rationale") or "").strip(),
            },
            reason=(
                str((plan or {}).get("rationale") or "").strip()
                or "Supporting questions were drafted so their findings can compose the final answer."
            ),
        ),
    ]
    path.extend(plan_nodes)
    path.extend(exec_nodes)
    path.append(
        trace_entry(
            "final_answer",
            stage="final_answer",
            result={
                "cited_question_indexes": list(cited_question_indexes or []),
                "final_answer": str(final_answer or "").strip(),
            },
            reason=(
                "Final answer synthesized from investigation findings, citing supporting Question N."
            ),
        )
    )

    return {
        "path": path,
        "persona": dict(persona or {}),
        "strategy": strategy_payload,
        "domains": list(selected_domains or []),
        "topics": list(selected_topics or []),
        "tables": list(selected_tables or []),
        "questions": questions,
        "plan_graph": plan_nodes,
        "execute_graph": exec_nodes,
        "plan_rationale": str((plan or {}).get("rationale") or "").strip(),
    }
