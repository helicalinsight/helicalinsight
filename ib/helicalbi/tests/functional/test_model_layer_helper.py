"""Functional tests for ``helicalbi.service.modelservice.ModelLayerHelper``."""
import json
from unittest.mock import patch

import pytest

from helicalbi.service.modelservice.ModelLayerHelper import ModelLayerHelper


pytestmark = pytest.mark.functional


@pytest.fixture
def fake_response_payload(sample_model_data):
    return {
        "status": 1,
        "response": {
            "data": {
                "state": sample_model_data,
                "description": "Retail analytics model",
                "metadata": {
                    "location": "/meta/dir",
                    "metadataFileName": "metadata.json",
                },
            }
        },
    }


class TestModelLayerHelper:
    def test_constructor_fetches_model_data(self, session_cookie, fake_response_payload):
        with patch(
            "helicalbi.service.modelservice.ModelLayerHelper.fetch_service_api",
            return_value=fake_response_payload,
        ) as mock_fetch:
            helper = ModelLayerHelper(session_cookie, "model.json", "/model/dir")

        assert helper.model_data == fake_response_payload["response"]
        payload = mock_fetch.call_args.kwargs["service_json"]
        assert payload["service"] == "getAiAgentForEdit"
        assert payload["serviceType"] == "instant"
        assert mock_fetch.call_args.kwargs["session_cookie"] == session_cookie

        form_data = json.loads(payload["formData"])
        assert form_data == {"dir": "/model/dir", "file": "model.json"}

    def test_get_metadata_layerlocation(self, session_cookie, fake_response_payload):
        with patch(
            "helicalbi.service.modelservice.ModelLayerHelper.fetch_service_api",
            return_value=fake_response_payload,
        ):
            helper = ModelLayerHelper(session_cookie, "model.json", "/model/dir")
        assert helper.get_metadata_layerlocation() == "/meta/dir"

    def test_get_metadata_layerfile(self, session_cookie, fake_response_payload):
        with patch(
            "helicalbi.service.modelservice.ModelLayerHelper.fetch_service_api",
            return_value=fake_response_payload,
        ):
            helper = ModelLayerHelper(session_cookie, "model.json", "/model/dir")
        assert helper.get_metadata_layerfile() == "metadata.json"

    def test_get_model_semantic_layer_returns_state(
        self, session_cookie, fake_response_payload, sample_model_data
    ):
        with patch(
            "helicalbi.service.modelservice.ModelLayerHelper.fetch_service_api",
            return_value=fake_response_payload,
        ):
            helper = ModelLayerHelper(session_cookie, "model.json", "/model/dir")
        assert helper.get_model_semantic_layer() == sample_model_data

    def test_description_fallback_when_no_domain_or_topic(self, session_cookie):
        payload = {
            "status": 1,
            "response": {
                "data": {
                    "description": "Travel spend analytics",
                    "state": {"domain": [], "cube": []},
                    "metadata": {
                        "location": "/meta/dir",
                        "metadataFileName": "metadata.json",
                    },
                }
            },
        }
        with patch(
            "helicalbi.service.modelservice.ModelLayerHelper.fetch_service_api",
            return_value=payload,
        ):
            helper = ModelLayerHelper(session_cookie, "model.json", "/model/dir")

        assert helper.get_model_description() == "Travel spend analytics"
        state = helper.get_model_semantic_layer()
        assert state["domain"] == [
            {
                "domain_name": "Travel spend analytics",
                "description": "Travel spend analytics",
                "topics": [],
            }
        ]

    def test_raises_when_service_returns_error_status(
        self, session_cookie
    ):
        with patch(
            "helicalbi.service.modelservice.ModelLayerHelper.fetch_service_api",
            return_value={
                "status": 0,
                "response": {
                    "message": "Error: The file missing.model doesn't exists. Aborting operation."
                },
            },
        ):
            with pytest.raises(RuntimeError, match="doesn't exists"):
                ModelLayerHelper(session_cookie, "missing.model", "/model/dir")
