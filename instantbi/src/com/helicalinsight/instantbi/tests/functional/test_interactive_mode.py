"""Tests for interactive mode (fast | think) helpers and hook."""

from unittest.mock import MagicMock

import pytest

from helicalbi.interactive.modes import (
    DEFAULT_INTERACTIVE_MODE,
    INTERACTIVE_MODE_AUTO,
    INTERACTIVE_MODE_FAST,
    INTERACTIVE_MODE_THINK,
    apply_requested_mode,
    is_auto_mode,
    is_think_mode,
    normalize_interactive_mode,
    resolve_interactive_mode_from_input,
)

pytestmark = pytest.mark.functional


def test_normalize_interactive_mode_defaults_to_fast():
    assert normalize_interactive_mode(None) == INTERACTIVE_MODE_FAST
    assert normalize_interactive_mode("") == INTERACTIVE_MODE_FAST
    assert normalize_interactive_mode("FAST") == INTERACTIVE_MODE_FAST
    assert normalize_interactive_mode("think") == INTERACTIVE_MODE_THINK
    assert normalize_interactive_mode("agentic") == INTERACTIVE_MODE_THINK
    assert normalize_interactive_mode("auto") == INTERACTIVE_MODE_AUTO
    assert normalize_interactive_mode("smart") == INTERACTIVE_MODE_AUTO
    assert normalize_interactive_mode("unknown") == DEFAULT_INTERACTIVE_MODE


def test_is_think_mode():
    assert is_think_mode("think") is True
    assert is_think_mode("fast") is False
    assert is_think_mode("auto") is False
    assert is_think_mode(None) is False


def test_is_auto_mode():
    assert is_auto_mode("auto") is True
    assert is_auto_mode("automatic") is True
    assert is_auto_mode("think") is False
    assert is_auto_mode(None) is False


def test_resolve_interactive_mode_from_input():
    assert resolve_interactive_mode_from_input({"mode": "think"}) == INTERACTIVE_MODE_THINK
    assert resolve_interactive_mode_from_input({}, {"mode": "think"}) == INTERACTIVE_MODE_THINK
    assert resolve_interactive_mode_from_input({"interactive_mode": "fast"}) == INTERACTIVE_MODE_FAST
    assert resolve_interactive_mode_from_input({}) == INTERACTIVE_MODE_FAST
    assert resolve_interactive_mode_from_input({"mode": "auto"}) == INTERACTIVE_MODE_AUTO
    assert (
        resolve_interactive_mode_from_input({}, default=INTERACTIVE_MODE_AUTO)
        == INTERACTIVE_MODE_AUTO
    )


def test_run_think_mode_executes_text_only_without_viz():
    from helicalbi.interactive.think_flow import ThinkCitedAnswer, run_think_mode

    plan_payload = {
        "phase": "plan",
        "plan": {
            "charts": [
                {"title": "KPI", "question": "What is total cost?", "viz_hint": "kpi"},
                {"title": "Trend", "question": "Cost by month?", "viz_hint": "line"},
                {"title": "Type", "question": "Cost by travel type?", "viz_hint": "bar"},
            ],
            "rationale": "Break down travel cost.",
        },
        "asked_questions": [
            "What is total cost?",
            "Cost by month?",
            "Cost by travel type?",
        ],
        "persona": {"name": "analyst"},
        "strategy": "mece_drivers",
        "mode": {"name": "balanced"},
        "token_usage": {"input_tokens": 10, "output_tokens": 5, "total_tokens": 15},
        "investigation_steps": [],
    }

    exec_result = {
        "original_question": "Analyze travel cost",
        "final_answer": "Total cost is 120k; Domestic drives most of spend.",
        "collected_data": [
            {
                "sub_question": "What is total cost?",
                "title": "KPI",
                "answer": "The travel cost is 120,000.",
                "analysis": "Total travel cost is 120,000 for the selected period.",
                "include_in_dashboard": False,
            },
            {
                "sub_question": "Cost by month?",
                "title": "Trend",
                "answer": "Spend peaked in March.",
                "analysis": "Spend peaked in March across the trend.",
                "include_in_dashboard": False,
            },
            {
                "sub_question": "Cost by travel type?",
                "title": "Type",
                "answer": "Domestic is the largest share.",
                "analysis": "Domestic is the largest share of travel cost.",
                "include_in_dashboard": False,
            },
        ],
        "asked_questions": [
            "What is total cost?",
            "Cost by month?",
            "Cost by travel type?",
        ],
        "token_usage": {"input_tokens": 20, "output_tokens": 8, "total_tokens": 28},
        "tool_loop_count": 6,
        "investigation_steps": [],
    }

    plan_mock = MagicMock(return_value=plan_payload)
    exec_mock = MagicMock(return_value=exec_result)
    load_mock = MagicMock(return_value={"status": "planned", "plan": plan_payload["plan"]})
    save_mock = MagicMock()
    progress = []

    payload = run_think_mode(
        "Analyze travel cost",
        session_cookie="sess",
        username="u1",
        model_file_name="model.json",
        model_location="/models",
        thread_id="chat-1",
        chat_seq_id="1",
        on_progress=lambda stage, status, message: progress.append((stage, message)),
        _create_and_store_plan=plan_mock,
        _run_dashboard_agent=exec_mock,
        _load_plan=load_mock,
        _save_plan=save_mock,
        _synthesize_final_answer=lambda *args, **kwargs: ThinkCitedAnswer(
            final_answer="Total cost is 120k driven by Domestic (Question 1, Question 3).",
            cited_question_indexes=[1, 3],
        ),
        _synthesize_opening_insight=lambda *args, **kwargs: "This looks at travel spend from a few angles.",
    )

    assert payload["mode"] == INTERACTIVE_MODE_THINK
    assert payload["phase"] == "execute"
    assert payload["asked_questions"] == [
        "What is total cost?",
        "Cost by month?",
        "Cost by travel type?",
    ]
    assert payload["chat_responses"] == []
    assert payload["chat_response"] == {}
    assert payload["cited_question_indexes"] == [1, 3]
    assert "120k" in payload["final_answer"] or "Domestic" in payload["final_answer"]
    assert len(payload["question_history"]) == 3
    assert payload["question_history"][0]["analysis"] == "Total travel cost is 120,000 for the selected period."
    assert payload["question_history"][0]["answer"] == "The travel cost is 120,000."
    assert payload["question_history"][0]["index"] == 1
    assert payload["opening_insight"] == "This looks at travel spend from a few angles."
    plan_mock.assert_called_once()
    exec_mock.assert_called_once()
    exec_kwargs = exec_mock.call_args.kwargs
    assert exec_kwargs.get("build_dashboard") is True
    for chart in exec_kwargs.get("investigation_plan", {}).get("charts") or []:
        assert chart.get("include_in_dashboard") is False
    save_mock.assert_called_once()
    assert any(stage == "think_execute" for stage, _msg in progress)
    intro_idx = next(i for i, (stage, _msg) in enumerate(progress) if stage == "think_intro")
    question_idx = next(i for i, (stage, _msg) in enumerate(progress) if stage == "think_question")
    assert intro_idx < question_idx
    assert "llm_activity_details" not in payload


def test_think_success_prompt_uses_same_parameters_as_fast_mode():
    from helicalbi.prompt.SqlSuccessPrompty import (
        success_prompt_formatted,
        think_success_prompt_formatted,
    )
    from helicalbi.interactive.think_flow import (
        _opening_metadata,
        _opening_sql_context,
        synthesize_think_opening_insight,
    )

    assert set(success_prompt_formatted.input_variables) == {"user_query", "sql_query", "metadata"}
    assert set(think_success_prompt_formatted.input_variables) == {"user_query", "sql_query", "metadata"}

    asked = ["What is total cost?", "Cost by month?"]
    plan = {"rationale": "Break down travel cost.", "domain": "Travel", "topics": ["Travel Cost"]}
    payload = {
        "selected_domains": ["Travel"],
        "selected_topics": ["Travel Cost"],
        "selected_tables": ["travel_details"],
    }
    sql_query = _opening_sql_context(asked, plan)
    metadata = _opening_metadata(plan, payload)
    rendered = think_success_prompt_formatted.format(
        user_query="Analyze travel cost",
        sql_query=sql_query,
        metadata=__import__("json").dumps(metadata),
    )
    assert "Analyze travel cost" in rendered
    assert "What is total cost?" in sql_query
    assert metadata["required_tables"] == ["travel_details"]
    assert metadata["domain"] == ["Travel"]
    assert metadata["topics"] == ["Travel Cost"]

    class _Msg:
        content = "This investigation looks at travel spend."

    insight = synthesize_think_opening_insight(
        "Analyze travel cost",
        asked,
        plan=plan,
        plan_payload=payload,
        _invoke_llm=lambda formatted, state=None: (_Msg(), None),
    )
    assert insight == "This investigation looks at travel spend."


def test_run_think_mode_includes_llm_activity_details_when_enabled():
    from helicalbi.common import app_config
    from helicalbi.interactive.think_flow import ThinkCitedAnswer, run_think_mode

    plan_payload = {
        "phase": "plan",
        "plan": {
            "charts": [
                {
                    "title": "KPI",
                    "question": "What is total cost?",
                    "viz_hint": "kpi",
                    "purpose": "Establish headline spend.",
                    "topic": "Travel Cost",
                    "components": ["Travel Cost"],
                },
            ],
            "rationale": "Break down travel cost.",
            "domain": "Travel",
            "topics": ["Travel Cost"],
        },
        "asked_questions": ["What is total cost?"],
        "persona": {"name": "analyst"},
        "strategy": "mece_drivers",
        "strategy_detail": {
            "id": "mece_drivers",
            "selection": {
                "source": "decision_tree",
                "intent": "overview",
                "persona": "analyst",
                "strategy_id": "mece_drivers",
            },
            "reason": "Decision tree selected strategy 'mece_drivers'; intent=overview; persona=analyst.",
        },
        "selected_domains": ["Travel"],
        "selected_topics": ["Travel Cost"],
        "selected_tables": ["travel_details"],
        "plan_activity_trace": [
            {
                "node": "select_domain_topics",
                "stage": "plan_graph",
                "status": "done",
                "result": {"domains": ["Travel"], "topics": ["Travel Cost"]},
                "reason": "Selected Travel / Travel Cost.",
            }
        ],
        "mode": {"name": "balanced"},
        "token_usage": {},
        "investigation_steps": [],
    }
    exec_result = {
        "original_question": "Analyze travel cost",
        "final_answer": "",
        "collected_data": [
            {
                "sub_question": "What is total cost?",
                "title": "KPI",
                "answer": "The travel cost is 120,000.",
                "analysis": "Total travel cost is 120,000.",
                "include_in_dashboard": False,
            }
        ],
        "asked_questions": ["What is total cost?"],
        "token_usage": {},
        "tool_loop_count": 2,
        "investigation_steps": [],
        "activity_trace": [
            {
                "node": "execute_plan",
                "stage": "execute_graph",
                "status": "done",
                "result": {
                    "charts": [
                        {
                            "index": 1,
                            "question": "What is total cost?",
                            "status": "ok",
                            "reason": "Establish headline spend.",
                        }
                    ]
                },
                "reason": "Executed 1 planned question(s).",
            }
        ],
    }

    original = getattr(app_config, "show_llm_activity_details", False)
    try:
        app_config.show_llm_activity_details = True
        payload = run_think_mode(
            "Analyze travel cost",
            session_cookie="sess",
            username="u1",
            model_file_name="model.json",
            model_location="/models",
            thread_id="chat-1",
            chat_seq_id="1",
            _create_and_store_plan=MagicMock(return_value=plan_payload),
            _run_dashboard_agent=MagicMock(return_value=exec_result),
            _load_plan=MagicMock(return_value={"status": "planned", "plan": plan_payload["plan"]}),
            _save_plan=MagicMock(),
            _synthesize_final_answer=lambda *args, **kwargs: ThinkCitedAnswer(
                final_answer="Total cost is 120k (Question 1).",
                cited_question_indexes=[1],
            ),
            _synthesize_opening_insight=lambda *args, **kwargs: "Travel cost overview.",
        )
    finally:
        app_config.show_llm_activity_details = original

    details = payload["llm_activity_details"]
    assert details["domains"] == ["Travel"]
    assert details["topics"] == ["Travel Cost"]
    assert details["tables"] == ["travel_details"]
    assert details["strategy"]["id"] == "mece_drivers"
    assert details["strategy"]["selection"]["source"] == "decision_tree"
    assert details["questions"][0]["question"] == "What is total cost?"
    assert "headline" in details["questions"][0]["why"].lower()
    assert any(n.get("node") == "select_domain_topics" for n in details["plan_graph"])
    assert any(n.get("node") == "execute_plan" for n in details["execute_graph"])
    assert any(step.get("stage") == "strategy" for step in details["path"])


def test_run_think_mode_request_override_enables_details():
    from helicalbi.common import app_config
    from helicalbi.interactive.think_flow import ThinkCitedAnswer, run_think_mode

    plan_payload = {
        "phase": "plan",
        "plan": {"charts": [{"title": "KPI", "question": "What is total cost?"}]},
        "asked_questions": ["What is total cost?"],
        "persona": {"name": "analyst"},
        "strategy": "mece_drivers",
        "strategy_detail": {"id": "mece_drivers", "selection": {"source": "hint"}},
        "selected_domains": [],
        "selected_topics": [],
        "selected_tables": [],
        "plan_activity_trace": [],
        "mode": {"name": "balanced"},
        "token_usage": {},
        "investigation_steps": [],
    }
    exec_result = {
        "original_question": "q",
        "final_answer": "",
        "collected_data": [
            {
                "sub_question": "What is total cost?",
                "analysis": "120k",
                "answer": "120k",
            }
        ],
        "asked_questions": ["What is total cost?"],
        "token_usage": {},
        "activity_trace": [],
    }
    original = getattr(app_config, "show_llm_activity_details", False)
    try:
        app_config.show_llm_activity_details = False
        payload = run_think_mode(
            "q",
            session_cookie="sess",
            username="u1",
            model_file_name="model.json",
            model_location="/models",
            thread_id="chat-1",
            show_llm_activity_details=True,
            _create_and_store_plan=MagicMock(return_value=plan_payload),
            _run_dashboard_agent=MagicMock(return_value=exec_result),
            _load_plan=MagicMock(return_value={}),
            _save_plan=MagicMock(),
            _synthesize_final_answer=lambda *a, **k: ThinkCitedAnswer(
                final_answer="ok", cited_question_indexes=[1]
            ),
            _synthesize_opening_insight=lambda *a, **k: "Overview.",
        )
    finally:
        app_config.show_llm_activity_details = original

    assert "llm_activity_details" in payload


def test_heuristic_interactive_mode_routes_simple_and_complex_questions():
    from helicalbi.interactive.auto_mode import heuristic_interactive_mode

    assert heuristic_interactive_mode("total travel cost") == INTERACTIVE_MODE_FAST
    assert heuristic_interactive_mode("bookings by month") == INTERACTIVE_MODE_FAST
    assert heuristic_interactive_mode("why did travel cost increase?") == INTERACTIVE_MODE_THINK
    assert heuristic_interactive_mode("compare regions and explain drivers") == INTERACTIVE_MODE_THINK


def test_classify_interactive_mode_uses_llm_then_heuristic():
    from helicalbi.interactive.auto_mode import classify_interactive_mode

    class Choice:
        mode = INTERACTIVE_MODE_THINK
        complexity = "complex"
        reason = "multi-angle why"

    assert classify_interactive_mode(
        "why did cost rise", invoke=lambda *a, **k: Choice()
    ) == INTERACTIVE_MODE_THINK

    def _fail(*_a, **_k):
        raise RuntimeError("down")

    assert classify_interactive_mode(
        "total travel cost", invoke=_fail
    ) == INTERACTIVE_MODE_FAST


def test_bind_routed_interactive_mode_rewrites_auto_to_fast_or_think():
    from helicalbi.interactive.auto_mode import bind_routed_interactive_mode

    class Choice:
        mode = INTERACTIVE_MODE_FAST
        complexity = "simple"
        reason = "one metric"

    data = {"input": {"inputString": "total travel cost", "mode": "auto"}}
    requested, routed = bind_routed_interactive_mode(
        data, invoke=lambda *a, **k: Choice()
    )
    assert requested == INTERACTIVE_MODE_AUTO
    assert routed == INTERACTIVE_MODE_FAST
    assert data["input"]["mode"] == INTERACTIVE_MODE_FAST
    assert data["input"]["requested_mode"] == INTERACTIVE_MODE_AUTO

    think_data = {"input": {"inputString": "q", "mode": "think"}}
    requested, routed = bind_routed_interactive_mode(
        think_data, invoke=lambda *a, **k: Choice()
    )
    assert requested == INTERACTIVE_MODE_THINK
    assert routed == INTERACTIVE_MODE_THINK
    assert "requested_mode" not in think_data["input"]


def test_apply_requested_mode_keeps_auto_on_response():
    stamped = apply_requested_mode(
        {"mode": INTERACTIVE_MODE_THINK, "final_answer": "ok"},
        {"requested_mode": INTERACTIVE_MODE_AUTO},
    )
    assert stamped["mode"] == INTERACTIVE_MODE_THINK
    assert stamped["requested_mode"] == INTERACTIVE_MODE_AUTO
    unstamped = apply_requested_mode(
        {"mode": INTERACTIVE_MODE_FAST},
        {"mode": INTERACTIVE_MODE_FAST},
    )
    assert "requested_mode" not in unstamped
