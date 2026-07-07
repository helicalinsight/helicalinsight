import { useSelector } from "react-redux";
import NodeTypography from "../nodeTypography";
import NodePadding from "../nodePadding";
import NodeBorders from "../nodeBorder";
import { hcrActions } from "../../../../redux/actions";
import { Button, Checkbox, Divider, Space, Switch } from "antd";
import NodeLineStyles from "./hcrCanvasLineStyles";
import NodeColorPicker from "../nodeColorPicker";
import notify from "../../../hi-notifications/notify";

export default function CanvasPageStyles({
  SelectField,
  InputNumberFiled,
  dispatch,
  getLabel,
  InputFiled,
  fromAdvanceComponent = false,
  styleValues = {
    lineStyles: {},
    borders: {},
    padding: {}
  },
  onChange = () => { },
  onSave = () => { },
  saveButtonDisabled,
  addConditionalStyleField
}) {
  const activeTab =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey
      )
    ) || {};
  const { canvasProperties } = activeTab;
  const pageStyles = canvasProperties?.pageStyles;
  let { selectStyles, options, keyValuePairs } = pageStyles;
  if (fromAdvanceComponent) {
    keyValuePairs = styleValues
  }
  const HcrPropertiesConfiguration = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations
        ?.HcrPropertiesConfiguration || {}
  );
  const designerProperties = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR
        ?.designerProperties || {}
  );
  const { rotationType, markUp } = designerProperties;
  const { tooltipInfo } = HcrPropertiesConfiguration;
  const {
    tooltip_StyleName,
    tooltip_rotationType,
    tooltip_markUp,
    tooltip_blankWhenNull,
    tooltip_isDefaultStyle,
    tooltip_pattern,
    tooltip_pageStyleSelectStyle
  } = tooltipInfo;
  const Notify = notify(dispatch);

  const selectTypes = [
    {
      children: rotationType,
      label: "Rotation",
      tooltip: tooltip_rotationType,
      key: "rotation",
    },
    {
      children: markUp,
      label: "Mark Up",
      tooltip: tooltip_markUp,
      key: "markUp",
      placement: "left",
    },
  ];

  const checkOptions = [
    {
      label: "Blank when null",
      tooltip: tooltip_blankWhenNull,
      key: "blankWhenNull",
    },
    {
      label: "Default style",
      tooltip: tooltip_isDefaultStyle,
      key: "defaultStyle",
    },
  ];

  function onPropertyChange({ key, value }) {
    if (fromAdvanceComponent) {
      onChange({ key, value })
      return;
    }
    dispatch(
      hcrActions.setHcrCanvasPageStyles({ key, value, isKeyValuePair: true })
    );
  }

  const onCheckboxChange = (isChecked, key) => {
    if (fromAdvanceComponent) {
      onChange({ key, value: isChecked })
      return;
    }
    dispatch(
      hcrActions.setHcrCanvasPageStyles({
        key,
        value: isChecked,
        isKeyValuePair: true,
      })
    );
  };

  // function onSave() {
  //   if (keyValuePairs.styleName) {
  //     dispatch(hcrActions.setHcrCanvasPageStyles({ saveOption: true }));
  //   }
  // }

  // function onClear() {
  //   dispatch(hcrActions.setHcrCanvasPageStyles({ clearKeyValuePairs: true }));
  // }

  const checkIfStyleNameIsAlreadyPresent = (name) => {
    const { options = [] } = pageStyles || {};
    return options.find((option) => option.styleName === name);
  };

  function onAdd() {
    if (keyValuePairs.styleName) {
      if (!checkIfStyleNameIsAlreadyPresent(keyValuePairs.styleName)) {
        dispatch(hcrActions.setHcrCanvasPageStyles({ type: "add" }));
        Notify.success({
          type: "Frontend",
          message: "Style added successfully.",
        });
      } else {
        Notify.warning({
          type: "Frontend",
          message: "Style name already used, please try other name.",
        });
      }
    }
  }

  function onDelete() {
    if (keyValuePairs.styleName) {
      dispatch(
        hcrActions.setHcrCanvasPageStyles({ type: "delete" })
      );
      Notify.success({
        type: "Frontend",
        message: "Style deleted successfully.",
      });
    }
    dispatch(hcrActions.setUpdateHcrCanvasPageStyles(false));
  }

  function onUpdate() {
    if (keyValuePairs.styleName) {
      dispatch(hcrActions.setHcrCanvasPageStyles({ type: "update" }));
      Notify.success({
        type: "Frontend",
        message: "Style updated successfully.",
      });
    }
    dispatch(hcrActions.setUpdateHcrCanvasPageStyles(false));
  }

  const nodeValues = {
    ...keyValuePairs,
    bold: keyValuePairs.bold,
    blankWhenNull: keyValuePairs.blankWhenNull,
    defaultStyle: keyValuePairs.defaultStyle,
    lineStyles: {
      stroke: keyValuePairs?.lineStyles?.stroke || 1,
      style: keyValuePairs?.lineStyles?.style || "SOLID",
      color: keyValuePairs?.lineStyles?.color || "#000000",
    },
    label: keyValuePairs.label,
    fontFamily: keyValuePairs.fontFamily || "Serif",
    fontSize: keyValuePairs.fontSize,
    italic: keyValuePairs.italic,
    strikeThrough: keyValuePairs.strikeThrough,
    underLine: keyValuePairs.underLine,
    mode: keyValuePairs.mode || "Transparent",
    fontFill: keyValuePairs.fontFill || "#000000",
    fill: keyValuePairs.fill || "#fefefe",
    horizontalAlign: keyValuePairs.horizontalAlign || "center",
    verticalAlign: keyValuePairs.verticalAlign || "middle",
    borders: {
      Top: keyValuePairs?.borders?.Top || {
        stroke: 1,
        style: "SOLID",
        color: "#000000",
      },
      Bottom: keyValuePairs?.borders?.Bottom || {
        stroke: 1,
        style: "SOLID",
        color: "#000000",
      },
      Right: keyValuePairs?.borders?.Right || {
        stroke: 1,
        style: "SOLID",
        color: "#000000",
      },
      Left: keyValuePairs?.borders?.Left || {
        stroke: 1,
        style: "SOLID",
        color: "#000000",
      },
    },
    padding: {
      Top: keyValuePairs?.padding?.Top,
      Bottom: keyValuePairs?.padding?.Bottom,
      Right: keyValuePairs?.padding?.Right,
      Left: keyValuePairs?.padding?.Left,
    },
    position: keyValuePairs.position || "FixRelativeTop",
    stretch: keyValuePairs.stretch || "NoStretch",
    rotation: keyValuePairs.rotation || "None",
    markUp: keyValuePairs.markUp || "none",
    evalTime: keyValuePairs.evalTime || "Now",
    evalGroup: keyValuePairs.evalGroup,
    styleName: keyValuePairs.styleName,
    pattern: keyValuePairs.pattern,
    patternExp: keyValuePairs.patternExp,
    printWithExp: keyValuePairs.printWithExp,
  };

  return (
    <>
      {!fromAdvanceComponent && <SelectField
        label={getLabel({ label: "Select Styles", tooltip: tooltip_pageStyleSelectStyle })}
        value={selectStyles}
        options={options.map((opt) => {
          return {
            label: opt.styleName,
            value: opt.id,
          };
        })}
        width={240}
        onChange={(value) => {
          dispatch(
            hcrActions.setHcrCanvasPageStyles({ key: "selectStyles", value })
          );
          dispatch(hcrActions.setUpdateHcrCanvasPageStyles(true));
        }}
      />}
      <InputFiled
        label={getLabel({ label: "Style Name", tooltip: tooltip_StyleName })}
        value={nodeValues.styleName}
        onChange={(value) => {
          if (fromAdvanceComponent) {
            onChange({ key: "styleName", value })
            return;
          }
          dispatch(
            hcrActions.setHcrCanvasPageStyles({
              isKeyValuePair: true,
              key: "styleName",
              value,
            })
          );
        }}
      />
      <NodeTypography
        getTooltipInfo={getLabel}
        InputNumberFiled={InputNumberFiled}
        SelectField={SelectField}
        nodeValues={nodeValues}
        onPropertyChange={onPropertyChange}
      />
      <Divider className="group-divider" />
      <div className="common-group">
        {selectTypes.map((ele) => {
          return (
            <SelectField
              label={getLabel({
                label: ele.label,
                tooltip: ele.tooltip,
                placement: ele.placement,
              })}
              value={nodeValues[ele.key]}
              options={ele.children.map((child) => {
                return {
                  label: child,
                  value: child,
                };
              })}
              width={110}
              onChange={(value) => {
                if (fromAdvanceComponent) {
                  onChange({ key: ele.key, value })
                  return;
                }
                dispatch(
                  hcrActions.setHcrCanvasPageStyles({
                    key: ele.key,
                    value,
                    isKeyValuePair: true,
                  })
                );
              }}
            />
          );
        })}
      </div>
      {checkOptions.map((ele) => {
        return (
          <div>
            <Checkbox
              style={{ marginLeft: 0 }}
              onChange={(e) => {
                onCheckboxChange(e.target.checked, ele.key);
              }}
              checked={nodeValues[ele.key]}
            >
              {getLabel({ label: ele.label, tooltip: ele.tooltip })}
            </Checkbox>
          </div>
        );
      })}
      <InputFiled
        label={getLabel({ label: "Pattern", tooltip: tooltip_pattern })}
        value={nodeValues.pattern}
        onChange={(value) => {
          if (fromAdvanceComponent) {
            onChange({ key: "pattern", value })
            return;
          }
          dispatch(
            hcrActions.setHcrCanvasPageStyles({
              key: "pattern",
              value,
              isKeyValuePair: true,
            })
          );
        }}
      />
      <Divider className="group-divider" />
      <div className="property-group">Padding</div>
      <NodePadding
        nodeValues={nodeValues}
        onPropertyChange={onPropertyChange}
        InputNumberFiled={InputNumberFiled}
      />
      <Divider className="group-divider" />
      <div className="property-group">Borders</div>
      <NodeBorders
        nodeValues={nodeValues}
        onPropertyChange={onPropertyChange}
        InputNumberFiled={InputNumberFiled}
        SelectField={SelectField}
      />
      <>
        <NodeLineStyles
          InputNumberFiled={InputNumberFiled}
          SelectField={SelectField}
          nodeValues={nodeValues}
          onPropertyChange={onPropertyChange}
        />
        <NodeColorPicker
          onPropertyChange={({ value }) => {
            onPropertyChange({
              key: "lineStyles",
              value: { ...nodeValues.lineStyles, color: value },
            });
          }}
          clrVal={nodeValues.lineStyles.color}
          keyWord="color"
          label={"Color"}
        />
        <Divider className="group-divider" />
        {fromAdvanceComponent && addConditionalStyleField ?
          <>
            <div className="property-group">Conditional Style</div>
            <div>
              <InputFiled
                label={<div className="property-label">Expression</div>}
                value={nodeValues.expression}
                onChange={(value) => {
                  onChange({ key: "expression", value })
                }}
              />
            </div>
            <NodeColorPicker
              onPropertyChange={({ value }) => {
                onChange({ key: "expressionBackColor", value })
              }}
              clrVal={nodeValues.expressionBackColor || "#BFE1FF"}
              keyWord="color"
              label={"Alt. Row Detail"}
            />
          </>
          : null}
      </>
      {fromAdvanceComponent && (
        <Space>
          <Button
            disabled={saveButtonDisabled}
            type="link"
            onClick={onSave}
          >
            Save
          </Button>
        </Space>
      )}
      {!fromAdvanceComponent && <Space>
        <Button disabled={!selectStyles} type="link" onClick={onDelete}>
          Delete
        </Button>{" "}
        {/* del */}
        <Button disabled={!selectStyles} type="link" onClick={onUpdate}>
          Update
        </Button>{" "}
        {/* clr */}
        <Button
          disabled={nodeValues.id || !nodeValues.styleName}
          type="link"
          onClick={onAdd}
        >
          Add
        </Button>{" "}
        {/* sav */}
      </Space>}
    </>
  );
}
