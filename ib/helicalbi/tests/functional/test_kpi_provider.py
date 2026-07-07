"""Functional tests for ``helicalbi.service.kpi.KpiProvider``."""
from unittest.mock import MagicMock, patch

import pytest

from helicalbi.model.output.KpiData import KpiSchema
from helicalbi.service.kpi.KpiProvider import KpiProvider


pytestmark = pytest.mark.functional


class TestKpiProvider:
    def test_top_kpis_invokes_chain_and_returns_answer(self, sample_agent_data):
        provider = KpiProvider(sample_agent_data, "Sales Operation", user_query="q")

        fake_response = KpiSchema(
            answer=["kpi-1", "kpi-2", "kpi-3"], reason="based on tables"
        )
        fake_chain = MagicMock()
        fake_chain.invoke.return_value = fake_response

        with patch(
            "helicalbi.service.kpi.KpiProvider.PromptTemplate"
        ) as mock_prompt_template, patch(
            "helicalbi.service.kpi.KpiProvider.PydanticOutputParser"
        ) as mock_parser_cls:
            mock_prompt = MagicMock()
            mock_prompt_template.return_value = mock_prompt
            mock_parser = MagicMock()
            mock_parser_cls.return_value = mock_parser
            mock_parser.get_format_instructions.return_value = "FMT"

            # prompt | llm returns an object; that | parser returns our chain
            llm_pipe = MagicMock()
            mock_prompt.__or__.return_value = llm_pipe
            llm_pipe.__or__.return_value = fake_chain

            result = provider.top_kpis()

        assert result == ["kpi-1", "kpi-2", "kpi-3"]
        fake_chain.invoke.assert_called_once()
        invoked_args = fake_chain.invoke.call_args.args[0]
        assert invoked_args["user_query"] == "q"
        assert "domain_topic_string" in invoked_args
        assert "semantic_string" in invoked_args
