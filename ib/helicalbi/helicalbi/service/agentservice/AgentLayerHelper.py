import json
import logging
import uuid

from helicalbi.api.HttpCallService import fetch_service_api
from helicalbi.common.ErrorMessages import normalize_service_error_message

logger = logging.getLogger(__name__)


def _service_error_message(api_response) -> str:
    if not api_response:
        return "Service API call failed."

    response = api_response.get("response")
    if isinstance(response, dict):
        message = response.get("message") or response.get("error") or "Failed to fetch agent."
    elif response is not None:
        message = str(response)
    else:
        message = "Failed to fetch agent."

    return normalize_service_error_message(str(message)) or "Failed to fetch agent."


class AgentLayerHelper:
    def __init__(self, session_cookie, agent_file_name, location):
        self.session_cookie = session_cookie
        self.agent_file_name = agent_file_name
        self.location = location
        self.agent_data = self.fetch_agent_semantic_layer()

    def fetch_agent_semantic_layer(self) -> dict:
        form_data = {
            "dir": self.location,
            "file": self.agent_file_name,
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
        return self.agent_data["data"]["metadata"]["location"]

    def get_metadata_layerfile(self):
        return self.agent_data["data"]["metadata"]["metadataFileName"]

    def get_agent_semantic_layer(self):
        return self.agent_data["data"]["state"]
