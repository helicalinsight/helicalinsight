from pydantic import BaseModel, Field


class SqlGen(BaseModel):
    """Extracted information from the prompt."""
    sql: str = Field(description="Provide the sql using the above information")
    reason: str = Field(description="Your reason for sql")
