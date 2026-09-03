from __future__ import annotations

import json
from typing import Any, Optional

from helicalbi.sql_agent.modes import DEFAULT_MODE, profile_from_state


def findings_for_prompt(collected_data, *, result_chars: Optional[int] = None) -> str:
    """Compact findings so chat_response / schema blobs never enter the LLM."""
    if not collected_data:
        return "(none yet)"
    limit = 2000 if result_chars is None else max(0, int(result_chars))
    compact = [
        {
            "sub_question": step.get("sub_question"),
            "analysis": step.get("analysis"),
            "execution_result": str(step.get("execution_result") or "")[:limit],
        }
        for step in collected_data
    ]
    return json.dumps(compact, indent=2, default=str)


def findings_for_state(state: dict[str, Any]) -> str:
    profile = profile_from_state(state) if state.get("agent_mode") else None
    result_chars = profile.findings_result_chars if profile else None
    return findings_for_prompt(state.get("collected_data"), result_chars=result_chars)
