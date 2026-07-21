import json

from helicalbi.api.DataProfiler import get_profile_values
from helicalbi.api.Metadata import get_json_data_metadata
from helicalbi.api.SqlExecutorModel import SqlExecutorModel


# ---------- helper to convert java type → simple type ----------
def map_java_type(type_dict):
    if not type_dict:
        return ""

    java_type = list(type_dict.keys())[0]

    mapping = {
        "java.lang.String": "text",
        "java.math.BigDecimal": "numeric",
        "java.lang.Boolean": "boolean",
        "java.lang.Byte": "numeric",
        "java.lang.Short": "numeric",
        "java.lang.Integer": "numeric",
        "java.lang.Long": "numeric",
        "java.lang.Float": "numeric",
        "java.lang.Double": "numeric",
        "java.sql.Date": "date",
        "java.sql.Time": "time",
        "java.sql.Timestamp": "dateTime",
        "java.lang.Object": "other",
        "java.sql.Struct": "other",
        "java.sql.Array": "other",
        "java.sql.SQLXML": "other",
        "java.sql.RowId": "other",
        "java.sql.Blob": "other"
    }

    return mapping.get(java_type, "text")


# ---------- main transform function ----------
def transform_json(session_cookie: str, metadata: str, location: str, requested_table: list):
    semantic_tables = []
    md_all = get_json_data_metadata(session_cookie, metadata, location)
    to_return={}

    to_return["metadata"]= {
        "domain": [""],
        "description": ""
    }

    for table_name, table in md_all.get("tables", {}).items():

        result = {
            "database_table": table_name,
            "columns": [],

        }

        columns = table.get("columns", {})

        for col_name, col in columns.items():
            java_type = map_java_type(col.get("type", {}))
            dimension = {

                "column_name": col_name,
            }
            if java_type != "other":
                sql_model = SqlExecutorModel(md_location=location, md_file_name=metadata,
                                             table=table_name, column=col_name, data_type=java_type)
                profile_val = get_profile_values(sql_model, session_cookie)
                profile_val["data_type"] = java_type
                dimension["column_metrics"] = profile_val

            result["columns"].append(dimension)

        semantic_tables.append(result)
    to_return["semantic_layer"] = semantic_tables
    return to_return
