"""Functional tests for ``helicalbi.service.agentservice.AgentLayerHelper``."""
import json
from unittest.mock import patch

import pytest

from helicalbi.service.agentservice.AgentLayerHelper import AgentLayerHelper


pytestmark = pytest.mark.functional


@pytest.fixture
def fake_response_payload(sample_agent_data):
    return {
        "status": 1,
        "response": {
            "data": {
                "state": sample_agent_data,
                "metadata": {
                    "location": "/meta/dir",
                    "metadataFileName": "metadata.json",
                },
            }
        },
    }


class TestAgentLayerHelper:
    def test_constructor_fetches_agent_data(self, session_cookie, fake_response_payload):
        with patch(
            "helicalbi.service.agentservice.AgentLayerHelper.fetch_service_api",
            return_value=fake_response_payload,
        ) as mock_fetch:
            helper = AgentLayerHelper(session_cookie, "agent.json", "/agent/dir")

        assert helper.agent_data == fake_response_payload["response"]
        payload = mock_fetch.call_args.kwargs["service_json"]
        assert payload["service"] == "getAiAgentForEdit"
        assert payload["serviceType"] == "instant"
        assert mock_fetch.call_args.kwargs["session_cookie"] == session_cookie

        form_data = json.loads(payload["formData"])
        assert form_data == {"dir": "/agent/dir", "file": "agent.json"}

    def test_get_metadata_layerlocation(self, session_cookie, fake_response_payload):
        with patch(
            "helicalbi.service.agentservice.AgentLayerHelper.fetch_service_api",
            return_value=fake_response_payload,
        ):
            helper = AgentLayerHelper(session_cookie, "agent.json", "/agent/dir")
        assert helper.get_metadata_layerlocation() == "/meta/dir"

    def test_get_metadata_layerfile(self, session_cookie, fake_response_payload):
        with patch(
            "helicalbi.service.agentservice.AgentLayerHelper.fetch_service_api",
            return_value=fake_response_payload,
        ):
            helper = AgentLayerHelper(session_cookie, "agent.json", "/agent/dir")
        assert helper.get_metadata_layerfile() == "metadata.json"

    def test_get_agent_semantic_layer_returns_state(
        self, session_cookie, fake_response_payload, sample_agent_data
    ):
        with patch(
            "helicalbi.service.agentservice.AgentLayerHelper.fetch_service_api",
            return_value=fake_response_payload,
        ):
            helper = AgentLayerHelper(session_cookie, "agent.json", "/agent/dir")
        assert helper.get_agent_semantic_layer() == sample_agent_data

    def test_raises_when_service_returns_error_status(
        self, session_cookie
    ):
        with patch(
            "helicalbi.service.agentservice.AgentLayerHelper.fetch_service_api",
            return_value={
                "status": 0,
                "response": {
                    "message": "Error: The file missing.agent doesn't exists. Aborting operation."
                },
            },
        ):
            with pytest.raises(RuntimeError, match="doesn't exists"):
                AgentLayerHelper(session_cookie, "missing.agent", "/agent/dir")
