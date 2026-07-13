from pydantic import BaseModel, Field, create_model

from helicalbi.common import app_config


def _reason_field(description: str):
    if app_config.optional_prompt_reason:
        return (str, Field(default="", description=description))
    return (str, Field(description=description))


def get_table_response_model():
    """Pydantic model for table-selection LLM output."""
    return create_model(
        "TableResponse",
        required_tables=(
            list[str],
            Field(description="List of database table names required to answer the user query"),
        ),
        reason=_reason_field("Your reason for selecting these tables"),
        __base__=BaseModel,
    )


TableResponse = get_table_response_model()
