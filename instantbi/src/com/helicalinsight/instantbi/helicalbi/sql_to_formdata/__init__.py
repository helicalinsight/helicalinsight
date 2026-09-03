"""SQL → wire formData converter (sqlglot + part builders)."""

from .assembler import assemble_form_data, load_function_catalog, sql_to_form_data
from .functions_catalog import FunctionCatalog
from .hr_parts import build_convert_hreport_parts, form_data_to_sql_parts, viz_model_to_viz_parts

__all__ = [
    "FunctionCatalog",
    "assemble_form_data",
    "load_function_catalog",
    "sql_to_form_data",
    "form_data_to_sql_parts",
    "viz_model_to_viz_parts",
    "build_convert_hreport_parts",
]
__version__ = "0.2.0"
