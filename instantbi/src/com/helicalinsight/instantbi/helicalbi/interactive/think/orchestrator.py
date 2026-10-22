"""Think-mode pipeline orchestrator (plan → intro → execute → conclude)."""
from __future__ import annotations

import logging
from typing import Any, Optional, Sequence

from helicalbi.common import app_config
from helicalbi.interactive.think.abort import AbortGuard
from helicalbi.interactive.think.constants import (
    BUILD_DASHBOARD,
    DEFAULT_AGENT_MODE,
    ERR_CHAT_ID_REQUIRED,
    ERR_INPUT_STRING_REQUIRED,
    ERR_MODEL_REQUIRED,
    FLAT_SQL,
    MSG_ANSWER_DONE,
    MSG_EXECUTE_STARTED,
    MSG_INTRO_DONE,
    MSG_INTRO_STARTED,
    MSG_PLAN_STARTED,
    PLAN_STATUS_EXECUTED,
    ProgressCallback,
    STAGE_ANSWER,
    STAGE_EXECUTE,
    STAGE_INTRO,
    STAGE_PLAN,
    STAGE_QUESTION,
    STATUS_DONE,
    STATUS_STARTED,
)
from helicalbi.interactive.think.details import ActivityDetailsPolicy
from helicalbi.interactive.think.filters.corrector import ThinkModeFilterCorrector
from helicalbi.interactive.think.history import QuestionHistoryBuilder
from helicalbi.interactive.think.payload_builder import ThinkResultPayloadBuilder
from helicalbi.interactive.think.plan import AskedQuestionsResolver, TextOnlyPlanTransformer
from helicalbi.interactive.think.progress import ProgressEmitter
from helicalbi.interactive.think.synthesis.final_answer import FinalAnswerSynthesizer
from helicalbi.interactive.think.synthesis.opening import OpeningInsightSynthesizer
from helicalbi.interactive.think.token_usage import TokenUsageMerger
from helicalbi.sql_agent.config import DEFAULT_DASHBOARD_SUB_QUESTIONS

logger = logging.getLogger(__name__)


class ThinkModeOrchestrator:
    """Facade over think pipeline stages; injectable collaborators for tests."""

    def __init__(
        self,
        *,
        plan_transformer: Optional[TextOnlyPlanTransformer] = None,
        questions: Optional[AskedQuestionsResolver] = None,
        history: Optional[QuestionHistoryBuilder] = None,
        opening: Optional[OpeningInsightSynthesizer] = None,
        final_answer: Optional[FinalAnswerSynthesizer] = None,
        filter_corrector: Optional[ThinkModeFilterCorrector] = None,
        payload_builder: Optional[ThinkResultPayloadBuilder] = None,
        token_merger: Optional[TokenUsageMerger] = None,
        details_policy: Optional[ActivityDetailsPolicy] = None,
    ) -> None:
        self._plan_transformer = plan_transformer or TextOnlyPlanTransformer()
        self._questions = questions or AskedQuestionsResolver()
        self._history = history or QuestionHistoryBuilder()
        self._opening = opening or OpeningInsightSynthesizer()
        self._final_answer = final_answer or FinalAnswerSynthesizer()
        self._filter_corrector = filter_corrector or ThinkModeFilterCorrector()
        self._payload_builder = payload_builder or ThinkResultPayloadBuilder()
        self._token_merger = token_merger or TokenUsageMerger()
        self._details_policy = details_policy or ActivityDetailsPolicy()

    def run(
        self,
        question: str,
        *,
        session_cookie: str,
        username: str,
        model_file_name: str,
        model_location: str,
        thread_id: str,
        chat_seq_id: Any = "1",
        last_chats: Optional[list] = None,
        request_id: Optional[str] = None,
        max_sub_questions: Optional[int] = None,
        agent_mode: Optional[str] = None,
        user_role: Optional[Sequence[Any]] = None,
        user_profile: Optional[Sequence[Any]] = None,
        persona_hint: Optional[str] = None,
        strategy_hint: Optional[str] = None,
        show_llm_activity_details: Optional[Any] = None,
        on_progress: Optional[ProgressCallback] = None,
        _create_and_store_plan=None,
        _run_dashboard_agent=None,
        _load_plan=None,
        _save_plan=None,
        _synthesize_final_answer=None,
        _synthesize_opening_insight=None,
    ) -> dict[str, Any]:
        """Plan then execute investigation questions as text findings.

        Charts are marked ``include_in_dashboard=False`` so execute stores analysis +
        report_model without auto-hydrating client charts. Dashboard layout is still
        built from those report_models (positions/sizing) for the Dashboard Model tab.
        """
        create_and_store_plan, run_dashboard_agent, load_plan, save_plan = (
            self._resolve_deps(
                _create_and_store_plan,
                _run_dashboard_agent,
                _load_plan,
                _save_plan,
            )
        )
        abort = AbortGuard(request_id)
        progress = ProgressEmitter(on_progress)

        abort.ensure()
        self._validate(question, model_file_name, model_location, thread_id)

        chart_ceiling = (
            int(max_sub_questions)
            if max_sub_questions is not None
            else int(
                app_config.dashboard_max_sub_questions or DEFAULT_DASHBOARD_SUB_QUESTIONS
            )
        )
        resolved_agent_mode = str(
            agent_mode or app_config.dashboard_default_mode or DEFAULT_AGENT_MODE
        )
        include_details = self._details_policy.enabled(show_llm_activity_details)

        logger.info(
            "Interactive think mode start user=%s thread=%s seq=%s agent_mode=%s",
            username,
            thread_id,
            chat_seq_id,
            resolved_agent_mode,
        )
        progress.emit(STAGE_PLAN, STATUS_STARTED, MSG_PLAN_STARTED)

        plan_payload = create_and_store_plan(
            question,
            session_cookie=session_cookie,
            username=username,
            model_file_name=model_file_name,
            model_location=model_location,
            thread_id=thread_id,
            chat_seq_id=chat_seq_id,
            last_chats=last_chats,
            request_id=request_id,
            max_sub_questions=chart_ceiling,
            agent_mode=resolved_agent_mode,
            user_role=user_role,
            user_profile=user_profile,
            persona_hint=persona_hint,
            strategy_hint=strategy_hint,
            flat_sql=FLAT_SQL,
        )
        abort.ensure()

        plan = self._plan_transformer.transform(plan_payload.get("plan") or {})
        asked_from_plan = (
            list(plan_payload.get("asked_questions") or [])
            or self._questions.from_plan(plan)
        )
        progress.emit(
            STAGE_PLAN,
            STATUS_DONE,
            f"Prepared {len(asked_from_plan)} investigation question(s).",
        )

        opening_insight, opening_state = self._run_opening(
            question,
            asked_from_plan,
            plan=plan,
            plan_payload=plan_payload,
            progress=progress,
            synthesize_opening=_synthesize_opening_insight,
        )
        for index, asked in enumerate(asked_from_plan, start=1):
            progress.emit(STAGE_QUESTION, STATUS_DONE, f"Question {index}: {asked}")

        progress.emit(STAGE_EXECUTE, STATUS_STARTED, MSG_EXECUTE_STARTED)
        result = run_dashboard_agent(
            question,
            session_cookie=session_cookie,
            username=username,
            model_file_name=model_file_name,
            model_location=model_location,
            thread_id=thread_id,
            chat_seq_id=chat_seq_id,
            last_chats=last_chats,
            request_id=request_id,
            max_sub_questions=chart_ceiling,
            agent_mode=str(
                (plan_payload.get("mode") or {}).get("name")
                or plan_payload.get("agent_mode")
                or resolved_agent_mode
            ),
            investigation_plan=plan,
            persona=plan_payload.get("persona") or {},
            user_role=list(user_role or []),
            user_profile=list(user_profile or []),
            build_dashboard=BUILD_DASHBOARD,
            on_progress=on_progress,
            flat_sql=FLAT_SQL,
        )
        abort.ensure()
        self._mark_plan_executed(
            load_plan, save_plan, thread_id, chat_seq_id, plan
        )

        asked_questions = asked_from_plan or self._questions.from_result(result, plan)
        question_history = self._history.build(
            asked_questions,
            plan,
            result.get("collected_data") or [],
        )
        progress.emit(
            STAGE_EXECUTE,
            STATUS_DONE,
            f"Collected findings for "
            f"{sum(1 for item in question_history if item.get('analysis'))} "
            "question(s).",
        )

        synthesizer_answer = str(result.get("final_answer") or "").strip()
        synthesize = _synthesize_final_answer or self._final_answer.synthesize
        cited = synthesize(
            question,
            asked_questions,
            plan=plan,
            question_history=question_history,
            state={"token_usage": dict(result.get("token_usage") or {})},
        )
        final_answer = str(cited.final_answer or "").strip() or synthesizer_answer
        cited_indexes = list(cited.cited_question_indexes or [])
        if not cited_indexes:
            cited_indexes = [
                int(item.get("index") or 0)
                for item in question_history
                if item.get("analysis") and int(item.get("index") or 0) > 0
            ]
        progress.emit(STAGE_ANSWER, STATUS_DONE, MSG_ANSWER_DONE)

        token_usage = self._token_merger.combine(
            dict(result.get("token_usage") or {}),
            plan_payload.get("token_usage") or {},
            opening_state.get("token_usage") or {},
        )

        payload = self._payload_builder.build(
            question=question,
            asked_questions=asked_questions,
            question_history=question_history,
            cited_indexes=cited_indexes,
            opening_insight=opening_insight,
            final_answer=final_answer,
            plan=plan,
            plan_payload=plan_payload,
            result=result,
            token_usage=token_usage,
            include_details=include_details,
        )
        payload = self._filter_corrector.correct(
            payload,
            result=result,
            question_history=question_history,
        )

        logger.info(
            "Interactive think completed thread=%s questions=%s cited=%s answer_chars=%s",
            thread_id,
            len(asked_questions),
            payload["cited_question_indexes"],
            len(final_answer or ""),
        )
        return payload

    def _run_opening(
        self,
        question: str,
        asked_from_plan: list[str],
        *,
        plan: dict[str, Any],
        plan_payload: dict[str, Any],
        progress: ProgressEmitter,
        synthesize_opening,
    ) -> tuple[str, dict[str, Any]]:
        opening_state: dict[str, Any] = {"token_usage": {}}
        synthesize = synthesize_opening or self._opening.synthesize
        progress.emit(STAGE_INTRO, STATUS_STARTED, MSG_INTRO_STARTED)
        try:
            opening_insight = str(
                synthesize(
                    question,
                    asked_from_plan,
                    plan=plan,
                    plan_payload=plan_payload,
                    state=opening_state,
                )
                or ""
            ).strip()
        except Exception:
            logger.exception(
                "Think opening insight hook failed; continuing without overview"
            )
            opening_insight = ""
        progress.emit(STAGE_INTRO, STATUS_DONE, MSG_INTRO_DONE)
        return opening_insight, opening_state

    @staticmethod
    def _mark_plan_executed(
        load_plan,
        save_plan,
        thread_id: str,
        chat_seq_id: Any,
        plan: dict[str, Any],
    ) -> None:
        try:
            stored = load_plan(thread_id, chat_seq_id) or {}
            if stored:
                stored["status"] = PLAN_STATUS_EXECUTED
                stored["plan"] = plan
                save_plan(thread_id, chat_seq_id, stored)
        except Exception:
            logger.exception(
                "Interactive think: failed to mark plan executed thread=%s seq=%s",
                thread_id,
                chat_seq_id,
            )

    @staticmethod
    def _validate(
        question: str,
        model_file_name: str,
        model_location: str,
        thread_id: str,
    ) -> None:
        if not str(question or "").strip():
            raise ValueError(ERR_INPUT_STRING_REQUIRED)
        if not model_file_name or not model_location:
            raise ValueError(ERR_MODEL_REQUIRED)
        if not thread_id:
            raise ValueError(ERR_CHAT_ID_REQUIRED)

    @staticmethod
    def _resolve_deps(
        create_and_store_plan,
        run_dashboard_agent,
        load_plan,
        save_plan,
    ):
        if create_and_store_plan is None or run_dashboard_agent is None:
            from helicalbi.sql_agent.dashboard_graph import (
                run_dashboard_agent as _rda,
            )
            from helicalbi.sql_agent.investigation import (
                create_and_store_plan as _casp,
            )

            create_and_store_plan = create_and_store_plan or _casp
            run_dashboard_agent = run_dashboard_agent or _rda
        if load_plan is None or save_plan is None:
            from helicalbi.sql_agent.plan_memory import load_plan as _lp
            from helicalbi.sql_agent.plan_memory import save_plan as _sp

            load_plan = load_plan or _lp
            save_plan = save_plan or _sp
        return create_and_store_plan, run_dashboard_agent, load_plan, save_plan


_ORCHESTRATOR = ThinkModeOrchestrator()


def run_think_mode(
    question: str,
    *,
    session_cookie: str,
    username: str,
    model_file_name: str,
    model_location: str,
    thread_id: str,
    chat_seq_id: Any = "1",
    last_chats: Optional[list] = None,
    request_id: Optional[str] = None,
    max_sub_questions: Optional[int] = None,
    agent_mode: Optional[str] = None,
    user_role: Optional[Sequence[Any]] = None,
    user_profile: Optional[Sequence[Any]] = None,
    persona_hint: Optional[str] = None,
    strategy_hint: Optional[str] = None,
    show_llm_activity_details: Optional[Any] = None,
    on_progress: Optional[ProgressCallback] = None,
    _create_and_store_plan=None,
    _run_dashboard_agent=None,
    _load_plan=None,
    _save_plan=None,
    _synthesize_final_answer=None,
    _synthesize_opening_insight=None,
) -> dict[str, Any]:
    return _ORCHESTRATOR.run(
        question,
        session_cookie=session_cookie,
        username=username,
        model_file_name=model_file_name,
        model_location=model_location,
        thread_id=thread_id,
        chat_seq_id=chat_seq_id,
        last_chats=last_chats,
        request_id=request_id,
        max_sub_questions=max_sub_questions,
        agent_mode=agent_mode,
        user_role=user_role,
        user_profile=user_profile,
        persona_hint=persona_hint,
        strategy_hint=strategy_hint,
        show_llm_activity_details=show_llm_activity_details,
        on_progress=on_progress,
        _create_and_store_plan=_create_and_store_plan,
        _run_dashboard_agent=_run_dashboard_agent,
        _load_plan=_load_plan,
        _save_plan=_save_plan,
        _synthesize_final_answer=_synthesize_final_answer,
        _synthesize_opening_insight=_synthesize_opening_insight,
    )
