from pydantic import BaseModel, Field, create_model

from helicalbi.model.output.reason_field import reason_field_kwargs
from helicalbi.model.output.viz.ChartSettings import (  # noqa: F401
    ChartSettings,
    DimensionSetting,
)

_VIZ_REASON_DESCRIPTION = (
    "Explanation of why this visualization type and title were chosen "
    "based on the data and user query."
)
_ANT_VIZ_REASON_DESCRIPTION = _VIZ_REASON_DESCRIPTION


def get_visualization_response_model():
  """Pydantic model for visualization selection LLM output."""
  return create_model(
      "VisualizationResponse",
      visualization_type=(
          str,
          Field(
              description=(
                  "Type of visualization selected. Examples: bar, line, pie, donut, "
                  "table, kpi, area and other standard type"
              )
          ),
      ),
      visualization_title=(
          str,
          Field(description="Human-readable and business-friendly title for the visualization."),
      ),
      **reason_field_kwargs(_VIZ_REASON_DESCRIPTION),
      __base__=BaseModel,
      __doc__="Extracted information from the prompt.",
  )


def get_ant_visualization_response_model():
  """Pydantic model for Ant Design visualization selection LLM output."""
  return create_model(
      "AntVisualizationResponse",
      visualization_type=(
          str,
          Field(description="Chose one from the exhaustive list of @ant-design/plots charts"),
      ),
      visualization_title=(
          str,
          Field(description="Human-readable and business-friendly title for the visualization."),
      ),
      **reason_field_kwargs(_ANT_VIZ_REASON_DESCRIPTION),
      __base__=BaseModel,
      __doc__="Extracted information from the prompt.",
  )


class ChartFillerResponse(BaseModel):
    """LLM chart settings fill (encodings only — formats are a separate step)."""

    settings: ChartSettings = Field(
        description=(
            "Filled chart settings matching the chart settings template. "
            "Use dimensions.name (or dimensions.names), measures, labelsX/labelsY/title, "
            "and color — do not return JavaScript or measure_formats."
        )
    )


class ChartFormatResponse(BaseModel):
    """LLM chart format fill: measure_formats only."""

    measure_formats: dict[str, str] = Field(
        default_factory=dict,
        description=(
            "Excel-style format strings keyed by result column / metric names "
            "used in the chart (e.g. total_travel_cost). Omit unused cube aliases."
        ),
    )


class OtherChartFillerResponse(BaseModel):
    """LLM output for the catch-all ``other`` chart: full DrawOther JS/JSX."""

    code: str = Field(
        description=(
            "Complete DrawOther() JavaScript/JSX function adapted to the user question "
            "and result metadata. No markdown fences, no ChartSettings JSON."
        )
    )


VisualizationResponse = get_visualization_response_model()
AntVisualizationResponse = get_ant_visualization_response_model()
