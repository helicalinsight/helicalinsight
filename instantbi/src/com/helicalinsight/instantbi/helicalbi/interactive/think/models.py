"""Think-mode domain models."""
from __future__ import annotations

from pydantic import BaseModel, Field


class ThinkCitedAnswer(BaseModel):
    final_answer: str = Field(
        description=(
            "Strategic markdown conclusion with inline [n] citations to supporting "
            "questions; lead with the decision/takeaway, bold key figures, optional "
            "short bullets, and one next action."
        )
    )
    cited_question_indexes: list[int] = Field(
        default_factory=list,
        description=(
            "1-based indexes of supporting questions cited in final_answer "
            "(must match the [n] markers used in the prose)"
        ),
    )
