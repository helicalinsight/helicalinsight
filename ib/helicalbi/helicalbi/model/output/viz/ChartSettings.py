"""Chart encoding / display settings returned by the LLM (not full JS).

Each chart JSON declares a ``settings`` *template* (prompts / shapes). The LLM
fills the same shape; the filled object is injected into the chart skeleton at
``${setting}`` and referenced as ``setting.dimensions.names``, ``setting.measures``,
``setting.labelsX``, etc.
"""
from __future__ import annotations

from typing import Any, Optional, Union

from pydantic import BaseModel, Field, field_validator, model_validator


class DimensionSetting(BaseModel):
    """Dimension binding(s) — always a list, same pattern as measures.

    Single-dim charts use one entry (``names[0]``). Multi-dim charts
    (e.g. heatmap) use multiple ordered entries.
    """

    names: list[str] = Field(
        default_factory=list,
        description=(
            "Ordered dimension column names from result metadata. "
            "Must be a JSON array of strings (not a comma-separated string)."
        ),
    )

    @model_validator(mode="before")
    @classmethod
    def _coerce_legacy_name(cls, value: object) -> object:
        """Accept legacy ``name`` / ``name``+``names`` payloads from older saves / LLMs."""
        if not isinstance(value, dict):
            return value
        data = dict(value)
        legacy = data.pop("name", None)
        names = data.get("names")
        if names is None and legacy is not None:
            text = str(legacy).strip()
            data["names"] = [text] if text else []
        elif isinstance(names, str) and not names.strip() and legacy is not None:
            text = str(legacy).strip()
            data["names"] = [text] if text else []
        return data

    @field_validator("names", mode="before")
    @classmethod
    def _coerce_names(cls, value: object) -> object:
        """Accept LLM quirks: comma-separated string or a single name string."""
        if value is None:
            return []
        if isinstance(value, str):
            text = value.strip()
            if not text:
                return []
            # "a,b" → ["a", "b"]; bare "a" → ["a"]
            return [part.strip() for part in text.split(",") if part.strip()]
        if isinstance(value, list):
            return value
        return value

    @model_validator(mode="after")
    def _clean_names(self) -> "DimensionSetting":
        cleaned = [str(n).strip() for n in (self.names or []) if str(n).strip()]
        self.names = cleaned
        return self


class ChartSettings(BaseModel):
    """LLM-filled chart configuration injected at ``${setting}`` in chart code."""

    dimensions: DimensionSetting = Field(
        default_factory=DimensionSetting,
        description="Dimension column binding(s). Always dimensions.names (JSON array).",
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

    @field_validator("color", mode="before")
    @classmethod
    def _coerce_color(cls, value: object) -> object:
        """Unwrap LLM echoes of the settings template schema for color."""
        if value is None:
            return None
        if isinstance(value, dict):
            # e.g. {"mode":"optional","value":["#5B8FF9"]} or {"value":"#5B8FF9"}
            if "value" in value:
                value = value.get("value")
            else:
                return None
        if isinstance(value, str):
            text = value.strip()
            return text or None
        if isinstance(value, list):
            colors = [str(item).strip() for item in value if str(item).strip()]
            return colors or None
        return value

    @field_validator("measure_formats", mode="before")
    @classmethod
    def _coerce_measure_formats(cls, value: object) -> object:
        """LLMs often emit null for unused measure_formats; treat as empty map."""
        if value is None:
            return {}
        return value

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
            return {"names": [text]} if text else {"names": []}
        if isinstance(value, list):
            names = [str(item).strip() for item in value if str(item).strip()]
            return {"names": names}
        return value

    def dimension_names(self) -> list[str]:
        return list(self.dimensions.names or [])

    def to_js_object(self) -> dict:
        """Plain dict suitable for JSON/JS injection (stable key order)."""
        payload: dict[str, Any] = {
            "dimensions": {"names": list(self.dimensions.names or [])},
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
