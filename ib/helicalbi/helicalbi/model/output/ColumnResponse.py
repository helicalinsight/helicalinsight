from typing import Optional



from pydantic import BaseModel, Field, create_model



from helicalbi.model.output.reason_field import reason_field_kwargs





def get_column_response_model():

  """Pydantic model for column-detection LLM output."""

  return create_model(

      "ColumnResponse",

      columnName=(

          list[str],

          Field(

              description=(

                  "Qualified physical column names as table.column "

                  "(no SQL quotes, no aliases)."

              )

          ),

      ),

      pickedDimensions=(

          list[str],

          Field(

              default_factory=list,

              description=(

                  "Cube dimension names from the schema Dimensions section only. "

                  "Use semantic dimension names (aliases), never quoted SQL forms."

              ),

          ),

      ),

      pickedMetrics=(

          list[str],

          Field(

              default_factory=list,

              description=(

                  "Cube measure names from the schema Measures section only. "

                  "Use semantic measure names (aliases), never quoted SQL forms."

              ),

          ),

      ),

      limit=(

          Optional[int],

          Field(

              default=None,

              description=(

                  "Row limit for the SQL query when the user asks for a specific "

                  "number of rows (e.g. top 10, first 5, limit 50). "

                  "Omit or leave null when no specific limit is requested."

              ),

          ),

      ),

      **reason_field_kwargs("Your reason for this list"),

      __base__=BaseModel,

      __doc__="Extracted information from the prompt.",

  )





ColumnResponse = get_column_response_model()

