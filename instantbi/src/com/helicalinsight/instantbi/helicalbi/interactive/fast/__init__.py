"""Object-oriented fast (normal) interactive chat package.

Public API mirrors the former ``fast_flow`` module. Prefer importing from
``helicalbi.interactive.fast`` or the compatibility facade
``helicalbi.interactive.fast_flow``.
"""

from helicalbi.interactive.fast.data_model import SqlDataModelBuilder, sql_to_data_model
from helicalbi.interactive.fast.orchestrator import FastModeOrchestrator, run_fast_mode

__all__ = [
    "FastModeOrchestrator",
    "SqlDataModelBuilder",
    "run_fast_mode",
    "sql_to_data_model",
]
