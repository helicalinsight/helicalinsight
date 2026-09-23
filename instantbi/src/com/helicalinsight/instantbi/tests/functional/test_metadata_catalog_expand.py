"""Validator falls back to physical metadata without expanding cube_metadata."""

from helicalbi.common.JsonToPara import metadata_has_column, metadata_has_table
from helicalbi.sql_agent.database.catalog import ColumnMeta, TableMeta
from helicalbi.sql_agent.database.schema_indexer import SchemaIndexer
from helicalbi.sql_agent.nodes.validator import validate_sql_against_catalog


def _semantic_only_catalog() -> SchemaIndexer:
    indexer = SchemaIndexer()
    indexer.index_tables(
        [
            TableMeta(
                name="travel_details",
                columns=[ColumnMeta(name="travel_cost", data_type="numeric")],
            )
        ]
    )
    return indexer


def test_metadata_helpers_check_physical_api():
    metadata = {
        "tables": {
            "travel_details": {
                "columns": {
                    "travel_cost": {"alias": "Travel Cost"},
                    "travel_distance": {},
                },
            }
        }
    }
    assert metadata_has_table(metadata, "travel_details") is True
    assert metadata_has_column(metadata, "travel_details", "travel_distance") is True
    assert metadata_has_column(metadata, "travel_details", "Travel Cost") is True
    assert metadata_has_column(metadata, "travel_details", "missing") is False


def test_cube_metadata_indexes_column_aliases():
    from helicalbi.sql_agent.database.catalog import SchemaCatalog, tables_from_cube_metadata

    tables = tables_from_cube_metadata(
        [
            {
                "database_table": "travel_details",
                "columns": [
                    {
                        "column_name": "travel_cost",
                        "alias_name": "Travel Cost",
                        "data_type": "numeric",
                    }
                ],
            }
        ]
    )
    catalog = SchemaCatalog(tables)
    assert catalog.has_column("travel_details", "travel_cost")
    assert catalog.has_column("travel_details", "Travel Cost")


def test_validator_accepts_metadata_column_without_expanding_cube():
    indexer = _semantic_only_catalog()
    metadata = {
        "tables": {
            "travel_details": {
                "columns": {"travel_cost": {}, "travel_distance": {}},
            }
        }
    }
    # Without metadata fallback this would fail — column is only in metadata.
    assert (
        validate_sql_against_catalog(
            "SELECT travel_details.travel_distance FROM travel_details",
            indexer.catalog,
            dialect="postgres",
        )
        is not None
    )
    assert (
        validate_sql_against_catalog(
            "SELECT travel_details.travel_distance FROM travel_details",
            indexer.catalog,
            dialect="postgres",
            metadata=metadata,
        )
        is None
    )


def test_validator_still_rejects_columns_missing_from_both():
    indexer = _semantic_only_catalog()
    metadata = {
        "tables": {
            "travel_details": {
                "columns": {"travel_cost": {}, "travel_distance": {}},
            }
        }
    }
    error = validate_sql_against_catalog(
        "SELECT travel_details.not_real FROM travel_details",
        indexer.catalog,
        dialect="postgres",
        metadata=metadata,
    )
    assert error
    assert "not_real" in error
