from pydantic import BaseModel, Field, create_model

from helicalbi.model.output.reason_field import reason_field_kwargs


def get_table_response_model():
    """Pydantic model for table-selection LLM output."""
    return create_model(
        "TableResponse",
        required_tables=(
            list[str],
            Field(description="List of database table names required to answer the user query"),
        ),
        **reason_field_kwargs("Your reason for selecting these tables"),
        __base__=BaseModel,
    )


TableResponse = get_table_response_model()
