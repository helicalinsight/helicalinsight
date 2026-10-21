"""Compatibility facade for think mode.

Implementation lives in ``helicalbi.interactive.think`` (small SRP classes).
This module re-exports the historical public and test-facing API.
"""
from __future__ import annotations

from helicalbi.interactive.think.chat_shaping import public_step as _public_step
from helicalbi.interactive.think.filters import correct_think_mode_filters
from helicalbi.interactive.think.models import ThinkCitedAnswer
from helicalbi.interactive.think.orchestrator import run_think_mode
from helicalbi.interactive.think.synthesis.context import (
    opening_metadata as _opening_metadata,
    opening_sql_context as _opening_sql_context,
)
from helicalbi.interactive.think.synthesis.final_answer import (
    synthesize_think_final_answer,
)
from helicalbi.interactive.think.synthesis.opening import (
    synthesize_think_opening_insight,
)
from helicalbi.interactive.think.turn import ThinkTurn

__all__ = [
    "ThinkCitedAnswer",
    "ThinkTurn",
    "correct_think_mode_filters",
    "run_think_mode",
    "synthesize_think_final_answer",
    "synthesize_think_opening_insight",
    "_public_step",
    "_opening_metadata",
    "_opening_sql_context",
]
