"""Plan transforms and asked-question resolution for think mode."""
from __future__ import annotations

from typing import Any, Mapping, Sequence

from helicalbi.interactive.think.constants import INCLUDE_IN_DASHBOARD_DEFAULT


class TextOnlyPlanTransformer:
    """Mark every chart ``include_in_dashboard=False`` for text-only execute."""

    def __init__(self, include_in_dashboard: bool = INCLUDE_IN_DASHBOARD_DEFAULT) -> None:
        self.include_in_dashboard = include_in_dashboard

    def transform(self, plan: Mapping[str, Any] | None) -> dict[str, Any]:
        raw = dict(plan or {})
        charts: list[dict[str, Any]] = []
        for chart in raw.get("charts") or []:
            if not isinstance(chart, dict):
                continue
            item = dict(chart)
            item["include_in_dashboard"] = self.include_in_dashboard
            charts.append(item)
        raw["charts"] = charts
        return raw


class AskedQuestionsResolver:
    """Resolve supporting questions from result, steps, or plan charts."""

    def from_plan(self, plan: Mapping[str, Any] | None) -> list[str]:
        questions: list[str] = []
        for chart in (plan or {}).get("charts") or []:
            if not isinstance(chart, dict):
                continue
            question = str(chart.get("question") or "").strip()
            if question:
                questions.append(question)
        return questions

    def from_result(
        self,
        result: Mapping[str, Any],
        plan: Mapping[str, Any],
    ) -> list[str]:
        asked = [
            str(q).strip()
            for q in (result.get("asked_questions") or [])
            if str(q or "").strip()
        ]
        if asked:
            return asked
        from_steps = [
            str(step.get("sub_question") or "").strip()
            for step in (result.get("collected_data") or [])
            if str(step.get("sub_question") or "").strip()
        ]
        if from_steps:
            return from_steps
        return self.from_plan(plan)

    def numbered_block(self, asked_questions: Sequence[str]) -> str:
        from helicalbi.interactive.think.constants import NONE_PLACEHOLDER

        lines = [
            f"Question {index}: {question}"
            for index, question in enumerate(asked_questions, start=1)
        ]
        return "\n".join(lines) if lines else NONE_PLACEHOLDER


_TEXT_ONLY = TextOnlyPlanTransformer()
_ASKED = AskedQuestionsResolver()


def text_only_plan(plan: Mapping[str, Any] | None) -> dict[str, Any]:
    return _TEXT_ONLY.transform(plan)


def asked_questions_from_plan(plan: Mapping[str, Any] | None) -> list[str]:
    return _ASKED.from_plan(plan)


def asked_questions_from_result(
    result: Mapping[str, Any],
    plan: Mapping[str, Any],
) -> list[str]:
    return _ASKED.from_result(result, plan)


def numbered_questions_block(asked_questions: Sequence[str]) -> str:
    return _ASKED.numbered_block(asked_questions)
