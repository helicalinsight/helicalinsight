"""Chart encoding / display settings returned by the LLM (not full JS).

Each chart JSON declares a ``settings`` *template* (prompts / shapes). The LLM
fills the same shape; the filled object is injected into the chart skeleton at
``${setting}`` and referenced as ``setting.dimensions.name``, ``setting.measures``,
``setting.labelsX``, etc.
"""
from __future__ import annotations

from typing import Optional, Union

from pydantic import BaseModel, Field, field_validator, model_validator


class DimensionSetting(BaseModel):
    """Dimension binding(s).

    Single-dim charts use ``name``. Multi-dim charts (e.g. heatmap) use ``names``.
    """

    name: Optional[str] = Field(
        default=None,
        description="Primary dimension column name from result metadata.",
    )
    names: Optional[list[str]] = Field(
        default=None,
        description=(
            "Ordered dimension column names when the chart needs more than one. "
            "Must be a JSON array of strings (not a comma-separated string)."
        ),
    )

    @field_validator("names", mode="before")
    @classmethod
    def _coerce_names(cls, value: object) -> object:
        """Accept LLM quirks: comma-separated string or a single name string."""
        if value is None:
            return None
        if isinstance(value, str):
            text = value.strip()
            if not text:
                return None
            # "a,b" → ["a", "b"]; bare "a" → ["a"]
            parts = [part.strip() for part in text.split(",") if part.strip()]
            return parts or None
        if isinstance(value, list):
            return value
        return value

    @model_validator(mode="after")
    def _require_name_or_names(self) -> "DimensionSetting":
        if not (self.name or "").strip() and not (self.names or []):
            # Allow empty during partial construction; callers normalize.
            return self
        if self.names:
            cleaned = [str(n).strip() for n in self.names if str(n).strip()]
            self.names = cleaned or None
            if self.names and not (self.name or "").strip():
                self.name = self.names[0]
        if (self.name or "").strip() and not self.names:
            self.name = self.name.strip()
        return self


class ChartSettings(BaseModel):
    """LLM-filled chart configuration injected at ``${setting}`` in chart code."""

    dimensions: DimensionSetting = Field(
        default_factory=DimensionSetting,
        description="Dimension column binding(s). Use dimensions.name (or names for multi-dim).",
    )
    measures: list[str] = Field(
        default_factory=list,
        description="Ordered measure column names from result metadata (numeric).",
    )
    labelsX: Optional[str] = Field(
        default=None,
        description="X-axis / primary categorical axis label.",
    )
    labelsY: Optional[str] = Field(
        default=None,
        description="Y-axis / primary measure axis label.",
    )
    labelsZ: Optional[str] = Field(
        default=None,
        description="Secondary / size / Z-axis label when applicable.",
    )
    title: Optional[str] = Field(
        default=None,
        description="Chart title text shown to the user.",
    )
    series: Optional[str] = Field(
        default=None,
        description="Optional series / group / stack field name when multi-series.",
    )
    color: Optional[Union[str, list[str]]] = Field(
        default=None,
        description=(
            "Solid hex color (e.g. '#5B8FF9') or palette array of hex colors."
        ),
    )
    measure_formats: dict[str, str] = Field(
        default_factory=dict,
        description=(
            "Excel-style format strings keyed by measure / column name "
            "(only for fields that have a provided format)."
        ),
    )

    @field_validator("measures", mode="before")
    @classmethod
    def _split_measures(cls, value: object) -> object:
        """Allow a comma-separated string from the LLM as well as a list."""
        if isinstance(value, str):
            return [part.strip() for part in value.split(",") if part.strip()]
        return value

    @field_validator("dimensions", mode="before")
    @classmethod
    def _coerce_dimensions(cls, value: object) -> object:
        if value is None:
            return {}
        if isinstance(value, str):
            text = value.strip()
            return {"name": text} if text else {}
        if isinstance(value, list):
            names = [str(item).strip() for item in value if str(item).strip()]
            if not names:
                return {}
            if len(names) == 1:
                return {"name": names[0]}
            return {"name": names[0], "names": names}
        return value

    def dimension_names(self) -> list[str]:
        if self.dimensions.names:
            return list(self.dimensions.names)
        if self.dimensions.name:
            return [self.dimensions.name]
        return []

    def to_js_object(self) -> dict:
        """Plain dict suitable for JSON/JS injection (stable key order)."""
        dims: dict = {}
        if self.dimensions.name:
            dims["name"] = self.dimensions.name
        if self.dimensions.names:
            dims["names"] = list(self.dimensions.names)
        payload: dict = {
            "dimensions": dims,
            "measures": list(self.measures or []),
        }
        if self.labelsX is not None:
            payload["labelsX"] = self.labelsX
        if self.labelsY is not None:
            payload["labelsY"] = self.labelsY
        if self.labelsZ is not None:
            payload["labelsZ"] = self.labelsZ
        if self.title is not None:
            payload["title"] = self.title
        if self.series is not None:
            payload["series"] = self.series
        if self.color is not None:
            payload["color"] = self.color
        if self.measure_formats:
            payload["measure_formats"] = dict(self.measure_formats)
        return payload
