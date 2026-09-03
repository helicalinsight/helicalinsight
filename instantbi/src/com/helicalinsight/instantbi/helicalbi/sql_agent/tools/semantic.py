"""Semantic-model RAG tools (domain/topic first, metadata fallback)."""
from __future__ import annotations

from typing import Annotated, Any

from langchain_core.tools import tool
from langgraph.prebuilt import InjectedState

from helicalbi.sql_agent.config import DEFAULT_SCHEMA_TOP_K
from helicalbi.sql_agent.database.schema_indexer import get_indexer
from helicalbi.sql_agent.database.semantic_indexer import get_semantic_indexer
from helicalbi.sql_agent.modes import profile_from_state, truncate_text
from helicalbi.sql_agent.tools.context import AgentToolContext


class SemanticTools:
    """Retrieve domain/topics and enriched model content; fall back to metadata RAG."""

    def retrieve(self, question: str, state: dict[str, Any]) -> dict[str, Any]:
        ctx = AgentToolContext(state)
        session = ctx.session
        profile = profile_from_state(state)
        cache_key = (question or "").strip().lower()
        cache = session.get("_semantic_cache")
        if not isinstance(cache, dict):
            cache = {}
            session["_semantic_cache"] = cache

        if cache_key and cache_key in cache:
            hit = dict(cache[cache_key])
            hit["cached"] = True
            hit["state_patch"] = {
                "current_sub_question": question,
                "current_semantic_context": hit.get("model_context") or "",
                "selected_domains": hit.get("domains") or [],
                "selected_topics": hit.get("topics") or [],
                "session_context": session,
            }
            if hit.get("schema"):
                hit["state_patch"]["current_schema_subset"] = hit.get("schema")
            return hit

        # Reuse last semantic hit for new facets in fast/balanced (token save).
        last = session.get("_last_semantic")
        if (
            profile.reuse_semantic
            and isinstance(last, dict)
            and last.get("sufficient")
            and (state.get("selected_topics") or last.get("topics"))
        ):
            reused = {
                "ok": True,
                "sufficient": True,
                "used_metadata_fallback": bool(last.get("used_metadata_fallback")),
                "domains": list(last.get("domains") or []),
                "topics": list(last.get("topics") or []),
                "model_context": last.get("model_context") or "",
                "schema": last.get("schema") or "",
                "cached": True,
                "reused": True,
                "state_patch": {
                    "current_sub_question": question,
                    "current_semantic_context": state.get("current_semantic_context")
                    or last.get("model_context")
                    or "",
                    "selected_domains": state.get("selected_domains") or last.get("domains") or [],
                    "selected_topics": state.get("selected_topics") or last.get("topics") or [],
                    "session_context": session,
                },
            }
            return reused

        top_k = int(state.get("schema_top_k") or DEFAULT_SCHEMA_TOP_K)
        semantic = get_semantic_indexer(ctx.catalog_id).retrieve(question, top_k=top_k)
        sufficient = bool(semantic.get("sufficient"))
        model_context = str(semantic.get("prompt") or "").strip()
        used_metadata_fallback = False
        schema = ""
        if not sufficient:
            schema = get_indexer(ctx.catalog_id).retrieve_schema(question, top_k=top_k)
            used_metadata_fallback = bool(schema)
        patch: dict[str, Any] = {
            "current_sub_question": question,
            "current_semantic_context": model_context,
            "selected_domains": semantic.get("domains") or [],
            "selected_topics": semantic.get("topics") or [],
            "session_context": session,
        }
        if schema:
            patch["current_schema_subset"] = schema
        payload = {
            "ok": True,
            "sufficient": sufficient,
            "used_metadata_fallback": used_metadata_fallback,
            "domains": semantic.get("domains") or [],
            "topics": semantic.get("topics") or [],
            "model_context": model_context or "(no matching semantic-model content)",
            "schema": schema,
            "cached": False,
            "state_patch": patch,
        }
        if cache_key:
            # Cache a flat copy without state_patch/session to avoid circular JSON refs.
            stored = {
                "ok": True,
                "sufficient": sufficient,
                "used_metadata_fallback": used_metadata_fallback,
                "domains": list(semantic.get("domains") or []),
                "topics": list(semantic.get("topics") or []),
                "model_context": truncate_text(
                    model_context or "(no matching semantic-model content)", 2000
                ),
                "schema": truncate_text(str(schema or ""), 1500),
                "cached": True,
            }
            cache[cache_key] = stored
            session["_last_semantic"] = stored
            session["_semantic_cache"] = cache
            patch["session_context"] = session
            payload["state_patch"] = patch
        return payload


semantic_tools = SemanticTools()


@tool
def retrieve_semantic_model(
    question: str,
    state: Annotated[dict, InjectedState],
) -> str:
    """RAG the semantic model to pick domain/topics and their definitions, metrics, and query explanations. Falls back to metadata table RAG when the model is not sufficient."""
    return AgentToolContext.dump(semantic_tools.retrieve(question, state))
