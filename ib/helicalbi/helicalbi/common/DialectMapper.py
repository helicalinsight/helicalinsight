sql_glot_dialects = {
        "athena": "athena",
        "bigquery": "bigquery",
        "calcite": "hive",  # Closest match
        "clickhouse": "clickhouse",
        "databricks": "databricks",
        "db2": "exasol",  # Closest match
        "db2390": "exasol",  # No direct equivalent
        "db2400": "exasol",
        "default": "prql",  # Default mapped to generic
        "derby": "oracle",  # Closest to Calcite/Hive
        "dremio": "dremio",
        "drill": "drill",
        "druid": "druid",
        "duckdb": "duckdb",
        "dynamo": "dune",  # No direct, dune is closest
        "elasticsearchsql": "tableau",  # Closest match
        "firebird": "exasol",
        "firebirddb": "exasol",
        "flatfile": "postgres",
        "frontbase": "exasol",
        "h2": "hive",
        "hihttp": "tableau",
        "himongo": "dune",
        "hsql": "hive",
        "informix": "exasol",
        "ingres": "exasol",
        "interbase": "exasol",
        "mckoi": "hive",
        "mimersql": "exasol",
        "mongoCData": "dune",
        "msaccess": "tableau",
        "mysql": "mysql",
        "oracle": "oracle",
        "pointbase": "hive",
        "postgresql": "postgres",
        "presto": "presto",
        "progress": "exasol",
        "redshift": "redshift",
        "sap": "tableau",
        "sapdb": "tableau",
        "snowflake": "snowflake",
        "spark": "spark",
        "sqlite": "sqlite",
        "sqlserver": "tsql",
        "sqlserver2012": "tsql",
        "sybase": "exasol",
        "sybaseanywhere": "exasol",
        "teradata": "teradata",
        "trino": "trino",
        "vertica": "exasol",
        "yugabyte": "postgres"
    }


def resolve_sqlglot_dialect(dialect):
    return sql_glot_dialects.get(dialect) or dialect