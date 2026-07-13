from pydantic import BaseModel, Field, create_model

from helicalbi.common import app_config


def _reason_field(description: str):
    if app_config.optional_prompt_reason:
        return (str, Field(default="", description=description))
    return (str, Field(description=description))


def get_sql_gen_model():
  """Pydantic model for final SQL generation LLM output."""
  return create_model(
      "SqlGen",
      sql=(str, Field(description="Provide the sql using the above information")),
      reason=_reason_field("Your reason for sql"),
      __base__=BaseModel,
      __doc__="Extracted information from the prompt.",
  )


SqlGen = get_sql_gen_model()
