export const getVisulizationType = ({ type }) => {
  switch (type) {
    case "table":
      return "Table";
    default:
      break;
  }
};

export const derivedFormDataConvertorToReportProps = ({
  derivedFormData = {},
  metadata = {},
}) => {
  // const formData = metadata.formData || {};
  const columns = derivedFormData.columns || [];
  const rows = derivedFormData.rows || [];
  const filters = derivedFormData.where || [];
  // {
  //     "visualization": "table",
  //     "columns": [
  //         "month"
  //     ],
  //     "limit": "",
  //     "from": [],
  //     "where": [
  //         {
  //             "filterValues": "null 2022 2021",
  //             "filterCondition": "",
  //             "filterColumn": ""
  //         }
  //     ],
  //     "groupBy": [
  //         "month"
  //     ],
  //     "aggregate": [
  //         {
  //             "aggregateColumnName": "travels",
  //             "aggregateFunction": "Total"
  //         }
  //     ]
  // }
  // const metadataInfo = metadata?.formData
  //   ? metadata?.formData
  //   : {
  //       location: metadata.location,
  //       metadataFileName: metadata.metadataFileName,
  //     };
  // {
  // "classifier": "db.generic",
  // "name": "HIUSER",
  // "dataSource": {
  //     "sync": false,
  //     "id": "1",
  //     "catSchemaPredicted": false,
  //     "catalog": "",
  //     "schema": "HIUSER",
  //     "type": "dynamicDataSource",
  //     "baseType": "global.jdbc"
  // },
  // "uniqueId": `Metadata_1.metadata`,
  // "tables": {
  //     "travel_details": {
  //         "id": "8a28627d07d04ef096d9935f12e0c7e9",
  //         "alias": "travel_details",
  //         "columns": {
  //             "booking_platform": {
  //                 "alias": "booking_platform",
  //                 "fullyQualifiedColumn": "travel_details.booking_platform",
  //                 "columnId": "fd2485db-11d7-431b-ae57-673960e5cd7f",
  //                 "defaultFunction": "db.generic.groupBy.group",
  //                 "type": {
  //                     "java.lang.String": "text"
  //                 }
  //             }
  //         },
  //         "name": "travel_details"
  //     }
  // },
  // "sets": [
  //     [
  //         "travel_details",
  //     ]
  // ],
  // metadataFileName: formData.metadataFileName,
  // location: formData.location,
  // };
  if (process.env.NODE_ENV === "test") {
    return {
      mode: "instant-bi",
      metadata: metadata,
      columns,
      rows,
      filters,
      // marks: [
      //   {
      //     table: "travel_details",
      //     column: "booking_platform",
      //     markType: "color",
      //   },
      // ],
      visualisationType: getVisulizationType({
        type: derivedFormData.visualization,
      }),
    };
  }

  return {
    mode: "instant-bi",
    metadata: metadata,
    // columns: [{ table: "travel_details", column: "booking_platform" }],
    columns: [],
    rows: [{ table: "travel_details", column: "booking_platform" }],
    // filters: [
    //   { table: "travel_details", column: "booking_platform", values: "Agent" },
    // ],
    // marks: [
    //   {
    //     table: "travel_details",
    //     column: "booking_platform",
    //     markType: "color",
    //   },
    // ],
    visualisationType: getVisulizationType({
      type: derivedFormData.visualization,
    }),
  };
};
