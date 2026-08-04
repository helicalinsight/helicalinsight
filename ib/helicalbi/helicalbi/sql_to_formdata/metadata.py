"""Optional metadata loader from a saved report JSON (formdatajson.json)."""

from __future__ import annotations

import json
from pathlib import Path
from typing import Any


def load_metadata_from_report(path: str | Path) -> dict[str, Any]:
    """
    Extract column type / ID hints from a report save file like formdatajson.json.
    Returns { by_alias, by_column, location, metadataFileName, limitBy, database }.
    """
    data = json.loads(Path(path).read_text(encoding="utf-8"))
    by_alias: dict[str, Any] = {}
    by_column: dict[str, Any] = {}

    for col in data.get("columns") or []:
        entry = {
            "type": col.get("type"),
            "columnID": col.get("columnID"),
            "hidden": col.get("hidden", False),
            "hiddenIncludeInResultSet": col.get("hiddenIncludeInResultSet", False),
            "databaseName": col.get("databaseName"),
        }
        if col.get("autogen_alias"):
            by_alias[col["autogen_alias"]] = entry
        if col.get("label"):
            by_alias[col["label"]] = entry
        if col.get("column"):
            by_column[col["column"]] = entry
            name = col["column"].split(".")[-1]
            by_column[name] = entry

    state = data.get("state") or {}
    meta = data.get("metadata") or {}
    return {
        "by_alias": by_alias,
        "by_column": by_column,
        "location": data.get("location") or meta.get("location", ""),
        "metadataFileName": meta.get("metadataFileName", ""),
        "limitBy": (state.get("options") or {}).get("limitBy", 1000),
        "database": state.get("database") or "",
    }
