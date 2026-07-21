"""Functional tests for the project's Pydantic output schemas."""
import pytest
from pydantic import ValidationError

from helicalbi.memory.getcontext.ContextClass import ChatItem, GraphState
from helicalbi.memory.model.LoggedinUser import ProfileItem, User
from helicalbi.model.output.ChatResponse import ChatResponse
from helicalbi.model.output.ColumnResponse import ColumnResponse
from helicalbi.model.output.DomainTopicReason import DomainTopicReason
from helicalbi.model.output.KpiData import KpiSchema
from helicalbi.common.LlmInvokeHelper import merge_time_consumed, merge_token_usage, read_time_consumed, read_token_usage
from helicalbi.integration.ollama.OllamaTokenUsageFactory import OllamaTokenUsageFactory
from helicalbi.integration.openai.OpenAITokenUsageFactory import OpenAITokenUsageFactory
from helicalbi.integration.anthropic.AnthropicTokenUsageFactory import AnthropicTokenUsageFactory
from helicalbi.integration.gemeni.GeminiTokenUsageFactory import GeminiTokenUsageFactory
from helicalbi.model.TimeConsumed import TimeConsumed
from helicalbi.model.TokenUsage import TokenUsage
from helicalbi.model.output.SqlGen import SqlGen, get_sql_gen_model
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

    @pytest.fixture
    def anthropic_factory(self):
        return AnthropicTokenUsageFactory()

    @pytest.fixture
    def gemini_factory(self):
        return GeminiTokenUsageFactory()

    def test_derives_total_when_omitted(self):
        usage = TokenUsage(input_tokens=10, output_tokens=5)
        assert usage.total_tokens == 15

    def test_from_usage_metadata(self, openai_factory):
        usage = openai_factory.from_usage_metadata(
            {
                "input_tokens": 100,
                "output_tokens": 25,
                "total_tokens": 125,
                "model_name": "gpt-4o-mini",
            }
        )
        assert usage.input_tokens == 100
        assert usage.output_tokens == 25
        assert usage.total_tokens == 125
        assert usage.model_name == "gpt-4o-mini"

    def test_from_response_metadata_openai_shape(self, openai_factory):
        usage = openai_factory.from_response_metadata(
            {
                "model_name": "gpt-4o",
                "token_usage": {"prompt_tokens": 50, "completion_tokens": 10, "total_tokens": 60},
            }
        )
        assert usage.input_tokens == 50
        assert usage.output_tokens == 10
        assert usage.total_tokens == 60
        assert usage.total_cost is None
        assert usage.model_name == "gpt-4o"

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
        assert usage.model_name == "deepseek-coder-v2"

    def test_from_response_metadata_anthropic_shape(self, anthropic_factory):
        usage = anthropic_factory.from_response_metadata(
            {
                "model": "claude-opus-4-6",
                "usage": {"input_tokens": 120, "output_tokens": 30},
            }
        )
        assert usage.input_tokens == 120
        assert usage.output_tokens == 30
        assert usage.total_tokens == 150
        assert usage.model_name == "claude-opus-4-6"

    def test_from_response_metadata_gemini_shape(self, gemini_factory):
        usage = gemini_factory.from_response_metadata(
            {
                "model_name": "gemini-2.5-flash",
                "token_usage": {"prompt_tokens": 40, "completion_tokens": 10, "total_tokens": 50},
            }
        )
        assert usage.input_tokens == 40
        assert usage.output_tokens == 10
        assert usage.total_tokens == 50
        assert usage.model_name == "gemini-2.5-flash"

    def test_from_ai_message_gemini_usage_and_response_metadata(self, gemini_factory):
        class _GeminiMessage:
            usage_metadata = {
                "input_tokens": 11,
                "output_tokens": 997,
                "total_tokens": 1008,
                "input_token_details": {"cache_read": 0},
                "output_token_details": {"reasoning": 850},
            }
            response_metadata = {
                "finish_reason": "STOP",
                "model_name": "gemini-2.5-flash",
                "safety_ratings": [],
                "model_provider": "google_genai",
            }

        usage = gemini_factory.from_ai_message(_GeminiMessage())
        assert usage.input_tokens == 11
        assert usage.output_tokens == 997
        assert usage.total_tokens == 1008
        assert usage.model_name == "gemini-2.5-flash"

    def test_from_ai_message_anthropic_usage_and_response_metadata(self, anthropic_factory):
        class _AnthropicMessage:
            usage_metadata = {
                "input_tokens": 19,
                "output_tokens": 177,
                "total_tokens": 196,
                "input_token_details": {
                    "cache_read": 0,
                    "cache_creation": 0,
                    "ephemeral_5m_input_tokens": 0,
                    "ephemeral_1h_input_tokens": 0,
                },
            }
            response_metadata = {
                "id": "msg_011CcqxMCbcbkixwetvXFxVv",
                "model": "claude-sonnet-4-6",
                "usage": {
                    "cache_creation": {
                        "ephemeral_1h_input_tokens": 0,
                        "ephemeral_5m_input_tokens": 0,
                    },
                    "cache_creation_input_tokens": 0,
                    "cache_read_input_tokens": 0,
                    "input_tokens": 19,
                    "output_tokens": 177,
                },
                "model_name": "claude-sonnet-4-6",
                "model_provider": "anthropic",
            }

        usage = anthropic_factory.from_ai_message(_AnthropicMessage())
        assert usage.input_tokens == 19
        assert usage.output_tokens == 177
        assert usage.total_tokens == 196
        assert usage.model_name == "claude-sonnet-4-6"

    def test_from_ai_message_openai_usage_and_response_metadata(self, openai_factory):
        class _OpenAIMessage:
            usage_metadata = {
                "input_tokens": 15,
                "output_tokens": 109,
                "total_tokens": 124,
                "input_token_details": {"audio": 0, "cache_read": 0},
                "output_token_details": {"audio": 0, "reasoning": 0},
            }
            response_metadata = {
                "token_usage": {
                    "prompt_tokens": 15,
                    "completion_tokens": 109,
                    "total_tokens": 124,
                    "prompt_tokens_details": {"audio_tokens": 0, "cached_tokens": 0},
                    "completion_tokens_details": {
                        "accepted_prediction_tokens": 0,
                        "audio_tokens": 0,
                        "reasoning_tokens": 0,
                        "rejected_prediction_tokens": 0,
                    },
                },
                "model_provider": "openai",
                "model_name": "gpt-4o-mini-2024-07-18",
            }

        usage = openai_factory.from_ai_message(_OpenAIMessage())
        assert usage.input_tokens == 15
        assert usage.output_tokens == 109
        assert usage.total_tokens == 124
        assert usage.model_name == "gpt-4o-mini-2024-07-18"

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
        assert usage.model_name == "deepseek-coder-v2"

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
        total = TokenUsage(input_tokens=1, output_tokens=2, model_name="gpt-4o-mini") + TokenUsage(
            input_tokens=3, output_tokens=4, model_name="gpt-4o-mini"
        )
        assert total.input_tokens == 4
        assert total.output_tokens == 6
        assert total.total_tokens == 10
        assert total.total_cost is None
        assert total.model_name == "gpt-4o-mini"

    def test_add_preserves_model_name_when_other_side_missing(self):
        total = TokenUsage(input_tokens=1, output_tokens=2, model_name="gpt-4o-mini") + TokenUsage(
            input_tokens=3, output_tokens=4
        )
        assert total.model_name == "gpt-4o-mini"

    def test_add_aggregates_cost_when_available(self):
        total = TokenUsage(input_cost=0.001, output_cost=0.002, total_cost=0.003) + TokenUsage(
            input_cost=0.004, output_cost=0.006, total_cost=0.01
        )
        assert total.input_cost == pytest.approx(0.005)
        assert total.output_cost == pytest.approx(0.008)
        assert total.total_cost == pytest.approx(0.013)

    def test_merge_token_usage_in_state(self):
        state = {}
        merge_token_usage(state, TokenUsage(input_tokens=10, output_tokens=5, model_name="gpt-4o-mini"))
        merge_token_usage(state, TokenUsage(input_tokens=3, output_tokens=2, model_name="gpt-4o-mini"))
        usage = read_token_usage(state)
        assert usage.input_tokens == 13
        assert usage.output_tokens == 7
        assert usage.total_tokens == 20
        assert usage.model_name == "gpt-4o-mini"


class TestTimeConsumed:
    def test_add_aggregates_llm_seconds(self):
        total = TimeConsumed(llm_seconds=1.2) + TimeConsumed(llm_seconds=0.8)
        assert total.llm_seconds == pytest.approx(2.0)

    def test_merge_time_consumed_in_state(self):
        state = {}
        merge_time_consumed(state, TimeConsumed(llm_seconds=1.5))
        merge_time_consumed(state, TimeConsumed(llm_seconds=0.5))
        consumed = read_time_consumed(state)
        assert consumed.llm_seconds == pytest.approx(2.0)

    def test_set_total_time_in_state(self):
        from helicalbi.common.LlmInvokeHelper import set_total_time_consumed

        state = {"time_consumed": {"llm_seconds": 1.25}}
        set_total_time_consumed(state, 3.456)
        consumed = read_time_consumed(state)
        assert consumed.llm_seconds == pytest.approx(1.25)
        assert consumed.total_seconds == pytest.approx(3.456)


class TestChatResponse:
    def test_includes_token_usage_from_model_state(self):
        response = ChatResponse.from_model_state(
            {
                "token_usage": {
                    "input_tokens": 100,
                    "output_tokens": 20,
                    "total_tokens": 120,
                    "total_cost": 0.00042,
                    "model_name": "gpt-4o-mini",
                },
                "time_consumed": {
                    "llm_seconds": 2.5,
                    "total_seconds": 8.75,
                },
            }
        )
        assert response.token_usage.input_tokens == 100
        assert response.token_usage.output_tokens == 20
        assert response.token_usage.total_tokens == 120
        assert response.token_usage.total_cost == pytest.approx(0.00042)
        assert response.token_usage.model_name == "gpt-4o-mini"
        assert response.time_consumed.llm_seconds == pytest.approx(2.5)
        assert response.time_consumed.total_seconds == pytest.approx(8.75)

    def test_includes_required_cube_info_from_sql_model(self):
        response = ChatResponse.from_model_state(
            {
                "sqlModel": {
                    "required_cube_info": {
                        "picked_dimensions": ["booking platform"],
                        "picked_metrics": ["cost of travel"],
                    }
                }
            }
        )
        assert response.sql.required_cube_info == {
            "picked_dimensions": ["booking platform"],
            "picked_metrics": ["cost of travel"],
        }


class TestSqlGen:
    def test_valid_payload_with_reason_when_flag_disabled(self, monkeypatch):
        from helicalbi.common import app_config

        monkeypatch.setattr(app_config, "hide_prompt_reason", False)
        model = get_sql_gen_model()
        m = model(sql="SELECT 1", reason="trivial")
        assert m.sql == "SELECT 1"
        assert m.reason == "trivial"

    def test_missing_reason_raises_when_flag_disabled(self, monkeypatch):
        from helicalbi.common import app_config

        monkeypatch.setattr(app_config, "hide_prompt_reason", False)
        model = get_sql_gen_model()
        with pytest.raises(ValidationError):
            model(sql="SELECT 1")


class TestColumnResponse:
    def test_accepts_list_of_strings(self):
        m = ColumnResponse(columnName=["t.a", "t.b"], reason="x")
        assert m.columnName == ["t.a", "t.b"]

    def test_accepts_picked_dimensions_and_metrics(self):
        m = ColumnResponse(
            columnName=["travel_details.booking_platform"],
            pickedDimensions=["booking platform"],
            pickedMetrics=["cost of travel"],
            reason="x",
        )
        assert m.pickedDimensions == ["booking platform"]
        assert m.pickedMetrics == ["cost of travel"]

    def test_accepts_optional_limit(self):
        m = ColumnResponse(columnName=["t.a"], reason="x", limit=10)
        assert m.limit == 10

    def test_limit_defaults_to_none(self):
        m = ColumnResponse(columnName=["t.a"], reason="x")
        assert m.limit is None


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


class TestOptionalPromptReason:
    def test_sql_gen_omits_reason_field_when_flag_enabled(self, monkeypatch):
        from helicalbi.common import app_config

        monkeypatch.setattr(app_config, "hide_prompt_reason", True)
        model = get_sql_gen_model()
        parsed = model(sql="SELECT 1")
        assert parsed.sql == "SELECT 1"
        assert "reason" not in model.model_fields

    def test_sql_gen_includes_reason_field_when_flag_disabled(self, monkeypatch):
        from helicalbi.common import app_config

        monkeypatch.setattr(app_config, "hide_prompt_reason", False)
        model = get_sql_gen_model()
        assert "reason" in model.model_fields

    def test_final_sql_prompt_omits_reason_when_flag_enabled(self, monkeypatch):
        from helicalbi.common import app_config
        from helicalbi.prompt.FinalSqlPrompt import (
            _FINAL_SQL_REASON_LINE,
            _final_sql_important_block,
        )

        monkeypatch.setattr(app_config, "hide_prompt_reason", True)
        block = _final_sql_important_block()
        assert _FINAL_SQL_REASON_LINE.strip() not in block

    def test_final_sql_prompt_includes_reason_when_flag_disabled(self, monkeypatch):
        from helicalbi.common import app_config
        from helicalbi.prompt.FinalSqlPrompt import _final_sql_important_block

        monkeypatch.setattr(app_config, "hide_prompt_reason", False)
        block = _final_sql_important_block()
        assert "highlight the same in your reason" in block

    def test_chat_response_omits_reason_fields_when_flag_enabled(self, monkeypatch):
        from helicalbi.common import app_config

        monkeypatch.setattr(app_config, "hide_prompt_reason", True)
        payload = ChatResponse.from_model_state(
            {
                "viz_reason": "bar chart fits aggregates",
                "sqlModel": {"sql_reason": "needs grouping"},
                "output2": "summary rationale",
                "output": "insight text",
            }
        ).to_dict()
        assert "vf_reason" not in payload["viz"]
        assert "reason" not in payload["sql"]
        assert "reason" not in payload["summary"]

    def test_chat_response_includes_reason_fields_when_flag_disabled(self, monkeypatch):
        from helicalbi.common import app_config

        monkeypatch.setattr(app_config, "hide_prompt_reason", False)
        payload = ChatResponse.from_model_state(
            {
                "viz_reason": "bar chart fits aggregates",
                "sqlModel": {"sql_reason": "needs grouping"},
                "output2": "summary rationale",
                "output": "insight text",
            }
        ).to_dict()
        assert payload["viz"]["vf_reason"] == "bar chart fits aggregates"
        assert payload["sql"]["reason"] == "needs grouping"
        assert payload["summary"]["reason"] == "summary rationale"


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
