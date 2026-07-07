"""Integration test for the agent → information → KPI pipeline.

This wires up several services that normally collaborate in the real
``/topNQuestion`` endpoint:

* ``AgentLayerHelper`` fetches the agent semantic layer (HTTP mocked).
* ``InformationProvider`` summarises the layer for downstream prompts.
* ``KpiProvider`` builds a LangChain prompt and parses an LLM response.

The LLM step is stubbed because we exercise the orchestration logic, not
the model itself.
"""
from unittest.mock import MagicMock, patch

import pytest

from helicalbi.model.output.KpiData import KpiSchema
from helicalbi.service.agentservice.AgentLayerHelper import AgentLayerHelper
from helicalbi.service.agentservice.InformationProvider import InformationProvider
from helicalbi.service.kpi.KpiProvider import KpiProvider


pytestmark = pytest.mark.integration


class TestAgentToKpiFlow:
    def test_pipeline_produces_kpis(self, session_cookie, sample_agent_data):
        agent_payload = {
            "status": 1,
            "response": {
                "data": {
                    "state": sample_agent_data,
                    "metadata": {
                        "location": "/meta",
                        "metadataFileName": "metadata.json",
                    },
                }
            }
        }
        with patch(
            "helicalbi.service.agentservice.AgentLayerHelper.fetch_service_api",
            return_value=agent_payload,
        ):
            helper = AgentLayerHelper(session_cookie, "agent.json", "/agents")

        assert helper.get_metadata_layerfile() == "metadata.json"

        info = InformationProvider(agent_data=helper.get_agent_semantic_layer())
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

        fake_chain = MagicMock()
        fake_chain.invoke.return_value = kpi_response

        with patch("helicalbi.service.kpi.KpiProvider.PromptTemplate") as pt, patch(
            "helicalbi.service.kpi.KpiProvider.PydanticOutputParser"
        ) as parser_cls:
            prompt = MagicMock()
            pt.return_value = prompt
            parser = MagicMock()
            parser_cls.return_value = parser
            parser.get_format_instructions.return_value = "FMT"

            llm_pipe = MagicMock()
            prompt.__or__.return_value = llm_pipe
            llm_pipe.__or__.return_value = fake_chain

            provider = KpiProvider(
                helper.get_agent_semantic_layer(), "Sales Operation"
            )
            kpis = provider.top_kpis()

        assert kpis == ["Revenue by region", "Bookings count"]
        invoked = fake_chain.invoke.call_args.args[0]
        assert invoked["user_query"]
        assert invoked["mapping_string"]
