import {
  BarsOutlined,
  CaretDownOutlined,
  CaretRightOutlined,
  CloseOutlined,
  DoubleRightOutlined,
  FunnelPlotOutlined,
  SearchOutlined,
} from "@ant-design/icons";
import { Button, Card, Checkbox, Dropdown, Input, Menu, Tag, Typography } from "antd";
import { useEffect, useRef, useState } from "react";
import { useDispatch } from "react-redux";
import ErrorFallback from "../../../../../errorBoundry/ErrorFallback";
import { useDebounce } from "../../../../../hooks";
import {
  changeFilterCondition,
  updateFilterAlias
} from "../../../../../redux/actions/hreport.actions";
import {
  checkIsValidDateFilter,
  getFilterDataType,
  getMinMaxValues,
  inputValueConditions,
  isDateFilter,
  rangeConditions,
  searchConditions
} from "../../../../../utils/filter-utils";
import { getFieldDisplayName } from "../../../../../utils/utilities";
import HITooltip from "../../../../common/components/hi-tooltip";
import { hcrParaDate, hcrParaDateRange, hcrParaInput, hcrParaQueryBasedDropdownList } from "../../../../hi-canned-reports/hcr-constants";
import { handleParaFilterType } from "../../../../hi-canned-reports/hcrHelperMethods";
import notify from "../../../../hi-notifications/notify";
import { dateTypes } from "../../../utils/constants";
import { allConditions } from "../../utils/constants";
import { checkForDisplayDBMonthFunction } from "../../utils/filter-utils";
import Advanced from "./advanced";
import BooleanFilter from "./boolean-filter";
import DateFilter from "./date-filter";
import NumericFilter from "./numeric-filter";
import RangeSelection from "./range-selection";
import TextFilter from "./text-filter";
import OutsideClickHandler from "react-outside-click-handler";
import { isEmpty } from "lodash";
import { getHTMLColorFormat } from "../../utils/property-utils";

const { Text } = Typography;

const nullConditions = ["IS_NULL", "IS_NOT_NULL"];

const Filter = (props) => {
  const dispatch = useDispatch();
  const [editing, setEditing] = useState(false);
  const [searchClickType, setSearchClickType] = useState("");
  const [isMouseHover, setIsMouseHover] = useState(false);
  const renameInput = useRef(null);
  let { filter,
    reportId,
    dashboardFilter,
    parentComp,
    handleQuotesToggle,
    handleCustomCondition,
    loadValues,
    handleSearch,
    removeFilter,
    handleToggle,
    onChange,
    apiRef,
    filters,
    metadata,
    dateFunctions,
    databaseFunctions,
    mode,
    setFilters,
    isFilterComponent,
    onFilterRequestAbort = () => { },
    filterProperties = {}
  } = props;
  let {
    uid,
    active,
    showSearch,
    search,
    customCondition,
    condition,
    values,
    drillDownId,
  } = filter;
  if (dashboardFilter) {
    // 7142
    let tempValues = props.allFilters[dashboardFilter.columnName] || [];
    if (!Array.isArray(tempValues) && typeof tempValues === "object") {
      values = Object.values(tempValues)
    } else {
      if (!Array.isArray(tempValues)) {
        values = [tempValues]
      }
      else {
        values = tempValues;
      }
    }
    // end [7142]

    // let dataType = getFilterDataType(filter) //[7130 , 7132]
    // if (!["text", "boolean", "numeric"].includes(dataType)) {
    //   filter = { ...filter, values: getRelativeDateFromValue(tempValues, filter) }
    //   // filter = { ...filter, values: tempValues }
    // }

    // values = props.allFilters[dashboardFilter.columnName] || [];
  }
  const isHCRFilter = parentComp === "hcr";
  const debouncedSearchTerm = useDebounce(search, 1000);
  let filterName = getFieldDisplayName(filter);
  useEffect(() => {
    if (debouncedSearchTerm !== undefined) {
      searchClickType === "valid" && loadValues({ debouncedSearchTerm, filter })
    }
  }, [debouncedSearchTerm]);
  useEffect(() => {
    if (editing && renameInput.current) {
      renameInput.current.focus();
    }
  }, [editing]);
  const handleOutsideClick = () => {
    if (isFilterComponent && active && !isDateFilter(filter)) {
      handleToggle({ filter })
    }
  }

  const abortFilterValues = () => {
    apiRef.current?.abort()
    if (typeof onFilterRequestAbort === "function") {
      onFilterRequestAbort(filter)
    }
  }
  const handleRename = () => {
    !isReadOnly && setEditing(true);
  };
  const hideAlias = () => {
    setEditing(false);
  };
  const handleAliasing = (event) => {
    switch (event.key) {
      case "Enter":
        handleSetAlias();
        break;
      case "Escape":
        hideAlias();
        break;
    }
  };
  const handleSetAlias = () => {
    let { value } = renameInput.current.input;
    if (value === filterName) {
      setEditing(false);
      return null;
    }
    if (!value) {
      setEditing(false);
      return null;
    }
    setEditing(false);
    if (isHCRFilter) {

    } else {
      dispatch(updateFilterAlias({ uid: filter.uid, alias: value, reportId }));
    }
  };
  const handleSelect = (condition) => {
    checkForDisplayDBMonthFunction(condition, filter, notify, dispatch)
    if (isHCRFilter) {
      const hcrFilterType = {
        IS_ONE_OF: hcrParaQueryBasedDropdownList,
        CONTAINS: hcrParaInput,
        EQUALS: hcrParaDate,
        IN_RANGE: hcrParaDateRange
      }
      handleParaFilterType({ canvasValues: filter.orgPara?.canvasValues, dispatch, id: filter.uid, value: hcrFilterType[condition] })
    } else {
      dispatch(changeFilterCondition({ uid, condition }));
    }
  };

  // filter styles, bug id : 8231
  let filterHeaderBGColor = "#337ab7";
  let filterHeaderTitleColor = "#ffffff";
  let filterBGColor = "#ffffff";
  let filterListItemColor = "rgba(55, 55, 55, 1)";
  let filterListItemFontSize = 14
  let filterHeaderFontSize = 12
  if (!isEmpty(filterProperties)) {
    if (filterProperties.filterHeaderBGColor) {
      filterHeaderBGColor = getHTMLColorFormat(filterProperties.filterHeaderBGColor)
    }
    if (filterProperties.filterHeaderTitleColor) {
      filterHeaderTitleColor = getHTMLColorFormat(filterProperties.filterHeaderTitleColor)
    }
    if (filterProperties.filterBGColor) {
      filterBGColor = getHTMLColorFormat(filterProperties.filterBGColor)
    }
    if (filterProperties.filterListItemColor) {
      filterListItemColor = getHTMLColorFormat(filterProperties.filterListItemColor)
    }
    if (filterProperties.filterListItemFontSize) {
      filterListItemFontSize = filterProperties.filterListItemFontSize
    }
    if (filterProperties.filterHeaderFontSize) {
      filterHeaderFontSize = filterProperties.filterHeaderFontSize
    }
  }


  let isReadOnly = dashboardFilter || mode === "open"
  const isEditMode = ["edit", "create"].includes(mode);
  // let title = <div onClick={handleToggle} >{filterName}</div>
  let title = editing ? (
    <Input
      data-testid={`filter-rename-input-${filterName}`}
      ref={renameInput}
      defaultValue={filterName}
      onKeyDown={handleAliasing}
      onBlur={handleSetAlias}
      className="filter-alias-input"
      bordered={false}
    />
  ) : (
    <Tag
      color={filterHeaderBGColor}
      className={isEditMode ? "filter-label-edit-mode" : isReadOnly ? "filter-label-open-mode" : "filter-label"}
      icon={
        editing ? null : (
          <span
            onClick={
              (e) => {
                e.stopPropagation()
                handleToggle({ filter })
              }
            }
            data-testid={`${filterName}-filter-toggle`} className={"cursor-pointer"}>
            {!active ? <CaretRightOutlined /> : <CaretDownOutlined />}
          </span>
        )
      }
      onClick={() => {
        if (isReadOnly) {
          handleToggle({ filter })
        }
      }}
    >
      <Text
        className="filter-title"
        ellipsis={true}
        data-testid={`hr-filter-label-${filterName}`}
        onDoubleClick={handleRename}
        style={{
          color: filterHeaderTitleColor,
          fontSize: filterHeaderFontSize
        }}
      >
        {drillDownId && (
          <HITooltip title={"Drill Down Filter"}>
            <span className="filter-drilldown-icon">
              <DoubleRightOutlined rotate={90} />
            </span>
          </HITooltip>
        )}
        <HITooltip title={filterName}>{filterName}</HITooltip>
      </Text>
    </Tag>
  );
  let conditionList = allConditions[getFilterDataType(filter)];
  let sortedConditionsList = Object.keys(conditionList)
    .map((key) => {
      return { key, display: conditionList[key] };
    })
    .sort((a, b) => (a.display > b.display ? 1 : -1));
  const conditionsMenu = (
    <Menu className="filter-conditions-list" selectedKeys={[condition]}>
      {sortedConditionsList.map((item) => {
        return (
          <Menu.Item
            key={item.key}
            onClick={() => handleSelect(item.key)}
            className="hr-filter-condition"
          >
            {item.display}
          </Menu.Item>
        );
      })}
    </Menu>
  );

  let header = (
    <div
      className="filters-drop-left"
      style={{ width: isMouseHover ? "auto" : isReadOnly ? "auto" : "30px" }}
      onMouseLeave={() => setIsMouseHover(false)}
    >
      {!isHCRFilter && searchConditions.includes(condition) && (
        <Button
          data-testid={`filters-search-icon-${reportId}-${uid}`}
          type="link"
          className="filters-extra-btn"
          disabled={getFilterDataType(filter) === "boolean" ? true : false}
          style={getFilterDataType(filter) === "boolean" ? { filter: "opacity(0.2)" } : {}}
          onClick={() => {
            setSearchClickType(debouncedSearchTerm ? "valid" : "inValid")
            handleSearch({ data: { search: "", showSearch: !showSearch }, filter })
            !active && handleToggle({ filter })
          }
          }
        >
          <HITooltip title={"Search value"}   >
            <SearchOutlined />
          </HITooltip>
        </Button>
      )}
      <HITooltip title={"Select all values"} >
        <span
          className="filters-extra-btn filters-all-btn"
          data-testid={`${filterName}-all-btn`}
          style={{ color: values.includes("_all_") ? "#1890ff" : "" }}
          onClick={() => onChange({ value: "_all_", filter })}
        >
          ALL
        </span>
      </HITooltip>
      {(!isReadOnly && !isHCRFilter) && (
        <Dropdown overlay={conditionsMenu} placement="bottomRight" trigger={["click"]}>
          <Button
            type="link"
            className="filters-extra-btn"
            data-testid={`filters-conditions-btn-${reportId}-${filterName}`}
          >
            <HITooltip title={"Select condition"}   >
              <FunnelPlotOutlined />
            </HITooltip>
          </Button>
        </Dropdown>
      )}
      {(!isReadOnly && !isHCRFilter) && (
        <Dropdown
          overlay={<Advanced reportId={reportId} filter={filter} />}
          placement="bottomLeft"
          trigger={["click"]}
        >
          <Button
            type="link"
            className="filters-advance-btn"
            data-testid={`filters-advance-btn-${reportId}-${filterName}`}
            onMouseOver={() => setIsMouseHover(true)}
          >
            <HITooltip title={"Advance mode"}  >
              <BarsOutlined />
            </HITooltip>
          </Button>
        </Dropdown>
      )}
      {(!isReadOnly && !isHCRFilter) && (
        <HITooltip title={"Remove"}  >
          <Button type="link" className="filters-close-btn" onClick={() => removeFilter({ filter })}>
            <CloseOutlined />
          </Button>
        </HITooltip>
      )}
    </div>
  );

  let className = active ? "filter-container filter-open" : "filter-container";
  let dataType = getFilterDataType(filter);
  let showRange = rangeConditions.includes(condition) && !dateTypes.includes(dataType);
  const listProps = {
    reportId, filter, values, loadValues, abortFilterValues, onChange, mode, isFilterComponent, filterBGColor, filterListItemColor, filterListItemFontSize, isHCRFilter
  }
  return (
    <ErrorFallback {...props}>
      <OutsideClickHandler onOutsideClick={handleOutsideClick}>
        <Card size="small" title={title} extra={editing ? null : header} className={className} style={{ background: filterBGColor }}>
          {active && (
            <div>
              {showSearch && searchConditions.includes(condition) && (
                <Input
                  data-testid={`filters-search-input-${reportId}-${uid}`}
                  value={search}
                  onChange={(e) => {
                    setSearchClickType("valid")
                    handleSearch({ data: { search: e.target.value, showSearch: true }, filter })
                  }
                  }
                  placeholder="Search"
                  className="search-value"
                />
              )}
              {inputValueConditions.includes(condition) && (
                <>
                  {condition === "CUSTOM" && (
                    <Input
                      placeholder="condition"
                      value={customCondition ? customCondition : ""}
                      onChange={(e) => handleCustomCondition({ value: e.target.value, filter })}
                    />
                  )}
                  {nullConditions.indexOf(condition) > -1 ? null : (
                    <Input
                      data-testid={`filters-value-input-${reportId}-${uid}`}
                      value={values[0] || ""}
                      disabled={nullConditions.indexOf(condition) > -1 || values.includes("_all_")}
                      placeholder={nullConditions.indexOf(condition) > -1 ? "" : "value"}
                      onChange={(e) => onChange({ value: e.target.value, filter })}
                    />
                  )}
                  {condition === "CUSTOM" && dataType !== "numeric" && (
                    <Checkbox onChange={() => handleQuotesToggle({ filter })} checked={filter.encloseInQuotes}>
                      <Text>Enclose in quotes</Text>
                    </Checkbox>
                  )}
                </>
              )}
              <TextFilter {...listProps} />
              <BooleanFilter {...listProps} />
              <NumericFilter {...listProps} />
              <DateFilter {...listProps}
                databaseFunctions={databaseFunctions}
                dateFunctions={dateFunctions}
                isReadOnly={isReadOnly}
                dashboardFilter={dashboardFilter}
                changeDashboardFilter={props.changeDashboardFilter}
                setFilters={setFilters}
              />
              {showRange && (
                <RangeSelection
                  reportId={reportId}
                  getMinMaxValues={() => getMinMaxValues({ filter, metadata, reportId, filters, databaseFunctions }, dispatch)}
                  filter={filter}
                  onChange={(value) => { onChange({ value, filter }) }}
                  values={values}
                />
              )}
            </div>
          )}
        </Card>
      </OutsideClickHandler>
    </ErrorFallback>
  );
};

export default Filter;
