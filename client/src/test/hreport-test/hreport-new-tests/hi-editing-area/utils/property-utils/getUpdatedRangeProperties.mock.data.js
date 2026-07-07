export const mock_data = {
  reportData: {
    metadata: [
      {
        1: {
          name: "sum_dim_id",
          type: "numeric",
        },
        2: {
          name: "modified_date",
          type: "date",
        },
      },
      {
        rows: 76,
      },
    ],
    metadata_file: {
      location: "02_03",
      metadataFileName: "1122views.metadata",
    },
    database: "",
    fields: [
      {
        column: "View 1.dim_id",
        columnID: "35013",
        label: "sum_dim_id",
        id: "ee78559a-c99c-40d0-886d-18346527d26f",
        type: {
          backendDataType: "java.lang.Float",
          dataType: "numeric",
        },
        autogen_alias: "sum_dim_id",
        isNormalTable: false,
        tableAlias: "test",
        aggregate: ["db.generic.aggregate.sum"],
        orderByColumn: false,
        showOrderByColumn: false,
        addedAs: "column",
        floatingType: "",
        functionsDefinition: "",
        applyBeforeAggregate: false,
        hiddenIncludeInResultSet: false,
        metaDataAlias: "dim_id",
        databaseName: "",
      },
      {
        column: "View 1.modified_date",
        columnID: "35014",
        label: "modified_date",
        id: "59eb7057-2c94-4594-812d-9e4353ec1278",
        type: {
          backendDataType: "java.sql.Date",
          dataType: "date",
        },
        autogen_alias: "modified_date",
        isNormalTable: false,
        tableAlias: "test",
        groupBy: ["db.generic.groupBy.group"],
        orderByColumn: false,
        showOrderByColumn: false,
        addedAs: "row",
        floatingType: "discrete",
        functionsDefinition: "",
        applyBeforeAggregate: false,
        hiddenIncludeInResultSet: false,
        metaDataAlias: "modified_date",
        databaseName: "",
      },
    ],
    rows: ["View 1.modified_date"],
    columns: ["View 1.dim_id"],
    filters: [],
    mark_fields: [],
    marks: [
      {
        value: "_all_",
        id: "c86f1749-1d81-4e60-88ab-3aca8ac4027c",
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
      {
        value: "sum_dim_id",
        id: "ee78559a-c99c-40d0-886d-18346527d26f",
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
        id: "c86f1749-1d81-4e60-88ab-3aca8ac4027c",
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
      {
        value: "sum_dim_id",
        id: "ee78559a-c99c-40d0-886d-18346527d26f",
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
    visualisation: "GridChart",
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
      axisRange: {
        fields: [],
        activeDatatype: "",
        activeId: "",
      },
      cache: {
        isCacheEnabled: false,
        interval: "00:00:01",
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
    user: {
      name: "hiadmin",
      email: "admin@helicalinsight.com",
      actualUserName: "hiadmin",
      organization: "",
      roles: ["ROLE_ADMIN", "ROLE_USER"],
      profile: [],
    },
    selectedType: "GridChart",
    lastModified: 1680593521311,
    limitBy: 1000,
    offset: 0,
    dataId: "69985238-f6cb-4690-a6bf-2bba5e72d2b8",
  },
  itemsData: [
    {
      key: "show",
      label: "Show",
      value: false,
      elementType: "Switch",
      groupId: "title",
    },
    {
      key: "value",
      label: "Text",
      placeHolder: "Enter title",
      value: "",
      elementType: "Input",
      groupId: "title",
    },
    {
      key: "padding",
      label: "Padding",
      value: 0,
      elementType: "InputNumber",
      groupId: "title",
    },
    {
      key: "fontSize",
      label: "Font Size",
      tooltip: "Font Size in px",
      value: 32,
      elementType: "InputNumber",
      groupId: "title",
    },
    {
      key: "fontColor",
      label: "Font Color",
      value: {
        a: 1,
        b: 0,
        g: 0,
        r: 0,
      },
      elementType: "ColorPicker",
      groupId: "title",
    },
    {
      key: "alignment",
      label: "Alignment",
      value: "center",
      elementType: "Select",
      groupId: "title",
      values: [
        {
          key: "left",
          label: "Left",
        },
        {
          key: "right",
          label: "Right",
        },
        {
          key: "center",
          label: "Center",
        },
      ],
    },
    {
      key: "position",
      label: "Position",
      value: "top",
      elementType: "Select",
      groupId: "title",
      values: [
        {
          key: "top",
          label: "Top",
        },
        {
          key: "bottom",
          label: "Bottom",
        },
      ],
    },
    {
      key: "show",
      label: "Show",
      value: false,
      elementType: "Switch",
      groupId: "subTitle",
    },
    {
      key: "value",
      label: "Text",
      placeHolder: "Enter sub title",
      value: "",
      elementType: "Input",
      groupId: "subTitle",
    },
    {
      key: "padding",
      label: "Padding",
      value: 0,
      elementType: "InputNumber",
      groupId: "subTitle",
    },
    {
      key: "fontSize",
      label: "Font Size",
      tooltip: "Font Size in px",
      value: 24,
      elementType: "InputNumber",
      groupId: "subTitle",
    },
    {
      key: "fontColor",
      label: "Font Color",
      value: {
        a: 1,
        b: 0,
        g: 0,
        r: 0,
      },
      elementType: "ColorPicker",
      groupId: "subTitle",
    },
    {
      key: "alignment",
      label: "Alignment",
      value: "center",
      elementType: "Select",
      groupId: "subTitle",
      values: [
        {
          key: "left",
          label: "Left",
        },
        {
          key: "right",
          label: "Right",
        },
        {
          key: "center",
          label: "Center",
        },
      ],
    },
    {
      key: "position",
      label: "Position",
      value: "top",
      elementType: "Select",
      groupId: "subTitle",
      values: [
        {
          key: "top",
          label: "Top",
        },
        {
          key: "bottom",
          label: "Bottom",
        },
      ],
    },
    {
      key: "isCacheEnabled",
      label: "Cache",
      value: false,
      elementType: "Switch",
      groupId: "cache",
    },
    {
      key: "interval",
      label: "Interval",
      value: "00:00:01",
      elementType: "TimePicker",
      defaultValue: "00:00:01",
      showNow: false,
      groupId: "cache",
      tooltip:
        "The report will be refreshed after predefined interval(HH:MM:SS).",
    },
    {
      key: "field",
      label: "Field",
      value: "",
      elementType: "Select",
      groupId: "format",
      values: [
        {
          key: "ee78559a-c99c-40d0-886d-18346527d26f",
          label: "sum_dim_id",
        },
        {
          key: "59eb7057-2c94-4594-812d-9e4353ec1278",
          label: "modified_date",
        },
      ],
    },
    {
      key: "apply",
      label: "Apply",
      tooltip: "Apply On",
      value: [],
      elementType: "Select",
      groupId: "format",
      multiSelect: true,
      values: [
        {
          key: "axis",
          label: "Axis",
        },
        {
          key: "pane",
          label: "Pane",
        },
        {
          key: "tooltip",
          label: "Tooltip",
        },
        {
          key: "label",
          label: "Label",
        },
        {
          key: "actions",
          label: "Actions",
        },
      ],
    },
    {
      key: "formatDatatype",
      label: "Data Type",
      tooltip: "Data Type",
      value: "",
      elementType: "Select",
      groupId: "format",
      disabled: true,
      values: [
        {
          key: "numeric",
          label: "Number",
        },
        {
          key: "date",
          label: "Date",
        },
        {
          key: "time",
          label: "Time",
        },
        {
          key: "dateTime",
          label: "DateTime",
        },
      ],
    },
    {
      key: "formatColorField",
      label: "Field",
      value: "",
      elementType: "Select",
      groupId: "color",
      values: [
        {
          key: "ee78559a-c99c-40d0-886d-18346527d26f",
          label: "sum_dim_id",
        },
        {
          key: "59eb7057-2c94-4594-812d-9e4353ec1278",
          label: "modified_date",
        },
      ],
    },
    {
      key: "formatColorStyle",
      label: "Format Style",
      value: "",
      elementType: "Select",
      groupId: "color",
      values: [
        {
          key: "gradient",
          label: "Gradient",
        },
        {
          key: "fieldValue",
          label: "Field Value",
        },
      ],
    },
    {
      key: "barType",
      label: "Type",
      value: "stacked",
      elementType: "Select",
      groupId: "bar",
      values: [
        {
          key: "stacked",
          label: "Stacked",
        },
        {
          key: "grouped",
          label: "Grouped",
        },
        {
          key: "percentage",
          label: "Percentage",
        },
      ],
    },
    {
      key: "showRadial",
      label: "Show Radial",
      value: false,
      elementType: "Switch",
      groupId: "radial",
    },
    {
      key: "legendPosition",
      label: "Position",
      value: "right",
      elementType: "Select",
      groupId: "legend",
      values: [
        {
          key: "top",
          label: "Top",
        },
        {
          key: "right",
          label: "Right",
        },
        {
          key: "left",
          label: "Left",
        },
        {
          key: "bottom",
          label: "Bottom",
        },
        {
          key: "none",
          label: "None",
        },
      ],
    },
    {
      key: "applyRangeOn",
      label: "Apply On",
      value: "ee78559a-c99c-40d0-886d-18346527d26f",
      elementType: "Select",
      groupId: "axisRange",
      values: [
        {
          key: "ee78559a-c99c-40d0-886d-18346527d26f",
          label: "sum_dim_id",
        },
        {
          key: "59eb7057-2c94-4594-812d-9e4353ec1278",
          label: "modified_date",
        },
      ],
    },
    {
      key: "minRange",
      label: "Min value",
      value: "50",
      elementType: "Input",
      groupId: "axisRange",
      tooltip:
        "Minimum value on axis will be adjusted automatically based on the data.",
    },
    {
      key: "maxRange",
      label: "Max value",
      value: "500",
      elementType: "Input",
      groupId: "axisRange",
    },
  ],
  fieldsData: [
    {
      id: "ee78559a-c99c-40d0-886d-18346527d26f",
      data: {
        name: "",
        minRange: "",
        maxRange: "",
        dataType: "numeric",
      },
    },
  ],
  expectedResult: {
    activeDatatype: "numeric",
    activeId: "ee78559a-c99c-40d0-886d-18346527d26f",
    fields: [
      {
        data: {
          applyRangeOn: "ee78559a-c99c-40d0-886d-18346527d26f",
          dataType: "numeric",
          maxRange: "500",
          minRange: "50",
          name: "sum_dim_id",
        },
        id: "ee78559a-c99c-40d0-886d-18346527d26f",
      },
    ],
    gridLines: [],
  },
};

export const mock_data1 = {
  reportData: {
    database: "",
    fields: [
      {
        column: "View 1.dim_id",
        columnID: "35013",
        label: "sum_dim_id",
        id: "ee78559a-c99c-40d0-886d-18346527d26f",
        type: {
          backendDataType: "java.lang.Float",
          dataType: "numeric",
        },
        autogen_alias: "sum_dim_id",
        isNormalTable: false,
        tableAlias: "test",
        aggregate: ["db.generic.aggregate.sum"],
        orderByColumn: false,
        showOrderByColumn: false,
        addedAs: "column",
        floatingType: "continous",
        functionsDefinition: "",
        applyBeforeAggregate: false,
        hiddenIncludeInResultSet: false,
        metaDataAlias: "dim_id",
        databaseName: "",
      },
      {
        column: "View 1.modified_date",
        columnID: "35014",
        label: "modified_date",
        id: "59eb7057-2c94-4594-812d-9e4353ec1278",
        type: {
          backendDataType: "java.sql.Date",
          dataType: "date",
        },
        autogen_alias: "modified_date",
        isNormalTable: false,
        tableAlias: "test",
        groupBy: ["db.generic.groupBy.group"],
        orderByColumn: false,
        showOrderByColumn: false,
        addedAs: "row",
        floatingType: "discrete",
        functionsDefinition: "",
        applyBeforeAggregate: false,
        hiddenIncludeInResultSet: false,
        metaDataAlias: "modified_date",
        databaseName: "",
      },
    ],
    rows: ["View 1.modified_date"],
    columns: ["View 1.dim_id"],
    filters: [],
    mark_fields: [],
    marks: [
      {
        value: "_all_",
        id: "c86f1749-1d81-4e60-88ab-3aca8ac4027c",
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
      {
        value: "sum_dim_id",
        id: "ee78559a-c99c-40d0-886d-18346527d26f",
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
        id: "c86f1749-1d81-4e60-88ab-3aca8ac4027c",
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
      {
        value: "sum_dim_id",
        id: "ee78559a-c99c-40d0-886d-18346527d26f",
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
    visualisation: "GridChart",
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
      axisRange: {
        fields: [],
        activeDatatype: "",
        activeId: "",
      },
      cache: {
        isCacheEnabled: false,
        interval: "00:00:01",
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
    user: {
      name: "hiadmin",
      email: "admin@helicalinsight.com",
      actualUserName: "hiadmin",
      organization: "",
      roles: ["ROLE_ADMIN", "ROLE_USER"],
      profile: [],
    },
    selectedType: "GridChart",
    lastModified: 1680593521311,
    limitBy: 1000,
    offset: 0,
    dataId: "69985238-f6cb-4690-a6bf-2bba5e72d2b8",
  },
  itemsData: [
    {
      key: "show",
      label: "Show",
      value: false,
      elementType: "Switch",
      groupId: "title",
    },
    {
      key: "value",
      label: "Text",
      placeHolder: "Enter title",
      value: "",
      elementType: "Input",
      groupId: "title",
    },
    {
      key: "padding",
      label: "Padding",
      value: 0,
      elementType: "InputNumber",
      groupId: "title",
    },
    {
      key: "fontSize",
      label: "Font Size",
      tooltip: "Font Size in px",
      value: 32,
      elementType: "InputNumber",
      groupId: "title",
    },
    {
      key: "fontColor",
      label: "Font Color",
      value: {
        a: 1,
        b: 0,
        g: 0,
        r: 0,
      },
      elementType: "ColorPicker",
      groupId: "title",
    },
    {
      key: "alignment",
      label: "Alignment",
      value: "center",
      elementType: "Select",
      groupId: "title",
      values: [
        {
          key: "left",
          label: "Left",
        },
        {
          key: "right",
          label: "Right",
        },
        {
          key: "center",
          label: "Center",
        },
      ],
    },
    {
      key: "position",
      label: "Position",
      value: "top",
      elementType: "Select",
      groupId: "title",
      values: [
        {
          key: "top",
          label: "Top",
        },
        {
          key: "bottom",
          label: "Bottom",
        },
      ],
    },
    {
      key: "show",
      label: "Show",
      value: false,
      elementType: "Switch",
      groupId: "subTitle",
    },
    {
      key: "value",
      label: "Text",
      placeHolder: "Enter sub title",
      value: "",
      elementType: "Input",
      groupId: "subTitle",
    },
    {
      key: "padding",
      label: "Padding",
      value: 0,
      elementType: "InputNumber",
      groupId: "subTitle",
    },
    {
      key: "fontSize",
      label: "Font Size",
      tooltip: "Font Size in px",
      value: 24,
      elementType: "InputNumber",
      groupId: "subTitle",
    },
    {
      key: "fontColor",
      label: "Font Color",
      value: {
        a: 1,
        b: 0,
        g: 0,
        r: 0,
      },
      elementType: "ColorPicker",
      groupId: "subTitle",
    },
    {
      key: "alignment",
      label: "Alignment",
      value: "center",
      elementType: "Select",
      groupId: "subTitle",
      values: [
        {
          key: "left",
          label: "Left",
        },
        {
          key: "right",
          label: "Right",
        },
        {
          key: "center",
          label: "Center",
        },
      ],
    },
    {
      key: "position",
      label: "Position",
      value: "top",
      elementType: "Select",
      groupId: "subTitle",
      values: [
        {
          key: "top",
          label: "Top",
        },
        {
          key: "bottom",
          label: "Bottom",
        },
      ],
    },
    {
      key: "isCacheEnabled",
      label: "Cache",
      value: false,
      elementType: "Switch",
      groupId: "cache",
    },
    {
      key: "interval",
      label: "Interval",
      value: "00:00:01",
      elementType: "TimePicker",
      defaultValue: "00:00:01",
      showNow: false,
      groupId: "cache",
      tooltip:
        "The report will be refreshed after predefined interval(HH:MM:SS).",
    },
    {
      key: "field",
      label: "Field",
      value: "",
      elementType: "Select",
      groupId: "format",
      values: [
        {
          key: "ee78559a-c99c-40d0-886d-18346527d26f",
          label: "sum_dim_id",
        },
        {
          key: "59eb7057-2c94-4594-812d-9e4353ec1278",
          label: "modified_date",
        },
      ],
    },
    {
      key: "apply",
      label: "Apply",
      tooltip: "Apply On",
      value: [],
      elementType: "Select",
      groupId: "format",
      multiSelect: true,
      values: [
        {
          key: "axis",
          label: "Axis",
        },
        {
          key: "pane",
          label: "Pane",
        },
        {
          key: "tooltip",
          label: "Tooltip",
        },
        {
          key: "label",
          label: "Label",
        },
        {
          key: "actions",
          label: "Actions",
        },
      ],
    },
    {
      key: "formatDatatype",
      label: "Data Type",
      tooltip: "Data Type",
      value: "",
      elementType: "Select",
      groupId: "format",
      disabled: true,
      values: [
        {
          key: "numeric",
          label: "Number",
        },
        {
          key: "date",
          label: "Date",
        },
        {
          key: "time",
          label: "Time",
        },
        {
          key: "dateTime",
          label: "DateTime",
        },
      ],
    },
    {
      key: "formatColorField",
      label: "Field",
      value: "",
      elementType: "Select",
      groupId: "color",
      values: [
        {
          key: "ee78559a-c99c-40d0-886d-18346527d26f",
          label: "sum_dim_id",
        },
        {
          key: "59eb7057-2c94-4594-812d-9e4353ec1278",
          label: "modified_date",
        },
      ],
    },
    {
      key: "formatColorStyle",
      label: "Format Style",
      value: "",
      elementType: "Select",
      groupId: "color",
      values: [
        {
          key: "gradient",
          label: "Gradient",
        },
        {
          key: "fieldValue",
          label: "Field Value",
        },
      ],
    },
    {
      key: "barType",
      label: "Type",
      value: "stacked",
      elementType: "Select",
      groupId: "bar",
      values: [
        {
          key: "stacked",
          label: "Stacked",
        },
        {
          key: "grouped",
          label: "Grouped",
        },
        {
          key: "percentage",
          label: "Percentage",
        },
      ],
    },
    {
      key: "showRadial",
      label: "Show Radial",
      value: false,
      elementType: "Switch",
      groupId: "radial",
    },
    {
      key: "legendPosition",
      label: "Position",
      value: "right",
      elementType: "Select",
      groupId: "legend",
      values: [
        {
          key: "top",
          label: "Top",
        },
        {
          key: "right",
          label: "Right",
        },
        {
          key: "left",
          label: "Left",
        },
        {
          key: "bottom",
          label: "Bottom",
        },
        {
          key: "none",
          label: "None",
        },
      ],
    },
    {
      key: "applyRangeOn",
      label: "Apply On",
      value: "ee78559a-c99c-40d0-886d-18346527d26f",
      elementType: "Select",
      groupId: "axisRange",
      values: [
        {
          key: "ee78559a-c99c-40d0-886d-18346527d26f",
          label: "sum_dim_id",
        },
        {
          key: "59eb7057-2c94-4594-812d-9e4353ec1278",
          label: "modified_date",
        },
      ],
    },
    {
      key: "minRange",
      label: "Min value",
      value: "50",
      elementType: "Input",
      groupId: "axisRange",
      tooltip:
        "Minimum value on axis will be adjusted automatically based on the data.",
    },
    {
      key: "maxRange",
      label: "Max value",
      value: "500",
      elementType: "Input",
      groupId: "axisRange",
    },
  ],
  fieldsData: [
    {
      id: "ee78559a-c99c-40d0-886d-18346527d26f",
      data: {
        name: "",
        minRange: "",
        maxRange: "",
        dataType: "numeric",
      },
    },
  ],
  expectedResult: {
    activeDatatype: "numeric",
    activeId: "ee78559a-c99c-40d0-886d-18346527d26f",
    fields: [
      {
        data: {
          applyRangeOn: "ee78559a-c99c-40d0-886d-18346527d26f",
          dataType: "numeric",
          maxRange: "500",
          minRange: "50",
          name: "sum_dim_id",
        },
        id: "ee78559a-c99c-40d0-886d-18346527d26f",
      },
    ],
    gridLines: [],
  },
};