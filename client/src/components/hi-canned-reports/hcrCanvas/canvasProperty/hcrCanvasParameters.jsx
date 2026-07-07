import { Button, Collapse, Input, Select, Space, Switch, Tooltip } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { hcrActions } from "../../../../redux/actions";
import {
  hcrDSParameter,
  hcrParaDate,
  hcrParaDateAndTime,
  hcrParaDateAndTimeRange,
  hcrParaDateRange,
  hcrParaInput,
  hcrParaQueryBasedDropdownList,
} from "../../hcr-constants";
import { handleParaFilterType } from "../../hcrHelperMethods";
import { getLabel } from "../hcrCanvasPaneHelperMethods";

const dateFormats = [
  {
    name: "DD-MM-YYYY",
  },
  {
    name: "YYYY-MM-DD",
  },
  {
    name: "MM-DD-YYYY",
  },
  {
    name: "D-M-YYYY",
  },
  {
    name: "YYYY-M-D",
  },
];

const dateTimeFormats = [
  {
    name: "DD-MM-YYYY HH:mm:ss.S",
  },
  {
    name: "YYYY-MM-DD HH:mm:ss.S",
  },
  {
    name: "MM-DD-YYYY HH:mm:ss.S",
  },
  {
    name: "D-M-YYYY HH:mm:ss.S",
  },
  {
    name: "YYYY-M-D HH:mm:ss.S",
  },
];

const paraTypes = [
  hcrParaInput,
  // 'Number',
  // 'Dropdown List',
  hcrParaQueryBasedDropdownList,
  hcrParaDate,
  hcrParaDateAndTime,
  // 'Date and Time w/Seconds',
  hcrParaDateRange,
  hcrParaDateAndTimeRange,
];

const rangeSelectionTypes = [
  {
    label: "Start",
    key: "start",
  },
  {
    label: "End",
    key: "end",
  },
];

const multipleTypes = [
  { key: "true", value: true },
  { key: "false", value: false },
];

function Parameter({
  parameter,
  EditorPanels,
  parametersMenu,
  tooltipInfo = {},
}) {
  const { InputFiled, Position, InputNumberFiled, Size, SelectField } =
    EditorPanels;
  const { name, canvasValues = {}, executeQueryData, id } = parameter;

  const {
    tooltip_hideFilter = {},
    tooltip_fieldType = {},
    tooltip_defaultValue = {},
    tooltip_options = {},
    tooltip_multiple = {},
    tooltip_display = {},
    tooltip_value = {},
    tooltip_quotes = {},
    tooltip_openQuotes = {},
    tooltip_closeQuotes = {},
    tooltip_dateRangeParameters = {},
    tooltip_dateRangeStart = {},
    tooltip_dateRangeEnd = {},
    tooltip_displayFormat = {},
    tooltip_valueFormat = {},
  } = tooltipInfo || {};
  const dispatch = useDispatch();

  let optionSelectTypes = [
    [
      {
        label: "Display",
        key: "display",
        options: executeQueryData.field,
      },
      {
        label: "Value",
        key: "value",
        options: executeQueryData.field,
      },
    ],
  ];

  if (canvasValues.filterType?.toLowerCase().includes("date")) {
    optionSelectTypes = [
      ...optionSelectTypes,
      [
        {
          label: "Display Format",
          key: "displayFormat",
          options: canvasValues.filterType?.toLowerCase().includes("time")
            ? dateTimeFormats
            : dateFormats,
        },
        {
          label: "Value Format",
          key: "valueFormat",
          options: canvasValues.filterType?.toLowerCase().includes("time")
            ? dateTimeFormats
            : dateFormats,
        },
      ],
    ];
  }

  return (
    <div className="canvas-parameter">
      <Collapse size={"small"} className="canvas-property-collapse">
        <Collapse.Panel
          header={<span className="canvas-property-title">{name}</span>}
          key={"parameter"}
        >
          <Space>
            <div className="parameter-label">
              {getLabel({ label: "Hide Filter", tooltip: tooltip_hideFilter })}
            </div>
            <Switch
              disabled={canvasValues.disabled}
              checked={canvasValues.isChecked}
              onChange={(isChecked) => {
                dispatch(
                  hcrActions.handleEditingDsPaneItem({
                    dataSourcePane: hcrDSParameter,
                    itemId: id,
                    key: "canvasValues",
                    value: { ...canvasValues, isChecked },
                  }),
                );
              }}
            />
          </Space>
          <div>
            <div className="parameter-label">
              {" "}
              {getLabel({ label: "Filter Type", tooltip: tooltip_fieldType })}
            </div>
            <Select
              style={{ width: 235 }}
              disabled={canvasValues.isChecked}
              onChange={(value) => {
                handleParaFilterType({ canvasValues, dispatch, id, value });
              }}
              value={canvasValues.filterType}
              dropdownClassName="canvas-parameter-select"
            >
              {paraTypes.map((paraType) => {
                return (
                  <Select.Option value={paraType}>{paraType}</Select.Option>
                );
              })}
            </Select>
          </div>
          <div>
            <div className="property-label">
              {" "}
              {getLabel({
                label: "Default Value",
                tooltip: tooltip_defaultValue,
              })}
            </div>
            <Input
              disabled={canvasValues.isChecked}
              value={canvasValues.defaultValue}
              onChange={(e) => {
                dispatch(
                  hcrActions.handleEditingDsPaneItem({
                    dataSourcePane: hcrDSParameter,
                    itemId: id,
                    key: "canvasValues",
                    value: { ...canvasValues, defaultValue: e.target.value },
                  }),
                );
              }}
            />
          </div>
          {(canvasValues.filterType === hcrParaDateRange ||
            canvasValues.filterType === hcrParaDateAndTimeRange) && (
            <Collapse size={"small"} className="canvas-parameter-collapse">
              <Collapse.Panel
                header={
                  <span className="canvas-parameter-inner-title">
                    {getLabel({
                      label: "Range Parameters",
                      tooltip: tooltip_dateRangeParameters,
                    })}
                  </span>
                }
                key={"range-parameters"}
              >
                <div className="para-select-group">
                  {rangeSelectionTypes.map((ele) => {
                    return (
                      <div>
                        <div className="parameter-label">
                          {ele.label === "Start" || ele.label === "End"
                            ? getLabel({
                                label: ele.label,
                                tooltip:
                                  ele.label === "Start"
                                    ? tooltip_dateRangeStart
                                    : tooltip_dateRangeEnd,
                                placement:
                                  ele.label === "End" ? "left" : undefined,
                              })
                            : ele.label}
                        </div>
                        <Select
                          style={{ width: 110 }}
                          disabled={canvasValues.isChecked}
                          onChange={(value) => {
                            let dupCanvasVals = { ...canvasValues };
                            if (ele.key === "start") {
                              if (dupCanvasVals.end === value) {
                                dupCanvasVals.end = "";
                              }
                            } else if (ele.key === "end") {
                              if (dupCanvasVals.start === value) {
                                dupCanvasVals.start = "";
                              }
                            }
                            let paraDisabled = true,
                              paraChecked = true,
                              paraToChange = value;
                            if (value === undefined) {
                              paraDisabled = false;
                              paraChecked = false;
                              if (canvasValues[ele.key]) {
                                paraToChange = canvasValues[ele.key];
                              }
                            }
                            dispatch(
                              hcrActions.handleEditingDsPaneItem({
                                dataSourcePane: hcrDSParameter,
                                itemId: id,
                                key: "canvasValues",
                                value: { ...dupCanvasVals, [ele.key]: value },
                                isDateRange: true,
                                paraToChange,
                                paraDisabled,
                                paraChecked,
                              }),
                            );
                          }}
                          value={canvasValues[ele.key]}
                          allowClear={canvasValues[ele.key]}
                          dropdownClassName="canvas-parameter-select"
                        >
                          {parametersMenu
                            .filter(
                              (i) =>
                                !(
                                  i.canvasValues.start === i.name ||
                                  i.canvasValues.end === i.name ||
                                  (i.canvasValues.isChecked &&
                                    i.canvasValues.disabled)
                                ) ||
                                i.name === canvasValues.start ||
                                i.name === canvasValues.end,
                            )
                            .map((para) => {
                              return (
                                <Select.Option value={para.name}>
                                  {para.name}
                                </Select.Option>
                              );
                            })}
                        </Select>
                      </div>
                    );
                  })}
                </div>
              </Collapse.Panel>
            </Collapse>
          )}
          <Collapse size={"small"} className="canvas-parameter-collapse">
            <Collapse.Panel
              header={
                <span className="canvas-parameter-inner-title">
                  {" "}
                  {getLabel({
                    label: "Options",
                    tooltip: tooltip_options,
                  })}
                </span>
              }
              key={"options"}
            >
              <div>
                <div className="parameter-label">
                  {" "}
                  {getLabel({
                    label: "Multiple",
                    tooltip: tooltip_multiple,
                  })}
                </div>
                <Select
                  style={{ width: 220 }}
                  disabled={canvasValues.isChecked}
                  onChange={(value) => {
                    dispatch(
                      hcrActions.handleEditingDsPaneItem({
                        dataSourcePane: hcrDSParameter,
                        itemId: id,
                        key: "canvasValues",
                        value: { ...canvasValues, multipleType: value },
                      }),
                    );
                  }}
                  value={canvasValues.multipleType}
                  dropdownClassName="canvas-parameter-select"
                >
                  {multipleTypes.map((multiType) => {
                    return (
                      <Select.Option value={multiType.value}>
                        {multiType.key}
                      </Select.Option>
                    );
                  })}
                </Select>
              </div>
              {optionSelectTypes.map((arr) => {
                return (
                  <div className="para-select-group">
                    {arr.map((ele) => {
                      return (
                        <div>
                          <div className="parameter-label">
                            {ele.label === "Display" ||
                            ele.label === "Value" ||
                            ele.label === "Display Format" ||
                            ele.label === "Value Format"
                              ? getLabel({
                                  label: ele.label,
                                  tooltip: {
                                    Display: tooltip_display,
                                    Value: tooltip_value,
                                    "Display Format": tooltip_displayFormat,
                                    "Value Format": tooltip_valueFormat,
                                  }[ele.label],
                                  placement:
                                    ele.label === "Value" ||
                                    ele.label === "Value Format"
                                      ? "left"
                                      : undefined,
                                })
                              : ele.label}
                          </div>
                          <Select
                            style={{ width: 110 }}
                            disabled={canvasValues.isChecked}
                            onChange={(value) => {
                              dispatch(
                                hcrActions.handleEditingDsPaneItem({
                                  dataSourcePane: hcrDSParameter,
                                  itemId: id,
                                  key: "canvasValues",
                                  value: { ...canvasValues, [ele.key]: value },
                                  isFormatChnged: ele.key
                                    .toLowerCase()
                                    .includes("format")
                                    ? true
                                    : false,
                                }),
                              );
                            }}
                            value={canvasValues[ele.key]}
                            dropdownClassName="canvas-parameter-select"
                          >
                            {ele.options.map((opt) => {
                              return (
                                <Select.Option value={opt.name}>
                                  {opt.name}
                                </Select.Option>
                              );
                            })}
                          </Select>
                        </div>
                      );
                    })}
                  </div>
                );
              })}
              <Collapse size={"small"} className="canvas-parameter-collapse">
                <Collapse.Panel
                  header={
                    <span className="canvas-parameter-inner-title">
                      {" "}
                      {getLabel({
                        label: "Quotes",
                        tooltip: tooltip_quotes,
                      })}
                    </span>
                  }
                  key={"options1"}
                >
                  {[{ label: "open" }, { label: "close" }].map((ele) => {
                    return (
                      <div>
                        <div className="property-label">
                          {ele.label === "open" || ele.label === "close"
                            ? getLabel({
                                label: ele.label,
                                tooltip:
                                  ele.label === "open"
                                    ? tooltip_openQuotes
                                    : tooltip_closeQuotes,
                              })
                            : ele.label}
                        </div>
                        <Input
                          style={{ width: "105%" }}
                          disabled={canvasValues.isChecked}
                          value={canvasValues[ele.label]}
                          onChange={(e) => {
                            dispatch(
                              hcrActions.handleEditingDsPaneItem({
                                dataSourcePane: hcrDSParameter,
                                itemId: id,
                                key: "canvasValues",
                                value: {
                                  ...canvasValues,
                                  [ele.label]: e.target.value,
                                },
                              }),
                            );
                          }}
                        />
                      </div>
                    );
                  })}
                </Collapse.Panel>
              </Collapse>
            </Collapse.Panel>
          </Collapse>
        </Collapse.Panel>
      </Collapse>
    </div>
  );
}

export default function HCRCanvasParameters({ EditorPanels }) {
  const activeTab =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey,
      ),
    ) || {};
  const { dsPaneTypes } = activeTab;
  const parameters = dsPaneTypes.find((ele) => ele.key === "parameter") || {};
  const dispatch = useDispatch();
  const HcrPropertiesConfiguration = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations
        ?.HcrPropertiesConfiguration || {},
  );
  const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
  const { tooltip_parameters = {} } = tooltipInfo || {};
  return (
    <div className="canvas-parameters">
      <Collapse size={"small"} className="canvas-property-collapse">
        <Collapse.Panel
          header={
            <span className="canvas-property-title">
              {getLabel({ label: "Parameters", tooltip: tooltip_parameters })}
            </span>
          }
          key={"parameter"}
        >
          {parameters.menu.map((parameter) => {
            return (
              <Parameter
                parameter={parameter}
                EditorPanels={EditorPanels}
                parametersMenu={parameters.menu}
                tooltipInfo={tooltipInfo}
              />
            );
          })}
        </Collapse.Panel>
      </Collapse>
    </div>
  );
}

{
  /* <SelectField
                    label={<div className="parameter-label">Filter Type</div>}
                    value={canvasValues.filterType}
                    options={paraTypes.map(paraType => {
                        return {
                            label: paraType,
                            value: paraType
                        }
                    })}
                    width={235}
                    onChange={(value) => {
                        let displayFormat = canvasValues.displayFormat, valueFormat = canvasValues.valueFormat;
                        if (!displayFormat) {
                            displayFormat = getFormat(value);
                        }
                        if (!valueFormat) {
                            valueFormat = getFormat(value);
                        }
                        dispatch(hcrActions.handleEditingDsPaneItem({ dataSourcePane: hcrDSParameter, itemId: id, key: 'canvasValues', value: { ...canvasValues, filterType: value, multipleType: value === hcrParaQueryBasedDropdownList ? true : false, displayFormat, valueFormat }, isFilterTypeChanged: true }));
                    }}
                /> */
}

{
  /* <InputFiled
                    label={<div className="property-label">Default Value</div>}
                    value={canvasValues.defaultValue}
                    onChange={(value) => {
                        dispatch(hcrActions.handleEditingDsPaneItem({ dataSourcePane: hcrDSParameter, itemId: id, key: 'canvasValues', value: { ...canvasValues, defaultValue: value } }));
                    }}
                /> */
}

//     <SelectField
//     label={<div className="parameter-label">{ele.label}</div>}
//     value={canvasValues[ele.key]}
//     options={parametersMenu?.map(para => {
//         return {
//             label: para.name,
//             value: para.name
//         }
//     })}
//     width={110}
//     onChange={(value) => {
//         let dupCanvasVals = { ...canvasValues };
//         if (ele.key === 'start') {
//             if (dupCanvasVals.end === value) {
//                 dupCanvasVals.end = '';
//             }
//         } else if (ele.key === 'end') {
//             if (dupCanvasVals.start === value) {
//                 dupCanvasVals.start = '';
//             }
//         }
//         dispatch(hcrActions.handleEditingDsPaneItem({ dataSourcePane: hcrDSParameter, itemId: id, key: 'canvasValues', value: { ...dupCanvasVals, [ele.key]: value }, isDateRange: true, paraToChange: value }));
//     }}
// />

{
  /* <SelectField
                            label={<div className="parameter-label">Multiple</div>}
                            value={canvasValues.multipleType}
                            options={multipleTypes.map(multiType => {
                                return {
                                    label: multiType.key,
                                    value: multiType.value
                                }
                            })}
                            width={220}
                            onChange={(value) => {
                                dispatch(hcrActions.handleEditingDsPaneItem({ dataSourcePane: hcrDSParameter, itemId: id, key: 'canvasValues', value: { ...canvasValues, multipleType: value } }));
                            }}
                        /> */
}

// <SelectField
//     label={<div className="parameter-label">{ele.label}</div>}
//     value={canvasValues[ele.key]}
//     options={ele.options?.map(opt => {
//         return {
//             label: opt.name,
//             value: opt.name
//         }
//     })}
//     width={110}
//     onChange={(value) => {
//         dispatch(hcrActions.handleEditingDsPaneItem({ dataSourcePane: hcrDSParameter, itemId: id, key: 'canvasValues', value: { ...canvasValues, [ele.key]: value }, isFormatChnged: ele.key.toLowerCase().includes('format') ? true : false }));
//     }}
// />
