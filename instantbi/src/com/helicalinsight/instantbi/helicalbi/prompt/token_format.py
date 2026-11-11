"""Compact token form for JSON values sent to a model."""
from __future__ import annotations

import json
from typing import Any, Optional


def to_token(value: Any) -> Any:
    """Return a token rendering when ``value`` is JSON. Leave other values unchanged."""
    parsed = _as_json(value)
    if parsed is None:
        return value
    return _render(parsed).rstrip("\n")


def prepare_prompt_inputs(inputs: Any) -> Any:
    """Convert JSON-valued prompt inputs. Leave questions and SQL text unchanged."""
    if not isinstance(inputs, dict):
        return inputs
    prepared = {}
    for key, value in inputs.items():
        if key in {"user_question", "question", "dialect", "sql", "username", "title"}:
            prepared[key] = value
            continue
        prepared[key] = to_token(value)
    return prepared


def _as_json(value: Any) -> Optional[Any]:
    if isinstance(value, (dict, list)):
        return value
    if not isinstance(value, str):
        return None
    text = value.strip()
    if not text or text[0] not in "{[":
        return None
    try:
        parsed = json.loads(text)
    except json.JSONDecodeError:
        return None
    if isinstance(parsed, (dict, list)):
        return parsed
    return None


def _render(value: Any, indent: int = 0) -> str:
    pad = "  " * indent
    if isinstance(value, list):
        return _render_list(value, indent)
    if isinstance(value, dict):
        if not value:
            return pad + "{}"
        lines = []
        for key, item in value.items():
            rendered_key = _cell(key)
            if isinstance(item, list) and item and all(isinstance(row, dict) for row in item):
                keys = _uniform_keys(item)
                if keys is not None:
                    lines.append(f"{pad}{rendered_key}[{len(item)}]{{{','.join(keys)}}}:")
                    for row in item:
                        lines.append(pad + "  " + ",".join(_cell(row.get(name)) for name in keys))
                    continue
            if isinstance(item, (dict, list)):
                nested = _render(item, indent + 1)
                lines.append(f"{pad}{rendered_key}:")
                lines.append(nested)
                continue
            lines.append(f"{pad}{rendered_key}: {_cell(item)}")
        return "\n".join(lines)
    return pad + _cell(value)


def _render_list(value: list, indent: int) -> str:
    pad = "  " * indent
    keys = _uniform_keys(value)
    if keys is not None:
        header = f"{pad}[{len(value)}]{{{','.join(keys)}}}:"
        rows = [
            pad + "  " + ",".join(_cell(row.get(name)) for name in keys)
            for row in value
        ]
        return "\n".join([header, *rows])
    if value and all(not isinstance(item, (dict, list)) for item in value):
        return pad + f"[{len(value)}]: " + ",".join(_cell(item) for item in value)
    if not value:
        return pad + "[0]:"
    lines = [f"{pad}[{len(value)}]:"]
    for item in value:
        lines.append(_render(item, indent + 1))
    return "\n".join(lines)


def _uniform_keys(value: list) -> Optional[list]:
    if not value or not all(isinstance(item, dict) and item for item in value):
        return None
    keys = list(value[0].keys())
    if any(list(item.keys()) != keys for item in value):
        return None
    for item in value:
        if any(isinstance(item.get(name), (dict, list)) for name in keys):
            return None
    return [str(key) for key in keys]


def _cell(value: Any) -> str:
    if value is None:
        return ""
    if isinstance(value, bool):
        return "true" if value else "false"
    if isinstance(value, (int, float)):
        return str(value)
    text = str(value)
    if text == "" or any(mark in text for mark in (",", ":", "\n", '"', " ")):
        return json.dumps(text, ensure_ascii=False)
    return text
