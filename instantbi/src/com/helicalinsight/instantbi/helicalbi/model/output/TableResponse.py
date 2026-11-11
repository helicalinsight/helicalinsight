from __future__ import annotations

from typing import Any

from pydantic import BaseModel, Field, create_model, model_validator

from helicalbi.model.output.reason_field import reason_field_kwargs


def _table_name(item: Any) -> str:
    if isinstance(item, dict):
        item = item.get("table") or item.get("table_name") or item.get("name") or ""
    return str(item or "").strip()


def _table_names(value: Any) -> list[str]:
    if isinstance(value, (str, dict)):
        value = [value]
    if not isinstance(value, list):
        return []
    names = []
    for item in value:
        name = _table_name(item)
        if name and name not in names:
            names.append(name)
    return names


class _TableResponseCoerce(BaseModel):
    """Shared hooks so LLM quirks (bare list, table objects) still parse."""

    @model_validator(mode="before")
    @classmethod
    def _coerce_bare_list(cls, value: Any) -> Any:
        # LLMs sometimes return ["t1","t2"] instead of {"required_tables":[...]}
        if isinstance(value, list):
            value = {"required_tables": value}
        if isinstance(value, dict) and "required_tables" in value:
            value = dict(value)
            value["required_tables"] = _table_names(value.get("required_tables"))
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
