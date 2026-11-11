from typing import Any, Literal

from pydantic import BaseModel, Field, model_validator


class UpdateRephrase(BaseModel):
    """
    Decide whether the user is updating the previous result, and provide
    rephrased queries for SQL and/or visualization.
    """

    action: Literal["updt_sql", "updt_viz", "updt_both", "none"] = Field(
        description="What should be updated w.r.t. the previous request."
    )
    viz_query: str = Field(description="User query rewritten for visualization generation.")
    sql_query: str = Field(description="User query rewritten for SQL generation/execution.")

    @model_validator(mode="before")
    @classmethod
    def blank_action_is_none(cls, value: Any) -> Any:
        if isinstance(value, dict):
            action = value.get("action")
            if not isinstance(action, str) or not action.strip():
                value = dict(value)
                value["action"] = "none"
        return value

