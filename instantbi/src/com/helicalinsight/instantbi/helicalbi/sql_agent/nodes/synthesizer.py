from __future__ import annotations

import logging
from typing import Any, Dict

from helicalbi.sql_agent.config import SYNTHESIZER_PROMPT
from helicalbi.sql_agent.llm import invoke_agent_model
from helicalbi.sql_agent.models import SynthesizerOutput
from helicalbi.sql_agent.modes import profile_from_state
from helicalbi.sql_agent.nodes.decomposer import findings_for_state
from helicalbi.sql_agent.nodes.run_summary import build_run_summary
from helicalbi.sql_agent.state import AgentState

logger = logging.getLogger(__name__)


def _rule_based_answer(state: AgentState, summary: Dict[str, Any]) -> str:
    """Cheap final answer for fast mode — no extra LLM call."""
    steps = summary.get("investigation_steps") or []
    if not steps:
        return "No findings were collected for this question."
    lines = ["Investigation summary:"]
    for step in steps:
        kind = step.get("kind") or "step"
        question = step.get("question") or ""
        analysis = step.get("analysis") or ""
        piece = f"{step.get('step')}. ({kind}) {question}"
        if analysis:
            piece = f"{piece} — {analysis}"
        lines.append(piece)
    lines.append(
        f"Attempts: {summary.get('attempt_count') or 0}; "
        f"questions asked: {summary.get('question_count') or 0}."
    )
    return "\n".join(lines)


def synthesizer_node(state: AgentState) -> Dict[str, Any]:
    """Combine collected findings; skip LLM synthesizer in fast mode."""
    summary = build_run_summary(state)
    profile = profile_from_state(state)
    if profile.use_llm_synthesizer:
        parsed = invoke_agent_model(
            SYNTHESIZER_PROMPT,
            {
                "original_question": state.get("original_question") or "",
                "collected_data": findings_for_state(state),
            },
            SynthesizerOutput,
            state=state,
        )
        answer = (parsed.final_answer or "").strip()
    else:
        answer = _rule_based_answer(state, summary)
    logger.info(
        "Synthesizer mode=%s llm=%s answer_chars=%s questions=%s attempts=%s",
        profile.name,
        profile.use_llm_synthesizer,
        len(answer),
        summary["question_count"],
        summary["attempt_count"],
    )
    return {
        "final_answer": answer,
        "is_complete": True,
        "asked_questions": summary["asked_questions"],
        "attempt_count": summary["attempt_count"],
        "investigation_steps": summary["investigation_steps"],
    }
