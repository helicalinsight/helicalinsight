from __future__ import annotations

from typing import Any

from pydantic import BaseModel, Field, create_model, model_validator

from helicalbi.model.output.reason_field import reason_field_kwargs


class _KpiSchemaCoerce(BaseModel):
    """Shared hooks so LLM quirks (bare list) still parse."""

    @model_validator(mode="before")
    @classmethod
    def _coerce_bare_list(cls, value: Any) -> Any:
        # LLMs sometimes return ["q1","q2"] instead of {"answer":[...]}
        if isinstance(value, list):
            return {"answer": value}
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
