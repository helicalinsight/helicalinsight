"""Lexical topic ranking for the plan-context graph."""
from __future__ import annotations

import re
from typing import Any, Dict

from helicalbi.sql_agent.constants import (
    TOKEN_PATTERN,
    TOPIC_COMPONENT_TOKEN_MIN_LENGTH,
    TOPIC_SCORE_COMPONENT,
    TOPIC_SCORE_COMPONENT_TOKEN,
    TOPIC_SCORE_EXACT_DOMAIN,
    TOPIC_SCORE_EXACT_TOPIC,
    TOPIC_SCORE_TOKEN_IN_BLOB,
    TOPIC_TOKEN_MIN_LENGTH,
)
from helicalbi.sql_agent.util.normalize import normalize_whitespace


class TopicScorer:
    """Score a semantic-model topic catalog entry against the user question."""

    def score(self, question: str, entry: Dict[str, Any]) -> float:
        q = normalize_whitespace(question)
        if not q:
            return 0.0
        tokens = set(re.findall(TOKEN_PATTERN, q))
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
            score += TOPIC_SCORE_EXACT_TOPIC
        if domain and domain in q:
            score += TOPIC_SCORE_EXACT_DOMAIN
        for token in tokens:
            if len(token) < TOPIC_TOKEN_MIN_LENGTH:
                continue
            if token in blob:
                score += TOPIC_SCORE_TOKEN_IN_BLOB
        for component in entry.get("components") or []:
            name = str(component or "").strip().lower()
            if name and name in q:
                score += TOPIC_SCORE_COMPONENT
            for part in re.findall(TOKEN_PATTERN, name):
                if len(part) >= TOPIC_COMPONENT_TOKEN_MIN_LENGTH and part in tokens:
                    score += TOPIC_SCORE_COMPONENT_TOKEN
        return score


_SCORER = TopicScorer()


def score_topic(question: str, entry: Dict[str, Any]) -> float:
    """Module-level facade used by ``plan_graph`` and tests."""
    return _SCORER.score(question, entry)
