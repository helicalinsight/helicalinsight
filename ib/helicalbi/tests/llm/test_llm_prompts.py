"""DeepEval-driven LLM prompt test cases for HelicalBI.

The model schema under evaluation is the Travel + Meetings cube defined in
``helicalbi.api.TravelDetailsCube``.  For each major LLM-driven flow
(domain/topic identification, column detection, SQL generation, viz
selection) we build a :class:`deepeval.test_case.LLMTestCase`, feed it
through the same :class:`PromptTemplate` instances used in production, and
score the output with a mix of:

* **Custom deterministic metrics** that work offline (schema compliance,
  hallucination against the cube metadata, expected-keyword recall).
  These let CI run the suite without any LLM credentials.
* **DeepEval ``GEval``** judge metrics for free-form criteria (semantic
  correctness, SQL faithfulness).  They are *opt-in*: they only run when
  ``HELICALBI_LLM_MODE=live`` is set, since the default judge needs an
  ``OPENAI_API_KEY``.

The runner mode (``stub`` vs ``live``) is controlled by the ``llm_mode``
fixture (see ``tests/llm/conftest.py``).
"""
from __future__ import annotations

import json
import re
from typing import Iterable

import pytest

deepeval = pytest.importorskip(
    "deepeval", reason="deepeval not installed - install with: pip install deepeval"
)
from deepeval import assert_test
from deepeval.metrics import BaseMetric
from deepeval.test_case import LLMTestCase

from helicalbi.common.app_config import default_sql_limit
from tests.llm.prompt_runner import (
    run_column_detection,
    run_domain_topic,
    run_sql_generation,
    run_viz_selection,
)


pytestmark = pytest.mark.llm


# ---------------------------------------------------------------------------
# Custom deepeval metrics that don't need a judge LLM.
# ---------------------------------------------------------------------------


class JsonShapeMetric(BaseMetric):
    """Pass when ``actual_output`` is JSON containing all ``required_keys``."""

    def __init__(self, required_keys: Iterable[str], threshold: float = 1.0):
        self.required_keys = list(required_keys)
        self.threshold = threshold
        self.async_mode = False
        self.strict_mode = True

    def measure(self, test_case: LLMTestCase) -> float:
        try:
            payload = json.loads(test_case.actual_output)
        except (TypeError, ValueError) as exc:
            self.score = 0.0
            self.success = False
            self.reason = f"Output is not valid JSON: {exc}"
            return self.score

        missing = [k for k in self.required_keys if k not in payload]
        self.score = 1.0 if not missing else 0.0
        self.success = not missing
        self.reason = (
            "All required keys present"
            if not missing
            else f"Missing keys: {missing}"
        )
        return self.score

    async def a_measure(self, test_case: LLMTestCase) -> float:
        return self.measure(test_case)

    def is_successful(self) -> bool:
        return bool(getattr(self, "success", False))

    @property
    def __name__(self) -> str:
        return "JsonShape"


class ExpectedValuesMetric(BaseMetric):
    """Pass when JSON output contains expected sets/values at given JSON keys.

    ``expected`` is a mapping of ``key -> iterable of values`` that must
    *all* appear in the parsed JSON value at ``key`` (the JSON value can
    be a string, a list, or any iterable).
    """

    def __init__(self, expected: dict[str, Iterable[str]], threshold: float = 1.0):
        self.expected = {k: list(v) for k, v in expected.items()}
        self.threshold = threshold
        self.async_mode = False
        self.strict_mode = True

    def measure(self, test_case: LLMTestCase) -> float:
        try:
            payload = json.loads(test_case.actual_output)
        except (TypeError, ValueError) as exc:
            self.score = 0.0
            self.success = False
            self.reason = f"Output is not valid JSON: {exc}"
            return self.score

        misses: list[str] = []
        for key, wanted in self.expected.items():
            actual_value = payload.get(key)
            haystack = (
                " ".join(actual_value)
                if isinstance(actual_value, list)
                else str(actual_value or "")
            )
            for needle in wanted:
                if needle.lower() not in haystack.lower():
                    misses.append(f"{key}: missing '{needle}' in {actual_value!r}")

        self.score = 1.0 if not misses else 0.0
        self.success = not misses
        self.reason = (
            "All expected values present" if not misses else "; ".join(misses)
        )
        return self.score

    async def a_measure(self, test_case: LLMTestCase) -> float:
        return self.measure(test_case)

    def is_successful(self) -> bool:
        return bool(getattr(self, "success", False))

    @property
    def __name__(self) -> str:
        return "ExpectedValues"


class NoHallucinationMetric(BaseMetric):
    """Pass when no token outside ``allowed_tokens`` appears in the output.

    Useful for guaranteeing the model only picks columns/tables/topics
    that actually exist in the TravelDetails cube.
    """

    def __init__(
        self,
        json_key: str,
        allowed_tokens: Iterable[str],
        threshold: float = 1.0,
    ):
        self.json_key = json_key
        self.allowed_tokens = {tok.lower() for tok in allowed_tokens}
        self.threshold = threshold
        self.async_mode = False
        self.strict_mode = True

    def measure(self, test_case: LLMTestCase) -> float:
        try:
            payload = json.loads(test_case.actual_output)
        except (TypeError, ValueError) as exc:
            self.score = 0.0
            self.success = False
            self.reason = f"Output is not valid JSON: {exc}"
            return self.score

        candidates = payload.get(self.json_key, [])
        if isinstance(candidates, str):
            candidates = [candidates]

        invented: list[str] = []
        for raw in candidates:
            token = str(raw).strip().lower()
            normalized = re.split(r"\s+as\s+", token)[0]
            if normalized not in self.allowed_tokens:
                invented.append(raw)

        self.score = 1.0 if not invented else 0.0
        self.success = not invented
        self.reason = (
            "No hallucinated tokens"
            if not invented
            else f"Invented tokens: {invented}"
        )
        return self.score

    async def a_measure(self, test_case: LLMTestCase) -> float:
        return self.measure(test_case)

    def is_successful(self) -> bool:
        return bool(getattr(self, "success", False))

    @property
    def __name__(self) -> str:
        return "NoHallucination"


class SqlKeywordsMetric(BaseMetric):
    """Pass when the SQL string contains all expected substrings (case-insensitive)."""

    def __init__(self, expected_substrings: Iterable[str], threshold: float = 1.0):
        self.expected_substrings = list(expected_substrings)
        self.threshold = threshold
        self.async_mode = False
        self.strict_mode = True

    def measure(self, test_case: LLMTestCase) -> float:
        try:
            payload = json.loads(test_case.actual_output)
            sql = payload.get("sql", "")
        except (TypeError, ValueError):
            sql = test_case.actual_output or ""

        sql_lower = sql.lower()
        missing = [
            s for s in self.expected_substrings if s.lower() not in sql_lower
        ]
        self.score = 1.0 if not missing else 0.0
        self.success = not missing
        self.reason = (
            "All SQL keywords present"
            if not missing
            else f"Missing in SQL: {missing}"
        )
        return self.score

    async def a_measure(self, test_case: LLMTestCase) -> float:
        return self.measure(test_case)

    def is_successful(self) -> bool:
        return bool(getattr(self, "success", False))

    @property
    def __name__(self) -> str:
        return "SqlKeywords"


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------


def _all_cube_columns(cube_metadata: list) -> set[str]:
    """All ``table.column`` and bare ``column`` tokens defined in the cube."""
    tokens: set[str] = set()
    for cube in cube_metadata:
        table = cube["database_table"]
        for col in cube["columns"]:
            tokens.add(col["column_name"])
            tokens.add(f"{table}.{col['column_name']}")
    return tokens


def _all_topics(domain: list) -> set[str]:
    return {topic for d in domain for topic in d["topics"]}


def _all_domains(domain: list) -> set[str]:
    return {d["domain_name"] for d in domain}


def _maybe_geval(name: str, criteria: str, params: list, mode: str):
    """Return a ``GEval`` metric only when running in live mode.

    GEval needs a judge LLM (OpenAI by default).  When ``mode`` is ``stub``
    we skip it entirely so the suite stays fully offline.
    """
    if mode != "live":
        return None
    from deepeval.metrics import GEval
    from deepeval.test_case import LLMTestCaseParams

    param_map = {
        "input": LLMTestCaseParams.INPUT,
        "actual_output": LLMTestCaseParams.ACTUAL_OUTPUT,
        "expected_output": LLMTestCaseParams.EXPECTED_OUTPUT,
        "context": LLMTestCaseParams.CONTEXT,
    }
    return GEval(
        name=name,
        criteria=criteria,
        evaluation_params=[param_map[p] for p in params],
        threshold=0.7,
    )


# ===========================================================================
# Tests: Domain + Topic identification
# ===========================================================================


class TestDomainTopicPrompt:
    """The model must map a NL query to the right domain + topic(s)."""

    @pytest.mark.parametrize(
        "user_query, expected_topics",
        [
            ("How many meetings were canceled last month?", ["Meetings"]),
            ("Show me total travel cost by destination city", ["Travel"]),
            (
                "Which employee had the highest number of successful meetings?",
                ["Meetings"],
            ),
            ("List travel and meeting details per employee", ["Travel", "Meetings"]),
        ],
    )
    def test_domain_and_topic_identified(
        self,
        travel_model_data,
        travel_domain,
        llm_mode,
        user_query,
        expected_topics,
    ):
        result = run_domain_topic(travel_model_data, user_query, mode=llm_mode)

        test_case = LLMTestCase(
            input=user_query,
            actual_output=result.output,
            expected_output=json.dumps(
                {"domain": ["Sales Operation"], "topics": expected_topics}
            ),
            context=[result.rendered],
        )

        metrics = [
            JsonShapeMetric(required_keys=["domain", "topics", "reason"]),
            ExpectedValuesMetric(
                expected={
                    "domain": ["Sales Operation"],
                    "topics": expected_topics,
                }
            ),
            NoHallucinationMetric(
                json_key="domain", allowed_tokens=_all_domains(travel_domain)
            ),
            NoHallucinationMetric(
                json_key="topics", allowed_tokens=_all_topics(travel_domain)
            ),
        ]

        geval = _maybe_geval(
            name="Domain/Topic Correctness",
            criteria=(
                "Determine if the actual_output selects the same domain and "
                "topics as the expected_output, allowing for reasonable "
                "ordering differences."
            ),
            params=["input", "actual_output", "expected_output"],
            mode=llm_mode,
        )
        if geval is not None:
            metrics.append(geval)

        assert_test(test_case, metrics)


# ===========================================================================
# Tests: Column detection
# ===========================================================================


class TestColumnDetectionPrompt:
    """Column detection must stay inside the cube schema."""

    @pytest.mark.parametrize(
        "user_query, table_names, expected_columns",
        [
            (
                "Show me total canceled meetings per client",
                ["meeting_details"],
                ["client_name", "meet_cancellation_status"],
            ),
            (
                "Average travel cost per employee",
                ["travel_details", "employee_details"],
                ["travel_cost", "travelled_by", "employee_id"],
            ),
            (
                "List meetings with their destination city",
                ["meeting_details", "travel_details", "geo_cordinates"],
                ["destination_id", "location"],
            ),
        ],
    )
    def test_columns_drawn_from_schema(
        self,
        travel_model_data,
        travel_cube_metadata,
        travel_synonyms,
        travel_relationships,
        llm_mode,
        user_query,
        table_names,
        expected_columns,
    ):
        result = run_column_detection(
            travel_model_data,
            travel_cube_metadata,
            travel_synonyms,
            travel_relationships,
            table_names,
            user_query,
            mode=llm_mode,
        )

        test_case = LLMTestCase(
            input=user_query,
            actual_output=result.output,
            expected_output=json.dumps({"columnName": expected_columns}),
            context=[result.rendered],
            retrieval_context=[json.dumps(travel_cube_metadata)],
        )

        allowed = _all_cube_columns(travel_cube_metadata)

        metrics = [
            JsonShapeMetric(required_keys=["columnName", "reason"]),
            ExpectedValuesMetric(expected={"columnName": expected_columns}),
            NoHallucinationMetric(json_key="columnName", allowed_tokens=allowed),
        ]

        geval = _maybe_geval(
            name="Column Faithfulness",
            criteria=(
                "Verify the actual_output picks columns that exist in the "
                "retrieval_context (cube_metadata) and are sufficient to "
                "answer the input question."
            ),
            params=["input", "actual_output", "context"],
            mode=llm_mode,
        )
        if geval is not None:
            metrics.append(geval)

        assert_test(test_case, metrics)


# ===========================================================================
# Tests: Final SQL generation
# ===========================================================================


class TestFinalSqlPrompt:
    """Generated SQL must use the right tables, filters and joins."""

    def test_canceled_meeting_count(self, travel_business_metrics, llm_mode):
        query_plan = {
            "columnName": [
                "meeting_details.meeting_id",
                "meeting_details.meet_cancellation_status",
            ],
            "reason": "Count canceled meetings only.",
        }
        required_metrics = [
            m for m in travel_business_metrics if m["metric"] == "canceled_meetings"
        ]
        result = run_sql_generation(
            user_question="Count canceled meetings",
            query_plan_json=json.dumps(query_plan),
            required_joins="",
            required_metrics=required_metrics,
            mode=llm_mode,
        )

        test_case = LLMTestCase(
            input="Count canceled meetings",
            actual_output=result.output,
            expected_output=json.dumps(
                {
                    "sql": (
                        "SELECT COUNT(meeting_details.meeting_id) AS canceled_meetings "
                        "FROM meeting_details "
                        "WHERE meeting_details.meet_cancellation_status = 'Yes' "
                        f"LIMIT {default_sql_limit}"
                    )
                }
            ),
            context=[result.rendered],
        )

        metrics = [
            JsonShapeMetric(required_keys=["sql", "reason"]),
            SqlKeywordsMetric(
                expected_substrings=[
                    "select",
                    "count(",
                    "meeting_details",
                    "meet_cancellation_status",
                    "'yes'",
                    "limit",
                ]
            ),
        ]

        geval = _maybe_geval(
            name="SQL Semantic Correctness",
            criteria=(
                "The actual_output SQL should answer the input question "
                "by counting only canceled meetings from meeting_details "
                "and limit the result set."
            ),
            params=["input", "actual_output", "expected_output"],
            mode=llm_mode,
        )
        if geval is not None:
            metrics.append(geval)

        assert_test(test_case, metrics)

    def test_travel_cost_per_destination_uses_join(
        self, travel_business_metrics, llm_mode
    ):
        query_plan = {
            "columnName": [
                "travel_details.travel_cost",
                "travel_details.destination_id",
                "geo_cordinates.location",
                "geo_cordinates.location_id",
            ],
            "reason": "Sum travel cost grouped by destination city.",
        }
        required_joins = (
            "geo_cordinates.location_id = travel_details.destination_id"
        )

        result = run_sql_generation(
            user_question="Total travel cost per destination city",
            query_plan_json=json.dumps(query_plan),
            required_joins=required_joins,
            required_metrics=[],
            mode=llm_mode,
        )

        test_case = LLMTestCase(
            input="Total travel cost per destination city",
            actual_output=result.output,
            expected_output=None,
            context=[result.rendered],
        )

        metrics = [
            JsonShapeMetric(required_keys=["sql", "reason"]),
            SqlKeywordsMetric(
                expected_substrings=[
                    "select",
                    "sum(",
                    "travel_details",
                    "geo_cordinates",
                    "destination_id",
                    "location_id",
                    "group by",
                    "limit",
                ]
            ),
        ]

        assert_test(test_case, metrics)


# ===========================================================================
# Tests: Visualization selection
# ===========================================================================


class TestVizPrompt:
    """The viz prompt should pick a sensible chart type for the data."""

    @pytest.mark.parametrize(
        "user_question, data_types, expected_viz",
        [
            (
                "Show meeting count by client as a chart",
                "client_name: string, meeting_count: number",
                "bar",
            ),
            (
                "Trend of travel cost over time",
                "travel_date: date, travel_cost: number",
                "line",
            ),
            (
                "Total canceled meetings as a single number",
                "canceled_meetings: number",
                "kpi",
            ),
        ],
    )
    def test_viz_pick(
        self, llm_mode, user_question, data_types, expected_viz
    ):
        result = run_viz_selection(
            domain_str="Sales Operation",
            topics_str="Travel, Meetings",
            user_question=user_question,
            data_types=data_types,
            mode=llm_mode,
        )

        test_case = LLMTestCase(
            input=user_question,
            actual_output=result.output,
            expected_output=json.dumps({"visualization": expected_viz}),
            context=[result.rendered],
        )

        metrics = [
            JsonShapeMetric(required_keys=["visualization"]),
            ExpectedValuesMetric(expected={"visualization": [expected_viz]}),
            NoHallucinationMetric(
                json_key="visualization",
                allowed_tokens={
                    "bar",
                    "column",
                    "pie",
                    "line",
                    "area",
                    "table",
                    "pivot table",
                    "kpi",
                },
            ),
        ]

        geval = _maybe_geval(
            name="Viz Reasonableness",
            criteria=(
                "Given the input question and data types in the context, "
                "is the chosen visualization a reasonable choice?"
            ),
            params=["input", "actual_output", "context"],
            mode=llm_mode,
        )
        if geval is not None:
            metrics.append(geval)

        assert_test(test_case, metrics)


# ===========================================================================
# Tests: Synonym resolution (no LLM needed; sanity-check the cube itself)
# ===========================================================================


class TestSchemaSynonyms:
    """Smoke-test the synonyms in TravelDetailsCube.

    These are deterministic checks that don't use the LLM but live next to
    the LLM tests because they protect the same prompt context.
    """

    @pytest.mark.parametrize(
        "phrase, expected_table",
        [
            ("client name", "meeting_details"),
            ("staff details", "employee_details"),
            ("travel expenses", "travel_details"),
            ("gps_coordinates", "geo_cordinates"),
        ],
    )
    def test_phrase_resolves_to_table(self, travel_synonyms, phrase, expected_table):
        match = next(
            (
                entry
                for entry in travel_synonyms
                if entry["database_table"] == expected_table
                and phrase in entry["synonyms"]
            ),
            None,
        )
        assert match is not None, (
            f"Phrase '{phrase}' should be a synonym for table {expected_table}"
        )
