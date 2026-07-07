import { InfoCircleOutlined } from "@ant-design/icons";
import { Collapse, Tooltip } from "antd";
import { useDispatch, useSelector } from "react-redux";
import CanvasCalculations from "./hcrCanvasCalculations";
import CanvasMargin from "./hcrCanvasMargin";
import CanvasPageProperties from "./hcrCanvasPageProperties";
import CanvasPageSetup from "./hcrCanvasPagesetup";
import CanvasPreviewParameters from "./hcrCanvasPreviewParameters";
import CanvasGroupProperties from "./hcrGroupProperties";
import CanvasPageStyles from "./hcrPageStyles";
import CanvasExportProperties from "./hcrCanvasExportProperties";
import { getLabel } from "../hcrCanvasPaneHelperMethods";

export default function HCRCanvasProperty({ EditorPanels }) {
  const { InputFiled, Position, InputNumberFiled, Size, SelectField } =
    EditorPanels;
  const designerProperties = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR
        ?.designerProperties || {},
  );
  const {
    pageProperties,
    orientation,
    printOrder,
    whenNoDataType,
    variables,
    calculations: calculationOptions,
    resetType,
    incrementType,
    parametersInPreview,
  } = designerProperties;
  const { classNames } = variables || {};
  const dispatch = useDispatch();

  const activeTab =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey,
      ),
    ) || {};
  const { canvasProperties } = activeTab;
  const { layout = {}, margin = {} } = canvasProperties || {};
  const { size = {}, orientation: orientationValue = "Portrait" } =
    layout || {};

  let { width = 0, height = 0 } = size || {};

  let displayWidth = width,
    displayHeight = height;
  if (orientationValue === "Landscape") {
    displayWidth = height;
    displayHeight = width;
  }

  const {
    top: marginTop = 20,
    bottom: marginBottom = 20,
    left: marginLeft = 20,
    right: marginRight = 20,
  } = margin;

  const renderTooltipContent = () => {
    const editableW = displayWidth - marginLeft - marginRight;
    const editableH = displayHeight - marginTop - marginBottom;

    return (
      <div className="tooltip-container">
        page width : {displayWidth}px, page height : {displayHeight}px, page
        margins top : {marginTop}px, bottom : {marginBottom}px, left :{" "}
        {marginLeft}px, right : {marginRight}px, editable page width :{" "}
        {editableW}px, editable page height : {editableH}px
      </div>
    );
  };

  return (
    <div className="canvas-properties">
      <Collapse size={"small"} className="canvas-property-collapse">
        <Collapse.Panel
          header={
            <span className="canvas-property-title">
              Page Setup
              <Tooltip placement="left" title={renderTooltipContent()}>
                <InfoCircleOutlined />
              </Tooltip>
            </span>
          }
          key={"pageSetup"}
        >
          <CanvasPageSetup
            getLabel={getLabel}
            SelectField={SelectField}
            dispatch={dispatch}
            orientation={orientation}
            pageProperties={pageProperties}
          />
        </Collapse.Panel>
      </Collapse>
      <Collapse size={"small"} className="canvas-property-collapse">
        <Collapse.Panel
          header={<span className="canvas-property-title">Margin</span>}
          key={"margin"}
        >
          <CanvasMargin
            InputNumberFiled={InputNumberFiled}
            dispatch={dispatch}
            getLabel={getLabel}
          />
        </Collapse.Panel>
      </Collapse>
      <Collapse size={"small"} className="canvas-property-collapse">
        <Collapse.Panel
          header={
            <span className="canvas-property-title">Page Properties</span>
          }
          key={"pageProperties"}
        >
          <CanvasPageProperties
            getLabel={getLabel}
            InputNumberFiled={InputNumberFiled}
            SelectField={SelectField}
            dispatch={dispatch}
            printOrder={printOrder}
            whenNoDataType={whenNoDataType}
          />
        </Collapse.Panel>
      </Collapse>
      <Collapse size={"small"} className="canvas-property-collapse">
        <Collapse.Panel
          header={<span className="canvas-property-title">Calculations</span>}
          key={"calculations"}
        >
          <CanvasCalculations
            getLabel={getLabel}
            InputFiled={InputFiled}
            SelectField={SelectField}
            dispatch={dispatch}
            classNames={classNames}
            calculationOptions={calculationOptions}
            resetType={resetType}
            incrementType={incrementType}
          />
        </Collapse.Panel>
      </Collapse>
      <Collapse size={"small"} className="canvas-property-collapse">
        <Collapse.Panel
          header={
            <span className="canvas-property-title">Preview Parameters</span>
          }
          key={"previewParameters"}
        >
          <CanvasPreviewParameters
            SelectField={SelectField}
            dispatch={dispatch}
            parametersInPreview={parametersInPreview}
            getLabel={getLabel}
          />
        </Collapse.Panel>
      </Collapse>
      <Collapse size={"small"} className="canvas-property-collapse">
        <Collapse.Panel
          header={
            <span className="canvas-property-title">Group Properties</span>
          }
          key={"previewParameters"}
        >
          <CanvasGroupProperties
            getLabel={getLabel}
            SelectField={SelectField}
            dispatch={dispatch}
            InputNumberFiled={InputNumberFiled}
          />
        </Collapse.Panel>
      </Collapse>
      <Collapse size={"small"} className="canvas-property-collapse">
        <Collapse.Panel
          header={<span className="canvas-property-title">Page Styles</span>}
          key={"pageStyles"}
        >
          <CanvasPageStyles
            getLabel={getLabel}
            SelectField={SelectField}
            dispatch={dispatch}
            InputNumberFiled={InputNumberFiled}
            InputFiled={InputFiled}
          />
        </Collapse.Panel>
      </Collapse>
      <Collapse size={"small"} className="canvas-property-collapse">
        <Collapse.Panel
          header={
            <span className="canvas-property-title">Report Properties</span>
          }
          key={"exportProperties"}
        >
          <CanvasExportProperties
            getLabel={getLabel}
            SelectField={SelectField}
            dispatch={dispatch}
            InputNumberFiled={InputNumberFiled}
            InputFiled={InputFiled}
          />
        </Collapse.Panel>
      </Collapse>
    </div>
  );
}
