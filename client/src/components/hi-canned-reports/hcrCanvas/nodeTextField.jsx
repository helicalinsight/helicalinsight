import { Checkbox, Select } from "antd";
import { useSelector } from "react-redux";
import FieldSelector from "./fieldSelector";

export default function NodeTextField({
  onPropertyChange,
  SelectField,
  positionType,
  stretchType,
  rotationType,
  markUp,
  evaluationTime,
  InputFiled,
  pattern,
  getTooltipInfo,
  nodeValues,
  groupEvalOptions = [],
  cascaderOptions = []
}) {
  const HcrPropertiesConfiguration = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations
        ?.HcrPropertiesConfiguration || {}
  );
  const activeTab =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey
      )
    ) || {};
  const { canvasProperties, tableStyles = [] } = activeTab;
  const pageStyles = canvasProperties?.pageStyles;
  const customStyles = [...(pageStyles?.options || []), ...(tableStyles || [])];
  const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
  const {
    tooltip_poition,
    tooltip_stretch,
    tooltip_rotationType,
    tooltip_markUp,
    tooltip_evalTime,
    tooltip_evalGroupName,
    tooltip_styleNameReference,
    tooltip_pattern,
    tooltip_patternExpression,
    tooltip_printWhenExpression,
    tooltip_printRepeatedValues,
    tooltip_removeLineWhenBlank,
    tooltip_printInFirstWholeBand,
    tooltip_printWhenDetailOverflows,
    tooltip_stretchWithOverflow,
    tooltip_blankWhenNull,
  } = tooltipInfo || {};

  const selectTypes = [
    {
      title: "Position",
      options: positionType,
      key: "position",
      tooltip: tooltip_poition,
      allowClear: true
    },
    {
      title: "Stretch",
      options: stretchType,
      key: "stretch",
      tooltip: tooltip_stretch,
      allowClear: true
    },
    {
      title: "Rotation",
      options: rotationType,
      key: "rotation",
      tooltip: tooltip_rotationType,
      allowClear: true
    },
    {
      title: "Mark Up",
      options: markUp,
      key: "markUp",
      tooltip: tooltip_markUp,
      allowClear: true
    },
    {
      title: "Eval Time",
      options: evaluationTime,
      key: "evalTime",
      tooltip: tooltip_evalTime,
      allowClear: true
    },
    {
      title: "Eval Group",
      options: nodeValues?.evalTime === "Group" ? groupEvalOptions : [],
      key: "evalGroup",
      tooltip: tooltip_evalGroupName,
      allowClear: true
    },
    {
      title: "Style Name",
      options: ["None", ...(customStyles.map((ele) => ele.styleName) || [])],
      key: "styleName",
      tooltip: tooltip_styleNameReference,
      allowClear: false
    },
  ];

  const inputTypes = [
    {
      label: "Pattern",
      key: "pattern",
      tooltip: tooltip_pattern,
    },
    {
      label: "Pattern Expression",
      key: "patternExp",
      tooltip: tooltip_patternExpression,
      expressionEditor: true
    },
    {
      label: "Print When Expression",
      key: "printWhenExp",
      tooltip: tooltip_printWhenExpression,
      expressionEditor: true
    },
  ];

  const checkOptions = [
    {
      label: "Print repeated values",
      key: "printRepeatedValues",
      tooltip: tooltip_printRepeatedValues,
    },
    {
      label: "Remove line when blank",
      key: "removeLineWhenBlank",
      tooltip: tooltip_removeLineWhenBlank,
    },
    {
      label: "Print in first whole section",
      key: "printInFirstWholeSection",
      tooltip: tooltip_printInFirstWholeBand,
    },
    {
      label: "Print when record overflows",
      key: "printWhenRecordOverflows",
      tooltip: tooltip_printWhenDetailOverflows,
    },
    {
      label: "Stretch with overflows",
      key: "stretchWithOverflows",
      tooltip: tooltip_stretchWithOverflow,
    },
    {
      label: "Blank when null",
      key: "blankWhenNull",
      tooltip: tooltip_blankWhenNull,
    },
  ];

  const onChange = (isChecked, key) => {
    onPropertyChange({ key, value: isChecked });
  };

  return (
    <>
      <div className="textfield-select-properties">
        <div style={{ display: 'flex', flexWrap: 'wrap' }}>
          {selectTypes.map((ele) => {
            return (
              <div className="property-group-wrapper">
                <div className="property-label">{getTooltipInfo({ label: ele.title, tooltip: ele.tooltip })}</div>
                <Select
                  value={
                    ele.key === "styleName"
                      ? nodeValues.styleName ?? "None"
                      : nodeValues[ele.key]
                  }
                  options={ele.options.map((option) => {
                    return {
                      label: option,
                      value: option,
                    };
                  })}
                  size="small"
                  style={{ width: 110 }}
                  onChange={(value) => {
                    let payload = { key: ele.key, value };
                    if (ele.key === "styleName" && value !== "None") {
                      const selectedStyles = customStyles.find(
                        (style) => style.styleName === value
                      );
                      const { id, name, isChanged, isConditionalStyleReq, tableId, bandsApplicable, ...rest } = selectedStyles;
                      payload = { ...payload, styles: rest };
                    }
                    if (ele.key === "styleName" && value === "None") {
                      payload = {
                        ...payload,
                        styles: {
                          borders: {},
                          padding: {},
                          fontFill: "#000000",
                          fill: "#fefefe",
                          fontSize: 10,
                          fontFamily: "Serif",
                          bold: false,
                          italic: false,
                          underLine: false,
                          strikeThrough: false,
                          verticalAlign: 'middle',
                          horizontalAlign: "center",
                          mode: "Transparent",
                          rotation: "None",
                        }
                      };
                    }
                    onPropertyChange(payload);
                  }}
                  allowClear={ele.allowClear}
                  dropdownStyle={{
                    fontSize: "10px",
                  }}
                />
              </div>
            );
          })}
        </div>
      </div>
      {inputTypes.map((ele) => {
        if (ele?.expressionEditor) {
          return (
            <div>
              <div className="property-label">
                {getTooltipInfo({ label: ele.label, tooltip: ele.tooltip })}
              </div>
              <FieldSelector
                onChange={(valueObj = {}) => {
                  const { value } = valueObj || {}
                  onPropertyChange({ key: ele.key, value: value ? value : undefined });
                }}
                options={cascaderOptions}
                value={nodeValues[ele.key]}
              />
            </div>
          )
        }
        return (
          <InputFiled
            label={getTooltipInfo({ label: ele.label, tooltip: ele.tooltip })}
            value={nodeValues[ele.key]}
            onChange={(value) => {
              onPropertyChange({ key: ele.key, value });
            }}
          />
        );
      })}
      {checkOptions.map((ele) => {
        return (
          <div>
            <Checkbox
              style={{ marginLeft: 0 }}
              onChange={(e) => {
                onChange(e.target.checked, ele.key);
              }}
              checked={nodeValues[ele.key]}
            >
              {getTooltipInfo({ label: ele.label, tooltip: ele.tooltip })}
            </Checkbox>
          </div>
        );
      })}
    </>
  );
}
