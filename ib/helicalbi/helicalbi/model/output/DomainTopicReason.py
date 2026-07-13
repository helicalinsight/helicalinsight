from pydantic import BaseModel, Field, create_model

from helicalbi.common import app_config


def _reason_field(description: str):
    if app_config.optional_prompt_reason:
        return (str, Field(default="", description=description))
    return (str, Field(description=description))


def get_domain_topic_reason_model():
  """Pydantic model for domain/topic selection LLM output."""
  return create_model(
      "DomainTopicReason",
      domain=(list[str], Field(description="Provide the list of domains which are relevant")),
      topics=(list[str], Field(description="Provide the list of topics which are relevant")),
      reason=_reason_field("Your reason to choose this doman and topics"),
      __base__=BaseModel,
  )


DomainTopicReason = get_domain_topic_reason_model()
