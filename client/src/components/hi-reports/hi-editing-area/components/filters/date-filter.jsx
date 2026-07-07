
import { FunctionOutlined } from "@ant-design/icons";
import { Menu } from "antd";
import { useDispatch } from "react-redux";
import { updateAnchorDate, updateDatePart, updateFilter } from "../../../../../redux/actions/hreport.actions";
import {
    addFilterValue,
    anchorConstant,
    equalConditions, funcMap, getDatePart,
    getFilterDataType,
    rangeConditions,
    valuesConditions
} from "../../../../../utils/filter-utils";
import DatePicker from "./date-picker";
import DateRange from "./date-range";
// import DateSettings from "./date-settings"
import React from "react";
import { getFieldDisplayName } from "../../../../../utils/utilities";
import HITooltip from "../../../../common/components/hi-tooltip";
import { getRelativeDateValues } from "../../utils/filter-utils";
import FilterValues from "./filter-values";
import RelativeList from "./relative-list";



const { SubMenu } = Menu

const hideConditions = ["IS_NOT_NULL"];

const DateFilter = props => {
    const dispatch = useDispatch()

    let {
        filter, dateFunctions, loadValues, values, isReadOnly, reportId, dashboardFilter, databaseFunctions,
        abortFilterValues, setFilters, filterBGColor, filterListItemColor, filterListItemFontSize, isHCRFilter
    } = props
    let { condition, datePart, dateValuesType, mapping, otherActiveMenu = "" } = filter
    let dataType = getFilterDataType(filter)
    let isEmptyDbFuncs = !databaseFunctions || !Object.keys(databaseFunctions).length

    let displayDateFunc = datePart, valueDateFunc = datePart

    const handleTypeChange = e => {
        if (dateValuesType === "relative-list") return null
        if (setFilters) {
            setFilters(prevFils => {
                return prevFils.map(fil => {
                    if (fil.uid === filter.uid) {
                        fil.dateValuesType = e;
                        fil.reportId = reportId;
                        fil.otherActiveMenu = e === "relative-list" ? e : ""
                    }
                    return { ...fil };
                })
            })
        } else {
            dispatch(updateFilter({ ...filter, dateValuesType: e, otherActiveMenu: e === "relative-list" ? e : "", reportId }))
        }
    }

    const toggleRelList = () => {
        if (setFilters) {
            setFilters(prevFils => {
                return prevFils.map(fil => {
                    if (fil.uid === filter.uid) {
                        fil.dateValuesType = "datePicker";
                        fil.reportId = reportId;
                        fil.values = [];
                        fil.otherActiveMenu = "";
                    }
                    return { ...fil };
                })
            })
        } else {
            dispatch(updateFilter({ ...filter, dateValuesType: "datePicker", otherActiveMenu: "", values: [], reportId }))
        }
    }

    if (mapping && mapping.DisplayDBFunction && mapping.DisplayDBFunction.functionName) {
        let val = mapping.DisplayDBFunction.functionName
        val = val ? val : ""
        displayDateFunc = getDatePart(val)
    }
    if (mapping && mapping.valueDBFunction && mapping.valueDBFunction.functionName) {
        let val = mapping.valueDBFunction.functionName
        val = val ? val : ""
        valueDateFunc = getDatePart(val)
    }
    let datePartList = dateFunctions[dataType]
    datePartList = datePartList ? datePartList : dateFunctions["dateTime"]
    if (isEmptyDbFuncs) {
        datePartList = []
    }
    let showDatePicker = funcMap.indexOf(displayDateFunc) > -1
    if (!showDatePicker) {
        dateValuesType = "select"
    }
    if (!datePart) return null
    const isDateValuesTypeSelect = dateValuesType === "select" && ["open", "dashboard", "filter"].includes(props?.mode)

    const changeDateFunc = part => {
        let formatMap = {
            date: 'YYYY-MM-DD',
            day: 'DD',
            month: 'MMM',
            quarter: '[Q]Q',
            year: 'YYYY',
            time: "HH:mm:ss",
            hour: "HH",
            minute: "mm",
            second: "ss",
            individual: dataType === "dateTime" ? "YYYY-MM-DD HH:mm:ss" : "YYYY-MM-DD"
        }
        if (["date", "year", "month", "quarter", "day", "time", "hour", "minute", "second", "individual"].indexOf(part) > -1) {
            dispatch(updateDatePart({ ...filter, datePart: part, values: [], reportId, format: formatMap[part] }));
        } else {
            dispatch(updateDatePart({ ...filter, datePart: part, values: [], reportId }))
        }
        // props.updateMapping(part)
    }

    const onChange = value => {
        if (typeof value === "object" && !Array.isArray(value)) value = value.value
        props.onChange({ value, filter })
    }
    const checkForAnchorDate = (data) => {
        return ['dashobard', 'filter'].includes(props.mode) && data?.anchorDateData?.isAnchor;
    }

    const prepareAnchorRealtiveDate = (anchorDateData, values) => {
        let tempValues = values;
        if (anchorDateData?.isAnchor) {
            const newAnchorDate = `${anchorConstant}${anchorDateData?.anchorDate}`;
            tempValues = [...tempValues, newAnchorDate]
        }
        return tempValues;
    }

    const updateFilterValues = data => {
        if (setFilters) {
            let { uid, value: val, anchorDateData, reportId } = data;
            let {
                anchorDate,
                isAnchor,
                active,
                relativePart,
                value,
                direction,
                lastInput,
                nextInput,
                part
            } = anchorDateData;
            let fil = {
                ...filter,
                anchor: {
                    anchorDate,
                    isAnchor,
                    active,
                    relativePart,
                    value,
                    direction,
                    lastInput,
                    nextInput,
                    part
                }
            };
            fil = addFilterValue(fil, val);
            fil = { ...fil, dateValuesType: 'datePicker' };
            props.onChange({ value: fil.values, filter: fil })
        } else {
            dispatch(updateAnchorDate({ ...data, reportId }))
        }
        if (dashboardFilter && typeof props.changeDashboardFilter === 'function') {
            let values = addFilterValue(filter, data.value).values
            values = getRelativeDateValues(data, values, filter)
            if (checkForAnchorDate(data)) { // added for [7142]
                values = prepareAnchorRealtiveDate(data?.anchorDateData, values)
            }
            // props.changeDashboardFilter(values)
            props.changeDashboardFilter(getFieldDisplayName(filter), values); // 
        }
    }

    return (
        <div className="hi-date-filter" data-testid={`date-filter-${filter.column}`} >
            {(condition !== "CUSTOM") && <Menu
                className="date-filter-modes" triggerSubMenuAction="hover"
                selectedKeys={[dateValuesType, datePart, otherActiveMenu]}
                mode="horizontal"
                onClick={e => {
                    if (["select", "datePicker", "date-functions", "relative-list"].includes(e.key)) {
                        if (hideConditions.includes(condition)) return;
                        handleTypeChange(e.key)
                    } else {
                        changeDateFunc(e.key)
                    }
                }}

            >
                {(!isReadOnly && !isHCRFilter) && valuesConditions.includes(condition) && <Menu.Item key="select" >
                    <HITooltip title={"Select value from list"} overlayStyle={{ position: 'fixed', maxWidth: "100%" }}>
                        Select
                    </HITooltip>
                </Menu.Item>}
                {!isDateValuesTypeSelect && <Menu.Item key="datePicker" >
                    <HITooltip title={"Select date from Date Picker"} overlayStyle={{ position: 'fixed', maxWidth: "100%" }}>
                        Date Picker
                    </HITooltip>
                </Menu.Item>}
                {(!isReadOnly && !isHCRFilter) && funcMap.includes(displayDateFunc) && <SubMenu
                    popupClassName="hr-date-func-list"
                    key="date-functions" title={
                        <span data-testid={`list-date-funcs-${filter.column}`} >
                            <HITooltip title={"Date/DateTime/Time Functions"} overlayStyle={{ position: 'fixed', maxWidth: "100%" }}>
                                <FunctionOutlined />
                            </HITooltip>
                        </span>}>
                    {!hideConditions.includes(condition) && datePartList.map((format, i) => {
                        let className = "hr-date-func-item"
                        if (["second", "year"].includes(format.part)) {
                            className = className + " group-end"
                        }
                        return (
                            <Menu.Item key={format.part ? format.part : "key" + i}
                                className={className}
                            >
                                <span data-testid={`date-func-${format.label}-${filter.column}`} >{format.label === "Years" ? "Year" : format.label}</span>
                            </Menu.Item>
                        )
                    })}
                </SubMenu>}
                {!isDateValuesTypeSelect && <Menu.Item key="relative-list" >
                    <span data-testid={`relative-list-${filter.column}`} >
                        <HITooltip title={"Relative dates"} overlayStyle={{ position: 'fixed', maxWidth: "100%" }}>
                            R
                        </HITooltip>
                    </span>
                </Menu.Item>}
            </Menu>}
            {((valuesConditions.indexOf(condition) > -1) && (dateValuesType === "select")) && (
                <FilterValues reportId={reportId} loadValues={loadValues} filter={filter} onChange={onChange}
                    abortFilterValues={abortFilterValues} values={values} mode={props.mode} isFilterComponent={props?.isFilterComponent} {...{ filterBGColor, filterListItemColor, filterListItemFontSize }} />
            )}
            {((rangeConditions.indexOf(condition) > -1) && showDatePicker) && (
                <DateRange condition={condition} filter={filter} onChange={onChange}
                    displayDateFunc={displayDateFunc} valueDateFunc={valueDateFunc} format={filter?.format} />
            )}
            {((equalConditions.indexOf(condition) > -1) && showDatePicker) && dateValuesType === "datePicker" && (
                <DatePicker filter={filter} onChange={onChange}
                    displayDateFunc={displayDateFunc} valueDateFunc={valueDateFunc} mode={props?.mode} format={filter?.format} />
            )}
            <RelativeList
                dateValuesType={dateValuesType}
                updateFilterValues={updateFilterValues}
                toggleRelList={toggleRelList}
                filter={filter}
                mode={props?.mode} />
        </div>
    )
}

const areEqual = (prevProps, nextProps) => {
    if (
        prevProps.filter !== nextProps.filter ||
        prevProps?.filter?.cascade?.isEnabled
    ) {
        return false;
    } else {
        return true;
    }
};

export default React.memo(DateFilter, areEqual);

