"""Short output reminder appended to structured prompts."""
from __future__ import annotations

import json
from typing import Any, Literal, get_args, get_origin

format_instruction_string = """
{format_instructions}
"""


def brief_format_instructions(model: Any) -> str:
    """One JSON example. No schema, no properties block."""
    fields = getattr(model, "model_fields", None)
    if not isinstance(fields, dict) or not fields:
        return "JSON only."
    example = {name: _sample(field.annotation) for name, field in fields.items()}
    text = "JSON only. " + json.dumps(example, separators=(",", ":"))
    choices = [
        f"{name}: {' | '.join(options)}"
        for name, field in fields.items()
        if (options := _literal_choices(field.annotation))
    ]
    if choices:
        text += " " + ". ".join(choices) + "."
    return text


def _literal_choices(annotation: Any) -> list[str]:
    if get_origin(annotation) is not Literal:
        return []
    return [str(arg) for arg in get_args(annotation) if isinstance(arg, (str, int))]


def _sample(annotation: Any) -> Any:
    choices = _literal_choices(annotation)
    if choices:
        return "none" if "none" in choices else choices[0]
    origin = get_origin(annotation)
    if origin is list:
        return []
    if origin is dict:
        return {}
    if origin is not None:
        args = [arg for arg in get_args(annotation) if arg is not type(None)]
        return _sample(args[0]) if args else ""
    if annotation is bool:
        return False
    if annotation is int:
        return 0
    if annotation is float:
        return 0
    return ""
