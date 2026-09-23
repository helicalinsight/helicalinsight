"""Functional tests for ``helicalbi.sql.SqlSanitizer.extract_sql``."""
import pytest

from helicalbi.sql.SqlSanitizer import extract_sql, normalize_sql_identifier_escapes


pytestmark = pytest.mark.functional


class TestNormalizeSqlIdentifierEscapes:
    def test_strips_backslash_before_double_quotes(self):
        sql = r'SELECT "t".\"col\" FROM \"t\"'
        assert normalize_sql_identifier_escapes(sql) == 'SELECT "t"."col" FROM "t"'

    def test_noop_when_no_backslashes(self):
        sql = 'SELECT "t"."col" FROM "t"'
        assert normalize_sql_identifier_escapes(sql) is sql


class TestExtractSql:
    def test_quotes_identifiers_in_simple_select(self):
        sql = "select id, name from users"
        result = extract_sql(sql, "postgres")
        assert '"id"' in result
        assert '"name"' in result
        assert '"users"' in result

    def test_preserves_select_semantics(self):
        sql = 'SELECT COUNT(*) FROM "orders" WHERE status = \'open\''
        result = extract_sql(sql, "postgres")
        assert "COUNT" in result.upper()
        assert "'open'" in result

    def test_returns_input_when_sql_is_invalid(self):
        broken = "this is :: not :: sql @@@"
        result = extract_sql(broken, "postgres")
        assert result == broken

    def test_normalizes_backslash_escaped_quotes(self):
        sql = r'SELECT COUNT(CASE WHEN "t".\"c\" = \'Yes\' THEN 1 END) FROM \"t\"'
        result = extract_sql(sql, "postgres")
        assert '\\"' not in result
        assert "CASE" in result.upper()

    @pytest.mark.parametrize("dialect", ["mysql", "sqlite", "postgres"])
    def test_supports_multiple_dialects(self, dialect):
        sql = "select 1 as one"
        result = extract_sql(sql, dialect)
        assert "1" in result
        assert "one" in result.lower()
