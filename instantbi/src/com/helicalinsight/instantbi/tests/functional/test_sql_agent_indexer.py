"""Schema RAG tests for the iterative SQL agent indexer."""

from helicalbi.sql_agent.database.catalog import ColumnMeta, ForeignKeyMeta, TableMeta
from helicalbi.sql_agent.database.schema_indexer import SchemaIndexer

import pytest


pytestmark = pytest.mark.functional


def _col(name, **kwargs):
    return ColumnMeta(name=name, data_type=kwargs.get("data_type", "integer"), **{
        k: v for k, v in kwargs.items() if k != "data_type"
    })


def _noise_tables(count: int) -> list[TableMeta]:
    tables = []
    for i in range(count):
        tables.append(
            TableMeta(
                name=f"misc_entity_{i:03d}",
                description=f"Generic auxiliary entity {i} for warehouse logistics widgets",
                columns=[
                    _col("id", is_primary_key=True),
                    _col(f"attr_{i}", data_type="text", description="opaque attribute"),
                    _col("created_at", data_type="timestamp"),
                ],
                primary_keys=["id"],
            )
        )
    return tables


def _sales_tables() -> list[TableMeta]:
    return [
        TableMeta(
            name="customers",
            description="Registered customers who place purchase orders",
            columns=[
                _col("customer_id", is_primary_key=True),
                _col("customer_name", data_type="text", description="full name"),
                _col("country", data_type="text", sample_values=["US", "UK", "IN"]),
            ],
            primary_keys=["customer_id"],
        ),
        TableMeta(
            name="orders",
            description="Customer purchase orders and order revenue",
            columns=[
                _col("order_id", is_primary_key=True),
                _col("customer_id", description="fk to customers"),
                _col("order_revenue", data_type="numeric", description="net revenue"),
                _col("status", data_type="text", sample_values=["open", "shipped", "cancelled"]),
            ],
            primary_keys=["order_id"],
            foreign_keys=[
                ForeignKeyMeta(column="customer_id", ref_table="customers", ref_column="customer_id"),
            ],
        ),
        TableMeta(
            name="order_items",
            description="Line items belonging to a customer order",
            columns=[
                _col("item_id", is_primary_key=True),
                _col("order_id"),
                _col("product_id"),
                _col("quantity"),
            ],
            primary_keys=["item_id"],
            foreign_keys=[
                ForeignKeyMeta(column="order_id", ref_table="orders", ref_column="order_id"),
            ],
        ),
        TableMeta(
            name="products",
            description="Sellable products in the catalog",
            columns=[
                _col("product_id", is_primary_key=True),
                _col("product_name", data_type="text"),
                _col("unit_price", data_type="numeric"),
            ],
            primary_keys=["product_id"],
        ),
        TableMeta(
            name="payments",
            description="Payments collected against customer orders",
            columns=[
                _col("payment_id", is_primary_key=True),
                _col("order_id"),
                _col("amount", data_type="numeric"),
            ],
            primary_keys=["payment_id"],
            foreign_keys=[
                ForeignKeyMeta(column="order_id", ref_table="orders", ref_column="order_id"),
            ],
        ),
    ]


def test_retrieve_schema_isolates_target_tables_from_150_dummy_tables():
    indexer = SchemaIndexer()
    indexer.index_tables(_noise_tables(150) + _sales_tables())
    assert len(indexer.catalog.tables()) >= 155

    subset = indexer.retrieve_schema(
        "What is total order revenue by customer country?",
        top_k=5,
    )

    assert "TABLE orders" in subset
    assert "TABLE customers" in subset
    noise_hits = [f"misc_entity_{i:03d}" for i in range(150) if f"TABLE misc_entity_{i:03d}" in subset]
    assert len(noise_hits) <= 3


def test_index_from_cube_metadata(sample_cube_metadata):
    indexer = SchemaIndexer()
    indexer.index_from_cube_metadata(sample_cube_metadata)
    subset = indexer.retrieve_schema("employee names and meeting clients", top_k=5)
    assert "TABLE employee_details" in subset
    assert "TABLE meeting_details" in subset


def test_instantbi_left_right_joins_become_foreign_keys():
    from helicalbi.sql_agent.database.catalog import tables_from_cube_metadata

    cubes = [
        {
            "database_table": "travel_details",
            "columns": [
                {"column_name": "travel_cost", "data_type": "numeric"},
                {"column_name": "travel_type", "data_type": "text"},
                {"column_name": "travelled_by", "data_type": "integer"},
                {"column_name": "travel_year", "data_type": "integer"},
            ],
        },
        {
            "database_table": "employee_details",
            "columns": [
                {"column_name": "employee_id", "data_type": "integer"},
                {"column_name": "department", "data_type": "text"},
            ],
        },
        {
            "database_table": "meeting_details",
            "columns": [
                {"column_name": "meeting_by", "data_type": "integer"},
                {"column_name": "client_name", "data_type": "text"},
            ],
        },
    ]
    joins = [
        {
            "type": "inner",
            "left": {"table": "employee_details", "column": "employee_id"},
            "right": {"table": "travel_details", "column": "travelled_by"},
        },
        {
            "type": "inner",
            "left": {"table": "employee_details", "column": "employee_id"},
            "right": {"table": "meeting_details", "column": "meeting_by"},
        },
    ]
    tables = {t.name: t for t in tables_from_cube_metadata(cubes, joins)}
    travel_fks = {(fk.ref_table, fk.column) for fk in tables["travel_details"].foreign_keys}
    assert ("employee_details", "travelled_by") in travel_fks
    emp_refs = {fk.ref_table for fk in tables["employee_details"].foreign_keys}
    assert "travel_details" in emp_refs
    assert "meeting_details" in emp_refs


def test_relation_exploration_pack_includes_same_table_and_joins():
    indexer = SchemaIndexer()
    cubes = [
        {
            "database_table": "travel_details",
            "columns": [
                {"column_name": "travel_cost", "data_type": "numeric"},
                {"column_name": "travel_type", "data_type": "text"},
                {"column_name": "travel_year", "data_type": "integer"},
                {"column_name": "travelled_by", "data_type": "integer"},
            ],
        },
        {
            "database_table": "employee_details",
            "columns": [
                {"column_name": "employee_id", "data_type": "integer"},
                {"column_name": "department", "data_type": "text"},
            ],
        },
    ]
    joins = [
        {
            "left": {"table": "employee_details", "column": "employee_id"},
            "right": {"table": "travel_details", "column": "travelled_by"},
        }
    ]
    indexer.index_from_cube_metadata(cubes, joins)
    pack, allowed, tables = indexer.relation_exploration_pack(
        "Total travel cost per year",
        seed_names=["Travel Cost"],
        top_k=3,
        max_tables=4,
    )
    assert "same_table_columns" in pack
    assert "travel_type" in pack
    assert "travel_year" in pack
    assert "travel_details" in tables
    assert "employee_details" in pack or "department" in pack
    assert "travel_cost" in allowed
    assert any(a.lower() == "travel cost" for a in allowed)
    assert "department" in allowed
