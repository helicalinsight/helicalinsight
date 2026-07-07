"""Pydantic models for semantic metadata: domain -> topic -> cube."""

from __future__ import annotations

from typing import Optional

from pydantic import BaseModel, Field


class SemanticItem(BaseModel):
    """Shared attributes for dimensions and measures."""

    name: str = Field(description="Business-facing name of the semantic item.")
    table_name: str = Field(description="Physical table name where this item is sourced from.")
    column_name: str = Field(description="Physical column name in the source table.")
    synonyms: list[str] = Field(
        default_factory=list,
        description="Alternative names users might use in natural language.",
    )
    formula: Optional[str] = Field(
        default=None,
        description="Optional expression used to derive this item.",
    )
    description: Optional[str] = Field(
        default=None,
        description="Optional human-readable explanation.",
    )


class Dimension(SemanticItem):
    """Categorical attribute used for grouping/filtering."""

    data_type: Optional[str] = Field(
        default=None,
        description="Optional semantic type, for example date/string/region.",
    )


class Measure(SemanticItem):
    """Numeric attribute typically aggregated in analytics."""

    aggregation: Optional[str] = Field(
        default=None,
        description="Default aggregation such as sum, avg, count, etc.",
    )


class Cube(BaseModel):
    """A semantic cube containing dimensions and measures."""

    name: str = Field(description="Cube name.")
    table_name: Optional[str] = Field(
        default=None,
        description="Primary cube table when available.",
    )
    synonyms: list[str] = Field(
        default_factory=list,
        description="Alternative names for the cube.",
    )
    dimensions: list[Dimension] = Field(
        default_factory=list,
        description="Dimension list under this cube.",
    )
    measures: list[Measure] = Field(
        default_factory=list,
        description="Measure list under this cube.",
    )


class Topic(BaseModel):
    """A topic groups related cubes under a domain."""

    name: str = Field(description="Topic name.")
    synonyms: list[str] = Field(
        default_factory=list,
        description="Alternative names for the topic.",
    )
    cubes: list[Cube] = Field(
        default_factory=list,
        description="Cubes available within the topic.",
    )


class Domain(BaseModel):
    """Top-level business domain."""

    name: str = Field(description="Domain name.")
    synonyms: list[str] = Field(
        default_factory=list,
        description="Alternative names for the domain.",
    )
    topics: list[Topic] = Field(
        default_factory=list,
        description="Topics that belong to this domain.",
    )

