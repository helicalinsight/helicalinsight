"""Programmatic investigation picture — questions asked and attempt count (no LLM)."""
from __future__ import annotations

from typing import Any, Dict, List


def _unique_preserve(questions: List[str]) -> List[str]:
    seen = set()
    out: List[str] = []
    for question in questions:
        key = question.strip().lower()
        if not key or key in seen:
            continue
        seen.add(key)
        out.append(question.strip())
    return out


def asked_questions_from_state(state: Dict[str, Any]) -> List[str]:
    """Focused sub-questions the agent framed — the multi-step picture, not one-shot."""
    tracked = [
        str(q).strip()
        for q in (state.get("asked_questions") or [])
        if str(q or "").strip()
    ]
    if tracked:
        return _unique_preserve(tracked)

    from_collected = [
        str(step.get("sub_question") or "").strip()
        for step in (state.get("collected_data") or [])
        if str(step.get("sub_question") or "").strip()
    ]
    current = str(state.get("current_sub_question") or "").strip()
    if current:
        from_collected.append(current)
    return _unique_preserve(from_collected)


def attempt_count_from_state(state: Dict[str, Any]) -> int:
    """How many planner tool loops ran while building the picture."""
    return int(state.get("tool_loop_count") or 0)


def investigation_steps_from_state(state: Dict[str, Any]) -> List[Dict[str, Any]]:
    """Ordered investigation steps from collected findings (programmatic)."""
    steps: List[Dict[str, Any]] = []
    for index, step in enumerate(state.get("collected_data") or [], start=1):
        question = str(step.get("sub_question") or "").strip()
        if not question:
            continue
        steps.append(
            {
                "step": index,
                "question": question,
                "kind": "chart" if step.get("include_in_dashboard") else "lookup",
                "analysis": str(step.get("analysis") or "").strip(),
            }
        )
    return steps


def build_run_summary(state: Dict[str, Any]) -> Dict[str, Any]:
    """Programmatic multi-step investigation picture for the API response."""
    questions = asked_questions_from_state(state)
    attempts = attempt_count_from_state(state)
    steps = investigation_steps_from_state(state)
    return {
        "asked_questions": questions,
        "attempt_count": attempts,
        "question_count": len(questions),
        "investigation_steps": steps,
    }


def append_question(existing: List[str] | None, question: str) -> List[str]:
    """Append a framed sub-question if non-empty (order preserved)."""
    text = (question or "").strip()
    if not text:
        return list(existing or [])
    out = list(existing or [])
    out.append(text)
    return out
