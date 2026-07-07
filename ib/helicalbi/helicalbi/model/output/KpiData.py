from pydantic import BaseModel, Field


class KpiSchema(BaseModel):
    """Extracted information from the prompt."""
    answer: list[str] = Field(description="Provide Top 10  KPIs questions without explanation" )
    reason: str = Field(description="Your reason for this list")
