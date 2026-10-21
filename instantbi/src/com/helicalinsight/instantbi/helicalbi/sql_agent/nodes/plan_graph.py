"""LangGraph plan-context pipeline for investigation questions.

Flow: load semantic context → select topics → enrich packs → pick analytical
operations → draft chart questions → validate / repair grounding.

Public entry: :func:`run_plan_context_graph` (used by ``investigation.create_and_store_plan``).
Topic scoring lives in ``helicalbi.sql_agent.planning.scoring``; literals in
``helicalbi.sql_agent.constants``.
"""
from __future__ import annotations

import logging
import re
from typing import Any, Dict, List, Mapping, Optional, Sequence, TypedDict

from langgraph.graph import END, START, StateGraph

from helicalbi.sql_agent.activity_details import append_trace, trace_entry
from helicalbi.sql_agent.constants import (
    DEFAULT_OVERVIEW_CHARS,
    MIN_OVERVIEW_CHARS,
    PLAN_GRAPH_MAX_REPAIR,
    RELATION_PACK_MAX_CHARS,
    RELATION_PACK_MIN_CHARS,
)
from helicalbi.sql_agent.modes import truncate_text
from helicalbi.sql_agent.models import InvestigationPlan
from helicalbi.sql_agent.nodes.investigation_planner import (
    build_investigation_plan,
    fallback_investigation_plan,
)
from helicalbi.sql_agent.planning.scoring import score_topic
from helicalbi.sql_agent.strategy_tree import (
    charts_from_strategy,
    diagnostic_activity_reason,
    diagnostic_field_scope,
    expand_decompose_measures,
    get_strategy,
    load_analytical_operations,
    match_diagnostic_capabilities,
    merge_custom_operations,
    measure_mentioned,
    measure_trait_guidance,
    operations_for_diagnostic_uses,
    question_mentions_operation,
    resolve_diagnostic_uses,
    select_analytical_operations,
)
from helicalbi.sql_agent.util.normalize import normalize_whitespace

logger = logging.getLogger(__name__)

_MAX_REPAIR = PLAN_GRAPH_MAX_REPAIR


class PlanContextState(TypedDict, total=False):
    question: str
    persona: Dict[str, Any]
    strategy: Dict[str, Any]
    session: Dict[str, Any]
    max_charts: int
    flat_sql: bool
    overview_chars: int
    max_domains: int
    max_topics: int
    request_id: Optional[str]
    token_usage: Dict[str, Any]
    semantic_overview: str
    topic_catalog: List[Dict[str, Any]]
    selected_domains: List[str]
    selected_topics: List[str]
    selected_tables: List[str]
    grounding_pack: str
    allowed_names: List[str]
    analytical_operations: List[Dict[str, Any]]
    custom_analytical_operations: List[Dict[str, Any]]
    selected_operations: List[str]
    diagnostic_capabilities: List[Dict[str, Any]]
    matched_diagnostic_capabilities: List[Dict[str, Any]]
    diagnostic_use_plan: List[Dict[str, str]]
    plan: Dict[str, Any]
    validation_ok: bool
    validation_errors: List[str]
    repair_count: int
    validation_feedback: str
    activity_trace: List[Dict[str, Any]]


def _chart_limit(state: Mapping) -> Optional[int]:
    """Chart ceiling when explicitly configured; None in think mode (LLM decides)."""
    if state.get("flat_sql"):
        return None
    if state.get("max_charts") is not None:
        return max(1, int(state["max_charts"]))
    return None


def _normalize(text: str) -> str:
    """Backward-compatible alias for :func:`normalize_whitespace`."""
    return normalize_whitespace(text)


def _score_topic(question: str, entry: Dict[str, Any]) -> float:
    """Backward-compatible alias for :func:`score_topic`."""
    return score_topic(question, entry)


def _analytical_operations_catalog(state: Mapping[str, Any]) -> list[dict[str, Any]]:
    """JSON catalog plus type-custom strategies saved on the semantic model."""
    return merge_custom_operations(
        load_analytical_operations(),
        list(state.get("custom_analytical_operations") or []),
    )


def load_semantic_context_node(state: PlanContextState) -> Dict[str, Any]:
    """Load semantic overview, topic catalog, and diagnostic capabilities from the session."""
    session = state.get("session") or {}
    indexer = session.get("semantic_indexer")
    overview = str(session.get("semantic_overview") or session.get("schema_overview") or "")
    catalog: List[Dict[str, Any]] = []
    if indexer is not None and hasattr(indexer, "topic_catalog"):
        catalog = list(indexer.topic_catalog() or [])
    if not catalog:
        model_data = {}
        prepared = session.get("cube_info_prepared") or {}
        base = session.get("base_state") or {}
        # Prefer domain from prepared/session if model was already indexed empty.
        if hasattr(indexer, "_topic_catalog"):
            catalog = list(getattr(indexer, "_topic_catalog") or [])
        if not catalog and prepared.get("topic_mappings"):
            from helicalbi.sql_agent.database.semantic_indexer import _topic_catalog_from_model

            catalog = _topic_catalog_from_model(model_data, prepared)
        if not catalog and base.get("topic_mappings"):
            from helicalbi.sql_agent.database.semantic_indexer import _topic_catalog_from_model

            catalog = _topic_catalog_from_model({}, {"topic_mappings": base.get("topic_mappings")})
    diag_caps: List[Dict[str, Any]] = []
    if indexer is not None and hasattr(indexer, "diagnostic_capabilities"):
        diag_caps = list(indexer.diagnostic_capabilities() or [])
    prepared_dict = session.get("cube_info_prepared") or {}
    if not diag_caps:
        diag_caps = list(
            prepared_dict.get("diagnosticCapabilities")
            or prepared_dict.get("diagnostic_capabilities")
            or []
        )
    custom_ops: List[Dict[str, Any]] = []
    if indexer is not None and hasattr(indexer, "custom_analytical_operations"):
        custom_ops = list(indexer.custom_analytical_operations() or [])
    if not custom_ops:
        from helicalbi.sql_agent.strategy.catalog import custom_operations_from_source

        custom_ops = custom_operations_from_source(prepared_dict)
    logger.info(
        "Plan graph load overview_chars=%s topics=%s diagnostic_caps=%s",
        len(overview),
        len(catalog),
        len(diag_caps),
    )
    reason = (
        f"Loaded semantic overview ({len(overview)} chars), topic catalog "
        f"with {len(catalog)} entr(y/ies), and {len(diag_caps)} diagnostic capability(ies)."
    )
    return {
        "semantic_overview": overview,
        "topic_catalog": catalog,
        "diagnostic_capabilities": diag_caps,
        "custom_analytical_operations": custom_ops,
        "token_usage": dict(state.get("token_usage") or {}),
        "repair_count": int(state.get("repair_count") or 0),
        "validation_feedback": str(state.get("validation_feedback") or ""),
        "activity_trace": append_trace(
            state.get("activity_trace"),
            trace_entry(
                "load_semantic_context",
                stage="plan_graph",
                result={
                    "overview_chars": len(overview),
                    "topic_catalog_size": len(catalog),
                },
                reason=reason,
            ),
        ),
    }


def select_domain_topics_node(state: PlanContextState) -> Dict[str, Any]:
    """Rank topic catalog entries against the question and keep top domains/topics."""
    question = str(state.get("question") or "")
    catalog = list(state.get("topic_catalog") or [])
    session = state.get("session") or {}
    indexer = session.get("semantic_indexer")
    max_domains = max(1, int(state.get("max_domains") or 2))
    max_topics = max(1, int(state.get("max_topics") or 4))

    scored = sorted(
        ((entry, _score_topic(question, entry)) for entry in catalog),
        key=lambda item: item[1],
        reverse=True,
    )
    rag_topics: List[str] = []
    rag_domains: List[str] = []
    if indexer is not None and hasattr(indexer, "retrieve"):
        try:
            hit = indexer.retrieve(question, top_k=max(4, max_topics))
            rag_topics = [str(t).strip() for t in (hit.get("topics") or []) if str(t).strip()]
            rag_domains = [str(d).strip() for d in (hit.get("domains") or []) if str(d).strip()]
        except Exception:
            logger.exception("Plan graph semantic retrieve failed")

    selected_topics: List[str] = []
    selected_domains: List[str] = []

    def _add_topic(name: str, domain: str = "") -> None:
        label = str(name or "").strip()
        if not label:
            return
        if label not in selected_topics and len(selected_topics) < max_topics:
            selected_topics.append(label)
        dlabel = str(domain or "").strip()
        if dlabel and dlabel not in selected_domains and len(selected_domains) < max_domains:
            selected_domains.append(dlabel)

    for name in rag_topics:
        domain = ""
        for entry, _score in scored:
            if str(entry.get("topic") or "").strip().lower() == name.lower():
                domain = str(entry.get("domain") or "")
                break
        _add_topic(name, domain)
    for name in rag_domains:
        if name not in selected_domains and len(selected_domains) < max_domains:
            selected_domains.append(name)

    for entry, score in scored:
        if score <= 0 and selected_topics:
            continue
        _add_topic(str(entry.get("topic") or ""), str(entry.get("domain") or ""))
        if len(selected_topics) >= max_topics and len(selected_domains) >= max_domains:
            break

    if not selected_topics and catalog:
        for entry, _score in scored[:max_topics]:
            _add_topic(str(entry.get("topic") or ""), str(entry.get("domain") or ""))

    top_scored = [
        {
            "topic": str(entry.get("topic") or "").strip(),
            "domain": str(entry.get("domain") or "").strip(),
            "score": round(float(score), 2),
        }
        for entry, score in scored[:8]
        if str(entry.get("topic") or "").strip()
    ]
    logger.info(
        "Plan graph selected domains=%s topics=%s",
        selected_domains,
        selected_topics,
    )
    reason_parts = []
    if rag_topics or rag_domains:
        reason_parts.append(
            f"Semantic retrieve suggested topics={rag_topics} domains={rag_domains}"
        )
    if top_scored:
        reason_parts.append(
            "Lexical scores favored "
            + ", ".join(
                f"{item['topic']}({item['score']})" for item in top_scored[:3] if item["topic"]
            )
        )
    reason_parts.append(
        f"Caps max_domains={max_domains}, max_topics={max_topics}; "
        f"selected domains={selected_domains}, topics={selected_topics}."
    )
    return {
        "selected_domains": selected_domains,
        "selected_topics": selected_topics,
        "activity_trace": append_trace(
            state.get("activity_trace"),
            trace_entry(
                "select_domain_topics",
                stage="plan_graph",
                result={
                    "domains": selected_domains,
                    "topics": selected_topics,
                    "rag_topics": rag_topics,
                    "rag_domains": rag_domains,
                    "top_scored": top_scored,
                    "max_domains": max_domains,
                    "max_topics": max_topics,
                },
                reason=" ".join(reason_parts),
            ),
        ),
    }


def enrich_topic_context_node(state: PlanContextState) -> Dict[str, Any]:
    """Build grounding + relation packs for the selected topics (char-budgeted)."""
    session = state.get("session") or {}
    indexer = session.get("semantic_indexer")
    schema_indexer = session.get("indexer")
    question = str(state.get("question") or "")
    topics = list(state.get("selected_topics") or [])
    overview_chars = max(
        MIN_OVERVIEW_CHARS, int(state.get("overview_chars") or DEFAULT_OVERVIEW_CHARS)
    )
    pack = ""
    allowed: List[str] = []
    selected_tables: List[str] = []
    if indexer is not None and hasattr(indexer, "topic_pack"):
        pack = indexer.topic_pack(topics, max_chars=overview_chars)
        allowed = list(indexer.allowed_component_names(topics) or [])
    if not pack:
        lines = ["Selected topic grounding pack:"]
        for entry in state.get("topic_catalog") or []:
            topic = str(entry.get("topic") or "").strip()
            if topics and topic.lower() not in {t.lower() for t in topics}:
                continue
            lines.append(f"TOPIC {topic}")
            if entry.get("description"):
                lines.append(f"  definition: {entry['description']}")
            components = [str(c).strip() for c in (entry.get("components") or []) if str(c).strip()]
            if components:
                lines.append(f"  components: {', '.join(components)}")
                for name in components:
                    if name not in allowed:
                        allowed.append(name)
        pack = truncate_text("\n".join(lines), overview_chars)
    if not allowed:
        for entry in state.get("topic_catalog") or []:
            topic = str(entry.get("topic") or "").strip()
            if topics and topic.lower() not in {t.lower() for t in topics}:
                continue
            for name in entry.get("components") or []:
                label = str(name).strip()
                if label and label not in allowed:
                    allowed.append(label)

    # Expand planning with same-table columns + join-related tables/columns.
    relation_budget = max(
        RELATION_PACK_MIN_CHARS,
        min(RELATION_PACK_MAX_CHARS, overview_chars // 2),
    )
    if schema_indexer is not None and hasattr(schema_indexer, "relation_exploration_pack"):
        try:
            relation_result = schema_indexer.relation_exploration_pack(
                question,
                seed_names=allowed,
                top_k=5,
                max_tables=6,
                max_chars=relation_budget,
            )
            if len(relation_result) >= 3:
                relation_pack, relation_names, relation_tables = (
                    relation_result[0],
                    relation_result[1],
                    relation_result[2],
                )
            else:
                relation_pack, relation_names = relation_result[0], relation_result[1]
                relation_tables = []
            if relation_pack:
                pack = truncate_text(
                    f"{pack}\n\n{relation_pack}".strip() if pack else relation_pack,
                    overview_chars + relation_budget,
                )
            for name in relation_names or []:
                label = str(name).strip()
                if label and label not in allowed:
                    allowed.append(label)
            selected_tables = [
                str(name).strip() for name in (relation_tables or []) if str(name).strip()
            ]
        except Exception:
            logger.exception("Plan graph relation exploration failed")

    reason = (
        f"Built grounding pack for topics={topics} with {len(allowed)} allowed name(s)"
        + (f"; relation exploration selected tables={selected_tables}." if selected_tables else ".")
    )
    return {
        "grounding_pack": pack,
        "allowed_names": allowed,
        "selected_tables": selected_tables,
        "activity_trace": append_trace(
            state.get("activity_trace"),
            trace_entry(
                "enrich_topic_context",
                stage="plan_graph",
                result={
                    "topics": topics,
                    "tables": selected_tables,
                    "allowed_name_count": len(allowed),
                    "allowed_names": allowed[:40],
                    "grounding_chars": len(pack or ""),
                },
                reason=reason,
            ),
        ),
    }


def _synonym_lookup(indexer: Any):
    def synonyms_for(name: str) -> str:
        if indexer is None or not hasattr(indexer, "measure_metadata"):
            return ""
        meta = indexer.measure_metadata(name) or {}
        return str(meta.get("synonyms") or "")

    return synonyms_for


def _diagnostic_measure_context(indexer: Any, names: List[str]) -> str:
    if indexer is None or not hasattr(indexer, "measure_metadata"):
        return ""
    lines: List[str] = []
    seen: set[str] = set()
    for name in names:
        label = str(name or "").strip()
        key = label.lower()
        if not label or key in seen:
            continue
        seen.add(key)
        meta = indexer.measure_metadata(label) or {}
        if not meta:
            continue
        bits = [f"field {label}:"]
        if meta.get("formula"):
            bits.append(f"formula: {meta['formula']}")
        if meta.get("grain"):
            bits.append(f"grain: {meta['grain']}")
        if meta.get("additivity"):
            bits.append(f"additivity: {meta['additivity']}")
        if meta.get("direction"):
            bits.append(f"business direction: {meta['direction']}")
        guidance = measure_trait_guidance(
            str(meta.get("grain") or ""),
            str(meta.get("additivity") or ""),
            str(meta.get("direction") or ""),
        )
        if guidance:
            bits.append(f"use: {guidance}")
        if len(bits) == 1:
            continue
        lines.append(" ".join(bits))
    if not lines:
        return ""
    return "DIAGNOSTIC MEASURE CONTEXT\n" + "\n".join(lines)


def _apply_measure_traits(indexer: Any, matched: List[Dict[str, Any]], use_plan: List[Dict[str, str]]) -> None:
    """Copy grain, additivity, and direction onto the measures a diagnostic capability uses."""
    if indexer is None or not hasattr(indexer, "measure_metadata"):
        return
    names: List[str] = []
    for cap in matched:
        names.extend(str(name).strip() for name in (cap.get("matchedMeasures") or []))
    for item in use_plan:
        if str(item.get("kind") or "") == "measure":
            names.append(str(item.get("name") or "").strip())
    cached: Dict[str, Dict[str, str]] = {}
    for name in names:
        key = name.lower()
        if not name or key in cached:
            continue
        meta = indexer.measure_metadata(name) or {}
        cached[key] = {
            "name": name,
            "grain": str(meta.get("grain") or "").strip(),
            "additivity": str(meta.get("additivity") or "").strip(),
            "direction": str(meta.get("direction") or "").strip(),
        }
    for cap in matched:
        cap["measureTraits"] = [
            dict(cached[str(name).strip().lower()])
            for name in (cap.get("matchedMeasures") or [])
            if str(name).strip().lower() in cached
        ]
    for item in use_plan:
        if str(item.get("kind") or "") != "measure":
            continue
        trait = cached.get(str(item.get("name") or "").strip().lower())
        if not trait:
            continue
        for key in ("grain", "additivity", "direction"):
            if trait[key]:
                item[key] = trait[key]


def select_analytical_operations_node(state: PlanContextState) -> Dict[str, Any]:
    """Match diagnostic capabilities and score analytical operations for the question."""
    """Select analytical operations from analytical_strategy.json to guide deep-dive question framing."""
    question = str(state.get("question") or "")
    diag_caps = list(state.get("diagnostic_capabilities") or [])
    flat_sql = bool(state.get("flat_sql"))
    session = state.get("session") or {}
    indexer = session.get("semantic_indexer")
    matched: List[Dict[str, Any]] = []
    use_plan: List[Dict[str, str]] = []
    allowed = list(state.get("allowed_names") or [])
    grounding_pack = str(state.get("grounding_pack") or "")
    if flat_sql and diag_caps:
        measures: List[str] = []
        dimensions: List[str] = []
        if indexer is not None and hasattr(indexer, "component_names_by_kind"):
            catalog = indexer.component_names_by_kind() or {}
            measures = list(catalog.get("measures") or [])
            dimensions = list(catalog.get("dimensions") or [])
        operations_catalog = _analytical_operations_catalog(state)
        matched = match_diagnostic_capabilities(
            question,
            diag_caps,
            synonyms_for=_synonym_lookup(indexer),
        )
        use_plan = resolve_diagnostic_uses(
            matched,
            operations=operations_catalog,
            measures=measures,
            dimensions=dimensions,
        )
        formulas: Dict[str, str] = {}
        if indexer is not None and hasattr(indexer, "measure_metadata"):
            for cap in matched:
                for measure in cap.get("matchedMeasures") or []:
                    meta = indexer.measure_metadata(str(measure)) or {}
                    formula = str(meta.get("formula") or "").strip()
                    if formula:
                        formulas[str(measure)] = formula
        use_plan = expand_decompose_measures(use_plan, formulas, measures)
        _apply_measure_traits(indexer, matched, use_plan)
        required_operations = operations_for_diagnostic_uses(use_plan, operations_catalog)
        if required_operations:
            operations = required_operations
        else:
            operations = select_analytical_operations(
                question,
                max_operations=5,
                catalog=operations_catalog,
                diagnostic_capabilities=matched or diag_caps,
            )
        context_names = [
            str(item.get("name") or "").strip()
            for item in use_plan
            if str(item.get("kind") or "") in {"measure", "dimension"}
        ]
        for cap in matched:
            context_names.extend(str(name).strip() for name in (cap.get("matchedMeasures") or []))
        for name in context_names:
            if name and name not in allowed:
                allowed.append(name)
        addon = _diagnostic_measure_context(indexer, context_names)
        if addon:
            grounding_pack = f"{addon}\n\n{grounding_pack}".strip()
    else:
        operations = select_analytical_operations(
            question,
            max_operations=5,
            catalog=_analytical_operations_catalog(state),
            diagnostic_capabilities=diag_caps,
        )
    op_names = [str(op.get("operation") or "").strip() for op in operations if op.get("operation")]
    if flat_sql and matched:
        use_summary = ", ".join(
            f"{item.get('kind')}:{item.get('name')}" for item in use_plan if item.get("name")
        )
        cap_names = ", ".join(
            str(cap.get("capability") or "").strip() for cap in matched if cap.get("capability")
        )
        activity = diagnostic_activity_reason(
            matched,
            diagnostic_field_scope(use_plan, matched),
        )
        reason = activity or (
            f"Think mode matched diagnostic capability {cap_names}. "
            f"Using applicable measures and declared uses ({use_summary}). "
            f"Analytical strategies: {', '.join(op_names)}."
        )
    else:
        reason = (
            f"Selected {len(op_names)} analytical operation(s) from catalog: "
            f"{', '.join(op_names)} to guide question framing."
        )
    logger.info("Plan graph selected analytical operations=%s", op_names)
    result: Dict[str, Any] = {
        "analytical_operations": operations,
        "selected_operations": op_names,
        "activity_trace": append_trace(
            state.get("activity_trace"),
            trace_entry(
                "select_analytical_operations",
                stage="plan_graph",
                result={
                    "operations": op_names,
                    "matched_capabilities": [
                        str(cap.get("capability") or "") for cap in matched
                    ],
                    "diagnostic_uses": use_plan,
                },
                reason=reason,
            ),
        ),
    }
    if flat_sql:
        result["matched_diagnostic_capabilities"] = matched
        result["diagnostic_use_plan"] = use_plan
        result["allowed_names"] = allowed
        if grounding_pack:
            result["grounding_pack"] = grounding_pack
    return result


def draft_chart_questions_node(state: PlanContextState) -> Dict[str, Any]:
    """Ask the LLM (or fallback) for an InvestigationPlan grounded in the packs."""
    work_state: Dict[str, Any] = {
        "token_usage": dict(state.get("token_usage") or {}),
        "request_id": state.get("request_id"),
    }
    plan = build_investigation_plan(
        str(state.get("question") or ""),
        persona=state.get("persona") or {},
        semantic_overview=str(state.get("semantic_overview") or ""),
        grounding_pack=str(state.get("grounding_pack") or ""),
        validation_feedback=str(state.get("validation_feedback") or ""),
        max_charts=int(state.get("max_charts") or 5),
        overview_chars=int(state.get("overview_chars") or DEFAULT_OVERVIEW_CHARS),
        state=work_state,
        strategy=state.get("strategy"),
        selected_domains=list(state.get("selected_domains") or []),
        selected_topics=list(state.get("selected_topics") or []),
        flat_sql=bool(state.get("flat_sql")),
        analytical_operations=list(state.get("analytical_operations") or []),
        diagnostic_capabilities=(
            list(state.get("matched_diagnostic_capabilities") or [])
            if state.get("flat_sql") and "matched_diagnostic_capabilities" in state
            else list(state.get("diagnostic_capabilities") or [])
        ),
        diagnostic_use_plan=(
            list(state.get("diagnostic_use_plan") or [])
            if state.get("flat_sql") and "diagnostic_use_plan" in state
            else None
        ),
    )
    plan_dict = plan.model_dump() if hasattr(plan, "model_dump") else dict(plan)
    # Prefer selector scope when LLM omits domain/topics.
    if not str(plan_dict.get("domain") or "").strip() and state.get("selected_domains"):
        plan_dict["domain"] = state["selected_domains"][0]
    if not plan_dict.get("topics") and state.get("selected_topics"):
        plan_dict["topics"] = list(state.get("selected_topics") or [])
    charts = [c for c in (plan_dict.get("charts") or []) if isinstance(c, dict)]
    questions = [
        {
            "index": index,
            "question": str(chart.get("question") or "").strip(),
            "analytical_strategy": str(chart.get("analytical_strategy") or chart.get("operation") or "").strip(),
            "operation": str(chart.get("operation") or chart.get("analytical_strategy") or "").strip(),
            "purpose": str(chart.get("purpose") or "").strip(),
            "why": str(chart.get("purpose") or "").strip()
            or "Supporting investigation question toward the user ask.",
            "topic": str(chart.get("topic") or "").strip(),
        }
        for index, chart in enumerate(charts, start=1)
        if str(chart.get("question") or "").strip()
    ]
    if not plan_dict.get("query_analytical_strategies") and charts:
        plan_dict["query_analytical_strategies"] = [
            {
                "question": str(chart.get("question") or "").strip(),
                "analytical_strategy": str(chart.get("analytical_strategy") or chart.get("operation") or "").strip(),
            }
            for chart in charts
            if str(chart.get("question") or "").strip()
        ]
    strategy = state.get("strategy") or {}
    repair = bool(str(state.get("validation_feedback") or "").strip())
    diagnostic_reason = ""
    if state.get("flat_sql") and state.get("diagnostic_use_plan"):
        diagnostic_reason = diagnostic_activity_reason(
            list(state.get("matched_diagnostic_capabilities") or []),
            diagnostic_field_scope(
                list(state.get("diagnostic_use_plan") or []),
                list(state.get("matched_diagnostic_capabilities") or []),
            ),
        )
    reason = (
        ("Repair rewrite using prior validation feedback. " if repair else "")
        + (
            diagnostic_reason
            if diagnostic_reason
            else (
                f"Drafted {len(questions)} question(s) using strategy "
                f"'{strategy.get('id') or ''}' against selected topics "
                f"{list(state.get('selected_topics') or [])}. "
                + (
                    str(plan_dict.get("rationale") or "").strip()
                    or "Each question targets a finding that can compose the final answer."
                )
            )
        )
    )
    return {
        "plan": plan_dict,
        "token_usage": work_state.get("token_usage") or {},
        "validation_ok": False,
        "validation_errors": [],
        "activity_trace": append_trace(
            state.get("activity_trace"),
            trace_entry(
                "draft_chart_questions",
                stage="plan_graph",
                result={
                    "strategy_id": strategy.get("id") or "",
                    "questions": questions,
                    "rationale": str(plan_dict.get("rationale") or "").strip(),
                    "repair": repair,
                },
                reason=reason,
            ),
        ),
    }


def _chart_mentions_allowed(question: str, allowed: List[str]) -> bool:
    q = _normalize(question)
    if not q:
        return False
    if not allowed:
        return True
    for name in allowed:
        label = _normalize(name)
        if label and label in q:
            return True
        # Token overlap for multi-word measures (e.g. "Failed Acquisition Cost").
        parts = [p for p in re.findall(r"[a-z0-9]+", label) if len(p) >= 4]
        if parts and all(part in q for part in parts):
            return True
    return False


def _dedupe_charts(charts: List[dict]) -> List[dict]:
    seen: set[str] = set()
    out: List[dict] = []
    for chart in charts:
        if not isinstance(chart, dict):
            continue
        key = _normalize(str(chart.get("question") or chart.get("title") or ""))
        if not key or key in seen:
            continue
        seen.add(key)
        out.append(chart)
    return out


def _grounded_fallback_plan(state: PlanContextState) -> Dict[str, Any]:
    question = str(state.get("question") or "")
    persona = state.get("persona") or {}
    strategy = state.get("strategy") or {}
    max_charts = int(state.get("max_charts") or 5)
    topics = list(state.get("selected_topics") or [])
    domains = list(state.get("selected_domains") or [])
    allowed = list(state.get("allowed_names") or [])
    base = fallback_investigation_plan(
        question,
        persona=persona,
        max_charts=max_charts,
        strategy=strategy,
    )
    data = base.model_dump()
    data["domain"] = domains[0] if domains else data.get("domain") or ""
    data["topics"] = topics or list(data.get("topics") or [])
    data["rationale"] = (
        (data.get("rationale") or "")
        + " Grounded fallback restricted to selected topic components."
    ).strip()

    measure_like = [
        name
        for name in allowed
        if any(
            token in name.lower()
            for token in ("cost", "rate", "average", "avg", "spend", "count", "total")
        )
    ] or allowed
    dim_like = [name for name in allowed if name not in measure_like]

    charts: List[dict] = []
    skeleton = charts_from_strategy(
        get_strategy(str(strategy.get("id") or "")) if strategy.get("id") else strategy,
        question,
        max_charts=max_charts,
    ) or data.get("charts") or []

    for index, spec in enumerate(skeleton[:max_charts]):
        measure = measure_like[index % len(measure_like)] if measure_like else ""
        dim = dim_like[index % len(dim_like)] if dim_like and index > 0 else ""
        topic = topics[index % len(topics)] if topics else ""
        if measure and dim:
            q = f"What is {measure} by {dim}?"
            title = f"{measure} by {dim}"
        elif measure:
            q = f"What is total {measure}?"
            title = measure
        else:
            q = str(spec.get("question") or question)
            title = str(spec.get("title") or f"Chart {index + 1}")
        charts.append(
            {
                "level": str(spec.get("level") or "middle"),
                "title": title,
                "question": q,
                "viz_hint": str(spec.get("viz_hint") or ("kpi" if index == 0 else "bar")),
                "purpose": str(spec.get("purpose") or "Grounded from selected topic components."),
                "context_anchor": str(spec.get("context_anchor") or ""),
                "include_in_dashboard": True,
                "topic": topic,
                "components": [c for c in (measure, dim) if c],
                "measure_hints": [measure] if measure else [],
            }
        )
    if not charts and measure_like:
        charts = [
            {
                "level": "top",
                "title": measure_like[0],
                "question": f"What is total {measure_like[0]}?",
                "viz_hint": "kpi",
                "purpose": "Headline metric from selected topics.",
                "context_anchor": "",
                "include_in_dashboard": True,
                "topic": topics[0] if topics else "",
                "components": [measure_like[0]],
                "measure_hints": [measure_like[0]],
            }
        ]
    data["charts"] = charts
    return InvestigationPlan.model_validate(data).model_dump()


def _chart_text(chart: Mapping[str, Any]) -> str:
    return " ".join(
        [
            str(chart.get("question") or ""),
            str(chart.get("title") or ""),
            str(chart.get("purpose") or ""),
            " ".join(str(item) for item in (chart.get("measure_hints") or [])),
            " ".join(str(item) for item in (chart.get("components") or [])),
        ]
    )


def _diagnostic_coverage_errors(charts: List[dict], state: Mapping[str, Any]) -> List[str]:
    """Think-mode gaps against applicable measures and classified diagnostic uses."""
    if not state.get("flat_sql"):
        return []
    use_plan = [item for item in (state.get("diagnostic_use_plan") or []) if isinstance(item, dict)]
    matched = [
        item
        for item in (state.get("matched_diagnostic_capabilities") or [])
        if isinstance(item, dict)
    ]
    if not use_plan and not matched:
        return []
    allowed = {str(name).strip().lower() for name in (state.get("allowed_names") or []) if str(name).strip()}
    errors: List[str] = []
    for item in use_plan:
        kind = str(item.get("kind") or "")
        name = str(item.get("name") or "").strip()
        if not name:
            continue
        if kind == "strategy":
            covered = any(
                str(chart.get("operation") or chart.get("analytical_strategy") or "").strip().lower() == name.lower()
                or question_mentions_operation(name, _chart_text(chart))
                for chart in charts
            )
            if not covered:
                errors.append(f"Missing diagnostic strategy '{name}'.")
            continue
        if kind == "other" and name.lower() not in allowed:
            continue
        if kind in {"measure", "dimension", "other"} and not any(
            measure_mentioned(name, _chart_text(chart)) for chart in charts
        ):
            errors.append(f"Missing diagnostic {kind} '{name}'.")
    for cap in matched:
        for measure in cap.get("matchedMeasures") or []:
            label = str(measure).strip()
            if label and not any(measure_mentioned(label, _chart_text(chart)) for chart in charts):
                errors.append(f"Questions do not use applicable measure '{label}'.")
    errors.extend(_diagnostic_scope_errors(charts, state, use_plan, matched))
    return errors


def _phrase_in_text(name: str, text: str) -> bool:
    label = str(name or "").strip().lower()
    if len(label) < 3:
        return False
    return re.search(r"(?<!\w)" + re.escape(label) + r"(?!\w)", str(text or "").lower()) is not None


def _diagnostic_scope_errors(
    charts: List[dict],
    state: Mapping[str, Any],
    use_plan: Sequence[Mapping[str, Any]],
    matched: Sequence[Mapping[str, Any]],
) -> List[str]:
    """Reject strategies, measures, and dimensions outside the configured diagnostic scope."""
    scope = diagnostic_field_scope(use_plan, matched)
    strategies = scope["strategies"]
    if not strategies:
        return []
    allowed_ops = {name.lower() for name in strategies}
    measures = scope["measures"]
    dimensions = scope["dimensions"]
    measure_keys = {name.lower() for name in measures}
    dimension_keys = {name.lower() for name in dimensions}
    field_keys = measure_keys | dimension_keys
    catalog_measures: List[str] = []
    catalog_dimensions: List[str] = []
    session = state.get("session") or {}
    indexer = session.get("semantic_indexer") if isinstance(session, Mapping) else None
    if indexer is not None and hasattr(indexer, "component_names_by_kind"):
        catalog = indexer.component_names_by_kind() or {}
        catalog_measures = list(catalog.get("measures") or [])
        catalog_dimensions = list(catalog.get("dimensions") or [])
    errors: List[str] = []
    allowed_strategy_text = ", ".join(strategies)
    allowed_field_text = ", ".join(measures + dimensions)
    for chart in charts:
        operation = str(chart.get("operation") or chart.get("analytical_strategy") or "").strip()
        if operation and operation.lower() not in allowed_ops:
            errors.append(
                f"Question uses strategy '{operation}', which is not configured. "
                f"Use only: {allowed_strategy_text}."
            )
        if measure_keys:
            for hint in chart.get("measure_hints") or []:
                label = str(hint).strip()
                if label and label.lower() not in measure_keys:
                    errors.append(
                        f"Question uses measure '{label}', which is outside the configured measures "
                        f"({', '.join(measures)})."
                    )
        if dimension_keys:
            for component in chart.get("components") or []:
                label = str(component).strip()
                if label and label.lower() not in field_keys:
                    errors.append(
                        f"Question uses '{label}', which is outside the configured measures and dimensions "
                        f"({allowed_field_text})."
                    )
        text = _chart_text(chart)
        if measure_keys:
            for name in catalog_measures:
                if name.lower() in measure_keys or any(name.lower() in allowed.lower() for allowed in measures):
                    continue
                if _phrase_in_text(name, text):
                    errors.append(
                        f"Question mentions measure '{name}', which is outside the configured measures "
                        f"({', '.join(measures)})."
                    )
        if dimension_keys:
            for name in catalog_dimensions:
                if name.lower() in dimension_keys or any(
                    name.lower() in allowed.lower() for allowed in dimensions
                ):
                    continue
                if _phrase_in_text(name, text):
                    errors.append(
                        f"Question mentions dimension '{name}', which is outside the configured dimensions "
                        f"({', '.join(dimensions)})."
                    )
    return errors


def _with_diagnostic_feedback(feedback: str, coverage_errors: List[str]) -> str:
    if not coverage_errors:
        return feedback
    return (
        feedback
        + "\nAlso follow the matched diagnostic capability:\n- "
        + "\n- ".join(coverage_errors)
    )


def validate_questions_node(state: PlanContextState) -> Dict[str, Any]:
    """Ground drafted charts against the semantic pack; queue repair when needed."""
    plan = dict(state.get("plan") or {})
    charts = [c for c in (plan.get("charts") or []) if isinstance(c, dict)]
    charts = _dedupe_charts(charts)
    max_charts = _chart_limit(state)
    if max_charts is not None:
        charts = charts[:max_charts]
    allowed = list(state.get("allowed_names") or [])
    allowed_lower = {a.lower() for a in allowed}
    errors: List[str] = []

    if not charts:
        errors.append("Plan has no charts.")

    grounded: List[dict] = []
    for chart in charts:
        question = str(chart.get("question") or "").strip()
        hints = [str(h).strip() for h in (chart.get("measure_hints") or []) if str(h).strip()]
        components = [str(c).strip() for c in (chart.get("components") or []) if str(c).strip()]
        bad_hints = [h for h in hints if h.lower() not in allowed_lower] if allowed_lower else []
        bad_components = [c for c in components if c.lower() not in allowed_lower] if allowed_lower else []
        if bad_hints or bad_components:
            errors.append(
                f"Chart '{chart.get('title') or question}' references unknown "
                f"names: {', '.join(bad_hints + bad_components)}"
            )
            continue
        if allowed and not (
            _chart_mentions_allowed(question, allowed)
            or any(h.lower() in allowed_lower for h in hints)
            or any(c.lower() in allowed_lower for c in components)
        ):
            errors.append(
                f"Chart question is not grounded in selected components: {question}"
            )
            continue
        # Fill missing topic / hints from selection when possible.
        if not str(chart.get("topic") or "").strip() and state.get("selected_topics"):
            chart = dict(chart)
            chart["topic"] = state["selected_topics"][0]
        if not hints and allowed:
            mentioned = [name for name in allowed if _normalize(name) in _normalize(question)]
            if mentioned:
                chart = dict(chart)
                chart["measure_hints"] = mentioned[:3]
                if not chart.get("components"):
                    chart["components"] = mentioned[:3]
        grounded.append(chart)

    if not grounded and charts:
        errors.append("No charts remained after grounding checks.")

    coverage_errors = _diagnostic_coverage_errors(grounded or charts, state)
    repair_count = int(state.get("repair_count") or 0)
    if errors and repair_count < _MAX_REPAIR and not grounded:
        feedback = _with_diagnostic_feedback(
            (
                "Previous draft failed grounding validation:\n- "
                + "\n- ".join(errors)
                + "\nRewrite every chart.question using only component/measure names "
                "from the grounding pack (topic components or schema relation columns). "
                "Set measure_hints and components accordingly."
            ),
            coverage_errors,
        )
        logger.info("Plan graph validation failed; scheduling repair: %s", errors)
        return {
            "plan": {**plan, "charts": charts},
            "validation_ok": False,
            "validation_errors": errors,
            "repair_count": repair_count + 1,
            "validation_feedback": feedback,
            "activity_trace": append_trace(
                state.get("activity_trace"),
                trace_entry(
                    "validate_questions",
                    stage="plan_graph",
                    status="repair",
                    result={
                        "ok": False,
                        "errors": errors,
                        "repair_count": repair_count + 1,
                        "action": "repair",
                    },
                    reason="Validation failed grounding checks; scheduling one repair rewrite.",
                ),
            ),
        }

    if errors and repair_count < _MAX_REPAIR and grounded and len(grounded) < max(1, len(charts) // 2):
        # Too many charts failed — ask the draft node to rewrite once.
        feedback = _with_diagnostic_feedback(
            (
                "Previous draft failed grounding validation:\n- "
                + "\n- ".join(errors)
                + "\nRewrite every chart.question using only component/measure names "
                "from the grounding pack (topic components or schema relation columns). "
                "Set measure_hints and components accordingly."
            ),
            coverage_errors,
        )
        logger.info("Plan graph validation weak; scheduling repair: %s", errors)
        return {
            "plan": {**plan, "charts": grounded},
            "validation_ok": False,
            "validation_errors": errors,
            "repair_count": repair_count + 1,
            "validation_feedback": feedback,
            "activity_trace": append_trace(
                state.get("activity_trace"),
                trace_entry(
                    "validate_questions",
                    stage="plan_graph",
                    status="repair",
                    result={
                        "ok": False,
                        "errors": errors,
                        "kept_charts": len(grounded),
                        "repair_count": repair_count + 1,
                        "action": "repair",
                    },
                    reason="Too few charts remained after grounding; scheduling repair rewrite.",
                ),
            ),
        }

    if not grounded:
        logger.info("Plan graph validation failed after repair; using grounded fallback")
        fallback = _grounded_fallback_plan(state)
        return {
            "plan": fallback,
            "validation_ok": True,
            "validation_errors": errors,
            "validation_feedback": "",
            "activity_trace": append_trace(
                state.get("activity_trace"),
                trace_entry(
                    "validate_questions",
                    stage="plan_graph",
                    status="fallback",
                    result={
                        "ok": True,
                        "errors": errors,
                        "used_fallback": True,
                        "chart_count": len(fallback.get("charts") or []),
                    },
                    reason="Grounding failed after repair; used strategy skeleton + allowed components fallback.",
                ),
            ),
        }

    if coverage_errors and repair_count < _MAX_REPAIR:
        feedback = (
            "Think mode must follow the matched diagnostic capability.\n- "
            + "\n- ".join(coverage_errors)
            + "\nRewrite the flat questions so each required strategy, measure, and dimension is used "
            "on the applicable measures. Keep every question grounded in the allowed names."
        )
        logger.info("Plan graph diagnostic coverage failed; scheduling repair: %s", coverage_errors)
        return {
            "plan": {**plan, "charts": grounded},
            "validation_ok": False,
            "validation_errors": errors + coverage_errors,
            "repair_count": repair_count + 1,
            "validation_feedback": feedback,
            "activity_trace": append_trace(
                state.get("activity_trace"),
                trace_entry(
                    "validate_questions",
                    stage="plan_graph",
                    status="repair",
                    result={
                        "ok": False,
                        "errors": coverage_errors,
                        "repair_count": repair_count + 1,
                        "action": "repair",
                    },
                    reason="Think-mode plan did not cover the diagnostic capability uses.",
                ),
            ),
        }

    if not str(plan.get("domain") or "").strip() and state.get("selected_domains"):
        plan["domain"] = state["selected_domains"][0]
    if not plan.get("topics") and state.get("selected_topics"):
        plan["topics"] = list(state.get("selected_topics") or [])
    plan["charts"] = grounded
    return {
        "plan": plan,
        "validation_ok": True,
        "validation_errors": errors,
        "validation_feedback": "",
        "activity_trace": append_trace(
            state.get("activity_trace"),
            trace_entry(
                "validate_questions",
                stage="plan_graph",
                result={
                    "ok": True,
                    "errors": errors,
                    "chart_count": len(grounded),
                    "used_fallback": False,
                },
                reason=(
                    f"Accepted {len(grounded)} grounded chart question(s)"
                    + (f" with warnings: {errors}" if errors else ".")
                ),
            ),
        ),
    }


def route_after_validate(state: PlanContextState) -> str:
    if state.get("validation_ok"):
        return "done"
    if int(state.get("repair_count") or 0) <= _MAX_REPAIR and state.get("validation_feedback"):
        return "repair"
    return "done"


def build_plan_context_graph():
    """Wire the plan-context LangGraph (no checkpointer; one-shot planning)."""
    workflow = StateGraph(PlanContextState)
    workflow.add_node("load_semantic_context", load_semantic_context_node)
    workflow.add_node("select_domain_topics", select_domain_topics_node)
    workflow.add_node("enrich_topic_context", enrich_topic_context_node)
    workflow.add_node("select_analytical_operations", select_analytical_operations_node)
    workflow.add_node("draft_chart_questions", draft_chart_questions_node)
    workflow.add_node("validate_questions", validate_questions_node)

    workflow.add_edge(START, "load_semantic_context")
    workflow.add_edge("load_semantic_context", "select_domain_topics")
    workflow.add_edge("select_domain_topics", "enrich_topic_context")
    workflow.add_edge("enrich_topic_context", "select_analytical_operations")
    workflow.add_edge("select_analytical_operations", "draft_chart_questions")
    workflow.add_edge("draft_chart_questions", "validate_questions")
    workflow.add_conditional_edges(
        "validate_questions",
        route_after_validate,
        {"repair": "draft_chart_questions", "done": END},
    )
    return workflow.compile()


_plan_context_graph = None


def get_plan_context_graph():
    global _plan_context_graph
    if _plan_context_graph is None:
        _plan_context_graph = build_plan_context_graph()
    return _plan_context_graph


def run_plan_context_graph(
    question: str,
    *,
    persona: Dict[str, Any],
    strategy: Dict[str, Any],
    session: Dict[str, Any],
    max_charts: int = 5,
    flat_sql: bool = False,
    overview_chars: int = DEFAULT_OVERVIEW_CHARS,
    max_domains: int = 2,
    max_topics: int = 4,
    request_id: Optional[str] = None,
    token_usage: Optional[Dict[str, Any]] = None,
    compiled=None,
) -> Dict[str, Any]:
    """Run the modular plan subgraph; returns plan dict + selection + usage."""
    graph = compiled or get_plan_context_graph()
    initial: PlanContextState = {
        "question": question,
        "persona": persona or {},
        "strategy": strategy or {},
        "session": session or {},
        "max_charts": max_charts,
        "flat_sql": bool(flat_sql),
        "overview_chars": overview_chars,
        "max_domains": max_domains,
        "max_topics": max_topics,
        "request_id": request_id,
        "token_usage": dict(token_usage or {}),
        "repair_count": 0,
        "validation_feedback": "",
        "validation_ok": False,
        "activity_trace": [],
        "selected_tables": [],
        "analytical_operations": [],
        "selected_operations": [],
        "diagnostic_capabilities": [],
    }
    result = graph.invoke(initial)
    plan = result.get("plan") or {}
    if not plan.get("charts"):
        plan = _grounded_fallback_plan(result)
    return {
        "plan": plan,
        "selected_domains": list(result.get("selected_domains") or []),
        "selected_topics": list(result.get("selected_topics") or []),
        "selected_tables": list(result.get("selected_tables") or []),
        "selected_operations": list(result.get("selected_operations") or []),
        "diagnostic_capabilities": list(result.get("diagnostic_capabilities") or []),
        "grounding_pack": str(result.get("grounding_pack") or ""),
        "allowed_names": list(result.get("allowed_names") or []),
        "token_usage": result.get("token_usage") or {},
        "validation_errors": list(result.get("validation_errors") or []),
        "activity_trace": list(result.get("activity_trace") or []),
    }

