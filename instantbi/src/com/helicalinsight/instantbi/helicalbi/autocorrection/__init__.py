"""SQL autocorrection: wrong-table columns, join keys, and missing join aliases.

Off unless ``sql.autocorrect`` is true in application config.
"""

from helicalbi.autocorrection.apply import apply_sql_correction, autocorrect_enabled
from helicalbi.autocorrection.columns import (
    columns_for_join_recheck,
    format_column_corrections,
    join_api_columns,
    join_needs_rewrite,
    replace_missing_columns,
    restore_join_aliases,
    sql_column_mismatches,
)

__all__ = [
    "apply_sql_correction",
    "autocorrect_enabled",
    "columns_for_join_recheck",
    "format_column_corrections",
    "join_api_columns",
    "join_needs_rewrite",
    "replace_missing_columns",
    "restore_join_aliases",
    "sql_column_mismatches",
]
