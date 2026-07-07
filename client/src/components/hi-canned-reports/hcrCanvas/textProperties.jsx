import {
  Collapse,
  Divider,
  Input,
  Tooltip
} from "antd";
import { useSelector } from "react-redux";
import useHCRCascadeSelector from "../../../hooks/useHCRCascadeSelector";
import { HCR_CROSSTAB_COLUMN_TOTAL_HEADER, HCR_CROSSTAB_ROW_TOTAL_HEADER } from "../hcr-constants";
import FieldSelector from "./fieldSelector";
import NodeAlignment from "./nodeAlignment";
import NodeBorders from "./nodeBorder";
import NodePadding from "./nodePadding";
import NodeTextField from "./nodeTextField";
import NodeTypography from "./nodeTypography";
import ElementProperties from "./elementProperties";

const { TextArea } = Input;

export default function TextProperties({
  EditorPanels,
  onNodeConfigChange,
  nodeConfig,
  groupOptions = []
}) {
  const { InputFiled, Position, InputNumberFiled, Size, SelectField } =
    EditorPanels;
  const designerProperties = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR
        ?.designerProperties || {}
  );
  const { positionType, stretchType, rotationType, markUp, evaluationTime } =
    designerProperties;
  const HcrPropertiesConfiguration = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations
        ?.HcrPropertiesConfiguration || {}
  );
  const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
  const { tooltip_content } = tooltipInfo || {};

  const nodeValues = {
    ...nodeConfig,
    label: nodeConfig.label,
    fontFamily: nodeConfig.fontFamily || "Serif",
    fontSize: nodeConfig.fontSize,
    italic: nodeConfig.italic,
    strikeThrough: nodeConfig.strikeThrough,
    underLine: nodeConfig.underLine,
    mode: nodeConfig.mode || "Transparent",
    fontFill: nodeConfig.fontFill || "#000000",
    fill: nodeConfig.fill || "#fefefe",
    horizontalAlign: nodeConfig.horizontalAlign || "center",
    verticalAlign: nodeConfig.verticalAlign || "middle",
    borders: {
      Top: nodeConfig.borders.Top || {
        stroke: 0,
        style: "SOLID",
        color: "#000000",
      },
      Bottom: nodeConfig.borders.Bottom || {
        stroke: 0,
        style: "SOLID",
        color: "#000000",
      },
      Right: nodeConfig.borders.Right || {
        stroke: 0,
        style: "SOLID",
        color: "#000000",
      },
      Left: nodeConfig.borders.Left || {
        stroke: 0,
        style: "SOLID",
        color: "#000000",
      },
    },
    padding: {
      Top: "Top" in nodeConfig.padding ? nodeConfig.padding.Top : 1,
      Bottom: "Bottom" in nodeConfig.padding ? nodeConfig.padding.Bottom : 1,
      Right: "Right" in nodeConfig.padding ? nodeConfig.padding.Right : 1,
      Left: "Left" in nodeConfig.padding ? nodeConfig.padding.Left : 1,
    },
    // position: nodeConfig.position || "FixRelativeTop",
    // stretch: nodeConfig.stretch || "NoStretch",
    // rotation: nodeConfig.rotation || "None",
    // markUp: nodeConfig.markUp || "none",
    // evalTime: nodeConfig.evalTime || "Now",
    position: nodeConfig.position,
    stretch: nodeConfig.stretch,
    rotation: nodeConfig.rotation,
    markUp: nodeConfig.markUp,
    evalTime: nodeConfig.evalTime,
    evalGroup: nodeConfig.evalGroup,
    styleName: nodeConfig.styleName || null,
    pattern: nodeConfig.pattern,
    patternExp: nodeConfig.patternExp,
    printWithExp: nodeConfig.printWithExp,
    printRepeatedValues: nodeConfig.printRepeatedValues,
    properties: nodeConfig.properties || [],
  };

  let groupEvalOptions = []
  if (groupOptions.length) {
    groupEvalOptions = groupOptions.map(({ name }) => name)
  }


  function onPropertyChange({ key, value, styles = null, otherKeyValuePairs = {} }) {
    if (['x', 'y', 'width', 'height'].includes(key) && (nodeValues.isCrosstabCell || nodeValues.isTableCell)) return;
    onNodeConfigChange(key, value, styles, otherKeyValuePairs);
  }

  function getTooltipInfo({ label, tooltip }) {
    const tooltipTitle = tooltip?.content?.[0]?.displayContent || "";
    let divTag = document.createElement("div");
    divTag.innerHTML = tooltipTitle;
    return (
      <Tooltip
        title={<div dangerouslySetInnerHTML={{ __html: divTag.innerHTML }} />}
      >
        <div className="property-label">{label}</div>
      </Tooltip>
    );
  }

  const checkItem = (obj = {}, key = "") => {
    return Object.keys(obj).includes(key);
  }

  let textAreaValue = nodeConfig.label
  // 
  if (nodeValues.isCrosstabCell) {
    if (![HCR_CROSSTAB_COLUMN_TOTAL_HEADER, HCR_CROSSTAB_ROW_TOTAL_HEADER].includes(nodeValues.cell)) {
      if (checkItem(nodeValues, "measureLabel")) {
        textAreaValue = nodeValues.measureLabel
      } else {
        textAreaValue = nodeValues.value;
        if (checkItem(nodeValues, "editedValue")) {
          textAreaValue = nodeValues.editedValue;
        } else {
          textAreaValue = `$V{${textAreaValue}}`
        }
      }
    }
    if ([HCR_CROSSTAB_COLUMN_TOTAL_HEADER, HCR_CROSSTAB_ROW_TOTAL_HEADER].includes(nodeValues.cell)) {
      if (checkItem(nodeValues, "editedValue")) {
        textAreaValue = nodeValues.editedValue;
      } else {
        textAreaValue = `${nodeValues.label} ${nodeValues.value}`
      }
    }
  }

  const { getCascaderOptions } = useHCRCascadeSelector({ node: nodeValues })
  const cascaderOptions = getCascaderOptions()

  const handleTextAreaChange = (value, otherKeyValuePairs = {}) => {
    if (nodeValues.isCrosstabCell) {
      if (checkItem(nodeValues, "measureLabel")) {
        onPropertyChange({ key: "measureLabel", value, otherKeyValuePairs });
        return;
      } else {
        onPropertyChange({ key: "editedValue", value, otherKeyValuePairs });
        return;
      }
    }
    onPropertyChange({ key: "label", value, otherKeyValuePairs });
  }

  return (
    <>
      <Collapse
        defaultActiveKey={"Typography"}
        size={"small"}
        className="node-property-collapse"
      >
        <Collapse.Panel
          header={<span className="node-property-title">Typography</span>}
          key={"Typography"}
        >
          <div className="property-group-wrapper">
            <div className="property-label">
              {getTooltipInfo({ label: "Content", tooltip: tooltip_content })}
            </div>
            <FieldSelector
              onChange={(valueObj = {}) => {
                const { value, pattern = null } = valueObj || {}
                handleTextAreaChange(value, pattern ? { pattern } : {})
              }}
              options={cascaderOptions}
              value={textAreaValue}
            />
          </div>
          <Divider className="group-divider" />
          <NodeTypography
            InputNumberFiled={InputNumberFiled}
            SelectField={SelectField}
            nodeValues={nodeValues}
            onPropertyChange={onPropertyChange}
            getTooltipInfo={getTooltipInfo}
          />
        </Collapse.Panel>
      </Collapse>
      <Collapse size={"small"} className="node-property-collapse">
        <Collapse.Panel
          header={<span className="node-property-title">Borders</span>}
          key={"borders"}
        >
          <NodeBorders
            nodeValues={nodeValues}
            onPropertyChange={onPropertyChange}
            InputNumberFiled={InputNumberFiled}
            SelectField={SelectField}
          />
        </Collapse.Panel>
      </Collapse>
      <Collapse size={"small"} className="node-property-collapse">
        <Collapse.Panel
          header={<span className="node-property-title">Padding</span>}
          key={"padding"}
        >
          <NodePadding
            nodeValues={nodeValues}
            onPropertyChange={onPropertyChange}
            InputNumberFiled={InputNumberFiled}
          />
        </Collapse.Panel>
      </Collapse>
      <Collapse size={"small"} className="node-property-collapse">
        <Collapse.Panel
          header={<span className="node-property-title">Alignment</span>}
          key={"alignment"}
        >
          <NodeAlignment
            onPropertyChange={onPropertyChange}
            nodeValues={nodeValues}
            InputNumberFiled={InputNumberFiled}
            Position={Position}
            Size={Size}
          />
        </Collapse.Panel>
      </Collapse>
      <Collapse size={"small"} className="node-property-collapse">
        <Collapse.Panel
          header={<span className="node-property-title">Text Field</span>}
          key={"Text Field"}
        >
          <NodeTextField
            onPropertyChange={onPropertyChange}
            nodeValues={nodeValues}
            SelectField={SelectField}
            positionType={positionType}
            stretchType={stretchType}
            rotationType={rotationType}
            markUp={markUp}
            evaluationTime={evaluationTime}
            InputFiled={InputFiled}
            getTooltipInfo={getTooltipInfo}
            groupEvalOptions={groupEvalOptions}
            cascaderOptions={cascaderOptions}
          />
        </Collapse.Panel>
      </Collapse>
      <Collapse size={"small"} className="node-property-collapse">
        <Collapse.Panel
          header={<span className="node-property-title">Properties</span>}
          key={"Properties"}
        >
          <ElementProperties
            InputFiled={InputFiled}
            getLabel={getTooltipInfo}
            component="Text"
            properties={nodeValues.properties || []}
            componentId={nodeValues.id}
            onPropertyChange={(updatedProperties) => {
              onPropertyChange({ key: "properties", value: updatedProperties });
            }}
          />
        </Collapse.Panel>
      </Collapse>
    </>
  );
}
