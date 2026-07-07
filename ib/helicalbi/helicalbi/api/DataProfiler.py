import json
import logging
import uuid

from helicalbi.api.HttpCallService import fetch_service_api
from helicalbi.api.SqlExecutorModel import SqlExecutorModel

logger = logging.getLogger(__name__)

SQL_TEMPLATES = {
    "numeric": """
            SELECT
                COUNT(*) - COUNT("{col}") AS null_count,
                COUNT(DISTINCT "{col}") AS distinct_count,
                MIN("{col}") AS min_value,
                MAX("{col}") AS max_value,
                AVG("{col}") AS avg_value
            FROM "{table}"
            """,
    "date": """
            SELECT
                COUNT(*) - COUNT("{col}") AS null_count,
                COUNT(DISTINCT "{col}") AS distinct_count,
                MIN("{col}") AS min_value,
                MAX("{col}") AS max_value
            FROM "{table}"
            """,
    "string": """
    SELECT
        COUNT(*) - COUNT("{col}") AS null_count,
        COUNT(DISTINCT "{col}") AS distinct_count
    FROM "{table}"
""",
    "string_val": """
    SELECT "{col}" as val, COUNT(*) AS value_count
    FROM "{table}"
    WHERE "{col}" IS NOT NULL
    GROUP BY "{col}"
    ORDER BY value_count DESC
    FETCH FIRST 3 ROW ONLY
"""
}

def get_query(table, column, datatype):
    if datatype == "dateTime":
        datatype= "date"
    if datatype == "text":
        datatype= "string"
    if datatype == "boolean":
        datatype= "string"

    template = SQL_TEMPLATES.get(datatype)
    strg = f"Unsupported datatype {datatype}"
    logger.debug("Data profiler datatype check: %s", strg)
    if not template:
        raise ValueError(strg)

    return template.format(table=table, col=column)



def get_profile_values(sqlModel: SqlExecutorModel, session_cookie: str):
    logger.info("Profiling request for table=%s column=%s", sqlModel.table, sqlModel.column)
    sql = get_query(sqlModel.table, sqlModel.column, sqlModel.data_type)
    sql2 = ""
    if sqlModel.data_type=="text":
        sql2 = get_query(sqlModel.table, sqlModel.column, "string_val")
    payload_json = prepare_formdata(sql, sqlModel)

    # print(payload_json)
    api_response = fetch_service_api(session_cookie=session_cookie, service_json=payload_json)
    data_from_sql = api_response["response"]["data"][0]
    if sql2:
        payload_json = prepare_formdata(sql2, sqlModel)
        api_response2 = fetch_service_api(session_cookie=session_cookie, service_json=payload_json)
        data_from_sql["column_stats"]=api_response2["response"]["data"]

    return data_from_sql


def prepare_formdata(sql, sqlModel):
    form_data = {
        "location": sqlModel.md_location,
        "uniqueId": True,
        "metadataFileName": sqlModel.md_file_name,
        "provideJoins": True,
        "replaceView": True,
        "runtimeView": True,
        
        "refresh": True,
        "classifier": "db.workflow",
        "query": sql
    }
    payload_json = {
        "type": "adhoc",
        "serviceType": "report",
        "service": "executeQuery",
        "formData": json.dumps(form_data),
        "requestId": str(uuid.uuid4())
    }
    return payload_json


