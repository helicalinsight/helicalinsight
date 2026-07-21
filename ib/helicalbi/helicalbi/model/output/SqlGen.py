from pydantic import BaseModel, Field, create_model

from helicalbi.model.output.reason_field import reason_field_kwargs


def get_sql_gen_model():
  """Pydantic model for final SQL generation LLM output."""
  return create_model(
      "SqlGen",
      sql=(str, Field(description="Provide the sql using the above information")),
      **reason_field_kwargs("Your reason for sql"),
      __base__=BaseModel,
      __doc__="Extracted information from the prompt.",
  )


SqlGen = get_sql_gen_model()
