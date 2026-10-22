"""LLM strategy for the think-mode opening overview insight."""
from __future__ import annotations

import json
import logging
from typing import Any, Mapping, Optional, Sequence

from helicalbi.interactive.think.synthesis.context import OpeningContextBuilder

logger = logging.getLogger(__name__)


class OpeningInsightSynthesizer:
    """Strategy: fast-mode SqlSuccess insight adapted for think plan overview."""

    def __init__(self, *, context: Optional[OpeningContextBuilder] = None) -> None:
        self._context = context or OpeningContextBuilder()

    def synthesize(
        self,
        question: str,
        asked_questions: Sequence[str],
        *,
        plan: Optional[Mapping[str, Any]] = None,
        plan_payload: Optional[Mapping[str, Any]] = None,
        state: Optional[dict[str, Any]] = None,
        _invoke_llm=None,
    ) -> str:
        from helicalbi.prompt.SqlSuccessPrompty import think_success_prompt_formatted

        formatted = think_success_prompt_formatted.format(
            user_query=question,
            sql_query=self._context.sql_context(asked_questions, plan),
            metadata=json.dumps(
                self._context.metadata(plan, plan_payload), default=str
            ),
        )
        invoke = _invoke_llm
        try:
            if invoke is None:
                from helicalbi.common.configuration import llm
                from helicalbi.common.LlmInvokeHelper import invoke_llm

                insight, _ = invoke_llm(llm, formatted, state=state)
            else:
                insight, _ = invoke(formatted, state=state)
            return str(getattr(insight, "content", insight) or "").strip()
        except Exception:
            logger.exception("Think opening insight failed; continuing without overview")
            return ""


_OPENING = OpeningInsightSynthesizer()


def synthesize_think_opening_insight(
    question: str,
    asked_questions: Sequence[str],
    *,
    plan: Optional[Mapping[str, Any]] = None,
    plan_payload: Optional[Mapping[str, Any]] = None,
    state: Optional[dict[str, Any]] = None,
    _invoke_llm=None,
) -> str:
    return _OPENING.synthesize(
        question,
        asked_questions,
        plan=plan,
        plan_payload=plan_payload,
        state=state,
        _invoke_llm=_invoke_llm,
    )
