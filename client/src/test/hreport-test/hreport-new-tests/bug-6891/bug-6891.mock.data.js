export const mock_data = {
  activeReport: {
    id: "247420c1-deb7-47ae-acf9-4b04ca0443d2",
    mode: "open",
    active: true,
    metadata: {
      classifier: "db.generic",
      name: "HIUSER",
      dataSource: {
        sync: false,
        id: "1400",
        catSchemaPredicted: false,
        catalog: "",
        schema: "HIUSER",
        type: "dynamicDataSource",
        baseType: "global.jdbc",
        dbId: "1302",
      },
      uniqueId: "Metadata",
      tables: {
        dimdate: {
          id: "1307",
          alias: "dimdate",
          columns: {
            dim_id: {
              alias: "dim_id",
              fullyQualifiedColumn: "dimdate.dim_id",
              id: "1644",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            fiscal_year: {
              alias: "fiscal_year",
              fullyQualifiedColumn: "dimdate.fiscal_year",
              id: "1645",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.sql.Date": "date",
              },
            },
            modified_date: {
              alias: "modified_date",
              fullyQualifiedColumn: "dimdate.modified_date",
              id: "1646",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.sql.Timestamp": "dateTime",
              },
            },
            date_key: {
              alias: "date_key",
              fullyQualifiedColumn: "dimdate.date_key",
              id: "1647",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            day_number: {
              alias: "day_number",
              fullyQualifiedColumn: "dimdate.day_number",
              id: "1648",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            fiscal_month_name: {
              alias: "fiscal_month_name",
              fullyQualifiedColumn: "dimdate.fiscal_month_name",
              id: "1649",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            fiscal_month_label: {
              alias: "fiscal_month_label",
              fullyQualifiedColumn: "dimdate.fiscal_month_label",
              id: "1650",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            created_date: {
              alias: "created_date",
              fullyQualifiedColumn: "dimdate.created_date",
              id: "1651",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            created_time: {
              alias: "created_time",
              fullyQualifiedColumn: "dimdate.created_time",
              id: "1652",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            rating: {
              alias: "rating",
              fullyQualifiedColumn: "dimdate.rating",
              id: "1653",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
          },
          name: "dimdate",
          cacheId: "4ac5d9f68b58bd7c0d179146e46795be",
          key: "eb1f1d57-9b57-4ca7-8c76-21e303cc3433",
        },
        employee_details: {
          id: "1308",
          alias: "employee_details",
          columns: {
            employee_id: {
              alias: "employee_id",
              fullyQualifiedColumn: "employee_details.employee_id",
              id: "1654",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            employee_name: {
              alias: "employee_name",
              fullyQualifiedColumn: "employee_details.employee_name",
              id: "1655",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            age: {
              alias: "age",
              fullyQualifiedColumn: "employee_details.age",
              id: "1656",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            address: {
              alias: "address",
              fullyQualifiedColumn: "employee_details.address",
              id: "1657",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
          },
          name: "employee_details",
          cacheId: "4e1fd245f4d13b77be423a43f01d80b2",
          key: "3c23d834-0288-4224-aefe-ce5465b1d374",
        },
        geo_cordinates: {
          id: "1309",
          alias: "geo_cordinates",
          columns: {
            location_id: {
              alias: "location_id",
              fullyQualifiedColumn: "geo_cordinates.location_id",
              id: "1658",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            location: {
              alias: "location",
              fullyQualifiedColumn: "geo_cordinates.location",
              id: "1659",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            latitude: {
              alias: "latitude",
              fullyQualifiedColumn: "geo_cordinates.latitude",
              id: "1660",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Double": "numeric",
              },
            },
            longitude: {
              alias: "longitude",
              fullyQualifiedColumn: "geo_cordinates.longitude",
              id: "1661",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Double": "numeric",
              },
            },
          },
          name: "geo_cordinates",
          cacheId: "be534112989b616b194bc59c2fb25a42",
          key: "14f18a1e-762c-42de-a96f-9a1e569eac81",
        },
        meeting_details: {
          id: "1310",
          alias: "meeting_details",
          columns: {
            meeting_id: {
              alias: "meeting_id",
              fullyQualifiedColumn: "meeting_details.meeting_id",
              id: "1662",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            meeting_date: {
              alias: "meeting_date",
              fullyQualifiedColumn: "meeting_details.meeting_date",
              id: "1663",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.sql.Timestamp": "dateTime",
              },
            },
            meeting_by: {
              alias: "meeting_by",
              fullyQualifiedColumn: "meeting_details.meeting_by",
              id: "1664",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            client_name: {
              alias: "client_name",
              fullyQualifiedColumn: "meeting_details.client_name",
              id: "1665",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            meeting_purpose: {
              alias: "meeting_purpose",
              fullyQualifiedColumn: "meeting_details.meeting_purpose",
              id: "1666",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            meeting_impact: {
              alias: "meeting_impact",
              fullyQualifiedColumn: "meeting_details.meeting_impact",
              id: "1667",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            meet_cancellation_status: {
              alias: "meet_cancellation_status",
              fullyQualifiedColumn: "meeting_details.meet_cancellation_status",
              id: "1668",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            cancellation_reason: {
              alias: "cancellation_reason",
              fullyQualifiedColumn: "meeting_details.cancellation_reason",
              id: "1669",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
          },
          name: "meeting_details",
          cacheId: "9645c648a1c0dbeec1287aaf1e996db3",
          key: "a690d2d8-b70c-4ede-8ee0-fba69523cf96",
        },
        travel_details: {
          id: "1311",
          alias: "travel_details",
          columns: {
            travel_id: {
              alias: "travel_id",
              fullyQualifiedColumn: "travel_details.travel_id",
              id: "1670",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            travel_date: {
              alias: "travel_date",
              fullyQualifiedColumn: "travel_details.travel_date",
              id: "1671",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.sql.Timestamp": "dateTime",
              },
            },
            travel_type: {
              alias: "travel_type",
              fullyQualifiedColumn: "travel_details.travel_type",
              id: "1672",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            travel_medium: {
              alias: "travel_medium",
              fullyQualifiedColumn: "travel_details.travel_medium",
              id: "1673",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            source_id: {
              alias: "source_id",
              fullyQualifiedColumn: "travel_details.source_id",
              id: "1674",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            source: {
              alias: "source",
              fullyQualifiedColumn: "travel_details.source",
              id: "1675",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            destination_id: {
              alias: "destination_id",
              fullyQualifiedColumn: "travel_details.destination_id",
              id: "1676",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            destination: {
              alias: "destination",
              fullyQualifiedColumn: "travel_details.destination",
              id: "1677",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            travel_cost: {
              alias: "travel_cost",
              fullyQualifiedColumn: "travel_details.travel_cost",
              id: "1678",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            mode_of_payment: {
              alias: "mode_of_payment",
              fullyQualifiedColumn: "travel_details.mode_of_payment",
              id: "1679",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            booking_platform: {
              alias: "booking_platform",
              fullyQualifiedColumn: "travel_details.booking_platform",
              id: "1680",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            travelled_by: {
              alias: "travelled_by",
              fullyQualifiedColumn: "travel_details.travelled_by",
              id: "1681",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
          },
          name: "travel_details",
          cacheId: "8a28627d07d04ef096d9935f12e0c7e9",
          key: "336c2eba-e18f-49c8-8134-13868815451f",
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
      metadataName: "Metadata",
      metadataDir: "Manish_05",
      databaseName: "HIUSER",
      formData: {
        location: "Manish_05",
        metadataFileName: "Metadata.metadata",
      },
      uid: "f6ae10f1-80dc-45c1-8543-34efd39f467e",
    },
    metadataLoading: false,
    hreportLoading: false,
    functions: {},
    databaseFunctions: {},
    fields: [
      {
        column: "travel_details.destination",
        columnID: "1677",
        label: "destination",
        id: "6bc49419-049b-472e-864b-b58f28daabce",
        type: {
          backendDataType: "java.lang.String",
          dataType: "text",
        },
        autogen_alias: "destination",
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
        metaDataAlias: "destination",
        databaseName: "HIUSER",
        geographicType: "",
        isView: false,
      },
    ],
    filters: [
      {
        column: "travel_details.destination",
        label: "destination",
        dataType: "text",
        backendDataType: "java.lang.String",
        condition: "IS_ONE_OF",
        values: ["Ambala", "Amsterdam", "Ahmedabad", "Agra", "Auckland"],
        valuesMode: "custom",
        mode: "auto",
        orderBy: "",
        valuesRange: {},
        rangeValuesType: "",
        dateTimeToggle: false,
        rangeSelectionToggole: true,
        maxInput: "",
        minInput: "",
        valuesList: [
          {
            display: "Agra",
            value: "Agra",
          },
          {
            display: "Ahmedabad",
            value: "Ahmedabad",
          },
          {
            display: "Ambala",
            value: "Ambala",
          },
          {
            display: "Amsterdam",
            value: "Amsterdam",
          },
          {
            display: "Auckland",
            value: "Auckland",
          },
          {
            display: "Aurangabad",
            value: "Aurangabad",
          },
          {
            display: "Bangalore",
            value: "Bangalore",
          },
          {
            display: "Beijing",
            value: "Beijing",
          },
          {
            display: "Bhopal",
            value: "Bhopal",
          },
          {
            display: "Bhubaneshwar",
            value: "Bhubaneshwar",
          },
          {
            display: "Birmingham",
            value: "Birmingham",
          },
          {
            display: "Canberra",
            value: "Canberra",
          },
          {
            display: "Capetown",
            value: "Capetown",
          },
          {
            display: "Chandigarh",
            value: "Chandigarh",
          },
          {
            display: "Chennai",
            value: "Chennai",
          },
          {
            display: "Chicago",
            value: "Chicago",
          },
          {
            display: "Cochin",
            value: "Cochin",
          },
          {
            display: "Coimbatore",
            value: "Coimbatore",
          },
          {
            display: "Dubai",
            value: "Dubai",
          },
          {
            display: "Frankfurt",
            value: "Frankfurt",
          },
          {
            display: "Glasgow",
            value: "Glasgow",
          },
          {
            display: "Goa",
            value: "Goa",
          },
          {
            display: "Gurgaon",
            value: "Gurgaon",
          },
          {
            display: "Guwahati",
            value: "Guwahati",
          },
          {
            display: "Hong Kong",
            value: "Hong Kong",
          },
          {
            display: "Hyderabad",
            value: "Hyderabad",
          },
          {
            display: "Jabalpur",
            value: "Jabalpur",
          },
          {
            display: "Jaipur",
            value: "Jaipur",
          },
          {
            display: "Jakarta",
            value: "Jakarta",
          },
          {
            display: "Jammu",
            value: "Jammu",
          },
          {
            display: "Jeddah",
            value: "Jeddah",
          },
          {
            display: "Jhansi",
            value: "Jhansi",
          },
          {
            display: "Johannesburg",
            value: "Johannesburg",
          },
          {
            display: "Kathmandu",
            value: "Kathmandu",
          },
          {
            display: "Kolkata",
            value: "Kolkata",
          },
          {
            display: "Kuala Lumpur",
            value: "Kuala Lumpur",
          },
          {
            display: "Kuwait",
            value: "Kuwait",
          },
          {
            display: "Las Vegas",
            value: "Las Vegas",
          },
          {
            display: "London",
            value: "London",
          },
          {
            display: "Lucknow",
            value: "Lucknow",
          },
          {
            display: "Melbourne",
            value: "Melbourne",
          },
          {
            display: "Mexico City",
            value: "Mexico City",
          },
          {
            display: "Montreal",
            value: "Montreal",
          },
          {
            display: "Mumbai",
            value: "Mumbai",
          },
          {
            display: "Munich",
            value: "Munich",
          },
          {
            display: "Muscat",
            value: "Muscat",
          },
          {
            display: "Mysore",
            value: "Mysore",
          },
          {
            display: "Nagpur",
            value: "Nagpur",
          },
          {
            display: "Nashik",
            value: "Nashik",
          },
          {
            display: "New Delhi",
            value: "New Delhi",
          },
          {
            display: "New York",
            value: "New York",
          },
          {
            display: "Noida",
            value: "Noida",
          },
          {
            display: "Paris",
            value: "Paris",
          },
          {
            display: "Patna",
            value: "Patna",
          },
          {
            display: "Pennsylvania",
            value: "Pennsylvania",
          },
          {
            display: "Philippines",
            value: "Philippines",
          },
          {
            display: "Phuket",
            value: "Phuket",
          },
          {
            display: "Pune",
            value: "Pune",
          },
          {
            display: "Ranchi",
            value: "Ranchi",
          },
          {
            display: "San Francisco",
            value: "San Francisco",
          },
          {
            display: "Sao Paulo",
            value: "Sao Paulo",
          },
          {
            display: "Shanghai",
            value: "Shanghai",
          },
          {
            display: "Sydney",
            value: "Sydney",
          },
          {
            display: "Taipei",
            value: "Taipei",
          },
          {
            display: "Tatanagar",
            value: "Tatanagar",
          },
          {
            display: "Tokyo",
            value: "Tokyo",
          },
          {
            display: "Toronto",
            value: "Toronto",
          },
        ],
        drillDownId: "",
        uid: "b007c0f5-ee2e-40e6-88b8-44e40a889011",
        configId: "812e450f-cd59-4018-9518-9c360d2b9fa8",
        dataId: "6882605e-ce69-4d96-b6f3-1d376a8c59a3",
        columnID: "1677",
        mapping: {
          isEnabled: true,
          unique: true,
          isDefaultFunction: true,
          valueDisplayMap: [],
          valueAliasName: "random",
          orderBy: {
            display: "asc",
            value: "none",
          },
          valueDBFuntionInfo: {},
          valueColumn: {
            alias: "destination",
            fullyQualifiedColumn: "travel_details.destination",
            id: "1677",
            defaultFunction: "none",
            type: {
              "java.lang.String": "text",
            },
          },
          displayColumn: {
            alias: "destination",
            fullyQualifiedColumn: "travel_details.destination",
            id: "1677",
            defaultFunction: "none",
            type: {
              "java.lang.String": "text",
            },
          },
        },
        cascade: {
          isEnabled: false,
          filters: [],
          filtersCount: 0,
        },
        active: true,
        loading: false,
        search: "",
      },
    ],
    defaultValueDisplayMap: {},
    editingField: null,
    marksList: [
      {
        value: "_all_",
        id: "0612370e-39b4-48df-9f47-8df160fd03f0",
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
    activeMark: "0612370e-39b4-48df-9f47-8df160fd03f0",
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
    stylesId: "hi-report-b0c3b3b4",
    savedStyles: "",
    sqlString: "",
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
    reportData: {
      metadata: [
        {
          1: {
            name: "destination",
            type: "text",
          },
        },
        {
          rows: 4,
        },
      ],
      metadata_file: {
        location: "Manish_05",
        metadataFileName: "Metadata.metadata",
      },
      database: "HIUSER",
      fields: [
        {
          column: "travel_details.destination",
          columnID: "1677",
          label: "destination",
          id: "6bc49419-049b-472e-864b-b58f28daabce",
          type: {
            backendDataType: "java.lang.String",
            dataType: "text",
          },
          autogen_alias: "destination",
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
          metaDataAlias: "destination",
          databaseName: "HIUSER",
          geographicType: "",
          isView: false,
        },
      ],
      rows: [],
      columns: ["travel_details.destination"],
      filters: [
        {
          column: "travel_details.destination",
          label: "destination",
          dataType: "text",
          backendDataType: "java.lang.String",
          condition: "IS_ONE_OF",
          values: ["Ambala", "Amsterdam", "Ahmedabad", "Agra"],
          valuesMode: "custom",
          mode: "auto",
          orderBy: "",
          valuesRange: {},
          rangeValuesType: "",
          dateTimeToggle: false,
          rangeSelectionToggole: true,
          maxInput: "",
          minInput: "",
          valuesList: [
            {
              display: "Agra",
              value: "Agra",
            },
            {
              display: "Ahmedabad",
              value: "Ahmedabad",
            },
            {
              display: "Ambala",
              value: "Ambala",
            },
            {
              display: "Amsterdam",
              value: "Amsterdam",
            },
            {
              display: "Auckland",
              value: "Auckland",
            },
            {
              display: "Aurangabad",
              value: "Aurangabad",
            },
            {
              display: "Bangalore",
              value: "Bangalore",
            },
            {
              display: "Beijing",
              value: "Beijing",
            },
            {
              display: "Bhopal",
              value: "Bhopal",
            },
            {
              display: "Bhubaneshwar",
              value: "Bhubaneshwar",
            },
            {
              display: "Birmingham",
              value: "Birmingham",
            },
            {
              display: "Canberra",
              value: "Canberra",
            },
            {
              display: "Capetown",
              value: "Capetown",
            },
            {
              display: "Chandigarh",
              value: "Chandigarh",
            },
            {
              display: "Chennai",
              value: "Chennai",
            },
            {
              display: "Chicago",
              value: "Chicago",
            },
            {
              display: "Cochin",
              value: "Cochin",
            },
            {
              display: "Coimbatore",
              value: "Coimbatore",
            },
            {
              display: "Dubai",
              value: "Dubai",
            },
            {
              display: "Frankfurt",
              value: "Frankfurt",
            },
            {
              display: "Glasgow",
              value: "Glasgow",
            },
            {
              display: "Goa",
              value: "Goa",
            },
            {
              display: "Gurgaon",
              value: "Gurgaon",
            },
            {
              display: "Guwahati",
              value: "Guwahati",
            },
            {
              display: "Hong Kong",
              value: "Hong Kong",
            },
            {
              display: "Hyderabad",
              value: "Hyderabad",
            },
            {
              display: "Jabalpur",
              value: "Jabalpur",
            },
            {
              display: "Jaipur",
              value: "Jaipur",
            },
            {
              display: "Jakarta",
              value: "Jakarta",
            },
            {
              display: "Jammu",
              value: "Jammu",
            },
            {
              display: "Jeddah",
              value: "Jeddah",
            },
            {
              display: "Jhansi",
              value: "Jhansi",
            },
            {
              display: "Johannesburg",
              value: "Johannesburg",
            },
            {
              display: "Kathmandu",
              value: "Kathmandu",
            },
            {
              display: "Kolkata",
              value: "Kolkata",
            },
            {
              display: "Kuala Lumpur",
              value: "Kuala Lumpur",
            },
            {
              display: "Kuwait",
              value: "Kuwait",
            },
            {
              display: "Las Vegas",
              value: "Las Vegas",
            },
            {
              display: "London",
              value: "London",
            },
            {
              display: "Lucknow",
              value: "Lucknow",
            },
            {
              display: "Melbourne",
              value: "Melbourne",
            },
            {
              display: "Mexico City",
              value: "Mexico City",
            },
            {
              display: "Montreal",
              value: "Montreal",
            },
            {
              display: "Mumbai",
              value: "Mumbai",
            },
            {
              display: "Munich",
              value: "Munich",
            },
            {
              display: "Muscat",
              value: "Muscat",
            },
            {
              display: "Mysore",
              value: "Mysore",
            },
            {
              display: "Nagpur",
              value: "Nagpur",
            },
            {
              display: "Nashik",
              value: "Nashik",
            },
            {
              display: "New Delhi",
              value: "New Delhi",
            },
            {
              display: "New York",
              value: "New York",
            },
            {
              display: "Noida",
              value: "Noida",
            },
            {
              display: "Paris",
              value: "Paris",
            },
            {
              display: "Patna",
              value: "Patna",
            },
            {
              display: "Pennsylvania",
              value: "Pennsylvania",
            },
            {
              display: "Philippines",
              value: "Philippines",
            },
            {
              display: "Phuket",
              value: "Phuket",
            },
            {
              display: "Pune",
              value: "Pune",
            },
            {
              display: "Ranchi",
              value: "Ranchi",
            },
            {
              display: "San Francisco",
              value: "San Francisco",
            },
            {
              display: "Sao Paulo",
              value: "Sao Paulo",
            },
            {
              display: "Shanghai",
              value: "Shanghai",
            },
            {
              display: "Sydney",
              value: "Sydney",
            },
            {
              display: "Taipei",
              value: "Taipei",
            },
            {
              display: "Tatanagar",
              value: "Tatanagar",
            },
            {
              display: "Tokyo",
              value: "Tokyo",
            },
            {
              display: "Toronto",
              value: "Toronto",
            },
          ],
          drillDownId: "",
          uid: "b007c0f5-ee2e-40e6-88b8-44e40a889011",
          configId: "812e450f-cd59-4018-9518-9c360d2b9fa8",
          dataId: "dce75fd0-0749-4030-afb2-54e588f2e60c",
          columnID: "1677",
          mapping: {
            isEnabled: true,
            unique: true,
            isDefaultFunction: true,
            valueDisplayMap: [],
            valueAliasName: "random",
            orderBy: {
              display: "asc",
              value: "none",
            },
            valueDBFuntionInfo: {},
            valueColumn: {
              alias: "destination",
              fullyQualifiedColumn: "travel_details.destination",
              id: "1677",
              defaultFunction: "none",
              type: {
                "java.lang.String": "text",
              },
            },
            displayColumn: {
              alias: "destination",
              fullyQualifiedColumn: "travel_details.destination",
              id: "1677",
              defaultFunction: "none",
              type: {
                "java.lang.String": "text",
              },
            },
          },
          cascade: {
            isEnabled: false,
            filters: [],
            filtersCount: 0,
          },
          active: true,
          loading: false,
          search: "",
        },
      ],
      mark_fields: [],
      marks: [
        {
          value: "_all_",
          id: "0612370e-39b4-48df-9f47-8df160fd03f0",
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
      marksList: [
        {
          value: "_all_",
          id: "0612370e-39b4-48df-9f47-8df160fd03f0",
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
      visualisation: "Table",
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
          showAll: false,
        },
        axisRange: {
          fields: [],
          activeDatatype: "",
          activeId: "",
          gridLines: [],
          synchronize: false,
        },
        cache: {
          isCacheEnabled: false,
          interval: "00:00:01",
        },
        card: {
          title: "",
          prefixType: "selectIcon",
          suffixType: "selectIcon",
          prefix: "",
          suffix: "",
          prefixColor: {
            a: 1,
            b: 0,
            g: 0,
            r: 0,
          },
          suffixColor: {
            a: 1,
            b: 0,
            g: 0,
            r: 0,
          },
        },
        bar: {
          barType: "stacked",
        },
        radial: {
          showRadial: false,
        },
        legend: {
          legendPosition: "right",
        },
        formatColor: {
          defaultColor: {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
          showAll: false,
          dataColors: [],
          formatColorStyle: "",
          formatColorField: "",
          minimum: {
            r: 183,
            g: 192,
            b: 232,
            a: 1,
          },
          maximum: {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
          backgroundColor: false,
        },
        labels: {
          rotateLabels: false,
        },
        crosstab: {
          showGrandTotals: false,
          showRowGrandTotals: false,
          showColumnGrandTotals: false,
          showSubTotals: false,
          showRowSubTotals: false,
          showColumnSubTotals: false,
          grandTotalsPosition: "Bottom",
          subTotalsPosition: "Auto",
        },
        table: {
          recordsPerPage: 10,
        },
      },
      settings: {
        limitBy: 1000,
        sample: "sample",
        prependTableNameToAlias: false,
      },
      options: {
        limitBy: 1000,
        sample: "sample",
        prependTableNameToAlias: false,
      },
      defaultValueDisplayMap: {},
      databaseFunctions: {},
      dateFunctions: {
        dateTime: [
          {
            value: "TODATE",
            label: "Date",
            part: "date",
            key: "sql.typeConversion.todate",
            returns: "date",
            parameters: [
              {
                name: "column",
              },
            ],
          },
          {
            value: "DAY",
            label: "Days",
            part: "day",
            key: "sql.dateTime.day",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "MONTH",
            label: "Months",
            part: "month",
            key: "sql.dateTime.month",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "QUARTER",
            label: "Quarters",
            part: "quarter",
            key: "sql.dateTime.quarter",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "YEAR",
            label: "Years",
            part: "year",
            key: "sql.dateTime.year",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "TOTIME",
            label: "Time",
            part: "time",
            key: "sql.typeConversion.totime",
            returns: "time",
            parameters: [
              {
                name: "column",
              },
            ],
          },
          {
            value: "HOUR",
            label: "Hours",
            part: "hour",
            key: "sql.dateTime.hour",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "MINUTE",
            label: "Minutes",
            part: "minute",
            key: "sql.dateTime.minute",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "SECOND",
            label: "Seconds",
            part: "second",
            key: "sql.dateTime.second",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "INIAVIDUAL",
            label: "Individual",
            part: "individual",
            key: "individual",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
        ],
        date: [
          {
            value: "TODATE",
            label: "Date",
            part: "date",
            key: "sql.typeConversion.todate",
            returns: "date",
            parameters: [
              {
                name: "column",
              },
            ],
          },
          {
            value: "DAY",
            label: "Days",
            part: "day",
            key: "sql.dateTime.day",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "MONTH",
            label: "Months",
            part: "month",
            key: "sql.dateTime.month",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "QUARTER",
            label: "Quarters",
            part: "quarter",
            key: "sql.dateTime.quarter",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "YEAR",
            label: "Years",
            part: "year",
            key: "sql.dateTime.year",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "INIAVIDUAL",
            label: "Individual",
            part: "individual",
            key: "individual",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
        ],
        time: [
          {
            value: "TOTIME",
            label: "Time",
            part: "time",
            key: "sql.typeConversion.totime",
            returns: "time",
            parameters: [
              {
                name: "column",
              },
            ],
          },
          {
            value: "HOUR",
            label: "Hours",
            part: "hour",
            key: "sql.dateTime.hour",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "MINUTE",
            label: "Minutes",
            part: "minute",
            key: "sql.dateTime.minute",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "SECOND",
            label: "Seconds",
            part: "second",
            key: "sql.dateTime.second",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "INIAVIDUAL",
            label: "Individual",
            part: "individual",
            key: "individual",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
        ],
      },
      user: {
        name: "hiadmin",
        email: "admin@helicalinsight.com",
        actualUserName: "hiadmin",
        organization: "",
        roles: ["ROLE_ADMIN", "ROLE_USER", "ROLE_VIEWER"],
        profile: [],
      },
      selectedType: "Table",
      data: [
        {
          destination: "Agra",
        },
        {
          destination: "Ahmedabad",
        },
        {
          destination: "Ambala",
        },
        {
          destination: "Amsterdam",
        },
      ],
      lastModified: 1702382381609,
      limitBy: 10,
      offset: 0,
      pageSize: 10,
      isPrintMode: false,
      dataId: "a611cd2f-00db-4b1e-bda3-0ea56f864e5c",
    },
    customStyles: "",
    customScripts: [],
    analytics: [
      {
        value: false,
        key: "subTotals",
        label: "Row Sub Totals",
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
        showAll: false,
      },
      axisRange: {
        fields: [],
        activeDatatype: "",
        activeId: "",
        gridLines: [],
        synchronize: false,
      },
      cache: {
        isCacheEnabled: false,
        interval: "00:00:01",
      },
      card: {
        title: "",
        prefixType: "selectIcon",
        suffixType: "selectIcon",
        prefix: "",
        suffix: "",
        prefixColor: {
          a: 1,
          b: 0,
          g: 0,
          r: 0,
        },
        suffixColor: {
          a: 1,
          b: 0,
          g: 0,
          r: 0,
        },
      },
      bar: {
        barType: "stacked",
      },
      radial: {
        showRadial: false,
      },
      legend: {
        legendPosition: "right",
      },
      formatColor: {
        defaultColor: {
          r: 84,
          g: 108,
          b: 230,
          a: 1,
        },
        showAll: false,
        dataColors: [],
        formatColorStyle: "",
        formatColorField: "",
        minimum: {
          r: 183,
          g: 192,
          b: 232,
          a: 1,
        },
        maximum: {
          r: 84,
          g: 108,
          b: 230,
          a: 1,
        },
        backgroundColor: false,
      },
      labels: {
        rotateLabels: false,
      },
      crosstab: {
        showGrandTotals: false,
        showRowGrandTotals: false,
        showColumnGrandTotals: false,
        showSubTotals: false,
        showRowSubTotals: false,
        showColumnSubTotals: false,
        grandTotalsPosition: "Bottom",
        subTotalsPosition: "Auto",
      },
      table: {
        recordsPerPage: 10,
      },
    },
    reportInfo: {
      location: "sadakshya_6891",
      uuid: "Report_1.hr",
      reportName: "Report_1",
    },
    cellMenuData: null,
    showHiddenColumns: false,
    showHiddenRows: false,
    geoJsonData: {},
    isAborted: false,
    referenceLineList: [
      {
        display: "All",
        id: "d673dcec-b1e0-4a1e-863d-01185658788d",
        referenceType: "Line",
        value: "",
        enabled: false,
        isStatic: true,
      },
    ],
    database: "HIUSER",
    appliedDbfs: {},
    dateFunctions: {
      dateTime: [
        {
          value: "TODATE",
          label: "Date",
          part: "date",
          key: "sql.typeConversion.todate",
          returns: "date",
          parameters: [
            {
              name: "column",
            },
          ],
        },
        {
          value: "DAY",
          label: "Days",
          part: "day",
          key: "sql.dateTime.day",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "MONTH",
          label: "Months",
          part: "month",
          key: "sql.dateTime.month",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "QUARTER",
          label: "Quarters",
          part: "quarter",
          key: "sql.dateTime.quarter",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "YEAR",
          label: "Years",
          part: "year",
          key: "sql.dateTime.year",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "TOTIME",
          label: "Time",
          part: "time",
          key: "sql.typeConversion.totime",
          returns: "time",
          parameters: [
            {
              name: "column",
            },
          ],
        },
        {
          value: "HOUR",
          label: "Hours",
          part: "hour",
          key: "sql.dateTime.hour",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "MINUTE",
          label: "Minutes",
          part: "minute",
          key: "sql.dateTime.minute",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "SECOND",
          label: "Seconds",
          part: "second",
          key: "sql.dateTime.second",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "INIAVIDUAL",
          label: "Individual",
          part: "individual",
          key: "individual",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
      ],
      date: [
        {
          value: "TODATE",
          label: "Date",
          part: "date",
          key: "sql.typeConversion.todate",
          returns: "date",
          parameters: [
            {
              name: "column",
            },
          ],
        },
        {
          value: "DAY",
          label: "Days",
          part: "day",
          key: "sql.dateTime.day",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "MONTH",
          label: "Months",
          part: "month",
          key: "sql.dateTime.month",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "QUARTER",
          label: "Quarters",
          part: "quarter",
          key: "sql.dateTime.quarter",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "YEAR",
          label: "Years",
          part: "year",
          key: "sql.dateTime.year",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "INIAVIDUAL",
          label: "Individual",
          part: "individual",
          key: "individual",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
      ],
      time: [
        {
          value: "TOTIME",
          label: "Time",
          part: "time",
          key: "sql.typeConversion.totime",
          returns: "time",
          parameters: [
            {
              name: "column",
            },
          ],
        },
        {
          value: "HOUR",
          label: "Hours",
          part: "hour",
          key: "sql.dateTime.hour",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "MINUTE",
          label: "Minutes",
          part: "minute",
          key: "sql.dateTime.minute",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "SECOND",
          label: "Seconds",
          part: "second",
          key: "sql.dateTime.second",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "INIAVIDUAL",
          label: "Individual",
          part: "individual",
          key: "individual",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
      ],
    },
    isPrintMode: false,
    undoRedoAction: true,
  },
  parameters: {
    destination: ["Ambala", "Amsterdam", "Ahmedabad", "Agra", "Auckland"],
  },
  expectedResult: {
    id: "247420c1-deb7-47ae-acf9-4b04ca0443d2",
    mode: "open",
    active: true,
    metadata: {
      classifier: "db.generic",
      name: "HIUSER",
      dataSource: {
        sync: false,
        id: "1400",
        catSchemaPredicted: false,
        catalog: "",
        schema: "HIUSER",
        type: "dynamicDataSource",
        baseType: "global.jdbc",
        dbId: "1302",
      },
      uniqueId: "Metadata",
      tables: {
        dimdate: {
          id: "1307",
          alias: "dimdate",
          columns: {
            dim_id: {
              alias: "dim_id",
              fullyQualifiedColumn: "dimdate.dim_id",
              id: "1644",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            fiscal_year: {
              alias: "fiscal_year",
              fullyQualifiedColumn: "dimdate.fiscal_year",
              id: "1645",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.sql.Date": "date",
              },
            },
            modified_date: {
              alias: "modified_date",
              fullyQualifiedColumn: "dimdate.modified_date",
              id: "1646",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.sql.Timestamp": "dateTime",
              },
            },
            date_key: {
              alias: "date_key",
              fullyQualifiedColumn: "dimdate.date_key",
              id: "1647",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            day_number: {
              alias: "day_number",
              fullyQualifiedColumn: "dimdate.day_number",
              id: "1648",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            fiscal_month_name: {
              alias: "fiscal_month_name",
              fullyQualifiedColumn: "dimdate.fiscal_month_name",
              id: "1649",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            fiscal_month_label: {
              alias: "fiscal_month_label",
              fullyQualifiedColumn: "dimdate.fiscal_month_label",
              id: "1650",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            created_date: {
              alias: "created_date",
              fullyQualifiedColumn: "dimdate.created_date",
              id: "1651",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            created_time: {
              alias: "created_time",
              fullyQualifiedColumn: "dimdate.created_time",
              id: "1652",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            rating: {
              alias: "rating",
              fullyQualifiedColumn: "dimdate.rating",
              id: "1653",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
          },
          name: "dimdate",
          cacheId: "4ac5d9f68b58bd7c0d179146e46795be",
          key: "eb1f1d57-9b57-4ca7-8c76-21e303cc3433",
        },
        employee_details: {
          id: "1308",
          alias: "employee_details",
          columns: {
            employee_id: {
              alias: "employee_id",
              fullyQualifiedColumn: "employee_details.employee_id",
              id: "1654",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            employee_name: {
              alias: "employee_name",
              fullyQualifiedColumn: "employee_details.employee_name",
              id: "1655",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            age: {
              alias: "age",
              fullyQualifiedColumn: "employee_details.age",
              id: "1656",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            address: {
              alias: "address",
              fullyQualifiedColumn: "employee_details.address",
              id: "1657",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
          },
          name: "employee_details",
          cacheId: "4e1fd245f4d13b77be423a43f01d80b2",
          key: "3c23d834-0288-4224-aefe-ce5465b1d374",
        },
        geo_cordinates: {
          id: "1309",
          alias: "geo_cordinates",
          columns: {
            location_id: {
              alias: "location_id",
              fullyQualifiedColumn: "geo_cordinates.location_id",
              id: "1658",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            location: {
              alias: "location",
              fullyQualifiedColumn: "geo_cordinates.location",
              id: "1659",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            latitude: {
              alias: "latitude",
              fullyQualifiedColumn: "geo_cordinates.latitude",
              id: "1660",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Double": "numeric",
              },
            },
            longitude: {
              alias: "longitude",
              fullyQualifiedColumn: "geo_cordinates.longitude",
              id: "1661",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Double": "numeric",
              },
            },
          },
          name: "geo_cordinates",
          cacheId: "be534112989b616b194bc59c2fb25a42",
          key: "14f18a1e-762c-42de-a96f-9a1e569eac81",
        },
        meeting_details: {
          id: "1310",
          alias: "meeting_details",
          columns: {
            meeting_id: {
              alias: "meeting_id",
              fullyQualifiedColumn: "meeting_details.meeting_id",
              id: "1662",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            meeting_date: {
              alias: "meeting_date",
              fullyQualifiedColumn: "meeting_details.meeting_date",
              id: "1663",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.sql.Timestamp": "dateTime",
              },
            },
            meeting_by: {
              alias: "meeting_by",
              fullyQualifiedColumn: "meeting_details.meeting_by",
              id: "1664",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            client_name: {
              alias: "client_name",
              fullyQualifiedColumn: "meeting_details.client_name",
              id: "1665",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            meeting_purpose: {
              alias: "meeting_purpose",
              fullyQualifiedColumn: "meeting_details.meeting_purpose",
              id: "1666",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            meeting_impact: {
              alias: "meeting_impact",
              fullyQualifiedColumn: "meeting_details.meeting_impact",
              id: "1667",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            meet_cancellation_status: {
              alias: "meet_cancellation_status",
              fullyQualifiedColumn: "meeting_details.meet_cancellation_status",
              id: "1668",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            cancellation_reason: {
              alias: "cancellation_reason",
              fullyQualifiedColumn: "meeting_details.cancellation_reason",
              id: "1669",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
          },
          name: "meeting_details",
          cacheId: "9645c648a1c0dbeec1287aaf1e996db3",
          key: "a690d2d8-b70c-4ede-8ee0-fba69523cf96",
        },
        travel_details: {
          id: "1311",
          alias: "travel_details",
          columns: {
            travel_id: {
              alias: "travel_id",
              fullyQualifiedColumn: "travel_details.travel_id",
              id: "1670",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            travel_date: {
              alias: "travel_date",
              fullyQualifiedColumn: "travel_details.travel_date",
              id: "1671",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.sql.Timestamp": "dateTime",
              },
            },
            travel_type: {
              alias: "travel_type",
              fullyQualifiedColumn: "travel_details.travel_type",
              id: "1672",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            travel_medium: {
              alias: "travel_medium",
              fullyQualifiedColumn: "travel_details.travel_medium",
              id: "1673",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            source_id: {
              alias: "source_id",
              fullyQualifiedColumn: "travel_details.source_id",
              id: "1674",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            source: {
              alias: "source",
              fullyQualifiedColumn: "travel_details.source",
              id: "1675",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            destination_id: {
              alias: "destination_id",
              fullyQualifiedColumn: "travel_details.destination_id",
              id: "1676",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            destination: {
              alias: "destination",
              fullyQualifiedColumn: "travel_details.destination",
              id: "1677",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            travel_cost: {
              alias: "travel_cost",
              fullyQualifiedColumn: "travel_details.travel_cost",
              id: "1678",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
            mode_of_payment: {
              alias: "mode_of_payment",
              fullyQualifiedColumn: "travel_details.mode_of_payment",
              id: "1679",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            booking_platform: {
              alias: "booking_platform",
              fullyQualifiedColumn: "travel_details.booking_platform",
              id: "1680",
              defaultFunction: "db.generic.groupBy.group",
              type: {
                "java.lang.String": "text",
              },
            },
            travelled_by: {
              alias: "travelled_by",
              fullyQualifiedColumn: "travel_details.travelled_by",
              id: "1681",
              defaultFunction: "db.generic.aggregate.sum",
              type: {
                "java.lang.Integer": "numeric",
              },
            },
          },
          name: "travel_details",
          cacheId: "8a28627d07d04ef096d9935f12e0c7e9",
          key: "336c2eba-e18f-49c8-8134-13868815451f",
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
      metadataName: "Metadata",
      metadataDir: "Manish_05",
      databaseName: "HIUSER",
      formData: {
        location: "Manish_05",
        metadataFileName: "Metadata.metadata",
      },
      uid: "f6ae10f1-80dc-45c1-8543-34efd39f467e",
    },
    metadataLoading: false,
    hreportLoading: false,
    functions: {},
    databaseFunctions: {},
    fields: [
      {
        column: "travel_details.destination",
        columnID: "1677",
        label: "destination",
        id: "6bc49419-049b-472e-864b-b58f28daabce",
        type: {
          backendDataType: "java.lang.String",
          dataType: "text",
        },
        autogen_alias: "destination",
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
        metaDataAlias: "destination",
        databaseName: "HIUSER",
        geographicType: "",
        isView: false,
      },
    ],
    filters: [
      {
        column: "travel_details.destination",
        label: "destination",
        dataType: "text",
        backendDataType: "java.lang.String",
        condition: "IS_ONE_OF",
        values: ["Ambala", "Amsterdam", "Ahmedabad", "Agra", "Auckland"],
        valuesMode: "custom",
        mode: "auto",
        orderBy: "",
        valuesRange: {},
        rangeValuesType: "",
        dateTimeToggle: false,
        rangeSelectionToggole: true,
        maxInput: "",
        minInput: "",
        valuesList: [
          {
            display: "Agra",
            value: "Agra",
          },
          {
            display: "Ahmedabad",
            value: "Ahmedabad",
          },
          {
            display: "Ambala",
            value: "Ambala",
          },
          {
            display: "Amsterdam",
            value: "Amsterdam",
          },
          {
            display: "Auckland",
            value: "Auckland",
          },
          {
            display: "Aurangabad",
            value: "Aurangabad",
          },
          {
            display: "Bangalore",
            value: "Bangalore",
          },
          {
            display: "Beijing",
            value: "Beijing",
          },
          {
            display: "Bhopal",
            value: "Bhopal",
          },
          {
            display: "Bhubaneshwar",
            value: "Bhubaneshwar",
          },
          {
            display: "Birmingham",
            value: "Birmingham",
          },
          {
            display: "Canberra",
            value: "Canberra",
          },
          {
            display: "Capetown",
            value: "Capetown",
          },
          {
            display: "Chandigarh",
            value: "Chandigarh",
          },
          {
            display: "Chennai",
            value: "Chennai",
          },
          {
            display: "Chicago",
            value: "Chicago",
          },
          {
            display: "Cochin",
            value: "Cochin",
          },
          {
            display: "Coimbatore",
            value: "Coimbatore",
          },
          {
            display: "Dubai",
            value: "Dubai",
          },
          {
            display: "Frankfurt",
            value: "Frankfurt",
          },
          {
            display: "Glasgow",
            value: "Glasgow",
          },
          {
            display: "Goa",
            value: "Goa",
          },
          {
            display: "Gurgaon",
            value: "Gurgaon",
          },
          {
            display: "Guwahati",
            value: "Guwahati",
          },
          {
            display: "Hong Kong",
            value: "Hong Kong",
          },
          {
            display: "Hyderabad",
            value: "Hyderabad",
          },
          {
            display: "Jabalpur",
            value: "Jabalpur",
          },
          {
            display: "Jaipur",
            value: "Jaipur",
          },
          {
            display: "Jakarta",
            value: "Jakarta",
          },
          {
            display: "Jammu",
            value: "Jammu",
          },
          {
            display: "Jeddah",
            value: "Jeddah",
          },
          {
            display: "Jhansi",
            value: "Jhansi",
          },
          {
            display: "Johannesburg",
            value: "Johannesburg",
          },
          {
            display: "Kathmandu",
            value: "Kathmandu",
          },
          {
            display: "Kolkata",
            value: "Kolkata",
          },
          {
            display: "Kuala Lumpur",
            value: "Kuala Lumpur",
          },
          {
            display: "Kuwait",
            value: "Kuwait",
          },
          {
            display: "Las Vegas",
            value: "Las Vegas",
          },
          {
            display: "London",
            value: "London",
          },
          {
            display: "Lucknow",
            value: "Lucknow",
          },
          {
            display: "Melbourne",
            value: "Melbourne",
          },
          {
            display: "Mexico City",
            value: "Mexico City",
          },
          {
            display: "Montreal",
            value: "Montreal",
          },
          {
            display: "Mumbai",
            value: "Mumbai",
          },
          {
            display: "Munich",
            value: "Munich",
          },
          {
            display: "Muscat",
            value: "Muscat",
          },
          {
            display: "Mysore",
            value: "Mysore",
          },
          {
            display: "Nagpur",
            value: "Nagpur",
          },
          {
            display: "Nashik",
            value: "Nashik",
          },
          {
            display: "New Delhi",
            value: "New Delhi",
          },
          {
            display: "New York",
            value: "New York",
          },
          {
            display: "Noida",
            value: "Noida",
          },
          {
            display: "Paris",
            value: "Paris",
          },
          {
            display: "Patna",
            value: "Patna",
          },
          {
            display: "Pennsylvania",
            value: "Pennsylvania",
          },
          {
            display: "Philippines",
            value: "Philippines",
          },
          {
            display: "Phuket",
            value: "Phuket",
          },
          {
            display: "Pune",
            value: "Pune",
          },
          {
            display: "Ranchi",
            value: "Ranchi",
          },
          {
            display: "San Francisco",
            value: "San Francisco",
          },
          {
            display: "Sao Paulo",
            value: "Sao Paulo",
          },
          {
            display: "Shanghai",
            value: "Shanghai",
          },
          {
            display: "Sydney",
            value: "Sydney",
          },
          {
            display: "Taipei",
            value: "Taipei",
          },
          {
            display: "Tatanagar",
            value: "Tatanagar",
          },
          {
            display: "Tokyo",
            value: "Tokyo",
          },
          {
            display: "Toronto",
            value: "Toronto",
          },
        ],
        drillDownId: "",
        uid: "b007c0f5-ee2e-40e6-88b8-44e40a889011",
        configId: "812e450f-cd59-4018-9518-9c360d2b9fa8",
        dataId: "6882605e-ce69-4d96-b6f3-1d376a8c59a3",
        columnID: "1677",
        mapping: {
          isEnabled: true,
          unique: true,
          isDefaultFunction: true,
          valueDisplayMap: [],
          valueAliasName: "random",
          orderBy: {
            display: "asc",
            value: "none",
          },
          valueDBFuntionInfo: {},
          valueColumn: {
            alias: "destination",
            fullyQualifiedColumn: "travel_details.destination",
            id: "1677",
            defaultFunction: "none",
            type: {
              "java.lang.String": "text",
            },
          },
          displayColumn: {
            alias: "destination",
            fullyQualifiedColumn: "travel_details.destination",
            id: "1677",
            defaultFunction: "none",
            type: {
              "java.lang.String": "text",
            },
          },
        },
        cascade: {
          isEnabled: false,
          filters: [],
          filtersCount: 0,
        },
        active: true,
        loading: false,
        search: "",
      },
    ],
    defaultValueDisplayMap: {},
    editingField: null,
    marksList: [
      {
        value: "_all_",
        id: "0612370e-39b4-48df-9f47-8df160fd03f0",
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
    activeMark: "0612370e-39b4-48df-9f47-8df160fd03f0",
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
    stylesId: "hi-report-b0c3b3b4",
    savedStyles: "",
    sqlString: "",
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
    reportData: {
      metadata: [
        {
          1: {
            name: "destination",
            type: "text",
          },
        },
        {
          rows: 4,
        },
      ],
      metadata_file: {
        location: "Manish_05",
        metadataFileName: "Metadata.metadata",
      },
      database: "HIUSER",
      fields: [
        {
          column: "travel_details.destination",
          columnID: "1677",
          label: "destination",
          id: "6bc49419-049b-472e-864b-b58f28daabce",
          type: {
            backendDataType: "java.lang.String",
            dataType: "text",
          },
          autogen_alias: "destination",
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
          metaDataAlias: "destination",
          databaseName: "HIUSER",
          geographicType: "",
          isView: false,
        },
      ],
      rows: [],
      columns: ["travel_details.destination"],
      filters: [
        {
          column: "travel_details.destination",
          label: "destination",
          dataType: "text",
          backendDataType: "java.lang.String",
          condition: "IS_ONE_OF",
          values: ["Ambala", "Amsterdam", "Ahmedabad", "Agra"],
          valuesMode: "custom",
          mode: "auto",
          orderBy: "",
          valuesRange: {},
          rangeValuesType: "",
          dateTimeToggle: false,
          rangeSelectionToggole: true,
          maxInput: "",
          minInput: "",
          valuesList: [
            {
              display: "Agra",
              value: "Agra",
            },
            {
              display: "Ahmedabad",
              value: "Ahmedabad",
            },
            {
              display: "Ambala",
              value: "Ambala",
            },
            {
              display: "Amsterdam",
              value: "Amsterdam",
            },
            {
              display: "Auckland",
              value: "Auckland",
            },
            {
              display: "Aurangabad",
              value: "Aurangabad",
            },
            {
              display: "Bangalore",
              value: "Bangalore",
            },
            {
              display: "Beijing",
              value: "Beijing",
            },
            {
              display: "Bhopal",
              value: "Bhopal",
            },
            {
              display: "Bhubaneshwar",
              value: "Bhubaneshwar",
            },
            {
              display: "Birmingham",
              value: "Birmingham",
            },
            {
              display: "Canberra",
              value: "Canberra",
            },
            {
              display: "Capetown",
              value: "Capetown",
            },
            {
              display: "Chandigarh",
              value: "Chandigarh",
            },
            {
              display: "Chennai",
              value: "Chennai",
            },
            {
              display: "Chicago",
              value: "Chicago",
            },
            {
              display: "Cochin",
              value: "Cochin",
            },
            {
              display: "Coimbatore",
              value: "Coimbatore",
            },
            {
              display: "Dubai",
              value: "Dubai",
            },
            {
              display: "Frankfurt",
              value: "Frankfurt",
            },
            {
              display: "Glasgow",
              value: "Glasgow",
            },
            {
              display: "Goa",
              value: "Goa",
            },
            {
              display: "Gurgaon",
              value: "Gurgaon",
            },
            {
              display: "Guwahati",
              value: "Guwahati",
            },
            {
              display: "Hong Kong",
              value: "Hong Kong",
            },
            {
              display: "Hyderabad",
              value: "Hyderabad",
            },
            {
              display: "Jabalpur",
              value: "Jabalpur",
            },
            {
              display: "Jaipur",
              value: "Jaipur",
            },
            {
              display: "Jakarta",
              value: "Jakarta",
            },
            {
              display: "Jammu",
              value: "Jammu",
            },
            {
              display: "Jeddah",
              value: "Jeddah",
            },
            {
              display: "Jhansi",
              value: "Jhansi",
            },
            {
              display: "Johannesburg",
              value: "Johannesburg",
            },
            {
              display: "Kathmandu",
              value: "Kathmandu",
            },
            {
              display: "Kolkata",
              value: "Kolkata",
            },
            {
              display: "Kuala Lumpur",
              value: "Kuala Lumpur",
            },
            {
              display: "Kuwait",
              value: "Kuwait",
            },
            {
              display: "Las Vegas",
              value: "Las Vegas",
            },
            {
              display: "London",
              value: "London",
            },
            {
              display: "Lucknow",
              value: "Lucknow",
            },
            {
              display: "Melbourne",
              value: "Melbourne",
            },
            {
              display: "Mexico City",
              value: "Mexico City",
            },
            {
              display: "Montreal",
              value: "Montreal",
            },
            {
              display: "Mumbai",
              value: "Mumbai",
            },
            {
              display: "Munich",
              value: "Munich",
            },
            {
              display: "Muscat",
              value: "Muscat",
            },
            {
              display: "Mysore",
              value: "Mysore",
            },
            {
              display: "Nagpur",
              value: "Nagpur",
            },
            {
              display: "Nashik",
              value: "Nashik",
            },
            {
              display: "New Delhi",
              value: "New Delhi",
            },
            {
              display: "New York",
              value: "New York",
            },
            {
              display: "Noida",
              value: "Noida",
            },
            {
              display: "Paris",
              value: "Paris",
            },
            {
              display: "Patna",
              value: "Patna",
            },
            {
              display: "Pennsylvania",
              value: "Pennsylvania",
            },
            {
              display: "Philippines",
              value: "Philippines",
            },
            {
              display: "Phuket",
              value: "Phuket",
            },
            {
              display: "Pune",
              value: "Pune",
            },
            {
              display: "Ranchi",
              value: "Ranchi",
            },
            {
              display: "San Francisco",
              value: "San Francisco",
            },
            {
              display: "Sao Paulo",
              value: "Sao Paulo",
            },
            {
              display: "Shanghai",
              value: "Shanghai",
            },
            {
              display: "Sydney",
              value: "Sydney",
            },
            {
              display: "Taipei",
              value: "Taipei",
            },
            {
              display: "Tatanagar",
              value: "Tatanagar",
            },
            {
              display: "Tokyo",
              value: "Tokyo",
            },
            {
              display: "Toronto",
              value: "Toronto",
            },
          ],
          drillDownId: "",
          uid: "b007c0f5-ee2e-40e6-88b8-44e40a889011",
          configId: "812e450f-cd59-4018-9518-9c360d2b9fa8",
          dataId: "dce75fd0-0749-4030-afb2-54e588f2e60c",
          columnID: "1677",
          mapping: {
            isEnabled: true,
            unique: true,
            isDefaultFunction: true,
            valueDisplayMap: [],
            valueAliasName: "random",
            orderBy: {
              display: "asc",
              value: "none",
            },
            valueDBFuntionInfo: {},
            valueColumn: {
              alias: "destination",
              fullyQualifiedColumn: "travel_details.destination",
              id: "1677",
              defaultFunction: "none",
              type: {
                "java.lang.String": "text",
              },
            },
            displayColumn: {
              alias: "destination",
              fullyQualifiedColumn: "travel_details.destination",
              id: "1677",
              defaultFunction: "none",
              type: {
                "java.lang.String": "text",
              },
            },
          },
          cascade: {
            isEnabled: false,
            filters: [],
            filtersCount: 0,
          },
          active: true,
          loading: false,
          search: "",
        },
      ],
      mark_fields: [],
      marks: [
        {
          value: "_all_",
          id: "0612370e-39b4-48df-9f47-8df160fd03f0",
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
      marksList: [
        {
          value: "_all_",
          id: "0612370e-39b4-48df-9f47-8df160fd03f0",
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
      visualisation: "Table",
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
          showAll: false,
        },
        axisRange: {
          fields: [],
          activeDatatype: "",
          activeId: "",
          gridLines: [],
          synchronize: false,
        },
        cache: {
          isCacheEnabled: false,
          interval: "00:00:01",
        },
        card: {
          title: "",
          prefixType: "selectIcon",
          suffixType: "selectIcon",
          prefix: "",
          suffix: "",
          prefixColor: {
            a: 1,
            b: 0,
            g: 0,
            r: 0,
          },
          suffixColor: {
            a: 1,
            b: 0,
            g: 0,
            r: 0,
          },
        },
        bar: {
          barType: "stacked",
        },
        radial: {
          showRadial: false,
        },
        legend: {
          legendPosition: "right",
        },
        formatColor: {
          defaultColor: {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
          showAll: false,
          dataColors: [],
          formatColorStyle: "",
          formatColorField: "",
          minimum: {
            r: 183,
            g: 192,
            b: 232,
            a: 1,
          },
          maximum: {
            r: 84,
            g: 108,
            b: 230,
            a: 1,
          },
          backgroundColor: false,
        },
        labels: {
          rotateLabels: false,
        },
        crosstab: {
          showGrandTotals: false,
          showRowGrandTotals: false,
          showColumnGrandTotals: false,
          showSubTotals: false,
          showRowSubTotals: false,
          showColumnSubTotals: false,
          grandTotalsPosition: "Bottom",
          subTotalsPosition: "Auto",
        },
        table: {
          recordsPerPage: 10,
        },
      },
      settings: {
        limitBy: 1000,
        sample: "sample",
        prependTableNameToAlias: false,
      },
      options: {
        limitBy: 1000,
        sample: "sample",
        prependTableNameToAlias: false,
      },
      defaultValueDisplayMap: {},
      databaseFunctions: {},
      dateFunctions: {
        dateTime: [
          {
            value: "TODATE",
            label: "Date",
            part: "date",
            key: "sql.typeConversion.todate",
            returns: "date",
            parameters: [
              {
                name: "column",
              },
            ],
          },
          {
            value: "DAY",
            label: "Days",
            part: "day",
            key: "sql.dateTime.day",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "MONTH",
            label: "Months",
            part: "month",
            key: "sql.dateTime.month",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "QUARTER",
            label: "Quarters",
            part: "quarter",
            key: "sql.dateTime.quarter",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "YEAR",
            label: "Years",
            part: "year",
            key: "sql.dateTime.year",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "TOTIME",
            label: "Time",
            part: "time",
            key: "sql.typeConversion.totime",
            returns: "time",
            parameters: [
              {
                name: "column",
              },
            ],
          },
          {
            value: "HOUR",
            label: "Hours",
            part: "hour",
            key: "sql.dateTime.hour",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "MINUTE",
            label: "Minutes",
            part: "minute",
            key: "sql.dateTime.minute",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "SECOND",
            label: "Seconds",
            part: "second",
            key: "sql.dateTime.second",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "INIAVIDUAL",
            label: "Individual",
            part: "individual",
            key: "individual",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
        ],
        date: [
          {
            value: "TODATE",
            label: "Date",
            part: "date",
            key: "sql.typeConversion.todate",
            returns: "date",
            parameters: [
              {
                name: "column",
              },
            ],
          },
          {
            value: "DAY",
            label: "Days",
            part: "day",
            key: "sql.dateTime.day",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "MONTH",
            label: "Months",
            part: "month",
            key: "sql.dateTime.month",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "QUARTER",
            label: "Quarters",
            part: "quarter",
            key: "sql.dateTime.quarter",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "YEAR",
            label: "Years",
            part: "year",
            key: "sql.dateTime.year",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "INIAVIDUAL",
            label: "Individual",
            part: "individual",
            key: "individual",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
        ],
        time: [
          {
            value: "TOTIME",
            label: "Time",
            part: "time",
            key: "sql.typeConversion.totime",
            returns: "time",
            parameters: [
              {
                name: "column",
              },
            ],
          },
          {
            value: "HOUR",
            label: "Hours",
            part: "hour",
            key: "sql.dateTime.hour",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "MINUTE",
            label: "Minutes",
            part: "minute",
            key: "sql.dateTime.minute",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "SECOND",
            label: "Seconds",
            part: "second",
            key: "sql.dateTime.second",
            returns: "numeric",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
          {
            value: "INIAVIDUAL",
            label: "Individual",
            part: "individual",
            key: "individual",
            parameters: [
              {
                name: "datetime",
              },
            ],
          },
        ],
      },
      user: {
        name: "hiadmin",
        email: "admin@helicalinsight.com",
        actualUserName: "hiadmin",
        organization: "",
        roles: ["ROLE_ADMIN", "ROLE_USER", "ROLE_VIEWER"],
        profile: [],
      },
      selectedType: "Table",
      data: [
        {
          destination: "Agra",
        },
        {
          destination: "Ahmedabad",
        },
        {
          destination: "Ambala",
        },
        {
          destination: "Amsterdam",
        },
      ],
      lastModified: 1702382381609,
      limitBy: 10,
      offset: 0,
      pageSize: 10,
      isPrintMode: false,
      dataId: "a611cd2f-00db-4b1e-bda3-0ea56f864e5c",
    },
    customStyles: "",
    customScripts: [],
    analytics: [
      {
        value: false,
        key: "subTotals",
        label: "Row Sub Totals",
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
        showAll: false,
      },
      axisRange: {
        fields: [],
        activeDatatype: "",
        activeId: "",
        gridLines: [],
        synchronize: false,
      },
      cache: {
        isCacheEnabled: false,
        interval: "00:00:01",
      },
      card: {
        title: "",
        prefixType: "selectIcon",
        suffixType: "selectIcon",
        prefix: "",
        suffix: "",
        prefixColor: {
          a: 1,
          b: 0,
          g: 0,
          r: 0,
        },
        suffixColor: {
          a: 1,
          b: 0,
          g: 0,
          r: 0,
        },
      },
      bar: {
        barType: "stacked",
      },
      radial: {
        showRadial: false,
      },
      legend: {
        legendPosition: "right",
      },
      formatColor: {
        defaultColor: {
          r: 84,
          g: 108,
          b: 230,
          a: 1,
        },
        showAll: false,
        dataColors: [],
        formatColorStyle: "",
        formatColorField: "",
        minimum: {
          r: 183,
          g: 192,
          b: 232,
          a: 1,
        },
        maximum: {
          r: 84,
          g: 108,
          b: 230,
          a: 1,
        },
        backgroundColor: false,
      },
      labels: {
        rotateLabels: false,
      },
      crosstab: {
        showGrandTotals: false,
        showRowGrandTotals: false,
        showColumnGrandTotals: false,
        showSubTotals: false,
        showRowSubTotals: false,
        showColumnSubTotals: false,
        grandTotalsPosition: "Bottom",
        subTotalsPosition: "Auto",
      },
      table: {
        recordsPerPage: 10,
      },
    },
    reportInfo: {
      location: "sadakshya_6891",
      uuid: "Report_1.hr",
      reportName: "Report_1",
    },
    cellMenuData: null,
    showHiddenColumns: false,
    showHiddenRows: false,
    geoJsonData: {},
    isAborted: false,
    referenceLineList: [
      {
        display: "All",
        id: "d673dcec-b1e0-4a1e-863d-01185658788d",
        referenceType: "Line",
        value: "",
        enabled: false,
        isStatic: true,
      },
    ],
    database: "HIUSER",
    appliedDbfs: {},
    dateFunctions: {
      dateTime: [
        {
          value: "TODATE",
          label: "Date",
          part: "date",
          key: "sql.typeConversion.todate",
          returns: "date",
          parameters: [
            {
              name: "column",
            },
          ],
        },
        {
          value: "DAY",
          label: "Days",
          part: "day",
          key: "sql.dateTime.day",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "MONTH",
          label: "Months",
          part: "month",
          key: "sql.dateTime.month",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "QUARTER",
          label: "Quarters",
          part: "quarter",
          key: "sql.dateTime.quarter",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "YEAR",
          label: "Years",
          part: "year",
          key: "sql.dateTime.year",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "TOTIME",
          label: "Time",
          part: "time",
          key: "sql.typeConversion.totime",
          returns: "time",
          parameters: [
            {
              name: "column",
            },
          ],
        },
        {
          value: "HOUR",
          label: "Hours",
          part: "hour",
          key: "sql.dateTime.hour",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "MINUTE",
          label: "Minutes",
          part: "minute",
          key: "sql.dateTime.minute",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "SECOND",
          label: "Seconds",
          part: "second",
          key: "sql.dateTime.second",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "INIAVIDUAL",
          label: "Individual",
          part: "individual",
          key: "individual",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
      ],
      date: [
        {
          value: "TODATE",
          label: "Date",
          part: "date",
          key: "sql.typeConversion.todate",
          returns: "date",
          parameters: [
            {
              name: "column",
            },
          ],
        },
        {
          value: "DAY",
          label: "Days",
          part: "day",
          key: "sql.dateTime.day",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "MONTH",
          label: "Months",
          part: "month",
          key: "sql.dateTime.month",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "QUARTER",
          label: "Quarters",
          part: "quarter",
          key: "sql.dateTime.quarter",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "YEAR",
          label: "Years",
          part: "year",
          key: "sql.dateTime.year",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "INIAVIDUAL",
          label: "Individual",
          part: "individual",
          key: "individual",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
      ],
      time: [
        {
          value: "TOTIME",
          label: "Time",
          part: "time",
          key: "sql.typeConversion.totime",
          returns: "time",
          parameters: [
            {
              name: "column",
            },
          ],
        },
        {
          value: "HOUR",
          label: "Hours",
          part: "hour",
          key: "sql.dateTime.hour",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "MINUTE",
          label: "Minutes",
          part: "minute",
          key: "sql.dateTime.minute",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "SECOND",
          label: "Seconds",
          part: "second",
          key: "sql.dateTime.second",
          returns: "numeric",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
        {
          value: "INIAVIDUAL",
          label: "Individual",
          part: "individual",
          key: "individual",
          parameters: [
            {
              name: "datetime",
            },
          ],
        },
      ],
    },
    isPrintMode: false,
    undoRedoAction: true,
  },
};
