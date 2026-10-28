"""Build question history and extract step analysis text."""
from __future__ import annotations

from typing import Any, Mapping, Optional, Sequence

from helicalbi.interactive.think.chat_shaping import InteractiveChatShaper
from helicalbi.interactive.think.constants import NONE_PLACEHOLDER


class StepAnalysisExtractor:
    """Prefer prose analysis; never surface raw JSON previews in the UI."""

    def extract(self, step: Mapping[str, Any] | None) -> str:
        if not isinstance(step, Mapping):
            return ""
        analysis = str(step.get("analysis") or "").strip()
        if analysis and not analysis.lstrip().startswith(("[", "{")):
            return analysis
        chat_response = (
            step.get("chat_response") if isinstance(step.get("chat_response"), dict) else {}
        )
        summary = (
            chat_response.get("summary")
            if isinstance(chat_response.get("summary"), dict)
            else {}
        )
        insight = str(summary.get("insight") or "").strip()
        if insight and not insight.lstrip().startswith(("[", "{")):
            return insight
        from helicalbi.sql_agent.tools.findings import describe_query_findings

        raw = (
            str(step.get("execution_result") or "").strip()
            or str(step.get("analysis") or "").strip()
        )
        described = describe_query_findings(
            raw,
            question=str(step.get("sub_question") or "").strip(),
            title=str(step.get("title") or "").strip(),
        )
        return described["analysis"]


class FindingsBlockFormatter:
    """Format question-history analyses for the final-answer prompt."""

    def format(self, history: Sequence[Mapping[str, Any]]) -> str:
        lines: list[str] = []
        for item in history:
            index = item.get("index") or 0
            analysis = str(item.get("analysis") or "").strip()
            title = str(item.get("title") or "").strip()
            question = str(item.get("question") or "").strip()
            if not analysis:
                continue
            label = title or question or f"Step {index}"
            lines.append(f"Question {index} — {label}: {analysis}")
        return "\n".join(lines) if lines else NONE_PLACEHOLDER


class QuestionHistoryBuilder:
    """Merge plan charts with collected execution steps into question_history."""

    def __init__(
        self,
        *,
        shaper: Optional[InteractiveChatShaper] = None,
        analysis: Optional[StepAnalysisExtractor] = None,
    ) -> None:
        self._shaper = shaper or InteractiveChatShaper()
        self._analysis = analysis or StepAnalysisExtractor()

    def build(
        self,
        asked_questions: Sequence[str],
        plan: Mapping[str, Any] | None,
        collected_data: Sequence[Mapping[str, Any]] | None = None,
    ) -> list[dict[str, Any]]:
        charts = [
            chart
            for chart in ((plan or {}).get("charts") or [])
            if isinstance(chart, dict)
        ]
        steps = [step for step in (collected_data or []) if isinstance(step, Mapping)]
        by_question: dict[str, Mapping[str, Any]] = {}
        for step in steps:
            key = str(step.get("sub_question") or "").strip().lower()
            if key and key not in by_question:
                by_question[key] = step

        history: list[dict[str, Any]] = []
        for index, question in enumerate(asked_questions, start=1):
            chart = charts[index - 1] if index - 1 < len(charts) else {}
            step = by_question.get(str(question).strip().lower())
            if step is None and index - 1 < len(steps):
                step = steps[index - 1]
            history.append(self._entry(index, question, chart, step, plan))
        return history

    def _entry(
        self,
        index: int,
        question: str,
        chart: Mapping[str, Any],
        step: Mapping[str, Any] | None,
        plan: Mapping[str, Any] | None,
    ) -> dict[str, Any]:
        chat_response = (step or {}).get("chat_response") if isinstance(step, Mapping) else None
        if not isinstance(chat_response, dict):
            chat_response = {}
        report_model = (step or {}).get("report_model") if isinstance(step, Mapping) else None
        if not isinstance(report_model, dict):
            report_model = (
                chat_response.get("report_model")
                if isinstance(chat_response.get("report_model"), dict)
                else {}
            )
        public_chat = self._shaper.chat_dict(chat_response) if chat_response else {}
        if report_model and not public_chat.get("report_model"):
            public_chat = dict(public_chat)
            public_chat["report_model"] = report_model

        components = [
            str(c).strip()
            for c in (chart.get("components") or chart.get("measure_hints") or [])
            if str(c).strip()
        ]
        dimensions = [
            str(d).strip() for d in (chart.get("dimensions") or []) if str(d).strip()
        ]
        public_chat = self._enrich_sql_section(
            public_chat, components, dimensions, plan
        )
        return {
            "index": index,
            "question": question,
            "title": str((step or {}).get("title") or chart.get("title") or "").strip(),
            "viz_hint": str(chart.get("viz_hint") or "").strip(),
            "purpose": str(chart.get("purpose") or "").strip(),
            "components": components,
            "measure_hints": components,
            "dimensions": dimensions,
            "analysis": self._analysis.extract(step),
            "chat_seq_id": str((step or {}).get("chat_seq_id") or "").strip(),
            "chat_response": public_chat,
        }

    def _enrich_sql_section(
        self,
        public_chat: dict[str, Any],
        components: list[str],
        dimensions: list[str],
        plan: Mapping[str, Any] | None,
    ) -> dict[str, Any]:
        # Fill empty Metrics fields from the plan chart so the UI is not blank
        # when ChatResponse.sql arrived with empty lists.
        sql_section = (
            public_chat.get("sql") if isinstance(public_chat.get("sql"), dict) else {}
        )
        sql_section = dict(sql_section)
        cube = sql_section.get("required_cube_info")
        if not isinstance(cube, dict):
            cube = {}
        else:
            cube = dict(cube)
        if components and not cube.get("picked_metrics"):
            cube["picked_metrics"] = components
        if dimensions and not cube.get("picked_dimensions"):
            cube["picked_dimensions"] = dimensions
        if cube:
            sql_section["required_cube_info"] = cube
        if components and not sql_section.get("required_column"):
            sql_section["required_column"] = components
        plan_domain = str((plan or {}).get("domain") or "").strip()
        plan_domains = (
            (plan or {}).get("domains")
            if isinstance((plan or {}).get("domains"), list)
            else []
        )
        plan_topics = (
            (plan or {}).get("topics")
            if isinstance((plan or {}).get("topics"), list)
            else []
        )
        if plan_domain and not sql_section.get("required_domain"):
            sql_section["required_domain"] = [plan_domain]
        elif plan_domains and not sql_section.get("required_domain"):
            sql_section["required_domain"] = [
                str(d).strip() for d in plan_domains if str(d).strip()
            ]
        if plan_topics and not sql_section.get("required_topic"):
            sql_section["required_topic"] = [
                str(t).strip() for t in plan_topics if str(t).strip()
            ]
        if sql_section:
            public_chat = dict(public_chat)
            public_chat["sql"] = sql_section
        return public_chat


_HISTORY = QuestionHistoryBuilder()
_FINDINGS = FindingsBlockFormatter()
_STEP_ANALYSIS = StepAnalysisExtractor()


def question_history(
    asked_questions: Sequence[str],
    plan: Mapping[str, Any] | None,
    collected_data: Sequence[Mapping[str, Any]] | None = None,
) -> list[dict[str, Any]]:
    return _HISTORY.build(asked_questions, plan, collected_data)


def findings_block(history: Sequence[Mapping[str, Any]]) -> str:
    return _FINDINGS.format(history)


def step_analysis(step: Mapping[str, Any] | None) -> str:
    return _STEP_ANALYSIS.extract(step)
