
import { useState } from "react"
import { Dropdown, Button, Menu, Input, Tooltip } from "antd"
import { DeleteOutlined } from "@ant-design/icons"
import produce from "immer"
import _ from "lodash"
import { useDispatch } from "react-redux"
import { useSelector } from "react-redux"
import { getDBFunctionObject, getColumnbject } from "../../../../../utils/filter-utils"
import { removeAggregateForAdvanceFilter, updateFilterMapping } from "../../../../../redux/actions/hreport.actions"
import { extractDatabaseFunctions1, getFieldDisplayName } from "../../../../../utils/utilities"
import notify from "../../../../hi-notifications/notify"
import { checkForDisplayDBMonthFunction } from "../../utils/filter-utils"

const List = props => {
    const [open, setOpen] = useState(false)
    const [search, setSearch] = useState("")
    const dispatch = useDispatch()
    let { initialValue, isAggregateList, list, columnType, filter, isDbFunc, isCascade, index } = props
    const { metadata, databaseFunctions, filters } = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.active)
        return activeReport
    })
    let { name, tables } = metadata
    let { mapping, column, aggregate, backendDataType, dataType, cascade,  displayAggregateForAdvanceFilters: displayAggregate = [], valueAggregateForAdvanceFilters: valueAaggregate = [] } = { ...filter }
    let { valueColumn, displayColumn } = mapping
    const handleClick = ({ item }) => {
        setOpen(false)
        let tempFilter = { ...filter }
        let newMapping = { ...mapping }
        let value = item.uid
        if (isCascade) {
            let tempCascade = { ...cascade }
            let updatedFilters = [...tempCascade.filters]
            let updatedFilter = { ...updatedFilters[index] }
            updatedFilter.uid = value
            updatedFilters[index] = updatedFilter
            tempCascade.filters = updatedFilters
            tempFilter.cascade = tempCascade
            dispatch(updateFilterMapping({ tempFilter, type: "cascade" }))

            // flux.filters.actions.updateCascade(filter.set("cascade", fromJS(cascade)))
            return null
        }
        // if (item.columnId || isAggregateList) {
        if (item.id || isAggregateList) {
            let value = item.fullyQualifiedColumn
            let columnFunction = columnType == 'value' ? valueColumn.defaultFunction : displayColumn.defaultFunction
            if (isAggregateList) {
                columnFunction = item.key
                value = columnType == 'value' ? valueColumn.fullyQualifiedColumn : displayColumn.fullyQualifiedColumn
            }
            let columnSelected = getColumnbject({
                fullyQualifiedColumn: value, type: 'getColumnObj', columnFunction, tables
            })

            if (columnType === 'display') {
                newMapping.displayColumn = columnSelected
                tempFilter.mapping = newMapping
                tempFilter.values = []
                if (displayAggregate?.length) {
                    dispatch(removeAggregateForAdvanceFilter({ uid: filter?.uid, type: "display" }))
                }
            } else if (columnType == 'value') {
                if (valueAaggregate?.length) {
                    dispatch(removeAggregateForAdvanceFilter({ uid: filter?.uid, type: "value" }))
                }
                newMapping.valueColumn = columnSelected
                // when we change value column it should go in generate query and report fomrData
                column = columnSelected.fullyQualifiedColumn
                if (columnSelected.defaultFunction && columnSelected.defaultFunction.split('.').indexOf('aggregate') != -1) {
                    aggregate = [columnSelected.defaultFunction]
                }
                else {
                    aggregate = []
                }
                if (columnSelected.type) { // changing datatype and backend datatype if value column is changed
                    backendDataType = Object.keys(columnSelected.type)[0]
                    dataType = Object.values(columnSelected.type)[0]
                }
                tempFilter.mapping = newMapping
                tempFilter.column = column
                tempFilter.backendDataType = backendDataType
                tempFilter.dataType = dataType
                tempFilter.values = []
                tempFilter.aggregate = aggregate

            }
        } else {
            let value = item.key
            checkForDisplayDBMonthFunction(filter?.condition, filter, notify, dispatch,value)
            let dbFuntions, dbObj;
            try {
                dbFuntions = databaseFunctions
                dbFuntions = _.flatten(Object.values(dbFuntions))
                dbFuntions.map((obj) => {
                    if (obj.key == value) {
                        dbObj = { ...obj }
                    }
                })
            } catch (err) {
            }
            let dName = name
            let fullyQualifiedColumn = newMapping[`${columnType}Column`].fullyQualifiedColumn
            let databaseFunction = dbObj ? extractDatabaseFunctions1(dbObj, `${dName}.${fullyQualifiedColumn}`) : {}
            let dbFunc = getDBFunctionObject({
                value: databaseFunction ? databaseFunction.functionName : "", databaseFunctions
            })
            if (value === "none") {
                databaseFunction = null
                dbFunc = {}
            }
            let mappingObj = {}
            if (columnType == 'value') {
                mappingObj = {
                    ...newMapping,
                    valueDBFunction: databaseFunction,
                    valueDBFuntionInfo: dbFunc
                }
                tempFilter.databaseFunction = dbFunc
            } else if (columnType == 'display') {
                mappingObj = {
                    ...mapping,
                    DisplayDBFunction: databaseFunction
                }
            }
            tempFilter.mapping = mappingObj
            tempFilter.valuesMode = "custom"
            tempFilter.values = "custom"
        }
        dispatch(updateFilterMapping({ tempFilter }))

    }
    const getInitialValue = (item) => {
        let label = ""
        let value = (item && item.key) ? item.key : ""
        if (isDbFunc) {
            list.map((func) => {
                if (func.key === value) {
                    label = func.value
                }
            })
            if (!label) label = "None"
        } else {
            let value = item && mapping.valueColumn.fullyQualifiedColumn ? item.fullyQualifiedColumn : ''
            list.map((item) => {
                if (item.fullyQualifiedColumn === value) {
                    label = item.alias
                }
            })
        }
        if (isCascade) {
            label = "select"
            let uid = cascade.filters[index]['uid'] ? cascade.filters[index]['uid'] : null
            filters.map(fltr => {
                if (fltr.uid === uid) {
                    label = getFieldDisplayName(fltr)
                }
            })
        }
        // if(isAggregateList && item){
        //     let func = list.find(func=>func.key === item.defaultFunction) 
        //     label = func ? func.label : "none"
        // }
        if (isAggregateList && item) {
            let func = list.find(func => func.key === item.defaultFunction)
            let aggregate = columnType === "display" ? displayAggregate : valueAaggregate;
            let aggregateFnsLabels = aggregate?.map((fn) => {
                let listItem = list.find(func => func.key === fn)
                if (listItem) return listItem?.label
                return null
            }).filter(Boolean);
            aggregateFnsLabels = aggregateFnsLabels?.includes(func?.label) ? aggregateFnsLabels : []
            label = func && func?.key !== "none" ? [...new Set([func?.label, ...aggregateFnsLabels])].join("_") : ["None"]
        }
        return label ? label : "select"
    }
    const deleteFilter = index => {
        let tempFilter = produce(filter, draft => {
            draft.cascade.filters = draft.cascade.filters.filter((item, i) => {
                if (index !== i) {
                    return true
                }
            })
        })
        dispatch(updateFilterMapping({ tempFilter, type: "cascade" }))
    }
    const handleChangeCondition = () => {
        let tempFilter = produce(filter, draft => {
            let condition = draft.cascade.filters[index]['condition']
            draft.cascade.filters[index]['condition'] = condition === 'AND' ? 'OR' : "AND"
        })
        dispatch(updateFilterMapping({ tempFilter, type: "cascade" }))
    }
    const onVisibleChange = e => {
        setOpen(e)
        setSearch("")
    }
    let filterName = getFieldDisplayName(filter)
    const Items = (
        <div>
            {isDbFunc && <Input value={search} onChange={e => setSearch(e.target.value)}
                data-testid={`advance-mode-list-input-${filterName}`} />}
            <Menu className="advanced-mode-list"
                selectedKeys={[getInitialValue(initialValue)]} data-testid={`advance-mode-list-${filterName}`} >
                {list.map((item) => {
                    let { key, value, columnId, alias } = item
                    let elementKey = columnId ? columnId : key
                    elementKey = elementKey ? elementKey : item.label
                    let valueLabel = columnId ? alias : value
                    // valueLabel = item.label ? item.label : valueLabel
                    valueLabel = alias ? alias : item.label ? item.label : valueLabel
                    if (search && !valueLabel.toLowerCase().includes(search.toLowerCase())) return null
                    return (
                        <Tooltip title={
                            (isDbFunc && key !== "none") ? <>
                                <span>Description : {item?.description}</span>
                                <br />
                                <span>Signature : {item?.signature}</span>
                            </>
                                : null
                        }>
                            <Menu.Item key={valueLabel} onClick={() => handleClick({ item })} >
                                {valueLabel}
                            </Menu.Item>
                        </Tooltip>
                    )
                })}
            </Menu>
        </div>
    )
    let className = "advance-mode-label"
    let extra = null
    if (columnType === "cascade") {
        className = "advance-mode-label cascade-filter"
        extra = <>
            <span onClick={handleChangeCondition} >{cascade.filters[index].condition}</span>
            <span onClick={() => deleteFilter(index)} ><DeleteOutlined /></span>
        </>
    }
    return (
        <Dropdown
            style={{ width: "100%" }}
            overlay={Items}
            onVisibleChange={onVisibleChange}
            visible={open}
            placement="bottomLeft" trigger={["click"]}>
            <div>
                <Button
                    data-testid={`advance-filter-${columnType === "display" ? "display" : "value"}-${isDbFunc ? "dbfunc" : ""}`}
                    className={className}>
                    {getInitialValue(initialValue)}
                </Button>
                {extra}
            </div>
        </Dropdown>

    )
}


export default List