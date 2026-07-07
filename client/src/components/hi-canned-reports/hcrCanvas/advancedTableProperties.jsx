import {
  BorderInnerOutlined,
  BorderOuterOutlined,
  BorderOutlined,
  InfoCircleOutlined,
} from "@ant-design/icons";
import {
  Button,
  Collapse,
  Divider,
  Select,
  Space,
  Switch,
  Tooltip
} from "antd";
import { cloneDeep, isEmpty, isEqual } from "lodash";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { useFieldSelection } from "../../../hooks/useHCRFieldsSelection";
import { hcrActions } from "../../../redux/actions";
import {
  COLUMN_DATA,
  HCR_TABLE_DATA_CELL_HEIGHT,
  HCR_TABLE_DATA_CELL_WIDTH,
  hcrDSQuery,
  hcrTableBandOrder,
  hcrTableBandsTypes
} from "../hcr-constants";
import { getAdvancedTableConfig, getInitialGroupData, getTableDefaultStyles, hcrCanvasPaneHelperMethods } from "./hcrCanvasPaneHelperMethods";
import CommonProperties from "./hcrTable/commonProperties";
import NodeAlignment from "./nodeAlignment";
import NodeColorPicker from "./nodeColorPicker";
import ElementProperties from "./elementProperties";
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
  {
    label: 'Add Group Header',
    key: 'addGroupHeader',
    tooltip: "Add Table Header",
  },
  {
    label: 'Add Group Footer',
    key: 'addGroupFooter',
    tooltip: "Add Group Footer",
  },
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

export default function AdvancedTableProperties({
  EditorPanels,
  nodeConfig,
  onNodeConfigChange = () => { },
}) {
  const { InputNumberFiled, InputFiled } = EditorPanels;

  const {
    copiedConfig = {},
    selectedColumnFields: prevSelectedFields = [],
    selectedGroupFields: prevSelectedGroupFields = [],
    tableConfig: prevConfig = {},
  } = nodeConfig || {};
  let actualConfig = nodeConfig;
  if (!isEmpty(copiedConfig)) actualConfig = copiedConfig;

  const {
    selectedQueryID,
    selectedFields = [],
    selectedColumnFields = [],
    selectedGroupFields = [],
    outlineDSFields = [],
    id: nodeId,
    x,
    y,
    width,
    isAppliedClicked,
    tableConfig = {},
    isTableConstructed
  } = actualConfig || {};
  const activeTab =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey,
      ),
    ) || {};
  const { dsPaneTypes, selectedQueryId: mainQuery = null, subDataSets = [], activeKey: reportKey, tableStyles = [] } = activeTab || {};
  const subDataSet = subDataSets.find((ds) => ds.id === selectedQueryID),
    isSubDataSetPresent = !isEmpty(subDataSet);
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
    tooltip_selectFieldsGroup = {},
    tooltip_addGroupHeader = {},
    tooltip_addGroupFooter = {},
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
  useEffect(() => {
    const availableFieldNames = fields.map((field) => field.name);
    const validFields = selectedFields.filter((field) =>
      availableFieldNames.includes(field),
    );
    const validColumnFields = selectedColumnFields.filter((field) =>
      availableFieldNames.includes(field),
    );
    const validGroupFields = selectedGroupFields.filter((field) =>
      availableFieldNames.includes(field),
    );
    if (
      (validFields.length !== selectedFields.length ||
        validColumnFields.length !== selectedColumnFields.length ||
        validGroupFields.length !== selectedGroupFields.length) &&
      !isTableConstructed
    ) {
      const otherProperties = {};
      if (validColumnFields.length !== selectedColumnFields.length) {
        otherProperties.selectedColumnFields = validColumnFields;
      }
      if (validGroupFields.length !== selectedGroupFields.length) {
        otherProperties.selectedGroupFields = validGroupFields;
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
    properties: actualConfig.properties || [],
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
    handleSelectGroupFields
  } = useFieldSelection({
    selectedFields,
    selectedColumnFields,
    onPropertyChange,
    availableFields: fields,
    selectedGroupFields
  });

  const tableNodeStyles = {
    addColumnFooter: tableConfig?.["addColumnFooter"],
    addColumnHeader: tableConfig?.["addColumnHeader"],
    addTableFooter: tableConfig?.["addTableFooter"],
    addTableHeader: tableConfig?.["addTableHeader"],
    addGroupHeader: tableConfig?.["addGroupHeader"],
    addGroupFooter: tableConfig?.["addGroupFooter"],
    bodyColor: tableConfig?.["bodyColor"] || "#ffffff",
    borderStyle: tableConfig?.["borderStyle"] || "all",
    bordersColor: tableConfig?.["bordersColor"] || "#000000",
    columnHeaderColor: tableConfig?.["columnHeaderColor"] || "#bfe1ff",
    headerColor: tableConfig?.["headerColor"] || "#f0f8ff",
  };

  const tableHeader = tableNodeStyles?.addTableHeader,
    columnHeader = tableNodeStyles?.addColumnHeader,
    isApplyButtonDisabled = isAppliedClicked,
    isSelectionDisabled = isTableConstructed,
    isDsItemSelectionDisabled = isTableConstructed || !isEmpty(subDataSet);

  const onCheckboxChange = (isChecked, key) => {
    onPropertyChange({ key, value: isChecked });
  };

  const handleApply = () => {
    let totalTableWidth = 0;
    const widthConstant = HCR_TABLE_DATA_CELL_WIDTH,
      heightConstant = HCR_TABLE_DATA_CELL_HEIGHT;
    let nodes = selectedColumnFields
      .map((field) => {
        let returnFields = [];
        totalTableWidth += widthConstant;

        let staticTextNodeId,
          staticTextNode = {},
          fieldTextNodeId = `node-${uuidv4()}`;
        let actualField = fields?.find((ele) => ele.name === field) || {};

        if (columnHeader) {
          staticTextNodeId = `node-${uuidv4()}`;
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
            band: hcrTableBandsTypes.COLUMN_HEADER,
            isTableCell: true,
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
          band: hcrTableBandsTypes.COLUMN_DATA,
          isTableCell: true,
        };
        returnFields.push(fieldTextNode);

        return returnFields;
      })
    let tableNewConfig = {}, tableWidth = width, tableHeight = copiedConfig?.height;
    if (!isTableConstructed) {
      const defaultStyles = tableStyles.filter((style) => style.tableId === nodeId);
      tableNewConfig = getAdvancedTableConfig(nodes.length || 1, HCR_TABLE_DATA_CELL_WIDTH, tableConfig, nodes, selectedGroupFields, defaultStyles)
      tableWidth = totalTableWidth > width ? totalTableWidth + nodes.length : width

      if (selectedColumnFields.length > 0) {
        const grpCount = selectedGroupFields.length;
        let factor = hcrTableBandOrder.length - 2;

        if (tableNodeStyles.addGroupHeader) factor += grpCount;
        if (tableNodeStyles.addGroupFooter) factor += grpCount;
        tableHeight = factor * HCR_TABLE_DATA_CELL_HEIGHT;
      }

      if (copiedConfig.expression) {
        const tableDetailStyle = defaultStyles.find((style) => style.isTD);
        dispatch(hcrActions.hcrUpdateTableStyles({
          actionType: "updateAltRowBanding",
          styleId: tableDetailStyle?.id,
          updatedStyles: {
            expression: "new Boolean($V{REPORT_COUNT}.intValue()%2==0)",
            expressionBackColor: copiedConfig?.expressionBackColor || "#BFE1FF",
          },
        }))
      }
    }
    dispatch(
      hcrActions.editNode({
        nodeId,
        multiple: true,
        isTable: true,
        propertiesToBeChange: {
          ...cloneDeep(copiedConfig),
          ...(tableNewConfig || {}),
          isAppliedClicked: true,
          width: tableWidth,
          height: tableHeight,
          copiedConfig: {},
          isTableConstructed: true,
        },
      }),
    );
    if (!isSubDataSetPresent && !isTableConstructed && selectedQueryID) {
      let groups = [];
      if (selectedGroupFields.length) {
        groups = selectedGroupFields.map((fName) => getInitialGroupData(fName));
      }
      dispatch(hcrActions.hcrUpdateSubdataSets({
        actionType: "add",
        reportKey,
        id: selectedQueryID,
        name: selectedQuery.name,
        groups,
        fields,
        selectedFields,
        selectedGroupFields
      }))
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

  const handleQueryChange = (value) => {
    let otherProperties = {};
    const subDataSet = subDataSets.find((ds) => ds.id === value);
    if (subDataSet) {
      otherProperties.selectedFields = subDataSet.selectedFields || [];
      otherProperties.selectedGroupFields = subDataSet.selectedGroupFields || [];
    }
    onPropertyChange({ key: "selectedQueryID", value }, otherProperties);
  }

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
          Once you clicked on "Apply Button", you won't be able to change: Query, Selected Fields, Table Columns Fields, Group Fields and table configuration options will also be disabled.
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
              options={queriesMenu
                .filter((query) => query.id !== mainQuery)
                .map((query) => ({
                  label: query.name,
                  value: query.id,
                }))}
              onChange={(value) => handleQueryChange(value)}
              placeholder="Please select query"
              allowClear={!selectedQueryID ? false : true}
              showArrow
              style={{ width: "100%" }}
              disabled={isSelectionDisabled}
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
              disabled={isDsItemSelectionDisabled}
            />
          </div>
          <div>
            <div className="property-label">
              {getHcrPropertyTooltipInfo({
                label: "Select Table Group Fields",
                tooltip: tooltip_selectFieldsGroup,
              })}
            </div>
            <Select
              value={selectedGroupFields.filter((field) =>
                fields.some((f) => f.name === field),
              )}
              mode="multiple"
              size={"small"}
              className="canvas-parameter-select"
              onChange={handleSelectGroupFields}
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
                      "selectedGroupFields",
                      getFields(selectedFields, searchStr),
                      searchStr,
                      selectedGroupFields,
                      true,
                    ),
                  getFields(selectedFields, searchStr),
                  selectedGroupFields,
                  true,
                )
              }
              onSearch={(value) => setSearchStr(value)}
              searchValue={searchStr}
              onDropdownVisibleChange={() => setSearchStr("")}
              disabled={isDsItemSelectionDisabled}
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
              disabled={isSelectionDisabled}
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
        <Collapse.Panel
          header={
            <span className="node-property-title">Table Configuration</span>
          }
          key={"tableConfiguration"}
        >
          <div>
            <div className="parameter-label">
              Use alternated detail rows background
            </div>
            <Switch
              checked={nodeValues.expression}
              onChange={(value) => onPropertyChange({ key: "expression", value })}
              disabled={isSelectionDisabled}
            />
          </div>
          <div>
            {nodeValues.expression && (
              <NodeColorPicker
                onPropertyChange={({ value }) => onPropertyChange({ key: "expressionBackColor", value })}
                clrVal={nodeValues.expressionBackColor || "#BFE1FF"}
                keyWord="color"
                label={"Alt. Row Detail"}
                disabled={isSelectionDisabled}
              />
            )}
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
              case "addGroupHeader":
                tooltip = tooltip_addGroupHeader;
                break
              case "addGroupFooter":
                tooltip = tooltip_addGroupFooter;
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
                  disabled={isSelectionDisabled}
                />
              </>
            );
          })}
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
            component="Table"
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
    </>
  );
}
