from __future__ import annotations

from typing import Any

from pydantic import BaseModel, Field, create_model, model_validator

from helicalbi.model.output.reason_field import reason_field_kwargs


class _TableResponseCoerce(BaseModel):
    """Shared hooks so LLM quirks (bare list) still parse."""

    @model_validator(mode="before")
    @classmethod
    def _coerce_bare_list(cls, value: Any) -> Any:
        # LLMs sometimes return ["t1","t2"] instead of {"required_tables":[...]}
        if isinstance(value, list):
            return {"required_tables": value}
        return value


def get_table_response_model():
    """Pydantic model for table-selection LLM output."""
    return create_model(
        "TableResponse",
        required_tables=(
            list[str],
            Field(
                description=(
                    "List of database table names required to answer the user query. "
                    "Must be returned as the 'required_tables' field of a JSON object."
                )
            ),
        ),
        **reason_field_kwargs("Your reason for selecting these tables"),
        __base__=_TableResponseCoerce,
    )


TableResponse = get_table_response_model()
