"""LangGraph: load semantic context → select topics → enrich → draft → validate."""
from __future__ import annotations

import logging
import re
from typing import Any, Dict, List, Mapping, Optional, TypedDict

from langgraph.graph import END, START, StateGraph

from helicalbi.sql_agent.activity_details import append_trace, trace_entry
from helicalbi.sql_agent.modes import truncate_text
from helicalbi.sql_agent.models import InvestigationPlan
from helicalbi.sql_agent.nodes.investigation_planner import (
    build_investigation_plan,
    fallback_investigation_plan,
)
from helicalbi.sql_agent.strategy_tree import charts_from_strategy, get_strategy

logger = logging.getLogger(__name__)

_MAX_REPAIR = 1


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
    plan: Dict[str, Any]
    validation_ok: bool
    validation_errors: List[str]
    repair_count: int
    validation_feedback: str
    activity_trace: List[Dict[str, Any]]


def _chart_limit(state: Mapping) -> int:
    """Usual chart ceiling, plus think-mode room to split subquery questions."""
    from helicalbi.sql_agent.config import THINK_EXTRA_QUESTIONS

    base = max(1, int(state.get("max_charts") or 5))
    if state.get("flat_sql"):
        return base + max(0, int(THINK_EXTRA_QUESTIONS))
    return base


def _normalize(text: str) -> str:
    return re.sub(r"\s+", " ", str(text or "").strip().lower())


def _score_topic(question: str, entry: Dict[str, Any]) -> float:
    q = _normalize(question)
    if not q:
        return 0.0
    tokens = set(re.findall(r"[a-z0-9]+", q))
    blob = " ".join(
        [
            str(entry.get("domain") or ""),
            str(entry.get("topic") or ""),
            str(entry.get("description") or ""),
            " ".join(str(c) for c in (entry.get("components") or [])),
        ]
    ).lower()
    score = 0.0
    topic = str(entry.get("topic") or "").strip().lower()
    domain = str(entry.get("domain") or "").strip().lower()
    if topic and topic in q:
        score += 5.0
    if domain and domain in q:
        score += 3.0
    for token in tokens:
        if len(token) < 3:
            continue
        if token in blob:
            score += 1.0
    for component in entry.get("components") or []:
        name = str(component or "").strip().lower()
        if name and name in q:
            score += 2.5
        for part in re.findall(r"[a-z0-9]+", name):
            if len(part) >= 4 and part in tokens:
                score += 0.5
    return score


def load_semantic_context_node(state: PlanContextState) -> Dict[str, Any]:
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
    logger.info(
        "Plan graph load overview_chars=%s topics=%s",
        len(overview),
        len(catalog),
    )
    reason = (
        f"Loaded semantic overview ({len(overview)} chars) and topic catalog "
        f"with {len(catalog)} entr(y/ies)."
    )
    return {
        "semantic_overview": overview,
        "topic_catalog": catalog,
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
    session = state.get("session") or {}
    indexer = session.get("semantic_indexer")
    schema_indexer = session.get("indexer")
    question = str(state.get("question") or "")
    topics = list(state.get("selected_topics") or [])
    overview_chars = max(500, int(state.get("overview_chars") or 3500))
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
    relation_budget = max(800, min(2500, overview_chars // 2))
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


def draft_chart_questions_node(state: PlanContextState) -> Dict[str, Any]:
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
        overview_chars=int(state.get("overview_chars") or 3500),
        state=work_state,
        strategy=state.get("strategy"),
        selected_domains=list(state.get("selected_domains") or []),
        selected_topics=list(state.get("selected_topics") or []),
        flat_sql=bool(state.get("flat_sql")),
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
            "purpose": str(chart.get("purpose") or "").strip(),
            "why": str(chart.get("purpose") or "").strip()
            or "Supporting investigation question toward the user ask.",
            "topic": str(chart.get("topic") or "").strip(),
        }
        for index, chart in enumerate(charts, start=1)
        if str(chart.get("question") or "").strip()
    ]
    strategy = state.get("strategy") or {}
    repair = bool(str(state.get("validation_feedback") or "").strip())
    reason = (
        ("Repair rewrite using prior validation feedback. " if repair else "")
        + f"Drafted {len(questions)} question(s) using strategy "
        f"'{strategy.get('id') or ''}' against selected topics "
        f"{list(state.get('selected_topics') or [])}. "
        + (
            str(plan_dict.get("rationale") or "").strip()
            or "Each question targets a finding that can compose the final answer."
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


def validate_questions_node(state: PlanContextState) -> Dict[str, Any]:
    plan = dict(state.get("plan") or {})
    charts = [c for c in (plan.get("charts") or []) if isinstance(c, dict)]
    charts = _dedupe_charts(charts)
    max_charts = _chart_limit(state)
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

    repair_count = int(state.get("repair_count") or 0)
    if errors and repair_count < _MAX_REPAIR and not grounded:
        feedback = (
            "Previous draft failed grounding validation:\n- "
            + "\n- ".join(errors)
            + "\nRewrite every chart.question using only component/measure names "
            "from the grounding pack (topic components or schema relation columns). "
            "Set measure_hints and components accordingly."
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
        feedback = (
            "Previous draft failed grounding validation:\n- "
            + "\n- ".join(errors)
            + "\nRewrite every chart.question using only component/measure names "
            "from the grounding pack (topic components or schema relation columns). "
            "Set measure_hints and components accordingly."
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
    workflow = StateGraph(PlanContextState)
    workflow.add_node("load_semantic_context", load_semantic_context_node)
    workflow.add_node("select_domain_topics", select_domain_topics_node)
    workflow.add_node("enrich_topic_context", enrich_topic_context_node)
    workflow.add_node("draft_chart_questions", draft_chart_questions_node)
    workflow.add_node("validate_questions", validate_questions_node)

    workflow.add_edge(START, "load_semantic_context")
    workflow.add_edge("load_semantic_context", "select_domain_topics")
    workflow.add_edge("select_domain_topics", "enrich_topic_context")
    workflow.add_edge("enrich_topic_context", "draft_chart_questions")
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
    overview_chars: int = 3500,
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
        "grounding_pack": str(result.get("grounding_pack") or ""),
        "allowed_names": list(result.get("allowed_names") or []),
        "token_usage": result.get("token_usage") or {},
        "validation_errors": list(result.get("validation_errors") or []),
        "activity_trace": list(result.get("activity_trace") or []),
    }
