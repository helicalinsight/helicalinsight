find_tables_prompt_string = """
Return every catalog table that owns a component named in the question.
A question can need more than one table.
Use only catalog names.

Catalog:
{table_column_description}

Chat:
{last_chats}

Question:
{user_query}
"""


def find_tables_format_instructions() -> str:
    """String names only. An empty list leads the model to invent table objects."""
    return (
        'JSON only. {"required_tables":["table_a","table_b"]} '
        "Every catalog table that owns a component named in the question."
    )
