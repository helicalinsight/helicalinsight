"""Score and select analytical operations for a question."""
from __future__ import annotations

import re
from typing import Any, Mapping, Optional, Sequence

from helicalbi.sql_agent.constants import (
    BASELINE_OPERATIONS,
    CAP_KEY_APPLICABLE_TO,
    CAP_KEY_APPLICABLE_TO_SNAKE,
    CAP_KEY_CAPABILITY,
    CAP_KEY_USES,
    DEFAULT_MAX_OPERATIONS,
    DESCRIPTION_TOKEN_MIN_LENGTH,
    OPERATION_KEY,
    OPERATION_KEYWORDS,
    SCORE_BASELINE_BONUS,
    SCORE_DESCRIPTION_TOKEN,
    SCORE_DIAGNOSTIC_BOOST,
    SCORE_KEYWORD_HIT,
    SCORE_NAME_IN_QUESTION,
    TOKEN_PATTERN,
)
from helicalbi.sql_agent.strategy.catalog import load_analytical_operations
from helicalbi.sql_agent.strategy.diagnostics import (
    canonical_operation_name,
    iter_applicable_measures,
    iter_diagnostic_uses,
    measure_mentioned,
)


class AnalyticalOperationSelector:
    """Select the most relevant analytical operations from analytical_strategy.json."""

    def select(
        self,
        question: str,
        *,
        max_operations: int = DEFAULT_MAX_OPERATIONS,
        catalog: Optional[list[dict[str, Any]]] = None,
        diagnostic_capabilities: Optional[Sequence[Mapping[str, Any]]] = None,
    ) -> list[dict[str, Any]]:
        operations = catalog or load_analytical_operations()
        if not operations:
            return []

        text = f" {str(question or '').strip().lower()} "
        tokens = set(re.findall(TOKEN_PATTERN, text))
        boosted_ops = self._boosted_ops(text, operations, diagnostic_capabilities)

        scored: list[tuple[dict[str, Any], float]] = []
        for op in operations:
            name = str(op.get(OPERATION_KEY) or "").strip().lower()
            desc = str(op.get("description") or "").strip().lower()
            score = 0.0

            if name in boosted_ops:
                score += SCORE_DIAGNOSTIC_BOOST
            if name in text:
                score += SCORE_NAME_IN_QUESTION
            for kw in OPERATION_KEYWORDS.get(name, ()):
                if kw in text:
                    score += SCORE_KEYWORD_HIT
            for token in tokens:
                if len(token) >= DESCRIPTION_TOKEN_MIN_LENGTH and token in desc:
                    score += SCORE_DESCRIPTION_TOKEN
            if name in BASELINE_OPERATIONS:
                score += SCORE_BASELINE_BONUS

            scored.append((op, score))

        scored.sort(key=lambda x: x[1], reverse=True)
        limit = max(1, int(max_operations or DEFAULT_MAX_OPERATIONS))
        if boosted_ops:
            limit = max(limit, len(boosted_ops))
        return [op for op, _ in scored[:limit]]

    @staticmethod
    def _boosted_ops(
        text: str,
        operations: Sequence[Mapping[str, Any]],
        diagnostic_capabilities: Optional[Sequence[Mapping[str, Any]]],
    ) -> set[str]:
        boosted: set[str] = set()
        if not diagnostic_capabilities:
            return boosted
        for cap in diagnostic_capabilities:
            if not isinstance(cap, Mapping):
                continue
            applicable = iter_applicable_measures(
                cap.get(CAP_KEY_APPLICABLE_TO) or cap.get(CAP_KEY_APPLICABLE_TO_SNAKE)
            )
            cap_name = str(cap.get(CAP_KEY_CAPABILITY) or "").strip().lower()
            applicable_hit = any(measure_mentioned(app, text) for app in applicable)
            if (cap_name and cap_name in text) or applicable_hit or not applicable:
                for ref in iter_diagnostic_uses(cap.get(CAP_KEY_USES)):
                    canonical = canonical_operation_name(ref["name"], operations)
                    boosted.add(canonical or ref["name"].strip().lower())
        return boosted


class OperationMentionDetector:
    """True when question text already expresses an analytical operation."""

    def mentions(self, operation: str, text: str) -> bool:
        name = str(operation or "").strip().lower()
        blob = f" {str(text or '').lower()} "
        if not name:
            return False
        if name in blob or name.replace("_", " ") in blob:
            return True
        return any(keyword in blob for keyword in OPERATION_KEYWORDS.get(name, ()))


_SELECTOR = AnalyticalOperationSelector()
_MENTIONS = OperationMentionDetector()


def select_analytical_operations(
    question: str,
    *,
    max_operations: int = DEFAULT_MAX_OPERATIONS,
    catalog: Optional[list[dict[str, Any]]] = None,
    diagnostic_capabilities: Optional[Sequence[Mapping[str, Any]]] = None,
) -> list[dict[str, Any]]:
    """Select the most relevant analytical operations for the question."""
    return _SELECTOR.select(
        question,
        max_operations=max_operations,
        catalog=catalog,
        diagnostic_capabilities=diagnostic_capabilities,
    )


def question_mentions_operation(operation: str, text: str) -> bool:
    """True when question text already expresses an analytical operation."""
    return _MENTIONS.mentions(operation, text)
