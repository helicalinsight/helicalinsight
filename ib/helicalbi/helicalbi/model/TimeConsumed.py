from __future__ import annotations

from typing import Optional

from pydantic import BaseModel, Field


class TimeConsumed(BaseModel):
    """Elapsed time for LLM calls and the overall agent request."""

    llm_seconds: float = Field(
        default=0.0,
        ge=0,
        description="Accumulated wall time spent in LLM invoke calls.",
    )
    total_seconds: Optional[float] = Field(
        default=None,
        ge=0,
        description="End-to-end request time including orchestration and SQL execution.",
    )

    @staticmethod
    def _merge_total(left: Optional[float], right: Optional[float]) -> Optional[float]:
        if left is None:
            return right
        if right is None:
            return left
        return max(left, right)

    def __add__(self, other: "TimeConsumed") -> "TimeConsumed":
        return TimeConsumed(
            llm_seconds=self.llm_seconds + other.llm_seconds,
            total_seconds=self._merge_total(self.total_seconds, other.total_seconds),
        )
