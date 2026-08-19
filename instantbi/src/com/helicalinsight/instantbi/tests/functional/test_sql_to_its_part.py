"""Functional tests for ``helicalbi.core.sqlflow.util.SqlToItsPart.analyze_sql``."""
import pytest

from helicalbi.core.sqlflow.util.SqlToItsPart import analyze_sql


pytestmark = pytest.mark.functional


class TestAnalyzeSql:
    def test_minimal_select_returns_only_select_true(self):
        result = analyze_sql("SELECT 1")
        assert result["select"] is True
        assert result["where"] is False
        assert result["group_by"] is False
        assert result["having"] is False
        assert result["order_by"] is False
        assert result["limit"] is False
        assert result["join"] is False

    def test_full_query_detects_all_clauses(self):
        sql = """
        SELECT c.client_id, c.name, SUM(t.amount) AS total_spent
        FROM clients c
        JOIN transactions t ON c.client_id = t.client_id
        WHERE t.type = 'domestic'
        GROUP BY c.client_id, c.name
        HAVING SUM(t.amount) > 100
        ORDER BY total_spent DESC
        LIMIT 1
        """
        result = analyze_sql(sql)
        assert result["select"] is True
        assert result["where"] is True
        assert result["group_by"] is True
        assert result["having"] is True
        assert result["order_by"] is True
        assert result["limit"] is True
        assert result["join"] is True
        assert result["function"] is True

    def test_detects_function_usage(self):
        result = analyze_sql("SELECT UPPER(name) FROM users")
        assert result["function"] is True

    def test_no_function_when_absent(self):
        result = analyze_sql("SELECT name FROM users")
        assert result["function"] is False
