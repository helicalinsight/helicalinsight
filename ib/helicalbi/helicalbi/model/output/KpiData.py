from pydantic import BaseModel, Field, create_model

from helicalbi.common import app_config


def _reason_field(description: str):
    if app_config.optional_prompt_reason:
        return (str, Field(default="", description=description))
    return (str, Field(description=description))


def get_kpi_schema_model():
  """Pydantic model for KPI suggestion LLM output."""
  return create_model(
      "KpiSchema",
      answer=(
          list[str],
          Field(description="Provide Top 10  KPIs questions without explanation"),
      ),
      reason=_reason_field("Your reason for this list"),
      __base__=BaseModel,
      __doc__="Extracted information from the prompt.",
  )


KpiSchema = get_kpi_schema_model()
