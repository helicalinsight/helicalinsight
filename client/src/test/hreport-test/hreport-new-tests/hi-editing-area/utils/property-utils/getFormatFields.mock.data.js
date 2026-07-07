export const mock_data = {

    itemsData : [
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
          value: "ee78559a-c99c-40d0-886d-18346527d26f",
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
          value: ["pane"],
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
          value: "numeric",
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
          value: "",
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
          key: "thousandSperator",
          label: "Thousand Separator",
          value: false,
          elementType: "Switch",
          groupId: "format",
        },
        {
          key: "decimalPlace",
          label: "Decimal Place",
          placeHolder: "Enter Number",
          value: 2,
          elementType: "InputNumber",
          groupId: "format",
        },
        {
          key: "prefix",
          label: "Prefix",
          placeHolder: "Enter prefix",
          value: "",
          elementType: "Input",
          groupId: "format",
        },
        {
          key: "suffix",
          label: "Suffix",
          placeHolder: "Enter suffix",
          value: "%",
          elementType: "Input",
          groupId: "format",
        },
        {
          key: "displayUnits",
          label: "Display Units",
          value: "None",
          elementType: "Select",
          groupId: "format",
          values: [
            {
              key: "auto",
              label: "Auto",
            },
            {
              key: "k",
              label: "Thousands",
            },
            {
              key: "m",
              label: "Millions",
            },
            {
              key: "b",
              label: "Billions",
            },
            {
              key: "none",
              label: "None",
            },
          ],
        },
        {
          key: "percentage",
          label: "Percentage",
          value: true,
          elementType: "Switch",
          groupId: "format",
        },
      ] ,
      fieldsData : [
        {
          id: "ee78559a-c99c-40d0-886d-18346527d26f",
          values: {
            thousandSperator: false,
            decimalPlace: 2,
            prefix: "",
            suffix: "",
            displayUnits: "None",
            percentage: false,
            numberCustom: "",
            apply: ["pane"],
            isApplyClicked: false,
          },
        },
      ] ,
      expectedResult : {
        activeFieldId: "ee78559a-c99c-40d0-886d-18346527d26f",
        formatDatatype: "numeric",
        formatFields: [
          {
            id: "ee78559a-c99c-40d0-886d-18346527d26f",
            values: {
              apply: ["pane"],
              decimalPlace: 2,
              displayUnits: "None",
              field: "ee78559a-c99c-40d0-886d-18346527d26f",
              formatDatatype: "numeric",
              isApplyClicked: true,
              numberCustom: "",
              percentage: true,
              prefix: "",
              suffix: "%",
              thousandSperator: false,
            },
          },
        ],
      }
}