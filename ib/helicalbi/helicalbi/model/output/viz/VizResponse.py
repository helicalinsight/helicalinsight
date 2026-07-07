from pydantic import BaseModel, Field


class VisualizationResponse(BaseModel):
    """Extracted information from the prompt."""
    visualization_type: str = Field(
        description="Type of visualization selected. Examples: bar, line, pie, donut, table, kpi, area and other standard type"
    )
    visualization_title: str = Field(
        description="Human-readable and business-friendly title for the visualization."
    )
    reason: str = Field(
        description="Explanation of why this visualization type and title were chosen based on the data and user query."
    )

class AntVisualizationResponse(BaseModel):
    """Extracted information from the prompt."""
    visualization_type: str = Field(
        description="Chose one from the exhaustive list of @ant-design/plots charts"
    )
    visualization_title: str = Field(
        description="Human-readable and business-friendly title for the visualization."
    )
    reason: str = Field(
        description="Explanation of why this visualization type and title were chosen based on the data and user query."
    )

class ChartFillerResponse(BaseModel):
    """Extracted information from the prompt."""
    js_func_string:str = Field(description="The final js function in antd as per the above skeleton")


