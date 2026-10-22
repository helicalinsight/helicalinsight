"""Build prompt context for think opening insight."""
from __future__ import annotations

from typing import Any, Mapping, Sequence

from helicalbi.interactive.think.constants import NONE_PLACEHOLDER


class OpeningContextBuilder:
    """Format numbered questions + metadata for the opening insight prompt."""

    def sql_context(
        self,
        asked_questions: Sequence[str],
        plan: Mapping[str, Any] | None = None,
    ) -> str:
        lines = []
        for index, asked in enumerate(asked_questions, start=1):
            text = str(asked or "").strip()
            if text:
                lines.append(f"{index}. {text}")
        body = "\n".join(lines) if lines else NONE_PLACEHOLDER
        rationale = str((plan or {}).get("rationale") or "").strip()
        if rationale:
            return f"{body}\n\nRationale: {rationale}"
        return body

    def metadata(
        self,
        plan: Mapping[str, Any] | None = None,
        plan_payload: Mapping[str, Any] | None = None,
    ) -> dict[str, Any]:
        payload = plan_payload if isinstance(plan_payload, Mapping) else {}
        plan_map = plan if isinstance(plan, Mapping) else {}
        domain = payload.get("selected_domains") or plan_map.get("domain") or []
        topics = payload.get("selected_topics") or plan_map.get("topics") or []
        tables = payload.get("selected_tables") or plan_map.get("tables") or []
        return {
            "required_tables": list(tables) if not isinstance(tables, str) else [tables],
            "domain": list(domain) if not isinstance(domain, str) else [domain],
            "topics": list(topics) if not isinstance(topics, str) else [topics],
        }


_CONTEXT = OpeningContextBuilder()


def opening_sql_context(
    asked_questions: Sequence[str],
    plan: Mapping[str, Any] | None = None,
) -> str:
    return _CONTEXT.sql_context(asked_questions, plan)


def opening_metadata(
    plan: Mapping[str, Any] | None = None,
    plan_payload: Mapping[str, Any] | None = None,
) -> dict[str, Any]:
    return _CONTEXT.metadata(plan, plan_payload)
