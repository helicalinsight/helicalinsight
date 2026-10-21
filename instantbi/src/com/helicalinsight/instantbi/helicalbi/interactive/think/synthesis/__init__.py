"""Think-mode synthesis strategies (opening overview + cited conclusion)."""

from helicalbi.interactive.think.synthesis.context import (
    OpeningContextBuilder,
    opening_metadata,
    opening_sql_context,
)
from helicalbi.interactive.think.synthesis.fallback import CitedAnswerFallback
from helicalbi.interactive.think.synthesis.final_answer import (
    CitationIndexNormalizer,
    FinalAnswerSynthesizer,
    synthesize_think_final_answer,
)
from helicalbi.interactive.think.synthesis.opening import (
    OpeningInsightSynthesizer,
    synthesize_think_opening_insight,
)

__all__ = [
    "CitedAnswerFallback",
    "CitationIndexNormalizer",
    "FinalAnswerSynthesizer",
    "OpeningContextBuilder",
    "OpeningInsightSynthesizer",
    "opening_metadata",
    "opening_sql_context",
    "synthesize_think_final_answer",
    "synthesize_think_opening_insight",
]
