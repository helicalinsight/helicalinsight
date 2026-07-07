export const vfEditorMockData = {
    activeReportId: "bce7a0d9-92a5-490a-9f3e-ed456e6226a2",
    reports: [
        {
            id: "bce7a0d9-92a5-490a-9f3e-ed456e6226a2",
            mode: "create",
            active: true,
            metadata: {
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
                    dbId: "5311",
                },
                uniqueId: "Metadata_new",
                tables: {
                    dimdate: {
                        id: "5736",
                        alias: "dimdate",
                        columns: {
                            dim_id: {
                                alias: "dim_id",
                                fullyQualifiedColumn: "dimdate.dim_id",
                                id: "13082",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Integer": "numeric",
                                },
                            },
                            fiscal_year: {
                                alias: "fiscal_year",
                                fullyQualifiedColumn: "dimdate.fiscal_year",
                                id: "13083",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.sql.Date": "date",
                                },
                            },
                            modified_date: {
                                alias: "modified_date",
                                fullyQualifiedColumn: "dimdate.modified_date",
                                id: "13084",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.sql.Timestamp": "dateTime",
                                },
                            },
                            date_key: {
                                alias: "date_key",
                                fullyQualifiedColumn: "dimdate.date_key",
                                id: "13085",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            day_number: {
                                alias: "day_number",
                                fullyQualifiedColumn: "dimdate.day_number",
                                id: "13086",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            fiscal_month_name: {
                                alias: "fiscal_month_name",
                                fullyQualifiedColumn: "dimdate.fiscal_month_name",
                                id: "13087",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            fiscal_month_label: {
                                alias: "fiscal_month_label",
                                fullyQualifiedColumn: "dimdate.fiscal_month_label",
                                id: "13088",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            created_date: {
                                alias: "created_date",
                                fullyQualifiedColumn: "dimdate.created_date",
                                id: "13089",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            created_time: {
                                alias: "created_time",
                                fullyQualifiedColumn: "dimdate.created_time",
                                id: "13090",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            rating: {
                                alias: "rating",
                                fullyQualifiedColumn: "dimdate.rating",
                                id: "13091",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                        },
                        name: "dimdate",
                        cacheId: "4ac5d9f68b58bd7c0d179146e46795be",
                        key: "74cd66a0-fa91-4f17-b4f3-48c0f025ac52",
                    },
                    employee_details: {
                        id: "5737",
                        alias: "employee_details",
                        columns: {
                            employee_id: {
                                alias: "employee_id",
                                fullyQualifiedColumn: "employee_details.employee_id",
                                id: "13092",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Integer": "numeric",
                                },
                            },
                            employee_name: {
                                alias: "employee_name",
                                fullyQualifiedColumn: "employee_details.employee_name",
                                id: "13093",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            age: {
                                alias: "age",
                                fullyQualifiedColumn: "employee_details.age",
                                id: "13094",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Integer": "numeric",
                                },
                            },
                            address: {
                                alias: "address",
                                fullyQualifiedColumn: "employee_details.address",
                                id: "13095",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                        },
                        name: "employee_details",
                        cacheId: "4e1fd245f4d13b77be423a43f01d80b2",
                        key: "ab43a422-a291-4878-ac70-362b84de979b",
                    },
                    geo_cordinates: {
                        id: "5738",
                        alias: "geo_cordinates",
                        columns: {
                            location_id: {
                                alias: "location_id",
                                fullyQualifiedColumn: "geo_cordinates.location_id",
                                id: "13096",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Integer": "numeric",
                                },
                            },
                            location: {
                                alias: "location",
                                fullyQualifiedColumn: "geo_cordinates.location",
                                id: "13097",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            latitude: {
                                alias: "latitude",
                                fullyQualifiedColumn: "geo_cordinates.latitude",
                                id: "13098",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Double": "numeric",
                                },
                            },
                            longitude: {
                                alias: "longitude",
                                fullyQualifiedColumn: "geo_cordinates.longitude",
                                id: "13099",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Double": "numeric",
                                },
                            },
                        },
                        name: "geo_cordinates",
                        cacheId: "be534112989b616b194bc59c2fb25a42",
                        key: "c49bb712-0e17-491e-ae33-5d776a689689",
                    },
                    meeting_details: {
                        id: "5739",
                        alias: "meeting_details",
                        columns: {
                            meeting_id: {
                                alias: "meeting_id",
                                fullyQualifiedColumn: "meeting_details.meeting_id",
                                id: "13100",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Integer": "numeric",
                                },
                            },
                            meeting_date: {
                                alias: "meeting_date",
                                fullyQualifiedColumn: "meeting_details.meeting_date",
                                id: "13101",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.sql.Timestamp": "dateTime",
                                },
                            },
                            meeting_by: {
                                alias: "meeting_by",
                                fullyQualifiedColumn: "meeting_details.meeting_by",
                                id: "13102",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Integer": "numeric",
                                },
                            },
                            client_name: {
                                alias: "client_name",
                                fullyQualifiedColumn: "meeting_details.client_name",
                                id: "13103",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            meeting_purpose: {
                                alias: "meeting_purpose",
                                fullyQualifiedColumn: "meeting_details.meeting_purpose",
                                id: "13104",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            meeting_impact: {
                                alias: "meeting_impact",
                                fullyQualifiedColumn: "meeting_details.meeting_impact",
                                id: "13105",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            meet_cancellation_status: {
                                alias: "meet_cancellation_status",
                                fullyQualifiedColumn:
                                    "meeting_details.meet_cancellation_status",
                                id: "13106",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            cancellation_reason: {
                                alias: "cancellation_reason",
                                fullyQualifiedColumn: "meeting_details.cancellation_reason",
                                id: "13107",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                        },
                        name: "meeting_details",
                        cacheId: "9645c648a1c0dbeec1287aaf1e996db3",
                        key: "2e091852-4dd5-42af-8a2d-a36b7e420df6",
                    },
                    travel_details: {
                        id: "5740",
                        alias: "travel_details",
                        columns: {
                            travel_id: {
                                alias: "travel_id",
                                fullyQualifiedColumn: "travel_details.travel_id",
                                id: "13108",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Integer": "numeric",
                                },
                            },
                            travel_date: {
                                alias: "travel_date",
                                fullyQualifiedColumn: "travel_details.travel_date",
                                id: "13109",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.sql.Timestamp": "dateTime",
                                },
                            },
                            travel_type: {
                                alias: "travel_type",
                                fullyQualifiedColumn: "travel_details.travel_type",
                                id: "13110",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            travel_medium: {
                                alias: "travel_medium",
                                fullyQualifiedColumn: "travel_details.travel_medium",
                                id: "13111",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            source_id: {
                                alias: "source_id",
                                fullyQualifiedColumn: "travel_details.source_id",
                                id: "13112",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Integer": "numeric",
                                },
                            },
                            source: {
                                alias: "source",
                                fullyQualifiedColumn: "travel_details.source",
                                id: "13113",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            destination_id: {
                                alias: "destination_id",
                                fullyQualifiedColumn: "travel_details.destination_id",
                                id: "13114",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Integer": "numeric",
                                },
                            },
                            destination: {
                                alias: "destination",
                                fullyQualifiedColumn: "travel_details.destination",
                                id: "13115",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            travel_cost: {
                                alias: "travel_cost",
                                fullyQualifiedColumn: "travel_details.travel_cost",
                                id: "13116",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Integer": "numeric",
                                },
                            },
                            mode_of_payment: {
                                alias: "mode_of_payment",
                                fullyQualifiedColumn: "travel_details.mode_of_payment",
                                id: "13117",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            booking_platform: {
                                alias: "booking_platform",
                                fullyQualifiedColumn: "travel_details.booking_platform",
                                id: "13118",
                                defaultFunction: "db.generic.groupBy.group",
                                type: {
                                    "java.lang.String": "text",
                                },
                            },
                            travelled_by: {
                                alias: "travelled_by",
                                fullyQualifiedColumn: "travel_details.travelled_by",
                                id: "13119",
                                defaultFunction: "db.generic.aggregate.sum",
                                type: {
                                    "java.lang.Integer": "numeric",
                                },
                            },
                        },
                        name: "travel_details",
                        cacheId: "8a28627d07d04ef096d9935f12e0c7e9",
                        key: "5404b776-948d-4b69-857b-1b9a59f9c3e2",
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
                metadataName: "Metadata_new",
                metadataDir: "Manish_18",
                formData: {
                    location: "Manish_18",
                    metadataFileName: "Metadata_new.metadata",
                },
                uid: "b5a32028-33d2-4511-b747-da336b71c12f",
                defaultexpandedRowKeys: ["5404b776-948d-4b69-857b-1b9a59f9c3e2"],
            },
            metadataLoading: false,
            hreportLoading: false,
            functions: {
                "db.generic.aggregate.avg": "avg",
                "db.generic.aggregate.count": "count",
                "db.generic.aggregate.distinct": "distinct",
                "db.generic.aggregate.max": "max",
                "db.generic.aggregate.min": "min",
                "db.generic.aggregate.sum": "sum",
                "db.generic.groupBy.group": "group by",
                "db.generic.orderBy.order": "order by",
            },
            databaseFunctions: {},
            fields: [
                {
                    column: "travel_details.booking_platform",
                    columnID: "13118",
                    label: "booking_platform",
                    id: "414770b6-a5dd-43d5-b9e3-9e97c0e1d693",
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
                    databaseName: "HIUSER",
                },
            ],
            filters: [],
            defaultValueDisplayMap: {},
            editingField: null,
            marksList: [
                {
                    value: "_all_",
                    id: "37876b0b-8b32-4b7c-885a-59aab9d5e3a8",
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
            activeMark: "37876b0b-8b32-4b7c-885a-59aab9d5e3a8",
            activeTool: "4",
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
            stylesId: "hi-report-bce7a0d9",
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
                            name: "booking_platform",
                            type: "text",
                        },
                    },
                    {
                        rows: 3,
                    },
                ],
                metadata_file: {
                    location: "Manish_18",
                    metadataFileName: "Metadata_new.metadata",
                },
                database: "HIUSER",
                fields: [
                    {
                        column: "travel_details.booking_platform",
                        columnID: "13118",
                        label: "booking_platform",
                        id: "414770b6-a5dd-43d5-b9e3-9e97c0e1d693",
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
                        databaseName: "HIUSER",
                    },
                ],
                rows: [],
                columns: ["travel_details.booking_platform"],
                filters: [],
                mark_fields: [],
                marks: [
                    {
                        value: "_all_",
                        id: "37876b0b-8b32-4b7c-885a-59aab9d5e3a8",
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
                        id: "37876b0b-8b32-4b7c-885a-59aab9d5e3a8",
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
                visualisation: "",
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
                dateFunctions: {},
                user: {
                    name: "hiadmin",
                    email: "admin@helicalinsight.com",
                    actualUserName: "hiadmin",
                    organization: "",
                    roles: ["ROLE_ADMIN", "ROLE_USER"],
                    profile: [],
                },
                selectedType: "Table",
                data: [
                    {
                        booking_platform: "Agent",
                    },
                    {
                        booking_platform: "Makemytrip",
                    },
                    {
                        booking_platform: "Website",
                    },
                ],
                lastModified: 1684906551145,
                limitBy: 10,
                offset: 0,
                pageSize: 10,
                dataId: "c7f8e485-861b-44e4-89e7-3e922bbcd7c6",
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
            },
            reportInfo: {
                location: "",
                uuid: "",
                reportName: "Untitled 1",
            },
            cellMenuData: null,
            showHiddenColumns: false,
            showHiddenRows: false,
            database: "HIUSER",
            dateFunctions: {},
            customChart: {
                selected: false,
                drawer: false,
                code: "",
                applied: false
            }
        },
    ],
    layout: {
        metadataShelf: true,
        toolsAreaShelf: true,
        fieldsAreaShelf: true,
    },
    hasUnsavedData: true,
    hrSidebar: "metadata",
    geoJsonData: {
        country: [],
        state: [],
        city: [],
    }
};
