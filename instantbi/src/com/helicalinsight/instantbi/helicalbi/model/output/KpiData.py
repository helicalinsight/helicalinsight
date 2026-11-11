from __future__ import annotations

from typing import Any

from pydantic import BaseModel, Field, create_model, model_validator

from helicalbi.model.output.reason_field import reason_field_kwargs


_SAMPLE_QUESTIONS = {
    "Question about one measure",
    "Question about that measure by one dimension",
}


def _question_list(value: Any) -> list[str]:
    if isinstance(value, str):
        value = [value]
    if not isinstance(value, list):
        return []
    questions = []
    for item in value:
        if isinstance(item, dict):
            item = item.get("question") or item.get("text") or item.get("answer") or ""
        text = str(item or "").strip()
        if text and text not in _SAMPLE_QUESTIONS:
            questions.append(text)
    return questions


class _KpiSchemaCoerce(BaseModel):
    """Shared hooks so LLM quirks (bare list, one string, empty example) still parse."""

    @model_validator(mode="before")
    @classmethod
    def _coerce_bare_list(cls, value: Any) -> Any:
        # LLMs sometimes return ["q1","q2"] instead of {"answer":[...]}
        if isinstance(value, list):
            value = {"answer": value}
        if isinstance(value, dict) and "answer" in value:
            value = dict(value)
            value["answer"] = _question_list(value.get("answer"))
        return value


def get_kpi_schema_model():
    """Pydantic model for KPI suggestion LLM output."""
    return create_model(
        "KpiSchema",
        answer=(
            list[str],
            Field(
                description=(
                    "Top 10 KPI questions without explanation. "
                    "Must be returned as the 'answer' field of a JSON object."
                ),
            ),
        ),
        **reason_field_kwargs("Your reason for this list"),
        __base__=_KpiSchemaCoerce,
        __doc__="Extracted information from the prompt.",
    )


KpiSchema = get_kpi_schema_model()
