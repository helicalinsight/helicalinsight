from pydantic import BaseModel, Field, create_model

from helicalbi.model.output.reason_field import reason_field_kwargs


def get_domain_topic_reason_model():
  """Pydantic model for domain/topic selection LLM output."""
  return create_model(
      "DomainTopicReason",
      domain=(list[str], Field(description="Provide the list of domains which are relevant")),
      topics=(list[str], Field(description="Provide the list of topics which are relevant")),
      **reason_field_kwargs("Your reason to choose this doman and topics"),
      __base__=BaseModel,
  )


DomainTopicReason = get_domain_topic_reason_model()
