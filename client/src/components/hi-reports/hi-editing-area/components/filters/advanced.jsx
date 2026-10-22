import {
  CaretDownOutlined,
  CaretUpOutlined,
  CloseOutlined,
  EditOutlined,
  InfoCircleOutlined,
  PlusOutlined,
  VerticalAlignMiddleOutlined,
} from "@ant-design/icons";
import { Button, Card, Checkbox, Col, Dropdown, Input, Menu, Row, Tooltip } from "antd";
import _ from "lodash";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  addCustomDisplayDateFormat,
  changeDateFilterFormat,
  removeAggregateForAdvanceFilter,
  toggleFilterIngnorance,
  toggleFilterUnique,
  toggleFilterVisibility,
  updateCustomDisplayDateFormat,
  updateFilterAdvDbFns,
  updateFilterMapping
} from "../../../../../redux/actions/hreport.actions";
import { getColumnbject, getDefaultDisplayDateFormats } from "../../../../../utils/filter-utils";
import HITooltip from "../../../../common/components/hi-tooltip";
import notify from "../../../../hi-notifications/notify";
import CustomColumnChip from "../../../hi-fields-area/custom-column-chip";
import AdvancedFnEditor from "./advanced-fn-editor";
import List from "./list";
import { useDrop } from "react-dnd";

const Advanced = (props) => {
  const { metadata, filters, fields, databaseFunctions, functions } = useSelector((state) => {
    let activeReport = state.hreport.present.reports.find((report) => report.active);
    return activeReport;
  });
  const dispatch = useDispatch();
  const [type, setType] = useState("column");
  const [open, setOpen] = useState(false);
  const [customFormat, setCustomFormat] = useState('')
  const [editingField, setEditingField] = useState(null);
  const Notify = notify(dispatch);
  let { tables } = metadata;
  let columnDbFunctions = databaseFunctions;
  let columnFunctions = functions;
  let { filter, reportId } = props;

  const [, displayDropRef] = useDrop(() => ({
    accept: ["metadata_field"],
    drop: (item) => {
      const { field } = item || {}
      const { column = {} } = field || {}
      handleUpdateColumn({
        value: column.fullyQualifiedColumn,
        columnFunction: displayColumn.defaultFunction,
        columnType: "display"
      })
    }
  }), [filter]);

  const [, valueDropRef] = useDrop(() => ({
    accept: ["metadata_field"],
    drop: (item) => {
      const { field } = item || {}
      const { column = {} } = field || {}
      handleUpdateColumn({
        value: column.fullyQualifiedColumn,
        columnFunction: valueColumn.defaultFunction,
        columnType: "value"
      })
    }
  }), [filter]);


  let { mapping, cascade, hideInViewMode, ignore, customDisplayDateFormats = [], displayAggregateForAdvanceFilters: displayAggregate = [], valueAggregateForAdvanceFilters: valueAaggregate = [] } = filter;
  mapping = mapping || {};
  let { orderBy, unique, valueColumn, displayColumn } = mapping;
  // let displayDateFunc = datePart
  // if (mapping && mapping.DisplayDBFunction && mapping.DisplayDBFunction.functionName) {
  //     let val = mapping.DisplayDBFunction.functionName
  //     val = val ? val : ""
  //     displayDateFunc = getDatePart(val)
  // }

  const toggleCascade = () => {
    let tempFilter = { ...filter };
    let tempCascade = { ...cascade };
    tempCascade.isEnabled = !tempCascade.isEnabled;
    tempFilter.cascade = tempCascade;
    dispatch(updateFilterMapping({ tempFilter, type: "cascade" }));
  };
  const handleAddFilterForCascade = () => {
    let tempFilter = { ...filter };
    let tempCascade = { ...cascade };
    let newCount = tempCascade.filtersCount + 1;
    let newFilters = [...tempCascade.filters];
    newFilters.push({ condition: "AND" });
    tempCascade.filtersCount = newCount;
    tempCascade.filters = newFilters;
    tempFilter.cascade = tempCascade;
    dispatch(updateFilterMapping({ tempFilter, type: "cascade" }));
  };
  let otherFilters = _.filter(filters, function (obj) {
    if (obj.uid === filter.uid) {
      return false;
    }
    // if (["IS_NULL", "IS_NOT_NULL"].indexOf(obj.condition) != -1) {
    //   return false;
    // }
    // if (obj.values.length == 0) {
    //   return false;
    // }
    return true;
  });
  columnDbFunctions = _.flatten(Object.values(columnDbFunctions));
  columnDbFunctions = columnDbFunctions.sort((a, b) => a.value.toUpperCase() > b.value.toUpperCase() ? 1 : -1)
  let noneDbFunc = {
    key: "none",
    description: "",
    value: "None",
    signature: "",
    returns: "",
    parameters: [],
  };
  columnDbFunctions = [noneDbFunc, ...columnDbFunctions];

  columnFunctions = Object.keys(columnFunctions).map((key) => {
    return { key: key, label: columnFunctions[key] };
  });
  columnFunctions = [{ key: "none", label: "None" }, ...columnFunctions];
  let onlyColumns = getColumnbject({ type: "getAllColumns", tables });
  let displayDateFormats = [...getDefaultDisplayDateFormats(), ...customDisplayDateFormats];
  const handleOrderByChange = ({ columnType }) => {
    let tempFilter = { ...filter };
    let tempMapping = { ...tempFilter.mapping };
    let tempOrderBy = { ...tempMapping.orderBy };
    let type = columnType;
    let opp = { value: "display", display: "value" };
    let orderValues = ["asc", "desc", "none"];
    if (type == "value" || type == "display") {
      let currentOrderValue = tempOrderBy[type];
      let index = orderValues.indexOf(currentOrderValue);
      let newOrderValue = index == 2 ? orderValues[0] : orderValues[++index];
      tempOrderBy[type] = newOrderValue;
      tempOrderBy[opp[type]] = "none";
      tempMapping.orderBy = tempOrderBy;
      tempFilter.mapping = tempMapping;
      dispatch(updateFilterMapping({ tempFilter }));
    }
  };
  const handleOrderByChangeWithValue = ({ columnType, value }) => {
    let tempFilter = { ...filter };
    let tempMapping = { ...tempFilter.mapping };
    let tempOrderBy = { ...tempMapping.orderBy };
    let type = columnType;
    let opp = { value: "display", display: "value" };
    tempOrderBy[type] = tempOrderBy[type] !== value ? value : "none";
    tempOrderBy[opp[type]] = "none";
    tempMapping.orderBy = tempOrderBy;
    tempFilter.mapping = tempMapping;
    dispatch(updateFilterMapping({ tempFilter }));
  };

  const handleTypeChange = (type) => {
    setType(type);
  };
  const handleVisibility = () => {
    dispatch(toggleFilterVisibility({ uid: filter.uid, reportId }));
  };
  const handleFilterUnique = () => {
    dispatch(toggleFilterUnique({ uid: filter.uid, reportId }));
  };
  const handleDynamicFilter = () => {
    dispatch(toggleFilterIngnorance({ uid: filter.uid, reportId }));
  };

  const updatedColumnFunctions = columnFunctions.filter((data) => data.label !== "order by"); //Fix 4852

  const handleChangeDisplayDateformat = (format) => {
    dispatch(changeDateFilterFormat({ uid: filter.uid, format, reportId }));
  }

  const handleChangeCustomFormat = (e) => {
    setCustomFormat(e.target.value)
  }

  const addCustomFormat = () => {
    if (!customFormat) return;
    if (displayDateFormats?.some(format => format.key === customFormat)) {
      Notify.warning({
        type: "Frontend",
        message: "Format already exists",
      });
      return;
    };
    dispatch(addCustomDisplayDateFormat({ uid: filter.uid, newFormat: { key: customFormat, label: customFormat, custom: true }, reportId }))
    setCustomFormat('')
  }

  const updateCustomFormat = (key) => {
    dispatch(updateCustomDisplayDateFormat({ uid: filter.uid, key, reportId, remove: true }));
  }

  const getFieldFromReport = (column = "") => {
    return fields.find((field) => field.column === column);
  }

  const resetEditingField = () => {
    setEditingField(null);
  }

  const handleDisplayEditingField = () => {
    const reportField = getFieldFromReport(mapping?.displayColumn?.fullyQualifiedColumn);
    setEditingField({
      ...(mapping?.displayColumn || {}),
      id: reportField?.id,
      functionsDefinition: mapping?.displayDBfunctionsDefinition || "",
      columnType: "display"
    });
  }

  const handleValueEditingField = () => {
    const reportField = getFieldFromReport(mapping?.valueColumn?.fullyQualifiedColumn);
    setEditingField({
      ...(mapping?.valueColumn || {}),
      id: reportField?.id,
      functionsDefinition: mapping?.valueDBfunctionsDefinition || "",
      columnType: "value"
    });
  }


  const handleClose = () => {
    resetEditingField();
  }

  const onChangeDisplayDBFnsDefinition = (value) => {
    dispatch(updateFilterAdvDbFns({ uid: filter.uid, key: "displayDBfunctionsDefinition", value, reportId }))
    setEditingField(({ ...editingField, functionsDefinition: value }))
  }

  const onChangeValueDBFnsDefinition = (value) => {
    dispatch(updateFilterAdvDbFns({ uid: filter.uid, key: "valueDBfunctionsDefinition", value, reportId }))
    setEditingField(({ ...editingField, functionsDefinition: value }))
  }


  const getDisplayFieldForDbFn = () => {
    const { fullyQualifiedColumn } = mapping?.displayColumn || {}
    return fields.find((field) => field.column === fullyQualifiedColumn) || {};
  }

  const getValueFieldForDbFn = () => {
    const { fullyQualifiedColumn } = mapping?.valueColumn || {}
    return fields.find((field) => field.column === fullyQualifiedColumn) || {};
  }

  const handleUpdateColumn = ({ value, columnFunction, columnType }) => {
    let tempFilter = { ...filter }
    let newMapping = { ...mapping }
    let aggregate = tempFilter.aggregate;

    let columnSelected = getColumnbject({
      fullyQualifiedColumn: value, type: 'getColumnObj', columnFunction, tables
    })

    if (columnType === "display") {
      newMapping.displayColumn = columnSelected
      tempFilter.mapping = newMapping
      tempFilter.values = []
      if (displayAggregate?.length) {
        dispatch(removeAggregateForAdvanceFilter({ uid: filter?.uid, type: "display" }))
      }
    } else {
      if (valueAaggregate?.length) {
        dispatch(removeAggregateForAdvanceFilter({ uid: filter?.uid, type: "value" }))
      }
      newMapping.valueColumn = columnSelected
      if (columnSelected.defaultFunction && columnSelected.defaultFunction.split('.').indexOf('aggregate') != -1) {
        aggregate = [columnSelected.defaultFunction]
      }
      else {
        aggregate = []
      }
      let backendDataType = filter.backendDataType;
      let dataType = filter.dataType;
      if (columnSelected.type) {
        backendDataType = Object.keys(columnSelected.type)[0]
        dataType = Object.values(columnSelected.type)[0]
      }
      tempFilter.mapping = newMapping
      tempFilter.column = filter.column
      tempFilter.backendDataType = backendDataType
      tempFilter.dataType = dataType
      tempFilter.values = []
      tempFilter.aggregate = aggregate
    }
    dispatch(updateFilterMapping({ tempFilter }))
  }

  const handleUpdateAggregate = (columnType, val) => {
    let value = columnType == 'value' ? valueColumn.fullyQualifiedColumn : displayColumn.fullyQualifiedColumn

    let defaultFunction = columnType == 'value' ? valueColumn.defaultFunction : displayColumn.defaultFunction;
    let columnFunction = defaultFunction !== val ? val : "none";
    handleUpdateColumn({ value, columnFunction, columnType })

  }

  const handleFilterChange = ({ columnType, key, value }) => {
    switch (key) {
      case "orderBy":
        if (value) {
          handleOrderByChangeWithValue({ columnType, value });
        } else {
          handleOrderByChange({ columnType });
        }
        break;
      case "groupBy":
      case "aggregate":
        handleUpdateAggregate(columnType, value)
        break;
      case "functions":
        if (columnType === "display") {
          handleDisplayEditingField()
        }
        if (columnType === "value") {
          handleValueEditingField()
        }
        break;
      default:
        break;
    }
  }

  const dateFormatSelector = (
    <Dropdown
      style={{ width: "100%" }}
      overlay={(
        <div>
          <Menu className="advanced-mode-list"
            selectedKeys={filter?.format ? [filter?.format] : []}>
            {displayDateFormats?.map(({ key, label, custom = false, example = null }) => {
              return (
                <Menu.Item
                  key={key}
                  onClick={(e) => {
                    setOpen(false)
                    handleChangeDisplayDateformat(key)
                  }}
                >
                  <Tooltip title={example ? `example: ${example}` : null}>
                    <div className="advance-filters-date-formatter">
                      <span>{label}</span>
                      {custom && <CloseOutlined zIndex={10} onClick={(e) => {
                        e.stopPropagation();
                        updateCustomFormat(key)
                      }
                      } />}
                    </div>
                  </Tooltip>
                </Menu.Item>
              )
            })}
            <Menu.Item key={'custom'}>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <Input
                  style={{ flex: 'auto' }}
                  value={customFormat}
                  onChange={handleChangeCustomFormat}
                />
                <Button
                  type="primary"
                  onClick={addCustomFormat}
                  style={{ marginLeft: 8 }}
                >
                  Add
                </Button>
              </div>
            </Menu.Item>
          </Menu>

        </div>
      )}
      onVisibleChange={(e) => setOpen(e)}
      visible={open}
      placement="bottomLeft" trigger={["click"]}
    >
      <div>
        <Button className="advance-mode-label">
          {filter?.format || 'Select'}
        </Button>

      </div>
    </Dropdown>
  )

  return (
    <div className="filter-advanced-mode" data-testid={"filter-advanced-mode"} >
      <Card
        title={"Advance"}
        className="main-card"
        extra={
          <div>
            <span data-testid={`unique-filter-${reportId}-${filter.uid}`}>
              <HITooltip customClassName='unique-filter' placement='topRight' title="If enabled It will generate unique result set">
                <Checkbox onChange={handleFilterUnique} checked={unique}>
                  Unique
                </Checkbox>
              </HITooltip>
            </span>
            <span data-testid={`hide-filter-${reportId}-${filter.uid}`}>
              <HITooltip customClassName='hide-filter' placement='topRight' title="If enabled, this filter will not be visible in open mode to change. User can change this filter via url method">
                <Checkbox onChange={handleVisibility} checked={hideInViewMode}>
                  Hide
                </Checkbox>
              </HITooltip>
            </span>
            <span data-testid={`ignore-filter-${reportId}-${filter.uid}`} >
              <HITooltip customClassName='ignore-filter' placement='topRight' title="Use this option, if you are managing the filter values by yourself. This can be done using dynamic view.">
                <Checkbox onChange={handleDynamicFilter} checked={ignore}>
                  Ignore
                </Checkbox>
              </HITooltip>
            </span>
          </div>
        }
        style={{
          visibility: editingField ? "hidden" : "visible"
        }}
      >
        <div className="advanced-mode-tabs" >
          <div onClick={() => handleTypeChange("column")}
            className={type === "column" ? "item selected" : "item"}>
            Column
          </div>
          <div onClick={() => handleTypeChange("cascade")}
            className={type === "cascade" ? "item selected" : "item"}>
            Cascade
          </div>
        </div>
        {type === "column" && (
          <>
            <Card
              type="inner"
              title={
                <div style={{ display: "flex", alignItems: "center", gap: 4 }}>
                  <Tooltip title="Drag and drop a field from the Metadata panel to change the display column.">
                    <InfoCircleOutlined style={{ fontSize: 12, marginBottom: 2, cursor:"pointer"}} />
                  </Tooltip>
                  <span>Display</span>
                </div>
              }
              ref={displayDropRef}
            // extra={
            //   <span
            //     onClick={() => handleOrderByChange({ columnType: "display" })}
            //     className="advanced-orderby"
            //   >
            //     {orderBy.display === "none" && <VerticalAlignMiddleOutlined />}
            //     {orderBy.display === "asc" && <CaretUpOutlined />}
            //     {orderBy.display === "desc" && <CaretDownOutlined />}
            //   </span>
            // }
            >
              <Row span={12} style={{ marginBottom: 4 }}>
                <CustomColumnChip
                  filter={filter}
                  alias={mapping.displayColumn.alias}
                  allowRename={false}
                  allowDataTypeChange={false}
                  allowMenu={true}
                  columnType="display"
                  onFilterChange={handleFilterChange}
                />
              </Row>
              {/* <Row span={12} justify={"space-between"} align={"middle"}>
                <Col span={21} >
                  <List
                    list={columnDbFunctions}
                    filter={filter}
                    columnType="display"
                    isDbFunc={true}
                    showOrderBy={true}
                    initialValue={mapping.DisplayDBFunction}
                  />
                </Col>
                <Col span={2}>
                  <Button
                    onClick={handleDisplayEditingField}
                    size="small"
                    type="link"
                    icon={
                      <Tooltip title="Edit in function editor.">
                        <EditOutlined />
                      </Tooltip>
                    }
                  />
                </Col>
              </Row>
              <Row span={12}>
                <List
                  list={updatedColumnFunctions}
                  filter={filter}
                  columnType="display"
                  isAggregateList={true}
                  initialValue={mapping.displayColumn}
                />
              </Row>
              <Row span={12}>
                <List
                  list={onlyColumns}
                  filter={filter}
                  columnType="display"
                  isDbFunc={false}
                  initialValue={mapping.displayColumn}
                />
              </Row> */}
              {["date", "dateTime"].includes(filter?.dataType) ? <Row span={12}>
                {dateFormatSelector}
              </Row> : null}
            </Card>
            <Card
              className="items-list"
              type="inner"
              title={
                <div style={{ display: "flex", alignItems: "center", gap: 4 }}>
                  <Tooltip title="Drag and drop a field from the Metadata panel to change the value column.">
                    <InfoCircleOutlined style={{ fontSize: 12, marginBottom: 2, cursor: "pointer" }} />
                  </Tooltip>
                  <span>Value</span>
                </div>
              }
              ref={valueDropRef}
            // extra={
            //   <span
            //     onClick={() => handleOrderByChange({ columnType: "value" })}
            //     className="advanced-orderby"
            //   >
            //     {orderBy.value === "none" && <VerticalAlignMiddleOutlined />}
            //     {orderBy.value === "asc" && <CaretUpOutlined />}
            //     {orderBy.value === "desc" && <CaretDownOutlined />}
            //   </span>
            // }
            >
              <Row span={12} style={{ marginBottom: 4 }}>
                <CustomColumnChip
                  filter={filter}
                  alias={mapping.valueColumn.alias}
                  allowRename={false}
                  allowDataTypeChange={false}
                  allowMenu={true}
                  columnType="value"
                  onFilterChange={handleFilterChange}
                />
              </Row>
              {/* <Row span={12} justify={"space-between"} align={"middle"} >
                <Col span={21}>
                  <List
                    list={columnDbFunctions}
                    filter={filter}
                    columnType="value"
                    isDbFunc={true}
                    showOrderBy={true}
                    initialValue={mapping.valueDBFunction}
                  />
                </Col>
                <Col span={2}>
                  <Button
                    onClick={handleValueEditingField}
                    size="small"
                    type="link"
                    icon={
                      <Tooltip title="Edit in function editor.">
                        <EditOutlined />
                      </Tooltip>
                    }
                  />
                </Col>
              </Row>
              <Row span={12}>
                <List
                  list={updatedColumnFunctions}
                  filter={filter}
                  columnType="value"
                  isAggregateList={true}
                  initialValue={mapping.valueColumn}
                />
              </Row>
              <Row span={12}>
                <List
                  list={onlyColumns}
                  filter={filter}
                  columnType="value"
                  isDbFunc={false}
                  initialValue={mapping.valueColumn}
                />
              </Row> */}
            </Card>
          </>
        )}
        {type === "cascade" && (
          <>
            <Row>
              <Checkbox onChange={toggleCascade} checked={cascade.isEnabled}>
                Cascade Filters
              </Checkbox>
              {cascade.isEnabled && (
                <span onClick={handleAddFilterForCascade}>
                  <PlusOutlined />
                </span>
              )}
            </Row>
            <Row>
              <Col span="24">
                {cascade.filters.map((fltr, i) => {
                  return (
                    <List
                      key={"cascade" + i}
                      list={otherFilters}
                      filter={filter}
                      columnType="cascade"
                      isDbFunc={false}
                      isCascade={true}
                      index={i}
                      cascadeFilter={fltr}
                      initialValue={mapping.valueColumn}
                    />
                  );
                })}
              </Col>
            </Row>
          </>
        )}
      </Card>
      {editingField?.columnType === "display" &&
        <AdvancedFnEditor
          editingField={editingField}
          field={getDisplayFieldForDbFn()}
          onChange={onChangeDisplayDBFnsDefinition}
          onClose={handleClose}
          filter={filter}
        />
      }
      {editingField?.columnType === "value" &&
        <AdvancedFnEditor
          editingField={editingField}
          field={getValueFieldForDbFn()}
          onChange={onChangeValueDBFnsDefinition}
          onClose={handleClose}
          filter={filter}
        />}
    </div>
  );
};

export default Advanced;
