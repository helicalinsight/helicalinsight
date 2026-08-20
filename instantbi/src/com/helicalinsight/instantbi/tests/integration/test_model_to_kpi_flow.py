"""Integration test for the model → information → KPI pipeline.

This wires up several services that normally collaborate in the real
``/topNQuestion`` endpoint:

* ``ModelLayerHelper`` fetches the model semantic layer (HTTP mocked).
* ``InformationProvider`` summarises the layer for downstream prompts.
* ``KpiProvider`` builds a LangChain prompt and parses an LLM response.

The LLM step is stubbed because we exercise the orchestration logic, not
the model itself.
"""
from unittest.mock import MagicMock, patch

import pytest

from helicalbi.model.output.KpiData import KpiSchema
from helicalbi.service.modelservice.ModelLayerHelper import ModelLayerHelper
from helicalbi.service.modelservice.InformationProvider import InformationProvider
from helicalbi.service.kpi.KpiProvider import KpiProvider


pytestmark = pytest.mark.integration


class TestModelToKpiFlow:
    def test_pipeline_produces_kpis(self, session_cookie, sample_model_data):
        model_payload = {
            "status": 1,
            "response": {
                "data": {
                    "state": sample_model_data,
                    "metadata": {
                        "location": "/meta",
                        "metadataFileName": "metadata.json",
                    },
                }
            }
        }
        with patch(
            "helicalbi.service.modelservice.ModelLayerHelper.fetch_service_api",
            return_value=model_payload,
        ):
            helper = ModelLayerHelper(session_cookie, "model.json", "/models")

        assert helper.get_metadata_layerfile() == "metadata.json"

        info = InformationProvider(model_data=helper.get_model_semantic_layer())
        topics = info.get_topics("Sales Operation")
        assert topics == ["Travel", "Meetings"]
        attrs = info.get_attribute_string(topics)
        merged = {k: v for d in attrs for k, v in d.items()}
        assert "Employee" in merged
        assert "Client" in merged

        kpi_response = KpiSchema(
            answer=["Revenue by region", "Bookings count"],
            reason="standard KPIs",
        )
        usage = {"input_tokens": 8, "output_tokens": 4, "total_tokens": 12}
        usage_obj = MagicMock()
        usage_obj.model_dump.return_value = usage

        with patch("helicalbi.service.kpi.KpiProvider.PromptTemplate") as pt, patch(
            "helicalbi.service.kpi.KpiProvider.PydanticOutputParser"
        ) as parser_cls, patch(
            "helicalbi.service.kpi.KpiProvider.invoke_structured",
            return_value=(kpi_response, usage_obj),
        ):
            pt.return_value = MagicMock()
            parser = MagicMock()
            parser_cls.return_value = parser
            parser.get_format_instructions.return_value = "FMT"

            provider = KpiProvider(
                helper.get_model_semantic_layer(), "Sales Operation"
            )
            kpis, token_usage = provider.top_kpis()

        assert kpis == ["Revenue by region", "Bookings count"]
        assert token_usage == usage
