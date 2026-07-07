from pydantic import BaseModel, Field


class ColumnResponse(BaseModel):
    """Extracted information from the prompt."""
    columnName: list[str] = Field(description="Provide comma separated list of all columns along with their table name table1.column1,")
    reason: str = Field(description="Your reason for this list")
