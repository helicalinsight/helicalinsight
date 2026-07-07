export const config = {
    "label": "employee_name",
    "mode": "auto",
    "alias": "employee_name",
    "mapping": {
        "isEnabled": true,
        "unique": true,
        "valueDBFunction": {
            "key": "sql.text.lower",
            "description": "Converts all characters in the specified string to lowercase. Example: LOWER('BENGALURU') result: bengaluru ",
            "value": "LOWER",
            "signature": "lower(${string})",
            "returns": "text",
            "parameters": [
                {
                    "name": "string",
                    "column": true,
                    "value": "HIUSER.employee_details.employee_name"
                }
            ]
        },
        "DisplayDBFunction": {
            "key": "sql.text.upper",
            "description": "Converts all characters in a string to uppercase. Example: UPPER('bengaluru') result: BENGALURU",
            "value": "UPPER",
            "signature": "upper(${string})",
            "returns": "text",
            "parameters": [
                {
                    "name": "string",
                    "column": true,
                    "value": "employee_details.employee_name"
                }
            ]
        },
        "isDefaultFunction": true,
        "valueDisplayMap": [],
        "valueAliasName": "random",
        "orderBy": {
            "display": "asc",
            "value": "none"
        },
        "valueColumn": {
            "alias": "employee_name",
            "fullyQualifiedColumn": "employee_details.employee_name",
            "columnId": "3a91c58b-388e-4617-b622-3b93a3c54766",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
                "java.lang.String": "text"
            }
        },
        "displayColumn": {
            "alias": "employee_name",
            "fullyQualifiedColumn": "employee_details.employee_name",
            "columnId": "3a91c58b-388e-4617-b622-3b93a3c54766",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
                "java.lang.String": "text"
            }
        }
    },
    "dataType": "text"
}