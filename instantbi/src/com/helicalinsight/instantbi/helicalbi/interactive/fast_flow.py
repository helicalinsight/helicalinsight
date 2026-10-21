"""Compatibility facade for interactive fast (normal) mode.

Implementation lives in ``helicalbi.interactive.fast`` (small SRP classes).
This module re-exports the historical public API used by the controller and tests.
"""
from __future__ import annotations

from helicalbi.interactive.fast.data_model import sql_to_data_model
from helicalbi.interactive.fast.orchestrator import run_fast_mode

# Re-export SqlExecutor for integration tests that patch
# ``helicalbi.interactive.fast_flow.SqlExecutor``.
from helicalbi.core.flows.SqlExecutor import SqlExecutor  # noqa: F401

__all__ = [
    "SqlExecutor",
    "run_fast_mode",
    "sql_to_data_model",
]
