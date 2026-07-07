import {
  Button,
  Checkbox,
  Collapse,
  Divider,
  Row,
  Select,
  Space,
  Tooltip,
} from "antd";
import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { hcrActions } from "../../../redux/actions";
import { hcrCrosstabMeasuresAggregateFns, hcrDSQuery } from "../hcr-constants";
import { hcrCanvasPaneHelperMethods } from "./hcrCanvasPaneHelperMethods";
import CommonProperties from "./hcrTable/commonProperties";
import NodeBorders from "./nodeBorder";
import NodePadding from "./nodePadding";
import { InfoCircleOutlined } from "@ant-design/icons";
import { cloneDeep, isEmpty, isEqual } from "lodash";
import { useFieldSelection } from "../../../hooks/useHCRFieldsSelection";
import ElementProperties from "./elementProperties";
const { getHcrPropertyTooltipInfo } = hcrCanvasPaneHelperMethods;

const { getCrosstabConfig } = hcrCanvasPaneHelperMethods;

const CrosstabProperties = ({
  EditorPanels,
  nodeConfig,
  onNodeConfigChange = () => { },
}) => {
  const { InputFiled, InputNumberFiled, SelectField } = EditorPanels;

  const {
    copiedConfig = {},
    selectedColumnFields: prevSelectedColumnFields = [],
    selectedRowFields: prevSelectedRowFields = [],
    selectedMeasures: prevSelectedMeasures = [],
  } = nodeConfig || {};
  let actualConfig = nodeConfig;
  if (!isEmpty(copiedConfig)) actualConfig = copiedConfig;

  const {
    selectedQueryID,
    selectedFields = [],
    selectedColumnFields = [],
    selectedRowFields = [],
    selectedMeasures = [],
    measuresAggregateMap = {},
    x: crossTabX,
    y: crossTabY,
    id: nodeId,
    isAppliedClicked,
    height: cHeight,
    width: cWidth,
    padding = {},
  } = actualConfig || {};
  const activeTab =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey,
      ),
    ) || {};
  const { dsPaneTypes, selectedQueryId: mainQuery = null } = activeTab || {};
  const designerProperties = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR
        ?.designerProperties || {},
  );
  const HcrPropertiesConfiguration = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations
        ?.HcrPropertiesConfiguration || {},
  );
  const { crosstab = {}, tooltipInfo } = HcrPropertiesConfiguration || {};
  const { content: availableCrossTabProperties = [] } =
    crosstab?.content?.find((item) => item?.name === "Crosstab") || {};
  const dispatch = useDispatch();
  const {
    tooltip_query = {},
    tooltip_selectFields = {},
    tooltip_selectColumns,
    tooltip_selectRows,
    tooltip_selectMeasures,
    tooltip_repeatColumnHeaders,
    tooltip_repeatRowHeaders,
    tooltip_repeatColumnBreakOffset,
    tooltip_measureAggregateFunctions,
  } = tooltipInfo || {};

  const onPropertyChange = ({ key, value }, otherProperties = {}) => {
    if (key === "selectedMeasures" && value.length > selectedMeasures.length) {
      otherProperties = {
        ...otherProperties,
        measuresAggregateMap: {
          ...measuresAggregateMap,
          [value[value.length - 1]]: hcrCrosstabMeasuresAggregateFns[0],
        },
      };
    }
    if (isEmpty(copiedConfig)) {
      let data = {
        ...cloneDeep(nodeValues),
        [key]: value,
        isAppliedClicked: false,
        ...(otherProperties || {}),
      };
      onNodeConfigChange("copiedConfig", data);
    } else {
      let data = {
        ...cloneDeep(copiedConfig),
        [key]: value,
        isAppliedClicked: false,
        ...(otherProperties || {}),
      };
      onNodeConfigChange("copiedConfig", data);
    }
  };

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
  useEffect(() => {
    const availableFieldNames = fields.map((field) => field.name);
    const validFields = selectedFields.filter((field) =>
      availableFieldNames.includes(field),
    );
    const validColumnFields = selectedColumnFields.filter((field) =>
      availableFieldNames.includes(field),
    );
    const validRowFields = selectedRowFields.filter((field) =>
      availableFieldNames.includes(field),
    );
    const validMeasures = selectedMeasures.filter((field) =>
      availableFieldNames.includes(field),
    );
    if (
      validFields.length !== selectedFields.length ||
      validColumnFields.length !== selectedColumnFields.length ||
      validRowFields.length !== selectedRowFields.length ||
      validMeasures.length !== selectedMeasures.length
    ) {
      const otherProperties = {};
      if (validColumnFields.length !== selectedColumnFields.length) {
        otherProperties.selectedColumnFields = validColumnFields;
      }
      if (validRowFields.length !== selectedRowFields.length) {
        otherProperties.selectedRowFields = validRowFields;
      }
      if (validMeasures.length !== selectedMeasures.length) {
        otherProperties.selectedMeasures = validMeasures;
      }
      onPropertyChange(
        { key: "selectedFields", value: validFields },
        otherProperties,
      );
    }
  }, [JSON.stringify(fields)]);

  useEffect(() => {
    if (selectedFields.length === 0) {
      const otherProperties = {};
      if (selectedColumnFields.length > 0) {
        otherProperties.selectedColumnFields = [];
      }
      if (selectedRowFields.length > 0) {
        otherProperties.selectedRowFields = [];
      }
      if (selectedMeasures.length > 0) {
        otherProperties.selectedMeasures = [];
      }
      if (Object.keys(otherProperties).length > 0) {
        onPropertyChange({ key: "selectedFields", value: [] }, otherProperties);
      }
    }
  }, [selectedFields.length]);

  // using common function component for fields
  const {
    searchStr,
    setSearchStr,
    columnSearchStr,
    setColumnSearchStr,
    rowSearchStr,
    setRowSearchStr,
    measureSearchStr,
    setMeasureSearchStr,
    handleSelectFieldsChange,
    handleSelectColumnFieldsChange,
    handleSelectRowFieldsChange,
    handleSelectMeasuresChange,
    getAvailableColumnFields,
    getFields,
    dropdownRender,
    handleSelectAllClick,
  } = useFieldSelection({
    selectedFields,
    selectedColumnFields,
    selectedRowFields,
    selectedMeasures,
    onPropertyChange,
    availableFields: fields,
  });

  const isApplyButtonDisabled =
    !(
      selectedColumnFields.length &&
      selectedRowFields.length &&
      selectedMeasures.length
    ) || isAppliedClicked;

  let nodeValues = {
    ...actualConfig,
    fontFamily: actualConfig.fontFamily || "Serif",
    fontFill: actualConfig.fontFill || "#000000",
    fill: actualConfig.fill || "#fefefe",
    borders: {
      Top: actualConfig.borders.Top || {
        stroke: 0,
        style: "SOLID",
        color: "#000000",
      },
      Bottom: actualConfig.borders.Bottom || {
        stroke: 0,
        style: "SOLID",
        color: "#000000",
      },
      Right: actualConfig.borders.Right || {
        stroke: 0,
        style: "SOLID",
        color: "#000000",
      },
      Left: actualConfig.borders.Left || {
        stroke: 0,
        style: "SOLID",
        color: "#000000",
      },
    },
    padding: {
      Top: "Top" in actualConfig.padding ? actualConfig.padding.Top : 1,
      Bottom:
        "Bottom" in actualConfig.padding ? actualConfig.padding.Bottom : 1,
      Right: "Right" in actualConfig.padding ? actualConfig.padding.Right : 1,
      Left: "Left" in actualConfig.padding ? actualConfig.padding.Left : 1,
    },
    properties: actualConfig.properties || [],
  };

  const onCheckboxChange = (isChecked, key) => {
    onPropertyChange({ key, value: isChecked });
  };

  const handleApply = () => {
    const { columnHeaders, rowHeaders, height, width, nodes } =
      getCrosstabConfig({
        columnFields: selectedColumnFields,
        rowFields: selectedRowFields,
        measures: selectedMeasures,
        crossTabX,
        crossTabY,
        nodeId,
        fields,
        measuresAggregateMap,
        padding,
      });
    if (
      !isEqual(selectedColumnFields, prevSelectedColumnFields) ||
      !isEqual(selectedRowFields, prevSelectedRowFields) ||
      !isEqual(selectedMeasures, prevSelectedMeasures)
    ) {
      dispatch(
        hcrActions.hcrAddNodes({
          nodes,
          nodeId,
          propertiesToBeChange: {
            ...cloneDeep(copiedConfig),
            isAppliedClicked: true,
            copiedConfig: {},
            rowHeaders,
            columnHeaders,
            height: cHeight > height ? cHeight : height,
            width: cWidth > width ? cWidth : width,
          },
        }),
      );
    } else {
      dispatch(
        hcrActions.editNode({
          nodeId,
          multiple: true,
          isCrosstab: true,
          propertiesToBeChange: {
            ...cloneDeep(copiedConfig),
            isAppliedClicked: true,
            copiedConfig: {},
            height: cHeight > height ? cHeight : height,
            width: cWidth > width ? cWidth : width,
          },
        }),
      );
    }
  };

  const measuresAggregation = (measures) => {
    return (
      <div className="flex-wrap">
        {measures.map((measure) => {
          return (
            <div>
              <SelectField
                label={<div className="property-label">{measure}</div>}
                value={measuresAggregateMap[measure]}
                options={hcrCrosstabMeasuresAggregateFns.map((aggregate) => ({
                  label: aggregate,
                  value: aggregate,
                }))}
                width={110}
                onChange={(value) =>
                  onPropertyChange({
                    key: "measuresAggregateMap",
                    value: { ...measuresAggregateMap, [measure]: value },
                  })
                }
                placeholder="Please aggreate Fn"
              />
            </div>
          );
        })}
      </div>
    );
  };

  const renderTooltipContent = () => {
    return (
      <div>
        <span>To Generate Crosstab, follow these steps :</span>
        <br />
        <span>
          Select Query {"->"} Select Fields {"->"} Select Columns {"->"} Select
          Rows {"->"} Select Measures {"->"} Click on Apply
        </span>
        <br />
        <b>Note: </b>
        <span>
          Columns, Row and Measure fields are required to enable the Apply
          Button.
        </span>
      </div>
    );
  };

  return (
    <React.Fragment>
      <Collapse
        defaultActiveKey={["crosstab"]}
        size={"small"}
        className="node-property-collapse"
      >
        <Collapse.Panel
          header={
            <span className="node-property-title">
              Cross Tab
              <Tooltip placement="left" title={renderTooltipContent()}>
                <InfoCircleOutlined className="ant-property-info-icon" />
              </Tooltip>
            </span>
          }
          key={"crosstab"}
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
                    ? {
                      selectedFields: [],
                      selectedColumnFields: [],
                      selectedRowFields: [],
                      selectedMeasures: [],
                    }
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
                label: "Select Fields",
                tooltip: tooltip_selectFields,
              })}
            </div>
            <Select
              // value={selectedFields}
              value={selectedFields.filter((field) =>
                fields.some((f) => f.name === field),
              )}
              mode="multiple"
              size={"small"}
              className="canvas-parameter-select"
              onChange={handleSelectFieldsChange}
              style={{ width: "100%" }}
              options={fields.map((field) => ({
                label: field.name,
                value: field.name,
              }))}
              showArrow
              allowClear
              maxTagCount={"responsive"}
              dropdownRender={(menu) =>
                dropdownRender(
                  menu,
                  () =>
                    handleSelectAllClick(
                      "selectedFields",
                      getFields(fields, searchStr),
                      searchStr,
                      selectedFields,
                      false,
                      false,
                      false,
                    ),
                  fields,
                  selectedFields,
                  false,
                  false,
                  false,
                )
              }
              onSearch={(value) => setSearchStr(value)}
              searchValue={searchStr}
              onDropdownVisibleChange={() => setSearchStr("")}
            />
          </div>
          <div>
            <div className="property-label">
              {getHcrPropertyTooltipInfo({
                label: "Select Columns",
                tooltip: tooltip_selectColumns,
              })}
            </div>
            <Select
              value={selectedColumnFields.filter((field) =>
                fields.some((f) => f.name === field),
              )}
              mode="multiple"
              size={"small"}
              className="canvas-parameter-select"
              onChange={handleSelectColumnFieldsChange}
              style={{ width: "100%" }}
              options={getAvailableColumnFields()}
              showArrow
              allowClear
              maxTagCount={"responsive"}
              dropdownRender={(menu) =>
                dropdownRender(
                  menu,
                  () =>
                    handleSelectAllClick(
                      "selectedColumnFields",
                      getFields(selectedFields, columnSearchStr),
                      columnSearchStr,
                      selectedColumnFields,
                      true,
                      false,
                      false,
                    ),
                  selectedFields,
                  selectedColumnFields,
                  true,
                  false,
                  false,
                )
              }
              onSearch={(value) => setColumnSearchStr(value)}
              searchValue={columnSearchStr}
              onDropdownVisibleChange={() => setColumnSearchStr("")}
            />
          </div>
          <div>
            <div className="property-label">
              {getHcrPropertyTooltipInfo({
                label: "Select Rows",
                tooltip: tooltip_selectRows,
              })}
            </div>
            <Select
              // value={selectedRowFields}
              value={selectedRowFields.filter((field) =>
                fields.some((f) => f.name === field),
              )}
              mode="multiple"
              size={"small"}
              className="canvas-parameter-select"
              onChange={handleSelectRowFieldsChange}
              style={{ width: "100%" }}
              options={
                selectedFields
                  ?.filter((field) => !selectedColumnFields.includes(field))
                  ?.map((field) => ({ label: field, value: field })) || []
              }
              showArrow
              allowClear
              maxTagCount={"responsive"}
              dropdownRender={(menu) =>
                dropdownRender(
                  menu,
                  () =>
                    handleSelectAllClick(
                      "selectedRowFields",
                      getFields(
                        selectedFields?.filter(
                          (field) => !selectedColumnFields.includes(field),
                        ),
                        rowSearchStr,
                      ),
                      rowSearchStr,
                      selectedRowFields,
                      false,
                      true,
                      false,
                    ),
                  selectedFields?.filter(
                    (field) => !selectedColumnFields.includes(field),
                  ),
                  selectedRowFields,
                  false,
                  true,
                  false,
                )
              }
              onSearch={(value) => setRowSearchStr(value)}
              searchValue={rowSearchStr}
              onDropdownVisibleChange={() => setRowSearchStr("")}
            />
          </div>
          <div>
            <div className="property-label">
              {getHcrPropertyTooltipInfo({
                label: "Select Measures",
                tooltip: tooltip_selectMeasures,
              })}
            </div>
            <Select
              value={selectedMeasures.filter((field) =>
                fields.some((f) => f.name === field),
              )}
              mode="multiple"
              size={"small"}
              className="canvas-parameter-select"
              onChange={handleSelectMeasuresChange}
              style={{ width: "100%" }}
              options={
                selectedFields?.map((field) => ({
                  label: field,
                  value: field,
                })) || []
              }
              showArrow
              allowClear
              maxTagCount={"responsive"}
              dropdownRender={(menu) =>
                dropdownRender(
                  menu,
                  () =>
                    handleSelectAllClick(
                      "selectedMeasures",
                      getFields(selectedFields, measureSearchStr),
                      measureSearchStr,
                      selectedMeasures,
                      false,
                      false,
                      true,
                    ),
                  selectedFields,
                  selectedMeasures,
                  false,
                  false,
                  true,
                )
              }
              onSearch={(value) => setMeasureSearchStr(value)}
              searchValue={measureSearchStr}
              onDropdownVisibleChange={() => setMeasureSearchStr("")}
            />
          </div>
          {selectedMeasures?.length ? (
            <div>
              <Divider className="group-divider" />
              <div className="property-label-12">
                {getHcrPropertyTooltipInfo({
                  label: "Measure Aggregate Functions",
                  tooltip: tooltip_measureAggregateFunctions,
                })}
              </div>
              {measuresAggregation(selectedMeasures)}
              <Divider className="group-divider" />
            </div>
          ) : null}
          <InputNumberFiled
            label={
              <div className="property-label">
                {getHcrPropertyTooltipInfo({
                  label: "Column Break Offset",
                  tooltip: tooltip_repeatColumnBreakOffset,
                })}
              </div>
            }
            value={nodeValues?.["columnBreakOffset"] || ""}
            onKeyDown={(e) => {
              if (e.key === "-" || e.key === "e") {
                e.preventDefault();
              }
            }}
            min={0}
            onChange={(value) => {
              onPropertyChange({ key: "columnBreakOffset", value });
            }}
          />

          <CommonProperties
            properties={availableCrossTabProperties}
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

          <Checkbox
            style={{ marginLeft: 0 }}
            onChange={(e) => {
              onCheckboxChange(e.target.checked, "repeatColumnHeaders");
            }}
            checked={nodeValues["repeatColumnHeaders"]}
          >
            <div className="property-label">
              {getHcrPropertyTooltipInfo({
                label: "Repeat Column Headers",
                tooltip: tooltip_repeatColumnHeaders,
              })}
            </div>
          </Checkbox>
          <br />
          <Checkbox
            style={{ marginLeft: 0 }}
            onChange={(e) => {
              onCheckboxChange(e.target.checked, "repeatRowHeaders");
            }}
            checked={nodeValues["repeatRowHeaders"]}
          >
            <div className="property-label">
              {getHcrPropertyTooltipInfo({
                label: "Repeat Row Headers",
                tooltip: tooltip_repeatRowHeaders,
              })}
            </div>
          </Checkbox>
          <br />
        </Collapse.Panel>

        {/* crosstab properties */}
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
      </Collapse>
      <Collapse size={"small"} className="node-property-collapse">
        <Collapse.Panel
          header={<span className="node-property-title">Properties</span>}
          key={"Properties"}
        >
          <ElementProperties
            InputFiled={InputFiled}
            getLabel={getHcrPropertyTooltipInfo}
            component="Crosstab"
            properties={nodeValues.properties || []}
            componentId={nodeValues.id}
            onPropertyChange={(updatedProperties) => {
              onPropertyChange({ key: "properties", value: updatedProperties });
            }}
          />
        </Collapse.Panel>
      </Collapse>
      <Divider className="group-divider" />
      <Space align="end">
        <Button
          disabled={isApplyButtonDisabled}
          type="link"
          onClick={handleApply}
        >
          Apply
        </Button>{" "}
      </Space>
    </React.Fragment>
  );
};

export default CrosstabProperties;
