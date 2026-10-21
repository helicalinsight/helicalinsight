"""Object-oriented think-mode package.

Public API mirrors the former ``think_flow`` module. Prefer importing from
``helicalbi.interactive.think`` (or the compatibility facade
``helicalbi.interactive.think_flow``).
"""

from helicalbi.interactive.think.chat_shaping import public_step
from helicalbi.interactive.think.filters import correct_think_mode_filters
from helicalbi.interactive.think.models import ThinkCitedAnswer
from helicalbi.interactive.think.orchestrator import (
    ThinkModeOrchestrator,
    run_think_mode,
)
from helicalbi.interactive.think.synthesis import (
    synthesize_think_final_answer,
    synthesize_think_opening_insight,
)
from helicalbi.interactive.think.turn import ThinkTurn

__all__ = [
    "ThinkCitedAnswer",
    "ThinkModeOrchestrator",
    "ThinkTurn",
    "correct_think_mode_filters",
    "run_think_mode",
    "synthesize_think_final_answer",
    "synthesize_think_opening_insight",
    "public_step",
]
