import json
import logging
import uuid
from typing import Any

from helicalbi.api.HttpCallService import fetch_service_api
from helicalbi.common.ErrorMessages import normalize_service_error_message

logger = logging.getLogger(__name__)


def _service_error_message(api_response) -> str:
    if not api_response:
        return "Service API call failed."

    response = api_response.get("response")
    if isinstance(response, dict):
        message = response.get("message") or response.get("error") or "Failed to fetch model."
    elif response is not None:
        message = str(response)
    else:
        message = "Failed to fetch model."

    return normalize_service_error_message(str(message)) or "Failed to fetch model."


def _topic_label(topic: Any) -> str:
    if isinstance(topic, dict):
        return str(
            topic.get("topic")
            or topic.get("topic_name")
            or topic.get("name")
            or ""
        ).strip()
    return str(topic or "").strip()


def _has_domain_or_topic(state: dict) -> bool:
    """True when the semantic layer already defines a domain and/or topic."""
    for entry in state.get("domain") or []:
        if not isinstance(entry, dict):
            continue
        if str(entry.get("domain_name") or "").strip():
            return True
        if str(entry.get("description") or "").strip():
            return True
        for topic in entry.get("topics") or []:
            if _topic_label(topic):
                return True
            if isinstance(topic, dict) and str(topic.get("description") or "").strip():
                return True
    if state.get("topic_mappings"):
        return True
    return False


class ModelLayerHelper:
    def __init__(self, session_cookie, model_file_name, location):
        self.session_cookie = session_cookie
        self.model_file_name = model_file_name
        self.location = location
        self.model_data = self.fetch_model_semantic_layer()

    def fetch_model_semantic_layer(self) -> dict:
        form_data = {
            "dir": self.location,
            "file": self.model_file_name,
        }
        payload_json = {
            "type": "instantbi",
            "serviceType": "instant",
            "service": "getAiAgentForEdit",
            "formData": json.dumps(form_data),
            "requestId": uuid.uuid4().hex
        }
        api_response = fetch_service_api(session_cookie=self.session_cookie, service_json=payload_json)
        if not api_response or api_response.get("status") != 1:
            raise RuntimeError(_service_error_message(api_response))
        return api_response["response"]

    def get_metadata_layerlocation(self):
        return self.model_data["data"]["metadata"]["location"]

    def get_metadata_layerfile(self):
        return self.model_data["data"]["metadata"]["metadataFileName"]

    def get_model_description(self) -> str:
        """Resource-level model description from getAiAgentForEdit (outside state)."""
        data = (self.model_data or {}).get("data") or {}
        return str(data.get("description") or "").strip()

    def get_model_semantic_layer(self):
        state = self.model_data["data"]["state"]
        if not isinstance(state, dict) or _has_domain_or_topic(state):
            return state
        description = self.get_model_description()
        if not description:
            return state
        # When domain/topic are absent, use the saved model description as context.
        enriched = dict(state)
        enriched["domain"] = [
            {
                "domain_name": description,
                "description": description,
                "topics": [],
            }
        ]
        logger.info(
            "Using model description as domain/topic fallback model=%s",
            self.model_file_name,
        )
        return enriched
