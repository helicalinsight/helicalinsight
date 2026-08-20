"""SQL → wire formData converter (sqlglot + layered builders)."""

from .assembler import assemble_form_data, load_function_catalog, sql_to_form_data
from .functions_catalog import FunctionCatalog

__all__ = [
    "FunctionCatalog",
    "assemble_form_data",
    "load_function_catalog",
    "sql_to_form_data",
]
__version__ = "0.2.0"
