import {
  BorderInnerOutlined,
  BorderOuterOutlined,
  BorderOutlined,
  InfoCircleOutlined,
} from "@ant-design/icons";
import {
  Button,
  Checkbox,
  Collapse,
  Divider,
  Row,
  Select,
  Space,
  Switch,
  Tooltip,
} from "antd";
import { useDispatch, useSelector } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { hcrActions } from "../../../redux/actions";
import {
  COLUMN_DATA,
  COLUMN_HEADER,
  hcrDSQuery,
  TABLE_HEADER,
} from "../hcr-constants";
import NodeBorders from "./nodeBorder";
import NodeColorPicker from "./nodeColorPicker";
import NodePadding from "./nodePadding";
import CommonProperties from "./hcrTable/commonProperties";
import NodeAlignment from "./nodeAlignment";
import { cloneDeep, isEmpty, isEqual, set } from "lodash";
import { useState, useEffect } from "react";
import { hcrCanvasPaneHelperMethods } from "./hcrCanvasPaneHelperMethods";
import { useFieldSelection } from "../../../hooks/useHCRFieldsSelection";
const { getHcrPropertyTooltipInfo } = hcrCanvasPaneHelperMethods;

const tableCellColors = [
  {
    label: "Table Header Color",
    key: "headerColor",
    tooltip: "Table Header Color",
    placement: "topLeft",
  },
  {
    label: "Column Header Color",
    key: "columnHeaderColor",
    tooltip: "Column Header Color",
    placement: "bottom",
  },
  {
    label: "Table Body Color",
    key: "bodyColor",
    tooltip: "Table Body Color",
    placement: "topLeft",
  },
];

const tableBooleanOptions = [
  {
    label: "Add Table Header",
    key: "addTableHeader",
    tooltip: "Add Table Header",
  },
  {
    label: "Add Column Header",
    key: "addColumnHeader",
    tooltip: "Add Column Header",
  },
  // {
  //     label: 'Add Group Header',
  //     key: 'addGroupHeader',
  //     tooltip: "Add Table Header",
  // },
  // {
  //     label: 'Add Group Footer',
  //     key: 'addGroupFooter',
  //     tooltip: "Add Group Footer",
  // },
  {
    label: "Add Column Footer",
    key: "addColumnFooter",
    tooltip: "Add Column Footer",
  },
  {
    label: "Add Table Footer",
    key: "addTableFooter",
    tooltip: "Add Table Footer",
  },
];

export default function TableProperties({
  EditorPanels,
  nodeConfig,
  onNodeConfigChange = () => { },
}) {
  const { InputNumberFiled, SelectField } = EditorPanels;

  const {
    copiedConfig = {},
    selectedColumnFields: prevSelectedFields = [],
    tableConfig: prevConfig = {},
  } = nodeConfig || {};
  let actualConfig = nodeConfig;
  if (!isEmpty(copiedConfig)) actualConfig = copiedConfig;

  const {
    selectedQueryID,
    selectedFields = [],
    selectedColumnFields = [],
    id: nodeId,
    x,
    y,
    width,
    isAppliedClicked,
    tableConfig = {},
  } = actualConfig || {};
  const activeTab =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey,
      ),
    ) || {};
  const { dsPaneTypes } = activeTab || {};
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
  const { table = {}, tooltipInfo = {} } = HcrPropertiesConfiguration || {};
  const { content: availableTableProperties = [] } =
    table?.content?.find((item) => item?.name === "Table") || {};
  const dispatch = useDispatch();
  const {
    tooltip_query = {},
    tooltip_selectFields = {},
    tooltip_selectFieldsColumns = {},
    tooltip_tableCellColor = {},
    tooltip_tableHeaderColor = {},
    tooltip_coloumnHeaderColor = {},
    tooltip_tableBorderColor = {},
    tooltip_addTableHeader = {},
    tooltip_addTableFooter = {},
    tooltip_addColumnFooter = {},
    tooltip_addColumnHeader = {},
  } = tooltipInfo || {};

  const prevTableConfig = {
    addColumnFooter: prevConfig?.["addColumnFooter"],
    addColumnHeader: prevConfig?.["addColumnHeader"],
    addTableFooter: prevConfig?.["addTableFooter"],
    addTableHeader: prevConfig?.["addTableHeader"],
  };

  const currentTableConfig = {
    addColumnFooter: tableConfig?.["addColumnFooter"],
    addColumnHeader: tableConfig?.["addColumnHeader"],
    addTableFooter: tableConfig?.["addTableFooter"],
    addTableHeader: tableConfig?.["addTableHeader"],
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
    if (
      validFields.length !== selectedFields.length ||
      validColumnFields.length !== selectedColumnFields.length
    ) {
      const otherProperties = {};
      if (validColumnFields.length !== selectedColumnFields.length) {
        otherProperties.selectedColumnFields = validColumnFields;
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
      if (Object.keys(otherProperties).length > 0) {
        onPropertyChange({ key: "selectedFields", value: [] }, otherProperties);
      }
    }
  }, [selectedFields.length]);

  const nodeValues = {
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
  };
  function onPropertyChange({ key, value }, otherProperties = {}) {
    if (isEmpty(copiedConfig)) {
      let data = {
        ...cloneDeep(nodeValues),
        [key]: value,
        isAppliedClicked: false,
        ...otherProperties,
      };
      onNodeConfigChange("copiedConfig", data);
    } else {
      let data = {
        ...cloneDeep(copiedConfig),
        [key]: value,
        isAppliedClicked: false,
        ...otherProperties,
      };
      onNodeConfigChange("copiedConfig", data);
    }
  }
  // common component functions & state
  const {
    searchStr,
    setSearchStr,
    columnSearchStr,
    setColumnSearchStr,
    handleSelectFieldsChange,
    handleSelectColumnFieldsChange,
    getAvailableColumnFields,
    getFields,
    dropdownRender,
    handleSelectAllClick,
  } = useFieldSelection({
    selectedFields,
    selectedColumnFields,
    onPropertyChange,
    availableFields: fields,
  });

  const tableNodeStyles = {
    addColumnFooter: tableConfig?.["addColumnFooter"],
    addColumnHeader: tableConfig?.["addColumnHeader"],
    addTableFooter: tableConfig?.["addTableFooter"],
    addTableHeader: tableConfig?.["addTableHeader"],
    bodyColor: tableConfig?.["bodyColor"] || "#ffffff",
    borderStyle: tableConfig?.["borderStyle"] || "all",
    bordersColor: tableConfig?.["bordersColor"] || "#000000",
    columnHeaderColor: tableConfig?.["columnHeaderColor"] || "#bfe1ff",
    headerColor: tableConfig?.["headerColor"] || "#f0f8ff",
  };

  const tableHeader = tableNodeStyles?.addTableHeader;
  const columnHeader = tableNodeStyles?.addColumnHeader;
  const isApplyButtonDisabled =
    !selectedColumnFields?.length || isAppliedClicked;

  const onCheckboxChange = (isChecked, key) => {
    onPropertyChange({ key, value: isChecked });
  };

  const handleApply = () => {
    if (
      !isEqual(prevSelectedFields, selectedColumnFields) ||
      !isEqual(prevTableConfig, currentTableConfig)
    ) {
      let totalTableWidth = 0;
      const widthConstant = 100;
      const groupChildren = [];
      let nodes = selectedColumnFields
        .map((field, i) => {
          let returnFields = [];
          totalTableWidth += widthConstant;

          let staticTextNodeId,
            staticTextNode = {},
            fieldTextNodeId = `node-${uuidv4()}`;
          let actualField = fields?.find((ele) => ele.name === field) || {};
          let sibling = `node-${uuidv4()}`;
          const addHeader = columnHeader || tableHeader;
          groupChildren.push(fieldTextNodeId);
          let yFactor = 0;

          let pY = y;
          if (addHeader) {
            staticTextNodeId = `node-${uuidv4()}`;
            groupChildren.push(staticTextNodeId);
            const bothHeaders = tableHeader && columnHeader;
            yFactor += 1;
            if (bothHeaders) {
              pY += 25;
            }

            staticTextNode = {
              id: staticTextNodeId,
              label: field,
              borders: {},
              padding: {},
              width: 100,
              height: 25,
              name: "text",
              renderKey: "text",
              parentKey: "elements",
              isLeaf: true,
              repeat: "na",
              category: "text",
              zIndex: 10,
              type: "defaultNodes",
              fontSize: 10,
              group: nodeId,
              x: x + i * widthConstant,
              y: pY,
              parent: nodeId,
              sibling,
              cell: bothHeaders
                ? COLUMN_HEADER
                : tableHeader
                  ? TABLE_HEADER
                  : COLUMN_HEADER,
              columnIndex: i,
              isTableCell: true,
              selectable: false
            };
            returnFields.push(staticTextNode);
          }
          let fieldTextNode = {
            id: fieldTextNodeId,
            name: field,
            width: 100,
            height: 25,
            label: `$F{${field}}`,
            renderKey: "text",
            isLeaf: true,
            zIndex: 10,
            fontSize: 10,
            type: "queryField",
            category: "text",
            repeat: "rd",
            borders: {},
            padding: {},
            backendDataType: actualField?.clazz || "",
            group: nodeId,
            x: x + i * widthConstant,
            y: pY + 25 * yFactor,
            parent: nodeId,
            sibling,
            cell: COLUMN_DATA,
            columnIndex: i,
            isTableCell: true,
            selectable: false
          };
          returnFields.push(fieldTextNode);

          return returnFields;
        })
        .flat(1);
      dispatch(
        hcrActions.hcrAddNodes({
          nodes,
          nodeId,
          maxColumns: selectedColumnFields?.length - 1,
          propertiesToBeChange: {
            ...cloneDeep(copiedConfig),
            isAppliedClicked: true,
            width: totalTableWidth > width ? totalTableWidth : width,
            groupChildren,
            copiedConfig: {},
            isGroup: true,
          },
        }),
      );
    } else {
      dispatch(
        hcrActions.editNode({
          nodeId,
          multiple: true,
          isTable: true,
          propertiesToBeChange: {
            ...cloneDeep(copiedConfig),
            isAppliedClicked: true,
            copiedConfig: {},
          },
        }),
      );
    }
  };

  const updateTableStyles = (key, value) => {
    onPropertyChange({
      key: "tableConfig",
      value: { ...tableConfig, [key]: value },
    });
  };

  const handleColorsChange = ({ key, value }) => {
    updateTableStyles(key, value);
  };

  const renderTooltipContent = () => {
    return (
      <div>
        <span>To Generate Table. follow these steps :</span>
        <br />
        <span>
          Select Query {"->"} Select Fields {"->"} Select Table Column {"->"}{" "}
          Click on Apply
        </span>
        <br />
        <b>Note: </b>
        <span>
          Table Column fields are required to enable the Apply Button.
        </span>
      </div>
    );
  };

  return (
    <>
      <Collapse
        defaultActiveKey={["table"]}
        size={"small"}
        className="node-property-collapse"
      >
        <Collapse.Panel
          header={
            <span className="node-property-title">
              Table
              <Tooltip placement="left" title={renderTooltipContent()}>
                <InfoCircleOutlined className="ant-property-info-icon" />
              </Tooltip>
            </span>
          }
          key={"table"}
        >
          <div>
            <div className="property-label">
              {getHcrPropertyTooltipInfo({
                label: "Query",
                tooltip: tooltip_query,
              })}
            </div>
            <Select
              value={selectedQueryID}
              options={queriesMenu.map((query) => ({
                label: query.name,
                value: query.id,
              }))}
              onChange={(value) =>
                onPropertyChange(
                  { key: "selectedQueryID", value },
                  !value || value !== selectedQueryID
                    ? { selectedFields: [], selectedColumnFields: [] }
                    : {},
                )
              }
              placeholder="Please select query"
              allowClear={!selectedQueryID ? false : true}
              showArrow
              style={{ width: "100%" }}
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
              allowClear={true}
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
                    ),
                  getFields(fields, searchStr),
                  selectedFields,
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
                label: "Select Fields For Table Columns",
                tooltip: tooltip_selectFieldsColumns,
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
              allowClear={true}
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
                    ),
                  getFields(selectedFields, columnSearchStr),
                  selectedColumnFields,
                  true,
                )
              }
              onSearch={(value) => setColumnSearchStr(value)}
              searchValue={columnSearchStr}
              onDropdownVisibleChange={() => setColumnSearchStr("")}
            />
          </div>

          <CommonProperties
            properties={availableTableProperties}
            excludeProperties={[
              "styleNameReference",
              "datasetExpression",
              "datasetRun",
            ]}
            {...{
              designerProperties,
              nodeValues,
              onPropertyChange,
              tooltipInfo,
              onCheckboxChange,
              EditorPanels,
            }}
          />
        </Collapse.Panel>

        {/* table properties */}
        <Collapse size={"small"} className="node-property-collapse">
          <Collapse.Panel
            header={<span className="node-property-title">Alignment</span>}
            key={"alignment"}
          >
            <NodeAlignment
              nodeValues={nodeValues}
              onPropertyChange={onPropertyChange}
              InputNumberFiled={InputNumberFiled}
            />
          </Collapse.Panel>
        </Collapse>
        {/* To removed Padding and Borders section in the table configurationn */}
        {/* <Collapse size={"small"} className="node-property-collapse">
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
            </Collapse> */}
        {/* <Collapse size={"small"} className="node-property-collapse">
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
            </Collapse> */}

        <Collapse.Panel
          header={
            <span className="node-property-title">Table Configuration</span>
          }
          key={"tableConfiguration"}
        >
          <div className="property-group">Cell Colors</div>
          <div className="table-cell-properties">
            {tableCellColors.map(({ key, label, placement }) => {
              let tooltip = {};
              switch (key) {
                case "headerColor":
                  tooltip = tooltip_tableHeaderColor?.[0] || {};
                  break;
                case "columnHeaderColor":
                  tooltip = tooltip_coloumnHeaderColor;
                  break;
                case "bodyColor":
                  tooltip = tooltip_tableCellColor;
                  break;
                default:
                  break;
              }
              return (
                <NodeColorPicker
                  onPropertyChange={handleColorsChange}
                  clrVal={tableNodeStyles[key]}
                  keyWord={key}
                  label={label}
                  placement={placement}
                  tooltip={tooltip}
                />
              );
            })}
          </div>

          <Divider className="group-divider" />

          <div className="property-group">Cell Borders</div>
          <NodeColorPicker
            onPropertyChange={handleColorsChange}
            clrVal={tableNodeStyles.bordersColor}
            keyWord={"bordersColor"}
            label={"Borders Color"}
            placement={"topLeft"}
            tooltip={tooltip_tableBorderColor}
          />
          <div className="property-label">Borders Style</div>
          <div className="table-cell-properties">
            <Button
              type={
                tableNodeStyles?.borderStyle === "all" ? "primary" : "default"
              }
              onClick={() => updateTableStyles("borderStyle", "all")}
              icon={
                <Tooltip title="All Borders">
                  <BorderOutlined />
                </Tooltip>
              }
              size={"small"}
            />
            <Button
              type={
                tableNodeStyles?.borderStyle === "inside"
                  ? "primary"
                  : "default"
              }
              onClick={() => updateTableStyles("borderStyle", "inside")}
              icon={
                <Tooltip title="Inside Borders Only ">
                  <BorderInnerOutlined />
                </Tooltip>
              }
              size={"small"}
            />
            <Button
              type={
                tableNodeStyles?.borderStyle === "outside"
                  ? "primary"
                  : "default"
              }
              onClick={() => updateTableStyles("borderStyle", "outside")}
              icon={
                <Tooltip title="Outside Borders Only">
                  <BorderOuterOutlined />
                </Tooltip>
              }
              size={"small"}
            />
          </div>

          <Divider className="group-divider" />

          {tableBooleanOptions.map(({ key, label }) => {
            let tooltip = {};
            switch (key) {
              case "addTableHeader":
                tooltip = tooltip_addTableHeader;
                break;
              case "addColumnHeader":
                tooltip = tooltip_addColumnHeader;
                break;
              case "addColumnFooter":
                tooltip = tooltip_addColumnFooter;
                break;
              case "addTableFooter":
                tooltip = tooltip_addTableFooter;
                break;
              default:
                break;
            }
            return (
              <>
                <div className="parameter-label">
                  {getHcrPropertyTooltipInfo({ label, tooltip })}
                </div>
                <Switch
                  checked={tableNodeStyles[key]}
                  onChange={(value) => {
                    updateTableStyles(key, value);
                  }}
                />
              </>
            );
          })}
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
    </>
  );
}
