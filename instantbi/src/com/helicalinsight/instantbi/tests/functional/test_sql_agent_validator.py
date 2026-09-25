"""AST catalog validation for the iterative SQL agent."""

import pytest

from helicalbi.sql_agent.database.catalog import ColumnMeta, TableMeta
from helicalbi.sql_agent.database.schema_indexer import SchemaIndexer
from helicalbi.sql_agent.nodes.validator import (
    sql_has_subquery,
    subquery_rejection,
    validate_sql_against_catalog,
)


pytestmark = pytest.mark.functional


def _catalog() -> SchemaIndexer:
    indexer = SchemaIndexer()
    indexer.index_tables(
        [
            TableMeta(
                name="employees",
                columns=[
                    ColumnMeta(name="employee_id", data_type="integer", is_primary_key=True),
                    ColumnMeta(name="employee_name", data_type="text"),
                    ColumnMeta(name="salary", data_type="numeric"),
                ],
                primary_keys=["employee_id"],
            ),
            TableMeta(
                name="departments",
                columns=[
                    ColumnMeta(name="department_id", data_type="integer", is_primary_key=True),
                    ColumnMeta(name="department_name", data_type="text"),
                ],
                primary_keys=["department_id"],
            ),
        ]
    )
    return indexer


def test_validator_accepts_known_columns():
    indexer = _catalog()
    error = validate_sql_against_catalog(
        'SELECT employees.employee_id, employees.employee_name FROM employees',
        indexer.catalog,
        dialect="postgres",
    )
    assert error is None


def test_validator_rejects_unknown_column_before_execution():
    indexer = _catalog()
    error = validate_sql_against_catalog(
        "SELECT employees.not_a_real_column FROM employees",
        indexer.catalog,
        dialect="postgres",
    )
    assert error
    assert "not_a_real_column" in error


def test_validator_rejects_write_statements():
    indexer = _catalog()
    error = validate_sql_against_catalog(
        "DELETE FROM employees WHERE employee_id = 1",
        indexer.catalog,
        dialect="postgres",
    )
    assert error
    assert "read-only" in error.lower()


def test_think_mode_rejects_subqueries_and_keeps_flat_selects():
    flat = 'SELECT employees.employee_name, SUM(employees.salary) FROM employees GROUP BY employees.employee_name'
    nested = (
        "SELECT employees.employee_name FROM employees "
        "WHERE employees.salary > (SELECT AVG(employees.salary) FROM employees)"
    )
    derived = (
        "SELECT g.employee_name FROM ("
        "SELECT employees.employee_name FROM employees"
        ") g"
    )
    cte = (
        "WITH totals AS (SELECT employees.employee_id FROM employees) "
        "SELECT totals.employee_id FROM totals"
    )
    assert sql_has_subquery(flat, "postgres") is False
    assert subquery_rejection(flat, "postgres") is None
    assert sql_has_subquery(nested, "postgres") is True
    assert sql_has_subquery(derived, "postgres") is True
    assert sql_has_subquery(cte, "postgres") is True
    message = subquery_rejection(nested, "postgres") or ""
    assert "Subqueries are not allowed" in message


def test_validator_rejects_select_star():
    indexer = _catalog()
    error = validate_sql_against_catalog(
        "SELECT * FROM employees",
        indexer.catalog,
        dialect="postgres",
    )
    assert error
    assert "SELECT *" in error
