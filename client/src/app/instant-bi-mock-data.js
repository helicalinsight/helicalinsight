export const getInstantBIReportState = ({ dir, file }) => {
  if (dir === "sai_ganesh" && file === "test.instant") {
    return {
      status: 1,
      response: {
        reportName: "searchValueFinalTest",
        metadata: {
          location: "sai_ganesh",
          metadataFileName: "Metadata_1.metadata",
          databaseName: "HIUSER",
          data: {
            classifier: "db.generic",
            name: "HIUSER",
            dataSource: {
              sync: false,
              id: "1",
              catSchemaPredicted: false,
              catalog: "",
              schema: "HIUSER",
              type: "dynamicDataSource",
              baseType: "global.jdbc",
            },
            uniqueId: "Metadata_1",
            tables: {
              dimdate: {
                id: "4ac5d9f68b58bd7c0d179146e46795be",
                alias: "dimdate",
                columns: {
                  dim_id: {
                    alias: "dim_id",
                    fullyQualifiedColumn: "dimdate.dim_id",
                    columnId: "d3ed050d-c444-4f95-9c6e-311cbfc5f2ec",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Integer": "numeric",
                    },
                  },
                  fiscal_year: {
                    alias: "fiscal_year",
                    fullyQualifiedColumn: "dimdate.fiscal_year",
                    columnId: "0cd69447-a985-4f83-983e-422c10fc6808",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.sql.Date": "date",
                    },
                  },
                  modified_date: {
                    alias: "modified_date",
                    fullyQualifiedColumn: "dimdate.modified_date",
                    columnId: "06902d8a-9f2b-43b5-875b-21e1179a581c",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.sql.Timestamp": "dateTime",
                    },
                  },
                  date_key: {
                    alias: "date_key",
                    fullyQualifiedColumn: "dimdate.date_key",
                    columnId: "6f7eff52-a72f-4dea-af57-4a919f8664c5",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  day_number: {
                    alias: "day_number",
                    fullyQualifiedColumn: "dimdate.day_number",
                    columnId: "2fe326b6-f2b2-42b7-ab54-23a85bf0be33",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  fiscal_month_name: {
                    alias: "fiscal_month_name",
                    fullyQualifiedColumn: "dimdate.fiscal_month_name",
                    columnId: "1bbfb7e9-4eca-407e-a033-7805f7aa11dc",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  fiscal_month_label: {
                    alias: "fiscal_month_label",
                    fullyQualifiedColumn: "dimdate.fiscal_month_label",
                    columnId: "8bcd4913-2673-4613-b131-1ce45c83a14e",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  created_date: {
                    alias: "created_date",
                    fullyQualifiedColumn: "dimdate.created_date",
                    columnId: "d04f6f98-2004-4f4e-ada2-af906cf374e4",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  created_time: {
                    alias: "created_time",
                    fullyQualifiedColumn: "dimdate.created_time",
                    columnId: "e668ff33-94b5-4d89-b189-47bff73c63bc",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  rating: {
                    alias: "rating",
                    fullyQualifiedColumn: "dimdate.rating",
                    columnId: "5e5c71cc-1f95-4ebe-b1ec-dd5f3ddf5f44",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                },
                name: "dimdate",
              },
              employee_details: {
                id: "4e1fd245f4d13b77be423a43f01d80b2",
                alias: "employee_details",
                columns: {
                  employee_id: {
                    alias: "employee_id",
                    fullyQualifiedColumn: "employee_details.employee_id",
                    columnId: "d3277c5c-4fdb-4bf2-92df-cd52c9d8f89a",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Integer": "numeric",
                    },
                  },
                  employee_name: {
                    alias: "employee_name",
                    fullyQualifiedColumn: "employee_details.employee_name",
                    columnId: "a3465a57-0205-44fb-a4df-27b497cc97c5",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  age: {
                    alias: "age",
                    fullyQualifiedColumn: "employee_details.age",
                    columnId: "f8f074b3-4af8-45c8-9aac-7be3f2000dc6",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Integer": "numeric",
                    },
                  },
                  address: {
                    alias: "address",
                    fullyQualifiedColumn: "employee_details.address",
                    columnId: "815869f2-744e-4cb3-aacd-9b23ea643357",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                },
                name: "employee_details",
              },
              geo_cordinates: {
                id: "be534112989b616b194bc59c2fb25a42",
                alias: "geo_cordinates",
                columns: {
                  location_id: {
                    alias: "location_id",
                    fullyQualifiedColumn: "geo_cordinates.location_id",
                    columnId: "065e66b1-3ce6-4e74-93fc-34d3a3785dbb",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Integer": "numeric",
                    },
                  },
                  location: {
                    alias: "location",
                    fullyQualifiedColumn: "geo_cordinates.location",
                    columnId: "bae8dd2e-b5a7-4783-8281-a6c20a543b2e",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  latitude: {
                    alias: "latitude",
                    fullyQualifiedColumn: "geo_cordinates.latitude",
                    columnId: "70b454c1-0831-4195-ad44-964ddcc3e706",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Double": "numeric",
                    },
                  },
                  longitude: {
                    alias: "longitude",
                    fullyQualifiedColumn: "geo_cordinates.longitude",
                    columnId: "10dee500-c040-4d1e-86ee-b9ac41522ea4",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Double": "numeric",
                    },
                  },
                },
                name: "geo_cordinates",
              },
              meeting_details: {
                id: "9645c648a1c0dbeec1287aaf1e996db3",
                alias: "meeting_details",
                columns: {
                  meeting_id: {
                    alias: "meeting_id",
                    fullyQualifiedColumn: "meeting_details.meeting_id",
                    columnId: "204dc18c-4e71-46fd-af95-aaf8250e2033",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Integer": "numeric",
                    },
                  },
                  meeting_date: {
                    alias: "meeting_date",
                    fullyQualifiedColumn: "meeting_details.meeting_date",
                    columnId: "f10efd37-5a52-4427-b3b9-226393c2fe91",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.sql.Timestamp": "dateTime",
                    },
                  },
                  meeting_by: {
                    alias: "meeting_by",
                    fullyQualifiedColumn: "meeting_details.meeting_by",
                    columnId: "7bfb4ca4-7d98-492e-8046-400d35650ecf",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Integer": "numeric",
                    },
                  },
                  client_name: {
                    alias: "client_name",
                    fullyQualifiedColumn: "meeting_details.client_name",
                    columnId: "95aded92-b924-4944-9867-29bcf2cc9c2f",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  meeting_purpose: {
                    alias: "meeting_purpose",
                    fullyQualifiedColumn: "meeting_details.meeting_purpose",
                    columnId: "5fcc27a5-8bfc-48bf-b678-814e52f714e6",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  meeting_impact: {
                    alias: "meeting_impact",
                    fullyQualifiedColumn: "meeting_details.meeting_impact",
                    columnId: "9df13b03-2df4-4075-b68d-d3b0ab3d1d83",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  meet_cancellation_status: {
                    alias: "meet_cancellation_status",
                    fullyQualifiedColumn:
                      "meeting_details.meet_cancellation_status",
                    columnId: "74a38576-2926-4ce9-8072-4dca62000c72",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  cancellation_reason: {
                    alias: "cancellation_reason",
                    fullyQualifiedColumn: "meeting_details.cancellation_reason",
                    columnId: "ee1efb86-f692-4d1c-a757-925f61842660",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                },
                name: "meeting_details",
              },
              travel_details: {
                id: "8a28627d07d04ef096d9935f12e0c7e9",
                alias: "travel_details",
                columns: {
                  travel_id: {
                    alias: "travel_id",
                    fullyQualifiedColumn: "travel_details.travel_id",
                    columnId: "b1e4edb4-9c77-4181-b2ba-81ef7aecf133",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Integer": "numeric",
                    },
                  },
                  travel_date: {
                    alias: "travel_date",
                    fullyQualifiedColumn: "travel_details.travel_date",
                    columnId: "cac92273-bb4d-4da5-9b51-027e8378043f",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.sql.Timestamp": "dateTime",
                    },
                  },
                  travel_type: {
                    alias: "travel_type",
                    fullyQualifiedColumn: "travel_details.travel_type",
                    columnId: "165082a5-b87b-4370-b5ad-871674308225",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  travel_medium: {
                    alias: "travel_medium",
                    fullyQualifiedColumn: "travel_details.travel_medium",
                    columnId: "8c31598b-f6cc-48c6-8e73-0af7f087de0e",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  source_id: {
                    alias: "source_id",
                    fullyQualifiedColumn: "travel_details.source_id",
                    columnId: "187e8a33-a013-499d-b2f5-5a0ac006b7e8",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Integer": "numeric",
                    },
                  },
                  source: {
                    alias: "source",
                    fullyQualifiedColumn: "travel_details.source",
                    columnId: "23d9f9a9-c284-473b-be85-d27ec5f6b300",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  destination_id: {
                    alias: "destination_id",
                    fullyQualifiedColumn: "travel_details.destination_id",
                    columnId: "ec4a048b-824f-4ca8-ad7f-a5e4c5854ce3",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Integer": "numeric",
                    },
                  },
                  destination: {
                    alias: "destination",
                    fullyQualifiedColumn: "travel_details.destination",
                    columnId: "532d2fe2-85ac-416f-aa3e-d3f1205a3c11",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  travel_cost: {
                    alias: "travel_cost",
                    fullyQualifiedColumn: "travel_details.travel_cost",
                    columnId: "513c3a99-f6ad-46a5-a2e8-6c209a25a912",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Integer": "numeric",
                    },
                  },
                  mode_of_payment: {
                    alias: "mode_of_payment",
                    fullyQualifiedColumn: "travel_details.mode_of_payment",
                    columnId: "8610b1d0-ae2f-409c-b3a5-c2c5e1f3be8a",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  booking_platform: {
                    alias: "booking_platform",
                    fullyQualifiedColumn: "travel_details.booking_platform",
                    columnId: "918295d1-b2dc-42c9-a23b-97ddfb4020c9",
                    defaultFunction: "db.generic.groupBy.group",
                    type: {
                      "java.lang.String": "text",
                    },
                  },
                  travelled_by: {
                    alias: "travelled_by",
                    fullyQualifiedColumn: "travel_details.travelled_by",
                    columnId: "aa0dc3b2-a629-4b6e-83bb-fd1e9b0a8079",
                    defaultFunction: "db.generic.aggregate.sum",
                    type: {
                      "java.lang.Integer": "numeric",
                    },
                  },
                },
                name: "travel_details",
              },
            },
            sets: [
              [
                "geo_cordinates",
                "dimdate",
                "travel_details",
                "employee_details",
                "meeting_details",
              ],
            ],
            metadataName: "Metadata_1",
            metadataDir: "sai_ganesh",
          },
        },
        state: {
          searchValue: "dasfasddsfadsfdfs",
          fields: [
            {
              column: "travel_details.booking_platform",
              label: "booking_platform",
              id: "8e3a297c-2b4b-41dc-b146-3a59dace675a",
              type: {
                backendDataType: "java.lang.String",
                dataType: "text",
              },
              autogen_alias: "booking_platform",
              isNormalTable: true,
              tableAlias: "travel_details",
              groupBy: ["db.generic.groupBy.group"],
              orderByColumn: false,
              showOrderByColumn: false,
              addedAs: "column",
              floatingType: "discrete",
              functionsDefinition: "",
              applyBeforeAggregate: false,
              hiddenIncludeInResultSet: false,
              metaDataAlias: "booking_platform",
            },
            {
              column: "travel_details.mode_of_payment",
              label: "mode_of_payment",
              id: "ea4b4bfb-1fa9-4199-8cff-f6ca87f1a3f1",
              type: {
                backendDataType: "java.lang.String",
                dataType: "text",
              },
              autogen_alias: "mode_of_payment",
              isNormalTable: true,
              tableAlias: "travel_details",
              groupBy: ["db.generic.groupBy.group"],
              orderByColumn: false,
              showOrderByColumn: false,
              addedAs: "column",
              floatingType: "discrete",
              functionsDefinition: "",
              applyBeforeAggregate: false,
              hiddenIncludeInResultSet: false,
              metaDataAlias: "mode_of_payment",
            },
          ],
          filters: [],
          marksList: [
            {
              value: "_all_",
              id: "7d5cc43f-62fa-4e11-adf5-f12dad7bde52",
              subVizType: "",
              color: {
                fields: [],
              },
              size: {
                fields: [],
              },
              label: {
                fields: [],
              },
              tooltip: {
                fields: [],
              },
              shape: {
                fields: [],
              },
              detail: {
                fields: [],
              },
            },
          ],
          activeMark: "7d5cc43f-62fa-4e11-adf5-f12dad7bde52",
          activeTool: "2",
          scripts: [
            {
              id: "pre-execution",
              value: "",
              title: "Pre Execution",
            },
            {
              id: "pre-fetch",
              value: "",
              title: "Pre Fetch",
            },
            {
              id: "post-fetch",
              value: "",
              title: "Post Fetch",
            },
            {
              id: "post-execution",
              value: "",
              title: "Post Execution",
            },
          ],
          selectedScript: "pre-execution",
          styles: "",
          options: {
            limitBy: 1000,
            sample: "sample",
            prependTableNameToAlias: false,
          },
          interactiveMode: false,
          drillDown: false,
          drillThrough: false,
          drillDownList: [],
          currentDrillDown: "",
          drillThroughList: [],
          toolbarConfig: {
            selectable: false,
          },
          selectedType: "Table",
          customStyles: "",
          customScripts: [],
          analytics: [
            {
              value: false,
              key: "rowSubTotals",
              label: "Row Sub Totals",
            },
            {
              value: false,
              key: "columnSubTotals",
              label: "Column Sub Totals",
            },
            {
              value: false,
              key: "rowGrandTotals",
              label: "Row Grand Totals",
            },
            {
              value: false,
              key: "columnGrandTotals",
              label: "Column Grand Totals",
            },
          ],
          properties: {
            title: {
              show: false,
              value: "",
              padding: 0,
              fontSize: 32,
              fontColor: {
                a: 1,
                b: 0,
                g: 0,
                r: 0,
              },
              alignment: "center",
              position: "top",
            },
            subTitle: {
              show: false,
              value: "",
              padding: 0,
              fontSize: 24,
              fontColor: {
                a: 1,
                b: 0,
                g: 0,
                r: 0,
              },
              alignment: "center",
              position: "top",
            },
            format: {
              formatFields: [],
              formatDatatype: "",
              activeFieldId: "",
            },
            cache: {
              isCacheEnabled: true,
              interval: "00:00:00",
            },
          },
          showHiddenColumns: false,
          showHiddenRows: false,
          database: "HIUSER",
        },
      },
    };
  }
};
export const getInstantBISaveResponse = ({ location, reportName }) => {
  if (location === "sai_ganesh" && reportName === "test.instant") {
    return {
      status: 1,
      response: {
        uuid: "test.instant",
        location: "sai_ganesh",
        message: "Successfully saved instant file",
        data: [
          {
            path: "sai_ganesh/test.instant",
            extension: "instant",
            permissionLevel: "5",
            name: "test.instant",
            options: {},
            lastModified: 1666694087211,
            title: "test",
            type: "file",
          },
        ],
      },
    };
  }
};
export const getInstantBINlpstringAPI = ({ formData }) => {
  if (formData.nlpString === "Total travels per month in  2022 and 2021") {
    return {
      status: 1,
      response: {
        derivedFormdata: {
          visualization: "table",
          columns: ["month"],
          limit: "",
          from: [],
          where: [
            {
              filterValues: ["null", "2022", "2021"],
              filterCondition: "",
              filterColumn: "",
            },
          ],
          groupBy: ["month"],
          aggregate: [
            {
              aggregateColumnName: "travels",
              aggregateFunction: "Total",
            },
          ],
          // lematizedColumns: {
          //   table: {
          //     commonColumn1: ["col1", "col2", "col3"],
          //     commonColumn2: ["col1", "col2", "col3"],
          //   },
          // },
        },
      },
    };
  }
};
