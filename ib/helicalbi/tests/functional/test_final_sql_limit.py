"""Tests for resolving SQL LIMIT from the query plan in FinalSqlGen."""

import json

import pytest

from helicalbi.common.app_config import default_sql_limit
from helicalbi.core.sqlflow.FinalSqlGen import _limit_from_query_plan


pytestmark = pytest.mark.functional


def test_limit_from_query_plan_dict():
    assert _limit_from_query_plan({"columnName": ["t.a"], "limit": 10}) == 10


def test_limit_from_query_plan_json_string():
    plan = json.dumps({"columnName": ["t.a"], "limit": 25})
    assert _limit_from_query_plan(plan) == 25


def test_limit_from_query_plan_missing_returns_none():
    assert _limit_from_query_plan({"columnName": ["t.a"]}) is None
    assert _limit_from_query_plan("{}") is None
    assert _limit_from_query_plan("") is None
    assert _limit_from_query_plan(None) is None


def test_limit_from_query_plan_rejects_invalid_values():
    assert _limit_from_query_plan({"limit": 0}) is None
    assert _limit_from_query_plan({"limit": -5}) is None
    assert _limit_from_query_plan({"limit": "abc"}) is None
    assert _limit_from_query_plan({"limit": ""}) is None


def test_resolved_limit_falls_back_to_default():
    plan_limit = _limit_from_query_plan({"columnName": ["t.a"]})
    sql_limit = plan_limit if plan_limit is not None else default_sql_limit
    assert sql_limit == default_sql_limit
