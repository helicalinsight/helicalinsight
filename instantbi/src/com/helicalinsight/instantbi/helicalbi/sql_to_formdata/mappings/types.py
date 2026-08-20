"""Data-type heuristics + Java backend type mapping (filter-conditions-map)."""

BACKEND_DATA_TYPE = {
    "text": "java.lang.String",
    "boolean": "java.lang.Boolean",
    "numeric": "java.lang.Integer",
    "date": "java.sql.Date",
    "time": "java.sql.Time",
    "dateTime": "java.sql.Timestamp",
    "other": "java.lang.Object",
}

# Column-name hints when metadata is absent
DATA_TYPE_HEURISTICS = {
    "date": "dateTime",
    "time": "dateTime",
    "timestamp": "dateTime",
    "cost": "numeric",
    "amount": "numeric",
    "price": "numeric",
    "id": "numeric",
    "count": "numeric",
    "qty": "numeric",
    "quantity": "numeric",
}


def infer_data_type(column_name: str, *, has_aggregate: bool = False) -> dict:
    name = (column_name or "").lower()
    data_type = "text"
    if has_aggregate:
        data_type = "numeric"
    else:
        for hint, dtype in DATA_TYPE_HEURISTICS.items():
            if hint in name:
                data_type = dtype
                break
    return {
        "backendDataType": BACKEND_DATA_TYPE[data_type],
        "dataType": data_type,
    }
