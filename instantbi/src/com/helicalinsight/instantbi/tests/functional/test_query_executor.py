"""Functional tests for report query services."""
import json
from unittest.mock import patch

import pytest

from helicalbi.api.QueryExecutor import generate_query


pytestmark = pytest.mark.functional


class TestGenerateQuery:
    def test_calls_adhoc_report_generate_query(self):
        form_data = {
            "location": "TestFold",
            "metadataFileName": "Metadata_1.metadata",
            "columns": [{"column": "travel.travel_cost"}],
            "filters": [{"column": "travel.travel_year", "value": "2018"}],
        }
        with patch(
            "helicalbi.api.QueryExecutor.fetch_service_api",
            return_value={"status": 1, "response": {"query": "SELECT 1"}},
        ) as mock_api:
            result = generate_query("cookie", form_data, request_id="req-1")

        assert result["response"]["query"] == "SELECT 1"
        payload = mock_api.call_args.kwargs["service_json"]
        assert mock_api.call_args.kwargs["session_cookie"] == "cookie"
        assert payload["type"] == "adhoc"
        assert payload["serviceType"] == "report"
        assert payload["service"] == "generateQuery"
        assert payload["requestId"] == "req-1"
        assert json.loads(payload["formData"]) == form_data
