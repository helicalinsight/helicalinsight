"""LLM strategy for the think-mode conclusive cited answer."""
from __future__ import annotations

import logging
import re
from typing import Any, Mapping, Optional, Sequence

from helicalbi.interactive.think.constants import (
    CITATION_MARKER_PATTERN,
    FALLBACK_CITATION_LIMIT,
    NONE_PLACEHOLDER,
)
from helicalbi.interactive.think.history import FindingsBlockFormatter
from helicalbi.interactive.think.models import ThinkCitedAnswer
from helicalbi.interactive.think.plan import AskedQuestionsResolver
from helicalbi.interactive.think.synthesis.fallback import CitedAnswerFallback
from helicalbi.sql_agent.prompt import THINK_FINAL_ANSWER_PROMPT

logger = logging.getLogger(__name__)


class CitationIndexNormalizer:
    """Keep only valid 1-based indexes within the asked-question range."""

    def normalize(
        self,
        raw_indexes: Sequence[Any],
        asked_count: int,
    ) -> list[int]:
        indexes: list[int] = []
        for raw in raw_indexes or []:
            try:
                idx = int(raw)
            except (TypeError, ValueError):
                continue
            if 1 <= idx <= asked_count and idx not in indexes:
                indexes.append(idx)
        return indexes

    def from_prose(self, answer: str, asked_count: int) -> list[int]:
        indexes: list[int] = []
        for match in re.finditer(CITATION_MARKER_PATTERN, answer):
            try:
                idx = int(match.group(1))
            except (TypeError, ValueError):
                continue
            if 1 <= idx <= asked_count and idx not in indexes:
                indexes.append(idx)
        return indexes

    def from_history(
        self,
        history: Sequence[Mapping[str, Any]],
        asked_count: int,
    ) -> list[int]:
        indexes = [
            int(item.get("index") or 0)
            for item in history
            if str(item.get("analysis") or "").strip() and int(item.get("index") or 0) > 0
        ]
        return indexes or list(range(1, min(FALLBACK_CITATION_LIMIT, asked_count) + 1))


class FinalAnswerSynthesizer:
    """Strategy: LLM cited conclusion with deterministic fallback."""

    def __init__(
        self,
        *,
        fallback: Optional[CitedAnswerFallback] = None,
        findings: Optional[FindingsBlockFormatter] = None,
        questions: Optional[AskedQuestionsResolver] = None,
        normalizer: Optional[CitationIndexNormalizer] = None,
    ) -> None:
        self._fallback = fallback or CitedAnswerFallback()
        self._findings = findings or FindingsBlockFormatter()
        self._questions = questions or AskedQuestionsResolver()
        self._normalizer = normalizer or CitationIndexNormalizer()

    def synthesize(
        self,
        question: str,
        asked_questions: Sequence[str],
        *,
        plan: Optional[Mapping[str, Any]] = None,
        question_history: Optional[Sequence[Mapping[str, Any]]] = None,
        state: Optional[dict[str, Any]] = None,
        _invoke=None,
    ) -> ThinkCitedAnswer:
        asked = [str(q).strip() for q in asked_questions if str(q or "").strip()]
        history = list(question_history or [])
        findings = [
            str(item.get("analysis") or "").strip()
            for item in history
            if str(item.get("analysis") or "").strip()
        ]
        if not asked:
            return self._fallback.build(question, asked, findings=findings)

        invoke = _invoke
        if invoke is None:
            from helicalbi.sql_agent.llm import invoke_agent_model

            invoke = invoke_agent_model

        try:
            parsed = invoke(
                THINK_FINAL_ANSWER_PROMPT,
                {
                    "original_question": question,
                    "numbered_questions": self._questions.numbered_block(asked),
                    "findings": self._findings.format(history),
                    "rationale": str((plan or {}).get("rationale") or "").strip()
                    or NONE_PLACEHOLDER,
                },
                ThinkCitedAnswer,
                state=state,
            )
            indexes = self._normalizer.normalize(
                parsed.cited_question_indexes or [], len(asked)
            )
            answer = str(parsed.final_answer or "").strip()
            if not answer:
                return self._fallback.build(question, asked, findings=findings)
            if not indexes:
                indexes = self._normalizer.from_prose(answer, len(asked))
            if not indexes:
                indexes = self._normalizer.from_history(history, len(asked))
            return ThinkCitedAnswer(
                final_answer=answer, cited_question_indexes=indexes
            )
        except Exception:
            logger.exception("Think final-answer synthesis failed; using fallback")
            return self._fallback.build(question, asked, findings=findings)


_SYNTHESIZER = FinalAnswerSynthesizer()


def synthesize_think_final_answer(
    question: str,
    asked_questions: Sequence[str],
    *,
    plan: Optional[Mapping[str, Any]] = None,
    question_history: Optional[Sequence[Mapping[str, Any]]] = None,
    state: Optional[dict[str, Any]] = None,
    _invoke=None,
) -> ThinkCitedAnswer:
    return _SYNTHESIZER.synthesize(
        question,
        asked_questions,
        plan=plan,
        question_history=question_history,
        state=state,
        _invoke=_invoke,
    )
