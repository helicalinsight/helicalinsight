from pydantic import BaseModel, Field, create_model

from helicalbi.model.output.reason_field import reason_field_kwargs

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
    """Extracted information from the prompt."""
    js_func_string: str = Field(description="The final js function in antd as per the above skeleton")


VisualizationResponse = get_visualization_response_model()
AntVisualizationResponse = get_ant_visualization_response_model()
