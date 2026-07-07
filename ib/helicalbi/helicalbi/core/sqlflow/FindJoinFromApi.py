import json
import logging
import uuid
from typing import Optional

import sqlglot
from sqlglot import exp

from helicalbi.api.HttpCallService import fetch_service_api
from helicalbi.common.DialectMapper import resolve_sqlglot_dialect
from helicalbi.api.Metadata import build_column
from helicalbi.common.JsonToPara import iter_cube_entries
from helicalbi.model.SQLAgent import SQLAgent

logger = logging.getLogger(__name__)


def _resolve_join_column(cube: dict) -> Optional[str]:
    """Use unique_identifier_of_table, or the first columns[].column_name as fallback."""
    if not isinstance(cube, dict):
        return None
    uid = cube.get("unique_identifier_of_table")
    if uid and str(uid).strip():
        return str(uid).strip()
    columns = cube.get("columns")
    if columns is None or not isinstance(columns, list) or not columns:
        return None
    first_col = columns[0]
    if not isinstance(first_col, dict):
        return None
    col_name = first_col.get("column_name")
    if col_name and str(col_name).strip():
        return str(col_name).strip()
    return None


class FindJoinFromApi:
    def extract_full_joins(self, sql, dialect=None):
        if not sql:
            return []

        try:
            parsed = sqlglot.parse_one(sql, read=resolve_sqlglot_dialect(dialect))
        except Exception:
            logger.error("SQL parse failed for dialect=%s", dialect, exc_info=True)
            return []

        joins = []
        for join in parsed.find_all(exp.Join):
            joins.append(join.sql())

        return joins

    def process_flow(self, state: SQLAgent):
        logger.info("FindJoinFromApi flow started")
        session_cookie = state["session_cookie"]
        cube_metadata = state["cube_metadata"]
        metadata_name = state["metadata_file_name"]
        location = state["location"]
        table_names = state["required_tables"] or []
        if len(table_names) <= 1:
            logger.info("Skipping join fetch: single or no required table(s)")
            state["hi_sql"] = json.dumps({})
            state["required_joins"] = ""
            return state

        output = self.fetch_sql(
            session_cookie, metadata_name, location, table_names, cube_metadata, state["dbname"]
        ) or {}
        logger.info(f"The output obtained for sql fetch {output}")
        dummy_sql = output.get("query", "")
        dummy_sql_join=""
        if dummy_sql:
            dummy_sql_join = self.extract_full_joins(dummy_sql, state.get("dialect"))
        if dummy_sql_join:
            dummy_sql = "\n".join(dummy_sql_join)
            output["required_joins"] = dummy_sql
        state["hi_sql"] = json.dumps(output)
        state["required_joins"] = dummy_sql
        return state

    def fetch_sql(self, session_cookie: str, metadata, location, tables=None,
                  cube_metadata=None, dbname="") -> dict:
        tables = tables or []
        columns = []
        reduced_cubes = []
        groupby = []
        for cube in iter_cube_entries(cube_metadata):
            source_table = cube.get("database_table")
            if source_table and source_table in tables:
                reduced_cubes.append(cube)

        for item in reduced_cubes:
            column_name = _resolve_join_column(item)
            table_name = item.get("database_table")
            if not column_name or not table_name:
                logger.warning(
                    "Skipping cube %s: no unique_identifier_of_table or columns[0].column_name",
                    table_name,
                )
                continue
            columns.append(
                build_column(f"{table_name}.{column_name}", column_name, dbname=dbname)
            )
            groupby.append({
                "column": column_name,
                "custom": True
            })

        if not columns:
            logger.warning("No join columns resolved from cube_metadata; skipping join API call")
            return {}

        form_data = {
            "location": location,
            "metadataFileName": metadata,
            "columns": columns,
            "limitBy": 1,
            "prependTableNameToAlias": False
        }
        payload_json = {
            "type": "adhoc",
            "serviceType": "report",
            "service": "generateQuery",
            "formData": json.dumps(form_data),
            "requestId": uuid.uuid4().hex
        }
        # print("final payload"+payload_json)
        api_response = fetch_service_api(session_cookie=session_cookie, service_json=payload_json)
        return (api_response or {}).get("response") or {}
