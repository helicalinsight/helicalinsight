"""Functional tests for ``helicalbi.service.kpi.KpiProvider``."""
from unittest.mock import MagicMock, patch

import pytest

from helicalbi.model.output.KpiData import KpiSchema
from helicalbi.service.kpi.KpiProvider import KpiProvider


pytestmark = pytest.mark.functional


class TestKpiProvider:
    def test_top_kpis_invokes_chain_and_returns_answer(self, sample_model_data):
        provider = KpiProvider(sample_model_data, "Sales Operation", user_query="q")

        fake_response = KpiSchema(
            answer=["kpi-1", "kpi-2", "kpi-3"], reason="based on tables"
        )
        usage = {"input_tokens": 10, "output_tokens": 5, "total_tokens": 15}
        usage_obj = MagicMock()
        usage_obj.model_dump.return_value = usage

        with patch(
            "helicalbi.service.kpi.KpiProvider.PromptTemplate"
        ) as mock_prompt_template, patch(
            "helicalbi.service.kpi.KpiProvider.PydanticOutputParser"
        ) as mock_parser_cls, patch(
            "helicalbi.service.kpi.KpiProvider.invoke_structured",
            return_value=(fake_response, usage_obj),
        ):
            mock_prompt_template.return_value = MagicMock()
            mock_parser = MagicMock()
            mock_parser_cls.return_value = mock_parser
            mock_parser.get_format_instructions.return_value = "FMT"

            answers, token_usage = provider.top_kpis()

        assert answers == ["kpi-1", "kpi-2", "kpi-3"]
        assert token_usage == usage
