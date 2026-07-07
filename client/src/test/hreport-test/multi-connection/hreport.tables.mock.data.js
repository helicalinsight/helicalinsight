export const md_multi_conn_res = {
    "classifier": "db.generic",
    "name": "HIUSER",
    "dataSource": {
      "sync": false,
      "id": "1",
      "catSchemaPredicted": false,
      "catalog": "",
      "schema": "HIUSER",
      "type": "dynamicDataSource",
      "baseType": "global.jdbc"
    },
    "uniqueId": "MysqlDery1",
    "tables": {
      "dimdate": {
        "id": "4ac5d9f68b58bd7c0d179146e46795be",
        "alias": "dimdate",
        "columns": {
          "dim_id": {
            "alias": "dim_id",
            "fullyQualifiedColumn": "dimdate.dim_id",
            "columnId": "0192a847-522c-4a06-85af-93e31b45ebed",
            "defaultFunction": "db.generic.aggregate.sum",
            "type": {
              "java.lang.Integer": "numeric"
            }
          },
          "fiscal_year": {
            "alias": "fiscal_year",
            "fullyQualifiedColumn": "dimdate.fiscal_year",
            "columnId": "7c8e0aa0-d815-4241-8ad1-62330b9bce64",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.sql.Date": "date"
            }
          },
          "modified_date": {
            "alias": "modified_date",
            "fullyQualifiedColumn": "dimdate.modified_date",
            "columnId": "d32ef917-bb7d-4f44-835e-58608437d62e",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.sql.Timestamp": "dateTime"
            }
          },
          "date_key": {
            "alias": "date_key",
            "fullyQualifiedColumn": "dimdate.date_key",
            "columnId": "1e7bc352-b57a-4e91-a649-ecb407e3ef95",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.lang.String": "text"
            }
          },
          "day_number": {
            "alias": "day_number",
            "fullyQualifiedColumn": "dimdate.day_number",
            "columnId": "94924a35-6325-4f98-8c0a-20a62273436d",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.lang.String": "text"
            }
          },
          "fiscal_month_name": {
            "alias": "fiscal_month_name",
            "fullyQualifiedColumn": "dimdate.fiscal_month_name",
            "columnId": "6923b00b-0e40-4eb5-be6b-39869d3f016a",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.lang.String": "text"
            }
          },
          "fiscal_month_label": {
            "alias": "fiscal_month_label",
            "fullyQualifiedColumn": "dimdate.fiscal_month_label",
            "columnId": "65a02bc3-9097-4a38-b256-e7438c6a8384",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.lang.String": "text"
            }
          },
          "created_date": {
            "alias": "created_date",
            "fullyQualifiedColumn": "dimdate.created_date",
            "columnId": "1746b3f9-1480-4497-a3fe-d66bd24c576e",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.lang.String": "text"
            }
          },
          "created_time": {
            "alias": "created_time",
            "fullyQualifiedColumn": "dimdate.created_time",
            "columnId": "3bf1a483-417e-418c-abbd-51e6227bbb1c",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.lang.String": "text"
            }
          },
          "rating": {
            "alias": "rating",
            "fullyQualifiedColumn": "dimdate.rating",
            "columnId": "83dd07ca-7853-435e-b888-13c09e379ef4",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.lang.String": "text"
            }
          }
        },
        "name": "dimdate"
      },
      "employee_details": {
        "id": "9645c648a1c0dbeec1287aaf1e996db3",
        "alias": "meeting_details",
        "columns": {
          "meeting_id": {
            "alias": "meeting_id",
            "fullyQualifiedColumn": "meeting_details.meeting_id",
            "columnId": "58d5c5b6-d3cb-4ee9-b95d-1cdcfbe8e038",
            "defaultFunction": "db.generic.aggregate.sum",
            "type": {
              "java.lang.Integer": "numeric"
            }
          },
          "meeting_date": {
            "alias": "meeting_date",
            "fullyQualifiedColumn": "meeting_details.meeting_date",
            "columnId": "44422989-997a-40bd-9e87-17f5221b1cb4",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.sql.Timestamp": "dateTime"
            }
          },
          "meeting_by": {
            "alias": "meeting_by",
            "fullyQualifiedColumn": "meeting_details.meeting_by",
            "columnId": "7eba1e91-83d1-4839-ba2d-7a1beca23778",
            "defaultFunction": "db.generic.aggregate.sum",
            "type": {
              "java.lang.Integer": "numeric"
            }
          },
          "client_name": {
            "alias": "client_name",
            "fullyQualifiedColumn": "meeting_details.client_name",
            "columnId": "53b1a600-100b-4b9b-a5e2-a817dd5552e8",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.lang.String": "text"
            }
          },
          "meeting_purpose": {
            "alias": "meeting_purpose",
            "fullyQualifiedColumn": "meeting_details.meeting_purpose",
            "columnId": "d85a2e91-fe18-4e44-a9ec-abdb9fe2a3e8",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.lang.String": "text"
            }
          },
          "meeting_impact": {
            "alias": "meeting_impact",
            "fullyQualifiedColumn": "meeting_details.meeting_impact",
            "columnId": "20b3237a-5d6d-473b-aeb1-bd4652d4b8c4",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.lang.String": "text"
            }
          },
          "meet_cancellation_status": {
            "alias": "meet_cancellation_status",
            "fullyQualifiedColumn": "meeting_details.meet_cancellation_status",
            "columnId": "9bedac46-f6ae-420d-a142-2912c455aa75",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.lang.String": "text"
            }
          },
          "cancellation_reason": {
            "alias": "cancellation_reason",
            "fullyQualifiedColumn": "meeting_details.cancellation_reason",
            "columnId": "68d48a76-7f82-4c9c-8e5c-2b16b6b127e5",
            "defaultFunction": "db.generic.groupBy.group",
            "type": {
              "java.lang.String": "text"
            }
          }
        },
        "name": "meeting_details"
      }
    },
    "sets": [
      [
        "meeting_details",
        "dimdate"
      ]
    ],
    "joins": [
      {
        "id": "e971fddf3d7b4e9ecbfaada0f65e6051",
        "type": "inner",
        "operator": "=",
        "left": {
          "table": "meeting_details",
          "column": "meeting_impact",
          "alias": "meeting_details.meeting_impact"
        },
        "right": {
          "table": "dimdate",
          "column": "fiscal_month_name",
          "alias": "dimdate.fiscal_month_name"
        }
      }
    ],
    "connections": [
      {
        "classifier": "db.generic",
        "name": "SampleTravelData",
        "dataSource": {
          "sync": false,
          "id": "1002",
          "catSchemaPredicted": false,
          "catalog": "SampleTravelData",
          "schema": "",
          "type": "dynamicDataSource",
          "baseType": "global.jdbc"
        },
        "uniqueId": "MysqlDery1",
        "tables": {
          "tasks": {
            "id": "73210e5e809f316b5b727202caffbf2f",
            "alias": "tasks",
            "columns": {
              "id": {
                "alias": "id",
                "fullyQualifiedColumn": "tasks.id",
                "columnId": "6a671f38-aaef-4366-bdda-900c8b54fc56",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                  "java.lang.Integer": "numeric"
                }
              },
              "title": {
                "alias": "title",
                "fullyQualifiedColumn": "tasks.title",
                "columnId": "d37040d0-9175-459f-a384-43a96a1fc1cf",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                  "java.lang.String": "text"
                }
              },
              "completed": {
                "alias": "completed",
                "fullyQualifiedColumn": "tasks.completed",
                "columnId": "b35dd944-cd9e-45bb-8320-409f7d665dc3",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                  "java.lang.Boolean": "boolean"
                }
              }
            },
            "name": "tasks"
          },
          "employee_details": {
            "id": "152371825108bf241d5e58d460282bf0",
            "alias": "employee_details",
            "columns": {
              "employee_id": {
                "alias": "employee_id",
                "fullyQualifiedColumn": "employee_details.employee_id",
                "columnId": "c92ce2b3-a3f2-4265-a79b-a92c0cc25ffa",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                  "java.lang.Integer": "numeric"
                }
              },
              "employee_name": {
                "alias": "employee_name",
                "fullyQualifiedColumn": "employee_details.employee_name",
                "columnId": "5d670be7-b10f-4a81-9962-e193c6b4a1a1",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                  "java.lang.String": "text"
                }
              },
              "age": {
                "alias": "age",
                "fullyQualifiedColumn": "employee_details.age",
                "columnId": "36112396-65be-4492-9427-760912d2b808",
                "defaultFunction": "db.generic.aggregate.sum",
                "type": {
                  "java.lang.Integer": "numeric"
                }
              },
              "address": {
                "alias": "address",
                "fullyQualifiedColumn": "employee_details.address",
                "columnId": "1e90d836-d40e-4447-937c-d2bf8d4f4a02",
                "defaultFunction": "db.generic.groupBy.group",
                "type": {
                  "java.lang.String": "text"
                }
              }
            },
            "name": "employee_details"
          }
        },
        "sets": [
          [
            "employee_details",
            "tasks"
          ]
        ],
        "joins": [
          {
            "id": "915eb98b6c4207de4a384e880e42a5d7",
            "type": "inner",
            "operator": "=",
            "left": {
              "table": "employee_details",
              "column": "employee_name",
              "alias": "employee_details.employee_name"
            },
            "right": {
              "table": "tasks",
              "column": "title",
              "alias": "tasks.title"
            }
          },
          {
            "id": "96883c9278fece1a0e0e064cdbd21317",
            "type": "inner",
            "operator": "=",
            "left": {
              "table": "employee_details",
              "column": "employee_id",
              "alias": "employee_details.employee_id"
            },
            "right": {
              "table": "tasks",
              "column": "id",
              "alias": "tasks.id"
            }
          },
          {
            "id": "9cae88661ca9f504326fca081ecc7dde",
            "type": "inner",
            "operator": "=",
            "left": {
              "table": "employee_details",
              "column": "employee_id",
              "alias": "employee_details.employee_id"
            },
            "right": {
              "table": "tasks",
              "column": "title",
              "alias": "tasks.title"
            }
          },
          {
            "id": "3e43899b016ada2b78831d64739a4ca8",
            "type": "inner",
            "operator": "=",
            "left": {
              "table": "employee_details",
              "column": "employee_name",
              "alias": "employee_details.employee_name"
            },
            "right": {
              "table": "employee_details",
              "column": "address",
              "alias": "employee_details.address"
            }
          },
          {
            "id": "3e43899b016ada2b78831d64739a4ca8",
            "type": "inner",
            "operator": "=",
            "left": {
              "table": "employee_details",
              "column": "employee_name",
              "alias": "employee_details.employee_name"
            },
            "right": {
              "table": "employee_details",
              "column": "address",
              "alias": "employee_details.address"
            }
          },
          {
            "id": "3e43899b016ada2b78831d64739a4ca8",
            "type": "inner",
            "operator": "=",
            "left": {
              "table": "employee_details",
              "column": "employee_name",
              "alias": "employee_details.employee_name"
            },
            "right": {
              "table": "employee_details",
              "column": "address",
              "alias": "employee_details.address"
            }
          }
        ],
        "connectionDatabaseId": "e31a96c0-c598-457c-9d77-12ed5df36672"
      }
    ],
    "metadataName": "Metadata_1",
    "metadataDir": "Gagan"
  }