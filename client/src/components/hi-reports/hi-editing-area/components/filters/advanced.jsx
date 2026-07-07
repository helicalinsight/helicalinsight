import { useState } from "react";
import { Card, Checkbox, Row, Col, Menu, Tooltip, Dropdown, Button, Input, Divider } from "antd";
import {
  PlusOutlined,
  CaretUpOutlined,
  VerticalAlignMiddleOutlined,
  CaretDownOutlined,
  CloseOutlined,
} from "@ant-design/icons";
import { useSelector } from "react-redux";
import { useDispatch } from "react-redux";
import {
  updateFilterMapping,
  toggleFilterVisibility,
  toggleFilterUnique,
  toggleFilterIngnorance,
  changeDateFilterFormat,
  addCustomDisplayDateFormat,
  updateCustomDisplayDateFormat
} from "../../../../../redux/actions/hreport.actions";
import { getColumnbject, getDefaultDisplayDateFormats } from "../../../../../utils/filter-utils";
import _ from "lodash";
import List from "./list";
import HITooltip from "../../../../common/components/hi-tooltip";
import notify from "../../../../hi-notifications/notify";

const Advanced = (props) => {
  const { metadata, filters, databaseFunctions, functions } = useSelector((state) => {
    let activeReport = state.hreport.present.reports.find((report) => report.active);
    return activeReport;
  });
  const dispatch = useDispatch();
  const [type, setType] = useState("column");
  const [open, setOpen] = useState(false);
  const [customFormat, setCustomFormat] = useState('')
  const Notify = notify(dispatch);
  let { tables } = metadata;
  let columnDbFunctions = databaseFunctions;
  let columnFunctions = functions;
  let { filter, reportId } = props;

  let { mapping, cascade, hideInViewMode, ignore, customDisplayDateFormats = [] } = filter;
  mapping = mapping || {};
  let { orderBy, unique } = mapping;
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
              title="Display"
              extra={
                <span
                  onClick={() => handleOrderByChange({ columnType: "display" })}
                  className="advanced-orderby"
                >
                  {orderBy.display === "none" && <VerticalAlignMiddleOutlined />}
                  {orderBy.display === "asc" && <CaretUpOutlined />}
                  {orderBy.display === "desc" && <CaretDownOutlined />}
                </span>
              }
            >
              <Row span={12} >
                <List
                  list={columnDbFunctions}
                  filter={filter}
                  columnType="display"
                  isDbFunc={true}
                  showOrderBy={true}
                  initialValue={mapping.DisplayDBFunction}
                />
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
              </Row>
              {["date", "dateTime"].includes(filter?.dataType) ? <Row span={12}>
                {dateFormatSelector}
              </Row> : null}
            </Card>
            <Card
              className="items-list"
              type="inner"
              title="Value"
              extra={
                <span
                  onClick={() => handleOrderByChange({ columnType: "value" })}
                  className="advanced-orderby"
                >
                  {orderBy.value === "none" && <VerticalAlignMiddleOutlined />}
                  {orderBy.value === "asc" && <CaretUpOutlined />}
                  {orderBy.value === "desc" && <CaretDownOutlined />}
                </span>
              }
            >
              <Row span={12}>
                <List
                  list={columnDbFunctions}
                  filter={filter}
                  columnType="value"
                  isDbFunc={true}
                  showOrderBy={true}
                  initialValue={mapping.valueDBFunction}
                />
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
              </Row>
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
    </div>
  );
};

export default Advanced;
