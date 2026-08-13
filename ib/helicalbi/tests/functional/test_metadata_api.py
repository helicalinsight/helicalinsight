"""Functional tests for ``helicalbi.api.Metadata``."""
from unittest.mock import patch

import pytest

from helicalbi.api.Metadata import build_column, get_json_data_metadata


pytestmark = pytest.mark.functional


class TestGetJsonDataMetadata:
    def test_returns_response_payload(self, session_cookie):
        fake_response = {"response": {"tables": {"t1": {}}}, "status": 1}
        with patch(
            "helicalbi.api.Metadata.fetch_service_api", return_value=fake_response
        ) as mock_fetch:
            result = get_json_data_metadata(session_cookie, "meta.json", "/data")
        assert result == {"tables": {"t1": {}}}

        called_args = mock_fetch.call_args
        payload = called_args.kwargs["service_json"]
        assert payload["service"] == "get"
        assert payload["serviceType"] == "metadata"
        assert "meta.json" in payload["formData"]
        assert "/data" in payload["formData"]
        assert "requestId" in payload

    def test_passes_session_cookie_through(self, session_cookie):
        with patch(
            "helicalbi.api.Metadata.fetch_service_api",
            return_value={"response": {}, "status": 1},
        ) as mock_fetch:
            get_json_data_metadata(session_cookie, "m", "/d")
        assert mock_fetch.call_args.kwargs["session_cookie"] == session_cookie


class TestMetadataBuildColumn:
    def test_unqualified_column_when_dbname_empty(self):
        result = build_column("travel_id", "TID", col_id=200)
        assert result["column"]["name"] == "travel_id"
        assert result["alias"] == "TID"
        assert result["column"]["id"] == "200"
        assert result["floatingType"] == "discrete"

    def test_qualifies_column_name_with_dbname(self):
        result = build_column("travel_id", "TID", col_id=200, dbname="sampletraveldata")
        assert result["column"]["name"] == "sampletraveldata.travel_id"
        assert result["alias"] == "TID"

    def test_default_id_is_1000(self):
        assert build_column("c", "a")["column"]["id"] == "1000"
