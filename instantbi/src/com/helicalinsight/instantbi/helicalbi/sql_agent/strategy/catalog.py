"""Load and query the dashboard / analytical strategy JSON catalogs.

Single responsibility: disk I/O + cache + list/get. Selection and diagnostics live elsewhere.
"""
from __future__ import annotations

import json
from functools import lru_cache
from pathlib import Path
from typing import Any, Dict, Mapping, Optional, Sequence

from helicalbi.sql_agent.constants import (
    ANALYTICAL_CATALOG_CACHE_SIZE,
    ANALYTICAL_CATALOG_PATH,
    ANALYTICAL_KEY_OPERATIONS,
    ANALYTICAL_KEY_WRAPPER,
    API_OPERATION_LIST_FIELDS,
    CATALOG_KEY_DEFAULT_STRATEGY,
    CATALOG_KEY_STRATEGIES,
    CATALOG_PATH,
    CUSTOM_OPERATION_TYPE,
    CUSTOM_OPERATIONS_KEY,
    CUSTOM_OPERATIONS_KEY_SNAKE,
    ERROR_EMPTY_CATALOG,
    ERROR_INVALID_ANALYTICAL_PAYLOAD,
    ERROR_INVALID_CATALOG,
    JSON_ENCODING,
    LAYOUT_ALIASES,
    OPERATION_CATEGORY_KEY,
    OPERATION_DESCRIPTION_KEY,
    OPERATION_KEY,
    OPERATION_TYPE_KEY,
    STRATEGY_CATALOG_CACHE_SIZE,
    STRATEGY_MECE,
)


class StrategyCatalog:
    """Cached reader for ``dashboard_strategies.json``."""

    def __init__(self, path: Optional[Path] = None) -> None:
        self.path = Path(path) if path is not None else CATALOG_PATH

    def read(self) -> dict[str, Any]:
        with self.path.open(encoding=JSON_ENCODING) as handle:
            data = json.load(handle)
        if not isinstance(data, dict) or not isinstance(
            data.get(CATALOG_KEY_STRATEGIES), dict
        ):
            raise ValueError(ERROR_INVALID_CATALOG.format(path=self.path))
        return data

    def list_strategies(self, catalog: Optional[Mapping[str, Any]] = None) -> Dict[str, dict[str, Any]]:
        data = catalog or self.load()
        strategies = data.get(CATALOG_KEY_STRATEGIES) or {}
        return {
            str(key): dict(value)
            for key, value in strategies.items()
            if isinstance(value, dict)
        }

    def get(
        self,
        strategy_id: Optional[str],
        catalog: Optional[Mapping[str, Any]] = None,
    ) -> dict[str, Any]:
        data = catalog or self.load()
        strategies = self.list_strategies(data)
        key = LAYOUT_ALIASES.get(str(strategy_id or "").strip(), str(strategy_id or "").strip())
        if key in strategies:
            return dict(strategies[key])
        default_id = str(data.get(CATALOG_KEY_DEFAULT_STRATEGY) or STRATEGY_MECE)
        if default_id in strategies:
            return dict(strategies[default_id])
        if strategies:
            return dict(next(iter(strategies.values())))
        raise ValueError(ERROR_EMPTY_CATALOG)

    def load(self, path: str = "") -> dict[str, Any]:
        return load_strategy_catalog(path)


@lru_cache(maxsize=STRATEGY_CATALOG_CACHE_SIZE)
def load_strategy_catalog(path: str = "") -> dict[str, Any]:
    """Cached dashboard strategy catalog. Pass a path string for alternate files (tests)."""
    catalog_path = Path(path) if path else CATALOG_PATH
    return StrategyCatalog(catalog_path).read()


def clear_strategy_catalog_cache() -> None:
    """Drop the LRU cache (tests / hot-reload)."""
    load_strategy_catalog.cache_clear()


def list_strategies(catalog: Optional[Mapping[str, Any]] = None) -> Dict[str, dict[str, Any]]:
    return StrategyCatalog().list_strategies(catalog)


def get_strategy(
    strategy_id: Optional[str],
    catalog: Optional[Mapping[str, Any]] = None,
) -> dict[str, Any]:
    return StrategyCatalog().get(strategy_id, catalog)


def operations_from_catalog_payload(data: Any) -> list[dict[str, Any]]:
    """Accept legacy list catalogs or analyticalOperationsCatalog wrappers."""
    if isinstance(data, list):
        return [dict(item) for item in data if isinstance(item, dict)]
    if isinstance(data, Mapping):
        nested = data.get(ANALYTICAL_KEY_WRAPPER)
        if isinstance(nested, Mapping):
            ops = nested.get(ANALYTICAL_KEY_OPERATIONS)
            if isinstance(ops, list):
                return [dict(item) for item in ops if isinstance(item, dict)]
        ops = data.get(ANALYTICAL_KEY_OPERATIONS)
        if isinstance(ops, list):
            return [dict(item) for item in ops if isinstance(item, dict)]
    raise ValueError(ERROR_INVALID_ANALYTICAL_PAYLOAD)


@lru_cache(maxsize=ANALYTICAL_CATALOG_CACHE_SIZE)
def load_analytical_operations(path: Optional[str] = None) -> list[dict[str, Any]]:
    """Cached list of analytical operations from analytical_strategy.json."""
    catalog_path = Path(path) if path else ANALYTICAL_CATALOG_PATH
    with catalog_path.open(encoding=JSON_ENCODING) as handle:
        data = json.load(handle)
    return operations_from_catalog_payload(data)


def list_analytical_operations_for_api(
    path: Optional[str] = None,
) -> list[dict[str, Any]]:
    """Public catalog projection for UI / InstantBI utility consumers."""
    operations: list[dict[str, Any]] = []
    for op in load_analytical_operations(path):
        name = str(op.get(OPERATION_KEY) or "").strip()
        if not name:
            continue
        entry: dict[str, Any] = {
            OPERATION_KEY: name,
            OPERATION_CATEGORY_KEY: str(op.get(OPERATION_CATEGORY_KEY) or "").strip(),
            OPERATION_DESCRIPTION_KEY: str(
                op.get(OPERATION_DESCRIPTION_KEY) or ""
            ).strip(),
        }
        for field in API_OPERATION_LIST_FIELDS:
            entry[field] = [
                str(item).strip()
                for item in (op.get(field) or [])
                if str(item).strip()
            ]
        operations.append(entry)
    return operations


def _text_list(value: Any) -> list[str]:
    if not isinstance(value, list):
        return []
    return [str(item).strip() for item in value if str(item).strip()]


def is_custom_operation(entry: Any) -> bool:
    """True when a saved strategy is marked type custom for edit mode."""
    if not isinstance(entry, Mapping):
        return False
    kind = str(entry.get(OPERATION_TYPE_KEY) or "").strip().lower()
    return kind == CUSTOM_OPERATION_TYPE or bool(entry.get(CUSTOM_OPERATION_TYPE))


def normalize_custom_operation(entry: Mapping[str, Any]) -> Optional[dict[str, Any]]:
    """Project a model custom strategy into the analytical-operation shape."""
    if not is_custom_operation(entry):
        return None
    name = str(entry.get(OPERATION_KEY) or "").strip()
    if not name:
        return None
    execution = entry.get("execution") if isinstance(entry.get("execution"), Mapping) else {}
    return {
        OPERATION_KEY: name,
        OPERATION_TYPE_KEY: CUSTOM_OPERATION_TYPE,
        OPERATION_CATEGORY_KEY: (
            str(entry.get(OPERATION_CATEGORY_KEY) or CUSTOM_OPERATION_TYPE).strip()
            or CUSTOM_OPERATION_TYPE
        ),
        OPERATION_DESCRIPTION_KEY: str(entry.get(OPERATION_DESCRIPTION_KEY) or "").strip(),
        "requiredInputs": _text_list(entry.get("requiredInputs")),
        "optionalInputs": _text_list(entry.get("optionalInputs")),
        "preconditions": _text_list(entry.get("preconditions")),
        "limitations": _text_list(entry.get("limitations")),
        "outputFields": _text_list(entry.get("outputFields")),
        "dashboardVisualizations": _text_list(entry.get("dashboardVisualizations")),
        "execution": {
            "method": str(execution.get("method") or "sql").strip() or "sql",
            "logic": str(execution.get("logic") or "").strip(),
        },
        CUSTOM_OPERATION_TYPE: True,
    }


def custom_operations_from_source(source: Any) -> list[dict[str, Any]]:
    """Read type-custom strategies saved on the semantic model."""
    if not isinstance(source, Mapping):
        return []
    raw = source.get(CUSTOM_OPERATIONS_KEY)
    if not isinstance(raw, list):
        raw = source.get(CUSTOM_OPERATIONS_KEY_SNAKE)
    if not isinstance(raw, list):
        return []
    operations: list[dict[str, Any]] = []
    seen: set[str] = set()
    for item in raw:
        if not isinstance(item, Mapping):
            continue
        normalized = normalize_custom_operation(item)
        if normalized is None:
            continue
        key = normalized[OPERATION_KEY].lower()
        if key in seen:
            continue
        seen.add(key)
        operations.append(normalized)
    return operations


def merge_custom_operations(
    catalog: Sequence[Mapping[str, Any]],
    custom: Optional[Sequence[Mapping[str, Any]]] = None,
) -> list[dict[str, Any]]:
    """Catalog operations plus model custom strategies. Custom names replace catalog ones."""
    extras = custom_operations_from_source({CUSTOM_OPERATIONS_KEY: list(custom or [])})
    overridden = {op[OPERATION_KEY].lower() for op in extras}
    merged: list[dict[str, Any]] = []
    seen: set[str] = set()
    for op in catalog or []:
        if not isinstance(op, Mapping):
            continue
        name = str(op.get(OPERATION_KEY) or "").strip()
        key = name.lower()
        if not name or key in overridden or key in seen:
            continue
        seen.add(key)
        merged.append(dict(op))
    for op in extras:
        key = op[OPERATION_KEY].lower()
        if key in seen:
            continue
        seen.add(key)
        merged.append(dict(op))
    return merged
