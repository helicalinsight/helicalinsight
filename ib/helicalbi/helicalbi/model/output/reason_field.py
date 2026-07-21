from pydantic import Field

from helicalbi.common import app_config


def reason_field_kwargs(description: str) -> dict:
    """Return create_model kwargs for an optional reason field, or none when hidden."""

    if app_config.hide_prompt_reason:
        return {}
    return {"reason": (str, Field(description=description))}
