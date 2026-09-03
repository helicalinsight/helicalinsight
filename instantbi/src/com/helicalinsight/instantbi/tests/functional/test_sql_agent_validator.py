"""AST catalog validation for the iterative SQL agent."""

import pytest

from helicalbi.sql_agent.database.catalog import ColumnMeta, TableMeta
from helicalbi.sql_agent.database.schema_indexer import SchemaIndexer
from helicalbi.sql_agent.nodes.validator import validate_sql_against_catalog


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


def test_validator_rejects_select_star():
    indexer = _catalog()
    error = validate_sql_against_catalog(
        "SELECT * FROM employees",
        indexer.catalog,
        dialect="postgres",
    )
    assert error
    assert "SELECT *" in error
