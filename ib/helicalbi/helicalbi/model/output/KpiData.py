from pydantic import BaseModel, Field, create_model

from helicalbi.model.output.reason_field import reason_field_kwargs


def get_kpi_schema_model():
  """Pydantic model for KPI suggestion LLM output."""
  return create_model(
      "KpiSchema",
      answer=(
          list[str],
          Field(description="Provide Top 10  KPIs questions without explanation"),
      ),
      **reason_field_kwargs("Your reason for this list"),
      __base__=BaseModel,
      __doc__="Extracted information from the prompt.",
  )


KpiSchema = get_kpi_schema_model()
