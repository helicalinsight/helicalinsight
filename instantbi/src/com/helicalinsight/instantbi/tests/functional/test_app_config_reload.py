"""Hot-reload behaviour for ``application_config.yaml`` live accessors."""
from copy import deepcopy

import pytest

from helicalbi.common import app_config
from helicalbi.service.kpi.KpiProvider import KpiProvider


pytestmark = pytest.mark.functional


def test_kpi_suggestion_query_tracks_in_memory_reload():
    original = app_config._raw
    try:
        updated = deepcopy(original)
        updated.setdefault("kpi", {})["suggestion_query"] = "Live KPI suggestion prompt"
        with app_config._lock:
            app_config._raw = updated

        assert app_config.kpi_suggestion_query == "Live KPI suggestion prompt"
        provider = KpiProvider({}, "Sales Operation")
        assert provider.user_query == "Live KPI suggestion prompt"
    finally:
        with app_config._lock:
            app_config._raw = original


def test_kpi_suggestion_query_prefers_model_override():
    provider = KpiProvider(
        {"suggestion_query": "From model JSON"},
        "Sales Operation",
    )
    assert provider.user_query == "From model JSON"


def test_dashboard_max_sub_questions_tracks_in_memory_reload():
    original = app_config._raw
    try:
        updated = deepcopy(original)
        updated.setdefault("dashboard", {})["max_sub_questions"] = 3
        with app_config._lock:
            app_config._raw = updated

        assert app_config.dashboard_max_sub_questions == 3
    finally:
        with app_config._lock:
            app_config._raw = original


def test_default_sql_limit_tracks_in_memory_reload():
    original = app_config._raw
    try:
        updated = deepcopy(original)
        updated.setdefault("sql", {})["default_limit"] = 25
        with app_config._lock:
            app_config._raw = updated

        assert app_config.default_sql_limit == 25
        from helicalbi.core.sqlflow.FinalSqlGen import _limit_from_query_plan

        plan_limit = _limit_from_query_plan({"columnName": ["t.a"]})
        sql_limit = plan_limit if plan_limit is not None else app_config.default_sql_limit
        assert sql_limit == 25
    finally:
        with app_config._lock:
            app_config._raw = original
