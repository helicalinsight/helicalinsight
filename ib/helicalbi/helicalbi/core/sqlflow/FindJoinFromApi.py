import json
import logging
import uuid
from typing import Dict, List, Optional

import sqlglot
from sqlglot import exp

from helicalbi.api.HttpCallService import fetch_service_api
from helicalbi.common.DialectMapper import resolve_sqlglot_dialect
from helicalbi.api.Metadata import build_column
from helicalbi.common.JsonToPara import iter_cube_entries, split_table_column_ref
from helicalbi.core.sqlflow.util.FallbackSqlHelpers import is_join_api_error
from helicalbi.model.SQLModel import SQLModel

logger = logging.getLogger(__name__)


def _plan_columns_by_table(query_plan) -> Dict[str, List[str]]:
    """Map table -> ordered bare column names referenced by the query plan.

    Accepts the query plan as a JSON string or dict. Only qualified
    ``table.column`` references contribute, since bare columns can't be
    attributed to a specific table for join-key selection.
    """
    if isinstance(query_plan, str):
        if not query_plan.strip():
            return {}
        try:
            query_plan = json.loads(query_plan)
        except json.JSONDecodeError:
            logger.error(
                "Invalid query_plan JSON in FindJoinFromApi; no plan columns",
                exc_info=True,
            )
            return {}
    if not isinstance(query_plan, dict):
        return {}

    mapping: Dict[str, List[str]] = {}
    for raw_ref in query_plan.get("columnName") or []:
        table_name, col_name = split_table_column_ref(raw_ref)
        if not table_name or not col_name:
            continue
        bucket = mapping.setdefault(table_name, [])
        if col_name not in bucket:
            bucket.append(col_name)
    return mapping


def _resolve_join_column(cube: dict, plan_columns: Optional[List[str]] = None) -> Optional[str]:
    """Resolve a join column for a cube.

    Preference order:
      1. ``unique_identifier_of_table`` (explicit join/PK column)
      2. a column referenced by the query plan for this table
      3. the first ``columns[].column_name`` as a final fallback
    """
    if not isinstance(cube, dict):
        return None
    uid = cube.get("unique_identifier_of_table")
    if uid and str(uid).strip():
        return str(uid).strip()

    if plan_columns:
        known_columns = _cube_column_names(cube)
        for col_name in plan_columns:
            candidate = str(col_name).strip()
            if not candidate:
                continue
            # Prefer plan columns that exist in the cube; otherwise trust the
            # qualified reference the plan already attributed to this table.
            if not known_columns or candidate in known_columns:
                return candidate

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


def _cube_column_names(cube: dict) -> set:
    """Collect column_name values from a cube's columns and measures."""
    names: set = set()
    for bucket in ("columns", "measures"):
        for item in cube.get(bucket) or []:
            if isinstance(item, dict):
                col_name = item.get("column_name")
                if col_name and str(col_name).strip():
                    names.add(str(col_name).strip())
    return names


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

    def process_flow(self, state: SQLModel):
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
            session_cookie, metadata_name, location, table_names, cube_metadata,
            state["dbname"], state.get("query_plan"),
        ) or {}
        logger.info(f"The output obtained for sql fetch {output}")

        if is_join_api_error(output):
            logger.warning(
                "Join API returned an error for tables=%s; continuing without joins: %s",
                table_names,
                output.get("message") or output.get("className") or output,
            )
            state["hi_sql"] = json.dumps(output)
            state["required_joins"] = ""
            return state

        dummy_sql = output.get("query", "") or ""
        dummy_sql_join = ""
        if dummy_sql:
            dummy_sql_join = self.extract_full_joins(dummy_sql, state.get("dialect"))
        if dummy_sql_join:
            dummy_sql = "\n".join(dummy_sql_join)
            output["required_joins"] = dummy_sql
        state["hi_sql"] = json.dumps(output)
        state["required_joins"] = dummy_sql
        return state

    def fetch_sql(self, session_cookie: str, metadata, location, tables=None,
                  cube_metadata=None, dbname="", query_plan=None) -> dict:
        tables = tables or []
        columns = []
        plan_columns_by_table = _plan_columns_by_table(query_plan)
        reduced_cubes = []
        for cube in iter_cube_entries(cube_metadata):
            source_table = cube.get("database_table")
            if source_table and source_table in tables:
                reduced_cubes.append(cube)

        for item in reduced_cubes:
            table_name = item.get("database_table")
            column_name = _resolve_join_column(
                item, plan_columns_by_table.get(table_name)
            )
            if not column_name or not table_name:
                logger.warning(
                    "Skipping cube %s: no unique_identifier_of_table, query-plan "
                    "column, or columns[0].column_name",
                    table_name,
                )
                continue
            columns.append(
                build_column(f"{table_name}.{column_name}", column_name, dbname=dbname)
            )

        if not columns:
            logger.warning("No join columns resolved from cube_metadata; skipping join API call")
            return {}

        form_data = {
            "location": location,
            "metadataFileName": metadata,
            "columns": columns,
            "limitBy": 1,
            "prependTableNameToAlias": False,
        }
        payload_json = {
            "type": "adhoc",
            "serviceType": "report",
            "service": "generateQuery",
            "formData": json.dumps(form_data),
            "requestId": uuid.uuid4().hex
        }
        api_response = fetch_service_api(session_cookie=session_cookie, service_json=payload_json)
        return (api_response or {}).get("response") or {}
