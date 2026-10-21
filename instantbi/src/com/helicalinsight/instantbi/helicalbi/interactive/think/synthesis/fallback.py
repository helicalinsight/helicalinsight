"""Deterministic fallback when LLM citation synthesis fails."""
from __future__ import annotations

from typing import Sequence

from helicalbi.interactive.think.constants import (
    FALLBACK_CITATION_LIMIT,
    FALLBACK_FINDINGS_LIMIT,
)
from helicalbi.interactive.think.models import ThinkCitedAnswer


class CitedAnswerFallback:
    """Build a short cited answer without calling the LLM."""

    def build(
        self,
        question: str,
        asked_questions: Sequence[str],
        *,
        findings: Sequence[str] | None = None,
    ) -> ThinkCitedAnswer:
        if not asked_questions:
            return ThinkCitedAnswer(
                final_answer=(
                    f"To answer “{question}”, I need supporting investigation questions "
                    "from the plan."
                ),
                cited_question_indexes=[],
            )
        noted = [str(item).strip() for item in (findings or []) if str(item or "").strip()]
        if noted:
            cited = list(range(1, len(asked_questions) + 1))
            body = " ".join(noted[:FALLBACK_FINDINGS_LIMIT])
            return ThinkCitedAnswer(final_answer=body, cited_question_indexes=cited)
        cited = list(range(1, min(FALLBACK_CITATION_LIMIT, len(asked_questions)) + 1))
        cites = ", ".join(f"Question {i}" for i in cited)
        body = (
            f"Investigation for “{question}” covered {cites}. "
            "Supporting findings were limited; refine the question or try Fast mode "
            "for a single focused chart."
        )
        return ThinkCitedAnswer(final_answer=body, cited_question_indexes=cited)
