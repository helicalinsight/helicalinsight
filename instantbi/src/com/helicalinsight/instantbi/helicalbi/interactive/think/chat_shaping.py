"""Shape chat / step payloads for the interactive wire format."""
from __future__ import annotations

from typing import Any, Mapping

from helicalbi.interactive.think.constants import INTERACTIVE_STRIP_KEYS
from helicalbi.model.output.ChatResponse import ChatResponse


class InteractiveChatShaper:
    """Convert ChatResponse / dict steps into client-safe interactive dicts."""

    def chat_dict(self, chat_response: Any) -> dict[str, Any]:
        if isinstance(chat_response, ChatResponse):
            return chat_response.to_interactive_client_dict()
        if not isinstance(chat_response, dict):
            return {}
        try:
            return ChatResponse.model_validate(chat_response).to_interactive_client_dict()
        except Exception:
            payload = dict(chat_response)
            for key in INTERACTIVE_STRIP_KEYS:
                payload.pop(key, None)
            return payload

    def public_step(self, step: Mapping[str, Any]) -> dict[str, Any]:
        chat_response = (
            step.get("chat_response") if isinstance(step.get("chat_response"), dict) else {}
        )
        return {
            "sub_question": step.get("sub_question") or "",
            "title": step.get("title") or "",
            "analysis": step.get("analysis") or "",
            "chat_seq_id": step.get("chat_seq_id") or "",
            "chat_response": self.chat_dict(chat_response),
            "report_model": step.get("report_model")
            or (chat_response.get("report_model") if isinstance(chat_response, dict) else {})
            or {},
        }


_DEFAULT_SHAPER = InteractiveChatShaper()


def interactive_chat_dict(chat_response: Any) -> dict[str, Any]:
    return _DEFAULT_SHAPER.chat_dict(chat_response)


def public_step(step: Mapping[str, Any]) -> dict[str, Any]:
    return _DEFAULT_SHAPER.public_step(step)
