"""Dashboard chrome / theme contract for convert-dashboard parent payload.

Field names mirror the HI dashboard designer / InstantBI client:

- ``background`` → dashboard page background (gridSettings background)
- ``dashboardComponent`` → card / tile body fill (gridItemConfig background)
- ``componentHeader`` → card header bar (gridItemConfig header.backgroundColor)
- ``textPrimary`` → primary text / header text (header.customColor)
- ``borderColor`` → card border (border.color)
- ``horizontalMargin`` / ``verticalMargin`` → spacing between cards (grid margins)
"""
from __future__ import annotations

import json
from functools import lru_cache
from pathlib import Path
from typing import Any, Optional

from pydantic import BaseModel, Field, model_validator

_THEME_CATALOG_PATH = (
    Path(__file__).resolve().parents[3]
    / "sql_agent"
    / "strategies"
    / "theme.json"
)


class DashboardTheme(BaseModel):
    """One named theme: surfaces, text, border, and card spacing."""

    name: str = Field(default="Default Blue", description="Human-readable theme label.")
    id: str = Field(default="default", description="Theme key from theme.json.")
    background: str = Field(
        default="#eef2f7",
        description="Dashboard page / canvas background.",
    )
    dashboardComponent: str = Field(
        default="#ffffff",
        description="Card / tile body background color.",
    )
    componentHeader: str = Field(
        default="#1f4e79",
        description="Card / tile header bar background color.",
    )
    textPrimary: str = Field(
        default="#1b2b3a",
        description="Primary text color (maps to designer header.customColor).",
    )
    borderColor: str = Field(
        default="#dbe3ec",
        description="Card border color (maps to designer border.color).",
    )
    horizontalMargin: int = Field(
        default=10,
        ge=0,
        description="Horizontal spacing between cards (grid.horizontalMargin).",
    )
    verticalMargin: int = Field(
        default=10,
        ge=0,
        description="Vertical spacing between cards (grid.verticalMargin).",
    )


class DashboardParameters(BaseModel):
    """Global filter / parameter drawer settings (HI designerSettings.parameters)."""

    enable: bool = Field(default=True, description="Show the parameters / filter drawer.")
    orientation: str = Field(
        default="right",
        description='Drawer side: "right", "left", or "top".',
    )
    enableApplyButton: bool = Field(default=True)
    floatingFilter: bool = Field(default=True)
    closeOnApply: bool = Field(default=False)


class DashboardConfig(BaseModel):
    """Parent-level dashboard chrome. Attach as ``dashboard_config`` on layout output."""

    theme: DashboardTheme = Field(
        default_factory=DashboardTheme,
        description="Selected theme colors and card spacing.",
    )
    parameters: DashboardParameters = Field(
        default_factory=DashboardParameters,
        description="Global filter drawer settings.",
    )

    @model_validator(mode="before")
    @classmethod
    def _coerce_theme_name(cls, data: Any) -> Any:
        """Allow ``{\"theme\": \"antd\"}`` or ``{\"theme\": {\"id\": \"carbon\"}}``."""
        if not isinstance(data, dict):
            return data
        raw = data.get("theme")
        if isinstance(raw, str):
            data = {**data, "theme": resolve_theme(raw).model_dump()}
        elif isinstance(raw, dict) and (
            "background" not in raw
            and "dashboardComponent" not in raw
            and ("id" in raw or "name" in raw)
        ):
            theme_id = str(raw.get("id") or raw.get("name") or "").strip()
            if theme_id:
                base = resolve_theme(theme_id).model_dump()
                data = {
                    **data,
                    "theme": {**base, **raw, "id": base["id"], "name": base["name"]},
                }
        return data


@lru_cache(maxsize=4)
def load_theme_catalog(path: str = "") -> dict[str, Any]:
    catalog_path = Path(path) if path else _THEME_CATALOG_PATH
    with catalog_path.open(encoding="utf-8") as handle:
        data = json.load(handle)
    if not isinstance(data, dict) or not isinstance(data.get("themes"), dict):
        raise ValueError(f"Invalid theme catalog: {catalog_path}")
    return data


def clear_theme_catalog_cache() -> None:
    load_theme_catalog.cache_clear()


def resolve_theme(theme_id: Optional[str] = None, *, path: str = "") -> DashboardTheme:
    """Load a theme by id from theme.json (falls back to default_theme)."""
    catalog = load_theme_catalog(path)
    themes = catalog.get("themes") or {}
    default_id = str(catalog.get("default_theme") or "default")
    key = str(theme_id or default_id).strip() or default_id
    raw = themes.get(key) or themes.get(default_id) or {}
    if not isinstance(raw, dict):
        raw = {}
    return DashboardTheme.model_validate(raw)


def default_dashboard_config(*, theme_id: Optional[str] = None) -> DashboardConfig:
    return DashboardConfig(theme=resolve_theme(theme_id))
