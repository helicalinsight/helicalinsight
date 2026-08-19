TABLE_PATH = "sampletraveldata"


def build_column(column_name, alias, col_id=1000):
    return {
        "column": {
            "name": f"{TABLE_PATH}.{column_name}",
            "id": str(col_id)
        },
        "alias": alias,
        "floatingType": "discrete"
    }


def convert(first_json):
    columns = []

    for col in first_json.get("columns", []):
        column_name = col["columnName"]
        alias = col.get("alias")
        columns.append(build_column(column_name, alias))

    return columns
