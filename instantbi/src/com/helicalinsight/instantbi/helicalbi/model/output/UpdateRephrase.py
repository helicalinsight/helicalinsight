from typing import Literal

from pydantic import BaseModel, Field


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

