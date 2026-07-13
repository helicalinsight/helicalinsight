from pydantic import BaseModel, Field, create_model

from helicalbi.common import app_config


def _reason_field(description: str):
    if app_config.optional_prompt_reason:
        return (str, Field(default="", description=description))
    return (str, Field(description=description))


def get_column_response_model():
  """Pydantic model for column-detection LLM output."""
  return create_model(
      "ColumnResponse",
      columnName=(
          list[str],
          Field(
              description=(
                  "Provide comma separated list of all columns along with their "
                  "table name table1.column1,"
              )
          ),
      ),
      reason=_reason_field("Your reason for this list"),
      pickedDimensions=(
          list[str],
          Field(
              default_factory=list,
              description=(
                  "Cube dimension names selected for the query. "
                  "Use semantic dimension names from the schema when available."
              ),
          ),
      ),
      pickedMetrics=(
          list[str],
          Field(
              default_factory=list,
              description=(
                  "Cube measure names selected for the query. "
                  "Use semantic measure names from the schema when available."
              ),
          ),
      ),
      __base__=BaseModel,
      __doc__="Extracted information from the prompt.",
  )


ColumnResponse = get_column_response_model()
