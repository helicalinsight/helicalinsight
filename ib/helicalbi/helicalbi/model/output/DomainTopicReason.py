from pydantic import BaseModel, Field


class DomainTopicReason(BaseModel):
    domain:  list[str] = Field(description="Provide the list of domains which are relevant"),
    topics: list[str] = Field(description="Provide the list of topics which are relevant"),
    reason: str = Field(description="Your reason to choose this doman and topics")
