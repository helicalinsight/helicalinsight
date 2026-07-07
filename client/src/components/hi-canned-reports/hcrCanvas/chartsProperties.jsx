import {
  BoldOutlined,
  ItalicOutlined,
  StrikethroughOutlined,
  UnderlineOutlined,
} from "@ant-design/icons";
import {
  Button,
  Collapse,
  Divider,
  InputNumber,
  Select,
  Space,
  Switch,
} from "antd";
import { cloneDeep, isObject } from "lodash";
import React from "react";
import { useSelector } from "react-redux";
import useHCRCascadeSelector from "../../../hooks/useHCRCascadeSelector";
import { vizListNew } from "../../hi-reports/hi-editing-area/utils/constants";
import {
  hcrChartOrientationOptions,
  hcrChartRenderTypeOptions,
  hcrChartsPositionOptions,
  hcrChartThemeOptions,
  hcrDSQuery,
} from "../hcr-constants";
import FieldSelector from "./fieldSelector";
import { hcrCanvasPaneHelperMethods } from "./hcrCanvasPaneHelperMethods";
import CommonProperties from "./hcrTable/commonProperties";
import NodeAlignment from "./nodeAlignment";
import NodeBorders from "./nodeBorder";
import NodeColor from "./nodeColor";
import NodeColorPicker from "./nodeColorPicker";
import NodePadding from "./nodePadding";
import ElementProperties from "./elementProperties";

const { getHcrPropertyTooltipInfo } = hcrCanvasPaneHelperMethods;

const ChartProperties = ({
  EditorPanels,
  nodeConfig,
  onNodeConfigChange = () => { },
  groupOptions = []
}) => {
  const { InputFiled, InputNumberFiled, SelectField } = EditorPanels;
  const activeTab =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey,
      ),
    ) || {};
  const {
    xField = "",
    yField = "",
    colorField = "",
    // sizeField = "",
    chartType = "",
    labelField = "",
    chartTitle = {},
    chartSubtitle = {},
    chartLegend = {},
    chartItemLabel = {},
    chartColors = {},
    evaluationTime = "",
    renderType = "",
    theme = "",
    evalGroup = ""
  } = nodeConfig;
  const { selectedQueryID } = nodeConfig || {};
  const { dsPaneTypes, selectedQueryId: mainQuery = null } = activeTab || {};

  const designerProperties = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR
        ?.designerProperties || {},
  );
  const { evaluationTime: evalOptions, fontName: fontNames } =
    designerProperties || {};
  const HcrPropertiesConfiguration = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations
        ?.HcrPropertiesConfiguration || {},
  );
  const { chart = {}, tooltipInfo } = HcrPropertiesConfiguration || {};
  const { content: chartProperties = [] } =
    chart?.content?.find((item) => item?.name === "chart") || [];
  const {
    tooltip_mode = {},
    tooltip_query = {},
    tooltip_selectChartType,
    tooltip_xField = {},
    tooltip_yField = {},
    tooltip_colorField = {},
    tooltip_labelField = {},
    tooltip_evalTime = {},
    tooltip_renderType = {},
    tooltip_theme = {},
    tooltip_title = {},
    tooltip_subTitle = {},
    tooltip_foreColor = {},
    tooltip_backColor = {},
    tooltip_textColor = {},
    tooltip_fontSize = {},
    tooltip_fontFamily = {},
    tooltip_fontStyles = {},
    tooltip_fontStylesBold = {},
    tooltip_fontStylesItalics = {},
    tooltip_fontStylesStrikeThrough = {},
    tooltip_fontStylesUnderlined = {},
    tooltip_textBackColor = {},
    tooltip_seriesColors = {},
    tooltip_legendPosition = {},
    tooltip_legendShowLegend = {},
    tooltip_colorOrientation = {},
    tooltip_colorBackgoundAlpha = {},
    tooltip_foreGroundBackgoundAlpha = {},
    tooltip_labelShowLabel = {},
    tooltip_componentColor = {},
    tooltip_chartColor = {},
    tooltip_keyField = {},
    tooltip_evalGroupName = {}
  } = tooltipInfo || {};

  const queriesMenu =
    dsPaneTypes
      ?.find((ele) => ele.dataSourcePane === hcrDSQuery)
      ?.menu?.filter(
        (ele) =>
          ele.executeQueryData?.data.length ||
          ele.executeQueryData?.field.length,
      ) || [];

  const selectedQuery = queriesMenu?.find((ele) => ele.id === selectedQueryID);
  let fields = [];
  if (selectedQuery) {
    const { executeQueryData } = selectedQuery || {};
    const { field } = executeQueryData || {};
    fields = field || [];
  }

  const { getCascaderOptions } = useHCRCascadeSelector({ fields });
  const cascaderOptions = getCascaderOptions();

  const isArc = chartType === "arc";

  let selectOptions = [
    {
      key: "xField",
      label: "Category",
      placeholder: "Select Category",
      options: cascaderOptions || [],
      value: xField,
      tooltip: tooltip_xField,
      cascade: true,
    },
    {
      key: "yField",
      label: "Value",
      placeholder: "Select Value",
      options: cascaderOptions || [],
      value: yField,
      tooltip: tooltip_yField,
      cascade: true,
    },
    {
      key: "colorField",
      label: isArc ? "Key" : "Series",
      placeholder: "Select Series",
      options: cascaderOptions || [],
      value: colorField,
      tooltip: tooltip_colorField,
      cascade: true,
    },
    // { key: "sizeField", label: "Size Field", placeholder: "Select Size Field", options: cascaderOptions || [], value: sizeField },
    {
      key: "labelField",
      label: "Label",
      placeholder: "Select Label",
      options: cascaderOptions || [],
      value: labelField,
      allowClear: labelField ? true : false,
      tooltip: tooltip_labelField,
      cascade: true,
    },
    {
      key: "evaluationTime",
      label: "Eval Time",
      placeholder: "Select Evaluation Time",
      options:
        evalOptions.map((option) => ({ label: option, value: option })) || [],
      value: evaluationTime,
      tooltip: tooltip_evalTime,
    },
    {
      key: "evalGroup",
      label: "Eval Group",
      placeholder: "Select Evaluation Group",
      options: evaluationTime === "Group" ? groupOptions.map(({ name }) => ({ label: name, value: name }) || []) : [],
      value: evalGroup,
      tooltip: tooltip_evalGroupName,
    },
    {
      key: "renderType",
      label: "Render Type",
      placeholder: "Select Render Type",
      options:
        hcrChartRenderTypeOptions.map((option) => ({
          label: option,
          value: option,
        })) || [],
      value: renderType,
      tooltip: tooltip_renderType,
    },
    // { key: "theme", label: "Theme", placeholder: "Select Theme", options: hcrChartThemeOptions.map((option) => ({ label: option, value: option })) || [], value: theme, tooltip: tooltip_theme },
  ];
  if (isArc) {
    selectOptions = selectOptions.filter(
      (option) => !["xField", "yField"].includes(option.key),
    );
    selectOptions = [
      {
        key: "xField",
        label: "Value",
        placeholder: "Select Key Field",
        options: cascaderOptions || [],
        value: xField,
        tooltip: tooltip_keyField,
        cascade: true,
      },
      ...selectOptions,
    ];
  }

  const chartTypes =
    vizListNew.find((viz) => viz.type === "Antcharts")?.children || [];
  const chartOptions = chartTypes
    .filter((chart) => ["bar", "arc"].includes(chart.title))
    .map((chart) => {
      return {
        label: chart.title,
        value: chart.title,
        icon: chart.icon,
      };
    });

  let nodeValues = {
    ...nodeConfig,
    fill: nodeConfig.fill || "#fefefe",
    fontFill: nodeConfig.fontFill || "#000000",
    mode: nodeConfig.mode || "Transparent",
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
    properties: nodeConfig.properties || [],
  };

  const onPropertyChange = ({ key, value }, otherProperties = {}) => {
    onNodeConfigChange(key, value, null, otherProperties);
  };

  const onCheckboxChange = (isChecked, key) => {
    onPropertyChange({ key, value: isChecked });
  };

  const handleChangeProperties = (key, pKey, pValue) => {
    let property = nodeValues[key];
    if (property && isObject(property)) {
      onPropertyChange({ key, value: { ...property, [pKey]: pValue } });
    }
  };

  const fontProperties = (values = {}, onChange, hasFontName) => {
    return (
      <div>
        <Divider className="group-divider" />
        <div className="property-group">Fonts</div>
        <div className="common-group">
          {hasFontName && (
            <SelectField
              label={
                <div className="property-label">
                  {getHcrPropertyTooltipInfo({
                    label: "Family",
                    tooltip: tooltip_fontFamily,
                  })}
                </div>
              }
              value={values?.fontFamily}
              options={fontNames.map((fontName) => {
                return {
                  label: fontName,
                  value: fontName,
                };
              })}
              width={110}
              onChange={(value) => {
                onChange({ key: "fontFamily", value });
              }}
            />
          )}
          <InputNumberFiled
            label={
              <div className="property-label">
                {getHcrPropertyTooltipInfo({
                  label: "Font Size",
                  tooltip: tooltip_fontSize,
                })}
              </div>
            }
            value={values?.fontSize}
            width={110}
            onChange={(value) => {
              onChange({ key: "fontSize", value });
            }}
          />
        </div>
        <div className="property-group-wrapper">
          <div className="property-group">
            {getHcrPropertyTooltipInfo({
              label: "Styles",
              tooltip: tooltip_fontStyles,
            })}
          </div>
          <div className="property-group-wrapper-div">
            {getHcrPropertyTooltipInfo({
              label: (
                <Button
                  onClick={() => {
                    onChange({ key: "bold", value: !values?.bold });
                  }}
                  type={values?.bold ? "primary" : "default"}
                  icon={<BoldOutlined />}
                  size={"small"}
                />
              ),
              tooltip: tooltip_fontStylesBold,
            })}
            {getHcrPropertyTooltipInfo({
              label: (
                <Button
                  onClick={() => {
                    onChange({ key: "italic", value: !values?.italic });
                  }}
                  type={values?.italic ? "primary" : "default"}
                  icon={<ItalicOutlined />}
                  size={"small"}
                />
              ),
              tooltip: tooltip_fontStylesItalics,
            })}
            {getHcrPropertyTooltipInfo({
              label: (
                <Button
                  onClick={() => {
                    onChange({
                      key: "strikeThrough",
                      value: !values?.strikeThrough,
                    });
                  }}
                  type={values?.strikeThrough ? "primary" : "default"}
                  icon={<StrikethroughOutlined />}
                  size={"small"}
                />
              ),
              tooltip: tooltip_fontStylesStrikeThrough,
            })}
            {getHcrPropertyTooltipInfo({
              label: (
                <Button
                  onClick={() => {
                    onChange({ key: "underLine", value: !values?.underLine });
                  }}
                  type={values?.underLine ? "primary" : "default"}
                  icon={<UnderlineOutlined />}
                  size={"small"}
                />
              ),
              tooltip: tooltip_fontStylesUnderlined,
            })}
          </div>
        </div>
      </div>
    );
  };

  const getSeriesColors = (colors) => {
    return (
      <div>
        <span className="property-group">
          {" "}
          {getHcrPropertyTooltipInfo({
            label: "Series Colors",
            tooltip: tooltip_seriesColors,
          })}
        </span>
        <div className="common-group">
          {colors.map((color, index) => {
            const handleChangeColor = ({ key, value }) => {
              let newColors = cloneDeep(chartColors.seriesColors);
              newColors[key] = value;
              handleChangeProperties("chartColors", "seriesColors", newColors);
            };
            return (
              <div key={`${color}-index`}>
                <NodeColorPicker
                  onPropertyChange={handleChangeColor}
                  clrVal={color}
                  keyWord={index}
                  label={null}
                  placement={index % 2 === 0 ? "topLeft" : "topRight"}
                />
              </div>
            );
          })}
        </div>
      </div>
    );
  };

  return (
    <React.Fragment>
      <Collapse
        defaultActiveKey={["chart"]}
        size={"small"}
        className="node-property-collapse"
      >
        <Collapse.Panel
          header={<span className="node-property-title">Chart</span>}
          key={"chart"}
        >
          <div>
            <div className="property-label">
              {getHcrPropertyTooltipInfo({
                label: "Query",
                tooltip: tooltip_query,
              })}
            </div>
            <Select
              // label={getHcrPropertyTooltipInfo({ label: "Query", tooltip: tooltip_query })}
              value={selectedQueryID}
              options={queriesMenu
                .filter((query) => query.id !== mainQuery)
                .map((query) => ({
                  label: query.name,
                  value: query.id,
                }))}
              // width={"100%"}
              style={{ width: "100%" }}
              onChange={(value) =>
                onPropertyChange(
                  { key: "selectedQueryID", value },
                  !value || value !== selectedQueryID
                    ? { xField: "", yField: "", colorField: "", labelField: "" }
                    : {},
                )
              }
              placeholder="Please select query"
              allowClear={selectedQueryID ? true : false}
            />
          </div>

          <div>
            <div className="property-label">
              {getHcrPropertyTooltipInfo({
                label: "Select Chart Type",
                tooltip: tooltip_selectChartType,
              })}
            </div>
            <Select
              value={chartType}
              size={"small"}
              className="canvas-parameter-select"
              onChange={(value) =>
                onPropertyChange({ key: "chartType", value })
              }
              style={{ width: "100%" }}
              showArrow
            >
              {chartOptions.map((option) => (
                <Select.Option key={option.value} value={option.value}>
                  {option.icon}
                  <span> {option.label}</span>
                </Select.Option>
              ))}
            </Select>
          </div>

          {selectOptions.map(
            ({
              key,
              label,
              placeholder,
              options,
              value: val,
              allowClear = false,
              tooltip,
              cascade = false,
            }) => (
              <div key={key}>
                <div className="property-label">
                  {getHcrPropertyTooltipInfo({ label, tooltip })}
                </div>
                {cascade ? (
                  <FieldSelector
                    onChange={(valueObj) => {
                      const { value } = valueObj || {}
                      onPropertyChange({ key, value })
                    }}
                    options={options}
                    value={val}
                  />
                ) : (
                  <Select
                    value={val}
                    size={"small"}
                    className="canvas-parameter-select"
                    onChange={(value) => onPropertyChange({ key, value })}
                    style={{ width: "100%" }}
                    showArrow
                    placeholder={placeholder}
                    allowClear={allowClear}
                  >
                    {options.map((option) => (
                      <Select.Option key={option.value} value={option.value}>
                        <span> {option.label}</span>
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </div>
            ),
          )}

          <Divider className="group-divider" />
          <NodeColor
            nodeValues={nodeValues}
            onPropertyChange={onPropertyChange}
            SelectField={SelectField}
            getTooltipInfo={getHcrPropertyTooltipInfo}
            tooltip_mode={tooltip_mode}
            {...{ tooltip_foreColor, tooltip_backColor }}
            labelTooltip={tooltip_componentColor}
          />

          <CommonProperties
            properties={chartProperties}
            excludeProperties={[
              "styleNameReference",
              "datasetExpression",
              "datasetRun",
            ]}
            {...{
              designerProperties,
              nodeValues,
              onPropertyChange,
              onCheckboxChange,
              tooltipInfo,
              EditorPanels,
            }}
          />
        </Collapse.Panel>

        <Collapse size={"small"} className="node-property-collapse">
          <Collapse.Panel
            header={<span className="node-property-title">Alignment</span>}
            key={"alignment"}
          >
            <NodeAlignment
              onPropertyChange={onPropertyChange}
              nodeValues={nodeValues}
              InputNumberFiled={InputNumberFiled}
            />
          </Collapse.Panel>
          <Collapse.Panel
            header={<span className="node-property-title">Title</span>}
            key={"chartTitle"}
          >
            <InputFiled
              label={getHcrPropertyTooltipInfo({
                label: "Title",
                tooltip: tooltip_title,
              })}
              value={chartTitle.expression}
              onChange={(value) => {
                handleChangeProperties("chartTitle", "expression", value);
              }}
            />
            <NodeColorPicker
              onPropertyChange={({ key, value }) =>
                handleChangeProperties("chartTitle", key, value)
              }
              clrVal={chartTitle?.color}
              keyWord={"color"}
              label={"Color"}
              tooltip={tooltip_textColor}
            />
            {fontProperties(chartTitle, ({ key, value }) =>
              handleChangeProperties("chartTitle", key, value),
            )}
          </Collapse.Panel>
          <Collapse.Panel
            header={<span className="node-property-title">Sub Title</span>}
            key={"chartSubtitle"}
          >
            <InputFiled
              label={getHcrPropertyTooltipInfo({
                label: "Subtitle",
                tooltip: tooltip_subTitle,
              })}
              value={chartSubtitle.expression}
              onChange={(value) => {
                handleChangeProperties("chartSubtitle", "expression", value);
              }}
            />
            <NodeColorPicker
              onPropertyChange={({ key, value }) =>
                handleChangeProperties("chartSubtitle", key, value)
              }
              clrVal={chartSubtitle?.color}
              keyWord={"color"}
              label={"Color"}
              tooltip={tooltip_textColor}
            />
            {fontProperties(chartSubtitle, ({ key, value }) =>
              handleChangeProperties("chartSubtitle", key, value),
            )}
          </Collapse.Panel>
          <Collapse.Panel
            header={<span className="node-property-title">Legend</span>}
            key={"chartLegend"}
          >
            <Space style={{ width: 150, alignItems: "flex-end" }}>
              <div className="parameter-label">
                {" "}
                {getHcrPropertyTooltipInfo({
                  label: "Show Legend",
                  tooltip: tooltip_legendShowLegend,
                })}
              </div>
              <Switch
                checked={chartLegend.showLegend}
                onChange={(value) => {
                  handleChangeProperties("chartLegend", "showLegend", value);
                }}
              />
            </Space>
            <div className="common-group">
              <SelectField
                label={
                  <div className="property-label">
                    {getHcrPropertyTooltipInfo({
                      label: "Position",
                      tooltip: tooltip_legendPosition,
                    })}
                  </div>
                }
                value={chartLegend.position}
                options={hcrChartsPositionOptions.map((position) => ({
                  label: position,
                  value: position,
                }))}
                width={110}
                onChange={(value) =>
                  handleChangeProperties("chartLegend", "position", value)
                }
              />
              <NodeColorPicker
                onPropertyChange={({ key, value }) =>
                  handleChangeProperties("chartLegend", key, value)
                }
                clrVal={chartLegend?.foreColor}
                keyWord={"foreColor"}
                label={"Fore Color"}
                tooltip={tooltip_foreColor}
              />
              <NodeColorPicker
                onPropertyChange={({ key, value }) =>
                  handleChangeProperties("chartLegend", key, value)
                }
                clrVal={chartLegend?.backColor}
                keyWord={"backColor"}
                label={"Back Color"}
                tooltip={tooltip_backColor}
              />
            </div>
            {fontProperties(chartLegend, ({ key, value }) =>
              handleChangeProperties("chartLegend", key, value),
            )}
          </Collapse.Panel>
          <Collapse.Panel
            header={<span className="node-property-title">Label</span>}
            key={"chartItemLabel"}
          >
            <Space style={{ width: 150, alignItems: "flex-end" }}>
              <div className="parameter-label">
                {" "}
                {getHcrPropertyTooltipInfo({
                  label: "Show Label",
                  tooltip: tooltip_labelShowLabel,
                })}
              </div>
              {/*  */}
              <Switch
                checked={chartItemLabel.showLabels}
                onChange={(value) => {
                  handleChangeProperties("chartItemLabel", "showLabels", value);
                }}
              />
            </Space>
            <div className="common-group">
              <NodeColorPicker
                onPropertyChange={({ key, value }) =>
                  handleChangeProperties("chartItemLabel", key, value)
                }
                clrVal={chartItemLabel?.color}
                keyWord={"color"}
                label={"Color"}
                tooltip={tooltip_textColor}
              />
              <NodeColorPicker
                onPropertyChange={({ key, value }) =>
                  handleChangeProperties("chartItemLabel", key, value)
                }
                clrVal={chartItemLabel?.backgroundColor}
                keyWord={"backgroundColor"}
                label={"Background Color"}
                tooltip={tooltip_textBackColor}
              />
            </div>
            {fontProperties(
              chartItemLabel,
              ({ key, value }) =>
                handleChangeProperties("chartItemLabel", key, value),
              true,
            )}
          </Collapse.Panel>
          <Collapse.Panel
            header={
              <span className="node-property-title">
                {getHcrPropertyTooltipInfo({
                  label: "Color",
                  tooltip: tooltip_chartColor,
                })}
              </span>
            }
            key={"chartColors"}
          >
            <div className="common-group">
              <NodeColorPicker
                onPropertyChange={({ key, value }) =>
                  handleChangeProperties("chartColors", key, value)
                }
                clrVal={chartColors?.backColor}
                keyWord={"backColor"}
                label={"Back Color"}
                tooltip={tooltip_textBackColor}
              />
              <SelectField
                label={
                  <div className="property-label">
                    {getHcrPropertyTooltipInfo({
                      label: "Orientation",
                      tooltip: tooltip_colorOrientation,
                    })}
                  </div>
                }
                value={chartColors.orientation}
                options={hcrChartOrientationOptions.map((option) => ({
                  label: option,
                  value: option,
                }))}
                width={110}
                onChange={(value) =>
                  handleChangeProperties("chartColors", "orientation", value)
                }
                placeholder="Select Orientation"
              />
              <div>
                <div className="property-label">
                  {getHcrPropertyTooltipInfo({
                    label: "Background Alpha",
                    tooltip: tooltip_colorBackgoundAlpha,
                  })}
                </div>
                <InputNumber
                  value={chartColors.backgroundAlpha}
                  onChange={(value) => {
                    handleChangeProperties(
                      "chartColors",
                      "backgroundAlpha",
                      value,
                    );
                  }}
                  min={0.1}
                  max={1}
                  step={0.1}
                  width={110}
                  formatter={(value) => {
                    return Number(value) === 1 ? "1" : value;
                  }}
                />
              </div>
              <div>
                <div className="property-label">
                  {getHcrPropertyTooltipInfo({
                    label: "Foreground Alpha",
                    tooltip: tooltip_foreGroundBackgoundAlpha,
                  })}
                </div>
                <InputNumber
                  value={chartColors.foregroundAlpha}
                  onChange={(value) => {
                    handleChangeProperties(
                      "chartColors",
                      "foregroundAlpha",
                      value,
                    );
                  }}
                  min={0.1}
                  max={1}
                  step={0.1}
                  width={110}
                  formatter={(value) => {
                    return Number(value) === 1 ? "1" : value;
                  }}
                />
              </div>
            </div>
            <Divider className="group-divider" />
            {getSeriesColors(chartColors.seriesColors)}
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
            header={<span className="node-property-title">Properties</span>}
            key={"Properties"}
          >
            <ElementProperties
              InputFiled={InputFiled}
              getLabel={getHcrPropertyTooltipInfo}
              component="Chart"
              properties={nodeValues.properties || []}
              componentId={nodeValues.id}
              onPropertyChange={(updatedProperties) => {
                onPropertyChange({ key: "properties", value: updatedProperties });
              }}
            />
          </Collapse.Panel>
        </Collapse>
      </Collapse>
    </React.Fragment>
  );
};

export default ChartProperties;
