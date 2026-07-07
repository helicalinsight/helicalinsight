"""Functional tests for the project's Pydantic output schemas."""
import pytest
from pydantic import ValidationError

from helicalbi.memory.getcontext.ContextClass import ChatItem, GraphState
from helicalbi.memory.model.LoggedinUser import ProfileItem, User
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.model.output.ColumnResponse import ColumnResponse
from helicalbi.model.output.DomainTopicReason import DomainTopicReason
from helicalbi.model.output.KpiData import KpiSchema
from helicalbi.common.LlmInvokeHelper import merge_token_usage, read_token_usage
from helicalbi.integration.ollama.OllamaTokenUsageFactory import OllamaTokenUsageFactory
from helicalbi.integration.openai.OpenAITokenUsageFactory import OpenAITokenUsageFactory
from helicalbi.model.TokenUsage import TokenUsage
from helicalbi.model.output.SqlGen import SqlGen
from helicalbi.model.output.UpdateRephrase import UpdateRephrase
from helicalbi.model.output.viz.VizResponse import (
    AntVisualizationResponse,
    ChartFillerResponse,
    VisualizationResponse,
)


pytestmark = pytest.mark.functional


class TestUpdateRephrase:
    @pytest.mark.parametrize(
        "action", ["updt_sql", "updt_viz", "updt_both", "none"]
    )
    def test_accepts_known_actions(self, action):
        model = UpdateRephrase(action=action, viz_query="v", sql_query="s")
        assert model.action == action
        assert model.viz_query == "v"
        assert model.sql_query == "s"

    def test_rejects_unknown_action(self):
        with pytest.raises(ValidationError):
            UpdateRephrase(action="rewrite", viz_query="v", sql_query="s")


class TestTokenUsage:
    @pytest.fixture
    def openai_factory(self):
        return OpenAITokenUsageFactory()

    @pytest.fixture
    def ollama_factory(self):
        return OllamaTokenUsageFactory()

    def test_derives_total_when_omitted(self):
        usage = TokenUsage(input_tokens=10, output_tokens=5)
        assert usage.total_tokens == 15

    def test_from_usage_metadata(self, openai_factory):
        usage = openai_factory.from_usage_metadata(
            {"input_tokens": 100, "output_tokens": 25, "total_tokens": 125}
        )
        assert usage.input_tokens == 100
        assert usage.output_tokens == 25
        assert usage.total_tokens == 125

    def test_from_response_metadata_openai_shape(self, openai_factory):
        usage = openai_factory.from_response_metadata(
            {"token_usage": {"prompt_tokens": 50, "completion_tokens": 10, "total_tokens": 60}}
        )
        assert usage.input_tokens == 50
        assert usage.output_tokens == 10
        assert usage.total_tokens == 60
        assert usage.total_cost is None

    def test_from_response_metadata_ollama_shape(self, ollama_factory):
        usage = ollama_factory.from_response_metadata(
            {
                "model": "deepseek-coder-v2",
                "prompt_eval_count": 559,
                "eval_count": 95,
                "done": True,
            }
        )
        assert usage.input_tokens == 559
        assert usage.output_tokens == 95
        assert usage.total_tokens == 654

    def test_from_ai_message_ollama_response_metadata(self, ollama_factory):
        class _OllamaMessage:
            usage_metadata = None
            response_metadata = {
                "model": "deepseek-coder-v2",
                "prompt_eval_count": 559,
                "eval_count": 95,
            }

        usage = ollama_factory.from_ai_message(_OllamaMessage())
        assert usage.input_tokens == 559
        assert usage.output_tokens == 95
        assert usage.total_tokens == 654

    def test_from_usage_metadata_includes_cost_when_available(self, openai_factory):
        usage = openai_factory.from_usage_metadata(
            {
                "input_tokens": 100,
                "output_tokens": 25,
                "total_tokens": 125,
                "input_cost": 0.00015,
                "output_cost": 0.000375,
            }
        )
        assert usage.input_cost == 0.00015
        assert usage.output_cost == 0.000375
        assert usage.total_cost == pytest.approx(0.000525)

    def test_from_response_metadata_includes_cost_when_available(self, openai_factory):
        usage = openai_factory.from_response_metadata(
            {
                "token_usage": {
                    "prompt_tokens": 50,
                    "completion_tokens": 10,
                    "total_tokens": 60,
                    "input_cost": 0.00005,
                    "output_cost": 0.00002,
                    "total_cost": 0.00007,
                }
            }
        )
        assert usage.total_cost == pytest.approx(0.00007)

    def test_add_aggregates_counts(self):
        total = TokenUsage(input_tokens=1, output_tokens=2) + TokenUsage(
            input_tokens=3, output_tokens=4
        )
        assert total.input_tokens == 4
        assert total.output_tokens == 6
        assert total.total_tokens == 10
        assert total.total_cost is None

    def test_add_aggregates_cost_when_available(self):
        total = TokenUsage(input_cost=0.001, output_cost=0.002, total_cost=0.003) + TokenUsage(
            input_cost=0.004, output_cost=0.006, total_cost=0.01
        )
        assert total.input_cost == pytest.approx(0.005)
        assert total.output_cost == pytest.approx(0.008)
        assert total.total_cost == pytest.approx(0.013)

    def test_merge_token_usage_in_state(self):
        state = {}
        merge_token_usage(state, TokenUsage(input_tokens=10, output_tokens=5))
        merge_token_usage(state, TokenUsage(input_tokens=3, output_tokens=2))
        usage = read_token_usage(state)
        assert usage.input_tokens == 13
        assert usage.output_tokens == 7
        assert usage.total_tokens == 20


class TestChatResponse:
    def test_includes_token_usage_from_agent_state(self):
        response = ChatResponse.from_agent_state(
            {
                "token_usage": {
                    "input_tokens": 100,
                    "output_tokens": 20,
                    "total_tokens": 120,
                    "total_cost": 0.00042,
                }
            }
        )
        assert response.token_usage.input_tokens == 100
        assert response.token_usage.output_tokens == 20
        assert response.token_usage.total_tokens == 120
        assert response.token_usage.total_cost == pytest.approx(0.00042)


class TestSqlGen:
    def test_valid_payload(self):
        m = SqlGen(sql="SELECT 1", reason="trivial")
        assert m.sql == "SELECT 1"

    def test_missing_field_raises(self):
        with pytest.raises(ValidationError):
            SqlGen(sql="SELECT 1")  # missing reason


class TestColumnResponse:
    def test_accepts_list_of_strings(self):
        m = ColumnResponse(columnName=["t.a", "t.b"], reason="x")
        assert m.columnName == ["t.a", "t.b"]


class TestKpiSchema:
    def test_answers_must_be_list(self):
        m = KpiSchema(answer=["kpi1", "kpi2"], reason="ok")
        assert m.answer == ["kpi1", "kpi2"]


class TestDomainTopicReason:
    def test_accepts_lists(self):
        m = DomainTopicReason(
            domain=["Sales Operation"], topics=["Travel"], reason="match"
        )
        assert m.domain == ["Sales Operation"]
        assert m.topics == ["Travel"]


class TestVisualizationResponses:
    def test_visualization_response(self):
        m = VisualizationResponse(
            visualization_type="bar",
            visualization_title="Sales",
            reason="aggregation needed",
        )
        assert m.visualization_type == "bar"

    def test_ant_visualization_response(self):
        m = AntVisualizationResponse(
            visualization_type="Column",
            visualization_title="Sales",
            reason="aggregation",
        )
        assert m.visualization_title == "Sales"

    def test_chart_filler_response(self):
        m = ChartFillerResponse(js_func_string="function(){}")
        assert m.js_func_string == "function(){}"


class TestLoggedInUser:
    def test_user_model_round_trip(self):
        user = User(
            userid=1,
            username="ada",
            organization="orgX",
            role=["admin"],
            profile=[ProfileItem(key="lang", value="en")],
        )
        assert user.userid == 1
        assert user.profile[0].key == "lang"

    def test_missing_required_field_raises(self):
        with pytest.raises(ValidationError):
            User(userid=1, username="ada", organization="x", role=["admin"])


class TestGraphState:
    def test_minimum_required_fields(self):
        s = GraphState(user_query="hi", chat_history=[])
        assert s.user_query == "hi"
        assert s.query_type is None

    def test_invalid_query_type_rejected(self):
        with pytest.raises(ValidationError):
            GraphState(
                user_query="hi",
                chat_history=[],
                query_type="OTHER",
            )

    def test_chat_history_accepts_chat_items(self):
        s = GraphState(
            user_query="hi",
            chat_history=[ChatItem(question="q")],
        )
        assert len(s.chat_history) == 1
        assert s.chat_history[0].question == "q"
