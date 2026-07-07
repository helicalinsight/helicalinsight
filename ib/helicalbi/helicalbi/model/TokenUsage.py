from __future__ import annotations

from typing import Optional

from pydantic import BaseModel, Field, model_validator


class TokenUsage(BaseModel):
    """Token counts and optional cost reported by an LLM provider for a single completion."""

    input_tokens: int = Field(default=0, ge=0, description="Tokens in the prompt / input.")
    output_tokens: int = Field(default=0, ge=0, description="Tokens in the model response.")
    total_tokens: int = Field(default=0, ge=0, description="Sum of input and output tokens.")
    input_cost: Optional[float] = Field(
        default=None, ge=0, description="Cost of input tokens in USD, when reported by the provider."
    )
    output_cost: Optional[float] = Field(
        default=None, ge=0, description="Cost of output tokens in USD, when reported by the provider."
    )
    total_cost: Optional[float] = Field(
        default=None, ge=0, description="Total request cost in USD, when reported by the provider."
    )

    @staticmethod
    def _add_optional_cost(left: Optional[float], right: Optional[float]) -> Optional[float]:
        if left is None and right is None:
            return None
        return (left or 0.0) + (right or 0.0)

    @model_validator(mode="after")
    def _derive_totals(self) -> "TokenUsage":
        if self.total_tokens == 0 and (self.input_tokens or self.output_tokens):
            self.total_tokens = self.input_tokens + self.output_tokens
        if self.total_cost is None and (self.input_cost is not None or self.output_cost is not None):
            self.total_cost = (self.input_cost or 0.0) + (self.output_cost or 0.0)
        return self

    def __add__(self, other: "TokenUsage") -> "TokenUsage":
        return TokenUsage(
            input_tokens=self.input_tokens + other.input_tokens,
            output_tokens=self.output_tokens + other.output_tokens,
            total_tokens=self.total_tokens + other.total_tokens,
            input_cost=self._add_optional_cost(self.input_cost, other.input_cost),
            output_cost=self._add_optional_cost(self.output_cost, other.output_cost),
            total_cost=self._add_optional_cost(self.total_cost, other.total_cost),
        )
