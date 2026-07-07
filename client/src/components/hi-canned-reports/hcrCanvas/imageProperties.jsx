import {
  Button,
  Collapse,
  Input,
  Space,
  Switch,
  Tooltip
} from "antd";
import { useDispatch, useSelector } from "react-redux";
import cannedReport from "../../../base/requests/cannedReports.request";
import { fileBrowserActions, hcrActions } from "../../../redux/actions";
import { fetchImagesData } from "../hcrHelperMethods";
import HorizontalVerticalAlign from "./horizontalVerticalAlign";
import NodeAlignment from "./nodeAlignment";
import NodeBorders from "./nodeBorder";
import NodeColor from "./nodeColor";
import NodeImage from "./nodeImage";
import NodePadding from "./nodePadding";
import ElementProperties from "./elementProperties";
import useHCRCascadeSelector from "../../../hooks/useHCRCascadeSelector";
import FieldSelector from "./fieldSelector";

const { TextArea } = Input;

export default function ImageProperties({
  EditorPanels,
  onNodeConfigChange,
  nodeConfig,
  groupOptions = [],
}) {
  const { InputFiled, Position, InputNumberFiled, Size, SelectField } =
    EditorPanels;
  const designerProperties = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR
        ?.designerProperties || {}
  );
  const { } = designerProperties;
  const HcrPropertiesConfiguration = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations
        ?.HcrPropertiesConfiguration || {}
  );
  const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};
  const { tooltip_mode, tooltip_foreColor, tooltip_backColor, tooltip_printWhenExpression } = tooltipInfo || {};
  const dispatch = useDispatch();
  const { getResources } = cannedReport(dispatch);
  const imagesList = useSelector(
    (state) => state.cannedReports.present?.imagesList
  );

  const nodeValues = {
    ...nodeConfig,
    label: nodeConfig.label,
    mode: nodeConfig.mode || "Transparent",
    fontFill: nodeConfig.fontFill || "#000000",
    fill: nodeConfig.fill || "#ffffff",
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
    fillType: nodeConfig.fillType || "Solid",
    scaleImage: nodeConfig.scaleImage || "Clip",
    onErrorType: nodeConfig.onErrorType || "Icon",
    styleName: nodeConfig.styleName || null,
    fileName: nodeConfig.imagePath,
    properties: nodeConfig.properties || [],
    customExpressionToggle: nodeConfig.customExpressionToggle || false,
    customExpression: nodeConfig.customExpression || ""
  };

  const { getCascaderOptions } = useHCRCascadeSelector({ node: nodeValues })
  const cascaderOptions = getCascaderOptions()

  function onPropertyChange({ key, value, styles = {} }) {
    onNodeConfigChange(key, value, styles);
  }

  function getLabel({ label, tooltip }) {
    const tooltipTitle = tooltip?.content?.reduce(
      (acc, cur) => acc + cur.displayContent,
      ""
    ) || "";
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

  function binaryToBase64(binaryStr) {
    const binary = new Uint8Array(binaryStr.length);
    for (let i = 0; i < binaryStr.length; i++) {
      binary[i] = binaryStr.charCodeAt(i);
    }

    // Convert to base64 string using built-in APIs
    const blob = new Blob([binary.buffer]);
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => {
        const base64data = reader.result.split(",")[1]; // remove "data:...;base64,"
        resolve(base64data);
      };
      reader.onerror = reject;
      reader.readAsDataURL(blob);
    });
  }

  const onSelectImage = (refreshObj = {}) => {
    const { refresh } = refreshObj;
    if (!imagesList) {
      fetchImagesData(dispatch, refresh || false, getResources);
    } else {
      dispatch(hcrActions.setHcrFilebrowserFor("images"));
      dispatch(fileBrowserActions.setSearchResults(null));
      dispatch(fileBrowserActions.setShowFileBrowser(true));
    }
  };

  return (
    <>
      <Collapse
        defaultActiveKey={"appearence"}
        size={"small"}
        className="node-property-collapse"
      >
        <Collapse.Panel
          header={<span className="node-property-title">Appearence</span>}
          key={"appearence"}
        >

          <Space style={{ width: 200, alignItems: 'flex-end' }}>
            <div className="parameter-label">{getLabel({ label: "Enable Custom Expression", tooltip: { content: [{ displayContent: "Add custom image expression." }] } })}</div>
            <Switch
              checked={nodeValues.customExpressionToggle}
              onChange={(value) => {
                onPropertyChange({ key: "customExpressionToggle", value });
              }} />
          </Space>

          {!nodeValues.customExpressionToggle ?
            <>
              <InputFiled
                style={{ width: 100 }}
                label={<div className="property-label">File Name</div>}
                value={nodeValues.fileName}
                onChange={(value) => {
                  onPropertyChange({ key: "imagePath", value });
                }}
              />
              <Tooltip
                title={
                  <span style={{ fontSize: "10px" }}>Open the file browser </span>
                }
              >
                <Button
                  type="link"
                  style={{
                    fontSize: "10px",
                    padding: 0,
                    height: "auto",
                    lineHeight: 1,
                  }}
                  onClick={onSelectImage}
                >
                  Select the image
                </Button>
              </Tooltip>
            </> :
            <FieldSelector
              onChange={(valueObj = {}) => {
                const { value } = valueObj || {}
                onPropertyChange({ key: "customExpression", value });
              }}
              options={cascaderOptions}
              value={nodeValues.customExpression}
            />
          }
          {/* <InputFiled
            label={getLabel({
              label: "Print When Expression",
              tooltip: tooltip_printWhenExpression,
            })}
            value={nodeValues.printWhenExpression}
            onChange={(value) => {
              onPropertyChange({ key: "printWhenExpression", value });
            }}
          /> */}
          <div>
            {getLabel({ label: "Print When Expression", tooltip: tooltip_printWhenExpression })}
            <FieldSelector
              onChange={(valueObj = {}) => {
                const { value } = valueObj || {}
                onPropertyChange({ key: "printWhenExpression", value: value ? value : undefined });
              }}
              options={cascaderOptions}
              value={nodeValues.printWhenExpression}
            />
          </div>
          <NodeAlignment
            onPropertyChange={onPropertyChange}
            nodeValues={nodeValues}
            InputNumberFiled={InputNumberFiled}
            Position={Position}
          />
          <HorizontalVerticalAlign
            nodeValues={nodeValues}
            onPropertyChange={onPropertyChange}
            showJustify={false}
          />
          <NodeColor
            popoverOverlayClass={"top--11"}
            nodeValues={nodeValues}
            onPropertyChange={onPropertyChange}
            SelectField={SelectField}
            getTooltipInfo={getLabel}
            tooltip_mode={tooltip_mode}
            {...{ tooltip_foreColor, tooltip_backColor }}
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
          header={<span className="node-property-title">Image</span>}
          key={"image"}
        >
          <NodeImage
            SelectField={SelectField}
            getLabel={getLabel}
            onPropertyChange={onPropertyChange}
            nodeValues={nodeValues}
            groupOptions={groupOptions.map(({ name }) => (name) || [])}
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
            getLabel={getLabel}
            component="Image"
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
